package thebetweenlands.client.render.model.loader.extension;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

/**
 * Loads a model and processes it with custom data if applicable
 */
public class ModelProcessorLoaderExtension extends LoaderExtension {
	@Override
	public String getName() {
		return "process_model";
	}

	@Override
	public IUnbakedModel loadModel(IUnbakedModel original, ResourceLocation location, String arg) {
		IUnbakedModel processedModel = original;
		String metadata = this.readMetadata(arg);
		JsonParser parser = new JsonParser();
		JsonObject json = JsonUtils.getJsonObject(parser.parse(metadata), location.toString() + " model metadata");

		if(json.has("custom")) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support custom data");
			}
			ImmutableMap<String, String> dataMap = parseJsonElementList(JsonUtils.getJsonObject(json.get("custom"), "custom"));
			processedModel = processedModel.process(dataMap);
		}

		if(json.has("smooth_lighting")) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support smooth lighting");
			}
			processedModel = processedModel.smoothLighting(JsonUtils.getBoolean(json.get("smooth_lighting"), "smooth_lighting"));
		}

		if(json.has("gui3d")) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support gui3d");
			}
			processedModel = processedModel.gui3d(JsonUtils.getBoolean(json.get("gui3d"), "gui3d"));
		}

		//TODO 1.13 Model baking uvlock can no longer be done in post
		/*if(json.has("uvlock")) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support uvlock");
			}
			processedModel = processedModel.uvlock(JsonUtils.getBoolean(json.get("uvlock"), "uvlock"));
		}*/

		if(json.has("textures")) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support retexturing");
			}
			ImmutableMap<String, String> dataMap = parseJsonElementList(JsonUtils.getJsonObject(json.get("textures"), "textures"));
			processedModel = processedModel.retexture(dataMap);
		}
		return processedModel;
	}
}
