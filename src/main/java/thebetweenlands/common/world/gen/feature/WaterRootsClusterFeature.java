package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.block.terrain.StalactiteBlock;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.util.WorldGenUtil;

public class WaterRootsClusterFeature extends Feature<NoneFeatureConfiguration> {

	public WaterRootsClusterFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		boolean generated = false;
		pos = WorldGenUtil.loopUntilSolid(level, pos);

		for (int i = 0; i < 128; ++i) {
			BlockPos offset = WorldGenUtil.randomOffset(rand, pos, 10, 8, 10);

			if (SurfaceType.WATER.matches(level, pos.above()) && SurfaceType.MIXED_GROUND.matches(level, pos)) {
				if (this.generateWaterRootsStack(level, rand, offset.above())) {
					generated = true;
				}
			}
		}
		return generated;
	}

	private boolean generateWaterRootsStack(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int height = 0;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int yo = 0; yo < 128; ++yo) {
			mutable.set(pos.getX(), pos.getY() + yo, pos.getZ());
			if (!level.getBlockState(mutable).isAir() && !SurfaceType.WATER.matches(level, mutable))
				return false;
			if (level.getBlockState(mutable).isAir() && level.getBlockState(mutable.set(pos.getX(), pos.getY() + yo + 1, pos.getZ())).isAir()) {
				height = yo;
				break;
			}
		}
		if (height < 1) {
			return false;
		}
		height = rand.nextInt(height) + 1 + rand.nextInt(4);
		for (int yo = 0; yo < height; ++yo) {
			BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos(pos.getX(), pos.getY() + yo, pos.getZ());
			BlockState state = level.getBlockState(offset);
			if (!SurfaceType.WATER.matches(state)) {
				if (!state.isAir())
					break;
				level.setBlock(offset, BlockRegistry.ROOT.get().defaultBlockState(), 2 | 16);
			} else {
				level.setBlock(offset, BlockRegistry.ROOT.get().defaultBlockState().setValue(StalactiteBlock.WATER_TYPE, SwampWaterLoggable.WaterType.SWAMP_WATER), 1 | 16);
			}
		}
		return true;
	}
}
