package thebetweenlands.api.item;

import net.minecraft.core.HolderLookup;
import thebetweenlands.api.aspect.DiscoveryContainer;

import javax.annotation.Nullable;

public interface DiscoveryProvider<P> {
	/**
	 * Returns the discovery container
	 *
	 * @param provider
	 * @return
	 */
	@Nullable
	DiscoveryContainer<P> getContainer(P provider, HolderLookup.Provider registries);

	/**
	 * Saves the discovery container
	 *
	 * @param provider
	 * @param container
	 */
	void saveContainer(P provider, HolderLookup.Provider registries, DiscoveryContainer<P> container);
}
