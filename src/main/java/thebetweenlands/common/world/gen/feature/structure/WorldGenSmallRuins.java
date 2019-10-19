package thebetweenlands.common.world.gen.feature.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenSmallRuins extends WorldGenHelper {

	private static final IBlockState ANGRY_BETWEENSTONE = BlockRegistry.ANGRY_BETWEENSTONE.getDefaultState();
	private static final IBlockState BETWEENSTONE_TILES = BlockRegistry.BETWEENSTONE_TILES.getDefaultState();
	private static final IBlockState BETWEENSTONE_BRICKS = BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState();
	private static final IBlockState BETWEENSTONE_BRICK_STAIRS = BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState();
	private static final IBlockState BETWEENSTONE_BRICK_SLAB = BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState();
	private static final IBlockState BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN = BETWEENSTONE_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	private static final IBlockState CHISELED_BETWEENSTONE = BlockRegistry.BETWEENSTONE_CHISELED.getDefaultState();
	private static final IBlockState BETWEENSTONE_PILLAR = BlockRegistry.BETWEENSTONE_PILLAR.getDefaultState();
	private static final IBlockState SMOOTH_BETWEENSTONE_WALL = BlockRegistry.SMOOTH_BETWEENSTONE_WALL.getDefaultState();
	private static final IBlockState WEEDWOOD_LOG = BlockRegistry.LOG_WEEDWOOD.getDefaultState();
	private static final IBlockState WEEDWOOD_PLANK_STAIRS = BlockRegistry.WEEDWOOD_PLANK_STAIRS.getDefaultState();
	private static final IBlockState WEEDWOOD_PLANK_SLAB = BlockRegistry.WEEDWOOD_PLANK_SLAB.getDefaultState();
	private static final IBlockState WEEDWOOD_PLANK_SLAB_UPSIDE_DOWN = WEEDWOOD_PLANK_SLAB.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	private static final IBlockState BETWEENSTONE_BRICK_WALL = BlockRegistry.BETWEENSTONE_BRICK_WALL.getDefaultState();
	private static final IBlockState WEEDWOOD_FENCE = BlockRegistry.WEEDWOOD_PLANK_FENCE.getDefaultState();
	private static final IBlockState ROPE = BlockRegistry.ROPE.getDefaultState();
	
	public WorldGenSmallRuins(){
		super(BlockRegistry.MUD.getDefaultState(), BlockRegistry.SWAMP_GRASS.getDefaultState());
		this.replaceable.add(state -> state.getBlock() instanceof IPlantable);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos position) {
		int x;
		int y;
		int z;

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "ruins", EnumLocationType.RUINS);

		List<RuinLocation> ruinLocations = new ArrayList<>();
		int attempts = 40;
		while (attempts >= 0) {
			x = position.getX() + random.nextInt(16) - 8;
			z = position.getZ() + random.nextInt(16) - 8;
			y = position.getY() + random.nextInt(8) - 3;
			if(random.nextInt(50) == 0) {
				if (structure7(world, random, x, y, z, false, locationStorage)) {
					ruinLocations.add(new RuinLocation(world, random, x, y, z, 6));
				}
			} else {
				int randomInt = random.nextInt(6);
				switch (randomInt) {
				case 0:
					if (structure1(world, random, x, y, z, false, locationStorage))
						ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
					break;
				case 1:
					if (structure2(world, random, x, y, z, false, locationStorage))
						ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
					break;
				case 2:
					if (structure3(world, random, x, y, z, false, locationStorage))
						ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
					break;
				case 3:
					if (structure4(world, random, x, y, z, false, locationStorage))
						ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
					break;
				case 4:
					if (structure5(world, random, x, y, z, false, locationStorage))
						ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
					break;
				case 5:
					if (structure6(world, random, x, y, z, false, locationStorage))
						ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
					break;
				}
			}
			attempts--;
		}

		if (ruinLocations.size() >= 3) {
			for (RuinLocation location : ruinLocations) {
				switch (location.structureID) {
				case 0:
					structure1(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				case 1:
					structure2(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				case 2:
					structure3(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				case 3:
					structure4(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				case 4:
					structure5(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				case 5:
					structure6(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				case 6:
					structure7(location.world, location.random, location.x, location.y, location.z, true, locationStorage);
					break;
				}
			}

			locationStorage.setSeed(random.nextLong());
			locationStorage.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);

			return true;
		} else
			return false;
	}

	private boolean structure1(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		int height = 9 + random.nextInt(2);
		width = 8;
		depth = 1;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, 0, 0, width, height, depth, direction).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, height, depth, direction, location);

			rotatedCubeVolume(world, x, y, z, 2, 0, 0, BETWEENSTONE_TILES, 1, height - 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 0, BETWEENSTONE_TILES, 1, height - 5, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 5, 0, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 5, 0, BETWEENSTONE_BRICKS, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, height - 5, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 0, height - 4, 0, BETWEENSTONE_BRICK_SLAB, 2, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 3, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, height - 3, 0, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, height - 3, 0, BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, height - 3, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, height - 2, 0, BETWEENSTONE_BRICK_SLAB, 3, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, height - 1, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, height - 1, 0, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedLootPot(world, random, x, y, z, 4, height - 2, 0, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
		}
		return true;
	}

	private boolean structure2(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		int height = 13 + random.nextInt(2);
		width = 7;
		depth = 1;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, 0, 0, width, height, depth, direction).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, height, depth, direction, location);

			rotatedCubeVolume(world, x, y, z, 2, 0, 0, BETWEENSTONE_TILES, 1, height - 9, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 0, BETWEENSTONE_TILES, 1, height - 9, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 9, 0, BETWEENSTONE_BRICKS, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 9, 0, BETWEENSTONE_BRICKS, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 8, 0, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 8, 0, CHISELED_BETWEENSTONE, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 7, 0, BETWEENSTONE_BRICKS, 1, 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 7, 0, BETWEENSTONE_BRICKS, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, height - 6, 0, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, height - 6, 0, BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, height - 6, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, height - 6, 0, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, height - 5, 0, BETWEENSTONE_BRICK_SLAB, 3, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, height - 2, 0, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, height - 2, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, BETWEENSTONE_BRICK_SLAB, 2, 1, 1, direction);

			if (random.nextInt(5) == 0) {
				rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, BETWEENSTONE_BRICKS, 1, 1, 1, direction);
				rotatedLootPot(world, random, x, y, z, 0, height, 0, direction, 1, 2, 1, LootTableRegistry.COMMON_POT_LOOT);
			}
		}
		return true;
	}

	private boolean structure3(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		width = 7;
		depth = 5;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 7, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND) || !rotatedCubeMatches(world, x, y, z, 2, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED_GROUND) || !rotatedCubeMatches(world, x, y, z, 6, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 7, depth, direction).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, 7, depth, direction, location);

			rotatedCubeVolume(world, x, y, z, 2, 0, 0, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 0, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 0, 4, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 4, BETWEENSTONE_TILES, 1, 4, 1, direction);

			rotatedCubeVolume(world, x, y, z, 1, 4, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, 4, 4, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 0, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 0, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 4, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 4, BETWEENSTONE_BRICKS, 1, 2, 1, direction);

			rotatedCubeVolume(world, x, y, z, 0, 5, 0, BETWEENSTONE_BRICK_SLAB, 2, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, 5, 0, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 5, 4, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 5, 0, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 5, 4, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 5, 1, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 5, 1, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 5, 3, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 5, 3, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, 6, 0, BETWEENSTONE_BRICK_SLAB, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 6, 4, BETWEENSTONE_BRICK_SLAB, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 6, 1, BETWEENSTONE_BRICK_SLAB, 1, 1, 3, direction);
			rotatedCubeVolume(world, x, y, z, 6, 6, 1, BETWEENSTONE_BRICK_SLAB, 1, 1, 3, direction);
			rotatedLootPot(world, random, x, y, z, 2, 6, 0, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
			rotatedLootPot(world, random, x, y, z, 6, 6, 0, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
			rotatedLootPot(world, random, x, y, z, 2, 6, 4, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
			rotatedLootPot(world, random, x, y, z, 6, 6, 4, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
		}
		return true;
	}

	//TODO switch this to the new system at some point....
	private boolean structure4(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		int height = 9 + random.nextInt(2);
		int width = 6;
		for (int zz = z; zz < z + width; zz++)
			for (int yy = y; yy < y + height; yy++)
				for (int xx = x; xx > x - width; xx--)
					if (!world.isBlockLoaded(this.getCheckPos(xx, yy, zz)) || !(world.getBlockState(new BlockPos(xx, yy, zz)).getBlock() == Blocks.AIR || (world.getBlockState(new BlockPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_WATER && yy < y + height - 2)))
						return false;

		AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z, x, y + height, z + width).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (!SurfaceType.MIXED_GROUND.matches(world.getBlockState(new BlockPos(x - width, y - 1, z + 1))) || !SurfaceType.MIXED_GROUND.matches(world.getBlockState(new BlockPos(x - width, y - 1, z + width - 1))) || !SurfaceType.MIXED_GROUND.matches(world.getBlockState(new BlockPos(x - 1, y - 1, z + width))) || !SurfaceType.MIXED_GROUND.matches(world.getBlockState(new BlockPos(x - 1, y - 1, z + width))))
			return false;
		if (doGen) {
			location.addBounds(new AxisAlignedBB(x - width, y, z, x, y + height, z + width).grow(6, 6, 6));
			
			for (int yy = y; yy < y + height; yy++) {
				if (yy <= y + height - 5) {
					if (yy == y)
						world.setBlockState(new BlockPos(x - width, yy, z + 1), BETWEENSTONE_TILES);
					else
						world.setBlockState(new BlockPos(x - width, yy, z + 1), BETWEENSTONE_BRICK_WALL);
					world.setBlockState(new BlockPos(x - width + 1, yy, z + 1), BETWEENSTONE_TILES);
				} else if (yy == y + height - 4) {
					world.setBlockState(new BlockPos(x - width, yy, z + 1), BETWEENSTONE_BRICK_WALL);
					world.setBlockState(new BlockPos(x - width + 1, yy, z + 1), BETWEENSTONE_BRICKS);
				} else if (yy == y + height - 3) {
					world.setBlockState(new BlockPos(x - width + 1, yy, z + 1), getStateFromRotation(3, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 3);
					world.setBlockState(new BlockPos(x - width + 1, yy, z + 2), getStateFromRotation(1, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3);
					world.setBlockState(new BlockPos(x - width + 1, yy + 1, z + 2), BETWEENSTONE_BRICK_SLAB);
					int zz;
					for (zz = z + 3; zz <= z + width - 3; zz++) {
						world.setBlockState(new BlockPos(x - width + 1, yy + 1, zz), BETWEENSTONE_BRICK_SLAB);
						world.setBlockState(new BlockPos(x - width + 1, yy, zz), BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 3);
					}
					world.setBlockState(new BlockPos(x - width + 1, yy, zz), getStateFromRotation(3, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3);
					world.setBlockState(new BlockPos(x - width + 1, yy + 1, zz), BETWEENSTONE_BRICK_SLAB);
				}
			}

			for (int yy = y; yy < y + height; yy++) {
				if (yy <= y + height - 5) {
					if (yy == y) {
						world.setBlockState(new BlockPos(x - width, yy, z + width - 1), BETWEENSTONE_TILES);
						world.setBlockState(new BlockPos(x - width + 1, yy, z + width), BETWEENSTONE_TILES);
					} else {
						world.setBlockState(new BlockPos(x - width, yy, z + width - 1), BETWEENSTONE_BRICK_WALL);
						world.setBlockState(new BlockPos(x - width + 1, yy, z + width), BETWEENSTONE_BRICK_WALL);
					}
					world.setBlockState(new BlockPos(x - width + 1, yy, z + width - 1), BETWEENSTONE_TILES);
				} else if (yy <= y + height - 2) {
					if (yy == y + height - 4) {
						world.setBlockState(new BlockPos(x - width, yy, z + width - 1), BETWEENSTONE_BRICK_WALL);
						world.setBlockState(new BlockPos(x - width + 1, yy, z + width), BETWEENSTONE_BRICK_WALL);
					}
					world.setBlockState(new BlockPos(x - width + 1, yy, z + width - 1), BETWEENSTONE_BRICKS);
				} else if (yy <= y + height - 1) {

					world.setBlockState(new BlockPos(x - width + 1, yy, z + width - 1), BETWEENSTONE_BRICK_STAIRS);
					world.setBlockState(new BlockPos(x - width + 2, yy, z + width - 1), getStateFromRotation(2, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3);
					world.setBlockState(new BlockPos(x - width + 2, yy + 1, z + width - 1), BETWEENSTONE_BRICK_SLAB);
					int xx;
					for (xx = x - width + 3; xx <= x - 3; xx++) {
						world.setBlockState(new BlockPos(xx, yy, z + width - 1), BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 3);
						if (random.nextInt(8) == 0) {
							generateLoot(world, random, xx, yy + 1, z + width - 1);
						} else
							world.setBlockState(new BlockPos(xx, yy + 1, z + width - 1), BETWEENSTONE_BRICK_SLAB);
					}

					world.setBlockState(new BlockPos(xx, yy, z + width - 1), getStateFromRotation(0, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3);
					world.setBlockState(new BlockPos(xx, yy + 1, z + width - 1), BETWEENSTONE_BRICK_SLAB);
				}
			}

			for (int yy = y; yy < y + height; yy++) {
				if (yy <= y + height - 5) {
					if (yy == y) {
						world.setBlockState(new BlockPos(x - 1, yy, z + width), BETWEENSTONE_TILES);
					} else {
						world.setBlockState(new BlockPos(x - 1, yy, z + width), BETWEENSTONE_BRICK_WALL);
					}
					world.setBlockState(new BlockPos(x - 1, yy, z + width - 1), BETWEENSTONE_TILES);
				} else if (yy <= y + height - 2) {
					if (yy == y + height - 4) {
						world.setBlockState(new BlockPos(x - 1, yy, z + width), BETWEENSTONE_BRICK_WALL);
					}
					if (yy == y + height - 3) {
						world.setBlockState(new BlockPos(x, yy, z + width - 1), getStateFromRotation(2, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3);
					}
					world.setBlockState(new BlockPos(x - 1, yy, z + width - 1), BETWEENSTONE_BRICKS);
				} else if (yy == y + height - 1) {
					world.setBlockState(new BlockPos(x - 1, yy, z + width - 1), getStateFromRotation(2, 0, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 3);
				}
			}
		}
		return true;
	}

	private boolean structure5(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		int height = 5 + random.nextInt(2);
		width = 1;
		depth = 1;
		int direction = 0;
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, 0, 0, width, height, depth, direction).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, height, depth, direction, location);

			rotatedCubeVolume(world, x, y, z, 0, 0, 0, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, 1, 0, BETWEENSTONE_PILLAR, 1, height - 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedLootPot(world, random, x, y, z, 0, height, 0, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
		}
		return true;
	}

	private boolean structure6(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		width = 5 + random.nextInt(2);
		depth = 1;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 1, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND) || !rotatedCubeMatches(world, x, y, z, width - 1, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 1, depth, direction).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, 1, depth, direction, location);

			rotatedCubeVolume(world, x, y, z, 0, 0, 0, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, 0, 0, getStateFromRotation(7, direction, BETWEENSTONE_PILLAR, EnumRotationSequence.PILLAR_SIDEWAYS), width - 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, width - 1, 0, 0, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedLootPot(world, random, x, y, z, 0, 1, 0, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);
			rotatedLootPot(world, random, x, y, z, width - 1, 1, 0, direction, 1, 2, 5, LootTableRegistry.COMMON_POT_LOOT);

		}
		return true;
	}

	private boolean structure7(World world, Random random, int x, int y, int z, boolean doGen, LocationStorage location) {
		width = 12;
		depth = 12;
		int direction = random.nextInt(4);
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED_GROUND)) {
			y--;
			if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED_GROUND))
				return false;
		}

		if (rotatedCubeCantReplace(world, x, y, z, 2, 1, 2, width - 4, 13, depth - 4, direction))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 13, depth, direction).grow(6, 6, 6);
		if (!world.isAreaLoaded(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)))
			return false;

		if (doGen) {
			this.addLocationArea(world, x, y, z, 0, 0, 0, width, 13, depth, direction, location);

			rotatedCubeVolume(world, x, y, z, 0, 0, 0, BETWEENSTONE_BRICKS, 1, 1, 4, direction);
			rotatedCubeVolume(world, x, y, z, 0, 1, 0, BETWEENSTONE_BRICKS, 1, 1, 3, direction);
			rotatedCubeVolume(world, x, y, z, 0, 1, 3, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, 2, 0, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 0, 2, 1, BETWEENSTONE_BRICKS, 1, 1, 2, direction);
			rotatedCubeVolume(world, x, y, z, 0, 2, 3, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 0, 1, BETWEENSTONE_TILES, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 1, 1, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 1, BETWEENSTONE_TILES, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 1, 1, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 0, 2, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 2, BETWEENSTONE_BRICKS, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 2, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 0, 2, BETWEENSTONE_BRICKS, 1, 7, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 7, 2, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 0, 2, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 2, 2, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 3, 2, BETWEENSTONE_BRICKS, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 0, 2, BETWEENSTONE_BRICKS, 1, 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 5, 2, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 2, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 4, 2, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 0, 2, BETWEENSTONE_TILES, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 1, 2, SMOOTH_BETWEENSTONE_WALL, 1, 3, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 5, 3, getStateFromRotation(1, direction, WEEDWOOD_PLANK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 3, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 3, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 3, WEEDWOOD_PLANK_SLAB_UPSIDE_DOWN, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 3, BETWEENSTONE_BRICKS, 1, 6, 3, direction);
			rotatedCubeVolume(world, x, y, z, 10, 3, 3, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 3, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 5, 4, WEEDWOOD_PLANK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 4, BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 4, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 3, 4, ROPE, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 4, getStateFromRotation(0, direction, WEEDWOOD_LOG, EnumRotationSequence.LOG_SIDEWAYS), 5, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 4, 4, WEEDWOOD_FENCE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 2, 4, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 4, BETWEENSTONE_BRICKS, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 5, 5, getStateFromRotation(3, direction, WEEDWOOD_PLANK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 5, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 5, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 5, WEEDWOOD_PLANK_SLAB_UPSIDE_DOWN, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 5, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, 0, 6, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 6, BETWEENSTONE_BRICKS, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 8, 6, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 8, 6, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 9, 6, BETWEENSTONE_BRICK_SLAB, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, 8, 6, BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 8, 6, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 6, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 6, BETWEENSTONE_BRICKS, 1, 6, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 10, 6, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 5, 6, WEEDWOOD_PLANK_SLAB_UPSIDE_DOWN, 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 0, 6, BETWEENSTONE_BRICKS, 1, 1, 1, direction);
			rotatedLootPot(world, random, x, y, z, 9, 1, 6, direction, 1, 2, 1, LootTableRegistry.COMMON_POT_LOOT);
			rotatedCubeVolume(world, x, y, z, 10, 0, 6, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 4, 6, BETWEENSTONE_BRICKS, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 5, 6, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 0, 6, BETWEENSTONE_TILES, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 1, 6, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);

			rotatedCubeVolume(world, x, y, z, 6, 8, 7, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 7, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 0, 7, BETWEENSTONE_BRICKS, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 5, 7, WEEDWOOD_PLANK_SLAB_UPSIDE_DOWN, 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 7, BETWEENSTONE_BRICKS, 1, 6, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 7, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 5, 0, 8, BETWEENSTONE_BRICKS, 4, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 8, BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 8, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 4, 8, WEEDWOOD_FENCE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 5, 8, getStateFromRotation(0, direction, WEEDWOOD_LOG, EnumRotationSequence.LOG_SIDEWAYS), 3, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 8, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 2, 8, CHISELED_BETWEENSTONE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 3, 8, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 8, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 3, 0, 9, BETWEENSTONE_BRICKS, 4, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 8, 9, getStateFromRotation(1, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 9, 9, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 0, 9, ANGRY_BETWEENSTONE, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 0, 9, BETWEENSTONE_BRICKS, 2, 1, 1, direction);
			rotatedLootPot(world, random, x, y, z, 9, 1, 9, direction, 1, 2, 1, LootTableRegistry.COMMON_POT_LOOT);
			rotatedCubeVolume(world, x, y, z, 10, 0, 9, BETWEENSTONE_BRICKS, 1, 5, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 5, 9, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 9, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);

			rotatedCubeVolume(world, x, y, z, 2, 0, 10, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, 4, 10, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 0, 10, BETWEENSTONE_BRICKS, 7, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 3, 2, 10, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, 2, 10, BETWEENSTONE_BRICKS, 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 3, 10, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 8, 10, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 9, 10, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 5, 11, 10, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 4, 12, 10, BETWEENSTONE_BRICK_SLAB, 2, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 0, 10, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 4, 10, getStateFromRotation(3, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 5, 10, BETWEENSTONE_BRICKS, 1, 6, 1, direction);
			rotatedCubeVolume(world, x, y, z, 6, 11, 10, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 2, 10, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 6, 10, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 7, 7, 10, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 2, 10, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 6, 10, BETWEENSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 8, 7, 10, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 2, 10, BETWEENSTONE_BRICKS, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 6, 10, getStateFromRotation(0, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 9, 7, 10, BETWEENSTONE_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 10, BETWEENSTONE_TILES, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 4, 10, BETWEENSTONE_BRICKS, 1, 2, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 6, 10, getStateFromRotation(2, direction, BETWEENSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 0, 10, BETWEENSTONE_TILES, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 11, 1, 10, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 0, 11, BETWEENSTONE_TILES, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 10, 1, 11, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
		}
		return true;
	}


	private boolean addLocationArea(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int sizeWidth, int sizeHeight, int sizeDepth, int direction, LocationStorage location) {
		location.addBounds(rotatedAABB(world, x, y, z, offsetX, offsetY, offsetZ, sizeWidth, sizeHeight, sizeDepth, direction).grow(6, 6, 6));
		return true;
	}

	private void generateLoot(World world, Random random, int x, int y, int z) {
		world.setBlockState(new BlockPos(x, y, z), getRandomBlock(random, EnumFacing.byHorizontalIndex(random.nextInt(4))), 2);
		TileEntityLootPot lootPot = BlockLootPot.getTileEntity(world, new BlockPos(x, y, z));
		if (lootPot != null) {
			lootPot.setLootTable(LootTableRegistry.COMMON_POT_LOOT, random.nextLong());
		}
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
