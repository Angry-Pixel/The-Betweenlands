package thebetweenlands.common.world.storage.location.guard;

import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.WorldGenLevel;

import javax.annotation.Nullable;

public interface ILocationGuard {
	/**
	 * Sets whether the specified position is guarded
	 *
	 * @param world   World
	 * @param pos     Position
	 * @param guarded Whether the block is guarded
	 * @return true if the the guard state was successfully changed
	 */
	public boolean setGuarded(WorldGenLevel world, BlockPos pos, boolean guarded);

	/**
	 * Returns whether the location is guarded at the specified position.
	 *
	 * @param world  World
	 * @param entity Entity that's checking for the guard
	 * @param pos    Position
	 * @return
	 */
	public boolean isGuarded(WorldGenLevel world, @Nullable Entity entity, BlockPos pos);

	/**
	 * Clears all guards
	 *
	 * @param world
	 */
	public void clear(WorldGenLevel world);

	/**
	 * Returns if the location is cleared
	 *
	 * @param world World
	 * @return
	 */
	public boolean isClear(WorldGenLevel world);

	/**
	 * Handles explosions that affect the location
	 *
	 * @param world
	 * @param explosion
	 */
	public void handleExplosion(WorldGenLevel world, Explosion explosion);

	/**
	 * Writes the guard to NBT
	 *
	 * @param nbt
	 * @return
	 */
	public NbtTagArgument writeToNBT(NbtTagArgument nbt);

	/**
	 * Reads the guard from NBT
	 *
	 * @param nbt
	 */
	public void readFromNBT(NbtTagArgument nbt);
}
