package thebetweenlands.common.world.gen.feature.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.progressivegen.ProgressiveGenChunkMarker;
import thebetweenlands.common.world.gen.progressivegen.ProgressiveGenerator;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;
import thebetweenlands.common.world.storage.world.shared.location.EnumLocationType;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

public class WorldGenGiantTree extends WorldGenGiantTreeTrunk implements ProgressiveGenerator {
	private final ChunkMaker marker;

	public WorldGenGiantTree(@Nullable ChunkMaker marker) {
		this.marker = marker;
	}

	public WorldGenGiantTree() {
		this.marker = null;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		boolean gen = super.generate(world, rand, pos);
		if(gen) {
			this.generateWorldLocation(world, rand, pos);
		}
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

	protected void generateBranches(World world, Random rand, int baseRadius, int height, int endRadius, int baseY, int blockX, int blockY, int blockZ, boolean top) {
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

	protected void generateBranch(World world, Random rand, float posX, float posY, float posZ, float yaw, float pitch, int length, float startSize, float endSize) {
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
		int maxRadius = largeCanopy ? 16: (length < 2 ? 1 : (length * 3 / 8 + rand.nextInt(length / 4)));
		generateCanopy(world, rand, (int) posX, (int) posY, (int) posZ, maxRadius);
	}

	protected void generateCanopy(World world, Random rand, int blockX, int blockY, int blockZ, int maxRadius) {
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
									this.setBlockAndNotifyAdequately(world, new BlockPos((int) posX + x, (int) posY + y, (int) posZ + z), BARK);
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
							this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), BlockRegistry.LOG_WEEDWOOD.getDefaultState());
						}
					}
					if (dist <= maxRadius) {
						if (world.getBlockState(new BlockPos(x, y, z)).getBlock() != BlockRegistry.LOG_WEEDWOOD && world.getBlockState(new BlockPos (x, y, z)) != BARK) {
							this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), LEAVES);
						}
					}
					if (dist <= maxRadius && rand.nextInt(2) == 0 && y == blockY) {
						if (world.getBlockState(new BlockPos(x, y, z)) == LEAVES) {
							for (int i = 1, length = rand.nextInt(3) + 1; i < length; i++) {
								this.setBlockAndNotifyAdequately(world, new BlockPos(x, y - i, z), LEAVES);
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

	protected void addHangers(World world, Random rand, int x, int startY, int z) {
		if (rand.nextInt(4) == 0) {
			int length = rand.nextInt(10) + 10;
			for (int y = startY; y > startY - length; y--) {
				if (world.getBlockState(new BlockPos(x, y, z)).getMaterial().isReplaceable()) {
					if(this.marker != null) {
						this.marker.addHanger(new BlockPos(x, y, z));
					} else {
						this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), HANGER);
					}
				} else {
					break;
				}
			}
		}
	}

	protected void generateWorldLocation(World world, Random rand, BlockPos pos) {
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		LocationStorage treeLocation = new LocationStorage(worldStorage, UUID.randomUUID().toString(), SharedRegion.getFromBlockPos(pos), "giantTree", EnumLocationType.GIANT_TREE);
		treeLocation.addBounds(new AxisAlignedBB(pos.getX() - 32, pos.getY() - 10, pos.getZ() - 32, pos.getX() + 32, pos.getY() + 80, pos.getZ() + 32));
		treeLocation.linkChunks();
		treeLocation.setDirty(true);
		treeLocation.setSeed(rand.nextLong());
		worldStorage.addSharedStorage(treeLocation);
	}

	@Override
	public void generateBulk(World world, Random rand, BlockPos pos) {
		this.generateTree(world, rand, pos);
	}

	@Override
	public void post(World world, Random rand, BlockPos pos) {
		this.generateWorldLocation(world, rand, pos);

		if(this.marker != null) {
			for(Long hangerPos : this.marker.hangers) {
				world.setBlockState(BlockPos.fromLong(hangerPos), HANGER);
			}
			this.marker.hangers.clear();
		}
	}

	public static class ChunkMaker extends ProgressiveGenChunkMarker<WorldGenGiantTree> {
		private final List<Long> hangers = new ArrayList<>();

		public ChunkMaker(WorldDataBase<?> worldStorage, String id, SharedRegion region) {
			super(worldStorage, id, region);
		}

		public ChunkMaker(WorldDataBase<?> worldStorage, String id, SharedRegion region, BlockPos pos, long seed) {
			super(worldStorage, id, region, pos, seed);
		}

		public void addHanger(BlockPos pos) {
			this.hangers.add(pos.toLong());
		}

		@Override
		protected WorldGenGiantTree getGenerator() {
			return new WorldGenGiantTree(this);
		}
	}
}
