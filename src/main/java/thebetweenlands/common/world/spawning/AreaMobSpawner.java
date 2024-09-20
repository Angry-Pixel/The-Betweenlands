package thebetweenlands.common.world.spawning;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.entity.spawning.BiomeSpawnEntriesData;
import thebetweenlands.api.entity.spawning.CustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.CustomSpawnEntry;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.util.WeightedList;

public abstract class AreaMobSpawner {
	@Nullable
	protected Predicate<LivingEntity> entityCountFilter = null;

	protected boolean strictDynamicLimit = true;

	/**
	 * Sets whether the dynamic limit is strict, i.e. enforced and not
	 * just approximated by randomness and weight.
	 * @param strict
	 */
	public void setStrictDynamicLimit(boolean strict) {
		this.strictDynamicLimit = strict;
	}

	/**
	 * Sets the entity count filter. The entity count filter determines which
	 * entities are supposed to be counted towards the entity count.
	 * @param filter
	 */
	public void setEntityCountFilter(@Nullable Predicate<LivingEntity> filter) {
		this.entityCountFilter = filter;
	}

	/**
	 * Returns whether the specified position is within this area mob spawner's area
	 * @param level
	 * @param pos
	 * @param entityCount
	 * @return
	 */
	public boolean isInsideSpawningArea(Level level, BlockPos pos, boolean entityCount) {
		return entityCount || level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 24D, false) == null;
	}

	/**
	 * How many attempts to reach the desired mob group size
	 * @return
	 */
	public int getSpawningAttemptsPerGroup() {
		return 24;
	}

	/**
	 * How many attempts per spawning run
	 * @return
	 */
	public int getSpawningAttempsPerChunk() {
		return 8;
	}

	/**
	 * Maximum spawns per spawning run
	 * @return
	 */
	public int getMaxSpawnsPerChunk() {
		return 6;
	}

	/**
	 * Total world entity limit
	 */
	public int getHardEntityLimit() {
		return BetweenlandsConfig.hardEntityLimit;
	}

	/**
	 * Maximum entities per chunk as a fraction
	 * @return
	 */
	public abstract float getMaxEntitiesPerSpawnChunkFraction(int spawnerChunks);

	/**
	 * Returns the approximate number of loaded (player) areas
	 * @return
	 */
	public float getLoadedAreasCount(int spawnerChunks) {
		return 1.0f;
	}

	/**
	 * Returns a list of all spawn entries at the specified position
	 * @param level
	 * @param pos
	 * @param provider
	 * @return
	 */
	public List<CustomSpawnEntry> getSpawnEntries(Level level, BlockPos pos, @Nullable CustomSpawnEntriesProvider provider) {
		return provider != null ? provider.getCustomSpawnEntries() : Collections.emptyList();
	}

	/**
	 * Returns the spawn entry data, such as spawning cooldowns etc.
	 * @param level
	 * @param pos
	 * @param provider
	 * @return
	 */
	@Nullable
	public BiomeSpawnEntriesData getSpawnEntriesData(Level level, BlockPos pos, @Nullable CustomSpawnEntriesProvider provider) {
		return null;
	}

	/**
	 * Default spawning weight is 100
	 */
	public static class BLSpawnEntry implements CustomSpawnEntry {
		private final Class<? extends Mob> entityType;
		private final Function<Level, ? extends Mob> entityCtor;
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

		public BLSpawnEntry(Class<? extends Mob> entityType, Function<Level, ? extends Mob> entityCtor) {
			this(-1, entityType, entityCtor, (short) 100);
		}

		public BLSpawnEntry(Class<? extends Mob> entityType, Function<Level, ? extends Mob> entityCtor, short weight) {
			this(-1, entityType, entityCtor, weight);
		}

		public BLSpawnEntry(int id, Class<? extends Mob> entityType, Function<Level, ? extends Mob> entityCtor) {
			this(id, entityType, entityCtor, (short) 100);
		}

		public BLSpawnEntry(int id, Class<? extends Mob> entityType, Function<Level, ? extends Mob> entityCtor, short weight) {
			this.id = TheBetweenlands.prefix(String.valueOf(id));
			this.entityType = entityType;
			this.entityCtor = entityCtor;
			this.weight = weight;
			this.baseWeight = weight;
		}

		@Override
		public ResourceLocation getID() {
			return this.id;
		}

		@Override
		public boolean isSaved() {
			return !"-1".equals(this.id.getPath());
		}

		@Override
		@SuppressWarnings("deprecation") // TODO: Unsure if there's a proper supported method of checking if a block is "solid". Suppressed due to Werror
		public boolean canSpawn(Level level, ChunkAccess chunk, BlockPos pos, BlockState blockState, BlockState surfaceBlockState) {
			return !blockState.isSolid() && surfaceBlockState.isSolid();
		}

		@Override
		public void tick(Level level, BlockPos pos) { }

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
		public Mob createEntity(Level level) {
			try {
				return this.entityCtor.apply(level);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		public final Class<? extends Mob> getEntityType() {
			return this.entityType;
		}

		public final Function<Level, ? extends Mob> getEntityCtor() {
			return this.entityCtor;
		}

		@Override
		public void onSpawned(Mob entity) { }
	}

	public void populate(ServerLevel level, boolean spawnHostiles, boolean spawnAnimals) {
		int totalLevelEntityCount = 0;
		for(Entity entity : level.getAllEntities()) {
			if(entity instanceof LivingEntity) {
				totalLevelEntityCount++;
			}
		}

		if(totalLevelEntityCount >= this.getHardEntityLimit()) {
			//Hard limit reached, don't spawn any more entities
			return;
		}

		this.updateSpawnerChunks(level, this.eligibleChunksForSpawning);

		if(this.eligibleChunksForSpawning.isEmpty()) {
			//No spawning chunks
			return;
		}

		List<ChunkPos> spawnerChunks = new ArrayList<>(this.eligibleChunksForSpawning.size());

		//Add valid chunks
		for(ChunkPos chunkPos : this.eligibleChunksForSpawning) {
			//Don't load chunks
			if(level.isLoaded(new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16))) {
				spawnerChunks.add(chunkPos);
			}
		}

		//this.updateEntityCounts(level, this.entityCounts); TODO see method
		int totalEligibleEntityCount = 0;
		for(int count : this.entityCounts.values()) {
			totalEligibleEntityCount += count;
		}

		int maxEntitiesForLoadedArea = Math.min(this.getHardEntityLimit(), (int) (spawnerChunks.size() * this.getMaxEntitiesPerSpawnChunkFraction(spawnerChunks.size())));

		if(totalEligibleEntityCount >= maxEntitiesForLoadedArea) {
			//Too many entities, don't spawn any more entities
			return;
		}

		Collections.shuffle(spawnerChunks);

		//The approximate number of loaded areas (one area is the area loaded by one player)
		float loadedAreas = Math.max(1.0f, this.getLoadedAreasCount(spawnerChunks.size()));

		for(ChunkPos chunkPos : spawnerChunks) {
			this.populateChunk(level, chunkPos, spawnHostiles, spawnAnimals, true, false,
					this.getSpawningAttempsPerChunk(), this.getMaxSpawnsPerChunk(), this.getSpawningAttemptsPerGroup(), maxEntitiesForLoadedArea, loadedAreas);
		}
	}

	public int populateChunk(ServerLevel level, ChunkPos chunkPos, boolean spawnHostiles, boolean spawnAnimals, boolean loadChunks, boolean ignoreRestrictions,
			int attemptsPerChunk, int maxSpawnsPerChunk, int attemptsPerGroup, int entityLimit, float loadedAreas) {
		loadedAreas = Math.max(1.0f, loadedAreas);

		int attempts = 0, chunkSpawnedEntities = 0;

		spawnLoop:
			while(attempts < attemptsPerChunk && chunkSpawnedEntities < maxSpawnsPerChunk) {
				attempts++;
				BlockPos spawnPos = this.getRandomSpawnPosition(level, chunkPos);

				if(!this.isInsideSpawningArea(level, spawnPos, false)) {
					continue;
				}

				Holder<Biome> biome = level.getBiome(spawnPos);

				int totalBaseWeight = 0;
				int totalWeight = 0;

				//Get possible spawn entries and update weights
				List<CustomSpawnEntry> possibleSpawns = new ArrayList<>(this.getSpawnEntries(level, spawnPos, biome instanceof CustomSpawnEntriesProvider ? (CustomSpawnEntriesProvider) biome : null));

				Iterator<CustomSpawnEntry> spawnEntriesIT = possibleSpawns.iterator();
				while(spawnEntriesIT.hasNext()) {
					CustomSpawnEntry spawnEntry = spawnEntriesIT.next();

					if((spawnEntry.isHostile() && !spawnHostiles) || (!spawnEntry.isHostile() && !spawnAnimals)) {
						spawnEntriesIT.remove();
						continue;
					}

					spawnEntry.tick(level, spawnPos);
					totalBaseWeight += spawnEntry.getBaseWeight();
					totalWeight += spawnEntry.getWeight();
				}

				if(possibleSpawns.isEmpty() || totalWeight == 0 || totalBaseWeight == 0) {
					continue;
				}

				WeightedList<CustomSpawnEntry> weightedPossibleSpawns = new WeightedList<>();
				weightedPossibleSpawns.addAll(possibleSpawns);
				weightedPossibleSpawns.recalculateWeight();

				CustomSpawnEntry spawnEntry = weightedPossibleSpawns.getRandomItem(level.getRandom());
				if(spawnEntry == null) {
					continue;
				}

				int dynamicLimitBase = Mth.ceil((double)entityLimit / (double)totalBaseWeight * spawnEntry.getBaseWeight());
				int dynamicLimit = Mth.ceil((double)entityLimit / (double)totalWeight * spawnEntry.getWeight());

				int spawnEntityCount = this.entityCounts.getInt(spawnEntry.getEntityType());

				int spawnEntityCountLimit = this.strictDynamicLimit ? Math.min(dynamicLimit, dynamicLimitBase) : Math.max(dynamicLimit, dynamicLimitBase);

				if(spawnEntityCount >= spawnEntityCountLimit || (spawnEntry.getWorldLimit() >= 0 && spawnEntityCount >= spawnEntry.getWorldLimit())) {
					//Entity reached level spawning limit
					continue;
				}

				int desiredGroupSize = spawnEntry.getMinGroupSize() + level.getRandom().nextInt(spawnEntry.getMaxGroupSize() - spawnEntry.getMinGroupSize() + 1);
				double groupCheckRadius = spawnEntry.getSpawnCheckRadius();
				//Check whether chunks are loaded in the check radius, prevents entities from spawning somewhere even though the group limit was already reached in an unloaded chunk
				int csx = Mth.floor(spawnPos.getX() - groupCheckRadius) >> 4;
				int cex = Mth.floor(spawnPos.getX() + groupCheckRadius) >> 4;
				int csz = Mth.floor(spawnPos.getZ() - groupCheckRadius) >> 4;
				int cez = Mth.floor(spawnPos.getZ() + groupCheckRadius) >> 4;
				for (int cx = csx; cx <= cex; ++cx) {
					for (int cz = csz; cz <= cez; ++cz) {
						if(level.getChunkSource().getChunkNow(cx, cz) == null && (cx != chunkPos.x || cz != chunkPos.z)) {
							continue spawnLoop;
						}
					}
				}
				double groupSpawnRadius = spawnEntry.getGroupSpawnRadius();
				Class<? extends Entity> entityType = spawnEntry.getEntityType();
				boolean checkExistingGroups = spawnEntry.shouldCheckExistingGroups();

				if(checkExistingGroups) {
					List<? extends Entity> foundGroupEntities = level.getEntitiesOfClass(entityType, new AABB(
							spawnPos.getX() - groupCheckRadius, spawnPos.getY() - spawnEntry.getSpawnCheckRangeY(), spawnPos.getZ() - groupCheckRadius,
							spawnPos.getX() + groupCheckRadius, spawnPos.getY() + spawnEntry.getSpawnCheckRangeY(), spawnPos.getZ() + groupCheckRadius));
					for(Entity foundGroupEntity : foundGroupEntities) {
						if(foundGroupEntity.distanceToSqr(spawnPos.getX(), foundGroupEntity.getY() + (spawnPos.getY() - foundGroupEntity.getY()) / spawnEntry.getSpawnCheckRangeY() * groupCheckRadius, spawnPos.getZ()) <= groupCheckRadius) {
							desiredGroupSize--;
						}
					}
				}

				if(desiredGroupSize > 0) {
					int groupSpawnedEntities = 0, groupSpawnAttempts = 0;
					int maxGroupSpawnAttempts = attemptsPerGroup + desiredGroupSize * 2;

					BiomeSpawnEntriesData spawnEntriesData = this.getSpawnEntriesData(level, spawnPos, biome instanceof CustomSpawnEntriesProvider ? (CustomSpawnEntriesProvider) biome : null);
					long lastSpawn = spawnEntriesData != null ? spawnEntriesData.getLastSpawn(spawnEntry) : -1;

					if(!ignoreRestrictions && lastSpawn >= 0) {
						//Adjust intervals for MP when there are multiple players and the loaded area is bigger -> smaller intervals
						int adjustedInterval = (int)(spawnEntry.getSpawningInterval() / loadedAreas);
						if(spawnEntriesData != null && level.getGameTime() - lastSpawn < adjustedInterval) {
							//Too early, don't spawn yet
							continue;
						}
					}

					SpawnGroupData groupData = null;

					Mob cachedEntity = null;

					while(groupSpawnAttempts++ < maxGroupSpawnAttempts && groupSpawnedEntities < desiredGroupSize) {
						BlockPos entitySpawnPos = this.getRandomSpawnPosition(level, spawnPos, Mth.floor(groupSpawnRadius));

						boolean inChunk = (entitySpawnPos.getX() >> 4) == chunkPos.x && (entitySpawnPos.getZ() >> 4) == chunkPos.z;

						if(!loadChunks && !inChunk) {
							continue;
						}

						if(!this.isInsideSpawningArea(level, entitySpawnPos, false)) {
							continue;
						}

						BlockState spawnBlockState = level.getBlockState(entitySpawnPos);

						int spawnSegmentY = entitySpawnPos.getY() / 16;
						ChunkAccess spawnChunk = level.getChunk(entitySpawnPos);
						//TODO: Reimplement this logic
						/*ClassInstanceMultiMap<Entity>[] entityLists = spawnChunk.getEntityLists();
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
								if(spawnEntry.getSubChunkLimit() >= 0 && subChunkEntityCount >= spawnEntry.getSubChunkLimit()) {
									//Entity reached sub chunk limit
									continue;
								}
							}
						}

						if(spawnEntry.getChunkLimit() >= 0 && chunkEntityCount >= spawnEntry.getChunkLimit()) {
							//Entity reached chunk limit
							continue;
						}*/

						BlockState surfaceBlockState = spawnChunk.getBlockState(BlockPos.containing(entitySpawnPos.getX() - spawnChunk.getPos().x * 16, entitySpawnPos.getY() - 1, entitySpawnPos.getZ() - spawnChunk.getPos().z * 16));

						if(spawnEntry.canSpawn(level, spawnChunk, entitySpawnPos, spawnBlockState, surfaceBlockState)) {
							double sx = entitySpawnPos.getX() + 0.5D;
							double sy = entitySpawnPos.getY();
							double sz = entitySpawnPos.getZ() + 0.5D;
							float yaw = level.getRandom().nextFloat() * 360.0F;

							Mob spawningEntity;

							//If a a previous attempt created an entity but it was not used then we
							//can reuse it for this attempt, since the spawnEntry doesn't change during group spawning
							if(cachedEntity != null) {
								spawningEntity = cachedEntity;
							} else {
								spawningEntity = cachedEntity = spawnEntry.createEntity(level);
							}

							if(spawningEntity != null) {
								spawningEntity.moveTo(sx, sy, sz, yaw, 0.0F);

								//TODO: this would be easy to reimplement, but events no longer have Results and there's no longer no collision checks
								/*Result canSpawn = EventHooks.finalizeMobSpawnSpawner(spawningEntity, level, (float)sx, (float)sy, (float)sz, null);
								if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && spawningEntity.check() && spawningEntity.isNotColliding())) {
									groupData = EventHooks.finalizeMobSpawn(spawningEntity, level, level.getCurrentDifficultyAt(BlockPos.containing(sx, sy, sz)), MobSpawnType.NATURAL, groupData);

									if(spawningEntity.isNotColliding()) {
										groupSpawnedEntities++;
										chunkSpawnedEntities++;

										level.addFreshEntity(spawningEntity);

										spawnEntry.onSpawned(spawningEntity);

										if(this.isCountedEntity(level, spawningEntity)) {
											this.entityCounts.put(spawningEntity.getClass(), 1);
										}

										//Entity was spawned so it can't be reused!
										cachedEntity = null;
									} else if(cachedEntity != null) {
										//Cached entity was onInitialSpawned but not spawned so it can't be reused and must be killed.
										cachedEntity.discard();
										cachedEntity = null;
									}

									if (groupSpawnedEntities >= spawningEntity.getMaxSpawnClusterSize())  {
										break;
									}
								}*/
							}
						}
					}

					if(cachedEntity != null) {
						cachedEntity.discard();
					}

					if(spawnEntriesData != null && !ignoreRestrictions && groupSpawnedEntities > 0) {
						spawnEntriesData.setLastSpawn(spawnEntry, level.getGameTime());
					}
				}
			}
		return chunkSpawnedEntities;
	}

	/**
	 * Generates a random position to potentially spawn a mob at
	 * @param level
	 * @param chunkPos
	 * @return
	 */
	protected BlockPos getRandomSpawnPosition(Level level, ChunkPos chunkPos) {
		ChunkAccess chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
		int x = chunkPos.x * 16 + level.getRandom().nextInt(16);
		int z = chunkPos.z * 16 + level.getRandom().nextInt(16);
		int y = Math.min(level.getRandom().nextInt(chunk == null ? level.getMaxBuildHeight() : chunk.getHighestFilledSectionIndex() + 16 - 1), 256);
		return new BlockPos(x, y, z);
	}

	/**
	 * Generates a random offset position from a group spawn position
	 * @param level
	 * @param centerPos
	 * @param radius
	 * @return
	 */
	protected BlockPos getRandomSpawnPosition(Level level, BlockPos centerPos, int radius) {
		return new BlockPos(
				centerPos.getX() + level.getRandom().nextInt(radius*2) - radius,
				Mth.clamp(centerPos.getY() + level.getRandom().nextInt(4) - 2, 1, level.getHeight()),
				centerPos.getZ() + level.getRandom().nextInt(radius*2) - radius);
	}

	private final Set<ChunkPos> eligibleChunksForSpawning = new HashSet<>();

	/**
	 * Finds all chunks that are eligible for mob spawning and updates the specified set accordingly
	 * @param level
	 * @param spawnerChunks
	 */
	protected abstract void updateSpawnerChunks(ServerLevel level, Set<ChunkPos> spawnerChunks);

	private final Object2IntMap<Class<? extends Entity>> entityCounts = new Object2IntOpenHashMap<>();

	/**
	 * TODO: Reimplement
	 * Updates the entity counts of the spawner chunks
	 * @param level
	 * @param //entityCounts
	 */
	/*protected void updateEntityCounts(Level level, Object2IntMap<Class<? extends Entity>> entityCounts) {
		entityCounts.clear();

		for(ChunkPos chunkPos : this.eligibleChunksForSpawning) {
			if(level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) != null) {
				ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
				ClassInstanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();

				for(ClassInstanceMultiMap<Entity> entityList : entityLists) {
					for(Entity entity : entityList) {
						if(this.isCountedEntity(level, entity)) {
							entityCounts.put(entity.getClass(), 1);
						}
					}
				}
			}
		}
	}*/

	private boolean isCountedEntity(Level level, Entity entity) {
		return entity instanceof LivingEntity && this.isInsideSpawningArea(level, entity.blockPosition(), true) && (this.entityCountFilter == null || this.entityCountFilter.test((LivingEntity) entity));
	}
}
