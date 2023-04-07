package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenTarPoolDungeon extends WorldGenHelper {
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		byte height = 10;
		int halfSize = 7;

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if (!world.isAreaLoaded(pos, halfSize + 1))
			return false;

		if (y + height + 1 >= WorldProviderBetweenlands.LAYER_HEIGHT - height || y - 1 <= WorldProviderBetweenlands.CAVE_WATER_HEIGHT + height)
			return false;

		for (int xx = x - halfSize - 1; xx <= x + halfSize + 1; ++xx)
			for (int yy = y - 1; yy <= y + height + 1; ++yy)
				for (int zz = z - halfSize - 1; zz <= z + halfSize + 1; ++zz) {
					Material mat = world.getBlockState(this.getCheckPos(xx, yy, zz)).getMaterial();
					if ((yy == y - 1 || yy == y + height + 1) && !mat.isSolid())
						return false;
				}

		for (int x1 = x - halfSize; x1 <= x + halfSize; x1++)
			for (int z1 = z - halfSize; z1 <= z + halfSize; z1++)
				for (int y1 = y; y1 < y + height; y1++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= halfSize && y1 >= y + 3)
						world.setBlockToAir(new BlockPos(x1, y1, z1));
				}

		for (int yy = y + 2; yy >= y; --yy) {
			for (int xx = halfSize * -1; xx <= halfSize; ++xx) {
				for (int zz = halfSize * -1; zz <= halfSize; ++zz) {
					double dSq = xx * xx + zz * zz;
					if (Math.round(Math.sqrt(dSq)) == halfSize - 2 && yy >= y + 1) {
						world.setBlockState(new BlockPos(x + xx, yy, z + zz), BlockRegistry.BETWEENSTONE_TILES.getDefaultState());
						if (rand.nextBoolean() && yy == y + 2)
							if ((xx % 4 == 0 || zz % 4 == 0 || xx % 3 == 0 || zz % 3 == 0 ) && world.getBlockState(this.getCheckPos(x + xx,  y + 6, z + zz)).getBlock() == BlockRegistry.BETWEENSTONE)
								placePillar(world, x + xx, y + 3, z + zz, rand);
					}
					if (Math.round(Math.sqrt(dSq)) <= halfSize && Math.round(Math.sqrt(dSq)) >= halfSize -1 && yy <= y + 2)
						world.setBlockState(new BlockPos(x + xx, yy, z + zz), BlockRegistry.BETWEENSTONE.getDefaultState());
					if (Math.round(Math.sqrt(dSq)) < halfSize - 2 && yy >= y + 1)
						world.setBlockState(new BlockPos(x + xx, yy, z + zz), BlockRegistry.TAR.getDefaultState());
					if (Math.round(Math.sqrt(dSq)) < halfSize - 2 && yy == y) {
						if (rand.nextBoolean() && rand.nextBoolean())
							world.setBlockState(new BlockPos(x + xx, yy, z + zz), BlockRegistry.TAR_SOLID.getDefaultState());
						else
							world.setBlockState(new BlockPos(x + xx, yy, z + zz), BlockRegistry.BETWEENSTONE.getDefaultState());
						if (rand.nextInt(18) == 0) {
							world.setBlockState(new BlockPos(x + xx, yy, z + zz), getRandomTarLootPot(rand).withProperty(BlockLootPot.FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]));
							TileEntity te = world.getTileEntity(this.getCheckPos(x + xx, yy, z + zz));
							if(te instanceof TileEntityLootPot) {
								((TileEntityLootPot)te).setLootTable(LootTableRegistry.TAR_POOL_POT, rand.nextLong());
							}
						}
					}
				}
			}
		}

		world.setBlockState(new BlockPos(x + rand.nextInt(halfSize - 2) - rand.nextInt(halfSize - 2), y, z + rand.nextInt(halfSize - 2) - rand.nextInt(halfSize - 2)), BlockRegistry.TAR_BEAST_SPAWNER.getDefaultState());

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationStorage location = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "tar_pool_dungeon", EnumLocationType.DUNGEON);
		location.setVisible(true);
		location.addBounds(new AxisAlignedBB(x - halfSize, y - 1, z - halfSize, x + halfSize, y + height, z + halfSize).grow(1, 1, 1));
		location.setLayer(0);
		location.setSeed(rand.nextLong());
		location.setVisible(true);
		location.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);

		return true;
	}

	private void placePillar(World world, int x, int y, int z, Random rand) {
		int randHeight = rand.nextInt(3);
		for (int yy = y; randHeight + y >= yy; ++yy)
			world.setBlockState(new BlockPos(x, yy, z), BlockRegistry.BETWEENSTONE_BRICK_WALL.getDefaultState());
	}

	private IBlockState getRandomTarLootPot(Random rand) {
		switch(rand.nextInt(3)) {
		default:
		case 0: return BlockRegistry.TAR_LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_1);
		case 1: return BlockRegistry.TAR_LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_2);
		case 2: return BlockRegistry.TAR_LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_3);
		}
	}
}