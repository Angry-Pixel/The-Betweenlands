package thebetweenlands.common.capability.lifecrystal;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandlerModifiable;

public class DamageLifeCrystalHandler implements ILifeCrystalHandlerModifiable {

	protected final ItemStack stack;
	@Nullable
	protected final Level level; // Unfortunately required context for damaging items
	protected final ChargeType chargeType;
	protected final DrainType drainType;
	/**
	 * If the stack should be broken when reduced to zero life power
	 */
	protected final boolean allowBreaking;
	
	public DamageLifeCrystalHandler(ItemStack stack, @Nullable Level level, ChargeType chargeType, DrainType drainType, boolean allowBreaking) {
		this.stack = Objects.requireNonNull(stack);
		this.level = level;
		this.chargeType = Objects.requireNonNull(chargeType);
		this.drainType = Objects.requireNonNull(drainType);
		this.allowBreaking = allowBreaking;
	}
	
	protected boolean stackHasDurability() {
		return this.stack.has(DataComponents.MAX_DAMAGE) && this.stack.has(DataComponents.DAMAGE);
	}
	
	protected boolean isUnbreakable() {
		return !this.stack.isDamageableItem();
	}
	
	protected void damageStackByAmount(int amount) {
		// Cannot use hurtAndBreak()
		// We may need to ignore unbreakable and may need to prevent breaking
		if(!this.stackHasDurability()) {
			return;
		}
		
		// Respect custom damage modifiers and enchantments
		// (This is the whole point of this version over the data component version of the capability)
		amount = this.stack.getItem().damageItem(stack, amount, null, null);
		if(amount > 0 && this.level instanceof ServerLevel serverLevel) {
			amount = EnchantmentHelper.processDurabilityChange(serverLevel, this.stack, amount);
		}
		if(amount <= 0) {
			return;
		}
		
		int damageValue = this.stack.getDamageValue() + amount;
		this.stack.setDamageValue(damageValue);
		if(damageValue >= this.stack.getMaxDamage() && this.allowBreaking) {
			this.stack.shrink(1);
		}
	}
	
	@Override
	public int getLifePower() {
		if(!this.stackHasDurability()) {
			return 0;
		}
		return Math.max(this.stack.getMaxDamage() - this.stack.getDamageValue(), 0);
	}

	@Override
	public int getMaxLifePower() {
		if(!this.stackHasDurability()) {
			return 0;
		}
		return Math.max(this.stack.getMaxDamage(), 0);
	}

	@Override
	public int chargeLifePower(int power, boolean simulate) {
		if(
			// No negative charging
			power <= 0 ||
			// No charging invalid stacks
			this.stack.isEmpty() || !this.stackHasDurability() ||
			// Do not allow charging if charging is disabled
			this.chargeType == ChargeType.NO_CHARGING ||
			// Do not allow charging if the stack is unbreakable and unbreakable charging is disabled
			(this.chargeType == ChargeType.NO_CHARGING_IF_UNBREAKABLE && this.isUnbreakable())
		) {
			return power;
		}
		
		// We cannot charge more than this amount
		final int amountChargable = this.stack.getDamageValue();
		
		// This is how much charge we added
		final int amountCharged = Math.min(power, amountChargable);
		
		if(!simulate) {
			if(!this.isUnbreakable() || this.chargeType == ChargeType.IGNORE_UNBREAKABLE) {
				this.stack.setDamageValue(amountChargable - amountCharged);
				this.onContentsChanged();
			}
		}
		
		return power - amountCharged;
	}

	@Override
	public int drainLifePower(int power, boolean simulate) {
		if(
			// No negative draining
			power <= 0 ||
			// No draining from invalid stacks
			this.stack.isEmpty() || !this.stackHasDurability() ||
			// Do not allow draining if draining is disabled
			this.drainType == DrainType.NO_DRAINING ||
			// Do not allow draining if the stack is unbreakable and unbreakable drain is disabled
			(this.drainType == DrainType.NO_DRAINING_IF_UNBREAKABLE && this.isUnbreakable())
		) {
			return 0;
		}

		final int maxDamage = this.stack.getMaxDamage();
		final int currentDamage = this.stack.getDamageValue();
		
		// We cannot remove more than this amount of charge
		final int amountDrainable = maxDamage - currentDamage;
		
		// Figure out how much life power we can drain
		final int amountDrained = Math.min(power, amountDrainable);
		
		// Drain the durability of the stack
		if(!simulate) {
			if(!this.isUnbreakable() || this.drainType == DrainType.IGNORE_UNBREAKABLE) {
				this.damageStackByAmount(amountDrained);
				this.onContentsChanged();
			}
		}
		
		return amountDrained;
	}

	@Override
	public void setLifePower(int power) {
		if(!this.stackHasDurability()) {
			throw new RuntimeException("Attempt to set life power on a stack that doesn't support durability");
		}
		int maxDamage = this.stack.getMaxDamage();
		if(power < 0 || power > maxDamage) {
			throw new IllegalArgumentException("Attempt to set life power to a value " + power + " out of range [0, " + maxDamage + "]");
		}
		this.stack.setDamageValue(maxDamage - power);
		this.onContentsChanged();
	}

	protected void onContentsChanged() {}
	
	public static enum ChargeType {
		/**
		 * Do not allow recharging life power at all
		 */
		NO_CHARGING,
		/**
		 * If the stack is unbreakable, do not allow recharging life power
		 * Otherwise, recharge life power like normal
		 */
		NO_CHARGING_IF_UNBREAKABLE,
		/**
		 * If the stack is unbreakable, accept charge but do not reduce durability
		 * Otherwise, drain life power like normal
		 */
		INFINITE_CHARGING_IF_UNBREAKABLE,
		/**
		 * Charge life power like normal, update durability even if unbreakable
		 */
		IGNORE_UNBREAKABLE;
	}

	public static enum DrainType {
		/**
		 * Do not allow draining life power at all
		 */
		NO_DRAINING,
		/**
		 * If the stack is unbreakable, do not allow draining life power
		 * Otherwise, drain life power like normal
		 */
		NO_DRAINING_IF_UNBREAKABLE,
		/**
		 * If the stack is unbreakable, supply power but do not reduce durability
		 * Otherwise, drain life power like normal
		 */
		INFINITE_DRAINING_IF_UNBREAKABLE,
		/**
		 * Drain life power like normal, update durability even if unbreakable
		 */
		IGNORE_UNBREAKABLE;
	}

}
