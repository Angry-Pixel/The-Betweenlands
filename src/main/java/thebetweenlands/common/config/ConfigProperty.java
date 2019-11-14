package thebetweenlands.common.config;

public abstract class ConfigProperty {
	/**
	 * (Re-)Initializes this property
	 */
	protected abstract void init();
	
	/**
	 * Called on game post init
	 */
	public void postInitGame() {
		
	}
}
