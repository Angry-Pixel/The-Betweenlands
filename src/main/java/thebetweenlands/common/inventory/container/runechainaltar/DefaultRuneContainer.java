package thebetweenlands.common.inventory.container.runechainaltar;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
import thebetweenlands.api.rune.gui.IRuneLink;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public class DefaultRuneContainer implements IRuneContainer {
	protected final ResourceLocation id;

	protected IRuneContainerContext context;

	protected INodeBlueprint<?, RuneExecutionContext> blueprint;

	public DefaultRuneContainer(ResourceLocation id, INodeBlueprint<?, RuneExecutionContext> blueprint) {
		this.id = id;
		this.blueprint = blueprint;
	}

	@Override
	public void init(IRuneContainerContext context) {
		this.context = context;
	}

	@Override
	public IRuneContainerContext getContext() {
		return this.context;
	}

	@Override
	public void onMarkLinked(int input, IRuneLink link) {

	}

	@Override
	public void onMarkUnlinked(int input, IRuneLink link) {

	}

	@Override
	public void onLinksMoved(int fromRuneIndex, int toRuneIndex) {

	}

	@Override
	public void onRuneShifted(int fromRuneIndex, int toRuneIndex) {

	}

	@Override
	public void onConfigurationChanged(INodeConfiguration oldConfiguration, INodeConfiguration newConfiguration) {

	}

	@Override
	public INodeBlueprint<?, RuneExecutionContext> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}
}
