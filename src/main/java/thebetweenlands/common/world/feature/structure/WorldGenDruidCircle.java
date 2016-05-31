package thebetweenlands.common.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.config.ConfigHandler;

import java.util.Random;

public class WorldGenDruidCircle implements IWorldGenerator {
    private final int height = 4;
    private final int baseRadius = 6;
    private final int checkRadius = 32;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            generate(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private void generate(World world, Random random, int startX, int startZ) {
        for (int xo = baseRadius + 1; xo <= checkRadius - (baseRadius + 1); xo++) {
            for (int zo = baseRadius + 1; zo <= checkRadius - (baseRadius + 1); zo++) {
                int x = startX + xo;
                int z = startZ + zo;
                Biome biome = world.getBiomeGenForCoords(new BlockPos(x, 0, z));
                int newY = world.getHeight(new BlockPos(x, 0, z)).getY();
                if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SWAMP)) {
                    IBlockState block = world.getBlockState(new BlockPos(x, newY, z));
                    if (block != null && block == biome.topBlock) {
                        if (random.nextInt(ConfigHandler.druidCircleFrequency) == 0) {
                            if (generateStructure(world, random, x, newY + 1, z))
                                return;
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
                    world.setBlockState(new BlockPos(x + newX, y - 1, z + newZ), world.getBiomeGenForCoords(new BlockPos(x, 0, z)).topBlock);
                }
            }
        }

        world.setBlockState(new BlockPos(x, y, z), BlockRegistry.DRUID_ALTAR.getDefaultState());
        world.setBlockState(new BlockPos(x, y - 1, z), BlockRegistry.DRUID_SPAWNER.getDefaultState());

        return true;
    }

    private void placeAir(World world, int x, int y, int z) {
        for (int yNew = y; height + y >= yNew; ++yNew) {
            world.setBlockState(new BlockPos(x, yNew, z), Blocks.AIR.getDefaultState());
        }
    }

    private void placePillar(World world, int x, int y, int z, Random rand) {
        int randHeight = rand.nextInt(3) + 3;
        for (int yy = y; randHeight + y >= yy; ++yy) {
            int randDirection = rand.nextInt(4);
            if (rand.nextBoolean()) {
                world.setBlockState(new BlockPos(x, yy, z), getRandomBlock(rand).getStateFromMeta(randDirection), 3);
            } else {
                world.setBlockState(new BlockPos(x, yy, z), Blocks.STONE.getDefaultState());
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
                    world.setBlockState(new BlockPos(x + 1, y, z), Blocks.VINE.getStateFromMeta(2), 3);
                }
                break;
            case 1:
                if (world.isAirBlock(new BlockPos(x - 1, y, z))) {
                    world.setBlockState(new BlockPos(x - 1, y, z), Blocks.VINE.getStateFromMeta(8), 3);
                }
                break;
            case 2:
                if (world.isAirBlock(new BlockPos(x, y, z + 1))) {
                    world.setBlockState(new BlockPos(x, y, z + 1), Blocks.VINE.getStateFromMeta(4), 3);
                }
                break;
            case 3:
                if (world.isAirBlock(new BlockPos(x, y, z - 1))) {
                    world.setBlockState(new BlockPos(x, y, z - 1), Blocks.VINE.getStateFromMeta(1), 3);
                }
                break;
        }
    }

    private Block getRandomBlock(Random rand) {
        switch (rand.nextInt(5)) {
            case 0:
                return BlockRegistry.DRUID_STONE_1;
            case 1:
                return BlockRegistry.DRUID_STONE_2;
            case 2:
                return BlockRegistry.DRUID_STONE_3;
            case 3:
                return BlockRegistry.DRUID_STONE_4;
            case 4:
                return BlockRegistry.DRUID_STONE_5;
            default:
                return Blocks.STONE;
        }
    }

}