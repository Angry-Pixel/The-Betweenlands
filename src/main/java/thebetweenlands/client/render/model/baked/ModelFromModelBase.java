package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import thebetweenlands.util.ModelConverter;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Model;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.TexturePacker;
import thebetweenlands.util.TexturePacker.TextureQuad;
import thebetweenlands.util.TexturePacker.TextureQuadMap;
import thebetweenlands.util.Vec3UV;

public class ModelFromModelBase implements IModel {
	public static interface IVertexProcessor {
		Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder);
	}

	public final ModelBase model;
	public final ResourceLocation texture;
	public final ResourceLocation particleTexture;
	public final int width;
	public final int height;
	public final IVertexProcessor vertexProcessor;
	private boolean ambientOcclusion = true;

	public ModelFromModelBase(ModelBase model, ResourceLocation texture, int width, int height) {
		this(model, texture, texture, width, height, null);
	}

	public ModelFromModelBase(ModelBase model, ResourceLocation texture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this(model, texture, texture, width, height, vertexProcessor);
	}

	public ModelFromModelBase(ModelBase model, ResourceLocation texture, ResourceLocation particleTexture, int width, int height) {
		this(model, texture, particleTexture, width, height, null);
	}

	public ModelFromModelBase(ModelBase model, ResourceLocation texture, ResourceLocation particleTexture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this.model = model;
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.vertexProcessor = vertexProcessor;
		this.particleTexture = particleTexture;
	}

	/**
	 * Sets whether ambient occlusion should be used
	 * @param ao
	 * @return
	 */
	public ModelFromModelBase setAmbientOcclusion(boolean ao) {
		this.ambientOcclusion = ao;
		return this;
	}

	/**
	 * Returns whether ambient occlusion should be used
	 * @return
	 */
	public boolean isAmbientOcclusion() {
		return this.ambientOcclusion;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		if(this.particleTexture != this.texture)
			return ImmutableSet.of(this.texture, this.particleTexture);
		return ImmutableSet.of(this.texture);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
		return new ModelBakedModelBase(this.vertexProcessor, state.apply(Optional.empty()), map, format, this.model, bakedTextureGetter.apply(this.texture), bakedTextureGetter.apply(this.particleTexture), this.width, this.height, this.ambientOcclusion);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
	
	//TODO Remove
	public int addToPacker(TexturePacker packer) {
		List<TextureQuad> quads = new ArrayList<>();
		
		int area = 0;
		
		ModelConverter converter = new ModelConverter(this.model, 0.0625D, false);
		Model convertedModel = converter.getModel();
		for(Box box : convertedModel.getBoxes()) {
			for(Quad quad : box.getQuads()) {
				int minU = Integer.MAX_VALUE;
				int minV = Integer.MAX_VALUE;
				int maxU = 0;
				int maxV = 0;
				
				for(int i = 0; i < quad.getVertices().length; i++) {
					Vec3UV vert = quad.getVertices()[i];
					
					minU = Math.min(minU, (int)Math.floor(vert.u * vert.uw));
					minV = Math.min(minV, (int)Math.floor(vert.v * vert.vw));
					maxU = Math.max(maxU, (int)Math.floor(vert.u * vert.uw));
					maxV = Math.max(maxV, (int)Math.floor(vert.v * vert.vw));
				}
				
				area += (maxU - minU) * (maxV - minV);
				
				quads.add(new TextureQuad(this.texture, (int)minU, (int)minV, (int)(maxU - minU), (int)(maxV - minV)));
			}
		}
		
		packer.addTextureMap(new TextureQuadMap(quads));
		
		return area;
	}
	

	public static class ModelBakedModelBase implements IBakedModel {
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
		protected final VertexFormat format;
		protected final TextureAtlasSprite texture;
		protected final TextureAtlasSprite particleTexture;
		protected final boolean ambientOcclusion;
		protected List<BakedQuad> quads;

		protected ModelBakedModelBase(IVertexProcessor vertexProcessor, Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, ModelBase model, TextureAtlasSprite texture, TextureAtlasSprite particleTexture, int width, int height, boolean ambientOcclusion) {
			this.transformation = transformation.orElse(null);
			this.transforms = transforms;
			this.format = format;
			this.texture = texture;
			this.particleTexture = particleTexture;
			ModelConverter converter = new ModelConverter(model, 0.0625D, true);
			Model convertedModel = converter.getModel();
			QuadBuilder builder = new QuadBuilder(this.format).setSprite(this.texture).setTransformation(this.transformation);
			for(Box box : convertedModel.getBoxes()) {
				for(Quad quad : box.getQuads()) {
					for(int i = 0; i < quad.getVertices().length; i++) {
						Vec3UV vert = quad.getVertices()[i];
						if(vertexProcessor != null)
							vert = vertexProcessor.process(vert, quad, box, builder);
						if(vert != null)
							builder.addVertex(vert.x + 0.5F, 1.5F - vert.y, vert.z + 0.5F, vert.getU(16.0F, width), vert.getV(16.0F, height));
					}
				}
			}
			this.quads = builder.build().nonCulledQuads;
			this.ambientOcclusion = ambientOcclusion;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState stateOld, EnumFacing side, long rand) {
			if(side == null)
				return this.quads;
			return Collections.emptyList();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return this.ambientOcclusion;
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

	@Override
	public ModelFromModelBase process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();
		
		ResourceLocation particleTexture = this.particleTexture;
		
		if(customData.containsKey("particle_texture")) {
			particleTexture = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("particle_texture")), "particle_texture"));
		}
		
		if(particleTexture == null) {
			particleTexture = TextureMap.LOCATION_MISSING_TEXTURE;
		}

		boolean ambientOcclusion = this.isAmbientOcclusion();
		
		if(customData.containsKey("ambient_occlusion")) {
			ambientOcclusion = JsonUtils.getBoolean(parser.parse(customData.get("ambient_occlusion")), "ambient_occlusion");
		}
		
		ResourceLocation texture = this.texture;
		
		if(customData.containsKey("texture")) {
			texture = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("texture")), "texture"));
		}
		
		if(texture == null) {
			texture = TextureMap.LOCATION_MISSING_TEXTURE;
		}

		return new ModelFromModelBase(this.model, texture, particleTexture, this.width, this.height, this.vertexProcessor).setAmbientOcclusion(ambientOcclusion);
	}
}