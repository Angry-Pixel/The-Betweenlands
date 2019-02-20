package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;

public class ModelCombined implements IUnbakedModel {
	private IUnbakedModel baseModel;
	private IUnbakedModel additionalModel;

	public ModelCombined() {
	}

	public ModelCombined(IUnbakedModel baseModel, IUnbakedModel additionalModel) {
		this.baseModel = baseModel;
		this.additionalModel = additionalModel;
	}
	
	@Override
	public Collection<ResourceLocation> getOverrideLocations() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		if (this.baseModel != null)
			dependencies.addAll(this.baseModel.getOverrideLocations());
		if (this.additionalModel != null)
			dependencies.addAll(this.additionalModel.getOverrideLocations());
		return dependencies;
	}
	
	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		if (this.baseModel != null)
			textures.addAll(this.baseModel.getTextures(modelGetter, missingTextureErrors));
		if (this.additionalModel != null)
			textures.addAll(this.additionalModel.getTextures(modelGetter, missingTextureErrors));
		return textures;
	}
	
	@Override
	public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter,
			Function<ResourceLocation, TextureAtlasSprite> spriteGetter, IModelState state, boolean uvlock,
			VertexFormat format) {
		if(baseModel != null && additionalModel != null) {
			IBakedModel baseBakedModel = this.baseModel.bake(modelGetter, spriteGetter, state, uvlock, format);
			IBakedModel additionalBakedModel = this.additionalModel.bake(modelGetter, spriteGetter, state, uvlock, format);
			return new BakedCombinedModel(baseBakedModel, additionalBakedModel);
		} else {
			return ModelLoaderRegistry.getMissingModel().bake(modelGetter, spriteGetter, state, uvlock, format);
		}
	}

	@Override
	public IModelState getDefaultState() {
		return this.baseModel.getDefaultState();
	}

	@Override
	public IUnbakedModel process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		IUnbakedModel baseModel = this.baseModel;
		
		if(customData.containsKey("model_base") || baseModel == null) {
			ResourceLocation baseModelLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("model_base")), "model_base"));
			baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find base model for combined model");
		}

		if(customData.containsKey("model_base_data")) {
			baseModel = baseModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_base_data"), "model_base_data"));
		}

		IUnbakedModel additionalModel = this.additionalModel;

		if(customData.containsKey("model_additional") || additionalModel == null) {
			ResourceLocation additionalModelLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("model_additional")), "model_additional"));
			additionalModel = ModelLoaderRegistry.getModelOrLogError(additionalModelLocation, "Could not find additional model for combined model");
		}

		if(customData.containsKey("model_additional_data")) {
			additionalModel = additionalModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_additional_data"), "model_additional_data"));
		}

		return new ModelCombined(baseModel, additionalModel);
	}

	public static class BakedCombinedModel implements IBakedModel {
		private final IBakedModel baseBakedModel;
		private final IBakedModel additionalBakedModel;

		public BakedCombinedModel(IBakedModel baseBakedModel, IBakedModel additionalBakedModel) {
			this.baseBakedModel = baseBakedModel;
			this.additionalBakedModel = additionalBakedModel;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, Random rand) {
			List<BakedQuad> quads = new ArrayList<BakedQuad>();
			quads.addAll(this.baseBakedModel.getQuads(state, side, rand));
			quads.addAll(this.additionalBakedModel.getQuads(state, side, rand));
			return quads;
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
