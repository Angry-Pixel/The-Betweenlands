package thebetweenlands.world.feature.trees;

import static thebetweenlands.blocks.BLBlockRegistry.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenGiantTree implements IWorldGenerator {
	private static final int MIN_TRUNK_RADIUS = 2;
	private static final int MAX_TRUNK_RADIUS = 11;

	private static final int STEEPNESS = 180;

	public static final ForgeDirection[] DIRECTIONS = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };

	public static final byte[][] TRUNK_LAYERS = new byte[MAX_TRUNK_RADIUS - MIN_TRUNK_RADIUS + 1][];

	static {
		initTrunkLayers();
	}

	@Override
	public final void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == ConfigHandler.DIMENSION_ID) {
			int blockX = chunkX * 16, blockZ = chunkZ * 16;
			int blockY = 82 + rand.nextInt(5);
			BiomeGenBase biomeBase = world.getBiomeGenForCoords(blockX, blockZ);
			if (isValidBiome(biomeBase)) {
				// magic numbers for testing
				for (int nearX = blockX - 5; nearX <= blockX + 5; nearX++) {
					for (int nearZ = blockZ - 5; nearZ <= blockZ + 5; nearZ++) {
						Block block = world.getBlock(nearX, blockY, nearZ);
						if (block != null && block == biomeBase.topBlock) {
							if (rand.nextInt(ConfigHandler.GIANT_TREE_DENSITY) == 0) {
								if (generateTree(world, rand, blockX, blockY, blockZ)) {
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	public final boolean generateTree(World world, Random rand, int blockX, int blockY, int blockZ) {
		int baseRadius = rand.nextInt(4) + 8;
		int height = rand.nextInt(baseRadius) + baseRadius * getRadiusHeightRatio();
		int maxRadius = baseRadius + height / 3;
		if (isSpaceOccupied(world, blockX, blockY, blockZ, maxRadius, height)) {
			return false;
		}
		int mirrorX = rand.nextBoolean() ? -1 : 1;
		int mirrorZ = rand.nextBoolean() ? -1 : 1;
		initAttributes(rand);
		List<Fungus> fungi = new ArrayList<Fungus>();
		for (int dy = 0; dy < height; dy++) {
			int y = blockY + dy;
			int radius = getRadius(baseRadius, dy);
			byte[] form = TRUNK_LAYERS[radius - MIN_TRUNK_RADIUS];
			int size = radius * 2 + 1;
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					byte state = form[dx * mirrorX + radius + (dz * mirrorZ + radius) * size];
					switch (state) {
					case 0:
						if (world.getBlock(blockX + dx, y - 1, blockZ + dz) == weedwood) {
							world.setBlock(blockX + dx, y - 1, blockZ + dz, weedwoodBark);
						}
						break;
					case 1:
						placeWood(world, rand, radius, height, blockX, blockY, blockZ, dx, dy, dz);
						break;
					case 2:
						placeBark(world, rand, radius, height, blockX, blockY, blockZ, dx, dy, dz);
						if (canFungusGenerateAtY(dy, height) && rand.nextInt(32) == 0) {
							float distance = MathHelper.sqrt_float(dx * dx + dz * dz);
							int fungusX = (int) (-dx / distance * 2);
							int fungusZ = (int) (-dz / distance * 2);
							int fungusRadius = rand.nextInt(3) + radius / 4 + 3;
							fungi.add(new Fungus(blockX + dx + fungusX, y, blockZ + dz + fungusZ, fungusRadius));
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
		int rootCount = rand.nextInt(3) + 4;
		float angle = 2 * (float) Math.PI / rootCount;
		float angleOffset = rand.nextFloat() * 2 * (float) Math.PI;
		for (int root = 0; root < rootCount; root++) {
			float yaw = angleOffset + angle * root;
			float pitch = -(float) Math.PI / 8 + rand.nextFloat() * (float) Math.PI / 16;
			int length = rand.nextInt(7) + 18;
			float posX = MathHelper.cos(yaw) * baseRadius * 0.2F + blockX, posY = blockY + 2, posZ = MathHelper.sin(yaw) * baseRadius * 0.2F + blockZ;
			generateRoot(world, rand, posX, posY, posZ, yaw, pitch, length, 2, 1);
		}
	}

	private void generateRoot(World world, Random rand, float posX, float posY, float posZ, float yaw, float pitch, int length, float startSize, float endSize) {
		float yawDelta = 0, pitchDelta = 0;
		int branchPoint = rand.nextBoolean() ? length / 2 + rand.nextInt(length / 4) : -1;
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
				return;
			}
			for (int x = -sizeRange; x <= sizeRange; x++) {
				for (int z = -sizeRange; z <= sizeRange; z++) {
					for (int y = -sizeRange; y <= sizeRange; y++) {
						if (MathHelper.sqrt_float(x * x + y * y + z * z) <= size) {
							Block block = world.getBlock((int) posX + x, (int) posY + y, (int) posZ + z);
							Block above = world.getBlock((int) posX + x, (int) posY + y + 1, (int) posZ + z);
							if (block == weedwood && above.getMaterial().isReplaceable()) {
								break;
							}
							world.setBlock((int) posX + x, (int) posY + y, (int) posZ + z, weedwoodBark);
						}
					}
				}
			}
		}
	}

	private boolean isValidBiome(BiomeGenBase biomeBase) {
		return biomeBase == BLBiomeRegistry.swampLands || biomeBase == BLBiomeRegistry.coarseIslands || biomeBase == BLBiomeRegistry.patchyIslands;
	}

	private boolean isSpaceOccupied(World world, int blockX, int blockY, int blockZ, int maxRadius, int height) {
		for (int x = blockX - maxRadius; x <= blockX + maxRadius; x++) {
			for (int z = blockZ - maxRadius; z <= blockZ + maxRadius; z++) {
				for (int y = blockY + 2; y < blockY + height; y++) {
					if (!world.getBlock(x, y, z).getMaterial().isReplaceable()) {
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
		world.setBlock(blockX + dx, blockY + dy, blockZ + dz, weedwood);
	}

	protected void placeBark(World world, Random rand, int radius, int height, int blockX, int blockY, int blockZ, int dx, int dy, int dz) {
		world.setBlock(blockX + dx, blockY + dy, blockZ + dz, weedwoodBark, 0, 2);		
	}

	protected int getRadius(int baseRadius, int dy) {
		// https://www.desmos.com/calculator/s8otj3uq6b
		return (STEEPNESS * MIN_TRUNK_RADIUS - baseRadius * STEEPNESS) / (-baseRadius * dy - STEEPNESS + MIN_TRUNK_RADIUS * dy) + MIN_TRUNK_RADIUS;
	}

	public static void initTrunkLayers() {
		float slope = 2F / (MAX_TRUNK_RADIUS - MIN_TRUNK_RADIUS);
		for (int i = 0; i < TRUNK_LAYERS.length; i++) {
			int radius = i + MIN_TRUNK_RADIUS;
			int size = radius * 2 + 1;
			TRUNK_LAYERS[i] = new byte[size * size];
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					float angle = (float) Math.atan2(z, x);
					float dist = MathHelper.sqrt_float(x * x + z * z) + (i < 3 ? 0 : 0);
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
