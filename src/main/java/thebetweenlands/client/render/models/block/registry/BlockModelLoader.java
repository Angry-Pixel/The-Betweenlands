package thebetweenlands.client.render.models.block.registry;

import java.util.Map.Entry;
import java.util.function.Supplier;

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
		for(Entry<ResourceLocation, Supplier<IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(modelLocation.toString().startsWith(entry.getKey().toString()))
				return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		for(Entry<ResourceLocation, Supplier<IModel>> entry : this.registry.registeredModels.entrySet()) {
			if(modelLocation.toString().startsWith(entry.getKey().toString()))
				return entry.getValue().get();
		}
		return null;
	}
}