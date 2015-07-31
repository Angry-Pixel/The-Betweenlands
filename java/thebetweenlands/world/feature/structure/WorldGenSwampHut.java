package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenSwampHut implements IWorldGenerator {
    private int length = -1;
    private int width = -1;
    private int height = -1;
    private int direction = -1;

    public WorldGenSwampHut() {
        length = 10;
        width = 10;
        height = 10;
        direction = 0;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if(world.provider.dimensionId == ConfigHandler.DIMENSION_ID) {
            generate(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private void generate(World world, Random random, int x, int z) {
        BiomeGenBase biomeBase = world.getBiomeGenForCoords(x, z);
        int newY = 80;
        if(biomeBase == BLBiomeRegistry.marsh1 || biomeBase == BLBiomeRegistry.marsh2) {
            for(int newX = x; newX <= x + 16; ++newX) {
                for(int newZ = z; newZ <= z + 16; ++newZ) {
                    Block block = world.getBlock(newX, newY, newZ);
                    if(block != null && block == biomeBase.topBlock) {
                      //  if(random.nextInt(ConfigHandler.SWAMP_HUT_FREQUENCY) == 0)
                            generateStructure(world, random, x, newY, z);
                    }
                }
            }
        }
    }

    public boolean generateStructure(World world, Random rand, int x, int y, int z) {
        // air check
        for(int newX = x; newX <= x + 16; ++newX) {
            for(int newZ = z; newZ <= z + 16; ++newZ) {
                for(int newY = y + 1; newY < y + height; ++newY ) {
                    if(!world.isAirBlock(newX, newY, newZ)) {
                        return false;
                    }
                }
            }
        }

        //hut generation starts here ;)
       

        System.out.println("Added Hut at: " + x + " " + z);
        return true;
    }
}
