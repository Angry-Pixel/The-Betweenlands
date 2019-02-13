package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.terrain.BlockRottenLog;
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

		if(4 + level < 19)
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

		if(level == 16)// this isn't ever called so TODO sort out a platform so stairs don't just end
			rotatedCubeVolume(world, rand, pos, 0, 8 + level, -11, blockHelper.BETWEENSTONE_BRICK_SLAB_UPPER, 1, 1, 2, facing);
	}
	
	public void addTowerFloor(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		if (level == 0 || level == 8) {
			rotatedCubeVolume(world, rand, pos, 8, 0 + level, 0, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 5, 0 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 0 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 3, 0 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 0, blockHelper.SMOOTH_BETWEENSTONE, 6, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 2, blockHelper.SMOOTH_BETWEENSTONE, 6, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 4, blockHelper.SMOOTH_BETWEENSTONE, 5, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 6, blockHelper.SMOOTH_BETWEENSTONE, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 7, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);
		}

	if (level == 16) {
		rotatedCubeVolume(world, rand, pos, 0, 0 + level, 1, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 3, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0 + level, 3, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 0 + level, 1, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 2, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 3, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 4, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 0 + level, 4, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0 + level, 4, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0 + level, 3, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 0 + level, 2, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0 + level, 2, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 0 + level, 5, blockHelper.LOG_ROTTEN_BARK.withProperty(BlockRottenLog.LOG_AXIS, BlockRottenLog.EnumAxis.fromFacingAxis(facing.getAxis())), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, -7, 0 + level, 3, blockHelper.LOG_ROTTEN_BARK.withProperty(BlockRottenLog.LOG_AXIS, BlockRottenLog.EnumAxis.fromFacingAxis(facing.rotateY().getAxis())), 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 0 + level, 3, blockHelper.BETWEENSTONE_TILES, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 0 + level, 6, blockHelper.BETWEENSTONE_TILES, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -7, 0 + level, 5, blockHelper.BETWEENSTONE_TILES, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 8, blockHelper.BETWEENSTONE_TILES, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -8, 0 + level, 1, blockHelper.BETWEENSTONE_TILES, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 0 + level, 6, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 7, blockHelper.getStairsForLevel(rand, 0, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0 + level, 1, blockHelper.getStairsForLevel(rand, 0, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 0 + level, 1, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -7, 0 + level, 2, blockHelper.getStairsForLevel(rand, 0, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -7, 0 + level, 1, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 0, 0 + level, 5, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -1, 0 + level, 6, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -6, 0 + level, 1, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0 + level, 6, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -7, 0 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 0 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 0 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 0 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);

		//top bit arches and stuff
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 8, blockHelper.BETWEENSTONE_PILLAR, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 4 + level, 8, blockHelper.BETWEENSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 4 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 4 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 4 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 4 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 5 + level, 8, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -4, 5 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 6 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, -6, 6 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 6 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 6 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 6 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 6 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 9, blockHelper.BETWEENSTONE_BRICKS, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 4 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 4 + level, 9, blockHelper.BETWEENSTONE_BRICKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 5 + level, 9, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 5 + level, 7, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 5 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 8, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 3 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 1 + level, 8, blockHelper.BETWEENSTONE_BRICKS, 1, 2, 2, facing);
			rotatedCubeVolume(world, rand, pos, -3, 3 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -5, 1 + level, 8, blockHelper.BETWEENSTONE_BRICKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 1 + level, 6, blockHelper.BETWEENSTONE_BRICKS, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 1 + level, 6, blockHelper.BETWEENSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 1 + level, 4, blockHelper.BETWEENSTONE_BRICKS, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -9, 1 + level, 3, blockHelper.BETWEENSTONE_BRICKS, 2, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 1 + level, 2, blockHelper.BETWEENSTONE_BRICKS, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 1 + level, 2, blockHelper.BETWEENSTONE_PILLAR, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 4 + level, 2, blockHelper.BETWEENSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 2 + level, 8, blockHelper.BETWEENSTONE_TILES, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 2 + level, 6, blockHelper.BETWEENSTONE_TILES, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 2 + level, 6, blockHelper.BETWEENSTONE_TILES, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 2 + level, 4, blockHelper.BETWEENSTONE_TILES, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -5, 3 + level, 8, blockHelper.BETWEENSTONE_PILLAR, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 5 + level, 8, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 5 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 5 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 3 + level, 6, blockHelper.BETWEENSTONE_PILLAR, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 6 + level, 6, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 7 + level, 6, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 3 + level, 5, blockHelper.BETWEENSTONE_PILLAR, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 5, blockHelper.SMOOTH_BETWEENSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 5 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 2, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 4 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, -7, 5 + level, 1, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 5 + level, 1, blockHelper.SMOOTH_BETWEENSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 4 + level, 1, blockHelper.BETWEENSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 4 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 3 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 4 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 3 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 4 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
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
