package thebetweenlands.api.rune;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneContainer {
	public void setContext(IRuneContainerContext context);

	public IRuneContainerContext getContext();

	public void init();

	public void setGui(@Nullable IRuneGui gui);
	
	public void onTokenLinked(int input, IRuneLink link);

	public void onTokenUnlinked(int input, IRuneLink link);

	public void onRuneDataMoved(int fromRuneIndex, int toRuneIndex);

	public void onRuneMoved(int fromRuneIndex, int toRuneIndex);

	public void onConfigurationChanged(INodeConfiguration oldConfiguration, INodeConfiguration newConfiguration);

	/**
	 * Returns the node blueprint of this rune. May be called before {@link #init()}.
	 * @return node blueprint of this rune
	 */
	public INodeBlueprint<?, RuneExecutionContext> getBlueprint();

	public ResourceLocation getId();

	/**
	 * Updates the rune container for the given item stack. If no changes are required return {@code this}, otherwise return {@code factory.createContainer()}.
	 * If the returned {@link IRuneContainer} is not equal to {@code this} the current rune container ({@code this}) will be replaced with the returned container.
	 * @param stack Item stack that has changed and to which this rune container belongs
	 * @param factory {@link IRuneContainerFactory} of the changed item stack
	 * @return {@code this} if no change is required, {@code factory.createContainer()} otherwise
	 */
	public default IRuneContainer updateRuneContainer(ItemStack stack, IRuneContainerFactory factory) {
		IRuneContainer newContainer = factory.createContainer();
		if(!this.getId().equals(newContainer.getId())) {
			return newContainer;
		}
		return this;
	}
}
