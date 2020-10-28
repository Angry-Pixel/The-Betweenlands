package thebetweenlands.client.render.particle.entity;

import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleSwarm extends ParticleAnimated implements IParticleSpriteReceiver {
	protected EnumFacing face;

	protected float rotateBias;

	protected Vec3d start;
	protected Supplier<Vec3d> end;

	protected ParticleSwarm(World world, double x, double y, double z, double mx, double my, double mz, EnumFacing face, float scale, int maxAge, Vec3d start, Supplier<Vec3d> end) {
		super(world, x, y, z, 0, 0, 0, maxAge, scale, false);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.face = face;
		this.canCollide = false;
		this.start = start;
		this.end = end;
		this.particleAlpha = 0;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			int variant = this.rand.nextInt(frames.length);
			
			this.animation.setFrames(frames[variant]);
			
			ResourceLocation location = frames[variant][0].getLocation();
			if(location instanceof ResourceLocationWithScale) {
				this.particleScale *= ((ResourceLocationWithScale) location).scale;
			}
			
			if(this.particleMaxAge < 0) {
				this.particleMaxAge = this.animation.getTotalDuration() - 1;
			}
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[variant][0].getSprite());
			}
		}
	}
	
	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.onGround) {
			this.motionX /= 0.699999988079071D;
			this.motionZ /= 0.699999988079071D;
		}


		double speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

		Vec3d dir = this.end.get().subtract(this.start);
		if(dir.lengthSquared() > 0.1f) {
			dir = dir.normalize();
		} else {
			dir = Vec3d.ZERO;
		}
		Vec3d normal = new Vec3d(this.face.getDirectionVec());
		Vec3d motion = new Vec3d(this.motionX, this.motionY, this.motionZ);
		Vec3d side = motion.normalize().crossProduct(normal);

		if(this.rand.nextInt(20) == 0) {
			this.rotateBias = ((float)this.rand.nextFloat() - 0.5f) * 0.5f;
		}

		Vec3d newMotion = motion.add(side.scale(speed * ((this.rand.nextFloat() - 0.5f) * 0.5f + this.rotateBias))).add(dir.scale(speed * this.rand.nextFloat() * 0.85f)).normalize().scale(speed);
		this.motionX = newMotion.x;
		this.motionY = newMotion.y;
		this.motionZ = newMotion.z;

		double dirX = this.motionX / speed;
		double dirY = this.motionY / speed;
		double dirZ = this.motionZ / speed;

		double ahead = this.particleScale * 0.2f * 0.125f * 8;

		BlockPos pos = new BlockPos(this.posX + dirX * ahead - this.face.getXOffset() * 0.1f, this.posY + dirY * ahead - this.face.getYOffset() * 0.1f, this.posZ + dirZ * ahead - this.face.getZOffset() * 0.1f);

		IBlockState state = this.world.getBlockState(pos);

		if(!state.isFullCube()) {
			this.particleAge = this.particleMaxAge;
		}

		Vec3d perpendicular;
		switch(this.face) {
		case UP:
			perpendicular = new Vec3d(1, 0, 0);
			break;
		case DOWN:
			perpendicular = new Vec3d(-1, 0, 0);
			break;
		default:
			perpendicular = new Vec3d(0, 1, 0);
		}
		Vec3d perpendicular2 = perpendicular.crossProduct(normal);

		double y = perpendicular.dotProduct(motion);
		double x = perpendicular2.dotProduct(motion);

		this.prevParticleAngle = this.particleAngle;
		this.particleAngle = (float) MathHelper.atan2(y, x) + (float)Math.PI * 0.5f;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0.1F * this.particleScale * 2;

		if (this.particleTexture != null) {
			minU = this.particleTexture.getMinU();
			maxU = this.particleTexture.getMaxU();
			minV = this.particleTexture.getMinV();
			maxV = this.particleTexture.getMaxV();
		}

		float rpx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;

		Vec3d normal = new Vec3d(this.face.getDirectionVec());
		Vec3d perpendicular;
		switch(this.face) {
		case UP:
			perpendicular = new Vec3d(1, 0, 0);
			break;
		case DOWN:
			perpendicular = new Vec3d(-1, 0, 0);
			break;
		default:
			perpendicular = new Vec3d(0, 1, 0);
		}
		Vec3d perpendicular2 = perpendicular.crossProduct(normal);

		double yOffset = 0.125D;
		Vec3d[] vertices = new Vec3d[] {perpendicular.add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale)};

		if (this.particleAngle != 0.0F) {
			float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * this.face.getXOffset();
			float f11 = MathHelper.sin(f8 * 0.5F) * this.face.getYOffset();
			float f12 = MathHelper.sin(f8 * 0.5F) * this.face.getZOffset();
			Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l) {
				vertices[l] = vec3d.scale(2.0D * vertices[l].dotProduct(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vertices[l]).scale((double)(2.0F * f9)));
			}
		}

		float alpha;
		if(this.particleAge >= this.particleMaxAge - 5) {
			alpha = this.particleAlpha * (this.particleMaxAge - this.particleAge) / 5.0f;
		} else if(this.particleAge <= 5) {
			alpha = this.particleAlpha * this.particleAge / 5.0f;
		} else {
			alpha = 1;
		}

		buff.pos((double)rpx + vertices[0].x, (double)rpy + vertices[0].y, (double)rpz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[1].x, (double)rpy + vertices[1].y, (double)rpz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].x, (double)rpy + vertices[2].y, (double)rpz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].x, (double)rpy + vertices[3].y, (double)rpz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	public static class ResourceLocationWithScale extends ResourceLocation {
		public final float scale;
		
		public ResourceLocationWithScale(String resourceName, float scale) {
			super(resourceName);
			this.scale = scale;
		}
	}
	
	public static final ParticleTextureStitcher<ParticleSwarm> SPRITES = ParticleTextureStitcher.create(ParticleSwarm.class,
			new ResourceLocationWithScale("thebetweenlands:particle/swarm_1", 1), new ResourceLocationWithScale("thebetweenlands:particle/swarm_2", 1), new ResourceLocationWithScale("thebetweenlands:particle/swarm_3", 1), new ResourceLocationWithScale("thebetweenlands:particle/swarm_4", 2)
			).setSplitAnimations(true);

	public static final class Factory extends ParticleFactory<Factory, ParticleSwarm> {
		public Factory() {
			super(ParticleSwarm.class, SPRITES);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ParticleSwarm createParticle(ImmutableParticleArgs args) {
			return new ParticleSwarm(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(EnumFacing.class, 0), args.scale, args.data.getInt(1), args.data.getObject(Vec3d.class, 2), args.data.getObject(Supplier.class, 3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(EnumFacing.UP, 40, Vec3d.ZERO, (Supplier<Vec3d>) () -> Vec3d.ZERO);
		}
	}
}

