package thebetweenlands.common.capability.base;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Internal representation and wrapper of item capabilities.
 * <p>Use their respective {@link Capability} to interface with the data.
 * <p><b>Note:</b> This class <b>must</b> be an implementation of the capability it provides!
 *
 * @param <F> The default implementation of the capability
 * @param <T> The capability
 */
public abstract class ItemCapability<F extends ItemCapability<F, T>, T> {
	private ItemStack stack;

	protected ItemCapability() {
		//Make sure the item capability is the implementation of the capability
		Preconditions.checkState(this.getCapabilityClass().isAssignableFrom(this.getClass()), "Item capability %s must implement %s", this.getClass().getName(), this.getCapabilityClass().getName());
	}

	void setStack(ItemStack stack) {
		this.stack = stack;
	}

	/**
	 * Returns the item stack
	 * @return
	 */
	public final ItemStack getItemStack() {
		return this.stack;
	}

	/**
	 * Returns the capability ID
	 * @return
	 */
	public abstract ResourceLocation getID();

	/**
	 * Returns a <b>new</b> instance of the capability with the default state
	 * @return
	 */
	protected abstract F getDefaultCapabilityImplementation();

	/**
	 * Returns the capability instance.
	 * <p>Use the {@link net.minecraftforge.common.capabilities.CapabilityInject} annotation to retrieve the capability
	 * @return
	 */
	protected abstract Capability<T> getCapability();

	/**
	 * Returns the capability class
	 * @return
	 */
	protected abstract Class<T> getCapabilityClass();

	/**
	 * Returns whether this capability is applicable to the specified item stack
	 * @param stack
	 * @return
	 */
	public abstract boolean isApplicable(ItemStack stack);
}
