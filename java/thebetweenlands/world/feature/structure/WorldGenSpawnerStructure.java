package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLSpawner;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootBasicList;
import thebetweenlands.world.loot.LootUtil;

/**
 * Created by Bart on 05/02/2016.
 */
public class WorldGenSpawnerStructure extends WorldGenerator {

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return generateStructure1(world, random, x, y, z);
    }

    public boolean generateStructure1(World world, Random random, int x, int y, int z) {
        for (int xx = x; xx < x + 5; xx++)
            for (int yy = y; yy < y + 5; yy++)
                for (int zz = z; zz < z + 5; zz++)
                    if (world.getBlock(xx, yy, zz) != Blocks.air || !SurfaceType.MIXED.matchBlock(world.getBlock(xx, y - 1, zz)))
                        return false;
        for (int yy = y; yy < y + 5; yy++) {
            if (yy == y) {
                world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBricks);
                world.setBlock(x + 4, yy, z, BLBlockRegistry.betweenstoneBricks);
                world.setBlock(x + 4, yy, z + 4, BLBlockRegistry.betweenstoneBricks);
                world.setBlock(x, yy, z + 4, BLBlockRegistry.betweenstoneBricks);
                for (int xx = x + 1; xx < x + 4; xx++) {
                    world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 2, 3);
                    world.setBlock(xx, yy, z + 4, BLBlockRegistry.betweenstoneBrickStairs, 3, 3);
                }
                for (int zz = z + 1; zz < z + 4; zz++) {
                    world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickStairs);
                    world.setBlock(x + 1, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    if (zz != z + 2)
                        world.setBlock(x + 2, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    else {
                        world.setBlock(x + 2, yy, zz, BLBlockRegistry.chiseledBetweenstone);
                        world.setBlock(x + 2, yy + 2, zz, BLBlockRegistry.blSpawner);
                        BlockBLSpawner.setRandomMob(world, x + 2, yy + 2, zz, random);
                    }
                    world.setBlock(x + 3, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    world.setBlock(x + 4, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
                }
                int randomInt = random.nextInt(4);
                TileEntityWeedWoodChest lootChest;
                switch (randomInt) {
                    case 0:
                        world.setBlock(x + 1, yy + 1, z + 1, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 1, yy + 1, z + 1);
                        if (lootChest != null)
                        	LootUtil.generateLoot(lootChest, random, LootBasicList.loot, 2, 4);
                        break;
                    case 1:
                        world.setBlock(x + 1, yy + 1, z + 3, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 1, yy + 1, z + 3);
                        if (lootChest != null)
                        	LootUtil.generateLoot(lootChest, random, LootBasicList.loot, 2, 4);
                        break;
                    case 2:
                        world.setBlock(x + 3, yy + 1, z + 1, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 3, yy + 1, z + 1);
                        if (lootChest != null)
                        	LootUtil.generateLoot(lootChest, random, LootBasicList.loot, 2, 4);
                        break;
                    case 3:
                        world.setBlock(x + 3, yy + 1, z + 3, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 3, yy + 1, z + 3);
                        if (lootChest != null)
                        	LootUtil.generateLoot(lootChest, random, LootBasicList.loot, 2, 4);
                        break;
                }
            } else if (yy < y + 4) {
                world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneTiles);
                world.setBlock(x + 4, yy, z, BLBlockRegistry.betweenstoneTiles);
                world.setBlock(x + 4, yy, z + 4, BLBlockRegistry.betweenstoneTiles);
                world.setBlock(x, yy, z + 4, BLBlockRegistry.betweenstoneTiles);
            } else {
                world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickSlab);
                world.setBlock(x + 4, yy, z, BLBlockRegistry.betweenstoneBrickSlab);
                world.setBlock(x + 4, yy, z + 4, BLBlockRegistry.betweenstoneBrickSlab);
                world.setBlock(x, yy, z + 4, BLBlockRegistry.betweenstoneBrickSlab);
                for (int xx = x + 1; xx < x + 4; xx++) {
                    world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneTiles);
                    world.setBlock(xx, yy, z + 4, BLBlockRegistry.betweenstoneTiles);
                }
                for (int zz = z + 1; zz < z + 4; zz++) {
                    world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    world.setBlock(x + 4, yy, zz, BLBlockRegistry.betweenstoneTiles);
                }
            }
        }

        return true;
    }
}
