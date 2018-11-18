package thebetweenlands.api.rune.gui;

import net.minecraft.util.ResourceLocation;

public interface IRuneContainer {
	public void init(IRuneContainerContext context);

	public void onMarkLinked(int input, IRuneLink link);

	public void onMarkUnlinked(int input, IRuneLink link);

	public void onLinksMoved(int fromRuneIndex, int toRuneIndex);

	public void onRuneShifted(int fromRuneIndex, int toRuneIndex);

	public ResourceLocation getId();
}
