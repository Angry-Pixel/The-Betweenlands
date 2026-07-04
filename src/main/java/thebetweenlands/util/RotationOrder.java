package thebetweenlands.util;

import net.minecraft.core.Direction.Axis;
import org.joml.Quaternionf;

public enum RotationOrder {

	XYZ(Axis.X, Axis.Y, Axis.Z),
	XZY(Axis.X, Axis.Z, Axis.Y),
	YXZ(Axis.Y, Axis.X, Axis.Z),
	YZX(Axis.Y, Axis.Z, Axis.X),
	ZXY(Axis.Z, Axis.X, Axis.Y),
	ZYX(Axis.Z, Axis.Y, Axis.X);

	private final Axis[] order;

	RotationOrder(Axis... order) {
		this.order = order;
	}

	public Quaternionf rotate(float x, float y, float z) {
		Quaternionf quaternion = new Quaternionf();
		for (Axis anOrder : this.order) {
			switch (anOrder) {
				case X:
					if (x != 0) {
						quaternion = quaternion.rotateX(x);
					}
					break;
				case Y:
					if (y != 0) {
						quaternion = quaternion.rotateY(y);
					}
					break;
				case Z:
					if (z != 0) {
						quaternion = quaternion.rotateZ(z);
					}
					break;
			}
		}
		return quaternion;
	}
}
