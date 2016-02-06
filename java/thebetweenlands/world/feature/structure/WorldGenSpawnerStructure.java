package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLSpawner;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.world.loot.LootItemStack;
import thebetweenlands.world.loot.LootUtil;
import thebetweenlands.world.loot.WeightedLootList;

import java.util.Random;

/**
 * Created by Bart on 05/02/2016.
 */
public class WorldGenSpawnerStructure extends WorldGenerator {

    public static final WeightedLootList loot = new WeightedLootList(new LootItemStack[]{
            new LootItemStack(BLItemRegistry.anglerToothArrow).setAmount(32, 48).setWeight(20),
            new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 32).setWeight(20),
            new LootItemStack(BLItemRegistry.poisonedAnglerToothArrow).setAmount(8, 16).setWeight(10),
            new LootItemStack(BLItemRegistry.weepingBluePetal).setAmount(2, 8).setWeight(10),
            new LootItemStack(BLItemRegistry.octineArrow).setAmount(8, 16).setWeight(8),
            new LootItemStack(BLItemRegistry.wightsHeart).setAmount(1, 3).setWeight(8),
            new LootItemStack(BLItemRegistry.basiliskArrow).setAmount(8).setWeight(4),
            new LootItemStack(BLItemRegistry.scroll).setAmount(1).setWeight(4)
    });

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return generateStructure1(world, random, x, y, z);
    }

    public boolean generateStructure1(World world, Random random, int x, int y, int z) {
        for (int xx = x; xx < x + 5; xx++)
            for (int yy = y; yy < y + 5; yy++)
                for (int zz = z; zz < z + 5; zz++)
                    if (world.getBlock(xx, yy, zz) != Blocks.air)
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

                    if (random.nextBoolean()) {
                        world.setBlock(xx, yy + 1, z, BLBlockRegistry.weedwoodChest);
                        TileEntityWeedWoodChest lootChest = (TileEntityWeedWoodChest) world.getTileEntity(xx, yy + 1, z);
                        if (lootChest != null)
                            LootUtil.generateLoot(lootChest, random, loot, 1, 2);
                    } else {
                        world.setBlock(xx, yy + 1, z + 4, BLBlockRegistry.weedwoodChest);
                        TileEntityWeedWoodChest lootChest = (TileEntityWeedWoodChest) world.getTileEntity(xx, yy + 1, z);
                        if (lootChest != null)
                            LootUtil.generateLoot(lootChest, random, loot, 1, 2);
                    }
                }
                for (int zz = z + 1; zz < z + 4; zz++) {
                    world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickStairs);
                    world.setBlock(x + 1, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    if (zz != z + 2)
                        world.setBlock(x + 2, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    else {
                        world.setBlock(x + 2, yy, zz, BLBlockRegistry.angryBetweenstone);
                        world.setBlock(x + 2, yy + 1, zz, BLBlockRegistry.blSpawner);
                        BlockBLSpawner.setRandomMob(world, x, y, z, random);
                    }
                    world.setBlock(x + 3, yy, zz, BLBlockRegistry.betweenstoneTiles);
                    world.setBlock(x + 4, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
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
