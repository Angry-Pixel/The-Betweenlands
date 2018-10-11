package thebetweenlands.common.world.gen.feature.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.container.BlockChestBetweenlands;
import thebetweenlands.common.block.misc.BlockSulfurTorch;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick.EnumCarvedMudBrickType;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockMudTiles;
import thebetweenlands.common.block.structure.BlockMudTiles.EnumMudTileType;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar.EnumWormPillarType;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.structure.utils.MazeGenerator;
import thebetweenlands.common.world.gen.feature.structure.utils.PerfectMazeGenerator;

public class WorldGenSludgeWormDungeon extends WorldGenerator {

	private IBlockState spawner = BlockRegistry.MOB_SPAWNER.getDefaultState();
	private IBlockState mushroomBlackHat = BlockRegistry.BLACK_HAT_MUSHROOM.getDefaultState();
	private IBlockState mushroomBulbCapped = BlockRegistry.BULB_CAPPED_MUSHROOM.getDefaultState();
	private IBlockState mushroomflatHead = BlockRegistry.FLAT_HEAD_MUSHROOM.getDefaultState();

	private IBlockState STAGNANT_WATER = BlockRegistry.STAGNANT_WATER.getDefaultState();
	private IBlockState TRAP_1 = Blocks.SLIME_BLOCK.getDefaultState();
	private IBlockState SPAWNER_TYPE_1 = Blocks.PRISMARINE.getDefaultState();
	private IBlockState SPAWNER_TYPE_2 = Blocks.PURPUR_BLOCK.getDefaultState();

	private IBlockState TORCH_NORTH = BlockRegistry.SULFUR_TORCH.getDefaultState().withProperty(BlockSulfurTorch.FACING, EnumFacing.NORTH);
	private IBlockState TORCH_EAST = BlockRegistry.SULFUR_TORCH.getDefaultState().withProperty(BlockSulfurTorch.FACING, EnumFacing.EAST);
	private IBlockState TORCH_SOUTH = BlockRegistry.SULFUR_TORCH.getDefaultState().withProperty(BlockSulfurTorch.FACING, EnumFacing.SOUTH);
	private IBlockState TORCH_WEST = BlockRegistry.SULFUR_TORCH.getDefaultState().withProperty(BlockSulfurTorch.FACING, EnumFacing.WEST);
	private IBlockState CHEST_NORTH = BlockRegistry.WEEDWOOD_CHEST.getDefaultState().withProperty(BlockChestBetweenlands.FACING, EnumFacing.NORTH);
	private IBlockState CHEST_EAST = BlockRegistry.WEEDWOOD_CHEST.getDefaultState().withProperty(BlockChestBetweenlands.FACING, EnumFacing.EAST);
	private IBlockState CHEST_SOUTH = BlockRegistry.WEEDWOOD_CHEST.getDefaultState().withProperty(BlockChestBetweenlands.FACING, EnumFacing.SOUTH);
	private IBlockState CHEST_WEST = BlockRegistry.WEEDWOOD_CHEST.getDefaultState().withProperty(BlockChestBetweenlands.FACING, EnumFacing.WEST);
	private IBlockState WORM_DUNGEON_PILLAR = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState();
	private IBlockState WORM_DUNGEON_PILLAR_TOP = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP);
	private IBlockState WORM_DUNGEON_PILLAR_DECAY_1 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_1);
	private IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_1 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_1);
	private IBlockState WORM_DUNGEON_PILLAR_DECAY_2 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_2);
	private IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_2 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_2);
	private IBlockState WORM_DUNGEON_PILLAR_DECAY_3 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_3);
	private IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_3 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_3);
	private IBlockState WORM_DUNGEON_PILLAR_DECAY_4 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_4);
	private IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_4 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_4);
	private IBlockState WORM_DUNGEON_PILLAR_DECAY_FULL = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_FULL);
	private IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_FULL = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_FULL);

	private IBlockState MUD_TILES = BlockRegistry.MUD_TILES.getDefaultState();
	private IBlockState MUD_TILES_DECAY = BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_DECAY);
	private IBlockState MUD_TILES_CRACKED = BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED);
	private IBlockState MUD_TILES_CRACKED_DECAY = BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED_DECAY);

	private IBlockState MUD_BRICK_STAIRS = BlockRegistry.MUD_BRICK_STAIRS.getDefaultState();
	private IBlockState MUD_BRICK_STAIRS_DECAY_1 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_1.getDefaultState();
	private IBlockState MUD_BRICK_STAIRS_DECAY_2 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_2.getDefaultState();
	private IBlockState MUD_BRICK_STAIRS_DECAY_3 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_3.getDefaultState();

	private IBlockState MUD_BRICK_SLAB = BlockRegistry.MUD_BRICK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	private IBlockState MUD_BRICK_SLAB_DECAY_1 = BlockRegistry.MUD_BRICK_SLAB_DECAY_1.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	private IBlockState MUD_BRICK_SLAB_DECAY_2 = BlockRegistry.MUD_BRICK_SLAB_DECAY_1.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	private IBlockState MUD_BRICK_SLAB_DECAY_3 = BlockRegistry.MUD_BRICK_SLAB_DECAY_1.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);

	private IBlockState MUD_BRICKS = BlockRegistry.MUD_BRICKS.getDefaultState();
	private IBlockState MUD_BRICKS_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_1);
	private IBlockState MUD_BRICKS_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_2);
	private IBlockState MUD_BRICKS_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_3);
	private IBlockState MUD_BRICKS_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_4);
	private IBlockState MUD_BRICKS_CARVED = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED);
	private IBlockState MUD_BRICKS_CARVED_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_1);
	private IBlockState MUD_BRICKS_CARVED_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_2);
	private IBlockState MUD_BRICKS_CARVED_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_3);
	private IBlockState MUD_BRICKS_CARVED_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_4);
	private IBlockState MUD_BRICKS_CARVED_EDGE = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE);
	private IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_1);
	private IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_2);
	private IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_3);
	private IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_4);

	private IBlockState MUD = BlockRegistry.MUD.getDefaultState();
	private IBlockState DRIPPING_MUD = BlockRegistry.DRIPPING_MUD.getDefaultState();
	private IBlockState PUFFSHROOM = BlockRegistry.PUFFSHROOM.getDefaultState();
	private IBlockState ROTTEN_BARK = BlockRegistry.LOG_ROTTEN_BARK.getDefaultState().withProperty(BlockLogBetweenlands.LOG_AXIS, EnumAxis.NONE);

	private IBlockState ROOT = BlockRegistry.ROOT.getDefaultState();

	
	private final Map<IBlockState, Boolean> STRUCTURE_BLOCKS = new HashMap<IBlockState, Boolean>();

	public WorldGenSludgeWormDungeon() {
		super(true);
		initStuctureBlockMap();
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		//conditions blah, blah...
		//makeTreeStructure(world, rand, pos);
		//makeBarrow(world, rand, pos);
		makeMaze(world, rand, pos);
		return true;
	}

	public void makeTreeStructure(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		generateMainDome(world, pos.up());
		//WorldGenStoneTree tree = new WorldGenStoneTree();
		//tree.generateTree(world, rand, pos.add(0,16,0));
		
	}

	private void generateMainDome(World world, BlockPos pos) {
		for (int xx = - 16; xx <= 16; xx++) {
			for (int zz = - 16; zz <= 16; zz++) {
				for (int yy = 0; yy > -16; yy--) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);
					if (Math.round(Math.sqrt(dSqDome)) < 17)
						if (dSqDome >= Math.pow(15, 2.0D))
							world.setBlockState(pos.add(xx, yy, zz), MUD_TILES_DECAY, 2);
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

	public void makeBarrow(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
	}
	
	public void makeMaze(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		for (int level = 0; level <= 7; level++) {
			int yy = -6 -(level * 6);
			if (level == 7) {
				for(int xx = 0; xx <= 28; xx++) {
					for(int zz = 0; zz <= 28; zz++) {
						for(int yUp = yy - 2; yUp < yy + 6; yUp++) {
							world.setBlockState(pos.add(xx, yUp, 0), getMudBricksForLevel(rand, level, yUp));
							world.setBlockState(pos.add(xx, yUp, 28), getMudBricksForLevel(rand, level, yUp));
							world.setBlockState(pos.add(0, yUp, zz), getMudBricksForLevel(rand, level, yUp));
							world.setBlockState(pos.add(28, yUp, zz), getMudBricksForLevel(rand, level, yUp));
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
				if (level == 1 || level == 3 || level == 5 || level == 7) {
					world.setBlockState(pos.add(1, yy + 1, 1), getMudBricksForLevel(rand, level, 1));
					world.setBlockState(pos.add(1, yy + 2, 1), getMudBricksForLevel(rand, level, 2));
					world.setBlockState(pos.add(1, yy + 3, 1), getMudBricksForLevel(rand, level, 3));
					world.setBlockState(pos.add(1, yy + 4, 1), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.BOTTOM), 2);
					if (world.isAirBlock(pos.add(1, yy + 1, 2)))
						world.setBlockState(pos.add(1, yy + 1, 2), getMudBricksForLevel(rand, level, 1));
					world.setBlockState(pos.add(1, yy + 2, 2), getMudBricksForLevel(rand, level, 2));
					world.setBlockState(pos.add(1, yy + 3, 2), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(1, yy + 2, 3), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(1, yy + 1, 3), getMudBricksForLevel(rand, level, 1));
					world.setBlockState(pos.add(2, yy + 1, 3), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.BOTTOM), 2);
				} else {
					world.setBlockState(pos.add(27, yy + 1, 27), getMudBricksForLevel(rand, level, 1));
					world.setBlockState(pos.add(27, yy + 2, 27), getMudBricksForLevel(rand, level, 2));
					world.setBlockState(pos.add(27, yy + 3, 27), getMudBricksForLevel(rand, level, 3));
					world.setBlockState(pos.add(27, yy + 4, 27), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.BOTTOM), 2);
					if (world.isAirBlock(pos.add(27, yy + 1, 26)))
						world.setBlockState(pos.add(27, yy + 1, 26), getMudBricksForLevel(rand, level, 1));
					world.setBlockState(pos.add(27, yy + 2, 26), getMudBricksForLevel(rand, level, 2));
					world.setBlockState(pos.add(27, yy + 3, 26), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(27, yy + 2, 25), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.BOTTOM), 2);
					world.setBlockState(pos.add(27, yy + 1, 25), getMudBricksForLevel(rand, level, 1));
					world.setBlockState(pos.add(26, yy + 1, 25), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.BOTTOM), 2);
				}
				if (level != 7)
					buildBeams(world, pos.add(0, yy + 4, 0), 7, 7, rand, level);
				stairsAir(world, rand, pos.add(0, yy, 0), level);
			}
			System.out.println("Y height is: " + (y + yy) + " level: " + level);
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
					buildFloor(world, pos, rand, mazeWidth, mazeHeight, true, false, level);
					buildRoof(world, pos.up(layer), rand, mazeWidth, mazeHeight, level);
					break;
			}
	//	System.out.println("Generated Maze At: X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
	}
	
	// Levels
	private void buildLevel(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		for (int i = 0; i < h; i++) {
			// draw the north edge
			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 1) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer), 2);
					world.setBlockState(pos.add(j * 4 + 1, 0, i * 4), getMudBricksForLevel(rand, level, layer));
					world.setBlockState(pos.add(j * 4 + 2, 0, i * 4), getMudBricksForLevel(rand, level, layer));
					world.setBlockState(pos.add(j * 4 + 3, 0, i * 4), getMudBricksForLevel(rand, level, layer));
				} else
					world.setBlockState(pos.add(j * 4, 0, i * 4), getMudBricksForLevel(rand, level, layer));
			// draw the west edge
			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 8) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
					world.setBlockState(pos.add(j * 4, 0, i * 4 + 1), getMudBricksForLevel(rand, level, layer));
					world.setBlockState(pos.add(j * 4, 0, i * 4 + 2), getMudBricksForLevel(rand, level, layer));
					world.setBlockState(pos.add(j * 4, 0, i * 4 + 3), getMudBricksForLevel(rand, level, layer));
				}
			
			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 4) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
				}
			
			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 2) == 0) {
					world.setBlockState(pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
				}

			world.setBlockState(pos.add(w * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
			world.setBlockState(pos.add(w * 4, 0, i * 4 + 1), getMudBricksForLevel(rand, level, layer));
			world.setBlockState(pos.add(w * 4, 0, i * 4 + 2), getMudBricksForLevel(rand, level, layer));
			world.setBlockState(pos.add(w * 4, 0, i * 4 + 3), getMudBricksForLevel(rand, level, layer));
		}
		// draw the bottom line
		for (int j = 0; j <= w * 4; j++)
			world.setBlockState(pos.add(j, 0, h * 4), (j%4 == 0 && (layer == 1 || layer == 2 || layer == 3)) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
	}

	// Level Feature Placements
	private void addFeature(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 1) == 0) {

					if(layer == 1) {
						if (rand.nextInt(100) == 0)
							placeChest(world, pos.add(1 + j * 4, 0, 1 + i * 4), CHEST_SOUTH, rand);
						else if (rand.nextInt(3) == 0)
							setRandomRoot(world, pos.add(1 + j * 4, 0, 1 + rand.nextInt(2) + i * 4), rand);
					}

					if(layer == 2) {
						if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 1 + i * 4), TORCH_SOUTH, 2);
						else if (rand.nextInt(10) == 0) {
								int randOffset = rand.nextInt(2);
								world.setBlockState(pos.add(1 + j * 4, - randOffset, i * 4), TRAP_1, 2);
							}
						else if (rand.nextInt(10) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 2 + i * 4)))) {
							if (rand.nextBoolean()) {
								world.setBlockState(pos.add(2 + j * 4, 0, 2 + i * 4), spawner);
								BlockMobSpawnerBetweenlands.setMob(world, pos.add(2 + j * 4, 0, 2 + i * 4), "thebetweenlands:chiromaw", logic -> logic.setSpawnRange(2));
							}
							else {
								world.setBlockState(pos.add(2 + j * 4, 0, 2 + i * 4), spawner);
								BlockMobSpawnerBetweenlands.setMob(world, pos.add(2 + j * 4, 0, 2 + i * 4), "thebetweenlands:termite");
							}
						}
					}

					if(layer == 3) {
					}

					if(layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(1 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(2 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(3 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(4 + j * 4, 0, 1 + i * 4))))
							world.setBlockState(pos.add(4 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP), 2);
					}
				}

			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 8) == 0) {

					if(layer == 1) {
						if (rand.nextInt(100) == 0)
							placeChest(world, pos.add(1 + j * 4, 0, 2 + i * 4), CHEST_EAST, rand);
					}

					if(layer == 2) {
						if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 2 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 2 + i * 4), TORCH_EAST, 2);
						else if (rand.nextInt(10) == 0) {
							int randOffset = rand.nextInt(2);
							world.setBlockState(pos.add(j * 4, - randOffset, 2 + i * 4), TRAP_1, 2);
						}
					}

					if(layer == 3) {
					}

					if(layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(1 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(1 + j * 4, 0, 2 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(1 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 4 + i * 4))))
							world.setBlockState(pos.add(1 + j * 4, 0, 4 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP), 2);
					}
				}

			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 4) == 0) {

					if(layer == 1) {
						if (rand.nextInt(100) == 0)
							placeChest(world, pos.add(3 + j * 4, 0, 2 + i * 4), CHEST_WEST, rand);
					}

					if(layer == 2) {
						if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 2 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 2 + i * 4), TORCH_WEST, 2);
						else if (rand.nextInt(10) == 0) {
							int randOffset = rand.nextInt(2);
							world.setBlockState(pos.add(4 + j * 4, - randOffset, 2 + i * 4), TRAP_1, 2);
						}
					}

					if(layer == 3) {
					}

					if(layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(3 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(3 + j * 4, 0, 2 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(3 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 4 + i * 4))))
							world.setBlockState(pos.add(3 + j * 4, 0, 4 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP), 2);
					}
				}

			for (int j = 0; j < w; j++)
				if ((maze[j][i] & 2) == 0) {

					if(layer == 1) {
						if (rand.nextInt(100) == 0)
							placeChest(world, pos.add(2 + j * 4, 0, 3 + i * 4), CHEST_NORTH, rand);
					}

					if(layer == 2) {
						if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(2 + j * 4, 0, 3 + i * 4), TORCH_NORTH, 2);
						else if (rand.nextInt(10) == 0) {
							int randOffset = rand.nextInt(2);
							world.setBlockState(pos.add(1 + j * 4, - randOffset, 4 + i * 4), TRAP_1, 2);
						}
					}

					if(layer == 3) {
					}

					if(layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(1 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(2 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
						world.setBlockState(pos.add(3 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
						if (!isSolidStructureBlock(world.getBlockState(pos.add(4 + j * 4, 0, 3 + i * 4))))
							world.setBlockState(pos.add(4 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP), 2);
					}
				}
		}
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
				world.setBlockState(pos.add(1, 6, 1), Blocks.WEB.getDefaultState());
				world.setBlockState(pos.add(1, 6, 2), Blocks.WEB.getDefaultState()); //lock
				world.setBlockState(pos.add(1, 6, 3), Blocks.WEB.getDefaultState());
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
				world.setBlockState(pos.add(27, 6, 27), Blocks.WEB.getDefaultState());
				world.setBlockState(pos.add(27, 6, 26), Blocks.WEB.getDefaultState()); //lock
				world.setBlockState(pos.add(27, 6, 25), Blocks.WEB.getDefaultState());
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
				if (rand.nextInt(15) == 0 && addFeature && !isSolidStructureBlock(world.getBlockState(pos.add(j, 1, i)))) {
					if (rand.nextBoolean() && rand.nextBoolean())
						world.setBlockState(pos.add(j, 0, i), STAGNANT_WATER);
					else if (rand.nextInt(10) == 0 && addSpawners)
						world.setBlockState(pos.add(j, 0, i), SPAWNER_TYPE_1, 2);
					else if (rand.nextInt(6) == 0 && addSpawners)
							world.setBlockState(pos.add(j, 0, i), SPAWNER_TYPE_2, 2);
					else
						world.setBlockState(pos.add(j, 0, i), PUFFSHROOM, 2);
				}
				else 
					world.setBlockState(pos.add(j, 0, i), getTilesForLevel(rand, level), 2);
				
				if(world.getBlockState(pos.add(j, 0, i)).isNormalCube() && world.isAirBlock(pos.add(j, 1, i)))
					if(rand.nextInt(40) == 0)
						world.setBlockState(pos.add(j, 1, i), getRandomMushroom(rand), 2);
					}
				}
	}
	
	// Roof
	private void buildRoof(World world, BlockPos pos, Random rand, int w, int h, int level) {
		for (int i = 0; i <= h * 4; i++)
			for (int j = 0; j <= w * 4; j++)
				//if (world.isAirBlock(pos.add(j, 0, i)))
					world.setBlockState(pos.add(j, 0, i), DRIPPING_MUD, 2);
	}
	
	// Wooden Beams
	private void buildBeams(World world, BlockPos pos, int w, int h, Random rand, int level) {
		for (int i = 0; i <= h * 4; i++)
			for (int j = 0; j <= w * 4; j++)
				if((j%4 == 0 || i%4 == 0) && world.isAirBlock(pos.add(j, -1, i))) {
					if(level == 0)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					if(level == 1)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					if(level == 2)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					if(level == 3)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					if(level == 4)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					if(level == 5)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					if(level == 6)
						world.setBlockState(pos.add(j, 0, i), ROTTEN_BARK, 2);
					
				}
				else if(world.isAirBlock(pos.add(j, 0, i)) && !isSolidStructureBlock(world.getBlockState(pos.add(j, 0, i)))) {
					if(level == 0)
						world.setBlockState(pos.add(j, 0, i), MUD_BRICK_SLAB, 2);
					if(level != 0 && rand.nextInt(level) == 0) {
						if(level == 1)
							world.setBlockState(pos.add(j, 0, i), rand.nextBoolean() ? MUD_BRICK_SLAB : MUD_BRICK_SLAB_DECAY_1, 2);
						if(level == 2)
							world.setBlockState(pos.add(j, 0, i), MUD_BRICK_SLAB_DECAY_1, 2);
						if(level == 3)
							world.setBlockState(pos.add(j, 0, i), rand.nextBoolean() ? MUD_BRICK_SLAB_DECAY_1 : MUD_BRICK_SLAB_DECAY_2, 2);
						if(level == 4)
							world.setBlockState(pos.add(j, 0, i), MUD_BRICK_SLAB_DECAY_2, 2);
						if(level == 5)
							world.setBlockState(pos.add(j, 0, i), rand.nextBoolean() ? MUD_BRICK_SLAB_DECAY_2 : MUD_BRICK_SLAB_DECAY_3, 2);
						if(level == 6)
							world.setBlockState(pos.add(j, 0, i), MUD_BRICK_SLAB_DECAY_3, 2);
					}
		}
	}

	// Block Selection and conditions
	private void placeChest(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockState(pos, state, 2);
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
		if (chest != null) {
			//add loot here
		}
	}

	public @Nullable IBlockState getMudBricksForLevel(Random rand, int level, int layer) {
		int type = rand.nextInt(3);
		switch (level) {
		case 0:
			if(layer == 1)
				return MUD_BRICKS;
			if(layer == 2)
				return MUD_BRICKS_CARVED;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE;
		case 1:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS : MUD_BRICKS_DECAY_1;
			if(layer == 2)
				return MUD_BRICKS_CARVED;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE : MUD_BRICKS_CARVED_EDGE_DECAY_1;
		case 2:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS_DECAY_1 : MUD_BRICKS_DECAY_2;
			if(layer == 2)
				return MUD_BRICKS_CARVED;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE_DECAY_1 : MUD_BRICKS_CARVED_EDGE_DECAY_2;
		case 3:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS_DECAY_2 : MUD_BRICKS_DECAY_3;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED : MUD_BRICKS_CARVED_DECAY_1;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE_DECAY_2 : MUD_BRICKS_CARVED_EDGE_DECAY_3;
		case 4:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS_DECAY_3 : MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_1 : MUD_BRICKS_CARVED_DECAY_2;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE_DECAY_3 :MUD_BRICKS_CARVED_EDGE_DECAY_4;
		case 5:
			if(layer == 1)
				return MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_1 : rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_2 : MUD_BRICKS_CARVED_DECAY_3;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE_DECAY_4;
		case 6:
			if(layer == 1)
				return MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_2 : rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_3 : MUD_BRICKS_CARVED_DECAY_4;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE_DECAY_4;
		case 7:
			return MUD_BRICKS;
		}
		return MUD_BRICKS;
	}

	public @Nullable IBlockState getPillarsForLevel(Random rand, int level, int layer) {
		int type = rand.nextInt(3);
		switch (level) {
		case 0:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP;
		case 1:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_1;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_1;
		case 2:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_2;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_2;
		case 3:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_3;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_1;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_3;
		case 4:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_4;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_2;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_4;
		case 5:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_FULL;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_3;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_4;
		case 6:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_FULL;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_4;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_FULL;
		case 7:
			return null;
		}
		return WORM_DUNGEON_PILLAR;
	}
	
	public @Nullable IBlockState getTilesForLevel(Random rand, int level) {
		int type = rand.nextInt(8);
		switch (level) {
		case 0:
			if(type == 0)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES;
		case 1:
			if(type == 0 || type == 1)
				return MUD_TILES_CRACKED;
			if(type == 2)
				return MUD_TILES_DECAY;
			else
				return MUD_TILES;
		case 2:
			if(type == 0 || type == 1)
				return MUD_TILES_DECAY;
			if(type == 2)
				return MUD_TILES_CRACKED_DECAY;
			else
				return MUD_TILES;
		case 3:
			if(type == 0 || type == 1)
				return MUD_TILES_DECAY;
			if(type == 2 || type == 3)
				return MUD_TILES_CRACKED_DECAY;
			if(type == 4)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES;
		case 4:
			if(type == 0 || type == 1 || type == 2)
				return MUD_TILES_DECAY;
			if(type == 3 || type == 4)
				return MUD_TILES_CRACKED;
			if(type == 5)
				return MUD_TILES_CRACKED_DECAY;
			else
				return MUD_TILES;
		case 5:
			if(type == 0 || type == 1 || type == 2)
				return MUD_TILES_DECAY;
			if(type == 3 || type == 4)
				return MUD_TILES_CRACKED_DECAY;
			if(type == 5)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES;
		case 6:
			if(type == 0 || type == 1)
				return MUD_TILES_CRACKED_DECAY;
			if(type == 3)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES_DECAY;
		case 7:
			return MUD_BRICKS;
		}
		return MUD_BRICKS;//betweenstoneBricks;
	}
	
	public @Nullable IBlockState getStairsForLevel(Random rand, int level, EnumFacing facing, EnumHalf half) {
		IBlockState state = MUD_BRICK_STAIRS;
		int type = rand.nextInt(3);
		switch (level) {
		case 0:
			state = MUD_BRICK_STAIRS;
			break;
		case 1:
			if(type == 0 || type == 1)
				state = MUD_BRICK_STAIRS;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_1;
			break;
		case 2:
			if(type == 0)
				state = MUD_BRICK_STAIRS;
			if(type == 1)
				state = MUD_BRICK_STAIRS_DECAY_1;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_2;
			break;
		case 3:
			if(type == 0 || type == 1)
				state = MUD_BRICK_STAIRS_DECAY_1;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_2;
			break;
		case 4:
			if(type == 0)
				state = MUD_BRICK_STAIRS_DECAY_1;
			if(type == 1)
				state = MUD_BRICK_STAIRS_DECAY_2;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		case 5:
			if(type == 0 || type == 1)
				state = MUD_BRICK_STAIRS_DECAY_2;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		case 6:
			state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		case 7:
			state = MUD_BRICK_STAIRS;
			break;
		}
		return state.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, half);
	}

	public IBlockState getRandomMushroom(Random rand) {
		int rnd = rand.nextInt(30);
		if(rnd < 14) {
			return mushroomflatHead;
		} else if(rnd < 28) {
			return mushroomBlackHat;
		} else {
			return mushroomBulbCapped;
		}
	}

	public void setRandomRoot(World world, BlockPos pos, Random rand) {
		int rnd = rand.nextInt(30);
		if(rnd < 10) {
			world.setBlockState(pos, ROOT);
		} else if(rnd < 20) {
			world.setBlockState(pos, ROOT);
			world.setBlockState(pos.up(1), ROOT);
		} else if(rnd < 25){
			world.setBlockState(pos, ROOT);
			world.setBlockState(pos.up(1), ROOT);
			world.setBlockState(pos.up(2), ROOT);																				
		} else {
			world.setBlockState(pos, ROOT);
			world.setBlockState(pos.up(1), ROOT);
			world.setBlockState(pos.up(2), ROOT);
			world.setBlockState(pos.up(3), ROOT);
		}
	}

	public boolean isSolidStructureBlock(IBlockState state) {
		return STRUCTURE_BLOCKS.get(state) != null;
	}

	private void initStuctureBlockMap() {
		if (STRUCTURE_BLOCKS.isEmpty()) {
			STRUCTURE_BLOCKS.put(DRIPPING_MUD, true);
			STRUCTURE_BLOCKS.put(ROTTEN_BARK, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_4, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_4, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_4, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS_DECAY_3, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_1, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_2, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_3, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_4, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_FULL, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_1, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_2, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_3, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_4, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_FULL, true);
			STRUCTURE_BLOCKS.put(MUD_TILES, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_DECAY, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_CRACKED, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_CRACKED_DECAY, true);
			STRUCTURE_BLOCKS.put(ROOT, true);
		}
	}
}
