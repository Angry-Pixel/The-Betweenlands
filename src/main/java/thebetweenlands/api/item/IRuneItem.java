package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.gui.IRuneSubMenuFactory;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneItem {
	//TODO Use capability instead?

	public INodeBlueprint<?, RuneExecutionContext> getRuneBlueprint(ItemStack stack);

	public IRuneSubMenuFactory getSubMenuFactory(ItemStack stack);
}
