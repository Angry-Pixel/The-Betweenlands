package thebetweenlands.api.item;

import thebetweenlands.api.aspect.DiscoveryContainer;

public interface IDiscoveryProvider<P> {
	/**
	 * Returns the discovery container
	 * @param provider
	 * @return
	 */
	public DiscoveryContainer<P> getContainer(P provider);
	
	/**
	 * Saves the discovery container
	 * @param provider
	 * @param container
	 */
	public void saveContainer(P provider, DiscoveryContainer<P> container);
}
