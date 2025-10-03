package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.block.farming.DugSoilBlock;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class FarmableDoublePlantBlock extends DoublePlantBlock implements FarmablePlant {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public FarmableDoublePlantBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state != null) {
			state = state.setValue(FACING, context.getHorizontalDirection().getOpposite());
		}
		return state;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockPos blockpos = pos.above();
		level.setBlock(blockpos, copyWaterloggedFrom(level, blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, level.getBlockState(pos).getValue(FACING))), 3);
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
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public float getSpreadChance(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return 0.25F;
	}

	@Override
	public boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return level.isEmptyBlock(targetPos) && level.isEmptyBlock(targetPos.above()) && state.canSurvive(level, targetPos);
	}

	@Override
	public int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 8;
	}

	@Override
	public void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random) {
		level.removeBlock(pos.above(), false);
		level.removeBlock(pos, false);
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		level.setBlockAndUpdate(targetPos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
		level.setBlockAndUpdate(targetPos.above(), this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FACING));
	}
}
