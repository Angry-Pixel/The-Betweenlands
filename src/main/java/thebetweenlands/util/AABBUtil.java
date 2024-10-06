package thebetweenlands.util;

import net.minecraft.world.phys.AABB;

//old methods yoinked form 1.12. If these have proper replacements please get rid of them
public class AABBUtil {

	public static double calculateXOffset(AABB current, AABB other, double xOffs) {
		if (!(other.maxY <= current.minY) && !(other.minY >= current.maxY) && !(other.maxZ <= current.minZ) && !(other.minZ >= current.maxZ)) {
			double dx;
			if (xOffs > 0.0 && other.maxX <= current.minX) {
				dx = current.minX - other.maxX;
				if (dx < xOffs) {
					xOffs = dx;
				}
			} else if (xOffs < 0.0 && other.minX >= current.maxX) {
				dx = current.maxX - other.minX;
				if (dx > xOffs) {
					xOffs = dx;
				}
			}

			return xOffs;
		} else {
			return xOffs;
		}
	}

	public static double calculateYOffset(AABB current, AABB other, double yOffs) {
		if (!(other.maxX <= current.minX) && !(other.minX >= current.maxX) && !(other.maxZ <= current.minZ) && !(other.minZ >= current.maxZ)) {
			double dy;
			if (yOffs > 0.0 && other.maxY <= current.minY) {
				dy = current.minY - other.maxY;
				if (dy < yOffs) {
					yOffs = dy;
				}
			} else if (yOffs < 0.0 && other.minY >= current.maxY) {
				dy = current.maxY - other.minY;
				if (dy > yOffs) {
					yOffs = dy;
				}
			}

			return yOffs;
		} else {
			return yOffs;
		}
	}

	public static double calculateZOffset(AABB current, AABB other, double zOffs) {
		if (!(other.maxX <= current.minX) && !(other.minX >= current.maxX) && !(other.maxY <= current.minY) && !(other.minY >= current.maxY)) {
			double dz;
			if (zOffs > 0.0 && other.maxZ <= current.minZ) {
				dz = current.minZ - other.maxZ;
				if (dz < zOffs) {
					zOffs = dz;
				}
			} else if (zOffs < 0.0 && other.minZ >= current.maxZ) {
				dz = current.maxZ - other.minZ;
				if (dz > zOffs) {
					zOffs = dz;
				}
			}

			return zOffs;
		} else {
			return zOffs;
		}
	}
}
