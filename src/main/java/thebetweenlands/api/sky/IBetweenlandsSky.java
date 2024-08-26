package thebetweenlands.api.sky;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public interface IBetweenlandsSky {

	void render(ClientLevel level, float partialTicks, Matrix4f projectionMatrix, Camera camera, Matrix4f frustrumMatrix, boolean isFoggy, Runnable skyFogSetup);

	/**
	 * Sets the rift renderer that renders the rift
	 * @param renderer
	 */
	void setRiftRenderer(IRiftRenderer renderer);

	/**
	 * Returns the rift renderer
	 * @return
	 */
	IRiftRenderer getRiftRenderer();
}
