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
	protected INodeConfiguration configuration;

	public DefaultRuneContainer(ResourceLocation id) {
		this.id = id;
	}

	public DefaultRuneContainer setBlueprint(INodeBlueprint<?, RuneExecutionContext> blueprint) {
		this.blueprint = blueprint;
		this.configuration = this.blueprint.getConfigurations().iterator().next();
		return this;
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
	public INodeBlueprint<?, RuneExecutionContext> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public INodeConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}
}
