package thebetweenlands.world.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

public class WorldGenRedirect implements IWorldGenerator {
	private List<Long> generatingChunks = new ArrayList<Long>();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.dimensionId == ConfigHandler.DIMENSION_ID) {
			int recChunkGenLimit = 4;
			long chunkID = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
			for(Long lc : this.generatingChunks) {
				if(lc.longValue() == chunkID) return;
			}
			if(this.generatingChunks.size() >= recChunkGenLimit) return;
			this.generatingChunks.add(chunkID);
			BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
			if(biome instanceof BiomeGenBaseBetweenlands) {
				BiomeGenBaseBetweenlands bgbb = (BiomeGenBaseBetweenlands)biome;
				bgbb.postChunkGen(world, random, chunkX * 16, chunkZ * 16);
			}
			this.generatingChunks.remove(chunkID);
		}
	}
}
