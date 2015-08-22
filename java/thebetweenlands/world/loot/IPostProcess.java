package thebetweenlands.world.loot;

import java.util.Random;

import net.minecraft.item.ItemStack;

public interface IPostProcess {

	public ItemStack postProcessItem(ItemStack is, Random rand);
}
