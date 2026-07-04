package thebetweenlands.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class BlockShapeUtils {

	/**
	 * Adds all blocks within the specified circle on the XZ plane to a list
	 */
	public static List<BlockPos> getCircle(BlockPos pos, int radius, List<BlockPos> list) {
		int xo = radius;
		int zo = 0;
		int err = 1 - radius;

		List<BlockPos> s1 = new ArrayList<>();
		List<BlockPos> s2 = new ArrayList<>();
		List<BlockPos> s3 = new ArrayList<>();
		List<BlockPos> s4 = new ArrayList<>();
		List<BlockPos> s5 = new ArrayList<>();
		List<BlockPos> s6 = new ArrayList<>();
		List<BlockPos> s7 = new ArrayList<>();
		List<BlockPos> s8 = new ArrayList<>();

		while (xo >= zo) {
			s1.add(pos.offset(xo, 0, zo));
			if (xo != zo) s2.add(pos.offset(zo, 0, xo));
			if (zo != 0) s3.add(pos.offset(-zo, 0, xo));
			if (xo != zo) s4.add(pos.offset(-xo, 0, zo));
			if (zo != 0) s5.add(pos.offset(-xo, 0, -zo));
			if (xo != zo) s6.add(pos.offset(-zo, 0, -xo));
			if (zo != 0) s7.add(pos.offset(zo, 0, -xo));
			if (xo != zo && zo != 0) s8.add(pos.offset(xo, 0, -zo));

			zo++;
			if (err < 0) {
				err = err + 2 * zo + 1;
			} else {
				xo--;
				err = err + 2 * (zo - xo) + 1;
			}
		}

		list.addAll(s1);
		for (ListIterator<BlockPos> it = s2.listIterator(s2.size()); it.hasPrevious(); ) {
			list.add(it.previous());
		}
		list.addAll(s3);
		for (ListIterator<BlockPos> it = s4.listIterator(s4.size()); it.hasPrevious(); ) {
			list.add(it.previous());
		}
		list.addAll(s5);
		for (ListIterator<BlockPos> it = s6.listIterator(s6.size()); it.hasPrevious(); ) {
			list.add(it.previous());
		}
		list.addAll(s7);
		for (ListIterator<BlockPos> it = s8.listIterator(s8.size()); it.hasPrevious(); ) {
			list.add(it.previous());
		}

		return list;
	}

	/**
	 * Adds all blocks within the specified circle/ring segment on the XZ plane to a list.
	 * {@code startAngle} must be < {@code endAngle}. Angles are in radians
	 */
	public static List<BlockPos> getRingSegment(BlockPos pos, double startAngle, double endAngle, double innerRadius, double outerRadius, boolean includeCenter, List<BlockPos> list) {
		final double twoPi = Mth.TWO_PI;
		final double halfPi = Mth.HALF_PI;

		startAngle %= twoPi;
		if (startAngle < 0) startAngle += twoPi;

		endAngle %= twoPi;
		if (endAngle < 0) endAngle += twoPi;

		int qa1 = Mth.floor(startAngle / halfPi);

		startAngle -= qa1 * halfPi;
		endAngle -= qa1 * halfPi;

		double radiusSq = outerRadius * outerRadius;

		int rotation = (4 - qa1) % 4;
		int maxRot = Mth.floor(endAngle / halfPi);

		for (int rot = 0; rot <= maxRot; rot++) {
			double ca1 = rot == 0 ? startAngle : 0;
			double ca2 = rot == maxRot ? (endAngle % halfPi) : halfPi;

			double cos1 = Math.cos(ca1);
			double tan1 = Math.tan(ca1);
			double tan2 = Math.tan(ca2);

			int minX = 0;
			int maxX = Mth.ceil(cos1 * outerRadius);

			for (int xo = minX; xo <= maxX; xo++) {
				double dxSq = (xo - 0.5D) * (xo - 0.5D);

				int minZ = Mth.floor(tan1 * xo);
				int maxZ = Mth.floor(Math.min(tan2 * xo, outerRadius));

				if (xo <= innerRadius) {
					minZ += Mth.ceil(innerRadius - Math.sqrt(xo * xo + minZ * minZ));
				}

				for (int zo = minZ; zo < maxZ; zo++) {
					double dstSq = dxSq + (zo + 0.5D) * (zo + 0.5D);

					if (dstSq >= radiusSq) {
						break;
					}

					if (dstSq <= innerRadius * innerRadius) {
						continue;
					}

					int nx = switch (rotation) {
						case 1 -> zo;
						case 2 -> -xo + 1;
						case 3 -> -zo;
						default -> xo - 1;
					};

					int nz = switch (rotation) {
						case 1 -> -xo + 1;
						case 2 -> -zo;
						case 3 -> xo - 1;
						default -> zo;
					};

					if (!includeCenter && nx == 0 && nz == 0) {
						continue;
					}

					list.add(pos.offset(nx, 0, nz));
				}
			}

			rotation = rotation - 1;
			if (rotation < 0) {
				rotation += 4;
			}
		}

		return list;
	}
}
