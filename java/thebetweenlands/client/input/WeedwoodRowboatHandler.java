package thebetweenlands.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import thebetweenlands.client.render.entity.RenderWeedwoodRowboat;
import thebetweenlands.entities.EntityWeedwoodRowboat;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WeedwoodRowboatHandler {
	public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

	private KeyBinding oarStrokeLeft = createKeyBinding("oar.stroke.left", Keyboard.KEY_Z);

	private KeyBinding oarStrokeRight = createKeyBinding("oar.stroke.right", Keyboard.KEY_X);

	private KeyBinding oarSquareLeft = createKeyBinding("oar.square.left", Keyboard.KEY_C);

	private KeyBinding oarSquareRight = createKeyBinding("oar.square.right", Keyboard.KEY_V);

	private WeedwoodRowboatHandler() {}

	private static KeyBinding createKeyBinding(String desc, int keycode) {
		return new KeyBinding("key.weedwoodRowboat." + desc, keycode, "key.categories.weedwoodRowboat");
	}

	public void init() {
		registerKeyBinding();
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(oarStrokeLeft);
		ClientRegistry.registerKeyBinding(oarStrokeRight);
		ClientRegistry.registerKeyBinding(oarSquareLeft);
		ClientRegistry.registerKeyBinding(oarSquareRight);
	}

	@SubscribeEvent
	public void onRenderLivingEvent(RenderLivingEvent.Pre event) {
		if (event.entity.ridingEntity instanceof EntityWeedwoodRowboat && RenderWeedwoodRowboat.notRenderingPilot) {
			event.setCanceled(true);
		}
	}
}
