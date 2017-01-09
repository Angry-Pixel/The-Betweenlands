package thebetweenlands.common.tile.spawner;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;

public abstract class MobSpawnerLogicBetweenlands {
	/*** The delay to spawn. */
	public int spawnDelay = 20;
	private String entityTypeName = "Pig";
	public double entityRotation;
	public double lastEntityRotation;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	/**
	 * A counter for spawn tries.
	 */
	private int spawnCount = 4;
	private Entity cachedEntity;
	private int maxNearbyEntities = 6;
	/**
	 * The distance from which a player activates the spawner.
	 */
	private int activatingRangeFromPlayer = 16;
	/**
	 * The range coefficient for spawning entities around.
	 */
	private int spawnRange = 4;
	private double checkRange = 8.0D;
	private boolean hasParticles = true;

	private boolean spawnInAir = true;

	/**
	 * Gets the entity name that should be spawned.
	 */
	public String getEntityNameToSpawn() {
		return this.entityTypeName;
	}

	/**
	 * Sets whether entities can spawn in the air
	 * @param spawnInAir
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setSpawnInAir(boolean spawnInAir) {
		this.spawnInAir = spawnInAir;
		return this;
	}

	/**
	 * Returns whether entities can spawn in the air
	 * @return
	 */
	public boolean canSpawnInAir() {
		return this.spawnInAir;
	}

	/**
	 * Sets whether the spawner creates particles
	 * @param hasParticles
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setHasParticles(boolean hasParticles) {
		this.hasParticles = hasParticles;
		return this;
	}

	/**
	 * Returns whether the spawner creates particles
	 * @return
	 */
	public boolean hasParticles() {
		return this.hasParticles;
	}

	/**
	 * Sets the maximum allowed entities within the check radius
	 * @param maxEntities
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setMaxEntities(int maxEntities) {
		this.maxNearbyEntities = maxEntities;
		return this;
	}

	/**
	 * Returns the maximum allowed entities within the spawn radius
	 * @return
	 */
	public int getMaxEntities() {
		return this.maxNearbyEntities;
	}

	/**
	 * Sets the spawn delay
	 * @param minDelay
	 * @param maxDelay
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setDelay(int minDelay, int maxDelay) {
		this.minSpawnDelay = minDelay;
		this.maxSpawnDelay = maxDelay;
		return this;
	}

	/**
	 * Returns the mimumum spawn delay
	 * @return
	 */
	public int getMinDelay() {
		return this.minSpawnDelay;
	}

	/**
	 * Returns the maximum spawn delay
	 * @return
	 */
	public int getMaxDelay() {
		return this.maxSpawnDelay;
	}

	/**
	 * Sets the entity name
	 * @param name
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setEntityName(String name) {
		this.entityTypeName = name;
		return this;
	}

	/**
	 * Sets the spawn range
	 * @param range
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setSpawnRange(int range) {
		this.spawnRange = range;
		return this;
	}

	/**
	 * Returns the spawn range
	 * @return
	 */
	public int getSpawnRange() {
		return this.spawnRange;
	}

	/**
	 * Sets the check range
	 * @param range
	 * @return
	 */
	public MobSpawnerLogicBetweenlands setCheckRange(double range) {
		this.checkRange = range;
		return this;
	}

	/**
	 * Returns the check range
	 * @return
	 */
	public double getCheckRange() {
		return this.checkRange;
	}

	/**
	 * Returns true if there's a player close enough to this mob spawner to activate it.
	 */
	public boolean isActivated() {
		return this.getSpawnerWorld().getClosestPlayer((double) this.getSpawnerX() + 0.5D, (double) this.getSpawnerY() + 0.5D, (double) this.getSpawnerZ() + 0.5D, (double) this.activatingRangeFromPlayer, false) != null;
	}

	/**
	 * Updates the spawner logic
	 */
	public void updateSpawner() {
		if (this.isActivated()) {
			if (this.getSpawnerWorld().isRemote) {
				if (this.spawnDelay > 0) {
					--this.spawnDelay;
				}

				if (this.hasParticles()) {
					double rx = (double) (this.getSpawnerWorld().rand.nextFloat());
					double ry = (double) (this.getSpawnerWorld().rand.nextFloat());
					double rz = (double) (this.getSpawnerWorld().rand.nextFloat());
					double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
					BLParticles.PORTAL.spawn(this.getSpawnerWorld(),
							(float) this.getSpawnerX() + rx, (float) this.getSpawnerY() + ry, (float) this.getSpawnerZ() + rz,
							ParticleFactory.ParticleArgs.get().withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D));
				}

				this.lastEntityRotation = this.entityRotation;
				this.entityRotation = (this.entityRotation + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
			} else {
				if (this.spawnDelay == -1) {
					this.resetTimer();
				}

				if (this.spawnDelay > 0) {
					--this.spawnDelay;
					return;
				}

				boolean entitySpawned = false;
				for (int i = 0; i < this.spawnCount; ++i) {
					Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());

					if (entity == null) {
						return;
					}

					List<Entity> entitiesInReach = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), new AxisAlignedBB((double) this.getSpawnerX(), (double) this.getSpawnerY(), (double) this.getSpawnerZ(), (double) (this.getSpawnerX() + 1), (double) (this.getSpawnerY() + 1), (double) (this.getSpawnerZ() + 1)).expand(this.checkRange, this.checkRange, this.checkRange));
					int nearbyEntities = 0;
					for (Entity e : entitiesInReach) {
						if (e.getDistance(this.getSpawnerX() + 0.5D, this.getSpawnerY() + 0.5D, this.getSpawnerZ() + 0.5D) <= this.checkRange)
							nearbyEntities++;
					}

					if (nearbyEntities >= this.maxNearbyEntities) {
						this.resetTimer();
						return;
					}

					double rx = 1.0D - this.getSpawnerWorld().rand.nextDouble() * 2.0D;
					double ry = 1.0D - this.getSpawnerWorld().rand.nextDouble() * 2.0D;
					double rz = 1.0D - this.getSpawnerWorld().rand.nextDouble() * 2.0D;
					double len = Math.sqrt(rx*rx + ry*ry + rz*rz);
					rx = this.getSpawnerX() + rx / len * this.spawnRange;
					ry = this.getSpawnerY() + ry / len * this.spawnRange;
					rz = this.getSpawnerZ() + rz / len * this.spawnRange;

					if(this.canSpawnInAir() || !this.getSpawnerWorld().isAirBlock(new BlockPos(rx, ry, rz).down())) {
						EntityLiving entityLiving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
						entity.setLocationAndAngles(rx, ry, rz, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

						if (entityLiving == null || ForgeEventFactory.canEntitySpawnSpawner(entityLiving, getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ)) {
							if (entityLiving != null) {
								if (!ForgeEventFactory.doSpecialSpawn(entityLiving, this.getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ)) {
									((EntityLiving)entity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
								}
							}

							AnvilChunkLoader.spawnEntity(entity, this.getSpawnerWorld());
							this.getSpawnerWorld().playEvent(2004, entity.getPosition(), 0);

							if (entityLiving != null) {
								entityLiving.spawnExplosionParticle();
							}

							entitySpawned = true;
						}
					}
				}
				if (entitySpawned) {
					this.resetTimer();
				}
			}
		}
	}

	/**
	 * Spawns an entity in the world
	 *
	 * @param entity
	 * @return
	 */
	public Entity spawnEntity(Entity entity) {
		((EntityLiving) entity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
		this.getSpawnerWorld().spawnEntityInWorld(entity);
		return entity;
	}

	/**
	 * Resets the timer
	 */
	private void resetTimer() {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			int i = this.maxSpawnDelay - this.minSpawnDelay;
			this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
		}

		this.sendBlockEvent(1);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		this.entityTypeName = nbt.getString("EntityId");
		this.spawnDelay = nbt.getShort("Delay");

		if (nbt.hasKey("MinSpawnDelay", 99)) {
			this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
			this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
			this.spawnCount = nbt.getShort("SpawnCount");
		}

		if (nbt.hasKey("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
			this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
		}

		if (nbt.hasKey("SpawnRange", 99)) {
			this.spawnRange = nbt.getShort("SpawnRange");
		}

		if (nbt.hasKey("CheckRange", 99)) {
			this.checkRange = nbt.getDouble("CheckRange");
		}

		if (nbt.hasKey("HasParticles")) {
			this.hasParticles = nbt.getBoolean("HasParticles");
		}

		if(nbt.hasKey("SpawnInAir")) {
			this.spawnInAir = nbt.getBoolean("SpawnInAir");
		}

		if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
			this.cachedEntity = null;
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("EntityId", this.getEntityNameToSpawn());
		nbt.setShort("Delay", (short) this.spawnDelay);
		nbt.setShort("MinSpawnDelay", (short) this.minSpawnDelay);
		nbt.setShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
		nbt.setShort("SpawnCount", (short) this.spawnCount);
		nbt.setShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
		nbt.setShort("RequiredPlayerRange", (short) this.activatingRangeFromPlayer);
		nbt.setShort("SpawnRange", (short) this.spawnRange);
		nbt.setDouble("CheckRange", this.checkRange);
		nbt.setBoolean("HasParticles", this.hasParticles);
		nbt.setBoolean("SpawnInAir", this.spawnInAir);
	}

	/**
	 * Sets the delay to minDelay if parameter given is 1, else return false.
	 */
	public boolean setDelayToMin(int event) {
		if (event == 1 && this.getSpawnerWorld().isRemote) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public Entity getCachedEntity() {
		if (this.cachedEntity != null) {
			if (!EntityList.getEntityString(this.cachedEntity).equals(this.getEntityNameToSpawn()))
				this.cachedEntity = null;
		}
		if (this.cachedEntity == null) {
			Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), (World) this.getSpawnerWorld());
			this.cachedEntity = entity;
		}
		return this.cachedEntity;
	}

	public abstract void sendBlockEvent(int event);

	public abstract World getSpawnerWorld();

	public abstract int getSpawnerX();

	public abstract int getSpawnerY();

	public abstract int getSpawnerZ();
}
