package thebetweenlands.items.tools;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public interface IHarvestable {
    public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z);
    public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune);
}