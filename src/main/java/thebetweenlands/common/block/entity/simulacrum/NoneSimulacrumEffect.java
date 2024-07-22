package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.SimulacrumEffect;

public class NoneSimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		//NO-OP
	}
}
