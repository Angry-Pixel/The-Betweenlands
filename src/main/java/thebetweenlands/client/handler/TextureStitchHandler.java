package thebetweenlands.client.handler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelLoaderRegistry.LoaderException;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.render.sprite.TextureCorrosion;
import thebetweenlands.client.render.sprite.TextureFromData;
import thebetweenlands.client.render.tile.RenderCenser;
import thebetweenlands.client.render.tile.RenderTarBarrel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.ModelRegistry;
import thebetweenlands.util.TexturePacker.TextureQuadMap;

public class TextureStitchHandler {
	public static final TextureStitchHandler INSTANCE = new TextureStitchHandler();

	private final List<TextureCorrosion> stitchedCorrosionSprites = new ArrayList<TextureCorrosion>();

	private final List<TextureStitcher> stitchers = new ArrayList<TextureStitcher>();

	/**
	 * Registers a texture stitcher
	 * @param splitter
	 */
	public void registerTextureStitcher(TextureStitcher splitter) {
		this.stitchers.add(splitter);
	}

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre e) {
		if(e.getMap() != Minecraft.getMinecraft().getTextureMapBlocks()) {
			//Only stitch to the main texture map
			return;
		}
		
		//Stich fluid textures onto atlas
		for(Fluid fluid : FluidRegistry.REGISTERED_FLUIDS) {
			e.getMap().registerSprite(fluid.getFlowing());
			e.getMap().registerSprite(fluid.getStill());
		}
		
		
		e.getMap().registerSprite(RenderTarBarrel.WHITE_SPRITE_PATH);
		e.getMap().registerSprite(RenderCenser.CENSER_FOG_PATH);

		
		//Pack model textures and stitch onto atlas
		long packingStartTime = System.nanoTime();
		Map<ResourceLocation, BufferedImage> packedTextures = ModelRegistry.MODEL_TEXTURE_PACKER.pack(Minecraft.getMinecraft().getResourceManager());

		TheBetweenlands.logger.info("Packed model textures in " + ((System.nanoTime() - packingStartTime) / 1000000.0f) + "ms");
		TheBetweenlands.logger.info("Optimal footprint: " + ModelRegistry.MODEL_TEXTURE_PACKER.getOptimalFootprint() + "px^2, Packed footprint: " + ModelRegistry.MODEL_TEXTURE_PACKER.getPackedFootprint() + "px^2");

		if(BetweenlandsConfig.DEBUG.dumpPackedTextures) {
			for(Entry<ResourceLocation, BufferedImage> packed : packedTextures.entrySet()) {
				try {
					File f = new File("betweenlands/packed_textures/" + packed.getKey().getPath() + ".png");
					f.mkdirs();
					ImageIO.write(packed.getValue(), "PNG", f);
				} catch (IOException ex) {
					TheBetweenlands.logger.error("Failed dumping packed texture", ex);
				}
			}
		}

		for(TextureQuadMap map : ModelRegistry.MODEL_TEXTURE_PACKER.getTextureMaps()) {
			if(map.getOwner() != null) {
				map.getOwner().onPacked();
			}
		}

		for(Entry<ResourceLocation, BufferedImage> packedTexture : packedTextures.entrySet()) {
			e.getMap().setTextureEntry(new TextureFromData(packedTexture.getKey().toString(), packedTexture.getValue()));
		}

		//Corrosion
		this.stitchedCorrosionSprites.clear();
		Map<String, TextureAtlasSprite> mapRegisteredSprites;
		try {
			mapRegisteredSprites = e.getMap().mapRegisteredSprites;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to load underlying sprite map", ex);
		}
		for (Item item : ItemRegistry.ITEMS) {
			if (item instanceof ICorrodible) {
				ResourceLocation[] variants = ICorrodible.getItemCorrodibleVariants((Item & ICorrodible) item);
				for(ResourceLocation variant : variants) {
					try {
						ResourceLocation modelLocation = new ResourceLocation(variant.getNamespace(), "item/" + variant.getPath());
						IModel model = ModelLoaderRegistry.getModel(modelLocation);
						List<ResourceLocation> textures = Lists.newArrayList();
						textures.addAll(model.getTextures());
						List<IModel> dependencies = this.gatherModelDependencies(model, new ArrayList<IModel>());
						for(IModel dependencyModel : dependencies) {
							Collection<ResourceLocation> dependencyTextures = dependencyModel.getTextures();
							for(ResourceLocation dependencyTexture : dependencyTextures) {
								if(!textures.contains(dependencyTexture))
									textures.add(dependencyTexture);
							}
						}
						for(ResourceLocation texture : textures) {
							String path = texture.getPath();
							if(path.contains("/")) {
								String corrodibleSuffix = "_corrodible";
								String fileName = texture.getPath().substring(texture.getPath().lastIndexOf("/") + 1);
								if(fileName.endsWith(corrodibleSuffix)) {
									ResourceLocation completeBaseTextureLocation = new ResourceLocation(texture.getNamespace(), String.format("textures/%s.png", texture.getPath()));
									for (int n = 0; n < CorrosionHelper.CORROSION_STAGE_COUNT; n++) {
										String corrosionSpriteName = texture.getNamespace() + ":" + path.substring(0, path.length() - corrodibleSuffix.length()) + "_corrosion_" + n;
										TextureCorrosion corrosionTexture = new TextureCorrosion(corrosionSpriteName, completeBaseTextureLocation, n, item.getTranslationKey().hashCode());
										//Forcibly sets the texture entry because TextureMap#setTextureEntry doesn't allow 
										//overwriting a previously added sprite (usually set in ModelLoader#setupModelRegistry).
										//Maybe find a better way to do this, if at all possible anyways
										mapRegisteredSprites.put(corrosionSpriteName, corrosionTexture);
										this.stitchedCorrosionSprites.add(corrosionTexture);
									}
								}
							}
						}
					} catch(Exception ex) {
						throw new RuntimeException("Failed to load corrosion texture", ex);
					}
				}
			}
		}

		//Stitch textures and split animations if necessary
		Map<ResourceLocation, Frame[]> animationFramesCache = new HashMap<>();
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		for(TextureStitcher stitcher : this.stitchers) {
			ResourceLocation[] textures = stitcher.getTextures();
			Frame[][] frames = new Frame[textures.length][];

			for(int i = 0; i < textures.length; i++) {
				TextureAtlasSprite sprite = e.getMap().registerSprite(textures[i]);

				if(stitcher.splitFrames) {
					try {
						ResourceLocation resourceLocation = this.getResourceLocation(e.getMap().getBasePath(), sprite);

						Frame[] cachedFrames = animationFramesCache.get(resourceLocation);
						if(cachedFrames != null) {
							//Use cached frames
							frames[i] = cachedFrames;
						} else {
							//Load sprite
							IResource resource = null;
							if (sprite.hasCustomLoader(resourceManager, resourceLocation)) {
								sprite.load(resourceManager, resourceLocation, l -> mapRegisteredSprites.get(l.toString()));
							} else {
								PngSizeInfo pngSizeInfo = PngSizeInfo.makeFromResource(resourceManager.getResource(resourceLocation));
								resource = resourceManager.getResource(resourceLocation);
								boolean hasAnimation = resource.getMetadata("animation") != null;
								sprite.loadSprite(pngSizeInfo, hasAnimation);
							}

							//Necessary to initialize animation metadata
							sprite.loadSpriteFrames(resource, e.getMap().getMipmapLevels() + 1);

							//Set sprites or split into seperate frames
							if(!sprite.hasAnimationMetadata() || sprite.getFrameCount() == 1) {
								//Single frame
								frames[i] = new Frame[]{ new Frame(sprite, 0) };
							} else {
								//Multiple frames, parse metadata and store seperate frames
								AnimationMetadataSection animationMetadata = resource.getMetadata("animation");
								boolean hasFrameMeta = animationMetadata.getFrameCount() > 0;
								int frameCount = hasFrameMeta ? animationMetadata.getFrameCount() : sprite.getFrameCount();
								frames[i] = new Frame[frameCount];
								for(int frame = 0; frame < frameCount; frame++) {
									int duration = hasFrameMeta ? animationMetadata.getFrameTimeSingle(frame) : animationMetadata.getFrameTime();
									int index = hasFrameMeta ? animationMetadata.getFrameIndex(frame) : frame;
									int frameData[] = sprite.getFrameTextureData(index)[0]; //Use original non-mipmap frame
									TextureAtlasSprite frameSprite = new TextureFromData(sprite.getIconName() + "_frame_" + frame, frameData, sprite.getIconWidth(), sprite.getIconHeight());
									e.getMap().setTextureEntry(frameSprite);
									frames[i][frame] = new Frame(frameSprite, duration);
								}
							}

							animationFramesCache.put(resourceLocation, frames[i]);
						}
					} catch(Exception ex) {
						throw new RuntimeException("Failed splitting texture animation", ex);
					}
				} else {
					frames[i] = new Frame[]{ new Frame(sprite, 0) };
				}
			}

			stitcher.frames = frames;
		}
	}

	private ResourceLocation getResourceLocation(String basePath, TextureAtlasSprite sprite) {
		ResourceLocation resourcelocation = new ResourceLocation(sprite.getIconName());
		return new ResourceLocation(resourcelocation.getNamespace(), String.format("%s/%s%s", basePath, resourcelocation.getPath(), ".png"));
	}

	@SubscribeEvent
	public void onTextureStitchPost(TextureStitchEvent.Post e) {
		if(e.getMap() != Minecraft.getMinecraft().getTextureMapBlocks()) {
			//Only stitch to the main texture map
			return;
		}

		//Corrosion
		TextureMap map = e.getMap();
		for(TextureCorrosion corrosionSprite : this.stitchedCorrosionSprites) {
			String parentIconName = corrosionSprite.getParentSpriteName().toString();
			TextureAtlasSprite parentSprite = map.getTextureExtry(parentIconName);
			if(parentSprite != null)
				corrosionSprite.setParentSprite(parentSprite);
		}
		this.stitchedCorrosionSprites.clear();

		//Frame splitters
		for(TextureStitcher splitter : this.stitchers) {
			if(splitter.callback != null) {
				splitter.callback.accept(splitter);
			}
		}
	}

	/**
	 * Recursively gathers all dependencies of the specified model
	 * @param model
	 * @param foundDependencies
	 * @return
	 */
	private List<IModel> gatherModelDependencies(IModel model, List<IModel> foundDependencies) {
		Collection<ResourceLocation> dependencies = model.getDependencies();
		for(ResourceLocation dependency : dependencies) {
			try {
				IModel dependencyModel = ModelLoaderRegistry.getModel(dependency);
				if(!foundDependencies.contains(dependencyModel)) {
					foundDependencies.add(dependencyModel);
					this.gatherModelDependencies(dependencyModel, foundDependencies);
				}
			} catch(LoaderException ex) {
				//Failed to load dependency, ignore
			} catch(Exception ex) {
				//Something else went wrong
				throw new RuntimeException(ex);
			}
		}
		return foundDependencies;
	}

	public static final class TextureStitcher {
		private final Consumer<TextureStitcher> callback;
		private final ResourceLocation[] textures;
		private Frame[][] frames;
		private boolean splitFrames;

		public TextureStitcher(ResourceLocation... textures) {
			this(null, textures);
		}

		public TextureStitcher(@Nullable Consumer<TextureStitcher> callback, ResourceLocation... textures) {
			this.textures = textures;
			this.callback = callback;
		}

		/**
		 * Sets whether texture animations should be split into separate sprites
		 * @param split
		 * @return
		 */
		public TextureStitcher setSplitFrames(boolean split) {
			this.splitFrames = split;
			return this;
		}

		/**
		 * Returns the texture locations
		 * @return
		 */
		public ResourceLocation[] getTextures() {
			return this.textures;
		}

		/**
		 * Returns the seperate frames of the animated texture
		 * @return
		 */
		public Frame[][] getFrames() {
			return this.frames;
		}
	}

	public static final class Frame {
		private final TextureAtlasSprite sprite;
		private final int duration;

		protected Frame(TextureAtlasSprite sprite, int duration) {
			this.sprite = sprite;
			this.duration = duration;
		}

		/**
		 * Returns the sprite of this frame
		 * @return
		 */
		public TextureAtlasSprite getSprite() {
			return this.sprite;
		}

		/**
		 * Returns the duration of this frame in ticks
		 * @return
		 */
		public int getDuration() {
			return this.duration;
		}
	}
}
