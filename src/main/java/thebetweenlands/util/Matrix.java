package thebetweenlands.util;

import net.minecraft.world.phys.Vec3;

import java.util.Stack;

public class Matrix {

	private final Stack<Mat4d> matrixStack;

	public Matrix() {
		this.matrixStack = new Stack<>();
		Mat4d mat = new Mat4d();
		mat.asIdentity();
		this.matrixStack.push(mat);
	}

	public void push() {
		this.matrixStack.push(new Mat4d(this.matrixStack.peek()));
	}

	public void pop() {
		if (this.matrixStack.size() < 2) {
			throw new RuntimeException("Stack underflow");
		}
		this.matrixStack.pop();
	}

	public void setIdentity() {
		this.matrixStack.peek().asIdentity();
	}

	public void translate(double x, double y, double z) {
		Mat4d mat = this.matrixStack.peek();
		Mat4d translation = new Mat4d();
		translation.asTranslation(x, y, z);
		mat.mul(translation);
	}

	public void rotate(double angle, double x, double y, double z) {
		Mat4d mat = this.matrixStack.peek();
		Mat4d rotation = new Mat4d();
		rotation.asRotation(x, y, z, angle);
		mat.mul(rotation);
	}

	public void rotate(Quat quat) {
		Mat4d mat = this.matrixStack.peek();
		Mat4d rotation = new Mat4d();
		rotation.asQuaternion(quat);
		mat.mul(rotation);
	}

	public void scale(double x, double y, double z) {
		Mat4d mat = this.matrixStack.peek();
		Mat4d scale = new Mat4d();
		scale.m00 = x;
		scale.m11 = y;
		scale.m22 = z;
		scale.m33 = 1;
		mat.mul(scale);
	}

	public Vec3 transform(Vec3 point) {
		return this.matrixStack.peek().transform(point);
	}
}
