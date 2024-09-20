package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.block.SimulacrumEffect;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class WispSimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (!level.isClientSide() && level.isEmptyBlock(pos.above()) && level.getGameTime() % 200 == 0 && BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.AURORAS)) {
			level.setBlockAndUpdate(pos.above(), BlockRegistry.WISP.get().defaultBlockState());
		}
	}
}
