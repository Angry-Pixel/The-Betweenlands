package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CaveThornsFeature extends CaveFeature {
	private static final BlockState THORNS = BlockRegistry.THORNS.get().defaultBlockState();
	private static final int MIN_RADIUS = 2;
	private static final int MAX_RADIUS = 3;
	private static final int MAX_HEIGHT = 8;

	public CaveThornsFeature(Codec<NoneFeatureConfiguration> codec) {
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
			double distSq = origin.distSqr(pos);
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
				} else if (supports(level, offsetPos)) {
					BlockPos below = offsetPos.below();
					if (!checked.contains(below)) {
						searching.push(below);
						checked.add(below);
					}
				}
			}
		}
		if (locations.size() < 5) {
			return false;
		}
		int maximumHeight = 0;
		for (PlantLocation location : locations) {
			if (location.getHeight() > maximumHeight) {
				maximumHeight = location.getHeight();
			}
		}
		if (maximumHeight < 3) {
			return false;
		}
		Direction[] facesWithThorns = new Direction[4];
		for (PlantLocation location : locations) {
			BlockPos pos = location.getPos();
			int sideCount = 0;
			int metadata = 0;
			Direction face;
			for (Direction dir : Direction.values()) {
				face = dir;
				BlockPos neighbourPos = pos.relative(face);
				if (isValidBlock(level, neighbourPos) && level.getBlockState(pos).isFaceSturdy(level, neighbourPos, face)) {
					facesWithThorns[sideCount++] = dir;
					metadata++;
					THORNS.setValue(VineBlock.PROPERTY_BY_DIRECTION.get(face), true);
				}
			}
			this.setBlock(level, pos, THORNS);
			if (metadata > 0 && location.getHeight() > 1) {
				Direction hangingDir = facesWithThorns[random.nextInt(sideCount)];
				int length = random.nextInt(location.getHeight() > MAX_HEIGHT ? MAX_HEIGHT : location.getHeight() - 1) + 1;
				for (int n = 1; n < length; n++) {
					setBlock(level, pos.offset(0, -n, 0), THORNS.setValue(VineBlock.PROPERTY_BY_DIRECTION.get(hangingDir), true));
				}
			}
		}
		return true;
	}
}
