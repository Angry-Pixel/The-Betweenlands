package thebetweenlands.client.render.model.baked;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

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
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite.EnumLifeCrystalType;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.StalactiteHelper;

public class ModelLifeCrystalStalactite implements IModel {
	public static final ResourceLocation TEXTURE_DEFAULT = new ResourceLocation(ModInfo.ID, "blocks/pitstone");
	public static final ResourceLocation TEXTURE_ORE_BACKGROUND = new ResourceLocation(ModInfo.ID, "blocks/life_crystal_ore_background");
	public static final ResourceLocation TEXTURE_ORE = new ResourceLocation(ModInfo.ID, "blocks/life_crystal_ore");

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Collections.unmodifiableCollection(Arrays.asList(new ResourceLocation[]{TEXTURE_DEFAULT, TEXTURE_ORE_BACKGROUND, TEXTURE_ORE}));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new ModelBakedLifeCrystalOre(format, bakedTextureGetter.apply(TEXTURE_DEFAULT), bakedTextureGetter.apply(TEXTURE_ORE_BACKGROUND), bakedTextureGetter.apply(TEXTURE_ORE));
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class ModelBakedLifeCrystalOre implements IBakedModel {
		private final VertexFormat format;
		private final TextureAtlasSprite textureDefault;
		private final TextureAtlasSprite textureOreBackground;
		private final TextureAtlasSprite textureOre;

		private ModelBakedLifeCrystalOre(VertexFormat format, TextureAtlasSprite textureDefault, TextureAtlasSprite textureOreBackground, TextureAtlasSprite textureOre) {
			this.format = format;
			this.textureDefault = textureDefault;
			this.textureOreBackground = textureOreBackground;
			this.textureOre = textureOre;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState stateOld, EnumFacing side, long rand) {
			IExtendedBlockState state = (IExtendedBlockState) stateOld;

			List<BakedQuad> quads = ImmutableList.of();
			
			if(side == null) {
				try {
					EnumLifeCrystalType type = state.getValue(BlockLifeCrystalStalactite.VARIANT);
					int distUp = state.getValue(BlockLifeCrystalStalactite.DIST_UP);
					int distDown = state.getValue(BlockLifeCrystalStalactite.DIST_DOWN);
					boolean noTop = state.getValue(BlockLifeCrystalStalactite.NO_TOP);
					boolean noBottom = state.getValue(BlockLifeCrystalStalactite.NO_BOTTOM);
					int posX = state.getValue(BlockLifeCrystalStalactite.POS_X);
					int posY = state.getValue(BlockLifeCrystalStalactite.POS_Y);
					int posZ = state.getValue(BlockLifeCrystalStalactite.POS_Z);
					float height = distUp == 0 && noTop ? 0.75F : 1.0F;

					int totalHeight = 1 + distDown + distUp;
					float distToMidBottom, distToMidTop;

					double squareAmount = 1.2D;
					double halfTotalHeightSQ;

					if(noTop) {
						halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
						distToMidBottom = Math.abs(distUp + 1);
						distToMidTop = Math.abs(distUp);
					} else if(noBottom) {
						halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
						distToMidBottom = Math.abs(distDown);
						distToMidTop = Math.abs(distDown + 1);
					} else {
						float halfTotalHeight = totalHeight * 0.5F;
						halfTotalHeightSQ = Math.pow(halfTotalHeight, squareAmount);
						distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
						distToMidTop = Math.abs(halfTotalHeight - distUp);
					}

					int minValBottom = (noBottom && distDown == 0) ? 0 : 1;
					int minValTop = (noTop && distUp == 0) ? 0 : 1;
					int scaledValBottom = (int) (Math.pow(distToMidBottom, squareAmount) / halfTotalHeightSQ * (8 - minValBottom)) + minValBottom;
					int scaledValTop = (int) (Math.pow(distToMidTop, squareAmount) / halfTotalHeightSQ * (8 - minValTop)) + minValTop;

					float umin = 0;
					float umax = 16;
					float vmin = 0;
					float vmax = 16;

					//This is necessary because vertices that are on the exact same point seem to cause problems with the lighting
					float bottomLightingFix = (distDown == 0 && noBottom ? 0.0001F : 0);
					float topLightingFix = (distUp == 0 && noTop ? 0.0001F : 0);

					float halfSize = (float) scaledValBottom / 16 + bottomLightingFix;
					float halfSizeTexW = halfSize * (umax - umin);
					float halfSize1 = (float) (scaledValTop) / 16 + topLightingFix;
					float halfSizeTex1 = halfSize1 * (umax - umin);

					StalactiteHelper core = StalactiteHelper.getValsFor(posX, posY, posZ);

					if(distDown == 0 && !noBottom) {
						core.bX = 0.5D;
						core.bZ = 0.5D;
					}
					if(distUp == 0 && !noTop) {
						core.tX = 0.5D;
						core.tZ = 0.5D;
					}

					QuadBuilder builder = new QuadBuilder(this.format);

					for(int i = 0; i < (type == EnumLifeCrystalType.DEFAULT ? 1 : 2); i++) {
						if(type == EnumLifeCrystalType.DEFAULT) {
							builder.setSprite(this.textureDefault);
						} else {
							if(i == 0) 
								builder.setSprite(this.textureOreBackground);
							else if(i == 1)
								builder.setSprite(this.textureOre);
						}

						if(i == 1) {
							builder.setLightmap(15, 15);
						}
						
						// front
						builder.addVertex(core.bX - halfSize, 0, core.bZ - halfSize, umin + halfSizeTexW * 2, vmax);
						builder.addVertex(core.bX - halfSize, 0, core.bZ + halfSize, umin, vmax);
						builder.addVertex(core.tX - halfSize1, height, core.tZ + halfSize1, umin, vmin);
						builder.addVertex(core.tX - halfSize1, height, core.tZ - halfSize1, umin + halfSizeTex1 * 2, vmin);
						// back
						builder.addVertex(core.bX + halfSize, 0, core.bZ + halfSize, umin + halfSizeTexW * 2, vmax);
						builder.addVertex(core.bX + halfSize, 0, core.bZ - halfSize, umin, vmax);
						builder.addVertex(core.tX + halfSize1, height, core.tZ - halfSize1, umin, vmin);
						builder.addVertex(core.tX + halfSize1, height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);
						// left
						builder.addVertex(core.bX + halfSize, 0, core.bZ - halfSize, umin + halfSizeTexW * 2, vmax);
						builder.addVertex(core.bX - halfSize, 0, core.bZ - halfSize, umin, vmax);
						builder.addVertex(core.tX - halfSize1, height, core.tZ - halfSize1, umin, vmin);
						builder.addVertex(core.tX + halfSize1, height, core.tZ - halfSize1, umin + halfSizeTex1 * 2, vmin);
						// right
						builder.addVertex(core.bX - halfSize, 0, core.bZ + halfSize, umin + halfSizeTexW * 2, vmax);
						builder.addVertex(core.bX + halfSize, 0, core.bZ + halfSize, umin, vmax);
						builder.addVertex(core.tX + halfSize1, height, core.tZ + halfSize1, umin, vmin);
						builder.addVertex(core.tX - halfSize1, height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);

						// top
						if(distUp == 0) {
							builder.addVertex(core.tX - halfSize1, height, core.tZ - halfSize1, umin, vmin);
							builder.addVertex(core.tX - halfSize1, height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);
							builder.addVertex(core.tX + halfSize1, height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin + halfSizeTex1 * 2);
							builder.addVertex(core.tX + halfSize1, height, core.tZ - halfSize1, umin, vmin + halfSizeTex1 * 2);
						}

						// bottom
						if(distDown == 0) {
							builder.addVertex(core.bX - halfSize, 0, core.bZ + halfSize, umin + halfSizeTexW * 2, vmin);
							builder.addVertex(core.bX - halfSize, 0, core.bZ - halfSize, umin, vmin);
							builder.addVertex(core.bX + halfSize, 0, core.bZ - halfSize, umin, vmin + halfSizeTexW * 2);
							builder.addVertex(core.bX + halfSize, 0, core.bZ + halfSize, umin + halfSizeTexW * 2, vmin + halfSizeTexW * 2);
						}
					}

					quads = builder.build().nonCulledQuads;
				} catch(Exception ex) {
					//throws inexplicable NPE when damaging block :(
				}
			}

			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.textureDefault;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}
	}
}