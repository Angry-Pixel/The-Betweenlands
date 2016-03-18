package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLSpawner;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.loot.LootBasicList;
import thebetweenlands.world.loot.LootUtil;

import java.util.Random;

/**
 * Created by Bart on 07/03/2016.
 */
public class WorldGenCragrockTower extends WorldGenerator {

    private static int[] stairSequence = new int[]{0, 3, 1, 2};
    private static int[] upsideDownStairSequence = new int[]{4, 7, 5, 6};
    private static Block genericStone = BLBlockRegistry.genericStone;
    private static Block cragrockTiles = BLBlockRegistry.cragTiles;
    private static Block cragrockBricks = BLBlockRegistry.cragrockBrick;
    private static Block smoothCragrockStairs = BLBlockRegistry.smoothCragrockStairs;
    private static Block cragrockBrickSlab = BLBlockRegistry.cragrockBrickSlab;
    private static Block smoothCragrockSlab = BLBlockRegistry.smoothCragrockSlab;
    private static Block cragrockBrickStairs = BLBlockRegistry.cragrockBrickStairs;
    private static Block cragrockPillar = BLBlockRegistry.cragrockPillar;
    private static Block smoothCragrock = BLBlockRegistry.smoothCragrock;
    private static Block carvedCragrock = BLBlockRegistry.carvedCrag;
    private static BlockBLSpawner spawner = BLBlockRegistry.blSpawner;
    private static Block root = BLBlockRegistry.root;
    private static Block smoothBetweenstoneWall = BLBlockRegistry.smoothBetweenstoneWall;
    private static Block cragrockBrickWall = BLBlockRegistry.cragrockWall;
    private static Block smoothCragrockWall = BLBlockRegistry.smoothCragrockWall;
    private static Block glowingSmoothCragrock = BLBlockRegistry.glowingSmoothCragrock;
    private static Block wisp = BLBlockRegistry.wisp;
    private static int cragrockMeta = 1;
    private static int mossyCragrockTopMeta = 2;
    private static int mossyCragrockBottomMeta = 3;
    private Block[] blackListBlocks = new Block[]{BLBlockRegistry.cragrockBrick, BLBlockRegistry.cragrockBrickSlab, BLBlockRegistry.cragrockWall, BLBlockRegistry.betweenstoneTiles, BLBlockRegistry.betweenstoneBrickStairs, BLBlockRegistry.betweenstoneBricks, BLBlockRegistry.betweenstoneBrickSlab};
    private int width = -1;
    private int depth = -1;
    private int height = -1;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return tower(world, random, x, y, z);
    }

    public boolean tower(World world, Random random, int x, int y, int z) {
        width = 17;
        depth = 19;
        height = 64;
        int direction = random.nextInt(4);
        if (!canGenerate(world, x, y, z, direction))
            return false;
        System.out.println("generating tower at: " + x + " " + y + " " + z);

        rotatedCubeSetToAir(world, x, y, z, 0, 0, 0, width, height, depth, direction);

        rotatedCubeVolume(world, x, y, z, 0, -1, 0, BLBlockRegistry.swampGrass, 0, width, 1, depth, direction);

        //FLOOR 0

        //WALLS
        rotatedCubeVolume(world, x, y, z, 7, 0, 5, genericStone, cragrockMeta, 3, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 6, genericStone, cragrockMeta, 2, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 6, genericStone, cragrockMeta, 2, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 0, 7, genericStone, cragrockMeta, 1, 3, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 0, 7, genericStone, cragrockMeta, 1, 3, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 0, 9, genericStone, cragrockMeta, 1, 3, 3, direction);
        rotatedCubeVolume(world, x, y, z, 13, 0, 9, genericStone, cragrockMeta, 1, 3, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 0, 12, genericStone, cragrockMeta, 1, 5, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 0, 12, genericStone, cragrockMeta, 1, 5, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 14, genericStone, cragrockMeta, 2, 5, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 14, genericStone, cragrockMeta, 2, 5, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 15, genericStone, cragrockMeta, 3, 5, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 3, 5, cragrockBricks, 0, 3, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 3, 6, cragrockBricks, 0, 2, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 3, 6, cragrockBricks, 0, 2, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 3, 7, cragrockBricks, 0, 1, 2, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 3, 7, cragrockBricks, 0, 1, 2, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 3, 9, cragrockBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 3, 9, cragrockBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 3, 10, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 3, 10, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 4, 10, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 4, 10, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 3, 11, genericStone, cragrockMeta, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 3, 11, genericStone, cragrockMeta, 1, 2, 1, direction);

        //FLOOR
        rotatedCubeVolume(world, x, y, z, 7, 0, 6, smoothCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 7, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 7, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 0, 7, smoothCragrock, 0, 1, 1, 8, direction);
        rotatedCubeVolume(world, x, y, z, 9, 0, 7, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 7, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 8, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 8, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 8, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 0, 8, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 8, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 0, 8, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 0, 9, smoothCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 9, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 9, smoothCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 9, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 0, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 0, 9, smoothCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 10, smoothCragrock, 0, 7, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 11, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 11, smoothCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 11, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 0, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 12, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 0, 12, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 0, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 13, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 0, 13, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 14, smoothCragrock, 0, 3, 1, 1, direction);

        //CEILING
        rotatedCubeVolume(world, x, y, z, 7, 4, 6, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 7, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 4, 7, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 4, 7, cragrockBrickSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 9, 4, 7, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 4, 7, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 8, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 8, cragrockBrickSlab, 8, 2, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 9, 4, 8, cragrockBrickSlab, 8, 2, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 8, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 4, 9, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 9, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 9, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 4, 9, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 10, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 10, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 4, 13, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 4, 13, smoothCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 9, 4, 13, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 4, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 4, 14, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 4, 14, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);

        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 8, 1, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 2, 10, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 3, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 4, 12, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 1, 10, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 1, 11, smoothCragrock, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 3, 12, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 3, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 1, 14, smoothCragrock, 0, 1, 3, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 2, 13, direction, "thebetweenlands.wight");
        rotatedCubeVolume(world, x, y, z, 9, 1, 11, root, 0, 1, 2 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 1, 10, root, 0, 1, 2 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 1, 10, root, 0, 1, 2 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 1, 14, root, 0, 1, 1 + random.nextInt(2), 1, direction);

        //FLOOR 1
        //WALLS
        rotatedCubeVolume(world, x, y, z, 7, 5, 5, cragrockBricks, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 6, cragrockBricks, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 5, 6, cragrockBricks, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 5, 7, cragrockBricks, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 5, 7, cragrockBricks, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 5, 9, cragrockBricks, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 13, 5, 9, cragrockBricks, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 5, 12, cragrockBricks, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 5, 12, cragrockBricks, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 5, 14, cragrockBricks, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 14, cragrockBricks, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 5, 15, cragrockBricks, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 6, 5, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 6, 6, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 6, 6, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 6, 7, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 6, 7, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 6, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 13, 6, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 6, 12, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 6, 12, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 6, 14, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 6, 14, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 6, 15, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 7, 5, cragrockBricks, 0, 3, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 7, 6, cragrockBricks, 0, 2, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 7, 6, cragrockBricks, 0, 2, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 7, 7, cragrockBricks, 0, 1, 6, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 7, 7, cragrockBricks, 0, 1, 6, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 7, 9, cragrockBricks, 0, 1, 6, 3, direction);
        rotatedCubeVolume(world, x, y, z, 13, 7, 9, cragrockBricks, 0, 1, 6, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 7, 12, cragrockBricks, 0, 1, 6, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 7, 12, cragrockBricks, 0, 1, 6, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 7, 14, cragrockBricks, 0, 2, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 7, 14, cragrockBricks, 0, 2, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 7, 15, cragrockBricks, 0, 3, 6, 1, direction);

        //CEILING
        rotatedCubeVolume(world, x, y, z, 7, 9, 6, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 7, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 9, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 9, 7, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 9, 9, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 9, 7, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 8, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 9, 8, smoothCragrockSlab, 8, 2, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 9, 9, 8, smoothCragrockSlab, 8, 2, 1, 5, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 8, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 9, 9, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 9, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 9, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 9, 9, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 11, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 11, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 12, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 12, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 9, 13, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 9, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 9, 13, cragrockBricks, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 9, 9, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 9, 13, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 9, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 9, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);

        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 8, 5, 8, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 6, 9, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 7, 10, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 8, 11, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 9, 12, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 5, 9, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 6, 10, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 7, 11, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 8, 12, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 8, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 7, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeSetToAir(world, x, y, z, 8, 5, 15, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 7, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 5, 7, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 13, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 5, 13, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 11, root, 0, 1, 2 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 5, 6, root, 0, 1, 2 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 5, 14, root, 0, 1, 2 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 5, 10, root, 0, 1, 2 + random.nextInt(2), 1, direction);


        //FLOOR 2
        //CEILING/WALLS
        rotatedCubeVolume(world, x, y, z, 7, 12, 6, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 12, 7, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 12, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 12, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 12, 7, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 12, 8, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 12, 8, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 12, 9, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 12, 9, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 12, 9, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 12, 9, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 12, 11, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 12, 11, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 12, 12, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 12, 12, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 12, 13, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 12, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 12, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 12, 13, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 12, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 12, 13, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 12, 10, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 12, 7, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 12, 10, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 13, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 10, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 7, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 10, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 14, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 10, smoothCragrock, 0, 7, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 14, 7, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 14, 8, smoothCragrock, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 14, 9, smoothCragrock, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 14, 11, smoothCragrock, 0, 4, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 14, 11, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 6, cragrockBricks, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 13, 7, cragrockBricks, 0, 2, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 13, 7, cragrockBricks, 0, 2, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 8, cragrockBricks, 0, 1, 3, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 8, cragrockBricks, 0, 1, 3, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 13, 10, cragrockBricks, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 13, 10, cragrockBricks, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 11, cragrockBricks, 0, 1, 3, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 11, cragrockBricks, 0, 1, 3, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 13, 13, cragrockBricks, 0, 2, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 13, 13, cragrockBricks, 0, 2, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 14, cragrockBricks, 0, 1, 3, 1, direction);


        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 5, 10, 7, smoothBetweenstoneWall, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 10, 7, smoothBetweenstoneWall, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 10, 13, smoothBetweenstoneWall, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 10, 13, smoothBetweenstoneWall, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 10, 8, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 10, 12, smoothBetweenstoneWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 10, 12, smoothBetweenstoneWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 10, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 10, 9, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 10, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 11, 10, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 11, 11, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 11, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 12, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 12, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 12, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 13, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 13, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 14, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 10, 9, root, 0, 1, 1 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 10, 11, root, 0, 1, 1 + random.nextInt(2), 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 10, 13, root, 0, 1, 1 + random.nextInt(2), 1, direction);


        //FLOOR 3
        //WALLS
        rotatedCubeVolume(world, x, y, z, 8, 16, 6, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 16, 7, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 16, 7, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 16, 8, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 16, 8, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 16, 10, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 16, 10, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 16, 11, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 16, 11, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 16, 13, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 16, 13, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 16, 14, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 17, 6, smoothCragrock, 0, 1, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 17, 7, smoothCragrock, 0, 2, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 17, 7, smoothCragrock, 0, 2, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 17, 8, smoothCragrock, 0, 1, 7, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 17, 8, smoothCragrock, 0, 1, 7, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 17, 10, smoothCragrock, 0, 1, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 17, 10, smoothCragrock, 0, 1, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 17, 11, smoothCragrock, 0, 1, 7, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 17, 11, smoothCragrock, 0, 1, 7, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 17, 13, smoothCragrock, 0, 2, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 17, 13, smoothCragrock, 0, 2, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 17, 14, smoothCragrock, 0, 1, 7, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 7, smoothCragrockWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 13, cragrockBrickWall, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 10, cragrockBrickWall, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 17, 7, cragrockBrickWall, 0, 1, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 10, cragrockBrickWall, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 16, 7, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 18, 10, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 20, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 22, 10, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 24, 6, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 24, 7, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 24, 7, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 24, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 24, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 24, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 24, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 24, 12, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 24, 12, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 24, 13, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 24, 13, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 24, 14, carvedCragrock, 0, 3, 1, 1, direction);

        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 8, 15, 10, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 16, 10, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 17, 10, direction, "thebetweenlands.swampHag");
        rotatedCubeVolume(world, x, y, z, 6, 15, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 15, 8, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 16, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 16, 8, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 16, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 17, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 17, 9, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 17, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 18, 10, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 18, 11, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 18, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 19, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 19, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 19, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 20, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 20, 12, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 20, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 21, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 21, 11, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 21, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 22, 10, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 22, 9, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 22, 8, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 23, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 22, 8, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 4, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 22, 9, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 4, direction);

        //CEILING
        rotatedCubeVolume(world, x, y, z, 7, 23, 7, smoothCragrock, 0, 4, 1, 7, direction);
        rotatedCubeVolume(world, x, y, z, 7, 23, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 23, 10, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 23, 10, smoothCragrock, 0, 1, 1, 1, direction);


        //WALLS FLOOR 4/5/6
        rotatedCubeVolume(world, x, y, z, 7, 25, 6, smoothCragrock, 0, 3, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 25, 7, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 25, 7, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 25, 8, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 25, 8, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 25, 9, smoothCragrock, 0, 1, 8, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 25, 9, smoothCragrock, 0, 1, 8, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 25, 12, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 25, 12, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 25, 13, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 25, 13, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 25, 14, smoothCragrock, 0, 3, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 33, 6, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 33, 7, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 33, 7, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 33, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 33, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 33, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 33, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 33, 12, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 33, 12, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 33, 13, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 33, 13, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 33, 14, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 34, 6, smoothCragrock, 0, 3, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 34, 7, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 34, 7, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 34, 8, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 34, 8, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 34, 9, smoothCragrock, 0, 1, 8, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 34, 9, smoothCragrock, 0, 1, 8, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 34, 12, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 34, 12, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 34, 13, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 34, 13, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 34, 14, smoothCragrock, 0, 3, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 42, 6, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 42, 7, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 42, 7, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 42, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 42, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 42, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 42, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 42, 12, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 42, 12, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 42, 13, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 42, 13, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 42, 14, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 43, 6, smoothCragrock, 0, 3, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 43, 7, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 43, 7, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 43, 8, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 43, 8, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 43, 9, smoothCragrock, 0, 1, 8, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 43, 9, smoothCragrock, 0, 1, 8, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 43, 12, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 43, 12, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 43, 13, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 43, 13, smoothCragrock, 0, 1, 8, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 43, 14, smoothCragrock, 0, 3, 8, 1, direction);


        //FLOOR 4
        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 8, 24, 10, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 25, 10, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 26, 10, direction, "thebetweenlands.swampHag");
        rotatedCubeVolume(world, x, y, z, 10, 24, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 24, 9, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 24, 10, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 25, 10, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 25, 11, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 25, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 26, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 26, 12, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 26, 12, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 27, 12, smoothCragrockSlab, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 7, 27, 12, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 27, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 28, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 28, 11, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 28, 10, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 29, 10, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 29, 9, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 29, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 30, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 30, 7, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 30, 7, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 31, 7, smoothCragrockSlab, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 9, 31, 7, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 31, 8, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 32, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 31, 9, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 31, 9, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 31, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 31, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 31, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 31, 12, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 31, 13, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 31, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 31, 13, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 31, 12, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 31, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 31, 10, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 30, 10, glowingSmoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 29, 10, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

        //CEILING
        rotatedCubeVolume(world, x, y, z, 5, 32, 9, smoothCragrock, 0, 7, 1, 4, direction);
        rotatedCubeVolume(world, x, y, z, 6, 32, 9, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 32, 13, smoothCragrock, 0, 3, 1, 1, direction);

        //FLOOR 5
        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 8, 33, 10, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 34, 10, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 35, 10, direction, "thebetweenlands.wight");
        rotatedCubeVolume(world, x, y, z, 10, 33, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 33, 12, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 33, 12, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 34, 12, smoothCragrockSlab, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 7, 34, 12, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 34, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 35, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 35, 11, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 35, 10, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 36, 10, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 36, 9, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 36, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 37, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 37, 7, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 37, 7, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 38, 7, smoothCragrockSlab, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 9, 38, 7, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 38, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 39, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 39, 9, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 39, 10, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 40, 10, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 40, 11, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 40, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 41, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 40, 12, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 40, 13, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 40, 13, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 40, 12, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 40, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 40, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 40, 10, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 5, 40, 9, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 40, 9, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 40, 8, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 40, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 39, 13, glowingSmoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 38, 13, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);


        //CEILING
        rotatedCubeVolume(world, x, y, z, 6, 41, 7, smoothCragrock, 0, 4, 1, 7, direction);
        rotatedCubeVolume(world, x, y, z, 9, 41, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 41, 9, smoothCragrock, 0, 1, 1, 3, direction);

        //FLOOR 6
        //INTERIOR
        rotatedCubeVolume(world, x, y, z, 8, 42, 10, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 43, 10, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 44, 10, direction, "thebetweenlands.swampHag");
        rotatedCubeVolume(world, x, y, z, 6, 42, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 42, 11, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 42, 10, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 43, 10, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 43, 9, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 43, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 44, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 44, 7, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 44, 7, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 45, 7, smoothCragrockSlab, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 9, 45, 7, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 10, 45, 8, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 46, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 46, 9, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 46, 10, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 47, 10, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 47, 11, smoothCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 47, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 48, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 48, 12, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 48, 12, smoothCragrockSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 8, 49, 12, smoothCragrockSlab, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 7, 49, 12, smoothCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 49, 12, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 50, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 49, 11, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 49, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 49, 9, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 49, 9, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 49, 8, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 49, 8, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 49, 7, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 49, 7, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 49, 7, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 49, 8, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 49, 8, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 49, 10, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 48, 10, glowingSmoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 47, 10, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);

        //CEILING
        rotatedCubeVolume(world, x, y, z, 5, 50, 8, smoothCragrock, 0, 7, 1, 4, direction);
        rotatedCubeVolume(world, x, y, z, 10, 50, 11, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 50, 7, smoothCragrock, 0, 3, 1, 1, direction);


        //TOP FLOOR
        rotatedCubeVolume(world, x, y, z, 7, 51, 6, smoothCragrockSlab, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 51, 7, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 51, 7, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 51, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 51, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 51, 9, smoothCragrockSlab, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 12, 51, 9, smoothCragrockSlab, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 5, 51, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 51, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 51, 13, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 51, 13, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 51, 14, smoothCragrockSlab, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 51, 5, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 51, 6, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 51, 6, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 51, 7, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 51, 7, carvedCragrock, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 51, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 51, 8, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 51, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 13, 51, 9, carvedCragrock, 0, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 4, 51, 12, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 51, 12, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 5, 51, 13, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 11, 51, 13, carvedCragrock, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 6, 51, 14, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 51, 14, carvedCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 51, 15, carvedCragrock, 0, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 51, 4, cragrockBrickSlab, 8, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 51, 5, cragrockBrickSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 51, 5, cragrockBrickSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 51, 6, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 51, 6, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 51, 7, cragrockBrickSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 13, 51, 7, cragrockBrickSlab, 8, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 2, 51, 9, cragrockBrickSlab, 8, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 14, 51, 9, cragrockBrickSlab, 8, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 3, 51, 12, cragrockBrickSlab, 8, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 13, 51, 12, cragrockBrickSlab, 8, 1, 1, 3, direction);
        rotatedCubeVolume(world, x, y, z, 4, 51, 14, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 51, 14, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 51, 15, cragrockBrickSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 51, 15, cragrockBrickSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 51, 16, cragrockBrickSlab, 8, 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 52, 4, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 52, 4, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 52, 4, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 52, 5, genericStone, mossyCragrockTopMeta, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 52, 5, genericStone, mossyCragrockTopMeta, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 52, 6, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 52, 6, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 52, 7, genericStone, mossyCragrockTopMeta, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 13, 52, 7, genericStone, mossyCragrockTopMeta, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 2, 52, 9, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 52, 10, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 52, 11, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 52, 9, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 52, 10, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 52, 11, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 52, 12, genericStone, mossyCragrockTopMeta, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 52, 14, cragrockPillar, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 52, 12, genericStone, mossyCragrockTopMeta, 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 13, 52, 14, cragrockPillar, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 52, 14, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 52, 14, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 52, 15, genericStone, mossyCragrockTopMeta, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 52, 15, genericStone, mossyCragrockTopMeta, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 52, 16, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 52, 16, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 52, 16, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 53, 4, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 53, 4, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 53, 4, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 53, 6, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 53, 6, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 53, 9, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 53, 10, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 53, 11, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 53, 9, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 53, 10, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 53, 11, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 53, 14, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 53, 14, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 53, 16, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 53, 16, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 53, 16, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 54, 4, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 54, 10, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 54, 10, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 54, 16, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 53, 14, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 53, 14, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 54, 14, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 54, 14, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 55, 14, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 55, 14, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 52, 11, cragrockPillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 55, 11, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 56, 11, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 57, 11, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 53, 8, cragrockPillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 57, 8, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 58, 8, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 59, 8, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 53, 5, cragrockPillar, 0, 1, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 59, 5, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 60, 5, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 61, 5, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 55, 4, cragrockPillar, 0, 1, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 61, 4, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 62, 4, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 63, 4, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 53, 5, cragrockPillar, 0, 1, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 59, 5, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 60, 5, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 61, 5, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 53, 8, cragrockPillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 57, 8, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 58, 8, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 59, 8, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 52, 11, cragrockPillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 55, 11, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 56, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 57, 11, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 52, 5, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 53, 5, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 54, 5, direction, "thebetweenlands.swampHag");
        rotatedCubeVolume(world, x, y, z, 8, 52, 15, cragrockBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 53, 15, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedSpawner(world, x, y, z, 8, 54, 15, direction, "thebetweenlands.wight");

        //OUTSIDE DECORATION
        rotatedCubeVolume(world, x, y, z, 0, 0, 6, cragrockPillar, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 1, 6, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 2, 6, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 3, 6, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 0, 9, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 1, 9, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 3, 9, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 4, 9, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 5, 9, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 6, 9, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 0, 12, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 2, 12, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 3, 12, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 5, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 6, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 0, 7, 12, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 0, 15, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 1, 15, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 3, 15, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 4, 15, cragrockPillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 7, 15, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 8, 15, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 9, 15, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 0, 17, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 1, 17, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 2, 17, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 17, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 5, 17, cragrockPillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 17, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 10, 17, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 11, 17, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 0, 18, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 1, 18, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 2, 18, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 3, 18, cragrockPillar, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 4, 18, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 5, 18, cragrockPillar, 0, 1, 6, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 11, 18, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 12, 18, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 12, 18, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 0, 6, cragrockPillar, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 1, 6, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 2, 6, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1611, 3, 6, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 0, 9, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 1, 9, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 3, 9, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 4, 9, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 5, 9, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 6, 9, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 0, 12, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 2, 12, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 3, 12, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 5, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 6, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 16, 7, 12, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 0, 15, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 1, 15, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 3, 15, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 4, 15, cragrockPillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 7, 15, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 8, 15, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 9, 15, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 0, 17, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 1, 17, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 2, 17, cragrockPillar, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 17, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 5, 17, cragrockPillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 17, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 10, 17, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 11, 17, wisp, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 8, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 9, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 0, 10, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 10, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 11, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 1, 12, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 1, 2, 12, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 2, 13, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 2, 14, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 3, 14, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 2, 15, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 3, 15, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 3, 16, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 3, 16, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 16, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 17, cragrockBricks, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 0, 8, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 0, 9, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 0, 10, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 1, 10, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 1, 11, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 1, 12, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 15, 2, 12, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 2, 13, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 2, 14, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 3, 14, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 2, 15, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 3, 15, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 3, 16, cragrockBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 3, 16, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 16, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 0, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 0, 9, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 0, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 1, 10, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 1, 11, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 1, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 2, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 1, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 2, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 2, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 2, 14, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 3, 14, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 3, 15, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 3, 15, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 4, 15, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 15, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 4, 16, smoothCragrock, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 4, 15, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 0, 8, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 0, 9, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 0, 10, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 1, 10, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 1, 11, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 1, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 2, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 1, 12, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 2, 12, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 2, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 2, 14, smoothCragrockSlab, 8, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 3, 14, smoothCragrockSlab, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 3, 15, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 3, 15, smoothCragrockSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 4, 15, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 13, 5, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 3, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 13, 6, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 13, 6, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 13, 6, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 13, 6, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 6, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 7, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 6, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 7, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 13, 7, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 13, 7, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 13, 8, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 12, 13, 8, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 3, 13, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 13, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 13, 10, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 13, 13, 10, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 2, direction);
        rotatedCubeVolume(world, x, y, z, 4, 13, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 13, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 13, 12, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 13, 13, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 13, 12, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 13, 13, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 13, smoothCragrock, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 14, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 14, smoothCragrockSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 13, 14, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 13, 14, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 2, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 13, 15, smoothCragrockStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 15, smoothCragrockStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 13, 15, smoothCragrockStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 23, 6, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 23, 6, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 23, 14, smoothCragrockStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 23, 14, smoothCragrockStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 23, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 23, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 23, 9, smoothCragrockStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 23, 11, smoothCragrockStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 7, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 14, 7, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 13, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 14, 13, cragrockBrickSlab, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 7, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 7, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 13, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 13, cragrockBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 7, 4, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 8, 4, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 9, 4, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 10, 4, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 4, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 14, 4, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 4, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 14, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 7, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 8, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 10, 5, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 6, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 6, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 7, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 8, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 10, 5, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 14, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 14, 6, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 6, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 7, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 8, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 9, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 10, 7, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 13, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 14, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 15, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 14, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 15, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 7, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 8, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 9, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 10, 10, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 13, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 14, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 14, 15, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 14, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 15, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 7, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 8, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 9, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 10, 13, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 13, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 14, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 15, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 14, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 15, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 7, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 8, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 9, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 10, 7, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 13, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 14, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 15, 7, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 14, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 15, 7, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 7, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 8, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 9, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 10, 10, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 13, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 14, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 15, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 14, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 15, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 7, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 8, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 9, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 10, 13, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 13, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 14, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 15, 13, cragrockBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 14, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 15, 13, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 7, 16, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 8, 16, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 9, 16, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 10, 16, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 13, 16, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 14, 16, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 16, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 14, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 15, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 7, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 8, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 9, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 10, 15, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 13, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 14, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 14, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 11, 15, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 7, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 8, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 9, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 10, 15, smoothCragrockWall, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 13, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 14, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 5, 15, 14, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 48, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 49, 5, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 50, 5, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 48, 8, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 49, 8, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 50, 8, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 48, 8, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 49, 8, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 50, 8, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 48, 6, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 49, 6, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 50, 6, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 48, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 49, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 50, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 48, 14, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 49, 14, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 50, 14, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 48, 6, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 49, 6, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 50, 6, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 48, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 49, 10, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 50, 10, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 48, 14, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 49, 14, cragrockBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 50, 14, cragrockBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 48, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 49, 15, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 50, 15, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 48, 12, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 49, 12, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 4, 50, 12, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 48, 12, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 49, 12, cragrockBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 50, 12, cragrockBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 4, genericStone, cragrockMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 1, 4, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 2, 4, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 0, 4, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 8, 1, 4, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 9, 0, 4, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 7, 0, 3, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 0, 5, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 10, 1, 5, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 0, 6, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 12, 1, 6, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 0, 7, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 0, 8, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 13, 1, 8, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 0, 5, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 6, 1, 5, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 2, 0, 6, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 0, 8, genericStone, mossyCragrockBottomMeta, 1, 1, 1, direction);
        rotatedCubeVolume(world, x, y, z, 3, 1, 8, genericStone, mossyCragrockTopMeta, 1, 1, 1, direction);
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

    public void rotatedSpawner(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int direction, String mob) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                world.setBlock(x + offsetA, y + offsetB, z + offsetC, spawner);
                BlockBLSpawner.setMob(world, x + offsetA, y + offsetB, z + offsetC, mob);
                break;
            case 1:
                world.setBlock(x + offsetC, y + offsetB, z + depth - offsetA - 1, spawner);
                BlockBLSpawner.setMob(world, x + offsetC, y + offsetB, z + depth - offsetA - 1, mob);
                break;
            case 2:
                world.setBlock(x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1, spawner);
                BlockBLSpawner.setMob(world, x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1, mob);
                break;
            case 3:
                world.setBlock(x + width - offsetC - 1, y + offsetB, z + offsetA, spawner);
                BlockBLSpawner.setMob(world, x + width - offsetC - 1, y + offsetB, z + offsetA, mob);
                break;
        }
    }

    public void rotatedCubeSetToAir(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
                            world.setBlockToAir(xx, yy, zz);
                        }
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            world.setBlockToAir(xx, yy, zz);
                        }
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            world.setBlockToAir(xx, yy, zz);
                        }
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            world.setBlockToAir(xx, yy, zz);
                        }
                break;
        }
    }


    public boolean canGenerate(World world, int x, int y, int z, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                for (int yy = y; yy < y + height; yy++)
                    for (int xx = x; xx < x + width; xx++)
                        for (int zz = z; zz < z + depth; zz++) {
                            if (isBlacklistedBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 1:
                for (int yy = y; yy < y + height; yy++)
                    for (int zz = z + depth - 1; zz > z + depth - width - 1; zz--)
                        for (int xx = x; xx < x + depth; xx++) {
                            if (isBlacklistedBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 2:
                for (int yy = y; yy < y + height; yy++)
                    for (int xx = x + width - 1; xx > x + width - width - 1; xx--)
                        for (int zz = z + depth - 1; zz > z + depth - depth - 1; zz--) {
                            if (isBlacklistedBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 3:
                for (int yy = y; yy < y + height; yy++)
                    for (int zz = z; zz < z + width; zz++)
                        for (int xx = x + width - 1; xx > x + width - depth - 1; xx--) {
                            if (isBlacklistedBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
        }
        return true;
    }


    private boolean isBlacklistedBlock(Block block) {
        for (Block blacklistBlock : blackListBlocks)
            if (block.equals(blacklistBlock))
                return true;
        return false;
    }

    private void generateLoot(World world, Random random, int x, int y, int z) {
        int randDirection = random.nextInt(4) + 2;
        world.setBlock(x, y, z, getRandomBlock(random), randDirection, 3);
        TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, z);
        if (lootPot != null)
            LootUtil.generateLoot(lootPot, random, LootBasicList.loot, 1, 2);
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
