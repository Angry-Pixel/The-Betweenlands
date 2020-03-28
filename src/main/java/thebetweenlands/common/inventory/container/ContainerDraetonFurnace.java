package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.tile.TileEntityBLFurnace;

public class ContainerDraetonFurnace extends ContainerBLFurnace {
	private final EntityDraeton draeton;
	private final int index;
	
	public ContainerDraetonFurnace(EntityPlayer player, InventoryPlayer inventory, TileEntityBLFurnace tile, EntityDraeton draeton, int index) {
		super(inventory, tile);
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
