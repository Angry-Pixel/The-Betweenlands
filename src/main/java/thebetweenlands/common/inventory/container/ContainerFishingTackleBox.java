package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.slot.SlotExclusion;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityFishingTackleBox;

public class ContainerFishingTackleBox extends Container {

    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
	private final EntityPlayer player;
	public ItemStack fishingTackleBoxStack = new ItemStack(BlockRegistry.FISHING_TACKLE_BOX);

	public ContainerFishingTackleBox(EntityPlayer player, TileEntityFishingTackleBox tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;

		addSlotToContainer(new SlotCrafting(playerInventory.player, craftMatrix, craftResult, 0, 152, 46));

		for (int y = 0; y < 2; ++y)
			for (int x = 0; x < 2; ++x)
				addSlotToContainer(new Slot(craftMatrix, x + y * 2, 98 + x * 18, 36 + y * 18));

		for (int j = 0; j < 4; ++j)
			for (int k = 0; k < 4; ++k)
				addSlotToContainer(new SlotExclusion(tile, k + j * 4, 8 + k * 18, 18 + j * 18, fishingTackleBoxStack, 64, this));

		for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 104 + l * 18));

        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 162));
	}

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        slotChangedCraftingGrid(player.world, player, craftMatrix, craftResult);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        craftResult.clear();
        if (!playerIn.world.isRemote)
            clearContainer(playerIn, playerIn.world, craftMatrix);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn){
        return true;
    }

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack is = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack is1 = slot.getStack();
			is = is1.copy();

			if (slotIndex == 0) {
				if (!this.mergeItemStack(is1, 5, 57, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(is1, is);
			} else if (slotIndex >= 1 && slotIndex < 5) {
				if (!this.mergeItemStack(is1, 5, 57, false))
					return ItemStack.EMPTY;
			} else if (slotIndex >= 5 && slotIndex < 21) {
				if (!mergeItemStack(is1, 21, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			} else if (!mergeItemStack(is1, 5, 21, false))
				return ItemStack.EMPTY;

			if (is1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (is1.getCount() == is.getCount())
				return ItemStack.EMPTY;

			ItemStack is2 = slot.onTake(player, is1);

			if (slotIndex == 0)
				player.dropItem(is2, false);
		}

		return is;
	}

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)  {
        return slotIn.inventory != craftResult && super.canMergeSlot(stack, slotIn);
    }

}