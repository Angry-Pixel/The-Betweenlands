package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.world.World;

public class ContainerBLCraftingTable extends ContainerWorkbench {

	public ContainerBLCraftingTable(InventoryPlayer playerInventory, World world, int x, int y, int z) {
		super(playerInventory, world, x, y, z);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}