package thebetweenlands.world.biomes.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;

import java.util.Random;

public class WorldGenFluidPool extends WorldGenerator {

    private Block[] blackListBlocks = new Block[]{BLBlockRegistry.betweenstoneTiles, BLBlockRegistry.betweenstoneBrickStairs, BLBlockRegistry.betweenstoneBricks, BLBlockRegistry.betweenstoneBrickSlab};

    private Block fillerFluid = BLBlockRegistry.tarFluid;

    private double size;

    private int minHeight = 6;
    private boolean destroyBlocks = false;

    public void prepare(double size) {
        this.size = size;
    }


    public boolean generatePool(World world, Random rand, int x, int y, int z, Block fluid, boolean destroyBlocks) {
        fillerFluid = fluid;
        this.destroyBlocks = destroyBlocks;
        return generate(world, rand, x, y, z);
    }

    public boolean generatePool(World world, Random rand, int x, int y, int z, Block fluid, int minHeight) {
        fillerFluid = fluid;
        this.minHeight = minHeight;
        return generate(world, rand, x, y, z);
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        x -= 8;
        z -= 8;

        while (world.isAirBlock(x, y, z) && y > minHeight)
            y--;

        if (y <= minHeight || (world.isAirBlock(x, y, z) && !destroyBlocks))
            return false;
        y -= 4;

        boolean[] placeFluid = new boolean[2048];

        int xx;

        for (int iteration = 0, iterAmount = rand.nextInt(3) + 5; iteration < iterAmount; ++iteration) {
            double d0 = (rand.nextDouble() * 6D + 3D) * size * (0.4D + rand.nextDouble() * 0.6D);
            double d1 = (rand.nextDouble() * 4D + 2D) * size / 2.5D;
            double d2 = (rand.nextDouble() * 6D + 3D) * size * (0.4D + rand.nextDouble() * 0.6D);
            double d3 = rand.nextDouble() * (16D - d0 - 2D) + 1D + d0 / 2D;
            double d4 = rand.nextDouble() * (8D - d1 - 4D) + 2D + d1 / 2D;
            double d5 = rand.nextDouble() * (16D - d2 - 2D) + 1D + d2 / 2D;

            for (xx = 1; xx < 15; ++xx)
                for (int zz = 1; zz < 15; ++zz)
                    for (int yy = 1; yy < 7; ++yy) {
                        double d6 = (xx - d3) / (d0 / 2.0D);
                        double d7 = (yy - d4) / (d1 / 2.0D);
                        double d8 = (zz - d5) / (d2 / 2.0D);
                        double dist = d6 * d6 + d7 * d7 + d8 * d8;

                        if (dist < 1D)
                            placeFluid[(xx * 16 + zz) * 8 + yy] = true;
                    }
        }

        int yy;
        int zz;
        boolean flag;

        for (xx = 0; xx < 16; ++xx)
            for (zz = 0; zz < 16; ++zz)
                for (yy = 0; yy < 8; ++yy) {
                    flag = !placeFluid[(xx * 16 + zz) * 8 + yy] && (xx < 15 && placeFluid[((xx + 1) * 16 + zz) * 8 + yy] || xx > 0 && placeFluid[((xx - 1) * 16 + zz) * 8 + yy] || zz < 15 && placeFluid[(xx * 16 + zz + 1) * 8 + yy] || zz > 0 && placeFluid[(xx * 16 + zz - 1) * 8 + yy] || yy < 7 && placeFluid[(xx * 16 + zz) * 8 + yy + 1] || yy > 0 && placeFluid[(xx * 16 + zz) * 8 + yy - 1]);

                    if (flag) {
                        Material material = world.getBlock(x + xx, y + yy, z + zz).getMaterial();

                        if (yy >= 4 && material.isLiquid())
                            return false;

                        if (yy < 4 && !material.isSolid() && world.getBlock(x + xx, y + yy, z + zz) != fillerFluid)
                            return false;

                        //	if (yy < 4 || rand.nextBoolean() && world.getBlock(x + xx, y + yy, z + zz).getMaterial().isSolid() && (checkSurface(world, SurfaceType.MIXED, x + xx, y + yy, z + zz) || world.getBlock(x + xx, y + yy, z + zz) == BLBlockRegistry.betweenstone)) //this is a problem it needs a better block check
                        //		world.setBlock(x + xx, y + yy, z + zz, BLBlockRegistry.solidTar, 0, 2);
                    }
                }
        for (xx = 0; xx < 16; ++xx)
            for (zz = 0; zz < 16; ++zz)
                for (yy = 0; yy < 8; ++yy)
                    if (isBlacklistedBlock(world.getBlock(x, y, z)))
                        return false;

        for (xx = 0; xx < 16; ++xx)
            for (zz = 0; zz < 16; ++zz)
                for (yy = 0; yy < 8; ++yy)
                    if (placeFluid[(xx * 16 + zz) * 8 + yy])
                        world.setBlock(x + xx, y + yy, z + zz, yy >= 4 ? Blocks.air : fillerFluid, 0, 2);

        return true;
    }

    private final boolean checkSurface(World world, SurfaceType surfaceType, int x, int y, int z) {
        return surfaceType.matchBlock(world.getBlock(x, y, z));
    }

    private boolean isBlacklistedBlock(Block block) {
        for (Block blacklistBlock : blackListBlocks)
            if (block.equals(blacklistBlock))
                return true;
        return false;
    }
}
