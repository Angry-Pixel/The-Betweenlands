package thebetweenlands.client.render.model.loader.extension;

import java.io.IOException;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public abstract class LoaderExtension {
	private IModel dummyModel = null;

	/**
	 * Returns the name of this loader argument
	 * @return
	 */
	public abstract String getName();

	/**
	 * Returns a location with the specified extension and arguments appended.
	 * <p>E.g. someExtension.getLocationWithExtension("domain:path/file", "someArg") -> "domain:path/file$someExtension(someArg)"
	 * @param location
	 * @param arg
	 * @return
	 */
	public final ResourceLocation getLocationWithExtension(ResourceLocation location, String arg) {
		return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + "$" + this.getName() + "(" + arg + ")");
	}

	/**
	 * Returns the model to load based on the specified arguments.
	 * @param original
	 * @param location
	 * @param arg
	 * @return
	 */
	@Nullable
	public abstract IModel loadModel(IModel original, ResourceLocation location, String arg);

	/**
	 * Returns a replacement for the specified resource location and baked model.
	 * <p><b>Do NOT return an {@link IBakedModel} from an {@link IModel} that wasn't obtained through {@link ModelLoaderRegistry#getModel(ResourceLocation)} at some point!</b>
	 * @param location
	 * @param original
	 * @return
	 */
	@Nullable
	public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
		return null;
	}

	/**
	 * Throws a {@link LoaderExtensionException}
	 * @param reason
	 */
	protected final void throwLoaderException(@Nullable String reason) {
		this.throwLoaderException(reason, null);
	}

	/**
	 * Throws a {@link LoaderExtensionException}
	 * @param cause
	 */
	protected final void throwLoaderException(@Nullable Throwable cause) {
		this.throwLoaderException(null, cause);
	}

	/**
	 * Throws a {@link LoaderExtensionException}
	 * @param reason
	 * @param cause
	 */
	protected final void throwLoaderException(@Nullable String reason, @Nullable Throwable cause) {
		if(reason != null)
			reason = String.format("Model loader extension %s failed loading a model. Reason: %s", this.getName(), reason);
		else
			reason = String.format("Model loader extension %s failed loading a model", this.getName());
		throw new LoaderExtensionException(reason, cause);
	}

	/**
	 * Returns a blank item dummy model
	 * @return
	 */
	protected final IModel getItemDummyModel() {
		if(this.dummyModel == null) {
			try {
				this.dummyModel = ModelLoaderRegistry.getModel(new ResourceLocation("thebetweenlands:item/dummy"));
			} catch (Exception ex) {
				this.throwLoaderException("Failed to load dummy item model", ex);
			}
		}
		return this.dummyModel;
	}

	/**
	 * Parses a .mlmeta file
	 * @param parser
	 * @param location
	 * @return
	 */
	protected ImmutableMap<String, String> parseMetadata(JsonParser parser, ResourceLocation location) {
		return parseJsonElementList(parser, this.readMetadata(location), location.toString() + " model metadata");
	}

	/**
	 * Reads the metadata from the specified String.
	 * If the metadata starts with "mlmeta:" the metadata is read from an external
	 * .mlmeta file with the following name after "mlmeta:"
	 * @param metadata
	 * @return
	 */
	protected String readMetadata(String metadata) {
		if(metadata.startsWith("mlmeta:")) {
			//Load metadata from external file
			metadata = this.readMetadata(new ResourceLocation(metadata.substring("mlmeta:".length())));
		}
		return metadata;
	}

	/**
	 * Reads a .mlmeta file
	 * @param file
	 * @return
	 */
	protected String readMetadata(ResourceLocation location) {
		location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mlmeta");
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		try {
			IResource metadataResource = manager.getResource(location);
			return IOUtils.toString(metadataResource.getInputStream());
		} catch (IOException ex) {
			this.throwLoaderException("Failed to load file " + location, ex);
		}
		return "";
	}

	/**
	 * Parses a json string into a map
	 * @param parser
	 * @param json
	 * @param member
	 * @return
	 */
	public static ImmutableMap<String, String> parseJsonElementList(JsonParser parser, String json, String member) {
		return parseJsonElementList(JsonUtils.getJsonObject(parser.parse(json), member));
	}

	/**
	 * Parses a json object into a map
	 * @param parser
	 * @param json
	 * @return
	 */
	public static ImmutableMap<String, String> parseJsonElementList(JsonObject jsonObj) {
		Builder<String, String> parsedElements = ImmutableMap.<String, String>builder();
		for(Entry<String, JsonElement> elementEntry : jsonObj.entrySet()) {
			parsedElements.put(elementEntry.getKey(), elementEntry.getValue().toString());
		}
		return parsedElements.build();
	}
}
