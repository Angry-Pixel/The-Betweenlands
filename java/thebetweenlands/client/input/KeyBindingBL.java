package thebetweenlands.client.input;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindingBL extends KeyBinding implements BLKey {
	private boolean prevIsDown;

	private boolean isDown;

	public KeyBindingBL(String desc, int keycode, String category) {
		super(desc, keycode, category);
	}

	@Override
	public void update() {
		prevIsDown = isDown;
	}

	@Override
	public boolean pollStateChange() {
		return prevIsDown != (isDown = Keyboard.isKeyDown(getKeyCode()));
	}

	@Override
	public boolean isDown() {
		return isDown;
	}

	@Override
	public void register() {
		ClientRegistry.registerKeyBinding(this);
	}
}
