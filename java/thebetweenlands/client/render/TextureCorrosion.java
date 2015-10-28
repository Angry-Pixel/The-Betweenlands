package thebetweenlands.client.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thebetweenlands.lib.ModInfo;
import thebetweenlands.utils.CorrodibleItemHelper;

import com.google.common.collect.Lists;

public class TextureCorrosion extends TextureAtlasSprite {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Random RANDOM = new Random(0);

	private static final ResourceLocation CORROSION_RESOURCE = new ResourceLocation(ModInfo.ID, "textures/items/toolCorrosion.png");
	private static int[] corrosionPixels;
	private static int corrosionWidth;
	private static int corrosionHeight;

	private AnimationMetadataSection animationMetadata;

	private long seed;

	private String baseIconName;

	private int corrosionAmount;

	public TextureCorrosion(String iconName, String baseIconName, int corrosionAmount) {
		super(iconName);
		this.baseIconName = baseIconName;
		this.corrosionAmount = corrosionAmount;
		seed = baseIconName.hashCode();
	}

	private void resetSprite() {
		animationMetadata = null;
		setFramesTextureData(Lists.newArrayList());
		frameCounter = 0;
		tickCounter = 0;
	}

	private void loadCorrosionPixels(IResourceManager manager) {
		BufferedImage corrosionImg;
		try {
			corrosionImg = ImageIO.read(manager.getResource(CORROSION_RESOURCE).getInputStream());
			corrosionPixels = new int[corrosionImg.getWidth() * corrosionImg.getHeight()];
			corrosionImg.getRGB(0, 0, corrosionImg.getWidth(), corrosionImg.getHeight(), corrosionPixels, 0, corrosionImg.getWidth());
			corrosionWidth = corrosionImg.getWidth();
			corrosionHeight = corrosionImg.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
		return true;
	}

	@Override
	public boolean load(IResourceManager manager, ResourceLocation location) {
		if (corrosionPixels == null) {
			loadCorrosionPixels(manager);
		}
		location = new ResourceLocation(baseIconName);
		ResourceLocation resourcelocation1 = completeResourceLocation(location, 0);
		try {
			IResource resource = manager.getResource(resourcelocation1);
			BufferedImage[] bufferedImage = new BufferedImage[1];
			bufferedImage[0] = ImageIO.read(resource.getInputStream());
			loadSprite(bufferedImage, (AnimationMetadataSection) resource.getMetadata("animation"), false);
		} catch (RuntimeException runtimeexception) {
			LOGGER.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
		} catch (IOException ioexception1) {
			ioexception1.printStackTrace();
		}
		return false;
	}

	private ResourceLocation completeResourceLocation(ResourceLocation location, int level) {
		return level == 0 ? new ResourceLocation(location.getResourceDomain(), String.format("textures/items/%s.png", location.getResourcePath())) : new ResourceLocation(location.getResourceDomain(), String.format("textures/items/mipmaps/%s.%d.png", location.getResourcePath(), Integer.valueOf(level)));
	}

	@Override
	public void loadSprite(BufferedImage[] mipmapImages, AnimationMetadataSection metadata, boolean useAnisotropicFiltering) {
		resetSprite();
		int width = mipmapImages[0].getWidth();
		int height = mipmapImages[0].getHeight();
		this.width = width;
		this.height = height;

		int[][] mipmapLevels = new int[mipmapImages.length][];
		RANDOM.setSeed(seed);
		BufferedImage bufferedimage = mipmapImages[0];

		mipmapLevels[0] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
		bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), mipmapLevels[0], 0, bufferedimage.getWidth());

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = mipmapLevels[0][x + y * width];
				int corrosion = 0;
				if (pixel >>> 24 != 0 && RANDOM.nextBoolean()) {
					corrosion = corrosionPixels[(x % width % corrosionWidth) + (y % width % corrosionWidth) * corrosionWidth];
				}
				int r1 = pixel >> 16 & 0xFF;
				int g1 = pixel >> 8 & 0xFF;
				int b1 = pixel & 0xFF;
				float alpha = corrosionAmount / (float) (CorrodibleItemHelper.CORROSION_STAGE_COUNT - 1) * ((corrosion >>> 24 & 0xFF) / 255F);
				int r2 = corrosion >> 16 & 0xFF;
				int g2 = corrosion >> 8 & 0xFF;
				int b2 = corrosion & 0xFF;
				pixel &= 0xFF000000;
				pixel |= (int) (alpha * r2 + (1 - alpha) * r1 + 0.5F) << 16;
				pixel |= (int) (alpha * g2 + (1 - alpha) * g1 + 0.5F) << 8;
				pixel |= (int) (alpha * b2 + (1 - alpha) * b1 + 0.5F);
				mipmapLevels[0][x + y * width] = pixel;
			}
		}

		if (metadata == null) {
			if (height != width) {
				throw new RuntimeException("broken aspect ratio and not an animation");
			}

			framesTextureData.add(mipmapLevels);
		} else {
			int frameCount = height / width;
			int frameWidth = width;
			int frameHeight = width;
			this.height = width;

			if (metadata.getFrameCount() > 0) {
				Iterator frameIndexIterator = metadata.getFrameIndexSet().iterator();

				int frameIndex = 0;
				while (frameIndexIterator.hasNext()) {
					frameIndex = ((Integer) frameIndexIterator.next()).intValue();

					if (frameIndex >= frameCount) {
						throw new RuntimeException("invalid frameindex " + frameIndex);
					}

					allocateFrameTextureData(frameIndex);
					framesTextureData.set(frameIndex, getFrameTextureData(mipmapLevels, frameWidth, frameHeight, frameIndex));
				}

				animationMetadata = metadata;
			} else {
				ArrayList arraylist = Lists.newArrayList();

				for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
					framesTextureData.add(getFrameTextureData(mipmapLevels, frameWidth, frameHeight, frameIndex));
					arraylist.add(new AnimationFrame(frameIndex, -1));
				}

				animationMetadata = new AnimationMetadataSection(arraylist, width, height, metadata.getFrameTime());
			}
		}
	}

	private void allocateFrameTextureData(int frameCount) {
		if (framesTextureData.size() <= frameCount) {
			for (int i = framesTextureData.size(); i <= frameCount; i++) {
				framesTextureData.add((Object) null);
			}
		}
	}

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
}
