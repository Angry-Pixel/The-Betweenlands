package thebetweenlands.client.render.particle.entity;

import java.util.List;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathParticle extends Particle {
	protected final List<Vec3d> targetPoints;

	public PathParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, List<Vec3d> targetPoints) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.targetPoints = targetPoints;
	}

	public Vec3d getPosition(double t) {
		int segments = (int)Math.ceil(this.targetPoints.size() / 3.0D);
		int segmentIndex = (int)Math.floor(segments * t);
		Vec3d segment[] = new Vec3d[4];
		for(int v = 0; v < 4; v++) {
			int pi = segmentIndex * 3 + v;
			if(pi >= this.targetPoints.size()) {
				segment[v] = this.targetPoints.get(this.targetPoints.size() - 1);
			} else {
				segment[v] = this.targetPoints.get(pi);
			}
		}
		double segmentStep = 1.0D / segments;
		double segmentStart = segmentStep * segmentIndex;
		double segmentT = ((t - segmentStart) / segmentStep);
		return this.getSegmentPos(segmentT, segment);
	}

	private Vec3d getSegmentPos(double t, Vec3d[] vecs) {
		Vec3d res = new Vec3d(0, 0, 0);
		Vec3d[] ctrlPoints = new Vec3d[4];
		double revT = 1 - t;
		ctrlPoints[0] = newScaledVector3d(revT * revT * revT, vecs[0]);
		ctrlPoints[1] = newScaledVector3d(revT * revT * 3 * t, vecs[1]);
		ctrlPoints[2] = newScaledVector3d(revT * 3 * t * t, vecs[2]); 
		ctrlPoints[3] = newScaledVector3d(t * t * t, vecs[3]);
		for (int i = 0 ; i < 4; i++) {
			res = res.add(ctrlPoints[i]);
		}
		return res;
	}

	private Vec3d newScaledVector3d(double vecScale, Vec3d vec) {
		return vec.scale(vecScale);
	}
}
