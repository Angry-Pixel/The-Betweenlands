package thebetweenlands.client.render.particle;

import java.util.function.Predicate;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatchType;
import thebetweenlands.client.render.shader.ShaderHelper;

public class DefaultParticleBatches {
	private DefaultParticleBatches() { }

	private static final Predicate<Particle> SHADER_FILTER = particle -> ShaderHelper.INSTANCE.isWorldShaderActive();

	private static final ResourceLocation PARTICLE_ATLAS = new ResourceLocation("textures/particle/particles.png");
	private static final ResourceLocation BLOCK_ATLAS = TextureMap.LOCATION_BLOCKS_TEXTURE;

	//Generic batches
	public static final ParticleBatch TRANSLUCENT_GLOWING = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchTypeBuilder().pass()
			.blend(SourceFactor.SRC_ALPHA, DestFactor.ONE)
			.depthMask(false)
			.texture(BLOCK_ATLAS)
			.blur(true)
			.end().build());

	public static final ParticleBatch UNBATCHED = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchType() {
		@Override
		protected void postRender(Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) { }

		@Override
		protected void preRender(Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) { }
	});

	//Specific batches (might be rendered from somewhere else than the main particle renderer)
	public static final ParticleBatch WISPS = BatchedParticleRenderer.INSTANCE.registerBatchType(TRANSLUCENT_GLOWING.type());
	public static final ParticleBatch GAS_CLOUDS = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchTypeBuilder().pass().depthMaskPass(true).texture(null).end().build(), false);
	public static final ParticleBatch HEAT_HAZE_PARTICLE_ATLAS = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchTypeBuilder().pass().texture(PARTICLE_ATLAS).end().filter(SHADER_FILTER).build(), false);
	public static final ParticleBatch HEAT_HAZE_BLOCK_ATLAS = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchTypeBuilder().pass().texture(BLOCK_ATLAS).end().filter(SHADER_FILTER).build(), false);
}