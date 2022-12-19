package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSyrmoriteShears extends ItemShears implements IAnimatorRepairable {
	public ItemSyrmoriteShears() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_SYRMORITE);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_SYRMORITE);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_SYRMORITE);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_SYRMORITE);
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
