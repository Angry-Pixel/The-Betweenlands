package thebetweenlands.common.registries;

import org.lwjgl.input.Keyboard;

import thebetweenlands.client.input.KeyBindingBL;

public class KeyBindRegistry {
	public static final KeyBindingBL RADIAL_MENU = new KeyBindingBL("key.radial_menu", Keyboard.KEY_C, "key.categories.betweenlands");
	public static final KeyBindingBL USE_RING = new KeyBindingBL("key.use_ring", Keyboard.KEY_R, "key.categories.betweenlands");
	public static final KeyBindingBL USE_SECONDARY_RING = new KeyBindingBL("key.use_secondary_ring", 0, "key.categories.betweenlands");
	public static final KeyBindingBL OPEN_POUCH = new KeyBindingBL("key.open_pouch", Keyboard.KEY_G, "key.categories.betweenlands");
	//public static final KeyBindingBL SEND_SCOUT = new KeyBindingBL("key.send_scout", Keyboard.KEY_K, "key.categories.betweenlands");
	//public static final KeyBindingBL RETURN_SCOUT = new KeyBindingBL("key.return_scout", Keyboard.KEY_L, "key.categories.betweenlands");
	public static final KeyBindingBL CONNECT_CAVING_ROPE = new KeyBindingBL("key.connect_caving_rope", Keyboard.KEY_V, "key.categories.betweenlands");
	
	public static void init() {
		RADIAL_MENU.register();
		USE_RING.register();
		USE_SECONDARY_RING.register();
		OPEN_POUCH.register();
		//SEND_SCOUT.register();
		//RETURN_SCOUT.register();
		CONNECT_CAVING_ROPE.register();
	}
}
