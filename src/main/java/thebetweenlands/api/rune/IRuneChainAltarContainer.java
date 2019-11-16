package thebetweenlands.api.rune;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public interface IRuneChainAltarContainer {
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
	 * Called when runes have changed, i.e. rune is added/removed, mark input is linked/unlinked.
	 * Custom {@link IRuneContainer}s must call this only if its node blueprint has changed, i.e. if anything of the rune's
	 * {@link IRuneContainer} has changed that affects the node blueprint.
	 * This method is what triggers the update of the output rune chain stack, similarly to {@link Container#onCraftMatrixChanged(net.minecraft.inventory.IInventory)}.
	 * @param runeIndex - index of the rune that has changed
	 */
	public void onRunesChanged();
}
