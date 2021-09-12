package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleSwirl extends Particle {
	protected float progress = 0;
	protected float startRotation;
	protected float endRadius;
	protected double dragX, dragY, dragZ;

	protected double offsetX, offsetY, offsetZ;
	protected double targetX, targetY, targetZ;
	protected double targetMotionX, targetMotionY, targetMotionZ;

	protected boolean rotate3D = false;
	
	private boolean firstUpdate = true;

	private static final float VELOCITY_OFFSET_MULTIPLIER = 4.0F;

	protected double prevRotSinX;
	protected double prevRotCosZ;
	protected double rotSinX;
	protected double rotCosZ;
	
	protected double rotationSpeed = 4.0D;
	
	public ParticleSwirl(World world, double x, double y, double z, int maxAge, float scale, float progress) {
		super(world, x, y, z, 0, 0, 0);
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.progress = progress;

		this.startRotation = (float) (this.rand.nextFloat() * Math.PI * 2.0F);
		this.endRadius = 0.35F + this.rand.nextFloat() * 0.35F;

		this.particleMaxAge = maxAge;
		
		this.particleScale = scale;
	}

	public ParticleSwirl setOffset(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		return this;
	}
	
	public ParticleSwirl setTarget(double x, double y, double z) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;

		if(this.firstUpdate) {
			this.updatePosition();
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
		}
		
		return this;
	}

	public ParticleSwirl setTargetMotion(double x, double y, double z) {
		this.targetMotionX = x;
		this.targetMotionY = y;
		this.targetMotionZ = z;

		if(this.firstUpdate) {
			double tmx = this.targetMotionX;
			double tmy = this.targetMotionY;
			double my = tmy * VELOCITY_OFFSET_MULTIPLIER;
			double tmz = this.targetMotionZ;

			this.dragX = MathHelper.clamp(tmx * VELOCITY_OFFSET_MULTIPLIER, -1, 1);
			this.dragY = MathHelper.clamp(my, -0.3D, 1);
			this.dragZ = MathHelper.clamp(tmz * VELOCITY_OFFSET_MULTIPLIER, -1, 1);
		}
		
		return this;
	}

	public ParticleSwirl setRotationSpeed(double speed) {
		this.rotationSpeed = speed;
		return this;
	}
	
	public ParticleSwirl setRotate3D(boolean rotate3D) {
		this.rotate3D = rotate3D;
		return this;
	}
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.particleAge > 2) {
			if(this.rotate3D) {
				float minU = (float)this.particleTextureIndexX / 16.0F;
		        float maxU = minU + 0.0624375F;
		        float minV = (float)this.particleTextureIndexY / 16.0F;
		        float maxV = minV + 0.0624375F;
		        float scale = 0.1F * this.particleScale;

		        if (this.particleTexture != null) {
		            minU = this.particleTexture.getMinU();
		            maxU = this.particleTexture.getMaxU();
		            minV = this.particleTexture.getMinV();
		            maxV = this.particleTexture.getMaxV();
		        }

		        float rx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		        float ry = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		        float rz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		        int brightness = this.getBrightnessForRender(partialTicks);
		        int lightmapX = brightness >> 16 & 65535;
		        int lightmapY = brightness & 65535;
		        /*Vec3d[] vertices = new Vec3d[] {new Vec3d((double)(-rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double)(-rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(rotationYZ * scale - rotationXZ * scale))};

		        if(this.particleAngle != 0.0F) {
		            float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
		            float f9 = MathHelper.cos(f8 * 0.5F);
		            float f10 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.x;
		            float f11 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.y;
		            float f12 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.z;
		            Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

		            for(int l = 0; l < 4; ++l) {
		                vertices[l] = vec3d.scale(2.0D * vertices[l].dotProduct(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vertices[l]).scale((double)(2.0F * f9)));
		            }
		        }*/


		        float irx = (float)(this.prevRotSinX + (this.rotSinX - this.prevRotSinX) * (double)partialTicks);
		        float irz = (float)(this.prevRotCosZ + (this.rotCosZ - this.prevRotCosZ) * (double)partialTicks);
		        
		        
		        
		        double tilt = -30.0D;
		        
		        double h = Math.cos(tilt / 360.0f * 2 * Math.PI);
		        double v = Math.sin(tilt / 360.0f * 2 * Math.PI);
		        
		        double side = h;
		        double out = v;

		        double sidex = -irz * scale;
		        double sidez = irx * scale;
		        double outx = irx * scale;
		        double outz = irz * scale;
		        
		        Vec3d[] vertices = new Vec3d[] {
		        		new Vec3d(-sidex * side + (outx * out - sidex * (1 - side)), -scale * side, -sidez * side + (outz * out - sidez * (1 - side))),
		        		new Vec3d(-sidex * side + (-outx * out - sidex * (1 - side)), scale * side, -sidez * side + (-outz * out - sidez * (1 - side))),
		        		new Vec3d(sidex * side + (-outx * out + sidex * (1 - side)), scale * side, sidez * side + (-outz * out + sidez * (1 - side))),
		        		new Vec3d(sidex * side + (outx * out + sidex * (1 - side)), -scale * side, sidez * side + (outz * out + sidez * (1 - side)))
		        };

		        buffer.pos((double)rx + vertices[0].x, (double)ry + vertices[0].y, (double)rz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		        buffer.pos((double)rx + vertices[1].x, (double)ry + vertices[1].y, (double)rz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		        buffer.pos((double)rx + vertices[2].x, (double)ry + vertices[2].y, (double)rz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		        buffer.pos((double)rx + vertices[3].x, (double)ry + vertices[3].y, (double)rz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();

		        buffer.pos((double)rx + vertices[3].x, (double)ry + vertices[3].y, (double)rz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		        buffer.pos((double)rx + vertices[2].x, (double)ry + vertices[2].y, (double)rz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		        buffer.pos((double)rx + vertices[1].x, (double)ry + vertices[1].y, (double)rz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		        buffer.pos((double)rx + vertices[0].x, (double)ry + vertices[0].y, (double)rz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
			} else {
				super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
			}
		}
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		if(this.progress > 1.0F) {
			this.setExpired();
		}

		this.progress += 0.01F;

		this.updateDrag();
		this.updatePosition();

		this.firstUpdate = false;
	}

	protected void updateDrag() {
		double tmx = this.targetMotionX;
		double tmy = this.targetMotionY;
		double my = tmy * VELOCITY_OFFSET_MULTIPLIER;
		double tmz = this.targetMotionZ;

		float dragIncrement = 0.1F;

		if(this.dragX > tmx * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragX -= dragIncrement;
		} else if(this.dragX < tmx * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragX += dragIncrement;
		}
		if(Math.abs(this.dragX - tmx * VELOCITY_OFFSET_MULTIPLIER) <= dragIncrement) {
			this.dragX = tmx * VELOCITY_OFFSET_MULTIPLIER;
		}
		if(this.dragY > my) {
			this.dragY -= dragIncrement;
		} else if(this.dragY < my) {
			this.dragY += dragIncrement;
		}
		if(Math.abs(this.dragY - my) <= dragIncrement) {
			this.dragY = my;
		}
		if(this.dragZ > tmz * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragZ -= dragIncrement;
		} else if(this.dragZ < tmz * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragZ += dragIncrement;
		}
		if(Math.abs(this.dragZ - tmz * VELOCITY_OFFSET_MULTIPLIER) <= dragIncrement) {
			this.dragZ = tmz * VELOCITY_OFFSET_MULTIPLIER;
		}

		this.dragX = MathHelper.clamp(this.dragX, -1, 1);
		this.dragY = MathHelper.clamp(this.dragY, -0.3D, 1);
		this.dragZ = MathHelper.clamp(this.dragZ, -1, 1);
	}

	protected void updatePosition() {
		double sx = this.targetX + this.offsetX - this.dragX;
		double sy = this.targetY + this.offsetY - this.dragY;
		double sz = this.targetZ + this.offsetZ - this.dragZ;

		double dx = this.targetX - sx;
		double dy = this.targetY - sy;
		double dz = this.targetZ - sz;

		this.prevRotSinX = this.rotSinX;
		this.prevRotCosZ = this.rotCosZ;
		this.rotSinX = Math.sin(this.startRotation + this.progress * this.rotationSpeed * Math.PI * 2.0F);
		this.rotCosZ = Math.cos(this.startRotation + this.progress * this.rotationSpeed * Math.PI * 2.0F);
		
		this.posX = sx + dx * (1 - Math.pow(1 - this.progress, 3)) + this.rotSinX * this.progress * this.endRadius;
		this.posY = sy + dy * this.progress;
		this.posZ = sz + dz * (1 - Math.pow(1 - this.progress, 3)) + this.rotCosZ * this.progress * this.endRadius;
	}
}
