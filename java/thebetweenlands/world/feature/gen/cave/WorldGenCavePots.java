package thebetweenlands.world.feature.gen.cave;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootTables;
import thebetweenlands.world.loot.LootUtil;

/**
 * Created by Bart on 17/01/2016.
 */
public class WorldGenCavePots extends WorldGenCave {
	public WorldGenCavePots() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (y > 70)
			return false;
		int randDirection = random.nextInt(4) + 2;
		for (int xx = x - 3; xx <= x + 3; xx++) {
			for (int zz = z - 3; zz <= z + 3; zz++) {
				for(int yy = y - 1; yy <= y + 1; yy++) {
					double dst = Math.sqrt((xx-x)*(xx-x)+(yy-y)*(yy-y)+(zz-z)*(zz-z));
					if (random.nextInt(MathHelper.ceiling_double_int(dst / 1.4D) + 1) == 0) {
						if (world.getBlock(xx, yy, zz) == Blocks.air && SurfaceType.UNDERGROUND.matchBlock(world.getBlock(xx, yy - 1, zz))) {
							world.setBlock(xx, yy, zz, getRandomBlock(random), randDirection, 3);
							TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(xx, yy, zz);
							if (lootPot != null)
								LootUtil.generateLoot(lootPot, random, LootTables.COMMON_POT_LOOT, 1, 2);
						}
					}
				}
			}
		}
		return true;
	}

	private Block getRandomBlock(Random rand) {
		switch (rand.nextInt(3)) {
		case 0:
			return BLBlockRegistry.lootPot1;
		case 1:
			return BLBlockRegistry.lootPot2;
		case 2:
			return BLBlockRegistry.lootPot3;
		default:
			return BLBlockRegistry.lootPot1;
		}
	}
}
