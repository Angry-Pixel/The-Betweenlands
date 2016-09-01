package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.loot.LootTables;
import thebetweenlands.common.world.gen.feature.loot.LootUtil;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationStorage;

public class WorldGenSmallRuins extends WorldGenerator {
	//private static final int[] stairSequence = new int[]{0, 3, 1, 2};
	private static final IBlockState[] betweenstoneBrickStairSequence = new IBlockState[]{
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH)
	};
	//private static final int[] upsideDownStairSequence = new int[]{4, 7, 5, 6};
	private static final IBlockState[] betweenstoneBrickStairSequenceUpsideDown = new IBlockState[]{
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP)
	};
	private static final IBlockState[] weedwoodPlankStairSequenceUpsideDown = new IBlockState[]{
			BlockRegistry.WEEDWOOD_PLANK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP),
			BlockRegistry.WEEDWOOD_PLANK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP),
			BlockRegistry.WEEDWOOD_PLANK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP),
			BlockRegistry.WEEDWOOD_PLANK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP)
	};
	//private static final int[] logSequence = new int[]{4, 8};
	private static final IBlockState[] logSequence = new IBlockState[]{
			BlockRegistry.LOG_WEEDWOOD.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X),
			BlockRegistry.LOG_WEEDWOOD.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z)
	};
	private static final IBlockState angryBetweenstone = BlockRegistry.ANGRY_BETWEENSTONE.getDefaultState();
	private static final IBlockState betweenstoneTiles = BlockRegistry.BETWEENSTONE_TILES.getDefaultState();
	private static final IBlockState betweenstoneBricks = BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState();
	//private static final IBlockState betweenstoneBrickStairs = BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState();
	private static final IBlockState betweenstoneBrickSlab = BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState();
	private static final IBlockState chiseledBetweenstone = BlockRegistry.BETWEENSTONE_CHISELED.getDefaultState();
	private static final IBlockState betweenstonePillar = BlockRegistry.BETWEENSTONE_PILLAR.getDefaultState();
	private static final IBlockState smoothBetweenstoneWall = BlockRegistry.SMOOTH_BETWEENSTONE_WALL.getDefaultState();
	//private static final IBlockState weedwoodLog = BlockRegistry.LOG_WEEDWOOD.getDefaultState();
	//private static final IBlockState weedwoodPlankStairs = BlockRegistry.WEEDWOOD_PLANK_STAIRS.getDefaultState();
	private static final IBlockState weedwoodPlankSlab = BlockRegistry.WEEDWOOD_PLANK_SLAB.getDefaultState();
	private static final IBlockState betweenstoneBrickWall = BlockRegistry.BETWEENSTONE_BRICK_WALL.getDefaultState();

	private int width = -1;
	private int depth = -1;

	@Override
	public boolean generate(World world, Random random, BlockPos position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		List<RuinLocation> ruinLocations = new ArrayList<>();
		List<LocationStorage> generatedLocations = new ArrayList<>();
		int attempts = 40;
		while (attempts >= 0) {
			x = x + random.nextInt(16) - 8;
			z = z + random.nextInt(16) - 8;
			y = y + random.nextInt(8) - 3;
			int randomInt = random.nextInt(7);
			switch (randomInt) {
			case 0:
				if (structure1(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			case 1:
				if (structure2(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			case 2:
				if (structure3(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			case 3:
				if (structure4(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			case 4:
				if (structure5(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			case 5:
				if (structure6(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			case 6:
				if (structure7(world, random, x, y, z, false, generatedLocations))
					ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
				break;
			}
			attempts--;
		}

		if (ruinLocations.size() >= 3) {
			for (RuinLocation location : ruinLocations) {
				switch (location.structureID) {
				case 0:
					structure1(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				case 1:
					structure2(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				case 2:
					structure3(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				case 3:
					structure4(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				case 4:
					structure5(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				case 5:
					structure6(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				case 6:
					structure7(location.world, location.random, location.x, location.y, location.z, true, generatedLocations);
					break;
				}
			}
			for(LocationStorage location : generatedLocations) {
				location.setSeed(random.nextLong());
			}
			return true;
		} else
			return false;
	}

	private boolean structure1(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		int height = 9 + random.nextInt(2);
		width = 8;
		depth = 1;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, height, depth, direction, generatedLocations);

			rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 1, height - 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 1, height - 5, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 5, 0, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 5, 0, betweenstoneBricks, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, height - 5, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 0, height - 4, 0, betweenstoneBrickSlab, 2, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 3, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, height - 3, 0, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, height - 3, 0, betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, height - 3, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, height - 2, 0, betweenstoneBrickSlab, 3, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, height - 1, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, height - 1, 0, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);

			if (random.nextInt(5) == 0)
				rotatedLoot(world, random, x, y, z, 4, height - 2, 0, direction);
		}
		return true;
	}

	private boolean structure2(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		int height = 13 + random.nextInt(2);
		width = 7;
		depth = 1;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
			return false;
		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, height, depth, direction, generatedLocations);

			rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 1, height - 9, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 1, height - 9, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 9, 0, betweenstoneBricks, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 9, 0, betweenstoneBricks, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 8, 0, chiseledBetweenstone, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 8, 0, chiseledBetweenstone, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 7, 0, betweenstoneBricks, 1, 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 7, 0, betweenstoneBricks, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, height - 6, 0, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, height - 6, 0, betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, height - 6, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 6, 0, getMetaFromDirection(2, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, height - 5, 0, betweenstoneBrickSlab, 3, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 2, 0, getMetaFromDirection(2, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, height - 2, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, betweenstoneBrickSlab, 2, 1, 1, direction);

			if (random.nextInt(5) == 0) {
				rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, betweenstoneBricks, 1, 1, 1, direction);
				rotatedLoot(world, random, x, y, z, 0, height, 0, direction);
			}
		}
		return true;
	}

	private boolean structure3(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		width = 7;
		depth = 5;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 7, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 2, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED))
			return false;
		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, 7, depth, direction, generatedLocations);

			rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 0, 4, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 4, betweenstoneTiles, 1, 4, 1, direction);

			rotatedCubeVolume(world, x, y, z, 1, 4, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, 4, 4, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 0, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 0, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 4, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 4, betweenstoneBricks, 1, 2, 1, direction);

			rotatedCubeVolume(world, x, y, z, 0, 5, 0, betweenstoneBrickSlab, 2, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, 5, 0, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 5, 4, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 5, 0, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 5, 4, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 5, 1, getMetaFromDirection(1, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 5, 1, getMetaFromDirection(1, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 5, 3, getMetaFromDirection(3, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 5, 3, getMetaFromDirection(3, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, 6, 0, betweenstoneBrickSlab, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 6, 4, betweenstoneBrickSlab, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 6, 1, betweenstoneBrickSlab, 1, 1, 3, direction);
			rotatedCubeVolume(world, x, y, z, 6, 6, 1, betweenstoneBrickSlab, 1, 1, 3, direction);
			if (random.nextInt(5) == 0)
				rotatedLoot(world, random, x, y, z, 2, 6, 0, direction);
			if (random.nextInt(5) == 0)
				rotatedLoot(world, random, x, y, z, 6, 6, 0, direction);
			if (random.nextInt(5) == 0)
				rotatedLoot(world, random, x, y, z, 2, 6, 4, direction);
			if (random.nextInt(5) == 0)
				rotatedLoot(world, random, x, y, z, 6, 6, 4, direction);
		}
		return true;
	}

	//TODO switch this to the new system at some point....
	private boolean structure4(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		int height = 9 + random.nextInt(2);
		int width = 6;
		for (int zz = z; zz < z + width; zz++)
			for (int yy = y; yy < y + height; yy++)
				for (int xx = x; xx > x - width; xx--)
					if (!(world.getBlockState(new BlockPos(xx, yy, zz)).getBlock() == Blocks.AIR || (world.getBlockState(new BlockPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_WATER && yy < y + height - 2)))
						return false;

		if (!SurfaceType.MIXED.matches(world.getBlockState(new BlockPos(x - width, y - 1, z + 1))) || !SurfaceType.MIXED.matches(world.getBlockState(new BlockPos(x - width, y - 1, z + width - 1))) || !SurfaceType.MIXED.matches(world.getBlockState(new BlockPos(x - 1, y - 1, z + width))) || !SurfaceType.MIXED.matches(world.getBlockState(new BlockPos(x - 1, y - 1, z + width))))
			return false;
		if (doGen) {
			//TODO: World locations
			//generatedLocations.addAll(StorageHelper.addArea(world, "translate:ruins", AxisAlignedBB.getBoundingBox(x - width, y, z, x, y + height, z + width).expand(6, 6, 6), EnumLocationType.RUINS, 0));

			for (int yy = y; yy < y + height; yy++) {
				if (yy <= y + height - 5) {
					if (yy == y)
						world.setBlockState(new BlockPos(x - width, yy, z + 1), betweenstoneTiles);
					else
						world.setBlockState(new BlockPos(x - width, yy, z + 1), betweenstoneBrickWall);
					world.setBlockState(new BlockPos(x - width + 1, yy, z + 1), betweenstoneTiles);
				} else if (yy == y + height - 4) {
					world.setBlockState(new BlockPos(x - width, yy, z + 1), betweenstoneBrickWall);
					world.setBlockState(new BlockPos(x - width + 1, yy, z + 1), betweenstoneBricks);
				} else if (yy == y + height - 3) {
					//Fuck you stairs
					//world.setBlockState(new BlockPos(x - width + 1, yy, z + 1), betweenstoneBrickStairs, 2, 3);
					//world.setBlockState(new BlockPos(x - width + 1, yy, z + 2), betweenstoneBrickStairs, 7, 3);
					world.setBlockState(new BlockPos(x - width + 1, yy + 1, z + 2), betweenstoneBrickSlab);
					int zz;
					for (zz = z + 3; zz <= z + width - 3; zz++) {
						world.setBlockState(new BlockPos(x - width + 1, yy + 1, zz), betweenstoneBrickSlab);
						world.setBlockState(new BlockPos(x - width + 1, yy, zz), betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 3);
					}
					//you too
					//world.setBlockState(new BlockPos(x - width + 1, yy, zz), betweenstoneBrickStairs, 6, 3);
					world.setBlockState(new BlockPos(x - width + 1, yy + 1, zz), betweenstoneBrickSlab);
				}
			}

			for (int yy = y; yy < y + height; yy++) {
				if (yy <= y + height - 5) {
					if (yy == y) {
						world.setBlockState(new BlockPos(x - width, yy, z + width - 1), betweenstoneTiles);
						world.setBlockState(new BlockPos(x - width + 1, yy, z + width), betweenstoneTiles);
					} else {
						world.setBlockState(new BlockPos(x - width, yy, z + width - 1), betweenstoneBrickWall);
						world.setBlockState(new BlockPos(x - width + 1, yy, z + width), betweenstoneBrickWall);
					}
					world.setBlockState(new BlockPos(x - width + 1, yy, z + width - 1), betweenstoneTiles);
				} else if (yy <= y + height - 2) {
					if (yy == y + height - 4) {
						world.setBlockState(new BlockPos(x - width, yy, z + width - 1), betweenstoneBrickWall);
						world.setBlockState(new BlockPos(x - width + 1, yy, z + width), betweenstoneBrickWall);
					}
					world.setBlockState(new BlockPos(x - width + 1, yy, z + width - 1), betweenstoneBricks);
				} else if (yy <= y + height - 1) {
					//<.<
					//world.setBlockState(new BlockPos(x - width + 1, yy, z + width - 1), betweenstoneBrickStairs);
					//world.setBlockState(new BlockPos(x - width + 2, yy, z + width - 1), betweenstoneBrickStairs, 5, 3);
					world.setBlockState(new BlockPos(x - width + 2, yy + 1, z + width - 1), betweenstoneBrickSlab);
					int xx;
					for (xx = x - width + 3; xx <= x - 3; xx++) {
						world.setBlockState(new BlockPos(xx, yy, z + width - 1), betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 3);
						if (random.nextInt(8) == 0) {
							generateLoot(world, random, xx, yy + 1, z + width - 1);
						} else
							world.setBlockState(new BlockPos(xx, yy + 1, z + width - 1), betweenstoneBrickSlab);
					}
					//>.>
					//world.setBlockState(new BlockPos(xx, yy, z + width - 1), betweenstoneBrickStairs, 4, 3);
					world.setBlockState(new BlockPos(xx, yy + 1, z + width - 1), betweenstoneBrickSlab);
				}
			}

			for (int yy = y; yy < y + height; yy++) {
				if (yy <= y + height - 5) {
					if (yy == y) {
						world.setBlockState(new BlockPos(x - 1, yy, z + width), betweenstoneTiles);
					} else {
						world.setBlockState(new BlockPos(x - 1, yy, z + width), betweenstoneBrickWall);
					}
					world.setBlockState(new BlockPos(x - 1, yy, z + width - 1), betweenstoneTiles);
				} else if (yy <= y + height - 2) {
					if (yy == y + height - 4) {
						world.setBlockState(new BlockPos(x - 1, yy, z + width), betweenstoneBrickWall);
					}
					if (yy == y + height - 3) {
						//>.<
						//world.setBlockState(new BlockPos(x, yy, z + width - 1), betweenstoneBrickStairs, 5, 3);
					}
					world.setBlockState(new BlockPos(x - 1, yy, z + width - 1), betweenstoneBricks);
				} else if (yy == y + height - 1) {
					//.-.
					//world.setBlockState(new BlockPos(x - 1, yy, z + width - 1), betweenstoneBrickStairs, 1, 3);
				}
			}
		}
		return true;
	}

	private boolean structure5(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		int height = 5 + random.nextInt(2);
		width = 1;
		depth = 1;
		int direction = 0;
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
			return false;
		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, height, depth, direction, generatedLocations);

			rotatedCubeVolume(world, x, y, z, 0, 0, 0, chiseledBetweenstone, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, 1, 0, betweenstonePillar, 1, height - 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, chiseledBetweenstone, 1, 1, 1, direction);
			if (random.nextInt(5) == 0) {
				rotatedLoot(world, random, x, y, z, 0, height, 0, direction);
			}
		}
		return true;
	}

	private boolean structure6(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		width = 5 + random.nextInt(2);
		depth = 1;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 1, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, width - 1, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
			return false;
		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, 1, depth, direction, generatedLocations);

			rotatedCubeVolume(world, x, y, z, 0, 0, 0, chiseledBetweenstone, 1, 1, 1, direction);
			//Who needs pillars? I don't
			//rotatedCubeVolume(world, x, y, z, 1, 0, 0, betweenstonePillar, direction == 0 || direction == 2 ? 7 : 8, width - 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, width - 1, 0, 0, chiseledBetweenstone, 1, 1, 1, direction);

			if (random.nextInt(5) == 0) {
				rotatedLoot(world, random, x, y, z, 0, 1, 0, direction);
			}
			if (random.nextInt(5) == 0) {
				rotatedLoot(world, random, x, y, z, width - 1, 1, 0, direction);
			}
		}
		return true;
	}

	private boolean structure7(World world, Random random, int x, int y, int z, boolean doGen, List<LocationStorage> generatedLocations) {
		width = 12;
		depth = 12;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 13, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED))
			return false;
		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, 13, depth, direction, generatedLocations);

			rotatedCubeVolume(world, x, y, z, 0, 0, 0, betweenstoneBricks, 1, 1, 4, direction);
			rotatedCubeVolume(world, x, y, z, 0, 1, 0, betweenstoneBricks, 1, 1, 3, direction);
			rotatedCubeVolume(world, x, y, z, 0, 1, 3, getMetaFromDirection(3, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, 2, 0, getMetaFromDirection(1, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, 2, 1, betweenstoneBricks, 1, 1, 2, direction);
			rotatedCubeVolume(world, x, y, z, 0, 2, 3, betweenstoneBrickSlab, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 0, 1, betweenstoneTiles, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 1, 1, smoothBetweenstoneWall, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 1, betweenstoneTiles, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 1, 1, smoothBetweenstoneWall, 1, 4, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 0, 2, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 2, betweenstoneBricks, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 2, getMetaFromDirection(3, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 0, 2, betweenstoneBricks, 1, 7, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 7, 2, getMetaFromDirection(2, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 0, 2, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 2, 2, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 3, 2, betweenstoneBricks, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 0, 2, betweenstoneBricks, 1, 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 5, 2, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 2, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 4, 2, getMetaFromDirection(1, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 0, 2, betweenstoneTiles, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 1, 2, smoothBetweenstoneWall, 1, 3, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 5, 3, getMetaFromDirection(1, direction, weedwoodPlankStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 3, getMetaFromDirection(1, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 3, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 3, weedwoodPlankSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 3, betweenstoneBricks, 1, 6, 3, direction);
			rotatedCubeVolume(world, x, y, z, 10, 3, 3, getMetaFromDirection(1, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 3, betweenstoneBrickSlab, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 5, 4, weedwoodPlankSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 4, betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 4, betweenstoneBrickSlab, 1, 1, 1, direction);
			//Stupid rope
			//rotatedCubeVolume(world, x, y, z, 7, 3, 4, BLBlockRegistry.rope, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 4, getMetaFromDirection(0, direction, logSequence), 5, 1, 1, direction);
			//Nuh uh
			//rotatedCubeVolume(world, x, y, z, 9, 4, 4, BlockRegistry.WEEDWOOD_PLANK_FENCE.getDefaultState(), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 2, 4, chiseledBetweenstone, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 4, betweenstoneBricks, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 5, 5, getMetaFromDirection(3, direction, weedwoodPlankStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 5, getMetaFromDirection(3, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 5, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 5, weedwoodPlankSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 5, getMetaFromDirection(3, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, 0, 6, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 6, betweenstoneBricks, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 8, 6, getMetaFromDirection(0, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 8, 6, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 9, 6, betweenstoneBrickSlab, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, 8, 6, betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 8, 6, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 6, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 6, betweenstoneBricks, 1, 6, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 10, 6, getMetaFromDirection(1, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 6, weedwoodPlankSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 0, 6, betweenstoneBricks, 1, 1, 1, direction);
			rotatedLoot(world, random, x, y, z, 9, 1, 6, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 6, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 4, 6, betweenstoneBricks, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 5, 6, getMetaFromDirection(1, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 0, 6, betweenstoneTiles, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 1, 6, smoothBetweenstoneWall, 1, 4, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 8, 7, getMetaFromDirection(1, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 7, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 0, 7, betweenstoneBricks, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 5, 7, weedwoodPlankSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 7, betweenstoneBricks, 1, 6, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 7, betweenstoneBrickSlab, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 5, 0, 8, betweenstoneBricks, 4, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 8, betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 8, betweenstoneBrickSlab, 1, 1, 1, direction);
			//NUHH UHH
			//rotatedCubeVolume(world, x, y, z, 9, 4, 8, BLBlockRegistry.weedwoodPlankFence, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 5, 8, getMetaFromDirection(0, direction, logSequence), 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 8, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 2, 8, chiseledBetweenstone, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 3, 8, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 8, betweenstoneBrickSlab, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, 0, 9, betweenstoneBricks, 4, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 9, getMetaFromDirection(1, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 9, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 0, 9, angryBetweenstone, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 0, 9, betweenstoneBricks, 2, 1, 1, direction);
			rotatedLoot(world, random, x, y, z, 9, 1, 9, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 9, betweenstoneBricks, 1, 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 5, 9, getMetaFromDirection(3, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 9, betweenstoneBrickSlab, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, 0, 10, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 10, getMetaFromDirection(0, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 0, 10, betweenstoneBricks, 7, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 2, 10, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, 2, 10, betweenstoneBricks, 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 3, 10, getMetaFromDirection(0, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 8, 10, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 9, 10, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 11, 10, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, 12, 10, betweenstoneBrickSlab, 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 10, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 10, getMetaFromDirection(3, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 5, 10, betweenstoneBricks, 1, 6, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 11, 10, getMetaFromDirection(2, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 2, 10, getMetaFromDirection(2, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 6, 10, getMetaFromDirection(2, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 7, 10, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 2, 10, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 6, 10, betweenstoneBrickSlab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 7, 10, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 2, 10, betweenstoneBricks, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 6, 10, getMetaFromDirection(0, direction, betweenstoneBrickStairSequenceUpsideDown), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 7, 10, betweenstoneBrickSlab, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 10, betweenstoneTiles, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 4, 10, betweenstoneBricks, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 10, getMetaFromDirection(2, direction, betweenstoneBrickStairSequence), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 0, 10, betweenstoneTiles, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 1, 10, smoothBetweenstoneWall, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 11, betweenstoneTiles, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 1, 11, smoothBetweenstoneWall, 1, 4, 1, direction);
		}
		return true;
	}

	private void rotatedCubeVolume(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, IBlockState blockType, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
		case 0:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
					for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		case 1:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
					for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		case 2:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
					for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		case 3:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
					for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		}
	}

	private void rotatedLoot(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, int direction) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
		case 0:
			generateLoot(world, rand, x + offsetA, y + offsetB, z + offsetC);
			break;
		case 1:
			generateLoot(world, rand, x + offsetC, y + offsetB, z + depth - offsetA - 1);
			break;
		case 2:
			generateLoot(world, rand, x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1);
			break;
		case 3:
			generateLoot(world, rand, x + width - offsetC - 1, y + offsetB, z + offsetA);
			break;
		}
	}

	private boolean rotatedCubeCantReplace(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		x -= width / 2;
		z -= depth / 2;
		boolean replaceable = true;
		switch (direction) {
		case 0:
			if(!world.isAreaLoaded(new BlockPos(x + offsetA - 4, y + offsetB, z + offsetC - 4), new BlockPos(x + offsetA + sizeWidth + 4, y + offsetB + sizeHeight, z + offsetC + sizeDepth + 4)))
				return true;
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
					for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
						if (!world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().isReplaceable(world, new BlockPos(xx, yy, zz)))
							replaceable = false;
					}
			break;
		case 1:
			if(!world.isAreaLoaded(new BlockPos(x + offsetC - 4, y + offsetB, z + depth - offsetA - sizeWidth - 1 - 4), new BlockPos(x + offsetC + sizeDepth + 4, y + offsetB + sizeHeight, z + depth - offsetA - 1 + 4)))
				return true;
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
					for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
						if (!world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().isReplaceable(world, new BlockPos(xx, yy, zz)))
							replaceable = false;
					}
			break;
		case 2:
			if(!world.isAreaLoaded(new BlockPos(x + width - offsetA - sizeWidth - 1 - 4, y + offsetB, z + depth - offsetC - sizeDepth - 1 - 4), new BlockPos(x + width - offsetA - 1 + 4, y + offsetB + sizeHeight, z + depth - offsetC - 1 + 4)))
				return true;
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
					for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
						if (!world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().isReplaceable(world, new BlockPos(xx, yy, zz)))
							replaceable = false;
					}
			break;
		case 3:
			if(!world.isAreaLoaded(new BlockPos(x + width - offsetC - sizeDepth - 1 - 4, y + offsetB, z + offsetA - 4), new BlockPos(x + width - offsetC - 1 + 4, y + offsetB + sizeHeight, z + offsetA + sizeWidth + 4)))
				return true;
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
					for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
						if (!world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().isReplaceable(world, new BlockPos(xx, yy, zz)))
							replaceable = false;
					}
			break;
		}
		return !replaceable;
	}

	private boolean rotatedCubeMatches(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction, SurfaceType type) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
		case 0:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
					for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
						if (!type.matches(world.getBlockState(new BlockPos(xx, yy, zz))))
							return false;
					}
			break;
		case 1:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
					for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
						if (!type.matches(world.getBlockState(new BlockPos(xx, yy, zz))))
							return false;
					}
			break;
		case 2:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
					for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
						if (!type.matches(world.getBlockState(new BlockPos(xx, yy, zz))))
							return false;
					}
			break;
		case 3:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
					for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
						if (!type.matches(world.getBlockState(new BlockPos(xx, yy, zz))))
							return false;
					}
			break;
		}
		return true;
	}

	private boolean addLocationArea(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction, List<LocationStorage> addedLocations) {
		x -= width / 2;
		z -= depth / 2;
		//TODO: World locations
		/*switch (direction) {
		case 0:
			addedLocations.addAll(StorageHelper.addArea(world, "translate:ruins", AxisAlignedBB.getBoundingBox(x+offsetA, y+offsetB, z+offsetC, x+offsetA+sizeWidth, y+offsetB+sizeHeight, z+offsetC+sizeDepth).expand(6, 6, 6), EnumLocationType.RUINS, 0));
			break;
		case 1:
			addedLocations.addAll(StorageHelper.addArea(world, "translate:ruins", AxisAlignedBB.getBoundingBox(x+offsetC, y+offsetB, z + depth - offsetA - sizeWidth - 1, x+offsetC+sizeDepth, y+offsetB+sizeHeight, z + depth - offsetA - 1).expand(6, 6, 6), EnumLocationType.RUINS, 0));
			break;
		case 2:
			addedLocations.addAll(StorageHelper.addArea(world, "translate:ruins", AxisAlignedBB.getBoundingBox(x + width - offsetA - sizeWidth - 1, y+offsetB, z + depth - offsetC - sizeDepth - 1, x + width - offsetA - 1, y+offsetB+sizeHeight, z + depth - offsetC - 1).expand(6, 6, 6), EnumLocationType.RUINS, 0));
			break;
		case 3:
			addedLocations.addAll(StorageHelper.addArea(world, "translate:ruins", AxisAlignedBB.getBoundingBox(x + width - offsetC - sizeDepth - 1, y+offsetB, z + offsetA, x + width - offsetC - 1, y+offsetB+sizeHeight, z + offsetA + sizeWidth).expand(6, 6, 6), EnumLocationType.RUINS, 0));
			break;
		}*/
		return true;
	}

	private void generateLoot(World world, Random random, int x, int y, int z) {
		world.setBlockState(new BlockPos(x, y, z), getRandomBlock(random, EnumFacing.getHorizontal(random.nextInt(4))), 2);
		TileEntityLootPot lootPot = (TileEntityLootPot) world.getTileEntity(new BlockPos(x, y, z));
		if (lootPot != null)
			LootUtil.generateLoot(lootPot, random, LootTables.COMMON_POT_LOOT, 1, 2);
	}

	private IBlockState getRandomBlock(Random rand, EnumFacing dir) {
		switch (rand.nextInt(3)) {
		case 0:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_1).withProperty(BlockLootPot.FACING, dir);
		case 1:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_2).withProperty(BlockLootPot.FACING, dir);
		case 2:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_3).withProperty(BlockLootPot.FACING, dir);
		default:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_1).withProperty(BlockLootPot.FACING, dir);
		}
	}

	private IBlockState getMetaFromDirection(int start, int direction, IBlockState[] sequence) {
		return sequence[(direction + start) % sequence.length];
	}

	private static class RuinLocation {
		World world;
		Random random;
		int x;
		int y;
		int z;
		int structureID;

		RuinLocation(World world, Random random, int x, int y, int z, int structureID) {
			this.world = world;
			this.random = random;
			this.x = x;
			this.y = y;
			this.z = z;
			this.structureID = structureID;
		}

	}
}
