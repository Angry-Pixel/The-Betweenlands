package thebetweenlands.common.inventory.container.runechainaltar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.rune.gui.IRuneChainAltarContainer;
import thebetweenlands.api.rune.gui.IRuneChainAltarGui;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
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
	protected IRuneContainerContext createRuneContainerContext(RuneContainerEntry entry) {
		return new IRuneContainerContext() {
			@Override
			public IRuneChainAltarContainer getRuneChainAltarContainer() {
				return ContainerRuneChainAltarGui.this;
			}

			@Override
			public IRuneChainAltarGui getRuneChainAltarGui() {
				return ContainerRuneChainAltarGui.this.gui;
			}

			@Override
			public int getRuneIndex() {
				return entry.runeIndex;
			}

			@Override
			public ItemStack getRuneItemStack() {
				return ContainerRuneChainAltarGui.this.getRuneItemStack(entry.runeIndex);
			}

			@Override
			public NBTTagCompound getData() {
				return ContainerRuneChainAltarGui.this.altar.getChainInfo().getContainerData(entry.runeIndex);
			}

			@Override
			public void setData(NBTTagCompound nbt) {
				ContainerRuneChainAltarGui.this.altar.getChainInfo().setContainerData(entry.runeIndex, nbt);
			}

			@Override
			public void addSlot(Slot slot) {
				entry.slots.add(slot);
			}
		};
	}
}