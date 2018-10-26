package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerItemNaming extends Container {
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
