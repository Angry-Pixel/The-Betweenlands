package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootTables;
import thebetweenlands.world.loot.LootUtil;

import java.util.Random;

public class WorldGenSmallRuins extends WorldGenerator {

    private static final boolean markReplaceableCheck = false;
    private static int[] stairSequence = new int[]{0, 3, 1, 2};
    private static int[] upsideDownStairSequence = new int[]{4, 7, 5, 6};
    private static Block betweenstoneTiles = BLBlockRegistry.betweenstoneTiles;
    private static Block betweenstoneBricks = BLBlockRegistry.betweenstoneBricks;
    private static Block betweenstoneBrickStairs = BLBlockRegistry.betweenstoneBrickStairs;
    private static Block betweenstoneBrickSlab = BLBlockRegistry.betweenstoneBrickSlab;
    private static Block chiseledBetweenstone = BLBlockRegistry.chiseledBetweenstone;
    private static Block betweenstonePillar = BLBlockRegistry.betweenstonePillar;
    private int width = -1;
    private int depth = -1;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int randomInt = random.nextInt(6);
        switch (randomInt) {
            case 0:
                return ark1(world, random, x, y, z);
            case 1:
                return ark2(world, random, x, y, z);
            case 2:
                return ark3(world, random, x, y, z);
            case 3:
                return ark4(world, random, x, y, z);
            case 4:
                return pillar1(world, random, x, y, z);
            case 5:
                return pillar2(world, random, x, y, z);
            default:
                return false;
        }
    }

    public boolean ark1(World world, Random random, int x, int y, int z) {
        int height = 9 + random.nextInt(2);
        width = 8;
        depth = 1;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 0, 1, height - 5, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 0, 1, height - 5, 1, direction);

        rotatedCubeVolume(world, x, y, z, 2, height - 5, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, height - 5, 0, betweenstoneBricks, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, height - 5, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 0, height - 4, 0, betweenstoneBrickSlab, 0, 2, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 2, height - 3, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, height - 3, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, height - 3, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, height - 3, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 3, height - 2, 0, betweenstoneBrickSlab, 0, 3, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 6, height - 1, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, height - 1, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);

        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 4, height - 2, 0, direction);
        return true;
    }


    public boolean ark2(World world, Random random, int x, int y, int z) {
        int height = 13 + random.nextInt(2);
        width = 7;
        depth = 1;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;
        rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 0, 1, height - 9, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 0, 1, height - 9, 1, direction);

        rotatedCubeVolume(world, x, y, z, 2, height - 9, 0, betweenstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, height - 9, 0, betweenstoneBricks, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 2, height - 8, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, height - 8, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 2, height - 7, 0, betweenstoneBricks, 0, 1, 5, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, height - 7, 0, betweenstoneBricks, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 3, height - 6, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, height - 6, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, height - 6, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, height - 6, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 3, height - 5, 0, betweenstoneBrickSlab, 0, 3, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 2, height - 2, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, height - 2, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, betweenstoneBrickSlab, 0, 2, 1, 1, direction);

        if (random.nextInt(5) == 0) {
            rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, betweenstoneBricks, 0, 1, 1, 1, direction);
            rotatedLoot(world, random, x, y, z, 0, height, 0, direction);
        }
        return true;
    }

    public boolean ark3(World world, Random random, int x, int y, int z) {
        width = 7;
        depth = 5;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 7, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 2, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 0, 4, betweenstoneTiles, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 4, betweenstoneTiles, 0, 1, 4, 1, direction);

        rotatedCubeVolume(world, x, y, z, 1, 4, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 4, 4, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 4, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 4, 4, betweenstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 4, betweenstoneBricks, 0, 1, 2, 1, direction);

        rotatedCubeVolume(world, x, y, z, 0, 5, 0, betweenstoneBrickSlab, 0, 2, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 3, 5, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 5, 4, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 4, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 5, 1, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 5, 1, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 5, 3, betweenstoneBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 5, 3, betweenstoneBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 3, 6, 0, betweenstoneBrickSlab, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 6, 4, betweenstoneBrickSlab, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 6, 1, betweenstoneBrickSlab, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 6, 6, 1, betweenstoneBrickSlab, 0, 1, 1, 3, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 2, 6, 0, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 6, 6, 0, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 2, 6, 4, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 6, 6, 4, direction);
        return true;
    }

    //TODO switch this to the new system at some point....
    public boolean ark4(World world, Random random, int x, int y, int z) {
        int height = 9 + random.nextInt(2);
        int width = 6;
        for (int zz = z; zz < z + width; zz++)
            for (int yy = y; yy < y + height; yy++)
                for (int xx = x; xx > x - width; xx--)
                    if (!(world.getBlock(xx, yy, zz) == Blocks.air || (world.getBlock(xx, yy, zz) == BLBlockRegistry.swampWater && yy < y + height - 2)))
                        return false;

        if (!SurfaceType.MIXED.matchBlock(world.getBlock(x - width, y - 1, z + 1)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - width, y - 1, z + width - 1)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - 1, y - 1, z + width)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - 1, y - 1, z + width)))
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
                if (yy == y + height - 4) {
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
                    if (random.nextInt(8) == 0) {
                        generateLoot(world, random, xx, yy + 1, z + width - 1);
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
            } else if (yy == y + height - 1) {
                world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
            }
        }

        return true;
    }

    public boolean pillar1(World world, Random random, int x, int y, int z) {
        int height = 5 + random.nextInt(2);
        width = 1;
        depth = 1;
        int direction = 0;
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 0, 0, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 0, 1, 0, betweenstonePillar, 0, 1, height - 2, 1, direction);

        rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);

        if (random.nextInt(5) == 0) {
            rotatedLoot(world, random, x, y, z, 0, height, 0, direction);
        }
        return true;
    }

    public boolean pillar2(World world, Random random, int x, int y, int z) {
        width = 5 + random.nextInt(2);
        depth = 1;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 1, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, width - 1, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 0, 0, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 0, betweenstonePillar, direction == 0 || direction == 2 ? 7 : 8, width - 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, width - 1, 0, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);

        if (random.nextInt(5) == 0) {
            rotatedLoot(world, random, x, y, z, 0, 1, 0, direction);
        }
        if (random.nextInt(5) == 0) {
            rotatedLoot(world, random, x, y, z, width - 1, 1, 0, direction);
        }
        return true;
    }

    public void rotatedCubeVolume(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
        }
    }

    public void rotatedLoot(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                generateLoot(world, rand, x + offsetA, y + offsetB, z + offsetC);
                break;
            case 1:
                generateLoot(world, rand, x + offsetC, y + offsetB, z + depth - offsetA - 1);
                break;
            case 2:
                generateLoot(world, rand, x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1);
                break;
            case 3:
                generateLoot(world, rand, x + width - offsetC - 1, y + offsetB, z + offsetA);
                break;
        }
    }

    public boolean rotatedCubeCantReplace(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        x -= width / 2;
        z -= depth / 2;
        boolean replaceable = true;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
        }
        return !replaceable;
    }

    public boolean rotatedCubeMatches(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction, SurfaceType type) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
        }
        return true;
    }

    private void generateLoot(World world, Random random, int x, int y, int z) {
        int randDirection = random.nextInt(4) + 2;
        world.setBlock(x, y, z, getRandomBlock(random), randDirection, 3);
        TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, z);
        if (lootPot != null)
            LootUtil.generateLoot(lootPot, random, LootTables.COMMON_POT_LOOT, 1, 2);
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

    public int getMetaFromDirection(int start, int direction, int[] sequence) {
        return sequence[(direction + start) % sequence.length];
    }

}
