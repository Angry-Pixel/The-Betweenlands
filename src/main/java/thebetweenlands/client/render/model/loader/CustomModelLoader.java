package thebetweenlands.client.render.model.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.render.model.loader.args.AdvancedItemLoaderArgs;
import thebetweenlands.client.render.model.loader.args.SimpleItemLoaderArgs;

public class CustomModelLoader implements ICustomModelLoader {
	private static enum MatchType {
		NORMAL,
		ARGS,
		NONE
	}

	static class MatchResult {
		private final MatchType type;
		private final LoaderArgs args;
		private final String params;
		private final ResourceLocation actualLocation;

		private MatchResult(MatchType type, LoaderArgs args, String params, ResourceLocation location) {
			this.type = type;
			this.args = args;
			this.params = params;
			if(location != null && location.getResourcePath().startsWith("models/")) {
				String path = location.getResourcePath();
				path = path.substring("models/".length());
				location = new ResourceLocation(location.getResourceDomain(), path);
			}
			this.actualLocation = location;
		}

		private MatchResult(ResourceLocation location) {
			this(MatchType.NORMAL, null, null, location);
		}

		private MatchResult(ResourceLocation location, LoaderArgs args, String params) {
			this(MatchType.ARGS, args, params, location);
		}

		public MatchType getType() {
			return this.type;
		}

		public String getParameters() {
			return this.params;
		}

		public LoaderArgs getArgument() {
			return this.args;
		}

		public ResourceLocation getActualLocation() {
			return this.actualLocation;
		}
	}

	public final CustomModelManager registry;
	private final List<LoaderArgs> loaderArgs = new ArrayList<LoaderArgs>();

	CustomModelLoader(CustomModelManager registry) {
		this.registry = registry;

		//Item model loader args
		this.loaderArgs.add(new SimpleItemLoaderArgs());
		this.loaderArgs.add(new AdvancedItemLoaderArgs());
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		//NOOP
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(this.getMatchResult(entry.getKey(), modelLocation).getType() != MatchType.NONE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			MatchResult match = this.getMatchResult(entry.getKey(), modelLocation);
			if(match.getType() == MatchType.NORMAL) {
				return entry.getValue().apply(match.getActualLocation());
			} else if(match.getType() == MatchType.ARGS) {
				LoaderArgs loaderArgs = match.getArgument();
				String loaderParam = match.getParameters();
				return loaderArgs.loadModel(entry.getValue().apply(match.getActualLocation()), match.getActualLocation(), loaderParam);
			}
		}
		return null;
	}

	private MatchResult getMatchResult(ResourceLocation registeredModel, ResourceLocation modelLocation) {
		if(!registeredModel.getResourceDomain().equals(modelLocation.getResourceDomain()))
			return new MatchResult(MatchType.NONE, null, null, null);
		String registeredPath = registeredModel.getResourcePath();
		String modelPath = modelLocation.getResourcePath();
		if(modelPath.startsWith(registeredPath)) {
			String suffix = modelPath.substring(registeredPath.length());
			//Find loader args in suffix
			LoaderArgs loaderArg = null;
			for(LoaderArgs arg : this.loaderArgs) {
				String argPrefix = "$" + arg.getName() + "(";
				if(suffix.startsWith(argPrefix)) {
					loaderArg = arg;
					break;
				}
			}
			String loaderParam = null;
			if(loaderArg != null) {
				suffix = suffix.substring(loaderArg.getName().length() + 2);
				loaderParam = suffix.substring(0, suffix.indexOf(")"));
				suffix = suffix.substring(loaderParam.length() + 1);
				if(loaderParam.length() == 0)
					loaderParam = null;
			}
			//Only accept if path fully matches or is a variant
			if(suffix.length() == 0 || suffix.startsWith("#")) {
				ResourceLocation actualLocation = new ResourceLocation(modelLocation.getResourceDomain(), registeredPath + suffix);
				if(loaderArg != null) {
					return new MatchResult(actualLocation, loaderArg, loaderParam);
				}
				return new MatchResult(actualLocation); 
			}
		}
		return new MatchResult(MatchType.NONE, null, null, null);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		List<Pair<ModelResourceLocation, IBakedModel>> loadedModels = new ArrayList<Pair<ModelResourceLocation, IBakedModel>>();
		for(ModelResourceLocation modelLocation : modelRegistry.getKeys()) {
			IBakedModel model = modelRegistry.getObject(modelLocation);
			if(model instanceof IBakedModelDependant) {
				IBakedModelDependant dependant = (IBakedModelDependant) model;
				Collection<ModelResourceLocation> dependencies = dependant.getDependencies(modelLocation);
				Map<ModelResourceLocation, IBakedModel> loadedDependencies = new HashMap<ModelResourceLocation, IBakedModel>();
				for(ModelResourceLocation dependencyLocation : dependencies) {
					IBakedModel bakedModel = modelRegistry.getObject(dependencyLocation);
					if(bakedModel == null) {
						ResourceLocation dependencyLocationNoVariants = new ResourceLocation(dependencyLocation.getResourceDomain(), dependencyLocation.getResourcePath());
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
			modelRegistry.putObject(loadedModel.getKey(), loadedModel.getValue());
		}

		//Replace models
		Set<ModelResourceLocation> keys = modelRegistry.getKeys();
		Map<ModelResourceLocation, IBakedModel> replacementMap = new HashMap<>();
		for(LoaderArgs args : this.loaderArgs) {
			for(ModelResourceLocation loc : keys) {
				IBakedModel replacement = args.getModelReplacement(loc, modelRegistry.getObject(loc));
				if(replacement != null)
					replacementMap.put(loc, replacement);
			}
		}
		List<IBakedModel> toRemove = new ArrayList<IBakedModel>();
		Set<ModelResourceLocation> replacementKeys = replacementMap.keySet();
		for(ModelResourceLocation loc : keys) {
			if(replacementKeys.contains(loc)) {
				IBakedModel bakedModel = modelRegistry.getObject(loc);
				toRemove.add(bakedModel);
			}
		}
		Iterator<IBakedModel> it = modelRegistry.iterator();
		IBakedModel model = null;
		while(it.hasNext()) {
			model = it.next();
			if(toRemove.contains(model))
				it.remove();
		}
		for(Entry<ModelResourceLocation, IBakedModel> replacement : replacementMap.entrySet()) {
			modelRegistry.putObject(replacement.getKey(), replacement.getValue());
		}
	}
}