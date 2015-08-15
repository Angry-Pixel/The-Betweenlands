package thebetweenlands.event.world;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.world.WorldProviderBetweenlands;

public class BloodSkyHandler {
	public static final BloodSkyHandler INSTANCE = new BloodSkyHandler();

	private final Map<SpawnListEntry, Entry<Integer, Integer>> spawnEntries = new HashMap<SpawnListEntry, Entry<Integer, Integer>>();

	public BloodSkyHandler() {
		this.spawnEntries.put(new SpawnListEntry(EntitySwampHag.class, 200, 1, 1), new AbstractMap.SimpleEntry(2, 5));
	}

	@SubscribeEvent
	public void onGetPotentialSpawns(PotentialSpawns event) {
		WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(event.world);
		if(provider != null) {
			for(SpawnListEntry spawnEntry : this.spawnEntries.keySet()) {
				if(event.list.contains(spawnEntry)) {
					while(true){
						if(!event.list.remove(spawnEntry)) {
							break;
						}
					}
				}
			}
			boolean canSpawnEntities = event.y >= WorldProviderBetweenlands.LAYER_HEIGHT - 4 && event.type == EnumCreatureType.ambient && event.world.difficultySetting != EnumDifficulty.PEACEFUL && event.world.rand.nextInt(50) == 0;
			if(!canSpawnEntities) {
				return;
			}
			if(provider.getEnvironmentEventRegistry().BLOODSKY.isActive()) {
				Chunk centerChunk = event.world.getChunkFromBlockCoords(event.x, event.z);
				Chunk[] neighbourChunks = new Chunk[] {
						centerChunk,
						event.world.getChunkFromChunkCoords(centerChunk.xPosition + 1, centerChunk.zPosition),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition - 1, centerChunk.zPosition),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition, centerChunk.zPosition + 1),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition, centerChunk.zPosition - 1),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition + 1, centerChunk.zPosition + 1),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition - 1, centerChunk.zPosition + 1),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition + 1, centerChunk.zPosition - 1),
						event.world.getChunkFromChunkCoords(centerChunk.xPosition - 1, centerChunk.zPosition - 1),
				};
				for(Entry<SpawnListEntry, Entry<Integer, Integer>> se : this.spawnEntries.entrySet()) {
					int maxEntities = se.getValue().getKey();
					int maxEntitiesChunk = se.getValue().getValue();
					SpawnListEntry spawnEntry = se.getKey();
					int subChunkY = MathHelper.floor_double(event.y / 16.0D);
					int entityCount = 0;
					int chunkEntityCount = 0;
					for(Chunk chunk : neighbourChunks) {
						for(int i = 0; i < chunk.entityLists.length; i++) {
							List<Entity> entityList = chunk.entityLists[subChunkY];
							for(Entity entity : entityList) {
								if(entity.getClass() == spawnEntry.entityClass) {
									if(i == subChunkY) {
										entityCount++;
									}
									chunkEntityCount++;
								}
							}
						}
					}
					boolean canSpawn = chunkEntityCount < maxEntitiesChunk && subChunkY < centerChunk.entityLists.length * 16 && entityCount < maxEntities;
					if(canSpawn) {
						if(!event.list.contains(spawnEntry)) {
							event.list.add(spawnEntry);
						}
					}
				}
			}
		}
	}
}
