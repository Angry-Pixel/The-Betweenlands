package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
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
import thebetweenlands.util.ModelConverter;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Model;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.Vec3UV;

public class ModelFromModelBase implements IModel {
	public static interface IVertexProcessor {
		Vec3UV process(Vec3UV vertexIn, Quad quad, Box box);
	}

	public final ModelBase model;
	public final ResourceLocation texture;
	public final int width;
	public final int height;
	public final IVertexProcessor vertexProcessor;

	public ModelFromModelBase(ModelBase model, ResourceLocation texture, int width, int height) {
		this(model, texture, width, height, null);
	}

	public ModelFromModelBase(ModelBase model, ResourceLocation texture, int width, int height, @Nullable IVertexProcessor vertexProcessor) {
		this.model = model;
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.vertexProcessor = vertexProcessor;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.of(this.texture);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = IPerspectiveAwareModel.MapWrapper.getTransforms(state);
		return new ModelBakedModelBase(this.vertexProcessor, state.apply(Optional.<IModelPart>absent()), map, format, this.model, bakedTextureGetter.apply(this.texture), this.width, this.height);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class ModelBakedModelBase implements IPerspectiveAwareModel {
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
		protected final VertexFormat format;
		protected final TextureAtlasSprite texture;
		protected final List<BakedQuad> quads;

		protected ModelBakedModelBase(IVertexProcessor vertexProcessor, Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, ModelBase model, TextureAtlasSprite texture, int width, int height) {
			this.transformation = transformation.isPresent() ? transformation.get() : null;
			this.transforms = transforms;
			this.format = format;
			this.texture = texture;
			this.quads = new ArrayList<BakedQuad>();
			ModelConverter converter = new ModelConverter(model, 0.0625D, true);
			Model convertedModel = converter.getModel();
			QuadBuilder builder = new QuadBuilder(this.format).setSprite(this.texture).setTransformation(this.transformation);
			for(Box box : convertedModel.getBoxes()) {
				for(Quad quad : box.getQuads()) {
					for(int i = 0; i < quad.getVertices().length; i++) {
						Vec3UV vert = quad.getVertices()[i];
						if(vertexProcessor != null)
							vert = vertexProcessor.process(vert, quad, box);
						builder.addVertex(vert.x + 0.5F, 1.5F - vert.y, vert.z + 0.5F, vert.getU(16.0F, width), vert.getV(16.0F, height));
					}
				}
			}
			this.quads.addAll(builder.build());
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState stateOld, EnumFacing side, long rand) {
			if(side == null)
				return Collections.synchronizedList(Lists.newArrayList(this.quads));
			return Collections.emptyList();
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