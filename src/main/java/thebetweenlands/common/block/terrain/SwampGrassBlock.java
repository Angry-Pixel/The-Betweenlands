package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.PlacedFeatureRegistry;

public class SwampGrassBlock extends Block implements BonemealableBlock {
	public SwampGrassBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide()) {
			updateGrass(level, pos, random);
		}
	}

	public static boolean updateGrass(Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockState(pos.above()).getLightBlock(level, pos.above()) > 2) {
			revertToDirt(level, pos);
			return true;
		} else {
			for (int i = 0; i < 4; ++i) {
				BlockPos blockPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

				if (blockPos.getY() >= level.getMinBuildHeight() && blockPos.getY() < level.getMaxBuildHeight() && !level.isLoaded(blockPos)) {
					return false;
				}

				BlockState blockStateAbove = level.getBlockState(blockPos.above());

				if (blockStateAbove.getLightBlock(level, pos.above()) <= 2) {
					spreadGrassTo(level, blockPos);
					return true;
				}
			}
		}
		return false;
	}

	public static void revertToDirt(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.is(BlockRegistry.SWAMP_GRASS)) {
			level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
		}

		if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil) {
			int compost = soil.getCompost();
			int decay = soil.getDecay();

			if (state.is(BlockRegistry.DUG_SWAMP_GRASS)) {
				level.setBlockAndUpdate(pos, BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState());
			}

			if (state.is(BlockRegistry.PURIFIED_DUG_SWAMP_GRASS)) {
				level.setBlockAndUpdate(pos, BlockRegistry.PURIFIED_DUG_SWAMP_DIRT.get().defaultBlockState());
			}

			if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity newSoil) {
				newSoil.setCompost(level, pos, compost);
				newSoil.setDecay(level, pos, decay);
			}
		}
	}

	public static void spreadGrassTo(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.is(BlockRegistry.SWAMP_DIRT)) {
			level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_GRASS.get().defaultBlockState());
		}

		if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil) {
			int compost = soil.getCompost();
			int decay = soil.getDecay();

			if (state.is(BlockRegistry.DUG_SWAMP_DIRT)) {
				level.setBlock(pos, BlockRegistry.DUG_SWAMP_GRASS.get().defaultBlockState(), 2); //don't do block update yet
			}

			if (state.is(BlockRegistry.PURIFIED_DUG_SWAMP_DIRT)) {
				level.setBlock(pos, BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get().defaultBlockState(), 2); //don't do block update yet
			}

			if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity newSoil) {
				newSoil.setCompost(level, pos, compost);
				newSoil.setDecay(level, pos, decay);
			}

			level.sendBlockUpdated(pos, state, level.getBlockState(pos), 1); //do block update now
		}
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		for (int i = 0; i < 4; i++) {
			//TODO generate 1 piece of swamp grass
			//TODO generate 1 cattail
			this.tryPlaceFeature(level, pos, random, PlacedFeatureRegistry.SWAMP_TALLGRASS_PATCH);
			if (random.nextInt(5) == 0) {
				this.tryPlaceFeature(level, pos, random, PlacedFeatureRegistry.CATTAIL_PATCH);
			}
			if (random.nextInt(3) == 0) {
				this.tryPlaceFeature(level, pos, random, PlacedFeatureRegistry.SHOOTS_PATCH);
			}
		}
	}

	private void tryPlaceFeature(ServerLevel level, BlockPos pos, RandomSource random, ResourceKey<PlacedFeature> feature) {
		level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getOrThrow(feature).place(level, level.getChunkSource().getGenerator(), random, pos);
	}

	@Override
	public Type getType() {
		return Type.NEIGHBOR_SPREADER;
	}
}
