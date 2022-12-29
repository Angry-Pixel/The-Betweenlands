package thebetweenlands.common.tile.spawner;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;

public abstract class MobSpawnerLogicBetweenlands {
    private final List<WeightedSpawnerEntity> entitySpawnList = new ArrayList<WeightedSpawnerEntity>();
    public double entityRotation;
    public double lastEntityRotation;
    private WeightedSpawnerEntity randomEntity = new WeightedSpawnerEntity();
    private Entity cachedEntity;
    private int spawnDelay = 20;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private int maxNearbyEntities = 6;
    private int activatingRangeFromPlayer = 16;
    private int spawnRange = 4;
    private double checkRange = 8.0D;
    private boolean hasParticles = true;
    private boolean spawnInAir = true;

    /**
     * Gets the entity name that should be spawned.
     */
    @Nullable
    public ResourceLocation getEntityId() {
        String s = this.randomEntity.getNbt().getString("id");
        return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
    }

    /**
     * Sets the entity name. Does not override NBT
     *
     * @param name
     * @return
     */
    public MobSpawnerLogicBetweenlands setNextEntityName(String name) {
        this.randomEntity.getNbt().setString("id", name);
        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
            this.cachedEntity = null;
        }
        return this;
    }

    /**
     * Sets the next entity to spawn
     *
     * @param entity
     */
    public MobSpawnerLogicBetweenlands setNextEntity(WeightedSpawnerEntity entity) {
        this.randomEntity = entity;
        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
            this.cachedEntity = null;
        }
        return this;
    }

    /**
     * Sets the next entity to spawn. Overrides NBT
     *
     * @param name
     */
    public MobSpawnerLogicBetweenlands setNextEntity(String name) {
        this.randomEntity = new WeightedSpawnerEntity();
        this.setNextEntityName(name);
        return this;
    }

    /**
     * Sets the entity spawn list
     *
     * @param entitySpawnList
     * @return
     */
    public MobSpawnerLogicBetweenlands setEntitySpawnList(List<WeightedSpawnerEntity> entitySpawnList) {
        this.entitySpawnList.clear();
        this.entitySpawnList.addAll(entitySpawnList);
        if (!this.entitySpawnList.isEmpty()) {
            this.setNextEntity((WeightedSpawnerEntity) WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.entitySpawnList));
        } else {
            this.setNextEntity(new WeightedSpawnerEntity());
        }
        return this;
    }

    /**
     * Sets whether entities can spawn in the air
     *
     * @param spawnInAir
     * @return
     */
    public MobSpawnerLogicBetweenlands setSpawnInAir(boolean spawnInAir) {
        this.spawnInAir = spawnInAir;
        return this;
    }

    /**
     * Returns whether entities can spawn in the air
     *
     * @return
     */
    public boolean canSpawnInAir() {
        return this.spawnInAir;
    }

    /**
     * Sets whether the spawner creates particles
     *
     * @param hasParticles
     * @return
     */
    public MobSpawnerLogicBetweenlands setParticles(boolean hasParticles) {
        this.hasParticles = hasParticles;
        return this;
    }

    /**
     * Returns whether the spawner creates particles
     *
     * @return
     */
    public boolean hasParticles() {
        return this.hasParticles;
    }

    /**
     * Returns the maximum allowed entities within the spawn radius
     *
     * @return
     */
    public int getMaxEntities() {
        return this.maxNearbyEntities;
    }

    /**
     * Sets the maximum allowed entities within the check radius
     *
     * @param maxEntities
     * @return
     */
    public MobSpawnerLogicBetweenlands setMaxEntities(int maxEntities) {
        this.maxNearbyEntities = maxEntities;
        return this;
    }

    /**
     * Sets the spawn delay range
     *
     * @param minDelay
     * @param maxDelay
     * @return
     */
    public MobSpawnerLogicBetweenlands setDelayRange(int minDelay, int maxDelay) {
        this.minSpawnDelay = minDelay;
        this.maxSpawnDelay = maxDelay;
        return this;
    }

    /**
     * Sets the current spawn delay
     *
     * @param delay
     * @return
     */
    public MobSpawnerLogicBetweenlands setDelay(int delay) {
        this.spawnDelay = delay;
        return this;
    }

    /**
     * Returns the mimumum spawn delay
     *
     * @return
     */
    public int getMinDelay() {
        return this.minSpawnDelay;
    }

    /**
     * Returns the maximum spawn delay
     *
     * @return
     */
    public int getMaxDelay() {
        return this.maxSpawnDelay;
    }

    /**
     * Returns the spawn range
     *
     * @return
     */
    public int getSpawnRange() {
        return this.spawnRange;
    }

    /**
     * Sets the spawn range
     *
     * @param range
     * @return
     */
    public MobSpawnerLogicBetweenlands setSpawnRange(int range) {
        this.spawnRange = range;
        return this;
    }

    /**
     * Returns the check range
     *
     * @return
     */
    public double getCheckRange() {
        return this.checkRange;
    }

    /**
     * Sets the check range
     *
     * @param range
     * @return
     */
    public MobSpawnerLogicBetweenlands setCheckRange(double range) {
        this.checkRange = range;
        return this;
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    public boolean isActivated() {
        return this.getSpawnerWorld().getClosestPlayer((double) this.getSpawnerX() + 0.5D, (double) this.getSpawnerY() + 0.5D, (double) this.getSpawnerZ() + 0.5D, (double) this.activatingRangeFromPlayer, false) != null;
    }

    /**
     * Sets the maximum number of entities to be spawned
     * @param count
     */
    public void setMaxSpawnCount(int count) {
    	this.spawnCount = count;
    }
    
    /**
     * Returns the maximum number of entities to be spawned
     * @return
     */
    public int getMaxSpawnCount() {
    	return this.spawnCount;
    }
    
    /**
     * Updates the spawner logic
     */
    public void updateSpawner() {
        if (!this.isActivated()) {
            this.lastEntityRotation = this.entityRotation;
        } else {
            if (this.getSpawnerWorld().isRemote) {
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                }

                if (this.hasParticles()) {
                    this.spawnParticles();
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

                int spawnCount = this.spawnCount > 1 ? this.getSpawnerWorld().rand.nextInt(this.spawnCount) + 1 : this.spawnCount;

                boolean entitySpawned = false;

                for (int i = 0; i < 128; ++i) {
                    if (spawnCount <= 0) {
                        break;
                    }

                    double rx = 1.0D - this.getSpawnerWorld().rand.nextDouble() * 2.0D;
                    double ry = 1.0D - this.getSpawnerWorld().rand.nextDouble() * 2.0D;
                    double rz = 1.0D - this.getSpawnerWorld().rand.nextDouble() * 2.0D;
                    double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
                    rx = this.getSpawnerX() + rx / len * this.spawnRange;
                    ry = this.getSpawnerY() + ry / len * this.spawnRange;
                    rz = this.getSpawnerZ() + rz / len * this.spawnRange;
                    NBTTagCompound entityNbt = this.randomEntity.getNbt();
                    NBTTagList posNbt = entityNbt.getTagList("Pos", 6);
                    World world = this.getSpawnerWorld();
                    int tags = posNbt.tagCount();
                    rx = tags >= 1 ? posNbt.getDoubleAt(0) : rx;
                    ry = tags >= 2 ? posNbt.getDoubleAt(1) : ry;
                    rz = tags >= 3 ? posNbt.getDoubleAt(2) : rz;
                    Entity entity = AnvilChunkLoader.readWorldEntityPos(entityNbt, world, rx, ry, rz, false);

                    if (entity == null) {
                        return;
                    }

                    List<Entity> entitiesInReach = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), new AxisAlignedBB((double) this.getSpawnerX(), (double) this.getSpawnerY(), (double) this.getSpawnerZ(), (double) (this.getSpawnerX() + 1), (double) (this.getSpawnerY() + 1), (double) (this.getSpawnerZ() + 1)).grow(this.checkRange, this.checkRange, this.checkRange));
                    int nearbyEntities = 0;
                    for (Entity e : entitiesInReach) {
                        if (e.getDistance(this.getSpawnerX() + 0.5D, this.getSpawnerY() + 0.5D, this.getSpawnerZ() + 0.5D) <= this.checkRange) {
                            nearbyEntities++;
                        }
                    }

                    if (nearbyEntities >= this.maxNearbyEntities) {
                        this.resetTimer();
                        return;
                    }

                    entity.setLocationAndAngles(rx, ry, rz, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

                    boolean canSpawn = this.canSpawnInAir() || entity.getEntityBoundingBox() == null;

                    //Check if entity can stand on block below and set position
                    if (!canSpawn) {
                        BlockPos down = new BlockPos(rx, ry, rz).down();
                        IBlockState blockState = this.getSpawnerWorld().getBlockState(down);
                        if (blockState.getBlock() != Blocks.AIR) {
                            AxisAlignedBB boundingBox = blockState.getCollisionBoundingBox(this.getSpawnerWorld(), down);
                            if (boundingBox != null) {
                                boundingBox = boundingBox.offset(down);
                                AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
                                if (boundingBox.intersects(entityBoundingBox.minX, boundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, boundingBox.maxY, entityBoundingBox.maxZ)) {
                                    RayTraceResult intercept = boundingBox.calculateIntercept(entity.getPositionVector(), entity.getPositionVector().add(0, -2, 0));
                                    if (intercept != null) {
                                        canSpawn = true;
                                        entity.setLocationAndAngles(entity.posX, intercept.hitVec.y + 0.1D, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                                    }
                                }
                            }
                        }
                    }

                    if (canSpawn) {
                        EntityLiving entityLiving = entity instanceof EntityLiving ? (EntityLiving) entity : null;

                        if (entityLiving == null || ForgeEventFactory.canEntitySpawnSpawner(entityLiving, getSpawnerWorld(), (float) entity.posX, (float) entity.posY, (float) entity.posZ)) {
                            if (entityLiving != null) {
                                if (!ForgeEventFactory.doSpecialSpawn(entityLiving, this.getSpawnerWorld(), (float) entity.posX, (float) entity.posY, (float) entity.posZ)) {
                                    ((EntityLiving) entity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
                                }
                            }

                            AnvilChunkLoader.spawnEntity(entity, this.getSpawnerWorld());
                            this.getSpawnerWorld().playEvent(2004, entity.getPosition(), 0);

                            if (entityLiving != null) {
                                entityLiving.spawnExplosionParticle();
                            }

                            spawnCount--;

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
     * Spawns the particles
     */
    protected void spawnParticles() {
        double rx = (double) (this.getSpawnerWorld().rand.nextFloat());
        double ry = (double) (this.getSpawnerWorld().rand.nextFloat());
        double rz = (double) (this.getSpawnerWorld().rand.nextFloat());
        double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
        BLParticles.SPAWNER.spawn(this.getSpawnerWorld(),
                (float) this.getSpawnerX() + rx, (float) this.getSpawnerY() + ry, (float) this.getSpawnerZ() + rz,
                ParticleFactory.ParticleArgs.get().withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D));
    }

    /**
     * Spawns an entity in the world
     *
     * @param entity
     * @return
     */
    public Entity spawnEntity(Entity entity) {
        ((EntityLiving) entity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
        this.getSpawnerWorld().spawnEntity(entity);
        return entity;
    }

    /**
     * Resets the timer
     */
    public void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
        }

        if (!this.entitySpawnList.isEmpty()) {
            this.setNextEntity((WeightedSpawnerEntity) WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.entitySpawnList));
        }

        this.broadcastEvent(1);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagCompound entityNbt = nbt.getCompoundTag("SpawnData");
        if (!entityNbt.hasKey("id", 8)) {
            entityNbt.setString("id", "Pig");
        }
        this.setNextEntity(new WeightedSpawnerEntity(1, entityNbt));
        this.spawnDelay = nbt.getShort("Delay");
        this.entitySpawnList.clear();
        if (nbt.hasKey("SpawnPotentials", 9)) {
            NBTTagList nbttaglist = nbt.getTagList("SpawnPotentials", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                this.entitySpawnList.add(new WeightedSpawnerEntity(nbttaglist.getCompoundTagAt(i)));
            }
        }
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
        if (nbt.hasKey("SpawnInAir")) {
            this.spawnInAir = nbt.getBoolean("SpawnInAir");
        }
        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
            this.cachedEntity = null;
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("SpawnData", this.randomEntity.getNbt().copy());
        NBTTagList entityNbtList = new NBTTagList();
        if (this.entitySpawnList.isEmpty()) {
            entityNbtList.appendTag(this.randomEntity.toCompoundTag());
        } else {
            for (WeightedSpawnerEntity weightedspawnerentity : this.entitySpawnList) {
                entityNbtList.appendTag(weightedspawnerentity.toCompoundTag());
            }
        }
        nbt.setTag("SpawnPotentials", entityNbtList);
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

        if (this.cachedEntity == null) {
            this.cachedEntity = AnvilChunkLoader.readWorldEntity(this.randomEntity.getNbt(), this.getSpawnerWorld(), false);

            if (this.randomEntity.getNbt().getSize() == 1 && this.randomEntity.getNbt().hasKey("id", 8) && this.cachedEntity instanceof EntityLiving) {
                ((EntityLiving) this.cachedEntity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(this.cachedEntity)), (IEntityLivingData) null);
            }
        }

        return this.cachedEntity;
    }

    public abstract void broadcastEvent(int event);

    public abstract World getSpawnerWorld();

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();
}
