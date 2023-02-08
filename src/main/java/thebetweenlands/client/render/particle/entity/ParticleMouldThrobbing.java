package thebetweenlands.client.render.particle.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.common.registries.BlockRegistry;

public class ParticleMouldThrobbing extends ParticleAnimated implements IParticleSpriteReceiver {

	private static class Wave {
		private final World world;
		private final Set<BlockPos> positions = new HashSet<>();
		private final int range;

		private int remainingReemits;

		public Wave(World world, int range, int reemits) {
			this.world = world;
			this.range = range;
			this.remainingReemits = reemits;
		}
	}

	private final BlockPos pos;
	private final Wave wave;

	private int remainingRange;

	private long originStartTime;
	private BlockPos origin;
	private BlockPos source;

	private List<EnumFacing> spreadingDirs = new ArrayList<>();

	private long waveAgeOffset;

	protected ParticleMouldThrobbing(World world, BlockPos pos, int maxAge, int remainingRange, Wave wave) { 
		super(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 0, 0, 0, maxAge, 1.0f, false);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.posX = this.prevPosX = pos.getX() + 0.5f;
		this.posY = this.prevPosY = pos.getY() + 0.5f;
		this.posZ = this.prevPosZ = pos.getZ() + 0.5f;
		this.pos = pos;
		this.originStartTime = world.getTotalWorldTime();
		this.origin = pos;
		this.source = pos;
		this.wave = wave;
		this.remainingRange = remainingRange;

		wave.positions.add(this.pos);

		for(EnumFacing dir : EnumFacing.VALUES) {
			this.spreadingDirs.add(dir);
		}
	}

	protected ParticleMouldThrobbing(World world, BlockPos pos, int maxAge, int remainingRange, int remainingReemits) {
		this(world, pos, maxAge, remainingRange, new Wave(world, remainingRange, remainingReemits));
	}

	protected ParticleMouldThrobbing(World world, BlockPos pos, ParticleMouldThrobbing source) {
		this(world, pos, source.particleMaxAge, source.remainingRange - 1, source.wave);
		this.originStartTime = source.originStartTime;
		this.origin = source.origin;
		this.source = source.pos;
	}

	protected ParticleMouldThrobbing(World world, BlockPos pos, ParticleMouldThrobbing source, int remainingRange) {
		this(world, pos, source.particleMaxAge, remainingRange, source.wave);
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		BlockPos blockpos = this.pos.up();
		return this.world.isBlockLoaded(blockpos) ? this.world.getCombinedLight(blockpos, 0) : 0;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		if(!this.hasMouldBelow(this.pos)) {
			return;
		}

		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0.5f;

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

		int timeToFullAlpha = this.particleMaxAge / 2;
		float alpha = this.particleAge + partialTicks;
		if(alpha < timeToFullAlpha) {
			alpha = alpha / timeToFullAlpha;
		} else if(alpha > timeToFullAlpha && alpha < this.particleMaxAge - timeToFullAlpha) {
			alpha = 1.0f;
		} else {
			alpha = Math.max(0, (this.particleMaxAge - alpha) / timeToFullAlpha);
		}
		
		double yOffset = Math.min(0.1D, Math.sqrt(rpx * rpx + rpz * rpz) * 0.0001D);
		
		double p1x = 0.5D;
		double p1z = 0.5D;
		double p2x = 0.5D;
		double p2z = -0.5D;
		double p3x = -0.5D;
		double p3z = -0.5D;
		double p4x = -0.5D;
		double p4z = 0.5D;
		
		buff.pos((double)rpx + p1x, (double)rpy + 0.5D + yOffset, (double)rpz + p1z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + p2x, (double)rpy + 0.5D + yOffset, (double)rpz + p2z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + p3x, (double)rpy + 0.5D + yOffset, (double)rpz + p3z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + p4x, (double)rpy + 0.5D + yOffset, (double)rpz + p4z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * alpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.animation.update();
		this.setParticleTexture(this.animation.getCurrentSprite());

		if(this.particleAge++ >= this.particleMaxAge || !this.hasMouldBelow(this.pos)) {
			this.setExpired();
		}

		int ticksPerBlock = this.particleMaxAge / 2;

		if(this.remainingRange > 0 && this.wave.positions.size() < 128) {
			Iterator<EnumFacing> dirIT = this.spreadingDirs.iterator();

			while(dirIT.hasNext()) {
				EnumFacing dir = dirIT.next();

				BlockPos offsetPos = this.pos.offset(dir);

				double dst = this.origin.getDistance(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

				if(dst * 2 < this.wave.range && dst * dst > this.pos.distanceSq(this.origin)) {
					if(this.world.getTotalWorldTime() - this.originStartTime > dst * ticksPerBlock) {
						dirIT.remove();

						if(!this.wave.positions.contains(offsetPos) && this.hasMouldBelow(offsetPos)) {
							BLParticles.MOULD_THROBBING_SPREAD.spawn(this.world, offsetPos.getX() + 0.5D, offsetPos.getY() + 0.5D, offsetPos.getZ() + 0.5D, ParticleArgs.get().withData(this));
						}

						IBlockState state = this.world.getBlockState(offsetPos.up());
						if(dst * 4 > this.wave.range && state.getBlock() == BlockRegistry.MOULD_HORN && this.world.rand.nextInt(3) == 0 && this.wave.remainingReemits-- > 0) {
							BLParticles.MOULD_THROBBING_REEMIT.spawn(this.world, offsetPos.getX() + 0.5D, offsetPos.getY() + 0.5D, offsetPos.getZ() + 0.5D, ParticleArgs.get().withData(this, this.wave.range));
						}
					}
				} else {
					dirIT.remove();
				}
			}
		}

		if(this.spreadingDirs.isEmpty() || this.isExpired) {
			this.wave.positions.remove(this.pos);
		}
	}

	protected boolean hasMouldBelow(BlockPos pos) {
		return this.world.getBlockState(pos).getBlock() == BlockRegistry.MOULDY_SOIL;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleMouldThrobbing> {
		public Factory() {
			super(ParticleMouldThrobbing.class, ParticleTextureStitcher.create(ParticleMouldThrobbing.class, new ResourceLocation("thebetweenlands:particle/mouldy_soil_throbbing")).setSplitAnimations(true));
		}

		@Override
		public ParticleMouldThrobbing createParticle(ImmutableParticleArgs args) {
			return new ParticleMouldThrobbing(args.world, new BlockPos(args.x, args.y, args.z), args.data.getInt(0), args.data.getInt(1), args.data.getInt(2));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(10, 16, 3);
		}
	}

	public static final class SpreadingFactory extends ParticleFactory<Factory, ParticleMouldThrobbing> {
		public SpreadingFactory() {
			super(ParticleMouldThrobbing.class, ParticleTextureStitcher.create(ParticleMouldThrobbing.class, new ResourceLocation("thebetweenlands:particle/mouldy_soil_throbbing")).setSplitAnimations(true));
		}

		@Override
		public ParticleMouldThrobbing createParticle(ImmutableParticleArgs args) {
			return new ParticleMouldThrobbing(args.world, new BlockPos(args.x, args.y, args.z), args.data.getObject(ParticleMouldThrobbing.class, 0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(null);
		}
	}

	public static final class ReemitFactory extends ParticleFactory<Factory, ParticleMouldThrobbing> {
		public ReemitFactory() {
			super(ParticleMouldThrobbing.class, ParticleTextureStitcher.create(ParticleMouldThrobbing.class, new ResourceLocation("thebetweenlands:particle/mouldy_soil_throbbing")).setSplitAnimations(true));
		}

		@Override
		public ParticleMouldThrobbing createParticle(ImmutableParticleArgs args) {
			return new ParticleMouldThrobbing(args.world, new BlockPos(args.x, args.y, args.z), args.data.getObject(ParticleMouldThrobbing.class, 0), args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(null, 3);
		}
	}
}

