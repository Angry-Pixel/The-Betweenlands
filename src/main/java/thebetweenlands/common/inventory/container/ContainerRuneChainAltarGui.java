package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.client.gui.inventory.GuiRuneChainAltar;
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
}