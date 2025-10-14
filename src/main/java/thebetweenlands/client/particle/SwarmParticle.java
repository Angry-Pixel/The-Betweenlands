package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

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
		Quaternionf quaternionf = new Quaternionf();
		quaternionf.set(this.face.getRotation());
		quaternionf.rotateX(-Mth.HALF_PI);
		if (this.roll != 0.0F) {
			quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll) + (this.face.getAxis() == Direction.Axis.Y ? Mth.HALF_PI : Mth.PI));
		}

		this.renderRotatedQuad(consumer, camera, quaternionf, partialTicks);
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
		@SuppressWarnings("unchecked")
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
