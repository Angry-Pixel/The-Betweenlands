package thebetweenlands.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface WeedwoodBushPassableEntity {

	/**
	 * Whether the weedwood bush should be considered solid or not by this entity at this time
	 * @param level
	 * @param bush
	 * @return
	 */
	public default boolean canPassThroughBush(BlockGetter level, BlockPos bush) {
		return true;
	}
	
}
