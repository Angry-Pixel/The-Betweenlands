package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.loot.LootTables;

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
					if(yy == y && SurfaceType.MIXED.matches(world.getBlockState(checkPos.setPos(xx, y - 1, zz))))
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
						BlockMobSpawnerBetweenlands.setRandomMob(world, new BlockPos(x + 2, yy + 2, zz), random);
					}
					world.setBlockState(new BlockPos(x + 3, yy, zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState(), 2);
					world.setBlockState(new BlockPos(x + 4, yy, zz), BlockRegistry.BETWEENSTONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST), 2);
				}
				int randomInt = random.nextInt(4);
				TileEntityChestBetweenlands lootChest;
				switch (randomInt) {
				case 0:
					world.setBlockState(new BlockPos(x + 1, yy + 1, z + 1), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					lootChest = (TileEntityChestBetweenlands) world.getTileEntity(new BlockPos(x + 1, yy + 1, z + 1));
					if (lootChest != null)
						lootChest.setLootTable(LootTables.DUNGEON_CHEST_LOOT, random.nextLong());
					break;
				case 1:
					world.setBlockState(new BlockPos(x + 1, yy + 1, z + 3), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					lootChest = (TileEntityChestBetweenlands) world.getTileEntity(new BlockPos(x + 1, yy + 1, z + 3));
					if (lootChest != null)
						lootChest.setLootTable(LootTables.DUNGEON_CHEST_LOOT, random.nextLong());
					break;
				case 2:
					world.setBlockState(new BlockPos(x + 3, yy + 1, z + 1), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					lootChest = (TileEntityChestBetweenlands) world.getTileEntity(new BlockPos(x + 3, yy + 1, z + 1));
					if (lootChest != null)
						lootChest.setLootTable(LootTables.DUNGEON_CHEST_LOOT, random.nextLong());
					break;
				case 3:
					world.setBlockState(new BlockPos(x + 3, yy + 1, z + 3), BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), 2);
					lootChest = (TileEntityChestBetweenlands) world.getTileEntity(new BlockPos(x + 3, yy + 1, z + 3));
					if (lootChest != null)
						lootChest.setLootTable(LootTables.DUNGEON_CHEST_LOOT, random.nextLong());
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

		//TODO: World locations
		//StorageHelper.addArea(world, "translate:smallDungeon", AxisAlignedBB.getBoundingBox(x, y, z, x + 5, y + 5, z + 5).expand(2, 2, 2), EnumLocationType.DUNGEON, 0);

		return true;
	}
}
