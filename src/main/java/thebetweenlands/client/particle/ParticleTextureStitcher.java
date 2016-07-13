package thebetweenlands.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public final class ParticleTextureStitcher<T> {
	/**
	 * Creates a new particle texture stitcher for the specified type and textures.
	 * The particle class must implement {@link IParticleSpriteReceiver}
	 * @param cls
	 * @param textures
	 * @return
	 */
	public static <T extends Particle & IParticleSpriteReceiver> ParticleTextureStitcher create(Class<T> cls, ResourceLocation[] textures) {
		return new ParticleTextureStitcher<T>(textures);
	}

	private final ResourceLocation[] textures;
	private TextureAtlasSprite[] loadedSprites;

	private ParticleTextureStitcher(ResourceLocation[] textures) {
		this.textures = textures;
	}

	/**
	 * Returns the particle textures
	 * @return
	 */
	public ResourceLocation[] getTextures() {
		return this.textures;
	}

	/**
	 * Sets the particle sprites
	 * @param sprites
	 */
	public void setSprites(TextureAtlasSprite[] sprites) {
		this.loadedSprites = sprites;
	}

	/**
	 * Returns the particle sprites
	 * @return
	 */
	public TextureAtlasSprite[] getSprites() {
		return this.loadedSprites;
	}

	/**
	 * Any particle that uses stitched textures must implement this interface
	 */
	public static interface IParticleSpriteReceiver {
		/**
		 * Sets the stitched particle sprites
		 * @param sprites
		 */
		void setStitchedSprites(TextureAtlasSprite[] sprites);
	}
}