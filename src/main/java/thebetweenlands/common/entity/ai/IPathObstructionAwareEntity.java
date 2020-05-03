package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IPathObstructionAwareEntity {
	/**
	 * Called when the mob tries to move along the path but is obstructed
	 */
	public void onPathingObstructed(EnumFacing facing);

	/**
	 * Returns how many ticks the mob can be stuck before the path is considered to be obstructed
	 * @return
	 */
	public default int getMaxStuckCheckTicks() {
		return 40;
	}

	/**
	 * Returns teh pathing malus for the given {@link PathNodeType} and block position
	 * @param type
	 * @param pos
	 * @return
	 */
	public default float getPathingMalus(EntityLiving entity, PathNodeType nodeType, BlockPos pos) {
		return entity.getPathPriority(nodeType);
	}
}
