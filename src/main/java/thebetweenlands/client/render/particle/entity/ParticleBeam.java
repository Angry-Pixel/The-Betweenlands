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
		float renderScale = 0.1F * this.particleScale;

		float rx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float ry = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;

		double len = this.end.length();

		Vec3d v1 = new Vec3d((double)(-rotationX - rotationXY), (double)(-rotationZ), (double)(-rotationYZ - rotationXZ));
		Vec3d v2 = new Vec3d((double)(-rotationX + rotationXY), (double)(rotationZ), (double)(-rotationYZ + rotationXZ));

		Vec3d facing = v1.crossProduct(v2);

		Vec3d perpendicularDir = this.end.crossProduct(facing).normalize();

		if(perpendicularDir.length() < 1.0E-4D) {
			//Special case where facing and particle direction perfectly match.
			//Instead of using the crossproduct we can just directly use the v1 and v2 vectors
			//to get the correct result
			facing = v2.subtract(v1).normalize();
			perpendicularDir = this.end.crossProduct(facing).normalize();
		}

		Vec3d perpendicularDir2 = perpendicularDir.crossProduct(this.end).normalize();

		Vec3d[] offsets = new Vec3d[] { perpendicularDir.scale(renderScale), perpendicularDir.scale(-renderScale) };
		Vec3d[] offsets2 = new Vec3d[] { perpendicularDir2.scale(renderScale), perpendicularDir2.scale(-renderScale) };

		float x1 = rx;
		float y1 = ry;
		float z1 = rz;

		float x2 = (float)(rx + this.end.x);
		float y2 = (float)(ry + this.end.y);
		float z2 = (float)(rz + this.end.z);

		float texUOffset = this.prevTexUOffset + (this.texUOffset - this.prevTexUOffset) * partialTicks;

		//br
		buffer.pos((double)x2 + offsets[0].x, (double)y2 + offsets[0].y, (double)z2 + offsets[0].z).tex(texUOffset + len / (0.2 * this.texUScale), 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		//tr                                                                                    
		buffer.pos((double)x2 + offsets[1].x, (double)y2 + offsets[1].y, (double)z2 + offsets[1].z).tex(texUOffset + len / (0.2 * this.texUScale), 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		//tl                                                                                    
		buffer.pos((double)x1 + offsets[1].x, (double)y1 + offsets[1].y, (double)z1 + offsets[1].z).tex(texUOffset, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		//bl                                                                                    
		buffer.pos((double)x1 + offsets[0].x, (double)y1 + offsets[0].y, (double)z1 + offsets[0].z).tex(texUOffset, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();

		//br
		buffer.pos((double)x2 + offsets2[0].x, (double)y2 + offsets2[0].y, (double)z2 + offsets2[0].z).tex(texUOffset + len / (0.2 * this.texUScale), 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		//tr                                                                                    
		buffer.pos((double)x2 + offsets2[1].x, (double)y2 + offsets2[1].y, (double)z2 + offsets2[1].z).tex(texUOffset + len / (0.2 * this.texUScale), 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		//tl                                                                                    
		buffer.pos((double)x1 + offsets2[1].x, (double)y1 + offsets2[1].y, (double)z1 + offsets2[1].z).tex(texUOffset, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		//bl                                                                                    
		buffer.pos((double)x1 + offsets2[0].x, (double)y1 + offsets2[0].y, (double)z1 + offsets2[0].z).tex(texUOffset, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevTexUOffset = this.texUOffset;
	}
}
