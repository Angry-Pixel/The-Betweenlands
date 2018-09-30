package thebetweenlands.common.world.biome.spawning;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage.BiomeSpawnEntriesData;
import thebetweenlands.util.WeightedList;

public class MobSpawnHandler {
	public static final MobSpawnHandler INSTANCE = new MobSpawnHandler();

	//How many times a chunk should be populated with mobs when it generates
	private static final int CHUNK_GEN_SPAWN_RUNS = 128;

	//Maximum distance from the player where mobs spawn
	private static final int SPAWN_CHUNK_MAX_RANGE = 8;
	//Minimum chunk distance from player where mobs spawn (exclusive)
	private static final int SPAWN_CHUNK_MIN_RANGE = 1;
	//Number of spawn chunks in one fully loaded area
	private static final int MAX_SPAWN_CHUNKS_PER_AREA = (SPAWN_CHUNK_MAX_RANGE * 2 + 1)*(SPAWN_CHUNK_MAX_RANGE * 2 + 1) - (SPAWN_CHUNK_MIN_RANGE * 2 + 1) * (SPAWN_CHUNK_MIN_RANGE * 2 + 1);

	//How many attempts per spawning run
	private static final int SPAWNING_ATTEMPTS_PER_CHUNK = 8;
	//How many attempts to reach the desired mob group size
	private static final int SPAWNING_ATTEMPTS_PER_GROUP = 32;

	//Maximum spawns per spawning run
	private static final int MAX_SPAWNS_PER_CHUNK = 6;

	//World entity limit
	private static final int HARD_ENTITY_LIMIT = BetweenlandsConfig.MOB_SPAWNING.hardEntityLimit;

	/**
	 * Maximum entities per chunk multiplier
	 * @return
	 */
	public static final float getMaxEntitiesPerSpawnChunkMultiplier() {
		return (float)BetweenlandsConfig.MOB_SPAWNING.maxEntitiesPerLoadedArea / (float)MAX_SPAWN_CHUNKS_PER_AREA;
	}

	/**
	 * Default spawning weight is 100
	 */
	public static class BLSpawnEntry implements ICustomSpawnEntry {
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
		private double spawnCheckRangeY = 6.0D;
		private double groupSpawnRadius = 6.0D;
		private int spawningInterval = 0;

		/**
		 * The ID is used to save the spawn entry data such as last spawn time. A negative ID means that no data will be saved
		 */
		public final ResourceLocation id;

		public BLSpawnEntry(Class<? extends EntityLiving> entityType) {
			this(-1, entityType, (short) 100);
		}

		public BLSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
			this(-1, entityType, weight);
		}

		public BLSpawnEntry(int id, Class<? extends EntityLiving> entityType) {
			this(id, entityType, (short) 100);
		}

		public BLSpawnEntry(int id, Class<? extends EntityLiving> entityType, short weight) {
			this.id = new ResourceLocation(ModInfo.ID, String.valueOf(id));
			this.entityType = entityType;
			try {
				this.entityCtor = this.entityType.getConstructor(World.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Can't find or access entity constructor (World) of entity: " + entityType.getName());
			}
			this.weight = weight;
			this.baseWeight = weight;
		}

		@Override
		public ResourceLocation getID() {
			return this.id;
		}

		@Override
		public boolean isSaved() {
			return !"-1".equals(this.id.getResourcePath());
		}

		@Override
		public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
			return surfaceBlockState.isNormalCube();
		}

		@Override
		public void update(World world, BlockPos pos) { }

		@Override
		public final short getWeight() {
			return this.weight;
		}

		@Override
		public final BLSpawnEntry setWeight(short weight) {
			this.weight = weight;
			return this;
		}

		@Override
		public final BLSpawnEntry setSpawningInterval(int interval) {
			this.spawningInterval = interval;
			return this;
		}

		@Override
		public final int getSpawningInterval() {
			return this.spawningInterval;
		}

		@Override
		public final short getBaseWeight() {
			return this.baseWeight;
		}

		@Override
		public final BLSpawnEntry setGroupSize(int min, int max) {
			if(max < min) {
				throw new RuntimeException("Maximum group size cannot be smaller than minimum group size!");
			}
			this.minGroupSize = min;
			this.maxGroupSize = max;
			return this;
		}

		@Override
		public final int getMaxGroupSize() {
			return this.maxGroupSize;
		}

		@Override
		public final int getMinGroupSize() {
			return this.minGroupSize;
		}

		@Override
		public final int getChunkLimit() {
			return this.chunkLimit;
		}

		@Override
		public final BLSpawnEntry setChunkLimit(int limit) {
			this.chunkLimit = limit;
			return this;
		}

		@Override
		public final int getWorldLimit() {
			return this.worldLimit;
		}

		@Override
		public final BLSpawnEntry setWorldLimit(int limit) {
			this.worldLimit = limit;
			return this;
		}

		@Override
		public final int getSubChunkLimit() {
			return this.subChunkLimit;
		}

		@Override
		public final BLSpawnEntry setSubChunkLimit(int limit) {
			this.subChunkLimit = limit;
			return this;
		}

		@Override
		public final BLSpawnEntry setHostile(boolean hostile) {
			this.hostile = hostile;
			return this;
		}

		@Override
		public final boolean isHostile() {
			return this.hostile;
		}

		@Override
		public final BLSpawnEntry setSpawnCheckRadius(double radius) {
			this.spawnCheckRadius = radius;
			return this;
		}

		@Override
		public final double getSpawnCheckRadius() {
			return this.spawnCheckRadius;
		}

		@Override
		public final BLSpawnEntry setSpawnCheckRangeY(double y) {
			this.spawnCheckRangeY = y;
			return this;
		}

		@Override
		public final double getSpawnCheckRangeY() {
			return this.spawnCheckRangeY;
		}

		@Override
		public final BLSpawnEntry setGroupSpawnRadius(double radius) {
			this.groupSpawnRadius = radius;
			return this;
		}

		@Override
		public final double getGroupSpawnRadius() {
			return this.groupSpawnRadius;
		}

		@Override
		public boolean shouldCheckExistingGroups() {
			return true;
		}

		@Override
		public EntityLiving createEntity(World world) {
			try {
				return this.entityCtor.newInstance(world);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		public final Class<? extends EntityLiving> getEntityType() {
			return this.entityType;
		}

		@Override
		public void onSpawned(EntityLivingBase entity) { }
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			WorldServer world = DimensionManager.getWorld(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
			if(world == null || world.playerEntities.isEmpty())
				return;

			if(world.getGameRules().getBoolean("doMobSpawning") && world.getTotalWorldTime() % 4 == 0) {
				//long start = System.nanoTime();
				this.populateWorld(world);
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
					SPAWNING_ATTEMPTS_PER_CHUNK * CHUNK_GEN_SPAWN_RUNS, 60, SPAWNING_ATTEMPTS_PER_GROUP, HARD_ENTITY_LIMIT, 1.0F);
			//System.out.println("Spawned: " + spawnedEntities + " Time: " + (System.nanoTime() - start) / 1000000.0F);
		}
	}

	private void populateWorld(WorldServer world) {
		if(world.provider instanceof WorldProviderBetweenlands == false) {
			return;
		}

		int totalWorldEntityCount = 0;
		for(Entity entity : (List<Entity>)world.loadedEntityList) {
			if(entity instanceof EntityLivingBase) {
				totalWorldEntityCount++;
			}
		}

		if(totalWorldEntityCount >= HARD_ENTITY_LIMIT) {
			//Hard limit reached, don't spawn any more entities
			return;
		}

		this.updateSpawnerChunks(world);

		if(this.eligibleChunksForSpawning.isEmpty()) {
			//No spawning chunks
			return;
		}

		List<ChunkPos> spawnerChunks = new ArrayList<ChunkPos>(this.eligibleChunksForSpawning.size());

		//Add valid chunks
		for(ChunkPos chunkPos : this.eligibleChunksForSpawning) {
			//Don't load chunks
			if(world.isBlockLoaded(new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16))) {
				spawnerChunks.add(chunkPos);
			}
		}

		this.updateEntityCounts(world);
		int totalEligibleEntityCount = 0;
		for(int count : this.entityCounts.values()) {
			totalEligibleEntityCount += count;
		}

		int maxEntitiesForLoadedArea = Math.min(HARD_ENTITY_LIMIT, (int) (spawnerChunks.size() * getMaxEntitiesPerSpawnChunkMultiplier()));

		if(totalEligibleEntityCount >= maxEntitiesForLoadedArea) {
			//Too many entities, don't spawn any more entities
			return;
		}

		Collections.shuffle(spawnerChunks);

		boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
		boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

		//The approximate number of loaded areas (one area is the area loaded by one player)
		float loadedAreas = (float)spawnerChunks.size() / (float)MAX_SPAWN_CHUNKS_PER_AREA;

		for(ChunkPos chunkPos : spawnerChunks) {
			this.populateChunk(world, chunkPos, spawnHostiles, spawnAnimals, true, false, 
					SPAWNING_ATTEMPTS_PER_CHUNK, MAX_SPAWNS_PER_CHUNK, SPAWNING_ATTEMPTS_PER_GROUP, maxEntitiesForLoadedArea, loadedAreas);
		}
	}

	private int populateChunk(World world, ChunkPos chunkPos, boolean spawnHostiles, boolean spawnAnimals, boolean loadChunks, boolean ignoreRestrictions,
			int attemptsPerChunk, int maxSpawnsPerChunk, int attemptsPerGroup, int entityLimit, float loadedAreas) {
		int attempts = 0, chunkSpawnedEntities = 0;
		spawnLoop:
			while(attempts++ < attemptsPerChunk && chunkSpawnedEntities < maxSpawnsPerChunk) {
				BlockPos spawnPos = this.getRandomSpawnPosition(world, chunkPos);
				Biome biome = world.getBiome(spawnPos);

				if(world.rand.nextFloat() > biome.getSpawningChance() || biome instanceof ICustomSpawnEntriesProvider == false) 
					continue;

				IBlockState centerSpawnBlockState = world.getBlockState(spawnPos);

				if(centerSpawnBlockState.isNormalCube()) 
					continue;

				int totalBaseWeight = 0;
				int totalWeight = 0;

				//Get possible spawn entries and update weights
				List<ICustomSpawnEntry> biomeSpawns = ((ICustomSpawnEntriesProvider)biome).getCustomSpawnEntries();
				List<ICustomSpawnEntry> possibleSpawns = new ArrayList<>();
				for(ICustomSpawnEntry spawnEntry : biomeSpawns) {
					if(!((spawnEntry.isHostile() && !spawnHostiles) || (!spawnEntry.isHostile() && !spawnAnimals))) {
						totalBaseWeight += spawnEntry.getBaseWeight();
						possibleSpawns.add(spawnEntry);
						spawnEntry.update(world, spawnPos);
						totalWeight += spawnEntry.getWeight();
					}
				}

				if(possibleSpawns.isEmpty())
					continue;

				WeightedList<ICustomSpawnEntry> weightedPossibleSpawns = new WeightedList<>();
				weightedPossibleSpawns.addAll(possibleSpawns);
				weightedPossibleSpawns.recalculateWeight();

				ICustomSpawnEntry spawnEntry = weightedPossibleSpawns.getRandomItem(world.rand);
				if(spawnEntry == null) {
					continue;
				}

				int dynamicLimitBase = (int)((double)entityLimit / (double)totalBaseWeight * spawnEntry.getBaseWeight());
				int dynamicLimit = (int)((double)entityLimit / (double)totalWeight * spawnEntry.getWeight());

				int spawnEntityCount = this.entityCounts.get(spawnEntry.getEntityType());

				if(spawnEntityCount >= Math.max(dynamicLimit, dynamicLimitBase) || (spawnEntry.getWorldLimit() >= 0 && spawnEntityCount >= spawnEntry.getWorldLimit())) {
					//Entity reached world spawning limit
					continue;
				}

				int desiredGroupSize = spawnEntry.getMinGroupSize() + world.rand.nextInt(spawnEntry.getMaxGroupSize() - spawnEntry.getMinGroupSize() + 1);
				double groupCheckRadius = spawnEntry.getSpawnCheckRadius();
				//Check whether chunks are loaded in the check radius, prevents entities from spawning somewhere even though the group limit was already reached in an unloaded chunk
				int csx = MathHelper.floor(spawnPos.getX() - groupCheckRadius) >> 4;
				int cex = MathHelper.floor(spawnPos.getX() + groupCheckRadius) >> 4;
				int csz = MathHelper.floor(spawnPos.getZ() - groupCheckRadius) >> 4;
				int cez = MathHelper.floor(spawnPos.getZ() + groupCheckRadius) >> 4;
				for (int cx = csx; cx <= cex; ++cx) {
					for (int cz = csz; cz <= cez; ++cz) {
						if(world.getChunkProvider().getLoadedChunk(cx, cz) == null && (cx != chunkPos.x || cz != chunkPos.z)) {
							continue spawnLoop;
						}
					}
				}
				double groupSpawnRadius = spawnEntry.getGroupSpawnRadius();
				Class<? extends Entity> entityType = spawnEntry.getEntityType();
				boolean checkExistingGroups = spawnEntry.shouldCheckExistingGroups();

				if(checkExistingGroups) {
					List<Entity> foundGroupEntities = world.getEntitiesWithinAABB(entityType, new AxisAlignedBB(
							spawnPos.getX() - groupCheckRadius, spawnPos.getY() - spawnEntry.getSpawnCheckRangeY(), spawnPos.getZ() - groupCheckRadius, 
							spawnPos.getX() + groupCheckRadius, spawnPos.getY() + spawnEntry.getSpawnCheckRangeY(), spawnPos.getZ() + groupCheckRadius));
					for(Entity foundGroupEntity : foundGroupEntities) {
						if(foundGroupEntity.getDistance(spawnPos.getX(), foundGroupEntity.posY + (spawnPos.getY() - foundGroupEntity.posY) / spawnEntry.getSpawnCheckRangeY() * groupCheckRadius, spawnPos.getZ()) <= groupCheckRadius) {
							desiredGroupSize--;
						}
					}
				}

				if(desiredGroupSize > 0) {
					int groupSpawnedEntities = 0, groupSpawnAttempts = 0;
					int maxGroupSpawnAttempts = attemptsPerGroup + desiredGroupSize * 2;

					BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
					BiomeSpawnEntriesData spawnEntriesData = null;
					long lastSpawn = -1;
					if(worldStorage != null) {
						spawnEntriesData = worldStorage.getBiomeSpawnEntriesData(biome);
						lastSpawn = spawnEntriesData.getLastSpawn(spawnEntry);
					}

					if(!ignoreRestrictions) {
						if(spawnEntriesData != null && lastSpawn == -1) {
							spawnEntriesData.setLastSpawn(spawnEntry, world.getTotalWorldTime());
							continue;
						}
						//Adjust intervals for MP when there are multiple players and the loaded area is bigger -> smaller intervals
						int adjustedInterval = (int)(spawnEntry.getSpawningInterval() / loadedAreas);
						if(spawnEntriesData != null && world.getTotalWorldTime() - lastSpawn < adjustedInterval) {
							//Too early, don't spawn yet
							continue;
						}
					}

					IEntityLivingData groupData = null;

					while(groupSpawnAttempts++ < maxGroupSpawnAttempts && groupSpawnedEntities < desiredGroupSize) {
						BlockPos entitySpawnPos = this.getRandomSpawnPosition(world, spawnPos, MathHelper.floor(groupSpawnRadius));

						boolean inChunk = (entitySpawnPos.getX() >> 4) == chunkPos.x && (entitySpawnPos.getZ() >> 4) == chunkPos.z;

						if(!loadChunks && !inChunk)
							continue;

						if(world.getClosestPlayer(entitySpawnPos.getX(), entitySpawnPos.getY(), entitySpawnPos.getZ(), 24D, false) != null)
							continue;

						IBlockState spawnBlockState = world.getBlockState(entitySpawnPos);

						if(spawnBlockState.isNormalCube())
							continue;

						int spawnSegmentY = entitySpawnPos.getY() / 16;
						Chunk spawnChunk = world.getChunkFromBlockCoords(entitySpawnPos);
						ClassInheritanceMultiMap<Entity>[] entityLists = spawnChunk.getEntityLists();
						int chunkEntityCount = 0;
						for(int l = 0; l < entityLists.length; l++) {
							int subChunkEntityCount = 0;
							for(Entity entity : entityLists[l]) {
								if(entity.getClass() == spawnEntry.getEntityType()) {
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

						IBlockState surfaceBlockState = spawnChunk.getBlockState(entitySpawnPos.getX() - spawnChunk.x * 16, entitySpawnPos.getY() - 1, entitySpawnPos.getZ() - spawnChunk.z * 16);

						if(spawnEntry.canSpawn(world, spawnChunk, entitySpawnPos, spawnBlockState, surfaceBlockState)) {
							double sx = entitySpawnPos.getX() + 0.5D;
							double sy = entitySpawnPos.getY();
							double sz = entitySpawnPos.getZ() + 0.5D;
							float yaw = world.rand.nextFloat() * 360.0F;

							EntityLiving newEntity = spawnEntry.createEntity(world);
							if(newEntity != null) {
								newEntity.setLocationAndAngles(sx, sy, sz, yaw, 0.0F);

								Result canSpawn = ForgeEventFactory.canEntitySpawn(newEntity, world, (float)sx, (float)sy, (float)sz, null);
								if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && newEntity.getCanSpawnHere() && newEntity.isNotColliding())) {
									groupSpawnedEntities++;
									chunkSpawnedEntities++;

									NBTTagCompound entityNBT = newEntity.getEntityData();
									entityNBT.setBoolean("naturallySpawned", true);

									world.spawnEntity(newEntity);

									if (!ForgeEventFactory.doSpecialSpawn(newEntity, world, (float)sx, (float)sy, (float)sz, null)) {
										groupData = newEntity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(sx, sy, sz)), groupData);
									}

									spawnEntry.onSpawned(newEntity);

									if (groupSpawnedEntities >= ForgeEventFactory.getMaxSpawnPackSize(newEntity))  {
										break;
									}
								}
							}
						}
					}

					if(spawnEntriesData != null && !ignoreRestrictions && groupSpawnedEntities > 0) {
						spawnEntriesData.setLastSpawn(spawnEntry, world.getTotalWorldTime());
					}
				}
			}
		return chunkSpawnedEntities;
	}

	private BlockPos getRandomSpawnPosition(World world, ChunkPos chunkPos) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
		int x = chunkPos.x * 16 + world.rand.nextInt(16);
		int z = chunkPos.z * 16 + world.rand.nextInt(16);
		int y = Math.min(world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1), 256);
		return new BlockPos(x, y, z);
	}

	private BlockPos getRandomSpawnPosition(World world, BlockPos centerPos, int radius) {
		return new BlockPos(
				centerPos.getX() + world.rand.nextInt(radius*2) - radius,
				MathHelper.clamp(centerPos.getY() + world.rand.nextInt(4) - 2, 1, world.getHeight()),
				centerPos.getZ() + world.rand.nextInt(radius*2) - radius);
	}

	private Set<ChunkPos> eligibleChunksForSpawning = new HashSet<>();

	/**
	 * Clears eligibleChunksForSpawning, finds chunks entities can spawn in and adds them to eligibleChunksForSpawning
	 * @param world
	 */
	private void updateSpawnerChunks(WorldServer world) {
		this.eligibleChunksForSpawning.clear();

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
				this.eligibleChunksForSpawning.add(entry.getKey());
			}
		}
	}

	private TObjectIntHashMap<Class<? extends Entity>> entityCounts = new TObjectIntHashMap<Class<? extends Entity>>();

	private void updateEntityCounts(World world) {
		this.entityCounts.clear();
		for(ChunkPos chunkPos : this.eligibleChunksForSpawning) {
			if(world.getChunkProvider().getLoadedChunk(chunkPos.x, chunkPos.z) != null) {
				Chunk chunk = world.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
				ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
				for(ClassInheritanceMultiMap<Entity> entityList : entityLists) {
					for(Entity entity : entityList) {
						if(entity instanceof EntityLivingBase) {
							this.entityCounts.adjustOrPutValue(entity.getClass(), 1, 1);
						}
					}
				}
			}
		}
	}
}
