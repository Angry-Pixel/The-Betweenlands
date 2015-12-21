package thebetweenlands.utils;

import java.util.Stack;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import thebetweenlands.utils.pool.InstanceProvider;
import thebetweenlands.utils.pool.Pool;

public class Matrix {
	private Pool<Matrix4d> matrixPool;

	private Pool<Vector3d> vectorPool;

	private Pool<AxisAngle4d> axisAnglePool;

	private Stack<Matrix4d> matrixStack;

	public Matrix() {
		this(0);
	}

	public Matrix(int poolSize) {
		if (poolSize < 0) {
			throw new IllegalArgumentException("poolSize must be greater or equal to zero");
		}
		matrixPool = new Pool<Matrix4d>(new InstanceProvider<Matrix4d>() {
			@Override
			public Matrix4d newInstance() {
				return new Matrix4d();
			}
		}, poolSize);
		vectorPool = new Pool<Vector3d>(new InstanceProvider<Vector3d>() {
			@Override
			public Vector3d newInstance() {
				return new Vector3d();
			}
		}, poolSize);
		axisAnglePool = new Pool<AxisAngle4d>(new InstanceProvider<AxisAngle4d>() {
			@Override
			public AxisAngle4d newInstance() {
				return new AxisAngle4d();
			}
		}, poolSize);
		matrixStack = new Stack<Matrix4d>();
		Matrix4d mat = new Matrix4d();
		mat.setIdentity();
		matrixStack.push(mat);
	}

	private Matrix4d getMatrix() {
		Matrix4d mat = matrixPool.getInstance();
		mat.setZero();
		return mat;
	}

	private void freeMatrix(Matrix4d mat) {
		matrixPool.freeInstance(mat);
	}

	private Vector3d getVector(double x, double y, double z) {
		Vector3d vector = vectorPool.getInstance();
		vector.set(x, y, z);
		return vector;
	}

	private void freeVector(Vector3d vector) {
		vectorPool.freeInstance(vector);
	}

	private AxisAngle4d getAxisAngle(double x, double y, double z, double angle) {
		AxisAngle4d axisAngle = axisAnglePool.getInstance();
		axisAngle.set(x, y, z, angle);
		return axisAngle;
	}

	private void freeAxisAngle(AxisAngle4d axisAngle) {
		axisAnglePool.freeInstance(axisAngle);
	}

	public void push() {
		Matrix4d mat = getMatrix();
		mat.set(matrixStack.peek());
		matrixStack.push(mat);
	}

	public void pop() {
		if (matrixStack.size() < 2) {
			throw new StackUnderflowError();
		}
		freeMatrix(matrixStack.pop());
	}

	public void setIdentity() {
		matrixStack.peek().setIdentity();
	}

	public void translate(double x, double y, double z) {
		Matrix4d mat = matrixStack.peek();
		Matrix4d translation = getMatrix();
		translation.setIdentity();
		Vector3d vector = getVector(x, y, z);
		translation.setTranslation(vector);
		freeVector(vector);
		mat.mul(translation);
		freeMatrix(translation);
	}

	public void rotate(double angle, double x, double y, double z) {
		Matrix4d mat = matrixStack.peek();
		Matrix4d rotation = getMatrix();
		rotation.setIdentity();
		AxisAngle4d axisAngle = getAxisAngle(x, y, z, angle);
		rotation.setRotation(axisAngle);
		freeAxisAngle(axisAngle);
		mat.mul(rotation);
		freeMatrix(rotation);
	}

	public void scale(double x, double y, double z) {
		Matrix4d mat = matrixStack.peek();
		Matrix4d scale = getMatrix();
		scale.m00 = x;
		scale.m11 = y;
		scale.m22 = z;
		scale.m33 = 1;
		mat.mul(scale);
		freeMatrix(scale);
	}

	public void transform(Point3f point) {
		Matrix4d mat = matrixStack.peek();
		mat.transform(point);
	}

	public void transform(Vector3f point) {
		Matrix4d mat = matrixStack.peek();
		mat.transform(point);
	}

	public Point3f getTranslation() {
		Matrix4d mat = matrixStack.peek();
		Point3f translation = new Point3f();
		mat.transform(translation);
		return translation;
	}

	public Quat4f getRotation() {
		Matrix4d mat = matrixStack.peek();
		Quat4f rotation = new Quat4f();
		mat.get(rotation);
		return rotation;
	}
}
