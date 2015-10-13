package thebetweenlands.client.input;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingBL extends KeyBinding {
	private boolean prevIsPressed;

	public KeyBindingBL(String desc, int keycode, String category) {
		super(desc, keycode, category);
	}

	public void update() {
		prevIsPressed = isPressed();
	}

	public boolean hasStateChanged() {
		return prevIsPressed != isPressed();
	}
}
