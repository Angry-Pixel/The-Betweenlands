package thebetweenlands.util;

import javax.annotation.Nullable;

import thebetweenlands.util.TexturePacker.TextureQuad;

public class Vec3UV {
	public double x, y, z, u, v, uw, vw;
	public boolean maxU, maxV;
	@Nullable
	public TextureQuad packedQuad;
	
	public Vec3UV(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = 0.0D;
		this.v = 0.0D;
		this.uw = 1.0D;
		this.vw = 1.0D;
	}
	
	public Vec3UV(double x, double y, double z, double u, double v, double uw, double vw) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = u;
		this.v = v;
		this.uw = uw;
		this.vw = vw;
	}
	
	public Vec3UV(Vec3UV vec, double u, double v, double uw, double vw) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.u = u;
		this.v = v;
		this.uw = uw;
		this.vw = vw;
	}
	
	public Vec3UV(Vec3UV vec, TextureQuad packedQuad, boolean maxU, boolean maxV, double uw, double vw) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.packedQuad = packedQuad;
		this.maxU = maxU;
		this.maxV = maxV;
		this.u = 0;
		this.v = 0;
		this.uw = uw;
		this.vw = vw;
	}
	
	public Vec3UV(double u, double v, double uw, double vw) {
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
		this.u = u;
		this.v = v;
		this.uw = uw;
		this.vw = vw;
	}
	
	public Vec3UV(Vec3UV vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.u = vec.u;
		this.v = vec.v;
		this.uw = vec.uw;
		this.vw = vec.vw;
		this.packedQuad = vec.packedQuad;
		this.maxU = vec.maxU;
		this.maxV = vec.maxV;
	}
	
	public Vec3UV cross(Vec3UV vec) {
		Vec3UV crossProduct = new Vec3UV(0, 0, 0);
		crossProduct.x = this.y * vec.z - vec.y * this.z;
		crossProduct.y = this.z * vec.x - vec.z * this.x;
		crossProduct.z = this.x * vec.y - vec.x * this.y;
		return crossProduct;
	}
	
	public Vec3UV sub(Vec3UV vec) {
		return new Vec3UV(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}
	
	public double dot(Vec3UV vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}
	
	public Vec3UV neg() {
		return new Vec3UV(-this.x, -this.y, -this.z);
	}
	
	public Vec3UV div(double d) {
		return new Vec3UV(this.x / d, this.y / d, this.z / d);
	}
	
	public double len() {
		return Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
	}
	
	public Vec3UV normalized() {
		return this.div(this.len());
	}
	
	public float getU(float umax, int width) {
		if(this.packedQuad != null) {
			return (float) (this.maxU ? this.packedQuad.getPackedMaxU() : this.packedQuad.getPackedU()) * umax;
		}
		double umin = 0;
		return (float) (umin + (umax - umin) * this.uw / (double)width * this.u);
	}
	
	public float getV(float vmax, int height) {
		if(this.packedQuad != null) {
			return (float) (this.maxV ? this.packedQuad.getPackedMaxV() : this.packedQuad.getPackedV()) * vmax;
		}
		double vmin = 0;
		return (float) (vmin + (vmax - vmin) * this.vw / (double)height * this.v);
	}
}