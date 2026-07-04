package thebetweenlands.util;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidHandlerHelper {

	/**
	 * Gets the comparator output from a block entity that may implement {@linkplain IFluidHandler}.<br/>
	 * <br/>
	 * Based on {@linkplain AbstractContainerMenu#getRedstoneSignalFromBlockEntity(BlockEntity)}.<br/>
	 * <br/>
	 * <strong>Note</strong>: does not check for capabilities, only {@code instanceof IFluidHandler}.
	 * @param blockEntity the block entity that might be a fluid handler
	 * @return the analog signal output
	 */
	public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity blockEntity) {
		return blockEntity instanceof IFluidHandler fluidHandler ? getRedstoneSignalFromFluidHandler(fluidHandler) : 0;
	}
	
	/**
	 * Gets the comparator output from an {@linkplain IFluidHandler}.<br/>
	 * <br/>
	 * Based on {@linkplain AbstractContainerMenu#getRedstoneSignalFromContainer(net.minecraft.world.Container)}.
	 * @param fluidHandler the IFluidHandler to get the output of
	 * @return the comparator output of the fluid handler, or 0 if it is null
	 */
	public static int getRedstoneSignalFromFluidHandler(@Nullable IFluidHandler fluidHandler) {
		if(fluidHandler == null) {
			return 0;
		} else {
			int amount = 0;
			int capacity = 0;
			
			for(int i = 0; i < fluidHandler.getTanks(); ++i) {
				amount += fluidHandler.getFluidInTank(i).getAmount();
				capacity += fluidHandler.getTankCapacity(i);
			}
			
			float f = ((float)amount) / ((float)capacity);
			return Mth.lerpDiscrete(f, 0, 15);
		}
	}
	
}
