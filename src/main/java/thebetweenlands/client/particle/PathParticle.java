package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class PathParticle extends TextureSheetParticle {

	protected final List<Vec3> targetPoints;

	public PathParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ, List<Vec3> targetPoints) {
		super(level, x, y, z, motionX, motionY, motionZ);
		this.targetPoints = targetPoints;
	}

	public Vec3 getPosition(double t) {
		int segments = (int)Math.ceil(this.targetPoints.size() / 3.0D);
		int segmentIndex = (int)Math.floor(segments * t);
		Vec3[] segment = new Vec3[4];
		for(int v = 0; v < 4; v++) {
			int pi = segmentIndex * 3 + v;
			if(pi >= this.targetPoints.size()) {
				segment[v] = this.targetPoints.getLast();
			} else {
				segment[v] = this.targetPoints.get(pi);
			}
		}
		double segmentStep = 1.0D / segments;
		double segmentStart = segmentStep * segmentIndex;
		double segmentT = ((t - segmentStart) / segmentStep);
		return this.getSegmentPos(segmentT, segment);
	}

	private Vec3 getSegmentPos(double t, Vec3[] vecs) {
		Vec3 res = new Vec3(0, 0, 0);
		Vec3[] ctrlPoints = new Vec3[4];
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

	private Vec3 newScaledVector3d(double vecScale, Vec3 vec) {
		return vec.scale(vecScale);
	}
}
