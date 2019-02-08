package thebetweenlands.api.rune.gui;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneContainer {
	public void setContext(IRuneContainerContext context);
	
	public IRuneContainerContext getContext();
	
	public void init();

	public void onMarkLinked(int input, IRuneLink link);

	public void onMarkUnlinked(int input, IRuneLink link);

	public void onLinksMoved(int fromRuneIndex, int toRuneIndex);

	public void onRuneShifted(int fromRuneIndex, int toRuneIndex);
	
	public void onConfigurationChanged(INodeConfiguration oldConfiguration, INodeConfiguration newConfiguration);

	/**
	 * Returns the node blueprint of this rune. May be called before {@link #init()}.
	 * @return node blueprint of this rune
	 */
	public INodeBlueprint<?, RuneExecutionContext> getBlueprint();

	public ResourceLocation getId();
}
