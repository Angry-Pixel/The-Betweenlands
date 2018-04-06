package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import thebetweenlands.common.inventory.InventoryWeedwoodWorkbench;
import thebetweenlands.common.inventory.InventoryWeedwoodWorkbenchResult;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class ContainerWeedwoodWorkbench extends ContainerWorkbench {
	private final TileEntityWeedwoodWorkbench tile;

	public ContainerWeedwoodWorkbench(InventoryPlayer playerInventory, TileEntityWeedwoodWorkbench tile) {
		super(playerInventory, tile.getWorld(), tile.getPos());
		this.tile = tile;

		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();

		this.craftMatrix = new InventoryWeedwoodWorkbench(this, tile);
		this.craftMatrix.openInventory(playerInventory.player);

		this.craftResult = new InventoryWeedwoodWorkbenchResult(tile);

		//Result
		this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

		//Crafting matrix
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				this.addSlotToContainer(new Slot(this.craftMatrix, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 142));
		}

		tile.onCraftMatrixChanged();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tile.getWorld().getBlockState(this.tile.getPos()).getBlock() == BlockRegistry.WEEDWOOD_WORKBENCH && 
				playerIn.getDistanceSq((double)this.tile.getPos().getX() + 0.5D, (double)this.tile.getPos().getY() + 0.5D, (double)this.tile.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		this.craftMatrix.closeInventory(playerIn);
	}
}
