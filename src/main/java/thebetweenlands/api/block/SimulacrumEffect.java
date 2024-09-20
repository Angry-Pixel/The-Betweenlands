package thebetweenlands.api.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;

public interface SimulacrumEffect {

	void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity);

	default String getDescriptionId() {
		return Util.makeDescriptionId("simulacrum_effect", BLRegistries.SIMULACRUM_EFFECTS.getKey(this));
	}
}
