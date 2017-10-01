package thebetweenlands.client.render.model.baked;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;
import thebetweenlands.common.registries.ModelRegistry;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

public class ModelCombined implements IModel {
    private IModel baseModel;
    private IModel additionalModel;

    public ModelCombined() {
    }

    public ModelCombined(IModel baseModel, IModel additionalModel) {
        this.baseModel = baseModel;
        this.additionalModel = additionalModel;
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
        if (this.additionalModel != null)
            dependencies.addAll(this.additionalModel.getDependencies());
        return dependencies;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
        if (this.baseModel != null)
            textures.addAll(this.baseModel.getTextures());
        if (this.additionalModel != null)
            textures.addAll(this.additionalModel.getTextures());
        return textures;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		if(baseModel != null && additionalModel != null) {
            IBakedModel baseBakedModel = this.baseModel.bake(state, format, bakedTextureGetter);
            IBakedModel additionalBakedModel = this.additionalModel.bake(state, format, bakedTextureGetter);
            return new BakedCombinedModel(baseBakedModel, additionalBakedModel);
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
        if (!customData.containsKey("model_base") || !customData.containsKey("model_additional")) return this;

        JsonParser parser = new JsonParser();
        String baseJsonStr = customData.get("model_base");
        String additionalJsonStr = customData.get("model_additional");
        ResourceLocation baseModelLocation = new ResourceLocation(parser.parse(baseJsonStr).getAsString());
        ResourceLocation additionalModelLocation = new ResourceLocation(parser.parse(additionalJsonStr).getAsString());

        IModel baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Could not find base model for combined model");
        baseModel = baseModel.process(getCustomDataFor(parser, customData.get("model_base_data")));
        IModel additionalModel = ModelLoaderRegistry.getModelOrLogError(additionalModelLocation, "Could not find additional model for combined model");
        additionalModel = additionalModel.process(getCustomDataFor(parser, customData.get("model_additional_data")));

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
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
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
