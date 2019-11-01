package thebetweenlands.common.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.plant.BlockHangingPlant;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick;
import thebetweenlands.common.block.structure.BlockDecayPitGroundChain;
import thebetweenlands.common.block.structure.BlockMudBrickRoof;
import thebetweenlands.common.block.structure.BlockMudTiles;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.entity.EntityCCGroundSpawner;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityMovingWall;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.common.world.biome.BiomeSludgePlainsClearing;
import thebetweenlands.common.world.gen.feature.structure.utils.MazeGenerator;
import thebetweenlands.common.world.gen.feature.structure.utils.PerfectMazeGenerator;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationAmbience.EnumLocationAmbience;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.TimeMeasurement;

public class WorldGenSludgeWormDungeon extends WorldGenerator {
	private SludgeWormMazeBlockHelper blockHelper;
	private SludgeWormMazeMicroBuilds microBuild;
	private LightTowerBuildParts lightTowerBuild;
	private DecayPitBuildParts decayPitBuild;

	private SharedLootPoolStorage lootStorage;
	private LocationSludgeWormDungeon location;
	private LocationStorage locationBarrisheeLair;
	private LocationStorage locationCrypt;
	private Random lootRng;

	private static final ThreadLocal<Boolean> CASCADING_GEN_MUTEX = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	
	public WorldGenSludgeWormDungeon() {
		super(false);
		this.blockHelper = new SludgeWormMazeBlockHelper(this);
		this.microBuild = new SludgeWormMazeMicroBuilds(this);
		this.lightTowerBuild = new LightTowerBuildParts(this);
		this.decayPitBuild = new DecayPitBuildParts(this);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		if(CASCADING_GEN_MUTEX.get()) {
			return false;
		}
		
		CASCADING_GEN_MUTEX.set(true);
		
		try {
			//If in sludge plains clearinig use biome base height as gen Y pos
			Biome biome = world.getBiome(pos);
			if(biome instanceof BiomeSludgePlainsClearing) {
				pos = new BlockPos(pos.getX(), (int)biome.getBaseHeight() + 1, pos.getZ());
			}
			
			//Check for other sludge worm dungeons
			List<LocationSludgeWormDungeon> dungeonLocations = BetweenlandsWorldStorage.forWorld(world).getLocalStorageHandler().getLocalStorages(LocationSludgeWormDungeon.class, new AxisAlignedBB(pos.getX() - 3, pos.getY() + 30, pos.getZ() - 3, pos.getX() + 29, pos.getY() - 58, pos.getZ() + 29), l -> true);
			if(!dungeonLocations.isEmpty()) {
				return false;
			}
			
			this.lootRng = new Random(rand.nextLong());
	
			//conditions blah, blah...
			//TimeMeasurement.start("Full_Mudgeon");
	
			//Locations must be generated first such that location guard can be used
			//TimeMeasurement.start("World_Locations");
			this.generateLocations(world, rand, pos);
			//TimeMeasurement.finish("World_Locations");
	
			//TimeMeasurement.start("Maze");
			makeMaze(world, rand, pos);
			//TimeMeasurement.finish("Maze");
	
			//TimeMeasurement.start("Tower");
			generateTower(world, rand, pos.down().add(12, 0, 12));
			//TimeMeasurement.finish("Tower");
	
			//TimeMeasurement.start("Crypt");
			generateCryptCrawlerDungeon(world, rand, pos.down(25).add(-3, 0, -3));
			//TimeMeasurement.finish("Crypt");
	
			//TimeMeasurement.start("Pit");
			generateDecayPit(world, rand, pos.down(44).add(14, 0, 14));
			//TimeMeasurement.finish("Pit");
	
			generateDecayPitEntrance(world, rand, pos.down(59).add(-3, 0, -3));
			
			protectTowerInside(world, rand, pos.down().add(12, 0, 12));
			
			//TimeMeasurement.finish("Full_Mudgeon");

			return true;
		} finally {
			CASCADING_GEN_MUTEX.set(false);
		}
	}

	public void generateLocations(World world, Random rand, BlockPos pos) {
		//Main location for spawning, fog, loot etc.
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);

		//Shared loot storage
		lootStorage = new SharedLootPoolStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), rand.nextLong());
		worldStorage.getLocalStorageHandler().addLocalStorage(lootStorage);
		
		//Main location
		location = new LocationSludgeWormDungeon(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos));
		location.addBounds(new AxisAlignedBB(pos.getX() - 3, pos.getY() + 30, pos.getZ() - 3, pos.getX() + 29, pos.getY() - 58, pos.getZ() + 29));
		location.setLayer(0);
		location.setSeed(rand.nextLong());
		location.setStructurePos(pos);
		location.setVisible(true);
		location.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);

		//Locations below are just for segmentation and location titles

		//Maze
		LocationStorage locationMaze = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "sludge_worm_dungeon_maze", EnumLocationType.SLUDGE_WORM_DUNGEON);
		locationMaze.addBounds(new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 29, pos.getY() - 8 * 5 - 3, pos.getZ() + 29));
		locationMaze.setLayer(1);
		locationMaze.setSeed(rand.nextLong());
		locationMaze.setVisible(true);
		locationMaze.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationMaze);

		//Barrishee Lair
		locationBarrisheeLair = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "sludge_worm_dungeon_barrishee_lair", EnumLocationType.SLUDGE_WORM_DUNGEON);
		locationBarrisheeLair.addBounds(new AxisAlignedBB(pos.getX() + 20, pos.getY() - 24, pos.getZ() - 3, pos.getX() + 29, pos.getY() - 19, pos.getZ()));
		locationBarrisheeLair.setLayer(1);
		locationBarrisheeLair.setSeed(rand.nextLong());
		locationBarrisheeLair.setVisible(true);
		locationBarrisheeLair.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationBarrisheeLair);

		//Crypt Walkways
		LocationStorage locationWalkays = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "sludge_worm_dungeon_winding_walkways", EnumLocationType.SLUDGE_WORM_DUNGEON);
		locationWalkays.addBounds(
				new AxisAlignedBB(pos.getX() - 3, pos.getY() - 43, pos.getZ() - 3, pos.getX(), pos.getY() - 24, pos.getZ() + 29),
				new AxisAlignedBB(pos.getX(), pos.getY() - 43, pos.getZ() - 3, pos.getX() + 29 , pos.getY() - 24, pos.getZ())
				);
		locationWalkays.setLayer(1);
		locationWalkays.setSeed(rand.nextLong());
		locationWalkays.setVisible(true);
		locationWalkays.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationWalkays);

		//Crypt Tunnels
		LocationStorage locationCryptTunnels = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "sludge_worm_dungeon_crypt_tunnels", EnumLocationType.SLUDGE_WORM_DUNGEON);
		locationCryptTunnels.addBounds(
				//entrance to tunnels
				new AxisAlignedBB(pos.getX() + 4, pos.getY() - 50, pos.getZ() + 28, pos.getX() - 3, pos.getY() - 43, pos.getZ() + 23),
				//tunnels, in order starting from entrance
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 45, pos.getZ() + 23, pos.getX() + 2 , pos.getY() - 50, pos.getZ() + 21),
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 50, pos.getZ() + 20, pos.getX() + 1 , pos.getY() - 46, pos.getZ() + 21),
				new AxisAlignedBB(pos.getX() + 1 , pos.getY() - 47, pos.getZ() + 20, pos.getX() - 3 , pos.getY() - 51, pos.getZ() + 18),
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 52, pos.getZ() + 18, pos.getX() + 1 , pos.getY() - 48, pos.getZ() + 16),
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 49, pos.getZ() + 16, pos.getX() + 1 , pos.getY() - 53, pos.getZ() + 12),
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 51, pos.getZ() + 12, pos.getX() + 1 , pos.getY() - 57, pos.getZ() + 10),
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 52, pos.getZ() + 10, pos.getX() + 2 , pos.getY() - 57, pos.getZ() + 7 ),
				new AxisAlignedBB(pos.getX() - 3 , pos.getY() - 53, pos.getZ() + 7 , pos.getX() + 2 , pos.getY() - 57, pos.getZ() + 4 ),
				new AxisAlignedBB(pos.getX() - 2 , pos.getY() - 57, pos.getZ() + 4 , pos.getX() + 4 , pos.getY() - 51, pos.getZ() - 1 ),
				new AxisAlignedBB(pos.getX() + 3 , pos.getY() - 57, pos.getZ() - 2 , pos.getX() + 23, pos.getY() - 54, pos.getZ() + 1 ),
				new AxisAlignedBB(pos.getX() + 22, pos.getY() - 57, pos.getZ() - 1 , pos.getX() + 28, pos.getY() - 51, pos.getZ() + 4 ),
				new AxisAlignedBB(pos.getX() + 28, pos.getY() - 57, pos.getZ() + 4 , pos.getX() + 25, pos.getY() - 54, pos.getZ() + 22),
				new AxisAlignedBB(pos.getX() + 28, pos.getY() - 57, pos.getZ() + 22, pos.getX() + 22, pos.getY() - 51, pos.getZ() + 27),
				new AxisAlignedBB(pos.getX() + 23, pos.getY() - 57, pos.getZ() + 28, pos.getX() + 3 , pos.getY() - 54, pos.getZ() + 25),
				new AxisAlignedBB(pos.getX() + 4 , pos.getY() - 57, pos.getZ() + 27, pos.getX() - 2 , pos.getY() - 51, pos.getZ() + 22)
				);
		locationCryptTunnels.setLayer(1);
		locationCryptTunnels.setSeed(rand.nextLong());
		locationCryptTunnels.setVisible(true);
		locationCryptTunnels.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationCryptTunnels);

		//Crypt
		locationCrypt = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "sludge_worm_dungeon_crypt", EnumLocationType.SLUDGE_WORM_DUNGEON);
		locationCrypt.addBounds(new AxisAlignedBB(pos.getX() - 3, pos.getY() - 57, pos.getZ() + 22, pos.getX() + 7, pos.getY() - 54, pos.getZ() + 12));
		locationCrypt.setLayer(1);
		locationCrypt.setSeed(rand.nextLong());
		locationCrypt.setVisible(true);
		locationCrypt.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationCrypt);

		//Pit
		LocationStorage locationPit = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "sludge_worm_dungeon_pit", EnumLocationType.SLUDGE_WORM_DUNGEON);
		locationPit.addBounds(
				//entrance
				new AxisAlignedBB(pos.getX() + 1 , pos.getY() - 43, pos.getZ() + 1 , pos.getX() + 9 , pos.getY() - 48, pos.getZ() + 4 ),
				//dome
				new AxisAlignedBB(pos.getX() + 1 , pos.getY() - 49, pos.getZ() + 1 , pos.getX() + 28, pos.getY() - 43, pos.getZ() + 22),
				new AxisAlignedBB(pos.getX() + 4 , pos.getY() - 49, pos.getZ() + 22, pos.getX() + 25, pos.getY() - 43, pos.getZ() + 28),
				new AxisAlignedBB(pos.getX() + 3 , pos.getY() - 54, pos.getZ() + 19, pos.getX() + 26, pos.getY() - 49, pos.getZ() + 9 ),
				new AxisAlignedBB(pos.getX() + 4 , pos.getY() - 49, pos.getZ() + 9 , pos.getX() + 24, pos.getY() - 54, pos.getZ() + 4 ),
				new AxisAlignedBB(pos.getX() + 8 , pos.getY() - 49, pos.getZ() + 3 , pos.getX() + 21, pos.getY() - 52, pos.getZ() + 4 ),
				new AxisAlignedBB(pos.getX() + 3 , pos.getY() - 49, pos.getZ() + 19, pos.getX() + 26, pos.getY() - 54, pos.getZ() + 22),
				new AxisAlignedBB(pos.getX() + 6 , pos.getY() - 49, pos.getZ() + 22, pos.getX() + 22, pos.getY() - 54, pos.getZ() + 26),
				new AxisAlignedBB(pos.getX() + 22, pos.getY() - 49, pos.getZ() + 22, pos.getX() + 23, pos.getY() - 51, pos.getZ() + 23),
				new AxisAlignedBB(pos.getX() + 21, pos.getY() - 54, pos.getZ() + 8 , pos.getX() + 8 , pos.getY() - 58, pos.getZ() + 21)
				);
		locationPit.setLayer(1);
		locationPit.setAmbience(
				new LocationAmbience(EnumLocationAmbience.SLUDGE_WORM_DUNGEON)
				.setFogColor(new int[] {120, 120, 120}).setFogRange(4.0f, 45.0f)
				.setCaveFog(false)
				);
		locationPit.setSeed(rand.nextLong());
		locationPit.setVisible(true);
		locationPit.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationPit);
	}

	private void protectTowerInside(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int height = 16;
		int radius = 9;

		for (int yy = y; y + height >= yy; yy++) {
			for (int i = radius * -1; i <= radius; ++i) {
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;

					if (Math.round(Math.sqrt(dSq)) <= radius) {
						this.location.getGuard().setGuarded(world, new BlockPos(x + i, yy, z + j), true);
					}
				}
			}
		}
	}
	
	private void generateCryptCrawlerDungeon(World world, Random rand, BlockPos pos) {
		for (int x = 0; x < 32; x ++)
			for (int z = 0; z < 3; z ++)
				for (int y = -18; y < 0; y ++)
					this.setBlockAndNotifyAdequately(world, pos.add(x, y, z), Blocks.AIR.getDefaultState());

		for (int x = 0; x < 3; x ++)
			for (int z = 3; z < 32; z ++)
				for (int y = -18; y < 0; y ++)
					this.setBlockAndNotifyAdequately(world, pos.add(x, y, z), Blocks.AIR.getDefaultState());

		//temp
		/*		for (int x = 0; x < 32; x ++)
			for (int z = 0; z < 32; z ++)
				for (int y = -33; y < -19; y ++)
				this.setBlockAndNotifyAdequately(world, pos.add(x, y, z), Blocks.AIR.getDefaultState());
		 */

		//S
		for (int x = 1; x < 32; x++)
			for (int z = 1; z < 3; z++) {
				this.setBlockAndNotifyAdequately(world, pos.add(x, 0, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -6, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -12, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -18, z), blockHelper.COMPACTED_MUD);

				if (rand.nextBoolean())
					this.setBlockAndNotifyAdequately(world, pos.add(x, -1, z), blockHelper.COMPACTED_MUD);
				if (rand.nextBoolean())
					this.setBlockAndNotifyAdequately(world, pos.add(x, -7, z), blockHelper.COMPACTED_MUD);
				if (rand.nextBoolean())
					this.setBlockAndNotifyAdequately(world, pos.add(x, -13, z), blockHelper.COMPACTED_MUD);

				this.setBlockAndNotifyAdequately(world, pos.add(x, -5, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -11, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -17, z), blockHelper.COMPACTED_MUD);
			}
		//E
		for (int x = 1; x < 3; x++)
			for (int z = 3; z < 32; z++) {
				this.setBlockAndNotifyAdequately(world, pos.add(x, 0, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -6, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -12, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -18, z), blockHelper.COMPACTED_MUD);

				if (rand.nextBoolean())
					this.setBlockAndNotifyAdequately(world, pos.add(x, -1, z), blockHelper.COMPACTED_MUD);
				if (rand.nextBoolean())
					this.setBlockAndNotifyAdequately(world, pos.add(x, -7, z), blockHelper.COMPACTED_MUD);
				if (rand.nextBoolean())
					this.setBlockAndNotifyAdequately(world, pos.add(x, -13, z), blockHelper.COMPACTED_MUD);

				this.setBlockAndNotifyAdequately(world, pos.add(x, -5, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -11, z), blockHelper.COMPACTED_MUD);
				this.setBlockAndNotifyAdequately(world, pos.add(x, -17, z), blockHelper.COMPACTED_MUD);
			}

		microBuild.buildCryptCrawlerWalkways(world, pos, EnumFacing.SOUTH, rand);

		// Plants S
		addHangingPlants(world, pos.add(1, 0, 1), rand, 32, 0, 3);
		addHangingPlants(world, pos.add(1, 0, 1), rand, 32, -6, 3);
		addHangingPlants(world, pos.add(1, 0, 1), rand, 32, -12, 3);

		addGroundPlants(world, pos.add(1, 0, 1), rand, 32, -5, 3, true, false, true, true);
		addGroundPlants(world, pos.add(1, 0, 1), rand, 32, -11, 3, true, false, true, true);
		addGroundPlants(world, pos.add(1, 0, 1), rand, 32, -17, 3, true, false, true, true);

		addEdgePlant(world, pos.add(1, 0, 1), rand, 32, -5, 3);
		addEdgePlant(world, pos.add(1, 0, 1), rand, 32, -11, 3);
		addEdgePlant(world, pos.add(1, 0, 1), rand, 32, -17, 3);

		addWallPlants(world, pos.add(0, -3, 0), rand, 32, 2, 1, EnumFacing.SOUTH);
		addWallPlants(world, pos.add(0, -9, 0), rand, 32, 2, 1, EnumFacing.SOUTH);
		addWallPlants(world, pos.add(0, -15, 0), rand, 32, 2, 1, EnumFacing.SOUTH);

		addWallPlants(world, pos.add(0, -3, 3), rand, 32, 2, 1, EnumFacing.NORTH);
		addWallPlants(world, pos.add(0, -9, 3), rand, 32, 2, 1, EnumFacing.NORTH);
		addWallPlants(world, pos.add(0, -15, 3), rand, 32 ,2, 1, EnumFacing.NORTH);

		//Plants E
		addHangingPlants(world, pos.add(1, 0, 3), rand, 3, 0, 32);
		addHangingPlants(world, pos.add(1, 0, 3), rand, 3, -6, 32);
		addHangingPlants(world, pos.add(1, 0, 3), rand, 32, -12, 3);

		addGroundPlants(world, pos.add(1, 0, 3), rand, 3, -5, 32, true, false, true, true);
		addGroundPlants(world, pos.add(1, 0, 3), rand, 3, -11, 32, true, false, true, true);
		addGroundPlants(world, pos.add(1, 0, 3), rand, 3, -17, 32, true, false, true, true);

		addEdgePlant(world, pos.add(1, 0, 3), rand, 3, -5, 32);
		addEdgePlant(world, pos.add(1, 0, 3), rand, 3, -11, 32);
		addEdgePlant(world, pos.add(1, 0, 3), rand, 3, -17, 32);

		addWallPlants(world, pos.add(0, -3, 0), rand, 1, 2, 32, EnumFacing.EAST);
		addWallPlants(world, pos.add(0, -9, 0), rand, 1, 2, 32, EnumFacing.EAST);
		addWallPlants(world, pos.add(0, -15, 0), rand, 1, 2, 32, EnumFacing.EAST);

		addWallPlants(world, pos.add(3, -3, 3), rand, 1, 2, 32 ,EnumFacing.WEST);
		addWallPlants(world, pos.add(3, -9, 3), rand, 1, 2, 32, EnumFacing.WEST);
		addWallPlants(world, pos.add(3, -15, 3), rand, 1, 2, 32, EnumFacing.WEST);

		addSphericalChamber(world, rand, pos.add(4, -29, 27)); // entrance
		addSphericalChamber(world, rand, pos.add(4, -29, 4));

		addSphericalChamber(world, rand, pos.add(27, -29, 4));
		addSphericalChamber(world, rand, pos.add(27, -29, 27));

		microBuild.buildCryptCrawlerBottomTunnels(world, pos.add(0, -33, 0), EnumFacing.SOUTH, rand);// zeros
		microBuild.buildCryptCrawlerBottomTunnels(world, pos.add(0, -33, 31), EnumFacing.EAST, rand);
		microBuild.buildCryptCrawlerBottomTunnels(world, pos.add(31, -33, 0), EnumFacing.WEST, rand);
		microBuild.buildCryptCrawlerBottomTunnels(world, pos.add(31, -33, 31), EnumFacing.NORTH, rand);

		microBuild.buildCryptCrawlerTunnelsConnect(world, pos.add(0, -33, 0), EnumFacing.SOUTH, rand);

		for (int y = -33; y < -23; y++) {
			addGroundPlants(world, pos.add(1, y, 1), rand, 32, 0, 32, false, false, true, true);
			addEdgePlant(world, pos.add(1, y, 1), rand, 32, 0, 32);
		}

		for (int y = -26; y < -18; y++)
			addHangingPlants(world, pos.add(1, y, 1), rand, 32, 0, 32);
	}

	public void addGroundPlants(World world, BlockPos pos, Random rand, int x, int y, int z, boolean addMudNoise, boolean addMoss, boolean addWeeds, boolean addMushrooms) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				if (addMudNoise)
					if (plantingChance(rand) && isPlantableAbove(world, pos.add(horizontalX, y, horizontalZ)))
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y, horizontalZ), blockHelper.COMPACTED_MUD_SLAB);
				if (isPlantableAbove(world, pos.add(horizontalX, y, horizontalZ)))
					if (addWeeds && plantingChance(rand))
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), getRandomFloorPlant(rand));
					else if (addMushrooms && plantingChance(rand))
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), getRandomMushroom(rand));
					else if (addMoss && rand.nextBoolean())
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), blockHelper.MOSS.withProperty(BlockMoss.FACING, EnumFacing.UP));
			}
	}

	public void addHangingPlants(World world, BlockPos pos, Random rand, int x, int y, int z) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				if (plantingChance(rand) && isPlantableBelow(world, pos.add(horizontalX, y, horizontalZ))) {
					IBlockState plant = getRandomHangingPlant(rand);
					this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y - 1, horizontalZ), plant);
					if (plantingChance(rand) && world.isAirBlock(pos.add(horizontalX, y - 2, horizontalZ))) {
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y - 2, horizontalZ), plant);
						if (plantingChance(rand) && world.isAirBlock(pos.add(horizontalX, y - 3, horizontalZ)))
							this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y - 3, horizontalZ), plant);
					}
				}
			}
	}

	public void addWallPlants(World world, BlockPos pos, Random rand, int x, int y, int z, EnumFacing facing) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++)
				for (int vertical = 0; vertical < y; vertical++)
					if (plantingChance(rand) && isPlantableWall(world, pos.add(horizontalX, vertical, horizontalZ), facing))
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, vertical, horizontalZ).offset(facing), blockHelper.MOSS.withProperty(BlockMoss.FACING, facing));
	}

	public void addEdgePlant(World world, BlockPos pos, Random rand, int x, int y, int z) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					if (world.getBlockState(pos.add(horizontalX, y + 1, horizontalZ).offset(facing)).isSideSolid(world, pos.add(horizontalX, y + 1, horizontalZ).offset(facing), facing.getOpposite())) {
						if (plantingChance(rand) && isPlantableAbove(world, pos.add(horizontalX, y, horizontalZ)))
							this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), blockHelper.getRandomEdgePlant(rand, facing.getOpposite()));
					}
				}
			}
	}

	public void addSphericalChamber(World world, Random rand, BlockPos pos) {
		for (int xx = - 4; xx <= 4; xx++) {
			for (int zz = - 4; zz <= 4; zz++) {
				for (int yy = -4; yy <= 4; yy++) {
					double dSqSphere = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);
					if (Math.round(Math.sqrt(dSqSphere)) <= 4)
						if (dSqSphere >= Math.pow(3, 2.0D))
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), blockHelper.COMPACTED_MUD);
						else
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), Blocks.AIR.getDefaultState());
				}
			}
		}

		this.setBlockAndNotifyAdequately(world, pos.add(0, -4, 0), blockHelper.COMPACTED_MUD); // something other here maybe?
		this.setBlockAndNotifyAdequately(world, pos.add(0, -3, 0), blockHelper.AIR);
		this.setBlockAndNotifyAdequately(world, pos.add(0, 2, 0), blockHelper.HANGER);
		this.setBlockAndNotifyAdequately(world, pos.add(0, 1, 0), blockHelper.HANGER);
		this.setBlockAndNotifyAdequately(world, pos, blockHelper.getRandomBeam(EnumFacing.NORTH, rand, 0, 2, false));
		this.setBlockAndNotifyAdequately(world, pos.add(-1, -3, -1), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.NORTH).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));
		this.setBlockAndNotifyAdequately(world, pos.add(0, -3, -1), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.NORTH).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));
		this.setBlockAndNotifyAdequately(world, pos.add(1, -3, -1), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.NORTH).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));
		this.setBlockAndNotifyAdequately(world, pos.add(-1, -3, 1), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.SOUTH).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));
		this.setBlockAndNotifyAdequately(world, pos.add(0, -3, 1), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.SOUTH).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));
		this.setBlockAndNotifyAdequately(world, pos.add(1, -3, 1), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.SOUTH).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));

		this.setBlockAndNotifyAdequately(world, pos.add(-1, -3, 0), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.WEST).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));
		this.setBlockAndNotifyAdequately(world, pos.add(1, -3, 0), blockHelper.MUD_BRICK_ROOF.withProperty(BlockMudBrickRoof.FACING, EnumFacing.EAST).withProperty(BlockMudBrickRoof.HALF, EnumHalf.BOTTOM));

		addHangingBlockEntity(world, pos);
		addCCGroundSpawnerEntity(world, pos.add(0, -3, 0));
	}

	public boolean plantingChance(Random rand) {
		return rand.nextBoolean() && rand.nextBoolean();
	}

	public boolean isPlantableAbove(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return SoilHelper.canSustainPlant(state) && world.isAirBlock(pos.up());
	}

	public boolean isPlantableBelow(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return SoilHelper.canSustainPlant(state) && world.isAirBlock(pos.down());
	}

	public boolean isPlantableWall(World world, BlockPos pos, EnumFacing facing) {
		IBlockState state = world.getBlockState(pos);
		return state.isFullBlock() && world.isAirBlock(pos.offset(facing));
	}

	public void generateTower(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int height = 16;
		int radius = 9;
		int radiusMud = 15;
		int level1 = 0;
		int level2 = 8;
		int level3 = 16;

		for (int yy = y + 1; y + 1 + height >= yy; yy++) {
			for (int i = radiusMud * -1; i <= radiusMud; ++i) {
				for (int j = radiusMud * -1; j <= radiusMud; ++j) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), Blocks.AIR.getDefaultState());
				}
			}
		}

		for (int i = radiusMud * -1; i <= radiusMud; ++i) {
			for (int j = radiusMud * -1; j <= radiusMud; ++j) {
				double dSq = i * i + j * j;
				if (Math.round(Math.sqrt(dSq)) == radiusMud -1)
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, y, z + j), blockHelper.getMudBricksForLevel(rand, 0, 1));
				if (Math.round(Math.sqrt(dSq)) == radiusMud)
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, y, z + j), rand.nextInt(3) == 0 ? blockHelper.getMudSlabsForLevel(rand, 0, EnumBlockHalfBL.BOTTOM) : blockHelper.getMudBricksForLevel(rand, 0, 1));
			}
		}

		for (int yy = y; y + height >= yy; yy++) {
			for (int i = radius * -1; i <= radius; ++i) {
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;

					if (Math.round(Math.sqrt(dSq)) == radius && yy - y < level2)
						this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.PITSTONE_BRICKS);

					if (Math.round(Math.sqrt(dSq)) == radius && yy - y > level2 && yy - y < level3) {
						if(yy == y + level2 + 1)
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.getMudBricksForLevel(rand, 0, 2));
						else if(yy == y + level2 + 6)
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.getMudBricksForLevel(rand, 0, 3));
						else
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.getMudBricksForLevel(rand, 0, 1));
					}

					if (yy == y + level1)
						if (Math.round(Math.sqrt(dSq)) <= radius - 8)
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.PITSTONE_TILES);

					if (yy == y + level2) {
						if (Math.round(Math.sqrt(dSq)) == radius - 8)
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.PITSTONE_TILES);
						if (Math.round(Math.sqrt(dSq)) == radius)
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.getRandomBeam(EnumFacing.SOUTH, rand, 0, 0, false));
					}

					if (yy == y + level3)
						if (Math.round(Math.sqrt(dSq)) == radius)
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), blockHelper.getRandomBeam(EnumFacing.SOUTH, rand, 0, 0, false));
				}
			}
		}

		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.SOUTH, rand, level1, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.EAST, rand, level1, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.NORTH, rand, level1, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.WEST, rand, level1, 0);

		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.SOUTH, rand, level2, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.EAST, rand, level2, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.NORTH, rand, level2, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.WEST, rand, level2, 0);

		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.SOUTH, rand, level3, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.EAST, rand, level3, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.NORTH, rand, level3, 0);
		lightTowerBuild.addTowerFloor(world, pos, EnumFacing.WEST, rand, level3, 0);

		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.SOUTH, rand, level1, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.SOUTH, rand, level2, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.EAST, rand, level1, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.EAST, rand, level2, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.NORTH, rand, level1, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.NORTH, rand, level2, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.WEST, rand, level1, 0);
		lightTowerBuild.addTowerDoorways(world, pos, EnumFacing.WEST, rand, level2, 0);

		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.SOUTH, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.EAST, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.NORTH, rand, level3, 0, true);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.EAST, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.NORTH, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.WEST, rand, level3, 0, true);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.NORTH, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.WEST, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.SOUTH, rand, level3, 0, true);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.WEST, rand, level1, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.SOUTH, rand, level2, 0, false);
		lightTowerBuild.buildsSpiralStairPart(world, pos, EnumFacing.EAST, rand, level3, 0, true);

		lightTowerBuild.addLightBeams(world, pos, EnumFacing.SOUTH, rand, level1, 0);
		lightTowerBuild.addLightBeams(world, pos, EnumFacing.SOUTH, rand, level2, 0);
		lightTowerBuild.addLightBeams(world, pos, EnumFacing.SOUTH, rand, level3, 0);

		lightTowerBuild.buildsMazeGate(world, pos.add(12, 0, 10), EnumFacing.WEST, rand, level1, 0);
	}

	public void generateDecayPit(World world, Random rand, BlockPos pos) {
		for (int xx = - 14; xx <= 14; xx++) {
			for (int zz = - 14; zz <= 14; zz++) {
				for (int yy = 0; yy > -16; yy--) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);
					if (Math.round(Math.sqrt(dSqDome)) < 15)
						this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), Blocks.AIR.getDefaultState());
				}
			}
		}

		decayPitBuild.buildMainAreaPart(world, pos.down(14), EnumFacing.SOUTH, rand, 0, 0);
		decayPitBuild.buildMainAreaPart(world, pos.down(14), EnumFacing.EAST, rand, 0, 0);
		decayPitBuild.buildMainAreaPart(world, pos.down(14), EnumFacing.NORTH, rand, 0, 0);
		decayPitBuild.buildMainAreaPart(world, pos.down(14), EnumFacing.WEST, rand, 0, 0);
		decayPitBuild.addSpikes(world, pos.down(14), EnumFacing.SOUTH, rand, 0, 0);
		decayPitBuild.addSpikes(world, pos.down(14), EnumFacing.EAST, rand, 0, 0);
		decayPitBuild.addSpikes(world, pos.down(14), EnumFacing.NORTH, rand, 0, 0);
		decayPitBuild.addSpikes(world, pos.down(14), EnumFacing.WEST, rand, 0, 0);
		this.setBlockAndNotifyAdequately(world, pos.down(14), BlockRegistry.DECAY_PIT_CONTROL.getDefaultState());
		this.setBlockAndNotifyAdequately(world, pos.up(1), BlockRegistry.DECAY_PIT_HANGING_CHAIN.getDefaultState());

		for (int y = 0; y < 7; y++) {
			addGroundPlants(world, pos.down(11).add(-11, y, -11), rand, 22, 0, 22, false, false, true, false);
			addEdgePlant(world, pos.down(11).add(-11, y, -11), rand, 22, 0, 22);
		}
		addHangingPlants(world, pos.add(-11, 1, -11), rand, 22, 0, 22);

		// S = 0, W = 1, N = 2, E = 3

		this.setBlockAndNotifyAdequately(world, pos.add(0, -4, -12F), BlockRegistry.DECAY_PIT_GROUND_CHAIN.getDefaultState().withProperty(BlockDecayPitGroundChain.FACING, EnumFacing.SOUTH));
		this.setBlockAndNotifyAdequately(world, pos.add(12, -4, 0), BlockRegistry.DECAY_PIT_GROUND_CHAIN.getDefaultState().withProperty(BlockDecayPitGroundChain.FACING, EnumFacing.WEST));
		this.setBlockAndNotifyAdequately(world, pos.add(0, -4, 12), BlockRegistry.DECAY_PIT_GROUND_CHAIN.getDefaultState().withProperty(BlockDecayPitGroundChain.FACING, EnumFacing.NORTH));
		this.setBlockAndNotifyAdequately(world, pos.add(-12, -4, 0), BlockRegistry.DECAY_PIT_GROUND_CHAIN.getDefaultState().withProperty(BlockDecayPitGroundChain.FACING, EnumFacing.EAST));

		EntityDecayPitTarget target = new EntityDecayPitTarget(world);
		target.setPosition(pos.getX() + 0.5F, pos.down(6).getY(), pos.getZ() + 0.5F);
		world.spawnEntity(target);
	}

	public void generateDecayPitEntrance(World world, Random rand, BlockPos pos) {
		lightTowerBuild.buildPitEntrance(world, pos, EnumFacing.EAST, rand, 0, 0);
	}

	public void makeMaze(World world, Random rand, BlockPos pos) {
		for (int level = 0; level <= 7; level++) {
			int yy = -6 -(level * 6);
			if (level == 7)
				buildRoof(world, pos.add(0, yy - 3, 0).up(8), rand, 7, 7, level);

			if (level < 7 && level >= 0)
				generateMaze(world, rand, pos.add(0, yy, 0), level);

			if (level <= 7) {
				// create STAIRS
				if (level == 1 || level == 3 || level == 5|| level == 7) {
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 5, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH);
					if (level != 7) {
						this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 0, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH);
						this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 1, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH);
						this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 2, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH);
						this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 3, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH);
						this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 4, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH);
					}
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 1, 1), getMudBricksForLevel(rand, level, 1));
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 2, 1), getMudBricksForLevel(rand, level, 2));
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 3, 1), getMudBricksForLevel(rand, level, 3));
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 4, 1), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.BOTTOM));
					if (world.isAirBlock(pos.add(1, yy + 1, 2)))
						this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 1, 2), getMudBricksForLevel(rand, level, 1));
					placeRuneCombination(world, pos.add(0, yy + 2, 0), level, rand);
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 3, 2), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.BOTTOM));
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 2, 3), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.BOTTOM));
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 1, 3), getMudBricksForLevel(rand, level, 1));
					this.setBlockAndNotifyAdequately(world, pos.add(2, yy + 1, 3), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.BOTTOM));
				} else {
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 5, 28), blockHelper.MUD_BRICKS_CLIMBABLE_NORTH);
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 0, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH);
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 1, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH);
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 2, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH);
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 3, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH);
					this.setBlockAndNotifyAdequately(world, pos.add(1, yy + 4, 0), blockHelper.MUD_BRICKS_CLIMBABLE_SOUTH);
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 1, 27), getMudBricksForLevel(rand, level, 1));
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 2, 27), getMudBricksForLevel(rand, level, 2));
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 3, 27), getMudBricksForLevel(rand, level, 3));
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 4, 27), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.BOTTOM));
					if (world.isAirBlock(pos.add(27, yy + 1, 26)))
						this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 1, 26), getMudBricksForLevel(rand, level, 1));
					placeRuneCombination(world, pos.add(0, yy + 2, 0), level, rand);
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 3, 26), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.BOTTOM));
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 2, 25), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.BOTTOM));
					this.setBlockAndNotifyAdequately(world, pos.add(27, yy + 1, 25), getMudBricksForLevel(rand, level, 1));
					this.setBlockAndNotifyAdequately(world, pos.add(26, yy + 1, 25), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.BOTTOM));
				}
				if (level != 7)
					addCeilingSlabs(world, pos.add(0, yy + 4, 0), 7, 7, rand, level);
				stairsAir(world, rand, pos.add(0, yy, 0), level);
				addHangingPlants(world, pos.add(1, yy + 5, 1), rand, 28, 0, 28);
				addEdgePlant(world, pos.add(1, yy, 1), rand, 28, 0, 28);
			}
			//	System.out.println("Y height is: " + (pos.getY() + yy) + " level: " + level);
		}
	}

	// Maze generation layers
	public void generateMaze(World world, Random rand, BlockPos pos, int level) {
		int sizeY = 6;
		int mazeWidth = 7;
		int mazeHeight = 7;

		int[][] maze = null;
		MazeGenerator generator = new PerfectMazeGenerator(mazeWidth, mazeHeight);
		maze = generator.generateMaze();
		for (int layer = 0; layer < sizeY; layer++)
			switch (layer) {
			case 0:
				break;
			case 1:
				buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				createAir(world, pos.add(0, layer, 0), rand, mazeWidth, mazeHeight, level);
				addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				break;
			case 2:
				buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				break;
			case 3:
				buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				break;
			case 4:
				buildLevel(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				break;
			case 5:
				buildRoof(world, pos.up(layer), rand, mazeWidth, mazeHeight, level);
				addMazeCellFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				buildFloor(world, pos, rand, mazeWidth, mazeHeight, true, false, level);
				addFeature(world, pos.up(layer), rand, mazeWidth, mazeHeight, maze, level, layer);
				break;
			}
		//	System.out.println("Generated Maze At: X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
	}

	// Levels
	private void buildLevel(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		for (int i = 0; i < h; i++) {
			// draw the north edge
			for (int j = 0; j < w; j++) {
				if ((maze[j][i] & 1) == 0) {
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4 + 1, 0, i * 4), getMudBricksForLevel(rand, level, layer));
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4 + 2, 0, i * 4), getMudBricksForLevel(rand, level, layer));
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4 + 3, 0, i * 4), getMudBricksForLevel(rand, level, layer));
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				} else
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4), getMudBricksForLevel(rand, level, layer));
			}
			for (int j = 0; j < w; j++) {
				// draw the west edge

				if ((maze[j][i] & 8) == 0) {
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4 + 1), getMudBricksForLevel(rand, level, layer));
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4 + 2), getMudBricksForLevel(rand, level, layer));
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4 + 3), getMudBricksForLevel(rand, level, layer));
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				}

				if ((maze[j][i] & 4) == 0) {
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				}

				if ((maze[j][i] & 2) == 0) {
					this.setBlockAndNotifyAdequately(world, pos.add(j * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
					if (layer == 3)
						addBeamSupports(world, pos.add(j * 4, 0, i * 4), rand, pos);
					if (layer == 4)
						addBeams(world, pos.add(j * 4, 0, i * 4), rand, level, pos);
				}

				this.setBlockAndNotifyAdequately(world, pos.add(w * 4, 0, i * 4), (layer == 1 || layer == 2 || layer == 3) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
				this.setBlockAndNotifyAdequately(world, pos.add(w * 4, 0, i * 4 + 1), getMudBricksForLevel(rand, level, layer));
				this.setBlockAndNotifyAdequately(world, pos.add(w * 4, 0, i * 4 + 2), getMudBricksForLevel(rand, level, layer));
				this.setBlockAndNotifyAdequately(world, pos.add(w * 4, 0, i * 4 + 3), getMudBricksForLevel(rand, level, layer));
				if (layer == 3)
					addBeamSupports(world, pos.add(w * 4, 0, i * 4), rand, pos);
				if (layer == 4)
					addBeams(world, pos.add(w * 4, 0, i * 4), rand, level, pos);
			}
		}
		// draw the bottom line
		for (int j = 0; j <= w * 4; j++) {
			this.setBlockAndNotifyAdequately(world, pos.add(j, 0, h * 4), (j % 4 == 0 && (layer == 1 || layer == 2 || layer == 3)) ? getPillarsForLevel(rand, level, layer) : getMudBricksForLevel(rand, level, layer));
			if (layer == 3 && j % 4 == 0)
				addBeamSupports(world, pos.add(j, 0, h * 4), rand, pos);
			if (layer == 4 && j % 4 == 0)
				addBeams(world, pos.add(j, 0, h * 4), rand, level, pos);
		}
	}

	private void addBeamSupports(World world, BlockPos pos, Random rand, BlockPos posOrigin) {
		EnumFacing[] sides = EnumFacing.HORIZONTALS;
		for (EnumFacing facing : sides) {
			if (!isSolidStructureBlock(world.getBlockState(pos.offset(facing))) && world.isAirBlock(pos.offset(facing)))
				if (isWithinMazeAreaForGen(posOrigin, pos.offset(facing)))
					this.setBlockAndNotifyAdequately(world, pos.offset(facing), getRandomSupportBeam(facing, true, rand));
		}
	}

	private void addBeams(World world, BlockPos pos, Random rand, int level, BlockPos posOrigin) {
		EnumFacing[] sides = EnumFacing.HORIZONTALS;
		for (EnumFacing facing : sides) {
			for (int count = 1; count <= 2; count++)
				if (!isSolidStructureBlock(world.getBlockState(pos.offset(facing, count))) && world.isAirBlock(pos.offset(facing, count)))
					if (isWithinMazeAreaForGen(posOrigin, pos.offset(facing, count)))
						this.setBlockAndNotifyAdequately(world, pos.offset(facing, count), getRandomBeam(facing.rotateY(), rand, level, count, false));
			if (isWithinMazeAreaForGen(posOrigin, pos.offset(facing, 3))) {
				if (level >= 5 && rand.nextInt(level == 5 ? 30 : 25) == 0)
					addFallingBlockEntity(world, pos.offset(facing, 1));
				if (level >= 5 && rand.nextInt(level == 5 ? 30 : 25) == 0)
					addFallingBlockEntity(world, pos.offset(facing, 2));
				if (level >= 5 && rand.nextInt(level == 5 ? 30 : 25) == 0)
					addFallingBlockEntity(world, pos.offset(facing, 3));
			}
		}
	}

	private void addFallingBlockEntity(World world, BlockPos pos) {
		EntityTriggeredFallingBlock falling_block = new EntityTriggeredFallingBlock(world);
		falling_block.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		world.spawnEntity(falling_block);
	}

	private void addHangingBlockEntity(World world, BlockPos pos) {
		EntityTriggeredFallingBlock falling_block = new EntityTriggeredFallingBlock(world);
		falling_block.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		falling_block.setHanging(true);
		world.spawnEntity(falling_block);
	}

	private void addCCGroundSpawnerEntity(World world, BlockPos pos) {
		EntityCCGroundSpawner ground_spawner = new EntityCCGroundSpawner(world);
		ground_spawner.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		ground_spawner.setIsWorldSpawned(false);
		world.spawnEntity(ground_spawner);
	}

	// Ceiling Slabs
	private void addCeilingSlabs(World world, BlockPos pos, int w, int h, Random rand, int level) {
		for (int i = 0; i <= h * 4; i++)
			for (int j = 0; j <= w * 4; j++)
				if(j%4 != 0 && i%4 != 0) {
					if(world.isAirBlock(pos.add(j, 0, i)) && !isSolidStructureBlock(world.getBlockState(pos.add(j, 0, i)))) {
						if(level == 0)
							this.setBlockAndNotifyAdequately(world, pos.add(j, 0, i), getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP));
						if(level != 0 && rand.nextInt(level) == 0)
							this.setBlockAndNotifyAdequately(world, pos.add(j, 0, i), getMudSlabsForLevel(rand, level, EnumBlockHalfBL.TOP));
					}
				}
	}

	private void addMazeCellFeature(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		// byte directions 1 = SOUTH, 2 = NORTH, 4 = WEST, 8 = EAST

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if ((maze[j][i] & 2) == 0 && (maze[j][i] & 4) == 0 && (maze[j][i] & 8) == 0) {
					// SOUTH
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4), level == 5 ? true : false))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, -4, 3 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.NORTH, rand, level, layer);
				}
				if ((maze[j][i] & 1) == 0 && (maze[j][i] & 4) == 0 && (maze[j][i] & 8) == 0) {
					// NORTH
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4), level == 5 ? true : false))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, -4, 1 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.SOUTH, rand, level, layer);
				}
				if ((maze[j][i] & 1) == 0 && (maze[j][i] & 2) == 0 && (maze[j][i] & 4) == 0) {
					// EAST
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4), level == 5 ? true : false))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, -4, 2 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.WEST, rand, level, layer);
				}
				if ((maze[j][i] & 1) == 0 && (maze[j][i] & 2) == 0 && (maze[j][i] & 8) == 0) {
					// WEST
					if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4), level == 5 ? true : false))
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, -4, 2 + i * 4))))
							microBuild.selectFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.EAST, rand, level, layer);
				}

				if (level == 2) {
					if ((maze[j][i] & 1) == 0 && (maze[j][i] & 2) == 0 && (maze[j][i] & 4) != 0 && (maze[j][i] & 8) != 0)
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
							microBuild.spikeFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.WEST, rand, level, layer);

					if ((maze[j][i] & 1) != 0 && (maze[j][i] & 2) != 0 && (maze[j][i] & 4) == 0 && (maze[j][i] & 8) == 0)
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
							microBuild.spikeFeature(world, pos.add(2 + j * 4, -4, 2 + i * 4), EnumFacing.NORTH, rand, level, layer);
				}

				if (level == 6) {
					if ((maze[j][i] & 1) == 0 && (maze[j][i] & 2) == 0 && (maze[j][i] & 4) != 0 && (maze[j][i] & 8) != 0)
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
							spawnMovingWall(world, pos.add(2 + j * 4, -3, 2 + i * 4));

					if ((maze[j][i] & 1) != 0 && (maze[j][i] & 2) != 0 && (maze[j][i] & 4) == 0 && (maze[j][i] & 8) == 0)
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -4, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -4, 2 + i * 4)))
							spawnMovingWall(world, pos.add(2 + j * 4, -3, 2 + i * 4));
				}
			}
		}
	}

	private void spawnMovingWall(World world, BlockPos pos) {
		EntityMovingWall wall = new EntityMovingWall(world);
		wall.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		world.spawnEntity(wall);
	}

	private boolean isBlackListedForGen(BlockPos pos, BlockPos posIn) {
		if(posIn.getX() == pos.getX() && posIn.getZ() == pos.getZ())
			return true;
		return false;
	}

	private boolean isBlackListedForGenSpecial(BlockPos pos, BlockPos posIn, boolean crapCheck) {
		if(posIn.getX() == pos.getX() && posIn.getZ() == pos.getZ() && crapCheck)
			return true;
		return false;
	}

	private boolean isBlackListedAreaForGen(BlockPos pos, BlockPos posIn, int radius) {
		for (int x = -radius; x <= radius; x++)
			for (int z = -radius; z <= radius; z++)
				if(posIn.getX() == pos.add(x, 0, z).getX() && posIn.getZ() == pos.add(x, 0, z).getZ())
					return true;
		return false;
	}

	private boolean isBlackListedAreaForGenSpecial(BlockPos pos, BlockPos posIn, int radius, boolean crapCheck) {
		for (int x = -radius; x <= radius; x++)
			for (int z = -radius; z <= radius; z++)
				if(posIn.getX() == pos.add(x, 0, z).getX() && posIn.getZ() == pos.add(x, 0, z).getZ() && crapCheck)
					return true;
		return false;
	}

	private boolean isWithinMazeAreaForGen(BlockPos posOrigin, BlockPos posIn) {
		for (int x = 0; x <= 28; x++)
			for (int z = 0; z <= 28; z++)
				if(posIn.getX() == posOrigin.add(x, 0, z).getX() && posIn.getZ() == posOrigin.add(x, 0, z).getZ())
					return true;
		return false;
	}

	// Roots, Alcoves, Torches and upside down Stairs for the ceiling
	private void addFeature(World world, BlockPos pos, Random rand, int w, int h, int[][] maze, int level, int layer) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if ((maze[j][i] & 1) == 0) {
					if (layer == 2) {
						if (!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4), level == 5 ? true : false))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 1 + i * 4))) && world.getBlockState(pos.add(2 + j * 4, 0, i * 4)).getBlock() instanceof BlockCarvedMudBrick)
								this.setBlockAndNotifyAdequately(world, pos.add(2 + j * 4, 0, 1 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_SOUTH);
							else {
								if (rand.nextInt(5) == 0 && level != 2)
									if(world.getBlockState(pos.add(2 + j * 4, 0, i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(2 + j * 4, 0, i * 4), blockHelper.MUD_BRICKS_ALCOVE_SOUTH, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(1 + j * 4, 0, i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(1 + j * 4, 0, i * 4), blockHelper.MUD_BRICKS_ALCOVE_SOUTH, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(3 + j * 4, 0, i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(3 + j * 4, 0, i * 4), blockHelper.MUD_BRICKS_ALCOVE_SOUTH, rand, level);
							}
					}

					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j *  4, 0, 1 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(1 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 1 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(2 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 1 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(3 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.NORTH, EnumHalf.TOP));
					}

					if (layer == 5) {
						if (rand.nextInt(3) == 0)
							if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -6, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -6, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -6, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -6, 2 + i * 4), level == 5 ? true : false)) {
								if(world.isAirBlock(pos.add(1 + j * 4, -4, 1 + rand.nextInt(2) + i * 4)))
									setRandomRoot(world, pos.add(1 + j * 4, -4, 1 + rand.nextInt(2) + i * 4), rand);
							}
					}
				}

				if ((maze[j][i] & 8) == 0) {
					if (layer == 2) {
						if (!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4), level == 5 ? true : false))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 2 + i * 4))) && world.getBlockState(pos.add(j * 4, 0, 2 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
								this.setBlockAndNotifyAdequately(world, pos.add(1 + j * 4, 0, 2 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_EAST);
							else {
								if (rand.nextInt(5) == 0 && level != 2)
									if(world.getBlockState(pos.add(j * 4, 0, 2 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(j * 4, 0, 2 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_EAST, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(j * 4, 0, 1 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(j * 4, 0, 1 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_EAST, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(j * 4, 0, 3 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(j * 4, 0, 3 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_EAST, rand, level);
							}
					}

					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 1 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(1 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 2 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(1 + j * 4, 0, 2 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 3 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(1 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.WEST, EnumHalf.TOP));
					}
				}

				if ((maze[j][i] & 4) == 0) {
					if (layer == 2) {
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4), level == 5 ? true : false))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 2 + i * 4))) && world.getBlockState(pos.add(4 + j * 4, 0, 2 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
								this.setBlockAndNotifyAdequately(world, pos.add(3 + j * 4, 0, 2 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_WEST);
							else {
								if (rand.nextInt(5) == 0 && level != 2)
									if(world.getBlockState(pos.add(4 + j * 4, 0, 2 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(4 + j * 4, 0, 2 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_WEST, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(4 + j * 4, 0, 1 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(4 + j * 4, 0, 1 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_WEST, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(4 + j * 4, 0,  3 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(4 + j * 4, 0, 3 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_WEST, rand, level);
							}
					}

					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 1 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(3 + j * 4, 0, 1 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 2 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(3 + j * 4, 0, 2 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 3 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(3 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.EAST, EnumHalf.TOP));
					}
				}

				if ((maze[j][i] & 2) == 0) {
					if (layer == 2) {
						if(!isBlackListedForGen(pos.add(2, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGen(pos.add(26, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4)) && !isBlackListedForGenSpecial(pos.add(26, 0, 2), pos.add(2 + j * 4, -3, 2 + i * 4), level == 3 ? true : false) && !isBlackListedForGenSpecial(pos.add(2, 0, 26), pos.add(2 + j * 4, -3, 2 + i * 4), level == 5 ? true : false))
							if (rand.nextInt(25) == 0 && !isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 3 + i * 4))) && world.getBlockState(pos.add(2 + j * 4, 0, 4 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
								this.setBlockAndNotifyAdequately(world, pos.add(2 + j * 4, 0, 3 + i * 4), blockHelper.DUNGEON_WALL_CANDLE_NORTH);
							else {
								if (rand.nextInt(5) == 0 && level != 2)
									if(world.getBlockState(pos.add(2 + j * 4, 0, 4 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(2 + j * 4, 0, 4 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_NORTH, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(1 + j * 4, 0, 4 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(1 + j * 4, 0, 4 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_NORTH, rand, level);
								if (rand.nextInt(5) == 0)
									if(world.getBlockState(pos.add(3 + j * 4, 0, 4 + i * 4)).getBlock() instanceof BlockCarvedMudBrick)
										setAlcoveForLevel(world, pos.add(3 + j * 4, 0, 4 + i * 4), blockHelper.MUD_BRICKS_ALCOVE_NORTH, rand, level);
							}
					}

					if (layer == 4) {
						if (!isSolidStructureBlock(world.getBlockState(pos.add(1 + j * 4, 0, 3 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(1 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(2 + j * 4, 0, 3 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(2 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP));
						if (!isSolidStructureBlock(world.getBlockState(pos.add(3 + j * 4, 0, 3 + i * 4))))
							this.setBlockAndNotifyAdequately(world, pos.add(3 + j * 4, 0, 3 + i * 4), getStairsForLevel(rand, level, EnumFacing.SOUTH, EnumHalf.TOP));	
					}
				}
			}
		}
	}

	// Places doors and code blocks
	private void placeRuneCombination(World world, BlockPos pos, int level, Random rand) {
		if (level <= 6) {
			if (level == 1 || level == 3 || level == 5) {
				this.setBlockAndNotifyAdequately(world, pos.add(1, 0, 2), blockHelper.DUNGEON_DOOR_COMBINATION_EAST);
				this.setBlockAndNotifyAdequately(world, pos.add(27, 0, 26), blockHelper.DUNGEON_DOOR_WEST);
				setRandomCombinations(world, pos.add(1, 0, 2), pos.add(27, 0, 26), rand, false);

				if(level == 3) {
					this.setBlockAndNotifyAdequately(world, pos.add(26, 0, 1), blockHelper.DUNGEON_DOOR_MIMIC_SOUTH);
					setRandomCombinations(world, pos.add(1, 0, 2), pos.add(26, 0, 1), rand, true);
					for(int x = -1; x <= 1; x++)
						for(int y = -1; y <= 1; y++)
							this.setBlockAndNotifyAdequately(world, pos.add(26 + x, y, 0), Blocks.AIR.getDefaultState());
					microBuild.addBarrisheeCubby(world, pos.add(20, -2, -3), EnumFacing.SOUTH, rand, level);
				}
				if(level == 5) {
					this.setBlockAndNotifyAdequately(world, pos.add(1, 0, 26), blockHelper.DUNGEON_DOOR_CRAWLER_EAST);
					setRandomCombinations(world, pos.add(1, 0, 2), pos.add(1, 0, 26), rand, true);
					for(int z = -1; z <= 1; z++)
						for(int y = -1; y <= 1; y++)
							this.setBlockAndNotifyAdequately(world, pos.add(0, y, 26 + z), Blocks.AIR.getDefaultState());
				}
			} else {
				this.setBlockAndNotifyAdequately(world, pos.add(27, 0, 26), blockHelper.DUNGEON_DOOR_COMBINATION_WEST);
				this.setBlockAndNotifyAdequately(world, pos.add(1, 0, 2), blockHelper.DUNGEON_DOOR_EAST);
				setRandomCombinations(world, pos.add(27, 0, 26), pos.add(1, 0, 2), rand, false);
			}
		}
		if (level == 7)
			this.setBlockAndNotifyAdequately(world, pos.add(1, 0, 2), getMudBricksForLevel(rand, level, 2));
	}

	public void setRandomCombinations(World world, BlockPos codePos, BlockPos lockPos, Random rand, boolean isMimic) {
		IBlockState codeState = world.getBlockState(codePos);
		IBlockState lockState = world.getBlockState(lockPos);
		TileEntityDungeonDoorCombination tileCode = (TileEntityDungeonDoorCombination) world.getTileEntity(codePos);
		if (tileCode instanceof TileEntityDungeonDoorCombination) {
			if(!isMimic) {
				tileCode.top_code = world.rand.nextInt(8);
				tileCode.mid_code = world.rand.nextInt(8);
				tileCode.bottom_code = world.rand.nextInt(8);
			}
			TileEntityDungeonDoorRunes tileLock = (TileEntityDungeonDoorRunes) world.getTileEntity(lockPos);
			if (tileLock instanceof TileEntityDungeonDoorRunes) {
				tileLock.top_code = tileCode.top_code;
				tileLock.mid_code = tileCode.mid_code;
				tileLock.bottom_code = tileCode.bottom_code;
				tileLock.is_in_dungeon = true;
			}
		}
		world.notifyBlockUpdate(codePos, codeState, codeState, 3);
		world.notifyBlockUpdate(lockPos, lockState, lockState, 3);
	}

	//Air Gaps for Stairs and barrier placements
	private void stairsAir(World world, Random rand, BlockPos pos, int level) {
		if (level == 1 || level == 3 || level == 5 || level == 7) {
			this.setBlockAndNotifyAdequately(world, pos.add(1, 5, 1), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 5, 2), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 5, 3), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 4, 2), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 4, 3), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 3, 3), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(2, 4, 3), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(2, 3, 3), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 6, 1), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 6, 2), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(1, 6, 3), Blocks.AIR.getDefaultState());
		} else if (level == 0 || level == 2 || level == 4 || level == 6) {
			this.setBlockAndNotifyAdequately(world, pos.add(27, 5, 27), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(27, 5, 26), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(27, 5, 25), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(27, 4, 26), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(27, 4, 25), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(27, 3, 25), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(26, 4, 25), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.add(26, 3, 25), Blocks.AIR.getDefaultState());
			if(level != 0) {
				this.setBlockAndNotifyAdequately(world, pos.add(27, 6, 27), Blocks.AIR.getDefaultState());
				this.setBlockAndNotifyAdequately(world, pos.add(27, 6, 26), Blocks.AIR.getDefaultState());
				this.setBlockAndNotifyAdequately(world, pos.add(27, 6, 25), Blocks.AIR.getDefaultState());
			}
		}
	}

	// Air gap for maze
	private void createAir(World world, BlockPos pos, Random rand, int w, int h, int level) {
		for (int i = 0; i < h * 4; i++)
			for (int j = 0; j < w * 4; j++)
				for (int k = 0; k <= 3; k++)
					if (!isSolidStructureBlock(world.getBlockState(pos.add(j, k, i))))
						this.setBlockAndNotifyAdequately(world, pos.add(j, k, i), Blocks.AIR.getDefaultState());
	}

	// Floor
	private void buildFloor(World world, BlockPos pos, Random rand, int w, int h, boolean addFeature, boolean addSpawners, int level) {
		for (int i = 0; i <= h * 4; i++) {
			for (int j = 0; j <= w * 4; j++) {
				if(!isSolidStructureBlock(world.getBlockState(pos.add(j, 0, i))))
					this.setBlockAndNotifyAdequately(world, pos.add(j, 0, i), getTilesForLevel(rand, level));
				if (rand.nextInt(15) == 0 && addFeature && !isSolidStructureBlock(world.getBlockState(pos.add(j, 1, i))) && world.getBlockState(pos.add(j, 0, i)).getBlock() instanceof BlockMudTiles) {
					if (!isBlackListedAreaForGen(pos.add(2, 0, 2), pos.add(j, 0, i), 1) && !isBlackListedAreaForGen(pos.add(26, 0, 26), pos.add(j, 0, i), 1) && !isBlackListedAreaForGenSpecial(pos.add(26, 0, 2), pos.add(j, 0, i), 1, level == 3 ? true : false) && !isBlackListedAreaForGenSpecial(pos.add(2, 0, 26), pos.add(j, 0, i), 1, level == 5 ? true : false)) {
						if (rand.nextBoolean() && rand.nextBoolean())
							this.setBlockAndNotifyAdequately(world, pos.add(j, 0, i), blockHelper.getMudTilesWater(rand));
						else
							if (rand.nextBoolean() && rand.nextBoolean() && level >= 4)
								this.setBlockAndNotifyAdequately(world, pos.add(j, 0, i), blockHelper.PUFFSHROOM);
					}
				} 

				if (world.getBlockState(pos.add(j, 0, i)).getBlock() instanceof BlockMudTiles && world.isAirBlock(pos.add(j, 1, i)))
					if (rand.nextInt(15) == 0)
						if (!isBlackListedAreaForGen(pos.add(2, 0, 2), pos.add(j, 0, i), 1) && !isBlackListedAreaForGen(pos.add(26, 0, 26), pos.add(j, 0, i), 1)  && !isBlackListedAreaForGenSpecial(pos.add(26, 0, 2), pos.add(j, 0, i), 1, level == 3 ? true : false) && !isBlackListedAreaForGenSpecial(pos.add(2, 0, 26), pos.add(j, 0, i), 1, level == 5 ? true : false))
							this.setBlockAndNotifyAdequately(world, pos.add(j, 1, i), rand.nextBoolean() ? getRandomMushroom(rand) : getRandomFloorPlant(rand));
			}
		}
	}

	// Roof
	private void buildRoof(World world, BlockPos pos, Random rand, int w, int h, int level) {
		for (int i = 0; i <= h * 4; i++) {
			for (int j = 0; j <= w * 4; j++) {
				//if (world.isAirBlock(pos.add(j, 0, i)))
				this.setBlockAndNotifyAdequately(world, pos.add(j, 0, i), blockHelper.COMPACTED_MUD);
			}
		}
	}

	// Places alcoves and sets TE level (used for textures atm)
	public void setAlcoveForLevel(World world, BlockPos pos, IBlockState state, Random rand, int level) {
		this.setBlockAndNotifyAdequately(world, pos, state);
		TileEntityMudBrickAlcove alcove = (TileEntityMudBrickAlcove) world.getTileEntity(pos);
		if (alcove != null)
			alcove.setDungeonLevel(level);
	}

	public @Nullable IBlockState getMudBricksForLevel(Random rand, int level, int layer) {
		return blockHelper.getMudBricksForLevel(rand, level, layer);
	}

	public @Nullable IBlockState getPillarsForLevel(Random rand, int level, int layer) {
		return blockHelper.getPillarsForLevel(rand, level, layer);
	}

	public @Nullable IBlockState getTilesForLevel(Random rand, int level) {
		return blockHelper.getTilesForLevel(rand, level);
	}

	public @Nullable IBlockState getStairsForLevel(Random rand, int level, EnumFacing facing, EnumHalf half) {
		return blockHelper.getStairsForLevel(rand, level, facing, half);
	}

	public @Nullable IBlockState getMudSlabsForLevel(Random rand, int level, EnumBlockHalfBL half) {
		return blockHelper.getMudSlabsForLevel(rand, level, half);
	}

	public IBlockState getRandomMushroom(Random rand) {
		return blockHelper.getRandomMushroom(rand);
	}

	public IBlockState getRandomFloorPlant(Random rand) {
		return blockHelper.getRandomFloorPlant(rand);
	}

	public IBlockState getRandomHangingPlant(Random rand) {
		return blockHelper.getRandomHangingPlant(rand);
	}

	public IBlockState getRandomBeam(EnumFacing facing, Random rand, int level, int count, boolean randomiseLine) {
		return blockHelper.getRandomBeam(facing, rand, level, count, randomiseLine);
	}

	public IBlockState getRandomSupportBeam(EnumFacing facing, boolean isTop, Random rand) {
		return blockHelper.getRandomSupportBeam(facing, isTop, rand);
	}

	public void setRandomRoot(World world, BlockPos pos, Random rand) {
		blockHelper.setRandomRoot(world, pos, rand);
	}

	public boolean isSolidStructureBlock(IBlockState state) {
		return blockHelper.STRUCTURE_BLOCKS.get(state) != null;
	}

	protected boolean isProtectedBlock(IBlockState state) {
		Block block = state.getBlock();
		if(block != Blocks.AIR && block != BlockRegistry.MOB_SPAWNER && block != BlockRegistry.LOOT_POT
				&& block != BlockRegistry.ROOT && block instanceof BlockPlant == false && block instanceof BlockHangingPlant == false && block != BlockRegistry.VALONITE_BLOCK
				&& block != BlockRegistry.SYRMORITE_BLOCK && block != BlockRegistry.OCTINE_BLOCK && block != BlockRegistry.WEEDWOOD_CHEST
				&& block != BlockRegistry.POSSESSED_BLOCK && block != BlockRegistry.WEAK_POLISHED_LIMESTONE
				&& block != BlockRegistry.WEAK_BETWEENSTONE_TILES && block != BlockRegistry.WEAK_MOSSY_BETWEENSTONE_TILES
				&& block != BlockRegistry.LOOT_URN && block != BlockRegistry.PUFFSHROOM && block != BlockRegistry.STAGNANT_WATER
				&& block != BlockRegistry.ROPE && block != BlockRegistry.DEAD_MOSS && block != BlockRegistry.DEAD_LICHEN && block != BlockRegistry.MUD_TILES_WATER
				&& block != BlockRegistry.HANGER) {
			return true;
		}
		return false;
	}

	@Override
	public void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
		if(this.isProtectedBlock(state)) {
			this.location.getGuard().setGuarded(worldIn, pos, true);
		} else {
			this.location.getGuard().setGuarded(worldIn, pos, false);
		}

		super.setBlockAndNotifyAdequately(worldIn, pos, state);
		
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile instanceof ISharedLootContainer) {
			ResourceLocation lootTable = this.getLootTableForBlock(worldIn, pos, state);
			
			if(lootTable != null) {
				((ISharedLootContainer) tile).setSharedLootTable(this.lootStorage, lootTable, this.lootRng.nextLong());
			}
		}
		
		if(worldIn instanceof WorldServer && tile instanceof TileEntityItemShelf) {
			LootTable lootTable = worldIn.getLootTableManager().getLootTableFromLocation(LootTableRegistry.SLUDGE_WORM_DUNGEON_ITEM_SHELF);
			
			Random random = new Random(this.lootRng.nextLong());

			LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) worldIn);

			List<ItemStack> loot = lootTable.generateLootForPools(random, lootBuilder.build());

			TileEntityLootInventory.fillInventoryRandomly(random, loot, (TileEntityItemShelf) tile);
		}
	}
	
	@Nullable
	protected ResourceLocation getLootTableForBlock(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		
		if(block == BlockRegistry.LOOT_URN || block == BlockRegistry.MUD_BRICK_ALCOVE) {
			if(this.locationCrypt.isInside(pos)) {
				return LootTableRegistry.SLUDGE_WORM_DUNGEON_CRYPT_URN;
			} else {
				return LootTableRegistry.SLUDGE_WORM_DUNGEON_URN;
			}
		} else if(block == BlockRegistry.WEEDWOOD_CHEST) {
			if(this.locationBarrisheeLair.isInside(pos)) {
				return LootTableRegistry.SLUDGE_WORM_DUNGEON_BARRISHEE_CHEST;
			} else {
				return LootTableRegistry.SLUDGE_WORM_DUNGEON_CHEST;
			}
		}
		
		return null;
	}
}