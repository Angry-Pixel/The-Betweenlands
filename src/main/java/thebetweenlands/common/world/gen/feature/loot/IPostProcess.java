package thebetweenlands.common.world.gen.feature.loot;

import net.minecraft.item.ItemStack;

import java.util.Random;

public interface IPostProcess {
    public ItemStack postProcessItem(ItemStack is, Random rand);
}