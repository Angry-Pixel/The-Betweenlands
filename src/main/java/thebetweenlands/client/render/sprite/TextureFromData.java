package thebetweenlands.client.render.sprite;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * Generates a single frame from raw texture data
 */
public class TextureFromData extends TextureAtlasSprite {
	private static final Logger LOGGER = LogManager.getLogger();

	private final int[] frameData;
	private int currentMipmapLevels = 0;

	public TextureFromData(String spriteName, int[] frameData, int width, int height) {
		super(spriteName);
		this.frameData = frameData;
		this.width = width;
		this.height = height;
	}
	
	public TextureFromData(String spriteName, BufferedImage image) {
		super(spriteName);
		this.frameData = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	/**
	 * Resets the sprite
	 */
	private void resetSprite() {
		this.setFramesTextureData(new ArrayList<int[][]>());
		this.frameCounter = 0;
		this.tickCounter = 0;
	}

	@Override
	public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
		return true;
	}

	@Override
	public void loadSprite(PngSizeInfo sizeInfo, boolean hasAnimationMeta) throws IOException {
		//This shouldn't happen because it uses a custom loader
		throw new RuntimeException("Not supported!");
	}

	@Override
	public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		try {
			this.loadSpriteFrames(null, this.currentMipmapLevels + 1);
		} catch (IOException ex) {
			LOGGER.error((String)("Using missing texture, unable to load " + location), (Throwable)ex);
		}
		return false;
	}

	@Override
	public void generateMipmaps(int mipmapLevels) {
		boolean generatedMipmaps = true;
		if(mipmapLevels > this.currentMipmapLevels) {
			this.currentMipmapLevels = mipmapLevels;
			this.load(null, null, null);
		}
		super.generateMipmaps(generatedMipmaps ? mipmapLevels : 0);
	}

	@Override
	public void loadSpriteFrames(IResource resource, int mipmapLevels) throws IOException {
		this.resetSprite();

		int[][] frameData = new int[mipmapLevels][];
		frameData[0] = this.frameData;
		this.framesTextureData.add(frameData);
	}

	@Override
	public boolean hasAnimationMetadata() {
		return false;
	}
}
