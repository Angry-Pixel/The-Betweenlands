package thebetweenlands.common.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import thebetweenlands.common.blocks.BetweenlandsSeaPlant;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

import java.util.Random;

public class SwampReedCluster extends Feature<VegetationPatchConfiguration> {
	public SwampReedCluster(Codec<VegetationPatchConfiguration> p_65786_) {
		super(p_65786_);
	}

	@Override
	public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> p_159749_) {
		WorldGenLevel level = p_159749_.level();
		BlockPos position = p_159749_.origin();
		Random rand = p_159749_.random();
		boolean generated = false;

		// no alike function
		// iblockstate.getBlock().isLeaves(iblockstate, level, position)

		for (BlockState iblockstate = level.getBlockState(position); (level.getBlockState(position).getBlock() == Blocks.AIR /*|| iblockstate.getBlock().isLeaves(iblockstate, level, position)*/) && position.getY() > 0; iblockstate = level.getBlockState(position)) {
			position = position.below();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.offset(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			//if (level.isBlockLoaded(pos)) {
			if (SurfaceType.WATER.matches(level, pos.above()) && level.getBlockState(pos).getBlock() == BlockRegistry.MUD.get() && level.getBlockState(pos.above(2)).getBlock() == Blocks.AIR) {
				this.generateReedStack(level, rand, pos.above());
				generated = true;
			} else if (SurfaceType.MIXED_GROUND.matches(level, pos) && BlockRegistry.SWAMP_REED.get().canSurvive(BlockRegistry.SWAMP_REED.get().defaultBlockState(), level, pos.above())) {
				this.generateReedStack(level, rand, pos.above());
				generated = true;
			}
			//}
		}

		return generated;
	}

	private void generateReedStack(WorldGenLevel world, Random rand, BlockPos pos) {
		int height = world.getRandom().nextInt(4) + 2;
		for (int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.offset(0, yo, 0);
			BlockState state = world.getBlockState(offsetPos);
			if (state.getBlock() != Blocks.AIR && !SurfaceType.WATER.matches(state)) {
				break;
			}
			if (SurfaceType.WATER.matches(state)) {
				world.setBlock(offsetPos, BlockRegistry.SWAMP_REED.get().defaultBlockState().setValue(BetweenlandsSeaPlant.WATERLOGGED, true), 2 | 16);
			} else {
				world.setBlock(offsetPos, BlockRegistry.SWAMP_REED.get().defaultBlockState(), 2 | 16);
			}
		}
	}
}
