package thebetweenlands.event.world;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

public class PopulationHandler {
	public static final PopulationHandler INSTANCE = new PopulationHandler();

	private List<Long> generatingChunks = new ArrayList<Long>();

	@SubscribeEvent
	public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
		World world = event.world;
		if(world.provider.dimensionId == ConfigHandler.DIMENSION_ID && !world.isRemote) {
			int recChunkGenLimit = 3;
			long chunkID = ChunkCoordIntPair.chunkXZ2Int(event.chunkX, event.chunkZ);
			for(Long lc : this.generatingChunks) {
				if(lc.longValue() == chunkID) return;
			}
			if(this.generatingChunks.size() >= recChunkGenLimit) return;
			this.generatingChunks.add(chunkID);
			BiomeGenBase biome = world.getBiomeGenForCoords(event.chunkX * 16, event.chunkZ * 16);
			if(biome instanceof BiomeGenBaseBetweenlands) {
				BiomeGenBaseBetweenlands bgbb = (BiomeGenBaseBetweenlands)biome;
				bgbb.postChunkGen(world, world.rand, event.chunkX * 16, event.chunkZ * 16);
			}
			this.generatingChunks.remove(chunkID);
		}
	}

}
