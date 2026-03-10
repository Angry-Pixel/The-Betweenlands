package thebetweenlands.common.capability.lifecrystal;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandlerModifiable;
import thebetweenlands.common.registries.DataComponentRegistry;

public class DataLifeCrystalHandler implements ILifeCrystalHandlerModifiable {

	protected final ItemStack stack;
	/**
	 * If the stack should be broken when reduced to zero life power
	 */
	protected final boolean allowBreaking;
	
	public DataLifeCrystalHandler(ItemStack stack, boolean allowBreaking) {
		this.stack = Objects.requireNonNull(stack);
		this.allowBreaking = allowBreaking;
	}
	
	@Nullable
	public static DataLifeCrystalHandler createIfValid(ItemStack stack, boolean allowBreaking) {
		if(stackIsValid(stack)) {
			return new DataLifeCrystalHandler(stack, allowBreaking);
		} else {
			return null;
		}
	}
	
	public static boolean stackIsValid(ItemStack stack) {
		return stack.has(DataComponentRegistry.LIFE_POWER) && stack.has(DataComponentRegistry.MAX_LIFE_POWER);
	}
	
	protected boolean stackIsValid() {
		return stackIsValid(this.stack);
	}
	
	@Override
	public int getLifePower() {
		if(!this.stackIsValid()) {
			return 0;
		}
		return this.stack.get(DataComponentRegistry.LIFE_POWER);
	}

	@Override
	public int getMaxLifePower() {
		if(!this.stackIsValid()) {
			return 0;
		}
		return this.stack.get(DataComponentRegistry.MAX_LIFE_POWER);
	}

	@Override
	public int chargeLifePower(int power, boolean simulate) {
		// Make sure charge and stack is valid
		if(power <= 0 || this.stack.isEmpty() || !this.stackIsValid()) {
			return power;
		}
		
		final int maxCharge = this.getMaxLifePower();
		final int currentCharge = this.getLifePower();
		
		// We cannot charge more than this amount
		final int amountChargable = maxCharge - currentCharge;
		
		// We can accept all of the charge
		if(power <= amountChargable) {
			if(!simulate) {
				this.stack.set(DataComponentRegistry.LIFE_POWER, currentCharge + power);
				this.onContentsChanged();
			}
			return 0;
		}
		// We can only accept some of the charge
		else {
			if(!simulate) {
				this.stack.set(DataComponentRegistry.LIFE_POWER, maxCharge);
				this.onContentsChanged();
			}
			return power - amountChargable;
		}
	}

	@Override
	public int drainLifePower(int power, boolean simulate) {
		// Make sure charge and stack is valid
		if(power <= 0 || this.stack.isEmpty() || !this.stackIsValid()) {
			return 0;
		}
		
		// We cannot remove more than this amount of charge
		final int amountDrainable = this.getLifePower();
		
		// We can fully drain the requested amount
		if(power < amountDrainable) {
			if(!simulate) {
				this.stack.set(DataComponentRegistry.LIFE_POWER, amountDrainable - power);
				this.onContentsChanged();
			}
			return power;
		}
		// We can only partially drain the requested amount
		else {
			if(!simulate) {
				this.stack.set(DataComponentRegistry.LIFE_POWER, 0);
				if(this.allowBreaking) {
					this.stack.shrink(1);
				}
				this.onContentsChanged();
			}
			return amountDrainable;
		}
	}

	@Override
	public void setLifePower(int power) {
		if(!this.stackIsValid()) {
			throw new RuntimeException("Attempt to set life power on a stack that doesn't support it");
		}
		int maxPower = this.getMaxLifePower();
		if(power < 0 || power > maxPower) {
			throw new IllegalArgumentException("Attempt to set life power to a value " + power + " out of range [0, " + maxPower + "]");
		}
		this.stack.set(DataComponentRegistry.LIFE_POWER, power);
		this.onContentsChanged();
	}

	protected void onContentsChanged() {}

}
