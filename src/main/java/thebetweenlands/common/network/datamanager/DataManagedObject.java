package thebetweenlands.common.network.datamanager;

import net.minecraft.network.datasync.DataParameter;

public interface DataManagedObject {
	/**
	 * This callback is called whenever a data key has changed its value
	 * @param key The data key that has changed its value
	 */
	public default void notifyDataManagerChange(DataParameter<?> key) {
		
	}
}
