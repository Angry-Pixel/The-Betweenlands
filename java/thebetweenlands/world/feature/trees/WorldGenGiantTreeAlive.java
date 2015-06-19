package thebetweenlands.world.feature.trees;

import static thebetweenlands.blocks.BLBlockRegistry.*;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenGiantTreeAlive extends WorldGenGiantTree {
	private static final ForgeDirection[] SHUFFLED_DIRECTIONS = DIRECTIONS.clone();

	@Override
	protected int getRadiusHeightRatio() {
		return 4;
	}

	@Override
	protected boolean canFungusGenerateAtY(int dy, int height) {
		return super.canFungusGenerateAtY(dy, height) && dy >= height / 4 && dy <= height / 4 * 3 - 3;
	}

	@Override
	protected void generateShoots(World world, Random rand, int baseRadius, int height, int blockX, int blockY, int blockZ) {
		super.generateShoots(world, rand, baseRadius, height, blockX, blockY, blockZ);
		int branchCount = world.rand.nextInt(3) + 3;
		float angle = 2 * (float) Math.PI / branchCount;
		float angleOffset = rand.nextFloat() * 2 * (float) Math.PI;
		int endRadius = getRadius(baseRadius, height);
		for (int branch = 0; branch < branchCount; branch++) {
			float yaw = angleOffset + angle * branch;
			float pitch = (float) Math.PI / 4 + rand.nextFloat() * (float) Math.PI / 6;
			int length = rand.nextInt(10) + 20;
			generateBranch(world, rand, blockX, blockY + height - rand.nextInt(height / 2), blockZ, yaw, pitch, length, (int) (endRadius * 0.65F), 1);
		}
		generateBranch(world, rand, blockX, blockY + height, blockZ, angleOffset, (float) Math.PI / 2, 15, endRadius, -2);
	}

	private void generateBranch(World world, Random rand, float posX, float posY, float posZ, float yaw, float pitch, int length, float startSize, float endSize) {
		float yawDelta = 0;
		int branchPoint = endSize < 0 ? -1 : length / 2 + rand.nextInt(length / 4);
		boolean largeCanopy = endSize == -2;
		if (endSize < 0) {
			endSize = 1;
		}
		for (int step = 0; step < length; step++) {
			float along = step / (float) length;
			float cosPitch = MathHelper.cos(pitch);
			posX += MathHelper.cos(yaw) * cosPitch;
			posY += MathHelper.sin(pitch);
			posZ += MathHelper.sin(yaw) * cosPitch;
			pitch *= (1 - along) + along * 0.1F;
			if (along > 0.5F && pitch < (float) Math.PI / 2) {
				pitch += along - 0.2F;
			}
			yaw += yawDelta * 0.1F;
			yawDelta *= 0.8F;
			yawDelta += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4 * (along + 0.1F);
			float size = along * endSize + (1 - along) * startSize;
			int sizeRange = (int) Math.ceil(size);
			if (step == branchPoint && startSize > 1.75F && length > 8) {
				float branchYaw = rand.nextFloat() * (float) Math.PI / 16;
				float branchAngle = rand.nextFloat() * (float) Math.PI / 4 + (float) Math.PI / 4;
				generateBranch(world, rand, posX, posY, posZ, yaw + branchYaw + branchAngle, pitch + (float) Math.PI / 6, length - step, size, -1);
				generateBranch(world, rand, posX, posY, posZ, yaw + branchYaw - branchAngle, pitch + (float) Math.PI / 6, length - step, size, -1);
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
		generateCanopy(world, rand, (int) posX, (int) posY, (int) posZ, (length / 4 + rand.nextInt(length / 4)) * (largeCanopy ? 3 : 1));
	}

	private void generateCanopy(World world, Random rand, int blockX, int blockY, int blockZ, int maxRadius) {
		for (int x = blockX - maxRadius; x <= blockX + maxRadius; x++) {
			for (int z = blockZ - maxRadius; z <= blockZ + maxRadius; z++) {
				for (int y = blockY; y < blockY + maxRadius; y++) {
					int dist = (int) Math.round(Math.sqrt(Math.pow(x - blockX, 2) + Math.pow(z - blockZ, 2) + Math.pow(y - blockY, 2.5)));
					if (dist < maxRadius - 1 && rand.nextInt(4) == 0 && y > blockY) {
						if (world.getBlock(x, y, z) != weedwoodLog) {
							world.setBlock(x, y, z, weedwoodLog);
						}
					}
					if (dist <= maxRadius) {
						if (world.getBlock(x, y, z) != weedwoodLog && world.getBlock(x, y, z) != weedwoodBark) {
							world.setBlock(x, y, z, weedwoodLeaves, 0, 15);
						}
					}
					if (dist <= maxRadius && rand.nextInt(2) == 0 && y == blockY) {
						if (world.getBlock(x, y, z) != weedwoodLog && world.getBlock(x, y, z) == weedwoodLeaves) {
							for (int i = 1; i < 1 + rand.nextInt(3); i++) {
								world.setBlock(x, y - i, z, weedwoodLeaves, 0, 15);
								if(i == 2)
									addHangers(world, rand, x, y - i - 1, z);
								}
						}
					}
				}
			}
		}
	}

	/*@Override
	public boolean generateTree(World world, Random rand, int x, int y, int z) {
		int baseRadius = rand.nextInt(4) + 8;
		int height = rand.nextInt(baseRadius) + baseRadius * 4;
		int maxRadius = baseRadius + height / 3;
	
		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz))
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			if ((yy - y) % 5 == 0 && baseRadius > 2)
				--baseRadius;

			for (int i = baseRadius * -1; i <= baseRadius; ++i)
				for (int j = baseRadius * -1; j <= baseRadius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < baseRadius)
						world.setBlock(x + i, yy, z + j, weedwood, 0, 2);
					if (Math.round(Math.sqrt(dSq)) == baseRadius || Math.round(Math.sqrt(dSq)) <= baseRadius && yy == y + height - 1)
						world.setBlock(x + i, yy, z + j, weedwoodBark, 0, 2);
					if (Math.round(Math.sqrt(dSq)) == baseRadius && yy % 5 == 0 && yy >= y + height/4 && yy <= y + height/4 * 3 - 3) {
						if(rand.nextInt(32) == 0) {
						}
					}
				}
			if (yy == y + height/4 * 3) {
				createBranch(world, rand, x + baseRadius + 1, yy, z, 1, false, height/3 + rand.nextInt(height/10));
				createBranch(world, rand, x - baseRadius - 1, yy, z, 2, false, height/3 + rand.nextInt(height/10));
				createBranch(world, rand, x, yy, z + baseRadius + 1, 3, false, height/3 + rand.nextInt(height/10));
				createBranch(world, rand, x, yy, z - baseRadius - 1, 4, false, height/3 + rand.nextInt(height/10));
			}
			if (yy == y + height/4 * 3 + 3) {
				createBranch(world, rand, x + baseRadius, yy, z + baseRadius, 5, false, height/4 + rand.nextInt(height/10));
				createBranch(world, rand, x - baseRadius - 1, yy, z - baseRadius - 1, 6, false, height/4 + rand.nextInt(height/10));
				createBranch(world, rand, x - baseRadius - 1, yy, z + baseRadius + 1, 7, false, height/4 + rand.nextInt(height/10));
				createBranch(world, rand, x + baseRadius + 1, yy, z - baseRadius - 1, 8, false, height/4 + rand.nextInt(height/10));
			}
			if (yy == y + height/4 * 3 + 6) {
				createBranch(world, rand, x + baseRadius + 1, yy, z, 1, false, height/5 + rand.nextInt(height/15));
				createBranch(world, rand, x - baseRadius - 1, yy, z, 2, false, height/5 + rand.nextInt(height/15));
				createBranch(world, rand, x, yy, z + baseRadius + 1, 3, false, height/5 + rand.nextInt(height/15));
				createBranch(world, rand, x, yy, z - baseRadius - 1, 4, false, height/5 + rand.nextInt(height/15));
			}
			if (yy == y + height/4 * 3 + 9) {
				createBranch(world, rand, x + baseRadius, yy, z + baseRadius, 5, false, height/6 + rand.nextInt(height/15));
				createBranch(world, rand, x - baseRadius - 1, yy, z - baseRadius - 1, 6, false, height/6 + rand.nextInt(height/15));
				createBranch(world, rand, x - baseRadius - 1, yy, z + baseRadius + 1, 7, false, height/6 + rand.nextInt(height/15));
				createBranch(world, rand, x + baseRadius + 1, yy, z - baseRadius - 1, 8, false, height/6 + rand.nextInt(height/15));
			}
			if (yy == y + 3) {
				createBranch(world, rand, x + baseRadius + 1, yy - rand.nextInt(3), z, 1, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x - baseRadius - 1, yy - rand.nextInt(3), z, 2, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x, yy - rand.nextInt(3), z + baseRadius + 1, 3, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x, yy - rand.nextInt(3), z - baseRadius - 1, 4, true, rand.nextInt(9) + 6);

				createBranch(world, rand, x + baseRadius - 1, yy - rand.nextInt(3), z + baseRadius, 5, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x - baseRadius + 1, yy - rand.nextInt(3), z - baseRadius, 6, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x - baseRadius, yy - rand.nextInt(3), z + baseRadius - 1, 7, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x + baseRadius, yy - rand.nextInt(3), z - baseRadius + 1, 8, true, rand.nextInt(9) + 6);
			}
		}
		createMainCanopy(world, rand, x, y + height, z, height/3 + rand.nextInt(height/10));
		return true;
	}

	private void createSmallBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				meta = 0;
			}

			if (dir == 1) {
				world.setBlock(x + i, y, z, weedwoodLog, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x + i, y, z, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 2) {
				world.setBlock(x - i, y, z, weedwoodLog, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x - i, y, z, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 3) {
				world.setBlock(x, y, z + i, weedwoodLog, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x, y, z + i, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 4) {
				world.setBlock(x, y, z - i, weedwoodLog, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x, y, z - i, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 5) {
				world.setBlock(x + i - 1, y, z + i - 1, weedwoodLog, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x + i - 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 6) {
				world.setBlock(x - i + 1, y, z - i + 1, weedwoodLog, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x - i + 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 7) {
				world.setBlock(x - i + 1, y, z + i - 1, weedwoodLog, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x - i + 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 8) {
				world.setBlock(x + i - 1, y, z - i + 1, weedwoodLog, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x + i - 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
		}
	}*/

	/*private void createBranch(World world, Random rand, int x, int y, int z, int dir, boolean isRoot, int branchLength) {
		for (int i = 0; i <= branchLength; i++) {
			if (!isRoot && i >= 3) {
				y++;
			} else if (isRoot && i >= 5) {
				y--;
			}
			if (dir == 1) {
				world.setBlock(x + i, y, z, weedwoodBark);
				world.setBlock(x + i, y - 1, z, weedwood);
				world.setBlock(x + i, y - 1, z + 1, weedwoodBark);
				world.setBlock(x + i, y - 1, z - 1, weedwoodBark);
				world.setBlock(x + i, y - 2, z, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x + i, y - 3, z, 2);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x + i, y - 1, z + 1, 3, 4);
						createSmallBranch(world, rand, x + i, y - 1, z - 1, 4, 4);
					}
						addVines(world, rand, x + i, y - 3, z, 2);
					if (i == branchLength)
						createMainCanopy(world, rand, x + i, y, z, branchLength / 2 + rand.nextInt(branchLength / 4));
				}
			}
			if (dir == 2) {
				world.setBlock(x - i, y, z, weedwoodBark);
				world.setBlock(x - i, y - 1, z, weedwood);
				world.setBlock(x - i, y - 1, z + 1, weedwoodBark);
				world.setBlock(x - i, y - 1, z - 1, weedwoodBark);
				world.setBlock(x - i, y - 2, z, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x - i, y - 3, z, 8);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x - i, y - 1, z + 1, 3, 4);
						createSmallBranch(world, rand, x - i, y - 1, z - 1, 4, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, x - i, y, z, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 3) {
				world.setBlock(x, y, z + i, weedwoodBark);
				world.setBlock(x, y - 1, z + i, weedwood);
				world.setBlock(x + 1, y - 1, z + i, weedwoodBark);
				world.setBlock(x - 1, y - 1, z + i, weedwoodBark);
				world.setBlock(x, y - 2, z + i, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x, y - 3, z + i, 4);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x + 1, y - 1, z + i, 1, 4);
						createSmallBranch(world, rand, x - 1, y - 1, z + i, 2, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, x, y, z + i, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 4) {
				world.setBlock(x, y, z - i, weedwoodBark);
				world.setBlock(x, y - 1, z - i, weedwood);
				world.setBlock(x + 1, y - 1, z - i, weedwoodBark);
				world.setBlock(x - 1, y - 1, z - i, weedwoodBark);
				world.setBlock(x, y - 2, z - i, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x, y - 3, z - i, 1);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x + 1, y - 1, z - i, 1, 4);
						createSmallBranch(world, rand,x - 1, y - 1, z - i, 2, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, x, y, z - i, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 5) {
				world.setBlock(x + i - 1, y, z + i - 1, weedwoodBark);
				world.setBlock(x + i - 1, y - 1, z + i - 1, weedwoodBark);
				world.setBlock(x + i - 1, y - 2, z + i - 1, weedwoodBark);
				world.setBlock(x + i - 2, y - 1, z + i - 1, weedwoodBark);
				world.setBlock(x + i - 1, y - 1, z + i - 2, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x + i - 1, y - 3, z + i - 1, 2);
					if (i == branchLength)
						createMainCanopy(world, rand, x + i - 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 6) {
				world.setBlock(x - i + 1, y, z - i + 1, weedwoodBark);
				world.setBlock(x - i + 1, y - 1, z - i + 1, weedwoodBark);
				world.setBlock(x - i + 1, y - 2, z - i + 1, weedwoodBark);
				world.setBlock(x - i + 2, y - 1, z - i + 1, weedwoodBark);
				world.setBlock(x - i + 1, y - 1, z - i + 2, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x - i + 1, y - 3, z - i + 1, 8);
					if (i == branchLength)
						createMainCanopy(world, rand, x - i + 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 7) {
				world.setBlock(x - i + 1, y, z + i - 1, weedwoodBark);
				world.setBlock(x - i + 1, y - 1, z + i - 1, weedwoodBark);
				world.setBlock(x - i + 1, y - 2, z + i - 1, weedwoodBark);
				world.setBlock(x - i + 2, y - 1, z + i - 1, weedwoodBark);
				world.setBlock(x - i + 1, y - 1, z + i - 2, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x - i + 1, y - 3, z + i - 1, 4);
					if (i == branchLength)
						createMainCanopy(world, rand, x - i + 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 8) {
				world.setBlock(x + i - 1, y, z - i + 1, weedwoodBark);
				world.setBlock(x + i - 1, y - 1, z - i + 1, weedwoodBark);
				world.setBlock(x + i - 1, y - 2, z - i + 1, weedwoodBark);
				world.setBlock(x + i - 2, y - 1, z - i + 1, weedwoodBark);
				world.setBlock(x + i - 1, y - 1, z - i + 2, weedwoodBark);
				if (!isRoot) {
					if (i <= branchLength)
						addVines(world, rand, x + i - 1, y - 3, z - i + 1, 1);
					if (i == branchLength)
						createMainCanopy(world, rand, x + i - 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
		}
	}*/

	/*public void addVineToRandomBlockSide(World world, Random rand, int x, int y, int z) {
		shuffleDirections(rand);;
		for (int i = 0; i < SHUFFLED_DIRECTIONS.length; i++) {
			ForgeDirection direction = SHUFFLED_DIRECTIONS[i];
			if (world.getBlock(x + direction.offsetX, y, z + direction.offsetZ).getMaterial().isReplaceable()) {
				world.setBlock(x + direction.offsetX, y, z + direction.offsetZ, poisonIvy, poisonIvy.getMetaForDirection(direction), 2);
				return;
			}
		}
	}

	private void shuffleDirections(Random rand) {
		for (int index = 0; index < SHUFFLED_DIRECTIONS.length; index++) {
			int swapIndex = rand.nextInt(index + 1);
			ForgeDirection element = SHUFFLED_DIRECTIONS[swapIndex];
			SHUFFLED_DIRECTIONS[swapIndex] = SHUFFLED_DIRECTIONS[index];
			SHUFFLED_DIRECTIONS[index] = element;
		}
	}*/

	/*public void addVines(World world, Random rand, int x, int startY, int z, int meta) {
		if (rand.nextInt(4) == 0) {
			int length = rand.nextInt(10) + 10;
			for (int y = startY; y > startY - length; y--) {
				if (world.getBlock(x, y, z).getMaterial().isReplaceable()) {
					world.setBlock(x, y, z, poisonIvy, meta, 2);
				} else {
					break;
				}
			}
		}
	}*/

	public void addHangers(World world, Random rand, int x, int startY, int z) {
		if (rand.nextInt(4) == 0) {
			int length = rand.nextInt(10) + 10;
			for (int y = startY; y > startY - length; y--) {
				if (world.getBlock(x, y, z).getMaterial().isReplaceable()) {
					world.setBlock(x, y, z, hanger);
				} else {
					break;
				}
			}
		}
	}
}
