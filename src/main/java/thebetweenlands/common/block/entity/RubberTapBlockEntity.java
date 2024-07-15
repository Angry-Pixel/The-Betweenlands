package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.block.RubberTapBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class RubberTapBlockEntity extends BlockEntity {

	private final FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME, fluidStack -> fluidStack.is(FluidRegistry.RUBBER_STILL.get()));
	private int fillProgress = 0;

	public RubberTapBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.RUBBER_TAP.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, RubberTapBlockEntity entity) {
		if (!level.isClientSide()) {
			final int ticksPerStep = ((RubberTapBlock) state.getBlock()).getTicksPerStep();
			if (entity.tank.getFluidAmount() < FluidType.BUCKET_VOLUME) {
				entity.fillProgress++;

				if (entity.fillProgress >= ticksPerStep) {
					entity.tank.fill(new FluidStack(FluidRegistry.RUBBER_STILL.get(), 67), IFluidHandler.FluidAction.EXECUTE);
					entity.fillProgress = 0;

					level.sendBlockUpdated(pos, state, state, 3);
					entity.setChanged();
				}
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		this.tank.writeToNBT(registries, tag);
		tag.putInt("fill_progress", this.fillProgress);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.tank.readFromNBT(registries, tag);
		this.fillProgress = tag.getInt("fill_progress");
	}
}
