package thebetweenlands.client.input;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingBL extends KeyBinding {
	private boolean prevIsDown;

	private boolean isDown;

	public KeyBindingBL(String desc, int keycode, String category) {
		super(desc, keycode, category);
	}

	public void update() {
		prevIsDown = isDown;
	}

	public boolean pollStateChange() {
		return prevIsDown != (isDown = Keyboard.isKeyDown(getKeyCode()));
	}

	public boolean isDown() {
		return isDown;
	}
}
