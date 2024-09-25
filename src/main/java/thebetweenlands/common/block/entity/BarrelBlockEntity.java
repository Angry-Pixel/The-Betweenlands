package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import javax.annotation.Nullable;
import thebetweenlands.common.block.container.BarrelBlock;
import thebetweenlands.common.inventory.BarrelMenu;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class BarrelBlockEntity extends SyncedBlockEntity implements MenuProvider, IFluidHandler {

	public final FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME * 8);

	public BarrelBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.BARREL.get(), pos, state);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		this.tank.writeToNBT(registries, tag);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.tank.readFromNBT(registries, tag);
	}

	@Override
	public Component getDisplayName() {
		return Component.empty();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new BarrelMenu(containerId, playerInventory, this);
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
		if (this.getLevel() != null) {
			BlockState state = this.getLevel().getBlockState(this.getBlockPos());

			boolean isFluidHot = resource.getFluid().getFluidType().getTemperature(resource) > 473.15F /*200°C*/ || resource.is(Fluids.LAVA);

			if (!isFluidHot || (state.getBlock() instanceof BarrelBlock barrel && barrel.isHeatResistant(this.getLevel(), this.getBlockPos(), state))) {
				int filled = this.tank.fill(resource, action);

				if (filled != 0 && action.execute()) {
					this.setChanged();
					this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
				}

				return filled;
			}
		}

		return 0;
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
