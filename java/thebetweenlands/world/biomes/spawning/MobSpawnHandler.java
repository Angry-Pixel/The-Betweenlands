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
import net.minecraft.entity.player.EntityPlayer;
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
import thebetweenlands.utils.IWeightProvider;
import thebetweenlands.utils.WeightedList;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

public class MobSpawnHandler {
	public static MobSpawnHandler INSTANCE = new MobSpawnHandler();

	private static final int HARD_ENTITY_LIMIT = 400;
	private static final byte SPAWN_CHUNK_DISTANCE = 6;
	private static final int SPAWNING_ATTEMPTS_PER_CHUNK = 40;
	private static final int SPAWNING_ATTEMPTS_PER_GROUP = 20;
	private static final int MAX_SPAWNS_PER_ATTEMPT = 4;

	/**
	 * Default spawning weight is 100
	 */
	public static class BLSpawnEntry implements IWeightProvider {
		private final Class<? extends EntityLiving> entityType;
		private final Constructor<? extends EntityLiving> entityCtor;
		private boolean hostile = false;
		private int subChunkLimit = -1;
		private int chunkLimit = -1;
		private int worldLimit = -1;
		private int minGroupSize = 1, maxGroupSize = 1;
		private short weight = 100;
		private double groupRadius = 6.0D;

		public BLSpawnEntry(Class<? extends EntityLiving> entityType) {
			this.entityType = entityType;
			try {
				this.entityCtor = this.entityType.getConstructor(World.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Can't find or access entity constructor (World) of entity: " + entityType.getName());
			}
		}

		public BLSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
			this(entityType);
			this.weight = weight;
		}

		/**
		 * Returns whether an entity can spawn based on the spawning position and the surface block below
		 * @param world
		 * @param x
		 * @param y
		 * @param z
		 * @param surfaceBlock
		 */
		protected boolean canSpawn(World world, int x, int y, int z, Block surfaceBlock) {
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

		public final BLSpawnEntry setGroupRadius(double radius) {
			this.groupRadius = radius;
			return this;
		}

		public final double getGroupRadius() {
			return this.groupRadius;
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

			//TODO:
			if(world.getGameRules().getGameRuleBooleanValue("doMobSpawning") && world.getTotalWorldTime() % 20 == 0) {
				long start = System.nanoTime();
				this.spawnMobs(world);
				//System.out.println("Time: " + (System.nanoTime() - start) / 1000000.0F);
			}
		}
	}

	private void spawnMobs(WorldServer world) {
		this.updateEntityCounts(world);
		int totalEntityCount = 0;
		for(int count : this.entityCounts.values()) {
			totalEntityCount += count;
		}
		if(totalEntityCount >= HARD_ENTITY_LIMIT) {
			//Hard limit reached, don't spawn any more entities
			return;
		}

		this.updateSpawnerChunks(world);

		if(this.eligibleChunksForSpawning.size() == 0) {
			//No spawning chunks
			return;
		}

		List<ChunkCoordIntPair> spawnerChunks = new ArrayList<ChunkCoordIntPair>(this.eligibleChunksForSpawning.keySet().size() / (SPAWN_CHUNK_DISTANCE*SPAWN_CHUNK_DISTANCE) * (SPAWN_CHUNK_DISTANCE-1) * 4);

		if(totalEntityCount >= this.eligibleChunksForSpawning.size()) {
			return;
		}

		//Add valid chunks
		for(Entry<ChunkCoordIntPair, Boolean> chunkEntry : this.eligibleChunksForSpawning.entrySet()) {
			if(chunkEntry.getValue()) spawnerChunks.add(chunkEntry.getKey());
		}

		Collections.shuffle(spawnerChunks);

		int attempts = 0, chunkSpawnedEntities = 0;

		for(ChunkCoordIntPair chunkPos : spawnerChunks) {
			while(attempts++ < SPAWNING_ATTEMPTS_PER_CHUNK && chunkSpawnedEntities < MAX_SPAWNS_PER_ATTEMPT) {
				ChunkPosition spawnPos = this.getRandomSpawnPosition(world, chunkPos);
				BiomeGenBase biome = world.getBiomeGenForCoords(spawnPos.chunkPosX, spawnPos.chunkPosZ);

				//TODO:
				if(world.rand.nextFloat() > biome.getSpawningChance() || biome instanceof BiomeGenBaseBetweenlands == false) 
					continue;

				Block centerSpawnBlock = world.getBlock(spawnPos.chunkPosX, spawnPos.chunkPosY, spawnPos.chunkPosZ);

				if(centerSpawnBlock.isNormalCube()) 
					continue;

				boolean spawnHostiles = true;
				boolean spawnAnimals = true;

				//Get possible spawn entries and update weights
				List<BLSpawnEntry> biomeSpawns = ((BiomeGenBaseBetweenlands)biome).getSpawnEntries();
				List<BLSpawnEntry> possibleSpawns = new ArrayList<BLSpawnEntry>();
				for(BLSpawnEntry spawnEntry : biomeSpawns) {
					if(!((spawnEntry.isHostile() && !spawnHostiles) || (!spawnEntry.isHostile() && !spawnAnimals))) {
						possibleSpawns.add(spawnEntry);
						spawnEntry.update(world, spawnPos.chunkPosX, spawnPos.chunkPosY, spawnPos.chunkPosZ);
					}
				}
				WeightedList<BLSpawnEntry> weightedPossibleSpawns = new WeightedList<BLSpawnEntry>();
				weightedPossibleSpawns.addAll(possibleSpawns);
				weightedPossibleSpawns.recalculateWeight();

				BLSpawnEntry spawnEntry = weightedPossibleSpawns.getRandomItem(world.rand);

				if(spawnEntry == null || spawnEntry.getWorldLimit() >= 0 && this.entityCounts.get(spawnEntry.entityType) >= spawnEntry.getWorldLimit())
					//Entity reached world spawning limit
					continue;

				int desiredGroupSize = spawnEntry.getMinGroupSize() + world.rand.nextInt(spawnEntry.getMaxGroupSize() - spawnEntry.getMinGroupSize() + 1);
				double groupRadius = spawnEntry.groupRadius;
				Class<? extends Entity> entityType = spawnEntry.entityType;
				boolean checkExistingGroups = spawnEntry.shouldCheckExistingGroups();

				if(checkExistingGroups) {
					List<Entity> foundGroupEntities = world.getEntitiesWithinAABB(entityType, AxisAlignedBB.getBoundingBox(
							spawnPos.chunkPosX - groupRadius, spawnPos.chunkPosY - 3, spawnPos.chunkPosZ - groupRadius, 
							spawnPos.chunkPosX + groupRadius, spawnPos.chunkPosY + 3, spawnPos.chunkPosZ + groupRadius));
					Iterator<Entity> foundGroupEntitiesIT = foundGroupEntities.iterator();
					while(foundGroupEntitiesIT.hasNext()) {
						Entity foundEntity = foundGroupEntitiesIT.next();
						if(foundEntity.getDistance(spawnPos.chunkPosX, foundEntity.posX, spawnPos.chunkPosZ) > groupRadius) {
							foundGroupEntitiesIT.remove();
						}
					}
					desiredGroupSize -= foundGroupEntities.size();
				}

				//System.out.println(spawnPos.chunkPosY);

				if(desiredGroupSize > 0) {
					int groupSpawnedEntities = 0, groupSpawnAttempts = 0;
					int maxGroupSpawnAttempts = SPAWNING_ATTEMPTS_PER_GROUP + desiredGroupSize * 2;

					groupIteration:
						while(groupSpawnAttempts++ < maxGroupSpawnAttempts && groupSpawnedEntities < desiredGroupSize) {
							ChunkPosition entitySpawnPos = this.getRandomSpawnPosition(world, spawnPos, MathHelper.floor_double(groupRadius));

							if(world.getClosestPlayer(entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY, entitySpawnPos.chunkPosZ, 24D) != null)
								continue;

							Block spawnBlock = world.getBlock(entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY, entitySpawnPos.chunkPosZ);

							if(spawnBlock.isNormalCube())
								continue;

							//System.out.println("Pr: " + spawnPos.chunkPosY);

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
										continue groupIteration;
								}
							}

							if(spawnEntry.getChunkLimit() >= 0 && chunkEntityCount >= spawnEntry.getChunkLimit())
								//Entity reached chunk limit
								continue;

							//System.out.println("Po: " + spawnPos.chunkPosY);

							Block surfaceBlock = world.getBlock(entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY - 1, entitySpawnPos.chunkPosZ);

							if(spawnEntry.canSpawn(world, entitySpawnPos.chunkPosX, entitySpawnPos.chunkPosY, entitySpawnPos.chunkPosZ, surfaceBlock)) {
								//System.out.println("Cs: " + spawnPos.chunkPosY);

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

										//System.out.println(newEntity);

										world.spawnEntityInWorld(newEntity);

										if (!ForgeEventFactory.doSpecialSpawn(newEntity, world, (float)sx, (float)sy, (float)sz)) {
											newEntity.onSpawnWithEgg(null);
										}

										if (groupSpawnedEntities >= ForgeEventFactory.getMaxSpawnPackSize(newEntity))  {
											continue groupIteration;
										}
									}
								}
							}
						}
				}
			}
		}
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
				centerPos.chunkPosY + world.rand.nextInt(4) - 2,
				centerPos.chunkPosZ + world.rand.nextInt(radius*2) - radius);
	}

	private Map<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning = new HashMap<ChunkCoordIntPair, Boolean>();

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
					boolean isOuterChunk = cox == -SPAWN_CHUNK_DISTANCE || cox == SPAWN_CHUNK_DISTANCE || coz == -SPAWN_CHUNK_DISTANCE || coz == SPAWN_CHUNK_DISTANCE;
					if(!isOuterChunk) {
						this.eligibleChunksForSpawning.put(chunkPos, false);
					} else if(!this.eligibleChunksForSpawning.containsKey(chunkPos)) {
						this.eligibleChunksForSpawning.put(chunkPos, true);
					}
				}
			}
		}
	}

	private TObjectIntHashMap<Class<? extends Entity>> entityCounts = new TObjectIntHashMap<Class<? extends Entity>>();

	private void updateEntityCounts(World world) {
		this.entityCounts.clear();
		for(Entity entity : (List<Entity>) world.loadedEntityList) {
			if(entity instanceof EntityLiving) {
				this.entityCounts.adjustOrPutValue(entity.getClass(), 1, 1);
			}
		}
	}
}
