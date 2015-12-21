package thebetweenlands.client.input;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingWrapper implements BLKey {
	private final KeyBinding keyBinding;

	private boolean prevIsPressed;

	public KeyBindingWrapper(KeyBinding keyBinding) {
		this.keyBinding = keyBinding;
	}

	@Override
	public void update() {
		prevIsPressed = keyBinding.getIsKeyPressed();
	}

	@Override
	public boolean pollStateChange() {
		return prevIsPressed != keyBinding.getIsKeyPressed();
	}

	@Override
	public boolean isDown() {
		return keyBinding.getIsKeyPressed();
	}

	@Override
	public void register() {}
}
