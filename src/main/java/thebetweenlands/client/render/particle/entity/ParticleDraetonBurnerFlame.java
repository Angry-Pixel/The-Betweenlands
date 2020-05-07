package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ParticleDraetonBurnerFlame extends ParticleFlame {
	private final EntityDraeton draeton;

	private double prevBurnerX, prevBurnerY, prevBurnerZ;

	protected ParticleDraetonBurnerFlame(World worldIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, EntityDraeton draeton) {
		super(worldIn, 0, 0, 0, xSpeedIn, ySpeedIn, zSpeedIn);
		this.draeton = draeton;

		Vec3d burnerPos = this.getBurnerPos();

		this.prevBurnerX = this.prevPosX = this.posX = burnerPos.x;
		this.prevBurnerY = this.prevPosY = this.posY = burnerPos.y;
		this.prevBurnerZ = this.prevPosZ = this.posZ = burnerPos.z;
		this.setPosition(this.posX, this.posY, this.posZ);
		
		this.particleMaxAge = 3;
	}

	protected Vec3d getBurnerPos() {
		return this.draeton.getBalloonPos(1).add(this.draeton.getRotatedBalloonPoint(new Vec3d(0, -0.15D, 0), 1));
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		Vec3d burnerPos = this.getBurnerPos();

		this.posX += burnerPos.x - this.prevBurnerX;
		this.posY += burnerPos.y - this.prevBurnerY;
		this.posZ += burnerPos.z - this.prevBurnerZ;
		
		this.setPosition(this.posX, this.posY, this.posZ);
		
		this.prevBurnerX = burnerPos.x;
		this.prevBurnerY = burnerPos.y;
		this.prevBurnerZ = burnerPos.z;

		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleDraetonBurnerFlame> {
		public Factory() {
			super(ParticleDraetonBurnerFlame.class);
		}

		@Override
		public ParticleDraetonBurnerFlame createParticle(ImmutableParticleArgs args) {
			return new ParticleDraetonBurnerFlame(args.world, args.motionX, args.motionY, args.motionZ, args.data.getObject(EntityDraeton.class, 0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(null);
		}
	}
}
