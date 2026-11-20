package thebetweenlands.client.renderer;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.world.phys.Vec3;

public class BeamRenderer {

	@FunctionalInterface
	public interface BeamVertexConsumer {
		void emit(float x, float y, float z, float u, float v);
	}

	/**
	 * Revised version of the beam renderer that always has the
	 * beam at a 45 degree angle to the camera along the beam's axis.
	 * @param rx         the beam's starting X (relative to the camera)
	 * @param ry         the beam's starting Y (relative to the camera)
	 * @param rz         the beam's starting Z (relative to the camera)
	 * @param offset     the beam's target offset vector
	 * @param scale      the scale of the beam's cross
	 * @param texUOffset texture offset
	 * @param texUScale  texture scale
	 * @param quat       quaternion representing the camera's rotation
	 * @param consumer   consumer for beam vertex
	 */
	public static void buildNewBeam(
			double rx, double ry, double rz, Vec3 offset,
			float scale, float texUOffset, float texUScale,
			Quaternionf quat, BeamVertexConsumer consumer) {
		buildNewBeam(
				rx, ry, rz,
				offset.x, offset.y, offset.z,
				scale, 0.0f,
				texUOffset, texUScale,
				quat, consumer);
	}

	/**
	 * Revised version of the beam renderer that always has the
	 * beam at a 45 degree angle to the camera along the beam's axis.
	 * 
	 * @param originX        the beam's starting X (relative to the camera)
	 * @param originY        the beam's starting Y (relative to the camera)
	 * @param originZ        the beam's starting Z (relative to the camera)
	 * @param offsetX        the beam's ending X (relative to {@code offsetX})
	 * @param offsetY        the beam's ending Y (relative to {@code offsetY})
	 * @param offsetZ        the beam's ending Z (relative to {@code offsetZ})
	 * @param scale          the scale (in-world) of the beam's quads
	 * @param rotationOffset any extra rotation (in radians) around the beam's axis to include
	 * @param texUOffset     texture offset
	 * @param texUScale      texture scale
	 * @param quat           quaternion representing the camera's rotation
	 * @param consumer       consumer for beam vertex
	 */
	public static void buildNewBeam(
			double originX, double originY, double originZ,
			double offsetX, double offsetY, double offsetZ,
			float scale,      float rotationOffset,
			float texUOffset, float texUScale,
			Quaternionf quat, BeamVertexConsumer consumer) {
		double len = Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
		
		// Create the offset vectors
		Vector3f offsets[]  = new Vector3f[] {new Vector3f(), new Vector3f()};
		Vector3f offsets2[] = new Vector3f[] {new Vector3f(), new Vector3f()};

		// Normalized axis for the beam
		Vector3f beamAxis = new Vector3f().set(offsetX, offsetY, offsetZ).normalize();
		
		// Get a unit vector pointing from
		//     <closest point on the beam axis> --> <camera position>
		Vector3f cameraRelativeAxis = new Vector3f().set(-originX, -originY, -originZ);
		projectOntoNormalize(cameraRelativeAxis, beamAxis, 1.0f);
		
		// <cameraRelativeAxis> is always going to be perpendicular to <beamAxis>,
		//     so <crossProduct> should have a length of roughly 1.0.
		// (Unless they're parallel, in which case its magnitude should be 0.0)
		Vector3f crossProduct = beamAxis.cross(cameraRelativeAxis, cameraRelativeAxis);
		
		if (crossProduct.lengthSquared() <= 1.0e-8) {
			// If the cross product is small, then the camera is exactly along the line of this beam
			offsets[0] .set( 1.0F, -1.0F, 0.0F).rotate(quat);
			offsets2[0].set(-1.0F,  1.0F, 0.0F).rotate(quat);
			
			projectOntoNormalize(offsets[0],  beamAxis, scale);
			projectOntoNormalize(offsets2[0], beamAxis, scale);
		} else {
			// Multiply the cross product by <scale>
			crossProduct.mul(scale);
			// Set the offsets to be at a 45 degree angle (+ rotationOffset) to the camera
			offsets[0].set(crossProduct)
					.rotateAxis(rotationOffset + (float) (Math.PI / 4.0), beamAxis.x, beamAxis.y, beamAxis.z);
			offsets2[0].set(crossProduct)
					.rotateAxis(rotationOffset - (float) (Math.PI / 4.0), beamAxis.x, beamAxis.y, beamAxis.z);
		}
		
		// Set offsets[1] / offsets2[1] to the negation of offsets[0] / offsets2[0]
		offsets[0] .negate(offsets[1]);
		offsets2[0].negate(offsets2[1]);
		
		// Ending positions (in-world)
		double x2 = originX + offsetX;
		double y2 = originY + offsetY;
		double z2 = originZ + offsetZ;

		//br
		consumer.emit((float) (x2 + offsets[0].x), (float) (y2 + offsets[0].y), (float) (z2 + offsets[0].z), (float) (texUOffset + len / (0.2F * texUScale)), 0);
		//tr
		consumer.emit((float) (x2 + offsets[1].x), (float) (y2 + offsets[1].y), (float) (z2 + offsets[1].z), (float) (texUOffset + len / (0.2F * texUScale)), 1);
		//tl
		consumer.emit((float) (originX + offsets[1].x), (float) (originY + offsets[1].y), (float) (originZ + offsets[1].z), texUOffset, 1);
		//bl
		consumer.emit((float) (originX + offsets[0].x), (float) (originY + offsets[0].y), (float) (originZ + offsets[0].z), texUOffset, 0);

		//br
		consumer.emit((float) (x2 + offsets2[0].x), (float) (y2 + offsets2[0].y), (float) (z2 + offsets2[0].z), (float) (texUOffset + len / (0.2 * texUScale)), 0);
		//tr
		consumer.emit((float) (x2 + offsets2[1].x), (float) (y2 + offsets2[1].y), (float) (z2 + offsets2[1].z), (float) (texUOffset + len / (0.2 * texUScale)), 1);
		//tl
		consumer.emit((float) (originX + offsets2[1].x), (float) (originY + offsets2[1].y), (float) (originZ + offsets2[1].z), texUOffset, 1);
		//bl
		consumer.emit((float) (originX + offsets2[0].x), (float) (originY + offsets2[0].y), (float) (originZ + offsets2[0].z), texUOffset, 0);
	}

	/**
	 * Projects vector {@code src} onto vector {@code axis}, and then normalizes the result to a magnitude of {@code scale}
	 * The result is stored in {@code src}.
	 * @param src   the offset vector
	 * @param axis  the axis vector
	 * @param scale the magnitude of the final vector
	 * @return {@code src}
	 */
	private static Vector3f projectOntoNormalize(Vector3f src, Vector3f axis, float scale) {
		return projectOntoNormalize(src, axis, scale, src);
	}
	
	/**
	 * Projects vector {@code src} onto vector {@code axis}, and then normalizes the result to a magnitude of {@code scale}.
	 * The result is stored in {@code dest}.
	 * @param src   the offset vector
	 * @param axis  the axis vector
	 * @param scale the magnitude of the final vector
	 * @param dest  the destination vector
	 * @return {@code dest}
	 */
	private static Vector3f projectOntoNormalize(Vector3f src, Vector3f axis, float scale, Vector3f dest) {
		return axis.mulAdd(-src.dot(axis), src, dest).normalize(scale);
	}
	
	public static void buildBeam(double rx, double ry, double rz, Vec3 end, float scale, float texUOffset, float texUScale,
	                             float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ, BeamVertexConsumer consumer) {
		double len = end.length();

		Vec3 v1 = new Vec3(-rotationX - rotationXY, -rotationZ, -rotationYZ - rotationXZ);
		Vec3 v2 = new Vec3(-rotationX + rotationXY, rotationZ, -rotationYZ + rotationXZ);

		Vec3 facing = v1.cross(v2);

		Vec3 perpendicularDir = end.cross(facing).normalize();

		if(perpendicularDir.length() < 1.0E-4D) {
			//Special case where facing and particle direction perfectly match.
			//Instead of using the crossproduct we can just directly use the v1 and v2 vectors
			//to get the correct result
			facing = v2.subtract(v1).normalize();
			perpendicularDir = end.cross(facing).normalize();
		}

		Vec3 perpendicularDir2 = perpendicularDir.cross(end).normalize();

		Vec3[] offsets = new Vec3[] { perpendicularDir.scale(scale), perpendicularDir.scale(-scale) };
		Vec3[] offsets2 = new Vec3[] { perpendicularDir2.scale(scale), perpendicularDir2.scale(-scale) };

		double x2 = rx + end.x;
		double y2 = ry + end.y;
		double z2 = rz + end.z;

		//br
		consumer.emit((float) (x2 + offsets[0].x), (float) (y2 + offsets[0].y), (float) (z2 + offsets[0].z), (float) (texUOffset + len / (0.2F * texUScale)), 0);
		//tr
		consumer.emit((float) (x2 + offsets[1].x), (float) (y2 + offsets[1].y), (float) (z2 + offsets[1].z), (float) (texUOffset + len / (0.2F * texUScale)), 1);
		//tl
		consumer.emit((float) (rx + offsets[1].x), (float) (ry + offsets[1].y), (float) (rz + offsets[1].z), texUOffset, 1);
		//bl
		consumer.emit((float) (rx + offsets[0].x), (float) (ry + offsets[0].y), (float) (rz + offsets[0].z), texUOffset, 0);

		//br
		consumer.emit((float) (x2 + offsets2[0].x), (float) (y2 + offsets2[0].y), (float) (z2 + offsets2[0].z), (float) (texUOffset + len / (0.2 * texUScale)), 0);
		//tr
		consumer.emit((float) (x2 + offsets2[1].x), (float) (y2 + offsets2[1].y), (float) (z2 + offsets2[1].z), (float) (texUOffset + len / (0.2 * texUScale)), 1);
		//tl
		consumer.emit((float) (rx + offsets2[1].x), (float) (ry + offsets2[1].y), (float) (rz + offsets2[1].z), texUOffset, 1);
		//bl
		consumer.emit((float) (rx + offsets2[0].x), (float) (ry + offsets2[0].y), (float) (rz + offsets2[0].z), texUOffset, 0);
	}
}
