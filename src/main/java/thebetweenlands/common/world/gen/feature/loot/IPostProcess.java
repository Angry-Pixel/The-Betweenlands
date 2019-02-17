package thebetweenlands.common.world.gen.feature.loot;

import java.util.Random;

import net.minecraft.item.ItemStack;

public interface IPostProcess {
    public ItemStack postProcessItem(ItemStack is, Random rand);
}