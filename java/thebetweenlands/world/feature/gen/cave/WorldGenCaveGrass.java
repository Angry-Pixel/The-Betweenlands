package thebetweenlands.world.feature.gen.cave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;

public class WorldGenCaveGrass extends WorldGenCave {

	private static final int MIN_RADIUS = 2;

	private static final int MAX_RADIUS = 7;

	public WorldGenCaveGrass() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if(!canPlaceGrass(world, x, y, z)) return false;
		int radius = random.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<ChunkCoordinates> searching = new Stack<ChunkCoordinates>();
		List<ChunkCoordinates> checked = new ArrayList<ChunkCoordinates>();
		List<ChunkCoordinates> locations = new ArrayList<ChunkCoordinates>();
		ChunkCoordinates start = new ChunkCoordinates(x, y, z);
		searching.push(start);
		checked.add(start);
		while (!searching.isEmpty()) {
			ChunkCoordinates pos = searching.pop();
			float distSq = (pos.posX - x) * (pos.posX - x) + (pos.posZ - z) * (pos.posZ - z);
			if (random.nextFloat() / 1.4F > distSq / radiusSq) {
				locations.add(pos);
			}
			for (ForgeDirection dir : directions) {
				int bx = pos.posX + dir.offsetX, bz = pos.posZ + dir.offsetZ;
				if ((bx - x) * (bx - x) + (bz - z) * (bz - z) > radiusSq) {
					continue;
				}
				if (canPlaceGrass(world, bx, pos.posY + 1, bz)) {
					ChunkCoordinates p = new ChunkCoordinates(bx, pos.posY + 1, bz);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				} else if (canPlaceGrass(world, bx, pos.posY, bz)) {
					ChunkCoordinates p = new ChunkCoordinates(bx, pos.posY, bz);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				} else if(canPlaceGrass(world, bx, pos.posY - 1, bz)) {
					ChunkCoordinates p = new ChunkCoordinates(bx, pos.posY - 1, bz);
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
		for(ChunkCoordinates pos : locations) {
			setBlockAndNotifyAdequately(world, pos.posX, pos.posY, pos.posZ, BLBlockRegistry.caveGrass, 0);
		}
		return true;
	}

	private boolean canPlaceGrass(World world, int x, int y, int z) {
		return world.isAirBlock(x, y, z) && BLBlockRegistry.caveGrass.canPlaceBlockOn(world.getBlock(x, y - 1, z));
	}
}
