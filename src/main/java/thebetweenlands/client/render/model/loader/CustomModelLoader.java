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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.render.model.baked.BakedModelItemWrapper;

public class CustomModelLoader implements ICustomModelLoader {
	private static enum MatchResult {
		NORMAL,
		ARGS,
		NONE
	}

	public final CustomModelManager registry;
	private final List<LoaderArgs> loaderArgs = new ArrayList<LoaderArgs>();

	CustomModelLoader(CustomModelManager registry) {
		this.registry = registry;

		//Item model loader args
		this.loaderArgs.add(new LoaderArgs() {
			private IModel dummyModel = null;
			private final Map<ModelResourceLocation, IModel> dummyReplacementMap = new HashMap<>();

			@Override
			public String getName() {
				return "from_item";
			}

			@Override
			public IModel loadModel(IModel original, ResourceLocation location, String[] args) {
				if(args.length != 1)
					this.throwInvalidArgs("Invalid number of arguments");
				ResourceLocation childModel = new ResourceLocation(args[0]);
				this.dummyReplacementMap.put(new ModelResourceLocation(new ResourceLocation(childModel.getResourceDomain(), childModel.getResourcePath()), "inventory"), original);
				return this.getDummyModel();
			}

			@Override
			public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
				IModel replacementModel = this.dummyReplacementMap.get(location);
				if(replacementModel != null) {
					IBakedModel bakedModel = replacementModel.bake(replacementModel.getDefaultState(), DefaultVertexFormats.ITEM, 
							(loc) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()));
					return new BakedModelItemWrapper(original, bakedModel);
				}
				return null;
			}

			private IModel getDummyModel() {
				if(this.dummyModel == null) {
					try {
						this.dummyModel = ModelLoaderRegistry.getModel(new ResourceLocation("thebetweenlands:item/dummy"));
					} catch (Exception ex) {
						throw new RuntimeException("Failed to load dummy item model!", ex);
					}
				}
				return this.dummyModel;
			}
		});
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		//NOOP
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(this.getMatchResult(entry.getKey(), modelLocation).getKey() != MatchResult.NONE)
				return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			Pair<MatchResult, Pair<LoaderArgs, String>> match = this.getMatchResult(entry.getKey(), modelLocation);
			if(match.getKey() == MatchResult.NORMAL) {
				return entry.getValue().apply(modelLocation);
			} else if(match.getKey() == MatchResult.ARGS) {
				LoaderArgs loaderArgs = match.getValue().getKey();
				String loaderParam = match.getValue().getValue();
				System.out.println("LOADER PARAM: " + loaderParam);
				String[] loaderParamsArray = loaderParam.contains(",") ? loaderParam.split(",") : new String[]{ loaderParam };
				return loaderArgs.loadModel(entry.getValue().apply(modelLocation), modelLocation, loaderParamsArray);
			}
		}
		return null;
	}

	private Pair<MatchResult, Pair<LoaderArgs, String>> getMatchResult(ResourceLocation registeredModel, ResourceLocation modelLocation) {
		if(!registeredModel.getResourceDomain().equals(modelLocation.getResourceDomain()))
			return Pair.of(MatchResult.NONE, null);
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
				return loaderArg != null ? Pair.of(MatchResult.ARGS, Pair.of(loaderArg, loaderParam)) : Pair.of(MatchResult.NORMAL, null); 
			}
		}
		return Pair.of(MatchResult.NONE, null);
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
		Map<ModelResourceLocation, IBakedModel> removedChildModels = new HashMap<>();
		Set<ModelResourceLocation> replacementKeys = replacementMap.keySet();
		for(ModelResourceLocation loc : keys) {
			if(replacementKeys.contains(loc)) {
				IBakedModel bakedModel = modelRegistry.getObject(loc);
				toRemove.add(bakedModel);
				removedChildModels.put(loc, bakedModel);
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