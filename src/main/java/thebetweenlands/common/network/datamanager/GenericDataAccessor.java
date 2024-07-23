package thebetweenlands.common.network.datamanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ObjectUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import thebetweenlands.api.network.IGenericDataAccessorAccess;
import thebetweenlands.common.config.BetweenlandsConfig;

public class GenericDataAccessor implements IGenericDataAccessorAccess {
	public interface Serializer<T> {
		void serialize(FriendlyByteBuf buf, T value) throws IOException;
	}

	public interface Deserializer<T> {
		T deserialize(FriendlyByteBuf buf) throws IOException;
	}

	private static final class CustomSerializer<T> implements EntityDataSerializer<Object> {
		private final Serializer<T> serializer;
		private final Deserializer<T> deserializer;

		private CustomSerializer(Serializer<T> serializer, Deserializer<T> deserializer) {
			this.serializer = serializer;
			this.deserializer = deserializer;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, Object> codec() {
			return StreamCodec.unit(Unit.INSTANCE);
		}

		@Override
		public EntityDataAccessor<Object> createAccessor(int id) {
			return new EntityDataAccessor<>(id, this);
		}

		@Override
		public Object copy(Object value) {
			return new CustomSerializer<T>(this.serializer, this.deserializer);
		}
	};

	private static final Object2IntMap<Class<?>> NEXT_ID_MAP = new Object2IntOpenHashMap<>();
	private final List<GenericDataAccessor.DataEntry<?>> trackedEntries = new ArrayList<>();
	private final Int2ObjectMap<DataEntry<?>> entries = new Int2ObjectOpenHashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
	private boolean dirty;
	private final Object owner;

	public GenericDataAccessor(Object owner) {
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
	public static <T> EntityDataAccessor<T> defineId(Class<?> clazz, Serializer<T> serializer, Deserializer<T> deserializer) {
		if (BetweenlandsConfig.debug) {
			try {
				Class<?> callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

				if (!callerClass.equals(clazz)) {
					throw new RuntimeException("GenericDataManager#createKey called for: " + clazz + " from " + callerClass);
				}
			} catch (ClassNotFoundException ex) { }
		}
		return (EntityDataAccessor<T>) new CustomSerializer<>(serializer, deserializer).createAccessor(createFreeId(clazz));
	}

	/**
	 * Creates a data parameter with a normal serializer. Values will be de-/serialized on the network thread.
	 * @param clazz
	 * @param serializer
	 * @return
	 */
	public static <T> EntityDataAccessor<T> defineId(Class<?> clazz, EntityDataSerializer<T> serializer) {
		if (BetweenlandsConfig.debug) {
			try {
				Class<?> callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

				if (!callerClass.equals(clazz)) {
					throw new RuntimeException("GenericDataManager#createKey called for: " + clazz + " from " + callerClass);
				}
			} catch (ClassNotFoundException ex) { }
		}
		return serializer.createAccessor(createFreeId(clazz));
	}

	private static int createFreeId(Class<?> clazz) {
		int freeId;

		if (NEXT_ID_MAP.containsKey(clazz)) {
			freeId = NEXT_ID_MAP.getInt(clazz) + 1;
		} else {
			int nextId = 0;
			Class<?> hierarchyCls = clazz;

			while ((hierarchyCls = hierarchyCls.getSuperclass()) != null) {
				if (NEXT_ID_MAP.containsKey(hierarchyCls)) {
					nextId = NEXT_ID_MAP.getInt(hierarchyCls) + 1;
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

	public <T> EntityDataAccessor<T> register(EntityDataAccessor<T> key, T value) {
		int id = key.id();

		if (id > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 254 + ")");
		} else if (this.entries.containsKey(id)) {
			throw new IllegalArgumentException("Duplicate id value for " + id + "!");
		} else if (!(key.serializer() instanceof CustomSerializer) && EntityDataSerializers.getSerializedId(key.serializer()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + key.serializer() + " for " + id + "!");
		} else {
			this.setEntry(key, value);
		}

		return key;
	}

	public <T> EntityDataAccessor<T> register(EntityDataAccessor<T> key, int trackingTime, T value) {
		this.register(key, value);
		this.setTrackingTime(key, trackingTime);
		return key;
	}

	public <T> EntityDataAccessor<T> setTrackingTime(EntityDataAccessor<T> key, int time) {
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
	private <T> void setEntry(EntityDataAccessor<T> key, T value) {
		GenericDataAccessor.DataEntry<T> entry = new GenericDataAccessor.DataEntry<T>(this, key, value);

		EntityDataSerializer<T> serializer = entry.getKey().serializer();
		if(serializer instanceof CustomSerializer) {
			entry.serializer = ((CustomSerializer<T>) serializer).serializer;
			entry.deserializer = ((CustomSerializer<T>) serializer).deserializer;
		}

		this.lock.writeLock().lock();
		try {
			this.entries.put(key.id(), entry);
			this.empty = false;
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> GenericDataAccessor.DataEntry<T> getEntry(EntityDataAccessor<T> key) {
		this.lock.readLock().lock();
		try {
			GenericDataAccessor.DataEntry<T> entry;

			try {
				entry = (GenericDataAccessor.DataEntry<T>) this.entries.get(key.id());
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synced " + this.owner.getClass().getName() + " data");
				CrashReportCategory crashreportcategory = crashreport.addCategory("Synced " + this.owner.getClass().getName() + " data");
				crashreportcategory.setDetail("Data ID", key);
				throw new ReportedException(crashreport);
			}

			return entry;
		} finally {
			this.lock.readLock().unlock();
		}
	}

	@Override
	public <T> T get(EntityDataAccessor<T> key) {
		GenericDataAccessor.DataEntry<T> entry = this.<T>getEntry(key);

		if(entry == null) {
			throw new IllegalArgumentException("Data parameter " + key + " is not registered!");
		}

		return entry.getValue();
	}

	public <T> EntryAccess<T> set(EntityDataAccessor<T> key, T value) {
		GenericDataAccessor.DataEntry<T> entry = this.getEntry(key);

		if(entry == null) {
			throw new IllegalArgumentException("Data parameter " + key + " is not registered!");
		}

		if (ObjectUtils.notEqual(value, entry.getValue())) {
			if(!(this.owner instanceof IDataManagedObject) || !((IDataManagedObject)this.owner).onParameterChange(key, value, false)) {
				entry.setValue(value);
			}
			entry.setDirty(true);
		}

		return entry.access;
	}

	public <T> EntryAccess<T> setDirty(EntityDataAccessor<T> key) {
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

	private <T> void serializeEntry(GenericDataAccessor.DataEntry<T> entry, GenericDataAccessor.DataEntry<?> copy) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		try {
			entry.serializer.serialize(buf, entry.value);
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
				for (GenericDataAccessor.DataEntry<?> entry : this.entries.values()) {
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
			for (GenericDataAccessor.DataEntry<?> entry : this.entries.values()) {
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

	public static void writeEntries(List<? extends IDataEntry<?>> entriesIn, RegistryFriendlyByteBuf buf) {
		if (entriesIn != null) {
			int i = 0;

			for (int j = entriesIn.size(); i < j; ++i) {
				GenericDataAccessor.DataEntry<?> dataentry = (GenericDataAccessor.DataEntry<?>) entriesIn.get(i);
				writeEntry(buf, dataentry);
			}
		}

		buf.writeByte(255);
	}

	private static <T> void writeEntry(RegistryFriendlyByteBuf buf, GenericDataAccessor.DataEntry<T> entry) {
		EntityDataAccessor<T> parameter = entry.getKey();

		buf.writeByte(parameter.id());

		if(entry.serializedData != null) {
			buf.writeBoolean(true);

			synchronized(entry.serializedData) {
				buf.writeVarInt(entry.serializedData.length);
				buf.writeBytes(entry.serializedData);
			}
		} else {
			buf.writeBoolean(false);

			int i = EntityDataSerializers.getSerializedId(parameter.serializer());
			if (i < 0) {
				throw new EncoderException("Unknown serializer type " + parameter.serializer());
			}

			buf.writeVarInt(i);

			parameter.serializer().codec().encode(buf, entry.getValue());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Nullable
	public static List<IDataEntry<?>> readEntries(RegistryFriendlyByteBuf buf) {
		List<IDataEntry<?>> list = null;
		int key;

		while ((key = buf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = new ArrayList<>();
			}

			EntityDataSerializer<?> serializer;
			Object value = null;
			byte[] serializedData = null;

			if(buf.readBoolean()) {
				serializedData = new byte[buf.readVarInt()];

				buf.readBytes(serializedData);

				serializer = new CustomSerializer(null, null);
			} else {
				int serializerId = buf.readVarInt();

				serializer = EntityDataSerializers.getSerializer(serializerId);
				if (serializer == null) {
					throw new DecoderException("Unknown serializer type " + serializerId);
				}

				value = serializer.codec().decode(buf);
			}

			GenericDataAccessor.DataEntry<?> entry = new GenericDataAccessor.DataEntry(null, serializer.createAccessor(key), value);

			entry.serializedData = serializedData;

			list.add(entry);
		}

		return list;
	}

	@Override
	public void setValuesFromPacket(List<? extends IDataEntry<?>> newEntries) {
		this.lock.writeLock().lock();

		try {
			for (IDataEntry<?> newEntry : newEntries) {
				GenericDataAccessor.DataEntry<?> entry = this.entries.get(newEntry.getKey().id());

				if (entry != null) {
					Object newValue;
					if(newEntry instanceof GenericDataAccessor.DataEntry<?> && entry.deserializer != null) {
						GenericDataAccessor.DataEntry<?> newGenericEntry = (GenericDataAccessor.DataEntry<?>) newEntry;
						if(newGenericEntry.deserializedValue == null) {
							ByteBuf buf = Unpooled.wrappedBuffer(newGenericEntry.serializedData);
							try {
								newGenericEntry.deserializedValue = entry.deserializer.deserialize(new FriendlyByteBuf(buf));
							} catch(Exception ex) {
								throw new DecoderException("Failed deserializing data with custom deserializer " + entry.deserializer.getClass().getName(), ex);
							} finally {
								((FriendlyByteBuf) buf).release();
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
	protected <T> void setEntryValue(GenericDataAccessor.DataEntry<T> target, Object value) {
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
			for (GenericDataAccessor.DataEntry<?> dataentry : this.entries.values()) {
				dataentry.setDirty(false);
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}

	@Override
	public void tick(Level level) {
		if(!this.trackedEntries.isEmpty()) {
			for (GenericDataAccessor.DataEntry<?> entry : this.trackedEntries) {
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
		private final GenericDataAccessor dataManager;
		private final EntityDataAccessor<T> key;
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

		public DataEntry(GenericDataAccessor dataManager, EntityDataAccessor<T> keyIn, T valueIn) {
			this.dataManager = dataManager;
			this.key = keyIn;
			this.value = valueIn;
			this.dirty = true;
			this.access = new EntryAccess<>(this);
		}

		private DataEntry(GenericDataAccessor dataManager, EntityDataAccessor<T> keyIn, T valueIn, int trackingTime) {
			this.dataManager = dataManager;
			this.key = keyIn;
			this.value = valueIn;
			this.dirty = true;
			this.trackingTime = trackingTime;
			this.access = new EntryAccess<>(this);
		}

		@Override
		public EntityDataAccessor<T> getKey() {
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
		public GenericDataAccessor.DataEntry<T> copy() {
			return new GenericDataAccessor.DataEntry<T>(this.dataManager, this.key, this.key.serializer().copy(this.value), this.trackingTime);
		}
	}
}