package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.block.plant.HangingPlantBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.CubicBezier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class CaveMossFeature extends CaveFeature {

	private static final CubicBezier HEIGHT_CDF = new CubicBezier(0.75F, 0, 0.9F, 1);
	private static final int MIN_RADIUS = 3;
	private static final int MAX_RADIUS = 6;
	private static final int MAX_HEIGHT = 5;

	public CaveMossFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource random, BlockPos origin) {
		if (!isGoodStart(level, origin)) {
			return false;
		}
		int radius = random.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<BlockPos> searching = new Stack<>();
		List<BlockPos> checked = new ArrayList<>();
		List<PlantLocation> locations = new ArrayList<>();
		BlockPos start = origin.below();
		searching.push(start);
		checked.add(start);
		while (!searching.isEmpty()) {
			BlockPos pos = searching.pop();
			double distSq = pos.distSqr(origin);
			if (random.nextFloat() > distSq / radiusSq) {
				locations.add(new PlantLocation(level, pos));
			}
			for (Direction dir : directions) {
				BlockPos offsetPos = pos.relative(dir);
				if (offsetPos.distSqr(origin) > radiusSq) {
					continue;
				}
				if (supports(level, offsetPos.above())) {
					if (!checked.contains(offsetPos)) {
						searching.push(offsetPos);
						checked.add(offsetPos);
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
			BlockPos pos = location.getPos();
			int height = location.getHeight();
			if (height > MAX_HEIGHT) {
				height = MAX_HEIGHT;
			}
			height = (int) (heights[i] * (height - 1) + 1);
			for (int dy = 0; dy < height; dy++) {
				this.setBlock(level, pos.offset(0, -dy, 0), BlockRegistry.CAVE_MOSS.get().defaultBlockState().setValue(HangingPlantBlock.CAN_GROW, false));
			}
		}
		return true;
	}
}
