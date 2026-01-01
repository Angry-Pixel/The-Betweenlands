package thebetweenlands.api.sky;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public interface BetweenlandsSky {

	void render(ClientLevel level, float partialTicks, Matrix4f viewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable skyFogSetup);

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
