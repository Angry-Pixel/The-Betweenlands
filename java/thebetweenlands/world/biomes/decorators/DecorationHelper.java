package thebetweenlands.world.biomes.decorators;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockSwampReed;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.blocks.plants.roots.BlockRoot;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.utils.CubicBezier;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.biomes.feature.WorldGenFluidPool;
import thebetweenlands.world.feature.gen.cave.*;
import thebetweenlands.world.feature.plants.WorldGenHugeMushroom;
import thebetweenlands.world.feature.plants.WorldGenMossPatch;
import thebetweenlands.world.feature.plants.WorldGenMushrooms;
import thebetweenlands.world.feature.plants.WorldGenWeedWoodBush;
import thebetweenlands.world.feature.structure.*;
import thebetweenlands.world.feature.trees.*;

import java.util.Random;

public class DecorationHelper {
	private final static WorldGenGiantTreeAlive GEN_GIANT_TREE = new WorldGenGiantTreeAlive();
	private final static WorldGenerator GEN_WEEDWOOD_TREE = new WorldGenWeedWoodTree();
	private final static WorldGenerator GEN_SMAL_WEEDWOOD_TREE = new WorldGenSmallWeedWoodTree();
	private final static WorldGenerator GEN_SAP_TREE = new WorldGenSapTree();
	private final static WorldGenerator GEN_RUBBER_TREE = new WorldGenRubberTree();
	private final static WorldGenerator GEN_WEEDWOOD_BUSH = new WorldGenWeedWoodBush();
	private final static WorldGenerator GEN_BLACK_HAT_MUSHROOMS = new WorldGenMushrooms(BLBlockRegistry.blackHatMushroom, 15);
	private final static WorldGenerator GEN_FLAT_HEAD_MUSHROOMS = new WorldGenMushrooms(BLBlockRegistry.flatHeadMushroom, 15);
	private final static WorldGenTallGrass GEN_NETTLE = new WorldGenTallGrass(BLBlockRegistry.nettle, 1);
	private final static WorldGenTallGrass GEN_CATTAIL = new WorldGenTallGrass(BLBlockRegistry.catTail, 1);
	private final static WorldGenTallGrass GEN_SWAMP_TALL_GRASS = new WorldGenTallGrass(BLBlockRegistry.swampTallGrass, 1);
	private final static WorldGenTallGrass GEN_SWAMP_DOUBLE_TALL_GRASS = new WorldGenTallGrass(BLBlockRegistry.doubleSwampTallgrass, 1);
	private final static WorldGenTallGrass GEN_SWAMP_PLANT = new WorldGenTallGrass(BLBlockRegistry.swampPlant, 1);
	private final static WorldGenTallGrass GEN_VENUS_FLY_TRAP = new WorldGenTallGrass(BLBlockRegistry.venusFlyTrap, 0);
	private final static WorldGenTallGrass GEN_VOLARPAD = new WorldGenTallGrass(BLBlockRegistry.volarpad, 0);
	private final static WorldGenerator GEN_MOSS_PATCH = new WorldGenMossPatch(0);
	private final static WorldGenerator GEN_LICHEN_PATCH = new WorldGenMossPatch(1);
	private final static WorldGenTallGrass GEN_ARROW_ARUM = new WorldGenTallGrass(BLBlockRegistry.arrowArum, 1);
	private final static WorldGenTallGrass GEN_BUTTON_BUSH = new WorldGenTallGrass(BLBlockRegistry.buttonBush, 1);
	private final static WorldGenTallGrass GEN_MARSH_HIBISCUS = new WorldGenTallGrass(BLBlockRegistry.marshHibiscus, 1);
	private final static WorldGenTallGrass GEN_PICKEREL_WEED = new WorldGenTallGrass(BLBlockRegistry.pickerelWeed, 1);
	private final static WorldGenTallGrass GEN_PHRAGMITES = new WorldGenTallGrass(BLBlockRegistry.phragmites, 1);
	private final static WorldGenTallGrass GEN_SOFT_RUSH = new WorldGenTallGrass(BLBlockRegistry.softRush, 1);
	private final static WorldGenTallGrass GEN_MARSH_MALLOW = new WorldGenTallGrass(BLBlockRegistry.marshMallow, 1);
	private final static WorldGenTallGrass GEN_MILKWEED = new WorldGenTallGrass(BLBlockRegistry.milkweed, 1);
	private final static WorldGenTallGrass GEN_SHOOTS = new WorldGenTallGrass(BLBlockRegistry.shoots, 1);
	private final static WorldGenTallGrass GEN_COPPER_IRIS = new WorldGenTallGrass(BLBlockRegistry.copperIris, 1);
	private final static WorldGenTallGrass GEN_BLUE_IRIS = new WorldGenTallGrass(BLBlockRegistry.blueIris, 1);
	private final static WorldGenTallGrass GEN_BLUE_EYED_GRASS = new WorldGenTallGrass(BLBlockRegistry.blueEyedGrass, 1);
	private final static WorldGenTallGrass GEN_BONESET = new WorldGenTallGrass(BLBlockRegistry.boneset, 1);
	private final static WorldGenTallGrass GEN_BOTTLE_BRUSH_GRASS = new WorldGenTallGrass(BLBlockRegistry.bottleBrushGrass, 1);
	private final static WorldGenGiantTreeDead GEN_DEAD_TREE = new WorldGenGiantTreeDead();
	private final static WorldGenHugeMushroom GEN_HUGE_MUSHROOM = new WorldGenHugeMushroom();
	private final static WorldGenFluidPool GEN_LIQUID_POOL = new WorldGenFluidPool();
	private final static WorldGenSmallHollowLog GEN_SMALL_HOLLOW_LOG = new WorldGenSmallHollowLog();
	private final static WorldGenSpeleothem GEN_SPELEOTHEM = new WorldGenSpeleothem();
	private final static WorldGenCaveGrass GEN_CAVE_GRASS = new WorldGenCaveGrass();
	private final static WorldGenThorns GEN_THORNS = new WorldGenThorns();
	private final static WorldGenCaveMoss GEN_CAVE_MOSS = new WorldGenCaveMoss();
	private final static WorldGenTallGrass GEN_SLUDGECREEP = new WorldGenTallGrass(BLBlockRegistry.sludgecreep, 1);
	private final static WorldGenTallGrass GEN_DEAD_WEEDWOOD_BUSH = new WorldGenTallGrass(BLBlockRegistry.deadWeedwoodBush, 1);
	private final static WorldGenCavePots GEN_CAVE_POTS = new WorldGenCavePots();
	private final static WorldGenSmallRuins GEN_SMALL_RUINS = new WorldGenSmallRuins();
	private final static WorldGenUnderGroundStructures GEN_UNDER_GROUND_STRUCTURES = new WorldGenUnderGroundStructures();
	private static final CubicBezier SPELEOTHEM_Y_CDF = new CubicBezier(0, 0.5F, 1, 0.2F);
	private static final CubicBezier CAVE_MOSS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier CAVE_POTS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier THORNS_Y_CDF = new CubicBezier(1, 0.5F, 1, -0.25F);
	private static final CubicBezier CAVE_GRASS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private final static WorldGenSpawnerStructure GEN_DUNGEON = new WorldGenSpawnerStructure();
	private final static WorldGenIdolHeads GEN_HEADS = new WorldGenIdolHeads();
	private final static WorldGenCragrockTower GEN_CRAGROCK_TOWER = new WorldGenCragrockTower();

	private final Random rand;
	private final int x, y, z;
	private final World world;
	private final boolean centerOffset;
	private ChunkProviderBetweenlands provider;

	public DecorationHelper(Random rand, World world, int x, int y, int z, boolean centerOffset) {
		this.rand = rand;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.centerOffset = centerOffset;
		provider = ((ChunkProviderBetweenlands) ((WorldServer) world).theChunkProviderServer.currentChunkProvider);
	}

	private final int offsetXZ() {
		return rand.nextInt(16) + (this.centerOffset ? 8 : 0);
	}

	private final boolean checkSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(this.world.getBlock(x, y - 1, z)) && this.world.isAirBlock(x, y, z);
	}

	private final boolean checkBelowWater(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(this.world.getBlock(x, y - 1, z));
	}

	public void generatePhragmites(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.phragmites, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.phragmites, 8, 2);
			}
		}
	}

	public void generateSludgecreep(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.DIRT, x, y, z)) {
				GEN_SLUDGECREEP.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateDeadWeedwoodBush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.DIRT, x, y, z)) {
				GEN_DEAD_WEEDWOOD_BUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateCardinalFlower(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.cardinalFlower, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.cardinalFlower, 8, 2);
			}
		}
	}

	public void generateBroomsedge(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.broomsedge, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.broomsedge, 8, 2);
			}
		}
	}

	public void generateTallCattail(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.tallCattail, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.tallCattail, 8, 2);
			}
		}
	}

	public void generateBoneset(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_BONESET.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateBlueEyedGrass(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_BLUE_EYED_GRASS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateBottleBrushGrass(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_BOTTLE_BRUSH_GRASS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateCopperIris(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_COPPER_IRIS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateBlueIris(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_BLUE_IRIS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateArrowArum(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_ARROW_ARUM.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateShoots(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SHOOTS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateMarshMallow(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_MARSH_MALLOW.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateButtonBush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_BUTTON_BUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSoftRush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SOFT_RUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateMilkweed(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_MILKWEED.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateMarshHibiscus(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_MARSH_HIBISCUS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generatePickerelWeed(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_PICKEREL_WEED.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSwampGrass(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SWAMP_TALL_GRASS.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSwampTallGrass(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.doubleSwampTallgrass, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.doubleSwampTallgrass, 8, 2);
			}
		}
	}


	public void generateSwampPlant(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SWAMP_PLANT.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateNettles(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.MIXED, x, y, z)) {
				GEN_NETTLE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateCattail(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_CATTAIL.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateVenusFlyTrap(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_VENUS_FLY_TRAP.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateVolarpad(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_VOLARPAD.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSundew(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y, z, BLBlockRegistry.sundew, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.sundew, 8, 2);
			}
		}
	}


	public void generatePitcherPlant(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y, z, BLBlockRegistry.pitcherPlant, 0, 2);
				world.setBlock(x, y+1, z, BLBlockRegistry.pitcherPlant, 8, 2);
			}
		}
	}

	public void generateRottenLog(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int length = rand.nextInt(5) + 4;
			int baseRadius = rand.nextInt(3) + 2;
			byte direction = (byte) rand.nextInt(2);
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();

			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) || this.checkBelowWater(SurfaceType.DIRT, x, y, z) && this.checkBelowWater(SurfaceType.WATER, x, y + 1, z))
				new WorldGenRottenLogs(length, baseRadius, direction).generate(world, rand, x, y, z);
		}
	}

	public void generateWeedwoodBush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_WEEDWOOD_BUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateBlackHatMushrooms(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_BLACK_HAT_MUSHROOMS.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateFlatHeadMushrooms(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_FLAT_HEAD_MUSHROOMS.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateWisp(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.WATER, x, y, z)) {
					BLBlockRegistry.wisp.generateBlock(this.world, x, y, z);
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.WATER, x, y, z)) {
					BLBlockRegistry.wisp.generateBlock(this.world, x, y, z);
				}
			}
		}
	}

	public void generateGiantWeedwoodTree(int rate) {
		if (rand.nextInt(rate) != 0) {
			return;
		}
		int x = this.x + this.offsetXZ();
		int y = 76;
		int z = this.z + this.offsetXZ();
		if (GEN_GIANT_TREE.generateTree(this.world, this.rand, x, y, z)) {
			return;
		}
	}

	public void generateDeadTree(int rate) {
		if (rand.nextInt(rate) != 0) {
			return;
		}
		int x = this.x + this.offsetXZ();
		int y = 76;
		int z = this.z + this.offsetXZ();
		if (GEN_DEAD_TREE.generateTree(this.world, this.rand, x, y, z)) {
			return;
		}
	}

	public void generateWeedwoodTree(int attempts) {
		if (canShortThingsGenerateHere()) {
			for (int i = 0; i < attempts; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) || this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)|| this.checkBelowWater(SurfaceType.DIRT, x, y, z) && this.checkBelowWater(SurfaceType.WATER, x, y + 1, z))
					GEN_WEEDWOOD_TREE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSmalWeedwoodTree(int attempts) {
		if (canShortThingsGenerateHere()) {
			for (int i = 0; i < attempts; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.DIRT, x, y, z) || this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)|| this.checkBelowWater(SurfaceType.DIRT, x, y, z) && this.checkBelowWater(SurfaceType.WATER, x, y + 1, z))
					GEN_SMAL_WEEDWOOD_TREE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSapTree(int attempts) {
		if (canShortThingsGenerateHere()) {
			for (int i = 0; i < attempts; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
					GEN_SAP_TREE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateHugeMushroom(int attempts) {
		if (canShortThingsGenerateHere()) {
			for (int i = 0; i < attempts; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
					GEN_HUGE_MUSHROOM.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateRubberTree(int attempts) {
		if (canShortThingsGenerateHere()) {
			for (int i = 0; i < attempts; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
					GEN_RUBBER_TREE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateWeepingBlue(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z)) {
				this.world.setBlock(x, y, z, BLBlockRegistry.weepingBlue, 0, 2);
				this.world.setBlock(x, y + 1, z, BLBlockRegistry.weepingBlue, 8, 2);
			}
		}
	}

	public void generateMireCoral(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.mireCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.mireCoral, 0, 3);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.mireCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.mireCoral, 0, 3);
					}
				}
			}
		}
	}

	public void generateWaterWeedsSmall(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.waterWeeds.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.waterWeeds, 0, 3);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.waterWeeds.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.waterWeeds, 0, 3);
					}
				}
			}
		}
	}

	public void generateDeepWaterCoral(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.deepWaterCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.deepWaterCoral, 0, 3);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.deepWaterCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.deepWaterCoral, 0, 3);
					}
				}
			}
		}
	}

	public void generateFlowerPatch(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y + 1, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(this.world, x, y, z)) {
						BlockWaterFlower.generateFlowerPatch(this.world, x, y, z, 35, 10);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(this.world, x, y, z)) {
						BlockWaterFlower.generateFlowerPatch(this.world, x, y, z, 35, 10);
					}
				}
			}
		}
	}

	public void generateWaterRoots(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					if (world.rand.nextInt(3) == 0) {
						BlockRoot.generateWaterRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					if (world.rand.nextInt(3) == 0) {
						BlockRoot.generateWaterRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		}
	}

	public void generateRoots(double probability, int patchProbability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				boolean hasSpace = blockAbove == Blocks.air && blockAbove2 == Blocks.air;
				if((cBlock == BLBlockRegistry.swampGrass || cBlock == BLBlockRegistry.deadGrass || cBlock == BLBlockRegistry.mud) && hasSpace) {
					if (world.rand.nextInt(patchProbability) == 0) {
						BlockRoot.generateRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				boolean hasSpace = blockAbove == Blocks.air && blockAbove2 == Blocks.air;
				if((cBlock == BLBlockRegistry.swampGrass || cBlock == BLBlockRegistry.deadGrass || cBlock == BLBlockRegistry.mud) && hasSpace) {
					if (world.rand.nextInt(patchProbability) == 0) {
						BlockRoot.generateRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		}
	}

	public void generateReeds(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
				} else if (block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if (BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y + 1, z)) {
						BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
				} else if (block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if (BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y + 1, z)) {
						BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
					}
				}
			}
		}
	}

	public void generateWaterWeeds(int attempts, int radius, int density) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			Block block = world.getBlock(x, y, z);
			Block blockAbove = world.getBlock(x, y + 1, z);
			if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater) {
				for(int i2 = 0; i2 < density; i2++) {
					int bx = x + world.rand.nextInt(radius) - radius/2;
					int by = y + world.rand.nextInt(radius) - radius/2;
					int bz = z + world.rand.nextInt(radius) - radius/2;
					if(Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y)+(bz-z)*(bz-z)) <= radius) {
						block = world.getBlock(bx, by, bz);
						blockAbove = world.getBlock(bx, by+1, bz);
						if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater) {
							for(int yo = 1; yo < this.rand.nextInt(WorldProviderBetweenlands.LAYER_HEIGHT - by + 1); yo++) {
								this.world.setBlock(bx, by+yo, bz, BLBlockRegistry.swampKelp);
							}
						}
					}
				}
			}
		}
	}

	public void generateOneDeep(int attempts, int radius, int density, BlockSwampWater uwBlock) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			Block block = world.getBlock(x, y, z);
			Block blockAbove = world.getBlock(x, y + 1, z);
			Block blockAbove2 = world.getBlock(x, y + 2, z);
			if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
				for(int i2 = 0; i2 < density; i2++) {
					int bx = x + world.rand.nextInt(radius) - radius/2;
					int by = y + world.rand.nextInt(radius) - radius/2;
					int bz = z + world.rand.nextInt(radius) - radius/2;
					if(Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y)+(bz-z)*(bz-z)) <= radius) {
						block = world.getBlock(bx, by, bz);
						blockAbove = world.getBlock(bx, by + 1, bz);
						blockAbove2 = world.getBlock(bx, by + 2, bz);
						if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
							this.world.setBlock(x, y+1, z, uwBlock);
						}
					}
				}
			}
		}
	}

	public void generateMossPatch(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int xx = x + offsetXZ();
			int yy = 30 + rand.nextInt(80);
			int zz = z + offsetXZ();

			if (world.isAirBlock(xx, yy, zz))
				GEN_MOSS_PATCH.generate(world, rand, xx, yy, zz);
		}
	}

	public void generateLichenPatch(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int xx = x + offsetXZ();
			int yy = 30 + rand.nextInt(80);
			int zz = z + offsetXZ();

			if (world.isAirBlock(xx, yy, zz))
				GEN_LICHEN_PATCH.generate(world, rand, xx, yy, zz);
		}
	}

	public void generateTarPool(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int xx = x + offsetXZ();
			int yy = 6 + rand.nextInt(80);
			int zz = z + offsetXZ();

			if (checkSurface(SurfaceType.MIXED, xx, yy, zz)) {
				GEN_LIQUID_POOL.prepare((rand.nextDouble() + 0.7D) * 1.5D);
				GEN_LIQUID_POOL.generatePool(world, rand, xx, yy, zz, BLBlockRegistry.tarFluid, false);
			}
		}
	}

	public void generateStagnantWaterPool(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int xx = x + offsetXZ();
			int yy = 6 + rand.nextInt(40);
			int zz = z + offsetXZ();

			if (checkSurface(SurfaceType.UNDERGROUND, xx, yy, zz)) {
				GEN_LIQUID_POOL.prepare((rand.nextDouble() + 0.7D) * 1.5D);
				GEN_LIQUID_POOL.generatePool(world, rand, xx, yy, zz, BLBlockRegistry.stagnantWaterFluid, true);
			}
		}
	}

	public void generateSmallRuins(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_SMALL_RUINS.generate(world, rand, x, y, z);
		}
	}

	public void generateUnderGroundStructures(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int x = this.x + this.offsetXZ();
			int y = WorldProviderBetweenlands.CAVE_WATER_HEIGHT + rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_UNDER_GROUND_STRUCTURES.generate(world, rand, x, y, z);
		}
	}

	public void generateDungeon(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_DUNGEON.generate(world, rand, x, y, z);
		}
	}
	
	public void generateHeads(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.MIXED, x, y, z) && checkSurface(SurfaceType.MIXED, x - 2, y, z - 2) && checkSurface(SurfaceType.MIXED, x + 2, y, z - 2) && checkSurface(SurfaceType.MIXED, x - 2, y, z + 2) && checkSurface(SurfaceType.MIXED, x + 2, y, z + 2))
				GEN_HEADS.generate(world, rand, x, y, z);
		}
	}

	public void generateSmallHollowLog(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();

			if (checkSurface(SurfaceType.MIXED, x, y, z))
				GEN_SMALL_HOLLOW_LOG.generate(world, rand, x, y, z);
		}
	}


	public void generateCragrockTower(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int x = this.x + this.offsetXZ() - 3;
			int y = 86;
			int z = this.z + this.offsetXZ() - 3;
			GEN_CRAGROCK_TOWER.generate(world, rand, x, y, z);
		}
	}

	public void populateCave() {
		generateSpeleothems(60);
		generateCavePots(20);
		generateThorns(200);
		generateCaveMoss(100);
		generateCaveGrass(120);
	}

	public void generateCaveGrass(int attempts) {
		while (attempts --> 0) {
			int x = this.x + offsetXZ();
			float v = CAVE_GRASS_Y_CDF.eval(rand.nextFloat());
			int y = (int) (v * (WorldProviderBetweenlands.PITSTONE_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
			int z = this.z + offsetXZ();
			GEN_CAVE_GRASS.generate(world, rand, x, y, z);
		}
	}

	public void generateSpeleothems(int attempts) {
		attempts += getSpeleothemAttemptAdditive();
		while (attempts --> 0) {
			int x = this.x + offsetXZ();
			float v = SPELEOTHEM_Y_CDF.eval(rand.nextFloat());
			int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
			int z = this.z + offsetXZ();
			GEN_SPELEOTHEM.generate(world, rand, x, y, z);
		}
	}

	public void generateThorns(int attempts) {
		while (attempts --> 0) {
			int x = this.x + offsetXZ();
			float v = THORNS_Y_CDF.eval(rand.nextFloat());
			int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
			int z = this.z + offsetXZ();
			GEN_THORNS.generate(world, rand, x, y, z);
		}
	}

	public void generateCaveMoss(int attempts) {
		while (attempts --> 0) {
			int x = this.x + offsetXZ();
			float v = CAVE_MOSS_Y_CDF.eval(rand.nextFloat());
			int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
			int z = this.z + offsetXZ();
			GEN_CAVE_MOSS.generate(world, rand, x, y, z);
		}
	}

	public void generateCavePots(int attempts){
		while (attempts --> 0){
			int x = this.x + offsetXZ();
			float v = CAVE_POTS_Y_CDF.eval(rand.nextFloat());
			int y = (int) (v * (70 - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
			int z = this.z + offsetXZ();
			GEN_CAVE_POTS.generate(world, rand, x, y, z);
		}
	}

	private boolean canShortThingsGenerateHere() {
		return provider.evalTreeNoise(x * 0.01, z * 0.01) > -0.25;
	}

	private int getSpeleothemAttemptAdditive() {
		return (int) ((provider.evalSpeleothemDensityNoise(x * 0.03, z * 0.03) * 0.5 + 0.5) * 20);
	}
}
