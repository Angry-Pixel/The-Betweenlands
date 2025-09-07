package thebetweenlands.util;

import net.minecraft.resources.ResourceLocation;

public class TextureQuad {

	private final int u, v, w, h;
	private int su, sv, sw, sh;
	private double packedU, packedV, packedMaxU, packedMaxV;
	private ResourceLocation packedLocation;

	public TextureQuad(int u, int v, int width, int height) {
		this.su = this.u = u;
		this.sv = this.v = v;
		this.sw = this.w = width;
		this.sh = this.h = height;
	}

	public double getPackedU() {
		return this.packedU;
	}

	public double getPackedV() {
		return this.packedV;
	}

	public double getPackedMaxU() {
		return this.packedMaxU;
	}

	public double getPackedMaxV() {
		return this.packedMaxV;
	}

	public ResourceLocation getPackedLocation() {
		return this.packedLocation;
	}

	public void rescale(float scaleU, float scaleV) {
		this.su = (int) Math.floor(this.u * scaleU);
		this.sv = (int) Math.floor(this.v * scaleV);
		this.sw = (int) Math.floor(this.w * scaleU);
		this.sh = (int) Math.floor(this.h * scaleV);
	}
}
