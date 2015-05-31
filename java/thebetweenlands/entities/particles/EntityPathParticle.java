package thebetweenlands.entities.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class EntityPathParticle extends EntityFX {
	private int positionIndex = 0;
	private final ArrayList<Vector3d> targetPoints;

	public EntityPathParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, ArrayList<Vector3d> targetPoints) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.targetPoints = targetPoints;
	}

	public Vector3d getPosition(double t) {
		int segments = (int)Math.ceil(this.targetPoints.size() / 3.0D);
		int segmentIndex = (int)Math.floor(segments * t);
		Vector3d segment[] = new Vector3d[4];
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

	private Vector3d getSegmentPos(double t, Vector3d[] vecs) {
		Vector3d res = new Vector3d();
		Vector3d[] ctrlPoints = new Vector3d[4];
		double revT = 1 - t;
		ctrlPoints[0] = newScaledVector3d(revT * revT * revT, vecs[0]);
		ctrlPoints[1] = newScaledVector3d(revT * revT * 3 * t, vecs[1]);
		ctrlPoints[2] = newScaledVector3d(revT * 3 * t * t, vecs[2]); 
		ctrlPoints[3] = newScaledVector3d(t * t * t, vecs[3]);
		for (int i = 0 ; i < 4; i++) {
			res.add(ctrlPoints[i]);
		}
		return res;
	}

	private Vector3d newScaledVector3d(double vecScale, Vector3d vec) {
		Vector3d res = new Vector3d(vec);
		res.scale(vecScale);
		return res;
	}
}
