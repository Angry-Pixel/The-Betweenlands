package thebetweenlands.utils;

import org.lwjgl.opengl.GL11;

/**
 * 
 * @author Paul Fulham
 *
 */
public enum RotationOrder {
	XYZ(Axis.X, Axis.Y, Axis.Z),
	XZY(Axis.X, Axis.Z, Axis.Y),
	YXZ(Axis.Y, Axis.X, Axis.Z),
	YZX(Axis.Y, Axis.Z, Axis.X),
	ZXY(Axis.Z, Axis.X, Axis.Y),
	ZYX(Axis.Z, Axis.Y, Axis.X);

	private Axis[] order;

	private RotationOrder(Axis... order) {
		this.order = order;
	}

	public void rotate(float x, float y, float z) {
		for (int r = 0; r < order.length; r++) {
			switch (order[r]) {
			case X:
				if (x == 0) {
					break;
				}
				GL11.glRotatef(x, 1, 0, 0);
				break;
			case Y:
				if (y == 0) {
					break;
				}
				GL11.glRotatef(y, 0, 1, 0);
				break;
			case Z:
				if (z == 0) {
					break;
				}
				GL11.glRotatef(z, 0, 0, 1);
				break;
			}
		}
	}
}
