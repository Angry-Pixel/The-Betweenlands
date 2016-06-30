package thebetweenlands.client.render.models.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import scala.actors.threadpool.Arrays;
import thebetweenlands.common.lib.ModInfo;

public class LifeCrystalOreModel implements IModel {
	public static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(ModInfo.ID, "blocks/life_crystal_ore_background");
	public static final ResourceLocation TEXTURE_ORE = new ResourceLocation(ModInfo.ID, "blocks/life_crystal_ore");

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Collections.unmodifiableCollection(Arrays.asList(new ResourceLocation[]{TEXTURE_BACKGROUND, TEXTURE_ORE}));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new LifeCrystalOreBakedModel(format, bakedTextureGetter.apply(TEXTURE_BACKGROUND), bakedTextureGetter.apply(TEXTURE_ORE));
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class LifeCrystalOreBakedModel implements IBakedModel {
		private final VertexFormat format;
		private final TextureAtlasSprite textureBackground;
		private final TextureAtlasSprite textureOre;

		private LifeCrystalOreBakedModel(VertexFormat format, TextureAtlasSprite textureBackground, TextureAtlasSprite textureOre) {
			this.format = format;
			this.textureBackground = textureBackground;
			this.textureOre = textureOre;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList<>();

			double o = .4;

			/*quads.add(this.createQuad(this.format, new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1, o), new Vec3d(1 - o, 1, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), this.textureBackground));
			quads.add(this.createQuad(this.format, new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1, 1 - o), new Vec3d(o, 1, o), new Vec3d(o, 1 - o, o), this.textureBackground));
			quads.add(this.createQuad(this.format, new Vec3d(o, 1, o), new Vec3d(1 - o, 1, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(o, 1 - o, o), this.textureBackground));
			quads.add(this.createQuad(this.format, new Vec3d(o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1, 1 - o), new Vec3d(o, 1, 1 - o), this.textureBackground));

			quads.add(this.createQuad(this.format, new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1, o), new Vec3d(1 - o, 1, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), this.textureOre));
			quads.add(this.createQuad(this.format, new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1, 1 - o), new Vec3d(o, 1, o), new Vec3d(o, 1 - o, o), this.textureOre));
			quads.add(this.createQuad(this.format, new Vec3d(o, 1, o), new Vec3d(1 - o, 1, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(o, 1 - o, o), this.textureOre));
			quads.add(this.createQuad(this.format, new Vec3d(o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1, 1 - o), new Vec3d(o, 1, 1 - o), this.textureOre));
			 */

			QuadBuilder builder = new QuadBuilder(this.format);

			builder.setSprite(this.textureBackground);
			builder.addVertexInferUV(new Vec3d(1 - o, 1 - o, o));
			builder.addVertexInferUV(new Vec3d(1 - o, 1, o));
			builder.addVertexInferUV(new Vec3d(1 - o, 1, 1 - o));
			builder.addVertexInferUV(new Vec3d(1 - o, 1 - o, 1 - o));
			quads.addAll(builder.build());
			
			builder.setSprite(this.textureOre);
			builder.addVertexInferUV(new Vec3d(1 - o, 1 - o, o));
			builder.addVertexInferUV(new Vec3d(1 - o , 1, o));
			builder.addVertexInferUV(new Vec3d(1 - o, 1, 1 - o));
			builder.addVertexInferUV(new Vec3d(1 - o, 1 - o, 1 - o));
			quads.addAll(builder.build());

			System.out.println(this.format);
			
			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
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
			return this.textureBackground;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return null;
		}
	}
}
