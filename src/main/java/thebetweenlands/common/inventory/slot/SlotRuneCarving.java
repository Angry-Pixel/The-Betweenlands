package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class SlotRuneCarving extends SlotPassthroughCraftingOutput {

	public SlotRuneCarving(EntityPlayer player, InventoryCrafting craftingMatrix, IInventory resultInventory,
			int slotIndex, int xPosition, int yPosition, SlotPassthroughCraftingInput source) {
		super(player, craftingMatrix, resultInventory, slotIndex, xPosition, yPosition, source);
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		this.onCrafting(stack);
		this.source.onTake(this.player, this.craftMatrix.decrStackSize(0, 1));
		return stack;
	}
}
