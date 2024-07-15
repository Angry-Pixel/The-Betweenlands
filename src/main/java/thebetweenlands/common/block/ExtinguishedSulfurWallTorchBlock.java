package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ExtinguishedSulfurWallTorchBlock extends SulfurWallTorchBlock {
	public ExtinguishedSulfurWallTorchBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!stack.isEmpty() && stack.is(ItemRegistry.OCTINE_INGOT)) {
			if (!level.isClientSide()) {
				level.setBlockAndUpdate(pos, BlockRegistry.SULFUR_WALL_TORCH.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
				level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

	}
}
