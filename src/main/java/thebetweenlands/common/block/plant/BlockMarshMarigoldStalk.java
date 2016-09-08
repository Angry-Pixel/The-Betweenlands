package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockMarshMarigoldStalk extends BlockStackablePlantUnderwater {
	public BlockMarshMarigoldStalk() {
		super(true);
		this.setMaxHeight(1);
	}

	@Override
	protected boolean isSamePlant(Block block) {
		return super.isSamePlant(block) || block == BlockRegistry.MARSH_MARIGOLD_FLOWER;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, BlockRegistry.MARSH_MARIGOLD_STALK.getDefaultState());
		worldIn.setBlockState(pos.up(), BlockRegistry.MARSH_MARIGOLD_FLOWER.getDefaultState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return worldIn.isAirBlock(pos.up()) && worldIn.getBlockState(pos).getMaterial() == Material.WATER && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
	}
	
	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(IS_TOP, IS_BOTTOM);
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(BlockRegistry.MARSH_MARIGOLD_FLOWER)));
	}
}
