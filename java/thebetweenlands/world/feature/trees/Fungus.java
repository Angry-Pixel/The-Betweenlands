package thebetweenlands.world.feature.trees;

import java.util.Random;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;

public class Fungus {
	private int posX;
	private int posY;
	private int posZ;

	private int radius;

	public Fungus(int posX, int posY, int posZ, int radius) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.radius = radius;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getPosZ() {
		return posZ;
	}

	public int getRadius() {
		return radius;
	}

	public void generate(World world, Random rand) {
		for (int y = posY; y > posY - radius; y -= 2) {
			if (radius > 1) {
				radius--;
			}
			ChunkCoordinates center = new ChunkCoordinates(posX, y, posZ);
			Stack<ChunkCoordinates> pendingCoords = new Stack<ChunkCoordinates>();
			pendingCoords.add(center);
			while (!pendingCoords.isEmpty()) {
				ChunkCoordinates coord = pendingCoords.pop();
				if (world.getBlock(coord.posX, coord.posY, coord.posZ).getMaterial().isReplaceable() || world.getBlock(coord.posX, coord.posY + 1, coord.posZ).getMaterial().isReplaceable()) {
					world.setBlock(coord.posX, coord.posY, coord.posZ, BLBlockRegistry.treeFungus);
				}
				for (ForgeDirection direction : WorldGenGiantTree.DIRECTIONS) {
					ChunkCoordinates neighborCoord = new ChunkCoordinates(coord.posX + direction.offsetX, coord.posY, coord.posZ + direction.offsetZ);
					Block block = world.getBlock(neighborCoord.posX, neighborCoord.posY, neighborCoord.posZ);
					Block above = world.getBlock(neighborCoord.posX, neighborCoord.posY + 1, neighborCoord.posZ);
					if (!pendingCoords.contains(neighborCoord) && getDistanceBetweenChunkCoordinates(center, neighborCoord) <= radius && (block.getMaterial().isReplaceable() || (above.getMaterial().isReplaceable() && block != BLBlockRegistry.treeFungus))) {
						pendingCoords.add(neighborCoord);
					}
				}
			}
		}
	}

	private int getDistanceBetweenChunkCoordinates(ChunkCoordinates a, ChunkCoordinates b) {
		return (int) Math.round(Math.sqrt(a.getDistanceSquaredToChunkCoordinates(b)));
	}
}
