package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
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
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.SWAMP_REED_ITEM;
	}


	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ItemRegistry.SWAMP_REED_ITEM);
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
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}