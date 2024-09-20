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

public class NettleBlock extends PlantBlock {
	public NettleBlock(Properties properties) {
		super(PlantBlock.FLOWER_SHAPE, true, properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(240) == 0) {
			level.setBlockAndUpdate(pos, BlockRegistry.FLOWERED_NETTLE.get().defaultBlockState());
		}
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && !(entity instanceof BLEntity) && entity instanceof LivingEntity living && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.get().isActive(living)) {
			entity.hurt(level.damageSources().cactus(), 1);
		}
	}
}
