package thebetweenlands.common.world.storage.location.guard;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ILocationGuard {
	/**
	 * Sets whether the specified position is guarded
	 *
	 * @param level   World
	 * @param pos     Position
	 * @param guarded Whether the block is guarded
	 * @return true if the guard state was successfully changed
	 */
	boolean setGuarded(Level level, BlockPos pos, boolean guarded);

	/**
	 * Returns whether the location is guarded at the specified position.
	 *
	 * @param level  World
	 * @param entity Entity that's checking for the guard
	 * @param pos    Position
	 * @return
	 */
	boolean isGuarded(Level level, @Nullable Entity entity, BlockPos pos);

	/**
	 * Clears all guards
	 *
	 * @param level
	 */
	void clear(Level level);

	/**
	 * Returns if the location is cleared
	 *
	 * @param level World
	 * @return
	 */
	boolean isClear(Level level);

	/**
	 * Handles explosions that affect the location
	 *
	 * @param level
	 * @param explosion
	 */
	void handleExplosion(Level level, Explosion explosion);

	/**
	 * Writes the guard to NBT
	 *
	 * @param nbt
	 * @return
	 */
	CompoundTag writeToNBT(CompoundTag nbt);

	/**
	 * Reads the guard from NBT
	 *
	 * @param nbt
	 */
	void readFromNBT(CompoundTag nbt);
}
