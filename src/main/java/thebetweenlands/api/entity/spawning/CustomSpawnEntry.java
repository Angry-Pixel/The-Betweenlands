package thebetweenlands.api.entity.spawning;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface CustomSpawnEntry extends WeightProvider {
	/**
	 * Returns the ID of this spawn entry
	 * @return
	 */
	ResourceLocation getID();

	/**
	 * Returns whether the data of this spawn entry should be saved
	 * @return
	 */
	boolean isSaved();

	/**
	 * Returns whether an entity can spawn based on the spawning position and the surface block below
	 * @param level
	 * @param pos
	 * @param blockState The block where the entity will spawn
	 * @param surfaceBlockState The block below where the entity will spawn
	 */
	boolean canSpawn(Level level, ChunkAccess chunk, BlockPos pos, BlockState blockState, BlockState surfaceBlockState);

	/**
	 * Updates the spawning data based on the spawning position
	 * @param level
	 * @param pos
	 * @return
	 */
	void tick(Level level, BlockPos pos);

	/**
	 * Returns the weight of this spawn entry
	 * @return
	 */
	@Override
	short getWeight();

	/**
	 * Sets the weight of this spawn entry
	 * @param weight
	 * @return
	 */
	CustomSpawnEntry setWeight(short weight);

	/**
	 * Sets the spawning interval
	 * @param interval
	 * @return
	 */
	CustomSpawnEntry setSpawningInterval(int interval);

	/**
	 * Returns the spawning interval
	 * @return
	 */
	int getSpawningInterval();

	/**
	 * Returns the initial base weight
	 * @return
	 */
	short getBaseWeight();

	/**
	 * Sets the desired minimum and maximum group size. The minimum desired group size
	 * may not always be achieved depending on the area around the initial spawn point.
	 * @param min
	 * @param max
	 * @return
	 */
	CustomSpawnEntry setGroupSize(int min, int max);

	/**
	 * Returns the maximum group size
	 * @return
	 */
	int getMaxGroupSize();

	/**
	 * Returns the minimum group size
	 * @return
	 */
	int getMinGroupSize();

	/**
	 * Returns the entity limit per chunk
	 * @return
	 */
	int getChunkLimit();

	/**
	 * Sets the entity limit per chunk
	 * @param limit
	 * @return
	 */
	CustomSpawnEntry setChunkLimit(int limit);

	/**
	 * Returns the entity limit per world
	 * @return
	 */
	int getWorldLimit();

	/**
	 * Sets the entity limit per world
	 * @param limit
	 * @return
	 */
	CustomSpawnEntry setWorldLimit(int limit);

	/**
	 * Returns the entity limit per cubic chunk
	 * @return
	 */
	int getSubChunkLimit();

	/**
	 * Sets the entity limit per cubic chunk
	 * @param limit
	 * @return
	 */
	CustomSpawnEntry setSubChunkLimit(int limit);

	/**
	 * Sets whether the entity is hostile
	 * @param hostile
	 * @return
	 */
	CustomSpawnEntry setHostile(boolean hostile);

	/**
	 * Returns whether the entity is hostile
	 * @return
	 */
	boolean isHostile();

	/**
	 * Sets the group size spawn check radius
	 * @param radius
	 * @return
	 */
	CustomSpawnEntry setSpawnCheckRadius(double radius);

	/**
	 * Returns the group size spawn check radius
	 * @return
	 */
	double getSpawnCheckRadius();

	/**
	 * Sets the group size spawn check range in Y direction
	 * @param y
	 * @return
	 */
	CustomSpawnEntry setSpawnCheckRangeY(double y);

	/**
	 * Returns the group size spawn check range in Y direction
	 * @return
	 */
	double getSpawnCheckRangeY();

	/**
	 * Sets the group spawn radius
	 * @param radius
	 * @return
	 */
	CustomSpawnEntry setGroupSpawnRadius(double radius);

	/**
	 * Returns the group spawn radius
	 * @return
	 */
	double getGroupSpawnRadius();

	/**
	 * Returns whether already existing entity should be taken into account when spawning groups
	 * @return
	 */
	boolean shouldCheckExistingGroups();

	/**
	 * Creates a new entity
	 * @param world
	 * @return
	 */
	Mob createEntity(Level world);

	/**
	 * Returns the entity type
	 * @return
	 */
	Class<? extends Mob> getEntityType();

	/**
	 * Called when the entity is spawned
	 * @param entity
	 */
	void onSpawned(Mob entity);
}
