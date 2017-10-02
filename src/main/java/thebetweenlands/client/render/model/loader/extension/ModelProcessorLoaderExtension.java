package thebetweenlands.client.render.model.loader.extension;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;

/**
 * Loads a model and processes it with custom data if applicable
 */
public class ModelProcessorLoaderExtension extends LoaderExtension {
	@Override
	public String getName() {
		return "process_model";
	}

	@Override
	public IModel loadModel(IModel original, ResourceLocation location, String arg) {
		IModel processedModel = original;
		String metadata = this.readMetadata(arg);
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(metadata).getAsJsonObject();

		if(json.has("custom") && json.get("custom").isJsonObject()) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support custom data");
			}
			ImmutableMap<String, String> dataMap = parseJsonElementList(json.get("custom").getAsJsonObject());
			processedModel = processedModel.process(dataMap);
		}

		if(json.has("smooth_lighting") && json.get("smooth_lighting").isJsonPrimitive()) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support smooth lighting");
			}
			processedModel = processedModel.smoothLighting(json.get("smooth_lighting").getAsBoolean());
		}

		if(json.has("gui3d") && json.get("gui3d").isJsonPrimitive()) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support gui3d");
			}
			processedModel = processedModel.gui3d(json.get("gui3d").getAsBoolean());
		}

		if(json.has("uvlock") && json.get("uvlock").isJsonPrimitive()) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support uvlock");
			}
			processedModel = processedModel.uvlock(json.get("uvlock").getAsBoolean());
		}

		if(json.has("textures") && json.get("textures").isJsonObject()) {
			if(original == null) {
				this.throwLoaderException("Specified model " + location + " does not support retexturing");
			}
			ImmutableMap<String, String> dataMap = parseJsonElementList(json.get("textures").getAsJsonObject());
			processedModel = processedModel.retexture(dataMap);
		}
		return processedModel;
	}
}
