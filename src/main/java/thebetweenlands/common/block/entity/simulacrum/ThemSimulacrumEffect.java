package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.block.SimulacrumEffect;

public class ThemSimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (level.isClientSide() && level.getGameTime() % 20 == 0 && level.getRandom().nextInt(5) == 0) {
			this.spawnThem(level, pos);
		}
	}

	private void spawnThem(Level level, BlockPos pos) {
//		Entity viewer = Minecraft.getInstance().getCameraEntity();
//
//		if (viewer != null && pos.distToCenterSqr(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16 && FogHandler.hasDenseFog(level) && (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 < 65.0F) {
//			if (SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(level.getBlockState(viewer.blockPosition().below())) || SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(level.getBlockState(viewer.blockPosition().below(2))) || SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(level.getBlockState(viewer.blockPosition().below(3)))) {
//				double xOff = level.getRandom().nextInt(50) - 25;
//				double zOff = level.getRandom().nextInt(50) - 25;
//				double sx = viewer.getX() + xOff;
//				double sz = viewer.getZ() + zOff;
//				double sy = viewer.blockPosition().getY() + level.getRandom().nextFloat() * 0.75f;
////				BLParticles.THEM.spawn(level, sx, sy, sz);
//			}
//		}
	}
}
