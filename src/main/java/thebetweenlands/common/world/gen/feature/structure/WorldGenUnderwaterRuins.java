package thebetweenlands.common.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenUnderwaterRuins extends WorldGenHelper {

	private final IBlockState cragrockBrick = BlockRegistry.CRAGROCK_BRICKS.getDefaultState();
	private final IBlockState cragrockBrickCracked = BlockRegistry.CRAGROCK_BRICKS_CRACKED.getDefaultState();
	private final IBlockState cragrockBrickMossy = BlockRegistry.CRAGROCK_BRICKS_MOSSY.getDefaultState();
	private final IBlockState cragrockTile = BlockRegistry.CRAGROCK_TILES.getDefaultState();
	private final IBlockState cragrockTileCracked = BlockRegistry.CRAGROCK_TILES_CRACKED.getDefaultState();
	private final IBlockState cragrockTileMossy = BlockRegistry.CRAGROCK_TILES_MOSSY.getDefaultState();
	private final IBlockState cragrockChisel = BlockRegistry.CRAGROCK_CHISELED.getDefaultState();
	private final IBlockState cragrockBrickStairs = BlockRegistry.CRAGROCK_BRICK_STAIRS.getDefaultState();
	private static final List<IBlockState> UNDERWATER_PLANTS = ImmutableList.of(BlockRegistry.SWAMP_REED_UNDERWATER.getDefaultState(), BlockRegistry.WATER_WEEDS.getDefaultState(), BlockRegistry.SWAMP_KELP.getDefaultState());

	public WorldGenUnderwaterRuins() {
		super(); //TODO: Replace the dirt underwater and the water
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "underwater_ruins", EnumLocationType.RUINS);

		/*
		 * 1. Check that we are placing under sufficient amount of water as well as surface. (test)
		 * 2. Generation (done)
		 * 3. Decorate with plants
		 * 4. LocationStorage placement
		 *
		 * 4a. WorldStorage (done), x (pos.getX), y (pos.getY), z (pos.getZ), offsetX 0, offsetY 0 , offsetZ 0, width (X), height (Y), depth (Z), Direction
		 * 4b. Grow or Expand is for resizing the box. Dynamic size or Static size?
		 * 4c. Direction isn't randomly determined. Find out a way to make use of it
		 */

		//Pick a random feature
		boolean generated;
		switch (rand.nextInt(5)) {
			case 1:
				generated = structureArch(world, rand, position);
				break;
			case 2:
				generated = structurePillars(world, rand, position);
				break;
			case 3:
				generated = structureRing(world, rand, position);
				break;
			case 4:
				generated = structureShrine(world, rand, position);
				break;
			case 0:
			default:
				generated = structureShelter(world, rand, position);
				break;
		}

		if (generated) {
			//Decorate some plants
			for (int x = -8; x <= 8; x++) {
				for (int z = -8; z <= 8; z++) {
					if (rand.nextInt(4) == 0) {
						IBlockState plant = UNDERWATER_PLANTS.get(rand.nextInt(UNDERWATER_PLANTS.size()));

						if (plant == BlockRegistry.SWAMP_REED_UNDERWATER.getDefaultState() || plant == BlockRegistry.SWAMP_KELP.getDefaultState()) {
							if (SurfaceType.DIRT.matches(world.getBlockState(position))) {
								int height = rand.nextInt(4) + 2;
								for (int y = 0; y <= height; y++) {
									BlockPos plantPos = position.add(x, y + 1, z);
									IBlockState state = world.getBlockState(plantPos);
									if(state.getBlock() != Blocks.AIR && !SurfaceType.WATER.matches(state)) {
										break;
									}
									if(SurfaceType.WATER.matches(state)) {
										world.setBlockState(plantPos, plant, 2 | 16);
									}
								}
							}
						} else {
							//We assume that other plants do not have a stacking height
							if (SurfaceType.DIRT.matches(world.getBlockState(position)) && SurfaceType.WATER.matches(world.getBlockState(position.up()))) {
								world.setBlockState(position.add(x, 1, z), plant, 2 | 16);
							}
						}
					}
				}
			}
		}

		return generated;
	}

	//generate a ring with 4 corner pillars
	private boolean structureRing(World world, Random rand, BlockPos position) {
		//NOTE: Ring size is 3, 5, 7. Generate 2-7, ring size 7 has a middle pillar
		int ringsize = MathHelper.clamp(rand.nextInt(6) + 2, 4, 7); // 0, 1, 2, 3, 4, 5 -> 2, 3, 4, 5, 6, 7
		int center = ringsize / 2;
		int pillarheight = ringsize + rand.nextInt(3); // + 0, 1, 2

		if (!checkValidSpace(world, position.getX(), position.getY(), position.getZ(), ringsize, pillarheight + 1, ringsize))
			return false;

		//floor
		for (int fx = -center; fx <= center; fx++) {
			for (int fz = -center; fz <= center; fz++) {
				//floor
				world.setBlockState(position.add(fx, 0, fz), getTileGrade(rand), 2 | 16);

				//put a pot in the middle of the ring
				if (fx > -center + 1 && fx < center - 1) {
					//do not put a pot in the middle, especially for ringsize = 7
					if (fx != 0 && fz != 0) {
						if (rand.nextInt(7) == 0) {
							setLootPot(world, rand, position.add(fx, 1, fz));
						}
					}
				}

				//ring
				if (!(Math.abs(fx) == center && Math.abs(fz) == center)) {
					if (Math.abs(fx) == center || Math.abs(fz) == center) {
						world.setBlockState(position.add(fx, 1, fz), getBrickGrade(rand), 2 | 16);

						//put a pot on the ring
						if (rand.nextInt(5) == 0) {
							setLootPot(world, rand, position.add(fx, 2, fz));
						}
					}
				} else {
					//pillar
					buildPillar(world, rand, position.add(fx, 1, fz), pillarheight);
				}

				//middle pillar
				if (ringsize >= 7 && (fx == 0 && fz == 0)) {
					buildPillar(world, rand, position.add(fx, 1, fz), ringsize);
				}
			}
		}

		return true;
	}

	private void buildPillar(World world, Random rand, BlockPos pos, int height) {
		for (int y = 0; y <= height; y++) {
			world.setBlockState(pos.add(0, y, 0), getTileGrade(rand), 2 | 16);
		}
		//put a pot on top
		if (rand.nextInt(4) == 0) {
			setLootPot(world, rand, pos.add(0, height + 1, 0));
		}
	}

	//generate a pillar ring
	private boolean structurePillars(World world, Random rand, BlockPos position) {
		if (!checkValidSpace(world, position.getX(), position.getY(), position.getZ(), 7, 9, 7))
			return false;

		//7x7 grid, 5x5 grid of tiles
		int basepillar = 3; //max height 8

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (rand.nextInt(3) == 0) {
					world.setBlockState(position.add(x, 0, z), getTileGrade(rand), 2 | 16);
				}
				if (rand.nextInt(8) == 0) {
					setLootPot(world, rand, position.add(x, 1, z));
				}
			}
		}
		world.setBlockState(position.add(-1, 0, 0), getBrickGrade(rand), 2 | 16);
		world.setBlockState(position.add(1, 0, 0), getBrickGrade(rand), 2 | 16);
		world.setBlockState(position.add(0, 0, -1), getBrickGrade(rand), 2 | 16);
		world.setBlockState(position.add(0, 0, 1), getBrickGrade(rand), 2 | 16);

		position.add(0, 1, 0);

		//north
		buildPillar2(world, rand, position.add(0, 0, -3), rand.nextInt(6) + basepillar);
		//ne
		buildPillar2(world, rand, position.add(2, 0, -2), rand.nextInt(6) + basepillar);
		//east
		buildPillar2(world, rand, position.add(3, 0, 0), rand.nextInt(6) + basepillar);
		//se
		buildPillar2(world, rand, position.add(2, 0, 2), rand.nextInt(6) + basepillar);
		//south
		buildPillar2(world, rand, position.add(0, 0, 3), rand.nextInt(6) + basepillar);
		//sw
		buildPillar2(world, rand, position.add(-2, 0, 2), rand.nextInt(6) + basepillar);
		//west
		buildPillar2(world, rand, position.add(-3, 0, 0), rand.nextInt(6) + basepillar);
		//nw
		buildPillar2(world, rand, position.add(-2, 0, -2), rand.nextInt(6) + basepillar);

		return true;
	}

	private void buildPillar2(World world, Random rand, BlockPos pos, int height) {
		for (int y = 0; y <= height; y++) {
			//if we hit the top, make it chiselled
			world.setBlockState(pos.add(0, y, 0), y == height ? cragrockChisel : getTileGrade(rand), 2 | 16);
		}
		//put a pot on top
		if (rand.nextInt(4) == 0) {
			setLootPot(world, rand, pos.add(0, height + 1, 0));
		}
	}

	//generate some arches
	private boolean structureArch(World world, Random rand, BlockPos pos) {
		if (!checkValidSpace(world, pos.getX(), pos.getY(), pos.getZ(), 7, 7, 7))
			return false;

		if (rand.nextBoolean()) buildArch(world, rand, pos.add(1, 0, 1), EnumFacing.NORTH); //north
		if (rand.nextBoolean()) buildArch(world, rand, pos.add(1, 0, 0), EnumFacing.EAST); //east
		if (rand.nextBoolean()) buildArch(world, rand, pos, EnumFacing.SOUTH); //south
		if (rand.nextBoolean()) buildArch(world, rand, pos.add(0, 0, 1), EnumFacing.WEST); //west

		return true;
	}

	private void buildArch(World world, Random rand, BlockPos pos, EnumFacing directionIn) {
		int direction = directionIn.getHorizontalIndex();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		rotatedCubeVolume(world, x, y, z, 0, 0, -3, getBrickGrade(rand), 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 4, -3, getStateFromRotation(3, direction, cragrockBrickStairs, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 4, -2, getStateFromRotation(1, direction, cragrockBrickStairs, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, -2, getStateFromRotation(3, direction, cragrockBrickStairs, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, -1, getStateFromRotation(1, direction, cragrockBrickStairs, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
	}

	//generate a shelter
	//TODO: Pretty sure it did generate, but check that basements do generate.
	private boolean structureShelter(World world, Random random, BlockPos pos) {
		int length = (random.nextInt(3) + 7) / 2;
		int width = (random.nextInt(3) + 7) / 2;
		boolean basement = random.nextInt(5) == 0;

		if (!checkValidSpace(world, pos.getX(), pos.getY(), pos.getZ(), length * 2, 5, width * 2))
			return false;

		for (int x = -length; x <= length; x++) {
			for (int z = -width; z <= width; z++) {
				world.setBlockState(pos.add(x, 0, z), getTileGrade(random), 2 | 16);

				//build wall
				if (Math.abs(x) == length || Math.abs(z) == width) {
					int height = random.nextInt(3) + 2;
					for (int y = 0; y <= height; y++) {
						world.setBlockState(pos.add(x, y + 1, z), getBrickGrade(random), 2 | 16);
					}
				} else {
					//fill with pots
					if (random.nextInt(10) == 0) {
						setLootPot(world, random, pos.add(0, 1, 0));
					}
				}

				//basement
				if (basement) {
					for (int y = -1; y >= -3; y--) {

						if (Math.abs(x) == length || Math.abs(z) == width) { //underground wall
							world.setBlockState(pos.add(x, y, z), getBrickGrade(random), 2 | 16);
						} else if (y == -3) { //floor
							world.setBlockState(pos.add(x, y, z), getTileGrade(random), 2 | 16);
						} else { //clear, make it water
							world.setBlockState(pos.add(x, y, z), BlockRegistry.SWAMP_WATER.getDefaultState());
						}

						//pots
						if ((Math.abs(x) != length || Math.abs(z) != width) && y == -2) {
							if (random.nextInt(3) == 0) {
								setLootPot(world, random, pos.add(x, y, z));
							}
						}
					}
				}
			}
		}

		return true;
	}

	//generate a large ring of pillars with a small shrine. Spawner?
	private boolean structureShrine(World world, Random random, BlockPos pos) {
		int basepillar = 5;

		if (!checkValidSpace(world, pos.getX(), pos.getY(), pos.getZ(), 10, basepillar + 5, 10))
			return false;

		//make floor
		for (int fx = -4; fx <= 4; fx++) {
			for (int fz = -4; fz <= 4; fz++) {
				if (random.nextBoolean()) {
					world.setBlockState(pos.add(fx, 0, fz), getTileGrade(random), 2 | 16);
				}
			}
		}

		//make pillar ring
		//n
		buildPillar3(world, random, pos.add(-2 , 0, -5), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(0, 0, -5), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(2, 0, -5), random.nextInt(4) + basepillar);
		//e
		buildPillar3(world, random, pos.add(-5, 0, -2), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(-5, 0, 0), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(-5, 0, 2), random.nextInt(4) + basepillar);
		//s
		buildPillar3(world, random, pos.add(-2 , 0, 5), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(0, 0, 5), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(2, 0, 5), random.nextInt(4) + basepillar);
		//w
		buildPillar3(world, random, pos.add(5, 0, -2), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(5, 0, 0), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(5, 0, 2), random.nextInt(4) + basepillar);
		//corners
		buildPillar3(world, random, pos.add(-4, 0, -4), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(4, 0, -4), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(-4, 0, 4), random.nextInt(4) + basepillar);
		buildPillar3(world, random, pos.add(4, 0, 4), random.nextInt(4) + basepillar);

		//shrine
		for (int sx = -2; sx <= 2; sx++) {
			for (int sz = -2; sz <= 2; sz++) {
				for (int sy = 1; sy <= 5; sy++) {
					if ((Math.abs(sx) != 2 || Math.abs(sz) != 2) && sy == 1) {
						world.setBlockState(pos.add(sx, sy, sz), getTileGrade(random), 2 | 16);
					}

					if ((Math.abs(sx) == 2 && Math.abs(sz) == 2) && sy <= 3) {
						world.setBlockState(pos.add(sx, sy, sz), getBrickGrade(random), 2 | 16);
					}

					if (!(Math.abs(sx) == 2 && Math.abs(sz) == 2) && sy == 4) {
						if (Math.abs(sx) == 2 || Math.abs(sz) == 2) {
							world.setBlockState(pos.add(sx, sy, sz), getBrickGrade(random), 2 | 16);
						}
					}

					if (((Math.abs(sx) == 2 && Math.abs(sz) == 0) || (Math.abs(sx) == 0 && Math.abs(sz) == 2)) && sy == 5) {
						world.setBlockState(pos.add(sx, sy, sz), getBrickGrade(random), 2 | 16);
					}

					if (sx == 0 && sz == 0 && sy == 2) {
						BlockPos spawnerpos = pos.add(sx, sy, sz);
						world.setBlockState(spawnerpos, BlockRegistry.MOB_SPAWNER.getDefaultState());
						TileEntity te = world.getTileEntity(spawnerpos);
						if (te instanceof TileEntityMobSpawnerBetweenlands) {
							MobSpawnerLogicBetweenlands logic = ((TileEntityMobSpawnerBetweenlands)te).getSpawnerLogic();
							logic.setNextEntityName("thebetweenlands:angler").setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false/*TODO: is water "air"?*/).setMaxEntities(1 + world.rand.nextInt(3));
						}
					}
				}
			}
		}

		return true;
	}

	private void buildPillar3(World world, Random rand, BlockPos pos, int height) {
		for (int y = 0; y <= height; y++) {
			world.setBlockState(pos.add(0, y, 0), getBrickGrade(rand), 2 | 16);
		}
		if (rand.nextInt(4) == 0) {
			setLootPot(world, rand, pos.add(0, height + 1, 0));
		}
	}

	/**
	 * @param world World
	 * @param posX start position x
	 * @param posY start position y
	 * @param posZ start position z
	 * @param width x size
	 * @param height y size
	 * @param depth z size
	 */
	private boolean checkValidSpace(World world, int posX, int posY, int posZ, int width, int height, int depth) {
		int x = width / 2;
		int z = depth / 2;

		//Check the area for suitable placement.
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int mx = posX - x; mx <= posX + x; mx++) {
			for (int mz = posZ - z; mz <= posZ + z; mz++) {
				for (int my = posY; my <= posY + height; my++) {
					mutable.setPos(mx, my, mz);
					if (my == posY) {
						if (!world.getBlockState(mutable.down()).isFullCube()) {
							return false;
						}
					} else {
						if (!world.isAirBlock(mutable) && !world.getBlockState(mutable).getBlock().isReplaceable(world, mutable)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	//degrades Cragrock Bricks
	private IBlockState getBrickGrade(Random rand) {
		switch (rand.nextInt(3)) {
			case 0:
				return cragrockBrick;
			case 1:
				return cragrockBrickCracked;
			case 2:
				return cragrockBrickMossy;
		}
		return cragrockBrick;
	}

	//degrades Cragrock Tiles
	private IBlockState getTileGrade(Random rand) {
		switch (rand.nextInt(3)) {
			case 0:
				return cragrockTile;
			case 1:
				return cragrockTileCracked;
			case 2:
				return cragrockTileMossy;
		}
		return cragrockTile;
	}

	//creates a pot of loot
	private void setLootPot(World world, Random random, BlockPos pos) {
		this.setBlockAndNotifyAdequately(world, pos, this.getRandomPot(random));
		TileEntityLootPot lootPot = BlockLootPot.getTileEntity(world, pos);
		if(lootPot != null) {
			lootPot.setLootTable(LootTableRegistry.CAVE_POT, random.nextLong()); //TODO: replace
		}
	}

	//get a random pot variant, TODO: Underwater exclusive pots?
	private IBlockState getRandomPot(Random rand) {
		switch (rand.nextInt(3)) {
			case 1:
				return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_2);
			case 2:
				return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_3);
			case 0:
			default:
				return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_1);
		}
	}
}
