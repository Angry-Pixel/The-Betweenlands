package thebetweenlands.api.item;

import thebetweenlands.api.aspect.DiscoveryContainer;

public interface DiscoveryProvider<P> {
	/**
	 * Returns the discovery container
	 *
	 * @param provider
	 * @return
	 */
	DiscoveryContainer<P> getContainer(P provider);

	/**
	 * Saves the discovery container
	 *
	 * @param provider
	 * @param container
	 */
	void saveContainer(P provider, DiscoveryContainer<P> container);
}
