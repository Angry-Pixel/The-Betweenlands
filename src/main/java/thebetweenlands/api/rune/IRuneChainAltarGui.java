package thebetweenlands.api.rune;

import java.util.Collection;

public interface IRuneChainAltarGui {
	public Collection<IRuneGui> getOpenRuneGuis();

	public boolean isRuneSlotInteractable(int runeIndex);

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
