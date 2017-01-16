package thebetweenlands.common.registries;

import org.lwjgl.input.Keyboard;

import thebetweenlands.client.input.KeyBindingBL;

public class KeyBindRegistry {
	public static final KeyBindingBL RADIAL_MENU = new KeyBindingBL("key.radialmenu", Keyboard.KEY_C, "key.categories.betweenlands");
	public static final KeyBindingBL USE_RING = new KeyBindingBL("key.useRing", Keyboard.KEY_R, "key.categories.betweenlands");
	public static final KeyBindingBL OPEN_POUCH = new KeyBindingBL("key.openPouch", Keyboard.KEY_G, "key.categories.betweenlands");

	public static void init() {
		RADIAL_MENU.register();
		USE_RING.register();
		OPEN_POUCH.register();
	}
}
