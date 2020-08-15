package thebetweenlands.common.item.herblore.rune;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.IRuneGui;
import thebetweenlands.api.rune.RuneMenuType;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.client.gui.inventory.runeweavingtable.DefaultRuneGui;
import thebetweenlands.common.inventory.container.runeweavingtable.DefaultRuneContainer;

public class DefaultRuneContainerFactory implements IRuneContainerFactory {
	private final ResourceLocation regName;

	private final Supplier<INodeBlueprint<?, RuneExecutionContext>> blueprintFactory;

	private boolean blueprintSet = false;
	private INodeBlueprint<?, RuneExecutionContext> blueprint;

	public DefaultRuneContainerFactory(ResourceLocation regName, Supplier<INodeBlueprint<?, RuneExecutionContext>> blueprintFactory) {
		this.regName = regName;
		this.blueprintFactory = blueprintFactory;
	}

	public DefaultRuneContainerFactory(ResourceLocation regName, INodeBlueprint<?, RuneExecutionContext> blueprint) {
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
