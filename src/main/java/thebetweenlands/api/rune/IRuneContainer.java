package thebetweenlands.api.rune;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneContainer {
	public void setContext(IRuneContainerContext context);
	
	public IRuneContainerContext getContext();
	
	public void init();

	public void onMarkLinked(int input, IRuneLink link);

	public void onMarkUnlinked(int input, IRuneLink link);

	public void onRuneDataMoved(int fromRuneIndex, int toRuneIndex);

	public void onRuneMoved(int fromRuneIndex, int toRuneIndex);
	
	public void onConfigurationChanged(INodeConfiguration oldConfiguration, INodeConfiguration newConfiguration);

	/**
	 * Returns the node blueprint of this rune. May be called before {@link #init()}.
	 * @return node blueprint of this rune
	 */
	public INodeBlueprint<?, RuneExecutionContext> getBlueprint();

	public ResourceLocation getId();
}
