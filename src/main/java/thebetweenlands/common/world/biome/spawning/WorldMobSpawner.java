package thebetweenlands.common.world.biome.spawning;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class WorldMobSpawner extends AreaMobSpawner {
	public static final WorldMobSpawner INSTANCE = new WorldMobSpawner();

	//How many times a chunk should be populated with mobs when it generates
	private static final int CHUNK_GEN_SPAWN_RUNS = 10;

	//Maximum distance from the player where mobs spawn
	private static final int SPAWN_CHUNK_MAX_RANGE = 8;
	//Minimum chunk distance from player where mobs spawn (exclusive)
	private static final int SPAWN_CHUNK_MIN_RANGE = 1;
	//Number of spawn chunks in one fully loaded area
	private static final int MAX_SPAWN_CHUNKS_PER_AREA = (SPAWN_CHUNK_MAX_RANGE * 2 + 1)*(SPAWN_CHUNK_MAX_RANGE * 2 + 1) - (SPAWN_CHUNK_MIN_RANGE * 2 + 1) * (SPAWN_CHUNK_MIN_RANGE * 2 + 1);

	public WorldMobSpawner() {
		this.setStrictDynamicLimit(false);
	}
	
	@Override
	public float getMaxEntitiesPerSpawnChunkFraction(int spawnerChunks) {
		return (float)BetweenlandsConfig.MOB_SPAWNING.maxEntitiesPerLoadedArea / (float)MAX_SPAWN_CHUNKS_PER_AREA;
	}

	@Override
	public float getLoadedAreasCount(int spawnerChunks) {
		return spawnerChunks / (float)MAX_SPAWN_CHUNKS_PER_AREA;
	}

	@Override
	protected void updateSpawnerChunks(WorldServer world, Set<ChunkPos> spawnerChunks) {
		spawnerChunks.clear();

		Map<ChunkPos, Boolean> eligibleChunks = new HashMap<>();

		for (EntityPlayer entityplayer : world.playerEntities) {
			if (!entityplayer.isSpectator()) {
				int cx = MathHelper.floor(entityplayer.posX / 16.0D);
				int cz = MathHelper.floor(entityplayer.posZ / 16.0D);

				for (int xo = -SPAWN_CHUNK_MAX_RANGE; xo <= SPAWN_CHUNK_MAX_RANGE; ++xo) {
					for (int zo = -SPAWN_CHUNK_MAX_RANGE; zo <= SPAWN_CHUNK_MAX_RANGE; ++zo) {
						boolean isBorder = Math.abs(xo) > SPAWN_CHUNK_MIN_RANGE || Math.abs(zo) > SPAWN_CHUNK_MIN_RANGE;
						ChunkPos chunkpos = new ChunkPos(xo + cx, zo + cz);

						if (world.getWorldBorder().contains(chunkpos)) {
							PlayerChunkMapEntry playerchunkmapentry = world.getPlayerChunkMap().getEntry(chunkpos.x, chunkpos.z);

							if (playerchunkmapentry != null && playerchunkmapentry.isSentToPlayers()) {
								if(!isBorder) {
									eligibleChunks.put(chunkpos, false);
								} else {
									if(!eligibleChunks.containsKey(chunkpos)) {
										eligibleChunks.put(chunkpos, true);
									}
								}
							}
						}
					}
				}
			}
		}

		for(Entry<ChunkPos, Boolean> entry : eligibleChunks.entrySet()) {
			if(entry.getValue()) {
				spawnerChunks.add(entry.getKey());
			}
		}			
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			WorldServer world = DimensionManager.getWorld(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
			if(world == null || world.playerEntities.isEmpty())
				return;

			if(world.getGameRules().getBoolean("doMobSpawning") && world.getTotalWorldTime() % 4 == 0) {
				boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
				boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

				//long start = System.nanoTime();
				this.populate(world, spawnHostiles, spawnAnimals);
				//System.out.println("Time: " + (System.nanoTime() - start) / 1000000.0F);
			}
		}
	}

	public void populateChunk(WorldServer world, int chunkX, int chunkZ) {
		if(world == null || world.provider.getDimension() != BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId)
			return;

		if(world.getGameRules().getBoolean("doMobSpawning")) {
			boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
			boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

			//long start = System.nanoTime();
			int spawnedEntities = 0;
			spawnedEntities += this.populateChunk(world, new ChunkPos(chunkX, chunkZ), spawnHostiles, spawnAnimals, false, true,
					this.getSpawningAttempsPerChunk() * CHUNK_GEN_SPAWN_RUNS, 60, this.getSpawningAttemptsPerGroup(), this.getHardEntityLimit(), 1.0F);
			//System.out.println("Spawned: " + spawnedEntities + " Time: " + (System.nanoTime() - start) / 1000000.0F);
		}
	}
}
