package thebetweenlands.util;

import net.minecraft.util.math.Vec3d;

public class RotationMatrix {
	private float matrix[] = new float[9];

	private float rotG;
	private float rotB;
	private float rotA;

	/**
	 * Sets the matrix rotations.
	 * @param rotG		Rotation gamma (roll)
	 * @param rotB		Rotation beta (yaw)
	 * @param rotA		Rotation alpha (pitch)
	 */
	public void setRotations(float rotG, float rotB, float rotA) {
		if(this.rotA == rotA && this.rotB == rotB && this.rotG == rotG) {
			return;
		}
		this.rotA = rotA;
		this.rotB = rotB;
		this.rotG = rotG;
		float sinAlpha = (float)Math.sin(rotA);
		float sinBeta = (float)Math.sin(rotB);
		float sinGamma = (float)Math.sin(rotG);
		float cosAlpha = (float)Math.cos(rotA);
		float cosBeta = (float)Math.cos(rotB);
		float cosGamma = (float)Math.cos(rotG);
		this.matrix[0] = cosAlpha * cosBeta;
		this.matrix[1] = (cosAlpha * sinBeta * sinGamma) - (sinAlpha * cosGamma); 
		this.matrix[2] = (cosAlpha * sinBeta * cosGamma) + (sinAlpha * sinGamma);
		this.matrix[3] = sinAlpha * cosBeta;
		this.matrix[4] = (sinAlpha * sinBeta * sinGamma) + (cosAlpha * cosGamma);
		this.matrix[5] = (sinAlpha * sinBeta * cosGamma) - (cosAlpha * sinGamma);
		this.matrix[6] = -sinBeta;
		this.matrix[7] = cosBeta * sinGamma;
		this.matrix[8] = cosBeta * cosGamma;
	}

	/**
	 * Transforms/Rotates the given point around the given center and returns the result.
	 * @param point			Point to rotate
	 * @param centerPoint	Rotation center
	 * @return
	 */
	public Vec3d transformVec(Vec3d point, Vec3d centerPoint) {
		double px = point.xCoord - centerPoint.xCoord;
		double py = point.yCoord - centerPoint.yCoord;
		double pz = point.zCoord - centerPoint.zCoord;
		return new Vec3d(
				this.matrix[0] * px + this.matrix[1] * py + this.matrix[2] * pz + centerPoint.xCoord, 
				this.matrix[3] * px + this.matrix[4] * py + this.matrix[5] * pz + centerPoint.yCoord, 
				this.matrix[6] * px + this.matrix[7] * py + this.matrix[8] * pz + centerPoint.zCoord);
	}

	/**
	 * Transforms/Rotates the given point around the given center and returns the result.
	 * @param point			Point to rotate
	 * @param centerPoint	Rotation center
	 * @return
	 */
	public Vec3UV transformVec(Vec3UV point, Vec3UV centerPoint) {
		double px = point.x - centerPoint.x;
		double py = point.y - centerPoint.y;
		double pz = point.z - centerPoint.z;

		Vec3UV result = new Vec3UV(0, 0, 0);

		result.x = this.matrix[0] * px + this.matrix[1] * py + this.matrix[2] * pz;
		result.y = this.matrix[3] * px + this.matrix[4] * py + this.matrix[5] * pz;
		result.z = this.matrix[6] * px + this.matrix[7] * py + this.matrix[8] * pz;

		result.x += centerPoint.x;
		result.y += centerPoint.y;
		result.z += centerPoint.z;

		return result;
	}
}