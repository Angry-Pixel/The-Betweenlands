package thebetweenlands.world.feature.trees;

import static thebetweenlands.blocks.BLBlockRegistry.*;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenGiantTreeDead extends WorldGenGiantTree {
	private static final float TILT_SCALE = 0.2F;

	private float tiltOffsetX;
	private float tiltOffsetZ;

	@Override
	protected boolean canFungusGenerateAtY(int dy, int height) {
		return super.canFungusGenerateAtY(dy, height) && dy > 5 && dy <= height - 4;
	}

	@Override
	protected void initAttributes(Random rand) {
		tiltOffsetX = rand.nextFloat() * 2 * (float) Math.PI / TILT_SCALE;
		tiltOffsetZ = rand.nextFloat() * 2 * (float) Math.PI / TILT_SCALE;
	}

	@Override
	protected void placeWood(World world, Random rand, int radius, int height, int blockX, int blockY, int blockZ, int dx, int dy, int dz) {
		float distanceToTop = calculateDistanceToTop(height, dx, dy, dz);
		int tilt = calculateTilt(dx, dz);
		if (distanceToTop > radius * (rand.nextFloat() * 0.1F + 0.8F) && height - tilt > dy) {
			super.placeWood(world, rand, radius, height, blockX, blockY, blockZ, dx, dy, dz);
		}
	}

	@Override
	protected void placeBark(World world, Random rand, int radius, int height, int blockX, int blockY, int blockZ, int dx, int dy, int dz) {
		float distanceToTop = calculateDistanceToTop(height, dx, dy, dz);
		int tilt = calculateTilt(dx, dz);
		if (distanceToTop > radius * 0.75F && height - tilt > dy) {
			super.placeBark(world, rand, radius, height, blockX, blockY, blockZ, dx, dy, dz);
			addVineToRandomBlockSide(world, rand, blockX + dx, blockY + dy, blockZ + dz);
		}
	}

	public void addVineToRandomBlockSide(World world, Random rand, int x, int y, int z) {
		ForgeDirection direction = ForgeDirection.values()[2 + rand.nextInt(3)];
		if (world.getBlock(x + direction.offsetX, y, z + direction.offsetZ).getMaterial().isReplaceable()) {
			world.setBlock(x + direction.offsetX, y, z + direction.offsetZ, poisonIvy, poisonIvy.getMetaForDirection(direction), 2);
		}
	}

	private float calculateDistanceToTop(int height, int dx, int dy, int dz) {
		return MathHelper.sqrt_float(dx * dx + dz * dz + (height - dy) * (height - dy));
	}

	private int calculateTilt(int dx, int dz) {
		final int tiltHeight = 3;
		return (int) ((((MathHelper.sin(dx * TILT_SCALE + tiltOffsetX) * 0.5F + MathHelper.sin(dz * TILT_SCALE + tiltOffsetZ) * 0.5F) + 1) * 0.5F) * tiltHeight);
	}
}
