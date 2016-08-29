package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class BlockSwampReed extends BlockStackablePlant {
	public BlockSwampReed() {
		super(false);
		this.setMaxHeight(4);
	}

	@Override
	protected boolean isSamePlant(Block block) {
		return super.isSamePlant(block) || block == BlockRegistry.SWAMP_REED_UNDERWATER;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || state.getBlock() == BlockRegistry.SWAMP_REED_UNDERWATER || SurfaceType.SAND.matches(state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.SWAMP_REED_ITEM;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(ItemRegistry.SWAMP_REED_ITEM));
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		boolean canPlace = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) &&
				soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
		if(canPlace) {
			if(worldIn.getBlockState(pos.down()).getBlock() == this)
				return true;
			BlockPos blockpos = pos.down();
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				IBlockState iblockstate = worldIn.getBlockState(blockpos.offset(enumfacing));
				if (iblockstate.getMaterial() == Material.WATER) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		if(soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this)) {
			if(worldIn.getBlockState(pos.down()).getBlock() == this)
				return true;
			BlockPos blockpos = pos.down();
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				IBlockState iblockstate = worldIn.getBlockState(blockpos.offset(enumfacing));
				if (iblockstate.getMaterial() == Material.WATER) {
					return true;
				}
			}
		}
		return false;
	}
}
