package thebetweenlands.api.runechain.container;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.IConfigurationOutput;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;

public interface IRuneWeavingTableContainer {
	public int getRuneInventorySize();

	public Slot getRuneSlot(int runeIndex);

	public int getSelectedRuneIndex();

	public void setSelectedRune(int runeIndex);

	public ItemStack getRuneItemStack(int runeIndex);

	public void setRuneItemStack(int runeIndex, ItemStack stack);

	public void shiftRune(int runeIndex, boolean back);

	public int getRuneShiftHoleIndex(int runeIndex, boolean back);

	public Collection<Integer> getLinkedInputs(int runeIndex);

	@Nullable
	public IRuneLink getLink(int runeIndex, int input);

	public boolean link(int runeIndex, int input, int outputRuneIndex, int output);

	@Nullable
	public IRuneLink unlink(int runeIndex, int input);

	public void unlinkAll(int runeIndex);

	public void moveRuneData(int fromRuneIndex, int toRuneIndex);

	@Nullable
	public IRuneContainer getRuneContainer(int runeIndex);

	/**
	 * Called when runes have changed, i.e. rune is added/removed, token input is linked/unlinked.
	 * Custom {@link IRuneContainer}s must call this only if its node blueprint has changed, i.e. if anything of the rune's
	 * {@link IRuneContainer} has changed that affects the node blueprint.
	 * This method is what triggers the update of the output rune chain stack, similarly to {@link Container#onCraftMatrixChanged(net.minecraft.inventory.IInventory)}.
	 * @param runeIndex the index of the rune that has changed
	 */
	public void onRunesChanged();
	
	public default IConfigurationLinkAccess createLinkAccess(int node) {
		return input -> {
			IRuneLink link = this.getLink(node, input);
			if(link != null && link.getOutput() >= 0) {
				IRuneContainer container = this.getRuneContainer(link.getOutputRune());
				if(container != null) {
					INodeConfiguration configuration = container.getContext().getConfiguration();
					if(configuration != null) {
						List<? extends IConfigurationOutput> outputs = configuration.getOutputs();
						if(link.getOutput() < outputs.size()) {
							return outputs.get(link.getOutput());
						}
					}
				}
			}
			return null;
		};
	}
}
