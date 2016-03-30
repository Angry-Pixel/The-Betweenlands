package thebetweenlands.client.input;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.perspective.Perspective;
import thebetweenlands.client.perspective.rowboat.PerspectiveWeedwoodRowboatFirstPerson;
import thebetweenlands.client.perspective.rowboat.PerspectiveWeedwoodRowboatThirdPerson;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.forgeevent.client.ClientAttackEvent;
import thebetweenlands.forgeevent.client.RenderEntitiesEvent;
import thebetweenlands.network.message.MessageWeedwoodRowboatInput;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class WeedwoodRowboatHandler {
	public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

	public static final Perspective THIRD_PERSON_PERSPECTIVE = new PerspectiveWeedwoodRowboatThirdPerson();

	public static final Perspective FIRST_PERSON_PERSPECTIVE = new PerspectiveWeedwoodRowboatFirstPerson();

	private List<BLKey> keyBindings = new ArrayList<BLKey>();

	private BLKey oarStrokeLeft;

	private BLKey oarStrokeRight;

	private boolean isRenderingEntities;

	private WeedwoodRowboatHandler() {
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		oarStrokeLeft = createKeyBinding(settings.keyBindLeft);
		oarStrokeRight = createKeyBinding(settings.keyBindRight);
	}

	private KeyBindingBL createKeyBinding(String desc, int keycode) {
		KeyBindingBL keyBinding = new KeyBindingBL("key.weedwoodRowboat." + desc, keycode, "key.categories.weedwoodRowboat");
		keyBindings.add(keyBinding);
		return keyBinding;
	}

	private KeyBindingWrapper createKeyBinding(KeyBinding keyBinding) {
		KeyBindingWrapper wrapper = new KeyBindingWrapper(keyBinding);
		keyBindings.add(wrapper);
		return wrapper;
	}

	public void init() {
		registerKeyBinding();
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		Perspective.register(THIRD_PERSON_PERSPECTIVE);
		Perspective.register(FIRST_PERSON_PERSPECTIVE);
		FMLCommonHandler.instance().bus().register(THIRD_PERSON_PERSPECTIVE);
	}

	private void registerKeyBinding() {
		for (BLKey keyBinding : keyBindings) {
			keyBinding.register();
		}
	}

	public boolean isPlayerInRowboat() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc.thePlayer == null ? false : mc.thePlayer.ridingEntity instanceof EntityWeedwoodRowboat;
	}

	@SubscribeEvent
	public void onKeyInputEvent(KeyInputEvent event) {
		if (pollKeyInput()) {
			boolean oarStrokeLeft = this.oarStrokeLeft.isDown();
			boolean oarStrokeRight = this.oarStrokeRight.isDown();
			MessageWeedwoodRowboatInput packet = new MessageWeedwoodRowboatInput(oarStrokeLeft, oarStrokeRight);
			TheBetweenlands.networkWrapper.sendToServer(packet);
		}
		for (BLKey keyBinding : keyBindings) {
			keyBinding.update();
		}
	}

	private boolean pollKeyInput() {
		boolean changed = false;
		for (BLKey keyBinding : keyBindings) {
			changed |= keyBinding.pollStateChange();
		}
		return changed;
	}

	@SubscribeEvent
	public void onRenderTickEvent(RenderEntitiesEvent.Pre event) {
		isRenderingEntities = true;
	}

	@SubscribeEvent
	public void onRenderTickEvent(RenderEntitiesEvent.Post event) {
		isRenderingEntities = false;
	}

	@SubscribeEvent
	public void onRenderLivingEvent(RenderLivingEvent.Pre event) {
		if (isRenderingEntities && event.entity.ridingEntity instanceof EntityWeedwoodRowboat && RenderWeedwoodRowboat.shouldPreventRidingRender) {
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

	public boolean shouldPreventWorldInteraction() {
		return isPlayerInRowboat() && THIRD_PERSON_PERSPECTIVE.isCurrentPerspective();
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player == null) {
			return;
		}
		if (player.ridingEntity instanceof EntityWeedwoodRowboat) {
			return;
		}
		Perspective perspective = Perspective.getCurrentPerspective();
		if (perspective == FIRST_PERSON_PERSPECTIVE || perspective == THIRD_PERSON_PERSPECTIVE) {
			Perspective.FIRST_PERSON.switchTo();
		}
	}
}
