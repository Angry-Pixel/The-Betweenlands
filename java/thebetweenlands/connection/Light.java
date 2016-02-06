package thebetweenlands.connection;

import thebetweenlands.items.lanterns.LightVariant;
import thebetweenlands.utils.vectormath.Point3f;
import thebetweenlands.utils.vectormath.Vector3f;

public class Light {
	private static final int NORMAL_LIGHT = -1;

	private Point3f point;

	private Vector3f rotation;

	private LightVariant variant;

	private Vector3f color;

	public Light(Point3f point) {
		this.point = point;
		rotation = new Vector3f();
		color = new Vector3f(0xFF / (float) 0xFF, 0xEA / (float) 0xFF, 0xC1 / (float) 0xFF);
	}

	public float getBrightness() {
		return NORMAL_LIGHT;
	}

	public LightVariant getVariant() {
		return variant;
	}

	public Vector3f getLight() {
		return color;
	}

	public Point3f getPoint() {
		return new Point3f(point);
	}

	public Vector3f getRotation() {
		return new Vector3f(rotation);
	}

	public void setVariant(LightVariant variant) {
		this.variant = variant;
	}

	public void setColor(int colorValue) {
		color = new Vector3f((colorValue >> 16 & 0xFF) / (float) 0xFF, (colorValue >> 8 & 0xFF) / (float) 0xff, (colorValue & 0xFF) / (float) 0xFF);
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
}
