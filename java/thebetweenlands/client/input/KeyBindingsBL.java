package thebetweenlands.client.input;

import org.lwjgl.input.Keyboard;

public class KeyBindingsBL {
	public static final KeyBindingBL dropAmulet = new KeyBindingBL("key.drop.amulet", Keyboard.KEY_J, "key.categories.amulet");

	public static void init() {
		dropAmulet.register();
	}
}
