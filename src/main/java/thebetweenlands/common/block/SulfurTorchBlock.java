package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class SulfurTorchBlock extends BaseTorchBlock {
	public SulfurTorchBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseTorchBlock> codec() {
		return null;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide() && level.isRainingAt(pos)) {
			level.setBlockAndUpdate(pos, BlockRegistry.EXTINGUISHED_SULFUR_TORCH.get().defaultBlockState());
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.7D;
		double d2 = (double) pos.getZ() + 0.5D;
//		BLParticles.SULFUR_TORCH.spawn(world, d0, d1, d2);
	}
}
