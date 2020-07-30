package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import thebetweenlands.client.gui.inventory.GuiRuneCarvingTable;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

public class ContainerRuneCarvingTableGui extends ContainerRuneCarvingTable {
	private GuiRuneCarvingTable gui;

	public ContainerRuneCarvingTableGui(InventoryPlayer playerInventory, TileEntityRuneCarvingTable tile) {
		super(playerInventory, tile);
	}

	public void setGui(GuiRuneCarvingTable gui) {
		this.gui = gui;
	}

	@Override
	protected void onCrafting() {
		this.gui.onCrafting();
	}
}
