package thebetweenlands.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class BetweenlandsKeybinds {

	public static final KeyMapping RADIAL_MENU = new KeyMapping("key.thebetweenlands.radial_menu", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.betweenlands");
	public static final KeyMapping USE_RING = new KeyMapping("key.thebetweenlands.use_ring", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.betweenlands");
	public static final KeyMapping USE_SECONDARY_RING = new KeyMapping("key.thebetweenlands.use_secondary_ring", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.categories.betweenlands");
	public static final KeyMapping OPEN_POUCH = new KeyMapping("key.thebetweenlands.open_pouch", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.betweenlands");
	//public static final KeyMapping SEND_SCOUT = new KeyMapping("key.send_scout", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.betweenlands");
	//public static final KeyMapping RETURN_SCOUT = new KeyMapping("key.return_scout", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, "key.categories.betweenlands");
	public static final KeyMapping CONNECT_CAVING_ROPE = new KeyMapping("key.thebetweenlands.connect_caving_rope", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.betweenlands");
}
