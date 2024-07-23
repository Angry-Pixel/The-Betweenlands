package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.SimulacrumEffect;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class FertilitySimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (!level.isClientSide() && BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.HEAVY_RAIN)) {
			entity.getSpawner().serverTick((ServerLevel) level, pos);
		}
	}
}
