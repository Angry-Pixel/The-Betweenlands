package thebetweenlands.common.item.herblore.rune;

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
	private final INodeBlueprint<?, RuneExecutionContext> blueprint;
	
	public DefaultRuneContainerFactory(ResourceLocation regName, INodeBlueprint<?, RuneExecutionContext> blueprint) {
		this.regName = regName;
		this.blueprint = blueprint;
	}

	@Override
	public IRuneContainer createContainer() {
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
