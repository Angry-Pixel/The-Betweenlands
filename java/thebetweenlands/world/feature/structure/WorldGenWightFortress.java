package thebetweenlands.world.feature.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLSpawner;
import thebetweenlands.entities.EntitySwordEnergy;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBossTeleporter;
import thebetweenlands.tileentities.TileEntityBLSign;
import thebetweenlands.tileentities.TileEntityItemCage;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.world.loot.LootTables;
import thebetweenlands.world.loot.LootUtil;
import thebetweenlands.world.storage.chunk.storage.StorageHelper;
import thebetweenlands.world.storage.chunk.storage.location.LocationAmbience;
import thebetweenlands.world.storage.chunk.storage.location.LocationAmbience.EnumLocationAmbience;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage.EnumLocationType;

public class WorldGenWightFortress extends WorldGenerator {

	private int length = -1;
	private int width = -1;
	private int height = -1;
	private int direction = -1;
	private int originX, originY, originZ = -1; 

	private Block limestonePolished = BLBlockRegistry.polishedLimestone;
	private Block limestoneChiselled = BLBlockRegistry.chiseledLimestone;
	private Block limestoneBrickSlab = BLBlockRegistry.limestoneBrickSlab;
	private Block limestonePolishedCollapsing = BLBlockRegistry.polishedLimestoneCollapsing;
	private Block betweenstone = BLBlockRegistry.betweenstone;
	private Block betweenstoneSmooth = BLBlockRegistry.smoothBetweenstone;
	private Block betweenstoneSmoothMossy = BLBlockRegistry.betweenstoneSmoothMossy;
	private Block betweenstoneTiles = BLBlockRegistry.betweenstoneTiles;
	private Block betweenstoneTilesMossy = BLBlockRegistry.betweenstoneTilesMossy;
	private Block betweenstoneTilesCracked = BLBlockRegistry.betweenstoneTilesCracked;
	private Block betweenstoneTilesCollapsing = BLBlockRegistry.betweenstoneTilesCollapsing;
	private Block betweenstoneTilesMossyCollapsing = BLBlockRegistry.betweenstoneTilesMossyCollapsing;
	private Block betweenstoneBrickStairs = BLBlockRegistry.betweenstoneBrickStairs;
	private Block betweenstoneBrickStairsMossy = BLBlockRegistry.betweenstoneBrickStairsMossy;
	private Block betweenstoneBrickStairsCracked = BLBlockRegistry.betweenstoneBrickStairsCracked;
	private Block betweenstoneBrickSlab = BLBlockRegistry.betweenstoneBrickSlab;
	private Block betweenstoneBrickWall = BLBlockRegistry.betweenstoneBrickWall;
	private Block betweenstoneBrickWallMossy = BLBlockRegistry.betweenstoneBrickWallMossy;
	private Block betweenstoneBrickWallCracked = BLBlockRegistry.betweenstoneBrickWallCracked;
	private Block betweenstoneBricks = BLBlockRegistry.betweenstoneBricks;
	private Block betweenstoneBricksMossy = BLBlockRegistry.betweenstoneBricksMossy;
	private Block betweenstoneBricksCracked = BLBlockRegistry.betweenstoneBricksCracked;
	private Block betweenstonePillar = BLBlockRegistry.betweenstonePillar;
	private Block betweenstoneStairsSmooth = BLBlockRegistry.smoothBetweenstoneStairs;
	private Block betweenstoneStairsSmoothMossy = BLBlockRegistry.betweenstoneSmoothStairsMossy;
	private Block betweenstoneTilesFortress = BLBlockRegistry.betweenstoneTilesFortress;
	private Block stagnantWater = BLBlockRegistry.stagnantWaterFluid;
	private Block spikeTrap = BLBlockRegistry.spikeTrap;
	private Block swordStone = BLBlockRegistry.itemCage;
	private Block root = BLBlockRegistry.root;
	private Block possessedBlock = BLBlockRegistry.possessedBlock;
	private Block chest = BLBlockRegistry.weedwoodChest;
	private Block lootPot1 = BLBlockRegistry.lootPot1;
	private Block lootPot2 = BLBlockRegistry.lootPot2;
	private Block lootPot3 = BLBlockRegistry.lootPot3;
	private BlockBLSpawner spawner = BLBlockRegistry.blSpawner;
	private Block obviousSign = BLBlockRegistry.weedwoodWallSign;
	private Block energyBarrier = BLBlockRegistry.energyBarrier;
	private Block valoniteBlock = BLBlockRegistry.valoniteBlock;
	private Block syrmoriteBlock = BLBlockRegistry.syrmoriteBlock;
	private Block octineBlock = BLBlockRegistry.octineBlock;

	public WorldGenWightFortress() {
		//these sizes are subject to change
		length = 13;
		width = 13;
		height = 19;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		List<LocationStorage> addedLocations = StorageHelper.addArea(world, "translate:wightTower", AxisAlignedBB.getBoundingBox(x - 10, y - 10, z - 10, x + 42, y + 80, z + 42), EnumLocationType.WIGHT_TOWER, 0);
		for(LocationStorage location : addedLocations) {
			location.setAmbience(new LocationAmbience(EnumLocationAmbience.WIGHT_TOWER).setFogRangeMultiplier(0.2F).setFogBrightness(80)).getChunkData().markDirty();
		}
		StorageHelper.addArea(world, "translate:wightTowerPuzzle", AxisAlignedBB.getBoundingBox(x - 10 + 20, y + 17, z - 10 + 20, x + 42 - 20, y + 17 + 6, z + 42 - 20), EnumLocationType.WIGHT_TOWER, 1);
		StorageHelper.addArea(world, "translate:wightTowerTeleporter", AxisAlignedBB.getBoundingBox(x - 10 + 23, y + 17 + 12, z - 10 + 23, x + 42 - 23, y + 17 + 6 + 11, z + 42 - 23), EnumLocationType.WIGHT_TOWER, 2);
		addedLocations = StorageHelper.addArea(world, "translate:wightTowerBoss", AxisAlignedBB.getBoundingBox(x - 10 + 17, y + 17 + 19, z - 10 + 17, x + 42 - 17, y + 17 + 12 + 32, z + 42 - 17), EnumLocationType.WIGHT_TOWER, 3);
		for(LocationStorage location : addedLocations) {
			location.setAmbience(new LocationAmbience(EnumLocationAmbience.WIGHT_TOWER).setFogRange(12.0F, 20.0F).setFogColorMultiplier(0.1F)).getChunkData().markDirty();
		}
		originX = x;
		originY = y;
		originZ = z;
		System.out.println("Fortress Here: X: " + x + " Y: " + y + " Z: "+ z);
		return generateStructure(world, rand, x, y, z);
	}

	public Block getRandomWall(Random rand) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return betweenstoneBrickWall;
		case 1:
			return betweenstoneBrickWallMossy;
		case 2:
			return betweenstoneBrickWallCracked;
		}
		return betweenstoneBrickWall;
	}

	public Block getRandomBricks(Random rand) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return betweenstoneBricks;
		case 1:
			return betweenstoneBricksMossy;
		case 2:
			return betweenstoneBricksCracked;
		}
		return betweenstoneBricks;
	}

	public Block getRandomTiles(Random rand) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return betweenstoneTiles;
		case 1:
			return betweenstoneTilesMossy;
		case 2:
			return betweenstoneTilesCracked;
		}
		return betweenstoneTiles;
	}

	public Block getRandomMetalBlock(Random rand) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return syrmoriteBlock;
		case 1:
			return octineBlock;
		case 2:
			return valoniteBlock;
		}
		return syrmoriteBlock;
	}

	public Block getRandomCollapsingTiles(Random rand) {
		return rand.nextBoolean() ? betweenstoneTilesCollapsing : betweenstoneTilesMossyCollapsing;
	}

	public Block getRandomSmoothBetweenstone(Random rand) {
		return rand.nextBoolean() ? betweenstoneSmooth : betweenstoneSmoothMossy;
	}

	public Block getRandomSmoothBetweenstoneStairs(Random rand) {
		return rand.nextBoolean() ? betweenstoneStairsSmooth : betweenstoneStairsSmoothMossy;
	}

	public Block getRandomBetweenstoneBrickStairs(Random rand) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return betweenstoneBrickStairs;
		case 1:
			return betweenstoneBrickStairsMossy;
		case 2:
			return betweenstoneBrickStairsCracked;
		}
		return betweenstoneBrickStairs;
	}

	public boolean generateStructure(World world, Random rand, int xx, int yy, int zz) {

		for (int xa = xx; xa <= xx + 32; ++xa) {
			for(int za = zz; za <= zz + 32; ++za) {
				for(int ya = yy - 12 ; ya < yy; ++ya ) {
					if(!world.getBlock(xa, ya, za).isNormalCube())
						world.setBlock(xa, ya, za, betweenstone);
				}
			}
		}

		// air just to erase old one :P
		for (int xa = xx; xa <= xx + 32; ++xa) {
			for(int za = zz; za <= zz + 32; ++za) {
				for(int ya = yy; ya < yy + 48; ++ya ) {
					world.setBlockToAir(xa, ya, za);
				}
			}
		}

		// loot room air just to erase old one
		for (int xa = xx + 8; xa <= xx + 24; ++xa) {
			for(int za = zz + 8; za <= zz + 24; ++za) {
				for(int ya = yy - 8; ya < yy ; ++ya ) {
					world.setBlockToAir(xa, ya, za);
				}
			}
		}

		length = 32;
		width = 32;

		for (direction = 0; direction < 4; direction++) {
			//loot room
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, -7, 8, betweenstoneBricks, 0, 8, 6, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, -7, 9, betweenstoneBricks, 0, 1, 6, 7, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, -8, 8, betweenstoneSmooth, 0, 8, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, -8, 9, betweenstoneSmooth, 0, 2, 1, 7, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, -8, 10, betweenstoneTilesFortress, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, -8, 10, betweenstoneTiles, 0, 5, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, -8, 11, betweenstoneTiles, 0, 1, 1, 5, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, -8, 11, betweenstoneSmooth, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, -7, 12, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, -7, 13, betweenstoneStairsSmooth, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -7, 13, stagnantWater, 0, 3, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -7, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -6, 9, betweenstonePillar, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -4, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -3, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, -3, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -3, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -7, 13, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -6, 13, betweenstonePillar, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -4, 13, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -3, 13, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, -3, 13, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -3, 14, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -3, 12, betweenstoneBrickStairs, direction == 0 ? 6 : direction== 1 ? 4 : direction == 2 ? 7 : 5, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -2, 9, betweenstoneBricks, 0, 7, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -2, 10, betweenstoneBricks, 0, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, -2, 10, limestoneBrickSlab, 8, 3, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -2, 10, limestoneBrickSlab, 8, 2, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, -2, 14, limestoneBrickSlab, 8, 3, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -2, 10, betweenstoneBricks, 0, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, -2, 13, betweenstoneBricks, 0, 6, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -7, 9, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -6, 9, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -5, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, -7, 9, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, -6, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -7, 11, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, -6, 11, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

			//ground floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 0, -1, 0, betweenstoneSmooth, 0, 13, 1, 13, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, -1, 4, betweenstoneTiles, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, -1, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, -1, 5, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 7, -1, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, -1, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, -1, 6, limestonePolished, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, -1, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, -1, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, -1, 7, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 7, -1, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, -1, 8, betweenstoneTiles, 0, 3, 1, 1, direction);

			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, xx, yy, zz, 6, 2, 6, spawner, 0, 1, 1, 1, direction);
			spawner.setMob(world, xx + 6, yy + 2, zz + 6, rand.nextBoolean() ? "thebetweenlands.swampHag" : "thebetweenlands.chiromaw");
			spawner.setMob(world, xx + 25, yy + 2, zz + 6, rand.nextBoolean() ? "thebetweenlands.swampHag" : "thebetweenlands.chiromaw");
			spawner.setMob(world, xx + 25, yy + 2, zz + 25, rand.nextBoolean() ? "thebetweenlands.swampHag" : "thebetweenlands.chiromaw");
			spawner.setMob(world, xx + 6, yy + 2, zz + 25, rand.nextBoolean() ? "thebetweenlands.swampHag" : "thebetweenlands.chiromaw");

			//1st floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 3, 5, 3, limestonePolished, 0, 7, 1, 7, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 5, 5, limestoneChiselled, 0, 1, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 5, 6, limestoneChiselled, 0, 3, 1, 1, direction);
			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, xx, yy, zz, 6, 8, 6, spawner, 0, 1, 1, 1, direction);
			spawner.setMob(world, xx + 6, yy + 8, zz + 6, "thebetweenlands.pyrad");
			spawner.setMob(world, xx + 25, yy + 8, zz + 6, "thebetweenlands.pyrad");
			spawner.setMob(world, xx + 25, yy + 8, zz + 25, "thebetweenlands.pyrad");
			spawner.setMob(world, xx + 6, yy + 8, zz + 25, "thebetweenlands.pyrad");


			//2nd floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 11, 4, limestonePolished, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 11, 6, limestoneChiselled, 0, 1, 1, 1, direction);
			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, xx, yy, zz, 6, 14, 6, spawner, 0, 1, 1, 1, direction);
			spawner.setMob(world, xx + 6, yy + 14, zz + 6, "thebetweenlands.termite");
			spawner.setMob(world, xx + 25, yy + 14, zz + 6, "thebetweenlands.termite");
			spawner.setMob(world, xx + 25, yy + 14, zz + 25, "thebetweenlands.termite");
			spawner.setMob(world, xx + 6, yy + 14, zz + 25, "thebetweenlands.termite");

			//3rd floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 16, 4, limestoneChiselled, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 16, 4, betweenstoneTiles, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 16, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 16, 5, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 7, 16, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 16, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 16, 6, limestonePolished, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, 16, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 16, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 16, 7, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 7, 16, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 16, 8, betweenstoneTiles, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 19, 6, spawner, 0, 1, 1, 1, direction);
			spawner.setMob(world, xx + 6, yy + 19, zz + 6, "thebetweenlands.wight");
			if(spawner.getLogic(world, xx + 6, yy + 19, zz + 6) != null)
				spawner.getLogic(world, xx + 6, yy + 19, zz + 6).setCheckRange(16.0D).setDelay(3000, 5000).setMaxEntities(1);
			spawner.setMob(world, xx + 25, yy + 19, zz + 6, "thebetweenlands.wight");
			if(spawner.getLogic(world, xx + 25, yy + 19, zz + 6) != null)
				spawner.getLogic(world, xx + 25, yy + 19, zz + 6).setCheckRange(16.0D).setDelay(3000, 5000).setMaxEntities(1);
			spawner.setMob(world, xx + 25, yy + 19, zz + 25, "thebetweenlands.wight");
			if(spawner.getLogic(world, xx + 25, yy + 19, zz + 25) != null)
				spawner.getLogic(world, xx + 25, yy + 19, zz + 25).setCheckRange(16.0D).setDelay(3000, 5000).setMaxEntities(1);
			spawner.setMob(world, xx + 6, yy + 19, zz + 25, "thebetweenlands.wight");
			if(spawner.getLogic(world, xx + 6, yy + 19, zz + 25) != null)
				spawner.getLogic(world, xx + 6, yy + 19, zz + 25).setCheckRange(16.0D).setDelay(3000, 5000).setMaxEntities(1);
			if (rand.nextBoolean())
				rotatedCubeVolume(world, rand, xx, yy, zz, 16, 26, 16, spawner, 0, 1, 1, 1, direction);
			spawner.setMob(world, xx + 16, yy + 26, zz + 16, "thebetweenlands.chiromaw");
			spawner.setMob(world, xx + 16, yy + 26, zz + 15, "thebetweenlands.chiromaw");
			spawner.setMob(world, xx + 15, yy + 26, zz + 16, "thebetweenlands.chiromaw");
			spawner.setMob(world, xx + 15, yy + 26, zz + 15, "thebetweenlands.chiromaw");
		}

		length = 13;
		width = 13;
		for (int tower = 0; tower  < 5; tower ++) {
			int x = xx, y = yy, z = zz;

			if (tower == 1)
				x = xx + 19;

			if (tower == 2) {
				x = xx + 19;
				z = zz + 19;
			}

			if (tower == 3)
				z = zz + 19;

			for (direction = 0; direction < 4; direction++) {
				if(tower < 4) {

					rotatedCubeVolume(world, rand, x, y, z, 3, 2, 1, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 7, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 4, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 8, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 0, 3, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 3, 1, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 0, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 4, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 4, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 10, 3, betweenstoneBricks, 0, 1, 1, 1, direction);

					//deco walls
					rotatedCubeVolume(world, rand, x, y, z, 0, 4, 0, betweenstoneBrickWall, 0, 9, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 4, 1, betweenstoneBrickWall, 0, 1, 1, 3, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 4, 1, betweenstoneBrickWall, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 4, 1, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 4, 3, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 6, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 6, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 7, 2, betweenstoneBrickWall, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 9, 2, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 9, 3, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 14, 3, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 16, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 16, 3, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 17, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 17, 1, betweenstoneBrickWall, 0, 4, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 17, 3, betweenstoneBrickWall, 0, 1, 1, 3, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 16, 1, betweenstoneBrickWall, 0, 5, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 16, 0, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 15, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 18, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 18, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 16, 0, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 15, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 18, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 18, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 10, 2, betweenstoneBrickWall, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 14, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 10, 2, betweenstoneBrickWall, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 10, 1, betweenstoneBrickWall, 0, 3, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 5, 1, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 8, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 5, 1, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 5, 0, betweenstoneBrickWall, 0, 3, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 9, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 9, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);

					rotatedCubeVolume(world, rand, x, y, z, 4, 3, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 4, 3, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 3, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 4, 3, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 4, 4, betweenstoneBrickSlab, 8, 5, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 3, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 3, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 0, 2, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 4, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 4, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 5, 2, betweenstoneBricks, 0, 1, 2, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 5, 3, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 5, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 3, 1, betweenstoneBricks, 0, 7, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 3, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 8, 3, betweenstoneBricks, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 16, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 16, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 15, 3, betweenstoneTilesFortress, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 14, 3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 9, 2, betweenstoneTiles, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 8, 2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 5, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 7, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 5, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 3, 1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 0, 3, 3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 2, 8, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 3, 8, 2, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 4, 8, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 8, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 6, 11, 4, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					// tower loot pots
					rotatedCubeVolume(world, rand, x, y, z, 3, 0, 1, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 9, 0, 1, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

					rotatedCubeVolume(world, rand, x, y, z, 4, 6, 2, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, x, y, z, 8, 6, 2, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
					if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2)
						rotatedCubeVolume(world, rand, x, y, z, 6, 10, 2, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

					//	tower chests
					rotatedCubeVolume(world, rand, x, y, z, 4, 12, 4, chest,direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);
					if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2) {
						rotatedCubeVolume(world, rand, x, y, z, 5, 17, 3, chest, direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 6, 17, 3, chest, direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 17, 3, chest, direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);
					}
				}
			}
		}
		for (int tower = 0; tower  < 5; tower ++) {
			int x = xx, y = yy, z = zz;

			if (tower == 1)
				x = xx + 19;

			if (tower == 2) {
				x = xx + 19;
				z = zz + 19;
			}

			if (tower == 3)
				z = zz + 19;

			for (direction = 0; direction < 4; direction++) {
				if(tower < 4) {

					//walkways
					if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2) {
						rotatedCubeVolume(world, rand, x, y, z, 5, 4, 11, betweenstoneTiles, 0, 3, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 5, 12, betweenstoneBricks, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 5, 12, betweenstoneBricks, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 4, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 3, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 6, 10, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 5, 11, Blocks.air, 0, 3, 5, 2, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 6, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 6, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 7, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 7, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 9, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 9, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 10, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 10, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 10, 10, betweenstoneTilesCollapsing, 0, 3, 1, 6, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 11, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 11, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 12, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 12, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 13, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 13, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 15, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 15, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 16, 10, betweenstoneTiles, 0, 3, 1, 6, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 11, 10, Blocks.air, 0, 3, 5, 2, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 17, 10, Blocks.air, 0, 3, 4, 3, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 5, 12, possessedBlock, tower == 0 && direction == 0 ? 5 : tower == 0 && direction == 1 ? 2 : tower == 1 && direction == 0 ? 5 : tower == 1 && direction == 3 ? 3 : tower == 2 && direction == 2 ? 4 : tower == 2 && direction == 3 ? 3 : tower == 3 && direction == 1 ? 2 : 4, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 5, 11, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 5, 12, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 5, 13, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 11, 10, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 11, 11, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 11, 12, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
					}

					//top Floor
					if (tower == 0 && direction == 0 || tower == 1 && direction == 3 || tower == 2 && direction == 2 || tower == 3 && direction == 1) {
						rotatedCubeVolume(world, rand, x, y, z, 8, 16, 11, betweenstoneBricks, 0, 1, 3, 4, direction);
						rotatedCubeVolume(world, rand, x, y, z, 7, 17, 15, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 17, 15, spikeTrap, 0, 3, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 9, 17, 14, betweenstoneBricks, 0, 2, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 19, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 19, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 19, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 21, 10, betweenstoneBrickSlab, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 16, 12, betweenstoneBrickWall, 0, 1, 2, 4, direction);	
					}
					if (tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 2 && direction == 3 || tower == 3 && direction == 2) {
						rotatedCubeVolume(world, rand, x, y, z, 4, 16, 11, betweenstoneBricks, 0, 1, 3, 4, direction);
						rotatedCubeVolume(world, rand, x, y, z, 5, 17, 15, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 2, 17, 15, spikeTrap, 0, 3, 1, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 2, 17, 14, betweenstoneBricks, 0, 2, 2, 1, direction);//
						rotatedCubeVolume(world, rand, x, y, z, 4, 19, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 19, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 19, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, x, y, z, 4, 21, 10, betweenstoneBrickSlab, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, x, y, z, 8, 16, 12, betweenstoneBrickWall, 0, 1, 2, 4, direction);
					}
				}
				//top tower
				if(tower == 4) {
					length = 14;
					width = 14;
					x = xx + 9;
					z = zz + 9;
					y = yy + 18;
					generateTopTowerRight(world, rand, x, y, z, direction);
					generateTopTowerLeft(world, rand, x, y, z, direction);
				}	
			}
		}
		//top tower decoration walls
		length = 32;
		width = 32;
		for (direction = 0; direction < 4; direction++) {
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 22, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 23, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 23, 11, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 24, 11, betweenstoneBrickWall, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 26, 11, betweenstoneBrickWall, 0, 1, 12, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 26, 12, betweenstoneBrickWall, 0, 1, 12, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 32, 12, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 34, 11, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 35, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 35, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 32, 11, betweenstoneBrickWall, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 32, 14, betweenstoneBrickWall, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 32, 11, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 32, 15, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 33, 10, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 33, 14, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 34, 9, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 9, 34, 14, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 34, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 34, 15, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 36, 11, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 36, 14, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 26, 10, betweenstoneBrickWall, 0, 1, 11, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 26, 13, betweenstoneBrickWall, 0, 1, 11, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 22, 10, betweenstoneBrickWall, 0, 1, 4, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 22, 12, betweenstoneBrickWall, 0, 1, 4, 1, direction);
		}

		for (direction = 0; direction < 4; direction++) {
			// Len's Middle stuff
			//pillars
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 9, betweenstonePillar, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 4, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 9, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 10, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 15, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 9, betweenstonePillar, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 9, betweenstonePillar, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 3, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 4, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 9, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 10, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 15, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 9, betweenstonePillar, 0, 1, 1, 1, direction);

			//arches
			// lower
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 3, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 3, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 4, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 4, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 4, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 4, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 4, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 4, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 4, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);

			// mid
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 9, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 9, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 10, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 10, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 9, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 10, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 9, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 10, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);

			// top
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 15, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 15, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 16, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 16, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 16, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 15, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 16, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 16, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 15, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 16, 9, betweenstoneBricks, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 22, 15, 9, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 16, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 15, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 10, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 11, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 12, betweenstoneBrickSlab, 8, 1, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 16, 12, betweenstoneBrickSlab, 8, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 15, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 10, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 11, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 12, betweenstoneBrickSlab, 8, 1, 1, 1, direction);

			//floor
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -1, 5, betweenstoneTiles, 0, 6, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -1, 6, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -1, 7, betweenstoneStairsSmooth, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 7, betweenstoneSmooth, 0, 2, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, -1, 7, betweenstoneStairsSmooth, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 9, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 10, betweenstoneSmooth, 0, 2, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -1, 6, betweenstoneTiles, 0, 1, 1, 7, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, -1, 6, betweenstoneTiles, 0, 1, 1, 7, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, -1, 12, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -1, 14, betweenstoneSmooth, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 15, stagnantWater, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -2, 15, stagnantWater, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 5, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 6, 1, 1, direction);

			// going back to my roots
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 5, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 5, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 0, 13, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 0, 18, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 11, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 11, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 0, 13, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 0, 18, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 4, 11, root, 0, 1, rand.nextInt(3) + 3, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 0, 14, root, 0, 1, rand.nextBoolean() ? rand.nextInt(4) + 4 : 0, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 16, 0, 14, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);

			// loot pots
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 5, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 16, 5, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 16, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

		}
		//retro-gen betweenstoneBrickStairs
		direction = rand.nextInt(4);

		//main betweenstoneBrickStairs
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 0, 4, betweenstoneBricks, 0, 8, 4, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 3, betweenstoneBricks, 0, 6, 3, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 2, betweenstoneBricks, 0, 6, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 1, betweenstoneBricks, 0, 6, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 3, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 2, 2, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 1, 1, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 0, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 3, 3, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 2, 2, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 1, 1, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 0, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 5, 3, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 4, 2, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 2, 0, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 5, 3, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 4, 2, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 3, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 2, 0, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 7, 4, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 7, 4, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 0, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 1, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 2, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 3, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 4, 4, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 5, 3, Blocks.air, 0, 4, 4, 2, direction);

		//infills
		rotatedCubeVolume(world, rand, xx, yy, zz, 4, 0, 13, betweenstone, 0, 1, 4, 6, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 4, 4, 14, betweenstone, 0, 1, 1, 4, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 3, 0, 14, betweenstone, 0, 1, 3, 4, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 3, 3, 15, betweenstone, 0, 1, 1, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 27, 0, 13, betweenstone, 0, 1, 4, 6, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 27, 4, 14, betweenstone, 0, 1, 1, 4, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 28, 0, 14, betweenstone, 0, 1, 3, 4, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 28, 3, 15, betweenstone, 0, 1, 1, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 27, betweenstone, 0, 6, 4, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 4, 27, betweenstone, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 0, 28, betweenstone, 0, 4, 3, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 15, 3, 28, betweenstone, 0, 2, 1, 1, direction);

		//2nd betweenstoneBrickStairs
		for(int count = 0; count < 6;count ++)
			rotatedCubeVolume(world, rand, xx, yy, zz, 16 + count, 5 + count, 24, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 17, 5, 24, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 6, 24, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 19, 7, 24, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 20, 8, 24, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 19, 5, 23, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 10, 24, Blocks.air, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 12, 24, possessedBlock, direction == 0 ? 4 : direction == 1 ? 3 : direction == 2 ? 5 : 2, 1, 1, 1, direction);

		//3rd betweenstoneBrickStairs
		for(int count = 0; count < 6 ;count ++)
			rotatedCubeVolume(world, rand, xx, yy, zz, 16 - count, 11 + count, 7, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 11, 7, betweenstoneBricks, 0, 6, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 12, 7, betweenstoneBricks, 0, 5, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 13, 7, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 14, 7, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 15, 7, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 16, 7, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 16, 7, Blocks.air, 0, 3, 1, 1, direction);


		//top tower stairs
		rotatedCubeVolume(world, rand, xx, yy, zz, 17, 19, 23, Blocks.air, 0, 3, 3, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 23, 17, 22, betweenstoneBricks, 0, 1, 4, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 21, 10, betweenstoneBrickSlab, 0, 2, 1, 14, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 21, 14, betweenstoneBricks, 0, 1, 1, 4, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 21, 10, betweenstoneBricks, 0, 2, 1, 1, direction);//
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 18, 22, betweenstoneBricks, 0, 1, 3, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 19, 19, 23, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 20, 20, 23, betweenstoneBricks, 0, 2, 1, 1, direction);
		for(int count = 0; count < 3 ;count ++)
			rotatedCubeVolume(world, rand, xx, yy, zz, 17 + count, 18 + count, 23, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 19, 22, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 20, 21, 22, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

		for(int count = 0; count < 6 ;count ++)        
			rotatedCubeVolume(world, rand, xx, yy, zz, 22 - count, 22 + count, 10, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 27, 10, Blocks.air, 0, 1, 5, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 21, 22, 10, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 19, 22, 10, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 15, 27, 10, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 27, 10, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 26, 10, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 17, 26, 10, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 26, 10, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 17, 29, 11, obviousSign, direction == 0 ? 2 : direction == 1 ? 4 : direction == 2 ? 3 : 5, 1, 1, 1, direction);
		//top tower floors

		//underneath
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 9, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 11, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 18, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 21, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 17, 21, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 17, 9, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 21, 17, 11, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 21, 17, 18, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 11, 17, 11, spikeTrap, 0, 10, 1, 10, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 9, betweenstoneBricks, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 17, 22, betweenstoneBricks, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 22, betweenstoneBricks, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 22, 17, 9, betweenstoneBricks, 0, 1, 1, 1, 0);

		setSwordStone(world, rand, xx + 12, yy + 22, zz + 12, swordStone, 0, (byte) 0);
		setSwordStone(world, rand, xx + 19, yy + 22, zz + 12, swordStone, 0, (byte) 1);
		setSwordStone(world, rand, xx + 19, yy + 22, zz + 19, swordStone, 0, (byte) 2);
		setSwordStone(world, rand, xx + 12, yy + 22, zz + 19, swordStone, 0, (byte) 3);

		EntitySwordEnergy swordEnergy = new EntitySwordEnergy(world);
		swordEnergy.setPosition(xx + 16D, yy + 21.5, zz + 16D);
		world.spawnEntityInWorld(swordEnergy);

		//floor 1
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 23, 12, limestonePolishedCollapsing, 0, 8, 1, 8, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 16, 24, 16, chest, direction == 0 ? 5 : direction == 1 ? 2 : direction == 2 ? 4 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 16, 24, 15, chest, direction == 0 ? 5 : direction == 1 ? 2 : direction == 2 ? 4 : 3, 1, 1, 1, direction);

		//floor2
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 28, 13, limestonePolished, 0, 6, 1, 6, 0);

		EntityFortressBossTeleporter tp = new EntityFortressBossTeleporter(world);
		tp.setLocationAndAngles(xx + 16, yy + 30, zz + 16, 0, 0);
		tp.setTeleportDestination(Vec3.createVectorHelper(xx + 16, yy + 17 + 19.2D, zz + 16));
		tp.setBossSpawnPosition(Vec3.createVectorHelper(xx + 16, yy + 17 + 19 + 5.2D, zz + 16));
		world.spawnEntityInWorld(tp);

		//floor3 (Boss fight Floor)
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 35, 13, betweenstoneTiles, 0, 6, 1, 6, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 35, 12, betweenstoneTiles, 0, 4, 1, 1, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 35, 19, betweenstoneTiles, 0, 4, 1, 1, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 35, 14, betweenstoneTiles, 0, 1, 1, 4, 0);
		rotatedCubeVolume(world, rand, xx, yy, zz, 19, 35, 14, betweenstoneTiles, 0, 1, 1, 4, 0);

		// more loot pots and energy barrier
		for (direction = 0; direction < 4; direction++) {
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);	
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 22, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 16, 22, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 28, 11, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 16, 28, 11, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

			//sword room pots
			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 18, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 19, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 19, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 18, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

			//energy barrier
			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 29, 13, energyBarrier, 0, 2, 4, 1, direction);
		}

		return true;
	}

	private void placeChest(World world, Random rand, int x, int y, int z, Block blockType, int blockMeta) {
		world.setBlock(x, y, z, chest, blockMeta, 2);
		TileEntityWeedWoodChest lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x, y, z);
		if (lootChest != null) {
			world.setBlockMetadataWithNotify(x, y, z, blockMeta, 3);
			LootUtil.generateLoot(lootChest, rand, LootTables.DUNGEON_CHEST_LOOT, 4, 8);
		}
	}

	private void placeRandomisedLootPot(World world, Random rand, int x, int y, int z, Block blockType, int blockMeta) {
		if(rand.nextInt(5) != 0 || world.isAirBlock(x, y - 1, z))
			return;
		else {
			world.setBlock(x, y, z, blockType, blockMeta, 2);
			TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, z);
			if (lootPot != null) {
				LootUtil.generateLoot(lootPot, rand, LootTables.DUNGEON_POT_LOOT, 1, 3);
				lootPot.setModelRotationOffset(world.rand.nextInt(41) - 20);
				world.markBlockForUpdate(x, y, z);
			}
		}
	}

	public void setSwordStone(World world, Random rand, int x, int y, int z, Block blockType, int blockMeta, byte type) {
		world.setBlock(x, y, z, blockType, blockMeta, 2);
		TileEntityItemCage swordStone = (TileEntityItemCage) world.getTileEntity(x, y, z);
		if (swordStone != null)
			swordStone.setType(type);
	}

	private void placeSign(World world, Random rand, int x, int y, int z, Block blockType, int blockMeta) {
		world.setBlock(x, y, z, obviousSign, blockMeta, 2);
		TileEntityBLSign sign = (TileEntityBLSign) world.getTileEntity(x, y, z);
		if (sign != null) {
			sign.signText = new String[] {
					StatCollector.translateToLocal("fortress.line1"),
					StatCollector.translateToLocal("fortress.line2"),
					StatCollector.translateToLocal("fortress.line3"),
					StatCollector.translateToLocal("fortress.line4")
			};
			world.markBlockForUpdate(x, y, z);
		}
	}

	public void rotatedCubeVolume(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		//special cases here

		switch (direction) {
		case 0:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
					for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
						if(blockType == betweenstoneTiles)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTiles : getRandomTiles(rand), blockMeta, 2);
						else if(blockType == betweenstoneBricks)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBricks : getRandomBricks(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickWall)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickWall : getRandomWall(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickStairs)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickStairs : getRandomBetweenstoneBrickStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneStairsSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneStairsSmooth : getRandomSmoothBetweenstoneStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneSmooth : getRandomSmoothBetweenstone(rand), blockMeta, 2);
						else if(blockType == betweenstoneTilesCollapsing)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTilesCollapsing : getRandomCollapsingTiles(rand), blockMeta, 2);
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, xx, yy, zz, rand.nextBoolean() ? lootPot1 : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= originY + 17) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, xx, yy, zz, chest, blockMeta);
							} else if (yy > originY + 17)
								placeChest(world, rand, xx, yy, zz, chest, blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, xx, yy, zz, obviousSign, blockMeta);
						else
							world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
					}
			break;
		case 1:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + length - offsetA - 1; zz > z + length - offsetA - sizeWidth - 1; zz--)
					for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
						if(blockType == betweenstoneTiles)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTiles : getRandomTiles(rand), blockMeta, 2);
						else if(blockType == betweenstoneBricks)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBricks : getRandomBricks(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickWall)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickWall : getRandomWall(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickStairs)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickStairs : getRandomBetweenstoneBrickStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneStairsSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneStairsSmooth : getRandomSmoothBetweenstoneStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneSmooth : getRandomSmoothBetweenstone(rand), blockMeta, 2);
						else if(blockType == betweenstoneTilesCollapsing)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTilesCollapsing : getRandomCollapsingTiles(rand), blockMeta, 2);
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, xx, yy, zz, rand.nextBoolean() ? lootPot1 : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= originY + 17) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, xx, yy, zz, chest, blockMeta);
							} else if (yy > originY + 17)
								placeChest(world, rand, xx, yy, zz, chest, blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, xx, yy, zz, obviousSign, blockMeta);
						else
							world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
					}
			break;
		case 2:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + length - offsetA - 1; xx > x + length - offsetA - sizeWidth - 1; xx--)
					for (int zz = z + length - offsetC - 1; zz > z + length - offsetC - sizeDepth - 1; zz--) {
						if(blockType == betweenstoneTiles)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTiles : getRandomTiles(rand), blockMeta, 2);
						else if(blockType == betweenstoneBricks)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBricks : getRandomBricks(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickWall)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickWall : getRandomWall(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickStairs)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickStairs : getRandomBetweenstoneBrickStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneStairsSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneStairsSmooth : getRandomSmoothBetweenstoneStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneSmooth : getRandomSmoothBetweenstone(rand), blockMeta, 2);
						else if(blockType == betweenstoneTilesCollapsing)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTilesCollapsing : getRandomCollapsingTiles(rand), blockMeta, 2);
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, xx, yy, zz, rand.nextBoolean() ? lootPot1 : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= originY + 17) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, xx, yy, zz, chest, blockMeta);
							} else if (yy > originY + 17)
								placeChest(world, rand, xx, yy, zz, chest, blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, xx, yy, zz, obviousSign, blockMeta);
						else
							world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
					}
			break;
		case 3:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
					for (int xx = x + length - offsetC - 1; xx > x + length - offsetC - sizeDepth - 1; xx--) {
						if(blockType == betweenstoneTiles)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTiles : getRandomTiles(rand), blockMeta, 2);
						else if(blockType == betweenstoneBricks)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBricks : getRandomBricks(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickWall)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickWall : getRandomWall(rand), blockMeta, 2);
						else if(blockType == betweenstoneBrickStairs)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneBrickStairs : getRandomBetweenstoneBrickStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneStairsSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneStairsSmooth : getRandomSmoothBetweenstoneStairs(rand), blockMeta, 2);
						else if(blockType == betweenstoneSmooth)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneSmooth : getRandomSmoothBetweenstone(rand), blockMeta, 2);
						else if(blockType == betweenstoneTilesCollapsing)
							world.setBlock(xx, yy, zz, rand.nextBoolean() ? betweenstoneTilesCollapsing : getRandomCollapsingTiles(rand), blockMeta, 2);
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, xx, yy, zz, rand.nextBoolean() ? lootPot1 : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= originY + 17) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, xx, yy, zz, chest, blockMeta);
							} else if (yy > originY + 17)
								placeChest(world, rand, xx, yy, zz, chest, blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, xx, yy, zz, obviousSign, blockMeta);
						else
							world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
					}
			break;
		}
	}

	public void generateTopTowerRight(World world, Random rand, int x, int y, int z, int direction) {		   
		rotatedCubeVolume(world, rand, x, y, z, 0, 0, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 0, 3, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 2, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 0, 1, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 0, 1, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, 3, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 4, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, 1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 8, 3, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 15, 3, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 14, 3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 9, 2, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 8, 2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 5, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 3, 1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 3, 3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);    	
		rotatedCubeVolume(world, rand, x, y, z, 5, 4, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 10, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
	}

	public void generateTopTowerLeft(World world, Random rand, int x, int y, int z, int direction) {
		if(direction == 2)
			z -= 13;
		if(direction == 0)
			z += 13;
		if(direction == 3)
			x -= 13;
		if(direction == 1)
			x += 13;

		rotatedCubeVolume(world, rand, x, y, z, 0, 0, -1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 0, -3, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, -1, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, -2, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 0, -1, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 0, -1, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, -3, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 4, -2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, -1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, -3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 8, -3, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 8, -2, betweenstoneBricks, 0, 1, 11, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 17, -2, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, -3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 10, -3, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 10, -3, betweenstoneBricks, 0, 1, 7, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 5, -2, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 6, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 15, -3,betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 14, -3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 9, -2, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 8, -2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 9, -2, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 5, -2, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 3, -1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 3, -3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	}

}
