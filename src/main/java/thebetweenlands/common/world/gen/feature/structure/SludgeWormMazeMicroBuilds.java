package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockChestBetweenlands;
import thebetweenlands.common.block.container.BlockItemShelf;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.block.structure.BlockMudBrickSpikeTrap;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class SludgeWormMazeMicroBuilds {

	private SludgeWormMazeBlockHelper blockHelper;
	private final WorldGenSludgeWormDungeon dungeon;
	
	public SludgeWormMazeMicroBuilds(WorldGenSludgeWormDungeon dungeon) {
		this.dungeon = dungeon;
		this.blockHelper = new SludgeWormMazeBlockHelper(dungeon);
	}
	
	public void selectFeature(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		// add random selection for builds here
		
		if(rand.nextInt(20) < 5) {
			//Higher chance for chest room
			buildWallPillarCentral(world, pos, facing, rand, level, layer);
		} else {
			int type = rand.nextInt(9);
			switch (type) {
			case 0:
				buildCryptBench1(world, pos, facing, rand, level, layer);
				break;
			case 1:
				buildWallPillarRoom(world, pos, facing, rand, level, layer);
				break;
			case 2:
				buildBigPillarCandle(world, pos, facing, rand, level, layer);
				break;
			case 3:
				buildWallPillarShelfRoom(world, pos, facing, rand, level, layer);
				break;
			case 4:
				buildBigPillarTableRoom(world, pos, facing, rand, level, layer);
				break;
			case 5:
				buildWallPillarRoomLowJamb(world, pos, facing, rand, level, layer);
				break;
			case 6:
				buildOpenShrineRoom(world, pos, facing, rand, level, layer);
				break;
			case 7:
				buildMudWallJamb(world, pos, facing, rand, level, layer);
				break;
			case 8:
				buildCubbyHole(world, pos, facing, rand, level, layer);
				break;
			}
		}
	}

	public void spikeFeature(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 0, -1, 0, blockHelper.MUD_TILES_SPIKE_TRAP, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -1, 1, blockHelper.MUD_TILES_SPIKE_TRAP, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -1, -1, blockHelper.MUD_TILES_SPIKE_TRAP, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, -1, 1, blockHelper.MUD_TILES_SPIKE_TRAP, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, -1, -1, blockHelper.MUD_TILES_SPIKE_TRAP, 1, 1, 1, facing);
		
		rotatedCubeVolume(world, rand, pos, 2, 1, 0, blockHelper.MUD_BRICKS_SPIKE_TRAP.withProperty(BlockMudBrickSpikeTrap.FACING, facing.rotateY()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 1, 0, blockHelper.MUD_BRICKS_SPIKE_TRAP.withProperty(BlockMudBrickSpikeTrap.FACING, facing.getOpposite().rotateY()), 1, 1, 1, facing);
	}

	private void buildCryptBench1(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, -1, blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, -1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite().rotateY(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, -1, blockHelper.getRandomLitCandle(rand), 1, 1, 1, facing);
	}

	private void buildWallPillarRoom(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, 0, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
	}

	private void buildBigPillarCandle(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 0, 0, 0, blockHelper.MUD_FLOWER_POT_CANDLE_UNLIT, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0, 1, blockHelper.getPillarsForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, 1, blockHelper.getPillarsForLevel(rand, level, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 1, blockHelper.getPillarsForLevel(rand, level, 3), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, 1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
	}

	private void buildWallPillarShelfRoom(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.ITEM_SHELF.withProperty(BlockItemShelf.FACING, facing.getOpposite()), 3, 2, 1, facing);
	}

	private void buildWallPillarCentral(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 0, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0, 0, blockHelper.CHEST.withProperty(BlockChestBetweenlands.FACING, facing.getOpposite()), 1, 1, 1, facing);
	}

	private void buildBigPillarTableRoom(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, blockHelper.getPillarsForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 1, blockHelper.getPillarsForLevel(rand, level, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 1, blockHelper.getPillarsForLevel(rand, level, 3), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 1, blockHelper.getPillarsForLevel(rand, level, 3), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, 1, blockHelper.getPillarsForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, 1, blockHelper.getPillarsForLevel(rand, level, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, 1, blockHelper.getPillarsForLevel(rand, level, 3), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, 0, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 0, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, 0, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, -1, blockHelper.getRandomLitCandle(rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, 0, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 0, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
	}

	private void buildWallPillarRoomLowJamb(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, 1, blockHelper.getMudBricksForLevel(rand, level, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 1, blockHelper.getMudBricksForLevel(rand, level, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 1, blockHelper.getMudBricksForLevel(rand, level, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite().rotateY(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, -1, blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, -1, blockHelper.getStairsForLevel(rand, level, facing.getOpposite().rotateY(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
	}

	private void buildOpenShrineRoom(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -1, 0, 1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, 0, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 0, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, 0, blockHelper.getRandomLitCandle(rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 0, blockHelper.getRandomLitCandle(rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, -1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
	}

	private void buildMudWallJamb(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 1, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, 0, blockHelper.getStairsForLevel(rand, level, facing, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 0, blockHelper.getStairsForLevel(rand, level, facing, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.getRandomLitCandle(rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
	}

	private void buildCubbyHole(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 0, 0, -1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, -1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, -1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, -1, blockHelper.getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, -1, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 2, 0, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 0, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 0, blockHelper.getStairsForLevel(rand, level, facing.getOpposite(), EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
	}

	public void addBarrisheeCubby(World world, BlockPos pos, EnumFacing facing, Random rand, int level) {
		for (int x = 0; x < 9; x++) {
			for (int z = 0; z < 3; z++) {
				for (int y = 1; y <= 3; y++) {
					if (x > 0 && x < 8 && z > 0 && z < 3) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.add(x, y, z), Blocks.AIR.getDefaultState());
					}
				}
				this.dungeon.setBlockAndNotifyAdequately(world, pos.add(x, 0, z), blockHelper.getTilesForLevel(rand, 3));
				this.dungeon.setBlockAndNotifyAdequately(world, pos.add(x, 4, z), blockHelper.getMudBricksForLevel(rand, 3, 1));
				if (x == 0 || x == 8) {
					if (z > 0) {
						for (int y = 1; y <= 3; y++)
							this.dungeon.setBlockAndNotifyAdequately(world, pos.add(x, y, z), blockHelper.getMudBricksForLevel(rand, 3, y));
					}
				}
				if (z == 0) {
					for (int y = 1; y <= 3; y++)
						this.dungeon.setBlockAndNotifyAdequately(world, pos.add(x, y, 0), blockHelper.getMudBricksForLevel(rand, 3, y));
				}
			}
		}
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(7, 0, 1), blockHelper.MUD_TILES_WATER);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(3, 0, 2), blockHelper.MUD_TILES_WATER);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(3, 0, 1), blockHelper.COMPACTED_MUD);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 0, 1), blockHelper.COMPACTED_MUD);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 0, 2), blockHelper.COMPACTED_MUD);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(3, 1, 1), blockHelper.TALL_SLUDGECREEP);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 1, 1), blockHelper.getRandomMushroom(rand));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 1, 2), blockHelper.TALL_SLUDGECREEP);
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(5, 1, 1), blockHelper.getRandomLootUrn(rand, facing.getOpposite()));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(1, 1, 1), blockHelper.CHEST.withProperty(BlockChestBetweenlands.FACING, facing.rotateYCCW()));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(1, 1, 2), blockHelper.CHEST.withProperty(BlockChestBetweenlands.FACING, facing.rotateYCCW()));
		
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(1, 3, 1), blockHelper.getMudBricksForLevel(rand, level, 0));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(1, 3, 2), blockHelper.getMudBricksForLevel(rand, level, 0));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(1, 2, 1), blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(1, 2, 2), blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 3, 1), blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP));
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 3, 2), blockHelper.getStairsForLevel(rand, level, facing.rotateY(), EnumHalf.TOP));
		
		this.dungeon.setBlockAndNotifyAdequately(world, pos.add(2, 1, 2), blockHelper.GROUND_ITEM);
		this.blockHelper.setBattleAxe(world, rand, pos.add(2, 1, 2));
	}

	public void buildCryptCrawlerWalkways(World world, BlockPos pos, EnumFacing facing, Random rand) {
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(20, -10, 1), facing, rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(8, -10, 2), facing.getOpposite(), rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(25, -10, 2), facing.getOpposite(), rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(28, -16, 1), facing, rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(8, -16, 2), facing, rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(1, -10, 10), facing.rotateYCCW(), rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(2, -10, 22), facing.getOpposite().rotateYCCW(), rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(1, -10, 25), facing.rotateYCCW(), rand);
		buildCryptCrawlerWalkwaysStairsNormal(world, pos.add(2, -16, 2), facing.rotateYCCW(), rand);

		rotatedCubeVolume(world, rand, pos, 1, -18, 27, blockHelper.AIR, 1, 2, 4, facing);

		//South walls
		rotatedCubeVolume(world, rand, pos, 14, -4, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 31, -4, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 6, -10, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 22, -10, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 31, -10, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 3, -16, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 31, -16, 1, blockHelper.COMPACTED_MUD, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, 0, -18, 0, blockHelper.COMPACTED_MUD, 32, 19, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, -18, 1, blockHelper.COMPACTED_MUD, 1, 19, 31, facing);

		//East walls
		rotatedCubeVolume(world, rand, pos, 1, -4, 16, blockHelper.COMPACTED_MUD, 2, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -4, 31, blockHelper.COMPACTED_MUD, 2, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -10, 8, blockHelper.COMPACTED_MUD, 2, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -10, 24, blockHelper.COMPACTED_MUD, 2, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -10, 31, blockHelper.COMPACTED_MUD, 2, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -16, 31, blockHelper.COMPACTED_MUD, 2, 4, 1, facing);

		//extra roots
		rotatedCubeVolume(world, rand, pos, 1, -4, 1, blockHelper.ROOT, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, -8, 1, blockHelper.ROOT, 1, 2, 2, facing);
		rotatedCubeVolume(world, rand, pos, 22, -14, 1, blockHelper.ROOT, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 21, -14, 2, blockHelper.ROOT, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 19, -16, 2, blockHelper.ROOT, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -8, 16, blockHelper.ROOT, 2, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -16, 10, blockHelper.ROOT, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, -14, 9, blockHelper.ROOT, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, -14, 8, blockHelper.ROOT, 1, 2, 1, facing);
	}

	public void buildCryptCrawlerWalkwaysStairsNormal(World world, BlockPos pos, EnumFacing facing, Random rand) {
		rotatedCubeVolume(world, rand, pos, 0, 0, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateY()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateYCCW()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateY()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 1, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateYCCW()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 2, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateY()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 2, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateYCCW()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 3, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateY()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 3, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateYCCW()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 4, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateY()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 5, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateY()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);

		// air gaps
		rotatedCubeVolume(world, rand, pos, -4, 5, 0, blockHelper.AIR, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 4, 0, blockHelper.AIR, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 3, 0, blockHelper.AIR, 3, 1, 1, facing);

		// roots
		rotatedCubeVolume(world, rand, pos, -2, 0, 0, blockHelper.ROOT, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 0, 0, blockHelper.ROOT, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0, 0, blockHelper.ROOT, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 0, 0, blockHelper.ROOT, 1, 4, 1, facing);
	}

	public void buildCryptCrawlerBottomTunnels(World world, BlockPos pos, EnumFacing facing, Random rand) {
		rotatedCubeVolume(world, rand, pos, 6, 1, 1, blockHelper.AIR, 20, 3, 3, facing);
		rotatedCubeVolume(world, rand, pos, 5, 2, 2, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1, 3, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 26, 2, 2, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 25, 1, 3, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 6, 0, 1, blockHelper.COMPACTED_MUD, 20, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1, 4, blockHelper.COMPACTED_MUD, 20, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1, 0, blockHelper.COMPACTED_MUD, 20, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 4, 1, blockHelper.COMPACTED_MUD, 20, 1, 3, facing); //roof

		rotatedCubeVolume(world, rand, pos, 6, 1, 1, blockHelper.COMPACTED_MUD, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1, 2, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 22, 1, 1, blockHelper.COMPACTED_MUD, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 24, 1, 2, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);
		if (facing != EnumFacing.EAST) {
			rotatedCubeVolume(world, rand, pos, 13, 1, 3, blockHelper.COMPACTED_MUD, 6, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 14, 2, 3, blockHelper.COMPACTED_MUD, 4, 1, 1, facing);
		}
	}

	public void buildCryptCrawlerTunnelsConnect(World world, BlockPos pos, EnumFacing facing, Random rand) {
		rotatedCubeVolume(world, rand, pos, 1, 4, 9, blockHelper.AIR, 3, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 0, 8, 31, blockHelper.COMPACTED_MUD, 8, 7, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 8, 28, blockHelper.COMPACTED_MUD, 1, 7, 3, facing);
		rotatedCubeVolume(world, rand, pos, 4, 9, 28, blockHelper.COMPACTED_MUD, 3, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 8, 27, blockHelper.COMPACTED_MUD, 7, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 6, 9, 27, blockHelper.COMPACTED_MUD, 1, 5, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 9, 26, blockHelper.COMPACTED_MUD, 1, 6, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 8, 25, blockHelper.COMPACTED_MUD, 1, 5, 1, facing);

		//stairs
		rotatedCubeVolume(world, rand, pos, 1, 9, 30, blockHelper.COMPACTED_MUD, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 10, 30, blockHelper.COMPACTED_MUD, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 11, 30, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 12, 29, blockHelper.COMPACTED_MUD, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 13, 26, blockHelper.COMPACTED_MUD, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 1, 14, 26, blockHelper.COMPACTED_MUD, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 15, 27, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 1, 12, 23, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 11, 21, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 10, 19, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 9, 17, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 8, 15, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 7, 13, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 6, 11, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 5, 9, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 4, 7, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 0, 8, 25, blockHelper.COMPACTED_MUD, 1, 7, 6, facing);
		rotatedCubeVolume(world, rand, pos, 1, 7, 25, blockHelper.COMPACTED_MUD, 1, 8, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 14, 25, blockHelper.COMPACTED_MUD, 3, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 3, 14, 29, blockHelper.COMPACTED_MUD, 4, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 5, 14, 27, blockHelper.COMPACTED_MUD, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 7, 22, blockHelper.COMPACTED_MUD, 4, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 6, 21, blockHelper.COMPACTED_MUD, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 6, 20, blockHelper.COMPACTED_MUD, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 5, 18, blockHelper.COMPACTED_MUD, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 4, 15, blockHelper.COMPACTED_MUD, 6, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 3, 4, 15, blockHelper.COMPACTED_MUD, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 14, blockHelper.COMPACTED_MUD, 6, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 13, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 3, 13, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 12, blockHelper.COMPACTED_MUD, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 11, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 10, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 12, 26, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 13, 28, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 9, 27, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 9, 29, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 9, 29, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 13, 25, blockHelper.COMPACTED_MUD, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 7, 24, blockHelper.COMPACTED_MUD, 1, 6, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 8, 23, blockHelper.COMPACTED_MUD, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 8, 22, blockHelper.COMPACTED_MUD, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 10, 22, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 7, 21, blockHelper.COMPACTED_MUD, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 7, 20, blockHelper.COMPACTED_MUD, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6, 19, blockHelper.COMPACTED_MUD, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6, 18, blockHelper.COMPACTED_MUD, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 5, 17, blockHelper.COMPACTED_MUD, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 5, 15, blockHelper.COMPACTED_MUD, 1, 3, 2, facing);
		rotatedCubeVolume(world, rand, pos, 4, 4, 9, blockHelper.COMPACTED_MUD, 1, 2, 5, facing);
		rotatedCubeVolume(world, rand, pos, 3, 3, 14, blockHelper.COMPACTED_MUD, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6, 13, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);

		//side caps
		rotatedCubeVolume(world, rand, pos, 0, 4, 9, blockHelper.COMPACTED_MUD, 1, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 0, 5, 11, blockHelper.COMPACTED_MUD, 1, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 0, 6, 13, blockHelper.COMPACTED_MUD, 1, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 0, 7, 15, blockHelper.COMPACTED_MUD, 1, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 0, 8, 17, blockHelper.COMPACTED_MUD, 1, 1, 8, facing);
		rotatedCubeVolume(world, rand, pos, 0, 9, 19, blockHelper.COMPACTED_MUD, 1, 1, 6, facing);
		rotatedCubeVolume(world, rand, pos, 0, 10, 21, blockHelper.COMPACTED_MUD, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 0, 11, 23, blockHelper.COMPACTED_MUD, 1, 1, 2, facing);
		
		//hoard room
		rotatedCubeVolume(world, rand, pos, 4, 1, 15, blockHelper.AIR, 3, 3, 9, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0, 15, blockHelper.COMPACTED_MUD, 4, 1, 9, facing);
		rotatedCubeVolume(world, rand, pos, 8, 0, 20, blockHelper.COMPACTED_MUD, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 7, 0, 24, blockHelper.COMPACTED_MUD, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 1, 14, blockHelper.COMPACTED_MUD, 3, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 15, blockHelper.COMPACTED_MUD, 1, 2, 5, facing);
		rotatedCubeVolume(world, rand, pos, 9, 1, 20, blockHelper.COMPACTED_MUD, 1, 2, 4, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 23, blockHelper.COMPACTED_MUD, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1, 24, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 1, 24, blockHelper.COMPACTED_MUD, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 25, blockHelper.COMPACTED_MUD, 3, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 15, blockHelper.COMPACTED_MUD, 1, 1, 9, facing);
		rotatedCubeVolume(world, rand, pos, 8, 3, 20, blockHelper.COMPACTED_MUD, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 8, 3, 24, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 4, 18, blockHelper.COMPACTED_MUD, 3, 1, 5, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 12, blockHelper.COMPACTED_MUD, 2, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 14, blockHelper.COMPACTED_MUD, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 3, 1, 13, blockHelper.COMPACTED_MUD_MIRAGE, 1, 2, 1, facing); //mirage blocks
		rotatedCubeVolume(world, rand, pos, 7, 1, 24, blockHelper.COMPACTED_MUD_MIRAGE, 1, 3, 1, facing);

		//floor
		rotatedCubeVolume(world, rand, pos, 1, 0, 16, blockHelper.MUD_TILES_DECAY, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 18, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 21, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 22, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0, 15, blockHelper.MUD_TILES, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0, 16, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0, 16, blockHelper.MUD_TILES_DECAY, 2, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0, 17, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0, 18, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 0, 17, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 0, 18, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0, 19, blockHelper.MUD_TILES_CRACKED, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0, 20, blockHelper.MUD_TILES_DECAY, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0, 22, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0, 23, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0, 19, blockHelper.MUD_TILES_DECAY, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0, 24, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0, 19, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 0, 19, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0, 20, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0, 21, blockHelper.MUD_TILES_CRACKED, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0, 20, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 0, 20, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 0, 20, blockHelper.MUD_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0, 21, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0, 22, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 0, 22, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0, 23, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0, 23, blockHelper.MUD_BRICKS_CARVED_DECAY_4, 1, 1, 2, facing);

		//solids
		rotatedCubeVolume(world, rand, pos, 0, 1, 15, blockHelper.MUD_BRICKS, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 14, blockHelper.MUD_BRICKS, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, 16, blockHelper.MUD_BRICKS_CARVED_DECAY_4, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, 19, blockHelper.MUD_BRICKS, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 20, blockHelper.MUD_BRICKS, 1, 1, 5, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 24, blockHelper.MUD_BRICKS, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 24, blockHelper.MUD_BRICKS, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 3, 22, blockHelper.MUD_BRICKS, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 8, 3, 15, blockHelper.MUD_BRICKS, 1, 1, 6, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 14, blockHelper.MUD_BRICKS, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 3, 14, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 15, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 16, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 17, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 18, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 15, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 15, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 19, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 23, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 23, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 3, 21, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 19, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 16, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 18, blockHelper.MUD_BRICKS_CARVED_DECAY_3, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 22, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, 23, blockHelper.MUD_BRICKS, 1, 2, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 23, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 24, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 23, blockHelper.MUD_BRICKS_CARVED_DECAY_4, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 24, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 2, 24, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 1, 24, blockHelper.MUD_BRICKS_CARVED_DECAY_4, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 2, 20, blockHelper.MUD_BRICKS_CARVED_DECAY_3, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 20, blockHelper.MUD_BRICKS_CARVED_DECAY_4, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 19, blockHelper.MUD_BRICKS, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 19, blockHelper.MUD_BRICKS_CARVED_DECAY_3, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 2, 19, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 18, blockHelper.MUD_BRICKS_CARVED_EDGE_DECAY_4, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 2, 18, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 17, blockHelper.MUD_BRICKS_CARVED_DECAY_3, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 2, 16, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 15, blockHelper.MUD_BRICKS, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 15, blockHelper.MUD_BRICKS_CARVED_DECAY_4, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 2, 15, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 14, blockHelper.MUD_BRICKS, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1, 14, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 2, 14, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 1, 14, blockHelper.MUD_BRICKS_CARVED_DECAY_3, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 2, 14, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 1, 14, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 15, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 15, blockHelper.MUD_BRICKS_CARVED, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 19, blockHelper.MUD_BRICKS_CARVED_DECAY_3, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 19, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 1, 23, blockHelper.MUD_BRICKS_CARVED_DECAY_1, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 1, 19, blockHelper.MUD_BRICKS_CARVED_DECAY_2, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 19, blockHelper.MUD_BRICKS_CARVED, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 19, blockHelper.MUD_BRICKS, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 15, blockHelper.MUD_BRICK_WALL, 1, 1, 1, facing);

		//stairs
		rotatedCubeVolume(world, rand, pos, 1, 3, 18, blockHelper.MUD_BRICK_STAIRS_DECAY_1.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 22, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 18, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 22, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 18, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 2, 1, 14, blockHelper.MUD_BRICK_STAIRS_DECAY_3.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 3, 15, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 3, 19, blockHelper.MUD_BRICK_STAIRS_DECAY_1.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 3, 23, blockHelper.MUD_BRICK_STAIRS_DECAY_2.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 1, 22, blockHelper.MUD_BRICK_STAIRS_DECAY_3.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 1, 24, blockHelper.MUD_BRICK_STAIRS_DECAY_3.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 3, 19, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 3, 23, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateY()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 2, 2, 14, blockHelper.MUD_BRICK_STAIRS_DECAY_2.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 16, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 20, blockHelper.MUD_BRICK_STAIRS_DECAY_1.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 1, 15, blockHelper.MUD_BRICK_STAIRS_DECAY_2.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 20, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 16, blockHelper.MUD_BRICK_STAIRS_DECAY_1.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 20, blockHelper.MUD_BRICK_STAIRS_DECAY_2.withProperty(BlockStairsBetweenlands.FACING, facing.getOpposite()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 4, 2, 14, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3, 15, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 24, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 3, 24, blockHelper.MUD_BRICK_STAIRS_DECAY_1.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 3, 19, blockHelper.MUD_BRICK_STAIRS.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 16, blockHelper.MUD_BRICK_STAIRS_DECAY_3.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 21, blockHelper.MUD_BRICK_STAIRS_DECAY_3.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 4, 1, 24, blockHelper.MUD_BRICK_STAIRS_DECAY_2.withProperty(BlockStairsBetweenlands.FACING, facing.rotateYCCW()).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);

		//slabs
		rotatedCubeVolume(world, rand, pos, 1, 1, 20, blockHelper.MUD_BRICK_SLAB_DECAY_3.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 23, blockHelper.MUD_BRICK_SLAB_DECAY_3.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 3, 15, blockHelper.MUD_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 16, blockHelper.MUD_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3, 19, blockHelper.MUD_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 3, 23, blockHelper.MUD_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 23, blockHelper.MUD_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, facing);

		//alcoves and urns - may need loot
		rotatedCubeVolume(world, rand, pos, 0, 2, 17, blockHelper.MUD_BRICKS_ALCOVE_EAST, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 2, 21, blockHelper.MUD_BRICKS_ALCOVE_EAST, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 2, 17, blockHelper.MUD_BRICKS_ALCOVE_WEST, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 2, 21, blockHelper.MUD_BRICKS_ALCOVE_WEST, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 5, 1, 15, blockHelper.getRandomLootUrn(rand, facing.getOpposite()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 16, blockHelper.getRandomLootUrn(rand, facing.rotateY()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 20, blockHelper.getRandomLootUrn(rand, facing.rotateY()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 18, blockHelper.getRandomLootUrn(rand, facing.rotateYCCW()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 22, blockHelper.getRandomLootUrn(rand, facing.rotateYCCW()), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 2, 1, 25, blockHelper.AIR, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 1, 26, blockHelper.AIR, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 25, blockHelper.AIR, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 2, 16, blockHelper.AIR, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 17, blockHelper.AIR, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 12, blockHelper.AIR, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 7, 2, 20, blockHelper.AIR, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 7, 1, 21, blockHelper.AIR, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 8, 1, 24, blockHelper.AIR, 2, 2, 1, facing); // second set is armour gap
		blockHelper.placeArmourStandLoot(world, pos.add(9, 1, 24), facing.rotateY(), rand);

		rotatedCubeVolume(world, rand, pos, 1, 5, 11, blockHelper.AIR, 3, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 6, 13, blockHelper.AIR, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 5, 14, blockHelper.AIR, 2, 2, 4, facing);
		rotatedCubeVolume(world, rand, pos, 1, 6, 18, blockHelper.AIR, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 7, 15, blockHelper.AIR, 2, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 1, 8, 17, blockHelper.AIR, 2, 1, 8, facing);
		rotatedCubeVolume(world, rand, pos, 1, 9, 19, blockHelper.AIR, 2, 1, 6, facing);
		rotatedCubeVolume(world, rand, pos, 1, 10, 21, blockHelper.AIR, 2, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 3, 8, 22, blockHelper.AIR, 1, 2, 3, facing);
		rotatedCubeVolume(world, rand, pos, 3, 10, 23, blockHelper.AIR, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 11, 23, blockHelper.AIR, 3, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 2, 9, 25, blockHelper.AIR, 2, 4, 5, facing);
		rotatedCubeVolume(world, rand, pos, 3, 10, 25, blockHelper.AIR, 2, 3, 5, facing);
		rotatedCubeVolume(world, rand, pos, 2, 8, 25, blockHelper.AIR, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 8, 26, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 9, 25, blockHelper.AIR, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 4, 9, 25, blockHelper.AIR, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 5, 10, 27, blockHelper.AIR, 1, 4, 4, facing);
		rotatedCubeVolume(world, rand, pos, 6, 10, 28, blockHelper.AIR, 1, 3, 3, facing);
		rotatedCubeVolume(world, rand, pos, 6, 13, 29, blockHelper.AIR, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 8, 26, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 9, 26, blockHelper.AIR, 1, 3, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 10, 29, blockHelper.AIR, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 12, 27, blockHelper.AIR, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 13, 26, blockHelper.AIR, 4, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 2, 13, 29, blockHelper.AIR, 3, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 3, 11, 30, blockHelper.AIR, 2, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 14, 29, blockHelper.AIR, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 2, 12, 30, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 13, 30, blockHelper.AIR, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 8, 2, 22, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 22, blockHelper.COMPACTED_MUD_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 20, blockHelper.DUNGEON_WALL_CANDLE_SOUTH, 1, 1, 1, facing);
		
		rotatedCubeVolume(world, rand, pos, 6, 1, 15, blockHelper.GROUND_ITEM, 1, 1, 1, facing);
		blockHelper.setGreatSword(world, rand, pos.add(6, 1, 15));
	}

	public void rotatedCubeVolume(World world, Random rand, BlockPos pos, int offsetA, int offsetB, int offsetC, IBlockState state, int sizeWidth, int sizeHeight, int sizeDepth, EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = offsetA; xx < offsetA + sizeWidth; xx++)
					for (int zz = offsetC; zz < offsetC + sizeDepth; zz++) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), state);
					}
			break;
		case EAST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = -offsetA; zz > -offsetA - sizeWidth; zz--)
					for (int xx = offsetC; xx < offsetC + sizeDepth; xx++) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), state);
					}
			break;
		case NORTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = -offsetA; xx > -offsetA - sizeWidth; xx--)
					for (int zz = -offsetC; zz > -offsetC - sizeDepth; zz--) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), state);
					}
			break;
		case WEST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = offsetA; zz < offsetA + sizeWidth; zz++)
					for (int xx = -offsetC; xx > -offsetC - sizeDepth; xx--) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), state);
					}
			break;
		}
	}
}
