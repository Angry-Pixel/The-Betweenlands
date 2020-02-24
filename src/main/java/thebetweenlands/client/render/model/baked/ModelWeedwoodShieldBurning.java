package thebetweenlands.client.render.model.baked;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelWeedwoodShield;
import thebetweenlands.common.registries.ModelRegistry;
import thebetweenlands.util.ModelConverter.Model;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.TexturePacker;

public class ModelWeedwoodShieldBurning extends ModelFromModelBase {
	public static final ResourceLocation FIRE_TEXTURE_LOCATION = new ResourceLocation("minecraft:blocks/fire_layer_0");

	public ModelWeedwoodShieldBurning(TexturePacker packer) {
		super(new ModelFromModelBase.Builder(new ModelWeedwoodShield(), new ResourceLocation("thebetweenlands:items/shields/weedwood_shield"), 64, 64).particleTexture(new ResourceLocation("thebetweenlands:particle/item/weedwood_shield_particle")).packer(packer).processor(ModelRegistry.SHIELD_VERTEX_PROCESSOR));
		this.usedTextures.add(FIRE_TEXTURE_LOCATION);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
		return new ModelBakedWeedwoodShieldBurning(this.vertexProcessor, state.apply(Optional.empty()), map, format, this.convertedModel, bakedTextureGetter, bakedTextureGetter.apply(this.texture), bakedTextureGetter.apply(FIRE_TEXTURE_LOCATION), this.width, this.height);
	}

	public static class ModelBakedWeedwoodShieldBurning extends ModelBakedModelBase {
		protected ModelBakedWeedwoodShieldBurning(IVertexProcessor vertexProcessor,
				Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms,
				VertexFormat format, Model convertedModel, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, TextureAtlasSprite texture, TextureAtlasSprite flameSprite, int width, int height) {
			super(vertexProcessor, transformation, transforms, format, convertedModel, bakedTextureGetter, texture, texture, width, height, true);

			QuadBuilder builder = new QuadBuilder(this.format).setSprite(flameSprite);
			for(int i = 0; i < 4; i++) {
				//Front
				builder.addVertexInferUV(new Vec3d(-0.5D, 1 - 0.5D + i / 4.0D, 0.35D - i / 16.0D));
				builder.addVertexInferUV(new Vec3d(-0.5D, 0 - 0.5D + i / 4.0D, 0.15D - i / 16.0D));
				builder.addVertexInferUV(new Vec3d(0.5D, 0 - 0.5D + i / 4.0D, 0.15D - i / 16.0D));
				builder.addVertexInferUV(new Vec3d(0.5D, 1 - 0.5D + i / 4.0D, 0.35D - i / 16.0D));
				//Back
				builder.addVertexInferUV(new Vec3d(0.5D, 1 - 0.5D + i / 4.0D, 0.3D - i / 16.0D));
				builder.addVertexInferUV(new Vec3d(0.5D, 0 - 0.5D + i / 4.0D, 0.1D - i / 16.0D));
				builder.addVertexInferUV(new Vec3d(-0.5D, 0 - 0.5D + i / 4.0D, 0.1D - i / 16.0D));
				builder.addVertexInferUV(new Vec3d(-0.5D, 1 - 0.5D + i / 4.0D, 0.3D - i / 16.0D));
			}
			ImmutableList.Builder<BakedQuad> combinedQuads = ImmutableList.builder();
			combinedQuads.addAll(this.quads);
			combinedQuads.addAll(builder.build().nonCulledQuads);
			this.quads = combinedQuads.build();
		}
	}
}
