package thebetweenlands.world.feature.plants;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

import java.util.Random;

import static net.minecraftforge.common.util.ForgeDirection.*;

public class WorldGenMossPatch extends WorldGenerator {
    boolean blockPlaced = false;
    private int mossType = -1;

    public WorldGenMossPatch(int type) {
        super(true);
        mossType = type;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        placeBlockAt(world, rand, x, y, z);
        if (blockPlaced)
            createPatch(world, rand, x, y, z);
        return true;
    }

    private void placeBlockAt(World world, Random rand, int x, int y, int z) {
        int offset = 1;
        int metaMapped = 0;
        int randomiseSide = rand.nextInt(6);
        if (mossType == 1)
            metaMapped += 6;
        switch (randomiseSide) {
            case 0:
                if (world.isSideSolid(x, y + offset, z, DOWN) && isValidBlock(world, x, y + offset, z)) {
                    world.setBlock(x, y, z, BLBlockRegistry.wallPlants, metaMapped + 2, 2);
                    blockPlaced = true;
                }
                break;
            case 1:
                if (world.isSideSolid(x, y - offset, z, UP) && isValidBlock(world, x, y - offset, z)) {
                    world.setBlock(x, y, z, BLBlockRegistry.wallPlants, metaMapped + 3, 2);
                    blockPlaced = true;
                }
                break;
            case 2:
                if (world.isSideSolid(x, y, z + offset, NORTH) && isValidBlock(world, x, y, z + offset)) {
                    world.setBlock(x, y, z, BLBlockRegistry.wallPlants, metaMapped + 4, 2);
                    blockPlaced = true;
                }
                break;
            case 3:
                if (world.isSideSolid(x, y, z - offset, SOUTH) && isValidBlock(world, x, y, z - offset)) {
                    world.setBlock(x, y, z, BLBlockRegistry.wallPlants, metaMapped + 5, 2);
                    blockPlaced = true;
                }
                break;
            case 4:
                if (world.isSideSolid(x + offset, y, z, WEST) && isValidBlock(world, x + offset, y, z)) {
                    world.setBlock(x, y, z, BLBlockRegistry.wallPlants, metaMapped + 6, 2);
                    blockPlaced = true;
                }
                break;
            case 5:
                if (world.isSideSolid(x - offset, y, z, EAST) && isValidBlock(world, x - offset, y, z)) {
                    world.setBlock(x, y, z, BLBlockRegistry.wallPlants, metaMapped + 7, 2);
                    blockPlaced = true;
                }
                break;
            default:
                blockPlaced = false;
                break;
        }
    }

    private void createPatch(World world, Random rand, int x, int y, int z) {
        byte radius = 2;
        for (int xx = x - radius; xx <= x + radius; ++xx)
            for (int zz = z - radius; zz <= z + radius; ++zz)
                for (int yy = y - radius; yy <= y + radius; ++yy)
                    if (world.isAirBlock(xx, yy, zz))
                        for (int attempt = 0; attempt < 3; attempt++)
                            placeBlockAt(world, rand, xx, yy, zz);
    }

    private boolean isValidBlock(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isNormalCube();
    }

}