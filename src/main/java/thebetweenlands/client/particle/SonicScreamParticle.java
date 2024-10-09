package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SonicScreamParticle extends TextureSheetParticle {
	protected final Vec3 dir;
	protected final Vec3 up;

	protected float initialAlpha = 1.0F;

	protected SonicScreamParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, float scale, int maxAge, int initialFrame) {
		super(level, x, y, z, 0, 0, 0);
		this.xd = mx;
		this.yd = my;
		this.zd = mz;
		this.x = this.xo = x;
		this.y = this.yo = y;
		this.z = this.zo = z;
		this.dir = new Vec3(mx, my, mz).normalize();
		this.up = new Vec3(1, 0, 0).cross(this.dir).normalize();
		this.quadSize = scale;
		this.lifetime = maxAge;
	}

	@Override
	public void tick() {
		super.tick();
		this.alpha = this.initialAlpha * (1.0f - this.age / (float)this.lifetime);
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		return super.getQuadSize(scaleFactor);
	}

	//TODO cleanup
	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		float scale = (0.1F * this.quadSize * 2 * (this.age / (float)this.lifetime));

		Vec3 vec3 = renderInfo.getPosition();
		float rpx = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
		float rpy = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
		float rpz = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

		int brightness = this.getLightColor(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;

		Vec3 normal = this.dir;
		Vec3 perpendicular = this.up;
		Vec3 perpendicular2 = perpendicular.cross(normal);

		double yOffset = 0.125D;
		Vec3[] vertices = new Vec3[] {perpendicular.add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale)};

		if (this.roll != 0.0F) {
			float f8 = this.roll + (this.roll - this.oRoll) * partialTicks;
			float f9 = Mth.cos(f8 * 0.5F);
			float f10 = Mth.sin(f8 * 0.5F) * (float)this.dir.x;
			float f11 = Mth.sin(f8 * 0.5F) * (float)this.dir.y;
			float f12 = Mth.sin(f8 * 0.5F) * (float)this.dir.z;
			Vec3 vec3d = new Vec3(f10, f11, f12);

			for (int l = 0; l < 4; ++l) {
				vertices[l] = vec3d.scale(2.0D * vertices[l].dot(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dot(vec3d))).add(vec3d.cross(vertices[l]).scale(2.0F * f9));
			}
		}

		buffer.addVertex((float) (rpx + vertices[0].x), (float) (rpy + vertices[0].y), (float) (rpz + vertices[0].z)).setUv(this.getU1(), this.getV1()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
		buffer.addVertex((float) (rpx + vertices[1].x), (float) (rpy + vertices[1].y), (float) (rpz + vertices[1].z)).setUv(this.getU1(), this.getV0()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
		buffer.addVertex((float) (rpx + vertices[2].x), (float) (rpy + vertices[2].y), (float) (rpz + vertices[2].z)).setUv(this.getU0(), this.getV0()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
		buffer.addVertex((float) (rpx + vertices[3].x), (float) (rpy + vertices[3].y), (float) (rpz + vertices[3].z)).setUv(this.getU0(), this.getV1()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);

		buffer.addVertex((float) (rpx + vertices[1].x), (float) (rpy + vertices[1].y), (float) (rpz + vertices[1].z)).setUv(this.getU1(), this.getV0()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
		buffer.addVertex((float) (rpx + vertices[0].x), (float) (rpy + vertices[0].y), (float) (rpz + vertices[0].z)).setUv(this.getU1(), this.getV1()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
		buffer.addVertex((float) (rpx + vertices[3].x), (float) (rpy + vertices[3].y), (float) (rpz + vertices[3].z)).setUv(this.getU0(), this.getV1()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
		buffer.addVertex((float) (rpx + vertices[2].x), (float) (rpy + vertices[2].y), (float) (rpz + vertices[2].z)).setUv(this.getU0(), this.getV0()).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv2(lightmapX, lightmapY);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public SonicScreamParticle createParticle(SimpleParticleType options, ImmutableParticleArgs args) {
			var particle = new SonicScreamParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0), args.data.getInt(1));
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(-1, 0);
		}
	}
}
