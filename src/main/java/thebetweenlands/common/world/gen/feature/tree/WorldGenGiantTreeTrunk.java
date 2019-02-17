package thebetweenlands.common.world.gen.feature.tree;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.block.terrain.BlockLeavesBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public abstract class WorldGenGiantTreeTrunk extends WorldGenerator {
	private static final int MIN_TRUNK_RADIUS = 2;
	private static final int MAX_TRUNK_RADIUS = 18;

	private static final int STEEPNESS = 160;

	public static final EnumFacing[] DIRECTIONS = { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST };

	private static final byte[][] TRUNK_LAYERS = new byte[MAX_TRUNK_RADIUS - MIN_TRUNK_RADIUS + 1][];

	public static IBlockState BARK = BlockRegistry.LOG_WEEDWOOD.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
	public static IBlockState WOOD = BlockRegistry.WEEDWOOD.getDefaultState();
	public static IBlockState LEAVES = BlockRegistry.LEAVES_WEEDWOOD_TREE.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
	public static IBlockState IVY = BlockRegistry.POISON_IVY.getDefaultState();
	public static IBlockState HANGER = BlockRegistry.HANGER.getDefaultState();

	static {
		initTrunkLayers();
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		return this.generateTree(world, rand, pos);
	}

	protected boolean generateTree(World world, Random rand, BlockPos pos) {
		int baseRadius = rand.nextInt(6) + 13;
		int height = rand.nextInt(getRadiusHeightRatio() * 4 + 1) + baseRadius * getRadiusHeightRatio() + 6;
		int blockX = pos.getX();
		int blockY = pos.getY();
		int blockZ = pos.getZ();

		if (isSpaceOccupied(world, blockX, blockY, blockZ, baseRadius, height)) {
			return false;
		}

		int mirrorX = rand.nextBoolean() ? -1 : 1;
		int mirrorZ = rand.nextBoolean() ? -1 : 1;
		initAttributes(rand);
		List<Fungus> fungi = new ArrayList<Fungus>();
		for (int dy = 0; dy < height; dy++) {
			int y = blockY + dy;
			int radius = getRadius(baseRadius, dy);
			byte[] layer = TRUNK_LAYERS[radius - MIN_TRUNK_RADIUS];
			int size = radius * 2 + 1;
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					byte state = layer[dx * mirrorX + radius + (dz * mirrorZ + radius) * size];
					switch (state) {
					case 0:
						if (world.getBlockState(new BlockPos(blockX + dx, y - 1, blockZ + dz)).getBlock() == BlockRegistry.LOG_WEEDWOOD) {
							this.setBlockAndNotifyAdequately(world, new BlockPos(blockX + dx, y - 1, blockZ + dz), BARK);
						}
						break;
					case 1:
						placeWood(world, rand, radius, height, blockX, blockY, blockZ, dx, dy, dz);
						break;
					case 2:
						placeBark(world, rand, radius, height, blockX, blockY, blockZ, dx, dy, dz);
						if (canFungusGenerateAtY(dy, height) && rand.nextInt(8) == 0) {
							float distance = MathHelper.sqrt(dx * dx + dz * dz);
							int fungusX = (int) (-dx / distance * 2);
							int fungusZ = (int) (-dz / distance * 2);
							int fungusRadius = rand.nextInt(3) + radius / 4 + 3;
							fungi.add(new Fungus(new BlockPos(blockX + dx + fungusX, y, blockZ + dz + fungusZ), fungusRadius));
						}
					}
				}
			}
		}
		generateShoots(world, rand, baseRadius, height, blockX, blockY, blockZ);
		for (Fungus fungus : fungi) {
			fungus.generate(world, rand);
		}
		return true;
	}

	protected void generateShoots(World world, Random rand, int baseRadius, int height, int blockX, int blockY, int blockZ) {
		generateRoots(world, rand, baseRadius, height, blockY, blockX, blockY + 2, blockZ, rand.nextInt(3) + 4, false);
		generateRoots(world, rand, baseRadius, height, blockY, blockX, blockY + 10, blockZ, rand.nextInt(3) + 3, true);
	}

	private void generateRoots(World world, Random rand, int baseRadius, int height, int baseY, int blockX, int blockY, int blockZ, int rootCount, boolean high) {
		float angle = 2 * (float) Math.PI / rootCount;
		float angleOffset = rand.nextFloat() * 2 * (float) Math.PI;
		for (int root = 0; root < rootCount; root++) {
			float yaw = angle * root + angleOffset;
			float pitch = high ? 0 : -(float) Math.PI / 8 + rand.nextFloat() * (float) Math.PI / 16;
			int length = rand.nextInt(7) + (high ? 25 : 18);
			float posX = MathHelper.cos(yaw) * baseRadius * 0.2F + blockX, posY = blockY, posZ = MathHelper.sin(yaw) * baseRadius * 0.2F + blockZ;
			generateRoot(world, rand, posX, posY, posZ, yaw, pitch, length, 2, 1);
		}
	}

	private void generateRoot(World world, Random rand, float posX, float posY, float posZ, float yaw, float pitch, int length, float startSize, float endSize) {
		float yawDelta = 0, pitchDelta = 0;
		int branchPoint = rand.nextBoolean() ? length / 2 + rand.nextInt(length / 4) : -1;
		int minX = 30_000_000, minY = 255, minZ = 30_000_000, maxX = -30_000_000, maxY = 0, maxZ = -30_000_000;
		for (int step = 0; step < length; step++) {
			float cosPitch = MathHelper.cos(pitch);
			posX += MathHelper.cos(yaw) * cosPitch;
			posY += MathHelper.sin(pitch);
			posZ += MathHelper.sin(yaw) * cosPitch;
			pitch += pitchDelta * 0.1F;
			yaw += yawDelta * 0.1F;
			pitchDelta *= 0.8F;
			yawDelta *= 0.8F;
			float along = step / (float) length;
			yawDelta += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4 * (along + 0.1F);
			if (rand.nextFloat() < 0.75F && pitch > -(float) Math.PI / 4 && along > 0.15F) {
				pitchDelta -= 0.4F;
			}
			float size = along * endSize + (1 - along) * startSize;
			int sizeRange = (int) Math.ceil(size);
			if (step == branchPoint && startSize > 1.75F) {
				float branchYaw = rand.nextFloat() * (float) Math.PI / 8;
				float branchAngle = rand.nextFloat() * (float) Math.PI / 4 + (float) Math.PI / 4;
				generateRoot(world, rand, posX, posY, posZ, yaw + branchYaw + branchAngle, pitch, length - step, size, 1);
				generateRoot(world, rand, posX, posY, posZ, yaw + branchYaw - branchAngle, pitch, length - step, size, 1);
				break;
			}
			if (world.getBlockState(new BlockPos((int) posX, (int) posY, (int) posZ)) == WOOD) {
				continue;
			}
			float sizeSq = size * size;
			for (int x = -sizeRange; x <= sizeRange; x++) {
				for (int z = -sizeRange; z <= sizeRange; z++) {
					for (int y = -sizeRange; y <= sizeRange; y++) {
						float dist = x * x + y * y + z * z;
						if (dist <= sizeSq) {
							int bx = (int) posX + x; 
							int by = (int) posY + y; 
							int bz = (int) posZ + z; 
							this.setBlockAndNotifyAdequately(world, new BlockPos(bx, by, bz), BARK);
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
	}

	protected void makeBarkInsideNotBark(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (world.getBlockState(new BlockPos(x, y, z)) == BARK) {
						IBlockState t;
						if (
								((t = world.getBlockState(new BlockPos(x + 1, y, z))) == BARK || t == WOOD) &&
								((t = world.getBlockState(new BlockPos(x - 1, y, z))) == BARK || t == WOOD) &&
								((t = world.getBlockState(new BlockPos(x, y + 1, z))) == BARK || t == WOOD) &&
								((t = world.getBlockState(new BlockPos(x, y - 1, z))) == BARK || t == WOOD) &&
								((t = world.getBlockState(new BlockPos(x, y, z + 1))) == BARK || t == WOOD) &&
								((t = world.getBlockState(new BlockPos(x, y, z - 1))) == BARK || t == WOOD)
								) {
							this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), WOOD);
						}
					}
				}
			}
		}
	}

	private boolean isSpaceOccupied(World world, int blockX, int blockY, int blockZ, int maxRadius, int height) {
		for (int x = blockX - maxRadius; x <= blockX + maxRadius; x += 2) {
			for (int z = blockZ - maxRadius; z <= blockZ + maxRadius; z += 2) {
				for (int y = blockY; y < blockY + 10 + height; y += 2) {
					BlockPos pos = new BlockPos(x, y, z);
					if (!world.isBlockLoaded(pos) || world.getBlockState(pos).getMaterial() == Material.WOOD) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected int getRadiusHeightRatio() {
		return 1;
	}

	protected boolean canFungusGenerateAtY(int dy, int height) {
		return dy % 5 == 0;
	}

	protected void initAttributes(Random rand) {
	}

	protected void placeWood(World world, Random rand, int radius, int height, int blockX, int blockY, int blockZ, int dx, int dy, int dz) {
		boolean isDentrothyst = dy < 40 ? (rand.nextInt(dy * 2 + 60) == 0 ? true : false) : false;
		if(isDentrothyst) {
			this.setBlockAndNotifyAdequately(world, new BlockPos(blockX + dx, blockY + dy, blockZ + dz), rand.nextInt(20) == 0 ? BlockRegistry.DENTROTHYST.getDefaultState().withProperty(BlockDentrothyst.TYPE, EnumDentrothyst.ORANGE) : BlockRegistry.DENTROTHYST.getDefaultState());
		} else {
			this.setBlockAndNotifyAdequately(world, new BlockPos(blockX + dx, blockY + dy, blockZ + dz), WOOD);
		}
	}

	protected void placeBark(World world, Random rand, int radius, int height, int blockX, int blockY, int blockZ, int dx, int dy, int dz) {
		this.setBlockAndNotifyAdequately(world, new BlockPos(blockX + dx, blockY + dy, blockZ + dz), BARK);		
	}

	protected int getRadius(int baseRadius, int dy) {
		// https://www.desmos.com/calculator/s8otj3uq6b
		return (STEEPNESS * MIN_TRUNK_RADIUS - baseRadius * STEEPNESS) / (-baseRadius * dy - STEEPNESS + MIN_TRUNK_RADIUS * dy) + MIN_TRUNK_RADIUS;
	}

	public static void initTrunkLayers() {
		float slope = 1.5F / (MAX_TRUNK_RADIUS - MIN_TRUNK_RADIUS);
		for (int i = 0; i < TRUNK_LAYERS.length; i++) {
			int radius = i + MIN_TRUNK_RADIUS;
			int size = radius * 2 + 1;
			TRUNK_LAYERS[i] = new byte[size * size];
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					float angle = (float) Math.atan2(z, x);
					float dist = MathHelper.sqrt(x * x + z * z) + (i < 3 ? 0 : 0);
					float fold = (MathHelper.sin(angle * 2 * (float) Math.PI * 2) + 1) * (slope * (radius - MIN_TRUNK_RADIUS));
					if (dist <= radius - fold) {
						TRUNK_LAYERS[i][x + radius + (z + radius) * size] = 1;
					}
				}
			}
			for (int x = 0; x < size; x++) {
				for (int z = 0; z < size; z++) {
					int index = x + z * size;
					int state = TRUNK_LAYERS[i][index];
					if (state == 1) {
						if (x > 0) {
							if (TRUNK_LAYERS[i][x - 1 + z * size] == 0) {
								TRUNK_LAYERS[i][index] = 2;
								continue;
							}
						}
						if (z > 0) {
							if (TRUNK_LAYERS[i][x + (z - 1) * size] == 0) {
								TRUNK_LAYERS[i][index] = 2;
								continue;
							}
						}
						if (x < size - 1) {
							if (TRUNK_LAYERS[i][x + 1 + z * size] == 0) {
								TRUNK_LAYERS[i][index] = 2;
								continue;
							}
						}
						if (z < size - 1) {
							if (TRUNK_LAYERS[i][x + (z + 1) * size] == 0) {
								TRUNK_LAYERS[i][index] = 2;
								continue;
							}
						}
					}
				}
			}
		}
	}
}
