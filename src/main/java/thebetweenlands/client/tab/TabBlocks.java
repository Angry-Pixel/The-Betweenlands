package thebetweenlands.client.tab;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.BlockRegistry;

public class TabBlocks extends CreativeTabBetweenlands {

	public TabBlocks() {
		super("thebetweenlands.block");
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(BlockRegistry.SWAMP_GRASS);
	}
}
