package thebetweenlands.common.item.herblore.rune.properties;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;

public class DefaultRuneItemProperties extends RuneItemProperties {
	private final IRuneContainerFactory factory;

	public DefaultRuneItemProperties(ResourceLocation regName, Supplier<INodeBlueprint<?, RuneExecutionContext>> blueprintFactory) {
		this.factory = new DefaultRuneContainerFactory(regName, blueprintFactory);
	}

	public DefaultRuneItemProperties(ResourceLocation regName, INodeBlueprint<?, RuneExecutionContext> blueprint) {
		this.factory = new DefaultRuneContainerFactory(regName, blueprint);
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return this.factory;
	}
}
