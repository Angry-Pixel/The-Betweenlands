package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemSyrmoriteShears extends ItemShears {
	public ItemSyrmoriteShears() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}
	
	//TODO: For sickle
	/*
	 * protected List<ItemStack> getDefaultDrops(World worldIn, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> finalItems = new ArrayList<>();
		List<ItemStack> items = getDrops(worldIn, pos, state, fortune);
        float chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, 1.0F, false, harvesters.get());
        for (ItemStack item : items) {
            if (worldIn.rand.nextFloat() <= chance) {
                finalItems.add(item);
            }
        }
        return finalItems;
	}
	 */
}
