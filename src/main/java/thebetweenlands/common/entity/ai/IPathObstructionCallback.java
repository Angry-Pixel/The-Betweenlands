package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityLiving;

public interface IPathObstructionCallback {
	/**
	 * Called when the mob tries to move along the path but is obstructed
	 */
	public void onPathingObstructed();

	/**
	 * Sets whether {@link EntityLiving#getPathPriority(net.minecraft.pathfinding.PathNodeType)} should return
	 * a cost malus for the given {@link net.minecraft.pathfinding.PathNodeType} or a path priority, i.e. to determine
	 * which {@link net.minecraft.pathfinding.PathNodeType} should be preferred at a certain block if multiple are available.
	 * @param on
	 */
	public default void setMalusMode(boolean on) {

	}
}
