package thebetweenlands.common.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.DecorationHelper;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorPositionProvider;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenSpawner extends WorldGenerator {
	private final List<Block> blackListedBlocks = ImmutableList.of(
			BlockRegistry.BETWEENSTONE_TILES, 
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS, 
			BlockRegistry.BETWEENSTONE_BRICKS, 
			BlockRegistry.BETWEENSTONE_BRICK_SLAB
			);
	private final double size;
	private int minY = 0;
	private DecoratorPositionProvider positionProvider = new DecoratorPositionProvider().setOffsetXZ(-4, 4).setOffsetY(0, 8);

	public WorldGenSpawner() {
		super(true);
		this.size = 1.0D;
	}

	public WorldGenSpawner setMinY(int minY) {
		this.minY = minY;
		return this;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		BlockPos center = position;

		position = position.add(-8, 0, -8);

		if (position.getY() <= 4 + this.minY) {
			return false;
		} else {
			position = position.down(4);

			for (int xx = 0; xx < 16; ++xx)
				for (int zz = 0; zz < 16; ++zz)
					for (int yy = 0; yy < 8; ++yy) {
						BlockPos pos = position.add(xx, yy, zz);
						if (!world.isBlockLoaded(pos) || this.blackListedBlocks.contains(world.getBlockState(pos).getBlock()))
							return false;
					}

			boolean canGenerateNearCave = rand.nextInt(8) == 0;

			boolean[] isInBlob = new boolean[4096];
			int blobs = rand.nextInt(32) + 16;

			for (int blob = 0; blob < blobs; ++blob) {
				double sx = (rand.nextDouble() * 6.0D + 3.0D) * this.size;
				double sy = (rand.nextDouble() * 6.0D + 3.0D + (rand.nextInt(12) == 0 ? 8 : 0)) * this.size;
				double sz = (rand.nextDouble() * 6.0D + 3.0D) * this.size;
				double bx = rand.nextDouble() * (16.0D - sx - 2.0D) + 1.0D + sx / 2.0D;
				double by = rand.nextDouble() * (8.0D - sy - 4.0D) + 2.0D + sy / 2.0D;
				double bz = rand.nextDouble() * (16.0D - sz - 2.0D) + 1.0D + sz / 2.0D;

				for (int ox = 1; ox < 15; ++ox) {
					for (int oz = 1; oz < 15; ++oz) {
						for (int oy = 1; oy < 15; ++oy) {
							double dx = ((double)ox - bx) / (sx / 2.0D);
							double dy = ((double)oy - by) / (sy / 2.0D);
							double dz = ((double)oz - bz) / (sz / 2.0D);
							double dst = dx * dx + dy * dy + dz * dz;

							if (dst < 1.0D) {
								isInBlob[(ox * 16 + oz) * 16 + oy] = true;
							}
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ++ox) {
				for (int oz = 0; oz < 16; ++oz) {
					for (int oy = 0; oy < 16; ++oy) {
						boolean isOuterBlock = !isInBlob[(ox * 16 + oz) * 16 + oy] && (ox < 15 && isInBlob[((ox + 1) * 16 + oz) * 16 + oy] || ox > 0 && isInBlob[((ox - 1) * 16 + oz) * 16 + oy] || oz < 15 && isInBlob[(ox * 16 + oz + 1) * 16 + oy] || oz > 0 && isInBlob[(ox * 16 + (oz - 1)) * 16 + oy] || oy < 15 && isInBlob[(ox * 16 + oz) * 16 + oy + 1] || oy > 0 && isInBlob[(ox * 16 + oz) * 16 + (oy - 1)]);

						if (isOuterBlock) {
							Material material = world.getBlockState(position.add(ox, oy, oz)).getMaterial();

							if((oy < 4 || !canGenerateNearCave) && material == Material.AIR) {
								return false;
							} else if(material.isLiquid()) {
								return false;
							}
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ++ox) {
				for (int oz = 0; oz < 16; ++oz) {
					for (int oy = 15; oy >= 0; --oy) {
						if(isInBlob[(ox * 16 + oz) * 16 + oy]) {
							this.setBlockAndNotifyAdequately(world, position.add(ox, oy, oz), Blocks.AIR.getDefaultState());
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ++ox) {
				for (int oz = 0; oz < 16; ++oz) {
					for (int oy = 15; oy >= 0; --oy) {
						boolean isOuterBlock = !isInBlob[(ox * 16 + oz) * 16 + oy] && (ox < 15 && isInBlob[((ox + 1) * 16 + oz) * 16 + oy] || ox > 0 && isInBlob[((ox - 1) * 16 + oz) * 16 + oy] || oz < 15 && isInBlob[(ox * 16 + oz + 1) * 16 + oy] || oz > 0 && isInBlob[(ox * 16 + (oz - 1)) * 16 + oy] || oy < 15 && isInBlob[(ox * 16 + oz) * 16 + oy + 1] || oy > 0 && isInBlob[(ox * 16 + oz) * 16 + (oy - 1)]);

						BlockPos pos = position.add(ox, oy, oz);

						if(isOuterBlock) {
							if(oy < 2) {
								if(world.getBlockState(pos.up()).isFullCube()) {
									this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.SWAMP_DIRT.getDefaultState());	
								} else {
									this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.SWAMP_GRASS.getDefaultState());	
								}
							} else {
								if(world.getBlockState(pos).isFullCube()) {
									world.setBlockState(pos, BlockRegistry.BETWEENSTONE.getDefaultState());
								}
							}
						}
					}
				}
			}

			BlockPos spawnerPos = center.add(0, -1, 0);
			this.setBlockAndNotifyAdequately(world, spawnerPos, BlockRegistry.MOB_SPAWNER.getDefaultState());
			BlockMobSpawnerBetweenlands.setRandomMob(world, spawnerPos, rand);
			MobSpawnerLogicBetweenlands logic = BlockMobSpawnerBetweenlands.getLogic(world, spawnerPos);
			if(logic != null) {
				logic.setSpawnRange(6);
				logic.setCheckRange(16);
				logic.setSpawnInAir(false);
			}

			boolean bigMushroom = false;
			for(int i = 0; i < 40; i++) {
				BlockPos pos = center.add(rand.nextInt(8) - 4, rand.nextInt(8) - 4, rand.nextInt(8) - 4);
				if(world.getBlockState(pos.down()).getBlock() == BlockRegistry.SWAMP_GRASS) {
					bigMushroom |= DecorationHelper.GEN_BIG_BULB_CAPPED_MUSHROOM.generate(world, rand, pos);
				}
			}

			if(!bigMushroom) {
				for(int i = 0; i < 8; i++) {
					BlockPos pos = center.add(rand.nextInt(8) - 4, rand.nextInt(8) - 4, rand.nextInt(8) - 4);
					if(BlockRegistry.BULB_CAPPED_MUSHROOM.canPlaceBlockAt(world, pos)) {
						this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.BULB_CAPPED_MUSHROOM.getDefaultState());
					}
				}
			}

			this.positionProvider.init(world, world.getBiomeForCoordsBody(center), null, rand, center.getX(), center.getY(), center.getZ());

			this.positionProvider.setOffsetY(-6, 3);
			this.positionProvider.setOffsetXZ(-8, 8);

			for(int i = 0; i < 80; i++) {
				DecorationHelper.generateWeepingBlue(this.positionProvider);
			}

			for(int i = 0; i < 260; i++) {
				DecorationHelper.generateSwampDoubleTallgrass(this.positionProvider);
			}

			for(int i = 0; i < 16; i++) {
				DecorationHelper.GEN_SWAMP_TALLGRASS.generate(world, rand, center.add(rand.nextInt(8) - 4, rand.nextInt(8) - 4, rand.nextInt(8) - 4));
			}

			this.positionProvider.setOffsetY(0, 8);
			this.positionProvider.setOffsetXZ(-3, 3);

			for(int i = 0; i < 80; i++) {
				DecorationHelper.generateSpeleothemCluster(this.positionProvider);
			}

			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
			LocationStorage location = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "underground_dungeon", EnumLocationType.DUNGEON);
			location.addBounds(new AxisAlignedBB(center).grow(6, 4, 6));
			location.setLayer(0);
			location.setSeed(rand.nextLong());
			location.setVisible(false);
			location.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(location);

			return true;
		}
	}
}