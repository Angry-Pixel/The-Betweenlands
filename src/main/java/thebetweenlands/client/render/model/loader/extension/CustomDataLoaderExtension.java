package thebetweenlands.client.render.model.loader.extension;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;

/**
 * Loads a model and processes it with custom data if applicable
 */
public class CustomDataLoaderExtension extends LoaderExtension {
	@Override
	public String getName() {
		return "custom_data";
	}

	@Override
	public IModel loadModel(IModel original, ResourceLocation location, String arg) {
		if(original instanceof IModelCustomData == false)
			this.throwLoaderException("Specified model " + location + " does not support custom data");

		String metadata = this.readMetadata(arg);
		JsonParser parser = new JsonParser();
		ImmutableMap<String, String> dataMap = parseJsonElementList(parser, metadata);
		return ((IModelCustomData)original).process(dataMap);
	}
}
