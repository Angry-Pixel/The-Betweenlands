package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CaveGrassFeature extends CaveFeature {

	private static final int MIN_RADIUS = 2;
	private static final int MAX_RADIUS = 7;

	public CaveGrassFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (!canPlaceGrass(level, pos))
			return false;

		int radius = rand.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<BlockPos> search = new Stack<>();
		List<BlockPos> check = new ArrayList<>();
		List<BlockPos> location = new ArrayList<>();
		search.push(pos);
		check.add(pos);

		while (!search.isEmpty()) {
			BlockPos position = search.pop();
			double distSq = position.distSqr(pos);

			if (rand.nextFloat() / 1.4F > distSq / radiusSq) {
				location.add(position);
			}

			for (Direction dir : directions) {
				BlockPos offset = position.relative(dir);
				if (offset.distSqr(pos) > radiusSq) {
					continue;
				}

				if (canPlaceGrass(level, offset.above())) {
					BlockPos p = offset.above();
					if (!check.contains(p)) {
						search.push(p);
						check.add(p);
					}
				} else if (canPlaceGrass(level, offset)) {
					if (!check.contains(offset)) {
						search.push(offset);
						check.add(offset);
					}
				} else if (canPlaceGrass(level, offset.below())) {
					BlockPos p = offset.below();
					if (!check.contains(p)) {
						search.push(p);
						check.add(p);
					}
				}
			}
		}
		if (location.size() < 3) {
			return false;
		}
		for (BlockPos position : location) {
			this.setBlock(level, position, BlockRegistry.CAVE_GRASS.get().defaultBlockState());
		}
		return true;
	}

	private boolean canPlaceGrass(WorldGenLevel level, BlockPos pos) {
		return level.getBlockState(pos).isAir() && BlockRegistry.CAVE_GRASS.get().defaultBlockState().canSurvive(level, pos);
	}
}
