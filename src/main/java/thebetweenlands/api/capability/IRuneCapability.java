package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.impl.RuneChainComposition;

public interface IRuneCapability {
	public IRuneContainerFactory getRuneContainerFactory();
}
