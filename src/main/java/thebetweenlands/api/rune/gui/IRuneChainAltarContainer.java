package thebetweenlands.api.rune.gui;

import java.util.Collection;

import javax.annotation.Nullable;

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

	public void moveAllLinks(int fromRuneIndex, int toRuneIndex);

	@Nullable
	public IRuneContainer getRuneContainer(int runeIndex);
}
