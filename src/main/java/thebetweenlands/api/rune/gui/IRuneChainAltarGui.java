package thebetweenlands.api.rune.gui;

import java.util.Collection;

public interface IRuneChainAltarGui {
	//TODO Maybe add a list of interactible rune marks?

	public Collection<IRuneGui> getOpenRuneGuis();

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
