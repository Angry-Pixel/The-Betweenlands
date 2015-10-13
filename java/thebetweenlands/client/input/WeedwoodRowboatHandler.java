package thebetweenlands.client.input;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat;
import thebetweenlands.entities.EntityWeedwoodRowboat;
import thebetweenlands.network.message.MessageWeedwoodRowboatInput;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class WeedwoodRowboatHandler {
	public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

	private List<KeyBindingBL> keyBindings = new ArrayList<KeyBindingBL>();

	private KeyBindingBL oarStrokeLeft = createKeyBinding("oar.stroke.left", Keyboard.KEY_Z);

	private KeyBindingBL oarStrokeRight = createKeyBinding("oar.stroke.right", Keyboard.KEY_X);

	private KeyBindingBL oarSquareLeft = createKeyBinding("oar.square.left", Keyboard.KEY_C);

	private KeyBindingBL oarSquareRight = createKeyBinding("oar.square.right", Keyboard.KEY_V);

	private WeedwoodRowboatHandler() {}

	private KeyBindingBL createKeyBinding(String desc, int keycode) {
		KeyBindingBL keyBinding = new KeyBindingBL("key.weedwoodRowboat." + desc, keycode, "key.categories.weedwoodRowboat");
		keyBindings.add(keyBinding);
		return keyBinding;
	}

	public void init() {
		registerKeyBinding();
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	private void registerKeyBinding() {
		for (KeyBindingBL keyBinding : keyBindings) {
			ClientRegistry.registerKeyBinding(keyBinding);
		}
	}

	@SubscribeEvent
	public void onKeyInputEvent(KeyInputEvent event) {
		if (hasControlInputChanged()) {
			boolean oarStrokeLeft = this.oarStrokeLeft.isPressed();
			boolean oarStrokeRight = this.oarStrokeRight.isPressed();
			boolean oarSquareLeft = this.oarSquareLeft.isPressed();
			boolean oarSquareRight = this.oarSquareRight.isPressed();
			MessageWeedwoodRowboatInput packet = new MessageWeedwoodRowboatInput(oarStrokeLeft, oarStrokeRight, oarSquareLeft, oarSquareRight);
			TheBetweenlands.networkWrapper.sendToServer(packet);
		}
		for (KeyBindingBL keyBinding : keyBindings) {
			keyBinding.update();
		}
	}

	private boolean hasControlInputChanged() {
		for (KeyBindingBL keyBinding : keyBindings) {
			if (keyBinding.hasStateChanged()) {
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onRenderLivingEvent(RenderLivingEvent.Pre event) {
		if (event.entity.ridingEntity instanceof EntityWeedwoodRowboat && RenderWeedwoodRowboat.notRenderingPilot) {
			event.setCanceled(true);
		}
	}
}
