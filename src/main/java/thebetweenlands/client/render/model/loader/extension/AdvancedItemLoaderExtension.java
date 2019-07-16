package thebetweenlands.client.render.model.loader.extension;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import thebetweenlands.client.render.model.baked.BakedModelItemWrapper;
import thebetweenlands.client.render.model.loader.CustomModelLoader;

/**
 * Allows json item models to load non-json models and specify certain properties or provide a .mlmeta file
 */
public class AdvancedItemLoaderExtension extends LoaderExtension {
	private final Map<ModelResourceLocation, ModelContext> modelContexts = new HashMap<>();

	protected final class ModelContext {
		public final ModelResourceLocation source;
		public final ResourceLocation replacement;
		public final String customData;
		public final boolean inheritOverrides;
		public final boolean cacheOverrides;

		/**
		 * Creates a new model context
		 * @param source
		 * @param replacement
		 * @param customData
		 * @param inheritsOverrides
		 * @param cacheOverrides
		 */
		protected ModelContext(ModelResourceLocation source, ResourceLocation replacement, String customData, boolean inheritsOverrides, boolean cacheOverrides) {
			this.source = source;
			this.replacement = replacement;
			this.customData = customData;
			this.inheritOverrides = inheritsOverrides;
			this.cacheOverrides = cacheOverrides;
		}

		protected boolean hasCustomData() {
			return this.customData != null && this.customData.length() > 0;
		}

		protected ImmutableMap<String, String> parseCustomData(JsonParser parser) {
			return parseJsonElementList(parser, this.customData, this.source.toString() + " model metadata");
		}
	}

	@Override
	public String getName() {
		return "from_item_advanced";
	}

	@Override
	public IModel loadModel(IModel original, ResourceLocation location, String arg) {
		ModelContext context = this.parseContextData(location, this.readMetadata(arg));
		this.modelContexts.put(context.source, context);
		this.findReplacementModelAndRegister(location, context);
		return this.getItemDummyModel();
	}

	/**
	 * Parses the specified metadata to a model context
	 * @param location
	 * @param metadata
	 * @return
	 */
	protected ModelContext parseContextData(ResourceLocation location, String metadata) {
		JsonParser parser = new JsonParser();
		ImmutableMap<String, String> dataMap = parseJsonElementList(parser, metadata, location.toString() + " model metadata");

		if(!dataMap.containsKey("source"))
			this.throwLoaderException("Source model was not specified");

		String sourceItem = JsonUtils.getString(parser.parse(dataMap.get("source")), "source");

		String customData = null;
		if(dataMap.containsKey("custom")) {
			customData = dataMap.get("custom");
		}

		boolean inheritOverrides = true;
		if(dataMap.containsKey("inherit_overrides")) {
			try {
				inheritOverrides = JsonUtils.getBoolean(parser.parse(dataMap.get("inherit_overrides")), "inherit_overrides");
			} catch(Exception ex) {
				this.throwLoaderException("Malformed inherit_overrides value. Must be a boolean", ex);
			}
		}

		boolean shouldCacheOverrides = true;
		if(dataMap.containsKey("cache_overrides")) {
			try {
				shouldCacheOverrides = JsonUtils.getBoolean(parser.parse(dataMap.get("cache_overrides")), "cache_overrides");
			} catch(Exception ex) {
				this.throwLoaderException("Malformed cache_overrides value. Must be a boolean", ex);
			}
		}

		ResourceLocation childModel = new ResourceLocation(sourceItem);
		ModelResourceLocation sourceLocation = new ModelResourceLocation(new ResourceLocation(childModel.getNamespace(), childModel.getPath()), "inventory");

		return new ModelContext(sourceLocation, location, customData, inheritOverrides, shouldCacheOverrides);
	}

	@Override
	public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
		ModelContext context = this.modelContexts.get(location);
		if(context != null) {
			IModel replacementModel = this.findReplacementModelAndRegister(location, context);

			//Bake replacement model
			IBakedModel bakedModel = replacementModel.bake(replacementModel.getDefaultState(), DefaultVertexFormats.ITEM, 
					(loc) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()));

			//Return wrapped model
			return new BakedModelItemWrapper(original, bakedModel).setInheritOverrides(context.inheritOverrides).setCacheOverrideModels(context.cacheOverrides);
		}
		//Nothing to replace
		return null;
	}
	
	private IModel findReplacementModelAndRegister(ResourceLocation location, ModelContext context) {
		ResourceLocation replacementModelLocation = context.replacement;

		if(context.hasCustomData()) {
			//Makes the loader process the model with custom data
			replacementModelLocation = CustomModelLoader.MODEL_PROCESSOR_LOADER_EXTENSION.getLocationWithExtension(replacementModelLocation, "{\"custom\": " + context.customData + "}");
		}

		//Retrieve replacement model
		try {
			//Makes sure that the model is loaded through the model loader and that the textures are registered properly
			return ModelLoaderRegistry.getModel(replacementModelLocation);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to load model " + replacementModelLocation + " for child model " + location, ex);
		}
	}
}
