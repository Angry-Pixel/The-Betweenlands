package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class SwarmParticle extends TextureSheetParticle {

	protected Direction face;

	protected float rotateBias;

	protected Vec3 start;
	protected Supplier<Vec3> end;

	protected int lightmapX, lightmapY;

	protected SwarmParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, Direction face, float scale, int maxAge, Vec3 start, Supplier<Vec3> end) {
		super(level, x, y, z, 0, 0, 0);
		this.xd = mx;
		this.yd = my;
		this.zd = mz;
		this.x = this.xo = x;
		this.y = this.yo = y;
		this.z = this.zo = z;
		this.face = face;
		this.hasPhysics = false;
		this.start = start;
		this.end = end;
		this.alpha = 0;
		this.lifetime = maxAge;
		this.scale(scale);
	}

	@Override
	public void tick() {
		super.tick();

		int brightness = this.getLightColor(1);
		this.lightmapX = (brightness >> 16) & 65535;
		this.lightmapY = brightness & 65535;

		if(this.onGround) {
			this.xd /= 0.699999988079071D;
			this.zd /= 0.699999988079071D;
		}


		double speed = Mth.sqrt((float) (this.xd * this.xd + this.yd * this.yd + this.zd * this.zd));

		Vec3 dir = this.end.get().subtract(this.start);
		if(dir.lengthSqr() > 0.1f) {
			dir = dir.normalize();
		} else {
			dir = Vec3.ZERO;
		}
		Vec3 normal = Vec3.atCenterOf(this.face.getNormal());
		Vec3 motion = new Vec3(this.xd, this.yd, this.zd);
		Vec3 side = motion.normalize().cross(normal);

		if(this.random.nextInt(20) == 0) {
			this.rotateBias = (this.random.nextFloat() - 0.5f) * 0.5f;
		}

		Vec3 newMotion = motion.add(side.scale(speed * ((this.random.nextFloat() - 0.5f) * 0.5f + this.rotateBias))).add(dir.scale(speed * this.random.nextFloat() * 0.85f)).normalize().scale(speed);
		this.xd = newMotion.x;
		this.yd = newMotion.y;
		this.zd = newMotion.z;

		double dirX = this.xd / speed;
		double dirY = this.yd / speed;
		double dirZ = this.zd / speed;

		double ahead = this.quadSize * 0.2f * 0.125f * 8;

		BlockPos pos = BlockPos.containing(this.x + dirX * ahead - this.face.getStepX() * 0.1f, this.y + dirY * ahead - this.face.getStepY() * 0.1f, this.z + dirZ * ahead - this.face.getStepZ() * 0.1f);

		BlockState state = this.level.getBlockState(pos);

		if(!Block.isFaceFull(state.getCollisionShape(this.level, pos), this.face)) {
			this.age = this.lifetime;
		}

		Vec3 perpendicular = switch (this.face) {
			case UP -> new Vec3(1, 0, 0);
			case DOWN -> new Vec3(-1, 0, 0);
			default -> new Vec3(0, 1, 0);
		};
		Vec3 perpendicular2 = perpendicular.cross(normal);

		double y = perpendicular.dot(motion);
		double x = perpendicular2.dot(motion);

		this.oRoll = this.roll;
		this.roll = (float) Mth.atan2(y, x) + Mth.PI * 0.5f;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
		float rpx = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camera.getPosition().x());
		float rpy = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camera.getPosition().y());
		float rpz = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camera.getPosition().z());

		Vec3i normal = this.face.getNormal();

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

		float v1x = (pp1x - pp2x + pp1x * yOffset);
		float v1y = (pp1y - pp2y + pp1y * yOffset);
		float v1z = (pp1z - pp2z + pp1z * yOffset);
		float v2x = (-pp1x - pp2x + pp1x * yOffset);
		float v2y = (-pp1y - pp2y + pp1y * yOffset);
		float v2z = (-pp1z - pp2z + pp1z * yOffset);
		float v3x = (-pp1x + pp2x + pp1x * yOffset);
		float v3y = (-pp1y + pp2y + pp1y * yOffset);
		float v3z = (-pp1z + pp2z + pp1z * yOffset);
		float v4x = (pp1x + pp2x + pp1x * yOffset);
		float v4y = (pp1y + pp2y + pp1y * yOffset);
		float v4z = (pp1z + pp2z + pp1z * yOffset);

		if(this.roll != 0.0F) {
			float angle = this.roll + (this.roll - this.oRoll) * partialTicks;
			float cos = Mth.cos(angle * 0.5F);
			float rdx = Mth.sin(angle * 0.5F) * this.face.getStepX();
			float rdy = Mth.sin(angle * 0.5F) * this.face.getStepY();
			float rdz = Mth.sin(angle * 0.5F) * this.face.getStepZ();

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
		if(this.age >= this.lifetime - 5) {
			alpha = this.alpha * (this.lifetime - this.age) / 5.0f;
		} else if(this.age <= 5) {
			alpha = this.alpha * this.age / 5.0f;
		} else {
			alpha = this.alpha;
		}

		consumer.addVertex(rpx + v1x, rpy + v1y, rpz + v1z).setUv(this.getU1(), this.getV1()).setColor(this.rCol, this.gCol, this.bCol, alpha).setUv2(this.lightmapX, this.lightmapY);
		consumer.addVertex(rpx + v2x, rpy + v2y, rpz + v2z).setUv(this.getU1(), this.getV0()).setColor(this.rCol, this.gCol, this.bCol, alpha).setUv2(this.lightmapX, this.lightmapY);
		consumer.addVertex(rpx + v3x, rpy + v3y, rpz + v3z).setUv(this.getU0(), this.getV0()).setColor(this.rCol, this.gCol, this.bCol, alpha).setUv2(this.lightmapX, this.lightmapY);
		consumer.addVertex(rpx + v4x, rpy + v4y, rpz + v4z).setUv(this.getU0(), this.getV1()).setColor(this.rCol, this.gCol, this.bCol, alpha).setUv2(this.lightmapX, this.lightmapY);
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

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public SwarmParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new SwarmParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Direction.class, 0), args.scale, args.data.getInt(1), args.data.getObject(Vec3.class, 2), args.data.getObject(Supplier.class, 3));
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Direction.UP, 40, Vec3.ZERO, (Supplier<Vec3>) () -> Vec3.ZERO);
		}
	}
}
