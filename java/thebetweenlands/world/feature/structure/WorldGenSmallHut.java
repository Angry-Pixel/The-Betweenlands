package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLDoor;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;

import java.util.Random;

/**
 * Created by Bart on 28/02/2016.
 */
public class WorldGenSmallHut extends WorldGenerator {
    private static int[] ladderSequence = new int[]{3, 5, 2, 4};
    private static int[] slopeSequence = new int[]{0, 3, 1, 2};
    private static int[] upsideDownSlopeSequence = new int[]{4, 7, 5, 6};
    private static int[] doorSequence = new int[]{0, 3, 2, 1};
    private static Block weedwoodPlankFence = BLBlockRegistry.weedwoodPlankFence;
    private static Block weedwoodPlanks = BLBlockRegistry.weedwoodPlanks;
    private static Block weedwoodLog = BLBlockRegistry.weedwoodLog;
    private int width = -1;
    private int depth = -1;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return hut(world, random, x, y, z);
    }

    public boolean hut(World world, Random random, int x, int y, int z) {
        width = 12;
        depth = 10;
        int direction = random.nextInt(4);
        /*if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 1, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 1, -1, 1, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 10, -1, 1, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 1, -1, 6, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 3, -1, 6, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 6, -1, 6, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 10, -1, 6, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 3, -1, 8, 1, 1, 1, direction, SurfaceType.MIXED)
                || !rotatedCubeMatches(world, x, y, z, 6, -1, 8, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;

        rotatedCubeVolume(world, x, y, z, 1, 0, 1, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 1, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 6, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 0, 6, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 6, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 6, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 0, 8, weedwoodPlankFence, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 8, weedwoodPlankFence, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, x, y, z, 1, 1, 1, weedwoodPlanks, 0, 10, 1, 6, direction);
        rotatedCubeVolume(world, x, y, z, 4, 1, 6, weedwoodPlanks, 0, 4, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 1, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 1, 1, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 6, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 1, 6, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 1, 6, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 1, 6, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 1, 8, weedwoodLog, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 1, 8, weedwoodLog, 0, 1, 4, 1, direction);*/

        rotatedCubeVolume(world, x, y, z, 1, 3, 0, BLBlockRegistry.pitstoneBrickStairs, getMetaFromDirection(3, direction, slopeSequence), 7, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 3, 1, BLBlockRegistry.pitstoneBrickStairs, getMetaFromDirection(0, direction, slopeSequence), 1, 1, 6, direction);
        rotatedCubeVolume(world, x, y, z, 2, 3, 6, BLBlockRegistry.pitstoneBrickStairs, getMetaFromDirection(1, direction, slopeSequence), 6, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 3, 1, BLBlockRegistry.pitstoneBrickStairs, getMetaFromDirection(2, direction, slopeSequence), 1, 1, 5, direction);

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

    public void rotatedCubeDoor(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, BlockBLDoor door, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                ItemDoor.placeDoorBlock(world, x + offsetA, y + offsetB, z + offsetC, getMetaFromDirection(1, direction, doorSequence), door);
                break;
            case 1:
                ItemDoor.placeDoorBlock(world, x + offsetC, y + offsetB, z + depth - offsetA - 1, getMetaFromDirection(1, direction, doorSequence), door);
                break;
            case 2:
                ItemDoor.placeDoorBlock(world, x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1, getMetaFromDirection(1, direction, doorSequence), door);
                break;
            case 3:
                ItemDoor.placeDoorBlock(world, x + width - offsetC - 1, y + offsetB, z + offsetA, getMetaFromDirection(1, direction, doorSequence), door);
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


    public int getMetaFromDirection(int start, int direction, int[] sequence) {
        return sequence[(direction + start) % sequence.length];
    }
}
