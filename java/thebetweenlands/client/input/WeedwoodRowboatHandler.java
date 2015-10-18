package thebetweenlands.client.input;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import org.lwjgl.input.Keyboard;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.perspective.Perspective;
import thebetweenlands.client.perspective.rowboat.PerspectiveWeedwoodRowboatFirstPerson;
import thebetweenlands.client.perspective.rowboat.PerspectiveWeedwoodRowboatThirdPerson;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.forgeevent.client.GetMouseOverEvent;
import thebetweenlands.forgeevent.client.ClientAttackEvent;
import thebetweenlands.network.message.MessageWeedwoodRowboatInput;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class WeedwoodRowboatHandler {
	public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

	public static final Perspective WEEDWOOD_ROWBOAT_THIRD_PERSON_PERSPECTIVE = new PerspectiveWeedwoodRowboatThirdPerson();

	public static final Perspective WEEDWOOD_ROWBOAT_FIRST_PERSON_PERSPECTIVE = new PerspectiveWeedwoodRowboatFirstPerson();

	private List<KeyBindingBL> keyBindings = new ArrayList<KeyBindingBL>();

	private KeyBindingBL oarStrokeLeft = createKeyBinding("oar.stroke.left", Keyboard.KEY_C);

	private KeyBindingBL oarStrokeRight = createKeyBinding("oar.stroke.right", Keyboard.KEY_X);

	private KeyBindingBL oarSquareLeft = createKeyBinding("oar.square.left", Keyboard.KEY_V);

	private KeyBindingBL oarSquareRight = createKeyBinding("oar.square.right", Keyboard.KEY_Z);

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
		Perspective.register(WEEDWOOD_ROWBOAT_THIRD_PERSON_PERSPECTIVE);
		Perspective.register(WEEDWOOD_ROWBOAT_FIRST_PERSON_PERSPECTIVE);
		FMLCommonHandler.instance().bus().register(WEEDWOOD_ROWBOAT_THIRD_PERSON_PERSPECTIVE);
	}

	private void registerKeyBinding() {
		for (KeyBindingBL keyBinding : keyBindings) {
			ClientRegistry.registerKeyBinding(keyBinding);
		}
	}

	public boolean isPlayerInRowboat() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc.thePlayer == null ? false : mc.thePlayer.ridingEntity instanceof EntityWeedwoodRowboat;
	}

	@SubscribeEvent
	public void onKeyInputEvent(KeyInputEvent event) {
		if (pollKeyInput()) {
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

	private boolean pollKeyInput() {
		boolean changed = false;
		for (KeyBindingBL keyBinding : keyBindings) {
			changed |= keyBinding.pollStateChange();
		}
		return changed;
	}

	@SubscribeEvent
	public void onRenderLivingEvent(RenderLivingEvent.Pre event) {
		if (event.entity.ridingEntity instanceof EntityWeedwoodRowboat && RenderWeedwoodRowboat.notRenderingPilot) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onGetMouseOver(GetMouseOverEvent.Pre event) {
		if (shouldPreventWorldInteraction()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerUseItemEventStart(PlayerUseItemEvent.Start event) {
		if (shouldPreventWorldInteraction()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (shouldPreventWorldInteraction()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerAttackEvent(ClientAttackEvent event) {
		if (shouldPreventWorldInteraction()) {
			event.setCanceled(true);
		}
	}

	private boolean shouldPreventWorldInteraction() {
		return isPlayerInRowboat() && WEEDWOOD_ROWBOAT_THIRD_PERSON_PERSPECTIVE.isCurrentPerspective();
	}
}
