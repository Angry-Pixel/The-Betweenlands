package thebetweenlands.api.entity.spawning;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface ICustomSpawnEntry extends IWeightProvider {
	/**
	 * Returns the ID of this spawn entry
	 * @return
	 */
	public ResourceLocation getID();
	
	/**
	 * Returns whether the data of this spawn entry should be saved
	 * @return
	 */
	public boolean isSaved();

	/**
	 * Returns whether an entity can spawn based on the spawning position and the surface block below
	 * @param world
	 * @param pos
	 * @param blockState The block where the entity will spawn
	 * @param surfaceBlockState The block below where the entity will spawn
	 */
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState);

	/**
	 * Updates the spawning data based on the spawning position
	 * @param world
	 * @param pos
	 * @return
	 */
	public void update(World world, BlockPos pos);

	/**
	 * Returns the weight of this spawn entry
	 * @return
	 */
	@Override
	public short getWeight();

	/**
	 * Sets the weight of this spawn entry
	 * @param weight
	 * @return
	 */
	public ICustomSpawnEntry setWeight(short weight);

	/**
	 * Sets the spawning interval
	 * @param interval
	 * @return
	 */
	public ICustomSpawnEntry setSpawningInterval(int interval);

	/**
	 * Returns the spawning interval
	 * @return
	 */
	public int getSpawningInterval();

	/**
	 * Returns the initial base weight
	 * @return
	 */
	public short getBaseWeight();

	/**
	 * Sets the desired minimum and maximum group size. The minimum desired group size
	 * may not always be achieved depending on the area around the initial spawn point.
	 * @param min
	 * @param max
	 * @return
	 */
	public ICustomSpawnEntry setGroupSize(int min, int max);

	/**
	 * Returns the maximum group size
	 * @return
	 */
	public int getMaxGroupSize();

	/**
	 * Returns the minimum group size
	 * @return
	 */
	public int getMinGroupSize();

	/**
	 * Returns the entity limit per chunk
	 * @return
	 */
	public int getChunkLimit();

	/**
	 * Sets the entity limit per chunk
	 * @param limit
	 * @return
	 */
	public ICustomSpawnEntry setChunkLimit(int limit);

	/**
	 * Returns the entity limit per world
	 * @return
	 */
	public int getWorldLimit();

	/**
	 * Sets the entity limit per world
	 * @param limit
	 * @return
	 */
	public ICustomSpawnEntry setWorldLimit(int limit);

	/**
	 * Returns the entity limit per cubic chunk
	 * @return
	 */
	public int getSubChunkLimit();

	/**
	 * Sets the entity limit per cubic chunk
	 * @param limit
	 * @return
	 */
	public ICustomSpawnEntry setSubChunkLimit(int limit);

	/**
	 * Sets whether the entity is hostile
	 * @param hostile
	 * @return
	 */
	public ICustomSpawnEntry setHostile(boolean hostile);

	/**
	 * Returns whether the entity is hostile
	 * @return
	 */
	public boolean isHostile();

	/**
	 * Sets the group size spawn check radius
	 * @param radius
	 * @return
	 */
	public ICustomSpawnEntry setSpawnCheckRadius(double radius);

	/**
	 * Returns the group size spawn check radius
	 * @return
	 */
	public double getSpawnCheckRadius();

	/**
	 * Sets the group size spawn check range in Y direction
	 * @param y
	 * @return
	 */
	public ICustomSpawnEntry setSpawnCheckRangeY(double y);

	/**
	 * Returns the group size spawn check range in Y direction
	 * @return
	 */
	public double getSpawnCheckRangeY();

	/**
	 * Sets the group spawn radius
	 * @param radius
	 * @return
	 */
	public ICustomSpawnEntry setGroupSpawnRadius(double radius);

	/**
	 * Returns the group spawn radius
	 * @return
	 */
	public double getGroupSpawnRadius();

	/**
	 * Returns whether already existing entity should be taken into account when spawning groups
	 * @return
	 */
	public boolean shouldCheckExistingGroups();

	/**
	 * Creates a new entity
	 * @param world
	 * @return
	 */
	public EntityLiving createEntity(World world);

	/**
	 * Returns the entity type
	 * @return
	 */
	public Class<? extends EntityLiving> getEntityType();

	/**
	 * Called when the entity is spawned
	 * @param entity
	 */
	public void onSpawned(EntityLivingBase entity);
}
