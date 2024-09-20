package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import thebetweenlands.common.registries.FluidTypeRegistry;

public class TarBlock extends LiquidBlock {
	public TarBlock(FlowingFluid fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity /*!(entity instanceof TarBeast)*/ && !(entity instanceof Player player && player.getAbilities().invulnerable)) {
			if (entity.isEyeInFluidType(FluidTypeRegistry.TAR.get())) {
				entity.hurt(level.damageSources().drown(), 2.0F);
			}
		}
	}
}
