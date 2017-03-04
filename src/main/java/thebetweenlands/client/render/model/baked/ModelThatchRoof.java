package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockStairs.EnumHalf;
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
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import thebetweenlands.common.block.structure.BlockThatchRoof;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.QuadBuilder;

public class ModelThatchRoof implements IModel {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "blocks/thatch");

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Collections.unmodifiableCollection(Arrays.asList(new ResourceLocation[]{TEXTURE}));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = IPerspectiveAwareModel.MapWrapper.getTransforms(state);
		return new ModelBakedThatchRoof(state.apply(Optional.<IModelPart>absent()), map, format, bakedTextureGetter.apply(TEXTURE));
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class ModelBakedThatchRoof implements IPerspectiveAwareModel {
		private final VertexFormat format;
		private final TextureAtlasSprite texture;
		private float slopeEdge = 1.0F / 16.0F * 3.0F;
		private final EnumMap<EnumFacing, List<BakedQuad>> faceQuads;
		private final List<BakedQuad> nonCulledQuads = new ArrayList<>();
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;

		private ModelBakedThatchRoof(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, TextureAtlasSprite texture) {
			this.transformation = transformation.isPresent() ? transformation.get() : null;
			this.transforms = transforms;
			this.format = format;
			this.texture = texture;

			this.faceQuads = Maps.newEnumMap(EnumFacing.class);
			for(EnumFacing side : EnumFacing.values()) {
				this.faceQuads.put(side, ImmutableList.<BakedQuad>of());
			}
		}

		private ModelBakedThatchRoof(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, TextureAtlasSprite texture, Integer key) {
			this(transformation, transforms, format, texture);

			boolean corner1 = (key & 0x1) != 0;
			boolean corner2 = (key & 0x2) != 0;
			boolean corner3 = (key & 0x4) != 0;
			boolean corner4 = (key & 0x8) != 0;
			boolean upsidedown = (key & 0x10) != 0;

			float cornerHeight1 = corner1 ? 1.0F : this.slopeEdge;
			float cornerHeight2 = corner2 ? 1.0F : this.slopeEdge;
			float cornerHeight3 = corner3 ? 1.0F : this.slopeEdge;
			float cornerHeight4 = corner4 ? 1.0F : this.slopeEdge;

			QuadBuilder builder = new QuadBuilder(this.format).setTransformation(this.transformation).setSprite(this.texture);

			if(!upsidedown) {
				//z- face
				builder.addVertex(0, 0, 0.0001f, 0, 0);
				builder.addVertex(0, cornerHeight1, 0.0001f, 0, cornerHeight1*16.0F);
				builder.addVertex(1, cornerHeight2, 0.0001f, 16, cornerHeight2*16.0F);
				builder.addVertex(1, 0, 0.0001f, 16, 0);
				this.faceQuads.put(EnumFacing.NORTH, builder.build());

				//z+ face
				builder.addVertex(0, 0, 1, 0, 0);
				builder.addVertex(1, 0, 1, 16, 0);
				builder.addVertex(1, cornerHeight3, 1, 16, cornerHeight3*16.0F);
				builder.addVertex(0, cornerHeight4, 1, 0, cornerHeight4*16.0F);
				this.faceQuads.put(EnumFacing.SOUTH, builder.build());

				//x+ face
				builder.addVertex(1, 0, 0, 0, 0);
				builder.addVertex(1, cornerHeight2, 0, 0, cornerHeight2*16.0F);
				builder.addVertex(1, cornerHeight3, 1, 16, cornerHeight3*16.0F);
				builder.addVertex(1, 0, 1, 16, 0);
				this.faceQuads.put(EnumFacing.EAST, builder.build());

				//x- face
				builder.addVertex(0.0001f, 0, 0, 0, 0);
				builder.addVertex(0.0001f, 0, 1, 16, 0);
				builder.addVertex(0.0001f, cornerHeight4, 1, 16, cornerHeight4*16.0F);
				builder.addVertex(0.0001f, cornerHeight1, 0, 0, cornerHeight1*16.0F);
				this.faceQuads.put(EnumFacing.WEST, builder.build());

				//top face
				if((corner2 || corner4) && (!corner1 && !corner3) || (corner1 == corner3)) {
					builder.addVertex(0, cornerHeight4, 1, 16, 0);
					builder.addVertex(1, cornerHeight3, 1, 16, 16);
					builder.addVertex(1, cornerHeight2, 0, 0, 16);
					builder.addVertex(0, cornerHeight1, 0, 0, 0);
				} else {
					builder.addVertex(0, cornerHeight1, 0, 0, 0);
					builder.addVertex(0, cornerHeight4, 1, 16, 0);
					builder.addVertex(1, cornerHeight3, 1, 16, 16);
					builder.addVertex(1, cornerHeight2, 0, 0, 16);
				}
				this.nonCulledQuads.addAll(builder.build());

				//bottom face
				builder.addVertex(0, 0, 0, 0, 0);
				builder.addVertex(1, 0, 0, 0, 16);
				builder.addVertex(1, 0, 1, 16, 16);
				builder.addVertex(0, 0, 1, 16, 0);
				this.faceQuads.put(EnumFacing.DOWN, builder.build());
			} else {
				//z- face
				builder.addVertex(0, 1, 0.0001f, 0, 0);
				builder.addVertex(1, 1, 0.0001f, 16, 0);
				builder.addVertex(1, 1-cornerHeight2, 0.0001f, 16, cornerHeight2*16.0F);
				builder.addVertex(0, 1-cornerHeight1, 0.0001f, 0, cornerHeight1*16.0F);
				this.faceQuads.put(EnumFacing.NORTH, builder.build());

				//z+ face
				builder.addVertex(0, 1, 1, 0, 0);
				builder.addVertex(0, 1-cornerHeight4, 1, 0, cornerHeight4*16.0F);
				builder.addVertex(1, 1-cornerHeight3, 1, 16, cornerHeight3*16.0F);
				builder.addVertex(1, 1, 1, 16, 0);
				this.faceQuads.put(EnumFacing.SOUTH, builder.build());

				//x+ face
				builder.addVertex(1, 1, 0, 0, 0);
				builder.addVertex(1, 1, 1, 16, 0);
				builder.addVertex(1, 1-cornerHeight3, 1, 16, cornerHeight3*16.0F);
				builder.addVertex(1, 1-cornerHeight2, 0, 0, cornerHeight2*16.0F);
				this.faceQuads.put(EnumFacing.EAST, builder.build());

				//x- face
				builder.addVertex(0.0001f, 1, 0, 0, 0);
				builder.addVertex(0.0001f, 1-cornerHeight1, 0, 0, cornerHeight1*16.0F);
				builder.addVertex(0.0001f, 1-cornerHeight4, 1, 16, cornerHeight4*16.0F);
				builder.addVertex(0.0001f, 1, 1, 16, 0);
				this.faceQuads.put(EnumFacing.WEST, builder.build());

				//bottom face
				if((corner2 || corner4) && (!corner1 && !corner3) || (corner1 == corner3)) {
					builder.addVertex(0, 1-cornerHeight4, 1, 16, 0);
					builder.addVertex(0, 1-cornerHeight1, 0, 0, 0);
					builder.addVertex(1, 1-cornerHeight2, 0, 0, 16);
					builder.addVertex(1, 1-cornerHeight3, 1, 16, 16);
				} else {
					builder.addVertex(0, 1-cornerHeight1, 0, 0, 0);
					builder.addVertex(1, 1-cornerHeight2, 0, 0, 16);
					builder.addVertex(1, 1-cornerHeight3, 1, 16, 16);
					builder.addVertex(0, 1-cornerHeight4, 1, 16, 0);
				}
				this.nonCulledQuads.addAll(builder.build());

				//top face
				builder.addVertex(0, 1, 0, 0, 0);
				builder.addVertex(0, 1, 1, 16, 0);
				builder.addVertex(1, 1, 1, 16, 16);
				builder.addVertex(1, 1, 0, 0, 16);
				this.faceQuads.put(EnumFacing.UP, builder.build());
			}
		}

		private final LoadingCache<Integer, ModelBakedThatchRoof> modelCache = CacheBuilder.newBuilder().maximumSize(64).build(new CacheLoader<Integer, ModelBakedThatchRoof>() {
			@Override
			public ModelBakedThatchRoof load(Integer key) throws Exception {
				return new ModelBakedThatchRoof(ModelBakedThatchRoof.this.transformation != null ? Optional.of(ModelBakedThatchRoof.this.transformation) : Optional.absent(), ModelBakedThatchRoof.this.transforms, ModelBakedThatchRoof.this.format, ModelBakedThatchRoof.this.texture, key);
			}
		});

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			boolean upsidedown = state != null ? state.getValue(BlockThatchRoof.HALF) == EnumHalf.TOP : false;
			boolean corner1 = false;
			boolean corner2 = false;
			boolean corner3 = false;
			boolean corner4 = false;

			if(state instanceof IExtendedBlockState) {
				IExtendedBlockState extendedState = (IExtendedBlockState) state;
				corner1 = extendedState.getValue(BlockThatchRoof.CORNER_1);
				corner2 = extendedState.getValue(BlockThatchRoof.CORNER_2);
				corner3 = extendedState.getValue(BlockThatchRoof.CORNER_3);
				corner4 = extendedState.getValue(BlockThatchRoof.CORNER_4);
			} else {
				corner1 = true;
				corner2 = false;
				corner3 = false;
				corner4 = true;
			}

			int index = 0;
			if(corner1) index |= 0x1;
			if(corner2) index |= 0x2;
			if(corner3) index |= 0x4;
			if(corner4) index |= 0x8;
			if(upsidedown) index |= 0x10;

			ModelBakedThatchRoof model = this.modelCache.getUnchecked(index);

			return side == null ? model.nonCulledQuads : model.faceQuads.get(side);
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
			return this.texture;
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
	}
}