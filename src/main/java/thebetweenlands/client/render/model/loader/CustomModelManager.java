package thebetweenlands.client.render.model.loader;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

public class CustomModelManager {
	public static final CustomModelManager INSTANCE = new CustomModelManager();

	private final CustomModelLoader loader = new CustomModelLoader(this);

	final Map<ResourceLocation, Function<ResourceLocation, IModel>> registeredModels = new HashMap<ResourceLocation, Function<ResourceLocation, IModel>>();

	public void registerLoader() {
		ModelLoaderRegistry.registerLoader(this.loader);
		MinecraftForge.EVENT_BUS.register(this.loader);
	}

	/**
	 * Registers a model getter.
	 * The getter is called when a block is requesting the model with the ModelResourceLocation of that block as parameter.
	 * @param modelLocation
	 * @param modelGetter
	 */
	private void registerModel(ResourceLocation modelLocation, Function<ResourceLocation, IModel> modelGetter) {
		this.registeredModels.put(modelLocation, modelGetter);
	}

	/**
	 * Registers a model
	 * @param modelLocation
	 * @param model
	 */
	public void registerModel(ResourceLocation modelLocation, IModel model) {
		if(model instanceof IModelVariantProvider)
			this.registerModel(modelLocation, (location) -> (((IModelVariantProvider)model).getModelVariant(location)));
		else 
			this.registerModel(modelLocation, (location) -> (model));
	}
}