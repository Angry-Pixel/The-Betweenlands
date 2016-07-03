package thebetweenlands.client.render.models.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class ModelCombined implements IModel {

	public final IModel baseModel;
	public final IModel additionalModel;

	public ModelCombined(IModel baseModel, IModel additionalModel) {
		this.baseModel = baseModel;
		this.additionalModel = additionalModel;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		dependencies.addAll(this.baseModel.getDependencies());
		dependencies.addAll(this.additionalModel.getDependencies());
		return dependencies;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		textures.addAll(this.baseModel.getTextures());
		textures.addAll(this.additionalModel.getTextures());
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		IBakedModel baseBakedModel = this.baseModel.bake(state, format, bakedTextureGetter);
		IBakedModel additionalBakedModel = this.additionalModel.bake(state, format, bakedTextureGetter);
		return new ModelBakedLifeCrystalOre(baseBakedModel, additionalBakedModel);
	}

	@Override
	public IModelState getDefaultState() {
		return this.baseModel.getDefaultState();
	}

	public static class ModelBakedLifeCrystalOre implements IBakedModel {
		private final IBakedModel baseBakedModel;
		private final IBakedModel additionalBakedModel;

		public ModelBakedLifeCrystalOre(IBakedModel baseBakedModel, IBakedModel additionalBakedModel) {
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

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return this.baseBakedModel.getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return this.baseBakedModel.getOverrides();
		}
	}
}
