package thebetweenlands.client.shader;

public class LightSource {
	public final double x, y, z;
	public final float r, g, b, radius;
	public LightSource(double x, double y, double z, float radius, int color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.r = (float)(color >> 16 & 0xff) / 255F;
		this.g = (float)(color >> 8 & 0xff) / 255F;
		this.b = (float)(color & 0xff) / 255F;
	}
	public LightSource(double x, double y, double z, float radius, float r, float g, float b) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.r = r;
		this.g = g;
		this.b = b;
	}
}