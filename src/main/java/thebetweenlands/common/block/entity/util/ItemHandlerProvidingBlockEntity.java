package thebetweenlands.common.block.entity.util;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;

public interface ItemHandlerProvidingBlockEntity {

	public IItemHandler getItemHandlerCapability(Direction context);
	
}
