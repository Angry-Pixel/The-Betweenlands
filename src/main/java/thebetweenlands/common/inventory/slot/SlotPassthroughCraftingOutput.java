package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;

public class SlotPassthroughCraftingOutput extends SlotCrafting {
	protected final SlotPassthroughCraftingInput source;

	protected final EntityPlayer player;
	protected final InventoryCrafting craftMatrix;

	public SlotPassthroughCraftingOutput(EntityPlayer player, InventoryCrafting craftingMatrix, IInventory resultInventory, int slotIndex, int xPosition, int yPosition, SlotPassthroughCraftingInput source) {
		super(player, craftingMatrix, resultInventory, slotIndex, xPosition, yPosition);
		this.player = player;
		this.craftMatrix = craftingMatrix;
		this.source = source;
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		//Same as super.onTake, but calls this.source.onTake when crafting matrix items are removed

		this.onCrafting(stack);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
		NonNullList<ItemStack> remainingItems = CraftingManager.getRemainingItems(this.craftMatrix, thePlayer.world);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

		for(int i = 0; i < remainingItems.size(); ++i) {
			ItemStack currentStack = this.craftMatrix.getStackInSlot(i);
			ItemStack remainingStack = remainingItems.get(i);

			if(!currentStack.isEmpty()) {
				this.source.onTake(this.player, this.craftMatrix.decrStackSize(i, 1));
				currentStack = this.craftMatrix.getStackInSlot(i);
			}

			if(!remainingStack.isEmpty()) {
				if(currentStack.isEmpty()) {
					this.craftMatrix.setInventorySlotContents(i, remainingStack);
				} else if(ItemStack.areItemsEqual(currentStack, remainingStack) && ItemStack.areItemStackTagsEqual(currentStack, remainingStack)) {
					remainingStack.grow(currentStack.getCount());
					this.craftMatrix.setInventorySlotContents(i, remainingStack);
				} else if(!this.player.inventory.addItemStackToInventory(remainingStack)) {
					this.player.dropItem(remainingStack, false);
				}
			}
		}

		return stack;
	}
}
