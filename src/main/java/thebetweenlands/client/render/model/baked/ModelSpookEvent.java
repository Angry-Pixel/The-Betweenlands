package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;

public class ModelSpookEvent implements IModel {
	private IModel baseModel;
	private IModel altModel;

	public ModelSpookEvent() {
	}

	public ModelSpookEvent(IModel baseModel, IModel altModel) {
		this.baseModel = baseModel;
		this.altModel = altModel;
	}

	public static ImmutableMap<String, String> getCustomDataFor(JsonParser parser, String customData) {
		if (customData == null)
			return null;
		JsonElement element = parser.parse(customData);
		JsonObject jsonObj = element.getAsJsonObject();
		Builder<String, String> parsedElements = ImmutableMap.<String, String>builder();
		for (Entry<String, JsonElement> elementEntry : jsonObj.entrySet()) {
			parsedElements.put(elementEntry.getKey(), elementEntry.getValue().toString());
		}
		return parsedElements.build();
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		if (this.baseModel != null)
			dependencies.addAll(this.baseModel.getDependencies());
		if (this.altModel != null)
			dependencies.addAll(this.altModel.getDependencies());
		return dependencies;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		if (this.baseModel != null)
			textures.addAll(this.baseModel.getTextures());
		if (this.altModel != null)
			textures.addAll(this.altModel.getTextures());
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		if(baseModel != null && altModel != null) {
			IBakedModel baseBakedModel = this.baseModel.bake(state, format, bakedTextureGetter);
			IBakedModel altBakedModel = this.altModel.bake(state, format, bakedTextureGetter);
			return new BakedSpookEventModel(baseBakedModel, altBakedModel);
		} else {
			return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
		}
	}

	@Override
	public IModelState getDefaultState() {
		return this.baseModel.getDefaultState();
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		if (!customData.containsKey("model_base") || !customData.containsKey("model_spook")) return this;

		JsonParser parser = new JsonParser();
		String baseJsonStr = customData.get("model_base");
		String altJsonStr = customData.get("model_spook");
		ResourceLocation baseModelLocation = new ResourceLocation(parser.parse(baseJsonStr).getAsString());
		ResourceLocation altModelLocation = new ResourceLocation(parser.parse(altJsonStr).getAsString());

		IModel baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find base model for combined model");
		baseModel = baseModel.process(getCustomDataFor(parser, customData.get("model_base_data")));
		IModel altModel = ModelLoaderRegistry.getModelOrLogError(altModelLocation, "Could not find spook event model for combined model");
		altModel = altModel.process(getCustomDataFor(parser, customData.get("model_spook_data")));

		return new ModelSpookEvent(baseModel, altModel);
	}

	public static class BakedSpookEventModel implements IBakedModel {
		private final IBakedModel baseBakedModel;
		private final IBakedModel altBakedModel;

		public BakedSpookEventModel(IBakedModel baseBakedModel, IBakedModel altBakedModel) {
			this.baseBakedModel = baseBakedModel;
			this.altBakedModel = altBakedModel;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			Minecraft mc = Minecraft.getMinecraft();
			if(mc != null && mc.world != null && mc.world.provider instanceof WorldProviderBetweenlands) {
				EnvironmentEventRegistry eeRegistry = ((WorldProviderBetweenlands)mc.world.provider).getWorldData().getEnvironmentEventRegistry();
				if(eeRegistry.SPOOPY.isActive()) {
					return this.altBakedModel.getQuads(state, side, rand);
				}
			}
			return this.baseBakedModel.getQuads(state, side, rand);
		}

		@Override
		public boolean isAmbientOcclusion() {
			return this.baseBakedModel.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return this.baseBakedModel.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return this.baseBakedModel.isBuiltInRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.baseBakedModel.getParticleTexture();
		}

		@SuppressWarnings("deprecation")
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return this.baseBakedModel.getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return this.baseBakedModel.getOverrides();
		}

		@SuppressWarnings("deprecation")
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			Pair<? extends IBakedModel, Matrix4f> result;
			if (this.baseBakedModel instanceof PerspectiveMapWrapper) {
				result = ((PerspectiveMapWrapper) this.baseBakedModel).handlePerspective(cameraTransformType);
			} else
				result = PerspectiveMapWrapper.handlePerspective(this, this.getItemCameraTransforms().getTransform(cameraTransformType), cameraTransformType);
			return Pair.of(this, result.getValue());
		}
	}
}
