package thebetweenlands.client.render.models.block.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

public class BlockModelRegistry {
	public static final BlockModelRegistry INSTANCE = new BlockModelRegistry();

	private final BlockModelLoader loader = new BlockModelLoader(this);

	final Map<ResourceLocation, Function<ResourceLocation, IModel>> registeredModels = new HashMap<ResourceLocation, Function<ResourceLocation, IModel>>();

	public void registerLoader() {
		ModelLoaderRegistry.registerLoader(this.loader);
		MinecraftForge.EVENT_BUS.register(this.loader);
	}

	public void registerModel(ResourceLocation modelLocation, Function<ResourceLocation, IModel> modelGetter) {
		this.registeredModels.put(modelLocation, modelGetter);
	}
}