package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.block.HangerBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.CubicBezier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class CaveHangersFeature extends CaveFeature {

	private static final CubicBezier HEIGHT_CDF = new CubicBezier(0.75F, 0.25F, 0.9F, 1.0F);
	private static final int MIN_RADIUS = 4;
	private static final int MAX_RADIUS = 8;
	private static final int MAX_HEIGHT = 8;

	public CaveHangersFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (!isGoodStart(level, pos)) {
			return false;
		}

		int radius = rand.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<BlockPos> search = new Stack<>();
		List<BlockPos> check = new ArrayList<>();
		List<PlantLocation> locations = new ArrayList<>();
		BlockPos start = pos.below();
		search.push(start);
		check.add(start);

		while (!search.isEmpty()) {
			BlockPos position = search.pop();
			double distSq = position.distSqr(pos);

			if (rand.nextFloat() > distSq / radiusSq) {
				locations.add(new PlantLocation(level, position));
			}

			for (Direction dir : directions) {
				BlockPos offset = position.relative(dir);

				if (offset.distSqr(pos) > radiusSq) {
					continue;
				}

				if (supports(level, offset.above())) {
					if (!check.contains(offset)) {
						search.push(offset);
						check.add(offset);
					}
				}
			}
		}

		if (locations.size() < 3) {
			return false;
		}

		float[] heights = new float[locations.size()];
		for (int i = 0; i < locations.size(); i++) {
			heights[i] = HEIGHT_CDF.eval(i / (float) (locations.size() - 1));
		}
		Collections.shuffle(locations);
		for (int i = 0; i < locations.size(); i++) {
			PlantLocation location = locations.get(i);
			BlockPos position = location.getPos();
			int height = Math.min(location.getHeight(), MAX_HEIGHT);
			height = (int) (heights[i] * (height - 1) + 1);

			for (int dy = 0; dy < height; dy++) {
				this.setBlock(level, position.offset(0, -dy, 0), BlockRegistry.HANGER.get().defaultBlockState().setValue(HangerBlock.CAN_GROW, false));
			}
		}
		return true;
	}
}
