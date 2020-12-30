package thebetweenlands.common.item.herblore.rune;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.container.IRuneContainer;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.container.gui.IRuneGui;
import thebetweenlands.api.runechain.container.gui.RuneMenuType;
import thebetweenlands.client.gui.inventory.runeweavingtable.DefaultRuneGui;
import thebetweenlands.common.inventory.container.runeweavingtable.DefaultRuneContainer;

public class DefaultRuneContainerFactory implements IRuneContainerFactory {
	private final ResourceLocation regName;

	private final Supplier<INodeBlueprint<?, IRuneExecutionContext>> blueprintFactory;

	private boolean blueprintSet = false;
	private INodeBlueprint<?, IRuneExecutionContext> blueprint;

	public DefaultRuneContainerFactory(ResourceLocation regName, Supplier<INodeBlueprint<?, IRuneExecutionContext>> blueprintFactory) {
		this.regName = regName;
		this.blueprintFactory = blueprintFactory;
	}

	public DefaultRuneContainerFactory(ResourceLocation regName, INodeBlueprint<?, IRuneExecutionContext> blueprint) {
		this(regName, () -> blueprint);
	}

	@Override
	public IRuneContainer createContainer() {
		if(!this.blueprintSet) {
			this.blueprint = this.blueprintFactory.get();
			this.blueprintSet = true;
		}
		return new DefaultRuneContainer(this.regName, this.blueprint);
	}

	@Override
	public ResourceLocation getId() {
		return this.regName;
	}

	@Override
	public IRuneGui createGui(RuneMenuType menu) {
		return new DefaultRuneGui(menu);
	}
}
