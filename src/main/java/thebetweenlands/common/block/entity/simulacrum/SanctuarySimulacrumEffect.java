package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.SimulacrumEffect;
import thebetweenlands.common.block.entity.RepellerBlockEntity;

public class SanctuarySimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		entity.setRadiusState(3);

		RepellerBlockEntity repeller = SimulacrumBlockEntity.getClosestActiveTile(RepellerBlockEntity.class, entity, level, pos.getX(), pos.getY(), pos.getZ(), 18.0D, null, null);

		if (repeller != null && repeller.getBlockPos().distToCenterSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > repeller.getRadius(1) * repeller.getRadius(1)) {
			repeller = null;
		}

		if (repeller instanceof SimulacrumBlockEntity simulacrum) {
			entity.sourceRepeller = repeller = simulacrum.sourceRepeller;
		} else {
			entity.sourceRepeller = repeller;
		}

		if (entity.sourceRepeller != null && (entity.sourceRepeller.isRemoved() || !level.isLoaded(entity.sourceRepeller.getBlockPos()))) {
			entity.sourceRepeller = null;

			if (repeller == entity.sourceRepeller) {
				repeller = null;
			}
		}

		int prevFuel = 0;

		if (repeller != null) {
			entity.setRunning(repeller.isRunning());
			entity.setShimmerstone(repeller.hasShimmerstone());
			prevFuel = entity.getFuel();
			entity.setFuel(repeller.getFuel());
		} else {
			entity.setRunning(false);
			entity.setShimmerstone(false);
			entity.emptyFuel();
		}

		RepellerBlockEntity.tick(level, pos, state, entity);

		if (repeller != null) {
			if (entity.getFuel() < prevFuel) {
				repeller.removeFuel(prevFuel - entity.getFuel());
				repeller.setChanged();
				entity.setChanged();
			}

			entity.setRunning(repeller.isRunning());
			entity.setShimmerstone(repeller.hasShimmerstone());
			entity.setFuel(repeller.getFuel());
		}
	}
}
