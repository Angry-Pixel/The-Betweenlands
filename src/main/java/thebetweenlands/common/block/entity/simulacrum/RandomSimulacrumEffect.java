package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.block.SimulacrumEffect;
import thebetweenlands.common.registries.SimulacrumEffectRegistry;

public class RandomSimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (!level.isClientSide() && level.getGameTime() % 20 == 0 && level.getRandom().nextInt(200) == 0) {
			entity.setSecondaryEffect(BLRegistries.SIMULACRUM_EFFECTS.getRandom(level.getRandom()).map(Holder.Reference::value).orElse(SimulacrumEffectRegistry.NONE.get()));
		}
	}
}
