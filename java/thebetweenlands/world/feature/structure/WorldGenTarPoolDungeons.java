package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityTarLootPot1;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.loot.LootTables;
import thebetweenlands.world.loot.LootUtil;
import thebetweenlands.world.loot.WeightedLootList;
import thebetweenlands.world.storage.chunk.storage.StorageHelper;
import thebetweenlands.world.storage.chunk.storage.location.EnumLocationType;

public class WorldGenTarPoolDungeons extends WorldGenerator {

	public static final WeightedLootList loot = LootTables.DUNGEON_POT_LOOT;

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		byte height = 10;
		int halfSize = 7;

		if (y + height + 1 >= WorldProviderBetweenlands.LAYER_HEIGHT - height || y - 1 <= WorldProviderBetweenlands.CAVE_WATER_HEIGHT + height)
			return false;

		for (int xx = x - halfSize - 1; xx <= x + halfSize + 1; ++xx)
			for (int yy = y - 1; yy <= y + height + 1; ++yy)
				for (int zz = z - halfSize - 1; zz <= z + halfSize + 1; ++zz) {
					Material mat = world.getBlock(xx, yy, zz).getMaterial();
					if ((yy == y - 1 || yy == y + height + 1) && !mat.isSolid())
						return false;
				}

		for (int x1 = x - halfSize; x1 <= x + halfSize; x1++)
			for (int z1 = z - halfSize; z1 <= z + halfSize; z1++)
				for (int y1 = y; y1 < y + height; y1++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= halfSize && y1 >= y + 3)
						world.setBlockToAir(x1, y1, z1);
				}

		for (int yy = y + 2; yy >= y; --yy) {
			for (int xx = halfSize * -1; xx <= halfSize; ++xx) {
				for (int zz = halfSize * -1; zz <= halfSize; ++zz) {
					double dSq = xx * xx + zz * zz;
					if (Math.round(Math.sqrt(dSq)) == halfSize - 2 && yy >= y + 1) {
						world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.betweenstoneTiles);
						if (rand.nextBoolean() && yy == y + 2)
							if ((xx % 4 == 0 || zz % 4 == 0 || xx % 3 == 0 || zz % 3 == 0 ) && world.getBlock(x + xx,  y + 6, z + zz) == BLBlockRegistry.betweenstone)
								placePillar(world, x + xx, y + 3, z + zz, rand);
					}
					if (Math.round(Math.sqrt(dSq)) <= halfSize && Math.round(Math.sqrt(dSq)) >= halfSize -1 && yy <= y + 2)
						world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.betweenstone);
					if (Math.round(Math.sqrt(dSq)) < halfSize - 2 && yy >= y + 1)
						world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.tarFluid);
					if (Math.round(Math.sqrt(dSq)) < halfSize - 2 && yy == y) {
						if (rand.nextBoolean() && rand.nextBoolean())
							world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.solidTar);
						else
							world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.betweenstone);
						if (rand.nextInt(18) == 0) {
							int randDirection = rand.nextInt(4) + 2;
							world.setBlock(x + xx, yy, z + zz, getRandomBlock(rand), randDirection, 3);
							TileEntityTarLootPot1 lootPot = (TileEntityTarLootPot1) world.getTileEntity(x + xx, yy, z + zz);
							if (lootPot != null)
								LootUtil.generateLoot(lootPot, rand, loot, 1, 3);
						}
					}
				}
			}
		}
		world.setBlock(x + rand.nextInt(halfSize - 2) - rand.nextInt(halfSize - 2), y, z + rand.nextInt(halfSize - 2) - rand.nextInt(halfSize - 2), BLBlockRegistry.tarBeastSpawner, 0, 3);

		StorageHelper.addArea(world, "translate:tarPoolDungeon", AxisAlignedBB.getBoundingBox(x - halfSize, y - 1, z - halfSize, x + halfSize, y + height, z + halfSize).expand(1, 1, 1), EnumLocationType.DUNGEON, 0);

		return true;
	}

	private void placePillar(World world, int x, int y, int z, Random rand) {
		int randHeight = rand.nextInt(3);
		for (int yy = y; randHeight + y >= yy; ++yy)
			world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickWall, 0, 3);
	}

	private Block getRandomBlock(Random rand) {
		switch(rand.nextInt(3)) {
		case 0: return BLBlockRegistry.tarLootPot1;
		case 1: return BLBlockRegistry.tarLootPot2;
		case 2: return BLBlockRegistry.tarLootPot3;
		default: return BLBlockRegistry.tarLootPot1;
		}
	}

}