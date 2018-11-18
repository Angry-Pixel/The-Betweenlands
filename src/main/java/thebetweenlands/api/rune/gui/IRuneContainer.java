package thebetweenlands.api.rune.gui;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneContainer {
	public void init(IRuneContainerContext context);

	public IRuneContainerContext getContext();

	public void onMarkLinked(int input, IRuneLink link);

	public void onMarkUnlinked(int input, IRuneLink link);

	public void onLinksMoved(int fromRuneIndex, int toRuneIndex);

	public void onRuneShifted(int fromRuneIndex, int toRuneIndex);

	public INodeBlueprint<?, RuneExecutionContext> getBlueprint();

	public INodeConfiguration getConfiguration();

	public ResourceLocation getId();
}
