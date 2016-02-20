package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.loot.LootBasicList;
import thebetweenlands.world.loot.LootUtil;

import java.util.Random;

/**
 * Created by Bart on 19/02/2016.
 */
public class WorldGenUnderGroundStructures extends WorldGenerator {
    private static boolean markReplaceableCheck = false;
    private int width = -1;
    private int depth = -1;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        boolean shouldStop = true;
        while (y > WorldProviderBetweenlands.CAVE_WATER_HEIGHT) {
            if (world.getBlock(x, y - 1, z) == BLBlockRegistry.pitstone && world.getBlock(x, y, z) == Blocks.air) {
                shouldStop = false;
                break;
            }
            y--;
        }
        if (shouldStop)
            return false;

        int randomInt = random.nextInt(5);
        switch (randomInt) {
            case 0:
                return structure1(world, random, x, y, z);
            case 1:
                return structure2(world, random, x, y, z);
            case 2:
                return structure3(world, random, x, y, z);
            case 3:
                return structure4(world, random, x, y, z);
            case 4:
                return structure5(world, random, x, y, z);
            default:
                return false;
        }
    }


    public boolean structure1(World world, Random random, int x, int y, int z) {
        width = 7;
        depth = 6;
        int direction = random.nextInt(4);
        if (!rotatedCubeCanReplace(world, x, y, z, 0, 0, 0, width, 4, depth, direction))
            return false;
        if (!rotatedCubeBlockCheck(world, x, y, z, 0, -1, 0, BLBlockRegistry.pitstone, 0, width, 1, depth, direction))
            return false;

        rotatedCubeVolume(world, random, x, y, z, 0, 0, 0, BLBlockRegistry.pitstoneTiles, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 0, BLBlockRegistry.pitstoneTiles, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 0, 0, BLBlockRegistry.pitstoneTiles, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 0, BLBlockRegistry.pitstoneTiles, 0, 2, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 5, BLBlockRegistry.pitstoneTiles, 0, 5, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 0, 1, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 5, BLBlockRegistry.pitstoneBricks, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1 + random.nextInt(5), 1, 5, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 2 + random.nextInt(2), Blocks.air, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 6, 2, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 2, 5, BLBlockRegistry.pitstoneBricks, 0, 7, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2 + random.nextInt(4), 2, 5, Blocks.air, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 2, 3, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 2, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 2, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 0, 3, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 2, direction);

        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 1, 0, 1, direction);
        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 5, 0, 1, direction);
        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 1, 0, 4, direction);
        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 5, 0, 4, direction);
        System.out.println("generated at: " + x + " " + y + " " + z);
        return true;
    }


    public boolean structure2(World world, Random random, int x, int y, int z) {
        width = 9;
        depth = 11;
        int direction = random.nextInt(4);
        int height = 6 + random.nextInt(2);
        if (!rotatedCubeCanReplace(world, x, y, z, 4, 0, 0, 5, height, depth, direction) || !rotatedCubeCanReplace(world, x, y, z, 0, 0, 6, 4, height, 5, direction))
            return false;
        if (!rotatedCubeBlockCheck(world, x, y, z, 0, -1, 6, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 0, -1, 10, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 4, -1, 6, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 4, -1, 10, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 4, -1, 2, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 8, -1, 6, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 8, -1, 10, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction)
                || !rotatedCubeBlockCheck(world, x, y, z, 8, -1, 2, BLBlockRegistry.pitstone, 0, 1, 1, 1, direction))
            return false;

        rotatedCubeVolume(world, random, x, y, z, 0, 0, 6, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 0, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 0, 6, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 0, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 0, 2, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, 0, 6, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, 0, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, 0, 2, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 0, 1, 6, BLBlockRegistry.pitstonePillar, 0, 1, 2 + random.nextInt(3), 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 1, 10, BLBlockRegistry.pitstonePillar, 0, 1, random.nextInt(3), 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 1, 6, BLBlockRegistry.pitstonePillar, 0, 1, height - 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 1, 10, BLBlockRegistry.pitstonePillar, 0, 1, height - 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 1, 2, BLBlockRegistry.pitstonePillar, 0, 1, height - 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, 1, 6, BLBlockRegistry.pitstonePillar, 0, 1, height - 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, 1, 10, BLBlockRegistry.pitstonePillar, 0, 1, height - 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, 1, 2, BLBlockRegistry.pitstonePillar, 0, 1, height - 3, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, height - 2, 10, BLBlockRegistry.pitstoneBricks, 0, 8, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, height - 2, 9, BLBlockRegistry.pitstoneTiles, 0, 6, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, height - 2, 8, BLBlockRegistry.pitstoneTiles, 0, 5, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, height - 2, 7, BLBlockRegistry.pitstoneTiles, 0, 6, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, height - 2, 6, BLBlockRegistry.pitstoneBricks, 0, 4, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 8, height - 2, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 10, direction);
        rotatedCubeVolume(world, random, x, y, z, 7, height - 2, 1, BLBlockRegistry.pitstoneTiles, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, height - 2, 2, BLBlockRegistry.pitstoneTiles, 0, 1, 1, 5, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, height - 2, 3, BLBlockRegistry.pitstoneTiles, 0, 1, 1, 4, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, height - 2, 2, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 4, direction);

        rotatedCubeVolume(world, random, x, y, z, 8, height - 1, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, height - 1, 1, BLBlockRegistry.pitstoneBrickWall, 0, 1, 1, 9, direction);
        rotatedCubeVolume(world, random, x, y, z, 8, height - 1, 2 + random.nextInt(7), Blocks.air, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, height - 1, 2, BLBlockRegistry.pitstoneBrickWall, 0, 1, 1, 4, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, height - 1, 3 + random.nextInt(2), Blocks.air, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, height - 1, 6, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, height - 1, 10, BLBlockRegistry.pitstoneBrickWall, 0, 6, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 3 + random.nextInt(4), height - 1, 10, Blocks.air, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, height - 1, 6, BLBlockRegistry.pitstoneBrickWall, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, height - 1, 6, BLBlockRegistry.pitstoneBrickWall, 0, 1, 1, 1, direction);
        if (random.nextBoolean())
            rotatedCubeVolume(world, random, x, y, z, 8, height, 10, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);


        if (random.nextInt(3) == 0)
            rotatedLoot(world, random, x, y, z, 7, height - 1, 9, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 5, height - 1, 4, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 1, 0, 7, direction);
        if (random.nextInt(5) == 0)
            rotatedLoot(world, random, x, y, z, 7, 0, 9, direction);
        System.out.println("generated at: " + x + " " + y + " " + z);
        return true;
    }

    public boolean structure3(World world, Random random, int x, int y, int z) {
        width = 6;
        depth = 6;
        int direction = 0;
        if (!rotatedCubeCanReplace(world, x, y, z, 0, 0, 0, width, 7, depth, direction))
            return false;
        if (!rotatedCubeBlockCheck(world, x, y, z, 0, -1, 0, BLBlockRegistry.pitstone, 0, width, 1, depth, direction))
            return false;

        rotatedCubeVolume(world, random, x, y, z, 1, 0, 1, BLBlockRegistry.pitstoneTiles, 0, 4, 1, 4, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 0, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 0, 5, BLBlockRegistry.pitstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 0, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 0, 5, BLBlockRegistry.pitstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 0, BLBlockRegistry.pitstoneBrickStairs, 2, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 0, 1, BLBlockRegistry.pitstoneBrickStairs, 0, 1, 1, 4, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 5, BLBlockRegistry.pitstoneBrickStairs, 3, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 0, 1, BLBlockRegistry.pitstoneBrickStairs, 1, 1, 1, 4, direction);

        rotatedCubeVolume(world, random, x, y, z, 0, 2, 0, BLBlockRegistry.pitstonePillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 2, 5, BLBlockRegistry.pitstonePillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 2, 0, BLBlockRegistry.pitstonePillar, 0, 1, 3, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 2, 5, BLBlockRegistry.pitstonePillar, 0, 1, 3, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 0, 5, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 5, 5, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 5, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 5, 5, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 0, BLBlockRegistry.pitstoneBrickSlab, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 5, 1, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 4, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 5, BLBlockRegistry.pitstoneBrickSlab, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 5, 1, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 4, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 6, 0, BLBlockRegistry.pitstoneBrickSlab, 0, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 6, 1, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 4, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 6, 5, BLBlockRegistry.pitstoneBrickSlab, 0, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 6, 1, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 4, direction);

        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 1, 1, 1, 0);
        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 4, 1, 1, 0);
        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 1, 1, 4, 0);
        if (random.nextInt(4) == 0)
            rotatedLoot(world, random, x, y, z, 4, 1, 4, 0);
        System.out.println("generated at: " + x + " " + y + " " + z);
        return true;
    }

    public boolean structure4(World world, Random random, int x, int y, int z) {
        depth = 11;
        width = 5;
        int direction = random.nextInt(4);
        if (!rotatedCubeCanReplace(world, x, y, z, 0, 0, 0, width, 4, depth, direction))
            return false;
        if (!rotatedCubeBlockCheck(world, x, y, z, 0, -1, 0, BLBlockRegistry.pitstone, 0, width, 1, depth, direction))
            return false;

        rotatedCubeVolume(world, random, x, y, z, 0, 0, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 11, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, 0, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, 0, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 0, 2, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 9, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 0, 1, 2, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 9, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 1, 3, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 8, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 5, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 5, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, 1, 5, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 5, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 1, 10, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 2, 5, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 5, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, 2, 5, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 5, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 2, 3, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 8, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 2, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 7, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 2, 10, BLBlockRegistry.pitstoneBricks, 0, 1, 2, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 2, 4, BLBlockRegistry.pitstoneBrickStairs, 6, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 3, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 3, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 6, direction);
        rotatedCubeVolume(world, random, x, y, z, 3, 3, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 6, direction);

        for (int i = 0; i <= 4; i++) {
            if (random.nextInt(4) == 0)
                rotatedLoot(world, random, x, y, z, 1, 0, 5 + i, direction);
            if (random.nextInt(4) == 0)
                rotatedLoot(world, random, x, y, z, 3, 0, 5 + i, direction);
        }
        System.out.println("generated at: " + x + " " + y + " " + z);
        return true;
    }

    public boolean structure5(World world, Random random, int x, int y, int z) {
        depth = 13;
        width = 8;
        int direction = 0;//random.nextInt(4);
        if (!rotatedCubeCanReplace(world, x, y, z, 0, 0, 0, width, 8, depth, direction))
            return false;
        if (!rotatedCubeBlockCheck(world, x, y, z, 0, -1, 0, BLBlockRegistry.pitstone, 0, width, 1, depth, direction))
            return false;


        rotatedCubeVolume(world, random, x, y, z, 1, 0, 1, BLBlockRegistry.pitstoneBrickStairs, 0, 1, 1, 12, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 0, BLBlockRegistry.pitstoneBrickStairs, 2, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 12, BLBlockRegistry.pitstoneBrickStairs, 3, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 0, 1, BLBlockRegistry.pitstoneTiles, 0, 4, 1, 11, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 1, BLBlockRegistry.pitstoneBrickStairs, 1, 1, 1, 12, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 0, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 4, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 8, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 0, 12, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 0, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 4, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 8, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 0, 12, BLBlockRegistry.chiseledPitstone, 0, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 1, 0, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 4, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 8, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 1, 12, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 0, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 4, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 8, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 1, 12, BLBlockRegistry.pitstonePillar, 0, 1, 4, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 6, 5, 0, BLBlockRegistry.pitstoneBrickSlab, 8, 2, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 0, 5, 0, BLBlockRegistry.pitstoneBrickSlab, 8, 2, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 8, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 1, 5, 12, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 0, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 4, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 8, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 5, 12, BLBlockRegistry.pitstoneBricks, 0, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 0, BLBlockRegistry.pitstoneBrickSlab, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 4, BLBlockRegistry.pitstoneBrickSlab, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 8, BLBlockRegistry.pitstoneBrickSlab, 8, 4, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 5, 12, BLBlockRegistry.pitstoneBrickSlab, 8, 4, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 1, 6, 0, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 6, 6, 0, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 6, 1, BLBlockRegistry.pitstoneBricks, 0, 4, 1, 11, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 6, 0, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 6, 0, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 2, 6, 12, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 1, direction);
        rotatedCubeVolume(world, random, x, y, z, 5, 6, 12, BLBlockRegistry.pitstoneBrickSlab, 8, 1, 1, 1, direction);

        rotatedCubeVolume(world, random, x, y, z, 3, 7, 0, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 13, direction);
        rotatedCubeVolume(world, random, x, y, z, 4, 7, 0, BLBlockRegistry.pitstoneBrickSlab, 0, 1, 1, 13, direction);

        if (random.nextInt(2) == 0)
            rotatedLoot(world, random, x, y, z, 5, 1, 2, 0);
        if (random.nextInt(2) == 0)
            rotatedLoot(world, random, x, y, z, 2, 1, 6, 0);
        if (random.nextInt(2) == 0)
            rotatedLoot(world, random, x, y, z, 5, 1, 10, 0);
        System.out.println("generated at: " + x + " " + y + " " + z);
        return true;
    }


    public void rotatedCubeVolume(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
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
                for (int yy = y + offsetB; yy < y + offsetB + 1; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + 1; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + 1; zz++)
                            generateLoot(world, rand, xx, yy, zz);
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + 1; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - 2; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + 1; xx++)
                            generateLoot(world, rand, xx, yy, zz);
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + 1; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - 2; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - 2; zz--)
                            generateLoot(world, rand, xx, yy, zz);
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + 1; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + 1; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - 2; xx--)
                            generateLoot(world, rand, xx, yy, zz);
                break;
        }
    }


    public boolean rotatedCubeBlockCheck(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        x -= width / 2;
        z -= depth / 2;
        boolean isSame = true;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++)
                            if (world.getBlock(xx, yy, zz) != blockType)
                                isSame = false;
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++)
                            if (world.getBlock(xx, yy, zz) != blockType)
                                isSame = false;
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--)
                            if (world.getBlock(xx, yy, zz) != blockType)
                                isSame = false;
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--)
                            if (world.getBlock(xx, yy, zz) != blockType)
                                isSame = false;
                break;
        }
        return isSame;
    }

    public boolean rotatedCubeCanReplace(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
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
        return replaceable;
    }

    private void generateLoot(World world, Random random, int x, int y, int z) {
        if (world.getBlock(x, y - 1, z) == BLBlockRegistry.pitstone) {
            int randDirection = random.nextInt(4) + 2;
            world.setBlock(x, y, z, getRandomBlock(random), randDirection, 3);
            TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, z);
            if (lootPot != null)
                LootUtil.generateLoot(lootPot, random, LootBasicList.loot, 1, 2);
        }
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
