package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.common.tile.TileEntityMudBricksAlcove;
import thebetweenlands.common.world.gen.feature.structure.utils.MazeGenerator;
import thebetweenlands.common.world.gen.feature.structure.utils.PerfectMazeGenerator;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;

public class WorldGenSludgeWormDungeon extends WorldGenerator {

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();
	private SludgeWormMazeMicroBuilds microBuild = new SludgeWormMazeMicroBuilds();
	private LightTowerBuildParts lightTowerBuild = new LightTowerBuildParts();

	public WorldGenSludgeWormDungeon() {
		super(true);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		//conditions blah, blah...
		makeMaze(world, rand, pos);

		//locations blah, blah, blah...
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationSludgeWormDungeon location = new LocationSludgeWormDungeon(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos));
		location.addBounds(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 29, pos.getY() - 8 * 6 - 3, pos.getZ() + 29));
		location.linkChunks();
		location.setLayer(0);
		location.setSeed(rand.nextLong());
		location.setStructurePos(pos);
		location.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);

		return true;
	}

	public void generateTower(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int height = 16;
		int radius = 9;
		int level1 = 0;
		int level2 = 8;
		int level3 = 16;

		// main
		for (int yy = y; y + height >= yy; yy++) {
			for (int i = radius * -1; i <= radius; ++i) {
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) == radius && yy - y < level2)
						world.setBlockState(new BlockPos(x + i, yy, z + j), blockHelper.getMudBricksForLevel(rand, 0, 1), 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy - y >= level2)
						world.setBlockState(new BlockPos(x + i, yy, z + j), blockHelper.getMudBricksForLevel(rand, 0, 1), 2);

					if (yy == y + level1 || yy == y + level2) {
						if (Math.round(Math.sqrt(dSq)) <= radius - 8)
							world.setBlockState(new BlockPos(x + i, yy, z + j), blockHelper.BETWEENSTONE_BRICKS, 2);
					}
					
					if (yy == y + level1) {
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.SOUTH, rand, level1, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.EAST, rand, level1, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.NORTH, rand, level1, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.WEST, rand, level1, 0);
					}

					if (yy == y + level2) {
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.SOUTH, rand, level2, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.EAST, rand, level2, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.NORTH, rand, level2, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.WEST, rand, level2, 0);
					}

					if (yy == y + level3) {
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.SOUTH, rand, level3, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.EAST, rand, level3, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.NORTH, rand, level3, 0);
						lightTowerBuild.addTowerFloor(world, pos, EnumFacing.WEST, rand, level3, 0);
					}

					//TODO meh, will sort out later
					if (yy == y + level1 + 1 || yy == y + level2 + 1 || yy == y + level3 + 1 || yy == y + level1 + 2 || yy == y + level2 + 2 || yy == y + level3 + 2) {
						if (i == 9 && j == 0 || i == -9 && j == 0 || i == 0 && j == 9 || i == 0 && j == -9)
							world.setBlockToAir(new BlockPos(x + i, yy, z + j));
					}
				}
			} 
		}

		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.SOUTH, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.EAST, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.NORTH, rand, level3, 0, true);

		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.EAST, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.NORTH, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.WEST, rand, level3, 0, true);

		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.NORTH, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.WEST, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.SOUTH, rand, level3, 0, true);

		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.WEST, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.SOUTH, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.EAST, rand, level3, 0, true);

		System.out.println("TOWER!");
	}

	private void generateDecayPit(World world, BlockPos pos) {
		for (int xx = - 16; xx <= 16; xx++) {
			for (int zz = - 16; zz <= 16; zz++) {
				for (int yy = 0; yy > -16; yy--) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);
					if (Math.round(Math.sqrt(dSqDome)) < 17)
						if (dSqDome >= Math.pow(15, 2.0D))
							world.setBlockState(pos.add(xx, yy, zz), blockHelper.MUD_TILES_DECAY, 2);
						else
							world.setBlockToAir(pos.add(xx, yy, zz));
				
				if(xx < -12 || xx > 12)
					world.setBlockToAir(pos.add(xx, yy, zz));
				if(zz < -12 || zz > 12)
					world.setBlockToAir(pos.add(xx, yy, zz));
				}
			}
		}
	}

	public void makeMaze(World world, Random rand, BlockPos pos) {
		for (int level = 0; level <= 7; level++) {
			int yy = -6 -(level * 6);
			if (level == 7) {
				for(int xx = 0; xx <= 28; xx++) {
					for(int zz = 0; zz <= 28; zz++) {
						for(int yUp = yy - 2; yUp < yy + 6; yUp++) {
							world.setBlockState(pos.add(xx, yUp, 0), getMudBricksForLevel(rand, level, yUp), 2);
							world.setBlockState(pos.add(xx, yUp, 28), getMudBricksForLevel(rand, level, yUp), 2);
							world.setBlockState(pos.add(0, yUp, zz), getMudBricksForLevel(rand, level, yUp), 2);
							world.setBlockState(pos.add(28, yUp, zz), getMudBricksForLevel(rand, level, yUp), 2);
							if (!isSolidStructureBlock(world.getBlockState(pos.add(xx, yUp, zz))) && !(world.getBlockState(pos.add(xx, yUp, zz)) instanceof BlockStairs))
								world.setBlockToAir(pos.add(xx, yUp, zz));
						}
					}
				}
				buildFloor(world, pos.add(0, yy - 3, 0), rand, 7, 7, true, true, level);
				buildRoof(world, pos.add(0, yy - 3, 0).up(8), rand, 7, 7, level);
			}

			if (level < 7 && level >= 0)
				generateMaze(world, rand, pos.add(0, yy, 0), level);

			if (level <= 7) {
				// create STAIRS
				if (level == 1 || level == 3 || level == 5 || level == 7) { //TODO seven won't need this, leaving for now
					world.setBlockState(pos.add(1, yy + 5, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH, 2);
					world.setBlockState(pos.add(27, yy + 0, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH, 2);
					world.setBlockState(pos.add(27, yy + 1, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH, 2);
					world.setBlockState(pos.add(27, yy + 2, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH, 2);
					world.setBlockState(pos.add(27, yy + 3, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH, 2);
					world.setBlockState(pos.add(27, yy + 4, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH, 2);
					world.setBlockState(pos.add(1, yy + 1, 1), getMudBricksForLevel(rand, level, 1), 2);
					world.setBlockState(pos.add(1, yy + 2, 1), getMudBricksForLevel(rand, level, 2), 2);
					world.setBlockState(pos.add(1, yy + 3, 1), getMudBricksForLevel(rand, level, 3), 2);
					world.setBlockState(pos.add(1, yy + 4, 1), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.BOTTOM), 2);
					if (world.isAirBlock(pos.add(1, yy + 1, 2)))
						world.setBlockState(pos.add(1, yy + 1, 2), getMudBricksForLevel(rand, level, 1), 2);
					placeRuneCombination(world, pos.add(0, yy + 2, 0), level, rand);
					world.setBlockState(pos.add(1, yy + 3, 2), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(1, yy + 2, 3), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(1, yy + 1, 3), getMudBricksForLevel(rand, level, 1), 2);
					world.setBlockState(pos.add(2, yy + 1, 3), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.BOTTOM), 2);
				} else {
					world.setBlockState(pos.add(27, yy + 5, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH, 2);
					world.setBlockState(pos.add(1, yy + 0, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH, 2);
					world.setBlockState(pos.add(1, yy + 1, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH, 2);
					world.setBlockState(pos.add(1, yy + 2, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH, 2);
					world.setBlockState(pos.add(1, yy + 3, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH, 2);
					world.setBlockState(pos.add(1, yy + 4, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH, 2);
					world.setBlockState(pos.add(27, yy + 1, 27), getMudBricksForLevel(rand, level, 1), 2);
					world.setBlockState(pos.add(27, yy + 2, 27), getMudBricksForLevel(rand, level, 2), 2);
					world.setBlockState(pos.add(27, yy + 3, 27), getMudBricksForLevel(rand, level, 3), 2);
					world.setBlockState(pos.add(27, yy + 4, 27), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.BOTTOM), 2);
					if (world.isAirBlock(pos.add(27, yy + 1, 26)))
						world.setBlockState(pos.add(27, yy + 1, 26), getMudBricksForLevel(rand, level, 1), 2);
					placeRuneCombination(world, pos.add(0, yy + 2, 0), level, rand);
					world.setBlockState(pos.add(27, yy + 3, 26), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(27, yy + 2, 25), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(27, yy + 1, 25), getMudBricksForLevel(rand, level, 1), 2);
					world.setBlockState(pos.add(26, yy + 1, 25), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.BOTTOM), 2);
				}
				if (level != 7)
					addCeilingSlabs(world, pos.add(0, yy + 4, 0), 7, 7, rand, level);
				stairsAir(world, rand, pos.add(0, yy, 0), level);
			}
		//	System.out.println("Y height is: " + (pos.getY() + yy) + " level: " + level);
		}
	}

	// Maze generation layers
	public void generateMaze(World world, Random rand, BlockPos pos, int level) {
		int sizeX = 16;
		int sizeY = 6;
		int sizeZ = 16;
		int mazeWidth = 7;
		int mazeHeight = 7;

		if (mazeWidth < 2 || mazeHeight < 2 || sizeY < 1)
			return;

		int[][] maze = null;
		MazeGenerator generator = new PerfectMazeGenerator(mazeWidth, mazeHeight);
		maze = generator.generateMaze();
		for (int layer = 0; layer < sizeY; layer++)
			switch (layer) {
				case 0:
					break;
				case 1:
					buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					createAir(world, pos.add(0, layer, 0), rand, mazeWidth, mazeHeight, level);
					addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					break;
				case 2:
					buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					break;
				case 3:
					buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					//addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer); //nothing specific here yet
					break;
				case 4:
					buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					break;
				case 5:
					buildRoof(world, pos.up(layer), rand, mazeWidth, mazeHeight, level);
					addMazeCellFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
					buildFloor(world, pos, rand, mazeWidth, mazeHeight, true, false, level);
					break;
			}
	//	System.out.println("Generated Maze At: X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
	}

	// Levels
	private void buildLevel(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		for (int i = 0; i < h; i++) {
			// draw the north edge
			for (int j = 0; j < w; j++) {
				if ((maze[j][i] & 1) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4 + 1, 0, i * 4), getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4 + 2, 0, i * 4), getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4 + 3, 0, i * 4), getMudBricksForLevel(rand, level, layer), 2);
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				} else
					world.setBlockState(pos.add(j * 4, 0, i * 4), getMudBricksForLevel(rand, level, layer), 2);
			}
				for (int j = 0; j < w; j++) {
				// draw the west edge

				if ((maze[j][i] & 8) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4, 0, i * 4 + 1), getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4, 0, i * 4 + 2), getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4, 0, i * 4 + 3), getMudBricksForLevel(rand, level, layer), 2);
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				}

				if ((maze[j][i] & 4) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				}

				if ((maze[j][i] & 2) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				}

				world.setBlockState(pos.add(w * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
				world.setBlockState(pos.add(w * 4, 0, i * 4 + 1), getMudBricksForLevel(rand, level, layer), 2);
				world.setBlockState(pos.add(w * 4, 0, i * 4 + 2), getMudBricksForLevel(rand, level, layer), 2);
				world.setBlockState(pos.add(w * 4, 0, i * 4 + 3), getMudBricksForLevel(rand, level, layer), 2);
				if (layer == 3)
					addBeamSupports(world, pos.add(w * 4, 0, i * 4), rand, pos);
				if (layer == 4)
					addBeams(world, pos.add(w * 4, 0, i * 4), rand, level, pos);
			}
		}
		// draw the bottom line
		for (int j = 0; j <= w * 4; j++) {
			world.setBlockState(pos.add(j, 0, h * 4), (j % 4 == 0 && (layer == 1 || layer == 2 || layer == 3)) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
			if (layer == 3 && j % 4 == 0)
				addBeamSupports(world, pos.add(j, 0, h * 4), rand, pos);
			if (layer == 4 && j % 4 == 0)
				addBeams(world, pos.add(j, 0, h * 4), rand, level, pos);
		}
	}

	private void addBeamSupports(World world, BlockPos pos, Random rand, BlockPos posOrigin) {
		EnumFacing[] sides = EnumFacing.HORIZONTALS;
		for (EnumFacing facing : sides) {
			if (!isSolidStructureBlock(world.getBlockState(pos.offset(facing))) && world.isAirBlock(pos.offset(facing)))
				if (isWithinMazeAreaForGen(posOrigin, pos.offset(facing)))
					world.setBlockState(pos.offset(facing), getRandomSupportBeam(facing, true, rand));
		}
	}

	private void addBeams(World world, BlockPos pos, Random rand, int level, BlockPos posOrigin) {
		EnumFacing[] sides = EnumFacing.HORIZONTALS;
		for (EnumFacing facing : sides) {
			for (int count = 1; count <= 2; count++)
				if (!isSolidStructureBlock(world.getBlockState(pos.offset(facing, count))) && world.isAirBlock(pos.offset(facing, count)))
					if (isWithinMazeAreaForGen(posOrigin, pos.offset(facing, count)))
						world.setBlockState(pos.offset(facing, count), getRandomBeam(facing.rotateY(), rand, level, count, false));
		}
	}

	// Ceiling Slabs
	private void addCeilingSlabs(World world, BlockPos pos, int w, int h, Random rand, int level) {
		for (int i = 0; i <= h * 4; i++)
			for (int j = 0; j <= w * 4; j++)
				if(j%4 != 0 && i%4 != 0) {
					if(world.isAirBlock(pos.add(j, 0, i)) && !isSolidStructureBlock(world.getBlockState(pos.add(j, 0, i)))) {
						if(level == 0)
							world.setBlockState(pos.add(j, 0, i), getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 2);
						if(level != 0 && rand.nextInt(level) == 0)
							world.setBlockState(pos.add(j, 0, i), getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 2);
				}
		}
	}

	private void addMazeCellFeature(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		// TODO add features here for end of tunnels
		// byte directions 1 = SOUTH, 2 = NORTH, 4 = WEST, 8 = EAST

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if ((maze[j][i] & 2) == 0 && (maze[j][i] & 4) == 0 && (maze[j][i] & 8) == 0) {
					// SOUTH
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, -4, 3 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.NORTH, rand, level, layer);
				}
				if ((maze[j][i] & 1) == 0 && (maze[j][i] & 4) == 0 && (maze[j][i] & 8) == 0) {
					// NORTH
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, -4, 1 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.SOUTH, rand, level, layer);
				}
				if ((maze[j][i] & 1) == 0 && (maze[j][i] & 2) == 0 && (maze[j][i] & 4) == 0) {
					// EAST
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, -4, 2 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.WEST, rand, level, layer);

				}
				if ((maze[j][i] & 1) == 0 && (maze[j][i] & 2) == 0 && (maze[j][i] & 8) == 0) {
					// WEST
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, -4, 2 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.EAST, rand, level, layer);
				}
			}
		}
	}

	private boolean isBlackListedForGen(BlockPos pos, BlockPos posIn) {
		if(posIn.getX() == pos.getX() && posIn.getZ() == pos.getZ())
			return true;
		return false;
	}

	private boolean isBlackListedAreaForGen(BlockPos pos, BlockPos posIn, int radius) {
		for (int x = -radius; x <= radius; x++)
			for (int z = -radius; z <= radius; z++)
				if(posIn.getX() == pos.add(x, 0, z).getX() && posIn.getZ() == pos.add(x, 0, z).getZ())
					return true;
		return false;
	}

	private boolean isWithinMazeAreaForGen(BlockPos posOrigin, BlockPos posIn) {
		for (int x = 0; x <= 28; x++)
			for (int z = 0; z <= 28; z++)
				if(posIn.getX() == posOrigin.add(x, 0, z).getX() && posIn.getZ() == posOrigin.add(x, 0, z).getZ())
					return true;
		return false;
	}

	// Roots, Alcoves, Torches and upside down Stairs for the ceiling
	private void addFeature(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if ((maze[j][i] & 1) == 0) {
					if (layer == 1) {
						if (rand.nextInt(3) == 0)
							if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)))
								setRandomRoot(world, pos.add(1 + j * 4, 0, 1 + rand.nextInt(2) + i * 4), rand);
					}
					if (layer == 2) {
						if (!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 1 + i * 4))))
								world.setBlockState(pos.add(2 + j * 4, 0, 1 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_SOUTH, 2);
							else {
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(2 + j * 4, 0, i * 4), blockHelper.MUD_BRICKS_ALCOVE_SOUTH, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(1 + j * 4, 0, i * 4), blockHelper.MUD_BRICKS_ALCOVE_SOUTH, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(3 + j * 4, 0, i * 4), blockHelper.MUD_BRICKS_ALCOVE_SOUTH, rand, level);
							}
					}
					if (layer == 3) {
					}
					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j *  4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(2 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
					}
				}

				if ((maze[j][i] & 8) == 0) {
					if (layer == 1) {
					}
					if (layer == 2) {
						if (!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 2 + i * 4))))
								world.setBlockState(pos.add(1 + j * 4, 0, 2 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_EAST, 2);
							else {
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(j * 4, 0, 2 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_EAST, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(j * 4, 0, 1 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_EAST, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(j * 4, 0, 3 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_EAST, rand, level);
							}
					}
					if (layer == 3) {
					}
					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 2 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 2 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
					}
				}

				if ((maze[j][i] & 4) == 0) {
					if (layer == 1) {
					}
					if (layer == 2) {
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 2 + i * 4))))
								world.setBlockState(pos.add(3 + j * 4, 0, 2 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_WEST, 2);
							else {
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(4 + j * 4, 0, 2 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_WEST, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(4 + j * 4, 0, 1 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_WEST, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(4 + j * 4, 0, 3 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_WEST, rand, level);
							}
					}
					if (layer == 3) {
					}
					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 2 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 2 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
					}
				}

				if ((maze[j][i] & 2) == 0) {
					if (layer == 1) {
					}
					if (layer == 2) {
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 3 + i * 4))))
								world.setBlockState(pos.add(2 + j * 4, 0, 3 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_NORTH, 2);
							else {
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(2 + j * 4, 0, 4 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_NORTH, rand, level);
								if (rand.nextInt(5) == 0)
									setAlcoveForLevel(world, pos.add(1 + j * 4, 0, 4 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_NORTH, rand, level);
								if (rand.nextInt(5) == 0) {
									setAlcoveForLevel(world, pos.add(3 + j * 4, 0, 4 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_NORTH, rand, level);
								}
							}
					}
					if (layer == 3) {
					}
					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(2 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);	
					}
				}
			}
		}
	}

	// Places doors and code blocks
	private void placeRuneCombination(World world, BlockPos pos, int level, Random rand) {
		if (level <= 6) {
			if (level == 1 || level == 3 || level == 5) {
				world.setBlockState(pos.add(1, 0, 2), blockHelper.DUNGEON_DOOR_COMBINATION_EAST, 2);
				world.setBlockState(pos.add(27, 0, 26), blockHelper.DUNGEON_DOOR_WEST, 2);
				setRandomCombinations(world, pos.add(1, 0, 2), pos.add(27, 0, 26), rand);
			} else {
				world.setBlockState(pos.add(27, 0, 26), blockHelper.DUNGEON_DOOR_COMBINATION_WEST, 2);
				world.setBlockState(pos.add(1, 0, 2), blockHelper.DUNGEON_DOOR_EAST, 2);
				setRandomCombinations(world, pos.add(27, 0, 26), pos.add(1, 0, 2), rand);
			}
		}
		if (level == 7)
			world.setBlockState(pos.add(1, 0, 2), getMudBricksForLevel(rand, level, 2));
	}

	public void setRandomCombinations(World world, BlockPos codePos, BlockPos lockPos, Random rand) {
		IBlockState codeState = world.getBlockState(codePos);
		IBlockState lockState = world.getBlockState(lockPos);
		TileEntityDungeonDoorCombination tileCode = (TileEntityDungeonDoorCombination) world.getTileEntity(codePos);
		if (tileCode instanceof TileEntityDungeonDoorCombination) {
			tileCode.top_code = world.rand.nextInt(8);
			tileCode.mid_code = world.rand.nextInt(8);
			tileCode.bottom_code = world.rand.nextInt(8);
			TileEntityDungeonDoorRunes tileLock = (TileEntityDungeonDoorRunes) world.getTileEntity(lockPos);
			if (tileLock instanceof TileEntityDungeonDoorRunes) {
				tileLock.top_code = tileCode.top_code;
				tileLock.mid_code = tileCode.mid_code;
				tileLock.bottom_code = tileCode.bottom_code;
				tileLock.is_in_dungeon = true;
			}
		}
		world.notifyBlockUpdate(codePos, codeState, codeState, 3);
		world.notifyBlockUpdate(lockPos, lockState, lockState, 3);
	}

	//Air Gaps for Stairs and barrier placements
	private void stairsAir(World world, Random rand, BlockPos pos, int level) {
			if (level == 1 || level == 3 || level == 5 || level == 7) {
				world.setBlockToAir(pos.add(1, 5, 1));
				world.setBlockToAir(pos.add(1, 5, 2));
				world.setBlockToAir(pos.add(1, 5, 3));
				world.setBlockToAir(pos.add(1, 4, 2));
				world.setBlockToAir(pos.add(1, 4, 3));
				world.setBlockToAir(pos.add(1, 3, 3));
				world.setBlockToAir(pos.add(2, 4, 3));
				world.setBlockToAir(pos.add(2, 3, 3));
				world.setBlockToAir(pos.add(1, 6, 1));
				world.setBlockToAir(pos.add(1, 6, 2));
				world.setBlockToAir(pos.add(1, 6, 3));
			} else if (level == 0 || level == 2 || level == 4 || level == 6) {
				world.setBlockToAir(pos.add(27, 5, 27));
				world.setBlockToAir(pos.add(27, 5, 26));
				world.setBlockToAir(pos.add(27, 5, 25));
				world.setBlockToAir(pos.add(27, 4, 26));
				world.setBlockToAir(pos.add(27, 4, 25));
				world.setBlockToAir(pos.add(27, 3, 25));
				world.setBlockToAir(pos.add(26, 4, 25));
				world.setBlockToAir(pos.add(26, 3, 25));
				if(level != 0) {
					world.setBlockToAir(pos.add(27, 6, 27));
					world.setBlockToAir(pos.add(27, 6, 26));
					world.setBlockToAir(pos.add(27, 6, 25));
				}
			}
	}

	// Air gap for maze
	private void createAir(World world, BlockPos pos, Random rand, int w, int h, int level) {
		for (int i = 0; i < h * 4; i++)
			for (int j = 0; j < w * 4; j++)
				for (int k = 0; k <= 3; k++)
					if (!isSolidStructureBlock(world.getBlockState(pos.add(j, k, i))))
						world.setBlockToAir(pos.add(j, k, i));
	}

	// Floor
	private void buildFloor(World world, BlockPos pos, Random rand, int w, int h, boolean addFeature, boolean addSpawners, int level) {
		for (int i = 0; i <= h * 4; i++) {
			for (int j = 0; j <= w * 4; j++) {
				world.setBlockState(pos.add(j, 0, i), getTilesForLevel(rand, level), 2);
				if (rand.nextInt(15) == 0 && addFeature && !isSolidStructureBlock(world.getBlockState(pos.add(j, 1, i)))) {
					if (!isBlackListedAreaForGen(pos.add(2, 0, 2), pos.add(j, 0, i), 1) && !isBlackListedAreaForGen(pos.add(26, 0, 26), pos.add(j, 0, i), 1)) {
						if (rand.nextBoolean() && rand.nextBoolean())
							world.setBlockState(pos.add(j, 0, i), blockHelper.getMudTilesWater(rand), 2); // change this
						else if (rand.nextInt(10) == 0 && addSpawners)
							world.setBlockState(pos.add(j, 0, i), blockHelper.SPAWNER_TYPE_1, 2);
						else if (rand.nextInt(6) == 0 && addSpawners)
							world.setBlockState(pos.add(j, 0, i), blockHelper.SPAWNER_TYPE_2, 2);
						else
							if (rand.nextBoolean())
								world.setBlockState(pos.add(j, 0, i), blockHelper.PUFFSHROOM, 2);
					}
				} 

				if (world.getBlockState(pos.add(j, 0, i)).isNormalCube() && world.isAirBlock(pos.add(j, 1, i)))
					if (rand.nextInt(40) == 0)
						if (!isBlackListedAreaForGen(pos.add(2, 0, 2), pos.add(j, 0, i), 1) && !isBlackListedAreaForGen(pos.add(26, 0, 26), pos.add(j, 0, i), 1))
							world.setBlockState(pos.add(j, 1, i), getRandomMushroom(rand), 2);
			}
		}
	}

	// Roof
	private void buildRoof(World world, BlockPos pos, Random rand, int w, int h, int level) {
		for (int i = 0; i <= h * 4; i++)
			for (int j = 0; j <= w * 4; j++)
				//if (world.isAirBlock(pos.add(j, 0, i)))
					world.setBlockState(pos.add(j, 0, i), blockHelper.DRIPPING_MUD, 2);
	}

	// Places chests
	private void placeChest(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockState(pos, state, 2);
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
		if (chest != null) {
			//add loot here
		}
	}

	// Places alcoves and sets TE level (used for textures atm)
	public void setAlcoveForLevel(World world, BlockPos pos, IBlockState state, Random rand, int level) {
		world.setBlockState(pos, state, 2);
		TileEntityMudBricksAlcove alcove = (TileEntityMudBricksAlcove) world.getTileEntity(pos);
		if (alcove != null)
			alcove.setDungeonLevel(level);
	}

	public @Nullable IBlockState getMudBricksForLevel(Random rand, int level, int layer) {
		return blockHelper.getMudBricksForLevel(rand, level, layer);
	}

	public @Nullable IBlockState getPillarsForLevel(Random rand, int level, int layer) {
		return blockHelper.getPillarsForLevel(rand, level, layer);
	}

	public @Nullable IBlockState getTilesForLevel(Random rand, int level) {
		return blockHelper.getTilesForLevel(rand, level);
	}

	public @Nullable IBlockState getStairsForLevel(Random rand, int level, EnumFacing facing, EnumHalf half) {
		return blockHelper.getStairsForLevel(rand, level, facing, half);
	}

	public @Nullable IBlockState getMudSlabsForLevel(Random rand, int level, EnumBlockHalfBL half) {
		return blockHelper.getMudSlabsForLevel(rand, level, half);
	}

	public IBlockState getRandomMushroom(Random rand) {
		return blockHelper.getRandomMushroom(rand);
	}

	public IBlockState getRandomBeam(EnumFacing facing, Random rand, int level, int count, boolean randomiseLine) {
		return blockHelper.getRandomBeam(facing, rand, level, count, randomiseLine);
	}

	public IBlockState getRandomSupportBeam(EnumFacing facing, boolean isTop, Random rand) {
		return blockHelper.getRandomSupportBeam(facing, isTop, rand);
	}

	public void setRandomRoot(World world, BlockPos pos, Random rand) {
		blockHelper.setRandomRoot(world, pos, rand);
	}

	public boolean isSolidStructureBlock(IBlockState state) {
		return blockHelper.STRUCTURE_BLOCKS.get(state) != null;
	}
}