package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootBasicList;
import thebetweenlands.world.loot.LootUtil;

import java.util.Random;

/**
 * Created by Bart on 25/03/2016.
 */
public class WorldGenMudStructures extends WorldGenerator {
    private static final int[] stairSequence = new int[]{0, 3, 1, 2};
    private static final int[] upsideDownStairSequence = new int[]{4, 7, 5, 6};
    private static final Block mudBrick = BLBlockRegistry.mudBrick;
    private static final Block mudBrickSlab = BLBlockRegistry.mudBrickSlab;
    private static final Block mudBrickStair = BLBlockRegistry.mudBrickStairs;
    private static final Block mudFlowerPot = BLBlockRegistry.mudFlowerPot;
    private static final Block rottenBark = BLBlockRegistry.rottenWeedwoodBark;
    private static final Block mudBrickWall = BLBlockRegistry.mudBrickWall;
    private int width = -1;
    private int depth = -1;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return structure1(world, random, x, y, z);
    }

    private boolean structure1(World world, Random random, int x, int y, int z) {
        width = 7;
        depth = 5;
        int direction = random.nextInt(4);

        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 2, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 0, 0, 0, mudBrick, 0, 4, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 1, 0, mudBrickStair, getMetaFromDirection(3, direction, stairSequence), 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 1, 0, mudFlowerPot, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 1, 1, mudBrickStair, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 0, 1, mudBrick, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 1, mudBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 0, 3, mudBrick, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 0, 1, 3, mudBrickStair, getMetaFromDirection(0, direction, stairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 4, mudBrick, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 4, mudBrickStair, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 0, 4, mudBrick, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 1, 4, mudBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 1, 4, mudBrickStair, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 1, 4, mudBrickStair, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 3, mudBrick, 0, 1, 1, 1, direction);
        rotatedLoot(world, random, x, y, z, 5, 0, 3, direction, 1, 3, 2);

        rotatedCubeVolume(world, x, y, z, 1, 1, 2, mudBrickStair, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 4, 1, 2, mudBrickStair, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedLoot(world, random, x, y, z, 1, 0, 2, direction, 1, 2, 3);
        rotatedLoot(world, random, x, y, z, 1, 0, 3, direction, 1, 2, 3);
        rotatedLoot(world, random, x, y, z, 1, 0, 4, direction, 1, 2, 3);
        rotatedLoot(world, random, x, y, z, 4, 0, 2, direction, 1, 2, 3);
        rotatedLoot(world, random, x, y, z, 4, 0, 3, direction, 1, 2, 3);
        rotatedLoot(world, random, x, y, z, 4, 0, 4, direction, 1, 2, 3);
        return true;
    }

    private boolean structure2(World world, Random random, int x, int y, int z) {
        width = 6;
        depth = 6;
        int direction = random.nextInt(4);

        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 2, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 1, 0, 0, mudBrick, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 0, mudBrickStair, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 0, 1, mudBrick, 0, 1, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 0, 1, 1, mudBrickStair, getMetaFromDirection(0, direction, stairSequence), 1, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 1, mudBrick, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 1, mudBrickSlab, 0, 4, 1, 4, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 5, mudBrick, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 5, mudBrickStair, getMetaFromDirection(1, direction, stairSequence), 5, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 0, 1, mudBrick, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 1, mudBrick, 0, 1, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 5, 1, 1, mudBrickStair, getMetaFromDirection(2, direction, stairSequence), 1, 1, 4, direction);
        rotatedCubeVolume(world, x, y, z, 4, 0, 0, mudBrick, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 1, 0, mudBrickStair, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        return true;
    }

    private boolean structure3(World world, Random random, int x, int y, int z) {
        width = 8;
        depth = 8;
        int direction = random.nextInt(4);

        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 2, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED))
            return false;
        rotatedCubeVolume(world, x, y, z, 1, 0, 1, mudBrick, 0, 4, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 1, mudBrick, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 1, mudBrick, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 0, mudBrickStair, getMetaFromDirection(3, direction, stairSequence), 4, 1, 1, direction);

        return true;
    }

    private void rotatedCubeVolume(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
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

    private void rotatedLoot(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, int direction, int min, int max, int chance) {
        x -= width / 2;
        z -= depth / 2;
        if (rand.nextInt(chance) == 0)
            return;
        switch (direction) {
            case 0:
                generateLoot(world, rand, x + offsetA, y + offsetB, z + offsetC, min, max);
                break;
            case 1:
                generateLoot(world, rand, x + offsetC, y + offsetB, z + depth - offsetA - 1, min, max);
                break;
            case 2:
                generateLoot(world, rand, x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1, min, max);
                break;
            case 3:
                generateLoot(world, rand, x + width - offsetC - 1, y + offsetB, z + offsetA, min, max);
                break;
        }
    }

    private boolean rotatedCubeCantReplace(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
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
                        }
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                        }
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                        }
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                        }
                break;
        }
        return !replaceable;
    }

    private boolean rotatedCubeMatches(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction, SurfaceType type) {
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

    private void generateLoot(World world, Random random, int x, int y, int z, int min, int max) {
        int randDirection = random.nextInt(4) + 2;
        world.setBlock(x, y, z, getRandomBlock(random), randDirection, 3);
        TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, z);
        if (lootPot != null)
            LootUtil.generateLoot(lootPot, random, LootBasicList.loot, min, max);
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

    private int getMetaFromDirection(int start, int direction, int[] sequence) {
        return sequence[(direction + start) % sequence.length];
    }
}
