package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockItemShelf;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class SludgeWormMazeMicroBuilds {

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();

	public SludgeWormMazeMicroBuilds() {
	}

	public void selectFeature(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		// add random selection for builds here
		int type = 4; //rand.nextInt(10);
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
			buildWallPillarCentral(world, pos, facing, rand, level, layer);
			break;
		case 5:
			buildBlockTest(world, pos, facing, rand, level, layer);
			break;
		case 6:
			buildBlockTest(world, pos, facing, rand, level, layer);
			break;
		case 7:
			buildBlockTest(world, pos, facing, rand, level, layer);
			break;
		case 8:
			buildBlockTest(world, pos, facing, rand, level, layer);
			break;
		case 9:
			buildBlockTest(world, pos, facing, rand, level, layer);
			break;
		}
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
		rotatedCubeVolume(world, rand, pos, 0, 0, 0, blockHelper.MUD_FLOWER_POT_CANDLE_LIT, 1, 1, 1, facing);
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
		rotatedCubeVolume(world, rand, pos, -1, 1, -1, blockHelper.ITEM_SHELF.withProperty(BlockItemShelf.FACING, facing.getOpposite()), 3, 2, 1, facing); //loot?
	}
	
	private void buildWallPillarCentral(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 0, 0, 1, blockHelper.MUD_BRICK_WALL, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, blockHelper.getMudBricksForLevel(rand, level, 1), 1, 1, 1, facing);
	}

	private void buildBlockTest(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
	}

	@SuppressWarnings("incomplete-switch")
	public void rotatedCubeVolume(World world, Random rand, BlockPos pos, int offsetA, int offsetB, int offsetC, IBlockState state, int sizeWidth, int sizeHeight, int sizeDepth, EnumFacing facing) {

		switch (facing) {
		case SOUTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = offsetA; xx < offsetA + sizeWidth; xx++)
					for (int zz = offsetC; zz < offsetC + sizeDepth; zz++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		case EAST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = -offsetA; zz > -offsetA - sizeWidth; zz--)
					for (int xx = offsetC; xx < offsetC + sizeDepth; xx++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		case NORTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = -offsetA; xx > -offsetA - sizeWidth; xx--)
					for (int zz = -offsetC; zz > -offsetC - sizeDepth; zz--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		case WEST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = offsetA; zz < offsetA + sizeWidth; zz++)
					for (int xx = -offsetC; xx > -offsetC - sizeDepth; xx--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		}
	}

}
