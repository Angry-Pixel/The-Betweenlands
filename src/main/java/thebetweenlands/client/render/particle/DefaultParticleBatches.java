package thebetweenlands.client.render.particle;

import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;

public class DefaultParticleBatches {
	private DefaultParticleBatches() { }

	//Generic batches
	public static final ParticleBatch TRANSLUCENT_GLOWING = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchTypeBuilder().pass()
			.blend(SourceFactor.SRC_ALPHA, DestFactor.ONE)
			.depthMask(false)
			.texture(TextureMap.LOCATION_BLOCKS_TEXTURE)
			.blur(true)
			.end().build());

	//Specific batches (might be rendered from somewhere else than the main particle renderer)
	public static final ParticleBatch WISPS = BatchedParticleRenderer.INSTANCE.registerBatchType(TRANSLUCENT_GLOWING.type());
	public static final ParticleBatch GAS_CLOUDS = BatchedParticleRenderer.INSTANCE.registerBatchType(new ParticleBatchTypeBuilder().pass().depthMaskPass(true).texture(null).end().build(), false);
}