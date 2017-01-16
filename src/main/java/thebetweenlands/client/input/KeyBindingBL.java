package thebetweenlands.client.input;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingBL extends KeyBinding implements BLKey {
	private boolean prevIsDown;

	private boolean isDown;

	public KeyBindingBL(String desc, int keycode, String category) {
		super(desc, keycode, category);
	}

	@Override
	public void update() {
		this.prevIsDown = this.isDown;
	}

	@Override
	public boolean pollStateChange() {
		return this.prevIsDown != (this.isDown = Keyboard.isKeyDown(getKeyCode()));
	}

	@Override
	public boolean isDown() {
		return this.isDown;
	}

	@Override
	public void register() {
		ClientRegistry.registerKeyBinding(this);
	}
}
