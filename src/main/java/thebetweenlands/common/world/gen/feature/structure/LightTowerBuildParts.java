package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.structure.BlockBeamLensSupport;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockBeamTube;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.terrain.BlockRottenLog;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class LightTowerBuildParts {

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();

	public LightTowerBuildParts() {
	}

	public void buildPitEntrance(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -7, 12 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, -7, 13 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, -7, 14 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 3), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -7, 15 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 3, facing);

		rotatedCubeVolume(world, rand, pos, -7, 12 + level, 3, blockHelper.getMudBricksForLevel(rand, 7, 1), 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -7, 13 + level, 3, blockHelper.getMudBricksForLevel(rand, 7, 2), 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -7, 14 + level, 3, blockHelper.getMudBricksForLevel(rand, 7, 3), 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -7, 15 + level, 3, blockHelper.getMudBricksForLevel(rand, 7, 1), 5, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, -3, 12 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, -3, 13 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, -3, 14 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 3), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, -3, 15 + level, 4, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 10, facing);

		rotatedCubeVolume(world, rand, pos, -3, 12 + level, 8, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 6, facing);
		rotatedCubeVolume(world, rand, pos, -3, 13 + level, 8, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 6, facing);
		rotatedCubeVolume(world, rand, pos, -3, 14 + level, 8, blockHelper.getMudBricksForLevel(rand, 7, 3), 1, 1, 6, facing);

		rotatedCubeVolume(world, rand, pos, -4, 15 + level, 5, blockHelper.getStairsForLevel(rand, 7, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, -4, 15 + level, 8, blockHelper.getStairsForLevel(rand, 7, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 3, facing);
		
		rotatedCubeVolume(world, rand, pos, -6, 12 + level, 7, blockHelper.AIR, 3, 3, 4, facing);
		rotatedCubeVolume(world, rand, pos, -7, 11 + level, 3, blockHelper.MUD_TILES_DECAY, 5, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, -4, 11 + level, 10, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);

		for (int count = 1; count <= 3; count++)
			world.setBlockState(pos.add(7, 15 + level, 3).offset(facing.rotateY(), count), blockHelper.getRandomBeam(facing.rotateY(), rand, level, count, false));
		rotatedCubeVolume(world, rand, pos, -3, 12 + level, 7, blockHelper.getPillarsForLevel(rand, 7, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 13 + level, 7, blockHelper.getPillarsForLevel(rand, 7, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -3, 14 + level, 7, blockHelper.getPillarsForLevel(rand, 7, 3), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 14 + level, 7, blockHelper.getRandomSupportBeam(facing.rotateY(), true, rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -6, 14 + level, 7, blockHelper.getRandomSupportBeam(facing.getOpposite().rotateY(), true, rand), 1, 1, 1, facing);

		
		rotatedCubeVolume(world, rand, pos, -4, 12 + level, 5, blockHelper.AIR, 1, 3, 2, facing);
		rotatedCubeVolume(world, rand, pos, -5, 12 + level, 5, blockHelper.AIR, 1, 4, 2, facing);
		rotatedCubeVolume(world, rand, pos, -6, 13 + level, 5, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -6, 12 + level, 6, blockHelper.AIR, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, -5, 12 + level, 4, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -7, 14 + level, 6, blockHelper.AIR, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -4, 12 + level, 11, blockHelper.AIR, 1, 1, 1, facing);
	}

	public void buildsMazeGate(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		
		rotatedCubeVolume(world, rand, pos, 1, 0 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 0), 6, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 0 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0 + level, -4, blockHelper.getTilesForLevel(rand, 0), 3, 1, 1, facing);
		
		rotatedCubeVolume(world, rand, pos, 4, 0 + level, -1, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0 + level, 0, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 2, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 4, 4 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 5 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 5 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing, BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 6 + level, -3, blockHelper.MUD_TOWER_BEAM_RELAY.withProperty(BlockBeamRelay.FACING, facing), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 4, 6 + level, -4, blockHelper.getPillarsForLevel(rand, 0, 3), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6 + level, -3, blockHelper.MUD_TOWER_BEAM_RELAY.withProperty(BlockBeamRelay.FACING, facing.getOpposite().rotateY()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 6 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 7 + level, -2, blockHelper.MUD_BRICK_WALL, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 7 + level, -4, blockHelper.MUD_BRICK_WALL, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 7 + level, -3, blockHelper.MUD_BRICK_WALL, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 7 + level, -3, blockHelper.MUD_BRICK_WALL, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 7 + level, -2, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 7 + level, -4, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 7 + level, -2, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 7 + level, -4, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 8 + level, -3, blockHelper.ROOT, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 0 + level, 4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 0 + level, 4, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0 + level, 1, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 0 + level, 2, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0 + level, 5, blockHelper.getMudBricksForLevel(rand, 0, 0), 3, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 0 + level, 5, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 2 + level, 5, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3 + level, 5, blockHelper.MUD_BRICK_WALL, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 5 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 6 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 7 + level, 5, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 5 + level, 5, blockHelper.getRandomSupportBeam(facing.rotateY(), true, rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 6 + level, 5, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 5 + level, 5, blockHelper.getRandomSupportBeam(facing.getOpposite().rotateY(), true, rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6 + level, 5, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 2 + level, 5, blockHelper.getRandomSupportBeam(facing.rotateY(), false, rand), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2 + level, 5, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 2 + level, 5, blockHelper.getRandomSupportBeam(facing.getOpposite().rotateY(), false, rand), 1, 1, 1, facing);

		// FL
		rotatedCubeVolume(world, rand, pos, 2, 0 + level, -1, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0 + level, -1, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 1 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 2 + level, -2, blockHelper.getPillarsForLevel(rand, 0, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 3 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 4 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 4 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 5 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);

		//FR
		rotatedCubeVolume(world, rand, pos, 6, 0 + level, -1, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 0 + level, -1, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 2 + level, -2, blockHelper.getPillarsForLevel(rand, 0, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 4 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 4 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 5 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 6 + level, -2, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);

		//MID L
		rotatedCubeVolume(world, rand, pos, 0, 0 + level, -3, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 0 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 1 + level, -3, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 3 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 4 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 5 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);

		//MID R
		rotatedCubeVolume(world, rand, pos, 6, 0 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 1 + level, -3, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 4 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 5 + level, -3, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);

		//BL
		rotatedCubeVolume(world, rand, pos, 2, 1 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 2 + level, -4, blockHelper.getPillarsForLevel(rand, 0, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 3 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 4 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 4 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 5 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 6 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);

		//BR
		rotatedCubeVolume(world, rand, pos, 6, 1 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 2 + level, -4, blockHelper.getPillarsForLevel(rand, 0, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 4 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 4 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 5 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 6 + level, -4, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 1 + level, -4, blockHelper.MUD_BRICK_WALL, 3, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 0 + level, -3, blockHelper.AIR, 3, 1, 1, facing);  //Not sure if this is needed

		world.setBlockState(pos.add(3, 2 + level, 4), blockHelper.DUNGEON_DOOR_WEST, 2);
		TileEntityDungeonDoorRunes tileLock = (TileEntityDungeonDoorRunes) world.getTileEntity(pos.add(3, 2 + level, 4));
		if (tileLock instanceof TileEntityDungeonDoorRunes) {
			tileLock.top_code = 1;
			tileLock.mid_code = 1;
			tileLock.bottom_code = 1;
			tileLock.is_in_dungeon = true;
			tileLock.is_gate_entrance = true;
		}
	}

	public void buildsSpiralStairPart(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer, boolean topEnd) {
		if(topEnd) {
			rotatedCubeVolume(world, rand, pos, 13, 0 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 12, 0 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 0 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 1 + level, -2, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 1 + level, -3, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 1 + level, -2, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 12, 1 + level, -3, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 1 + level, -4, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 1 + level, -4, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 1 + level, -4, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 2 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 12, 2 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 2 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 12, 3 + level, -3, blockHelper.ROOT, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 3 + level, -4, blockHelper.ROOT, 1, 5, 1, facing);
		}
		rotatedCubeVolume(world, rand, pos, 10, 0 + level, 0, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 1 + level, -1, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, -1 + level, 0, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, 0 + level, -1, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, 0 + level, 0, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, 1 + level, -1, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, 1 + level, 0, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, 2 + level, -1, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 14, 3 + level, -1, blockHelper.ROOT, 1, 5, 1, facing);

		if(level < 16) {
			rotatedCubeVolume(world, rand, pos, 8, 4 + level, -6, level == 0 ? blockHelper.PITSTONE_BRICKS : blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 8, 5 + level, -6, blockHelper.getRandomLootUrn(rand, facing), 1, 1, 1, facing);
		}
		if (level == 0) {
			rotatedCubeVolume(world, rand, pos, 10, level, 1, blockHelper.ROTTEN_PLANKS, 6, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, level, 2, blockHelper.ROTTEN_PLANKS, 5, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, level, 3, blockHelper.ROTTEN_PLANKS, 4, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, level, 4, blockHelper.ROTTEN_PLANKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 1 + level, -5, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 1 + level, -6, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 12, 1 + level, -7, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 1 + level, -8, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 1 + level, -9, blockHelper.ROOT, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 1 + level, -10, blockHelper.ROOT, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 1 + level, -12, blockHelper.ROOT, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 1 + level, -13, blockHelper.ROOT, 1, 4, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 1 + level, -14, blockHelper.ROOT, 1, 5, 1, facing);
		}

		if(!topEnd) {
			rotatedCubeVolume(world, rand, pos, 10, 1 + level, -2, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 4, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 2 + level, -3, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 4, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 2 + level, -4, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 4, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 3 + level, -5, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 4, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 3 + level, -6, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 4, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 14, 2 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 14, 3 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 3 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 4 + level, -5, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 4 + level, -6, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 14, 1 + level, -2, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 14, 2 + level, -3, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 2 + level, -4, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 3 + level, -5, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 3 + level, -6, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 14, 0 + level, -2, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 14, 1 + level, -3, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 1 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 2 + level, -5, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 2 + level, -6, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 14, 4 + level, -3, blockHelper.ROOT, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 13, 5 + level, -5, blockHelper.ROOT, 1, 5, 1, facing);

			rotatedCubeVolume(world, rand, pos, 9, 4 + level, -7, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 4 + level, -8, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 4 + level, -9, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 4 + level, -10, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 12, 3 + level, -7, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 3 + level, -8, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 3 + level, -9, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 3 + level, -10, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 3 + level, -10, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 12, 4 + level, -7, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 4 + level, -8, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 4 + level, -9, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 4 + level, -10, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 4 + level, -10, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 12, 5 + level, -7, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 5 + level, -8, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 5 + level, -9, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 10, 5 + level, -10, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 5 + level, -10, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 12, 6 + level, -7, blockHelper.ROOT, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 11, 6 + level, -9, blockHelper.ROOT, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 6 + level, -10, blockHelper.ROOT, 1, 5, 1, facing);

			rotatedCubeVolume(world, rand, pos, 8, 4 + level, -10, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 1, 1, 4, facing);
			rotatedCubeVolume(world, rand, pos, 7, 5 + level, -11, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 1, 1, 5, facing);
			rotatedCubeVolume(world, rand, pos, 6, 5 + level, -12, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 1, 1, 5, facing);

			rotatedCubeVolume(world, rand, pos, 8, 3 + level, -11, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 4 + level, -12, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 4 + level, -13, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 8, 4 + level, -11, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 5 + level, -12, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 5 + level, -13, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 8, 5 + level, -11, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 6 + level, -12, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 6 + level, -13, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, 7 + level, -12, blockHelper.ROOT, 1, 5, 1, facing);

			rotatedCubeVolume(world, rand, pos, 5, 6 + level, -12, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 1, 1, 4, facing);
			rotatedCubeVolume(world, rand, pos, 4, 6 + level, -12, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 1, 1, 4, facing);
			rotatedCubeVolume(world, rand, pos, 3, 7 + level, -13, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 1, 1, 4, facing);
			rotatedCubeVolume(world, rand, pos, 2, 7 + level, -13, blockHelper.ROTTEN_PLANK_SLAB_UPPER, 1, 1, 4, facing);
			rotatedCubeVolume(world, rand, pos, 1, 8 + level, -13, blockHelper.ROTTEN_PLANK_SLAB_LOWER, 1, 1, 4, facing);

			rotatedCubeVolume(world, rand, pos, 5, 5 + level, -13, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 5 + level, -13, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 6 + level, -14, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 6 + level, -14, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 7 + level, -14, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 5, 6 + level, -13, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 6 + level, -13, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 7 + level, -14, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 7 + level, -14, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 8 + level, -14, blockHelper.getRandomBeam(facing.rotateY(), rand, level, 0, false), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 5, 7 + level, -13, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 7 + level, -13, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 8 + level, -14, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 8 + level, -14, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 9 + level, -14, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 5, 8 + level, -13, blockHelper.ROOT, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 9 + level, -14, blockHelper.ROOT, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 10 + level, -14, blockHelper.ROOT, 1, 5, 1, facing);
		}
	}

	public void addTowerFloor(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		if (level == 0 || level == 8) {
			rotatedCubeVolume(world, rand, pos, 0, 1 + level, 0, blockHelper.ENERGY_BARRIER_MUD, 1, 7, 1, facing);
			rotatedCubeVolume(world, rand, pos, 8, 0 + level, 0, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 8, 0 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 0 + level, 6, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 0 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 0 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 3, 0 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 0, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 6, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 2, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 6, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 4, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 5, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 6, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 1, 0 + level, 7, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 1, 1 + level, 1, blockHelper.getEnergyBarrier(facing == EnumFacing.SOUTH ? true : facing == EnumFacing.NORTH ? true : false), 1, 7, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 1 + level, 2, blockHelper.getEnergyBarrier(facing == EnumFacing.SOUTH ? true : facing == EnumFacing.NORTH ? true : false), 1, 7, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 1 + level, 3, blockHelper.getEnergyBarrier(facing == EnumFacing.SOUTH ? true : facing == EnumFacing.NORTH ? true : false), 1, 7, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 1 + level, 4, blockHelper.getEnergyBarrier(facing == EnumFacing.SOUTH ? true : facing == EnumFacing.NORTH ? true : false), 1, 7, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 1 + level, 5, blockHelper.getEnergyBarrier(facing == EnumFacing.SOUTH ? true : facing == EnumFacing.NORTH ? true : false), 1, 7, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 1 + level, 6, blockHelper.getEnergyBarrier(facing == EnumFacing.SOUTH ? true : facing == EnumFacing.NORTH ? true : false), 1, 7, 1, facing);

			rotatedCubeVolume(world, rand, pos, 8, 7 + level, 0, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, 7, 7 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 7 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 6, 7 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 7 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 6, 7 + level, 6, level == 0 ? blockHelper.SMOOTH_PITSTONE : blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 7 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 4, 7 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 3, 7 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 7 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, 1, 7 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, level == 0 ? false : true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 8 + level, 0, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
		}

		if (level == 16) {
			rotatedCubeVolume(world, rand, pos, 0, 0 + level, 1, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 1, facing);
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

			rotatedCubeVolume(world, rand, pos, -3, 0 + level, 3, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing); //braziers go above this
			rotatedCubeVolume(world, rand, pos, -3, 1 + level, 3, blockHelper.getRandomBeam(facing, rand, level, 0, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 2 + level, 3, blockHelper.BRAZIER_BOTTOM, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 3 + level, 3, blockHelper.BRAZIER_TOP, 1, 1, 1, facing); 
			rotatedCubeVolume(world, rand, pos, -4, 1 + level, 3, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -4, 1 + level, 2, blockHelper.getStairsForLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -4, 1 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 2, blockHelper.getStairsForLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 4, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 1 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 1 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -4, 2 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 1 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -5, 0 + level, 6, blockHelper.PITSTONE_BRICKS, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 0 + level, 5, blockHelper.PITSTONE_BRICKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 0 + level, 4, blockHelper.PITSTONE_BRICKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -4, 0 + level, 5, blockHelper.PITSTONE_BRICKS, 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, -5, 0 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -2, 0 + level, 5, blockHelper.PITSTONE_BRICKS, 5, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, -1, 0 + level, 4, blockHelper.PITSTONE_BRICKS, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 0 + level, 8, blockHelper.PITSTONE_BRICKS, 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 0 + level, 1, blockHelper.PITSTONE_BRICKS, 1, 1, 2, facing);

			//top bit arches and stuff
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 8, blockHelper.PITSTONE_PILLAR, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 4 + level, 8, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 4 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 4 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 4 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 4 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 5 + level, 8, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -4, 5 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 6 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, -6, 6 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 6 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 6 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 6 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 6 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 3, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 9, blockHelper.PITSTONE_BRICKS, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 4 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 4 + level, 9, blockHelper.PITSTONE_BRICKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 5 + level, 9, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 5 + level, 7, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 5 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 8, blockHelper.SMOOTH_PITSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 3 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 1 + level, 8, blockHelper.PITSTONE_BRICKS, 1, 2, 2, facing);
			rotatedCubeVolume(world, rand, pos, -3, 3 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -5, 1 + level, 8, blockHelper.PITSTONE_BRICKS, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 1 + level, 6, blockHelper.PITSTONE_BRICKS, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 1 + level, 6, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 1 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -9, 1 + level, 3, blockHelper.PITSTONE_BRICKS, 2, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 1 + level, 2, blockHelper.PITSTONE_BRICKS, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 1 + level, 2, blockHelper.PITSTONE_PILLAR, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 4 + level, 2, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 2 + level, 8, blockHelper.PITSTONE_TILES, 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 2 + level, 6, blockHelper.PITSTONE_TILES, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 2 + level, 6, blockHelper.PITSTONE_TILES, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 2 + level, 4, blockHelper.PITSTONE_TILES, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -5, 3 + level, 8, blockHelper.PITSTONE_PILLAR, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 5 + level, 8, blockHelper.SMOOTH_PITSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 5 + level, 7, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 5 + level, 8, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 3 + level, 6, blockHelper.PITSTONE_PILLAR, 1, 3, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 6 + level, 6, blockHelper.SMOOTH_PITSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -6, 7 + level, 6, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 3 + level, 5, blockHelper.PITSTONE_PILLAR, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 5, blockHelper.SMOOTH_PITSTONE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 4, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 5 + level, 5, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 6, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 2, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 2, facing);
			rotatedCubeVolume(world, rand, pos, -7, 4 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 3, facing);
			rotatedCubeVolume(world, rand, pos, -7, 5 + level, 1, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 5 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, false), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 5 + level, 1, blockHelper.SMOOTH_PITSTONE_SLAB_LOWER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 4 + level, 1, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 4 + level, 2, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 3 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM, true), 2, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 4 + level, 3, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -9, 3 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -8, 4 + level, 1, blockHelper.getStairsForTowerLevel(rand, level, facing, BlockStairsBetweenlands.EnumHalf.TOP, false), 1, 1, 1, facing);
		}
	}

	public void addTowerDoorways(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 0, 1 + level, 9, blockHelper.AIR, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 1 + level, 9, blockHelper.AIR, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1 + level, 9, blockHelper.AIR, 1, 2, 1, facing);
		
		if(level == 0) {
			rotatedCubeVolume(world, rand, pos, 1, 3 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 3 + level, 9, blockHelper.getStairsForTowerLevel(rand, level, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP, true), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 3 + level, 9, blockHelper.PITSTONE_BRICK_SLAB_UPPER, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 4 + level, 9,  blockHelper.PITSTONE_BRICK_SLAB_UPPER, 1, 1, 1, facing);
		}
		if(level == 8) {
			rotatedCubeVolume(world, rand, pos, 1, 3 + level, 9, blockHelper.getStairsForLevel(rand, 0, facing.getOpposite().rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -1, 3 + level, 9, blockHelper.getStairsForLevel(rand, 0, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -3, 1 + level, 8, blockHelper.getPillarsForLevel(rand, 0, 1), 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 3, 1 + level, 8, blockHelper.getPillarsForLevel(rand, 0, 1), 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 3 + level, 9, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 4 + level, 9, blockHelper.getMudSlabsForLevel(rand, 0, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		}
	}

	public void addLightBeams(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int i) {
		if(level == 0) {
			rotatedCubeVolume(world, rand, pos, -2, 2 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 2 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 15, 6 + level, -1, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 14, 6 + level, -1, blockHelper.AIR, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 14, 7 + level, -1, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 15, 7 + level, -1, blockHelper.getRandomSupportBeam(facing.getOpposite().rotateY(), false, rand), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -2, 3 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 2, 1, facing);
			
			rotatedCubeVolume(world, rand, pos, 2, 3 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 2, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, 7 + level, -1, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 6 + level, -1, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 8, 6 + level, -1, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS.withProperty(BlockBeamLensSupport.FACING, facing.rotateY()), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 9, 6 + level, -1, blockHelper.MUD_TOWER_BEAM_TUBE.withProperty(BlockBeamTube.FACING, facing.rotateY()), 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 5, 1 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 2 + level, 4, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 7 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 5, 1 + level, -4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 2 + level, -4, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 5, 7 + level, -4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -5, 1 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 2 + level, 4, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 7 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -5, 1 + level, -4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 2 + level, -4, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, -5, 7 + level, -4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
		}
		
		if(level == 8) {
			rotatedCubeVolume(world, rand, pos, 0, 2 + level, 0, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 0, 1 + level, 0, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -2, 0 + level, 4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 3 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 0 + level, 4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 2 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, -1 + level, 4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 3 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 4 + level, 4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 5 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 6 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 7 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, -1 + level, 4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 6 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, 7 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 8 + level, 4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 2, 1 + level, 0, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 2 + level, 0, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -2, 0 + level, -4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 4, 1, facing);
			rotatedCubeVolume(world, rand, pos, -2, 4 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 2, 0 + level, -4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 2, 2 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, -1 + level, -4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 3, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, 4 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 5 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 6 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 7 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, 6 + level, -1, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 7 + level, -1, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 8, 6 + level, -1, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS.withProperty(BlockBeamLensSupport.FACING, facing.rotateY()), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 0 + level, -1, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, 0 + level, -2, blockHelper.PITSTONE_BRICKS, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 2 + level, -2, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 7 + level, -2, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, -7, 0 + level, 2, blockHelper.PITSTONE_BRICKS, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 2 + level, 2, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 7 + level, 2, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, 0 + level, 2, blockHelper.PITSTONE_BRICKS, 1, 2, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 2 + level, 2, blockHelper.PITSTONE_PILLAR, 1, 5, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 7 + level, 2, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);

			rotatedCubeVolume(world, rand, pos, 7, 2 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 3 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 4 + level, -4, blockHelper.getMudBricksForLevel(rand, 0, 0), 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 5 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 6 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, 7 + level, -4, blockHelper.PITSTONE_BRICKS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, 7, -1 + level, -4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 2, 1, facing);
		}

		if(level == 16) {
			rotatedCubeVolume(world, rand, pos, 0, 5 + level, 0, blockHelper.MUD_TOWER_BEAM_ORIGIN, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 2 + level, 4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 1 + level, 4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 0 + level, 4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 2 + level, -4, blockHelper.MUD_TOWER_BEAM_RELAY, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 1 + level, -4, blockHelper.MUD_TOWER_BEAM_LENS_SUPPORTS, 1, 1, 1, facing);
			rotatedCubeVolume(world, rand, pos, -7, 0 + level, -4, blockHelper.MUD_TOWER_BEAM_TUBE, 1, 1, 1, facing);
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
						 if (state == blockHelper.ROTTEN_PLANK_SLAB_LOWER)
							 if(rand.nextInt(10) == 0)
								 addTriggeredFallingBlockEntity(world, pos.add(xx, yy, zz));
					}
			break;
		case EAST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = -offsetA; zz > -offsetA - sizeWidth; zz--)
					for (int xx = offsetC; xx < offsetC + sizeDepth; xx++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
						 if (state == blockHelper.ROTTEN_PLANK_SLAB_LOWER)
							 if(rand.nextInt(10) == 0)
								 addTriggeredFallingBlockEntity(world, pos.add(xx, yy, zz));
					}
			break;
		case NORTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = -offsetA; xx > -offsetA - sizeWidth; xx--)
					for (int zz = -offsetC; zz > -offsetC - sizeDepth; zz--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
						 if (state == blockHelper.ROTTEN_PLANK_SLAB_LOWER)
							 if(rand.nextInt(10) == 0)
								 addTriggeredFallingBlockEntity(world, pos.add(xx, yy, zz));
					}
			break;
		case WEST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = offsetA; zz < offsetA + sizeWidth; zz++)
					for (int xx = -offsetC; xx > -offsetC - sizeDepth; xx--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 2);//16);
						 if (state.getBlock() instanceof BlockLootUrn)
							 blockHelper.setLootUrnTileProperties(world, rand, pos.add(xx, yy, zz));
						 if (state == blockHelper.ROTTEN_PLANK_SLAB_LOWER)
							 if(rand.nextInt(10) == 0)
								 addTriggeredFallingBlockEntity(world, pos.add(xx, yy, zz));
					}
			break;
		}
	}

	private void addTriggeredFallingBlockEntity(World world, BlockPos pos) {
		EntityTriggeredFallingBlock trap = new EntityTriggeredFallingBlock(world);
		trap.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		trap.setWalkway(true);
		world.spawnEntity(trap);
	}

	public void destroyTowerBeamLenses(World world, BlockPos pos) {
			removeblock(world, pos, -2, 2, 4);
			removeblock(world, pos, 2, 2, 4);
			removeblock(world, pos, -7, 1, 4);
			removeblock(world, pos, 7, 1, 4);
			removeblock(world, pos, 15, 6, -1);
			removeblock(world, pos, -2, 3, -4);
			removeblock(world, pos, 2, 3, -4);
			removeblock(world, pos, -7, 1, -4);
			removeblock(world, pos, 7, 1, -4);
			removeblock(world, pos, 7, 6, -1);

			removeblock(world, pos, 0, 10, 0);
			removeblock(world, pos, -2, 11, 4);
			removeblock(world, pos, 2, 10, 4);
			removeblock(world, pos, -7, 11, 4);
			removeblock(world, pos, -7, 14, 4);
			removeblock(world, pos, 7, 14, 4);
			removeblock(world, pos, 2, 10, 0);
			removeblock(world, pos, -2, 12, -4);
			removeblock(world, pos, 2, 10, -4);
			removeblock(world, pos, -7, 12, -4);
			removeblock(world, pos, -7, 14, -4);
			removeblock(world, pos, 7, 14, -1);
			removeblock(world, pos, 7, 10, -4);
			removeblock(world, pos, 7, 14, -4);

			removeblock(world, pos, 0, 21, 0);
			removeblock(world, pos, -7, 18, 4);
			removeblock(world, pos, -7, 18, -4);
	}

	public void destroyGateBeamLenses(World world, BlockPos pos) {
		removeblock(world, pos, -8, 0, 0);
		removeblock(world, pos, -8, 4, 0);
		removeblock(world, pos, -1, 4, 0);
		removeblock(world, pos, 0, 4, 0);
		removeblock(world, pos, 0, 4, -1);
	}

	private void removeblock(World world, BlockPos pos, int x, int y, int z) {
		world.destroyBlock(pos.add(x, y, z), false);
	}

}
