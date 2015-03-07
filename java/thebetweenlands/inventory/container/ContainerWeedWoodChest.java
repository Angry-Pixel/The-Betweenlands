package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;

public class ContainerWeedWoodChest extends ContainerChest {

	public ContainerWeedWoodChest(IInventory player, IInventory chest) {
		super(player, chest);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}