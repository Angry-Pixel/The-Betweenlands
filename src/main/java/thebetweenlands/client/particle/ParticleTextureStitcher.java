package thebetweenlands.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public final class ParticleTextureStitcher<T> {
	/**
	 * Creates a new particle texture stitcher for the specified type and texture.
	 * The particle class must implement {@link IParticleSpriteReceiver}
	 * @param cls
	 * @param texture
	 * @return
	 */
	public static <T extends Particle & IParticleSpriteReceiver> ParticleTextureStitcher create(Class<T> cls, ResourceLocation texture) {
		return new ParticleTextureStitcher<T>(texture);
	}

	private final ResourceLocation texture;
	private TextureAtlasSprite loadedSprite;

	private ParticleTextureStitcher(ResourceLocation texture) {
		this.texture = texture;
	}

	/**
	 * Returns the particle texture
	 * @return
	 */
	public ResourceLocation getTexture() {
		return this.texture;
	}

	/**
	 * Sets the particle sprite
	 * @param sprite
	 */
	public void setSprite(TextureAtlasSprite sprite) {
		this.loadedSprite = sprite;
	}

	/**
	 * Returns the particle sprite
	 * @return
	 */
	public TextureAtlasSprite getSprite() {
		return this.loadedSprite;
	}

	/**
	 * Any particle that uses stitched textures must implement this interface
	 */
	public static interface IParticleSpriteReceiver {
		/**
		 * Sets the stitched particle sprites
		 * @param sprite
		 */
		void setStitchedSprite(TextureAtlasSprite sprite);
	}
}