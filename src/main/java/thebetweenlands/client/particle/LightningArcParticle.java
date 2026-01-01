package thebetweenlands.client.particle;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.options.LightningArcParticleOptions;
import thebetweenlands.client.renderer.BLParticleRenderType;
import thebetweenlands.client.renderer.BeamRenderer;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;

public class LightningArcParticle extends Particle {
	private static class Arc {
		@Nullable
		private final Arc parent;
		private final float t;
		private Vec3 from, dir;
		private int splits;
		private final int subdivs;
		private final int depth;
		private final float size;

		private Arc(@Nullable Arc parent, float t, Vec3 from, Vec3 to, int splits, int subdivs, int depth, float size) {
			this.parent = parent;
			this.t = t;
			this.from = from;
			this.dir = to;
			this.splits = splits;
			this.subdivs = subdivs;
			this.depth = depth;
			this.size = size;
		}
	}

	private final Vec3 target;

	private final List<Arc> arcs = new ArrayList<>();

	private final LightningArcParticleOptions options;

	protected LightningArcParticle(LightningArcParticleOptions options, ClientLevel level, double xIn, double yIn, double zIn, double mx, double my, double mz, Vec3 target, int lifetime) {
		super(level, xIn, yIn, zIn);
		this.xd = mx;
		this.yd = my;
		this.zd = mz;
		this.hasPhysics = false;
		this.target = target;
		this.options = options;
		this.lifetime = lifetime;
	}

	@Override
	public AABB getRenderBoundingBox(float partialTicks) {
		return AABB.INFINITE;
	}

	private void addArc(List<Arc> arcs, Arc arc, float offsets, int subdivs) {
		Vec3 startpoint = arc.from;
		Arc prevArc = null;

		for (int i = 0; i < subdivs; i++) {
			float t2 = (i + 1) / (float) subdivs;

			float offsetScale = i == subdivs - 1 ? 0.0f : 1.0f;

			Vec3 endpoint = arc.from.add(arc.dir.scale(t2)).add((this.random.nextFloat() - 0.5f) * offsets * offsetScale, (this.random.nextFloat() - 0.5f) * offsets * offsetScale, (this.random.nextFloat() - 0.5f) * offsets * offsetScale);

			Arc newArc;
			if (prevArc == null) {
				newArc = new Arc(arc.parent, arc.t, startpoint, endpoint.subtract(startpoint), arc.splits, subdivs, arc.depth, arc.size);
			} else {
				newArc = new Arc(prevArc, 1, startpoint, endpoint.subtract(startpoint), arc.splits, subdivs, arc.depth, arc.size);
			}

			arcs.add(newArc);

			prevArc = newArc;
			startpoint = endpoint;
		}
	}

	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
		float rx = (float) (this.xo + (this.x - this.xo) * (double) partialTicks - camera.getPosition().x());
		float ry = (float) (this.yo + (this.y - this.yo) * (double) partialTicks - camera.getPosition().y());
		float rz = (float) (this.zo + (this.z - this.zo) * (double) partialTicks - camera.getPosition().z());

		//int light = this.getLightColor(partialTicks);
		var rot = camera.rotation();

		float scale = ((Mth.sin((this.age + partialTicks) * 0.8f) + 1) * 0.5f * 0.5f + 0.5f) * this.options.baseSize() * (1 - (this.age - 1 + partialTicks) / this.lifetime);
		PoseStack poseStack = new PoseStack();
		for (Arc arc : this.arcs) {
			BeamRenderer.buildBeam(rx + arc.from.x, ry + arc.from.y, rz + arc.from.z, arc.dir, scale * arc.size, 0, scale * 10, rot.x(), rot.z(), rot.y() * rot.z(), rot.x() * rot.y(), rot.x() * rot.z(), (x, y, z, u, v) ->
				buffer.addVertex(poseStack.last(), x, y, z).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F));
		}

		if (this.options.lighting() && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double distFromCam = camera.getEntity().distanceToSqr(this.target.x, this.target.y, this.target.z);
			if (distFromCam < 40) {
				ShaderHelper.INSTANCE.require();

				float strength = (1 - this.age / (float) this.lifetime) * this.alpha * 50.0f;

				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(this.target.x, this.target.y, this.target.z,
					1.0f + 5.0f * this.options.baseSize(), this.rCol * strength, this.gCol * strength, this.bCol * strength));
			}
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.arcs.isEmpty()) {
			this.addArc(this.arcs, new Arc(null, 0, Vec3.ZERO, target.subtract(this.x, this.y, this.z), this.options.splits(), 0, 0, 1.0f), this.options.baseOffsets(), this.options.baseSubdivs());
		}

		for (Arc arc : this.arcs) {
			if (arc.parent != null) {
				arc.from = arc.parent.from.add(arc.parent.dir.scale(arc.t));
			}

			float jitter = (arc.depth + 1) * 0.02f;

			float length = (float) arc.dir.length() * (arc.depth > 0 ? 0.9f * arc.t : 1.0f);

			arc.dir = arc.dir.add((this.random.nextFloat() - 0.5f) * jitter, (this.random.nextFloat() - 0.5f) * jitter, (this.random.nextFloat() - 0.5f) * jitter).normalize().scale(length);
		}

		int iters = Math.round(this.options.minSplitSpeed() + this.random.nextFloat() * (this.options.maxSplitSpeed() - this.options.minSplitSpeed()));

		for (int j = 0; j < iters; j++) {
			List<Arc> newArcs = new ArrayList<>();

			for (Arc arc : this.arcs) {
				if (arc.splits > 0) {
					int numSplits = arc.splits / 2 + this.random.nextInt(arc.splits / 2 + 1);
					for (int i = 0; i < numSplits; i++) {
						arc.splits--;

						float len = (float) arc.dir.length();

						Vec3 dir = arc.dir.add((this.random.nextFloat() - 0.5f) * len * 0.5f, (this.random.nextFloat() - 0.5f) * len * 0.5f, (this.random.nextFloat() - 0.5f) * len * 0.5f).normalize().scale(len * arc.subdivs * this.options.lengthDecay());

						float t = this.random.nextFloat();
						Vec3 from = arc.from.add(arc.dir.scale(t));

						this.addArc(newArcs, new Arc(arc, t, from, dir, arc.splits / 2, this.options.branchSubdivs(), arc.depth + 1, arc.size * this.options.sizeDecay()), len * this.options.branchOffsets() / 3.0f, this.options.branchSubdivs());
					}
				}
			}

			this.arcs.addAll(newArcs);
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return BLParticleRenderType.BEAM;
	}

	public static final class Factory extends ParticleFactory<Factory, LightningArcParticleOptions> {

		@Override
		public LightningArcParticle createParticle(LightningArcParticleOptions options, ImmutableParticleArgs args) {
			return new LightningArcParticle(options, args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Vec3.class, 0), args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Vec3.ZERO, 5);
		}

	}
}
