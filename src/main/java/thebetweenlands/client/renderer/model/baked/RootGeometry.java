package thebetweenlands.client.renderer.model.baked;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import thebetweenlands.common.block.RootBlock;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.StalactiteHelper;

public class RootGeometry implements IUnbakedGeometry<RootGeometry> {

	public final ResourceLocation textureTop;
	public final ResourceLocation textureMiddle;
	public final ResourceLocation textureBottom;
	
	public RootGeometry(ResourceLocation textureTop, ResourceLocation textureMiddle, ResourceLocation textureBottom) {
		this.textureTop = textureTop;
		this.textureMiddle = textureMiddle;
		this.textureBottom = textureBottom;
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		// TODO redo
		return new RootDynamicModel(spriteGetter.apply(context.getMaterial(this.textureTop.toString())), spriteGetter.apply(context.getMaterial(this.textureMiddle.toString())), spriteGetter.apply(context.getMaterial(this.textureBottom.toString())));
	}

	public static class RootDynamicModel implements IDynamicBakedModel {

		public final TextureAtlasSprite textureTop;
		public final TextureAtlasSprite textureMiddle;
		public final TextureAtlasSprite textureBottom;
		
		public RootDynamicModel(TextureAtlasSprite textureTop, TextureAtlasSprite textureMiddle, TextureAtlasSprite textureBottom) {
			this.textureTop = textureTop;
			this.textureMiddle = textureMiddle;
			this.textureBottom = textureBottom;
		}
		
		
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
//			extraData.get(null)
			// TODO Auto-generated method stub
			List<BakedQuad> quads;
			
			if(side == null) {
				int distUp = Optional.of(extraData.get(RootBlock.DIST_UP)).orElse(0);
				int distDown = Optional.of(extraData.get(RootBlock.DIST_DOWN)).orElse(0);
				boolean noTop = Optional.of(extraData.get(RootBlock.NO_TOP)).orElse(true);
				boolean noBottom = Optional.of(extraData.get(RootBlock.NO_BOTTOM)).orElse(true);
				int posX = Optional.of(extraData.get(RootBlock.POS_X)).orElse(0);
				int posY = Optional.of(extraData.get(RootBlock.POS_Y)).orElse(0);
				int posZ = Optional.of(extraData.get(RootBlock.POS_Z)).orElse(0);
				float height = 1.0F;

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

				float halfSize = (float) scaledValBottom / 16;
				float halfSizeTexW = halfSize * (umax - umin);
				float halfSize1 = (float) (scaledValTop) / 16;
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
				
//				QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
				QuadBuilder builder = new QuadBuilder(24, renderType.format);

				boolean hasTop = distUp == 0 && !noTop;
				boolean hasBottom = distDown == 0 && !noBottom;

				builder.setSprite(hasTop ? this.textureTop : hasBottom ? this.textureBottom : this.textureMiddle);

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

				quads = builder.build().nonCulledQuads;
			} else {
				quads = ImmutableList.of();
			}
			return null;
		}
		
		@Override
		public boolean useAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean usesBlockLight() {
			return true;
		}

		@Override
		public boolean isCustomRenderer() {
			// TODO double check
			return true;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return this.textureTop;
		}

		@Override
		public ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}
	}

	public static class RootGeometryLoader implements IGeometryLoader<RootGeometry> {
		public static final RootGeometryLoader INSTANCE = new RootGeometryLoader();
		
		private RootGeometryLoader() {}
		
		@Override
		public RootGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			JsonObject textures = jsonObject.getAsJsonObject("textures");
			ResourceLocation
				topTexture = MissingTextureAtlasSprite.getLocation(),
				middleTexture = MissingTextureAtlasSprite.getLocation(),
				bottomTexture = MissingTextureAtlasSprite.getLocation();
			
			try {
				if(textures != null) {
					String top = textures.get("top").getAsString();
					String middle = textures.get("middle").getAsString();
					String bottom = textures.get("bottom").getAsString();
					topTexture = ResourceLocation.tryParse(top);
					middleTexture = ResourceLocation.tryParse(middle);
					bottomTexture = ResourceLocation.tryParse(bottom);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			return new RootGeometry(topTexture, middleTexture, bottomTexture);
		}
		
	}
}
