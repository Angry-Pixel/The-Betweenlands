package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class BlockSwampReed extends BlockStackablePlant implements ICustomItemBlock {
	public BlockSwampReed() {
		this.setMaxHeight(4);
		this.setCreativeTab(null);
	}

	@Override
	protected boolean isSamePlant(IBlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.SWAMP_REED_UNDERWATER;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || state.getBlock() == BlockRegistry.SWAMP_REED_UNDERWATER || SurfaceType.SAND.matches(state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.SWAMP_REED_ITEM;
	}

	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		boolean canPlace = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) &&
				soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
		if(canPlace) {
			if(this.isSamePlant(worldIn.getBlockState(pos.down())))
				return true;
			BlockPos blockpos = pos.down();
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				if (worldIn.isBlockLoaded(blockpos.offset(enumfacing))) {
					IBlockState iblockstate = worldIn.getBlockState(blockpos.offset(enumfacing));
					if (iblockstate.getMaterial() == Material.WATER) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		if(soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this)) {
			if(this.isSamePlant(worldIn.getBlockState(pos.down())))
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
	@OnlyIn(Dist.CLIENT)
	public void animateTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.rand.nextInt(65) == 0) {
			BLParticles.MOSQUITO.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
