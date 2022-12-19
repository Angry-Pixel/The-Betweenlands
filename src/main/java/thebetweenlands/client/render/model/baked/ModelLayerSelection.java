package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;

public class ModelLayerSelection implements IModel {
	private IModel model;
	private final boolean[] renderLayer = new boolean[BlockRenderLayer.values().length + 1];

	public ModelLayerSelection() {
	}

	public ModelLayerSelection(IModel model, List<BlockRenderLayer> layers, boolean renderNone) {
		this.model = model;
		for(BlockRenderLayer layer : layers) {
			this.renderLayer[layer.ordinal() + 1] = true;
		}
		if(renderNone) {
			this.renderLayer[0] = true;
		}
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		if (this.model != null)
			dependencies.addAll(this.model.getDependencies());
		return dependencies;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		if (this.model != null)
			textures.addAll(this.model.getTextures());
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		if(this.model != null) {
			IBakedModel bakedModel = this.model.bake(state, format, bakedTextureGetter);
			return new BakedLayerSelectionModel(bakedModel, this.renderLayer);
		} else {
			return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
		}
	}

	@Override
	public IModelState getDefaultState() {
		return this.model.getDefaultState();
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();
		
		IModel baseModel = this.model;
		
		if(customData.containsKey("model")) {
			ResourceLocation baseModelLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("model")), "model"));
			baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find base model for combined model");
		}
		
		if(baseModel == null) {
			baseModel = ModelLoaderRegistry.getMissingModel();
		}
		
		if(customData.containsKey("model_data")) {
			baseModel = baseModel.process(LoaderExtension.parseJsonElementList(parser, customData.get("model_data"), "model_data"));
		}

		List<BlockRenderLayer> renderLayers = new ArrayList<>();
		boolean renderNone = false;
		
		if(!customData.containsKey("layers")) {
			for(int i = 1; i < this.renderLayer.length; i++) {
				if(this.renderLayer[i]) {
					renderLayers.add(BlockRenderLayer.values()[i - 1]);
				}
			}
			renderNone = this.renderLayer[0];
		} else {
			String layers = customData.get("layers");
			JsonArray layersArray = JsonUtils.getJsonArray(parser.parse(layers), "layers");
			for(JsonElement element : layersArray) {
				String layer = element.getAsString();
				switch(layer) {
				case "solid":
					renderLayers.add(BlockRenderLayer.SOLID);
					break;
				case "cutout_mipped":
					renderLayers.add(BlockRenderLayer.CUTOUT_MIPPED);
					break;
				case "cutout":
					renderLayers.add(BlockRenderLayer.CUTOUT);
					break;
				case "translucent":
					renderLayers.add(BlockRenderLayer.TRANSLUCENT);
					break;
				case "none":
					renderNone = true;
					break;
				}
			}
		}

		return new ModelLayerSelection(baseModel, renderLayers, renderNone);
	}

	public static class BakedLayerSelectionModel implements IBakedModel {
		private final IBakedModel model;
		private final boolean[] renderLayer;

		public BakedLayerSelectionModel(IBakedModel model, boolean[] renderLayer) {
			this.model = model;
			this.renderLayer = renderLayer;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
			if((layer == null && this.renderLayer[0]) || (layer != null && this.renderLayer[layer.ordinal() + 1])) {
				return this.model.getQuads(state, side, rand);
			}
			return Collections.emptyList();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return this.model.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return this.model.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return this.model.isBuiltInRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.model.getParticleTexture();
		}

		@SuppressWarnings("deprecation")
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return this.model.getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return this.model.getOverrides();
		}

		@SuppressWarnings("deprecation")
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			Pair<? extends IBakedModel, Matrix4f> result;
			if (this.model instanceof PerspectiveMapWrapper) {
				result = ((PerspectiveMapWrapper) this.model).handlePerspective(cameraTransformType);
			} else
				result = PerspectiveMapWrapper.handlePerspective(this, this.getItemCameraTransforms().getTransform(cameraTransformType), cameraTransformType);
			return Pair.of(this, result.getValue());
		}
	}
}
