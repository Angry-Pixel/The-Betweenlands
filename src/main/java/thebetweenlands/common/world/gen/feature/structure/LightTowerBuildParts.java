package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class LightTowerBuildParts {

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();

	public LightTowerBuildParts() {
	}

	public void buildsSpiralStairPart(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 10, 0 + level, 0, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 10, 1 + level, -1, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 1 + level, -2, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 10, 2 + level, -3, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 2 + level, -4, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 3, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 9, 3 + level, -5, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 3 + level, -6, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 3, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 8, 4 + level, -6, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 9, 4 + level, -7, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 4 + level, -8, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 4 + level, -9, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 4 + level, -10, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 4 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 1, 1, 5, facing);

		rotatedCubeVolume(world, rand, pos, 7, 5 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 1, 1, 5, facing);
		rotatedCubeVolume(world, rand, pos, 6, 5 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 1, 1, 4, facing);

		rotatedCubeVolume(world, rand, pos, 5, 6 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 4, 6 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 1, 1, 3, facing);

		rotatedCubeVolume(world, rand, pos, 3, 7 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 2, 7 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 1, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 1, 8 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_LOWER, 1, 1, 2, facing);
		//rotatedCubeVolume(world, rand, pos, 0, 8, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 1, 1, 2, facing);
	}
	
	public void addTowerFloor(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		if(level == 0 || level == 8) {
			rotatedCubeVolume(world, rand, pos, 8, 0 + level, 0, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 5, 0 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 0 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 3, 0 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 0, blockHelper.SMOOTH_BETWEENSTONE, 6, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 2, blockHelper.SMOOTH_BETWEENSTONE, 6, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 4, blockHelper.SMOOTH_BETWEENSTONE, 5, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 6, blockHelper.SMOOTH_BETWEENSTONE, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 7, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);
		}
		if (level == 16) {
			//build the top floor fancy stuff part
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void rotatedCubeVolume(World world, Random rand, BlockPos pos, int offsetA, int offsetB, int offsetC, IBlockState state, int sizeWidth, int sizeHeight, int sizeDepth, EnumFacing facing) {

		switch (facing) {
		case SOUTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = offsetA; xx < offsetA + sizeWidth; xx++)
					for (int zz = offsetC; zz < offsetC + sizeDepth; zz++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		case EAST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = -offsetA; zz > -offsetA - sizeWidth; zz--)
					for (int xx = offsetC; xx < offsetC + sizeDepth; xx++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		case NORTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = -offsetA; xx > -offsetA - sizeWidth; xx--)
					for (int zz = -offsetC; zz > -offsetC - sizeDepth; zz--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		case WEST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = offsetA; zz < offsetA + sizeWidth; zz++)
					for (int xx = -offsetC; xx > -offsetC - sizeDepth; xx--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
					}
			break;
		}
	}

}
