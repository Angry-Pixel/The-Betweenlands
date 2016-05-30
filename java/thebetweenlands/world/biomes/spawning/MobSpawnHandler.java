package thebetweenlands.world.biomes.spawning;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import thebetweenlands.utils.IWeightProvider;
import thebetweenlands.utils.WeightedList;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

public class MobSpawnHandler {
	public static MobSpawnHandler INSTANCE = new MobSpawnHandler();

	//How many times a chunk should be populated with mobs when it generates
	private static final int CHUNK_GEN_SPAWN_RUNS = 64;

	//Distance from the player where mobs spawn (middle of the spawner chunk rim)
	private static final byte SPAWN_CHUNK_DISTANCE = 4;
	//Size of the chunk rim where mobs can spawn (SPAWN_CHUNK_DISTANCE +- SPAWN_CHUNK_RIM)
	private static final byte SPAWN_CHUNK_RIM = 2;

	//How many attempts per spawning run
	private static final int SPAWNING_ATTEMPTS_PER_CHUNK = 8;
	//How many attempts to reach the desired mob group size
	private static final int SPAWNING_ATTEMPTS_PER_GROUP = 32;

	//Maximum spawns per spawning run
	private static final int MAX_SPAWNS_PER_CHUNK = 6;
	//Maximum entities per loaded area around a player
	private static final int MAX_ENTITIES_PER_LOADED_AREA = ConfigHandler.maxEntitiesPerLoadedArea;
	//Maximum entities per chunk multiplier (MAX_ENTITIES_PER_CHUNK * eligibleChunks)
	private static final float MAX_ENTITIES_PER_CHUNK_MULTIPLIER = (float)MAX_ENTITIES_PER_LOADED_AREA / (float)((SPAWN_CHUNK_DISTANCE+SPAWN_CHUNK_RIM)*(SPAWN_CHUNK_DISTANCE+SPAWN_CHUNK_RIM) - SPAWN_CHUNK_RIM*SPAWN_CHUNK_RIM);

	//World entity limit
	private static final int HARD_ENTITY_LIMIT = ConfigHandler.hardEntityLimit;



	/**
	 * Default spawning weight is 100
	 */
	public static class BLSpawnEntry implements IWeightProvider {
		private final Class<? extends EntityLiving> entityType;
		private final Constructor<? extends EntityLiving> entityCtor;
		private final short baseWeight;
		private short weight;
		private boolean hostile = false;
		private int subChunkLimit = -1;
		private int chunkLimit = -1;
		private int worldLimit = -1;
		private int minGroupSize = 1, maxGroupSize = 1;
		private double spawnCheckRadius = 16.0D;
		private double groupSpawnRadius = 6.0D;
		private int spawningInterval = 0;
		private long lastSpawn = -1;

		public BLSpawnEntry(Class<? extends EntityLiving> entityType) {
			this(entityType, (short) 100);
		}

		public BLSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
			this.entityType = entityType;
			try {
				this.entityCtor = this.entityType.getConstructor(World.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Can't find or access entity constructor (World) of entity: " + entityType.getName());
			}
			this.weight = weight;
			this.baseWeight = weight;
		}

		/**
		 * Returns whether an entity can spawn based on the spawning position and the surface block below
		 * @param world
		 * @param x
		 * @param y
		 * @param z
		 * @param block
		 * @param surfaceBlock
		 */
		protected boolean canSpawn(World world, Chunk chunk, int x, int y, int z, Block block, Block surfaceBlock) {
			return surfaceBlock.isNormalCube();
		}

		/**
		 * Updates the spawning data based on the spawning position
		 * @param world
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		protected void update(World world, int x, int y, int z) { }

		@Override
		public final short getWeight() {
			return this.weight;
		}

		protected final BLSpawnEntry setWeight(short weight) {
			this.weight = weight;
			return this;
		}

		/**
		 * Sets the spawning interval
		 * @param interval
		 * @return
		 */
		public final BLSpawnEntry setSpawningInterval(int interval) {
			this.spawningInterval = interval;
			return this;
		}

		/**
		 * Returns the spawning interval
		 * @return
		 */
		public final int getSpawningInterval() {
			return this.spawningInterval;
		}

		/**
		 * Returns the initial base weight
		 * @return
		 */
		public final short getBaseWeight() {
			return this.baseWeight;
		}

		/**
		 * Sets the desired minimum and maximum group size. The minimum desired group size
		 * may not always be achieved depending on the area around the initial spawn point.
		 * @param min
		 * @param max
		 * @return
		 */
		public final BLSpawnEntry setGroupSize(int min, int max) {
			if(max < min) {
				throw new RuntimeException("Maximum group size cannot be smaller than minimum group size!");
			}
			this.minGroupSize = min;
			this.maxGroupSize = max;
			return this;
		}

		public final int getMaxGroupSize() {
			return this.maxGroupSize;
		}

		public final int getMinGroupSize() {
			return this.minGroupSize;
		}

		public final int getChunkLimit() {
			return this.chunkLimit;
		}

		public final BLSpawnEntry setChunkLimit(int limit) {
			this.chunkLimit = limit;
			return this;
		}

		public final int getWorldLimit() {
			return this.worldLimit;
		}

		public final BLSpawnEntry setWorldLimit(int limit) {
			this.worldLimit = limit;
			return this;
		}

		public final int getSubChunkLimit() {
			return this.subChunkLimit;
		}

		public final BLSpawnEntry setSubChunkLimit(int limit) {
			this.subChunkLimit = limit;
			return this;
		}

		public final BLSpawnEntry setHostile(boolean hostile) {
			this.hostile = hostile;
			return this;
		}

		public final boolean isHostile() {
			return this.hostile;
		}

		public final BLSpawnEntry setSpawnCheckRadius(double radius) {
			this.spawnCheckRadius = radius;
			return this;
		}

		public final double getSpawnCheckRadius() {
			return this.spawnCheckRadius;
		}

		public final BLSpawnEntry setGroupSpawnRadius(double radius) {
			this.groupSpawnRadius = radius;
			return this;
		}

		public final double getGroupSpawnRadius() {
			return this.groupSpawnRadius;
		}

		/**
		 * Returns whether already existing entity should be taken into account when spawning groups
		 * @return
		 */
		protected boolean shouldCheckExistingGroups() {
			return true;
		}

		/**
		 * Creates a new entity
		 * @param world
		 * @return
		 */
		protected EntityLiving createEntity(World world) {
			try {
				return this.entityCtor.newInstance(world);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			WorldServer world = DimensionManager.getWorld(ConfigHandler.DIMENSION_ID);
			if(world == null || world.playerEntities.isEmpty())
				return;

			if(world.getGameRules().getGameRuleBooleanValue("doMobSpawning") && world.getWorldTime() % 2 == 0) {
				//long start = System.nanoTime();
				this.populateWorld(world);
				//System.out.println("Time: " + (System.nanoTime() - start) / 1000000.0F);
			}
		}
	}

	@SubscribeEvent
	public void onChunkPopulate(PopulateChunkEvent.Post event) {
		World world = event.world;
		if(world == null || world.provider.dimensionId != ConfigHandler.DIMENSION_ID)
			return;

		if(world.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
			boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
			boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

			//long start = System.nanoTime();
			int spawnedEntities = 0;
			for(int i = 0; i < CHUNK_GEN_SPAWN_RUNS; i++) {
				spawnedEntities += this.populateChunk(world, new ChunkCoordIntPair(event.chunkX, event.chunkZ), spawnHostiles, spawnAnimals, false, true,
						SPAWNING_ATTEMPTS_PER_CHUNK, 60, SPAWNING_ATTEMPTS_PER_GROUP, HARD_ENTITY_LIMIT, 1.0F);
			}
			//System.out.println("Spawned: " + spawnedEntities + " Time: " + (System.nanoTime() - start) / 1000000.0F);
		}
	}

	private void populateWorld(World world) {
		if(world.provider instanceof WorldProviderBetweenlands == false) 
			return;

		int totalWorldEntityCount = 0;
		for(Entity entity : (List<Entity>)world.loadedEntityList)
			if(entity instanceof EntityLivingBase)
				totalWorldEntityCount++;
		if(totalWorldEntityCount >= HARD_ENTITY_LIMIT)
			//Hard limit reached, don't spawn any more entities
			return;

		this.updateSpawnerChunks(world);

		if(this.eligibleChunksForSpawning.size() == 0)
			//No spawning chunks
			return;

		List<ChunkCoordIntPair> spawnerChunks = new ArrayList<ChunkCoordIntPair>(this.eligibleChunksForSpawning.keySet().size() / (SPAWN_CHUNK_DISTANCE*SPAWN_CHUNK_DISTANCE) * (SPAWN_CHUNK_DISTANCE-1) * 4);

		//Add valid chunks
		for(Entry<ChunkCoordIntPair, Boolean> chunkEntry : this.eligibleChunksForSpawning.entrySet()) {
			if(chunkEntry.getValue()) spawnerChunks.add(chunkEntry.getKey());
		}

		this.updateEntityCounts(world);
		int totalEligibleEntityCount = 0;
		for(int count : this.entityCounts.values()) {
			totalEligibleEntityCount += count;
		}

		int maxEntitiesForLoadedArea = Math.min(HARD_ENTITY_LIMIT, (int) (spawnerChunks.size() * MAX_ENTITIES_PER_CHUNK_MULTIPLIER));

		if(totalEligibleEntityCount >= maxEntitiesForLoadedArea)
			//Too many entities, don't spawn any more entities
			return;

		Collections.shuffle(spawnerChunks);

		boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
		boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

		float areaMultiplier = (float)spawnerChunks.size() / (float)((SPAWN_CHUNK_DISTANCE+SPAWN_CHUNK_RIM)*(SPAWN_CHUNK_DISTANCE+SPAWN_CHUNK_RIM) - SPAWN_CHUNK_RIM*SPAWN_CHUNK_RIM);

		for(ChunkCoordIntPair chunkPos : spawnerChunks) {
			this.populateChunk(world, chunkPos, spawnHostiles, spawnAnimals, true, false, 
					SPAWNING_ATTEMPTS_PER_CHUNK, MAX_SPAWNS_PER_CHUNK, SPAWNING_ATTEMPTS_PER_GROUP, maxEntitiesForLoadedArea, areaMultiplier);
		}
	}

	private int populateChunk(World world, ChunkCoordIntPair chunkPos, boolean spawnHostiles, boolean spawnAnimals, boolean loadChunks, boolean ignoreTimers,
			int attemptsPerChunk, int maxSpawnsPerChunk, int attemptsPerGroup, int entityLimit, float areaMultiplier) {
		int attempts = 0, chunkSpawnedEntities = 0;
		spawnLoop:
			while(attempts++ < attemptsPerChunk && chunkSpawnedEntities < maxSpawnsPerChunk) {
				ChunkPosition spawnPos = this.getRandomSpawnPosition(world, chunkPos);
				BiomeGenBase biome = world.getBiomeGenForCoords(spawnPos.chunkPosX, spawnPos.chunkPosZ);

				if(world.rand.nextFloat() > biome.getSpawningChance() || biome instanceof BiomeGenBaseBetweenlands == false) 
					continue;

				Block centerSpawnBlock = world.getBlock(spawnPos.chunkPosX, spawnPos.chunkPosY, spawnPos.chunkPosZ);

				if(centerSpawnBlock.isNormalCube()) 
					continue;

				int totalBaseWeight = 0;
				int totalWeight = 0;

				//Get possible spawn entries and update weights
				List<BLSpawnEntry> biomeSpawns = ((BiomeGenBaseBetweenlands)biome).getSpawnEntries();
				List<BLSpawnEntry> possibleSpawns = new ArrayList<BLSpawnEntry>();
				for(BLSpawnEntry spawnEntry : biomeSpawns) {
					if(!((spawnEntry.isHostile() && !spawnHostiles) || (!spawnEntry.isHostile() && !spawnAnimals))) {
						totalBaseWeight += spawnEntry.getBaseWeight();
						possibleSpawns.add(spawnEntry);
						spawnEntry.update(world, spawnPos.chunkPosX, spawnPos.chunkPosY, spawnPos.chunkPosZ);
						totalWeight += spawnEntry.getWeight();
					}
				}

				if(possibleSpawns.isEmpty())
					continue;

				WeightedList<BLSpawnEntry> weightedPossibleSpawns = new WeightedList<BLSpawnEntry>();
				weightedPossibleSpawns.addAll(possibleSpawns);
				weightedPossibleSpawns.recalculateWeight();

				BLSpawnEntry spawnEntry = weightedPossibleSpawns.getRandomItem(world.rand);
				if(spawnEntry == null)
					continue;

				int dynamicLimitBase = (int)((double)entityLimit / (double)totalBaseWeight * spawnEntry.getBaseWeight());
				int dynamicLimit = (int)((double)entityLimit / (double)totalWeight * spawnEntry.getWeight());

				int spawnEntityCount = this.entityCounts.get(spawnEntry.entityType);

				if(spawnEntityCount >= Math.max(dynamicLimit, dynamicLimitBase) || (spawnEntry.getWorldLimit() >= 0 && spawnEntityCount >= spawnEntry.getWorldLimit()))
					//Entity reached world spawning limit
					continue;

				int desiredGroupSize = spawnEntry.getMinGroupSize() + world.rand.nextInt(spawnEntry.getMaxGroupSize() - spawnEntry.getMinGroupSize() + 1);
				double groupCheckRadius = spawnEntry.spawnCheckRadius;
				//Check whether chunks are loaded in the check radius, prevents entities from spawning somewhere even though the group limit was already reached in an unloaded chunk
				int csx = MathHelper.floor_double((spawnPos.chunkPosX - groupCheckRadius) / 16.0D);
				int cex = MathHelper.floor_double((spawnPos.chunkPosX + groupCheckRadius) / 16.0D);
				int csz = MathHelper.floor_double((spawnPos.chunkPosZ - groupCheckRadius) / 16.0D);
				int cez = MathHelper.floor_double((spawnPos.chunkPosZ + groupCheckRadius) / 16.0D);
				for (int cx = csx; cx <= cex; ++cx) {
					for (int cz = csz; cz <= cez; ++cz) {
						if(!world.getChunkProvider().chunkExists(cx, cz)) {
							continue spawnLoop;
						}
					}
				}
				double groupSpawnRadius = spawnEntry.groupSpawnRadius;
				Class<? extends Entity> entityType = spawnEntry.entityType;
				boolean checkExistingGroups = spawnEntry.shouldCheckExistingGroups();

				if(checkExistingGroups) {
					List<Entity> foundGroupEntities = world.getEntitiesWithinAABB(entityType, AxisAlignedBB.getBoundingBox(
							spawnPos.chunkPosX - groupCheckRadius, spawnPos.chunkPosY - 6, spawnPos.chunkPosZ - groupCheckRadius, 
							spawnPos.chunkPosX + groupCheckRadius, spawnPos.chunkPosY + 6, spawnPos.chunkPosZ + groupCheckRadius));
					Iterator<Entity> foundGroupEntitiesIT = foundGroupEntities.iterator();
					while(foundGroupEntitiesIT.hasNext()) {
						Entity foundEntity = foundGroupEntitiesIT.next();
						if(foundEntity.getDistance(spawnPos.chunkPosX, foundEntity.posY, spawnPos.chunkPosZ) > groupCheckRadius) {
							foundGroupEntitiesIT.remove();
						}
					}
					desiredGroupSize -= foundGroupEntities.size();
				}

				if(desiredGroupSize > 0) {
					int groupSpawnedEntities = 0, groupSpawnAttempts = 0;
					int maxGroupSpawnAttempts = attemptsPerGroup + desiredGroupSize * 2;

					if(!ignoreTimers) {
						if(spawnEntry.lastSpawn == -1) {
							spawnEntry.lastSpawn = world.getTotalWorldTime();
							continue;
						}
						//Adjust intervals for MP when there are multiple players and the loaded area is bigger -> smaller intervals
						int adjustedInterval = (int)(spawnEntry.spawningInterval / areaMultiplier);
						if(world.getTotalWorldTime() - spawnEntry.lastSpawn < adjustedInterval)
							//Too early, don't spawn yet
							continue;
					}

					while(groupSpawnAttempts++ < maxGroupSpawnAttempts && groupSpawnedEntities < desiredGroupSize) {
						ChunkPosition entitySpawnPos = this.getRandomSpawnPosition(world, spawnPos, MathHelper.floor_double(groupSpawnRadius));

						boolean inChunk = (entitySpawnPos.chunkPosX >> 4) == chunkPos.chunkXPos && (entitySpawnPos.chunkPosZ >> 4) == chunkPos.chunkZPos;

						if(!loadChunks && !inChunk)
							continue;

						if(world.getClosestPlayer(entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY, entitySpawnPos.chunkPosZ, 24D) != null)
							continue;

						Block spawnBlock = world.getBlock(entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY, entitySpawnPos.chunkPosZ);

						if(spawnBlock.isNormalCube())
							continue;

						int spawnSegmentY = entitySpawnPos.chunkPosY / 16;
						Chunk spawnChunk = world.getChunkFromBlockCoords(entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosZ);
						List<Entity>[] entityLists = spawnChunk.entityLists;
						int chunkEntityCount = 0;
						boolean nextGroupAttempt = false;
						for(int l = 0; l < entityLists.length; l++) {
							int subChunkEntityCount = 0;
							for(Entity entity : entityLists[l]) {
								if(entity.getClass() == spawnEntry.entityType) {
									subChunkEntityCount++;
									chunkEntityCount++;
								}
							}
							if(l == spawnSegmentY) {
								if(spawnEntry.getSubChunkLimit() >= 0 && subChunkEntityCount >= spawnEntry.getSubChunkLimit())
									//Entity reached sub chunk limit
									continue;
							}
						}

						if(spawnEntry.getChunkLimit() >= 0 && chunkEntityCount >= spawnEntry.getChunkLimit())
							//Entity reached chunk limit
							continue;

						Block surfaceBlock = spawnChunk.getBlock(entitySpawnPos.chunkPosX - spawnChunk.xPosition * 16, entitySpawnPos.chunkPosY - 1, entitySpawnPos.chunkPosZ - spawnChunk.zPosition * 16);

						if(spawnEntry.canSpawn(world, spawnChunk, entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY, entitySpawnPos.chunkPosZ, spawnBlock, surfaceBlock)) {
							double sx = entitySpawnPos.chunkPosX + 0.5D;
							double sy = entitySpawnPos.chunkPosY;
							double sz = entitySpawnPos.chunkPosZ + 0.5D;
							float yaw = world.rand.nextFloat() * 360.0F;

							EntityLiving newEntity = spawnEntry.createEntity(world);
							if(newEntity != null) {
								newEntity.setLocationAndAngles(sx, sy, sz, yaw, 0.0F);

								Result canSpawn = ForgeEventFactory.canEntitySpawn(newEntity, world, (float)sx, (float)sy, (float)sz);
								if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && newEntity.getCanSpawnHere())) {
									groupSpawnedEntities++;
									chunkSpawnedEntities++;

									NBTTagCompound entityNBT = newEntity.getEntityData();
									entityNBT.setBoolean("naturallySpawned", true);

									world.spawnEntityInWorld(newEntity);

									if (!ForgeEventFactory.doSpecialSpawn(newEntity, world, (float)sx, (float)sy, (float)sz)) {
										newEntity.onSpawnWithEgg(null);
									}

									if (groupSpawnedEntities >= ForgeEventFactory.getMaxSpawnPackSize(newEntity))  {
										break;
									}
								}
							}
						}
					}

					if(!ignoreTimers && groupSpawnedEntities > 0)
						spawnEntry.lastSpawn = world.getTotalWorldTime();
				}
			}
		return chunkSpawnedEntities;
	}

	private ChunkPosition getRandomSpawnPosition(World world, ChunkCoordIntPair chunkPos) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos);
		int x = chunkPos.chunkXPos * 16 + world.rand.nextInt(16);
		int z = chunkPos.chunkZPos * 16 + world.rand.nextInt(16);
		int y = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
		return new ChunkPosition(x, y, z);
	}

	private ChunkPosition getRandomSpawnPosition(World world, ChunkPosition centerPos, int radius) {
		return new ChunkPosition(
				centerPos.chunkPosX + world.rand.nextInt(radius*2) - radius,
				MathHelper.clamp_int(centerPos.chunkPosY + world.rand.nextInt(4) - 2, 1, world.getHeight()),
				centerPos.chunkPosZ + world.rand.nextInt(radius*2) - radius);
	}

	private Map<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning = new HashMap<ChunkCoordIntPair, Boolean>();

	/**
	 * Clears eligibleChunksForSpawning, finds chunks entities can spawn in and adds them to eligibleChunksForSpawning
	 * @param world
	 */
	private void updateSpawnerChunks(World world) {
		this.eligibleChunksForSpawning.clear();
		for(EntityPlayer player : (List<EntityPlayer>) world.playerEntities) {
			int pcx = player.chunkCoordX;
			int pcz = player.chunkCoordZ;
			for(int cox = -SPAWN_CHUNK_DISTANCE; cox <= SPAWN_CHUNK_DISTANCE; cox++) {
				for(int coz = -SPAWN_CHUNK_DISTANCE; coz <= SPAWN_CHUNK_DISTANCE; coz++) {
					int cx = pcx + cox;
					int cz = pcz + coz;
					ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(cx, cz);
					boolean isOuterChunk = (Math.abs(cox - SPAWN_CHUNK_DISTANCE) < SPAWN_CHUNK_RIM) || (Math.abs(coz - SPAWN_CHUNK_DISTANCE) < SPAWN_CHUNK_RIM);
					if(isOuterChunk) {
						if(!this.eligibleChunksForSpawning.containsKey(chunkPos))
							this.eligibleChunksForSpawning.put(chunkPos, true);
					} else {
						this.eligibleChunksForSpawning.put(chunkPos, false);
					}
				}
			}
		}
	}

	private TObjectIntHashMap<Class<? extends Entity>> entityCounts = new TObjectIntHashMap<Class<? extends Entity>>();

	private void updateEntityCounts(World world) {
		this.entityCounts.clear();
		for(Entry<ChunkCoordIntPair, Boolean> eligibleChunk : this.eligibleChunksForSpawning.entrySet()) {
			ChunkCoordIntPair chunkPos = eligibleChunk.getKey();
			if(world.getChunkProvider().chunkExists(chunkPos.chunkXPos, chunkPos.chunkZPos)) {
				Chunk chunk = world.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos);
				List[] entityLists = chunk.entityLists;
				for(List<Entity> entityList : entityLists) {
					for(Entity entity : entityList) {
						if(entity instanceof EntityLivingBase)
							this.entityCounts.adjustOrPutValue(entity.getClass(), 1, 1);
					}
				}
			}
		}
	}
}
