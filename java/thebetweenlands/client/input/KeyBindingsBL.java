package thebetweenlands.client.input;

import org.lwjgl.input.Keyboard;

public class KeyBindingsBL {
	public static final KeyBindingBL radialMenu = new KeyBindingBL("key.radialmenu", Keyboard.KEY_F, "key.categories.betweenlands");
	public static final KeyBindingBL useRing = new KeyBindingBL("key.useRing", Keyboard.KEY_R, "key.categories.betweenlands");

	public static void init() {
		radialMenu.register();
		useRing.register();
	}
}
