package thebetweenlands.common.herblore.aspect;

public interface IDiscoveryProvider<P> {
	public DiscoveryContainer getContainer(P provider);
	public void saveContainer(P provider, DiscoveryContainer container);
}
