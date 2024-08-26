package thebetweenlands.api.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public interface IRiftMaskRenderer {
	/**
	 * Renders the sky mask
	 * @param partialTicks
	 * @param level
	 * @param stack
	 * @param skyBrightness
	 */
	void renderMask(ClientLevel level, float partialTicks, PoseStack stack, float skyBrightness);

	/**
	 * Renders the rift overlay
	 * @param partialTicks
	 * @param level
	 * @param stack
	 * @param skyBrightness
	 */
	void renderOverlay(ClientLevel level, float partialTicks, PoseStack stack, float skyBrightness);

	/**
	 * Renders the rift projection mesh
	 * @param partialTicks
	 * @param level
	 * @param camera
	 * @param skyBrightness
	 */
	void renderRiftProjection(ClientLevel level, float partialTicks, Camera camera, float skyBrightness);
}
