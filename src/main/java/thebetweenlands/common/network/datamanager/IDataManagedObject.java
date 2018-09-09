package thebetweenlands.common.network.datamanager;

import net.minecraft.network.datasync.DataParameter;

public interface IDataManagedObject {
	/**
	 * Called whenever a data key is changing its value
	 * @param key The data key that has changed its value
	 * @param value The new value
	 * @param fromPacket Whether the new value is from a packet
	 * @return Return true if the change was handled and no further processing is required
	 */
	public default boolean onParameterChange(DataParameter<?> key, Object value, boolean fromPacket) {
		return false;
	}
}
