package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.gui.IRuneMenuFactory;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneItem {
	//TODO Use capability instead?

	public INodeBlueprint<?, RuneExecutionContext> getRuneBlueprint(ItemStack stack, NBTTagCompound data /*TODO From rune container*/);

	public IRuneMenuFactory getRuneMenuFactory(ItemStack stack);
}
