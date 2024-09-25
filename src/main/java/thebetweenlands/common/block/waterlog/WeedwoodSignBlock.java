package thebetweenlands.common.block.waterlog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import javax.annotation.Nullable;

import java.util.Optional;

public abstract class WeedwoodSignBlock extends SignBlock implements SwampWaterLoggable {
	protected WeedwoodSignBlock(WoodType type, Properties properties) {
		super(type, properties);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(currentPos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return state;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATER_TYPE);
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return SwampWaterLoggable.super.canPlaceLiquid(player, level, pos, state, fluid);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return SwampWaterLoggable.super.placeLiquid(level, pos, state, fluidState);
	}

	@Override
	public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
		return SwampWaterLoggable.super.pickupBlock(player, level, pos, state);
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return SwampWaterLoggable.super.getPickupSound();
	}
}
