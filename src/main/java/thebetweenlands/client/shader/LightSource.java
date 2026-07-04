package thebetweenlands.client.shader;

import net.minecraft.world.phys.Vec3;

public record LightSource(double x, double y, double z, float radius, float r, float g, float b) {

	public LightSource(Vec3 pos, float radius, int color) {
		this(pos.x(), pos.y(), pos.z(), radius, color);
	}

	public LightSource(double x, double y, double z, float radius, int color) {
		this(x, y, z, radius, (float) (color >> 16 & 0xff) / 255F, (float) (color >> 8 & 0xff) / 255F, (float) (color & 0xff) / 255F);
	}
}