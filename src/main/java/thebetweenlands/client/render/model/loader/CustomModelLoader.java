package thebetweenlands.client.render.model.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class CustomModelLoader implements ICustomModelLoader {
	public final CustomModelManager registry;

	CustomModelLoader(CustomModelManager registry) {
		this.registry = registry;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		//NOOP
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(this.isMatchingOrVariant(entry.getKey(), modelLocation))
				return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(this.isMatchingOrVariant(entry.getKey(), modelLocation))
				return entry.getValue().apply(modelLocation);
		}
		return null;
	}

	private boolean isMatchingOrVariant(ResourceLocation registeredModel, ResourceLocation modelLocation) {
		if(!registeredModel.getResourceDomain().equals(modelLocation.getResourceDomain()))
			return false;
		String registeredPath = registeredModel.getResourcePath();
		String modelPath = modelLocation.getResourcePath();
		if(modelPath.startsWith(registeredPath)) {
			String suffix = modelPath.substring(registeredPath.length());
			//Only accept if path fully matches or is a variant
			if(suffix.length() == 0 || suffix.startsWith("#"))
				return true;
		}
		return false;
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
	}
}