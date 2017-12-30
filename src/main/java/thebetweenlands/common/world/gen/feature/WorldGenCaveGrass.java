package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenCaveGrass extends WorldGenCave {

	private static final int MIN_RADIUS = 2;

	private static final int MAX_RADIUS = 7;

	public WorldGenCaveGrass() {
		super(false);
	}

	private boolean canPlaceGrass(World world, BlockPos pos) {
		return world.isAirBlock(pos) && BlockRegistry.CAVE_GRASS.canPlaceBlockAt(world, pos);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos origin) {
		if(!canPlaceGrass(world, origin)) return false;
		int radius = random.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<BlockPos> searching = new Stack<BlockPos>();
		List<BlockPos> checked = new ArrayList<BlockPos>();
		List<BlockPos> locations = new ArrayList<BlockPos>();
		BlockPos start = origin;
		searching.push(start);
		checked.add(start);
		while (!searching.isEmpty()) {
			BlockPos pos = searching.pop();
			double distSq = pos.distanceSq(origin.getX(), origin.getY(), origin.getZ());
			if (random.nextFloat() / 1.4F > distSq / radiusSq) {
				locations.add(pos);
			}
			for (EnumFacing dir : directions) {
				BlockPos offsetPos = pos.offset(dir);
				if (offsetPos.distanceSq(origin.getX(), origin.getY(), origin.getZ()) > radiusSq) {
					continue;
				}
				if (canPlaceGrass(world, offsetPos.add(0, 1, 0))) {
					BlockPos p = offsetPos.add(0, 1, 0);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				} else if (canPlaceGrass(world, offsetPos)) {
					BlockPos p = offsetPos;
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				} else if(canPlaceGrass(world, offsetPos.add(0, -1, 0))) {
					BlockPos p = offsetPos.add(0, -1, 0);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				}
			}
		}
		if (locations.size() < 3) {
			return false;
		}
		for(BlockPos pos : locations) {
			this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.CAVE_GRASS.getDefaultState());
		}
		return true;
	}
}
