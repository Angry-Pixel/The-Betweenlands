package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.block.plant.BlockVineBL;

public class WorldGenGiantTreeDead extends WorldGenGiantTreeTrunk {
	private static final float TILT_SCALE = 0.2F;

	private float tiltOffsetX;
	private float tiltOffsetZ;

	private boolean genIvy;
	
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		this.genIvy = rand.nextInt(5) == 0;
		return super.generate(world, rand, pos);
	}
	
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
			if(this.genIvy) {
				addVineToRandomBlockSide(world, rand, blockX + dx, blockY + dy, blockZ + dz);
			}
		}
	}

	public void addVineToRandomBlockSide(World world, Random rand, int x, int y, int z) {
		EnumFacing direction = EnumFacing.VALUES[2 + rand.nextInt(3)];
		if (world.getBlockState(new BlockPos(x + direction.getXOffset(), y, z + direction.getZOffset())).getMaterial().isReplaceable()) {
			world.setBlockState(new BlockPos(x + direction.getXOffset(), y, z + direction.getZOffset()), IVY.withProperty(BlockVineBL.getPropertyFor(direction.getOpposite()), Boolean.valueOf(true)), 2);
		}
	}

	private float calculateDistanceToTop(int height, int dx, int dy, int dz) {
		return MathHelper.sqrt(dx * dx + dz * dz + (height - dy) * (height - dy));
	}

	private int calculateTilt(int dx, int dz) {
		final int tiltHeight = 3;
		return (int) ((((MathHelper.sin(dx * TILT_SCALE + tiltOffsetX) * 0.5F + MathHelper.sin(dz * TILT_SCALE + tiltOffsetZ) * 0.5F) + 1) * 0.5F) * tiltHeight);
	}
}
