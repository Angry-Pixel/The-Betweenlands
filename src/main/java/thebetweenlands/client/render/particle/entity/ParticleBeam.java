package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBeam extends Particle {
	protected Vec3d end = new Vec3d(0, 0, 0);
	protected float prevTexUOffset = 0.0f;
	protected float texUOffset = 0.0f;
	protected float texUScale = 1.0f;

	public ParticleBeam(World worldIn, double x, double y, double z, double vx, double vy, double vz, Vec3d end) {
		super(worldIn, x, y, z, vx, vy, vz);
		this.end = end;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float rx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float ry = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

		float renderScale = 0.1F * this.particleScale;

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;

		float texUOffset = this.prevTexUOffset + (this.texUOffset - this.prevTexUOffset) * partialTicks;

		buildBeam(rx, ry, rz, this.end, renderScale, texUOffset, this.texUScale, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ,
				(vx, vy, vz, u, v) -> {
					buffer.pos(vx, vy, vz).tex(u, v).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
				});
	}

	@FunctionalInterface
	public static interface BeamVertexConsumer {
		public void emit(double x, double y, double z, double u, double v);
	}

	public static void buildBeam(double rx, double ry, double rz, Vec3d end, float scale, float texUOffset, float texUScale,
			float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ, BeamVertexConsumer consumer) {
		double len = end.length();

		Vec3d v1 = new Vec3d((double)(-rotationX - rotationXY), (double)(-rotationZ), (double)(-rotationYZ - rotationXZ));
		Vec3d v2 = new Vec3d((double)(-rotationX + rotationXY), (double)(rotationZ), (double)(-rotationYZ + rotationXZ));

		Vec3d facing = v1.crossProduct(v2);

		Vec3d perpendicularDir = end.crossProduct(facing).normalize();

		if(perpendicularDir.length() < 1.0E-4D) {
			//Special case where facing and particle direction perfectly match.
			//Instead of using the crossproduct we can just directly use the v1 and v2 vectors
			//to get the correct result
			facing = v2.subtract(v1).normalize();
			perpendicularDir = end.crossProduct(facing).normalize();
		}

		Vec3d perpendicularDir2 = perpendicularDir.crossProduct(end).normalize();

		Vec3d[] offsets = new Vec3d[] { perpendicularDir.scale(scale), perpendicularDir.scale(-scale) };
		Vec3d[] offsets2 = new Vec3d[] { perpendicularDir2.scale(scale), perpendicularDir2.scale(-scale) };

		double x1 = rx;
		double y1 = ry;
		double z1 = rz;

		double x2 = rx + end.x;
		double y2 = ry + end.y;
		double z2 = rz + end.z;

		//br
		consumer.emit((double)x2 + offsets[0].x, (double)y2 + offsets[0].y, (double)z2 + offsets[0].z, texUOffset + len / (0.2 * texUScale), 0);
		//tr                                                                                    
		consumer.emit((double)x2 + offsets[1].x, (double)y2 + offsets[1].y, (double)z2 + offsets[1].z, texUOffset + len / (0.2 * texUScale), 1);
		//tl                                                                                    
		consumer.emit((double)x1 + offsets[1].x, (double)y1 + offsets[1].y, (double)z1 + offsets[1].z, texUOffset, 1);
		//bl                                                                                    
		consumer.emit((double)x1 + offsets[0].x, (double)y1 + offsets[0].y, (double)z1 + offsets[0].z, texUOffset, 0);

		//br
		consumer.emit((double)x2 + offsets2[0].x, (double)y2 + offsets2[0].y, (double)z2 + offsets2[0].z, texUOffset + len / (0.2 * texUScale), 0);
		//tr                                                                                    
		consumer.emit((double)x2 + offsets2[1].x, (double)y2 + offsets2[1].y, (double)z2 + offsets2[1].z, texUOffset + len / (0.2 * texUScale), 1);
		//tl                                                                                    
		consumer.emit((double)x1 + offsets2[1].x, (double)y1 + offsets2[1].y, (double)z1 + offsets2[1].z, texUOffset, 1);
		//bl                                                                                    
		consumer.emit((double)x1 + offsets2[0].x, (double)y1 + offsets2[0].y, (double)z1 + offsets2[0].z, texUOffset, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevTexUOffset = this.texUOffset;
	}
}
