package thebetweenlands.common.network.datamanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.common.config.BetweenlandsConfig;

public class GenericDataManager implements IGenericDataManagerAccess {
	public static interface Serializer<T> {
		public void serialize(PacketBuffer buf, T value) throws IOException;
	}

	public static interface Deserializer<T> {
		public T deserialize(PacketBuffer buf) throws IOException;
	}

	private static final class CustomSerializer<T> implements DataSerializer<Object> {
		private final Serializer<T> serializer;
		private final Deserializer<T> deserializer;

		private CustomSerializer(Serializer<T> serializer, Deserializer<T> deserializer) {
			this.serializer = serializer;
			this.deserializer = deserializer;
		}

		@Override
		public void write(PacketBuffer buf, Object value) {

		}

		@Override
		public Object read(PacketBuffer buf) throws IOException {
			return null;
		}

		@Override
		public DataParameter<Object> createKey(int id) {
			return new DataParameter<>(id, this);
		}

		@Override
		public Object copyValue(Object value) {
			return new CustomSerializer<T>(this.serializer, this.deserializer);
		}
	};

	private static final TObjectIntMap<Class<?>> NEXT_ID_MAP = new TObjectIntHashMap<>();
	private final List<GenericDataManager.DataEntry<?>> trackedEntries = new ArrayList<>();
	private final TIntObjectMap<GenericDataManager.DataEntry<?>> entries = new TIntObjectHashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
	private boolean dirty;
	private final Object owner;

	public GenericDataManager(Object owner) {
		this.owner = owner;
	}

	/**
	 * Creates a data parameter with custom de-/serializers. Values will be de-/serialized on the main thread.
	 * @param clazz
	 * @param serializer
	 * @param deserializer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> DataParameter<T> createKey(Class<?> clazz, Serializer<T> serializer, Deserializer<T> deserializer) {
		if (BetweenlandsConfig.DEBUG.debug) {
			try {
				Class<?> callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

				if (!callerClass.equals(clazz)) {
					throw new RuntimeException("GenericDataManager#createKey called for: " + clazz + " from " + callerClass);
				}
			} catch (ClassNotFoundException ex) { }
		}
		return (DataParameter<T>) new CustomSerializer<>(serializer, deserializer).createKey(createFreeId(clazz));
	}

	/**
	 * Creates a data parameter with a normal serializer. Values will be de-/serialized on the network thread.
	 * @param clazz
	 * @param serializer
	 * @return
	 */
	public static <T> DataParameter<T> createKey(Class<?> clazz, DataSerializer<T> serializer) {
		if (BetweenlandsConfig.DEBUG.debug) {
			try {
				Class<?> callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

				if (!callerClass.equals(clazz)) {
					throw new RuntimeException("GenericDataManager#createKey called for: " + clazz + " from " + callerClass);
				}
			} catch (ClassNotFoundException ex) { }
		}
		return serializer.createKey(createFreeId(clazz));
	}

	private static int createFreeId(Class<?> clazz) {
		int freeId;

		if (NEXT_ID_MAP.containsKey(clazz)) {
			freeId = NEXT_ID_MAP.get(clazz) + 1;
		} else {
			int nextId = 0;
			Class<?> hierarchyCls = clazz;

			while ((hierarchyCls = hierarchyCls.getSuperclass()) != null) {
				if (NEXT_ID_MAP.containsKey(hierarchyCls)) {
					nextId = NEXT_ID_MAP.get(hierarchyCls) + 1;
					break;
				}
			}

			freeId = nextId;
		}

		if (freeId > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + freeId + "! (Max is " + 254 + ")");
		} else {
			NEXT_ID_MAP.put(clazz, freeId);
			return freeId;
		}
	}

	public <T> DataParameter<T> register(DataParameter<T> key, T value) {
		int id = key.getId();

		if (id > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 254 + ")");
		} else if (this.entries.containsKey(Integer.valueOf(id))) {
			throw new IllegalArgumentException("Duplicate id value for " + id + "!");
		} else if (key.getSerializer() instanceof CustomSerializer == false && DataSerializers.getSerializerId(key.getSerializer()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + key.getSerializer() + " for " + id + "!");
		} else {
			this.setEntry(key, value);
		}

		return key;
	}

	public <T> DataParameter<T> register(DataParameter<T> key, int trackingTime, T value) {
		this.register(key, value);
		this.setTrackingTime(key, trackingTime);
		return key;
	}

	public <T> DataParameter<T> setTrackingTime(DataParameter<T> key, int time) {
		DataEntry<?> entry = this.getEntry(key);

		if(entry == null) {
			throw new IllegalArgumentException("Data parameter " + key + " is not registered!");
		}

		entry.trackingTime = time;

		if(time > 0) {
			if(!this.trackedEntries.contains(entry)) {
				this.trackedEntries.add(entry);
			}
		} else {
			this.trackedEntries.remove(entry);
		}

		return key;
	}

	@SuppressWarnings("unchecked")
	private <T> void setEntry(DataParameter<T> key, T value) {
		GenericDataManager.DataEntry<T> entry = new GenericDataManager.DataEntry<T>(this, key, value);

		DataSerializer<T> serializer = entry.getKey().getSerializer();
		if(serializer instanceof CustomSerializer) {
			entry.serializer = ((CustomSerializer<T>) serializer).serializer;
			entry.deserializer = ((CustomSerializer<T>) serializer).deserializer;
		}

		this.lock.writeLock().lock();
		try {
			this.entries.put(Integer.valueOf(key.getId()), entry);
			this.empty = false;
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> GenericDataManager.DataEntry<T> getEntry(DataParameter<T> key) {
		this.lock.readLock().lock();
		try {
			GenericDataManager.DataEntry<T> entry;

			try {
				entry = (GenericDataManager.DataEntry<T>) this.entries.get(Integer.valueOf(key.getId()));
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting synced " + this.owner.getClass().getName() + " data");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Synced " + this.owner.getClass().getName() + " data");
				crashreportcategory.addCrashSection("Data ID", key);
				throw new ReportedException(crashreport);
			}

			return entry;
		} finally {
			this.lock.readLock().unlock();
		}
	}

	@Override
	public <T> T get(DataParameter<T> key) {
		GenericDataManager.DataEntry<T> entry = this.<T>getEntry(key);

		if(entry == null) {
			throw new IllegalArgumentException("Data parameter " + key + " is not registered!");
		}

		return entry.getValue();
	}

	public <T> EntryAccess<T> set(DataParameter<T> key, T value) {
		GenericDataManager.DataEntry<T> entry = this.<T>getEntry(key);

		if(entry == null) {
			throw new IllegalArgumentException("Data parameter " + key + " is not registered!");
		}

		if (ObjectUtils.notEqual(value, entry.getValue())) {
			if(this.owner instanceof IDataManagedObject == false || !((IDataManagedObject)this.owner).onParameterChange(key, value, false)) {
				entry.setValue(value);
			}
			entry.setDirty(true);
		}

		return entry.access;
	}

	public <T> EntryAccess<T> setDirty(DataParameter<T> key) {
		DataEntry<T> entry = this.getEntry(key);

		if(entry == null) {
			throw new IllegalArgumentException("Data parameter " + key + " is not registered!");
		}

		entry.setDirty(true);

		return entry.access;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	private <T> void serializeEntry(GenericDataManager.DataEntry<T> entry, GenericDataManager.DataEntry<?> copy) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		try {
			entry.serializer.serialize(buf, (T) entry.value);
			copy.serializedData = new byte[buf.readableBytes()];
			buf.readBytes(copy.serializedData);
		} catch(Exception ex) {
			throw new DecoderException("Failed serializing data with custom serializer " + entry.serializer.getClass().getName(), ex);
		} finally {
			buf.release();
		}
	}

	@Override
	@Nullable
	public List<IDataEntry<?>> getDirty() {
		List<IDataEntry<?>> list = null;

		if (this.dirty) {
			this.lock.readLock().lock();

			try {
				for (GenericDataManager.DataEntry<?> entry : this.entries.valueCollection()) {
					if (entry.isDirty()) {
						entry.setDirty(false);

						if (list == null) {
							list = new ArrayList<>();
						}

						DataEntry<?> copy = entry.copy();

						list.add(copy);

						if(entry.serializer != null) {
							this.serializeEntry(entry, copy);
						}
					}
				}
			} finally {
				this.lock.readLock().unlock();
			}
		}

		this.dirty = false;
		return list;
	}

	@Override
	@Nullable
	public List<IDataEntry<?>> getAll() {
		List<IDataEntry<?>> list = null;
		this.lock.readLock().lock();

		try {
			for (GenericDataManager.DataEntry<?> entry : this.entries.valueCollection()) {
				if (list == null) {
					list = new ArrayList<>();
				}

				DataEntry<?> copy = entry.copy();

				list.add(copy);

				if(entry.serializer != null) {
					this.serializeEntry(entry, copy);
				}
			}
		} finally {
			this.lock.readLock().unlock();
		}

		return list;
	}

	public static void writeEntries(List<? extends IDataEntry<?>> entriesIn, PacketBuffer buf) throws IOException {
		if (entriesIn != null) {
			int i = 0;

			for (int j = entriesIn.size(); i < j; ++i) {
				GenericDataManager.DataEntry<?> dataentry = (GenericDataManager.DataEntry<?>) entriesIn.get(i);
				writeEntry(buf, dataentry);
			}
		}

		buf.writeByte(255);
	}

	private static <T> void writeEntry(PacketBuffer buf, GenericDataManager.DataEntry<T> entry) throws IOException {
		DataParameter<T> parameter = entry.getKey();

		buf.writeByte(parameter.getId());

		if(entry.serializedData != null) {
			buf.writeBoolean(true);

			synchronized(entry.serializedData) {
				buf.writeVarInt(entry.serializedData.length);
				buf.writeBytes(entry.serializedData);
			}
		} else {
			buf.writeBoolean(false);

			int i = DataSerializers.getSerializerId(parameter.getSerializer());
			if (i < 0) {
				throw new EncoderException("Unknown serializer type " + parameter.getSerializer());
			}

			buf.writeVarInt(i);

			parameter.getSerializer().write(buf, entry.getValue());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Nullable
	public static List<IDataEntry<?>> readEntries(PacketBuffer buf) throws IOException {
		List<IDataEntry<?>> list = null;
		int key;

		while ((key = buf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = new ArrayList<>();
			}

			DataSerializer<?> serializer;
			Object value = null;
			byte[] serializedData = null;

			if(buf.readBoolean()) {
				serializedData = new byte[buf.readVarInt()];

				buf.readBytes(serializedData);

				serializer = new CustomSerializer(null, null);
			} else {
				int serializerId = buf.readVarInt();

				serializer = DataSerializers.getSerializer(serializerId);
				if (serializer == null) {
					throw new DecoderException("Unknown serializer type " + serializerId);
				}

				value = serializer.read(buf);
			}

			GenericDataManager.DataEntry<?> entry = new GenericDataManager.DataEntry(null, serializer.createKey(key), value);

			entry.serializedData = serializedData;

			list.add(entry);
		}

		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setValuesFromPacket(List<? extends IDataEntry<?>> newEntries) {
		this.lock.writeLock().lock();

		try {
			for (IDataEntry<?> newEntry : newEntries) {
				GenericDataManager.DataEntry<?> entry = this.entries.get(Integer.valueOf(newEntry.getKey().getId()));

				if (entry != null) {
					Object newValue;
					if(newEntry instanceof GenericDataManager.DataEntry<?> && entry.deserializer != null) {
						GenericDataManager.DataEntry<?> newGenericEntry = (GenericDataManager.DataEntry<?>) newEntry;
						if(newGenericEntry.deserializedValue == null) {
							ByteBuf buf = Unpooled.wrappedBuffer(newGenericEntry.serializedData);
							try {
								newGenericEntry.deserializedValue = entry.deserializer.deserialize(new PacketBuffer(buf));
							} catch(Exception ex) {
								throw new DecoderException("Failed deserializing data with custom deserializer " + entry.deserializer.getClass().getName(), ex);
							} finally {
								buf.release();
							}
						}
						newValue = newGenericEntry.deserializedValue;
					} else {
						newValue = newEntry.getValue();
					}
					if(this.owner instanceof IDataManagedObject == false || !((IDataManagedObject)this.owner).onParameterChange(entry.getKey(), newValue, true)) {
						this.setEntryValue(entry, newValue);
					}
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}

		this.dirty = true;
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	protected <T> void setEntryValue(GenericDataManager.DataEntry<T> target, Object value) {
		target.setValue((T) value);
	}

	@Override
	public boolean isEmpty() {
		return this.empty;
	}

	@Override
	public void setClean() {
		this.dirty = false;
		this.lock.readLock().lock();
		try {
			for (GenericDataManager.DataEntry<?> dataentry : this.entries.valueCollection()) {
				dataentry.setDirty(false);
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}

	@Override
	public void update() {
		if(!this.trackedEntries.isEmpty()) {
			for (GenericDataManager.DataEntry<?> entry : this.trackedEntries) {
				if(entry.trackingTimer >= 0) {
					entry.trackingTimer--;
				}
				if(entry.queuedDirty && entry.trackingTimer < 0) {
					entry.trackingTimer = entry.trackingTime;
					entry.dirty = true;
					this.dirty = true;
					entry.queuedDirty = false;
				}
			}
		}
	}

	public static class EntryAccess<T> {
		private DataEntry<T> entry;

		private EntryAccess(DataEntry<T> entry) {
			this.entry = entry;
		}

		/**
		 * Returns the value of the data parameter
		 * @return
		 */
		public T getValue() {
			return this.entry.value;
		}

		public EntryAccess<T> setDirty() {
			this.entry.setDirty(true);
			return this;
		}

		/**
		 * Causes the data parameter to sync immediately if it is currently dirty
		 * @return this
		 */
		public EntryAccess<T> syncImmediately() {
			if(this.entry.queuedDirty) {
				this.entry.dirty = true;
				this.entry.dataManager.dirty = true;
				this.entry.queuedDirty = false;
			}
			return this;
		}
	}

	public static class DataEntry<T> implements IDataEntry<T> {
		private final GenericDataManager dataManager;
		private final DataParameter<T> key;
		private T value;
		private boolean queuedDirty;
		private boolean dirty;
		private int trackingTime;
		private int trackingTimer;
		private EntryAccess<T> access;

		private byte[] serializedData;
		private Object deserializedValue;
		private Serializer<T> serializer;
		private Deserializer<T> deserializer;

		public DataEntry(GenericDataManager dataManager, DataParameter<T> keyIn, T valueIn) {
			this.dataManager = dataManager;
			this.key = keyIn;
			this.value = valueIn;
			this.dirty = true;
			this.access = new EntryAccess<>(this);
		}

		private DataEntry(GenericDataManager dataManager, DataParameter<T> keyIn, T valueIn, int trackingTime) {
			this.dataManager = dataManager;
			this.key = keyIn;
			this.value = valueIn;
			this.dirty = true;
			this.trackingTime = trackingTime;
			this.access = new EntryAccess<>(this);
		}

		@Override
		public DataParameter<T> getKey() {
			return this.key;
		}

		@Override
		public void setValue(T valueIn) {
			this.value = valueIn;
		}

		@Override
		public T getValue() {
			return this.value;
		}

		@Override
		public boolean isDirty() {
			return this.dirty;
		}

		@Override
		public void setDirty(boolean dirtyIn) {
			if(this.trackingTime > 0 && dirtyIn) {
				this.queuedDirty = true;
			} else {
				this.queuedDirty = false;
				this.dirty = dirtyIn;
				if(dirtyIn) {
					this.dataManager.dirty = true;
				}
			}
		}

		@Override
		public GenericDataManager.DataEntry<T> copy() {
			return new GenericDataManager.DataEntry<T>(this.dataManager, this.key, this.key.getSerializer().copyValue(this.value), this.trackingTime);
		}
	}
}