package thebetweenlands.api.network;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import thebetweenlands.api.storage.TickableStorage;

public interface GenericDataAccessorAccess extends TickableStorage {
	interface IDataEntry<T> {
		EntityDataAccessor<T> getKey();

		void setValue(T valueIn);

		T getValue();

		boolean isDirty();

		void setDirty(boolean dirty);

		IDataEntry<T> copy();
	}

	interface IDataManagedObject {
		/**
		 * Called whenever a data key is changing its value
		 * @param key The data key that has changed its value
		 * @param value The new value
		 * @param fromPacket Whether the new value is from a packet
		 * @return Return true if the change was handled and no further processing is required
		 */
		default boolean onParameterChange(EntityDataAccessor<?> key, Object value, boolean fromPacket) {
			return false;
		}
	}

	<T> T get(EntityDataAccessor<T> key);

	boolean isDirty();

	@Nullable
	List<IDataEntry<?>> getDirty();

	@Nullable
	List<IDataEntry<?>> getAll();

	void setValuesFromPacket(List<? extends IDataEntry<?>> newEntries);

	boolean isEmpty();

	void setClean();
}
