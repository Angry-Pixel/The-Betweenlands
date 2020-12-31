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
import net.minecraft.util.math.Vec3i;
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

	protected int lightmapX, lightmapY;

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

		int brightness = this.getBrightnessForRender(1);
		this.lightmapX = (brightness >> 16) & 65535;
		this.lightmapY = brightness & 65535;

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

		if(this.particleTexture != null) {
			minU = this.particleTexture.getMinU();
			maxU = this.particleTexture.getMaxU();
			minV = this.particleTexture.getMinV();
			maxV = this.particleTexture.getMaxV();
		}

		float rpx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

		Vec3i normal = this.face.getDirectionVec();

		float pp1x = 0, pp1y = 0, pp1z = 0;
		switch(this.face) {
		case UP:
			pp1x = 1;
			break;
		case DOWN:
			pp1x = -1;
			break;
		default:
			pp1y = 1;
			break;
		}

		float pp2x = crossX(pp1x, pp1y, pp1z, (float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
		float pp2y = crossY(pp1x, pp1y, pp1z, (float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
		float pp2z = crossZ(pp1x, pp1y, pp1z, (float) normal.getX(), (float) normal.getY(), (float) normal.getZ());

		float yOffset = 0.125f;

		float v1x = (pp1x - pp2x + pp1x * yOffset) * scale;
		float v1y = (pp1y - pp2y + pp1y * yOffset) * scale;
		float v1z = (pp1z - pp2z + pp1z * yOffset) * scale;
		float v2x = (-pp1x - pp2x + pp1x * yOffset) * scale;
		float v2y = (-pp1y - pp2y + pp1y * yOffset) * scale;
		float v2z = (-pp1z - pp2z + pp1z * yOffset) * scale;
		float v3x = (-pp1x + pp2x + pp1x * yOffset) * scale;
		float v3y = (-pp1y + pp2y + pp1y * yOffset) * scale;
		float v3z = (-pp1z + pp2z + pp1z * yOffset) * scale;
		float v4x = (pp1x + pp2x + pp1x * yOffset) * scale;
		float v4y = (pp1y + pp2y + pp1y * yOffset) * scale;
		float v4z = (pp1z + pp2z + pp1z * yOffset) * scale;

		if(this.particleAngle != 0.0F) {
			float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float cos = MathHelper.cos(angle * 0.5F);
			float rdx = MathHelper.sin(angle * 0.5F) * this.face.getXOffset();
			float rdy = MathHelper.sin(angle * 0.5F) * this.face.getYOffset();
			float rdz = MathHelper.sin(angle * 0.5F) * this.face.getZOffset();

			float dotrdrd = cos * cos - dot(rdx, rdy, rdz, rdx, rdy, rdz);

			float dotvrd = 2 * dot(v1x, v1y, v1z, rdx, rdy, rdz);
			float nx = rdx * dotvrd + v1x * dotrdrd + crossX(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			float ny = rdy * dotvrd + v1y * dotrdrd + crossY(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			float nz = rdz * dotvrd + v1z * dotrdrd + crossZ(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			v1x = nx;
			v1y = ny;
			v1z = nz;

			dotvrd = 2 * dot(v2x, v2y, v2z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v2x * dotrdrd + crossX(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			ny = rdy * dotvrd + v2y * dotrdrd + crossY(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			nz = rdz * dotvrd + v2z * dotrdrd + crossZ(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			v2x = nx;
			v2y = ny;
			v2z = nz;

			dotvrd = 2 * dot(v3x, v3y, v3z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v3x * dotrdrd + crossX(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			ny = rdy * dotvrd + v3y * dotrdrd + crossY(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			nz = rdz * dotvrd + v3z * dotrdrd + crossZ(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			v3x = nx;
			v3y = ny;
			v3z = nz;

			dotvrd = 2 * dot(v4x, v4y, v4z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v4x * dotrdrd + crossX(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			ny = rdy * dotvrd + v4y * dotrdrd + crossY(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			nz = rdz * dotvrd + v4z * dotrdrd + crossZ(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			v4x = nx;
			v4y = ny;
			v4z = nz;
		}

		float alpha;
		if(this.particleAge >= this.particleMaxAge - 5) {
			alpha = this.particleAlpha * (this.particleMaxAge - this.particleAge) / 5.0f;
		} else if(this.particleAge <= 5) {
			alpha = this.particleAlpha * this.particleAge / 5.0f;
		} else {
			alpha = this.particleAlpha;
		}

		buff.pos((double)rpx + v1x, (double)rpy + v1y, (double)rpz + v1z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(this.lightmapX, this.lightmapY).endVertex();
		buff.pos((double)rpx + v2x, (double)rpy + v2y, (double)rpz + v2z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(this.lightmapX, this.lightmapY).endVertex();
		buff.pos((double)rpx + v3x, (double)rpy + v3y, (double)rpz + v3z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(this.lightmapX, this.lightmapY).endVertex();
		buff.pos((double)rpx + v4x, (double)rpy + v4y, (double)rpz + v4z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(this.lightmapX, this.lightmapY).endVertex();
	}

	private static float crossX(float x1, float y1, float z1, float x2, float y2, float z2) {
		return y1 * z2 - z1 * y2;
	}

	private static float crossY(float x1, float y1, float z1, float x2, float y2, float z2) {
		return z1 * x2 - x1 * z2;
	}

	private static float crossZ(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * y2 - y1 * x2;
	}

	private static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
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

