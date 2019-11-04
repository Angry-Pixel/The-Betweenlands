package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.BlockChestBetweenlands;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.structure.BlockWallWeedwoodSign;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.mobs.EntityFortressBossTeleporter;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationAmbience.EnumLocationAmbience;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

public class WorldGenWightFortress extends WorldGenerator {

	private int length = -1;
	private int width = -1;
	private int height = -1;
	private int direction = -1;

	private IBlockState limestonePolished = BlockRegistry.POLISHED_LIMESTONE.getDefaultState();
	private IBlockState limestoneChiselled = BlockRegistry.LIMESTONE_CHISELED.getDefaultState();
	private IBlockState limestoneBrickSlab = BlockRegistry.LIMESTONE_BRICK_SLAB.getDefaultState();
	private IBlockState limestonePolishedCollapsing = BlockRegistry.WEAK_POLISHED_LIMESTONE.getDefaultState();
	private IBlockState betweenstone = BlockRegistry.BETWEENSTONE.getDefaultState();
	private IBlockState betweenstoneSmooth = BlockRegistry.SMOOTH_BETWEENSTONE.getDefaultState();
	private IBlockState betweenstoneSmoothMossy = BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE.getDefaultState();
	private IBlockState betweenstoneTiles = BlockRegistry.BETWEENSTONE_TILES.getDefaultState();
	private IBlockState betweenstoneTilesMossy = BlockRegistry.MOSSY_BETWEENSTONE_TILES.getDefaultState();
	private IBlockState betweenstoneTilesCracked = BlockRegistry.CRACKED_BETWEENSTONE_TILES.getDefaultState();
	private IBlockState betweenstoneTilesCollapsing = BlockRegistry.WEAK_BETWEENSTONE_TILES.getDefaultState();
	private IBlockState betweenstoneTilesMossyCollapsing = BlockRegistry.WEAK_MOSSY_BETWEENSTONE_TILES.getDefaultState();
	private IBlockState betweenstoneBrickStairs = BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState();
	private IBlockState betweenstoneBrickStairsMossy = BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS.getDefaultState();
	private IBlockState betweenstoneBrickStairsCracked = BlockRegistry.CRACKED_BETWEENSTONE_BRICK_STAIRS.getDefaultState();
	private IBlockState betweenstoneBrickSlab = BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState();
	private IBlockState betweenstoneBrickWall = BlockRegistry.BETWEENSTONE_BRICK_WALL.getDefaultState();
	private IBlockState betweenstoneBrickWallMossy = BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL.getDefaultState();
	private IBlockState betweenstoneBrickWallCracked = BlockRegistry.CRACKED_BETWEENSTONE_BRICK_WALL.getDefaultState();
	private IBlockState betweenstoneBricks = BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState();
	private IBlockState betweenstoneBricksMirage = BlockRegistry.BETWEENSTONE_BRICKS_MIRAGE.getDefaultState();
	private IBlockState betweenstoneBricksMossy = BlockRegistry.MOSSY_BETWEENSTONE_BRICKS.getDefaultState();
	private IBlockState betweenstoneBricksCracked = BlockRegistry.CRACKED_BETWEENSTONE_BRICKS.getDefaultState();
	private IBlockState betweenstonePillar = BlockRegistry.BETWEENSTONE_PILLAR.getDefaultState();
	private IBlockState betweenstoneStairsSmooth = BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS.getDefaultState();
	private IBlockState betweenstoneStairsSmoothMossy = BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS.getDefaultState();
	private IBlockState betweenstoneTilesFortress = BlockRegistry.GLOWING_BETWEENSTONE_TILE.getDefaultState();
	private IBlockState stagnantWater = BlockRegistry.STAGNANT_WATER.getDefaultState();
	private IBlockState spikeTrap = BlockRegistry.SPIKE_TRAP.getDefaultState();
	private IBlockState swordStone = BlockRegistry.ITEM_CAGE.getDefaultState();;
	private IBlockState root = BlockRegistry.ROOT.getDefaultState();
	private IBlockState possessedBlock = BlockRegistry.POSSESSED_BLOCK.getDefaultState();
	private IBlockState chest = BlockRegistry.WEEDWOOD_CHEST.getDefaultState();
	private IBlockState lootPot1 = BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_1);
	private IBlockState lootPot2 = BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_2);
	private IBlockState lootPot3 = BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_3);
	private IBlockState spawner = BlockRegistry.MOB_SPAWNER.getDefaultState();
	private IBlockState obviousSign = BlockRegistry.WALL_WEEDWOOD_SIGN.getDefaultState();
	private IBlockState valoniteBlock = BlockRegistry.VALONITE_BLOCK.getDefaultState();
	private IBlockState syrmoriteBlock = BlockRegistry.SYRMORITE_BLOCK.getDefaultState();
	private IBlockState octineBlock = BlockRegistry.OCTINE_BLOCK.getDefaultState();
	private IBlockState mushroomBlackHat = BlockRegistry.BLACK_HAT_MUSHROOM.getDefaultState();
	private IBlockState mushroomBulbCapped = BlockRegistry.BULB_CAPPED_MUSHROOM.getDefaultState();
	private IBlockState mushroomflatHead = BlockRegistry.FLAT_HEAD_MUSHROOM.getDefaultState();
	private IBlockState energyBarrier = BlockRegistry.ENERGY_BARRIER.getDefaultState();

	private ILocationGuard guard;
	private Random lootRng;
	private SharedLootPoolStorage lootStorage;

	private static final ThreadLocal<Boolean> CASCADING_GEN_MUTEX = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	
	public WorldGenWightFortress() {
		//these sizes are subject to change
		length = 13;
		width = 13;
		height = 19;
	}

	protected boolean isProtectedBlock(IBlockState state) {
		Block block = state.getBlock();
		if(block != Blocks.AIR && block != BlockRegistry.MOB_SPAWNER && block != BlockRegistry.LOOT_POT
				&& block != BlockRegistry.ROOT && block instanceof BlockPlant == false && block != BlockRegistry.VALONITE_BLOCK &&
				block != BlockRegistry.SYRMORITE_BLOCK && block != BlockRegistry.OCTINE_BLOCK && block != BlockRegistry.WEEDWOOD_CHEST
				&& block != BlockRegistry.ITEM_CAGE && block != BlockRegistry.POSSESSED_BLOCK && block != BlockRegistry.WEAK_POLISHED_LIMESTONE
				&& block != BlockRegistry.WEAK_BETWEENSTONE_TILES && block != BlockRegistry.WEAK_MOSSY_BETWEENSTONE_TILES) {
			return true;
		}
		return false;
	}

	@Override
	protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
		if(this.isProtectedBlock(state)) {
			this.guard.setGuarded(worldIn, pos, true);
		} else {
			this.guard.setGuarded(worldIn, pos, false);
		}
		
		super.setBlockAndNotifyAdequately(worldIn, pos, state);
		
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile instanceof ISharedLootContainer) {
			ResourceLocation lootTable = this.getLootTableForBlock(worldIn, pos, state);
			
			if(lootTable != null) {
				((ISharedLootContainer) tile).setSharedLootTable(this.lootStorage, lootTable, this.lootRng.nextLong());
			}
		}
	}
	
	@Nullable
	protected ResourceLocation getLootTableForBlock(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		
		if(block == BlockRegistry.LOOT_POT) {
			return LootTableRegistry.WIGHT_FORTRESS_POT;
		} else if(block == BlockRegistry.WEEDWOOD_CHEST) {
			return LootTableRegistry.WIGHT_FORTRESS_CHEST;
		}
		
		return null;
	}

	protected boolean canGenerateAt(World world, Random rand, BlockPos pos) {
		MutableBlockPos checkPos = new MutableBlockPos();

		if(!this.isBiomeValid(world.getBiome(pos))) {
			return false;
		}
		
		if(!this.isBiomeValid(world.getBiome(pos.add(32, 0, 0)))) {
			return false;
		}

		if(!this.isBiomeValid(world.getBiome(pos.add(32, 0, 32)))) {
			return false;
		}

		if(!this.isBiomeValid(world.getBiome(pos.add(0, 0, 32)))) {
			return false;
		}

		for (int xa = 0; xa <= 31; ++xa) {
			for(int za = 0; za <= 31; ++za) {
				for(int ya = 0; ya < 42; ++ya ) {
					checkPos.setPos(pos.getX() + xa, pos.getY() + ya, pos.getZ() + za);
					IBlockState state = world.getBlockState(checkPos);
					boolean replaceable = state.getBlock() == Blocks.AIR || state.getBlock().isReplaceable(world, checkPos) || state.getBlock() instanceof BlockPlant ||
							state.getBlock() instanceof BlockDoublePlantBL;
					if(!replaceable) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public boolean isBiomeValid(Biome biome) {
		return biome == BiomeRegistry.MARSH_0 || biome == BiomeRegistry.MARSH_1;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		if(CASCADING_GEN_MUTEX.get()) {
			return false;
		}
		
		CASCADING_GEN_MUTEX.set(true);
		
		try {
			if(!this.canGenerateAt(world, rand, pos)) {
				return false;
			}
	
			
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
	
			long locationSeed = rand.nextLong();
	
			LocalRegion region = LocalRegion.getFromBlockPos(pos);
	
			//Shared loot storage
			this.lootRng = new Random(rand.nextLong());
			this.lootStorage = new SharedLootPoolStorage(worldStorage, new StorageUUID(UUID.randomUUID()), region, rand.nextLong());
			worldStorage.getLocalStorageHandler().addLocalStorage(this.lootStorage);
			
			LocationGuarded fortressLocation = new LocationGuarded(worldStorage, new StorageUUID(UUID.randomUUID()), region, "wight_tower", EnumLocationType.WIGHT_TOWER);
			this.guard = fortressLocation.getGuard();
			fortressLocation.addBounds(new AxisAlignedBB(pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10, pos.getX() + 42, pos.getY() + 80, pos.getZ() + 42));
			fortressLocation.setAmbience(new LocationAmbience(EnumLocationAmbience.WIGHT_TOWER).setFogRangeMultiplier(0.2F).setFogBrightness(80));
			fortressLocation.setLayer(0);
			fortressLocation.setDirty(true);
			fortressLocation.setSeed(locationSeed);
	
			LocationStorage puzzleLocation = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), region, "wight_tower_puzzle", EnumLocationType.WIGHT_TOWER);
			puzzleLocation.addBounds(new AxisAlignedBB(pos.getX() - 10 + 20, pos.getY() + 17, pos.getZ() - 10 + 20, pos.getX() + 42 - 20, pos.getY() + 17 + 6, pos.getZ() + 42 - 20));
			puzzleLocation.setLayer(1);
			puzzleLocation.setDirty(true);
			puzzleLocation.setSeed(locationSeed);
	
			LocationStorage teleporterLocation = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), region, "wight_tower_teleporter", EnumLocationType.WIGHT_TOWER);
			teleporterLocation.addBounds(new AxisAlignedBB(pos.getX() - 10 + 23, pos.getY() + 17 + 12, pos.getZ() - 10 + 23, pos.getX() + 42 - 23, pos.getY() + 17 + 6 + 11, pos.getZ() + 42 - 23));
			teleporterLocation.setLayer(2);
			teleporterLocation.setDirty(true);
			teleporterLocation.setSeed(locationSeed);
	
			LocationStorage bossLocation = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), region, "wight_tower_boss", EnumLocationType.WIGHT_TOWER);
			bossLocation.addBounds(new AxisAlignedBB(pos.getX() - 10 + 17, pos.getY() + 17 + 19, pos.getZ() - 10 + 17, pos.getX() + 42 - 17, pos.getY() + 17 + 12 + 32, pos.getZ() + 42 - 17));
			bossLocation.setAmbience(new LocationAmbience(EnumLocationAmbience.WIGHT_TOWER).setFogRange(12.0F, 20.0F).setFogColorMultiplier(0.1F));
			bossLocation.setLayer(3);
			bossLocation.setDirty(true);
			bossLocation.setSeed(locationSeed);
	
			if(generateStructure(world, rand, pos)) {
				worldStorage.getLocalStorageHandler().addLocalStorage(fortressLocation);
				worldStorage.getLocalStorageHandler().addLocalStorage(puzzleLocation);
				worldStorage.getLocalStorageHandler().addLocalStorage(teleporterLocation);
				worldStorage.getLocalStorageHandler().addLocalStorage(bossLocation);
				return true;
			}

			return false;
		} finally {
			CASCADING_GEN_MUTEX.set(false);
		}
	}

	public IBlockState getRandomWall(Random rand) {
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

	public IBlockState getRandomBricks(Random rand) {
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

	public IBlockState getRandomTiles(Random rand) {
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

	public IBlockState getRandomMetalBlock(Random rand) {
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

	public IBlockState getRandomMushroom(Random rand) {
		int rnd = rand.nextInt(30);
		if(rnd < 14) {
			return mushroomflatHead;
		} else if(rnd < 28) {
			return mushroomBlackHat;
		} else {
			return mushroomBulbCapped;
		}
	}

	public IBlockState getRandomCollapsingTiles(Random rand) {
		return rand.nextBoolean() ? betweenstoneTilesCollapsing : betweenstoneTilesMossyCollapsing;
	}

	public IBlockState getRandomSmoothBetweenstone(Random rand) {
		return rand.nextBoolean() ? betweenstoneSmooth : betweenstoneSmoothMossy;
	}

	public IBlockState getRandomSmoothBetweenstoneStairs(Random rand, IBlockState state, int blockMeta) {
		int type = rand.nextInt(4);

		switch (type) {
		case 0:
		case 1:
		case 2:
			break;
		case 3:
			state = betweenstoneStairsSmoothMossy;
			break;
		}

		return getStairRotations(state, blockMeta);
	}

	public IBlockState getRandomBetweenstoneBrickStairs(Random rand, IBlockState state, int blockMeta) {
		int type = rand.nextInt(8);

		switch (type) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			break;
		case 6:
			state = betweenstoneBrickStairsMossy;
			break;
		case 7:
			state = betweenstoneBrickStairsCracked;
			break;
		}

		return getStairRotations(state, blockMeta);
	}

	public IBlockState getStairRotations(IBlockState state, int blockMeta) {
		int direction = blockMeta;
		switch (direction) {
		case 0:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST);
		case 1:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST);
		case 2:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH);
		case 3:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH);
		case 4:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP);
		case 5:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP);
		case 6:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP);
		case 7:
			return state.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP);
		}
		return state;
	}

	public IBlockState getWeedWoodChestRotations(IBlockState state, int blockMeta) {
		int direction = blockMeta;
		switch (direction) {
		case 0: //unused
		case 1: //unused
		case 2:
			return state.withProperty(BlockChestBetweenlands.FACING, EnumFacing.NORTH);
		case 3:
			return state.withProperty(BlockChestBetweenlands.FACING, EnumFacing.SOUTH);
		case 4:
			return state.withProperty(BlockChestBetweenlands.FACING, EnumFacing.WEST);
		case 5:
			return state.withProperty(BlockChestBetweenlands.FACING, EnumFacing.EAST);
		}
		return state;
	}

	private IBlockState getLootPotRotations(IBlockState state, int blockMeta) {
		int direction = blockMeta;
		switch (direction) {
		case 0: //unused
		case 1: //unused
		case 2:
			return state.withProperty(BlockLootPot.FACING, EnumFacing.NORTH);
		case 3:
			return state.withProperty(BlockLootPot.FACING, EnumFacing.SOUTH);
		case 4:
			return state.withProperty(BlockLootPot.FACING, EnumFacing.WEST);
		case 5:
			return state.withProperty(BlockLootPot.FACING, EnumFacing.EAST);
		}
		return state;
	}

	public IBlockState getPossessedBlockRotations(IBlockState state, int blockMeta) {
		int direction = blockMeta;
		switch (direction) {
		case 0: //unused
		case 1: //unused
		case 2:
			return state.withProperty(BlockPossessedBlock.FACING, EnumFacing.NORTH);
		case 3:
			return state.withProperty(BlockPossessedBlock.FACING, EnumFacing.SOUTH);
		case 4:
			return state.withProperty(BlockPossessedBlock.FACING, EnumFacing.WEST);
		case 5:
			return state.withProperty(BlockPossessedBlock.FACING, EnumFacing.EAST);
		}
		return state;
	}

	private IBlockState getSlabType(IBlockState slabType, int blockMeta) {
		return blockMeta == 0 ? slabType.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM) : slabType.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	}

	public boolean generateStructure(World world, Random rand, BlockPos pos) {

		for (int xa = 0; xa <= 32; ++xa) {
			for(int za = 0; za <= 32; ++za) {
				for(int ya = - 12 ; ya < 0; ++ya ) {
					if(!world.getBlockState(pos.add(xa, ya, za)).isNormalCube())
						this.setBlockAndNotifyAdequately(world, pos.add(xa, ya, za), betweenstone);
				}
			}
		}

		// loot room AIR just to erase old one
		for (int xa = 8; xa <= 24; ++xa) {
			for(int za = 8; za <= 24; ++za) {
				for(int ya = - 8; ya < 0; ++ya ) {
					world.setBlockToAir(pos.add(xa, ya, za));
				}
			}
		}

		length = 32;
		width = 32;

		for (direction = 0; direction < 4; direction++) {
			//loot room
			rotatedCubeVolume(world, rand, pos, 8, -7, 8, betweenstoneBricks, 0, 8, 6, 1, direction);
			rotatedCubeVolume(world, rand, pos, 8, -7, 9, betweenstoneBricks, 0, 1, 6, 7, direction);
			rotatedCubeVolume(world, rand, pos, 8, -8, 8, betweenstoneSmooth, 0, 8, 1, 2, direction);
			rotatedCubeVolume(world, rand, pos, 8, -8, 9, betweenstoneSmooth, 0, 2, 1, 7, direction);
			rotatedCubeVolume(world, rand, pos, 10, -8, 10, betweenstoneTilesFortress, 0, 1, 1, 1, direction);
			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, pos, 10, -6, 10, spawner, 0, 1, 1, 1, direction);
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(10, -6, 10), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : rand.nextBoolean() ? "thebetweenlands:chiromaw" : "thebetweenlands:termite");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(21, -6, 21), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : rand.nextBoolean() ? "thebetweenlands:chiromaw" : "thebetweenlands:termite");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(10, -6, 21), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : rand.nextBoolean() ? "thebetweenlands:chiromaw" : "thebetweenlands:termite");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(21, -6, 10), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : rand.nextBoolean() ? "thebetweenlands:chiromaw" : "thebetweenlands:termite");

			rotatedCubeVolume(world, rand, pos, 11, -8, 10, betweenstoneTiles, 0, 5, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, -8, 11, betweenstoneTiles, 0, 1, 1, 5, direction);
			rotatedCubeVolume(world, rand, pos, 11, -8, 11, betweenstoneSmooth, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, pos, 12, -7, 12, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, -7, 13, betweenstoneStairsSmooth, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 3, direction);
			rotatedCubeVolume(world, rand, pos, 13, -7, 13, stagnantWater, 0, 3, 1, 3, direction);
			rotatedCubeVolume(world, rand, pos, 13, -7, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, -6, 9, betweenstonePillar, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, -4, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, -3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, -3, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, -3, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, -3, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -7, 13, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -6, 13, betweenstonePillar, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -4, 13, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -3, 13, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, -3, 13, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -3, 14, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -3, 12, betweenstoneBrickStairs, direction == 0 ? 6 : direction== 1 ? 4 : direction == 2 ? 7 : 5, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -2, 9, betweenstoneBricks, 0, 7, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -2, 10, betweenstoneBricks, 0, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, pos, 10, -2, 10, limestoneBrickSlab, 8, 3, 1, 3, direction);
			rotatedCubeVolume(world, rand, pos, 14, -2, 10, limestoneBrickSlab, 8, 2, 1, 3, direction);
			rotatedCubeVolume(world, rand, pos, 10, -2, 14, limestoneBrickSlab, 8, 3, 1, 2, direction);
			rotatedCubeVolume(world, rand, pos, 13, -2, 10, betweenstoneBricks, 0, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, pos, 10, -2, 13, betweenstoneBricks, 0, 6, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -7, 9, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -6, 9, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -5, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, -7, 9, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, -6, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -7, 11, getRandomMetalBlock(rand), 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, -6, 11, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

			//ground floors
			rotatedCubeVolume(world, rand, pos, 0, -1, 0, betweenstoneSmooth, 0, 13, 1, 13, direction);
			rotatedCubeVolume(world, rand, pos, 5, -1, 4, betweenstoneTiles, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 4, -1, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 6, -1, 5, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 7, -1, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 4, -1, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 5, -1, 6, limestonePolished, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 8, -1, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 4, -1, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 6, -1, 7, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 7, -1, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 5, -1, 8, betweenstoneTiles, 0, 3, 1, 1, direction);

			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, pos, 6, 2, 6, spawner, 0, 1, 1, 1, direction);
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 2, 6), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : "thebetweenlands:chiromaw");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 2, 6), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : "thebetweenlands:chiromaw");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 2, 25), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : "thebetweenlands:chiromaw");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 2, 25), rand.nextBoolean() ? "thebetweenlands:swamp_hag" : "thebetweenlands:chiromaw");

			//1st floors
			rotatedCubeVolume(world, rand, pos, 3, 5, 3, limestonePolished, 0, 7, 1, 7, direction);
			rotatedCubeVolume(world, rand, pos, 6, 5, 5, limestoneChiselled, 0, 1, 1, 3, direction);
			rotatedCubeVolume(world, rand, pos, 5, 5, 6, limestoneChiselled, 0, 3, 1, 1, direction);

			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, pos, 6, 8, 6, spawner, 0, 1, 1, 1, direction);

			//Make Pyrads always active
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound entityNbt = new NBTTagCompound();
			entityNbt.setString("id", "thebetweenlands:pyrad");
			EntityPyrad pyrad = new EntityPyrad(world);
			pyrad.getEntityAttribute(EntityPyrad.AGRESSIVE).setBaseValue(1);
			entityNbt.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(pyrad.getAttributeMap()));
			nbt.setTag("Entity", entityNbt);

			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 8, 6), "thebetweenlands:pyrad", logic -> logic.setNextEntity(new WeightedSpawnerEntity(nbt)).setCheckRange(16.0D).setMaxEntities(1));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 8, 6), "thebetweenlands:pyrad", logic -> logic.setNextEntity(new WeightedSpawnerEntity(nbt)).setCheckRange(16.0D).setMaxEntities(1));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 8, 25), "thebetweenlands:pyrad", logic -> logic.setNextEntity(new WeightedSpawnerEntity(nbt)).setCheckRange(16.0D).setMaxEntities(1));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 8, 25), "thebetweenlands:pyrad", logic -> logic.setNextEntity(new WeightedSpawnerEntity(nbt)).setCheckRange(16.0D).setMaxEntities(1));

			//2nd floors
			rotatedCubeVolume(world, rand, pos, 4, 11, 4, limestonePolished, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, pos, 6, 11, 6, limestoneChiselled, 0, 1, 1, 1, direction);
			if(rand.nextBoolean())
				rotatedCubeVolume(world, rand, pos, 6, 14, 6, spawner, 0, 1, 1, 1, direction);
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 14, 6), "thebetweenlands:termite");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 14, 6), "thebetweenlands:termite");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 14, 25), "thebetweenlands:termite");
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 14, 25), "thebetweenlands:termite");

			//3rd floors
			rotatedCubeVolume(world, rand, pos, 4, 16, 4, limestoneChiselled, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, pos, 5, 16, 4, betweenstoneTiles, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 4, 16, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 6, 16, 5, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 7, 16, 5, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 4, 16, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 5, 16, 6, limestonePolished, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 8, 16, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 4, 16, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 6, 16, 7, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 7, 16, 7, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 5, 16, 8, betweenstoneTiles, 0, 3, 1, 1, direction);

			rotatedCubeVolume(world, rand, pos, 6, 19, 6, spawner, 0, 1, 1, 1, direction);
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 19, 6), "thebetweenlands:wight", logic -> logic.setCheckRange(32.0D).setDelayRange(3000, 5000).setMaxEntities(3));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 19, 6), "thebetweenlands:wight", logic -> logic.setCheckRange(32.0D).setDelayRange(3000, 5000).setMaxEntities(3));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(25, 19, 25), "thebetweenlands:wight", logic -> logic.setCheckRange(32.0D).setDelayRange(3000, 5000).setMaxEntities(3));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(6, 19, 25), "thebetweenlands:wight", logic -> logic.setCheckRange(32.0D).setDelayRange(3000, 5000).setMaxEntities(3));

			if (rand.nextBoolean())
				rotatedCubeVolume(world, rand, pos, 16, 26, 16, spawner, 0, 1, 1, 1, direction);
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(16, 26, 16), "thebetweenlands:chiromaw", logic -> logic.setSpawnRange(2));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(16, 26, 15), "thebetweenlands:chiromaw", logic -> logic.setSpawnRange(2));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(15, 26, 16), "thebetweenlands:chiromaw", logic -> logic.setSpawnRange(2));
			BlockMobSpawnerBetweenlands.setMob(world, pos.add(15, 26, 15), "thebetweenlands:chiromaw", logic -> logic.setSpawnRange(2));

		}

		length = 13;
		width = 13;
		for (int tower = 0; tower  < 5; tower ++) {
			int x = 0, y = 0, z = 0;

			if (tower == 1)
				x = 19;

			if (tower == 2) {
				x = 19;
				z = 19;
			}

			if (tower == 3)
				z = 19;

			for (direction = 0; direction < 4; direction++) {
				if(tower < 4) {

					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 2, 1, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 7, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 4, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 8, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 0, 3, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 0, 0, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 3, 1, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 0, 1, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 0, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 4, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 4, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 10, 3, betweenstoneBricks, 0, 1, 1, 1, direction);

					//deco walls
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 4, 0, betweenstoneBrickWall, 0, 9, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 4, 1, betweenstoneBrickWall, 0, 1, 1, 3, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 4, 1, betweenstoneBrickWall, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 4, 1, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 4, 3, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 6, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 6, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 7, 2, betweenstoneBrickWall, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 9, 2, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 9, 3, betweenstoneBrickWall, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 14, 3, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 16, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 16, 3, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 17, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 17, 1, betweenstoneBrickWall, 0, 4, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 17, 3, betweenstoneBrickWall, 0, 1, 1, 3, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 16, 1, betweenstoneBrickWall, 0, 5, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 16, 0, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 15, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 18, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 18, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 16, 0, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 15, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 18, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 18, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 10, 2, betweenstoneBrickWall, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 14, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 10, 2, betweenstoneBrickWall, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 10, 1, betweenstoneBrickWall, 0, 3, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 5, 1, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 8, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 5, 1, betweenstoneBrickWall, 0, 1, 5, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 5, 0, betweenstoneBrickWall, 0, 3, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 9, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 9, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);

					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 3, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 4, 3, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 3, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 4, 3, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 4, 4, betweenstoneBrickSlab, 8, 5, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 3, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 3, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 0, 2, betweenstone, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 4, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 4, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 5, 2, betweenstoneBricks, 0, 1, 2, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 5, 3, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 5, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 3, 1, betweenstoneBricks, 0, 7, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 3, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 8, 3, betweenstoneBricks, 0, 1, 6, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 16, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 16, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 15, 3, betweenstoneTilesFortress, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 14, 3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 9, 2, betweenstoneTiles, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 8, 2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 5, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 3, 1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 0, 3, 3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 8, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 8, 2, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 8, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 8, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 11, 4, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
					// tower loot pots
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 3, 0, 1, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 9, 0, 1, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 6, 2, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 6, 2, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
					if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2)
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 10, 2, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

					//	tower chests
					rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 12, 4, chest,direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);
					if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2) {
						// should stop triple chests
						int chest_randomiser = rand.nextInt(7);

						if(chest_randomiser == 1 || chest_randomiser == 4 || chest_randomiser == 6)
							rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 17, 3, chest, direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);

						if(chest_randomiser == 2 || chest_randomiser == 4 || chest_randomiser == 5)
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 17, 3, chest, direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);

						if(chest_randomiser == 3 || chest_randomiser == 5 || chest_randomiser == 6)
							rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 17, 3, chest, direction == 0 ? 3 : direction== 1 ? 5 : direction == 2 ? 2 : 4, 1, 1, 1, direction);
					}
				}
			}
		}
		for (int tower = 0; tower  < 5; tower ++) {
			int x = 0, y = 0, z = 0;

			if (tower == 1)
				x = 19;

			if (tower == 2) {
				x = 19;
				z = 19;
			}

			if (tower == 3)
				z = 19;

			for (direction = 0; direction < 4; direction++) {
				if(tower < 4) {

					//walkways
					if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2) {
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 4, 11, betweenstoneTiles, 0, 3, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 5, 12, betweenstoneBricks, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 5, 12, betweenstoneBricks, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 4, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 3, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 6, 10, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 5, 11, Blocks.AIR.getDefaultState(), 0, 3, 5, 2, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 6, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 6, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 7, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 7, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 9, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 9, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 10, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 10, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 10, 10, betweenstoneTilesCollapsing, 0, 3, 1, 6, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 11, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 11, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 12, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 12, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 13, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 13, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 15, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 15, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 16, 10, betweenstoneTiles, 0, 3, 1, 6, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 11, 10, Blocks.AIR.getDefaultState(), 0, 3, 5, 2, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 17, 10, Blocks.AIR.getDefaultState(), 0, 3, 4, 3, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 5, 12, possessedBlock, tower == 0 && direction == 0 ? 5 : tower == 0 && direction == 1 ? 2 : tower == 1 && direction == 0 ? 5 : tower == 1 && direction == 3 ? 3 : tower == 2 && direction == 2 ? 4 : tower == 2 && direction == 3 ? 3 : tower == 3 && direction == 1 ? 2 : 4, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 5, 11, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 5, 12, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 5, 13, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 11, 10, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 11, 11, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 11, 12, lootPot1, direction == 0 ? 5 : direction== 1 ? 3 : direction == 2 ? 4 : 2, 1, 1, 1, direction);
					}

					//top Floor
					if (tower == 0 && direction == 0 || tower == 1 && direction == 3 || tower == 2 && direction == 2 || tower == 3 && direction == 1) {
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 16, 11, betweenstoneBricks, 0, 1, 3, 4, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 7, 17, 15, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 17, 15, spikeTrap, 0, 3, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 9, 17, 14, betweenstoneBricks, 0, 2, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 19, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 19, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 19, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 21, 10, betweenstoneBrickSlab, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 16, 12, betweenstoneBrickWall, 0, 1, 2, 4, direction);	
					}
					if (tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 2 && direction == 3 || tower == 3 && direction == 2) {
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 16, 11, betweenstoneBricks, 0, 1, 3, 4, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 5, 17, 15, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 17, 15, spikeTrap, 0, 3, 1, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 2, 17, 14, betweenstoneBricks, 0, 2, 2, 1, direction);//
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 19, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 19, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 19, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 4, 21, 10, betweenstoneBrickSlab, 0, 1, 1, 5, direction);
						rotatedCubeVolume(world, rand, pos.add(x, y, z), 8, 16, 12, betweenstoneBrickWall, 0, 1, 2, 4, direction);
					}
				}
				//top tower
				if(tower == 4) {
					length = 14;
					width = 14;
					x = 9;
					z = 9;
					y = 18;
					generateTopTowerRight(world, rand, pos.add(x, y, z), direction);
					generateTopTowerLeft(world, rand, pos.add(x, y, z), direction);
				}	
			}
		}
		//top tower decoration walls
		length = 32;
		width = 32;
		for (direction = 0; direction < 4; direction++) {
			rotatedCubeVolume(world, rand, pos, 10, 22, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 23, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 23, 11, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 24, 11, betweenstoneBrickWall, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 26, 11, betweenstoneBrickWall, 0, 1, 12, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 26, 12, betweenstoneBrickWall, 0, 1, 12, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 32, 12, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 34, 11, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 35, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 35, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 32, 11, betweenstoneBrickWall, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 32, 14, betweenstoneBrickWall, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 32, 11, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 32, 15, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 33, 10, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 33, 14, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 34, 9, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 9, 34, 14, betweenstoneBrickWall, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 34, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 34, 15, betweenstoneBrickWall, 0, 1, 2, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 36, 11, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 36, 14, betweenstoneBrickWall, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 26, 10, betweenstoneBrickWall, 0, 1, 11, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 26, 13, betweenstoneBrickWall, 0, 1, 11, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 22, 10, betweenstoneBrickWall, 0, 1, 4, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 22, 12, betweenstoneBrickWall, 0, 1, 4, 1, direction);
		}

		for (direction = 0; direction < 4; direction++) {
			// Len's Middle stuff
			//pillars
			rotatedCubeVolume(world, rand, pos, 13, 0, 9, betweenstonePillar, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 3, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 4, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 9, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 10, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 15, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 16, 9, betweenstonePillar, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 0, 9, betweenstonePillar, 0, 1, 3, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 3, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 4, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 9, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 10, 9, betweenstonePillar, 0, 1, 5, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 15, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 16, 9, betweenstonePillar, 0, 1, 1, 1, direction);

			//arches
			// lower
			rotatedCubeVolume(world, rand, pos, 14, 3, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 3, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 4, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 4, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 4, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 4, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 4, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 20, 4, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 4, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);

			// mid
			rotatedCubeVolume(world, rand, pos, 14, 9, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 9, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 10, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 10, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 9, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 10, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 9, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 20, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 10, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);

			// top
			rotatedCubeVolume(world, rand, pos, 14, 15, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 15, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 16, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 16, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 16, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 15, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 10, 16, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 12, 16, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 15, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 20, 16, 9, betweenstoneBricks, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 22, 15, 9, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 16, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 15, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 16, 10, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 16, 11, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 16, 12, betweenstoneBrickSlab, 8, 1, 1, 2, direction);
			rotatedCubeVolume(world, rand, pos, 14, 16, 12, betweenstoneBrickSlab, 8, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 15, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 16, 10, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 16, 11, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 16, 12, betweenstoneBrickSlab, 8, 1, 1, 1, direction);

			//floor
			rotatedCubeVolume(world, rand, pos, 13, -1, 5, betweenstoneTiles, 0, 6, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, -1, 6, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, -1, 7, betweenstoneStairsSmooth, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, pos, 15, -1, 7, betweenstoneSmooth, 0, 2, 1, 2, direction);
			rotatedCubeVolume(world, rand, pos, 17, -1, 7, betweenstoneStairsSmooth, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 6, direction);
			rotatedCubeVolume(world, rand, pos, 15, -1, 9, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, -1, 10, betweenstoneSmooth, 0, 2, 1, 3, direction);
			rotatedCubeVolume(world, rand, pos, 15, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, -1, 6, betweenstoneTiles, 0, 1, 1, 7, direction);
			rotatedCubeVolume(world, rand, pos, 18, -1, 6, betweenstoneTiles, 0, 1, 1, 7, direction);
			rotatedCubeVolume(world, rand, pos, 12, -1, 12, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, -1, 14, betweenstoneSmooth, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, -1, 15, stagnantWater, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, -2, 15, stagnantWater, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 3, 5, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 6, 1, 1, direction);

			// going back to my roots
			rotatedCubeVolume(world, rand, pos, 13, 0, 5, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 0, 5, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 5, 0, 13, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 5, 0, 18, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 1 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 0, 11, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 0, 11, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 0, 13, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 0, 18, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 4, 11, root, 0, 1, rand.nextInt(3) + 3, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 0, 14, root, 0, 1, rand.nextBoolean() ? rand.nextInt(4) + 4 : 0, 1, direction);
			rotatedCubeVolume(world, rand, pos, 16, 0, 14, root, 0, 1, rand.nextBoolean() ? rand.nextInt(3) + 2 : 0, 1, direction);

			// loot pots
			rotatedCubeVolume(world, rand, pos, 13, 0, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 0, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 5, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 16, 5, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 16, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 20, 11, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

		}
		//retro-gen betweenstoneBrickStairs
		direction = rand.nextInt(4);

		for (int xa = 1; xa <= 31; ++xa) {
			for(int za = 1; za <= 31; ++za) {
				if(world.getBlockState(pos.add(xa, -1, za)).isNormalCube() && world.isAirBlock(pos.add(xa, 0, za)))
					if(rand.nextInt(8) == 0)
						this.setBlockAndNotifyAdequately(world, pos.add(xa, 0, za), getRandomMushroom(rand));
			}
		}

		//main betweenstoneBrickStairs

		rotatedCubeVolume(world, rand, pos, 13, 0, 3, betweenstoneBricks, 0, 1, 5, 2, direction);
		rotatedCubeVolume(world, rand, pos, 13, 0, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 0, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 0, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 0, 3, betweenstoneBricks, 0, 1, 5, 2, direction);
		rotatedCubeVolume(world, rand, pos, 18, 0, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 0, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 0, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 0, 1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 2, 4, betweenstoneBricks, 0, 4, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 2, 3, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 15, 0, 4, betweenstoneBricks, 0, 2, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 0, 4, betweenstoneBricksMirage, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 17, 0, 4, betweenstoneBricksMirage, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, -1, 0, betweenstoneTiles, 0, 4, 1, 4, direction);
		rotatedCubeVolume(world, rand, pos, 15, 0, 3, chest, direction == 0 ? 4 : direction == 1 ? 3 : direction == 2 ? 5 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 16, 0, 3, chest, direction == 0 ? 4 : direction == 1 ? 3 : direction == 2 ? 5 : 2, 1, 1, 1, direction);

		rotatedCubeVolume(world, rand, pos, 13, 5, 3, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 4, 2, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 3, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 2, 0, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 5, 3, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 4, 2, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 3, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 2, 0, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 7, 4, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 7, 4, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 0, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 1, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 2, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 3, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 4, 4, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 5, 3, Blocks.AIR.getDefaultState(), 0, 4, 4, 2, direction);

		//infills
		rotatedCubeVolume(world, rand, pos, 4, 0, 13, betweenstone, 0, 1, 4, 6, direction);
		rotatedCubeVolume(world, rand, pos, 4, 4, 14, betweenstone, 0, 1, 1, 4, direction);
		rotatedCubeVolume(world, rand, pos, 3, 0, 14, betweenstone, 0, 1, 3, 4, direction);
		rotatedCubeVolume(world, rand, pos, 3, 3, 15, betweenstone, 0, 1, 1, 2, direction);
		rotatedCubeVolume(world, rand, pos, 27, 0, 13, betweenstone, 0, 1, 4, 6, direction);
		rotatedCubeVolume(world, rand, pos, 27, 4, 14, betweenstone, 0, 1, 1, 4, direction);
		rotatedCubeVolume(world, rand, pos, 28, 0, 14, betweenstone, 0, 1, 3, 4, direction);
		rotatedCubeVolume(world, rand, pos, 28, 3, 15, betweenstone, 0, 1, 1, 2, direction);
		rotatedCubeVolume(world, rand, pos, 13, 0, 27, betweenstone, 0, 6, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 4, 27, betweenstone, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 0, 28, betweenstone, 0, 4, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 15, 3, 28, betweenstone, 0, 2, 1, 1, direction);

		//2nd betweenstoneBrickStairs
		for(int count = 0; count < 6;count ++)
			rotatedCubeVolume(world, rand, pos, 16 + count, 5 + count, 24, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 17, 5, 24, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 6, 24, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 19, 7, 24, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 20, 8, 24, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 19, 5, 23, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 10, 24, Blocks.AIR.getDefaultState(), 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 22, 12, 24, possessedBlock, direction == 0 ? 4 : direction == 1 ? 3 : direction == 2 ? 5 : 2, 1, 1, 1, direction);

		//3rd betweenstoneBrickStairs
		for(int count = 0; count < 6 ;count ++)
			rotatedCubeVolume(world, rand, pos, 16 - count, 11 + count, 7, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 10, 11, 7, betweenstoneBricks, 0, 6, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 10, 12, 7, betweenstoneBricks, 0, 5, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 10, 13, 7, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 10, 14, 7, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 10, 15, 7, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 10, 16, 7, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 12, 16, 7, Blocks.AIR.getDefaultState(), 0, 3, 1, 1, direction);


		//top tower stairs
		rotatedCubeVolume(world, rand, pos, 17, 19, 23, Blocks.AIR.getDefaultState(), 0, 3, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 23, 17, 22, betweenstoneBricks, 0, 1, 4, 2, direction);
		rotatedCubeVolume(world, rand, pos, 22, 21, 10, betweenstoneBrickSlab, 0, 2, 1, 14, direction);
		rotatedCubeVolume(world, rand, pos, 22, 21, 14, betweenstoneBricks, 0, 1, 1, 4, direction);
		rotatedCubeVolume(world, rand, pos, 22, 21, 10, betweenstoneBricks, 0, 2, 1, 1, direction);//
		rotatedCubeVolume(world, rand, pos, 22, 18, 22, betweenstoneBricks, 0, 1, 3, 2, direction);
		rotatedCubeVolume(world, rand, pos, 19, 19, 23, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 20, 20, 23, betweenstoneBricks, 0, 2, 1, 1, direction);
		for(int count = 0; count < 3 ;count ++)
			rotatedCubeVolume(world, rand, pos, 17 + count, 18 + count, 23, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 19, 22, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 20, 21, 22, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

		for(int count = 0; count < 6 ;count ++)        
			rotatedCubeVolume(world, rand, pos, 22 - count, 22 + count, 10, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 18, 27, 10, Blocks.AIR.getDefaultState(), 0, 1, 5, 1, direction);
		rotatedCubeVolume(world, rand, pos, 21, 22, 10, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 19, 22, 10, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 15, 27, 10, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 27, 10, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 13, 26, 10, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 17, 26, 10, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 14, 26, 10, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 17, 29, 11, obviousSign, direction == 0 ? 2 : direction == 1 ? 4 : direction == 2 ? 3 : 5, 1, 1, 1, direction);
		//top tower floors

		//underneath
		rotatedCubeVolume(world, rand, pos, 9, 17, 9, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, pos, 9, 17, 11, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, pos, 9, 17, 18, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, pos, 9, 17, 21, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, pos, 18, 17, 21, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, pos, 18, 17, 9, limestonePolished, 0, 5, 1, 2, 0);
		rotatedCubeVolume(world, rand, pos, 21, 17, 11, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, pos, 21, 17, 18, limestonePolished, 0, 2, 1, 3, 0);
		rotatedCubeVolume(world, rand, pos, 11, 17, 11, spikeTrap, 0, 10, 1, 10, 0);
		rotatedCubeVolume(world, rand, pos, 9, 17, 9, betweenstoneBricks, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 22, 17, 22, betweenstoneBricks, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 9, 17, 22, betweenstoneBricks, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 22, 17, 9, betweenstoneBricks, 0, 1, 1, 1, 0);

		setSwordStone(world, rand, pos.add(12, 22, 12), swordStone, (byte) 0);
		setSwordStone(world, rand, pos.add(19, 22, 12), swordStone, (byte) 1);
		setSwordStone(world, rand, pos.add(19, 22, 19), swordStone, (byte) 2);
		setSwordStone(world, rand, pos.add(12, 22, 19), swordStone, (byte) 3);

		rotatedCubeVolume(world, rand, pos, 12, 17, 12, betweenstoneTilesFortress, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 19, 17, 12, betweenstoneTilesFortress, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 19, 17, 19, betweenstoneTilesFortress, 0, 1, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 12, 17, 19, betweenstoneTilesFortress, 0, 1, 1, 1, 0);

		EntitySwordEnergy swordEnergy = new EntitySwordEnergy(world);
		swordEnergy.setPosition(pos.getX() + 16D, pos.getY() + 21.5, pos.getZ() + 16D);
		world.spawnEntity(swordEnergy);

		//floor 1
		rotatedCubeVolume(world, rand, pos, 12, 23, 12, limestonePolishedCollapsing, 0, 8, 1, 8, 0);
		rotatedCubeVolume(world, rand, pos, 16, 24, 16, chest, direction == 0 ? 5 : direction == 1 ? 2 : direction == 2 ? 4 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 16, 24, 15, chest, direction == 0 ? 5 : direction == 1 ? 2 : direction == 2 ? 4 : 3, 1, 1, 1, direction);

		//floor2
		rotatedCubeVolume(world, rand, pos, 13, 28, 13, limestonePolished, 0, 6, 1, 6, 0);

		EntityFortressBossTeleporter tp = new EntityFortressBossTeleporter(world);
		tp.setLocationAndAngles(pos.getX() + 16, pos.getY() + 30, pos.getZ() + 16, 0, 0);
		tp.setTeleportDestination(new Vec3d(pos.getX() + 16, pos.getY() + 17 + 19.2D, pos.getZ() + 16));
		tp.setBossSpawnPosition(new BlockPos(pos.getX() + 16, pos.getY() + 17 + 19 + 5.2D, pos.getZ() + 16));
		world.spawnEntity(tp);
		 
		//floor3 (Boss fight Floor)
		rotatedCubeVolume(world, rand, pos, 13, 35, 13, betweenstoneTiles, 0, 6, 1, 6, 0);
		rotatedCubeVolume(world, rand, pos, 14, 35, 12, betweenstoneTiles, 0, 4, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 14, 35, 19, betweenstoneTiles, 0, 4, 1, 1, 0);
		rotatedCubeVolume(world, rand, pos, 12, 35, 14, betweenstoneTiles, 0, 1, 1, 4, 0);
		rotatedCubeVolume(world, rand, pos, 19, 35, 14, betweenstoneTiles, 0, 1, 1, 4, 0);

		// more loot pots and energy barrier
		for (direction = 0; direction < 4; direction++) {
			rotatedCubeVolume(world, rand, pos, 12, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 11, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 13, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);	
			rotatedCubeVolume(world, rand, pos, 17, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 17, 7, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 18, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 20, 18, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 22, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 16, 22, 9, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 15, 28, 11, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 16, 28, 11, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

			//sword room pots
			rotatedCubeVolume(world, rand, pos, 12, 18, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 14, 19, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 17, 19, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, pos, 19, 18, 10, lootPot1, direction == 0 ? 2 : direction== 1 ? 5 : direction == 2 ? 3 : 4, 1, 1, 1, direction);

			//energy barrier
			rotatedCubeVolume(world, rand, pos, 15, 29, 13, energyBarrier, 0, 2, 4, 1, direction);
		}
		//System.out.println("FORTRESS GEN HERE!: " + pos);
		return true;
	}

	private void placeChest(World world, Random rand, BlockPos pos, int blockMeta) {
		this.setBlockAndNotifyAdequately(world, pos, getWeedWoodChestRotations(chest, blockMeta));
	}

	private void placeRandomisedLootPot(World world, Random rand, BlockPos pos, IBlockState blockType, int blockMeta) {
		if(rand.nextInt(5) != 0 || world.isAirBlock(pos.down())) {
			return;
		} else {
			this.setBlockAndNotifyAdequately(world, pos, getLootPotRotations(blockType, blockMeta));
			TileEntityLootPot lootPot = BlockLootPot.getTileEntity(world, pos);
			if (lootPot != null) {
				//TODO Make proper shared loot tables
				lootPot.setModelRotationOffset(world.rand.nextInt(41) - 20);
				world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
			}
		}
	}


	public void setSwordStone(World world, Random rand, BlockPos pos, IBlockState blockType, byte type) {
		this.setBlockAndNotifyAdequately(world, pos, blockType);
		TileEntityItemCage swordStone = (TileEntityItemCage) world.getTileEntity(pos);
		if (swordStone != null)
			swordStone.setType(type);
	}

	private void placeSign(World world, Random rand, BlockPos pos, IBlockState state, int blockMeta) {
		this.setBlockAndNotifyAdequately(world, pos, state.withProperty(BlockWallWeedwoodSign.FACING, EnumFacing.byIndex(blockMeta)));
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityWeedwoodSign) {
			TileEntityWeedwoodSign sign = (TileEntityWeedwoodSign) tile;
			sign.signText[0] = new TextComponentTranslation("sign.fortress.line1");
			sign.signText[1] = new TextComponentTranslation("sign.fortress.line2");
			sign.signText[2] = new TextComponentTranslation("sign.fortress.line3");
			sign.signText[3] = new TextComponentTranslation("sign.fortress.line4");
			sign.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
		}
	}

	public void rotatedCubeVolume(World world, Random rand, BlockPos pos, int offsetA, int offsetB, int offsetC, IBlockState blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		//special cases here

		switch (direction) {
		case 0:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = offsetA; xx < offsetA + sizeWidth; xx++)
					for (int zz = offsetC; zz < offsetC + sizeDepth; zz++) {
						if(blockType == limestoneBrickSlab || blockType == betweenstoneBrickSlab)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getSlabType(blockType, blockMeta));
						else if(blockType == betweenstoneTiles)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomTiles(rand));
						else if(blockType == betweenstoneBricks)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomBricks(rand));
						else if(blockType == betweenstoneBrickWall)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomWall(rand));
						else if(blockType == betweenstoneBrickStairs)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomBetweenstoneBrickStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneStairsSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomSmoothBetweenstoneStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomSmoothBetweenstone(rand));
						else if(blockType == betweenstoneTilesCollapsing)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomCollapsingTiles(rand));
						else if(blockType == possessedBlock)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getPossessedBlockRotations(blockType, blockMeta));
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= 17 && yy != 0) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
							} else if (yy > 17 || yy == 0)
								placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, pos.add(xx, yy, zz), obviousSign, blockMeta);
						else
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), blockType);
					}
			break;
		case 1:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = length - offsetA - 1; zz > length - offsetA - sizeWidth - 1; zz--)
					for (int xx = offsetC; xx < offsetC + sizeDepth; xx++) {
						if(blockType == limestoneBrickSlab || blockType == betweenstoneBrickSlab)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getSlabType(blockType, blockMeta));
						else if(blockType == betweenstoneTiles)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomTiles(rand));
						else if(blockType == betweenstoneBricks)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomBricks(rand));
						else if(blockType == betweenstoneBrickWall)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomWall(rand));
						else if(blockType == betweenstoneBrickStairs)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomBetweenstoneBrickStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneStairsSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomSmoothBetweenstoneStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomSmoothBetweenstone(rand));
						else if(blockType == betweenstoneTilesCollapsing)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomCollapsingTiles(rand));
						else if(blockType == possessedBlock)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getPossessedBlockRotations(blockType, blockMeta));
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= 17 && yy != 0) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
							} else if (yy > 17 || yy == 0)
								placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, pos.add(xx, yy, zz), obviousSign, blockMeta);
						else
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), blockType);
					}
			break;
		case 2:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = length - offsetA - 1; xx > length - offsetA - sizeWidth - 1; xx--)
					for (int zz = length - offsetC - 1; zz > length - offsetC - sizeDepth - 1; zz--) {
						if(blockType == limestoneBrickSlab || blockType == betweenstoneBrickSlab)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getSlabType(blockType, blockMeta));
						else if(blockType == betweenstoneTiles)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomTiles(rand));
						else if(blockType == betweenstoneBricks)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomBricks(rand));
						else if(blockType == betweenstoneBrickWall)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomWall(rand));
						else if(blockType == betweenstoneBrickStairs)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomBetweenstoneBrickStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneStairsSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomSmoothBetweenstoneStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomSmoothBetweenstone(rand));
						else if(blockType == betweenstoneTilesCollapsing)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomCollapsingTiles(rand));
						else if(blockType == possessedBlock)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getPossessedBlockRotations(blockType, blockMeta));
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= 17 && yy != 0) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
							} else if (yy > 17 || yy == 0)
								placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, pos.add(xx, yy, zz), obviousSign, blockMeta);
						else
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), blockType);
					}
			break;
		case 3:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = offsetA; zz < offsetA + sizeWidth; zz++)
					for (int xx = length - offsetC - 1; xx > length - offsetC - sizeDepth - 1; xx--) {
						if(blockType == limestoneBrickSlab || blockType == betweenstoneBrickSlab)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getSlabType(blockType, blockMeta));
						else if(blockType == betweenstoneTiles)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomTiles(rand));
						else if(blockType == betweenstoneBricks)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomBricks(rand));
						else if(blockType == betweenstoneBrickWall)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomWall(rand));
						else if(blockType == betweenstoneBrickStairs)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomBetweenstoneBrickStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneStairsSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getRandomSmoothBetweenstoneStairs(rand, blockType, blockMeta));
						else if(blockType == betweenstoneSmooth)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomSmoothBetweenstone(rand));
						else if(blockType == betweenstoneTilesCollapsing)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : getRandomCollapsingTiles(rand));
						else if(blockType == possessedBlock)
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), getPossessedBlockRotations(blockType, blockMeta));
						else if(blockType == lootPot1)
							placeRandomisedLootPot(world, rand, pos.add(xx, yy, zz), rand.nextBoolean() ? blockType : rand.nextBoolean() ? lootPot2 : lootPot3, blockMeta);
						else if (blockType == chest) {
							if (yy <= 17 && yy != 0) {
								if (rand.nextInt(4) == 0)
									placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
							} else if (yy > 17 || yy == 0)
								placeChest(world, rand, pos.add(xx, yy, zz), blockMeta);
						}
						else if (blockType == obviousSign)
							placeSign(world, rand, pos.add(xx, yy, zz), obviousSign, blockMeta);
						else
							this.setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), blockType);
					}
			break;
		}
	}

	public void generateTopTowerRight(World world, Random rand, BlockPos pos, int direction) {		   
		rotatedCubeVolume(world, rand, pos, 0, 0, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 0, 0, 3, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 1, 0, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos, 1, 0, 2, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, pos, 2, 0, 1, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, pos, 4, 0, 1, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, pos, 2, 4, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 2, 4, 3, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos, 3, 4, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos, 3, 3, 1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 1, 3, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 3, 8, 3, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, pos, 4, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 16, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 5, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 16, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 4, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, pos, 5, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
		rotatedCubeVolume(world, rand, pos, 1, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 3, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 5, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 4, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 5, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 15, 3, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 14, 3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 9, 2, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 8, 2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 5, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 5, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 0, 3, 1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 0, 3, 3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);    	
		rotatedCubeVolume(world, rand, pos, 5, 4, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos, 6, 10, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
	}

	public void generateTopTowerLeft(World world, Random rand, BlockPos pos, int direction) {
		int x = 0;
		int z = 0;
		if(direction == 2)
			z = -13;
		if(direction == 0)
			z = 13;
		if(direction == 3)
			x = -13;
		if(direction == 1)
			x = 13;

		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 0, 0, -1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 0, 0, -3, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 1, 0, -1, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 1, 0, -2, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 2, 0, -1, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 4, 0, -1, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 2, 4, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 2, 4, -3, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 3, 4, -2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 3, 3, -1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 1, 3, -3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 3, 8, -3, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 4, 8, -2, betweenstoneBricks, 0, 1, 11, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 16, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 5, 17, -2, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 16, -3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 4, 10, -3, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 5, 10, -3, betweenstoneBricks, 0, 1, 7, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 1, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 3, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 5, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 4, 5, -2, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 5, 6, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 15, -3,betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 14, -3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 9, -2, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 8, -2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 5, 9, -2, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 6, 5, -2, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 0, 3, -1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, pos.add(x, 0, z), 0, 3, -3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	}

}
