package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
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

public class ModelEventSelection implements IUnbakedModel {
	private IUnbakedModel baseModel;
	private IUnbakedModel altModel;

	private BooleanSupplier predicate;

	private volatile boolean active;

	public ModelEventSelection() {
		this(null, null);
	}

	public ModelEventSelection(IUnbakedModel baseModel, IUnbakedModel altModel) {
		this.baseModel = baseModel;
		this.altModel = altModel;
		this.predicate = () -> this.isActive();
	}

	public ModelEventSelection(BooleanSupplier predicate, IUnbakedModel baseModel, IUnbakedModel altModel) {
		this.baseModel = baseModel;
		this.altModel = altModel;
		this.predicate = predicate;
	}

	public final synchronized void setActive(boolean active) {
		this.active = active;
	}

	public final synchronized boolean isActive() {
		return this.active;
	}

	@Override
	public Collection<ResourceLocation> getOverrideLocations() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		if (this.baseModel != null)
			dependencies.addAll(this.baseModel.getOverrideLocations());
		if (this.altModel != null)
			dependencies.addAll(this.altModel.getOverrideLocations());
		return dependencies;
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		if (this.baseModel != null)
			textures.addAll(this.baseModel.getTextures(modelGetter, missingTextureErrors));
		if (this.altModel != null)
			textures.addAll(this.altModel.getTextures(modelGetter, missingTextureErrors));
		return textures;
	}

	@Override
	public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter,
			Function<ResourceLocation, TextureAtlasSprite> spriteGetter, IModelState state, boolean uvlock,
			VertexFormat format) {
		if(baseModel != null && altModel != null) {
			IBakedModel baseBakedModel = this.baseModel.bake(modelGetter, spriteGetter, state, uvlock, format);
			IBakedModel altBakedModel = this.altModel.bake(modelGetter, spriteGetter, state, uvlock, format);
			return new BakedEventSelectionModel(this.predicate, baseBakedModel, altBakedModel);
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
			baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find base model for event selection model");
		}

		if(customData.containsKey("model_base_data")) {
			baseModel = baseModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_base_data"), "model_base_data"));
		}

		IUnbakedModel altModel = this.altModel;

		if(customData.containsKey("model_active") || altModel == null) {
			ResourceLocation additionalModelLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("model_active")), "model_active"));
			altModel = ModelLoaderRegistry.getModelOrLogError(additionalModelLocation, "Could not find active model for event selection model");
		}

		if(customData.containsKey("model_active_data")) {
			altModel = altModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_active_data"), "model_active_data"));
		}

		return new ModelEventSelection(this.predicate, baseModel, altModel);
	}

	public static class BakedEventSelectionModel implements IBakedModel {
		private final IBakedModel baseBakedModel;
		private final IBakedModel altBakedModel;
		private final BooleanSupplier isActive;

		public BakedEventSelectionModel(BooleanSupplier isActive, IBakedModel baseBakedModel, IBakedModel altBakedModel) {
			this.baseBakedModel = baseBakedModel;
			this.altBakedModel = altBakedModel;
			this.isActive = isActive;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, Random rand) {
			if(this.isActive.getAsBoolean()) {
				return this.altBakedModel.getQuads(state, side, rand);
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
