package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

public class ThornsBlock extends VineBlock {
	public ThornsBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && !(entity instanceof BLEntity) && entity instanceof LivingEntity living && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.get().isActive(living)) {
			living.hurt(level.damageSources().cactus(), 1.0F);
		}
	}
}
