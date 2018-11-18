package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.gui.IRuneMenuFactory;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneItem {
	//TODO Use capability instead?

	//TODO Remove from this interface

	public IRuneMenuFactory getRuneMenuFactory(ItemStack stack);
}
