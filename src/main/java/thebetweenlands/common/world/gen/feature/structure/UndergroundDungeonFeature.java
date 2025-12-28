package thebetweenlands.common.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.block.structure.MobSpawnerBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ConfiguredFeatureRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.CubicBezier;

import java.util.UUID;

public class UndergroundDungeonFeature extends Feature<NoneFeatureConfiguration> {

	private static final CubicBezier SPELEOTHEM_Y_CDF = new CubicBezier(0, 0.5F, 1, 0.2F);

	public UndergroundDungeonFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos position = context.origin();
		RandomSource random = context.random();
		BlockPos center = context.origin().offset(8, -4, 8);

		boolean canGenerateNearCave = random.nextInt(8) == 0;

		boolean[] isInBlob = new boolean[4096];
		int blobs = random.nextInt(32) + 16;

		for (int blob = 0; blob < blobs; ++blob) {
			double sx = (random.nextDouble() * 6.0D + 3.0D);
			double sy = (random.nextDouble() * 6.0D + 3.0D + (random.nextInt(12) == 0 ? 8 : 0));
			double sz = (random.nextDouble() * 6.0D + 3.0D);
			double bx = random.nextDouble() * (16.0D - sx - 2.0D) + 1.0D + sx / 2.0D;
			double by = random.nextDouble() * (8.0D - sy - 4.0D) + 2.0D + sy / 2.0D;
			double bz = random.nextDouble() * (16.0D - sz - 2.0D) + 1.0D + sz / 2.0D;

			for (int ox = 1; ox < 15; ++ox) {
				for (int oz = 1; oz < 15; ++oz) {
					for (int oy = 1; oy < 15; ++oy) {
						double dx = ((double) ox - bx) / (sx / 2.0D);
						double dy = ((double) oy - by) / (sy / 2.0D);
						double dz = ((double) oz - bz) / (sz / 2.0D);
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
						BlockState state = level.getBlockState(position.offset(ox, oy, oz));

						if ((oy < 4 || !canGenerateNearCave) && state.isAir()) {
							return false;
						} else if (state.liquid()) {
							return false;
						}
					}
				}
			}
		}

		for (int ox = 0; ox < 16; ++ox) {
			for (int oz = 0; oz < 16; ++oz) {
				for (int oy = 15; oy >= 0; --oy) {
					if (isInBlob[(ox * 16 + oz) * 16 + oy]) {
						level.removeBlock(position.offset(ox, oy, oz), false);
					}
				}
			}
		}

		for (int ox = 0; ox < 16; ++ox) {
			for (int oz = 0; oz < 16; ++oz) {
				for (int oy = 15; oy >= 0; --oy) {
					boolean isOuterBlock = !isInBlob[(ox * 16 + oz) * 16 + oy] && (ox < 15 && isInBlob[((ox + 1) * 16 + oz) * 16 + oy] || ox > 0 && isInBlob[((ox - 1) * 16 + oz) * 16 + oy] || oz < 15 && isInBlob[(ox * 16 + oz + 1) * 16 + oy] || oz > 0 && isInBlob[(ox * 16 + (oz - 1)) * 16 + oy] || oy < 15 && isInBlob[(ox * 16 + oz) * 16 + oy + 1] || oy > 0 && isInBlob[(ox * 16 + oz) * 16 + (oy - 1)]);

					BlockPos pos = position.offset(ox, oy, oz);

					if (isOuterBlock) {
						if (oy < 2) {
							if (level.getBlockState(pos.above()).isRedstoneConductor(level, pos.above())) {
								level.setBlock(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState(), 3);
							} else {
								level.setBlock(pos, BlockRegistry.SWAMP_GRASS.get().defaultBlockState(), 3);
							}
						} else {
							if (level.getBlockState(pos).isRedstoneConductor(level, pos)) {
								level.setBlock(pos, BlockRegistry.BETWEENSTONE.get().defaultBlockState(), 3);
							}
						}
					}
				}
			}
		}

		BlockPos spawnerPos = center.offset(0, 8, 0);
		level.setBlock(spawnerPos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
		MobSpawnerBlock.setRandomMob(level.getLevel(), spawnerPos, random);
		BetweenlandsBaseSpawner logic = MobSpawnerBlock.getLogic(level, spawnerPos);
		if (logic != null) {
			logic.setSpawnRange(6);
			logic.setCheckRange(16);
			logic.setSpawnInAir(false);
		}

		boolean bigMushroom = false;
		for (int i = 0; i < 40; i++) {
			BlockPos pos = center.offset(random.nextInt(8) - 4, random.nextInt(8) - 4, random.nextInt(8) - 4);
			if (level.getBlockState(pos.below()).is(BlockRegistry.SWAMP_GRASS)) {
				bigMushroom |= level.registryAccess().holderOrThrow(ConfiguredFeatureRegistry.BIG_BULB_CAPPED_MUSHROOM).value().place(level, context.chunkGenerator(), random, pos);
			}
		}

		if (!bigMushroom) {
			for (int i = 0; i < 8; i++) {
				BlockPos pos = center.offset(random.nextInt(8) - 4, random.nextInt(8) - 4, random.nextInt(8) - 4);
				if (BlockRegistry.BULB_CAPPED_MUSHROOM.get().defaultBlockState().canSurvive(level, pos)) {
					level.setBlock(pos, BlockRegistry.BULB_CAPPED_MUSHROOM.get().defaultBlockState(), 3);
				}
			}
		}

		//TODO
//		for (int i = 0; i < 80; i++) {
//			DecorationHelper.generateWeepingBlue(this.positionProvider);
//		}
//
//		for (int i = 0; i < 260; i++) {
//			DecorationHelper.generateSwampDoubleTallgrass(this.positionProvider);
//		}
//
//		for (int i = 0; i < 16; i++) {
//			DecorationHelper.GEN_SWAMP_TALLGRASS.generate(world, random, center.add(random.nextInt(8) - 4, random.nextInt(8) - 4, random.nextInt(8) - 4));
//		}

		for (int i = 0; i < 80; i++) {
			this.generateSpeleothemCluster(level, context.chunkGenerator(), center, random);
		}

		BetweenlandsWorldStorage worldStorage = WorldStorageGetter.getNullable(level);
		if (worldStorage != null) {
			LocationStorage location = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "underground_dungeon", EnumLocationType.DUNGEON);
			location.addBounds(new AABB(center).inflate(6, 4, 6));
			location.setLayer(0);
			location.setSeed(random.nextLong());
			location.setVisible(false);
			location.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(level, location);
		}

		return true;
	}

	private void generateSpeleothemCluster(WorldGenLevel level, ChunkGenerator generator, BlockPos pos, RandomSource random) {
		int x = pos.getX();
		float v = SPELEOTHEM_Y_CDF.eval(random.nextFloat());
		int y = (int) (v * (TheBetweenlands.LAYER_HEIGHT - TheBetweenlands.CAVE_WATER_HEIGHT) + TheBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = pos.getZ();
		for(int i = 0; i < 35; i++) {
			int gx = x + random.nextInt(7) - 3;
			int gy = y + random.nextInt(7) - 3;
			int gz = z + random.nextInt(7) - 3;
			level.registryAccess().holderOrThrow(ConfiguredFeatureRegistry.SPELEOTHEM).value().place(level, generator, random, new BlockPos(gx, gy, gz));
		}
	}

}
