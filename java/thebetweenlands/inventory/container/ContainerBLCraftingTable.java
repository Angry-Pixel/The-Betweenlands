package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import thebetweenlands.inventory.InventoryBLCrafting;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;

public class ContainerBLCraftingTable extends ContainerWorkbench {

	public ContainerBLCraftingTable(InventoryPlayer playerInventory, TileEntityBLCraftingTable tile) {
		super(playerInventory, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        this.craftMatrix = new InventoryBLCrafting(this, tile);

        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        int l;
        int i1;

        for (l = 0; l < 3; ++l)
        {
            for (i1 = 0; i1 < 3; ++i1)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
            }
        }

        for (l = 0; l < 3; ++l)
        {
            for (i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInventory, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

    @Override
    public void onContainerClosed(EntityPlayer p_75134_1_) {
//        super.onContainerClosed(p_75134_1_);
    }
}
