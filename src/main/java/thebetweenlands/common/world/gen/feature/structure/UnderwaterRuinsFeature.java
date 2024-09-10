package thebetweenlands.common.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.entity.LootPotBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.List;
import java.util.UUID;

public class UnderwaterRuinsFeature extends WorldGenHelper<NoneFeatureConfiguration> {

	private final BlockState brick = BlockRegistry.CRAGROCK_BRICKS.get().defaultBlockState();
	private final BlockState brickCracked = BlockRegistry.CRACKED_CRAGROCK_BRICKS.get().defaultBlockState();
	private final BlockState brickMossy = BlockRegistry.MOSSY_CRAGROCK_BRICKS.get().defaultBlockState();
	private final BlockState tile = BlockRegistry.CRAGROCK_TILES.get().defaultBlockState();
	private final BlockState tileCracked = BlockRegistry.CRACKED_CRAGROCK_TILES.get().defaultBlockState();
	private final BlockState tileMossy = BlockRegistry.MOSSY_CRAGROCK_TILES.get().defaultBlockState();
	private final BlockState chisel = BlockRegistry.CHISELED_CRAGROCK.get().defaultBlockState();
	private final BlockState stairs = BlockRegistry.CRAGROCK_STAIRS.get().defaultBlockState();
	//TODO: Plants in here need to consider water state. Some do, some don't
	private final List<BlockState> UNDERWATER_PLANTS = ImmutableList.of(
		BlockRegistry.SWAMP_REED.get().defaultBlockState(),
		BlockRegistry.WATER_WEEDS.get().defaultBlockState(),
		BlockRegistry.SWAMP_KELP.get().defaultBlockState());

	public UnderwaterRuinsFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return false;
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.getOrThrow(level);
		LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "underwater_ruins", EnumLocationType.RUINS);
		boolean generated;

		//Pick a random feature
		generated = switch (rand.nextInt(5)) {
			case 1: yield structureArch(level, rand, pos);
			case 2: yield structurePillars(level, rand, pos);
			case 3: yield structureRing(level, rand, pos);
			case 4: yield structureShrine(level, rand, pos);
			default: yield structureShelter(level, rand, pos);
		};

		if (generated) {
			//Decorate with some plants
			for (int x = -8; x <= 8; x++) {
				for (int z = -8; z <= 8; z++) {
					if (rand.nextInt(4) == 0) {
						BlockState plant = UNDERWATER_PLANTS.get(rand.nextInt(UNDERWATER_PLANTS.size()));
						BlockPos offset = pos.offset(x, 0, z);

						if (plant.is(BlockRegistry.SWAMP_REED.get()) || plant.is(BlockRegistry.SWAMP_KELP)) {
							if (SurfaceType.DIRT.matches(level.getBlockState(offset))) {
								int height = rand.nextInt(6) + 3;
								for (int y = 0; y <= height; y++) {
									BlockPos plantpos = offset.above(y + 1);
									BlockState state = level.getBlockState(plantpos);
									if (!state.isAir() && !SurfaceType.WATER.matches(state)) {
										break;
									}
									if (SurfaceType.WATER.matches(state) && state.canBeReplaced()) {
										level.setBlock(plantpos, plant, 2 | 16);
									}
								}
							}
						} else {
							//We assume that other plants do not have a stacking height
							BlockState state = level.getBlockState(offset.above());
							if (SurfaceType.DIRT.matches(level.getBlockState(pos)) && SurfaceType.WATER.matches(state) && state.canBeReplaced()) {
								level.setBlock(pos.above(), plant, 2 | 16);
							}
						}
					}
				}
			}

			//location storage
			locationStorage.addBounds(new AABB(pos.above(4)).inflate(8.0D, 4.0D, 8.0D));
			locationStorage.setLayer(0);
			locationStorage.setSeed(level.getSeed());
			locationStorage.setVisible(true);
			locationStorage.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
		}

		return generated;
	}

	private boolean structureShelter(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int length = (rand.nextInt(3) + 7) / 2;
		int width = (rand.nextInt(3) + 7) / 2;

		if (!checkValidSpace(level, pos, length * 2, 5, width * 2)) {
			return false;
		}

		for (int x = -length; x <= length; x++) {
			for (int z = -width; z <= width; z++) {
				//floor
				level.setBlock(pos.offset(x, 0, z), getTileGrade(rand), 2 | 16);

				//wall
				if (Math.abs(x) == length || Math.abs(z) == width) {
					int height = rand.nextInt(3) + 2;
					for (int y = 0; y <= height; y++) {
						level.setBlock(pos.offset(x, y + 1, z), getBrickGrade(rand), 2 | 16);
					}
				}

				//floor pots
				if ((Math.abs(x) != length || Math.abs(z) != width)) {
					if (rand.nextInt(10) == 0) {
						setMudLootPot(level, rand, pos.offset(x, 0, z));
					}
				}

				//basement
				if (rand.nextInt(5) == 0) {
					for (int y = -1; y >= -3; y--) {
						if (Math.abs(x) == length || Math.abs(z) == width) { //basement wall
							level.setBlock(pos.offset(x, y, z), getBrickGrade(rand), 2 | 16);
						} else if (y == -3) { // floor
							level.setBlock(pos.offset(x, y, z), getTileGrade(rand), 2 | 16);
						} else {
							if (rand.nextInt(3) == 0 && y == -2) {
								setLootPot(level, rand, pos.offset(x, y, z));
							} else {
								level.setBlock(pos.offset(x, y, z), BlockRegistry.SWAMP_WATER.get().defaultBlockState(), 2 | 16);
							}
						}
					}
				}
			}
		}

		return true;
	}

	//generate some arches
	private boolean structureArch(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (!checkValidSpace(level, pos, 7, 7, 7)) {
			return false;
		}

		boolean north, south, east, west;
		do {
			north = buildArch(level, rand, pos.offset(1, 0, 1), Direction.NORTH);
			east = buildArch(level, rand, pos.offset(1, 0, 0), Direction.EAST);
			south = buildArch(level, rand, pos, Direction.SOUTH);
			west = buildArch(level, rand, pos.offset(0, 0, 1), Direction.WEST);
		} while (!north && !south && !east && !west);

		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				if (rand.nextInt(9) == 0) {
					level.setBlock(pos.offset(x, 0, z), BlockRegistry.PEARL_BLOCK.get().defaultBlockState(), 2 | 16);
				}
			}
		}

		return true;
	}

	private boolean buildArch(WorldGenLevel level, RandomSource rand, BlockPos pos, Direction direction) {
		if (rand.nextBoolean()) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			rotatedCubeVolume(level, x, y, z, 0, 0, -3, getBrickGrade(rand), 1, 4, 1, direction);
			rotatedCubeVolume(level, x, y, z, 0, 4, -3, stairs.setValue(StairBlock.FACING, Direction.EAST), 1, 1, 1, direction);
			rotatedCubeVolume(level, x, y, z, 0, 4, -2, stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(level, x, y, z, 0, 5, -2, stairs.setValue(StairBlock.FACING, Direction.EAST), 1, 1, 1, direction);
			rotatedCubeVolume(level, x, y, z, 0, 5, -1, stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction);

			return true;
		}
		return false;
	}

	//generate a pillar ring
	private boolean structurePillars(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (!checkValidSpace(level, pos, 7, 9, 7)) {
			return false;
		}

		//7x7 grid, 5x5 grid of tiles
		//floor
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (rand.nextInt(3) == 0) {
					level.setBlock(pos.offset(x, 0, z), getTileGrade(rand), 2 | 16);
				}
				if (rand.nextInt(8) == 0) {
					this.setMudLootPot(level, rand, pos.offset(x, 0, z));
				}
			}
		}
		level.setBlock(pos.offset(-1, 0, 0), getBrickGrade(rand), 2 | 16);
		level.setBlock(pos.offset(1, 0, 0), getBrickGrade(rand), 2 | 16);
		level.setBlock(pos.offset(0, 0, -1), getBrickGrade(rand), 2 | 16);
		level.setBlock(pos.offset(0, 0, 1), getBrickGrade(rand), 2 | 16);

		pos = pos.above();

		int basepillar = 3; //max height 8

		buildPillar(level, rand, pos.offset(0, 0, -3), rand.nextInt(6) + basepillar); //N
		buildPillar(level, rand, pos.offset(2, 0, -2), rand.nextInt(6) + basepillar); //NE
		buildPillar(level, rand, pos.offset(3, 0, 0), rand.nextInt(6) + basepillar); //E
		buildPillar(level, rand, pos.offset(2, 0, 2), rand.nextInt(6) + basepillar); //SE
		buildPillar(level, rand, pos.offset(0, 0, 3), rand.nextInt(6) + basepillar); //S
		buildPillar(level, rand, pos.offset(-2, 0, 2), rand.nextInt(6) + basepillar); //SW
		buildPillar(level, rand, pos.offset(-3, 0, 0), rand.nextInt(6) + basepillar); //W
		buildPillar(level, rand, pos.offset(-2, 0, -2), rand.nextInt(6) + basepillar); //NW

		return true;
	}

	private void buildPillar(WorldGenLevel level, RandomSource rand, BlockPos pos, int height) {
		for (int y = 0; y <= height; y++) {
			//chiseled cragrock on top
			level.setBlock(pos.offset(0, y, 0), y == height ? chisel : getTileGrade(rand), 2 | 16);
		}
		//put a pot on top
		if (rand.nextInt(4) == 0) {
			setLootPot(level, rand, pos.offset(0, height + 1, 0));
		}
	}

	//generate a ring with 4 corner pillars
	private boolean structureRing(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		//NOTE: Ring size is 3, 5, 7. Generate 2-7, ring size 7 has a middle pillar
		int ringsize = Mth.clamp(rand.nextInt(6) + 2, 4, 7);  // 0, 1, 2, 3, 4, 5 -> 2, 3, 4, 5, 6, 7
		int pillarheight = ringsize + rand.nextInt(3); // + 0, 1, 2

		if (!checkValidSpace(level, pos, ringsize, pillarheight + 1, ringsize)) {
			return false;
		}

		int center = ringsize / 2;
		for (int x = -center; x <= center; x++) {
			for (int z = -center; z <= center; z++) {
				//floor
				level.setBlock(pos.offset(x, 0, z), getTileGrade(rand), 2 | 16);

				//put pots in the ring, except for the middle
				if (x > -center + 1 && x < center - 1) {
					if (rand.nextInt(7) == 0) {
						setLootPot(level, rand, pos.offset(x, 1, z));
					}
				}

				//ring
				if (!(Math.abs(x) == center && Math.abs(z) == center)) {
					if (Math.abs(x) == center || Math.abs(z) == center) {
						level.setBlock(pos.offset(x, 1, z), getBrickGrade(rand), 2 | 16);

						//put a pot on the ring
						if (rand.nextInt(5) == 0) {
							setLootPot(level, rand, pos.offset(x, 2, z));
						}
					}
				} else {
					//pillar
					buildPillar(level, rand, pos.offset(x, 1, z), pillarheight);
				}

				//middle pillse
				if (ringsize >= 7 && (x == 0 && z == 0)) {
					buildPillar(level, rand, pos.offset(x, 1, z), ringsize);
				}
			}
		}
		return true;
	}

	//generate a large ring of pillars with a small shrine, spawns Anglers
	private boolean structureShrine(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int basepillar = 5;

		if (!checkValidSpace(level, pos, 10, basepillar + 5, 10)) {
			return false;
		}

		//floor
		for (int x = -4; x <= 4; x++) {
			for (int z = -4; z <= 4; z++) {
				if (rand.nextBoolean()) {
					level.setBlock(pos.offset(x, 0, z), getTileGrade(rand), 2 | 16);
				}
			}
		}

		//pillar ring
		//n
		buildPillar(level, rand, pos.offset(-2, 0, -5), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(0, 0, -5), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(2, 0, -5), rand.nextInt(4) + basepillar);
		//e
		buildPillar(level, rand, pos.offset(-5, 0, -2), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(-5, 0, 0), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(-5, 0, 2), rand.nextInt(4) + basepillar);
		//s
		buildPillar(level, rand, pos.offset(-2, 0, 5), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(0, 0, 5), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(2, 0, 5), rand.nextInt(4) + basepillar);
		//w
		buildPillar(level, rand, pos.offset(5, 0, -2), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(5, 0, 0), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(5, 0, 2), rand.nextInt(4) + basepillar);
		//corners
		buildPillar(level, rand, pos.offset(-4, 0, -4), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(4, 0, -4), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(-4, 0, 4), rand.nextInt(4) + basepillar);
		buildPillar(level, rand, pos.offset(4, 0, 4), rand.nextInt(4) + basepillar);

		//shrine
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = 1; y <= 5; y++) {
					if ((Math.abs(x) != 2 || Math.abs(z) != 2) && y == 1) {
						level.setBlock(pos.offset(x, y, z), getTileGrade(rand), 2 | 16);
					}

					if ((Math.abs(x) == 2 && Math.abs(z) == 2) && y <= 3) {
						level.setBlock(pos.offset(x, y, z), getBrickGrade(rand), 2 | 16);
					}

					if (!(Math.abs(x) == 2 && Math.abs(z) == 2) && y == 4) {
						if (Math.abs(x) == 2 || Math.abs(z) == 2) {
							level.setBlock(pos.offset(x, y, z), getBrickGrade(rand), 2 | 16);
						}
					}

					if (((Math.abs(x) == 2 && Math.abs(z) == 0) || (Math.abs(x) == 0 && Math.abs(z) == 2)) && y == 5) {
						level.setBlock(pos.offset(x, y, z), getBrickGrade(rand), 2 | 16);
					}

					if (x == 0 && z == 0 && y == 2) {
						BlockPos spawnerpos = pos.offset(x, y, z);
						level.setBlock(spawnerpos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 2 | 16);
						//TODO: Anglers have not been registered yet
						//TODO: BetweenlandsBaseSpawner cannot accept a WorldGenLevel
//						BlockEntity be = level.getBlockEntity(spawnerpos);
//						if (be instanceof MobSpawnerBlockEntity spawner) {
//							BetweenlandsBaseSpawner logic = spawner.getSpawner();
//							logic.setNextEntityName(EntityRegistry.ANGLER.get(), level, rand, spawnerpos).setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false).setMaxEntities(1 + rand.nextInt(3));
//						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Validate the area for feature generation.
	 *
	 * @param level Level
	 * @param position start position
	 * @param width x size
	 * @param height y size
	 * @param depth z size
	 */
	private boolean checkValidSpace(WorldGenLevel level, BlockPos position, int width, int height, int depth) {
		int posX = position.getX();
		int posY = position.getY();
		int posZ = position.getZ();
		int x = width / 2;
		int z = depth / 2;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		//Check the area for suitable placement
		for (int mx = posX - x; mx <= posX + x; mx++) {
			for (int mz = posZ - z; mz <= posZ + z; mz++) {
				for (int my = posY; my <= posY + height; my++) {
					mutable.set(mx, my, mz);
					if (my <= posY + 1) {
						if (!Block.isShapeFullBlock(level.getBlockState(mutable.below()).getShape(level, mutable.below()))) {
							return false;
						}
					} else {
						if (!level.getBlockState(mutable).isAir() && !level.getBlockState(mutable).canBeReplaced()) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void setLootPot(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		BlockState loot;
		if (level.getBlockState(pos) == BlockRegistry.SWAMP_WATER.get().defaultBlockState()) {
			loot = rand.nextInt(4) == 0 ? BlockRegistry.PEARL_BLOCK.get().defaultBlockState() : getRandomMudPot(rand);
		} else {
			loot = this.getRandomPot(rand);
		}
		this.setBlock(level, pos, loot);

		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof LootPotBlockEntity lootPot) {
			lootPot.setLootTable(LootTableRegistry.CAVE_POT, rand.nextLong());
		}
	}

	private void setMudLootPot(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		this.setBlock(level, pos, getRandomMudPot(rand));
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof LootPotBlockEntity lootPot) {
			lootPot.setLootTable(LootTableRegistry.UNDERWATER_RUINS_POT, rand.nextLong());
		}
	}

	private BlockState getBrickGrade(RandomSource rand) {
		return switch (rand.nextInt(3)) {
			case 1 -> brickCracked;
			case 2 -> brickMossy;
			default -> brick;
		};
	}

	private BlockState getTileGrade(RandomSource rand) {
		return switch (rand.nextInt(3)) {
			case 1 -> tileCracked;
			case 2 -> tileMossy;
			default -> tile;
		};
	}

	private BlockState getRandomPot(RandomSource rand) {
		return switch (rand.nextInt(3)) {
			case 1 -> BlockRegistry.LOOT_POT_2.get().defaultBlockState();
			case 2 -> BlockRegistry.LOOT_POT_3.get().defaultBlockState();
			default -> BlockRegistry.LOOT_POT_1.get().defaultBlockState();
		};
	}

	private BlockState getRandomMudPot(RandomSource rand) {
		return switch (rand.nextInt(3)) {
			case 1 -> BlockRegistry.MUD_LOOT_POT_2.get().defaultBlockState();
			case 2 -> BlockRegistry.MUD_LOOT_POT_3.get().defaultBlockState();
			default -> BlockRegistry.MUD_LOOT_POT_1.get().defaultBlockState();
		};
	}
}
