package thebetweenlands.common.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.LootPotBlock;
import thebetweenlands.common.block.entity.LootPotBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.UUID;

public class TarPoolDungeonFeature extends Feature<NoneFeatureConfiguration> {

	public TarPoolDungeonFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos pos = context.origin();
		RandomSource random = context.random();
		byte height = 10;
		int halfSize = 7;

		for (int xx = -halfSize - 1; xx <= halfSize + 1; ++xx) {
			for (int yy = -1; yy <= height + 1; ++yy) {
				for (int zz = halfSize - 1; zz <= halfSize + 1; ++zz) {
					if ((yy == -1 || yy == height + 1) && !level.getBlockState(pos.offset(xx, yy, zz)).isSolid())
						return false;
				}
			}
		}

		for (int x1 = -halfSize; x1 <= halfSize; x1++) {
			for (int z1 = -halfSize; z1 <= halfSize; z1++) {
				for (int y1 = 0; y1 < height; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= halfSize && y1 >= 3)
						level.removeBlock(pos.offset(x1, y1, z1), false);
				}
			}
		}

		for (int yy = 2; yy >= 0; --yy) {
			for (int xx = halfSize * -1; xx <= halfSize; ++xx) {
				for (int zz = halfSize * -1; zz <= halfSize; ++zz) {
					BlockPos placePos = pos.offset(xx, yy, zz);
					double dSq = xx * xx + zz * zz;
					if (Math.round(Math.sqrt(dSq)) == halfSize - 2 && yy >= 1) {
						level.setBlock(placePos, BlockRegistry.BETWEENSTONE_TILES.get().defaultBlockState(), 3);
						if (random.nextBoolean() && yy == 2) {
							if ((xx % 4 == 0 || zz % 4 == 0 || xx % 3 == 0 || zz % 3 == 0) && level.getBlockState(pos.offset(xx, 6, zz)).is(BlockRegistry.BETWEENSTONE)) {
								this.placePillar(level, pos, xx, 3, zz, random);
							}
						}
					}
					if (Math.round(Math.sqrt(dSq)) <= halfSize && Math.round(Math.sqrt(dSq)) >= halfSize - 1 && yy <= 2)
						level.setBlock(placePos, BlockRegistry.BETWEENSTONE.get().defaultBlockState(), 3);
					if (Math.round(Math.sqrt(dSq)) < halfSize - 2 && yy >= 1) {
						level.setBlock(placePos, BlockRegistry.TAR.get().defaultBlockState(), 3);
					}
					if (Math.round(Math.sqrt(dSq)) < halfSize - 2 && yy == 0) {
						if (random.nextBoolean() && random.nextBoolean()) {
							level.setBlock(placePos, BlockRegistry.SOLID_TAR.get().defaultBlockState(), 3);
						} else {
							level.setBlock(placePos, BlockRegistry.BETWEENSTONE.get().defaultBlockState(), 3);
						}
						if (random.nextInt(18) == 0) {
							level.setBlock(placePos, this.getRandomTarLootPot(random).setValue(LootPotBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), 3);
							BlockEntity entity = level.getBlockEntity(placePos);
							if (entity instanceof LootPotBlockEntity pot) {
								pot.setLootTable(LootTableRegistry.TAR_POOL_POT, random.nextLong());
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < 5; i++) {
			BlockPos spawnerPos = pos.offset(random.nextInt(halfSize - 2) - random.nextInt(halfSize - 2), 0, random.nextInt(halfSize - 2) - random.nextInt(halfSize - 2));
			if (level.getBlockEntity(spawnerPos) == null) {
				level.setBlock(spawnerPos, BlockRegistry.TAR_BEAST_SPAWNER.get().defaultBlockState(), 3);
				break;
			}
		}

		BetweenlandsWorldStorage worldStorage = WorldStorageGetter.getNullable(level);
		if (worldStorage != null) {
			LocationStorage location = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "tar_pool_dungeon", EnumLocationType.DUNGEON);
			location.setVisible(true);
			location.addBounds(new AABB(-halfSize, -1, -halfSize, halfSize, height, halfSize).inflate(1, 1, 1));
			location.setLayer(0);
			location.setSeed(random.nextLong());
			location.setVisible(true);
			location.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(level, location);
		}
		return true;
	}

	private void placePillar(WorldGenLevel level, BlockPos pos, int x, int y, int z, RandomSource random) {
		int randHeight = random.nextInt(3);
		for (int yy = y; randHeight + y >= yy; ++yy) {
			level.setBlock(pos.offset(x, y, z), BlockRegistry.BETWEENSTONE_BRICK_WALL.get().defaultBlockState(), 3);
		}
	}

	private BlockState getRandomTarLootPot(RandomSource random) {
		return switch (random.nextInt(3)) {
			case 1 -> BlockRegistry.TAR_LOOT_POT_2.get().defaultBlockState();
			case 2 -> BlockRegistry.TAR_LOOT_POT_3.get().defaultBlockState();
			default -> BlockRegistry.TAR_LOOT_POT_1.get().defaultBlockState();
		};
	}
}
