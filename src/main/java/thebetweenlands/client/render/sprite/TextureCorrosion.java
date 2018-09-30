package thebetweenlands.client.render.sprite;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.lib.ModInfo;

/**
 * Generates a corrosion overlay for the specified item.
 * Sprite naming goes as follows: some_item_corrodible -> some_item_corrosion_[0,5]
 */
public class TextureCorrosion extends TextureAtlasSprite {
	public static final ResourceLocation CORROSION_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/items/tool_corrosion.png");

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Random RANDOM = new Random(0);

	private static int[] corrosionPixels;
	private static int corrosionWidth;

	private AnimationMetadataSection parentAnimationMetadata;
	private final long seed;
	private final ResourceLocation parentTexture;
	private final ResourceLocation parentTextureSpriteName;
	private final int corrosionAmount;
	private int currentMipmapLevels = 0;
	private ResourceLocation loadedResourceLocation = null;
	private IResourceManager loaderResourceManager = null;
	private TextureAtlasSprite parentSprite;
	private Function<ResourceLocation, TextureAtlasSprite> textureGetter;

	public TextureCorrosion(String spriteName, ResourceLocation parentTexture, int corrosionAmount, long seed) {
		super(spriteName);
		this.parentTexture = parentTexture;
		String spritePath = this.parentTexture.getResourcePath();
		spritePath = spritePath.substring("textures/".length(), spritePath.length() - ".png".length());
		this.parentTextureSpriteName = new ResourceLocation(this.parentTexture.getResourceDomain(), spritePath);
		this.corrosionAmount = corrosionAmount;
		this.seed = seed;
	}

	/**
	 * Returns the parent texture location
	 * @return
	 */
	public ResourceLocation getParentTexture() {
		return this.parentTexture;
	}

	/**
	 * Returns the parent sprite name
	 * @return
	 */
	public ResourceLocation getParentSpriteName() {
		return this.parentTextureSpriteName;
	}

	/**
	 * Sets the parent sprite
	 * @param sprite
	 */
	public void setParentSprite(TextureAtlasSprite sprite) {
		this.parentSprite = sprite;
	}

	/**
	 * Returns the parent sprite
	 * @return
	 */
	public TextureAtlasSprite getParentSprite() {
		return this.parentSprite;
	}

	/**
	 * Resets the sprite
	 */
	private void resetSprite() {
		this.parentAnimationMetadata = null;
		this.setFramesTextureData(new ArrayList<int[][]>());
		this.frameCounter = 0;
		this.tickCounter = 0;
	}

	/**
	 * Loads the corrosion texture
	 * @param manager
	 */
	private void loadCorrosionPixels(IResourceManager manager) {
		BufferedImage corrosionImg;
		try {
			corrosionImg = ImageIO.read(manager.getResource(CORROSION_TEXTURE).getInputStream());
			corrosionPixels = new int[corrosionImg.getWidth() * corrosionImg.getHeight()];
			corrosionImg.getRGB(0, 0, corrosionImg.getWidth(), corrosionImg.getHeight(), corrosionPixels, 0, corrosionImg.getWidth());
			corrosionWidth = corrosionImg.getWidth();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if (corrosionPixels == null) {
			this.loadCorrosionPixels(manager);
		}
		IResource resource = null;
		try {
			this.loadedResourceLocation = location;
			this.loaderResourceManager = manager;
			this.textureGetter = textureGetter;
			resource = manager.getResource(this.parentTexture);
			this.loadSpriteFrames(resource, this.currentMipmapLevels + 1);
		} catch (RuntimeException runtimeexception) {
			LOGGER.error((String)("Unable to parse metadata from " + this.loadedResourceLocation), (Throwable)runtimeexception);
		} catch (IOException ioexception) {
			LOGGER.error((String)("Using missing texture, unable to load " + this.loadedResourceLocation), (Throwable)ioexception);
		} finally {
			IOUtils.closeQuietly((Closeable)resource);
		}
		return false;
	}

	@Override
	public void generateMipmaps(int mipmapLevels) {
		boolean generatedMipmaps = true;
		if(mipmapLevels > this.currentMipmapLevels) {
			if(this.loaderResourceManager != null && this.loadedResourceLocation != null) {
				this.currentMipmapLevels = mipmapLevels;
				try {
					this.load(this.loaderResourceManager, this.loadedResourceLocation, textureGetter);
				} catch(Exception ex) {
					//Failed loading resources, ignore and generate no mipmaps
					generatedMipmaps = false;
					this.currentMipmapLevels = 0;
				}
			}
		}
		super.generateMipmaps(generatedMipmaps ? mipmapLevels : 0);
	}

	@Override
	public void loadSpriteFrames(IResource resource, int mipmapLevels) throws IOException {
		BufferedImage spriteTexture = TextureUtil.readBufferedImage(resource.getInputStream());
		AnimationMetadataSection spriteMetadata = (AnimationMetadataSection)resource.getMetadata("animation");
		int[][] mipmappedFrames = new int[mipmapLevels][];
		mipmappedFrames[0] = new int[spriteTexture.getWidth() * spriteTexture.getHeight()];
		spriteTexture.getRGB(0, 0, spriteTexture.getWidth(), spriteTexture.getHeight(), mipmappedFrames[0], 0, spriteTexture.getWidth());

		resetSprite();

		int width = spriteTexture.getWidth();
		int height = spriteTexture.getHeight();
		this.width = width;
		this.height = height;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if(y % width == 0) {
					//Reset seed for every frame
					RANDOM.setSeed(this.seed);
				}
				int pixel = mipmappedFrames[0][x + y * width];
				int corrosion = 0;
				if (pixel >>> 24 != 0 && RANDOM.nextFloat() > 0.2F) {
					corrosion = corrosionPixels[(x % width % corrosionWidth) + (y % width % corrosionWidth) * corrosionWidth];
				}
				float alpha = (this.corrosionAmount / (float) (CorrosionHelper.CORROSION_STAGE_COUNT - 1) * ((corrosion >>> 24 & 0xFF) / 255.0F)) * (0.5F + RANDOM.nextFloat() * 0.5F);
				mipmappedFrames[0][x + y * width] = (((int)(alpha * 255.0F) & 0xFF) << 24) | ((corrosion >> 16 & 0xFF) << 16) | ((corrosion >> 8 & 0xFF) << 8) | (corrosion & 0xFF);
			}
		}

		if (spriteMetadata == null) {
			if (height != width) {
				throw new RuntimeException("broken aspect ratio and not an animation");
			}

			this.framesTextureData.add(mipmappedFrames);
		} else {
			int frameCount = height / width;
			int frameWidth = width;
			int frameHeight = width;
			this.height = width;

			if (spriteMetadata.getFrameCount() > 0) {
				Iterator<Integer> frameIndexIterator = spriteMetadata.getFrameIndexSet().iterator();

				int frameIndex = 0;
				while (frameIndexIterator.hasNext()) {
					frameIndex = frameIndexIterator.next().intValue();

					if (frameIndex >= frameCount) {
						throw new RuntimeException("invalid frameindex " + frameIndex);
					}

					this.allocateFrameTextureData(frameIndex);
					this.framesTextureData.set(frameIndex, getFrameTextureData(mipmappedFrames, frameWidth, frameHeight, frameIndex));
				}

				this.parentAnimationMetadata = spriteMetadata;
			} else {
				List<AnimationFrame> animationFrames = Lists.newArrayList();

				for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
					framesTextureData.add(getFrameTextureData(mipmappedFrames, frameWidth, frameHeight, frameIndex));
					animationFrames.add(new AnimationFrame(frameIndex, -1));
				}

				this.parentAnimationMetadata = new AnimationMetadataSection(animationFrames, width, height, spriteMetadata.getFrameTime(), spriteMetadata.isInterpolate());
			}
		}
	}

	/**
	 * Allocates memory for the specified amount of frames
	 * @param frameCount
	 */
	private void allocateFrameTextureData(int frameCount) {
		if (this.framesTextureData.size() <= frameCount) {
			for (int i = this.framesTextureData.size(); i <= frameCount; i++) {
				this.framesTextureData.add((int[][]) null);
			}
		}
	}

	/**
	 * Returns the frame data at the specified offset
	 * @param framesData
	 * @param width
	 * @param height
	 * @param offset
	 * @return
	 */
	private static int[][] getFrameTextureData(int[][] framesData, int width, int height, int offset) {
		int[][] newFrameData = new int[framesData.length][];

		for (int frame = 0; frame < framesData.length; ++frame) {
			int[] frameData = framesData[frame];

			if (frameData != null) {
				newFrameData[frame] = new int[(width >> frame) * (height >> frame)];
				System.arraycopy(frameData, offset * newFrameData[frame].length, newFrameData[frame], 0, newFrameData[frame].length);
			}
		}

		return newFrameData;
	}

	@Override
	public void updateAnimation() {
		if(this.parentSprite == null) {
			++this.tickCounter;

			if (this.tickCounter >= this.parentAnimationMetadata.getFrameTimeSingle(this.frameCounter)) {
				int frameIndex = this.parentAnimationMetadata.getFrameIndex(this.frameCounter);
				int frameCount = this.parentAnimationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.parentAnimationMetadata.getFrameCount();
				this.frameCounter = (this.frameCounter + 1) % frameCount;
				this.tickCounter = 0;
				int nextFrameIndex = this.parentAnimationMetadata.getFrameIndex(this.frameCounter);

				if (frameIndex != nextFrameIndex && nextFrameIndex >= 0 && nextFrameIndex < this.framesTextureData.size()) {
					TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(nextFrameIndex), this.width, this.height, this.originX, this.originY, false, false);
				}
			} else if (this.parentAnimationMetadata.isInterpolate()) {
				this.updateAnimationInterpolated();
			}
		} else {
			int prevFrameIndex = this.parentAnimationMetadata.getFrameIndex(this.frameCounter);
			try {
				this.tickCounter = parentSprite.tickCounter;
				this.frameCounter = parentSprite.frameCounter;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			int nextFrameIndex = this.parentAnimationMetadata.getFrameIndex(this.frameCounter);
			if (prevFrameIndex != nextFrameIndex) {
				TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(nextFrameIndex), this.width, this.height, this.originX, this.originY, false, false);
			} else if (this.parentAnimationMetadata.isInterpolate()) {
				this.updateAnimationInterpolated();
			}
		}
	}

	/**
	 * Uploads the interpolated frames
	 */
	private void updateAnimationInterpolated() {
		double interpolatedFrameIndex = 1.0D - (double)this.tickCounter / (double)this.parentAnimationMetadata.getFrameTimeSingle(this.frameCounter);
		int frameIndex = this.parentAnimationMetadata.getFrameIndex(this.frameCounter);
		int frameCount = this.parentAnimationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.parentAnimationMetadata.getFrameCount();
		int nextFrameIndex = this.parentAnimationMetadata.getFrameIndex((this.frameCounter + 1) % frameCount);

		if (frameIndex != nextFrameIndex && nextFrameIndex >= 0 && nextFrameIndex < this.framesTextureData.size()) {
			int[][] frame = (int[][])this.framesTextureData.get(frameIndex);
			int[][] nextFrame = (int[][])this.framesTextureData.get(nextFrameIndex);

			if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != frame.length) {
				this.interpolatedFrameData = new int[frame.length][];
			}

			for (int mipmapLevel = 0; mipmapLevel < frame.length; ++mipmapLevel) {
				if (this.interpolatedFrameData[mipmapLevel] == null) {
					this.interpolatedFrameData[mipmapLevel] = new int[frame[mipmapLevel].length];
				}

				if (mipmapLevel < nextFrame.length && nextFrame[mipmapLevel].length == frame[mipmapLevel].length) {
					for (int framePixelIndex = 0; framePixelIndex < frame[mipmapLevel].length; ++framePixelIndex) {
						int pixel = frame[mipmapLevel][framePixelIndex];
						int nextPixel = nextFrame[mipmapLevel][framePixelIndex];
						int interpR = this.interpolateColor(interpolatedFrameIndex, pixel >> 16 & 255, nextPixel >> 16 & 255);
						int interpG = this.interpolateColor(interpolatedFrameIndex, pixel >> 8 & 255, nextPixel >> 8 & 255);
						int interpB = this.interpolateColor(interpolatedFrameIndex, pixel & 255, nextPixel & 255);
						this.interpolatedFrameData[mipmapLevel][framePixelIndex] = pixel & -16777216 | interpR << 16 | interpG << 8 | interpB;
					}
				}
			}

			TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
		}
	}

	/**
	 * Linearly interpolates between two integers
	 * @param lerp
	 * @param val1
	 * @param val2
	 * @return
	 */
	private int interpolateColor(double lerp, int val1, int val2) {
		return (int)(lerp * (double)val1 + (1.0D - lerp) * (double)val2);
	}

	@Override
	public boolean hasAnimationMetadata() {
		return this.parentAnimationMetadata != null;
	}
}
