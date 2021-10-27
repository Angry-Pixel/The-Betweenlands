package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockDeadGrass extends BasicBlock implements IGrowable {
	public BlockDeadGrass() {
		super(Material.GRASS);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			if(worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
				worldIn.setBlockState(pos, BlockRegistry.SWAMP_DIRT.getDefaultState());
			}
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return BlockRegistry.SWAMP_DIRT.getItemDropped(BlockRegistry.SWAMP_DIRT.getDefaultState(), rand, fortune);
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		//TODO: Bonemeal growing
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
		if(super.canSustainPlant(state, world, pos, direction, plantable)) {
			return true;
		}

		EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		switch(plantType) {
		case Beach:
			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
			world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
			world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
			world.getBlockState(pos.south()).getMaterial() == Material.WATER);
			return hasWater;
		case Plains:
			return true;
		default:
			return false;
		}
	}
}