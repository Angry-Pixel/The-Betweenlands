package thebetweenlands.client.render.model.loader.args;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import thebetweenlands.client.render.model.baked.BakedModelItemWrapper;
import thebetweenlands.client.render.model.loader.LoaderExtension;

/**
 * Allows json item models to load non-json models and specify certain properties or provide a metadata file
 * <b>NYI/WIP</b>
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
			return parseJsonElementList(parser, this.customData);
		}
	}

	@Override
	public String getName() {
		return "from_item_advanced";
	}

	@Override
	public IModel loadModel(IModel original, ResourceLocation location, String arg) {
		String metadata = null;

		if(arg.startsWith("metadata:")) {
			//Load metadata from external file
			ResourceLocation metadataLocation = new ResourceLocation(arg.substring("metadata:".length()) + ".mlmeta");
			IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
			try {
				IResource metadataResource = manager.getResource(metadataLocation);
				metadata = IOUtils.toString(metadataResource.getInputStream());
			} catch (IOException ex) {
				this.throwInvalidArgs("Failed to load .mlmeta file", ex);
			}
		} else {
			//Argument is metadata
			metadata = arg;
		}

		ModelContext context = this.parseContextData(location, metadata);
		this.modelContexts.put(context.source, context);

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
		ImmutableMap<String, String> dataMap = parseJsonElementList(parser, metadata);

		if(!dataMap.containsKey("source"))
			this.throwInvalidArgs("Source model was not specified");

		String sourceItem = parser.parse(dataMap.get("source")).getAsString();

		String customData = null;
		if(dataMap.containsKey("custom")) {
			customData = dataMap.get("custom");
		}

		boolean inheritOverrides = true;
		if(dataMap.containsKey("inherit_overrides")) {
			try {
				inheritOverrides = parser.parse(dataMap.get("inherit_overrides")).getAsBoolean();
			} catch(Exception ex) {
				this.throwInvalidArgs("Malformed inherit_overrides value. Must be a boolean", ex);
			}
		}

		boolean shouldCacheOverrides = true;
		if(dataMap.containsKey("cache_overrides")) {
			try {
				shouldCacheOverrides = parser.parse(dataMap.get("cache_overrides")).getAsBoolean();
			} catch(Exception ex) {
				this.throwInvalidArgs("Malformed cached value. Must be a boolean", ex);
			}
		}

		ResourceLocation childModel = new ResourceLocation(sourceItem);
		ModelResourceLocation sourceLocation = new ModelResourceLocation(new ResourceLocation(childModel.getResourceDomain(), childModel.getResourcePath()), "inventory");

		return new ModelContext(sourceLocation, location, customData, inheritOverrides, shouldCacheOverrides);
	}

	private static ImmutableMap<String, String> parseJsonElementList(JsonParser parser, String customData) {
		JsonElement element = parser.parse(customData);
		JsonObject jsonObj = element.getAsJsonObject();
		Builder<String, String> parsedElements = ImmutableMap.<String, String>builder();
		for(Entry<String, JsonElement> elementEntry : jsonObj.entrySet()) {
			parsedElements.put(elementEntry.getKey(), elementEntry.getValue().toString());
		}
		return parsedElements.build();
	}

	@Override
	public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
		ModelContext context = this.modelContexts.get(location);
		if(context != null) {
			ResourceLocation replacementModelLocation = context.replacement;
			ImmutableMap<String, String> customData = context.hasCustomData() ? context.parseCustomData(new JsonParser()) : null;

			//Retrieve replacement model and process with the custom data
			IModel replacementModel;
			try {
				//Makes sure that the model is loaded through the model loader and that the textures are registered properly
				replacementModel = ModelLoaderRegistry.getModel(replacementModelLocation);
				if(customData != null && customData.size() > 0) {
					if(replacementModel instanceof IModelCustomData) {
						//TODO: Load this through a model loader or already load models in #loadModel?
						replacementModel = ((IModelCustomData)replacementModel).process(customData);
					} else 
						throw new RuntimeException("Specified model " + replacementModelLocation + " does not support custom data. Parent loader " + location);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Failed to load model " + replacementModelLocation + " for child model " + location, ex);
			}

			//Bake replacement model
			IBakedModel bakedModel = replacementModel.bake(replacementModel.getDefaultState(), DefaultVertexFormats.ITEM, 
					(loc) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()));

			//Return wrapped model
			return new BakedModelItemWrapper(original, bakedModel).setInheritOverrides(context.inheritOverrides).setCacheOverrideModels(context.cacheOverrides);
		}
		//Nothing to replace
		return null;
	}
}
