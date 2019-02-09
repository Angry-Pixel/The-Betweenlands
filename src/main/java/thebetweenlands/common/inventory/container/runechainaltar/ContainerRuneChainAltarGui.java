package thebetweenlands.common.inventory.container.runechainaltar;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.api.rune.IRuneChainAltarGui;
import thebetweenlands.client.gui.inventory.runechainaltar.GuiRuneChainAltar;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;

public class ContainerRuneChainAltarGui extends ContainerRuneChainAltar {
	private GuiRuneChainAltar gui;

	public ContainerRuneChainAltarGui(EntityPlayer player, TileEntityRuneChainAltar tile) {
		super(player, tile);
	}

	public void setGui(GuiRuneChainAltar gui) {
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
	protected IRuneChainAltarGui getRuneChainAltarGui() {
		return this.gui;
	}
}