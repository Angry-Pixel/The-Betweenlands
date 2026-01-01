package thebetweenlands.api.environment.fog;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BiomeFogBetweenlands {

	private float[] fogColorRGB = new float[]{1.0f, 1.0f, 1.0f};

	public BiomeFogBetweenlands() {
		this.setFogColor(10, 30, 22);
	}

	/**
	 * Sets the biome fog color
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public final BiomeFogBetweenlands setFogColor(int red, int green, int blue) {
		this.fogColorRGB[0] = (float)red / 255;
		this.fogColorRGB[1] = (float)green / 255;
		this.fogColorRGB[2] = (float)blue / 255;
		return this;
	}

	/**
	 * Returns the distance where the fog starts to build up.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@OnlyIn(Dist.CLIENT)
	public float getFogStart(float farPlaneDistance, int mode) {
		return mode == -1 ? 0.0F : farPlaneDistance * 0.5F;
	}

	/**
	 * Returns the distance where the fog is fully opaque.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@OnlyIn(Dist.CLIENT)
	public float getFogEnd(float farPlaneDistance, int mode) {
		return farPlaneDistance;
	}

	/**
	 * Returns the fog RGB color.
	 * @return int[3]
	 */
	@OnlyIn(Dist.CLIENT)
	public float[] getFogRGB() {
		return this.fogColorRGB;
	}

	/**
	 * Called to update the fog range and color
	 */
	public void updateFog() {

	}
}
