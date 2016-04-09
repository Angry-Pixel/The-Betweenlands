package thebetweenlands.util;

import net.minecraft.client.renderer.OpenGlHelper;

public class LightingUtil {
    public static final LightingUtil INSTANCE = new LightingUtil();

    private float lastX, lastY;

    /**
     * Sets the currently used lighting (0 - 255).
     * Use {@link LightingUtil#revert()} after rendering to revert to previous lighting.
     *
     * @param lighting
     */
    public void setLighting(int lighting) {
        this.lastX = OpenGlHelper.lastBrightnessX;
        this.lastY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 238.0f / 255.0f * lighting, 238.0f / 255.0f * lighting);
    }

    /**
     * Reverts the lighting to the previous state.
     */
    public void revert() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, this.lastX, this.lastY);
    }
}
