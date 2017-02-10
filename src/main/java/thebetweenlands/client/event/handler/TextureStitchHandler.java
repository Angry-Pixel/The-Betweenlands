package thebetweenlands.client.event.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.sprite.TextureCorrosion;
import thebetweenlands.client.render.sprite.TextureFromData;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.registries.ItemRegistry;

public class TextureStitchHandler {
	public static final TextureStitchHandler INSTANCE = new TextureStitchHandler();

	private static final Field f_mapRegisteredSprites = ReflectionHelper.findField(TextureMap.class, "mapRegisteredSprites", "field_110574_e", "j");

	private final List<TextureCorrosion> stitchedCorrosionSprites = new ArrayList<TextureCorrosion>();

	private final List<TextureFrameSplitter> splitters = new ArrayList<TextureFrameSplitter>();

	private final Map<ResourceLocation, Frame[]> animationFramesCache = new HashMap<ResourceLocation, Frame[]>();

	/**
	 * Registers a texture frame splitter
	 * @param splitter
	 */
	public void registerTextureFrameSplitter(TextureFrameSplitter splitter) {
		this.splitters.add(splitter);
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre e) {
		//Corrosion
		this.stitchedCorrosionSprites.clear();
		Map<String, TextureAtlasSprite> mapRegisteredSprites;
		try {
			mapRegisteredSprites = (Map<String, TextureAtlasSprite>) f_mapRegisteredSprites.get(e.getMap());
		} catch (Exception ex) {
			throw new RuntimeException("Failed to load underlying sprite map", ex);
		}
		for (Item item : ItemRegistry.ITEMS) {
			if (item instanceof ICorrodible) {
				ResourceLocation[] variants = ICorrodible.getItemCorrodibleVariants((Item & ICorrodible) item);
				for(ResourceLocation variant : variants) {
					try {
						ResourceLocation modelLocation = new ResourceLocation(variant.getResourceDomain(), "item/" + variant.getResourcePath());
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
							String path = texture.getResourcePath();
							if(path.contains("/")) {
								String corrodibleSuffix = "_corrodible";
								String fileName = texture.getResourcePath().substring(texture.getResourcePath().lastIndexOf("/") + 1);
								if(fileName.endsWith(corrodibleSuffix)) {
									ResourceLocation completeBaseTextureLocation = new ResourceLocation(texture.getResourceDomain(), String.format("textures/%s.png", texture.getResourcePath()));
									for (int n = 0; n < CorrosionHelper.CORROSION_STAGE_COUNT; n++) {
										String corrosionSpriteName = texture.getResourceDomain() + ":" + path.substring(0, path.length() - corrodibleSuffix.length()) + "_corrosion_" + n;
										TextureCorrosion corrosionTexture = new TextureCorrosion(corrosionSpriteName, completeBaseTextureLocation, n, item.getUnlocalizedName().hashCode());
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

		//Stitch particle textures that aren't split
		BLParticles[] particles = BLParticles.values();
		for(BLParticles particle : particles) {
			ParticleTextureStitcher<?> stitcher = particle.getFactory().getStitcher();
			if(stitcher != null && !stitcher.shouldSplitAnimations()) {
				ResourceLocation[] textures = stitcher.getTextures();
				Frame[][] frames = new Frame[textures.length][];
				for(int i = 0; i < textures.length; i++) {
					frames[i] = new Frame[]{ new Frame(e.getMap().registerSprite(textures[i]), -1) };
				}
				stitcher.setFrames(frames);
			}
		}

		//Split animated textures into seperate frames
		this.animationFramesCache.clear();
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		for(TextureFrameSplitter splitter : this.splitters) {
			ResourceLocation[] textures = splitter.getTextures();
			Frame[][] frames = new Frame[textures.length][];

			for(int i = 0; i < textures.length; i++) {
				TextureAtlasSprite sprite = e.getMap().registerSprite(textures[i]);

				try {
					ResourceLocation resourceLocation = this.getResourceLocation(e.getMap().getBasePath(), sprite);

					Frame[] cachedFrames = this.animationFramesCache.get(resourceLocation);
					if(cachedFrames != null) {
						//Use cached frames
						frames[i] = cachedFrames;
					} else {
						//Load sprite
						IResource resource = null;
						if (sprite.hasCustomLoader(resourceManager, resourceLocation)) {
							sprite.load(resourceManager, resourceLocation);
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

						this.animationFramesCache.put(resourceLocation, frames[i]);
					}
				} catch(Exception ex) {
					throw new RuntimeException("Failed splitting texture animation", ex);
				}
			}

			splitter.frames = frames;
		}
	}

	private ResourceLocation getResourceLocation(String basePath, TextureAtlasSprite sprite) {
		ResourceLocation resourcelocation = new ResourceLocation(sprite.getIconName());
		return new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", new Object[] {basePath, resourcelocation.getResourcePath(), ".png"}));
	}

	@SubscribeEvent
	public void onTextureStitchPost(TextureStitchEvent.Post e) {
		//Corrosion
		TextureMap map = e.getMap();
		for(TextureCorrosion corrosionSprite : this.stitchedCorrosionSprites) {
			String parentIconName = corrosionSprite.getParentSpriteName().toString();
			TextureAtlasSprite parentSprite = map.getTextureExtry(parentIconName);
			if(parentSprite != null)
				corrosionSprite.setParentSprite(parentSprite);
		}

		//Frame splitters
		for(TextureFrameSplitter splitter : this.splitters) {
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

	public static final class TextureFrameSplitter {
		private final Consumer<TextureFrameSplitter> callback;
		private final ResourceLocation[] textures;
		private Frame[][] frames;

		public TextureFrameSplitter(ResourceLocation... textures) {
			this(null, textures);
		}

		public TextureFrameSplitter(@Nullable Consumer<TextureFrameSplitter> callback, ResourceLocation... textures) {
			this.textures = textures;
			this.callback = callback;
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
