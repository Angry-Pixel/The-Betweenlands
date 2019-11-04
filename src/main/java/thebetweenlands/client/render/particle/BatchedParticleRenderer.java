package thebetweenlands.client.render.particle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BatchedParticleRenderer {
	public static class ParticleBatchType {
		public int priority() {
			return 0;
		}

		public int maxParticles() {
			return 8192;
		}

		public int batchSize() {
			return this.maxParticles();
		}

		public boolean filter(Particle particle) {
			return true;
		}

		protected void render(Iterable<Particle> particles, Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
			BufferBuilder buffer = tessellator.getBuffer();

			for(Particle particle : particles) {
				particle.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
			}
		}

		protected void preRender(Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
			tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}

		protected void postRender(Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
			tessellator.draw();
		}
	}

	public static final class ParticleBatch implements Comparable<ParticleBatch> {
		private final ParticleBatchType type;
		private final Queue<Particle> queue = new ArrayDeque<>();
		private final Deque<Particle> particles = new ArrayDeque<>();

		private ParticleBatch(ParticleBatchType type) {
			this.type = type;
		}

		public ParticleBatchType type() {
			return this.type;
		}

		public Collection<Particle> getParticles() {
			return Collections.unmodifiableCollection(this.particles);
		}

		public boolean isEmpty() {
			return this.particles.isEmpty();
		}

		@Override
		public int compareTo(ParticleBatch other) {
			return Integer.compare(this.type.priority(), other.type.priority());
		}
	}

	public static final BatchedParticleRenderer INSTANCE = new BatchedParticleRenderer();

	private BatchedParticleRenderer() { }

	private final List<ParticleBatch> batches = new ArrayList<>();
	private final List<ParticleBatch> renderedBatches = new ArrayList<>();

	public ParticleBatch registerBatchType(ParticleBatchType type) {
		return this.registerBatchType(type, true);
	}

	public ParticleBatch registerBatchType(ParticleBatchType type, boolean render) {
		ParticleBatch batch = new ParticleBatch(type);
		this.batches.add(batch);
		if(render) {
			this.renderedBatches.add(batch);
			Collections.sort(this.renderedBatches);
		}
		return batch;
	}
	
	public ParticleBatch createBatchType(ParticleBatchType type) {
		return new ParticleBatch(type);
	}

	public boolean addParticle(ParticleBatch batch, Particle particle) {
		if(batch.type.filter(particle)) {
			batch.queue.add(particle);
			return true;
		}
		return false;
	}

	public void update() {
		for(ParticleBatch batch : this.batches) {
			this.updateBatch(batch);
		}
	}
	
	public void updateBatch(ParticleBatch batch) {
		Iterator<Particle> it = batch.particles.iterator();

		while(it.hasNext()) {
			Particle particle = it.next();

			particle.onUpdate();

			if(!particle.isAlive()) {
				it.remove();
			}
		}

		if(!batch.queue.isEmpty()) {
			for(Particle particle = batch.queue.poll(); particle != null; particle = batch.queue.poll()) {
				if(batch.particles.size() >= batch.type.maxParticles()) {
					batch.particles.removeFirst();
				}

				batch.particles.add(particle);
			}
		}
	}
	
	public void renderAll(Entity entity, float partialTicks) {
		for(ParticleBatch batch : this.renderedBatches) {
			this.renderBatch(batch, entity, partialTicks);
		}
	}

	public void renderBatch(ParticleBatch batch, Entity entity, float partialTicks) {
		this.renderBatchType(batch.type, batch.particles, entity, partialTicks);
	}

	public void renderBatchType(ParticleBatchType batchType, Collection<Particle> particles, Entity entity, float partialTicks) {
		if(!particles.isEmpty()) {
			float rx = ActiveRenderInfo.getRotationX();
			float rz = ActiveRenderInfo.getRotationZ();
			float ryz = ActiveRenderInfo.getRotationYZ();
			float rxy = ActiveRenderInfo.getRotationXY();
			float rxz = ActiveRenderInfo.getRotationXZ();

			Particle.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
			Particle.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
			Particle.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
			Particle.cameraViewDir = entity.getLook(partialTicks);

			Tessellator tessellator = Tessellator.getInstance();

			GlStateManager.pushMatrix();
			GlStateManager.enableDepth();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(true);
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			final int batchSize = batchType.batchSize();
			final int batches = particles.size() / batchSize + 1;

			final Iterator<Particle> it = particles.iterator();

			for(int i = 0; i < batches; i++) {
				final Iterable<Particle> batchView = new Iterable<Particle>() {
					private int counter = 0;

					@Override
					public Iterator<Particle> iterator() {
						return new Iterator<Particle>() {
							@Override
							public boolean hasNext() {
								return counter < batchSize && it.hasNext();
							}

							@Override
							public Particle next() {
								counter++;
								return it.next();
							}
						};
					}
				};

				batchType.preRender(tessellator, entity, partialTicks, rx, rxz, rz, ryz, rxy);
				batchType.render(batchView, tessellator, entity, partialTicks, rx, rxz, rz, ryz, rxy);
				batchType.postRender(tessellator, entity, partialTicks, rx, rxz, rz, ryz, rxy);
			}

			GlStateManager.enableDepth();
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.depthMask(true);
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.popMatrix();
		}
	}
}
