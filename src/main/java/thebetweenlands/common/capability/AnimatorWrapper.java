package thebetweenlands.common.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;

public class AnimatorWrapper extends SidedInvWrapper {

	private final AnimatorBlockEntity entity;
	
	public AnimatorWrapper(AnimatorBlockEntity entity, Direction context) {
		super(entity, context);
		this.entity = entity;
	}
	
	@Override
	public int getSlotLimit(int slot) {
		if (getSlot(this.inv, slot, this.side) == AnimatorBlockEntity.FOCAL_SLOT && !entity.hasOutputItems) {
			return 1;
		}
		return super.getSlotLimit(slot);
	}
}
