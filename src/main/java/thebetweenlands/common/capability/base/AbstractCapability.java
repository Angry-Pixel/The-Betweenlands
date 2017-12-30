package thebetweenlands.common.capability.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

public abstract class AbstractCapability<F extends AbstractCapability<F, T, A>, T, A> {
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
}
