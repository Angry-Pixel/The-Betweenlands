package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class FloweredNettleBlock extends PlantBlock {
	public FloweredNettleBlock(Properties properties) {
		super(PlantBlock.FLOWER_SHAPE, true, properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(350) == 0) {
			BlockPos randOffset = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
			if (level.isLoaded(randOffset) && level.isEmptyBlock(randOffset) && state.canSurvive(level, randOffset)) {
				level.setBlockAndUpdate(randOffset, BlockRegistry.NETTLE.get().defaultBlockState());
			}
		}
		if (random.nextInt(220) == 0) {
			level.setBlockAndUpdate(pos, BlockRegistry.NETTLE.get().defaultBlockState());
		}
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && !(entity instanceof BLEntity) && entity instanceof LivingEntity living && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.get().isActive(living)) {
			entity.hurt(level.damageSources().cactus(), 1);
		}
	}
}
