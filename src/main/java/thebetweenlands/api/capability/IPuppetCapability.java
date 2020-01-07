package thebetweenlands.api.capability;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IPuppetCapability {
	/**
	 * Sets the entity's puppetteer
	 * @param puppeteer
	 */
	public void setPuppeteer(@Nullable Entity puppeteer);

	/**
	 * Returns whether this entity has a puppetteer
	 * @return
	 */
	public boolean hasPuppeteer();

	/**
	 * Returns this entity's puppetteer
	 * @return
	 */
	public Entity getPuppeteer();

	/**
	 * Sets how many ticks remaining
	 * @param ticks
	 */
	public void setRemainingTicks(int ticks);

	/**
	 * Returns how many ticks remaining
	 * @return
	 */
	public int getRemainingTicks();

	/**
	 * Sets whether this entity should stay still and not move
	 * @param stay
	 */
	public default void setStay(boolean stay) {

	}

	/**
	 * Returns whether this entity should stay still and not move
	 * @return
	 */
	public default boolean getStay() {
		return false;
	}
	
	/**
	 * Returns whether this entity should guard the nearby area and
	 * attack any hostile mob
	 * @param guard
	 * @param pos Home position. Entity will return to this position if it gets too far away
	 */
	public default void setGuard(boolean guard, @Nullable BlockPos pos) {
		
	}
	
	/**
	 * Returns whether this entity should guard the nearby area and
	 * attack any hostile mob
	 * @return
	 */
	public default boolean getGuard() {
		return false;
	}
	
	/**
	 * Returns the guard home position
	 * @return
	 */
	@Nullable
	public default BlockPos getGuardHome() {
		return null;
	}

	/**
	 * Sets the uuid of the ring that this entity was recruited with
	 * @param uuid
	 */
	public default void setRingUuid(@Nullable UUID uuid) {

	}

	/**
	 * Returns the uuid of the ring that this entity was recruited with.
	 * Used to ensure that unloaded entities that are no longer supposed to be linked
	 * can unlink themselves when loaded again.
	 * @return
	 */
	@Nullable
	public default UUID getRingUuid() {
		return null;
	}

	/**
	 * Sets how much the recruitment of this entity costed
	 * @param cost
	 */
	public default void setRecruitmentCost(int cost) {

	}

	/**
	 * Returns how much the recruitment of this entity costed.
	 * Used to refund the cost when unlinking or the entity dies while linked.
	 * @return
	 */
	public default int getRecruitmentCost() {
		return 0;
	}
}
