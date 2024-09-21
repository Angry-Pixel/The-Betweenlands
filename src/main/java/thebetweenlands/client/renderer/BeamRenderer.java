package thebetweenlands.client.renderer;

import net.minecraft.world.phys.Vec3;

public class BeamRenderer {

	@FunctionalInterface
	public interface BeamVertexConsumer {
		void emit(float x, float y, float z, float u, float v);
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
