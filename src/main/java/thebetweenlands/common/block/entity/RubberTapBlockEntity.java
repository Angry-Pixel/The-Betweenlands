package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.block.RubberTapBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class RubberTapBlockEntity extends SyncedBlockEntity implements IFluidHandler {

	public final FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME, fluidStack -> fluidStack.is(FluidRegistry.RUBBER_STILL.get()));
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

	@Override
	public int getTanks() {
		return this.tank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.tank.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (action.execute()) {
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
		return this.tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (action.execute()) {
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
		return this.tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (action.execute()) {
			this.setChanged();
			BlockState stat = this.getLevel().getBlockState(this.getBlockPos());
			this.getLevel().sendBlockUpdated(this.getBlockPos(), stat, stat, 2);
		}
		return this.tank.drain(maxDrain, action);
	}
}
