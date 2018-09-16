package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.terrain.BlockSwampGrass;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class BlockDugGrass extends BlockGenericDugSoil implements ITintedBlock {
	public BlockDugGrass(boolean purified) {
		super(Material.GRASS, purified);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		if(!world.isRemote) {
			TileEntityDugSoil te = getTile(world, pos);

			if(te != null) {
				if(!te.isComposted() && rand.nextInt(20) == 0) {
					if(this.isPurified(world, pos, state)) {
						world.setBlockState(pos, BlockRegistry.DUG_PURIFIED_SWAMP_GRASS.getDefaultState());
					} else {
						world.setBlockState(pos, BlockRegistry.SWAMP_GRASS.getDefaultState());
					}
				} else {
					if(world.getBlockState(pos.up()).getLightOpacity(world, pos.up()) > 2) {
						world.setBlockState(pos, BlockRegistry.DUG_SWAMP_DIRT.getDefaultState());
						BlockGenericDugSoil.copy(world, pos, te);
					}
				}
			}

			BlockSwampGrass.updateGrass(world, pos, rand);
		}
	}

	@Override
	public IBlockState getUnpurifiedDugSoil(World world, BlockPos pos, IBlockState state) {
		return BlockRegistry.DUG_SWAMP_GRASS.getDefaultState().withProperty(COMPOSTED, state.getValue(COMPOSTED)).withProperty(DECAYED, state.getValue(DECAYED));
	}
	
	@Override
	public int getPurifiedHarvests(World world, BlockPos pos, IBlockState state) {
		return 6;
	}
}
