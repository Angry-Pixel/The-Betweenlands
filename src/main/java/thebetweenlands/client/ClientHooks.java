package thebetweenlands.client;

import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.PreRenderShadersEvent;

public final class ClientHooks {
	private ClientHooks() { }

	/**
	 * Called before the vanilla shaders are applied to the screen
	 */
	public static void onPreRenderShaders(float partialTicks) {
		MinecraftForge.EVENT_BUS.post(new PreRenderShadersEvent(partialTicks));
	}
}
