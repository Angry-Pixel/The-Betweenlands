package thebetweenlands.world.feature.gen.cave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.CubicBezier;

public class WorldGenCaveMoss extends WorldGenCave {
	private static final CubicBezier HEIGHT_CDF = new CubicBezier(0.75F, 0, 0.9F, 1);

	private static final int MIN_RADIUS = 3;

	private static final int MAX_RADIUS = 6;

	private static final int MAX_HEIGHT = 5;

	public WorldGenCaveMoss() {
		super(false);
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
			ChunkCoordinates pos = location.getPos();
			int bx = pos.posX;
			int by = pos.posY;
			int bz = pos.posZ;
			int height = location.getHeight();
			if (height > MAX_HEIGHT) {
				height = MAX_HEIGHT;
			}
			height = (int) (heights[i] * (height - 1) + 1);
			for (int dy = 0; dy < height; dy++) {
				setBlockAndNotifyAdequately(world, bx, by - dy, bz, BLBlockRegistry.caveMoss, dy < height - 1 ? 0 : 1);
			}
		}
		return true;
	}
}
