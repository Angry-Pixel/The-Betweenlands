package thebetweenlands.common.inventory.container.runeweavingtable;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneWeavingTableGui;
import thebetweenlands.client.gui.inventory.runeweavingtable.GuiRuneWeavingTable;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;

public class ContainerRuneWeavingTableGui extends ContainerRuneWeavingTable {
	private GuiRuneWeavingTable gui;

	public ContainerRuneWeavingTableGui(EntityPlayer player, TileEntityRuneWeavingTable tile) {
		super(player, tile);
	}

	public void setGui(GuiRuneWeavingTable gui) {
		this.gui = gui;
	}

	@Override
	public void setSelectedRune(int runeIndex) {
		int prevSelected = this.getSelectedRuneIndex();

		super.setSelectedRune(runeIndex);

		if(prevSelected != runeIndex) {
			this.gui.onSetSelectedRune(runeIndex);
		}
	}

	@Override
	protected IRuneWeavingTableGui getRuneWeavingTableGui() {
		return this.gui;
	}
}