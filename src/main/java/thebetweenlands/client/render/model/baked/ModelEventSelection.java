package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;

public class ModelEventSelection implements IModel {
	private IModel baseModel;
	private IModel altModel;

	private BooleanSupplier predicate;

	private volatile boolean active;

	public ModelEventSelection() {
		this(null, null);
	}
	
	public ModelEventSelection(IModel baseModel, IModel altModel) {
		this.baseModel = baseModel;
		this.altModel = altModel;
		this.predicate = () -> this.isActive();
	}

	public ModelEventSelection(BooleanSupplier predicate, IModel baseModel, IModel altModel) {
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
			return new BakedEventSelectionModel(this.predicate, baseBakedModel, altBakedModel);
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
		JsonParser parser = new JsonParser();

		IModel baseModel = this.baseModel;

		if(customData.containsKey("model_base") || baseModel == null) {
			ResourceLocation baseModelLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("model_base")), "model_base"));
			baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find base model for event selection model");
		}

		if(customData.containsKey("model_base_data")) {
			baseModel = baseModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_base_data"), "model_base_data"));
		}

		IModel altModel = this.altModel;

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
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
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
