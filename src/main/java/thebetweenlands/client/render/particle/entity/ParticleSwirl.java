package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleSwirl extends Particle {
	protected float progress = 0;
	protected float startRotation;
	protected float endRadius;
	protected double dragX, dragY, dragZ;

	protected double offsetX, offsetY, offsetZ;
	protected double targetX, targetY, targetZ;
	protected double targetMotionX, targetMotionY, targetMotionZ;

	private boolean firstUpdate = true;

	private static final float VELOCITY_OFFSET_MULTIPLIER = 4.0F;

	public ParticleSwirl(World world, double x, double y, double z, int maxAge, float scale, float progress) {
		super(world, x, y, z, 0, 0, 0);
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.progress = progress;

		this.startRotation = (float) (this.rand.nextFloat() * Math.PI * 2.0F);
		this.endRadius = 0.35F + this.rand.nextFloat() * 0.35F;

		this.particleMaxAge = maxAge;
		
		this.particleScale = scale;
	}

	public void setOffset(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}
	
	public void setTarget(double x, double y, double z) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;

		if(this.firstUpdate) {
			this.updatePosition();
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
		}
	}

	public void setTargetMotion(double x, double y, double z) {
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
	}

	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.particleAge > 2) {
			super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
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

		this.posX = sx + dx * (1 - Math.pow(1 - this.progress, 3)) + Math.sin(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;
		this.posY = sy + dy * this.progress;
		this.posZ = sz + dz * (1 - Math.pow(1 - this.progress, 3)) + Math.cos(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;
	}
}
