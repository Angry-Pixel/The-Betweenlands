package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ContainerDraetonWorkbench extends ContainerWorkbench {
	private final EntityDraeton draeton;
	private final int slot;

	public ContainerDraetonWorkbench(InventoryPlayer playerInventory, EntityDraeton draeton, int slot) {
		super(playerInventory, draeton.getWorld(), BlockPos.ORIGIN);
		this.draeton = draeton;
		this.slot = slot;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if(playerIn.getDistanceSq(draeton) <= 64.0D) {
			IInventory inv = this.draeton.getUpgradesInventory();
			if(this.slot >= 0 && this.slot < inv.getSizeInventory()) {
				ItemStack stack = inv.getStackInSlot(this.slot);
				if(!stack.isEmpty() && this.draeton.isCraftingUpgrade(stack)) {
					return true;
				}
			}
		}
		return false;
	}
}
