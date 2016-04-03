package thebetweenlands.common.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import thebetweenlands.util.config.ConfigHandler;

import java.util.Random;

public class WorldGenDruidCircle implements IWorldGenerator {
    private int height = -1;
    private int baseRadius = -1;

    public WorldGenDruidCircle() {
        height = 4;
        baseRadius = 6;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            generate(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private void generate(World world, Random random, int x, int z) {
        BiomeGenBase biomeBase = world.getBiomeGenForCoords(new BlockPos(x, 1, z));
        int newY = 63;
        if (biomeBase == Biomes.swampland) {
            for (int newX = x - baseRadius; newX <= x + baseRadius; ++newX) {
                for (int newZ = z - baseRadius; newZ <= z + baseRadius; ++newZ) {
                    Block block = world.getBlockState(new BlockPos(newX, newY, newZ)).getBlock();
                    if (block != null && block == biomeBase.topBlock) {
                        if (random.nextInt(ConfigHandler.druidCircleFrequency) == 0) { // this may need to change ie. give it a config option
                            generateStructure(world, random, x, newY, z);
                        }
                    }
                }
            }
        }
    }

    public boolean generateStructure(World world, Random rand, int x, int y, int z) {
        // air check
        for (int newX = x - baseRadius; newX <= x + baseRadius; ++newX) {
            for (int newZ = z - baseRadius; newZ <= z + baseRadius; ++newZ) {
                for (int newY = y + 1; newY < y + height; ++newY) {
                    if (!world.isAirBlock(new BlockPos(newX, newY, newZ))) {
                        return false;
                    }
                }
            }
        }

        //circle
        for (int newX = baseRadius * -1; newX <= baseRadius; ++newX) {
            for (int newZ = baseRadius * -1; newZ <= baseRadius; ++newZ) {
                double dSq = newX * newX + newZ * newZ;
                if (Math.round(Math.sqrt(dSq)) == baseRadius && newX % 2 == 0 && newZ % 2 == 0) {
                    placePillar(world, x + newX, y, z + newZ, rand);
                } else if (Math.round(Math.sqrt(dSq)) == baseRadius && newX % 2 != 0 && newZ % 2 != 0) {
                    placeAir(world, x + newX, y, z + newZ);
                }

                if (Math.round(Math.sqrt(dSq)) <= baseRadius) {
                    world.setBlockState(new BlockPos(x + newX, y - 1, z + newZ), Blocks.grass.getDefaultState());
                }
            }
        }

        //world.setBlockState(new BlockPos(x, y, z), BlockRegistry.druidAltar.getDefaultState());
        //world.setBlockState(new BlockPos(x, y - 1, z), BlockRegistry.druidSpawner.getDefaultState());

        return true;
    }

    private void placeAir(World world, int x, int y, int z) {
        for (int yNew = y; height + y >= yNew; ++yNew) {
            world.setBlockState(new BlockPos(x, yNew, z), Blocks.air.getDefaultState());
        }
    }

    private void placePillar(World world, int x, int y, int z, Random rand) {
        int randHeight = rand.nextInt(3) + 3;
        for (int yy = y; randHeight + y >= yy; ++yy) {
            int randDirection = rand.nextInt(4);
            if (rand.nextBoolean()) {
                world.setBlockState(new BlockPos(x, yy, z), getRandomBlock(rand).getStateFromMeta(randDirection), 3);
            } else {
                world.setBlockState(new BlockPos(x, yy, z), Blocks.stone.getDefaultState());
                for (int vineCount = 0; vineCount < 4; vineCount++) {
                    setRandomFoliage(world, x, yy, z, rand);
                }
            }
        }
    }

    private void setRandomFoliage(World world, int x, int y, int z, Random rand) {
        switch (rand.nextInt(4)) { // get random side
            case 0:
                if (world.isAirBlock(new BlockPos(x + 1, y, z))) {
                    world.setBlockState(new BlockPos(x + 1, y, z), Blocks.vine.getStateFromMeta(2), 3);
                }
                break;
            case 1:
                if (world.isAirBlock(new BlockPos(x - 1, y, z))) {
                    world.setBlockState(new BlockPos(x - 1, y, z), Blocks.vine.getStateFromMeta(8), 3);
                }
                break;
            case 2:
                if (world.isAirBlock(new BlockPos(x, y, z + 1))) {
                    world.setBlockState(new BlockPos(x, y, z + 1), Blocks.vine.getStateFromMeta(4), 3);
                }
                break;
            case 3:
                if (world.isAirBlock(new BlockPos(x, y, z - 1))) {
                    world.setBlockState(new BlockPos(x, y, z - 1), Blocks.vine.getStateFromMeta(1), 3);
                }
                break;
        }
    }

    private Block getRandomBlock(Random rand) {
        switch (rand.nextInt(5)) {
            /*case 0:
                return BlockRegistry.druidStone1;
            case 1:
                return BlockRegistry.druidStone2;
            case 2:
                return BlockRegistry.druidStone3;
            case 3:
                return BlockRegistry.druidStone4;
            case 4:
                return BlockRegistry.druidStone5;*/
            default:
                return Blocks.stone;
        }
    }

}