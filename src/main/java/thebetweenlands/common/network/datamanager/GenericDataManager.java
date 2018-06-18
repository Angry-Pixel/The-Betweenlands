package thebetweenlands.common.network.datamanager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

public class GenericDataManager<F extends DataManagedObject> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Class<? extends DataManagedObject>, Integer> NEXT_ID_MAP = Maps.<Class<?  extends DataManagedObject>, Integer>newHashMap();
	private final Map<Integer, GenericDataManager.DataEntry<?>> entries = Maps.<Integer, GenericDataManager.DataEntry<?>>newHashMap();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
	private boolean dirty;
	private final F owner;

	public GenericDataManager(F owner) {
		this.owner = owner;
	}

	public static <T> DataParameter<T> createKey(Class<? extends DataManagedObject> clazz, DataSerializer<T> serializer) {
		if (LOGGER.isDebugEnabled()) {
			try {
				Class<?> oclass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

				if (!oclass.equals(clazz)) {
					LOGGER.debug("createKey called for: {} from {}", clazz, oclass, new RuntimeException());
				}
			} catch (ClassNotFoundException ex) { }
		}

		int freeId;

		if (NEXT_ID_MAP.containsKey(clazz)) {
			freeId = ((Integer) NEXT_ID_MAP.get(clazz)).intValue() + 1;
		} else {
			int nextId = 0;
			Class<?> hierarchyCls = clazz;

			while (DataManagedObject.class.isAssignableFrom(hierarchyCls)) {
				hierarchyCls = hierarchyCls.getSuperclass();

				if(hierarchyCls == null || !DataManagedObject.class.isAssignableFrom(hierarchyCls)) {
					break;
				}

				if (NEXT_ID_MAP.containsKey(hierarchyCls)) {
					nextId = ((Integer) NEXT_ID_MAP.get(hierarchyCls)).intValue() + 1;
					break;
				}
			}

			freeId = nextId;
		}

		if (freeId > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + freeId + "! (Max is " + 254 + ")");
		} else {
			NEXT_ID_MAP.put(clazz, Integer.valueOf(freeId));
			return serializer.createKey(freeId);
		}
	}

	public <T> void register(DataParameter<T> key, T value) {
		int id = key.getId();

		if (id > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 254 + ")");
		} else if (this.entries.containsKey(Integer.valueOf(id))) {
			throw new IllegalArgumentException("Duplicate id value for " + id + "!");
		} else if (DataSerializers.getSerializerId(key.getSerializer()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + key.getSerializer() + " for " + id + "!");
		} else {
			this.setEntry(key, value);
		}
	}

	private <T> void setEntry(DataParameter<T> key, T value) {
		GenericDataManager.DataEntry<T> dataentry = new GenericDataManager.DataEntry<T>(key, value);
		this.lock.writeLock().lock();
		this.entries.put(Integer.valueOf(key.getId()), dataentry);
		this.empty = false;
		this.lock.writeLock().unlock();
	}

	@SuppressWarnings("unchecked")
	private <T> GenericDataManager.DataEntry<T> getEntry(DataParameter<T> key) {
		this.lock.readLock().lock();
		GenericDataManager.DataEntry<T> entry;

		try {
			entry = (GenericDataManager.DataEntry<T>) this.entries.get(Integer.valueOf(key.getId()));
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting synced " + this.owner.getClass().getName() + " data");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Synced " + this.owner.getClass().getName() + " data");
			crashreportcategory.addCrashSection("Data ID", key);
			throw new ReportedException(crashreport);
		}

		this.lock.readLock().unlock();
		return entry;
	}

	public <T> T get(DataParameter<T> key) {
		return (T) this.getEntry(key).getValue();
	}

	public <T> void set(DataParameter<T> key, T value) {
		GenericDataManager.DataEntry<T> dataentry = this.<T>getEntry(key);

		if (ObjectUtils.notEqual(value, dataentry.getValue())) {
			dataentry.setValue(value);
			if(this.owner instanceof DataManagedObject) {
				((DataManagedObject) this.owner).notifyDataManagerChange(key);
			}
			dataentry.setDirty(true);
			this.dirty = true;
		}
	}

	public <T> void setDirty(DataParameter<T> key) {
		this.getEntry(key).dirty = true;
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void writeEntries(List<GenericDataManager.DataEntry<?>> entriesIn, PacketBuffer buf) throws IOException {
		if (entriesIn != null) {
			int i = 0;

			for (int j = entriesIn.size(); i < j; ++i) {
				GenericDataManager.DataEntry<?> dataentry = (GenericDataManager.DataEntry<?>) entriesIn.get(i);
				writeEntry(buf, dataentry);
			}
		}

		buf.writeByte(255);
	}

	@Nullable
	public List<GenericDataManager.DataEntry<?>> getDirty() {
		List<GenericDataManager.DataEntry<?>> list = null;

		if (this.dirty) {
			this.lock.readLock().lock();

			for (GenericDataManager.DataEntry<?> dataentry : this.entries.values()) {
				if (dataentry.isDirty()) {
					dataentry.setDirty(false);

					if (list == null) {
						list = Lists.<GenericDataManager.DataEntry<?>>newArrayList();
					}

					list.add(dataentry.copy());
				}
			}

			this.lock.readLock().unlock();
		}

		this.dirty = false;
		return list;
	}

	public void writeEntries(PacketBuffer buf) throws IOException {
		this.lock.readLock().lock();

		for (GenericDataManager.DataEntry<?> dataentry : this.entries.values()) {
			writeEntry(buf, dataentry);
		}

		this.lock.readLock().unlock();
		buf.writeByte(255);
	}

	@Nullable
	public List<GenericDataManager.DataEntry<?>> getAll() {
		List<GenericDataManager.DataEntry<?>> list = null;
		this.lock.readLock().lock();

		for (GenericDataManager.DataEntry<?> dataentry : this.entries.values()) {
			if (list == null) {
				list = Lists.<GenericDataManager.DataEntry<?>>newArrayList();
			}

			list.add(dataentry.copy());
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static <T> void writeEntry(PacketBuffer buf, GenericDataManager.DataEntry<T> entry) throws IOException {
		DataParameter<T> dataparameter = entry.getKey();
		int i = DataSerializers.getSerializerId(dataparameter.getSerializer());

		if (i < 0) {
			throw new EncoderException("Unknown serializer type " + dataparameter.getSerializer());
		} else {
			buf.writeByte(dataparameter.getId());
			buf.writeVarInt(i);
			dataparameter.getSerializer().write(buf, entry.getValue());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Nullable
	public static List<GenericDataManager.DataEntry<?>> readEntries(PacketBuffer buf) throws IOException {
		List<GenericDataManager.DataEntry<?>> list = null;
		int i;

		while ((i = buf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = Lists.<GenericDataManager.DataEntry<?>>newArrayList();
			}

			int j = buf.readVarInt();
			DataSerializer<?> dataserializer = DataSerializers.getSerializer(j);

			if (dataserializer == null) {
				throw new DecoderException("Unknown serializer type " + j);
			}

			list.add(new GenericDataManager.DataEntry(dataserializer.createKey(i), dataserializer.read(buf)));
		}

		return list;
	}

	@SideOnly(Side.CLIENT)
	public void setEntryValues(List<GenericDataManager.DataEntry<?>> entriesIn) {
		this.lock.writeLock().lock();

		for (GenericDataManager.DataEntry<?> dataentry : entriesIn) {
			GenericDataManager.DataEntry<?> dataentry1 = this.entries.get(Integer.valueOf(dataentry.getKey().getId()));

			if (dataentry1 != null) {
				this.setEntryValue(dataentry1, dataentry);
				if(this.owner instanceof DataManagedObject) {
					((DataManagedObject) this.owner).notifyDataManagerChange(dataentry.getKey());
				}
			}
		}

		this.lock.writeLock().unlock();
		this.dirty = true;
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	protected <T> void setEntryValue(GenericDataManager.DataEntry<T> target, GenericDataManager.DataEntry<?> source) {
		target.setValue((T) source.getValue());
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void setClean() {
		this.dirty = false;
		this.lock.readLock().lock();

		for (GenericDataManager.DataEntry<?> dataentry : this.entries.values()) {
			dataentry.setDirty(false);
		}

		this.lock.readLock().unlock();
	}

	public static class DataEntry<T> {
		private final DataParameter<T> key;
		private T value;
		private boolean dirty;

		public DataEntry(DataParameter<T> keyIn, T valueIn) {
			this.key = keyIn;
			this.value = valueIn;
			this.dirty = true;
		}

		public DataParameter<T> getKey() {
			return this.key;
		}

		public void setValue(T valueIn) {
			this.value = valueIn;
		}

		public T getValue() {
			return this.value;
		}

		public boolean isDirty() {
			return this.dirty;
		}

		public void setDirty(boolean dirtyIn) {
			this.dirty = dirtyIn;
		}

		public GenericDataManager.DataEntry<T> copy() {
			return new GenericDataManager.DataEntry<T>(this.key, this.key.getSerializer().copyValue(this.value));
		}
	}
}