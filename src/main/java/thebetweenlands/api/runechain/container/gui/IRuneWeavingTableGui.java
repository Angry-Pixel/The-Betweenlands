package thebetweenlands.api.runechain.container.gui;

import java.util.Collection;

public interface IRuneWeavingTableGui {
	public Collection<IRuneGui> getOpenRuneGuis();

	public boolean isRuneSlotInteractable(int runeIndex);

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
