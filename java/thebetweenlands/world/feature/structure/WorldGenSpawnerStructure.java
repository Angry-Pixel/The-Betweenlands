package thebetweenlands.world.feature.structure;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLSpawner;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootItemStack;
import thebetweenlands.world.loot.LootUtil;
import thebetweenlands.world.loot.WeightedLootList;

import java.util.Random;

/**
 * Created by Bart on 05/02/2016.
 */
public class WorldGenSpawnerStructure extends WorldGenerator {

    public static final WeightedLootList loot = new WeightedLootList(new LootItemStack[]{
            //common
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH.id).setAmount(4, 8).setWeight(20),
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.LURKER_SKIN.id).setAmount(2, 4).setWeight(20),
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SULFUR.id).setAmount(8, 16).setWeight(20),
            new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 32).setWeight(20),
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE.id).setAmount(4, 8).setWeight(20),
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.PARCHMENT.id).setAmount(8, 16).setWeight(20),
            //in between
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(12),
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(12),
            new LootItemStack(BLItemRegistry.marshmallowPink).setAmount(1).setWeight(22),
            new LootItemStack(BLItemRegistry.marshmallow).setAmount(1).setWeight(12),
            new LootItemStack(BLItemRegistry.reedDonut).setAmount(2, 4).setWeight(12),
            new LootItemStack(BLItemRegistry.jamDonut).setAmount(2, 4).setWeight(12),
            //rare
            new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.VALONITE_SHARD.id).setAmount(1, 4).setWeight(6),
            new LootItemStack(BLItemRegistry.angryPebble).setAmount(8, 16).setWeight(6),
            new LootItemStack(BLItemRegistry.scroll).setAmount(1, 3).setWeight(6),
            new LootItemStack(BLItemRegistry.middleFruitSeeds).setAmount(1, 8).setWeight(6),
            new LootItemStack(BLItemRegistry.aspectrusCropSeed).setAmount(1, 8).setWeight(6),
            new LootItemStack(BLItemRegistry.astatos).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.betweenYouAndMe).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.christmasOnTheMarsh).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.theExplorer).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.hagDance).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.lonelyFire).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.mysteriousRecord).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.ancient).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.beneathAGreenSky).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.dJWightsMixtape).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.onwards).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.stuckInTheMud).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.wanderingWisps).setAmount(1).setWeight(6),
            new LootItemStack(BLItemRegistry.waterlogged).setAmount(1).setWeight(6),
            //very rare
            new LootItemStack(BLItemRegistry.forbiddenFig).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.explorerHat).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.voodooDoll).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.ringOfPower).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.swiftPick).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.wightsBane).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.skullMask).setAmount(1).setWeight(1),
            new LootItemStack(BLItemRegistry.tribalPants).setAmount(1).setWeight(1)

    });

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
                            LootUtil.generateLoot(lootChest, random, loot, 2, 4);
                        break;
                    case 1:
                        world.setBlock(x + 1, yy + 1, z + 3, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 1, yy + 1, z + 3);
                        if (lootChest != null)
                            LootUtil.generateLoot(lootChest, random, loot, 2, 4);
                        break;
                    case 2:
                        world.setBlock(x + 3, yy + 1, z + 1, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 3, yy + 1, z + 1);
                        if (lootChest != null)
                            LootUtil.generateLoot(lootChest, random, loot, 2, 4);
                        break;
                    case 3:
                        world.setBlock(x + 3, yy + 1, z + 3, BLBlockRegistry.weedwoodChest);
                        lootChest = (TileEntityWeedWoodChest) world.getTileEntity(x + 3, yy + 1, z + 3);
                        if (lootChest != null)
                            LootUtil.generateLoot(lootChest, random, loot, 2, 4);
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
