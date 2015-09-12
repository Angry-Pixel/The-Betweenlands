package thebetweenlands.world.feature.gen.cave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;

public class WorldGenThorns extends WorldGenCave {
	private Block thorns = BLBlockRegistry.thorns;

	private static final int MIN_RADIUS = 2;

	private static final int MAX_RADIUS = 3;

	private static final int LENGTH_RANGE = 5;

	private static final int MIN_LENGTH = 2;

	private static final int MAX_HEIGHT = 8;

	public WorldGenThorns() {
		this(false);
	}

	public WorldGenThorns(boolean doBlockNotify) {
		super(doBlockNotify);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (!isGoodStart(world, x, y, z)) {
			return false;
		}
		int radius = random.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
		int radiusSq = radius * radius;
		Stack<ChunkCoordinates> searching = new Stack<ChunkCoordinates>();
		List<ChunkCoordinates> checked = new ArrayList<ChunkCoordinates>();
		List<PlantLocation> locations = new ArrayList<PlantLocation>();
		ChunkCoordinates start = new ChunkCoordinates(x, y - 1, z);
		searching.push(start);
		checked.add(start);
		while (!searching.isEmpty()) {
			ChunkCoordinates pos = searching.pop();
			float distSq = (pos.posX - x) * (pos.posX - x) + (pos.posZ - z) * (pos.posZ - z);
			if (random.nextFloat() > distSq / radiusSq) {
				locations.add(new PlantLocation(world, pos));
			}
			for (ForgeDirection dir : directions) {
				int bx = pos.posX + dir.offsetX, bz = pos.posZ + dir.offsetZ;
				if ((bx - x) * (bx - x) + (bz - z) * (bz - z) > radiusSq) {
					continue;
				}
				if (supports(world, bx, pos.posY + 1, bz)) {
					ChunkCoordinates p = new ChunkCoordinates(bx, pos.posY, bz);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				} else if (supports(world, bx, pos.posY, bz)) {
					ChunkCoordinates p = new ChunkCoordinates(bx, pos.posY - 1, bz);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
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
			ChunkCoordinates pos = location.getPos();
			int bx = pos.posX;
			int by = pos.posY;
			int bz = pos.posZ;
			int sideCount = 0;
			int metadata = 0;
			for (int n = 0; n < directions.length; n++) {
				ForgeDirection face = directions[n];
				int cx = bx + face.offsetX;
				int cy = by + face.offsetY;
				int cz = bz + face.offsetZ;
				if (isValidBlock(world, cx, cy, cz) && world.isSideSolid(cx, cy, cz, face)) {
					int side = 1 << n;
					facesWithThorns[sideCount++] = side;
					metadata |= side;
				}
			}
			setBlockAndNotifyAdequately(world, bx, by, bz, thorns, metadata);
			if (metadata > 0 && location.getHeight() > 1) {
				int hangingMetadata = facesWithThorns[random.nextInt(sideCount)];
				int length = random.nextInt(location.getHeight() > MAX_HEIGHT ? MAX_HEIGHT : location.getHeight() - 1) + 1;
				for (int n = 1; n < length; n++) {
					setBlockAndNotifyAdequately(world, bx, by - n, bz, thorns, hangingMetadata);
				}
			}
		}
		return true;
	}
}
