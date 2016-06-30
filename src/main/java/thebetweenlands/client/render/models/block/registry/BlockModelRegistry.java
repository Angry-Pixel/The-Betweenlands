package thebetweenlands.client.render.models.block.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class BlockModelRegistry {
	public static final BlockModelRegistry INSTANCE = new BlockModelRegistry();

	private final BlockModelLoader loader = new BlockModelLoader(this);

	final Map<ResourceLocation, Supplier<IModel>> registeredModels = new HashMap<ResourceLocation, Supplier<IModel>>();

	public void registerLoader() {
		ModelLoaderRegistry.registerLoader(this.loader);
	}

	public void registerModel(ResourceLocation modelLocation, Supplier<IModel> modelSupplier) {
		this.registeredModels.put(modelLocation, modelSupplier);
	}
}