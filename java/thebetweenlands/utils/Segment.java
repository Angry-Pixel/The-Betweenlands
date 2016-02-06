package thebetweenlands.utils;

import net.minecraft.util.MathHelper;
import thebetweenlands.utils.vectormath.Point3f;
import thebetweenlands.utils.vectormath.Vector3f;

public class Segment {
	private Point3f start;

	private Point3f to;

	private Vector3f rotation;

	private float length;

	public Segment(Point3f vertex) {
		start = vertex;
	}

	public void connectTo(Point3f to) {
		this.to = to;
		length = start.distance(to);
		Point3f delta = new Point3f(start);
		delta.sub(to);
		float rotationYaw = (float) -Math.atan2(delta.z, delta.x) - CatenaryUtils.HALF_PI;
		float rotationPitch = (float) Math.atan2(delta.y, MathHelper.sqrt_float(delta.x * delta.x + delta.z * delta.z));
		rotation = new Vector3f(rotationYaw, rotationPitch, 0);
	}

	public float getLength() {
		return length;
	}

	public Vector3f getRotation() {
		return new Vector3f(rotation);
	}

	public Point3f getVertex() {
		return new Point3f(start);
	}

	public Point3f pointAt(float t) {
		Point3f interpolated = new Point3f(start);
		interpolated.interpolate(to, t);
		return interpolated;
	}

	public void setLength(float length) {
		this.length = length;
	}
}
