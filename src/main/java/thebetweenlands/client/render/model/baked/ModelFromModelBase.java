package thebetweenlands.client.render.model.baked;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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
import thebetweenlands.util.ModelConverter.Packing;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.TexturePacker;
import thebetweenlands.util.TexturePacker.ITexturePackable;
import thebetweenlands.util.Vec3UV;

public class ModelFromModelBase implements IModel, ITexturePackable {
	public static interface IVertexProcessor {
		Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder);
	}

	public final ResourceLocation texture;
	public final ResourceLocation particleTexture;
	public final int width;
	public final int height;
	public final IVertexProcessor vertexProcessor;
	private boolean ambientOcclusion = true;

	public final ModelBase model;
	
	public final Model convertedModel;
	
	@Nullable
	protected final TexturePacker packer;
	
	protected final Set<ResourceLocation> usedTextures = new HashSet<>();
	
	private static class ModelCacheKey {
		private final String model;
		private final ResourceLocation texture;
		private final String vertexProcessor;
		
		private ModelCacheKey(ModelBase model, ResourceLocation texture, IVertexProcessor vertexProcessor) {
			this.model = model.getClass().getName();
			this.texture = texture;
			this.vertexProcessor = vertexProcessor == null ? "null" : vertexProcessor.getClass().getName();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((model == null) ? 0 : model.hashCode());
			result = prime * result + ((texture == null) ? 0 : texture.hashCode());
			result = prime * result + ((vertexProcessor == null) ? 0 : vertexProcessor.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ModelCacheKey other = (ModelCacheKey) obj;
			if (model == null) {
				if (other.model != null)
					return false;
			} else if (!model.equals(other.model))
				return false;
			if (texture == null) {
				if (other.texture != null)
					return false;
			} else if (!texture.equals(other.texture))
				return false;
			if (vertexProcessor == null) {
				if (other.vertexProcessor != null)
					return false;
			} else if (!vertexProcessor.equals(other.vertexProcessor))
				return false;
			return true;
		}
	}
	
	protected final Map<ModelCacheKey, Model> derivativeModelCache;
	
	public ModelFromModelBase(TexturePacker packer, ModelBase model, ResourceLocation texture, int width, int height) {
		this(packer, model, texture, texture, width, height, null);
	}

	public ModelFromModelBase(TexturePacker packer, ModelBase model, ResourceLocation texture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this(packer, model, texture, texture, width, height, vertexProcessor);
	}

	public ModelFromModelBase(TexturePacker packer, ModelBase model, ResourceLocation texture, ResourceLocation particleTexture, int width, int height) {
		this(packer, model, texture, particleTexture, width, height, null);
	}

	public ModelFromModelBase(TexturePacker packer, ModelBase model, ResourceLocation texture, ResourceLocation particleTexture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this(new HashMap<>(), packer, model, texture, particleTexture, width, height, vertexProcessor);
	}
	
	/**
	 * Constructor to create derived models with changing the main texture.
	 */
	public ModelFromModelBase(ModelFromModelBase parent, ResourceLocation texture, ResourceLocation particleTexture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this(parent.derivativeModelCache, parent.packer, parent.model, texture, particleTexture, width, height, vertexProcessor);
	}
	
	/**
	 * Constructor used to create derived models without changing the main texture (i.e. reusing the already converted model).
	 */
	public ModelFromModelBase(ModelFromModelBase parent, ResourceLocation particleTexture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this(parent.derivativeModelCache, parent.model, parent.convertedModel, parent.texture, particleTexture, width, height, vertexProcessor);
	}
	
	private ModelFromModelBase(Map<ModelCacheKey, Model> modelCache, TexturePacker packer, ModelBase model, ResourceLocation texture, ResourceLocation particleTexture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		Preconditions.checkNotNull(packer, "Texture packer must not be null for non-derived models");
		
		this.derivativeModelCache = modelCache;
		this.packer = packer;
		this.model = model;
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.vertexProcessor = vertexProcessor;
		this.particleTexture = particleTexture;
		
		//Converted models are cached such that their textures are not
		//packed onto the atlas multiple times.
		ModelCacheKey key = new ModelCacheKey(model, texture, vertexProcessor);
		Model cachedConvertedModel = this.derivativeModelCache.get(key);
		if(cachedConvertedModel == null) {
			ModelConverter converter = new ModelConverter(new Packing(texture, packer, this), model, 0.0625D, true);
			this.derivativeModelCache.put(key, cachedConvertedModel = converter.getModel());
		}
		this.convertedModel = cachedConvertedModel;
		
		//Textures are collected right after the model is loaded, but at
		//that point the packed textures aren't created yet so best we can
		//do is to just add the particle texture before the packed textures
		//are created
		if(this.particleTexture != null) {
			this.usedTextures.add(this.particleTexture);
		}
	}
	
	private ModelFromModelBase(Map<ModelCacheKey, Model> modelCache, ModelBase model, Model convertedModel, ResourceLocation texture, ResourceLocation particleTexture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this.derivativeModelCache = modelCache;
		this.packer = null;
		this.model = model;
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.vertexProcessor = vertexProcessor;
		this.particleTexture = particleTexture;
		this.convertedModel = convertedModel;
		
		if(this.particleTexture != null) {
			this.usedTextures.add(this.particleTexture);
		}
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
	public void onPacked() {
		for(Box box : this.convertedModel.getBoxes()) {
			for(Quad quad : box.getQuads()) {
				for(Vec3UV vert : quad.getVertices()) {
					this.usedTextures.add(vert.packedQuad.getPackedLocation());
				}
			}
		}
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return this.usedTextures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
		return new ModelBakedModelBase(this.vertexProcessor, state.apply(Optional.empty()), map, format, this.convertedModel, bakedTextureGetter, bakedTextureGetter.apply(this.particleTexture), this.width, this.height, this.ambientOcclusion);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
	public static class ModelBakedModelBase implements IBakedModel {
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
		protected final VertexFormat format;
		protected final TextureAtlasSprite particleTexture;
		protected final boolean ambientOcclusion;
		protected List<BakedQuad> quads;

		protected ModelBakedModelBase(IVertexProcessor vertexProcessor, Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms,
				VertexFormat format, Model convertedModel, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, TextureAtlasSprite particleTexture, int width, int height, boolean ambientOcclusion) {
			this.transformation = transformation.orElse(null);
			this.transforms = transforms;
			this.format = format;
			this.particleTexture = particleTexture;
			
			QuadBuilder builder = new QuadBuilder(this.format).setTransformation(this.transformation);
			for(Box box : convertedModel.getBoxes()) {
				for(Quad quad : box.getQuads()) {
					for(int i = 0; i < quad.getVertices().length; i++) {
						Vec3UV vert = quad.getVertices()[i];
						
						TextureAtlasSprite quadSprite = bakedTextureGetter.apply(vert.packedQuad.getPackedLocation());
						float u = vert.getU(16.0F, width);
						float v = vert.getV(16.0F, height);
						
						if(vertexProcessor != null) {
							vert = vertexProcessor.process(vert, quad, box, builder);
						}
						
						if(vert != null) {
							builder.setSprite(quadSprite);
							builder.addVertex(vert.x + 0.5F, 1.5F - vert.y, vert.z + 0.5F, u, v);
						}
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

		if(texture == this.texture) {
			return new ModelFromModelBase(this.derivativeModelCache, this.model, this.convertedModel, texture, particleTexture, this.width, this.height, this.vertexProcessor).setAmbientOcclusion(ambientOcclusion);
		} else {
			return new ModelFromModelBase(this.derivativeModelCache, this.packer, this.model, texture, particleTexture, this.width, this.height, this.vertexProcessor).setAmbientOcclusion(ambientOcclusion);
		}
	}
}