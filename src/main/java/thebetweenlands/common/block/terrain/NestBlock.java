package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.plant.WeedwoodBushBlock;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

public class NestBlock extends WeedwoodBushBlock {
	public NestBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canConnectTo(BlockGetter level, BlockPos pos, Direction direction) {
		if (direction == Direction.DOWN && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
			return true;
		}
		return level.getBlockState(pos).is(this);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && !(entity instanceof BLEntity) && entity instanceof LivingEntity living && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.get().isActive(living)) {
			entity.hurt(level.damageSources().cactus(), 1);
		}
	}
}
