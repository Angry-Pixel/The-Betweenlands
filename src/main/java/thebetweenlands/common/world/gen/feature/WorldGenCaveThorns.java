package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.plant.BlockThorns;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenCaveThorns extends WorldGenCave {
	private BlockThorns thorns = (BlockThorns) BlockRegistry.THORNS;

	private static final int MIN_RADIUS = 2;

	private static final int MAX_RADIUS = 3;

	private static final int LENGTH_RANGE = 5;

	private static final int MIN_LENGTH = 2;

	private static final int MAX_HEIGHT = 8;

	public WorldGenCaveThorns() {
		this(false);
	}

	public WorldGenCaveThorns(boolean doBlockNotify) {
		super(doBlockNotify);
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
			double distSq = origin.distanceSq(pos.getX(), pos.getY(), pos.getZ());
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
				} else if (supports(world, offsetPos)) {
					BlockPos below = offsetPos.add(0, -1, 0);
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
		int[] facesWithThorns = new int[4];
		for (PlantLocation location : locations) {
			BlockPos pos = location.getPos();
			int sideCount = 0;
			int metadata = 0;
			for (int n = 0; n < directions.length; n++) {
				EnumFacing face = directions[n];
				BlockPos neighbourPos = pos.offset(face);
				if (isValidBlock(world, neighbourPos) && world.isSideSolid(neighbourPos, face)) {
					int side = 1 << n;
					facesWithThorns[sideCount++] = side;
					metadata |= side;
				}
			}
			this.setBlockAndNotifyAdequately(world, pos, thorns.getStateFromMeta(metadata));
			if (metadata > 0 && location.getHeight() > 1) {
				int hangingMetadata = facesWithThorns[random.nextInt(sideCount)];
				int length = random.nextInt(location.getHeight() > MAX_HEIGHT ? MAX_HEIGHT : location.getHeight() - 1) + 1;
				for (int n = 1; n < length; n++) {
					setBlockAndNotifyAdequately(world, pos.add(0, -n, 0), thorns.getStateFromMeta(hangingMetadata));
				}
			}
		}
		return true;
	}
}
