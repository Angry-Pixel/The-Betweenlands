package thebetweenlands.common.world.gen.feature.structure.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockLootUrn.EnumLootUrn;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.common.block.misc.BlockMudFlowerPotCandle;
import thebetweenlands.common.block.plant.BlockHangingPlant;
import thebetweenlands.common.block.structure.BlockBrazier;
import thebetweenlands.common.block.structure.BlockBrazier.EnumBrazierHalf;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick.EnumCarvedMudBrickType;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.block.structure.BlockDungeonDoorCombination;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.block.structure.BlockDungeonWallCandle;
import thebetweenlands.common.block.structure.BlockMudBricksClimbable;
import thebetweenlands.common.block.structure.BlockMudTiles;
import thebetweenlands.common.block.structure.BlockMudTiles.EnumMudTileType;
import thebetweenlands.common.block.structure.BlockMudTilesWater;
import thebetweenlands.common.block.structure.BlockRottenBarkCarved;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.structure.BlockWoodenSupportBeam;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar.EnumWormPillarType;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntityLootUrn;

public class SludgeWormMazeBlockHelper {
	
	public IBlockState AIR = Blocks.AIR.getDefaultState();
	public IBlockState MOB_SPAWNER = BlockRegistry.MOB_SPAWNER.getDefaultState();

	//shrooms
	public IBlockState BLACK_HAT_MUSHROOM = BlockRegistry.BLACK_HAT_MUSHROOM.getDefaultState();
	public IBlockState FLAT_HEAD_MUSHROOM = BlockRegistry.FLAT_HEAD_MUSHROOM.getDefaultState();
	public IBlockState ROTBULB = BlockRegistry.ROTBULB.getDefaultState();

	//floor plants
	public IBlockState TALL_SLUDGECREEP = BlockRegistry.TALL_SLUDGECREEP.getDefaultState();
	public IBlockState PALE_GRASS = BlockRegistry.PALE_GRASS.getDefaultState();
	
	//wall plants
	public IBlockState MOSS = BlockRegistry.DEAD_MOSS.getDefaultState();
	public IBlockState LICHEN = BlockRegistry.DEAD_LICHEN.getDefaultState();
	
	//hanging plants
	public IBlockState CRYPTWEED = BlockRegistry.CRYPTWEED.getDefaultState().withProperty(BlockHangingPlant.CAN_GROW, false);
	public IBlockState STRING_ROOTS = BlockRegistry.STRING_ROOTS.getDefaultState().withProperty(BlockHangingPlant.CAN_GROW, false);

	public IBlockState STAGNANT_WATER = BlockRegistry.STAGNANT_WATER.getDefaultState();
	public IBlockState SPAWNER_TYPE_1 = Blocks.PRISMARINE.getDefaultState();
	public IBlockState SPAWNER_TYPE_2 = Blocks.PURPUR_BLOCK.getDefaultState();

	public IBlockState DUNGEON_WALL_CANDLE_NORTH = BlockRegistry.DUNGEON_WALL_CANDLE.getDefaultState().withProperty(BlockDungeonWallCandle.FACING, EnumFacing.NORTH);
	public IBlockState DUNGEON_WALL_CANDLE_EAST = BlockRegistry.DUNGEON_WALL_CANDLE.getDefaultState().withProperty(BlockDungeonWallCandle.FACING, EnumFacing.EAST);
	public IBlockState DUNGEON_WALL_CANDLE_SOUTH = BlockRegistry.DUNGEON_WALL_CANDLE.getDefaultState().withProperty(BlockDungeonWallCandle.FACING, EnumFacing.SOUTH);
	public IBlockState DUNGEON_WALL_CANDLE_WEST = BlockRegistry.DUNGEON_WALL_CANDLE.getDefaultState().withProperty(BlockDungeonWallCandle.FACING, EnumFacing.WEST);
	public IBlockState CHEST = BlockRegistry.WEEDWOOD_CHEST.getDefaultState();
	public IBlockState WORM_DUNGEON_PILLAR = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState();
	public IBlockState WORM_DUNGEON_PILLAR_TOP = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP);
	public IBlockState WORM_DUNGEON_PILLAR_DECAY_1 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_1);
	public IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_1 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_1);
	public IBlockState WORM_DUNGEON_PILLAR_DECAY_2 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_2);
	public IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_2 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_2);
	public IBlockState WORM_DUNGEON_PILLAR_DECAY_3 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_3);
	public IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_3 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_3);
	public IBlockState WORM_DUNGEON_PILLAR_DECAY_4 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_4);
	public IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_4 = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_4);
	public IBlockState WORM_DUNGEON_PILLAR_DECAY_FULL = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_FULL);
	public IBlockState WORM_DUNGEON_PILLAR_TOP_DECAY_FULL = BlockRegistry.WORM_DUNGEON_PILLAR.getDefaultState().withProperty(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_FULL);
	public IBlockState MUD_BRICKS_SPAWNER_HOLE = BlockRegistry.MUD_BRICK_SPAWNER_HOLE.getDefaultState();

	public IBlockState MUD_TILES = BlockRegistry.MUD_TILES.getDefaultState();
	public IBlockState MUD_TILES_DECAY = BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_DECAY);
	public IBlockState MUD_TILES_CRACKED = BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED);
	public IBlockState MUD_TILES_CRACKED_DECAY = BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED_DECAY);
	public IBlockState MUD_TILES_WATER = BlockRegistry.MUD_TILES_WATER.getDefaultState();

	public IBlockState MUD_BRICK_STAIRS = BlockRegistry.MUD_BRICK_STAIRS.getDefaultState();
	public IBlockState MUD_BRICK_STAIRS_DECAY_1 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_1.getDefaultState();
	public IBlockState MUD_BRICK_STAIRS_DECAY_2 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_2.getDefaultState();
	public IBlockState MUD_BRICK_STAIRS_DECAY_3 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_3.getDefaultState();

	public IBlockState MUD_BRICK_SLAB = BlockRegistry.MUD_BRICK_SLAB.getDefaultState();
	public IBlockState MUD_BRICK_SLAB_DECAY_1 = BlockRegistry.MUD_BRICK_SLAB_DECAY_1.getDefaultState();
	public IBlockState MUD_BRICK_SLAB_DECAY_2 = BlockRegistry.MUD_BRICK_SLAB_DECAY_2.getDefaultState();
	public IBlockState MUD_BRICK_SLAB_DECAY_3 = BlockRegistry.MUD_BRICK_SLAB_DECAY_3.getDefaultState();

	public IBlockState MUD_BRICKS = BlockRegistry.MUD_BRICKS.getDefaultState();
	public IBlockState MUD_BRICKS_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_1);
	public IBlockState MUD_BRICKS_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_2);
	public IBlockState MUD_BRICKS_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_3);
	public IBlockState MUD_BRICKS_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_4);
	public IBlockState MUD_BRICKS_CARVED = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED);
	public IBlockState MUD_BRICKS_CARVED_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_1);
	public IBlockState MUD_BRICKS_CARVED_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_2);
	public IBlockState MUD_BRICKS_CARVED_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_3);
	public IBlockState MUD_BRICKS_CARVED_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_4);
	public IBlockState MUD_BRICKS_CARVED_EDGE = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE);
	public IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_1);
	public IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_2);
	public IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_3);
	public IBlockState MUD_BRICKS_CARVED_EDGE_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_4);
	public IBlockState MUD_BRICKS_SPIKE_TRAP = BlockRegistry.MUD_BRICK_SPIKE_TRAP.getDefaultState();
	public IBlockState MUD_TILES_SPIKE_TRAP = BlockRegistry.MUD_TILES_SPIKE_TRAP.getDefaultState();
	
	public IBlockState MUD = BlockRegistry.MUD.getDefaultState();
	public IBlockState COMPACTED_MUD = BlockRegistry.COMPACTED_MUD.getDefaultState();
	public IBlockState COMPACTED_MUD_SLOPE = BlockRegistry.COMPACTED_MUD_SLOPE.getDefaultState();
	public IBlockState PUFFSHROOM = BlockRegistry.PUFFSHROOM.getDefaultState();
	public IBlockState ROTTEN_BARK = BlockRegistry.LOG_ROTTEN_BARK.getDefaultState().withProperty(BlockLogBetweenlands.LOG_AXIS, EnumAxis.NONE);

	public IBlockState ROOT = BlockRegistry.ROOT.getDefaultState();

	public IBlockState DUNGEON_DOOR_COMBINATION_EAST = BlockRegistry.DUNGEON_DOOR_COMBINATION.getDefaultState().withProperty(BlockDungeonDoorCombination.FACING, EnumFacing.EAST);
	public IBlockState DUNGEON_DOOR_COMBINATION_WEST = BlockRegistry.DUNGEON_DOOR_COMBINATION.getDefaultState().withProperty(BlockDungeonDoorCombination.FACING, EnumFacing.WEST);

	public IBlockState DUNGEON_DOOR_EAST = BlockRegistry.DUNGEON_DOOR_RUNES.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.EAST);
	public IBlockState DUNGEON_DOOR_WEST = BlockRegistry.DUNGEON_DOOR_RUNES.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.WEST);

	public IBlockState DUNGEON_DOOR_NORTH = BlockRegistry.DUNGEON_DOOR_RUNES.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.NORTH);
	public IBlockState DUNGEON_DOOR_SOUTH = BlockRegistry.DUNGEON_DOOR_RUNES.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.SOUTH);

	public IBlockState DUNGEON_DOOR_MIMIC_EAST = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.EAST);
	public IBlockState DUNGEON_DOOR_MIMIC_WEST = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.WEST);

	public IBlockState DUNGEON_DOOR_MIMIC_NORTH = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.NORTH);
	public IBlockState DUNGEON_DOOR_MIMIC_SOUTH = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.getDefaultState().withProperty(BlockDungeonDoorRunes.FACING, EnumFacing.SOUTH);
	
	private IBlockState LOOT_URN_1 = BlockRegistry.LOOT_URN.getDefaultState().withProperty(BlockLootUrn.VARIANT, EnumLootUrn.URN_1);
	private IBlockState LOOT_URN_2 = BlockRegistry.LOOT_URN.getDefaultState().withProperty(BlockLootUrn.VARIANT, EnumLootUrn.URN_2);
	private IBlockState LOOT_URN_3 = BlockRegistry.LOOT_URN.getDefaultState().withProperty(BlockLootUrn.VARIANT, EnumLootUrn.URN_3);

	public IBlockState MUD_BRICKS_ALCOVE_NORTH = BlockRegistry.MUD_BRICK_ALCOVE.getDefaultState().withProperty(BlockMudBrickAlcove.FACING, EnumFacing.NORTH);
	public IBlockState MUD_BRICKS_ALCOVE_EAST = BlockRegistry.MUD_BRICK_ALCOVE.getDefaultState().withProperty(BlockMudBrickAlcove.FACING, EnumFacing.EAST);
	public IBlockState MUD_BRICKS_ALCOVE_SOUTH = BlockRegistry.MUD_BRICK_ALCOVE.getDefaultState().withProperty(BlockMudBrickAlcove.FACING, EnumFacing.SOUTH);
	public IBlockState MUD_BRICKS_ALCOVE_WEST = BlockRegistry.MUD_BRICK_ALCOVE.getDefaultState().withProperty(BlockMudBrickAlcove.FACING, EnumFacing.WEST);

	public IBlockState MUD_BRICKS_CLIMBABLE_NORTH = BlockRegistry.MUD_BRICKS_CLIMBABLE.getDefaultState().withProperty(BlockMudBricksClimbable.FACING, EnumFacing.NORTH);
	public IBlockState MUD_BRICKS_CLIMBABLE_EAST = BlockRegistry.MUD_BRICKS_CLIMBABLE.getDefaultState().withProperty(BlockMudBricksClimbable.FACING, EnumFacing.EAST);
	public IBlockState MUD_BRICKS_CLIMBABLE_SOUTH = BlockRegistry.MUD_BRICKS_CLIMBABLE.getDefaultState().withProperty(BlockMudBricksClimbable.FACING, EnumFacing.SOUTH);
	public IBlockState MUD_BRICKS_CLIMBABLE_WEST = BlockRegistry.MUD_BRICKS_CLIMBABLE.getDefaultState().withProperty(BlockMudBricksClimbable.FACING, EnumFacing.WEST);

	public IBlockState MUD_FLOWER_POT_CANDLE_LIT = BlockRegistry.MUD_FLOWER_POT_CANDLE.getDefaultState().withProperty(BlockMudFlowerPotCandle.LIT, true);
	public IBlockState MUD_FLOWER_POT_CANDLE_UNLIT = BlockRegistry.MUD_FLOWER_POT_CANDLE.getDefaultState().withProperty(BlockMudFlowerPotCandle.LIT, false);

	public IBlockState MUD_BRICK_WALL = BlockRegistry.MUD_BRICK_WALL.getDefaultState();
	public IBlockState ITEM_SHELF = BlockRegistry.ITEM_SHELF.getDefaultState();
	public IBlockState WOODEN_SUPPORT_BEAM_ROTTEN_1 = BlockRegistry.WOODEN_SUPPORT_BEAM_ROTTEN_1.getDefaultState();
	public IBlockState WOODEN_SUPPORT_BEAM_ROTTEN_2 = BlockRegistry.WOODEN_SUPPORT_BEAM_ROTTEN_2.getDefaultState();
	public IBlockState WOODEN_SUPPORT_BEAM_ROTTEN_3 = BlockRegistry.WOODEN_SUPPORT_BEAM_ROTTEN_2.getDefaultState();

	public IBlockState LOG_ROTTEN_BARK = BlockRegistry.LOG_ROTTEN_BARK.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_1 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_1.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_2 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_2.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_3 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_3.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_4 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_4.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_5 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_5.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_6 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_6.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_7 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_7.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_8 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_8.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_9 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_9.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_10 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_10.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_11 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_11.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_12 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_12.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_13 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_13.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_14 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_14.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_15 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_15.getDefaultState();
	public IBlockState LOG_ROTTEN_BARK_CARVED_16 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_16.getDefaultState();

	public IBlockState ROTTEN_PLANKS = BlockRegistry.ROTTEN_PLANKS.getDefaultState();
	public IBlockState ROTTEN_PLANK_SLAB_UPPER = BlockRegistry.ROTTEN_PLANK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public IBlockState ROTTEN_PLANK_SLAB_LOWER = BlockRegistry.ROTTEN_PLANK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	//Tower
	public IBlockState BETWEENSTONE = BlockRegistry.BETWEENSTONE.getDefaultState();
	
	public IBlockState SMOOTH_BETWEENSTONE = BlockRegistry.SMOOTH_BETWEENSTONE.getDefaultState();
	public IBlockState SMOOTH_BETWEENSTONE_STAIRS = BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS.getDefaultState();
	public IBlockState SMOOTH_BETWEENSTONE_SLAB_UPPER = BlockRegistry.SMOOTH_BETWEENSTONE_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public IBlockState SMOOTH_BETWEENSTONE_SLAB_LOWER = BlockRegistry.SMOOTH_BETWEENSTONE_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	
	public IBlockState BETWEENSTONE_BRICKS = BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState();
	public IBlockState BETWEENSTONE_BRICK_STAIRS = BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState();
	public IBlockState BETWEENSTONE_BRICK_SLAB_UPPER = BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public IBlockState BETWEENSTONE_BRICK_SLAB_LOWER = BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	public IBlockState BETWEENSTONE_PILLAR = BlockRegistry.BETWEENSTONE_PILLAR.getDefaultState();
	public IBlockState BETWEENSTONE_TILES = BlockRegistry.BETWEENSTONE_TILES.getDefaultState();

	public IBlockState SMOOTH_PITSTONE = BlockRegistry.SMOOTH_PITSTONE.getDefaultState();
	public IBlockState SMOOTH_PITSTONE_STAIRS = BlockRegistry.SMOOTH_PITSTONE_STAIRS.getDefaultState();
	public IBlockState SMOOTH_PITSTONE_SLAB_UPPER = BlockRegistry.SMOOTH_PITSTONE_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public IBlockState SMOOTH_PITSTONE_SLAB_LOWER = BlockRegistry.SMOOTH_PITSTONE_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);

	public IBlockState PITSTONE_BRICKS = BlockRegistry.PITSTONE_BRICKS.getDefaultState();
	public IBlockState PITSTONE_BRICK_STAIRS = BlockRegistry.PITSTONE_BRICK_STAIRS.getDefaultState();
	public IBlockState PITSTONE_BRICK_SLAB_UPPER = BlockRegistry.PITSTONE_BRICK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public IBlockState PITSTONE_BRICK_SLAB_LOWER = BlockRegistry.PITSTONE_BRICK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	public IBlockState PITSTONE_PILLAR = BlockRegistry.PITSTONE_PILLAR.getDefaultState();
	public IBlockState PITSTONE_TILES = BlockRegistry.PITSTONE_TILES.getDefaultState();

	public IBlockState PITSTONE_CHISELED = BlockRegistry.PITSTONE_CHISELED.getDefaultState();
	public IBlockState SCABYST_PITSTONE_DOTTED = BlockRegistry.SCABYST_PITSTONE_DOTTED.getDefaultState();
    public IBlockState SCABYST_PITSTONE_HORIZONTAL = BlockRegistry.SCABYST_PITSTONE_HORIZONTAL.getDefaultState();
    public IBlockState ENERGY_BARRIER_MUD = BlockRegistry.ENERGY_BARRIER_MUD.getDefaultState();
    public IBlockState DIAGONAL_ENERGY_BARRIER = BlockRegistry.DIAGONAL_ENERGY_BARRIER.getDefaultState();
    public IBlockState MUD_TOWER_BEAM_ORIGIN = BlockRegistry.MUD_TOWER_BEAM_ORIGIN.getDefaultState();
    public IBlockState MUD_TOWER_BEAM_RELAY = BlockRegistry.MUD_TOWER_BEAM_RELAY.getDefaultState();
    public IBlockState MUD_TOWER_BEAM_TUBE = BlockRegistry.MUD_TOWER_BEAM_TUBE.getDefaultState();
    public IBlockState MUD_TOWER_BEAM_LENS_SUPPORTS = BlockRegistry.MUD_TOWER_BEAM_LENS_SUPPORTS.getDefaultState();
    public IBlockState BRAZIER_TOP = BlockRegistry.MUD_TOWER_BRAZIER.getDefaultState().withProperty(BlockBrazier.HALF, EnumBrazierHalf.UPPER);
    public IBlockState BRAZIER_BOTTOM = BlockRegistry.MUD_TOWER_BRAZIER.getDefaultState().withProperty(BlockBrazier.HALF, EnumBrazierHalf.LOWER);
    public IBlockState COMPACTED_MUD_MIRAGE = BlockRegistry.COMPACTED_MUD_MIRAGE.getDefaultState();
    public IBlockState COMPACTED_MUD_SLAB = BlockRegistry.COMPACTED_MUD_SLAB.getDefaultState();
    public IBlockState GROUND_ITEM = BlockRegistry.GROUND_ITEM.getDefaultState();

	public final Map<IBlockState, Boolean> STRUCTURE_BLOCKS = new HashMap<IBlockState, Boolean>();

	public SludgeWormMazeBlockHelper() {
		initStuctureBlockMap();
	}

	public @Nullable IBlockState getMudBricksForLevel(Random rand, int level, int layer) {
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
			if(layer == 1)
				return MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return MUD_BRICKS_CARVED_DECAY_4;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE_DECAY_4;
		}
		return MUD_BRICKS;
	}

	public @Nullable IBlockState getMudSlabsForLevel(Random rand, int level, EnumBlockHalfBL half) {
		IBlockState state = MUD_BRICK_SLAB;
		switch (level) {
		case 0:
			state = MUD_BRICK_SLAB;
			break;
		case 1:
			state = rand.nextBoolean() ? MUD_BRICK_SLAB : MUD_BRICK_SLAB_DECAY_1;
			break;
		case 2:
			state = MUD_BRICK_SLAB_DECAY_1;
			break;
		case 3:
			state = rand.nextBoolean() ? MUD_BRICK_SLAB_DECAY_1 : MUD_BRICK_SLAB_DECAY_2;
			break;
		case 4:
			state =  MUD_BRICK_SLAB_DECAY_2;
			break;
		case 5:
			state = rand.nextBoolean() ? MUD_BRICK_SLAB_DECAY_2 : MUD_BRICK_SLAB_DECAY_3;
			break;
		case 6:
		case 7:
			state = MUD_BRICK_SLAB_DECAY_3;
			break;
		}
		return state.withProperty(BlockSlabBetweenlands.HALF, half);
	}

	public @Nullable IBlockState getPillarsForLevel(Random rand, int level, int layer) {
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
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_FULL;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_4;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_FULL;
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
		return MUD_BRICKS;
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
		case 7:
			state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		}
		return state.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, half);
	}

	public IBlockState getRandomBeam(EnumFacing facing, Random rand, int level, int count, boolean randomiseLine) {
		IBlockState state = LOG_ROTTEN_BARK_CARVED_1;
		if(randomiseLine)
			count = rand.nextInt(6); // overrides fixed ends and middles with a random choice for multi-placed blocks

		if (count == 1 || count == 3) {
			int endType = rand.nextInt(6);
			switch (endType) {
			case 0:
				state = LOG_ROTTEN_BARK_CARVED_11;
				break;
			case 1:
				state = LOG_ROTTEN_BARK_CARVED_12;
				break;
			case 2:
				state = LOG_ROTTEN_BARK_CARVED_13;
				break;
			case 3:
				state = LOG_ROTTEN_BARK_CARVED_14;
				break;
			case 4:
				state = LOG_ROTTEN_BARK_CARVED_15;
				break;
			case 5:
				state = LOG_ROTTEN_BARK_CARVED_16;
				break;
			}
		} else {
			int midType = rand.nextInt(10);
			switch (midType) {
			case 0:
				state = LOG_ROTTEN_BARK_CARVED_1;
				break;
			case 1:
				state = LOG_ROTTEN_BARK_CARVED_2;
				break;
			case 2:
				state = LOG_ROTTEN_BARK_CARVED_3;
				break;
			case 3:
				state = LOG_ROTTEN_BARK_CARVED_4;
				break;
			case 4:
				state = LOG_ROTTEN_BARK_CARVED_5;
				break;
			case 5:
				state = LOG_ROTTEN_BARK_CARVED_6;
				break;
			case 6:
				state = LOG_ROTTEN_BARK_CARVED_7;
				break;
			case 7:
				state = LOG_ROTTEN_BARK_CARVED_8;
				break;
			case 8:
				state = LOG_ROTTEN_BARK_CARVED_9;
				break;
			case 9:
				state = LOG_ROTTEN_BARK_CARVED_10;
				break;
			}
		}
		return state.withProperty(BlockRottenBarkCarved.FACING, facing);
	}

	public IBlockState getRandomSupportBeam(EnumFacing facing, boolean isTop, Random rand) {
		IBlockState state = WOODEN_SUPPORT_BEAM_ROTTEN_1;
		int type = rand.nextInt(3);
		if(type == 0)
			state = WOODEN_SUPPORT_BEAM_ROTTEN_1;
		if(type == 1)
			state = WOODEN_SUPPORT_BEAM_ROTTEN_2;
		if(type == 2)
			state = WOODEN_SUPPORT_BEAM_ROTTEN_3;
		return state.withProperty(BlockWoodenSupportBeam.FACING, facing).withProperty(BlockWoodenSupportBeam.TOP, isTop);
	}

	public IBlockState getRandomLitCandle(Random rand) {
		//return rand.nextBoolean() ? MUD_FLOWER_POT_CANDLE_UNLIT : MUD_FLOWER_POT_CANDLE_LIT;
		return MUD_FLOWER_POT_CANDLE_UNLIT; // lighting updates kill atm
	}

	public IBlockState getRandomMushroom(Random rand) {
		int type = rand.nextInt(30);
		if (type < 10)
			return FLAT_HEAD_MUSHROOM;
		else if (type < 20)
			return BLACK_HAT_MUSHROOM;
		else
			return ROTBULB;
	}

	public IBlockState getRandomFloorPlant(Random rand) {
		return rand.nextBoolean() ? TALL_SLUDGECREEP : PALE_GRASS;
	}

	public IBlockState getRandomHangingPlant(Random rand) {
		return rand.nextBoolean() ? CRYPTWEED : STRING_ROOTS;
	}

	public IBlockState getMudTilesWater(Random rand) {
		int randDirection = rand.nextInt(4);
		IBlockState state = MUD_TILES_WATER;
		switch (randDirection) {
		case 0:
			state = MUD_TILES_WATER.withProperty(BlockMudTilesWater.FACING, EnumFacing.NORTH);
			break;
		case 1:
			state = MUD_TILES_WATER.withProperty(BlockMudTilesWater.FACING, EnumFacing.SOUTH);
			break;
		case 2:
			state = MUD_TILES_WATER.withProperty(BlockMudTilesWater.FACING, EnumFacing.WEST);
			break;
		case 3:
			state = MUD_TILES_WATER.withProperty(BlockMudTilesWater.FACING, EnumFacing.EAST);
			break;
		}
		return state;
	}

	public void setRandomRoot(World world, BlockPos pos, Random rand) {
			if (!isSolidStructureBlock(world.getBlockState(pos)) && world.getBlockState(pos.down()).getBlock() instanceof BlockMudTiles) {
				int rnd = rand.nextInt(32);
				if (rnd < 8) {
					world.setBlockState(pos, ROOT, 2);
				} else if (rnd < 16) {
					world.setBlockState(pos, ROOT, 2);
					if (world.isAirBlock(pos.up(1)))
						world.setBlockState(pos.up(1), ROOT, 2);
				} else if (rnd < 24) {
					world.setBlockState(pos, ROOT, 2);
					if (world.isAirBlock(pos.up(1)) && world.isAirBlock(pos.up(2))) {
						world.setBlockState(pos.up(1), ROOT, 2);
						world.setBlockState(pos.up(2), ROOT, 2);
					}
				} else {

					world.setBlockState(pos, ROOT, 2);
					if (world.isAirBlock(pos.up(1)) && world.isAirBlock(pos.up(2)) && world.isAirBlock(pos.up(3))) {
						world.setBlockState(pos.up(1), ROOT, 2);
						world.setBlockState(pos.up(2), ROOT, 2);
						world.setBlockState(pos.up(3), ROOT, 2);
					}
				}
			}
	}

	public IBlockState getRandomLootUrn(Random rand, EnumFacing facing) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return LOOT_URN_1.withProperty(BlockLootUrn.FACING, facing);
		case 1:
			return LOOT_URN_2.withProperty(BlockLootUrn.FACING, facing);
		case 2:
			return LOOT_URN_3.withProperty(BlockLootUrn.FACING, facing);
		}
		return LOOT_URN_1.withProperty(BlockLootUrn.FACING, facing);
	}

	public void setLootUrnTileProperties(World world, Random rand, BlockPos pos) {
		TileEntityLootUrn lootUrn = BlockLootUrn.getTileEntity(world, pos);
		if (lootUrn != null) {
			// TODO Make proper shared loot tables
			lootUrn.setLootTable(LootTableRegistry.DUNGEON_POT_LOOT, rand.nextLong());
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}

	public void setGreatSword(World world, Random rand, BlockPos pos) {
		TileEntityGroundItem groundItem = (TileEntityGroundItem) world.getTileEntity(pos);
		if (groundItem != null) {
			groundItem.setStack(new ItemStack(ItemRegistry.ANCIENT_GREATSWORD));
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}

	/// TOWER STUFF

	// TODO improve for more types
	public @Nullable IBlockState getStairsForTowerLevel(Random rand, int level, EnumFacing facing, EnumHalf half, boolean bricks) {
		IBlockState state = SMOOTH_PITSTONE_STAIRS;
		if(bricks)
			state = PITSTONE_BRICK_STAIRS;
		return state.withProperty(BlockStairsBetweenlands.FACING, facing).withProperty(BlockStairsBetweenlands.HALF, half);
	}
	
	public @Nullable IBlockState getEnergyBarrier(boolean flipped) {
		return DIAGONAL_ENERGY_BARRIER.withProperty(BlockDiagonalEnergyBarrier.FLIPPED, flipped);
	}

	public boolean isSolidStructureBlock(IBlockState state) {
		return STRUCTURE_BLOCKS.get(state) != null;
	}

	private void initStuctureBlockMap() {
		if (STRUCTURE_BLOCKS.isEmpty()) {
			STRUCTURE_BLOCKS.put(COMPACTED_MUD, true);
			STRUCTURE_BLOCKS.put(ROTTEN_BARK, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_3, true);
			STRUCTURE_BLOCKS.put(COMPACTED_MUD, true);
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
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_COMBINATION_EAST, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_COMBINATION_WEST, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_EAST, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_WEST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_NORTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_EAST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_SOUTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_WEST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_NORTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_EAST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_SOUTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_WEST, true);
			STRUCTURE_BLOCKS.put(MUD_FLOWER_POT_CANDLE_LIT, true);
			STRUCTURE_BLOCKS.put(MUD_FLOWER_POT_CANDLE_UNLIT, true);
			STRUCTURE_BLOCKS.put(LOOT_URN_1, true);
			STRUCTURE_BLOCKS.put(LOOT_URN_2, true);
			STRUCTURE_BLOCKS.put(LOOT_URN_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_WALL, true);
			STRUCTURE_BLOCKS.put(ITEM_SHELF, true);
			STRUCTURE_BLOCKS.put(WOODEN_SUPPORT_BEAM_ROTTEN_1, true);
			STRUCTURE_BLOCKS.put(WOODEN_SUPPORT_BEAM_ROTTEN_2, true);
			STRUCTURE_BLOCKS.put(WOODEN_SUPPORT_BEAM_ROTTEN_3, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_1, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_2, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_3, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_4, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_5, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_6, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_7, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_8, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_9, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_10, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_11, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_12, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_13, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_14, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_15, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_16, true);
			STRUCTURE_BLOCKS.put(CHEST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_SPIKE_TRAP, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_SPIKE_TRAP, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_SPAWNER_HOLE, true);
		}
	}
}