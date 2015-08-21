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

public class WorldGenThorns extends WorldGenerator {
	private static final ForgeDirection[] DIRECTIONS = { ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST };

	private Block thorns = BLBlockRegistry.thorns;

	private int minRadius = 2;

	private int maxRadius = 3;

	private int lengthRange = 5;

	private int minLength = 2;

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
		int radius = random.nextInt(maxRadius - minRadius + 1) + minRadius;
		int radiusSq = radius * radius;
		Stack<ChunkCoordinates> searching = new Stack<ChunkCoordinates>();
		List<ChunkCoordinates> checked = new ArrayList<ChunkCoordinates>();
		List<ThornLocation> locations = new ArrayList<ThornLocation>();
		ChunkCoordinates start = new ChunkCoordinates(x, y - 1, z);
		searching.push(start);
		checked.add(start);
		while (!searching.isEmpty()) {
			ChunkCoordinates pos = searching.pop();
			float distSq = (pos.posX - x) * (pos.posX - x) + (pos.posZ - z) * (pos.posZ - z);
			if (random.nextFloat() > distSq / radiusSq) {
				locations.add(new ThornLocation(world, pos));
			}
			for (ForgeDirection dir : DIRECTIONS) {
				int bx = pos.posX + dir.offsetX, bz = pos.posZ + dir.offsetZ;
				if ((bx - x) * (bx - x) + (bz - z) * (bz - z) > radiusSq) {
					continue;
				}
				if (supportsThorns(world, bx, pos.posY + 1, bz)) {
					ChunkCoordinates p = new ChunkCoordinates(bx, pos.posY, bz);
					if (!checked.contains(p)) {
						searching.push(p);
						checked.add(p);
					}
				} else if (supportsThorns(world, bx, pos.posY, bz)) {
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
		for (ThornLocation location : locations) {
			if (location.height > maximumHeight) {
				maximumHeight = location.height;
			}
		}
		if (maximumHeight < 3) {
			return false;
		}
		int[] facesWithThorns = new int[4];
		for (ThornLocation location : locations) {
			int bx = location.pos.posX;
			int by = location.pos.posY;
			int bz = location.pos.posZ;
			int sideCount = 0;
			int metadata = 0;
			for (int n = 0; n < DIRECTIONS.length; n++) {
				ForgeDirection face = DIRECTIONS[n];
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
			if (metadata > 0 && location.height > 1) {
				int hangingMetadata = facesWithThorns[random.nextInt(sideCount)];
				int length = random.nextInt(location.height - 1) + 1;
				for (int n = 1; n < length; n++) {
					setBlockAndNotifyAdequately(world, bx, by - n, bz, thorns, hangingMetadata);
				}
			}
		}
		return true;
	}

	private boolean isGoodStart(World world, int x, int y, int z) {
		if (supportsThorns(world, x, y, z)) {
			int sides = 0;
			for (ForgeDirection dir : DIRECTIONS) {
				if (!isValidBlock(world, x + dir.offsetX, y, z + dir.offsetZ)) {
					return false;
				}
				if (isValidBlock(world, x + dir.offsetX, y - 1, z + dir.offsetZ) && world.isSideSolid(x + dir.offsetX, y - 1, z + dir.offsetZ, dir)) {
					sides++;
				}
			}
			return sides > 0;
		}
		return false;
	}

	private boolean supportsThorns(World world, int x, int y, int z) {
		return isValidBlock(world, x, y, z) && world.isAirBlock(x, y - 1, z);
	}

	private boolean isValidBlock(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isNormalCube();
	}

	private class ThornLocation {
		private ChunkCoordinates pos;

		private int height;

		public ThornLocation(World world, ChunkCoordinates pos) {
			this.pos = pos;
			height = 1;
			while (world.isAirBlock(pos.posX, pos.posY - height, pos.posZ) && (pos.posY - height) > 0) {
				height++;
			}
		}
	}
}
