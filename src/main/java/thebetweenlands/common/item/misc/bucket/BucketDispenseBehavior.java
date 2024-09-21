package thebetweenlands.common.item.misc.bucket;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BucketDispenseBehavior extends OptionalDispenseItemBehavior {
	@Override
	protected ItemStack execute(BlockSource source, ItemStack stack) {
		var level = source.level();
		var facing = source.state().getValue(DispenserBlock.FACING);
		var pos = source.pos().relative(facing);

		var action = FluidUtil.tryPickUpFluid(stack, null, level, pos, facing.getOpposite());
		var resultStack = action.getResult();

		if (!action.isSuccess() || resultStack.isEmpty()) {
			var singleStack = stack.copyWithCount(1);

			var fluidHandler = FluidUtil.getFluidHandler(singleStack);
			if (fluidHandler.isEmpty()) return super.execute(source, stack);

			var fluidStack = fluidHandler.get().drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
			var result = !fluidStack.isEmpty() ? FluidUtil.tryPlaceFluid(null, level, InteractionHand.MAIN_HAND, pos, stack, fluidStack) : FluidActionResult.FAILURE;

			if (result.isSuccess()) {
				var drainedStack = result.getResult();

				if (drainedStack.getCount() == 1) {
					return drainedStack;
				} else if (!drainedStack.isEmpty() && !source.blockEntity().insertItem(drainedStack).isEmpty()) {
					this.dispense(source, drainedStack);
				}

				drainedStack.shrink(1);
				return drainedStack;
			} else {
				return this.dispense(source, stack);
			}
		} else {
			if (stack.getCount() == 1) {
				return resultStack;
			} else if (!source.blockEntity().insertItem(resultStack).isEmpty()) {
				this.dispense(source, resultStack);
			}
		}

		return resultStack;
	}
}
