package thebetweenlands.event.world;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.world.WorldProviderBetweenlands;

public class EntitySpawnHandler {
	public abstract class EntitySpawnEntry {
		public final SpawnListEntry spawnEntry;
		public final int maxChunkCount, maxNeighbourChunkCount;

		public EntitySpawnEntry(SpawnListEntry spawnEntry, int maxChunkCount, int maxNeighbourChunkCount) { 
			this.spawnEntry = spawnEntry; 
			this.maxChunkCount = maxChunkCount;
			this.maxNeighbourChunkCount = maxNeighbourChunkCount;
		}

		public abstract boolean isActive(World world, WorldProviderBetweenlands provider);
	}

	public static final EntitySpawnHandler INSTANCE = new EntitySpawnHandler();
	private final List<EntitySpawnEntry> spawnEntryList = new ArrayList<EntitySpawnEntry>();

	public void registerSpawn(EntitySpawnEntry entry) {
		this.spawnEntryList.add(entry);
	}

	public EntitySpawnHandler() {
		//Increased swamp hag spawning during Blood Sky
		this.registerSpawn(new EntitySpawnEntry(new SpawnListEntry(EntitySwampHag.class, 200, 1, 1), 2, 5) {
			@Override
			public boolean isActive(World world, WorldProviderBetweenlands provider) {
				return provider.getEnvironmentEventRegistry().BLOODSKY.isActive();
			}
		});

		//Increased fire fly spawning during Spoopy event
		this.registerSpawn(new EntitySpawnEntry(new SpawnListEntry(EntityFirefly.class, 800, 1, 4), 1, 6) {
			@Override
			public boolean isActive(World world, WorldProviderBetweenlands provider) {
				return provider.getEnvironmentEventRegistry().SPOOPY.isActive();
			}
		});
	}

	@SubscribeEvent
	public void onGetPotentialSpawns(PotentialSpawns event) {
		WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(event.world);
		if(provider != null) {
			for(EntitySpawnEntry spawnEntry : this.spawnEntryList) {
				if(event.list.contains(spawnEntry.spawnEntry)) {
					while(true){
						if(!event.list.remove(spawnEntry.spawnEntry)) {
							break;
						}
					}
				}
			}
			boolean canSpawnEntities = event.y >= WorldProviderBetweenlands.LAYER_HEIGHT - 4 && event.type == EnumCreatureType.ambient && event.world.difficultySetting != EnumDifficulty.PEACEFUL && event.world.rand.nextInt(50) == 0;
			if(!canSpawnEntities) {
				return;
			}
			boolean anyActive = false;
			for(EntitySpawnEntry ese : this.spawnEntryList) {
				if(ese.isActive(event.world, provider)) {
					anyActive = true;
					break;
				}
			}
			if(!anyActive) {
				return;
			}
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
			for(EntitySpawnEntry ese : this.spawnEntryList) {
				if(!ese.isActive(event.world, provider)) continue;
				int maxEntities = ese.maxChunkCount;
				int maxEntitiesChunk = ese.maxNeighbourChunkCount;
				SpawnListEntry spawnEntry = ese.spawnEntry;
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
