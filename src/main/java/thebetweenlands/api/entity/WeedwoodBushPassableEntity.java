package thebetweenlands.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface WeedwoodBushPassableEntity {

	/**
	 * Whether the weedwood bush should be considered solid or not by this entity at this time
	 * @param bushLevel
	 * @param bush
	 * @return
	 */
	public default boolean canPassThroughBush(LevelAccessor bushLevel, BlockPos bush) {
		return true;
	}
	
}
