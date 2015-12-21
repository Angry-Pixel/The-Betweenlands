package thebetweenlands.client.input;

public interface BLKey {
	public void update();

	public boolean pollStateChange();

	public boolean isDown();

	public void register();
}
