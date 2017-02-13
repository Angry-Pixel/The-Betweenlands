package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.util.QuadBuilder;

public class ModelConnectedTexture implements IModelCustomData {
	protected final ResourceLocation[] textures;
	protected final ResourceLocation particleTexture;
	protected final boolean ambientOcclusion;
	protected final String index0, index1, index2, index3;
	protected final Vec3d p1, p2, p3, p4;

	public ModelConnectedTexture() {
		this(null, null, false, null, null, null, null, null, null, null, null);
	}

	public ModelConnectedTexture(ResourceLocation[] textures, ResourceLocation particleTexture, boolean ambientOcclusion,
			String index0, String index1, String index2, String index3,
			Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4) {
		this.textures = textures;
		this.particleTexture = particleTexture;
		this.ambientOcclusion = ambientOcclusion;
		this.index0 = index0;
		this.index1 = index1;
		this.index2 = index2;
		this.index3 = index3;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		if(this.textures != null) {
			return Collections.unmodifiableCollection(Arrays.asList(this.textures));
		}
		return ImmutableList.of();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		TextureAtlasSprite[] sprites = null;
		if(this.textures != null) {
			sprites = new TextureAtlasSprite[this.textures.length];
			for(int i = 0; i < this.textures.length; i++) {
				sprites[i] = bakedTextureGetter.apply(this.textures[i]);
			}
		}
		ImmutableMap<TransformType, TRSRTransformation> map = IPerspectiveAwareModel.MapWrapper.getTransforms(state);
		return new ModelBakedConnectedFace(state.apply(Optional.<IModelPart>absent()), map, format, sprites, 
				bakedTextureGetter.apply(this.particleTexture != null ? this.particleTexture : new ResourceLocation("")), this.ambientOcclusion, this.index0, this.index1, this.index2, this.index3,
				this.p1, this.p2, this.p3, this.p4);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class ModelBakedConnectedFace implements IPerspectiveAwareModel {
		protected final VertexFormat format;
		protected final TextureAtlasSprite[] textures;
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
		protected final TextureAtlasSprite particleTexture;
		protected final boolean ambientOcclusion;
		protected final String index0, index1, index2, index3;
		protected final Vec3d p1, p2, p3, p4;

		protected final BakedQuad[][] quads = new BakedQuad[4][5];

		private ModelBakedConnectedFace(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, 
				VertexFormat format, TextureAtlasSprite[] textures, 
				TextureAtlasSprite particleTexture, boolean ambientOcclusion,
				String index0, String index1, String index2, String index3,
				Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4) {
			this.format = format;
			this.textures = textures;
			this.transforms = transforms;
			this.transformation = transformation.isPresent() ? transformation.get() : null;
			this.particleTexture = particleTexture;
			this.ambientOcclusion = ambientOcclusion;
			this.index0 = index0;
			this.index1 = index1;
			this.index2 = index2;
			this.index3 = index3;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;

			if(this.textures != null) {
				this.buildQuads();
			}
		}

		protected void buildQuads() {
			Vec3d p12 = this.p1.add(this.p2).scale(0.5D);
			Vec3d p23 = this.p2.add(this.p3).scale(0.5D);
			Vec3d p34 = this.p3.add(this.p4).scale(0.5D);
			Vec3d p14 = this.p4.add(this.p1).scale(0.5D);
			Vec3d cp = p12.add(p34).scale(0.5D);

			this.quads[0] = this.bakeTextureVariants(this.p1, p12, cp, p14, 0);
			this.quads[1] = this.bakeTextureVariants(p12, this.p2, p23, cp, 2);
			this.quads[2] = this.bakeTextureVariants(cp, p23, this.p3, p34, 3);
			this.quads[3] = this.bakeTextureVariants(p14, cp, p34, this.p4, 1);
		}

		protected BakedQuad[] bakeTextureVariants(Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4, int quadrant) {
			float umin = 0;
			float vmin = 0;
			float umax = 16;
			float vmax = 16;
			if(quadrant == 0) {
				umin = 0; umax = 8;
				vmin = 0; vmax = 8;
			} else if(quadrant == 1) {
				umin = 8; umax = 16;
				vmin = 0; vmax = 8;
			} else if(quadrant == 2) {
				umin = 0; umax = 8;
				vmin = 8; vmax = 16;
			} else if(quadrant == 3) {
				umin = 8; umax = 16;
				vmin = 8; vmax = 16;
			}
			QuadBuilder builder = new QuadBuilder(4 * this.textures.length, this.format);
			for(int i = 0; i < this.textures.length; i++) {
				builder.setSprite(this.textures[i]);
				builder.addVertex(p1, umin, vmin);
				builder.addVertex(p2, umin, vmax);
				builder.addVertex(p3, umax, vmax);
				builder.addVertex(p4, umax, vmin);
			}
			return builder.build().toArray(new BakedQuad[0]);
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState stateOld, EnumFacing side, long rand) {
			IExtendedBlockState state = (IExtendedBlockState) stateOld;
			ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = state.getUnlistedProperties();

			int[] indices = new int[4];

			for(Entry<IUnlistedProperty<?>, Optional<?>> entry : properties.entrySet()) {
				String property = entry.getKey().getName();

				if(this.index0.equals(property)) {
					indices[0] = (Integer) entry.getValue().get();
				} else if(this.index1.equals(property)) {
					indices[1] = (Integer) entry.getValue().get();
				} else if(this.index2.equals(property)) {
					indices[2] = (Integer) entry.getValue().get();
				} else if(this.index3.equals(property)) {
					indices[3] = (Integer) entry.getValue().get();
				}
			}

			List<BakedQuad> quads = new ArrayList<>(4);

			for(int i = 0; i < 4; i++) {
				int textureIndex = indices[i];
				quads.add(this.quads[i][textureIndex]);
			}

			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return this.ambientOcclusion;
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
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
			return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, this.transforms, type);
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.particleTexture;
		}
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		String[] indices = new String[] {this.index0, this.index1, this.index2, this.index3};
		for(int i = 0; i < 4; i++) {
			if(customData.containsKey("index_" + i)) {
				indices[i] = parser.parse(customData.get("index_" + i)).getAsString();
			}
		}

		Vec3d[] vertices = new Vec3d[] {this.p1, this.p2, this.p3, this.p4};
		for(int i = 0; i < 4; i++) {
			if(customData.containsKey("vertex_" + i)) {
				JsonObject jsonObj = parser.parse(customData.get("vertex_" + i)).getAsJsonObject();
				vertices[i] = new Vec3d(jsonObj.get("x").getAsDouble(), jsonObj.get("y").getAsDouble(), jsonObj.get("z").getAsDouble());
			}
		}

		ResourceLocation[] textures = this.textures;
		if(textures == null) {
			textures = new ResourceLocation[5];
		}
		for(int i = 0; i < 5; i++) {
			if(customData.containsKey("texture_" + i)) {
				textures[i] = new ResourceLocation(parser.parse(customData.get("texture_" + i)).getAsString());
			}
			if(textures[i] == null) {
				textures[i] = new ResourceLocation("");
			}
		}

		boolean ambientOcclusion = this.ambientOcclusion;
		if(customData.containsKey("ambient_occlusion")) {
			ambientOcclusion = parser.parse(customData.get("ambient_occlusion")).getAsBoolean();
		}

		ResourceLocation particleTexture = this.particleTexture;
		if(customData.containsKey("particle_texture")) {
			particleTexture = new ResourceLocation(parser.parse(customData.get("particle_texture")).getAsString());
		}

		return new ModelConnectedTexture(textures, particleTexture, ambientOcclusion, 
				indices[0], indices[1], indices[2], indices[3],
				vertices[0], vertices[1], vertices[2], vertices[3]);
	}
}