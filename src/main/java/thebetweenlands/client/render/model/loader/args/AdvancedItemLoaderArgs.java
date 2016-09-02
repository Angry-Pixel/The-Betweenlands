package thebetweenlands.client.render.model.loader.args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import thebetweenlands.client.render.model.baked.BakedModelItemWrapper;
import thebetweenlands.client.render.model.loader.LoaderArgs;

/**
 * <b>NYI/WIP</b>
 */
public class AdvancedItemLoaderArgs extends LoaderArgs {
	private final Map<ModelResourceLocation, ResourceLocation> dummyReplacementMap = new HashMap<>();
	private final Map<ModelResourceLocation, String> customDataMap = new HashMap<>();
	private final Map<ModelResourceLocation, Boolean> inheritOverridesMap = new HashMap<>();
	private final Map<ModelResourceLocation, Boolean> cacheOverridesMap = new HashMap<>();

	@Override
	public String getName() {
		return "from_item_advanced";
	}

	@Override
	public IModel loadModel(IModel original, ResourceLocation location, String arg) {
		JsonParser parser = new JsonParser();
		ImmutableMap<String, String> dataMap = this.getCustomDataFor(parser, arg);

		if(!dataMap.containsKey("source"))
			this.throwInvalidArgs("Source model was not specified");

		String sourceItem = parser.parse(dataMap.get("source")).getAsString();

		String customData = null;
		if(dataMap.containsKey("custom")) {
			customData = dataMap.get("custom");
		}

		List<ResourceLocation> foundTextures = new ArrayList<ResourceLocation>();
		if(dataMap.containsKey("textures")) {
			String texturesToLoad = dataMap.get("textures");
			if(texturesToLoad != null && texturesToLoad.length() > 0) {
				try {
					JsonArray textureJsonArray = (JsonArray) parser.parse(texturesToLoad);
					Iterator<JsonElement> elementIT = textureJsonArray.iterator();
					while(elementIT.hasNext()) {
						JsonElement textureElement = elementIT.next();
						String textureLocation = textureElement.getAsString();
						foundTextures.add(new ResourceLocation(textureLocation));
					}
				} catch(Exception ex) {
					this.throwInvalidArgs("Malformed texture array", ex);
				}
			}
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
		ModelResourceLocation dummyLocation = new ModelResourceLocation(new ResourceLocation(childModel.getResourceDomain(), childModel.getResourcePath()), "inventory");

		this.dummyReplacementMap.put(dummyLocation, location);
		this.customDataMap.put(dummyLocation, customData);
		this.inheritOverridesMap.put(dummyLocation, inheritOverrides);
		this.cacheOverridesMap.put(dummyLocation, shouldCacheOverrides);

		return this.getDummyModel(foundTextures);
	}

	@Override
	public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
		ResourceLocation replacementModelLocation = this.dummyReplacementMap.get(location);
		if(replacementModelLocation != null) {
			ImmutableMap<String, String> customData = this.customDataMap.containsKey(location) ? this.getCustomDataFor(new JsonParser(), this.customDataMap.get(location)) : null;

			boolean inheritOverrides = true;
			if(this.inheritOverridesMap.containsKey(location))
				inheritOverrides = this.inheritOverridesMap.get(location);

			boolean shouldCacheOverrides = true;
			if(this.cacheOverridesMap.containsKey(location))
				shouldCacheOverrides = this.cacheOverridesMap.get(location);

			//Retrieve replacement model and process with the custom data
			IModel replacementModel;
			try {
				//Makes sure that the model is loaded through the model loader and that the textures are registered properly
				replacementModel = ModelLoaderRegistry.getModel(replacementModelLocation);
				if(customData != null && customData.size() > 0) {
					if(replacementModel instanceof IModelCustomData) {
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
			return new BakedModelItemWrapper(original, bakedModel).setInheritOverrides(inheritOverrides).setCacheOverrideModels(shouldCacheOverrides);
		}
		//Nothing to replace
		return null;
	}

	private ImmutableMap<String, String> getCustomDataFor(JsonParser parser, String customData) {
		JsonElement element = parser.parse(customData);
		JsonObject jsonObj = element.getAsJsonObject();
		Builder<String, String> parsedElements = ImmutableMap.<String, String>builder();
		for(Entry<String, JsonElement> elementEntry : jsonObj.entrySet()) {
			parsedElements.put(elementEntry.getKey(), elementEntry.getValue().toString());
		}
		return parsedElements.build();
	}

	private IModel getDummyModel(List<ResourceLocation> texturesToLoad) {
		//TODO: Implement this at some point. Make it load the through a model loader
		//And then #process or #retexture the model so that it uses the passed in textures
		//return new ModelBlank(texturesToLoad);
		try {
			return ModelLoaderRegistry.getModel(new ResourceLocation("thebetweenlands:item/dummy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
