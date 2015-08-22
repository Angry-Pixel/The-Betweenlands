package thebetweenlands.world.feature.gen.cave;

import java.util.Random;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class WorldGenCave extends WorldGenerator {
	protected final ForgeDirection[] directions = { ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST };

	public WorldGenCave(boolean doBlockNotify) {
		super(doBlockNotify);
	}

	protected boolean isGoodStart(World world, int x, int y, int z) {
		if (supports(world, x, y, z)) {
			int sides = 0;
			for (ForgeDirection dir : directions) {
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

	protected boolean supports(World world, int x, int y, int z) {
		return isValidBlock(world, x, y, z) && world.isAirBlock(x, y - 1, z);
	}

	protected boolean isValidBlock(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isNormalCube();
	}


	protected class PlantLocation {
		private ChunkCoordinates pos;

		private int height;

		public PlantLocation(World world, ChunkCoordinates pos) {
			this.setPos(pos);
			setHeight(1);
			while (world.isAirBlock(pos.posX, pos.posY - getHeight(), pos.posZ) && (pos.posY - getHeight()) > 0) {
				setHeight(getHeight() + 1);
			}
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public ChunkCoordinates getPos() {
			return pos;
		}

		public void setPos(ChunkCoordinates pos) {
			this.pos = pos;
		}
	}
}
