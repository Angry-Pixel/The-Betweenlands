package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenSpawnerStructure extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, BlockPos position) {
		return generateStructure1(world, random, position);
	}

	public boolean generateStructure1(World world, Random random, BlockPos pos) {
		MutableBlockPos checkPos = new MutableBlockPos();

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int surfaceBlocks = 0;
		for (int xx = x; xx < x + 5; xx++) {
			for (int zz = z; zz < z + 5; zz++) {
				for (int yy = y; yy < y + 5; yy++) {
					if(yy == y && SurfaceType.MIXED_GROUND.matches(world.getBlockState(checkPos.setPos(xx, y - 1, zz))))
						surfaceBlocks++;
					if (!world.isAirBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz)))
						return false;
				}
				for (int yy = y; yy >= y - 5; yy--) {
					if(!world.isAirBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz)))
						break;
					if(yy <= y - 5)
						return false;
				}
			}
		}
		if(surfaceBlocks < 8)
			return false;

		for (int xx = x; xx < x + 5; xx++) {
			for (int zz = z; zz < z + 5; zz++) {
				for (int yy = y; yy >= y - 5; yy--) {
					if(!world.isAirBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz)))
						break;
					world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState(), 2);
				}
			}
		}

		for (int yy = y; yy < y + 5; yy++) {
			if (yy == y) {
				world.setBlockState(new BlockPos(x, yy, z), BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x + 4, yy, z), BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x + 4, yy, z + 4), BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x, yy, z + 4), BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState(), 2);
				for (int xx = x + 1; xx < x + 4; xx++) {
					world.setBlockState(new BlockPos(xx, yy, z), BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH), 2);
					world.setBlockState(new BlockPos(xx, yy, z + 4), BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH), 2);
				}
				for (int zz = z + 1; zz < z + 4; zz++) {
					world.setBlockState(new BlockPos(x, yy, zz), BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST), 2);
					world.setBlockState(new BlockPos(x + 1, yy, zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
					if (zz != z + 2)
						world.setBlockState(new BlockPos(x + 2, yy, zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
					else {
						world.setBlockState(new BlockPos(x + 2, yy, zz), BlockRegistry.BETWEENSTONE_CHISELED.getDefaultState(), 2);
						world.setBlockState(new BlockPos(x + 2, yy + 2, zz), BlockRegistry.MOB_SPAWNER.getDefaultState(), 2);
						MobSpawnerLogicBetweenlands logic = BlockMobSpawnerBetweenlands.setRandomMob(world, new BlockPos(x + 2, yy + 2, zz), random);
						if(logic != null) {
							logic.setSpawnInAir(false);
						}
					}
					world.setBlockState(new BlockPos(x + 3, yy, zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
					world.setBlockState(new BlockPos(x + 4, yy, zz), BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST), 2);
				}
				int randomInt = random.nextInt(4);
				TileEntity tile;
				switch (randomInt) {
				case 0:
					world.setBlockState(new BlockPos(x + 1, yy + 1, z + 1), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					tile = world.getTileEntity(new BlockPos(x + 1, yy + 1, z + 1));
					if (tile instanceof TileEntityChest) {
						((TileEntityChest) tile).setLootTable(LootTableRegistry.SPAWNER_CHEST, random.nextLong());
					}
					break;
				case 1:
					world.setBlockState(new BlockPos(x + 1, yy + 1, z + 3), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					tile = world.getTileEntity(new BlockPos(x + 1, yy + 1, z + 3));
					if (tile instanceof TileEntityChest) {
						((TileEntityChest) tile).setLootTable(LootTableRegistry.SPAWNER_CHEST, random.nextLong());
					}
					break;
				case 2:
					world.setBlockState(new BlockPos(x + 3, yy + 1, z + 1), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					tile = world.getTileEntity(new BlockPos(x + 3, yy + 1, z + 1));
					if (tile instanceof TileEntityChest) {
						((TileEntityChest) tile).setLootTable(LootTableRegistry.SPAWNER_CHEST, random.nextLong());
					}
					break;
				case 3:
					world.setBlockState(new BlockPos(x + 3, yy + 1, z + 3), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					tile = world.getTileEntity(new BlockPos(x + 3, yy + 1, z + 3));
					if (tile instanceof TileEntityChest) {
						((TileEntityChest) tile).setLootTable(LootTableRegistry.SPAWNER_CHEST, random.nextLong());
					}
					break;
				}
			} else if (yy < y + 4) {
				world.setBlockState(new BlockPos(x, yy, z), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x + 4, yy, z), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x + 4, yy, z + 4), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x, yy, z + 4), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
			} else {
				world.setBlockState(new BlockPos(x, yy, z), BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x + 4, yy, z), BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x + 4, yy, z + 4), BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x, yy, z + 4), BlockRegistry.BETWEENSTONE_BRICK_SLAB.getDefaultState(), 2);
				for (int xx = x + 1; xx < x + 4; xx++) {
					world.setBlockState(new BlockPos(xx, yy, z), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
					world.setBlockState(new BlockPos(xx, yy, z + 4), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
				}
				for (int zz = z + 1; zz < z + 4; zz++) {
					world.setBlockState(new BlockPos(x, yy, zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
					world.setBlockState(new BlockPos(x + 4, yy, zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
				}
			}
		}

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationStorage location = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(x, z), "small_dungeon", EnumLocationType.DUNGEON);
		location.setVisible(true);
		location.addBounds(new AxisAlignedBB(x, y, z, x + 5, y + 5, z + 5).grow(2, 2, 2));
		location.setLayer(0);
		location.setSeed(random.nextLong());
		location.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);
		
		return true;
	}
}
