package thebetweenlands.util;

import net.minecraft.world.inventory.ContainerData;

public interface BooleanContainerData extends ContainerData {
    default int get(int index) {
    	return this.getBoolean(index) ? 1 : 0;
    }

    default void set(int index, int value) {
    	this.set(index, value != 0);
    }

    boolean getBoolean(int index);
    
    void set(int index, boolean value);
}
