package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.inventory.InventoryItem;

public class ContainerDraetonPouch extends ContainerPouch {
	private final EntityDraeton draeton;
	private final int index;

	public ContainerDraetonPouch(EntityPlayer player, InventoryPlayer playerInventory, InventoryItem itemInventory, EntityDraeton draeton, int index) {
		super(player, playerInventory, itemInventory);
		this.draeton = draeton;
		this.index = index;
		draeton.openStorage(player, index);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.draeton.closeStorage(playerIn, this.index);
	}
}
