package thebetweenlands.world.biomes;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

import java.util.Random;

public class WorldGenRedirect implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.dimensionId == ConfigHandler.DIMENSION_ID) {
			BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
           	if(biome instanceof BiomeGenBaseBetweenlands) {
           		BiomeGenBaseBetweenlands bgbb = (BiomeGenBaseBetweenlands)biome;
           		bgbb.postChunkGen(world, random, chunkX * 16, chunkZ * 16);
           	}
        }
	}
}
