package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlock;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockDiagonal;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockL1;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockL2;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockR1;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockR2;
import thebetweenlands.common.block.structure.BlockMudBrickSpikeTrap;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class DecayPitBuildParts {

	private SludgeWormMazeBlockHelper blockHelper;
	private final WorldGenSludgeWormDungeon dungeon;
	
	public DecayPitBuildParts(WorldGenSludgeWormDungeon dungeon) {
		this.dungeon = dungeon;
		this.blockHelper = new SludgeWormMazeBlockHelper(dungeon);
	}

	public void buildMainAreaPart(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, 2, 0, 0, blockHelper.MUD_TILES_DECAY, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 2, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 3, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 0, 4, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 4, 1, 0, blockHelper.MUD_TILES_DECAY, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 3, 1, 2, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 1, 3, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 4, blockHelper.MUD_TILES_DECAY, 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 5, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 6, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 5, 2, 0, blockHelper.MUD_TILES_DECAY, 4, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 5, 2, 2, blockHelper.MUD_TILES_DECAY, 3, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 3, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 2, 4, blockHelper.MUD_TILES_DECAY, 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 5, blockHelper.MUD_TILES_DECAY, 6, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 6, blockHelper.MUD_TILES_DECAY, 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 7, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 8, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 7, 3, 0, blockHelper.MUD_TILES_DECAY, 3, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 7, 3, 3, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 3, 4, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 3, 5, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 3, 6, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 7, blockHelper.MUD_TILES_DECAY, 6, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 8, blockHelper.MUD_TILES_DECAY, 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 3, 9, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 9, 4, 0, blockHelper.MUD_TILES_DECAY, 2, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 9, 4, 3, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 4, 4, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 8, 4, 6, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 4, 8, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 4, 9, blockHelper.MUD_TILES_DECAY, 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 4, 10, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 10, 5, 0, blockHelper.MUD_TILES_DECAY, 2, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 9, 5, 3, blockHelper.MUD_TILES_DECAY, 2, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 9, 5, 6, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 5, 9, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 5, 10, blockHelper.MUD_TILES_DECAY, 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 5, 11, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 11, 6, 2, blockHelper.MUD_TILES_DECAY, 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 9, 6, 5, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 9, 6, 7, blockHelper.MUD_TILES_DECAY, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 8, 6, 8, blockHelper.MUD_TILES_DECAY, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 5, 6, 9, blockHelper.MUD_TILES_DECAY, 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 6, 10, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 6, 11, blockHelper.MUD_TILES_DECAY, 4, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 11, 7, 3, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 10, 7, 5, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 10, 7, 7, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 7, 8, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 7, 9, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 7, 10, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 7, 10, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 3, 7, 11, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 13, 8, 0, blockHelper.MUD_TILES_DECAY, 1, 2, 4, facing);
		rotatedCubeVolume(world, rand, pos, 10, 8, 6, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 9, 8, 8, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 8, 9, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 8, 10, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 8, 10, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 8, 13, blockHelper.MUD_TILES_DECAY, 2, 2, 1, facing);

		rotatedCubeVolume(world, rand, pos, 13, 9, 3, blockHelper.MUD_TILES_DECAY, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 10, 9, 7, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 10, 9, 9, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 9, 8, blockHelper.MUD_TILES_DECAY, 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 8, 9, 9, blockHelper.MUD_TILES_DECAY, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 9, 10, blockHelper.MUD_TILES_DECAY, 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 3, 9, 13, blockHelper.MUD_TILES_DECAY, 2, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 14, 11, 0, blockHelper.MUD_TILES_DECAY, 1, 4, 3, facing);
		rotatedCubeVolume(world, rand, pos, 14, 12, 3, blockHelper.MUD_TILES_DECAY, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 13, 10, 4, blockHelper.MUD_TILES_DECAY, 1, 5, 2, facing);
		rotatedCubeVolume(world, rand, pos, 13, 12, 6, blockHelper.MUD_TILES_DECAY, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 12, 13, 8, blockHelper.MUD_TILES_DECAY, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 11, 12, 9, blockHelper.MUD_TILES_DECAY, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 11, 10, blockHelper.MUD_TILES_DECAY, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 12, 11, blockHelper.MUD_TILES_DECAY, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 13, 12, blockHelper.MUD_TILES_DECAY, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 12, 13, blockHelper.MUD_TILES_DECAY, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 10, 13, blockHelper.MUD_TILES_DECAY, 2, 5, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 12, 14, blockHelper.MUD_TILES_DECAY, 1, 3, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 11, 14, blockHelper.MUD_TILES_DECAY, 2, 4, 1, facing);

		rotatedCubeVolume(world, rand, pos, 7, 4, 3, blockHelper.ROOT, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 4, 3, blockHelper.ROOT, 1, 4, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 7, 8, blockHelper.ROOT, 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 11, 8, 5, blockHelper.ROOT, 1, 3, 1, facing);

		rotatedCubeVolume(world, rand, pos, 7, 4, 7, blockHelper.getPillarsForLevel(rand, 7, 2), 1, 11, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 5, 7, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 5, 8, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);

		rotatedCubeVolume(world, rand, pos, 8, 7, 7, blockHelper.getStairsForLevel(rand, 7, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 7, 8, blockHelper.getStairsForLevel(rand, 7, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 7, 7, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 7, 9, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 12, 6, 0, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 6, 12, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 12, 7, 0, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 7, 12, blockHelper.getMudBricksForLevel(rand, 7, 1), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 12, 8, 0, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 6, facing);
		rotatedCubeVolume(world, rand, pos, 1, 8, 12, blockHelper.getMudBricksForLevel(rand, 7, 2), 5, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 11, 9, 0, blockHelper.getMudBricksForLevel(rand, 7, 1), 2, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 12, 9, 3, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 1, 9, 11, blockHelper.getMudBricksForLevel(rand, 7, 1), 2, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 3, 9, 12, blockHelper.getMudBricksForLevel(rand, 7, 1), 4, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 10, 9, 0, blockHelper.getStairsForLevel(rand, 7, facing.rotateYCCW(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 10, 9, 3, blockHelper.getStairsForLevel(rand, 7, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 11, 9, 3, blockHelper.getStairsForLevel(rand, 7, facing.rotateYCCW(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 11, 9, 4, blockHelper.getStairsForLevel(rand, 7, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 10, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 11, 10, 4, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 1, 9, 10, blockHelper.getStairsForLevel(rand, 7, facing, BlockStairsBetweenlands.EnumHalf.TOP), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 9, 10, blockHelper.getStairsForLevel(rand, 7, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 9, 11, blockHelper.getStairsForLevel(rand, 7, facing, BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 9, 11, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 10, 11, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 10, 10, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 3, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 12, 10, 4, blockHelper.getPillarsForLevel(rand, 7, 2), 1, 5, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 10, 12, blockHelper.getPillarsForLevel(rand, 7, 2), 1, 5, 1, facing);

		rotatedCubeVolume(world, rand, pos, 13, 10, 0, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 4, facing);
		rotatedCubeVolume(world, rand, pos, 13, 11, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 13, 14, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 13, 11, 2, blockHelper.getStairsForLevel(rand, 7, facing, BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 13, 14, 2, blockHelper.getStairsForLevel(rand, 7, facing, BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 13, 11, 3, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 4, 1, facing);

		rotatedCubeVolume(world, rand, pos, 1, 10, 13, blockHelper.getMudBricksForLevel(rand, 7, 1), 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 11, 13, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 14, 13, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 11, 13, blockHelper.getStairsForLevel(rand, 7, facing.rotateYCCW(), BlockStairsBetweenlands.EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 14, 13, blockHelper.getStairsForLevel(rand, 7, facing.rotateYCCW(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 11, 13, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 4, 1, facing);

		rotatedCubeVolume(world, rand, pos, 12, 10, 5, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 3, facing);
		rotatedCubeVolume(world, rand, pos, 11, 10, 8, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 10, 9, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 10, 10, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 10, 11, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 10, 12, blockHelper.getMudBricksForLevel(rand, 7, 1), 3, 2, 1, facing);

		rotatedCubeVolume(world, rand, pos, 12, 12, 5, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 11, 12, 8, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 12, 9, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 12, 10, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 12, 11, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 12, 12, blockHelper.getMudBricksForLevel(rand, 7, 2), 3, 2, 1, facing);

		rotatedCubeVolume(world, rand, pos, 12, 13, 5, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 3, facing);
		rotatedCubeVolume(world, rand, pos, 11, 13, 8, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 13, 9, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 13, 10, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 13, 11, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 5, 13, 12, blockHelper.getMudBricksForLevel(rand, 7, 1), 3, 2, 1, facing);

		rotatedCubeVolume(world, rand, pos, 8, 14, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 6, facing);
		for(int count = 0; count < 9; count++)
			rotatedCubeVolume(world, rand, pos, 9, 14, 0 + count, blockHelper.getRandomBeam(facing.getOpposite(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 14, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 7, facing);
		rotatedCubeVolume(world, rand, pos, 11, 14, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 12, 14, 1, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 1, 14, 7, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 14, 8, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 5, 1, 1, facing);
		for(int count = 0; count < 9; count++)
			rotatedCubeVolume(world, rand, pos, 1 + count, 14, 9, blockHelper.getRandomBeam(facing.rotateYCCW(), rand, level, 0, false), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 14, 10, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 6, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 14, 11, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 11, 12, 8, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 12, 9, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 12, 10, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 12, 11, blockHelper.getMudBricksForLevel(rand, 7, 2), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 11, 13, 8, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 10, 13, 9, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 13, 10, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 13, 11, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 2, 1, facing);

		rotatedCubeVolume(world, rand, pos, 1, 14, 1, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 2, 14, 0, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 10, 14, 7, blockHelper.getMudBricksForLevel(rand, 7, 1), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 8, 14, 7, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 14, 8, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 14, 10, blockHelper.getMudBricksForLevel(rand, 7, 1), 1, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 6, 14, 6, blockHelper.getStairsForLevel(rand, 7, facing, BlockStairsBetweenlands.EnumHalf.TOP), 3, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 14, 7, blockHelper.getStairsForLevel(rand, 7, facing.rotateYCCW(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 8, 13, 7, blockHelper.getStairsForLevel(rand, 7, facing.rotateY(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 9, 13, 7, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 2, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 11, 13, 7, blockHelper.getStairsForLevel(rand, 7, facing.rotateYCCW(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 13, 8, blockHelper.getStairsForLevel(rand, 7, facing.getOpposite(), BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 7, 13, 9, blockHelper.getMudSlabsForLevel(rand, 7, BlockSlabBetweenlands.EnumBlockHalfBL.TOP), 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 7, 13, 11, blockHelper.getStairsForLevel(rand, 7, facing, BlockStairsBetweenlands.EnumHalf.TOP), 1, 1, 1, facing);
		
		
		rotatedCubeVolume(world, rand, pos, 2, 1, 0, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing.rotateYCCW()).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 3, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 2, blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, facing).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 3, 1, 0, blockHelper.COMPACTED_MUD, 1, 1, 2, facing);
		rotatedCubeVolume(world, rand, pos, 1, 1, 3, blockHelper.COMPACTED_MUD, 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 1, 0, 0, blockHelper.COMPACTED_MUD, 1, 1, 2, facing);

		rotatedCubeVolume(world, rand, pos, 4, 2, 0, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK.withProperty(BlockDecayPitInvisibleFloorBlock.FACING, facing.rotateY()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 1, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_1.withProperty(BlockDecayPitInvisibleFloorBlockR1.FACING, facing.rotateY()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 4, 2, 2, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_2.withProperty(BlockDecayPitInvisibleFloorBlockR2.FACING, facing.rotateY()), 1, 1, 1, facing);

		if (facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH)
			rotatedCubeVolume(world, rand, pos, 3, 2, 3, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_DIAGONAL.withProperty(BlockDecayPitInvisibleFloorBlockDiagonal.FLIPPED, false), 1, 1, 1, facing);
		else
			rotatedCubeVolume(world, rand, pos, 3, 2, 3, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_DIAGONAL.withProperty(BlockDecayPitInvisibleFloorBlockDiagonal.FLIPPED, true), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 1, 2, 4, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_1.withProperty(BlockDecayPitInvisibleFloorBlockL1.FACING, facing.getOpposite()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 2, 4, blockHelper.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_2.withProperty(BlockDecayPitInvisibleFloorBlockL2.FACING, facing.getOpposite()), 1, 1, 1, facing);

		rotatedCubeVolume(world, rand, pos, 6, 4, 6, blockHelper.BRAZIER_BOTTOM, 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 6, 5, 6, blockHelper.BRAZIER_TOP, 1, 1, 1, facing); 
		rotatedCubeVolume(world, rand, pos, 6, 6, 6, Blocks.FIRE.getDefaultState(), 1, 1, 1, facing);
	}

	public void addSpikes(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		rotatedCubeVolume(world, rand, pos, -2, 7, 12, blockHelper.MUD_BRICKS_SPIKE_TRAP.withProperty(BlockMudBrickSpikeTrap.FACING, facing.getOpposite()), 5, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -2, 8, 12, blockHelper.MUD_BRICKS_SPIKE_TRAP.withProperty(BlockMudBrickSpikeTrap.FACING, facing.getOpposite()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, 2, 8, 12, blockHelper.MUD_BRICKS_SPIKE_TRAP.withProperty(BlockMudBrickSpikeTrap.FACING, facing.getOpposite()), 1, 1, 1, facing);
		rotatedCubeVolume(world, rand, pos, -1, 5, 11, blockHelper.MUD_TILES_SPIKE_TRAP, 3, 1, 1, facing);
	}

	@SuppressWarnings("incomplete-switch")
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
