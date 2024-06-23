package thebetweenlands.common.blocks;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsGrassBlock extends BetweenlandsBlock implements BlockColor, BonemealableBlock {

	public BetweenlandsGrassBlock(Properties props) {
		super(props);
	}

	//TODO: Verify logic
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!canBeGrass(state, level, pos)) {
			if (!level.isAreaLoaded(pos, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
			revertToDirt(level, pos);
		} else {
			if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
				for (int i = 0; i < 4; ++i) {
					BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					spreadGrassTo(level, blockpos);
				}
			}

		}
	}

	private static boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.above();
		BlockState blockstate = level.getBlockState(blockpos);
		if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			int i = LightEngine.getLightBlockInto(level, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(level, blockpos));
			return i < level.getMaxLightLevel();
		}
	}

	//TODO: I reckon this could be a bit more efficient, rather than rely on hardcoded blocks - maybe a parameter?
	public static void revertToDirt(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.is(BlockRegistry.SWAMP_GRASS)) {
			level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
		}

		/*Dug Swamp Grass to Dug Swamp Dirt*/
		/*Dug Purified Swamp Grass to Dug Purified Swamp Dirt*/
		/*Set compost and decay*/
	}

	public static void spreadGrassTo(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (canPropagate(state, level, pos)) {
			if(state.is(BlockRegistry.SWAMP_DIRT)) {
				level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_GRASS.get().defaultBlockState());
			}

			/*Dug Swamp Dirt to Dug Swamp Grass*/
			/*Dug Purified Swamp Dirt to Dug Purified Swamp Grass*/
			/*Set compost and decay, update*/
		}
	}

	private static boolean canPropagate(BlockState p_56828_, LevelReader p_56829_, BlockPos p_56830_) {
		BlockPos blockpos = p_56830_.above();
		return canBeGrass(p_56828_, p_56829_, p_56830_) && !p_56829_.getFluidState(blockpos).is(FluidTags.WATER);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		PlantType type = plantable.getPlantType(world, pos.relative(facing));
		return PlantType.PLAINS.equals(type);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		return true;
	}

	//TODO: Reintroduce logic
	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
//		DecoratorPositionProvider provider = new DecoratorPositionProvider();
//		provider.init(worldIn, worldIn.getBiome(pos), null, rand, pos.getX(), pos.getY() + 1, pos.getZ());
//		provider.setOffsetXZ(-4, 4);
//		provider.setOffsetY(-2, 2);
//
//		for(int i = 0; i < 4; i++) {
//			DecorationHelper.generateSwampDoubleTallgrass(provider);
//			DecorationHelper.generateTallCattail(provider);
//			DecorationHelper.generateSwampTallgrassCluster(provider);
//			if(rand.nextInt(5) == 0) {
//				DecorationHelper.generateCattailCluster(provider);
//			}
//			if(rand.nextInt(3) == 0) {
//				DecorationHelper.generateShootsCluster(provider);
//			}
//		}
	}

	// TODO: Change block tint colors but not particles
	@Override
	public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int index) {
		return level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.get(0.5D, 1.0D);
	}
}