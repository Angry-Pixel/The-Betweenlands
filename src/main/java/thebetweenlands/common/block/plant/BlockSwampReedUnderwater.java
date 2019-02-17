package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSwampReedUnderwater extends BlockStackablePlantUnderwater {
	public BlockSwampReedUnderwater() {
		this.resetAge = true;
		this.setHardness(0.1F);
		this.setCreativeTab(null);
	}

	@Override
	protected boolean isSamePlant(IBlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.SWAMP_REED;
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.SWAMP_REED_ITEM;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	@Override
	protected boolean canGrowUp(World world, BlockPos pos, IBlockState state, int height) {
		return world.getBlockState(pos.up()) != this && 
				(world.getBlockState(pos.up()).getMaterial() == Material.WATER || (world.getBlockState(pos).getMaterial() == Material.WATER && world.isAirBlock(pos.up()))) 
				&& (this.maxHeight == -1 || height < this.maxHeight);
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		if(!world.getBlockState(pos.up()).getMaterial().isLiquid()) {
			world.setBlockState(pos.up(), BlockRegistry.SWAMP_REED.getDefaultState());
		} else {
			world.setBlockState(pos.up(), this.getDefaultState());
		}
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(ItemRegistry.SWAMP_REED_ITEM));
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}