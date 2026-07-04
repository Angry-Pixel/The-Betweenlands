package thebetweenlands.common.capability.lifecrystal;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.capability.BLCapabilities;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandler;

// TODO write javadocs and move to api package
public class LifeCrystalHelper {
	@Nullable
	public static ILifeCrystalHandler getLifeCrystalHandler(ItemStack stack) {
		return stack.getCapability(BLCapabilities.LifeCrystalHandler.ITEM);
	}

	/**
	 * Returns {@code true} if the target stack is a valid life crystal for the purposes of this helper.
	 * @param stack The life crystal to test.
	 * @return
	 * @ensures the target stack has a valid life crystal handler (if {@code true})
	 */
	public static boolean isValidLifeCrystal(ItemStack stack) {
		return !stack.isEmpty() && getLifeCrystalHandler(stack) != null;
	}

	public static boolean hasLifePower(ItemStack stack) {
		if(isValidLifeCrystal(stack)) {
			// isValidLifeCrystal(ItemStack) ensures the stack has a valid life crystal handler
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(stack);
			return lifeCrystalHandler.getLifePower() > 0;
		}
		return false;
	}

	public static int drainLifePower(ItemStack stack, int power, boolean simulate) {
		if(isValidLifeCrystal(stack)) {
			// isValidLifeCrystal(ItemStack) ensures the stack has a valid life crystal handler
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(stack);
			return lifeCrystalHandler.drainLifePower(power, simulate);
		}
		return 0;
	}

	public static int chargeLifePower(ItemStack stack, int power, boolean simulate) {
		if(isValidLifeCrystal(stack)) {
			// isValidLifeCrystal(ItemStack) ensures the stack has a valid life crystal handler
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(stack);
			return lifeCrystalHandler.chargeLifePower(power, simulate);
		}
		return power;
	}
	
	public static ItemStack withLifeDrain(ItemStack stack, int power) {
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack copyStack = stack.copy();
		if(isValidLifeCrystal(copyStack)) {
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(copyStack);
			lifeCrystalHandler.drainLifePower(power, false);
		}
		return copyStack;
	}
	
	public static ItemStack withLifeCharge(ItemStack stack, int power) {
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack copyStack = stack.copy();
		if(isValidLifeCrystal(copyStack)) {
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(copyStack);
			lifeCrystalHandler.chargeLifePower(power, false);
		}
		return copyStack;
	}
}
