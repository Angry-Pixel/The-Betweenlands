package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootItemStack;
import thebetweenlands.world.loot.LootUtil;
import thebetweenlands.world.loot.WeightedLootList;

import java.util.Random;

/**
 * Created by Bart on 26/01/2016.
 */
public class WorldGenSmallRuins extends WorldGenerator {

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
        int randomInt = random.nextInt(4);
        switch (randomInt) {
            case 0:
                return ark1(world, random, x, y, z);
            case 1:
                return ark2(world, random, x, y, z);
            case 2:
                return ark3(world, random, x, y, z);
            case 3:
                return ark4(world, random, x, y, z);
            default:
                return false;
        }
    }

    public boolean ark1(World world, Random random, int x, int y, int z) {
        int height = 9 + random.nextInt(2);
        int width = 7 + random.nextInt(2);
        if (random.nextBoolean()) {
            for (int xx = x; xx < x + width; xx++)
                for (int yy = y; yy < y + height; yy++)
                    if (!(world.getBlock(xx, yy, z) == Blocks.air))
                        return false;
            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 2)
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x + 2, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                    world.setBlock(x + 2, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                    int xx;
                    for (xx = x + 3; xx <= x + 3 + width - 7; xx++) {
                        world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                        if (random.nextInt(5) == 0 && SurfaceType.MIXED.matchBlock(world.getBlock(xx, y - 1, z))) {
                            int randDirection = random.nextInt(4) + 2;
                            world.setBlock(xx, y, z, getRandomBlock(random), randDirection, 3);
                            TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(xx, y, z);
                            if (lootPot != null)
                                LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                        }
                    }
                    world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                    world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
            for (int yy = y; yy < y + height - 2; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 4)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
                }

                if (yy == y + height - 4) {
                    world.setBlock(x + width - 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x + width - 1, yy, z, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x + width, yy, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
        } else {
            for (int zz = z; zz < z + width; zz++)
                for (int yy = y; yy < y + height; yy++)
                    if (!(world.getBlock(x, yy, zz) == Blocks.air))
                        return false;
            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 2)
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneBrickStairs, 3, 3);
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x, yy, z + 2, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                    world.setBlock(x, yy + 1, z + 2, BLBlockRegistry.betweenstoneBrickSlab);
                    int zz;
                    for (zz = z + 3; zz <= z + 3 + width - 7; zz++) {
                        world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                        if (random.nextInt(5) == 0 && SurfaceType.MIXED.matchBlock(world.getBlock(x, y - 1, zz))) {
                            int randDirection = random.nextInt(4) + 2;
                            world.setBlock(x, y, zz, getRandomBlock(random), randDirection, 3);
                            TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, zz);
                            if (lootPot != null)
                                LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                        }
                    }
                    world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                    world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
            for (int yy = y; yy < y + height - 2; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 4)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBrickStairs, 3, 3);
                }

                if (yy == y + height - 4) {
                    world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x, yy, z + width, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
        }
        return true;
    }


    public boolean ark2(World world, Random random, int x, int y, int z) {
        int height = 13 + random.nextInt(2);
        int width = 7 + random.nextInt(2);
        if (random.nextBoolean()) {
            for (int xx = x; xx < x + width; xx++)
                for (int yy = y; yy < y + height; yy++)
                    if (!(world.getBlock(xx, yy, z) == Blocks.air))
                        return false;
            for (int yy = y; yy < y + height - 4; yy++) {
                if (yy <= y + height - 9)
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy == y + height - 8 || yy == y + height - 6)
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBricks);
                else if (yy == y + height - 7)
                    world.setBlock(x, yy, z, BLBlockRegistry.chiseledBetweenstone);
                else {
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickStairs);
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                    world.setBlock(x + 1, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                    int xx;
                    for (xx = x + 2; xx <= x + 2 + width - 6; xx++) {
                        world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                        if (random.nextInt(5) == 0 && SurfaceType.MIXED.matchBlock(world.getBlock(xx, y - 1, z))) {
                            int randDirection = random.nextInt(4) + 2;
                            world.setBlock(xx, y, z, getRandomBlock(random), randDirection, 3);
                            TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(xx, y, z);
                            if (lootPot != null)
                                LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                        }
                    }
                    world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                    world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }

            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 9)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy == y + height - 8)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBricks);
                else if (yy == y + height - 7)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.chiseledBetweenstone);
                else if (yy <= y + height - 2)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBricks);
                else if (yy == y + height - 1) {
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBrickStairs);
                    world.setBlock(x + width - 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                    world.setBlock(x + width - 1, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x + width, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
        } else {
            for (int zz = z; zz < z + width; zz++)
                for (int yy = y; yy < y + height; yy++)
                    if (!(world.getBlock(x, yy, zz) == Blocks.air))
                        return false;
            for (int yy = y; yy < y + height - 4; yy++) {
                if (yy <= y + height - 9)
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy == y + height - 8 || yy == y + height - 6)
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBricks);
                else if (yy == y + height - 7)
                    world.setBlock(x, yy, z, BLBlockRegistry.chiseledBetweenstone);
                else {
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 2, 3);
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                    world.setBlock(x, yy + 1, z + 1, BLBlockRegistry.betweenstoneBrickSlab);
                    int zz;
                    for (zz = z + 2; zz <= z + 2 + width - 6; zz++) {
                        world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                        if (random.nextInt(5) == 0 && SurfaceType.MIXED.matchBlock(world.getBlock(x, y - 1, zz))) {
                            int randDirection = random.nextInt(4) + 2;
                            world.setBlock(x, y, zz, getRandomBlock(random), randDirection, 3);
                            TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, zz);
                            if (lootPot != null)
                                LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                        }
                    }
                    world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                    world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 9)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneTiles);
                else if (yy == y + height - 8)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBricks);
                else if (yy == y + height - 7)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.chiseledBetweenstone);
                else if (yy <= y + height - 2)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBricks);
                else if (yy == y + height - 1) {
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBrickStairs, 2, 3);
                    world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                    world.setBlock(x, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x, yy + 1, z + width, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
        }
        return true;
    }

    public boolean ark3(World world, Random random, int x, int y, int z) {
        int height = 6 + random.nextInt(2);
        int width = 5 + random.nextInt(2);
        for (int zz = z; zz < z + width; zz++)
            for (int yy = y; yy < y + height; yy++)
                for (int xx = x; xx > x - width; xx--)
                    if (!(world.getBlock(xx, yy, zz) == Blocks.air))
                        return false;
        if (!SurfaceType.MIXED.matchBlock(world.getBlock(x, y - 1, z)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - width, y - 1, z)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - width, y - 1, z + width)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x, y - 1, z + width)))
            return false;
        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 3)
                world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneTiles);
            else if (yy <= y + height - 1)
                world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBricks);
            if (yy == y + height - 1) {
                world.setBlock(x - 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                world.setBlock(x - 1, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                int xx;
                for (xx = x - 2; xx >= x - width + 2; xx--) {
                    world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
                world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
            }
        }

        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 3)
                world.setBlock(x - width, yy, z, BLBlockRegistry.betweenstoneTiles);
            else if (yy <= y + height - 1)
                world.setBlock(x - width, yy, z, BLBlockRegistry.betweenstoneBricks);
            if (yy == y + height - 1) {
                world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                world.setBlock(x - width, yy + 1, z + 1, BLBlockRegistry.betweenstoneBrickSlab);
                int zz;
                for (zz = z + 2; zz <= z + width - 2; zz++) {
                    world.setBlock(x - width, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                }
                world.setBlock(x - width, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                world.setBlock(x - width, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
            }
        }

        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 3)
                world.setBlock(x - width, yy, z + width, BLBlockRegistry.betweenstoneTiles);
            else if (yy <= y + height - 1)
                world.setBlock(x - width, yy, z + width, BLBlockRegistry.betweenstoneBricks);
            if (yy == y + height - 1) {
                world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                world.setBlock(x - width + 1, yy + 1, z + width, BLBlockRegistry.betweenstoneBrickSlab);
                int xx;
                for (xx = x - width + 2; xx <= x - 2; xx++) {
                    world.setBlock(xx, yy + 1, z + width, BLBlockRegistry.betweenstoneBrickSlab);
                }
                world.setBlock(xx, yy, z + width, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                world.setBlock(xx, yy + 1, z + width, BLBlockRegistry.betweenstoneBrickSlab);
            }
        }

        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 3)
                world.setBlock(x, yy, z + width, BLBlockRegistry.betweenstoneTiles);
            else if (yy <= y + height - 1)
                world.setBlock(x, yy, z + width, BLBlockRegistry.betweenstoneBricks);
            if (yy == y + height - 1) {
                world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                world.setBlock(x, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                int zz;
                for (zz = z + width - 2; zz >= z + 2; zz--) {
                    world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                }
                world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
            }
        }

        if (random.nextInt(5) == 0 && width - 5 == 1) {
            if (SurfaceType.MIXED.matchBlock(world.getBlock(x - 3, y - 1, z + 3))) {
                int randDirection = random.nextInt(4) + 2;
                world.setBlock(x - 3, y, z + 3, getRandomBlock(random), randDirection, 3);
                TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x - 3, y, z + 3);
                if (lootPot != null)
                    LootUtil.generateLoot(lootPot, random, loot, 1, 2);
            }
        } else {
            if (SurfaceType.MIXED.matchBlock(world.getBlock(x - 3, y - 1, z + 3))) {
                if (random.nextInt(8) == 0) {
                    int randDirection = random.nextInt(4) + 2;
                    world.setBlock(x - 2, y, z + 2, getRandomBlock(random), randDirection, 3);
                    TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x - 2, y, z + 2);
                    if (lootPot != null)
                        LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                }
                if (random.nextInt(8) == 0) {
                    int randDirection = random.nextInt(4) + 2;
                    world.setBlock(x - 2, y, z + 3, getRandomBlock(random), randDirection, 3);
                    TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x - 2, y, z + 3);
                    if (lootPot != null)
                        LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                }
                if (random.nextInt(8) == 0) {
                    int randDirection = random.nextInt(4) + 2;
                    world.setBlock(x - 3, y, z + 2, getRandomBlock(random), randDirection, 3);
                    TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x - 3, y, z + 2);
                    if (lootPot != null)
                        LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                }
                if (random.nextInt(8) == 0) {
                    int randDirection = random.nextInt(4) + 2;
                    world.setBlock(x - 3, y, z + 3, getRandomBlock(random), randDirection, 3);
                    TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x - 3, y, z + 3);
                    if (lootPot != null)
                        LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                }
            }
        }
        return true;
    }


    public boolean ark4(World world, Random random, int x, int y, int z) {
        int height = 9 + random.nextInt(2);
        int width = 6 + random.nextInt(2);
        for (int zz = z; zz < z + width; zz++)
            for (int yy = y; yy < y + height; yy++)
                for (int xx = x; xx > x - width; xx--)
                    if (!(world.getBlock(xx, yy, zz) == Blocks.air))
                        return false;
        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 5) {
                if (yy == y)
                    world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneTiles);
                else
                    world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneBrickWall);
                world.setBlock(x - width + 1, yy, z + 1, BLBlockRegistry.betweenstoneTiles);
            } else if (yy == y + height - 4) {
                world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneBrickWall);
                world.setBlock(x - width + 1, yy, z + 1, BLBlockRegistry.betweenstoneBricks);
            } else if (yy == y + height - 3) {
                world.setBlock(x - width + 1, yy, z + 1, BLBlockRegistry.betweenstoneBrickStairs, 2, 3);
                world.setBlock(x - width + 1, yy, z + 2, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                world.setBlock(x - width + 1, yy + 1, z + 2, BLBlockRegistry.betweenstoneBrickSlab);
                int zz;
                for (zz = z + 3; zz <= z + width - 3; zz++) {
                    world.setBlock(x - width + 1, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x - width + 1, yy, zz, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                }
                world.setBlock(x - width + 1, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                world.setBlock(x - width + 1, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
            }
        }

        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 5) {
                if (yy == y) {
                    world.setBlock(x - width, yy, z + width - 1, BLBlockRegistry.betweenstoneTiles);
                    world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneTiles);
                } else {
                    world.setBlock(x - width, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickWall);
                    world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                }
                world.setBlock(x - width + 1, yy, z + width - 1, BLBlockRegistry.betweenstoneTiles);
            } else if (yy <= y + height - 2) {
                if (yy == y + height - 4){
                    world.setBlock(x - width, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickWall);
                    world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                }
                world.setBlock(x - width + 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBricks);
            } else if (yy <= y + height - 1) {
                world.setBlock(x - width + 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs);
                world.setBlock(x - width + 2, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                world.setBlock(x - width + 2, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                int xx;
                for (xx = x - width + 3; xx <= x - 3; xx++) {
                    world.setBlock(xx, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                    if (random.nextInt(8) == 0){
                        int randDirection = random.nextInt(4) + 2;
                        world.setBlock(xx, yy + 1, z + width - 1, getRandomBlock(random), randDirection, 3);
                        TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(xx, yy + 1, z + width - 1);
                        if (lootPot != null)
                            LootUtil.generateLoot(lootPot, random, loot, 1, 2);
                    } else
                        world.setBlock(xx, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                }
                world.setBlock(xx, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                world.setBlock(xx, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
            }
        }

        for (int yy = y; yy < y + height; yy++) {
            if (yy <= y + height - 5) {
                if (yy == y) {
                    world.setBlock(x - 1, yy, z + width, BLBlockRegistry.betweenstoneTiles);
                } else {
                    world.setBlock(x - 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                }
                world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneTiles);
            } else if (yy <= y + height - 2) {
                if (yy == y + height - 4) {
                    world.setBlock(x - 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                }
                world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBricks);
            } else if (yy == y + height - 1){
                world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
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
