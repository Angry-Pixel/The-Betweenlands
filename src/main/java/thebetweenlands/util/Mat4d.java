package thebetweenlands.util;

import net.minecraft.world.phys.Vec3;

public class Mat4d {

	public double m00;
	public double m01;
	public double m02;
	public double m03;
	public double m10;
	public double m11;
	public double m12;
	public double m13;
	public double m20;
	public double m21;
	public double m22;
	public double m23;
	public double m30;
	public double m31;
	public double m32;
	public double m33;

	public Mat4d() {}

	public Mat4d(Mat4d matrix) {
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

	public void asIdentity() {
		this.m00 = this.m11 = this.m22 = this.m33 = 1;
		this.m01 = this.m02 = this.m03 = this.m10 = this.m12 = this.m13 = this.m20 = this.m21 = this.m23 = this.m30 = this.m31 = this.m32 = 0;
	}

	public void asTranslation(double x, double y, double z) {
		this.asIdentity();
		this.m03 = x;
		this.m13 = y;
		this.m23 = z;
	}

	public void asRotation(double x, double y, double z, double angle) {
		this.asIdentity();
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		double t = 1 - c;
		this.m00 = c + x * x * t;
		this.m11 = c + y * y * t;
		this.m22 = c + z * z * t;
		double a = x * y * t;
		double b = z * s;
		this.m10 = a + b;
		this.m01 = a - b;
		a = x * z * t;
		b = y * s;
		this.m20 = a - b;
		this.m02 = a + b;
		a = y * z * t;
		b = x * s;
		this.m21 = a + b;
		this.m12 = a - b;
	}

	public void asQuaternion(Quat quat) {
		this.asQuaternion(quat.x, quat.y, quat.z, quat.w);
	}

	public void asQuaternion(double x, double y, double z, double w) {
		this.m00 = 1 - 2 * y * y - 2 * z * z;
		this.m10 = 2 * (x * y + w * z);
		this.m20 = 2 * (x * z - w * y);
		this.m01 = 2 * (x * y - w * z);
		this.m11 = 1 - 2 * x * x - 2 * z * z;
		this.m21 = 2 * (y * z + w * x);
		this.m02 = 2 * (x * z + w * y);
		this.m12 = 2 * (y * z - w * x);
		this.m22 = 1 - 2 * x * x - 2 * y * y;
		this.m03 = 0;
		this.m13 = 0;
		this.m23 = 0;
		this.m30 = 0;
		this.m31 = 0;
		this.m32 = 0;
		this.m33 = 1;
	}

	public void mul(Mat4d m) {
		double m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;
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

	public Vec3 transform(Vec3 point) {
		return new Vec3(this.m00 * point.x() + this.m01 * point.y() + this.m02 * point.z() + this.m03, this.m10 * point.x() + this.m11 * point.y() + this.m12 * point.z() + this.m13, this.m20 * point.x() + this.m21 * point.y() + this.m22 * point.z() + this.m23);
	}
}
