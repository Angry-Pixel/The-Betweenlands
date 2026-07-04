package thebetweenlands.common.capability;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface ICompostHandlerModifiable extends ICompostHandler, IItemHandlerModifiable {

	public void setCompostInSlot(int slot, ItemStack stack, int compostAmount, int compostProgress, int compostDuration);
	
}
