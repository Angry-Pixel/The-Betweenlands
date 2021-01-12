package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelRenderer;
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
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import thebetweenlands.client.render.model.baked.ModelFromModelBase.IVertexProcessor;
import thebetweenlands.client.render.model.baked.modelbase.ModelMudBricksAlcove;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Model;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.StatePropertyHelper;
import thebetweenlands.util.TexturePacker;
import thebetweenlands.util.Vec3UV;

public class ModelAlcove implements IModel {
	private static final ModelMudBricksAlcove ALCOVE_MODEL_BASE = new ModelMudBricksAlcove();

	private static final class PredicateVertexProcessor implements IVertexProcessor {
		private final Predicate<ModelRenderer> partPredicate;

		private PredicateVertexProcessor(Predicate<ModelRenderer> partPredicate) {
			this.partPredicate = partPredicate;
		}

		@Override
		public Vec3UV process(Vec3UV vertexIn, Model model, Quad quad, Box box, QuadBuilder builder) {
			return this.partPredicate.test(box.getModelRenderer()) ? vertexIn : null;
		}
	}

	private static final Predicate<ModelRenderer> OUTCROP_PREDICATE = part -> {
		return part == ALCOVE_MODEL_BASE.outcrop_a || part == ALCOVE_MODEL_BASE.outcrop_b;
	};

	private static final Predicate<ModelRenderer> TOP_COBWEB_PREDICATE = part -> {
		return part == ALCOVE_MODEL_BASE.cobweb1 || part == ALCOVE_MODEL_BASE.cobweb2 || part == ALCOVE_MODEL_BASE.cobweb2b;
	};

	private static final Predicate<ModelRenderer> BOTTOM_COBWEB_PREDICATE = part -> {
		return part == ALCOVE_MODEL_BASE.cobweb2 || part == ALCOVE_MODEL_BASE.cobweb2b;
	};

	private static final Predicate<ModelRenderer> SMALL_CANDLE_PREDICATE = part -> {
		return part == ALCOVE_MODEL_BASE.candle2 || part == ALCOVE_MODEL_BASE.wicker2 || part == ALCOVE_MODEL_BASE.drip_texture2;
	};

	private static final Predicate<ModelRenderer> BIG_CANDLE_PREDICATE = part -> {
		return part == ALCOVE_MODEL_BASE.candle1 || part == ALCOVE_MODEL_BASE.wicker1 || part == ALCOVE_MODEL_BASE.drip_texture1;
	};

	private static final Predicate<ModelRenderer> BASE_PREDICATE = part -> {
		return !OUTCROP_PREDICATE.test(part) && !TOP_COBWEB_PREDICATE.test(part) && !BOTTOM_COBWEB_PREDICATE.test(part) && !SMALL_CANDLE_PREDICATE.test(part) && !BIG_CANDLE_PREDICATE.test(part);
	};

	private final ModelFromModelBase[] baseModel;
	private final ModelFromModelBase[] topCobwebModel;
	private final ModelFromModelBase[] bottomCobwebModel;
	private final ModelFromModelBase[] smallCandleModel;
	private final ModelFromModelBase[] bigCandleModel;

	private final ResourceLocation particleTexture;

	public ModelAlcove(TexturePacker packer, ResourceLocation[] baseTextures, ResourceLocation particleTexture, int width, int height) {
		this.baseModel = new ModelFromModelBase[baseTextures.length];
		this.topCobwebModel = new ModelFromModelBase[baseTextures.length];
		this.bottomCobwebModel = new ModelFromModelBase[baseTextures.length];
		this.smallCandleModel = new ModelFromModelBase[baseTextures.length];
		this.bigCandleModel = new ModelFromModelBase[baseTextures.length];

		for(int i = 0; i < baseTextures.length; i++) {
			ModelFromModelBase baseModel = this.baseModel[i] = new ModelFromModelBase.Builder(ALCOVE_MODEL_BASE, baseTextures[i], width, height).packer(packer).particleTexture(particleTexture).processor(new PredicateVertexProcessor(BASE_PREDICATE)).doubleFace(false).build();

			this.topCobwebModel[i] = new ModelFromModelBase(baseModel, particleTexture, width, height, new PredicateVertexProcessor(TOP_COBWEB_PREDICATE), baseModel.doubleFace, baseModel.ambientOcclusion);
			this.bottomCobwebModel[i] = new ModelFromModelBase(baseModel, particleTexture, width, height, new PredicateVertexProcessor(BOTTOM_COBWEB_PREDICATE), baseModel.doubleFace, baseModel.ambientOcclusion);
			this.smallCandleModel[i] = new ModelFromModelBase(baseModel, particleTexture, width, height, new PredicateVertexProcessor(SMALL_CANDLE_PREDICATE), baseModel.doubleFace, baseModel.ambientOcclusion);
			this.bigCandleModel[i] = new ModelFromModelBase(baseModel, particleTexture, width, height, new PredicateVertexProcessor(BIG_CANDLE_PREDICATE), baseModel.doubleFace, baseModel.ambientOcclusion);
		}

		this.particleTexture = particleTexture;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		ImmutableSet.Builder<ResourceLocation> textures = ImmutableSet.builder();
		for(int i = 0; i < this.baseModel.length; i++) {
			textures.addAll(this.baseModel[i].getTextures());
			textures.addAll(this.topCobwebModel[i].getTextures());
			textures.addAll(this.bottomCobwebModel[i].getTextures());
			textures.addAll(this.smallCandleModel[i].getTextures());
			textures.addAll(this.bigCandleModel[i].getTextures());
		}
		return textures.build();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		IBakedModel[] bakedBaseModel = new IBakedModel[this.baseModel.length];
		IBakedModel[] bakedTopCobwedModel = new IBakedModel[this.baseModel.length];
		IBakedModel[] bakedBottomCobwebModel = new IBakedModel[this.baseModel.length];
		IBakedModel[] bakedSmallCandleModel = new IBakedModel[this.baseModel.length];
		IBakedModel[] bakedBigCandleModel = new IBakedModel[this.baseModel.length];

		for(int i = 0; i < this.baseModel.length; i++) {
			bakedBaseModel[i] = this.baseModel[i].bake(state, format, bakedTextureGetter);
			bakedTopCobwedModel[i] = this.topCobwebModel[i].bake(state, format, bakedTextureGetter);
			bakedBottomCobwebModel[i] = this.bottomCobwebModel[i].bake(state, format, bakedTextureGetter);
			bakedSmallCandleModel[i] = this.smallCandleModel[i].bake(state, format, bakedTextureGetter);
			bakedBigCandleModel[i] = this.bigCandleModel[i].bake(state, format, bakedTextureGetter);
		}
		
		return new ModelBakedAlcove(PerspectiveMapWrapper.getTransforms(state), bakedBaseModel, bakedTopCobwedModel, bakedBottomCobwebModel, bakedSmallCandleModel, bakedBigCandleModel, bakedTextureGetter.apply(this.particleTexture));
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class ModelBakedAlcove implements IBakedModel {
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		
		private final IBakedModel[] bakedBaseModel;
		private final IBakedModel[] bakedTopCobwebModel;
		private final IBakedModel[] bakedBottomCobwebModel;
		private final IBakedModel[] bakedSmallCandleModel;
		private final IBakedModel[] bakedBigCandleModel;

		private final TextureAtlasSprite particleTexture;

		private ModelBakedAlcove(ImmutableMap<TransformType, TRSRTransformation> transforms, IBakedModel[] bakedBaseModel, IBakedModel[] bakedTopCobwedModel, IBakedModel[] bakedBottomCobwebMode,
				IBakedModel[] bakedSmallCandleModel, IBakedModel[] bakedBigCandleModel, TextureAtlasSprite particleTexture) {
			this.transforms = transforms;
			this.bakedBaseModel = bakedBaseModel;
			this.bakedTopCobwebModel = bakedTopCobwedModel;
			this.bakedBottomCobwebModel = bakedBottomCobwebMode;
			this.bakedSmallCandleModel = bakedSmallCandleModel;
			this.bakedBigCandleModel = bakedBigCandleModel;
			this.particleTexture = particleTexture;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if(side == null) {
				int level = StatePropertyHelper.getPropertyOptional(state, BlockMudBrickAlcove.LEVEL).orElse(0);

				//wtf
				if(level <= 2) level = 0;
				if(level == 3) level = 1;
				if(level == 4) level = 2;
				if(level == 5) level = 3;
				if(level >= 6) level = 4;

				level = Math.min(this.bakedBaseModel.length - 1, Math.max(0, level));

				List<BakedQuad> quads = new ArrayList<>(this.bakedBaseModel[level].getQuads(state, side, rand));

				if(StatePropertyHelper.getPropertyOptional(state, BlockMudBrickAlcove.TOP_COBWEB).orElse(true)) {
					quads.addAll(this.bakedTopCobwebModel[level].getQuads(state, side, rand));
				}

				if(StatePropertyHelper.getPropertyOptional(state, BlockMudBrickAlcove.BOTTOM_COBWEB).orElse(true)) {
					quads.addAll(this.bakedBottomCobwebModel[level].getQuads(state, side, rand));
				}

				if(StatePropertyHelper.getPropertyOptional(state, BlockMudBrickAlcove.SMALL_CANDLE).orElse(true)) {
					quads.addAll(this.bakedSmallCandleModel[level].getQuads(state, side, rand));
				}

				if(StatePropertyHelper.getPropertyOptional(state, BlockMudBrickAlcove.BIG_CANDLE).orElse(true)) {
					quads.addAll(this.bakedBigCandleModel[level].getQuads(state, side, rand));
				}

				return quads;
			} else {
				return ImmutableList.of();
			}
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.particleTexture;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}
		
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
			return PerspectiveMapWrapper.handlePerspective(this, this.transforms, type);
		}
	}
}