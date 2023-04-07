package thebetweenlands.common.world.gen.feature.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenUndergroundRuins extends WorldGenHelper {
	public IBlockState PITSTONE_TILES = BlockRegistry.PITSTONE_TILES.getDefaultState();
	public IBlockState PITSTONE_BRICKS = BlockRegistry.PITSTONE_BRICKS.getDefaultState();
	public IBlockState PITSTONE_PILLAR = BlockRegistry.PITSTONE_PILLAR.getDefaultState();
	public IBlockState PITSTONE_BRICK_WALL = BlockRegistry.PITSTONE_BRICK_WALL.getDefaultState();
	public IBlockState PITSTONE_CHISELED = BlockRegistry.PITSTONE_PILLAR.getDefaultState();
	public IBlockState PITSTONE_BRICK_SLAB = BlockRegistry.PITSTONE_BRICK_SLAB.getDefaultState();
	public IBlockState PITSTONE_BRICK_SLAB_UPSIDE_DOWN = BlockRegistry.PITSTONE_BRICK_SLAB.getDefaultState();
	public IBlockState PITSTONE_BRICK_STAIRS = BlockRegistry.PITSTONE_BRICK_STAIRS.getDefaultState();

	private List<AxisAlignedBB> locationAABBs = new ArrayList<>();
	
	private boolean structure1(World world, Random random, int x, int y, int z, LocationStorage location) {
		width = 7;
		depth = 6;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, 1, 4, depth, direction)
				|| rotatedCubeCantReplace(world, x, y, z, 6, 0, 0, 1, 4, depth, direction)
				|| rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 4, 1, direction)
				|| rotatedCubeCantReplace(world, x, y, z, 0, 0, 5, width, 4, 1, direction))
			return false;
		if (!makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, true))
			return false;
		
		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 0, 1, 4, depth, direction).grow(3, 2, 3));
		location.addBounds(this.rotatedAABB(world, x, y, z, 6, 0, 0, 1, 4, depth, direction).grow(3, 2, 3));
		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 4, 1, direction).grow(3, 2, 3));
		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 5, width, 4, 1, direction).grow(3, 2, 3));
		
		makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, false);
		
		rotatedCubeVolume(world, x, y, z, 0, 0, 0, PITSTONE_TILES, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 0, PITSTONE_TILES, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 0, PITSTONE_TILES, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 0, PITSTONE_TILES, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 5, PITSTONE_TILES, 5, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 1, 0, PITSTONE_BRICKS, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 5, PITSTONE_BRICKS, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1 + random.nextInt(5), 1, 5, PITSTONE_CHISELED, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 1, 0, PITSTONE_BRICKS, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 6, 1, 2 + random.nextInt(2), Blocks.AIR.getDefaultState(), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 0, PITSTONE_BRICKS, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 6, 2, 0, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 5, PITSTONE_BRICKS, 7, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2 + random.nextInt(4), 2, 5, Blocks.AIR.getDefaultState(), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 3, PITSTONE_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 0, PITSTONE_BRICKS, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 3, 4, PITSTONE_BRICKS, 1, 1, 2, direction);

		rotatedLootPot(world, random, x, y, z, 1, 0, 1, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 5, 0, 1, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 1, 0, 4, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 5, 0, 4, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		return true;
	}


	private boolean structure2(World world, Random random, int x, int y, int z, LocationStorage location) {
		width = 9;
		depth = 11;
		int direction = random.nextInt(4);
		int height = 6 + random.nextInt(2);
		if (rotatedCubeCantReplace(world, x, y, z, 4, height - 3, 0, 5, height - 3, depth, direction) || rotatedCubeCantReplace(world, x, y, z, 0, height - 3, 6, 4, height - 3, 5, direction))
			return false;
		if (!makePitstoneSupport(world, x, y, z, 0, -1, 6, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 0, -1, 10, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 4, -1, 6, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 4, -1, 10, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 4, -1, 2, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 8, -1, 6, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 8, -1, 10, 1, 1, direction, true)
				|| !makePitstoneSupport(world, x, y, z, 8, -1, 2, 1, 1, direction, true))
			return false;
		
		location.addBounds(this.rotatedAABB(world, x, y, z, 4, height - 3, 0, 5, height - 3, depth, direction).grow(2, 2, 2));
		
		makePitstoneSupport(world, x, y, z, 0, -1, 6, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 0, -1, 10, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 4, -1, 6, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 4, -1, 10, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 4, -1, 2, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 8, -1, 6, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 8, -1, 10, 1, 1, direction, false);
		makePitstoneSupport(world, x, y, z, 8, -1, 2, 1, 1, direction, false);

		rotatedCubeVolume(world, x, y, z, 0, 0, 6, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 10, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 6, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 10, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 2, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 0, 6, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 0, 10, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 0, 2, PITSTONE_BRICKS, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 1, 6, PITSTONE_PILLAR, 1, 2 + random.nextInt(3), 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 10, PITSTONE_PILLAR, 1, random.nextInt(3), 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 6, PITSTONE_PILLAR, 1, height - 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 10, PITSTONE_PILLAR, 1, height - 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 2, PITSTONE_PILLAR, 1, height - 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 6, PITSTONE_PILLAR, 1, height - 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 10, PITSTONE_PILLAR, 1, height - 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 2, PITSTONE_PILLAR, 1, height - 3, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, height - 2, 10, PITSTONE_BRICKS, 8, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, height - 2, 9, PITSTONE_TILES, 6, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, height - 2, 8, PITSTONE_TILES, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, height - 2, 7, PITSTONE_TILES, 6, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, height - 2, 6, PITSTONE_BRICKS, 4, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 8, height - 2, 0, PITSTONE_BRICKS, 1, 1, 10, direction);
		rotatedCubeVolume(world, x, y, z, 7, height - 2, 1, PITSTONE_TILES, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 6, height - 2, 2, PITSTONE_TILES, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 5, height - 2, 3, PITSTONE_TILES, 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 4, height - 2, 2, PITSTONE_BRICKS, 1, 1, 4, direction);

		rotatedCubeVolume(world, x, y, z, 8, height - 1, 10, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, height - 1, 1, PITSTONE_BRICK_WALL, 1, 1, 9, direction);
		rotatedCubeVolume(world, x, y, z, 8, height - 1, 2 + random.nextInt(7), Blocks.AIR.getDefaultState(), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, height - 1, 2, PITSTONE_BRICK_WALL, 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 4, height - 1, 3 + random.nextInt(2), Blocks.AIR.getDefaultState(), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, height - 1, 6, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, height - 1, 10, PITSTONE_BRICK_WALL, 6, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3 + random.nextInt(4), height - 1, 10, Blocks.AIR.getDefaultState(), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, height - 1, 6, PITSTONE_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, height - 1, 6, PITSTONE_BRICK_WALL, 1, 1, 1, direction);
		if (random.nextBoolean())
			rotatedCubeVolume(world, x, y, z, 8, height, 10, PITSTONE_CHISELED, 1, 1, 1, direction);


		rotatedLootPot(world, random, x, y, z, 7, height - 1, 9, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 5, height - 1, 4, direction, 1, 2, 4, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 1, 0, 7, direction, 1, 2, 4, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 7, 0, 9, direction, 1, 2, 4, LootTableRegistry.UNDERGROUND_RUINS_POT);
		return true;
	}

	private boolean structure3(World world, Random random, int x, int y, int z, LocationStorage location) {
		width = 6;
		depth = 6;
		int direction = 0;
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 7, depth, direction))
			return false;
		if (!makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, true))
			return false;

		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 7, depth, direction).grow(2, 1, 2));
		
		makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, false);

		rotatedCubeVolume(world, x, y, z, 1, 0, 1, PITSTONE_TILES, 4, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 0, PITSTONE_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 5, PITSTONE_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 0, PITSTONE_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 5, PITSTONE_BRICKS, 1, 2, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 0, 0, getStateFromRotation(3, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 1, getStateFromRotation(0, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 5, getStateFromRotation(1, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 1, getStateFromRotation(2, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 4, direction);

		rotatedCubeVolume(world, x, y, z, 0, 2, 0, PITSTONE_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 5, PITSTONE_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 2, 0, PITSTONE_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 2, 5, PITSTONE_PILLAR, 1, 3, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 5, 0, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, 5, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 0, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 5, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 5, 0, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, 1, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 1, 5, 5, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 1, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 4, direction);

		rotatedCubeVolume(world, x, y, z, 1, 6, 0, PITSTONE_BRICK_SLAB, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 6, 1, PITSTONE_BRICK_SLAB, 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 1, 6, 5, PITSTONE_BRICK_SLAB, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 6, 1, PITSTONE_BRICK_SLAB, 1, 1, 4, direction);

		rotatedLootPot(world, random, x, y, z, 1, 1, 1, 0, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 4, 1, 1, 0, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 1, 1, 4, 0, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 4, 1, 4, 0, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		return true;
	}

	private boolean structure4(World world, Random random, int x, int y, int z, LocationStorage location) {
		depth = 11;
		width = 5;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 4, depth, direction))
			return false;
		if (!makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, true))
			return false;
		
		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 4, depth, direction).grow(2, 1, 2));
		
		makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, false);

		rotatedCubeVolume(world, x, y, z, 0, 0, 0, PITSTONE_BRICKS, 1, 1, 11, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 4, PITSTONE_BRICKS, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 10, PITSTONE_BRICKS, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 0, 4, PITSTONE_BRICKS, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 0, 10, PITSTONE_BRICKS, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 2, PITSTONE_BRICKS, 1, 1, 9, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 10, PITSTONE_BRICKS, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 1, 2, PITSTONE_BRICKS, 1, 1, 9, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 3, PITSTONE_BRICKS, 1, 1, 8, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 5, PITSTONE_BRICK_SLAB, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 3, 1, 5, PITSTONE_BRICK_SLAB, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 2, 1, 10, PITSTONE_CHISELED, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 2, 5, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 5, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 3, PITSTONE_BRICKS, 1, 1, 8, direction);
		rotatedCubeVolume(world, x, y, z, 4, 2, 4, PITSTONE_BRICKS, 1, 1, 7, direction);
		rotatedCubeVolume(world, x, y, z, 2, 2, 10, PITSTONE_BRICKS, 1, 2, 1, direction);

		//TODO: Check stairs
		rotatedCubeVolume(world, x, y, z, 2, 2, 4, getStateFromRotation(3, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 3, 4, PITSTONE_BRICKS, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 2, 3, 4, PITSTONE_BRICKS, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 3, 3, 4, PITSTONE_BRICKS, 1, 1, 6, direction);

		for (int i = 0; i <= 4; i++) {
			rotatedLootPot(world, random, x, y, z, 1, 0, 5 + i, direction, 1, 2, 4, LootTableRegistry.UNDERGROUND_RUINS_POT);
			rotatedLootPot(world, random, x, y, z, 3, 0, 5 + i, direction, 1, 2, 4, LootTableRegistry.UNDERGROUND_RUINS_POT);
		}
		return true;
	}


	private boolean structure5(World world, Random random, int x, int y, int z, LocationStorage location) {
		depth = 8;
		width = 7;
		int direction = random.nextInt(4);

		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 5, depth, direction))
			return false;
		if (!makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, true))
			return false;
		
		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 0, width, 5, depth, direction).grow(2, 1, 2));
		
		makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, false);

		rotatedCubeVolume(world, x, y, z, 1, 0, 0, PITSTONE_BRICKS, 7, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 6, PITSTONE_BRICKS, 7, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 0, PITSTONE_BRICKS, 1, 1, 7, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 0, PITSTONE_BRICKS, 1, 1, 7, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 2, PITSTONE_BRICK_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 4, PITSTONE_BRICK_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 1, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 0, 1, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 1, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 5, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 0, 5, PITSTONE_BRICK_SLAB_UPSIDE_DOWN, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 5, PITSTONE_BRICKS, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 1, 0, PITSTONE_TILES, 7, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 6, PITSTONE_TILES, 7, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 0, PITSTONE_TILES, 1, 2, 7, direction);
		rotatedCubeVolume(world, x, y, z, 7, 1, 0, PITSTONE_TILES, 1, 2, 7, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 0, PITSTONE_BRICK_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 6, PITSTONE_BRICK_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 1, 3, PITSTONE_BRICK_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 1, PITSTONE_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 5, PITSTONE_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 3, Blocks.AIR.getDefaultState(), 1, 2, 1, direction);

		//TODO: Check stairs
		rotatedCubeVolume(world, x, y, z, 0, 2, 2, getStateFromRotation(3, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 3, getStateFromRotation(0, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 4, getStateFromRotation(1, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 3, 0, getStateFromRotation(3, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 7, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 3, 1, getStateFromRotation(0, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 2, 3, 6, getStateFromRotation(1, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 6, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 3, 1, getStateFromRotation(2, direction, PITSTONE_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 5, direction);

		rotatedCubeVolume(world, x, y, z, 2, 4, 1, PITSTONE_BRICK_SLAB, 5, 1, 5, direction);

		rotatedLootPot(world, random, x, y, z, 2, 1, 1, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 2, 1, 5, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 6, 1, 1, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 6, 1, 5, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		return true;
	}

	private boolean structure6(World world, Random random, int x, int y, int z, LocationStorage location) {
		width = 7;
		depth = 7;
		int direction = random.nextInt(4);
		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, 3, 2, 1, direction)
				|| rotatedCubeCantReplace(world, x, y, z, 1, 0, 0, 1, 1, 1, direction)
				|| rotatedCubeCantReplace(world, x, y, z, 6, 0, 3, 1, 2, 3, direction)
				|| rotatedCubeCantReplace(world, x, y, z, 2, 0, 6, 4, 3, 1, direction))
			return false;
		if (!makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, true))
			return false;
		
		location.addBounds(this.rotatedAABB(world, x, y, z, 0, 0, 0, 3, 2, 1, direction).grow(3, 2, 3));
		location.addBounds(this.rotatedAABB(world, x, y, z, 1, 0, 0, 1, 1, 1, direction).grow(3, 2, 3));
		location.addBounds(this.rotatedAABB(world, x, y, z, 6, 0, 3, 1, 2, 3, direction).grow(3, 2, 3));
		location.addBounds(this.rotatedAABB(world, x, y, z, 2, 0, 6, 4, 3, 1, direction).grow(3, 2, 3));
		
		makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, false);

		rotatedCubeVolume(world, x, y, z, 0, 0, 0, PITSTONE_BRICKS, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 0, PITSTONE_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 3, PITSTONE_BRICKS, 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 6, PITSTONE_BRICKS, 4, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 1, 0, PITSTONE_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 1, 4, PITSTONE_BRICKS, 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 3, 1, 6, PITSTONE_BRICKS, 3, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 5, 2, 6, PITSTONE_BRICKS, 2, 1, 1, direction);

		rotatedLootPot(world, random, x, y, z, 5, 0, 5, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		rotatedLootPot(world, random, x, y, z, 1, 0, 1, direction, 1, 2, 3, LootTableRegistry.UNDERGROUND_RUINS_POT);
		return true;
	}

	/*public boolean structure5(World world, Random random, int x, int y, int z) {
        depth = 13;
        width = 8;
        int direction = 0;//random.nextInt(4);
        if (!rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 1, depth, direction)
                || !rotatedCubeCantReplace(world, x, y, z, 0, 5, 0, width, 1, depth, direction)
                || !rotatedCubeCantReplace(world, x, y, z, 2, 6, 0, 6, 3, depth, direction))
            return false;
        if (!makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, true))
            return false;
        makePitstoneSupport(world, x, y, z, 0, -1, 0, width, depth, direction, false);

        rotatedCubeVolume(world, random, x, y, z, 1, 0, 1, PITSTONE_BRICK_STAIRS, 0, 1, 1, 12, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 0, PITSTONE_BRICK_STAIRS, 2, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 12, PITSTONE_BRICK_STAIRS, 3, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 1, PITSTONE_TILES, 0, 4, 1, 11, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 1, PITSTONE_BRICK_STAIRS, 1, 1, 1, 12, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 0, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 4, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 8, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 12, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 0, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 4, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 8, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 12, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 1, 0, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 4, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 8, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 12, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 0, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 4, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 8, PITSTONE_PILLAR, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 12, PITSTONE_PILLAR, 0, 1, 4, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 6, 5, 0, PITSTONE_BRICK_SLAB, 8, 2, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 5, 0, PITSTONE_BRICK_SLAB, 8, 2, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 0, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 4, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 8, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 12, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 0, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 4, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 8, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 12, PITSTONE_BRICKS, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 0, PITSTONE_BRICK_SLAB, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 4, PITSTONE_BRICK_SLAB, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 8, PITSTONE_BRICK_SLAB, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 12, PITSTONE_BRICK_SLAB, 8, 4, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 6, 0, PITSTONE_BRICK_SLAB, 0, 1, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 6, 0, PITSTONE_BRICK_SLAB, 0, 1, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 6, 1, PITSTONE_BRICKS, 0, 4, 1, 11, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 6, 0, PITSTONE_BRICK_SLAB, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 6, 0, PITSTONE_BRICK_SLAB, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 6, 12, PITSTONE_BRICK_SLAB, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 6, 12, PITSTONE_BRICK_SLAB, 8, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 3, 7, 0, PITSTONE_BRICK_SLAB, 0, 1, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 7, 0, PITSTONE_BRICK_SLAB, 0, 1, 1, 13, direction);

        if (random.nextInt(2) == 0)
            rotatedLootPot(world, random, x, y, z, 5, 1, 2, 0);
        if (random.nextInt(2) == 0)
            rotatedLootPot(world, random, x, y, z, 2, 1, 6, 0);
        if (random.nextInt(2) == 0)
            rotatedLootPot(world, random, x, y, z, 5, 1, 10, 0);
        System.out.println("generated at: " + x + " " + y + " " + z);
        return true;
    }*/


	private boolean makePitstoneSupport(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int sizeWidth, int sizeDepth, int direction, boolean simulate) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
		case 0:
			if (world.isAreaLoaded(new BlockPos(x + offsetX, y, z + offsetZ), new BlockPos(x + offsetX + sizeWidth, y + offsetZ, z + offsetZ + sizeWidth))) {
				for (int xx = x + offsetX; xx < x + offsetX + sizeWidth; xx++)
					for (int zz = z + offsetZ; zz < z + offsetZ + sizeDepth; zz++) {
						int yy = y + offsetY;
						int times = 0;
						while (world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))) {
							if (!simulate)
								world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.PITSTONE.getDefaultState(), 2 | 16);
							yy--;
							times++;
							if (times > 4) {
								if (simulate) {
									return false;
								} else {
									break;
								}
							}
						}
					}
			} else
				return false;
			break;
		case 1:
			if (world.isAreaLoaded(new BlockPos(x + offsetZ, y, z + depth - offsetX - sizeWidth - 1), new BlockPos(x + offsetZ + sizeDepth, y + offsetZ, z + depth - offsetX - 1))) {
				for (int zz = z + depth - offsetX - 1; zz > z + depth - offsetX - sizeWidth - 1; zz--)
					for (int xx = x + offsetZ; xx < x + offsetZ + sizeDepth; xx++) {
						int yy = y + offsetY;
						int times = 0;
						while (world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))) {
							if (!simulate)
								world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.PITSTONE.getDefaultState(), 2 | 16);
							yy--;
							times++;
							if (times > 4) {
								if (simulate) {
									return false;
								} else {
									break;
								}
							}
						}
					}
			} else
				return false;
			break;
		case 2:
			if (world.isAreaLoaded(new BlockPos(x - offsetX - sizeWidth - 1, y, z + depth - offsetZ - sizeDepth - 1), new BlockPos(x + width - offsetX - 1, y + offsetZ, z + depth - offsetZ - 1))) {
				for (int xx = x + width - offsetX - 1; xx > x + width - offsetX - sizeWidth - 1; xx--)
					for (int zz = z + depth - offsetZ - 1; zz > z + depth - offsetZ - sizeDepth - 1; zz--) {
						int yy = y + offsetY;
						int times = 0;
						while (world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))) {
							if (!simulate)
								world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.PITSTONE.getDefaultState(), 2 | 16);
							yy--;
							times++;
							if (times > 4) {
								if (simulate) {
									return false;
								} else {
									break;
								}
							}
						}
					}
			} else
				return false;
			break;
		case 3:
			if (world.isAreaLoaded(new BlockPos(x + width - offsetZ - sizeDepth - 1, y, z + offsetX), new BlockPos(x + width - offsetZ - 1, y + offsetZ, z + offsetX + sizeWidth))) {
				for (int zz = z + offsetX; zz < z + offsetX + sizeWidth; zz++)
					for (int xx = x + width - offsetZ - 1; xx > x + width - offsetZ - sizeDepth - 1; xx--) {
						int yy = y + offsetY;
						int times = 0;
						while (world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))) {
							if (!simulate)
								world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.PITSTONE.getDefaultState(), 2 | 16);
							yy--;
							times++;
							if (times > 4) {
								if (simulate) {
									return false;
								} else {
									break;
								}
							}
						}
					}
			} else
				return false;
			break;
		}

		return true;
	}


	@Override
	public boolean generate(World world, Random random, BlockPos position) {
		boolean shouldStop = true;
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		while (y > WorldProviderBetweenlands.CAVE_WATER_HEIGHT) {
			if (world.getBlockState(this.getCheckPos(x, y - 1, z)).getBlock() == BlockRegistry.PITSTONE && world.isAirBlock(this.getCheckPos(x, y, z))) {
				shouldStop = false;
				break;
			}
			y--;
		}
		if (shouldStop)
			return false;

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "underground_ruins", EnumLocationType.RUINS);
		
		boolean generated = false;
		
		int randomInt = random.nextInt(6);
		switch (randomInt) {
		case 0:
			generated = structure1(world, random, x, y, z, locationStorage);
			break;
		case 1:
			generated = structure2(world, random, x, y, z, locationStorage);
			break;
		case 2:
			generated = structure3(world, random, x, y, z, locationStorage);
			break;
		case 3:
			generated = structure4(world, random, x, y, z, locationStorage);
			break;
		case 4:
			generated = structure5(world, random, x, y, z, locationStorage);
			break;
		case 5:
			generated = structure6(world, random, x, y, z, locationStorage);
			break;
		default:
		}
		
		if(generated) {
			locationStorage.setVisible(true);
			locationStorage.setSeed(random.nextLong());
			locationStorage.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
			return true;
		}
		
		return false;
	}
}
