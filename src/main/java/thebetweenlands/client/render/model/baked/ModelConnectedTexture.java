package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
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
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.util.QuadBuilder;

public class ModelConnectedTexture implements IModel {
	protected static class ConnectedTextureQuad {
		protected final ResourceLocation[] textures;
		protected final String index0, index1, index2, index3;
		protected final Vec3d p1, p2, p3, p4;
		protected final EnumFacing cullFace;
		protected final int tintIndex;
		protected final BakedQuad[][] quads;

		protected ConnectedTextureQuad(ResourceLocation[] textures,
				String index0, String index1, String index2, String index3,
				Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4, EnumFacing cullFace,
				int tintIndex) {
			this.textures = textures;
			this.index0 = index0;
			this.index1 = index1;
			this.index2 = index2;
			this.index3 = index3;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
			this.cullFace = cullFace;
			this.tintIndex = tintIndex;
			this.quads = new BakedQuad[4][this.textures.length];
		}

		public BakedQuad[][] getQuads() {
			return this.quads;
		}

		public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter,
						 Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms,
						 VertexFormat format) {
			Vec3d p12 = this.p1.add(this.p2).scale(0.5D);
			Vec3d p23 = this.p2.add(this.p3).scale(0.5D);
			Vec3d p34 = this.p3.add(this.p4).scale(0.5D);
			Vec3d p14 = this.p4.add(this.p1).scale(0.5D);
			Vec3d cp = p12.add(p34).scale(0.5D);

			TextureAtlasSprite[] sprites = new TextureAtlasSprite[this.textures.length];
			for(int i = 0; i < sprites.length; i++) {
				sprites[i] = bakedTextureGetter.apply(this.textures[i]);
			}

			this.quads[0] = this.bakeTextureVariants(format, transformation, sprites, this.p1, p12, cp, p14, 0);
			this.quads[1] = this.bakeTextureVariants(format, transformation, sprites, p12, this.p2, p23, cp, 2);
			this.quads[2] = this.bakeTextureVariants(format, transformation, sprites, cp, p23, this.p3, p34, 3);
			this.quads[3] = this.bakeTextureVariants(format, transformation, sprites, p14, cp, p34, this.p4, 1);
		}

		protected BakedQuad[] bakeTextureVariants(VertexFormat format, Optional<TRSRTransformation> transformation,
				TextureAtlasSprite[] sprites, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4, int quadrant) {
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
			QuadBuilder builder = new QuadBuilder(4 * this.textures.length, format);
			if(transformation.isPresent()) {
				builder.setTransformation(transformation.get());
			}
			for(int i = 0; i < sprites.length; i++) {
				builder.setSprite(sprites[i]);
				builder.addVertex(p1, umin, vmin);
				builder.addVertex(p2, umin, vmax);
				builder.addVertex(p3, umax, vmax);
				builder.addVertex(p4, umax, vmin);
			}
			return builder.build(b -> b.setQuadTint(this.tintIndex)).nonCulledQuads.toArray(new BakedQuad[0]);
		}
	}

	protected final List<ConnectedTextureQuad> connectedTextures;
	protected final ResourceLocation particleTexture;
	protected final boolean ambientOcclusion;

	public ModelConnectedTexture() {
		this(TextureMap.LOCATION_MISSING_TEXTURE, true, ImmutableList.of());
	}

	public ModelConnectedTexture(ResourceLocation particleTexture, boolean ambientOcclusion, List<ConnectedTextureQuad> textures) {
		this.particleTexture = particleTexture;
		this.ambientOcclusion = ambientOcclusion;
		this.connectedTextures = textures;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		if(!this.connectedTextures.isEmpty()) {
			List<ResourceLocation> textures = new ArrayList<>();
			for(ConnectedTextureQuad quad : this.connectedTextures) {
				Collections.addAll(textures, quad.textures);
			}
			return textures;
		}
		return ImmutableList.of();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
		Optional<TRSRTransformation> transformation = state.apply(Optional.empty());
		for(ConnectedTextureQuad connectedTexture : this.connectedTextures) {
			connectedTexture.bake(bakedTextureGetter, transformation, map, format);
		}
		return new ModelBakedConnectedFace(transformation, map, format, bakedTextureGetter.apply(this.particleTexture),
				this.ambientOcclusion, this.connectedTextures);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class ModelBakedConnectedFace implements IBakedModel {
		protected final ConnectedTextureQuad[][] connectedTextures;
		protected final TextureAtlasSprite particleTexture;
		protected final boolean ambientOcclusion;
		protected final VertexFormat format;
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;

		private ModelBakedConnectedFace(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, 
				VertexFormat format, TextureAtlasSprite particleTexture, boolean ambientOcclusion, List<ConnectedTextureQuad> connectedTextures) {
			this.ambientOcclusion = ambientOcclusion;
			this.format = format;
			this.transforms = transforms;
			this.transformation = transformation.isPresent() ? transformation.get() : null;
			this.particleTexture = particleTexture;

			this.connectedTextures = new ConnectedTextureQuad[EnumFacing.VALUES.length + 1][];
			List<ConnectedTextureQuad> connectedTextureQuads = new ArrayList<>();
			for(int i = 0; i < EnumFacing.VALUES.length + 1; i++) {
				EnumFacing face = i == 0 ? null : EnumFacing.VALUES[i - 1];
				connectedTextureQuads.clear();
				for(ConnectedTextureQuad tex : connectedTextures) {
					if(face == tex.cullFace) {
						connectedTextureQuads.add(tex);
					}
				}
				this.connectedTextures[i == 0 ? 0 : face.getIndex() + 1] = connectedTextureQuads.toArray(new ConnectedTextureQuad[0]);
			}
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState stateOld, EnumFacing side, long rand) {
			IExtendedBlockState state = (IExtendedBlockState) stateOld;
			ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = state.getUnlistedProperties();
			int faceIndex = side == null ? 0 : side.getIndex() + 1;
			ConnectedTextureQuad[] connectedTextures = this.connectedTextures[faceIndex];

			if(connectedTextures.length > 0) {
				ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

				for(ConnectedTextureQuad tex : connectedTextures) {
					int[] indices = new int[4];

					for(Entry<IUnlistedProperty<?>, Optional<?>> entry : properties.entrySet()) {
						String property = entry.getKey().getName();

						if(tex.index0.equals(property)) {
							indices[0] = (Integer) entry.getValue().get();
						} else if(tex.index1.equals(property)) {
							indices[1] = (Integer) entry.getValue().get();
						} else if(tex.index2.equals(property)) {
							indices[2] = (Integer) entry.getValue().get();
						} else if(tex.index3.equals(property)) {
							indices[3] = (Integer) entry.getValue().get();
						}
					}

					for(int i = 0; i < 4; i++) {
						builder.add(tex.quads[i][indices[i]]);
					}
				}

				return builder.build();
			}

			return ImmutableList.of();
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
			return PerspectiveMapWrapper.handlePerspective(this, this.transforms, type);
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.particleTexture;
		}
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		boolean changed = false;

		List<ConnectedTextureQuad> connectedTextures = this.connectedTextures;
		if(customData.containsKey("connected_texture_faces")) {
			changed = true;
			connectedTextures = new ArrayList<>();

			JsonElement jsonObj = parser.parse(customData.get("connected_texture_faces"));
			Preconditions.checkState(jsonObj != null && !jsonObj.isJsonNull() && jsonObj.isJsonArray(), "Connected texture model must provide a connected_texture_faces array");
			JsonArray array = jsonObj.getAsJsonArray();

			for(JsonElement element : array) {
				JsonObject ctJson = element.getAsJsonObject();

				Preconditions.checkState(ctJson.has("indices") && ctJson.get("indices").isJsonArray() && ctJson.get("indices").getAsJsonArray().size() == 4, "Connected texture face must provide 4 indices");
				String[] indices = new String[4];
				JsonArray indicesArray = ctJson.get("indices").getAsJsonArray();
				for(int i = 0; i < 4; i++) {
					indices[i] = indicesArray.get(i).getAsString();
				}

				Preconditions.checkState(ctJson.has("vertices") && ctJson.get("vertices").isJsonArray() && ctJson.get("vertices").getAsJsonArray().size() == 4, "Connected texture face must provide 4 vertices");
				Vec3d[] vertices = new Vec3d[4];
				JsonArray verticesArray = ctJson.get("vertices").getAsJsonArray();
				for(int i = 0; i < 4; i++) {
					JsonObject vertexJson = verticesArray.get(i).getAsJsonObject();
					vertices[i] = new Vec3d(vertexJson.get("x").getAsDouble(), vertexJson.get("y").getAsDouble(), vertexJson.get("z").getAsDouble());
				}

				Preconditions.checkState(ctJson.has("connected_textures") && ctJson.get("connected_textures").isJsonArray() && ctJson.get("connected_textures").getAsJsonArray().size() == 5, "Connected texture face must provide 5 textures");
				ResourceLocation[] textures = new ResourceLocation[5];
				JsonArray texturesArray = ctJson.get("connected_textures").getAsJsonArray();
				for(int i = 0; i < 5; i++) {
					textures[i] = new ResourceLocation(texturesArray.get(i).getAsString());
				}

				int tintIndex = -1;
				if(ctJson.has("tintindex")) {
					tintIndex = ctJson.get("tintindex").getAsInt();
				}

				EnumFacing cullFace = null;
				if(ctJson.has("cullface")) {
					cullFace = EnumFacing.byName(ctJson.get("cullface").getAsString());
				}

				connectedTextures.add(new ConnectedTextureQuad(textures, indices[0], indices[1], indices[2], indices[3],
						vertices[0], vertices[1], vertices[2], vertices[3], cullFace, tintIndex));
			}
		}

		boolean ambientOcclusion = this.ambientOcclusion;
		if(customData.containsKey("ambient_occlusion")) {
			changed = true;
			ambientOcclusion = parser.parse(customData.get("ambient_occlusion")).getAsBoolean();
		}

		ResourceLocation particleTexture = this.particleTexture;
		if(customData.containsKey("particle_texture")) {
			changed = true;
			particleTexture = new ResourceLocation(parser.parse(customData.get("particle_texture")).getAsString());
		}

		if(changed) {
			return new ModelConnectedTexture(particleTexture == null ? TextureMap.LOCATION_MISSING_TEXTURE : particleTexture, ambientOcclusion, connectedTextures);
		}

		return this;
	}
}