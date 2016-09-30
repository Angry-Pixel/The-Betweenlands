package thebetweenlands.common.herblore.aspect;

public interface IDiscoveryProvider<P> {
	public DiscoveryContainer<P> getContainer(P provider);
	public void saveContainer(P provider, DiscoveryContainer<P> container);
}
