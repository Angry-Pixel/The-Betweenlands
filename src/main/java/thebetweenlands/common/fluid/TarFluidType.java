package thebetweenlands.common.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.block.terrain.TarBlock;
import thebetweenlands.common.datagen.tags.BLDimensionTypeTagProvider;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;

public class TarFluidType extends FluidType {

	public static final BooleanProperty BOILING = TarFlowingFluid.BOILING;
	
	public TarFluidType(Properties properties) {
		super(properties);
	}

	// can only be boiling when in-world
	@Override
	public int getTemperature(FluidStack stack) {
		return super.getTemperature(stack);
	}
	
	@Override
	public int getTemperature(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		final int temperature = super.getTemperature(state, getter, pos);
		if(state.getValue(TarFlowingFluid.BOILING)) {
			return temperature + 100;
		}
		return temperature;
	}
	
	@Override
	public BlockState getBlockForFluidState(BlockAndTintGetter getter, BlockPos pos, FluidState state) {
		return super.getBlockForFluidState(getter, pos, state).trySetValue(BOILING, state.getValue(BOILING));
	}
	
	@Override
	public FluidState getStateForPlacement(BlockAndTintGetter getter, BlockPos pos, FluidStack stack) {
		FluidState state = super.getStateForPlacement(getter, pos, stack);
    	if(!state.isEmpty()) {
        	state = state.trySetValue(BOILING, TarBlock.shouldFloorCauseBoiling(getter, pos.below()));
    	}
		return state;
	}
	
	@Override
	public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
		return super.isVaporizedOnPlacement(level, pos, stack) || level.dimensionTypeRegistration().is(BLDimensionTypeTagProvider.TAR_VAPORIZES);
	}
	
	@Override
	public boolean canDrownIn(LivingEntity entity) {
		return super.canDrownIn(entity) && !entity.getType().is(BLEntityTagProvider.CAN_BREATHE_UNDER_TAR);
	}
	
	@Override
	public boolean canHydrate(Entity entity) {
		return entity.getType().is(BLEntityTagProvider.TAR_BEING);
	}
	
	@Override
	public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos) {
		return super.canExtinguish(state, getter, pos) && !state.getValue(BOILING);
	}
	
	@Override
	// so if you return true from this method it'll forego doing normal movement
	public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
		boolean moved = super.move(state, entity, movementVector, gravity);
		if(!entity.getType().is(BLEntityTagProvider.IMMUNE_TO_TAR_SLOWDOWN) && !entity.isSpectator()) {
			Vec3 deltaMovement = entity.getDeltaMovement();
			entity.setDeltaMovement(
					deltaMovement.x() * 0.6D,
					deltaMovement.y() * 0.8D - 0.0175D,
					deltaMovement.z() * 0.6D
				);
		}
		return moved;
	}
	
}
