package thebetweenlands.client.input;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import thebetweenlands.utils.MathUtils;
import cpw.mods.fml.client.registry.ClientRegistry;

public class WeedwoodRowboatInputHandler {
	public static final WeedwoodRowboatInputHandler INSTANCE = new WeedwoodRowboatInputHandler();

	private KeyBinding oarStrokeLeft = createKeyBinding("oar.stroke.left", Keyboard.KEY_Z);

	private KeyBinding oarStrokeRight = createKeyBinding("oar.stroke.right", Keyboard.KEY_X);

	private KeyBinding oarSquareLeft = createKeyBinding("oar.square.left", Keyboard.KEY_C);

	private KeyBinding oarSquareRight = createKeyBinding("oar.square.right", Keyboard.KEY_V);

	private WeedwoodRowboatInputHandler() {}

	private static KeyBinding createKeyBinding(String id, int keycode) {
		return new KeyBinding("key.weedwoodRowboat." + id, keycode, "key.categories.weedwoodRowboat");
	}

	public void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(oarStrokeLeft);
		ClientRegistry.registerKeyBinding(oarStrokeRight);
		ClientRegistry.registerKeyBinding(oarSquareLeft);
		ClientRegistry.registerKeyBinding(oarSquareRight);
	}
}
