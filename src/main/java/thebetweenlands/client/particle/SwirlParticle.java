package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import thebetweenlands.client.particle.options.SwirlParticleOptions;

public class SwirlParticle extends TextureSheetParticle {

	protected float progress;
	protected final float startRotation;
	protected final float endRadius;
	protected Vec3 drag = Vec3.ZERO;

	protected Vec3 offset;
	protected Vec3 target;
	protected Vec3 targetMotion = Vec3.ZERO;

	protected boolean rotate3D = false;

	private static final float VELOCITY_OFFSET_MULTIPLIER = 4.0F;

	protected double prevRotSinX;
	protected double prevRotCosZ;
	protected double rotSinX;
	protected double rotCosZ;

	protected double rotationSpeed = 4.0D;

	public SwirlParticle(SwirlParticleOptions options, ClientLevel level, double x, double y, double z, int maxAge, float scale, float progress) {
		super(level, x, y, z, 0, 0, 0);
		this.xd = this.yd = this.zd = 0.0D;
		this.progress = progress;

		this.startRotation = (float) (level.random.nextFloat() * Math.PI * 2.0F);
		this.endRadius = 0.35F + level.random.nextFloat() * 0.35F;
		this.lifetime = maxAge;
		this.quadSize = scale * 0.1F;
		this.offset = options.offset;
		this.target = options.target;
		if (this.target != Vec3.ZERO) {
			this.updatePosition();
			this.xo = this.x;
			this.yo = this.y;
			this.zo = this.z;
		}

		this.targetMotion = options.targetMotion;
		if (this.targetMotion != Vec3.ZERO) {
			double tmx = this.targetMotion.x();
			double tmy = this.targetMotion.y();
			double my = tmy * VELOCITY_OFFSET_MULTIPLIER;
			double tmz = this.targetMotion.z();

			this.drag = new Vec3(Mth.clamp(tmx * VELOCITY_OFFSET_MULTIPLIER, -1, 1), Mth.clamp(my, -0.3D, 1), Mth.clamp(tmz * VELOCITY_OFFSET_MULTIPLIER, -1, 1));
		}
		this.rotationSpeed = options.rotationSpeed;
		this.rotate3D = options.rotate3D;
	}

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		if (this.age > 2) {
			if (this.rotate3D) {
				float irx = (float) (this.prevRotSinX + (this.rotSinX - this.prevRotSinX) * (double) partialTicks);
				float irz = (float) (this.prevRotCosZ + (this.rotCosZ - this.prevRotCosZ) * (double) partialTicks);

				float tilt = -30.0F;

				float h = (float) Math.cos(tilt / 360.0f * 2.0F * Mth.PI);
				float v = (float) Math.sin(tilt / 360.0f * 2.0F * Mth.PI);

				float sidex = -irz * this.quadSize;
				float sidez = irx * this.quadSize;
				float outx = irx * this.quadSize;
				float outz = irz * this.quadSize;

				Vector3f[] vertices = new Vector3f[] {
					new Vector3f(-sidex * h + (outx * v - sidex * (1 - h)), -this.quadSize * h, -sidez * h + (outz * v - sidez * (1 - h))),
					new Vector3f(-sidex * h + (-outx * v - sidex * (1 - h)), this.quadSize * h, -sidez * h + (-outz * v - sidez * (1 - h))),
					new Vector3f(sidex * h + (-outx * v + sidex * (1 - h)), this.quadSize * h, sidez * h + (-outz * v + sidez * (1 - h))),
					new Vector3f(sidex * h + (outx * v + sidex * (1 - h)), -this.quadSize * h, sidez * h + (outz * v + sidez * (1 - h)))
				};

				float rx = (float)(Mth.lerp(partialTicks, this.xo, this.x) - renderInfo.getPosition().x());
				float ry = (float)(Mth.lerp(partialTicks, this.yo, this.y) - renderInfo.getPosition().y());
				float rz = (float)(Mth.lerp(partialTicks, this.zo, this.z) - renderInfo.getPosition().z());
				float minU = this.getU0();
				float maxU = this.getU1();
				float minV = this.getV0();
				float maxV = this.getV1();
				int light = this.getLightColor(partialTicks);

				buffer.addVertex(rx + vertices[0].x, ry + vertices[0].y, rz + vertices[0].z).setUv(maxU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
				buffer.addVertex(rx + vertices[1].x, ry + vertices[1].y, rz + vertices[1].z).setUv(maxU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
				buffer.addVertex(rx + vertices[2].x, ry + vertices[2].y, rz + vertices[2].z).setUv(minU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
				buffer.addVertex(rx + vertices[3].x, ry + vertices[3].y, rz + vertices[3].z).setUv(minU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);

				buffer.addVertex(rx + vertices[3].x, ry + vertices[3].y, rz + vertices[3].z).setUv(minU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
				buffer.addVertex(rx + vertices[2].x, ry + vertices[2].y, rz + vertices[2].z).setUv(minU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
				buffer.addVertex(rx + vertices[1].x, ry + vertices[1].y, rz + vertices[1].z).setUv(maxU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
				buffer.addVertex(rx + vertices[0].x, ry + vertices[0].y, rz + vertices[0].z).setUv(maxU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
			} else {
				super.render(buffer, renderInfo, partialTicks);
			}
		}
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		if (this.progress > 1.0F) {
			this.remove();
		}

		this.progress += 0.01F;

		this.updateDrag();
		this.updatePosition();
	}

	protected void updateDrag() {
		double tmx = this.targetMotion.x();
		double tmy = this.targetMotion.y();
		double my = tmy * VELOCITY_OFFSET_MULTIPLIER;
		double tmz = this.targetMotion.z();

		double dragX = this.drag.x();
		double dragY = this.drag.y();
		double dragZ = this.drag.z();

		float dragIncrement = 0.1F;

		if (dragX > tmx * VELOCITY_OFFSET_MULTIPLIER) {
			dragX -= dragIncrement;
		} else if (dragX < tmx * VELOCITY_OFFSET_MULTIPLIER) {
			dragX += dragIncrement;
		}
		if (Math.abs(dragX - tmx * VELOCITY_OFFSET_MULTIPLIER) <= dragIncrement) {
			dragX = tmx * VELOCITY_OFFSET_MULTIPLIER;
		}
		if (dragY > my) {
			dragY -= dragIncrement;
		} else if (dragY < my) {
			dragY += dragIncrement;
		}
		if (Math.abs(dragY - my) <= dragIncrement) {
			dragY = my;
		}
		if (dragZ > tmz * VELOCITY_OFFSET_MULTIPLIER) {
			dragZ -= dragIncrement;
		} else if (dragZ < tmz * VELOCITY_OFFSET_MULTIPLIER) {
			dragZ += dragIncrement;
		}
		if (Math.abs(dragZ - tmz * VELOCITY_OFFSET_MULTIPLIER) <= dragIncrement) {
			dragZ = tmz * VELOCITY_OFFSET_MULTIPLIER;
		}

		this.drag = new Vec3(Mth.clamp(dragX, -1, 1), Mth.clamp(dragY, -0.3D, 1), Mth.clamp(dragZ, -1, 1));
	}

	protected void updatePosition() {
		double sx = this.target.x() + this.offset.x() - this.drag.x();
		double sy = this.target.x() + this.offset.y() - this.drag.y();
		double sz = this.target.z() + this.offset.z() - this.drag.z();

		double dx = this.target.x() - sx;
		double dy = this.target.y() - sy;
		double dz = this.target.z() - sz;

		this.prevRotSinX = this.rotSinX;
		this.prevRotCosZ = this.rotCosZ;
		this.rotSinX = Math.sin(this.startRotation + this.progress * this.rotationSpeed * Math.PI * 2.0F);
		this.rotCosZ = Math.cos(this.startRotation + this.progress * this.rotationSpeed * Math.PI * 2.0F);

		this.x = sx + dx * (1 - Math.pow(1 - this.progress, 3)) + this.rotSinX * this.progress * this.endRadius;
//		this.y = sy + dy * this.progress;
		this.z = sz + dz * (1 - Math.pow(1 - this.progress, 3)) + this.rotCosZ * this.progress * this.endRadius;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
}
