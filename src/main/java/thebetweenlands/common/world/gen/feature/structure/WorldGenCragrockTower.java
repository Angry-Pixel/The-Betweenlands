package thebetweenlands.common.world.gen.feature.structure;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

public class WorldGenCragrockTower extends WorldGenHelper {
	private static IBlockState CRAGROCK;
	private static IBlockState MOSSY_CRAGROCK_TOP;
	private static IBlockState MOSSY_CRAGROCK_BOTTOM;
	private static IBlockState CRAGROCK_BRICKS;
	private static IBlockState SMOOTH_CRAGROCK_STAIRS;
	private static IBlockState CRAGROCK_BRICK_SLAB;
	private static IBlockState CRAGROCK_BRICK_SLAB_UPSIDEDOWN;
	private static IBlockState SMOOTH_CRAGROCK_SLAB;
	private static IBlockState SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN;
	private static IBlockState CRAGROCK_BRICK_STAIRS;
	private static IBlockState CRAGROCK_PILLAR;
	private static IBlockState SMOOTH_CRAGROCK;
	private static IBlockState CHISELED_CRAGROCK;
	private static IBlockState ROOT;
	private static IBlockState SMOOTH_BETWEENSTONE_WALL;
	private static IBlockState CRAGROCK_BRICK_WALL;
	private static IBlockState SMOOTH_CRAGROCK_WALL;
	private static IBlockState INACTIVE_GLOWING_SMOOTH_CRAGROCK;
	private static IBlockState AIR;

	private Random lootRng;
	private SharedLootPoolStorage lootStorage;
	private ILocationGuard guard;
	private LocationCragrockTower towerLocation;
	private BetweenlandsWorldStorage worldStorage;

	private static final ThreadLocal<Boolean> CASCADING_GEN_MUTEX = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	
	public WorldGenCragrockTower() {
		super(17, 64, 19);
	}

	protected boolean isProtectedBlock(IBlockState state) {
		Block block = state.getBlock();
		if(block != Blocks.AIR && block != BlockRegistry.MOB_SPAWNER && block != BlockRegistry.LOOT_POT && block != BlockRegistry.ROOT && block != BlockRegistry.WEAK_SMOOTH_CRAGROCK) {
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
			return LootTableRegistry.CRAGROCK_TOWER_POT;
		} else if(block == BlockRegistry.WEEDWOOD_CHEST) {
			return LootTableRegistry.CRAGROCK_TOWER_CHEST;
		}
		
		return null;
	}
	
	@Override
	public boolean generate(World worldIn, Random rand, BlockPos pos) {
		if(CASCADING_GEN_MUTEX.get()) {
			return false;
		}
		
		CASCADING_GEN_MUTEX.set(true);
		
		try {
			CRAGROCK = BlockRegistry.CRAGROCK.getDefaultState();
			MOSSY_CRAGROCK_TOP = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, BlockCragrock.EnumCragrockType.MOSSY_1);
			MOSSY_CRAGROCK_BOTTOM = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, BlockCragrock.EnumCragrockType.MOSSY_2);
			CRAGROCK_BRICKS = BlockRegistry.CRAGROCK_BRICKS.getDefaultState();
			SMOOTH_CRAGROCK_STAIRS = BlockRegistry.SMOOTH_CRAGROCK_STAIRS.getDefaultState();
			CRAGROCK_BRICK_SLAB = BlockRegistry.CRAGROCK_BRICK_SLAB.getDefaultState();
			CRAGROCK_BRICK_SLAB_UPSIDEDOWN = BlockRegistry.CRAGROCK_BRICK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, BlockSlabBetweenlands.EnumBlockHalfBL.TOP);
			SMOOTH_CRAGROCK_SLAB = BlockRegistry.SMOOTH_CRAGROCK_SLAB.getDefaultState();
			SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN = BlockRegistry.SMOOTH_CRAGROCK_SLAB.getDefaultState().withProperty(BlockSlabBetweenlands.HALF, BlockSlabBetweenlands.EnumBlockHalfBL.TOP);
			CRAGROCK_BRICK_STAIRS = BlockRegistry.CRAGROCK_BRICK_STAIRS.getDefaultState();
			CRAGROCK_PILLAR = BlockRegistry.CRAGROCK_PILLAR.getDefaultState();
			SMOOTH_CRAGROCK = BlockRegistry.SMOOTH_CRAGROCK.getDefaultState();
			ROOT = BlockRegistry.ROOT.getDefaultState();
			SMOOTH_BETWEENSTONE_WALL = BlockRegistry.SMOOTH_BETWEENSTONE_WALL.getDefaultState();
			CRAGROCK_BRICK_WALL = BlockRegistry.SMOOTH_CRAGROCK_WALL.getDefaultState();
			SMOOTH_CRAGROCK_WALL = BlockRegistry.SMOOTH_CRAGROCK_WALL.getDefaultState();
			INACTIVE_GLOWING_SMOOTH_CRAGROCK = BlockRegistry.INACTIVE_GLOWING_SMOOTH_CRAGROCK.getDefaultState();
			CHISELED_CRAGROCK = BlockRegistry.CRAGROCK_CHISELED.getDefaultState();
			AIR = Blocks.AIR.getDefaultState();
	
			while (worldIn.isAirBlock(pos) && pos.getY() > WorldProviderBetweenlands.LAYER_HEIGHT)
				pos = pos.add(0, -1, 0);
	
	
			this.worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
			this.towerLocation = new LocationCragrockTower(this.worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos));
			this.guard = this.towerLocation.getGuard();
	
			//Shared loot storage
			this.lootRng = new Random(rand.nextLong());
			this.lootStorage = new SharedLootPoolStorage(this.worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), rand.nextLong());
			this.worldStorage.getLocalStorageHandler().addLocalStorage(this.lootStorage);
			
			return tower(worldIn, rand, pos.getX(), pos.getY(), pos.getZ());
		} finally {
			CASCADING_GEN_MUTEX.set(false);
		}
	}


	private boolean canGenerate(World world, int x, int y, int z, int direction, int width, int depth) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
		case 0:
			for (int yy = y; yy < y + height; yy++)
				for (int xx = x; xx < x + width; xx++)
					for (int zz = z; zz < z + depth; zz++) {
						if (!(world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.BETWEENSTONE || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.CRAGROCK || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_DIRT || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_GRASS || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.WEEDWOOD_BUSH || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() instanceof BlockBush || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))))
							return false;
					}
			break;
		case 1:
			for (int yy = y; yy < y + height; yy++)
				for (int zz = z + depth - 1; zz > z + depth - width - 1; zz--)
					for (int xx = x; xx < x + depth; xx++) {
						if (!(world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.BETWEENSTONE || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.CRAGROCK || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_DIRT || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_GRASS || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.WEEDWOOD_BUSH || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() instanceof BlockBush || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))))
							return false;

					}
			break;
		case 2:
			for (int yy = y; yy < y + height; yy++)
				for (int xx = x + width - 1; xx > x + width - width - 1; xx--)
					for (int zz = z + depth - 1; zz > z + depth - depth - 1; zz--) {
						if (!(world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.BETWEENSTONE || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.CRAGROCK || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_DIRT || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_GRASS || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.WEEDWOOD_BUSH || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() instanceof BlockBush || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))))
							return false;

					}
			break;
		case 3:
			for (int yy = y; yy < y + height; yy++)
				for (int zz = z; zz < z + width; zz++)
					for (int xx = x + width - 1; xx > x + width - depth - 1; xx--) {
						if (!(world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.BETWEENSTONE || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.CRAGROCK || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_DIRT || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock() == BlockRegistry.SWAMP_GRASS || world.getBlockState(this.getCheckPos(xx, yy, zz)).getBlock().isReplaceable(world, this.getCheckPos(xx, yy, zz))))
							return false;

					}
			break;
		}
		return true;
	}

	private boolean tower(World world, Random random, int x, int y, int z) {
		int direction = random.nextInt(4);

		BlockPos highestPoint = new BlockPos(x, y, z);
		for(int xo = -8; xo <= 8; xo++) {
			for(int zo = -8; zo <= 8; zo++) {
				BlockPos localPoint = world.getHeight(new BlockPos(x + xo, y, z + zo));
				if(localPoint.getY() - y >= 0 && localPoint.getY() - y <= 5 && localPoint.getY() > highestPoint.getY()) {
					highestPoint = localPoint;
				}
			}
		}
		x = highestPoint.getX();
		y = highestPoint.getY();
		z = highestPoint.getZ();

		if (!canGenerate(world, x, y, z, direction, 21, 21))
			return false;

		if (!rotatedCubeMatches(world, x, y, z, 1, -1, 7, 2, 1, 1, direction, SurfaceType.MIXED_GROUND_OR_REPLACEABLE)
				|| !rotatedCubeMatches(world, x, y, z, 14, -1, 7, 2, 1, 1, direction, SurfaceType.MIXED_GROUND_OR_REPLACEABLE))
			return false;

		rotatedCubeVolume(world, x, y, z, 0, 0, 3, Blocks.AIR.getDefaultState(), width, height, depth - 3, direction);

		List<BlockPos> inactiveGlowingCragrockBlocks = new ArrayList<BlockPos>();
		List<BlockPos> inactiveWisps = new ArrayList<BlockPos>();
		List<BlockPos> blockades = new ArrayList<BlockPos>();
		BlockPos[][] levelBlockades = new BlockPos[5][];

		//FLOOR 0
		//WALLS
		rotatedCubeVolumeExtendedDown(world, x, y, z, 7, 0, 5, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 8, 0, 5, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 9, 0, 5, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 5, 0, 6, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 6, 0, 6, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 10, 0, 6, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 11, 0, 6, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 4, 0, 7, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 4, 0, 8, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 12, 0, 7, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 12, 0, 8, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 3, 0, 9, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 3, 0, 10, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 3, 0, 11, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 13, 0, 9, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 13, 0, 10, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 13, 0, 11, CRAGROCK, 1, 3, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 12, 0, 12, CRAGROCK, 1, 5, 2, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 12, 0, 13, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 4, 0, 12, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 4, 0, 13, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 10, 0, 14, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 11, 0, 14, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 5, 0, 14, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 6, 0, 14, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 7, 0, 15, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 8, 0, 15, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 9, 0, 15, CRAGROCK, 1, 5, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 3, 5, CRAGROCK_BRICKS, 3, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 3, 6, CRAGROCK_BRICKS, 2, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 3, 6, CRAGROCK_BRICKS, 2, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 3, 7, CRAGROCK_BRICKS, 1, 2, 2, direction);
		rotatedCubeVolume(world, x, y, z, 12, 3, 7, CRAGROCK_BRICKS, 1, 2, 2, direction);
		rotatedCubeVolume(world, x, y, z, 3, 3, 9, CRAGROCK_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 3, 9, CRAGROCK_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 3, 10, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 3, 10, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 4, 10, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 4, 10, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 3, 11, CRAGROCK, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 3, 11, CRAGROCK, 1, 2, 1, direction);

		//FLOOR
		rotatedCubeVolume(world, x, y, z, 7, 0, 6, SMOOTH_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 7, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 7, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 0, 7, SMOOTH_CRAGROCK, 1, 1, 8, direction);
		rotatedCubeVolume(world, x, y, z, 9, 0, 7, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 7, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 8, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 8, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 0, 8, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 8, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 0, 8, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 9, SMOOTH_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 9, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 9, SMOOTH_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 9, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 0, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 0, 9, SMOOTH_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 10, SMOOTH_CRAGROCK, 7, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 11, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 11, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 11, SMOOTH_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 11, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 0, 11, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 12, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 0, 12, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 0, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 13, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 0, 13, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 14, SMOOTH_CRAGROCK, 3, 1, 1, direction);

		//CEILING
		rotatedCubeVolume(world, x, y, z, 7, 4, 6, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 7, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 4, 7, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 4, 7, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 9, 4, 7, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 4, 7, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 4, 8, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 2, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 9, 4, 8, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 2, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 8, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 4, 9, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 9, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 9, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 4, 9, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 10, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 10, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 4, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 4, 13, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 4, 13, SMOOTH_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 9, 4, 13, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 4, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 4, 14, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 4, 14, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 8, 1, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 2, 10, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 3, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 4, 12, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 10, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 11, SMOOTH_CRAGROCK, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 3, 12, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 3, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 14, SMOOTH_CRAGROCK, 1, 3, 1, direction);
		rotatedSpawner(world, x, y, z, 8, 2, 13, direction, "thebetweenlands:termite").setMaxEntities(6).setDelayRange(120, 300).setSpawnInAir(false);
		rotatedCubeVolume(world, x, y, z, 9, 1, 11, ROOT, 1, 2 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 10, ROOT, 1, 2 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 1, 14, ROOT, 1, 1 + random.nextInt(2), 1, direction);
		rotatedLootPot(world, random, x, y, z, 9, 1, 14, direction, 1, 2, 3, null);
		rotatedLootPot(world, random, x, y, z, 12, 1, 9, direction, 1, 2, 3, null);
		rotatedLootPot(world, random, x, y, z, 12, 1, 10, direction, 1, 2, 3, null);
		rotatedLootPot(world, random, x, y, z, 12, 1, 11, direction, 1, 2, 3, null);

		//FLOOR 1
		//WALLS
		rotatedCubeVolume(world, x, y, z, 7, 5, 5, CRAGROCK_BRICKS, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 6, CRAGROCK_BRICKS, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 5, 6, CRAGROCK_BRICKS, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 5, 7, CRAGROCK_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 12, 5, 7, CRAGROCK_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 3, 5, 9, CRAGROCK_BRICKS, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 13, 5, 9, CRAGROCK_BRICKS, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 5, 12, CRAGROCK_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 5, 12, CRAGROCK_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 10, 5, 14, CRAGROCK_BRICKS, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 14, CRAGROCK_BRICKS, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 5, 15, CRAGROCK_BRICKS, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 6, 5, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 6, 6, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 6, 6, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 6, 7, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 12, 6, 7, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 3, 6, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 13, 6, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 6, 12, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 6, 12, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 10, 6, 14, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 6, 14, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 6, 15, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 7, 5, CRAGROCK_BRICKS, 3, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 7, 6, CRAGROCK_BRICKS, 2, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 7, 6, CRAGROCK_BRICKS, 2, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 7, 7, CRAGROCK_BRICKS, 1, 6, 2, direction);
		rotatedCubeVolume(world, x, y, z, 12, 7, 7, CRAGROCK_BRICKS, 1, 6, 2, direction);
		rotatedCubeVolume(world, x, y, z, 3, 7, 9, CRAGROCK_BRICKS, 1, 6, 3, direction);
		rotatedCubeVolume(world, x, y, z, 13, 7, 9, CRAGROCK_BRICKS, 1, 6, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 7, 12, CRAGROCK_BRICKS, 1, 6, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 7, 12, CRAGROCK_BRICKS, 1, 6, 2, direction);
		rotatedCubeVolume(world, x, y, z, 10, 7, 14, CRAGROCK_BRICKS, 2, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 7, 14, CRAGROCK_BRICKS, 2, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 7, 15, CRAGROCK_BRICKS, 3, 6, 1, direction);

		//CEILING
		rotatedCubeVolume(world, x, y, z, 7, 9, 6, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 7, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 9, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 9, 7, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 9, 9, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 9, 7, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 8, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 9, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 9, 9, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 8, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 9, 9, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 9, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 9, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 9, 9, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 11, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 11, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 12, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 12, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 9, 13, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 9, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 9, 13, CRAGROCK_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 9, 9, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 9, 13, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 9, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 9, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);

		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 8, 5, 8, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 6, 9, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 7, 10, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 8, 11, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 9, 12, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 5, 9, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 6, 10, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 7, 11, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 8, 12, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 8, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 7, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 5, 15, Blocks.AIR.getDefaultState(), 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 7, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 5, 7, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 13, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 5, 13, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 11, ROOT, 1, 2 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 5, 6, ROOT, 1, 2 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 5, 14, ROOT, 1, 2 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 5, 10, ROOT, 1, 2 + random.nextInt(2), 1, direction);
		rotatedLootPot(world, random, x, y, z, 8, 5, 6, direction, 1, 2, 3, null);

		//FLOOR 2
		//CEILING/WALLS
		rotatedCubeVolume(world, x, y, z, 7, 12, 6, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 12, 7, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 12, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 12, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 12, 7, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 12, 8, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 12, 8, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 12, 9, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 12, 9, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 12, 9, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 12, 9, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 12, 11, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 12, 11, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 12, 12, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 12, 12, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 12, 13, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 12, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 12, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 12, 13, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 12, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 12, 13, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 12, 10, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 12, 7, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 12, 10, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 13, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 10, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 7, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 10, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 14, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 10, SMOOTH_CRAGROCK, 7, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 14, 7, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 14, 8, SMOOTH_CRAGROCK, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 14, 9, SMOOTH_CRAGROCK, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 14, 11, SMOOTH_CRAGROCK, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 14, 11, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 6, CRAGROCK_BRICKS, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 13, 7, CRAGROCK_BRICKS, 2, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 13, 7, CRAGROCK_BRICKS, 2, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 8, CRAGROCK_BRICKS, 1, 3, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 8, CRAGROCK_BRICKS, 1, 3, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 13, 10, CRAGROCK_BRICKS, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 13, 10, CRAGROCK_BRICKS, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 11, CRAGROCK_BRICKS, 1, 3, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 11, CRAGROCK_BRICKS, 1, 3, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 13, 13, CRAGROCK_BRICKS, 2, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 13, 13, CRAGROCK_BRICKS, 2, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 14, CRAGROCK_BRICKS, 1, 3, 1, direction);

		//BLOCKADE 1
		blockades.clear();
		rotatedCubeVolume(world, x, y, z, 7, 14, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 4, 1, 1, direction, pos -> blockades.add(pos));
		levelBlockades[0] = blockades.toArray(new BlockPos[0]);

		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 5, 10, 7, SMOOTH_BETWEENSTONE_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 10, 7, SMOOTH_BETWEENSTONE_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 10, 13, SMOOTH_BETWEENSTONE_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 10, 13, SMOOTH_BETWEENSTONE_WALL, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 10, 8, SMOOTH_BETWEENSTONE_WALL, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 10, 12, SMOOTH_BETWEENSTONE_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 10, 12, SMOOTH_BETWEENSTONE_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 10, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 10, 9, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 10, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 11, 10, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 11, 11, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 11, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 12, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 12, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 12, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 13, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 13, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 14, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 10, 9, ROOT, 1, 1 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 10, 11, ROOT, 1, 1 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 10, 13, ROOT, 1, 1 + random.nextInt(2), 1, direction);
		rotatedLootPot(world, random, x, y, z, 7, 10, 14, direction, 1, 2, 4, null);
		rotatedLootPot(world, random, x, y, z, 8, 10, 14, direction, 1, 2, 4, null);
		rotatedLootPot(world, random, x, y, z, 9, 10, 14, direction, 1, 2, 4, null);
		rotatedLootPot(world, random, x, y, z, 12, 10, 9, direction, 1, 2, 4, null);
		rotatedLootPot(world, random, x, y, z, 12, 10, 10, direction, 1, 2, 4, null);
		rotatedLootPot(world, random, x, y, z, 12, 10, 11, direction, 1, 2, 4, null);

		//FLOOR 3
		//WALLS
		rotatedCubeVolume(world, x, y, z, 8, 16, 6, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 16, 7, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 16, 7, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 16, 8, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 16, 8, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 16, 10, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 16, 10, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 16, 11, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 16, 11, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 16, 13, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 16, 13, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 16, 14, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 17, 6, SMOOTH_CRAGROCK, 1, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 17, 7, SMOOTH_CRAGROCK, 2, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 17, 7, SMOOTH_CRAGROCK, 2, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 17, 8, SMOOTH_CRAGROCK, 1, 7, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 17, 8, SMOOTH_CRAGROCK, 1, 7, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 17, 10, SMOOTH_CRAGROCK, 1, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 17, 10, SMOOTH_CRAGROCK, 1, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 17, 11, SMOOTH_CRAGROCK, 1, 7, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 17, 11, SMOOTH_CRAGROCK, 1, 7, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 17, 13, SMOOTH_CRAGROCK, 2, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 17, 13, SMOOTH_CRAGROCK, 2, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 17, 14, SMOOTH_CRAGROCK, 1, 7, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 7, SMOOTH_CRAGROCK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 13, CRAGROCK_BRICK_WALL, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 10, CRAGROCK_BRICK_WALL, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 17, 7, CRAGROCK_BRICK_WALL, 1, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 10, CRAGROCK_BRICK_WALL, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 16, 7, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 18, 10, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 20, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 22, 10, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 24, 6, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 24, 7, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 24, 7, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 24, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 24, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 24, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 24, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 24, 12, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 24, 12, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 24, 13, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 24, 13, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 24, 14, CHISELED_CRAGROCK, 3, 1, 1, direction);

		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 8, 15, 10, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 16, 10, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedSpawner(world, x, y, z, 8, 17, 10, direction, "thebetweenlands:chiromaw").setMaxEntities(4).setCheckRange(16.0D).setSpawnRange(3).setDelayRange(180, 500);
		rotatedCubeVolume(world, x, y, z, 6, 15, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 15, 8, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 16, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 16, 8, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 16, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 17, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 17, 9, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 17, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 18, 10, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 18, 11, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 18, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 19, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 19, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 19, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 20, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 20, 12, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 20, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 21, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 21, 11, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 21, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 22, 10, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 22, 9, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 22, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 23, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 22, 8, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 22, 9, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 4, direction);
		rotatedLootPot(world, random, x, y, z, 10, 15, 8, direction, 2, 3, 3, null);
		rotatedLootPot(world, random, x, y, z, 9, 15, 8, direction, 2, 3, 3, null);

		//CEILING
		rotatedCubeVolume(world, x, y, z, 7, 23, 7, SMOOTH_CRAGROCK, 4, 1, 7, direction);

		//BLOCKADE 2
		blockades.clear();
		rotatedCubeVolume(world, x, y, z, 6, 23, 9, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 4, direction, pos -> blockades.add(pos));
		levelBlockades[1] = blockades.toArray(new BlockPos[0]);

		rotatedCubeVolume(world, x, y, z, 7, 23, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 23, 10, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 23, 10, SMOOTH_CRAGROCK, 1, 1, 1, direction);


		//WALLS FLOOR 4/5/6
		rotatedCubeVolume(world, x, y, z, 7, 25, 6, SMOOTH_CRAGROCK, 3, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 25, 7, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 25, 7, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 25, 8, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 25, 8, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 25, 9, SMOOTH_CRAGROCK, 1, 8, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 25, 9, SMOOTH_CRAGROCK, 1, 8, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 25, 12, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 25, 12, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 25, 13, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 25, 13, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 25, 14, SMOOTH_CRAGROCK, 3, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 33, 6, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 33, 7, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 33, 7, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 33, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 33, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 33, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 33, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 33, 12, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 33, 12, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 33, 13, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 33, 13, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 33, 14, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 34, 6, SMOOTH_CRAGROCK, 3, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 34, 7, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 34, 7, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 34, 8, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 34, 8, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 34, 9, SMOOTH_CRAGROCK, 1, 8, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 34, 9, SMOOTH_CRAGROCK, 1, 8, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 34, 12, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 34, 12, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 34, 13, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 34, 13, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 34, 14, SMOOTH_CRAGROCK, 3, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 42, 6, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 42, 7, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 42, 7, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 42, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 42, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 42, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 42, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 42, 12, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 42, 12, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 42, 13, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 42, 13, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 42, 14, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 43, 6, SMOOTH_CRAGROCK, 3, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 43, 7, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 43, 7, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 43, 8, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 43, 8, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 43, 9, SMOOTH_CRAGROCK, 1, 8, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 43, 9, SMOOTH_CRAGROCK, 1, 8, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 43, 12, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 43, 12, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 43, 13, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 43, 13, SMOOTH_CRAGROCK, 1, 8, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 43, 14, SMOOTH_CRAGROCK, 3, 8, 1, direction);

		//FLOOR 4
		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 8, 24, 10, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 25, 10, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedSpawner(world, x, y, z, 8, 26, 10, direction, "thebetweenlands:swamp_hag").setMaxEntities(3).setCheckRange(16.0D).setDelayRange(180, 500).setSpawnInAir(false);
		rotatedCubeVolume(world, x, y, z, 10, 24, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 24, 9, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 24, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 25, 10, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 25, 11, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 25, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 26, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 26, 12, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 26, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 27, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 7, 27, 12, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 27, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 28, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 28, 11, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 28, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 29, 10, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 29, 9, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 29, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 30, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 30, 7, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 30, 7, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 31, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 9, 31, 7, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 10, 31, 8, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 32, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 31, 9, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 31, 9, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 31, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 31, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 31, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 31, 12, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 31, 13, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 31, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 31, 13, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 31, 12, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 31, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 31, 10, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 30, 10, INACTIVE_GLOWING_SMOOTH_CRAGROCK, 1, 1, 1, direction, pos -> inactiveGlowingCragrockBlocks.add(pos));
		rotatedCubeVolume(world, x, y, z, 11, 29, 10, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedLootPot(world, random, x, y, z, 8, 24, 13, direction, 2, 3, 4, null);
		rotatedLootPot(world, random, x, y, z, 7, 24, 13, direction, 2, 3, 4, null);
		rotatedLootPot(world, random, x, y, z, 9, 24, 13, direction, 2, 3, 4, null);
		rotatedLootPot(world, random, x, y, z, 10, 24, 12, direction, 2, 3, 4, null);
		rotatedLootPot(world, random, x, y, z, 10, 24, 11, direction, 2, 3, 4, null);

		//CEILING
		rotatedCubeVolume(world, x, y, z, 5, 32, 9, SMOOTH_CRAGROCK, 7, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 6, 32, 9, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 32, 13, SMOOTH_CRAGROCK, 3, 1, 1, direction);

		//BLOCKADE 3
		blockades.clear();
		rotatedCubeVolume(world, x, y, z, 6, 32, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 4, 1, 1, direction, pos -> blockades.add(pos));
		rotatedCubeVolume(world, x, y, z, 7, 32, 7, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 3, 1, 1, direction, pos -> blockades.add(pos));
		levelBlockades[2] = blockades.toArray(new BlockPos[0]);

		//FLOOR 5
		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 8, 33, 10, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 34, 10, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);

		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound entityNbt = new NBTTagCompound();
		entityNbt.setString("id", "thebetweenlands:pyrad");
		EntityPyrad pyrad = new EntityPyrad(world);
		pyrad.getEntityAttribute(EntityPyrad.AGRESSIVE).setBaseValue(1);
		entityNbt.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(pyrad.getAttributeMap()));
		nbt.setTag("Entity", entityNbt);
		rotatedSpawner(world, x, y, z, 8, 35, 10, direction, "thebetweenlands:pyrad")
		.setMaxEntities(3)
		.setCheckRange(16.0D)
		.setDelayRange(180, 500)
		.setSpawnInAir(false)
		.setNextEntity(new WeightedSpawnerEntity(nbt));

		rotatedCubeVolume(world, x, y, z, 10, 33, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 33, 12, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 33, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 34, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 7, 34, 12, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 34, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 35, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 35, 11, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 35, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 36, 10, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 36, 9, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 36, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 37, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 37, 7, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 37, 7, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 38, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 9, 38, 7, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 10, 38, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 39, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 39, 9, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 39, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 40, 10, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 40, 11, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 40, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 41, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 40, 12, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 40, 13, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 40, 13, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 40, 12, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 40, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 40, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 40, 10, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 5, 40, 9, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 40, 9, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 40, 8, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 40, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 39, 13, INACTIVE_GLOWING_SMOOTH_CRAGROCK, 1, 1, 1, direction, pos -> inactiveGlowingCragrockBlocks.add(pos));
		rotatedCubeVolume(world, x, y, z, 8, 38, 13, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedLootPot(world, random, x, y, z, 7, 33, 12, direction, 2, 5, 3, null);
		rotatedLootPot(world, random, x, y, z, 6, 33, 12, direction, 2, 5, 3, null);
		rotatedLootPot(world, random, x, y, z, 5, 33, 11, direction, 2, 5, 3, null);
		rotatedLootPot(world, random, x, y, z, 5, 33, 10, direction, 2, 5, 3, null);
		rotatedLootPot(world, random, x, y, z, 5, 33, 9, direction, 2, 5, 3, null);

		//CEILING
		rotatedCubeVolume(world, x, y, z, 6, 41, 7, SMOOTH_CRAGROCK, 4, 1, 7, direction);
		rotatedCubeVolume(world, x, y, z, 9, 41, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 41, 9, SMOOTH_CRAGROCK, 1, 1, 3, direction);

		//BLOCKADE 4
		blockades.clear();
		rotatedCubeVolume(world, x, y, z, 10, 41, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 4, direction, pos -> blockades.add(pos));
		rotatedCubeVolume(world, x, y, z, 11, 41, 9, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 3, direction, pos -> blockades.add(pos));
		levelBlockades[3] = blockades.toArray(new BlockPos[0]);

		//FLOOR 6
		//INTERIOR
		rotatedCubeVolume(world, x, y, z, 8, 42, 10, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 43, 10, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedSpawner(world, x, y, z, 8, 44, 10, direction, "thebetweenlands:wight").setMaxEntities(2).setCheckRange(24.0D).setDelayRange(300, 600).setSpawnInAir(false);
		rotatedCubeVolume(world, x, y, z, 6, 42, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 42, 11, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 42, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 43, 10, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 43, 9, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 43, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 44, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 44, 7, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 44, 7, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 45, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 9, 45, 7, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 10, 45, 8, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 46, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 46, 9, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 46, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 47, 10, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 47, 11, SMOOTH_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 47, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 48, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 48, 12, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 48, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 8, 49, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 7, 49, 12, SMOOTH_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 49, 12, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 50, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 49, 11, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 49, 11, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 49, 9, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 49, 9, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 49, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 49, 8, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 49, 7, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 49, 7, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 49, 7, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 49, 8, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 49, 8, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 49, 10, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 48, 10, INACTIVE_GLOWING_SMOOTH_CRAGROCK, 1, 1, 1, direction, pos -> inactiveGlowingCragrockBlocks.add(pos));
		rotatedCubeVolume(world, x, y, z, 5, 47, 10, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedLootPot(world, random, x, y, z, 8, 42, 7, direction, 3, 7, 4, null);
		rotatedLootPot(world, random, x, y, z, 7, 42, 7, direction, 3, 7, 4, null);
		rotatedLootPot(world, random, x, y, z, 9, 42, 7, direction, 3, 7, 4, null);
		rotatedLootPot(world, random, x, y, z, 6, 42, 8, direction, 3, 7, 4, null);
		rotatedLootPot(world, random, x, y, z, 6, 42, 9, direction, 3, 7, 4, null);

		//CEILING
		rotatedCubeVolume(world, x, y, z, 5, 50, 8, SMOOTH_CRAGROCK, 7, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 10, 50, 11, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 50, 7, SMOOTH_CRAGROCK, 3, 1, 1, direction);

		//BLOCKADE 5
		blockades.clear();
		rotatedCubeVolume(world, x, y, z, 7, 50, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 4, 1, 1, direction, pos -> blockades.add(pos));
		rotatedCubeVolume(world, x, y, z, 7, 50, 13, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 3, 1, 1, direction, pos -> blockades.add(pos));
		levelBlockades[4] = blockades.toArray(new BlockPos[0]);

		//TOP FLOOR
		rotatedCubeVolume(world, x, y, z, 7, 51, 6, SMOOTH_CRAGROCK_SLAB, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 51, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 51, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 51, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 51, 9, SMOOTH_CRAGROCK_SLAB, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 12, 51, 9, SMOOTH_CRAGROCK_SLAB, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 5, 51, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 51, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 51, 13, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 13, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 51, 14, SMOOTH_CRAGROCK_SLAB, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 51, 5, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 51, 6, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 6, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 51, 7, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 51, 7, CHISELED_CRAGROCK, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 51, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 51, 8, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 51, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 13, 51, 9, CHISELED_CRAGROCK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 4, 51, 12, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 12, 51, 12, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 5, 51, 13, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 11, 51, 13, CHISELED_CRAGROCK, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 6, 51, 14, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 14, CHISELED_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 51, 15, CHISELED_CRAGROCK, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 51, 4, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 51, 5, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 5, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 51, 6, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 51, 6, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 51, 7, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 13, 51, 7, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 2, 51, 9, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 14, 51, 9, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 3, 51, 12, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 13, 51, 12, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 4, 51, 14, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 51, 14, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 51, 15, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 15, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 51, 16, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 52, 4, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 52, 4, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 52, 4, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 52, 5, MOSSY_CRAGROCK_TOP, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 52, 5, MOSSY_CRAGROCK_TOP, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 52, 6, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 52, 6, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 52, 7, MOSSY_CRAGROCK_TOP, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 13, 52, 7, MOSSY_CRAGROCK_TOP, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 2, 52, 9, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 52, 10, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 52, 11, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 52, 9, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 52, 10, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 52, 11, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 52, 12, MOSSY_CRAGROCK_TOP, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 3, 52, 14, CRAGROCK_PILLAR, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 52, 12, MOSSY_CRAGROCK_TOP, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 13, 52, 14, CRAGROCK_PILLAR, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 52, 14, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 52, 14, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 52, 15, MOSSY_CRAGROCK_TOP, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 52, 15, MOSSY_CRAGROCK_TOP, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 52, 16, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 52, 16, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 52, 16, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 53, 4, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 53, 4, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 53, 4, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 53, 6, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 53, 6, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 53, 9, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 53, 10, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 53, 11, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 53, 9, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 53, 10, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 53, 11, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 53, 14, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 53, 14, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 53, 16, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 53, 16, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 53, 16, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 54, 4, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 54, 10, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 54, 10, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 54, 16, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 53, 14, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 53, 14, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 54, 14, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 54, 14, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 55, 14, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 13, 55, 14, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 3, 52, 11, CRAGROCK_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 55, 11, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 56, 11, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 57, 11, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 3, 53, 8, CRAGROCK_PILLAR, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 57, 8, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 58, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 59, 8, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 5, 53, 5, CRAGROCK_PILLAR, 1, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 59, 5, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 60, 5, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 61, 5, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 8, 55, 4, CRAGROCK_PILLAR, 1, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 61, 4, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 62, 4, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 63, 4, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 11, 53, 5, CRAGROCK_PILLAR, 1, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 59, 5, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 60, 5, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 61, 5, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 13, 53, 8, CRAGROCK_PILLAR, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 57, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 58, 8, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 59, 8, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 13, 52, 11, CRAGROCK_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 55, 11, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 56, 11, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 57, 11, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 8, 52, 5, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 53, 5, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedSpawner(world, x, y, z, 8, 54, 5, direction, "thebetweenlands:swamp_hag").setMaxEntities(3).setCheckRange(10.0D).setDelayRange(150, 500).setSpawnInAir(false);
		rotatedCubeVolume(world, x, y, z, 8, 52, 15, CRAGROCK_BRICK_WALL, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 53, 15, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedSpawner(world, x, y, z, 8, 54, 15, direction, "thebetweenlands:wight").setMaxEntities(2).setCheckRange(24.0D).setDelayRange(300, 600).setSpawnInAir(false);
		rotatedLootChest(world, random, x, y, z, 7, 52, 5, direction, 5, 8, 2, 0, null);
		rotatedLootChest(world, random, x, y, z, 9, 52, 5, direction, 5, 8, 2, 0, null);
		rotatedLootChest(world, random, x, y, z, 7, 52, 15, direction, 5, 8, 2, 2, null);
		rotatedLootPot(world, random, x, y, z, 10, 52, 6, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 11, 52, 6, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 6, 52, 6, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 5, 52, 6, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 12, 52, 8, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 12, 52, 12, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 12, 52, 13, direction, 3, 4, 3, null);
		rotatedLootPot(world, random, x, y, z, 3, 52, 10, direction, 3, 4, 3, null);
		rotatedCubeVolume(world, x, y, z, 7, 51, 7, ROOT, 1, 2 + random.nextInt(5), 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 51, 11, ROOT, 1, 1 + random.nextInt(2), 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 52, 9, ROOT, 1, 2 + random.nextInt(3), 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 52, 13, ROOT, 1, 1 + random.nextInt(3), 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 52, 15, ROOT, 1, 2 + random.nextInt(3), 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 52, 7, ROOT, 1, 1 + random.nextInt(3), 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 52, 10, ROOT, 1, 1 + random.nextInt(2), 1, direction);


		//OUTSIDE DECORATION
		rotatedCubeVolumeExtendedDown(world, x, y, z, 0, 0, 6, CRAGROCK_PILLAR, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 6, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 6, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 3, 6, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolumeExtendedDown(world, x, y, z, 0, 0, 9, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 9, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 3, 9, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 4, 9, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, 9, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolumeExtendedDown(world, x, y, z, 0, 0, 12, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 2, 12, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 3, 12, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 6, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 7, 12, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		if (isReplaceable(world, x, y, z, 2, -1, 15, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 2, -1, 15, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 15, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 1, 15, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 3, 15, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 4, 15, CRAGROCK_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 7, 15, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 8, 15, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 9, 15, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		if (isReplaceable(world, x, y, z, 5, -1, 17, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 5, -1, 17, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 17, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 1, 17, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 2, 17, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 17, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 5, 17, CRAGROCK_PILLAR, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 17, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 10, 17, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 11, 17, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolumeExtendedDown(world, x, y, z, 8, 0, 18, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 18, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 2, 18, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 3, 18, CRAGROCK_PILLAR, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 4, 18, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 5, 18, CRAGROCK_PILLAR, 1, 6, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 11, 18, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 12, 18, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 18, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolumeExtendedDown(world, x, y, z, 16, 0, 6, CRAGROCK_PILLAR, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 1, 6, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 2, 6, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 3, 6, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolumeExtendedDown(world, x, y, z, 16, 0, 9, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 1, 9, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 3, 9, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 4, 9, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 5, 9, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolumeExtendedDown(world, x, y, z, 16, 0, 12, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 2, 12, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 3, 12, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 5, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 6, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 16, 7, 12, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		rotatedCubeVolume(world, x, y, z, 14, 0, 15, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 14, -1, 15, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 14, -1, 15, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 1, 15, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 3, 15, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 4, 15, CRAGROCK_PILLAR, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 7, 15, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 8, 15, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 9, 15, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
		if (isReplaceable(world, x, y, z, 11, -1, 17, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 11, -1, 17, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 0, 17, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 1, 17, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 2, 17, CRAGROCK_PILLAR, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 17, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 5, 17, CRAGROCK_PILLAR, 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 17, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 10, 17, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 11, 17, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));

		//STAIRS 1
		int stair1Length = 16;
		for(int steps = 0; steps < 17; steps++) {
			BlockPos stairPosRight = this.rotatePos(world, x, y, z, 1, -1 - steps, 8 - steps*2, direction);
			BlockPos stairPosLeft = this.rotatePos(world, x, y, z, 2, -1 - steps, 8 - steps*2, direction);
			IBlockState stairBlockRight = world.getBlockState(stairPosRight);
			IBlockState stairBlockLeft = world.getBlockState(stairPosLeft);
			if(!stairBlockRight.getBlock().isReplaceable(world, stairPosRight) || stairBlockRight.getMaterial().isLiquid() ||
					!stairBlockLeft.getBlock().isReplaceable(world, stairPosLeft) || stairBlockLeft.getMaterial().isLiquid() || steps == 16) {
				stair1Length = steps;

				if(world.getBlockState(stairPosRight).getBlock().isReplaceable(world, stairPosRight)) {
					this.setBlockAndNotifyAdequately(world, stairPosRight, CRAGROCK_BRICKS);
				}

				if(world.getBlockState(stairPosLeft).getBlock().isReplaceable(world, stairPosLeft)) {
					this.setBlockAndNotifyAdequately(world, stairPosLeft, SMOOTH_CRAGROCK);
				}

				break;
			}

			if(steps % 2 == 0) {
				rotatedCubeVolumeExtendedDown(world, x, y, z, 0,  - steps, 6 - steps*2, CRAGROCK_PILLAR, 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 0, 1 - steps, 6 - steps*2, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 0, 2 - steps, 6 - steps*2, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 0, 3 - steps, 6 - steps*2, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));

				rotatedCubeVolumeExtendedDown(world, x, y, z, 3,  - steps, 6 - steps*2, CRAGROCK_PILLAR, 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 3, 1 - steps, 6 - steps*2, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 3, 2 - steps, 6 - steps*2, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 3, 3 - steps, 6 - steps*2, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
			}

			rotatedCubeVolume(world, x, y, z, 1, -1 - steps, 8 - steps*2, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, -1 - steps, 8 - steps*2, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, -1 - steps, 7 - steps*2, CRAGROCK_BRICKS, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, -1 - steps, 7 - steps*2, SMOOTH_CRAGROCK, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1, -1 - steps, 6 - steps*2, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2, -1 - steps, 6 - steps*2, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		}

		//STAIRS 2
		int stair2Length = 16;
		for(int steps = 0; steps < 17; steps++) {
			BlockPos stairPosRight = this.rotatePos(world, x, y, z, 1 + 13, -1 - steps, 8 - steps*2, direction);
			BlockPos stairPosLeft = this.rotatePos(world, x, y, z, 2 + 13, -1 - steps, 8 - steps*2, direction);
			IBlockState stairBlockRight = world.getBlockState(stairPosRight);
			IBlockState stairBlockLeft = world.getBlockState(stairPosLeft);
			if(!stairBlockRight.getBlock().isReplaceable(world, stairPosRight) || stairBlockRight.getMaterial().isLiquid() ||
					!stairBlockLeft.getBlock().isReplaceable(world, stairPosLeft) || stairBlockLeft.getMaterial().isLiquid() || steps == 16) {
				stair2Length = steps;

				if(world.getBlockState(stairPosRight).getBlock().isReplaceable(world, stairPosRight)) {
					this.setBlockAndNotifyAdequately(world, stairPosRight, SMOOTH_CRAGROCK);
				}

				if(world.getBlockState(stairPosLeft).getBlock().isReplaceable(world, stairPosLeft)) {
					this.setBlockAndNotifyAdequately(world, stairPosLeft, CRAGROCK_BRICKS);
				}

				break;
			}

			if(steps % 2 == 0) {
				rotatedCubeVolumeExtendedDown(world, x, y, z, 0 + 13,  - steps, 6 - steps*2, CRAGROCK_PILLAR, 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 0 + 13, 1 - steps, 6 - steps*2, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 0 + 13, 2 - steps, 6 - steps*2, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 0 + 13, 3 - steps, 6 - steps*2, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));

				rotatedCubeVolumeExtendedDown(world, x, y, z, 3 + 13,  - steps, 6 - steps*2, CRAGROCK_PILLAR, 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 3 + 13, 1 - steps, 6 - steps*2, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 3 + 13, 2 - steps, 6 - steps*2, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
				rotatedCubeVolume(world, x, y, z, 3 + 13, 3 - steps, 6 - steps*2, AIR, 1, 1, 1, direction, pos -> inactiveWisps.add(pos));
			}

			rotatedCubeVolume(world, x, y, z, 2 + 13, -1 - steps, 8 - steps*2, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1 + 13, -1 - steps, 8 - steps*2, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2 + 13, -1 - steps, 7 - steps*2, CRAGROCK_BRICKS, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1 + 13, -1 - steps, 7 - steps*2, SMOOTH_CRAGROCK, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 2 + 13, -1 - steps, 6 - steps*2, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
			rotatedCubeVolume(world, x, y, z, 1 + 13, -1 - steps, 6 - steps*2, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		}

		int stairsLength = Math.max(stair2Length, stair1Length);


		AxisAlignedBB stairsAABB;

		switch(direction) {
		default:
		case 0:
			stairsAABB = new AxisAlignedBB(x - 8, y - stairsLength - 8, z - stairsLength * 2 - 1, x + 9, y, z);
			break;
		case 1:
			stairsAABB = new AxisAlignedBB(x - stairsLength * 2, y - stairsLength - 8, z - 7, x + 1, y, z + 10);
			break;
		case 2:
			stairsAABB = new AxisAlignedBB(x - 8, y - stairsLength - 8, z + stairsLength * 2 + 2, x + 9, y, z);
			break;
		case 3:
			stairsAABB = new AxisAlignedBB(x + stairsLength * 2 + 1, y - stairsLength - 8, z - 9, x, y, z + 8);
			break;
		}

		stairsAABB = stairsAABB.grow(4, 0, 4);

		rotatedCubeVolume(world, x, y, z, 1, 0, 8, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 9, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 10, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 10, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 11, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 12, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 2, 12, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 2, 13, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 2, 14, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 3, 14, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 15, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 3, 15, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 3, 16, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 3, 16, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 16, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 4, 17, CRAGROCK_BRICKS, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 0, 8, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 0, 9, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 0, 10, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 1, 10, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 1, 11, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 1, 12, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 15, 2, 12, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 2, 13, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 2, 14, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 3, 14, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 2, 15, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 3, 15, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 3, 16, CRAGROCK_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 3, 16, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 16, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 2, 0, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 9, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 1, 10, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 1, 11, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 1, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 2, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 1, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 14, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 3, 14, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 3, 15, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 3, 15, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 4, 15, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 4, 15, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 4, 16, SMOOTH_CRAGROCK, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 4, 15, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 0, 8, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 0, 9, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 0, 10, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 1, 10, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 1, 11, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 1, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 2, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 1, 12, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 2, 12, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 2, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 2, 14, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 3, 14, SMOOTH_CRAGROCK_SLAB, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 3, 15, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 3, 15, SMOOTH_CRAGROCK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 4, 15, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 7, 13, 5, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 13, 6, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 13, 6, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 13, 6, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 13, 6, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 6, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 7, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 6, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 7, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 13, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 13, 7, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 13, 8, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 12, 13, 8, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 3, 13, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 13, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 13, 10, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 13, 13, 10, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 4, 13, 11, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 13, 11, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 13, 12, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 13, 13, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 13, 12, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 13, 13, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 13, SMOOTH_CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 14, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 14, SMOOTH_CRAGROCK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 13, 14, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 13, 14, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 13, 15, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 15, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 13, 15, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 23, 6, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 23, 6, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 23, 14, getStateFromRotation(0, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 23, 14, getStateFromRotation(2, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 23, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 23, 11, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 23, 9, getStateFromRotation(3, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 23, 11, getStateFromRotation(1, direction, SMOOTH_CRAGROCK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 7, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 14, 7, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 13, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 14, 13, CRAGROCK_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 7, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 7, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 13, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 13, CRAGROCK_BRICK_SLAB_UPSIDEDOWN, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 7, 4, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 8, 4, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 9, 4, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 10, 4, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 4, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 14, 4, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 4, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 14, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 7, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 8, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 10, 5, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 6, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 6, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 7, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 8, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 10, 5, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 14, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 14, 6, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 6, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 7, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 8, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 9, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 10, 7, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 13, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 14, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 15, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 14, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 15, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 7, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 8, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 9, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 10, 10, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 13, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 14, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 14, 15, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 14, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 15, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 7, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 8, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 9, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 10, 13, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 13, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 14, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 15, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 14, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 15, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 7, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 8, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 9, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 10, 7, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 13, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 14, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 15, 7, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 14, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 15, 7, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 7, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 8, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 9, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 10, 10, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 13, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 14, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 15, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 14, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 15, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 7, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 8, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 9, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 10, 13, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 13, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 14, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 15, 13, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 14, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 15, 13, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 7, 16, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 8, 16, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 9, 16, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 10, 16, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 13, 16, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 14, 16, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 16, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 14, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 15, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 7, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 8, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 9, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 10, 15, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 13, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 14, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 14, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 11, 15, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 7, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 8, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 9, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 10, 15, SMOOTH_CRAGROCK_WALL, 1, 3, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 13, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 14, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 15, 14, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 48, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 49, 5, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 50, 5, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 48, 8, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 49, 8, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 50, 8, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 48, 8, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 49, 8, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 50, 8, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 48, 6, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 49, 6, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 50, 6, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 48, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 49, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 50, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 48, 14, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 49, 14, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 50, 14, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 48, 6, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 49, 6, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 50, 6, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 48, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 49, 10, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 50, 10, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 48, 14, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 49, 14, getStateFromRotation(0, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 50, 14, getStateFromRotation(2, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 48, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 49, 15, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 50, 15, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 48, 12, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 49, 12, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 50, 12, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 48, 12, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 49, 12, getStateFromRotation(3, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 50, 12, getStateFromRotation(1, direction, CRAGROCK_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 7, 0, 4, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 1, 4, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 2, 4, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 8, -1, 4, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 8, -1, 4, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 0, 4, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 8, 1, 4, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 9, -1, 4, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 9, -1, 4, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 9, 0, 4, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 7, -1, 3, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 7, -1, 3, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 3, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 10, -1, 5, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 10, -1, 5, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 0, 5, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 10, 1, 5, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 12, -1, 6, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 12, -1, 6, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 0, 6, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 12, 1, 6, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 13, -1, 7, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 13, -1, 7, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 0, 7, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 13, -1, 8, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 13, -1, 8, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 0, 8, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 13, 1, 8, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 6, -1, 5, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 6, -1, 5, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 5, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 1, 5, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 4, -1, 6, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 4, -1, 6, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 6, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);
		if (isReplaceable(world, x, y, z, 3, -1, 8, direction))
			rotatedCubeVolumeExtendedDown(world, x, y, z, 3, -1, 8, CRAGROCK, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 0, 8, MOSSY_CRAGROCK_BOTTOM, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 1, 8, MOSSY_CRAGROCK_TOP, 1, 1, 1, direction);


		//Add world location
		x -= width / 2;
		z -= depth / 2;
		AxisAlignedBB locationBounds;
		BlockPos entrance;
		switch (direction) {
		default:
		case 0:
			locationBounds = new AxisAlignedBB(x - 1, y - 8, z + 1, x + width + 1, y + height, z + depth + 1).grow(6, 6, 6);
			entrance = new BlockPos(x + width / 2, y + 5, z + depth / 2 + 8);
			break;
		case 1:
			locationBounds = new AxisAlignedBB(x + 1, y - 8, z + depth - width - 1, x + depth + 1, y + height, z + depth + 1).grow(6, 6, 6);
			entrance = new BlockPos(x + width / 2 + 9, y + 5, z + depth / 2 + 1);
			break;
		case 2:
			locationBounds = new AxisAlignedBB(x - 1 + width - width, y - 8, z + depth - depth - 1, x + 1 + width, y + height, z + depth - 1).grow(6, 6, 6);
			entrance = new BlockPos(x + width / 2, y + 5, z + depth / 2 - 8);
			break;
		case 3:
			locationBounds = new AxisAlignedBB(x - 1 + width - depth, y - 8, z - 1, x - 1 + width, y + height, z + 1 + width).grow(6, 6, 6);
			entrance = new BlockPos(x + width / 2 - 9, y + 5, z + depth / 2 - 1);
			break;
		}
		x += width / 2;
		z += depth / 2;

		if(stairsAABB.minY < locationBounds.minY) {
			double addY = (locationBounds.minY - stairsAABB.minY) / 2;
			locationBounds = locationBounds.grow(0, addY, 0).offset(0, -addY, 0);
		}

		this.towerLocation.addBounds(locationBounds, locationBounds.grow(-12, -10, -12), stairsAABB);
		this.towerLocation.setLayer(0);
		this.towerLocation.setSeed(random.nextLong());
		this.towerLocation.setStructurePos(entrance);
		for(BlockPos pos : inactiveGlowingCragrockBlocks) {
			this.towerLocation.addGlowingCragrock(pos);
		}
		for(BlockPos pos : inactiveWisps) {
			this.towerLocation.addInactiveWisp(pos);
		}
		for(int i = 0; i < 5; i++) {
			this.towerLocation.setLevelBlockadeBlocks(i, levelBlockades[i]);
		}
		this.towerLocation.setDirty(true);
		this.worldStorage.getLocalStorageHandler().addLocalStorage(this.towerLocation);

		return true;
	}
}
