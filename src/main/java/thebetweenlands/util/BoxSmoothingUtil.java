package thebetweenlands.util;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BoxSmoothingUtil {
	private static float sampleSdf(List<AxisAlignedBB> boxes, double x, double y, double z, float smoothingRange, float boxScale) {
		float sdfDst = 0.0f;

		boolean first = true;

		for(AxisAlignedBB box : boxes) {
			float erx = (float)(box.maxX - box.minX) / 2 * boxScale;
			float ery = (float)(box.maxY - box.minY) / 2 * boxScale;
			float erz = (float)(box.maxZ - box.minZ) / 2 * boxScale;

			float rsx = (float)(x - (box.minX + box.maxX) / 2);
			float rsy = (float)(y - (box.minY + box.maxY) / 2);
			float rsz = (float)(z - (box.minZ + box.maxZ) / 2);

			//Ellipsoid SDF - https://www.iquilezles.org/www/articles/ellipsoids/ellipsoids.htm
			float prx = rsx / erx;
			float pry = rsy / ery;
			float prz = rsz / erz;
			float k1 = MathHelper.sqrt(prx * prx + pry * pry + prz * prz);
			//float ellipsoidDst = (k1 - 1.0f) * Math.min(Math.min(erx, ery), erz);
			float ellipsoidDst = MathHelper.sqrt(rsx * rsx + rsy * rsy + rsz * rsz) * (1.0f - 1.0f / k1);
			/*float prx2 = rsx / (erx * erx);
			float pry2 = rsy / (ery * ery);
			float prz2 = rsz / (erz * erz);
			float k2 = (float)MathHelper.fastInvSqrt(prx2 * prx2 + pry2 * pry2 + prz2 * prz2);
			float ellipsoidDst = k1 * (k1 - 1.0f) * k2;*/

			if(!first) {
				//Smooth min - https://www.iquilezles.org/www/articles/smin/smin.htm
				float h = MathHelper.clamp(0.5f + 0.5f * (ellipsoidDst - sdfDst) / smoothingRange, 0.0f, 1.0f);
				sdfDst = ellipsoidDst + (sdfDst - ellipsoidDst) * h - smoothingRange * h * (1.0f - h);
			} else {
				sdfDst = ellipsoidDst;
				first = false;
			}
		}

		return sdfDst;
	}

	@Nullable
	public static Pair<Vec3d, Vec3d> findClosestSmoothPoint(List<AxisAlignedBB> boxes, float smoothingRange, float boxScale, float dx, int iters, float threshold, Vec3d p) {
		double px = p.x;
		double py = p.y;
		double pz = p.z;

		for(int i = 0; i < iters; i++) {
			float dst = sampleSdf(boxes, px, py, pz, smoothingRange, boxScale);

			float fx1 = sampleSdf(boxes, px + dx, py, pz, smoothingRange, boxScale);
			float fx2 = sampleSdf(boxes, px - dx, py, pz, smoothingRange, boxScale);
			float fy1 = sampleSdf(boxes, px, py + dx, pz, smoothingRange, boxScale);
			float fy2 = sampleSdf(boxes, px, py - dx, pz, smoothingRange, boxScale);
			float fz1 = sampleSdf(boxes, px, py, pz + dx, smoothingRange, boxScale);
			float fz2 = sampleSdf(boxes, px, py, pz - dx, smoothingRange, boxScale);

			float gx = fx2 - fx1;
			float gy = fy2 - fy1;
			float gz = fz2 - fz1;
			float m = (float) MathHelper.fastInvSqrt(gx * gx + gy * gy + gz * gz);
			gx *= m;
			gy *= m;
			gz *= m;

			if(Float.isNaN(gx) || Float.isNaN(gy) || Float.isNaN(gz) || Double.isNaN(px) || Double.isNaN(py) || Double.isNaN(pz)) {
				return null;
			}
			
			float step = Math.max(dst, threshold / 2.0f);

			px += gx * step;
			py += gy * step;
			pz += gz * step;

			if(dst < threshold) {
				return Pair.of(new Vec3d(px, py, pz), new Vec3d(-gx, -gy, -gz).normalize());
			}
		}

		return null;
	}
}
