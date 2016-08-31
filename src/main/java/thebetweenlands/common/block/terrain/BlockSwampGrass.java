package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSwampGrass extends BasicBlock implements IGrowable, ITintedBlock {

	public BlockSwampGrass() {
		super(Material.GRASS);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			if(worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
				worldIn.setBlockState(pos, BlockRegistry.SWAMP_DIRT.getDefaultState());
			} else {
				for (int i = 0; i < 4; ++i) {
					BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

					if (blockPos.getY() >= 0 && blockPos.getY() < 256 && !worldIn.isBlockLoaded(blockPos)) {
						return;
					}

					IBlockState blockStateAbove = worldIn.getBlockState(blockPos.up());
					IBlockState blockState = worldIn.getBlockState(blockPos);

					if (blockState.getBlock() == BlockRegistry.SWAMP_DIRT && blockStateAbove.getLightOpacity(worldIn, pos.up()) <= 2) {
						worldIn.setBlockState(blockPos, BlockRegistry.SWAMP_GRASS.getDefaultState());
					}
				}
			}
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
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
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}