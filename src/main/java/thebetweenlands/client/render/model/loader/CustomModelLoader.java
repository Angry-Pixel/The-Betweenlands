package thebetweenlands.client.render.model.loader;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import thebetweenlands.client.render.entity.RenderDraeton;
import thebetweenlands.client.render.model.loader.extension.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;

public final class CustomModelLoader implements ICustomModelLoader {
	private static enum LoaderType {
		NORMAL,
		EXTENSION
	}

	private static class LoaderResult {
		private final LoaderType type;
		private final LoaderExtension extension;
		private final String args;
		private final ResourceLocation actualLocation;

		private LoaderResult(LoaderType type, LoaderExtension extension, String args, ResourceLocation location) {
			this.type = type;
			this.extension = extension;
			this.args = args;
			if(location != null && location.getPath().startsWith("models/")) {
				String path = location.getPath();
				path = path.substring("models/".length());
				location = new ResourceLocation(location.getNamespace(), path);
			}
			this.actualLocation = location;
		}

		private LoaderResult(ResourceLocation location) {
			this(LoaderType.NORMAL, null, null, location);
		}

		private LoaderResult(ResourceLocation location, LoaderExtension extension, String args) {
			this(LoaderType.EXTENSION, extension, args, location);
		}
	}

	private final CustomModelManager manager;
	private final List<LoaderExtension> loaderExtensions = new ArrayList<LoaderExtension>();

	//Default loader extensions
	public static final LoaderExtension SIMPLE_ITEM_LOADER_EXTENSION = new SimpleItemLoaderExtension();
	public static final LoaderExtension ADVANCED_ITEM_LOADER_EXTENSION = new AdvancedItemLoaderExtension();
	public static final LoaderExtension MODEL_PROCESSOR_LOADER_EXTENSION = new ModelProcessorLoaderExtension();

	CustomModelLoader(CustomModelManager manager) {
		this.manager = manager;

		//Item model loader extensions
		this.registerExtension(SIMPLE_ITEM_LOADER_EXTENSION);
		this.registerExtension(ADVANCED_ITEM_LOADER_EXTENSION);
		this.registerExtension(MODEL_PROCESSOR_LOADER_EXTENSION);
	}

	/**
	 * Registers a loader extension
	 * @param extension
	 * @return
	 */
	public CustomModelLoader registerExtension(@Nonnull LoaderExtension extension) {
		Validate.notNull(extension);
		this.loaderExtensions.add(extension);
		return this;
	}

	/**
	 * Returns an unmodifiable list of all registered loader extensions
	 * @return
	 */
	public List<LoaderExtension> getExtensions() {
		return Collections.unmodifiableList(this.loaderExtensions);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		for(LoaderExtension extension : this.loaderExtensions) {
			if(extension instanceof IResourceManagerReloadListener) {
				((IResourceManagerReloadListener)extension).onResourceManagerReload(resourceManager);
			}
		}
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		//Check for loader extensions
		if(modelLocation.getPath().contains("$")) {
			LoaderResult result = this.getLoaderResult(modelLocation);
			if(result.type != LoaderType.NORMAL) {
				if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Accepting model %s (full path: %s) through loader extension %s with args %s", result.actualLocation, modelLocation, result.extension.getName(), result.args));
				return true;
			}
		}

		//Check for registered model providers
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.manager.getRegisteredModelProviders().entrySet()) {
			ResourceLocation registeredModel = entry.getKey();
			if(registeredModel.getNamespace().equals(modelLocation.getNamespace()) && modelLocation.getPath().startsWith(registeredModel.getPath())) {
				String suffix = modelLocation.getPath().substring(registeredModel.getPath().length());

				//Only accept if path fully matches or is a variant
				if(suffix.length() == 0 || suffix.startsWith("#")) {
					if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Accepting model %s as %s through model registry", modelLocation, registeredModel));
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		boolean accepted = false;

		//Check for loader extensions
		if(modelLocation.getPath().contains("$")) {
			LoaderResult result = this.getLoaderResult(modelLocation);

			if(result.type == LoaderType.EXTENSION) {
				accepted = true;

				//Load actual model
				IModel model = ModelLoaderRegistry.getModel(result.actualLocation);

				//Let the extension process the model
				LoaderExtension loaderExtension = result.extension;
				String loaderArgs = result.args;
				try {
					if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Loading model %s (full path: %s) through loader extension %s with args %s", result.actualLocation, modelLocation, result.extension.getName(), result.args));
					IModel loadedModel = loaderExtension.loadModel(model, result.actualLocation, loaderArgs);
					if(loadedModel != null) {
						return loadedModel;
					}
				} catch(Exception ex) {
					if(ex instanceof LoaderExtensionException == false) {
						this.throwLoaderException(loaderExtension, ex);
					} else {
						throw ex;
					}
				}
			}
		}

		if(!accepted) {
			//Check for registered model providers
			for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.manager.getRegisteredModelProviders().entrySet()) {
				ResourceLocation registeredModel = entry.getKey();
				if(registeredModel.getNamespace().equals(modelLocation.getNamespace()) && modelLocation.getPath().startsWith(registeredModel.getPath())) {
					String suffix = modelLocation.getPath().substring(registeredModel.getPath().length());

					//Only accept if path fully matches or is a variant
					if(suffix.length() == 0 || suffix.startsWith("#")) {
						accepted = true;
						if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Loading model %s as %s through model registry", modelLocation, registeredModel));
						IModel model = entry.getValue().apply(modelLocation);
						if(model != null) {
							return model;
						}
					}
				}
			}
		}

		if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.error("Unable to load model %s!", modelLocation);

		return null;
	}

	/**
	 * Returns how the specified model should be handled
	 * @param modelLocation
	 * @return
	 */
	private LoaderResult getLoaderResult(ResourceLocation modelLocation) {
		String fullModelPath = modelLocation.getPath();
		String modelPath = fullModelPath.substring(0, fullModelPath.indexOf("$"));
		String suffix = fullModelPath.substring(fullModelPath.indexOf("$"));

		//Find loader extension in suffix
		LoaderExtension loaderExtension = null;
		for(LoaderExtension arg : this.loaderExtensions) {
			String argPrefix = "$" + arg.getName() + "(";
			if(suffix.startsWith(argPrefix)) {
				loaderExtension = arg;
				break;
			}
		}

		//Find loader args in suffix
		String loaderArgs = null;
		if(loaderExtension != null) {
			suffix = suffix.substring(loaderExtension.getName().length() + 2);
			loaderArgs = suffix.substring(0, suffix.indexOf(")"));
			suffix = suffix.substring(loaderArgs.length() + 1);
			if(loaderArgs.length() == 0) {
				loaderArgs = null;
			}
		}

		ResourceLocation actualLocation = new ResourceLocation(modelLocation.getNamespace(), modelPath + suffix);

		//Extension loader
		if(loaderExtension != null) {
			return new LoaderResult(actualLocation, loaderExtension, loaderArgs);
		}

		//Normal loader
		return new LoaderResult(actualLocation); 
	}
	
	private void stitchLocation(TextureStitchEvent.Pre event, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Failed loading model '" + location);
		for(ResourceLocation texture : model.getTextures()) {
			event.getMap().registerSprite(texture);
		}
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		//Register FastTESR baked model textures
		for(TileEntitySpecialRenderer<?> renderer : TileEntityRendererDispatcher.instance.renderers.values()) {
			if(renderer instanceof IFastTESRBakedModels) {
				Collection<ModelResourceLocation> locations = ((IFastTESRBakedModels) renderer).getModelLocations();
				
				for(ModelResourceLocation location : locations) {
					this.stitchLocation(event, location);
				}
			}
		}
		
		this.stitchLocation(event, RenderDraeton.FRAME_MAP_MODEL);
		this.stitchLocation(event, RenderDraeton.FRAME_MODEL);
	}
	
	private IBakedModel bakeLocation(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Failed loading model '" + location);
		IBakedModel bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, (loc) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()));
		modelRegistry.putObject(location, bakedModel);
		return bakedModel;
	}
	
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		List<Pair<ModelResourceLocation, IBakedModel>> loadedModels = new ArrayList<Pair<ModelResourceLocation, IBakedModel>>();

		//Register FastTESR baked models
		for(TileEntitySpecialRenderer<?> renderer : TileEntityRendererDispatcher.instance.renderers.values()) {
			if(renderer instanceof IFastTESRBakedModels) {
				Collection<ModelResourceLocation> locations = ((IFastTESRBakedModels) renderer).getModelLocations();
				
				for(ModelResourceLocation location : locations) {
					((IFastTESRBakedModels) renderer).onModelBaked(location, this.bakeLocation(modelRegistry, location));
				}
			}
		}
		
		this.bakeLocation(modelRegistry, RenderDraeton.FRAME_MAP_MODEL);
		this.bakeLocation(modelRegistry, RenderDraeton.FRAME_MODEL);
		
		for(ModelResourceLocation modelLocation : modelRegistry.getKeys()) {
			IBakedModel model = modelRegistry.getObject(modelLocation);

			//Model depends on other baked models
			if(model instanceof IBakedModelDependant) {
				IBakedModelDependant dependant = (IBakedModelDependant) model;
				Collection<ModelResourceLocation> dependencies = dependant.getDependencies(modelLocation);
				Map<ModelResourceLocation, IBakedModel> loadedDependencies = new HashMap<ModelResourceLocation, IBakedModel>();

				for(ModelResourceLocation dependencyLocation : dependencies) {
					IBakedModel bakedModel = modelRegistry.getObject(dependencyLocation);

					if(bakedModel == null) {
						ResourceLocation dependencyLocationNoVariants = new ResourceLocation(dependencyLocation.getNamespace(), dependencyLocation.getPath());
						try {
							IModel externalModel = ModelLoaderRegistry.getModel(dependencyLocationNoVariants);
							bakedModel = externalModel.bake(dependant.getModelState(externalModel), dependant.getVertexFormat(externalModel), dependant.getTextureGetter(externalModel));
							loadedModels.add(Pair.of(dependencyLocation, bakedModel));
						} catch (Exception ex) {
							throw new RuntimeException("Failed to load model dependency " + dependencyLocationNoVariants + " for model " + modelLocation, ex);
						}
					}

					loadedDependencies.put(dependencyLocation, bakedModel);
				}

				dependant.setDependencies(modelLocation, loadedDependencies);
			}
		}
		for(Pair<ModelResourceLocation, IBakedModel> loadedModel : loadedModels) {
			if(loadedModel.getValue() != null) {
				if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Registering additional baked model %s", loadedModel.getKey()));
				modelRegistry.putObject(loadedModel.getKey(), loadedModel.getValue());
			} else {
				if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.warn(String.format("Additional baked model %s is null!", loadedModel.getKey()));
			}
		}

		//Replace loader extensions models
		Set<ModelResourceLocation> keys = modelRegistry.getKeys();
		Map<ModelResourceLocation, IBakedModel> replacementMap = new HashMap<>();

		//Get model replacements from extensions
		for(LoaderExtension extension : this.loaderExtensions) {
			for(ModelResourceLocation loc : keys) {
				try {
					IBakedModel replacement = extension.getModelReplacement(loc, modelRegistry.getObject(loc));
					if(replacement != null) {
						replacementMap.put(loc, replacement);
					}
				} catch(Exception ex) {
					if(ex instanceof LoaderExtensionException == false) {
						this.throwLoaderException(extension, ex);
					} else {
						throw ex;
					}
				}
			}
		}

		this.replaceRegistryObjects(modelRegistry, replacementMap);
	}

	/**
	 * Throws a {@link LoaderExtensionException}
	 * @param extension
	 * @param cause
	 */
	private void throwLoaderException(LoaderExtension extension, Throwable cause) {
		throw new LoaderExtensionException(String.format("Model loader extension %s failed loading a model", extension.getName()), cause);
	}

	/**
	 * Replaces the specified objects in the specified registry
	 * @param registry
	 * @param map
	 */
	private <K, T> void replaceRegistryObjects(IRegistry<K, T> registry, Map<K, T> map) {
		for(Entry<K, T> e : map.entrySet()) {
			if(BetweenlandsConfig.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Replaced model %s", e.getKey()));
			registry.putObject(e.getKey(), e.getValue());
		}

		//This doesn't work anymore since fml.skipFirstModelBake was introduced -.-
		//Might debug log "Adding duplicate key {...} to registry" now
		/*Map<T, K> objectsToRemove = new HashMap<T, K>(map.size());
		Set<K> replacementKeys = map.keySet();

		//Gather registered objects
		for(K replacementKey : replacementKeys) {
			if(ConfigHandler.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Searching model for %s", replacementKey));
			T obj = registry.getObject(replacementKey);
			if(obj != null) {
				if(ConfigHandler.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Found model %s", obj));
				objectsToRemove.put(obj, replacementKey);
			}
		}

		//Remove registered objects
		Iterator<T> it = registry.iterator();
		T obj = null;
		while(it.hasNext()) {
			obj = it.next();
			if(objectsToRemove.containsKey(obj)) {
				if(ConfigHandler.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Removed model %s, %s from registry", obj, objectsToRemove.get(obj)));
				it.remove();
			}
		}

		//Add replacement objects
		for(Entry<K, T> replacement : map.entrySet()) {
			if(replacement.getValue() != null) {
				if(ConfigHandler.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Replaced model %s", replacement.getKey()));
				registry.putObject(replacement.getKey(), replacement.getValue());
			} else {
				if(ConfigHandler.DEBUG.debugModelLoader) TheBetweenlands.logger.info(String.format("Removed model %s", replacement.getKey()));
			}
		}*/
	}
}