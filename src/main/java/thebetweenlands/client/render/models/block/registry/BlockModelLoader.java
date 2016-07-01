package thebetweenlands.client.render.models.block.registry;

import java.util.Map.Entry;
import java.util.function.Function;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class BlockModelLoader implements ICustomModelLoader {
	public final BlockModelRegistry registry;

	BlockModelLoader(BlockModelRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		//NOOP
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(this.isMatching(entry.getKey(), modelLocation))
				return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(this.isMatching(entry.getKey(), modelLocation))
				return entry.getValue().apply(modelLocation);
		}
		return null;
	}

	private boolean isMatching(ResourceLocation registeredModel, ResourceLocation modelLocation) {
		return registeredModel.getResourceDomain().equals(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().startsWith(registeredModel.getResourcePath());
	}
}