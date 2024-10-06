package thebetweenlands.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class Matrix4f {
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	public Matrix4f() {
		this.m00 = this.m11 = this.m22 = this.m33 = 1;
	}

	public Matrix4f(Matrix4f matrix) {
		this.m00 = matrix.m00;
		this.m01 = matrix.m01;
		this.m02 = matrix.m02;
		this.m03 = matrix.m03;
		this.m10 = matrix.m10;
		this.m11 = matrix.m11;
		this.m12 = matrix.m12;
		this.m13 = matrix.m13;
		this.m20 = matrix.m20;
		this.m21 = matrix.m21;
		this.m22 = matrix.m22;
		this.m23 = matrix.m23;
		this.m30 = matrix.m30;
		this.m31 = matrix.m31;
		this.m32 = matrix.m32;
		this.m33 = matrix.m33;
	}

	public Matrix4f(float angle, float x, float y, float z) {
		this();
		float sx = Mth.sin(0.5F * angle * x);
		float cx = Mth.cos(0.5F * angle * x);
		float sy = Mth.sin(0.5F * angle * y);
		float cy = Mth.cos(0.5F * angle * y);
		float sz = Mth.sin(0.5F * angle * z);
		float cz = Mth.cos(0.5F * angle * z);
		float qx = sx * cy * cz + cx * sy * sz;
		float qy = cx * sy * cz - sx * cy * sz;
		float qz = sx * sy * cz + cx * cy * sz;
		float qw = cx * cy * cz - sx * sy * sz;
		float qx2 = 2.0F * qx * qx;
		float qy2 = 2.0F * qy * qy;
		float qz2 = 2.0F * qz * qz;
		this.m00 = 1.0F - qy2 - qz2;
		this.m11 = 1.0F - qz2 - qx2;
		this.m22 = 1.0F - qx2 - qy2;
		this.m33 = 1.0F;
		float qxy = qx * qy;
		float qyz = qy * qz;
		float qzx = qz * qx;
		float qxw = qx * qw;
		float qyw = qy * qw;
		float qzw = qz * qw;
		this.m10 = 2.0F * (qxy + qzw);
		this.m01 = 2.0F * (qxy - qzw);
		this.m20 = 2.0F * (qzx - qyw);
		this.m02 = 2.0F * (qzx + qyw);
		this.m21 = 2.0F * (qyz + qxw);
		this.m12 = 2.0F * (qyz - qxw);
	}

	public void multiply(Matrix4f m) {
		float
			m00, m01, m02, m03,
			m10, m11, m12, m13,
			m20, m21, m22, m23,
			m30, m31, m32, m33;

		m00 = this.m00 * m.m00 + this.m01 * m.m10 + this.m02 * m.m20 + this.m03 * m.m30;
		m01 = this.m00 * m.m01 + this.m01 * m.m11 + this.m02 * m.m21 + this.m03 * m.m31;
		m02 = this.m00 * m.m02 + this.m01 * m.m12 + this.m02 * m.m22 + this.m03 * m.m32;
		m03 = this.m00 * m.m03 + this.m01 * m.m13 + this.m02 * m.m23 + this.m03 * m.m33;
		m10 = this.m10 * m.m00 + this.m11 * m.m10 + this.m12 * m.m20 + this.m13 * m.m30;
		m11 = this.m10 * m.m01 + this.m11 * m.m11 + this.m12 * m.m21 + this.m13 * m.m31;
		m12 = this.m10 * m.m02 + this.m11 * m.m12 + this.m12 * m.m22 + this.m13 * m.m32;
		m13 = this.m10 * m.m03 + this.m11 * m.m13 + this.m12 * m.m23 + this.m13 * m.m33;
		m20 = this.m20 * m.m00 + this.m21 * m.m10 + this.m22 * m.m20 + this.m23 * m.m30;
		m21 = this.m20 * m.m01 + this.m21 * m.m11 + this.m22 * m.m21 + this.m23 * m.m31;
		m22 = this.m20 * m.m02 + this.m21 * m.m12 + this.m22 * m.m22 + this.m23 * m.m32;
		m23 = this.m20 * m.m03 + this.m21 * m.m13 + this.m22 * m.m23 + this.m23 * m.m33;
		m30 = this.m30 * m.m00 + this.m31 * m.m10 + this.m32 * m.m20 + this.m33 * m.m30;
		m31 = this.m30 * m.m01 + this.m31 * m.m11 + this.m32 * m.m21 + this.m33 * m.m31;
		m32 = this.m30 * m.m02 + this.m31 * m.m12 + this.m32 * m.m22 + this.m33 * m.m32;
		m33 = this.m30 * m.m03 + this.m31 * m.m13 + this.m32 * m.m23 + this.m33 * m.m33;

		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public Vec3 multiply(Vec3 point) {
		return this.multiply(point, 0);
	}

	public Vec3 multiply(Vec3 point, float w) {
		return new Vec3(this.m00 * point.x + this.m01 * point.y + this.m02 * point.z + this.m03 * w, this.m10 * point.x + this.m11 * point.y + this.m12 * point.z + this.m13 * w, this.m20 * point.x + this.m21 * point.y + this.m22 * point.z + this.m23 * w);
	}
}
