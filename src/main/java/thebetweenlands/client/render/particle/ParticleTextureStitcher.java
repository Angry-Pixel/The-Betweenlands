package thebetweenlands.client.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;

public final class ParticleTextureStitcher<T> {
	/**
	 * Creates a new particle texture stitcher for the specified type and textures.
	 * The particle class must implement {@link IParticleSpriteReceiver}
	 * @param cls
	 * @param textures
	 * @return
	 */
	public static <T extends Particle & IParticleSpriteReceiver> ParticleTextureStitcher<T> create(Class<T> cls, ResourceLocation... textures) {
		return new ParticleTextureStitcher<T>(textures);
	}

	/**
	 * Creates a new particle texture stitcher for the specified type and texture.
	 * The particle class must implement {@link IParticleSpriteReceiver}
	 * @param cls
	 * @param texture
	 * @return
	 */
	public static <T extends Particle & IParticleSpriteReceiver> ParticleTextureStitcher<T> create(Class<T> cls, ResourceLocation texture) {
		return create(cls, new ResourceLocation[]{texture});
	}

	private final ResourceLocation[] textures;
	private Frame[][] loadedFrames;
	private boolean splitAnimations = false;

	private ParticleTextureStitcher(ResourceLocation[] textures) {
		this.textures = textures;
	}

	/**
	 * Makes the texture stitcher split animated textures into single frames
	 * @param split
	 * @return
	 */
	public ParticleTextureStitcher<T> setSplitAnimations(boolean split) {
		this.splitAnimations = true;
		return this;
	}

	/**
	 * Sets the frames
	 * @param frames
	 */
	public void setFrames(Frame[][] frames) {
		this.loadedFrames = frames;
	}

	/**
	 * Returns whether animated textures should be split into single frames
	 * @return
	 */
	public boolean shouldSplitAnimations() {
		return this.splitAnimations;
	}

	/**
	 * Returns the particle textures
	 * @return
	 */
	public ResourceLocation[] getTextures() {
		return this.textures;
	}

	/**
	 * Returns the particle sprites
	 * @return
	 */
	public Frame[][] getSprites() {
		return this.loadedFrames;
	}

	/**
	 * Any particle that uses stitched textures must implement this interface.
	 * {@link Particle#getFXLayer()} must return 1 in order for this to work.
	 */
	public static interface IParticleSpriteReceiver {
		/**
		 * Sets the stitched particle frames
		 * @param frames
		 */
		default void setStitchedSprites(Frame[][] frames) {
			((Particle)this).setParticleTexture(frames[0][0].getSprite());
		}
	}
}