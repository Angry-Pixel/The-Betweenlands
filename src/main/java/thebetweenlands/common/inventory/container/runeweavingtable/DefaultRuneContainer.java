package thebetweenlands.common.inventory.container.runeweavingtable;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.container.IRuneContainer;
import thebetweenlands.api.runechain.container.IRuneContainerContext;
import thebetweenlands.api.runechain.container.IRuneLink;
import thebetweenlands.api.runechain.container.gui.IRuneGui;
import thebetweenlands.client.gui.inventory.runeweavingtable.DefaultRuneGui;

public class DefaultRuneContainer implements IRuneContainer {
	protected final ResourceLocation id;

	protected IRuneContainerContext context;

	protected INodeBlueprint<?, IRuneExecutionContext> blueprint;

	protected IRuneGui gui;

	public DefaultRuneContainer(ResourceLocation id, INodeBlueprint<?, IRuneExecutionContext> blueprint) {
		this.id = id;
		this.blueprint = blueprint;
	}

	@Override
	public void setContext(IRuneContainerContext context) {
		this.context = context;
	}

	@Override
	public IRuneContainerContext getContext() {
		return this.context;
	}

	@Override
	public void init() {

	}

	@Override
	public void setGui(IRuneGui gui) {
		this.gui = gui;
	}

	@Override
	public void onTokenLinked(int input, IRuneLink link) {

	}

	@Override
	public void onTokenUnlinked(int input, IRuneLink link) {

	}

	@Override
	public void onRuneDataMoved(int fromRuneIndex, int toRuneIndex) {

	}

	@Override
	public void onRuneMoved(int fromRuneIndex, int toRuneIndex) {

	}

	@Override
	public void onConfigurationChanged(INodeConfiguration oldConfiguration, INodeConfiguration newConfiguration) {
		if(this.gui instanceof DefaultRuneGui) {
			((DefaultRuneGui) this.gui).initGui();
		}
	}

	@Override
	public INodeBlueprint<?, IRuneExecutionContext> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}
}
