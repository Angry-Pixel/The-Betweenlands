package thebetweenlands.common.world.gen.feature.structure;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.plant.BlockEdgePlant;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockCragrock.EnumCragrockType;
import thebetweenlands.common.entity.EntityGreeblingCorpse;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

public class WorldGenChiromawNest extends WorldGenerator {

	public IBlockState CRAGROCK = BlockRegistry.CRAGROCK.getDefaultState();
	public IBlockState NESTING_BLOCK_BONES = BlockRegistry.NESTING_BLOCK_BONES.getDefaultState();
	public IBlockState NESTING_BLOCK_STICKS = BlockRegistry.NESTING_BLOCK_STICKS.getDefaultState();
	public IBlockState ROOT = BlockRegistry.ROOT.getDefaultState();

	//shrooms
	public IBlockState BLACK_HAT_MUSHROOM = BlockRegistry.BLACK_HAT_MUSHROOM.getDefaultState();
	public IBlockState FLAT_HEAD_MUSHROOM = BlockRegistry.FLAT_HEAD_MUSHROOM.getDefaultState();
	public IBlockState ROTBULB = BlockRegistry.ROTBULB.getDefaultState();

	//floor plants
	public IBlockState SWAMP_TALLGRASS = BlockRegistry.SWAMP_TALLGRASS.getDefaultState();
	public IBlockState SHOOTS = BlockRegistry.SHOOTS.getDefaultState();

	//wall plants
	public IBlockState MOSS = BlockRegistry.DEAD_MOSS.getDefaultState();
	public IBlockState LICHEN = BlockRegistry.DEAD_LICHEN.getDefaultState();

	//edge plants
	public IBlockState EDGE_SHROOM = BlockRegistry.EDGE_SHROOM.getDefaultState();
	public IBlockState EDGE_MOSS = BlockRegistry.EDGE_MOSS.getDefaultState();
	public IBlockState EDGE_LEAF = BlockRegistry.EDGE_LEAF.getDefaultState();
	
	//ground item loot
	public IBlockState GROUND_ITEM = BlockRegistry.GROUND_ITEM.getDefaultState();

	private ILocationGuard guard;
	
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		for(int xo = -4; xo <= 4; xo++) {
			for(int zo = -4; zo <= 4; zo++) {
				BlockPos checkPos = pos.add(xo, -1, zo);
				if(xo*xo + zo*zo <= 16 && (world.isAirBlock(checkPos) || world.getBlockState(checkPos).getMaterial().isLiquid())) {
					return false;
				}
			}
		}
		
		for(int xo = -2; xo <= 2; xo++) {
			for(int yo = 3; yo <= 6; yo++) {
				for(int zo = -2; zo <= 2; zo++) {
					BlockPos checkPos = pos.add(xo, yo, zo);
					IBlockState state = world.getBlockState(checkPos);
					
					if(!state.getBlock().isReplaceable(world, checkPos) && !SurfaceType.MIXED_GROUND.apply(state) && state.getBlock() instanceof BlockPlant == false) {
						return false;
					}
				}
			}
		}
		
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		
		LocationChiromawMatriarchNest location = new LocationChiromawMatriarchNest(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), pos.up(7));
		location.addBounds(new AxisAlignedBB(pos).grow(5, 0, 5).expand(0, 8, 0));
		location.setSeed(rand.nextLong());
		
		this.guard = location.getGuard();
		
		generateRockPile(world, rand, pos);
		generateNest(world, rand, pos.up(6));

		location.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);
		
		return true;
	}

	public void generateNest(World world, Random rand, BlockPos pos) {
		for (int xx = -3; xx <= 3; xx++) {
			for (int zz = -3; zz <= 3; zz++) {
				for (int yy = 0; yy > -3; yy--) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);

					if (Math.round(Math.sqrt(dSqDome)) < 4) {
						setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextInt(4) == 0 ? NESTING_BLOCK_BONES : NESTING_BLOCK_STICKS);

						if (yy == 0 && Math.round(Math.sqrt(dSqDome)) == 1) {
							setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), Blocks.AIR.getDefaultState());
							addEntitiesAndLootBlocks(world, rand, pos.add(xx, yy, zz));
						}
						if (yy == 0 && Math.round(Math.sqrt(dSqDome)) == 0) {
							addMatiarch(world, rand, pos.add(xx, yy, zz));
						}
					}
					setBlockAndNotifyAdequately(world, pos.add(0, yy, 0),  CRAGROCK.withProperty(BlockCragrock.VARIANT, getCragrockForYLevel(rand, yy + 3)));
				}
			}
		}
	}

	private void addMatiarch(World world, Random rand, BlockPos pos) {
		EntityChiromawMatriarch matriarch = new EntityChiromawMatriarch(world);
		matriarch.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		matriarch.onInitialSpawn(world.getDifficultyForLocation(pos), null);
		world.spawnEntity(matriarch);
	}

	private void addEntitiesAndLootBlocks(World world, Random rand, BlockPos pos) {
		if (rand.nextBoolean()) {
			EntityChiromawHatchling egg = new EntityChiromawHatchling(world);
			egg.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			world.spawnEntity(egg);
		}
		else if(rand.nextBoolean() && rand.nextBoolean()) {
			EntityGreeblingCorpse corpse = new EntityGreeblingCorpse(world);
			corpse.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			world.spawnEntity(corpse);
		}
		else {
			setBlockAndNotifyAdequately(world, pos, GROUND_ITEM);
			setScatteredLoot(world, rand, pos);
		}
	}

	public void setScatteredLoot(World world, Random rand, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityGroundItem) {
			ItemStack stack = getScatteredLoot(world, rand);
			((TileEntityGroundItem) tile).setStack(stack);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}
	
	public ItemStack getScatteredLoot(World world, Random rand) {
		LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(getScatteredLootTable());
		if (lootTable != null) {
			LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) world));
			List<ItemStack> loot = lootTable.generateLootForPools(rand, lootBuilder.build());
			if (!loot.isEmpty()) {
				Collections.shuffle(loot); // mix it up a bit
				return loot.get(0);
			}
		}
		return EnumItemMisc.SLIMY_BONE.create(1); // to stop null;
	}

	protected ResourceLocation getScatteredLootTable() {
		return LootTableRegistry.CHIROMAW_NEST_SCATTERED_LOOT;
	}

	public void generateRockPile(World world, Random rand, BlockPos pos) {
		for (int xx = -6; xx <= 6; xx++) {
			for (int zz = -6; zz <= 6; zz++) {
				for (int yy = 0; yy < 4; yy++) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);

					if (yy == 0 && rand.nextBoolean() && Math.round(Math.sqrt(dSqDome)) == 5)
						setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), CRAGROCK.withProperty(BlockCragrock.VARIANT, getCragrockForYLevel(rand, 1)));

					if (yy == 0 && rand.nextBoolean() && Math.round(Math.sqrt(dSqDome)) == 6)
						if (isPlantableAbove(world, pos.add(xx, yy, zz)))
							setRandomRoot(world, pos.add(xx, yy, zz), rand);

					if (Math.round(Math.sqrt(dSqDome)) < 5)
						setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), CRAGROCK.withProperty(BlockCragrock.VARIANT, getCragrockForYLevel(rand, yy)));
				}
			}
		}

		for (int yy = -1; yy < 4; yy++) {
			addGroundPlants(world, pos.add(-5, 0, -5), rand, 11, yy, 11, false, true, true);
			addEdgePlant(world, pos.add(-5, 0, -5), rand, 11, yy, 11);
		}

		addWallPlants(world, pos.add(2, 0, -2), rand, 3, 4, 5, EnumFacing.EAST);
		addWallPlants(world, pos.add(-4, 0, -2), rand, 3, 4, 5, EnumFacing.WEST);
		addWallPlants(world, pos.add(-2, 0, -4), rand, 5, 4, 3, EnumFacing.NORTH);
		addWallPlants(world, pos.add(-2, 0, 2), rand, 5, 4, 3, EnumFacing.SOUTH);

	}
	
	public void setRandomRoot(World world, BlockPos pos, Random rand) {
		int rnd = rand.nextInt(32);
		if (rnd < 8) {
			setBlockAndNotifyAdequately(world, pos, ROOT);
		} else if (rnd < 16) {
			setBlockAndNotifyAdequately(world, pos, ROOT);
			if (world.isAirBlock(pos.up(1)))
				setBlockAndNotifyAdequately(world, pos.up(1), ROOT);
		} else if (rnd < 24) {
			setBlockAndNotifyAdequately(world, pos, ROOT);
			if (world.isAirBlock(pos.up(1)) && world.isAirBlock(pos.up(2))) {
				setBlockAndNotifyAdequately(world, pos.up(1), ROOT);
				setBlockAndNotifyAdequately(world, pos.up(2), ROOT);
			}
		} else {
			setBlockAndNotifyAdequately(world, pos, ROOT);
			if (world.isAirBlock(pos.up(1)) && world.isAirBlock(pos.up(2)) && world.isAirBlock(pos.up(3))) {
				setBlockAndNotifyAdequately(world, pos.up(1), ROOT);
				setBlockAndNotifyAdequately(world, pos.up(2), ROOT);
				setBlockAndNotifyAdequately(world, pos.up(3), ROOT);
			}
		}
	}

	public void addGroundPlants(World world, BlockPos pos, Random rand, int x, int y, int z, boolean addMoss, boolean addWeeds, boolean addMushrooms) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				if (isPlantableAbove(world, pos.add(horizontalX, y, horizontalZ)))
					if (addWeeds && plantingChance(rand))
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), getRandomFloorPlant(rand));
					else if (addMushrooms && plantingChance(rand))
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), getRandomMushroom(rand));
					else if (addMoss && rand.nextBoolean())
						this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), MOSS.withProperty(BlockMoss.FACING, EnumFacing.UP));
			}
	}

	public void addEdgePlant(World world, BlockPos pos, Random rand, int x, int y, int z) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					if (world.getBlockState(pos.add(horizontalX, y + 1, horizontalZ).offset(facing)).isSideSolid(world, pos.add(horizontalX, y + 1, horizontalZ).offset(facing), facing.getOpposite())) {
						if (plantingChance(rand) && isPlantableAbove(world, pos.add(horizontalX, y, horizontalZ)))
							this.setBlockAndNotifyAdequately(world, pos.add(horizontalX, y + 1, horizontalZ), getRandomEdgePlant(rand, facing.getOpposite()));
					}
				}
			}
	}

	public void addWallPlants(World world, BlockPos pos, Random rand, int x, int y, int z, EnumFacing facing) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++)
				for (int vertical = 0; vertical < y; vertical++)
					if (plantingChance(rand) && isPlantableWall(world, pos.add(horizontalX, vertical, horizontalZ), facing))
						setBlockAndNotifyAdequately(world, pos.add(horizontalX, vertical, horizontalZ).offset(facing), MOSS.withProperty(BlockMoss.FACING, facing));
	}

	public boolean isPlantableAbove(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return SoilHelper.canSustainPlant(state) && world.isAirBlock(pos.up());
	}

	public boolean isPlantableWall(World world, BlockPos pos, EnumFacing facing) {
		IBlockState state = world.getBlockState(pos);
		return state.isFullBlock() && world.isAirBlock(pos.offset(facing));
	}

	public boolean plantingChance(Random rand) {
		return rand.nextBoolean() && rand.nextBoolean();
	}

	public EnumCragrockType getCragrockForYLevel(Random rand, int y) {
		return y < 1 ? EnumCragrockType.DEFAULT : y >= 1 && y < 3 ? (rand.nextBoolean() ? EnumCragrockType.DEFAULT : EnumCragrockType.MOSSY_2) : EnumCragrockType.MOSSY_1;
	}

	public IBlockState getRandomFloorPlant(Random rand) {
		return rand.nextBoolean() ? SWAMP_TALLGRASS : SHOOTS; //what plants do we want
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

	public IBlockState getRandomEdgePlant(Random rand, EnumFacing facing) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return EDGE_SHROOM.withProperty(BlockEdgePlant.FACING, facing);
		case 1:
			return EDGE_MOSS.withProperty(BlockEdgePlant.FACING, facing);
		case 2:
			return EDGE_LEAF.withProperty(BlockEdgePlant.FACING, facing);
		}
		return EDGE_SHROOM.withProperty(BlockEdgePlant.FACING, facing);
	}

	@Override
	protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
		super.setBlockAndNotifyAdequately(worldIn, pos, state);
		
		if(this.guard != null && state.getBlock() instanceof BlockPlant == false) {
			this.guard.setGuarded(worldIn, pos, true);
		}
	}
}