package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.plant.BlockCaveMoss;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.CubicBezier;

public class WorldGenCaveMoss extends WorldGenCave {
	private static final CubicBezier HEIGHT_CDF = new CubicBezier(0.75F, 0, 0.9F, 1);

	private static final int MIN_RADIUS = 3;

	private static final int MAX_RADIUS = 6;

	private static final int MAX_HEIGHT = 5;

	public WorldGenCaveMoss() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos origin) {
		if (!isGoodStart(world, origin)) {
			return false;
		}
		int radius = random.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<BlockPos> searching = new Stack<BlockPos>();
		List<BlockPos> checked = new ArrayList<BlockPos>();
		List<PlantLocation> locations = new ArrayList<PlantLocation>();
		BlockPos start = origin.add(0, -1, 0);
		searching.push(start);
		checked.add(start);
		while (!searching.isEmpty()) {
			BlockPos pos = searching.pop();
			double distSq = pos.distanceSq(origin.getX(), origin.getY(), origin.getZ());
			if (random.nextFloat() > distSq / radiusSq) {
				locations.add(new PlantLocation(world, pos));
			}
			for (EnumFacing dir : directions) {
				BlockPos offsetPos = pos.offset(dir);
				if (offsetPos.distanceSq(origin.getX(), origin.getY(), origin.getZ()) > radiusSq) {
					continue;
				}
				if (supports(world, offsetPos.add(0, 1, 0))) {
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
		Collections.shuffle(locations, random);
		for (int i = 0; i < locations.size(); i++) {
			PlantLocation location = locations.get(i);
			BlockPos pos = location.getPos();
			int height = location.getHeight();
			if (height > MAX_HEIGHT) {
				height = MAX_HEIGHT;
			}
			height = (int) (heights[i] * (height - 1) + 1);
			for (int dy = 0; dy < height; dy++) {
				this.setBlockAndNotifyAdequately(world, pos.add(0, -dy, 0), BlockRegistry.CAVE_MOSS.getDefaultState().withProperty(BlockCaveMoss.CAN_GROW, false));
			}
		}
		return true;
	}
}
