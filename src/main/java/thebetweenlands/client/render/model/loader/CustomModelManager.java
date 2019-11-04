package thebetweenlands.client.render.model.loader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Function;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

public enum CustomModelManager {
	INSTANCE;

	private final CustomModelLoader loader = new CustomModelLoader(this);

	private final Map<ResourceLocation, Function<ResourceLocation, IModel>> registeredModelProviders = new HashMap<ResourceLocation, Function<ResourceLocation, IModel>>();

	private CustomModelManager() { }

	/**
	 * Registers the loader
	 */
	public void registerLoader() {
		ModelLoaderRegistry.registerLoader(this.loader);
		MinecraftForge.EVENT_BUS.register(this.loader);
	}

	/**
	 * Returns the loader
	 * @return
	 */
	public CustomModelLoader getLoader() {
		return this.loader;
	}

	/**
	 * Returns an unmodifiable map of all registered model providers
	 * @return
	 */
	public Map<ResourceLocation, Function<ResourceLocation, IModel>> getRegisteredModelProviders() {
		return Collections.unmodifiableMap(this.registeredModelProviders);
	}

	/**
	 * Registers a model getter.
	 * The getter is called when a block is requesting the model with the ModelResourceLocation of that block as parameter.
	 * @param modelLocation
	 * @param modelGetter
	 */
	public void registerModelProvider(@Nonnull ResourceLocation modelLocation, @Nonnull Function<ResourceLocation, IModel> modelGetter) {
		Validate.notNull(modelLocation);
		Validate.notNull(modelGetter);
		this.registeredModelProviders.put(modelLocation, modelGetter);
	}

	/**
	 * Registers a model
	 * @param modelLocation
	 * @param model
	 */
	public void registerModel(@Nonnull ResourceLocation modelLocation, @Nonnull IModel model) {
		Validate.notNull(modelLocation);
		Validate.notNull(model);
		if(model instanceof IModelVariantProvider)
			this.registerModelProvider(modelLocation, (location) -> (((IModelVariantProvider)model).getModelVariant(location)));
		else 
			this.registerModelProvider(modelLocation, (location) -> (model));
	}
}