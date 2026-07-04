package thebetweenlands.common.capability;

import net.minecraft.world.Container;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class SmokingRackWrapper extends InvWrapper {

	public SmokingRackWrapper(Container inv) {
		super(inv);
	}
	
	@Override
	public int getSlotLimit(int slot) {
		// Max stack size of 1
		if(slot == 1 || slot == 2 || slot == 3) {
			return 1;
		}
		return super.getSlotLimit(slot);
	}

}
