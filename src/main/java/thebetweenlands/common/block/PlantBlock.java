package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.registries.ItemRegistry;

public class PlantBlock extends BushBlock implements FarmablePlant {

	public static final MapCodec<PlantBlock> CODEC = simpleCodec(PlantBlock::new);

	public PlantBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BushBlock> codec() {
		return CODEC;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.is(ItemRegistry.COMPOST)) {
			pos = pos.below();
			for (int i = 0; i < 3; i++) {
				state = level.getBlockState(pos);
				if (state.getBlock() instanceof DugSoilBlock) {
					return state.useItemOn(stack, level, player, hand, hitResult);
				} else if (!state.is(this)) {
					break;
				}
				pos = pos.below();
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public float getSpreadChance(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return 0.25F;
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return level.isEmptyBlock(targetPos) && state.canSurvive(level, targetPos);
	}

	@Override
	public int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 4;
	}

	@Override
	public void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random) {
		level.destroyBlock(pos, false);
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		level.setBlockAndUpdate(pos, this.defaultBlockState());
	}
}
