package thebetweenlands.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class BlockShapeUtils {
	private BlockShapeUtils() { }

	/**
	 * Adds all blocks within the specified circle on the XZ plane to a list
	 * @param pos
	 * @param radius
	 * @param list
	 * @return
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
			s1.add(pos.add(xo, 0, zo));
			if(xo != zo) s2.add(pos.add(zo, 0, xo));
			if(zo != 0) s3.add(pos.add(-zo, 0, xo));
			if(xo != zo) s4.add(pos.add(-xo, 0, zo));
			if(zo != 0) s5.add(pos.add(-xo, 0, -zo));
			if(xo != zo) s6.add(pos.add(-zo, 0, -xo));
			if(zo != 0) s7.add(pos.add(zo, 0, -xo));
			if(xo != zo && zo != 0) s8.add(pos.add(xo, 0, -zo));

			if(err < 0) {
				zo++;
				err = err + 2 * zo + 1;
			} else {
				zo++;
				xo--;
				err = err + 2 * (zo - xo) + 1;
			}
		}

		list.addAll(s1);
		for(ListIterator<BlockPos> it = s2.listIterator(s2.size()); it.hasPrevious();) {
			list.add(it.previous());
		}
		list.addAll(s3);
		for(ListIterator<BlockPos> it = s4.listIterator(s4.size()); it.hasPrevious();) {
			list.add(it.previous());
		}
		list.addAll(s5);
		for(ListIterator<BlockPos> it = s6.listIterator(s6.size()); it.hasPrevious();) {
			list.add(it.previous());
		}
		list.addAll(s7);
		for(ListIterator<BlockPos> it = s8.listIterator(s8.size()); it.hasPrevious();) {
			list.add(it.previous());
		}

		return list;
	}

	/**
	 * Adds all blocks within the specified circle/ring segment on the XZ plane to a list.
	 * {@code startAngle} must be < {@code endAngle}. Angles are in radians
	 * @param pos
	 * @param startAngle
	 * @param endAngle
	 * @param innerRadius
	 * @param outerRadius
	 * @param includeCenter
	 * @param list
	 * @return
	 */
	public static List<BlockPos> getRingSegment(BlockPos pos, double startAngle, double endAngle, double innerRadius, double outerRadius, boolean includeCenter, List<BlockPos> list) {
		final double twoPi = Math.PI * 2;
		final double halfPi = Math.PI / 2;

		startAngle %= twoPi;
		if(startAngle < 0) startAngle += twoPi;

		endAngle %= twoPi;
		if(endAngle < 0) endAngle += twoPi;

		int qa1 = MathHelper.floor(startAngle / halfPi);

		startAngle -= qa1 * halfPi;
		endAngle -= qa1 * halfPi;

		double radiusSq = outerRadius * outerRadius;

		int rotation = (4 - qa1) % 4;
		int maxRot = MathHelper.floor(endAngle / halfPi);

		for(int rot = 0; rot <= maxRot; rot++) {
			double ca1 = rot == 0 ? startAngle : 0;
			double ca2 = rot == maxRot ? (endAngle % halfPi) : halfPi;

			double cos1 = Math.cos(ca1);
			double tan1 = Math.tan(ca1);
			double tan2 = Math.tan(ca2);

			int minX = 0;
			int maxX = MathHelper.ceil(cos1 * outerRadius);

			for(int xo = minX; xo <= maxX; xo++) {
				double dxSq = (xo - 0.5D) * (xo - 0.5D);

				int minZ = MathHelper.floor(tan1 * xo);
				int maxZ = MathHelper.floor(Math.min(tan2 * xo, outerRadius));

				if(xo <= innerRadius) {
					minZ += MathHelper.ceil(innerRadius - Math.sqrt(xo * xo + minZ * minZ));
				}

				for(int zo = minZ; zo < maxZ; zo++) {
					double dstSq = dxSq + (zo + 0.5D) * (zo + 0.5D);

					if(dstSq >= radiusSq) {
						break;
					}

					if(dstSq <= innerRadius*innerRadius) {
						continue;
					}

					int nx;
					switch(rotation) {
					default:
					case 0:
						nx = xo - 1;
						break;
					case 1:
						nx = zo;
						break;
					case 2:
						nx = -xo + 1;
						break;
					case 3:
						nx = -zo;
						break;
					}

					int nz;
					switch(rotation) {
					default:
					case 0:
						nz = zo;
						break;
					case 1:
						nz = -xo + 1;
						break;
					case 2:
						nz = -zo;
						break;
					case 3:
						nz = xo - 1;
						break;
					}

					if(!includeCenter && nx == 0 && nz == 0) {
						continue;
					}

					list.add(pos.add(nx, 0, nz));
				}
			}

			rotation = rotation - 1;
			if(rotation < 0) {
				rotation += 4;
			}
		}

		return list;
	}
}
