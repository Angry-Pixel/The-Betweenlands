package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.InventoryPlayer;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.inventory.container.ContainerDraetonWorkbench;

public class GuiDraetonCrafting extends GuiCrafting {
	public GuiDraetonCrafting(InventoryPlayer player, EntityDraeton draeton, int slot) {
		super(player, draeton.getWorld());
		this.inventorySlots = new ContainerDraetonWorkbench(player, draeton, slot);
	}
}
