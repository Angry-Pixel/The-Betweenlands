package thebetweenlands.api.sky;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public interface IRiftRenderer {
	/**
	 * Renders the sky rift
	 * @param partialTicks
	 * @param level
	 */
	void render(ClientLevel level, float partialTicks, Matrix4f projectionMatrix, Camera camera, Matrix4f frustrumMatrix, boolean isFoggy, Runnable skyFogSetup);

	/**
	 * Sets the rift mask renderer that renders the rift mask and the overlay
	 * @param maskRenderer
	 */
	void setRiftMaskRenderer(IRiftMaskRenderer maskRenderer);

	/**
	 * Returns the rift mask renderer
	 * @return
	 */
	IRiftMaskRenderer getRiftMaskRenderer();

	/**
	 * Sets the rift sky renderer
	 * @param skyRenderer
	 */
	void setRiftSkyRenderer(IRiftSkyRenderer skyRenderer);

	/**
	 * Returns the rift sky renderer
	 * @return
	 */
	IRiftSkyRenderer getRiftSkyRenderer();
}
