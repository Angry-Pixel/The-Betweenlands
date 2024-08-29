package thebetweenlands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.client.event.ClientRegistrationEvents;

import javax.annotation.Nullable;

public class BetweenlandsClient {

	@Nullable
	public static ClientLevel getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Nullable
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}
	
	@Nullable
	public static Player getCameraPlayer(Minecraft mc) {
		return mc != null && mc.getCameraEntity() instanceof Player player ? player : null;
	}
	
	public static void playLocalSound(SoundInstance instance) {
		Minecraft.getInstance().getSoundManager().play(instance);
	}

	public static RiftVariantReloadListener getRiftVariantLoader() {
		return ClientRegistrationEvents.riftVariantListener;
	}
}
