package thebetweenlands.common.capability.base;

import com.google.common.base.Preconditions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

/**
 * <b>THIS IS CURRENTLY BROKEN AND DOES _NOT_ SYNC ON SERVERS</b>
 * 
 * <p>
 * Internal representation and wrapper of item capabilities.
 * <p>Use their respective {@link Capability} to interface with the data.
 * <p><b>Note:</b> This class <b>must</b> be an implementation of the capability it provides!
 *
 * @param <F> The default implementation of the capability
 * @param <T> The capability
 */
public abstract class ItemCapability<F extends ItemCapability<F, T>, T> extends AbstractCapability<F, T, Item> {
	private ItemStack stack;

	protected ItemCapability() {
		//Make sure the item capability is the implementation of the capability
		Preconditions.checkState(this.getCapabilityClass().isAssignableFrom(this.getClass()), "Item capability %s must implement %s", this.getClass().getName(), this.getCapabilityClass().getName());
	}

	void setStack(ItemStack stack) {
		this.stack = stack;
	}

	/**
	 * Initializes the default values
	 * <p><b>Note:</b> This is called before the capability is attached to the item stack
	 */
	protected void init() {

	}

	/**
	 * Returns the item stack
	 * @return
	 */
	public final ItemStack getItemStack() {
		return this.stack;
	}

	/**
	 * Returns whether this capability is applicable to the item
	 * @param obj
	 * @return
	 */
	public boolean isApplicable(Item item) {
		return false;
	}
}
