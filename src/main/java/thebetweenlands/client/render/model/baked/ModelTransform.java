package thebetweenlands.client.render.model.baked;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;

public class ModelTransform implements IModel {
	private static final Gson GSON = (new GsonBuilder())
			.registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
			.create();

	private final IModel model;
	private final TRSRTransformation transform;

	public ModelTransform(IModel model, TRSRTransformation transform) {
		this.model = model;
		this.transform = transform;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		IModelState stateWrapper = (part) -> {
			Optional<TRSRTransformation> transformation = state.apply(part);

			if(!part.isPresent()) {
				if(transformation.isPresent()) {
					transformation = Optional.of(transformation.get().compose(TRSRTransformation.blockCenterToCorner(this.transform)));
				} else {
					transformation = Optional.of(TRSRTransformation.blockCenterToCorner(this.transform));
				}
			}

			return transformation;
		};

		return this.model.bake(stateWrapper, format, bakedTextureGetter);
	}

	@Override
	public Optional<? extends IClip> getClip(String name) {
		return this.model.getClip(name);
	}

	@Override
	public IModelState getDefaultState() {
		return this.model.getDefaultState();
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return this.model.getDependencies();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return this.model.getTextures();
	}

	@Override
	public IModel gui3d(boolean value) {
		return new ModelTransform(this.model.gui3d(value), this.transform);
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		TRSRTransformation transform = this.transform;

		String transformJsonStr = customData.get("trsr_transformation");
		if(transformJsonStr != null) {
			transform = GSON.fromJson(transformJsonStr, TRSRTransformation.class);
		}

		JsonParser parser = new JsonParser();

		IModel transformedModel = this.model;

		if(customData.containsKey("transformed_model") || transformedModel == null) {
			ResourceLocation baseModelLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("transformed_model")), "transformed_model"));
			transformedModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find model for transform model");
		}

		if(customData.containsKey("model_data")) {
			transformedModel = transformedModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_data"), "model_data"));
		}

		return new ModelTransform(transformedModel, transform);
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		return new ModelTransform(this.model.retexture(textures), this.transform);
	}

	@Override
	public IModel smoothLighting(boolean value) {
		return new ModelTransform(this.model.smoothLighting(value), this.transform);
	}

	@Override
	public IModel uvlock(boolean value) {
		return new ModelTransform(this.model.uvlock(value), this.transform);
	}
}
