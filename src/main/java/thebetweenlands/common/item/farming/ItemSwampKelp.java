package thebetweenlands.common.item.farming;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemSwampKelp extends ItemPlantable {
	public ItemSwampKelp() {
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	@Override
	protected Block getBlock(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos).getMaterial() == Material.WATER ? BlockRegistry.SWAMP_KELP : null;
	}
}
