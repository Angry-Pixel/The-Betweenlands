package thebetweenlands.client.model.baked.connectedtextures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.ElementsModel;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import thebetweenlands.api.client.ConnectedTextureHelper;
import thebetweenlands.common.TheBetweenlands;

public class ConnectedTextureGeometry implements IUnbakedGeometry<ConnectedTextureGeometry> {

	protected final BlockModel baseModel;
	protected final List<UnbakedConnectedTexturesQuad> connectedTextureQuads;

	public ConnectedTextureGeometry(BlockModel baseModel, List<UnbakedConnectedTexturesQuad> quads) {
		this.baseModel = baseModel;
		this.connectedTextureQuads = ImmutableList.copyOf(quads);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		BakedModel bakedBase = new ElementsModel(this.baseModel.getElements()).bake(context, baker, spriteGetter, modelState, overrides);
		if(this.connectedTextureQuads.isEmpty())
			return bakedBase;

		BakedConnectedTexturesQuad[] bakedQuads = new BakedConnectedTexturesQuad[connectedTextureQuads.size()];
		for(int i = 0; i < connectedTextureQuads.size(); ++i) {
			final UnbakedConnectedTexturesQuad unbakedQuad = connectedTextureQuads.get(i);
			bakedQuads[i] = BakedConnectedTexturesQuad.bakeFrom(unbakedQuad, context, baker, spriteGetter, modelState, overrides);
		}

		return new ConnectedTexturesDynamicModel(bakedBase, bakedQuads);
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		IUnbakedGeometry.super.resolveParents(modelGetter, context);
		this.baseModel.resolveParents(modelGetter);
	}

	public static class ConnectedTexturesDynamicModel implements IDynamicBakedModel {

		protected final BakedModel bakedBase;
		protected final BakedConnectedTexturesQuad[] bakedQuads;
		/// Sided version of bakedQuads
		protected final BakedConnectedTexturesQuad[][] connectedTextures;

		public ConnectedTexturesDynamicModel(BakedModel bakedBase, BakedConnectedTexturesQuad[] bakedQuads) {
			this.bakedBase = bakedBase;
			this.bakedQuads = bakedQuads;

			this.connectedTextures = new BakedConnectedTexturesQuad[Direction.values().length + 1][];

			final List<BakedConnectedTexturesQuad> connectedTextureQuads = new ArrayList<>();
			for(int i = 0; i < connectedTextures.length; ++i) {
				Direction face = i == 0 ? null : Direction.values()[i - 1];
				connectedTextureQuads.clear();
				for(BakedConnectedTexturesQuad tex : this.bakedQuads) {
					if(face == tex.cullface) {
						connectedTextureQuads.add(tex);
					}
				}
				this.connectedTextures[i == 0 ? 0 : face.get3DDataValue() + 1] = connectedTextureQuads.toArray(new BakedConnectedTexturesQuad[0]);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
			final List<BakedQuad> quads = new ArrayList<>(this.bakedBase.getQuads(state, side, rand, extraData, renderType));

			final int faceIndex = side == null ? 0 : side.get3DDataValue() + 1;
			final BakedConnectedTexturesQuad[] connectedTextures = this.connectedTextures[faceIndex];

			for (BakedConnectedTexturesQuad tex : connectedTextures) {
				for (int i = 0; i < 4; i++) {
					// tex.cullface filtered out in the constructor
					final ModelProperty<Boolean> cullfaceProperty = tex.cullfaceProperty;
					if (cullfaceProperty == null || !extraData.has(cullfaceProperty) || extraData.get(cullfaceProperty)) {
						final ModelProperty<?> indexProperty = tex.indexProperties[i];
						if (indexProperty != null) {
							final int index = extraData.has(indexProperty) ? (int) extraData.get(indexProperty) : 0;
							if (index != -1)
								quads.add(tex.quads[i][index]);
						} else {
							quads.add(tex.quads[i][tex.indices[i]]);
						}
					}
				}
			}

			return quads;
		}

		@Override
		public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
			ModelData data = IDynamicBakedModel.super.getModelData(level, pos, state, modelData);
			data = this.bakedBase.getModelData(level, pos, state, data);
			return ConnectedTextureHelper.getModelData(level, pos, state, data);
		}

		@Override
		public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
//			return ChunkRenderTypeSet.union(IDynamicBakedModel.super.getRenderTypes(state, rand, data), this.bakedBase.getRenderTypes(state, rand, data));
			return this.bakedBase.getRenderTypes(state, rand, data);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return bakedBase.useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return bakedBase.isGui3d();
		}

		@Override
		public boolean usesBlockLight() {
			return bakedBase.usesBlockLight();
		}

		@Override
		public boolean isCustomRenderer() {
			return true;
		}

		@SuppressWarnings("deprecation")
		@Override
		public TextureAtlasSprite getParticleIcon() {
			return bakedBase.getParticleIcon();
		}

		@Override
		public ItemOverrides getOverrides() {
			return bakedBase.getOverrides();
		}

	}

	public static class ConnectedTextureGeometryLoader implements IGeometryLoader<ConnectedTextureGeometry> {
	    public static final ConnectedTextureGeometryLoader INSTANCE = new ConnectedTextureGeometryLoader();

		@Override
		public ConnectedTextureGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			// Child block model
			BlockModel base;

			{
				// Trick it into parsing a normal model as well
				JsonObject copy = jsonObject.deepCopy();
				copy.remove("loader");
				base = deserializationContext.deserialize(copy, BlockModel.class);
			}

	        List<UnbakedConnectedTexturesQuad> quads = new ArrayList<>();
	        if(jsonObject.has("connected_texture_faces")) {
//	        	TheBetweenlands.LOGGER.info("Loading block model with connected texture loader");

	        	// Does type checks for us
	        	JsonArray facesArray = jsonObject.getAsJsonArray("connected_texture_faces");
				Preconditions.checkState(facesArray != null, "Connected texture model must provide a connected_texture_faces array");

	        	for(JsonElement element : facesArray) {
					/// I will not be changing the effects on the format of the final JSON
					JsonObject faceObject = element.getAsJsonObject();

					Preconditions.checkState(faceObject.has("indices") && faceObject.get("indices").isJsonArray() && faceObject.getAsJsonArray("indices").size() == 4, "Connected texture face must provide 4 indices");

					// Indices:
					// 		Number to always use that texture on a vertex
					// 		String to dynamically check what texture to use

					String[] indexNames = new String[4];
					int[] indices = new int[4];

					{
						JsonArray indicesArray = faceObject.getAsJsonArray("indices");
						for(int i = 0; i < 4; i++) {
							JsonPrimitive indexValue = indicesArray.get(i).getAsJsonPrimitive();
							try {
								indices[i] = indexValue.getAsInt();
							} catch(NumberFormatException ex) {
								indexNames[i] = indexValue.getAsString();
							}
						}
					}

					// Vertices: X, Y, Z and U, V coords. Goes from xyz 0.0, 0.0, 0.0 -> xyz 1.0, 1.0, 1.0

					Preconditions.checkState(faceObject.has("vertices") && faceObject.get("vertices").isJsonArray() && faceObject.getAsJsonArray("vertices").size() == 4, "Connected texture face must provide 4 vertices");
					ConnectedTexturesVertex[] vertices = new ConnectedTexturesVertex[4];

					{
						JsonArray verticesArray = faceObject.getAsJsonArray("vertices");
						for(int i = 0; i < 4; i++) {
							vertices[i] = ConnectedTexturesVertex.deserialize(verticesArray.get(i).getAsJsonObject());
						}
					}

					// Connected Textures: List of 5 textures.
					// 		Texture 0 is the default
					//		Texture 1 & 2 are for when there are no sides/top connected
					//		Texture 3 is for when no sides or top are connected
					//		Texture 4 is for when there are only corners connected

					Preconditions.checkState(faceObject.has("connected_textures") && faceObject.get("connected_textures").isJsonArray() && faceObject.getAsJsonArray("connected_textures").size() == 5, "Connected texture face must provide 5 textures");
					ResourceLocation[] textures = new ResourceLocation[5];
					{
						JsonArray texturesArray = faceObject.getAsJsonArray("connected_textures");
						for(int i = 0; i < 5; i++) {
							ResourceLocation location = ResourceLocation.tryParse(texturesArray.get(i).getAsString());
							if(location == null) location = MissingTextureAtlasSprite.getLocation();
							textures[i] = location;
						}
					}

					// Tint index for the textures
					int tintIndex = faceObject.has("tintindex") ? faceObject.get("tintindex").getAsInt() : -1;

					// Custom cull faces may need to be defined by blocks (e.g panes)
					String cullFaceProperty = null;
					Direction cullFace = null;
					if(faceObject.has("cullface")) {
						String cullFaceName = faceObject.get("cullface").getAsString();
						cullFace = Direction.byName(cullFaceName);
						if(cullFace == null) {
							cullFaceProperty = cullFaceName;
						}
					}


					float minU = 0, minV = 0, maxU = 1, maxV = 1;
					if(faceObject.has("minU")) {
						minU = faceObject.get("minU").getAsFloat();
					}
					if(faceObject.has("minV")) {
						minV = faceObject.get("minV").getAsFloat();
					}
					if(faceObject.has("maxU")) {
						maxU = faceObject.get("maxU").getAsFloat();
					}
					if(faceObject.has("maxV")) {
						maxV = faceObject.get("maxV").getAsFloat();
					}

					quads.add(new UnbakedConnectedTexturesQuad(textures, indexNames, indices, vertices, cullFace, cullFaceProperty, tintIndex, minU, minV, maxU, maxV));
	        	}
	        } else {
	        	TheBetweenlands.LOGGER.warn("Block model specifies connected texture loader, but is missing a \"connected_texture_faces\" key");
	        }

			return new ConnectedTextureGeometry(base, quads);
		}

	}

}
