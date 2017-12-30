package thebetweenlands.util;

public class ColorUtils {
	public static int toHex(float r, float g, float b, float a) {
		return (((int)(a * 255.0F) & 255) << 24) | (((int)(r * 255.0F) & 255) << 16) | (((int)(g * 255.0F) & 255) << 8) | ((int)(b * 255.0F) & 255);
	}

	public static float[] getRGBA(int color) {
		float a = (float)(color >> 24 & 255) / 255.0F;
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		return new float[] {r, g, b, a};
	}
}
