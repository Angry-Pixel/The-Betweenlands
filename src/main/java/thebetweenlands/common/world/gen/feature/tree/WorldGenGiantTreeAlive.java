package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenGiantTreeAlive extends WorldGenGiantTree {
	private static final EnumFacing[] SHUFFLED_DIRECTIONS = DIRECTIONS.clone();

	@Override
	public boolean generateTree(World world, Random rand, BlockPos pos) {
		boolean gen = super.generateTree(world, rand, pos);
		/*if(gen) {
			List<LocationStorage> addedLocations = StorageHelper.addArea(world, "giantTree", AxisAlignedBB.getBoundingBox(blockX - 32, blockY - 10, blockZ - 32, blockX + 32, blockY + 80, blockZ + 32), EnumLocationType.GIANT_TREE, 0);
			for(LocationStorage location : addedLocations) {
			location.setVisible(false).getChunkData().markDirty();
			}}	
		*/
		return gen;
	}

	@Override
	protected int getRadiusHeightRatio() {
		return 3;
	}

	@Override
	protected boolean canFungusGenerateAtY(int dy, int height) {
		return super.canFungusGenerateAtY(dy, height) && dy >= height / 4 && dy <= height / 4 * 3 - 3;
	}

	@Override
	protected void generateShoots(World world, Random rand, int baseRadius, int height, int blockX, int blockY, int blockZ) {
		super.generateShoots(world, rand, baseRadius, height, blockX, blockY, blockZ);
		int endRadius = getRadius(baseRadius, height);
		generateBranches(world, rand, baseRadius, height, endRadius, blockY, blockX, blockY + height / 3 * 2, blockZ, true);
		generateBranches(world, rand, baseRadius, height, endRadius, blockY, blockX, blockY + height / 3, blockZ, false);
		generateBranch(world, rand, blockX, blockY + height, blockZ, rand.nextFloat() * 2 * (float) Math.PI, (float) Math.PI / 2, 6, endRadius, -2);
	}

	private void generateBranches(World world, Random rand, int baseRadius, int height, int endRadius, int baseY, int blockX, int blockY, int blockZ, boolean top) {
		int branchCount = rand.nextInt(3) + (top ? 2 : 1);
		float angle = 2 * (float) Math.PI / branchCount;
		float angleOffset = rand.nextFloat() * 2 * (float) Math.PI;
		for (int branch = 0; branch < branchCount; branch++) {
			float yaw = angle * branch + angleOffset;
			float pitch = (float) Math.PI / 4 + rand.nextFloat() * (float) Math.PI / 6;
			int length = rand.nextInt(8) + 25;
			generateBranch(world, rand, blockX, Math.min(blockY + rand.nextInt(10), baseY + height - rand.nextInt(10)), blockZ, yaw, pitch, length, (int) (endRadius * 0.85F), (int) (endRadius * 0.4F));
		}
	}

	private void generateBranch(World world, Random rand, float posX, float posY, float posZ, float yaw, float pitch, int length, float startSize, float endSize) {
		float yawDelta = 0;
		boolean largeCanopy = endSize == -2;
		int branchPoint = largeCanopy ? -1 : length / 4 + rand.nextInt(length / 4);
		if (endSize == -1) {
			endSize = 1;
		} else if (largeCanopy) {
			endSize = startSize * 0.75F;
		}
		int minX = 30_000_000, minY = 255, minZ = 30_000_000, maxX = -30_000_000, maxY = 0, maxZ = -30_000_000;
		for (int step = 0; step < length; step++) {
			float along = step / (float) length;
			float cosPitch = MathHelper.cos(pitch);
			posX += MathHelper.cos(yaw) * cosPitch;
			posY += MathHelper.sin(pitch);
			posZ += MathHelper.sin(yaw) * cosPitch;
			if (!largeCanopy) {
				pitch *= (1 - along) + along * 0.1F;
				if (along > 0.5F && pitch < (float) Math.PI / 2) {
					pitch += along - 0.45F;
				}
			}
			yaw += yawDelta * 0.1F;
			yawDelta *= 0.8F;
			yawDelta += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4 * (along + 0.1F);
			float size = along * endSize + (1 - along) * startSize;
			int sizeRange = (int) Math.ceil(size);
			if (step == branchPoint && startSize > 1 && length > 5) {
				float branchYaw = rand.nextFloat() * (float) Math.PI / 16;
				float branchAngle = rand.nextFloat() * (float) Math.PI / 4 + (float) Math.PI / 4;
				generateBranch(world, rand, posX, posY, posZ, yaw + branchYaw + branchAngle, pitch + (float) Math.PI / 6, length - step, size * 0.8F, -1);
				generateBranch(world, rand, posX, posY, posZ, yaw + branchYaw - branchAngle, pitch + (float) Math.PI / 6, length - step, size * 0.8F, -1);
			}
			for (int x = -sizeRange; x <= sizeRange; x++) {
				for (int z = -sizeRange; z <= sizeRange; z++) {
					for (int y = -sizeRange; y <= sizeRange; y++) {
						float dist = MathHelper.sqrt_float(x * x + y * y + z * z);
						if (dist <= size) {
							IBlockState block = world.getBlockState(new BlockPos((int) posX + x, (int) posY + y, (int) posZ + z));
							IBlockState above = world.getBlockState(new BlockPos((int) posX + x, (int) posY + y + 1, (int) posZ + z));
							if (block == WOOD && above.getMaterial().isReplaceable()) {
								continue;
							}
							int bx = (int) posX + x; 
							int by = (int) posY + y; 
							int bz = (int) posZ + z; 
							world.setBlockState(new BlockPos(bx, by, bz), BARK);
							if (bx < minX) minX = bx;
							if (by < minY) minY = by;
							if (bz < minZ) minZ = bz;
							if (bx > maxX) maxX = bx;
							if (by > maxY) maxY = by;
							if (bz > maxZ) maxZ = bz;
						}
					}
				}
			}
		}
		makeBarkInsideNotBark(world, minX, minY, minZ, maxX, maxY, maxZ);
		int maxRadius = largeCanopy ? 16: (length < 2 ? 1 : (length * 3 / 8 + rand.nextInt(length / 4)));
		generateCanopy(world, rand, (int) posX, (int) posY, (int) posZ, maxRadius);
	}

	private void generateCanopy(World world, Random rand, int blockX, int blockY, int blockZ, int maxRadius) {
		if (maxRadius > 4) {
			int branchCount = rand.nextInt(3) + 3;
			float angleOffset = rand.nextFloat() * 2 * (float) Math.PI;
			float angle = (float) Math.PI * 2 / branchCount;
			float pitch = (float) Math.PI / 8;
			float deltaY = MathHelper.sin(pitch);
			float cosPitch = MathHelper.cos(pitch);
			int size = 1;
			for (int branch = 0; branch < branchCount; branch++) {
				double posX = blockX, posY = blockY - 2, posZ = blockZ;
				float yaw = angle * branch + angleOffset;
				float deltaX = MathHelper.cos(yaw) * cosPitch;
				float deltaZ = MathHelper.sin(yaw) * cosPitch;
				int length = (int) (maxRadius * (rand.nextFloat() * (0.9F - 0.5F) + 0.5F));
				for (int step = 0; step < length; step++) {
					posX += deltaX;
					posY += deltaY;
					posZ += deltaZ;
					for (int x = -size; x <= size; x++) {
						for (int z = -size; z <= size; z++) {
							for (int y = -size; y <= size; y++) {
								if (MathHelper.sqrt_float(x * x + y * y + z * z) <= size) {
									world.setBlockState(new BlockPos((int) posX + x, (int) posY + y, (int) posZ + z), BARK);
								}
							}
						}
					}
				}
			}
		}
		for (int x = blockX - maxRadius; x <= blockX + maxRadius; x++) {
			for (int z = blockZ - maxRadius; z <= blockZ + maxRadius; z++) {
				for (int y = blockY; y < blockY + maxRadius; y++) {
					int dist = (int) Math.round(Math.sqrt(Math.pow(x - blockX, 2) + Math.pow(z - blockZ, 2) + Math.pow(y - blockY, 2.5)));
					if (dist < maxRadius - 1 && rand.nextInt(4) == 0 && y > blockY) {
						if (world.getBlockState(new BlockPos(x, y, z)).getBlock() != BlockRegistry.LOG_WEEDWOOD) {
							world.setBlockState(new BlockPos(x, y, z), BlockRegistry.LOG_WEEDWOOD.getDefaultState());
						}
					}
					if (dist <= maxRadius) {
						if (world.getBlockState(new BlockPos(x, y, z)).getBlock() != BlockRegistry.LOG_WEEDWOOD && world.getBlockState(new BlockPos (x, y, z)) != BARK) {
							world.setBlockState(new BlockPos(x, y, z), LEAVES, 0);
						}
					}
					if (dist <= maxRadius && rand.nextInt(2) == 0 && y == blockY) {
						if (world.getBlockState(new BlockPos(x, y, z)) == LEAVES) {
							for (int i = 1, length = rand.nextInt(3) + 1; i < length; i++) {
								world.setBlockState(new BlockPos(x, y - i, z), LEAVES, 0);
								if (i == 2) {
									addHangers(world, rand, x, y - i - 1, z);
								}
							}
						}
					}
				}
			}
		}
	}

	private void addHangers(World world, Random rand, int x, int startY, int z) {
		if (rand.nextInt(4) == 0) {
			int length = rand.nextInt(10) + 10;
			for (int y = startY; y > startY - length; y--) {
				if (world.getBlockState(new BlockPos(x, y, z)).getMaterial().isReplaceable()) {
					//world.setBlockState(new BlockPos(x, y, z), HANGER);
				} else {
					break;
				}
			}
		}
	}
}
