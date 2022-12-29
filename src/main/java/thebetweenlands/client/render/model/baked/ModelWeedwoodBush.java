package thebetweenlands.client.render.model.baked;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.StatePropertyHelper;

public class ModelWeedwoodBush implements IModel {
	private final ResourceLocation leavesTexture;
	private final ResourceLocation sticksTexture;
	private final int leavesTintIndex;

	public ModelWeedwoodBush() {
		this.leavesTexture = new ResourceLocation(ModInfo.ID, "blocks/leaves_weedwood_bush");
		this.sticksTexture = new ResourceLocation(ModInfo.ID, "items/weedwood_stick");
		this.leavesTintIndex = 0;
	}

	public ModelWeedwoodBush(ResourceLocation leaves, ResourceLocation sticks, int leavesTintIndex) {
		this.leavesTexture = leaves;
		this.sticksTexture = sticks;
		this.leavesTintIndex = leavesTintIndex;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Collections.unmodifiableCollection(Arrays.asList(new ResourceLocation[]{this.leavesTexture, this.sticksTexture}));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
		return new ModelBakedWeedwoodBush(format, state.apply(Optional.empty()), map, bakedTextureGetter.apply(this.leavesTexture), bakedTextureGetter.apply(this.sticksTexture), this.leavesTintIndex);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	@Override
	public ModelWeedwoodBush process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		ResourceLocation leaves = this.leavesTexture;

		if(customData.containsKey("texture_leaves")) {
			leaves = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("texture_leaves")), "texture_leaves"));
		}

		if(leaves == null) {
			leaves = TextureMap.LOCATION_MISSING_TEXTURE;
		}

		ResourceLocation sticks = this.sticksTexture;

		if(customData.containsKey("texture_sticks")) {
			sticks = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("texture_sticks")), "texture_sticks"));
		}

		if(sticks == null) {
			sticks = TextureMap.LOCATION_MISSING_TEXTURE;
		}	

		int leavesTintIndex = this.leavesTintIndex;

		if(customData.containsKey("tint_index_leaves")) {
			leavesTintIndex = JsonUtils.getInt(parser.parse(customData.get("tint_index_leaves")), "tint_index_leaves");
		}

		return new ModelWeedwoodBush(leaves, sticks, leavesTintIndex);
	}

	public static class ModelBakedWeedwoodBush implements IBakedModel {
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private final VertexFormat format;
		private final TextureAtlasSprite textureLeaves;
		private final TextureAtlasSprite textureSticks;
		private final int leavesTintIndex;
		private final List<BakedQuad> baseQuads;
		private final List<BakedQuad> fancyQuads;

		private final LoadingCache<Long, ModelBakedWeedwoodBush> modelCache = CacheBuilder.newBuilder().maximumSize(128).build(new CacheLoader<Long, ModelBakedWeedwoodBush>() {
			@Override
			public ModelBakedWeedwoodBush load(Long key) throws Exception {
				return new ModelBakedWeedwoodBush(format, Optional.ofNullable(transformation), transforms, textureLeaves, textureSticks, leavesTintIndex, key);
			}
		});

		private ModelBakedWeedwoodBush(VertexFormat format, Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, TextureAtlasSprite textureLeaves, TextureAtlasSprite textureSticks, int leavesTintIndex) {
			this(format, transformation, transforms, textureLeaves, textureSticks, leavesTintIndex, -1);
		}

		private ModelBakedWeedwoodBush(VertexFormat format, Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, TextureAtlasSprite textureLeaves, TextureAtlasSprite textureSticks, int leavesTintIndex,
				long index) {
			this.transformation = transformation.isPresent() ? transformation.get() : null;
			this.transforms = transforms;
			this.format = format;
			this.textureLeaves = textureLeaves;
			this.textureSticks = textureSticks;
			this.leavesTintIndex = leavesTintIndex;

			if(index != -1) {
				QuadBuilder builder = new QuadBuilder(this.format).setTransformation(this.transformation);

				builder.setTintIndex(leavesTintIndex);
				builder.setSprite(this.textureLeaves);

				float mini = (index & 1) != 0 ? -0.25F : 0.0F;
				float maxi = (index & (1 << 1)) != 0 ? 0.25F : 0.0F;
				float minj = (index & (1 << 2)) != 0 ? -0.25F : 0.0F;
				float maxj = (index & (1 << 3)) != 0 ? 0.25F : 0.0F;
				float mink = (index & (1 << 4)) != 0 ? -0.25F : 0.0F;
				float maxk = (index & (1 << 5)) != 0 ? 0.25F : 0.0F;

				// Right Side
				builder.addVertex(0, 0.25 + minj, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(0, 0.75 + maxj, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(0, 0.75 + maxj, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(0, 0.25 + minj, 0.25 + mink, 16.0F, 0.0F);

				// Right-Top Side
				builder.addVertex(0, 0.75, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(0.25, 1, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(0.25, 1, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(0, 0.75, 0.25 + mink, 16.0F, 0.0F);

				// Right-Bottom Side
				builder.addVertex(0.25, 0.0, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(0.0, 0.25, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(0.0, 0.25, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(0.25, 0.0, 0.25 + mink, 16.0F, 0.0F);

				// Left Side
				builder.addVertex(1, 0.25 + minj, 0.25 + mink, 0.0F, 0.0F);
				builder.addVertex(1, 0.75 + maxj, 0.25 + mink, 0.0F, 16.0F);
				builder.addVertex(1, 0.75 + maxj, 0.75 + maxk, 16.0F, 16.0F);
				builder.addVertex(1, 0.25 + minj, 0.75 + maxk, 16.0F, 0.0F);

				// Left-Top Side
				builder.addVertex(0.75, 1, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(1, 0.75, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(1, 0.75, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(0.75, 1, 0.25 + mink, 16.0F, 0.0F);

				// Left-Bottom Side
				builder.addVertex(1, 0.25, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(0.75, 0.0, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(0.75, 0.0, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(1, 0.25, 0.25 + mink, 16.0F, 0.0F);

				// Front Side
				builder.addVertex(0.25 + mini, 0.25 + minj, 0, 0.0F, 0.0F);
				builder.addVertex(0.25 + mini, 0.75 + maxj, 0, 0.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.75 + maxj, 0, 16.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.25 + minj, 0, 16.0F, 0.0F);

				// Front-Right Side
				builder.addVertex(0.0, 0.25 + minj, 0.25, 0.0F, 0.0F);
				builder.addVertex(0.0, 0.75 + maxj, 0.25, 0.0F, 16.0F);
				builder.addVertex(0.25, 0.75 + maxj, 0, 16.0F, 16.0F);
				builder.addVertex(0.25, 0.25 + minj, 0, 16.0F, 0.0F);

				// Front-Left Side
				builder.addVertex(0.75, 0.25 + minj, 0.0, 0.0F, 0.0F);
				builder.addVertex(0.75, 0.75 + maxj, 0.0, 0.0F, 16.0F);
				builder.addVertex(1, 0.75 + maxj, 0.25, 16.0F, 16.0F);
				builder.addVertex(1, 0.25 + minj, 0.25, 16.0F, 0.0F);

				// Front-Top Side
				builder.addVertex(0.25 + mini, 0.75, 0, 0.0F, 0.0F);
				builder.addVertex(0.25 + mini, 1, 0.25, 0.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 1, 0.25, 16.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.75, 0, 16.0F, 0.0F);

				// Front-Bottom Side
				builder.addVertex(0.25 + mini, 0.0, 0.25, 0.0F, 0.0F);
				builder.addVertex(0.25 + mini, 0.25, 0.0, 0.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.25, 0.0, 16.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.0, 0.25, 16.0F, 0.0F);

				// Back Side
				builder.addVertex(0.75 + maxi, 0.25 + minj, 1, 0.0F, 0.0F);
				builder.addVertex(0.75 + maxi, 0.75 + maxj, 1, 0.0F, 16.0F);
				builder.addVertex(0.25 + mini, 0.75 + maxj, 1, 16.0F, 16.0F);
				builder.addVertex(0.25 + mini, 0.25 + minj, 1, 16.0F, 0.0F);

				// Back-Top Side
				builder.addVertex(0.25 + mini, 0.75, 1, 0.0F, 0.0F);
				builder.addVertex(0.75 + maxi, 0.75, 1, 0.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 1, 0.75, 16.0F, 16.0F);
				builder.addVertex(0.25 + mini, 1, 0.75, 16.0F, 0.0F);

				// Back-Left Side
				builder.addVertex(1, 0.25 + minj, 0.75, 0.0F, 0.0F);
				builder.addVertex(1, 0.75 + maxj, 0.75, 0.0F, 16.0F);
				builder.addVertex(0.75, 0.75 + maxj, 1, 16.0F, 16.0F);
				builder.addVertex(0.75, 0.25 + minj, 1, 16.0F, 0.0F);

				// Back-Right Side
				builder.addVertex(0.25, 0.25 + minj, 1, 0.0F, 0.0F);
				builder.addVertex(0.25, 0.75 + maxj, 1, 0.0F, 16.0F);
				builder.addVertex(0, 0.75 + maxj, 0.75, 16.0F, 16.0F);
				builder.addVertex(0, 0.25 + minj, 0.75, 16.0F, 0.0F);

				// Back-Bottom Side
				builder.addVertex(0.25 + mini, 0, 0.75, 0.0F, 0.0F);
				builder.addVertex(0.75 + maxi, 0, 0.75, 0.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.25, 1, 16.0F, 16.0F);
				builder.addVertex(0.25 + mini, 0.25, 1, 16.0F, 0.0F);

				// Top Side
				builder.addVertex(0.25 + mini, 1, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(0.75 + maxi, 1, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 1, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(0.25 + mini, 1, 0.25 + mink, 16.0F, 0.0F);

				// Bottom Side
				builder.addVertex(0.75 + maxi, 0.0, 0.75 + maxk, 0.0F, 0.0F);
				builder.addVertex(0.25 + mini, 0.0, 0.75 + maxk, 0.0F, 16.0F);
				builder.addVertex(0.25 + mini, 0.0, 0.25 + mink, 16.0F, 16.0F);
				builder.addVertex(0.75 + maxi, 0.0, 0.25 + mink, 16.0F, 0.0F);

				// Corners
				builder.addVertex(1, 0.25, 0.25, 0.0F, 0.0F);
				builder.addVertex(0.75, 0.0, 0.25, 0.0F, 16.0F);
				builder.addVertex(0.75, 0.25, 0.0, 16.0F, 16.0F);
				builder.addVertex(0.75, 0.25, 0.0, 16.0F, 0.0F);

				builder.addVertex(0.75, 0.25, 1, 0.0F, 0.0F);
				builder.addVertex(0.75, 0.0, 0.75, 0.0F, 16.0F);
				builder.addVertex(1, 0.25, 0.75, 16.0F, 16.0F);
				builder.addVertex(1, 0.25, 0.75, 16.0F, 0.0F);

				builder.addVertex(0.75, 1, 0.75, 0.0F, 0.0F);
				builder.addVertex(0.75, 0.75, 1, 0.0F, 16.0F);
				builder.addVertex(1, 0.75, 0.75, 16.0F, 16.0F);
				builder.addVertex(1, 0.75, 0.75, 16.0F, 0.0F);

				builder.addVertex(0.75, 0.75, 0, 0.0F, 0.0F);
				builder.addVertex(0.75, 1, 0.25, 0.0F, 16.0F);
				builder.addVertex(1, 0.75, 0.25, 16.0F, 16.0F);
				builder.addVertex(0.75, 0.75, 0, 16.0F, 0.0F);

				builder.addVertex(0.0, 0.25, 0.25, 0.0F, 0.0F);
				builder.addVertex(0.25, 0.25, 0.0, 0.0F, 16.0F);
				builder.addVertex(0.25, 0.0, 0.25, 16.0F, 16.0F);
				builder.addVertex(0.25, 0.0, 0.25, 16.0F, 0.0F);

				builder.addVertex(0.25, 1, 0.75, 0.0F, 0.0F);
				builder.addVertex(0.0, 0.75, 0.75, 0.0F, 16.0F);
				builder.addVertex(0.25, 0.75, 1, 16.0F, 16.0F);
				builder.addVertex(0.25, 0.75, 1, 16.0F, 0.0F);

				builder.addVertex(0.25, 0.25, 1, 0.0F, 0.0F);
				builder.addVertex(0.0, 0.25, 0.75, 0.0F, 16.0F);
				builder.addVertex(0.25, 0.0, 0.75, 16.0F, 16.0F);
				builder.addVertex(0.25, 0.0, 0.75, 16.0F, 0.0F);

				builder.addVertex(0.25, 1, 0.25, 0.0F, 0.0F);
				builder.addVertex(0.25, 0.75, 0.0, 0.0F, 16.0F);
				builder.addVertex(0.0, 0.75, 0.25, 16.0F, 16.0F);
				builder.addVertex(0.0, 0.75, 0.25, 16.0F, 0.0F);

				this.baseQuads = builder.build().nonCulledQuads;

				builder.addVertex(0.1, 0.5, -0.1, 0.0F, 0.0F);
				builder.addVertex(0.5, 1.1, 0.5, 16.0F, 0.0F);
				builder.addVertex(0.9, 0.5, 1.1, 16.0F, 16.0F);
				builder.addVertex(0.5, -0.1, 0.5, 0.0F, 16.0F);

				builder.addVertex(0.9, 0.5, 1.1, 0.0F, 0.0F);
				builder.addVertex(0.5, 1.1, 0.5, 16.0F, 0.0F);
				builder.addVertex(0.1, 0.5, -0.1, 16.0F, 16.0F);
				builder.addVertex(0.5, -0.1, 0.5, 0.0F, 16.0F);

				builder.addVertex(0.1, 0.5, 0.7, 0.0F, 0.0F);
				builder.addVertex(0.5, 1.1, 0.5, 16.0F, 0.0F);
				builder.addVertex(0.9, 0.5, 0.3, 16.0F, 16.0F);
				builder.addVertex(0.5, -0.1, 0.5, 0.0F, 16.0F);

				builder.addVertex(0.9, 0.5, 0.3, 0.0F, 0.0F);
				builder.addVertex(0.3, 1.1, 0.5, 16.0F, 0.0F);
				builder.addVertex(0.1, 0.5, 0.7, 16.0F, 16.0F);
				builder.addVertex(0.5, -0.1, 0.5, 0.0F, 16.0F);

				builder.addVertex(0.3, 0.5, 1.1, 0.0F, 0.0F);
				builder.addVertex(0.5, 1.1, 0.5, 16.0F, 0.0F);
				builder.addVertex(0.9, 0.5, -0.1, 16.0F, 16.0F);
				builder.addVertex(0.5, -0.1, 0.5, 0.0F, 16.0F);

				builder.addVertex(0.9, 0.5, -0.1, 0.0F, 0.0F);
				builder.addVertex(0.5, 1.1, 0.5, 16.0F, 0.0F);
				builder.addVertex(0.3, 0.5, 1.1, 16.0F, 16.0F);
				builder.addVertex(0.5, -0.1, 0.5, 0.0F, 16.0F);

				this.fancyQuads = builder.build().nonCulledQuads;
			} else {
				this.baseQuads = this.fancyQuads = Collections.emptyList();
			}
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if(side == null) {
				int posX = 0, posY = 0, posZ = 0;

				long index = 0;

				if (StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.WEST).orElse(false))
					index |= 1;
				if (StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.EAST).orElse(false))
					index |= 1 << 1;
				if (StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.DOWN).orElse(false))
					index |= 1 << 2;
				if (StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.UP).orElse(false))
					index |= 1 << 3;
				if (StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.NORTH).orElse(false))
					index |= 1 << 4;
				if (StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.SOUTH).orElse(false))
					index |= 1 << 5;
				
				posX = StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.POS_X).orElse(0);
				posY = StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.POS_Y).orElse(0);
				posZ = StatePropertyHelper.getPropertyOptional(state, BlockWeedwoodBush.POS_Z).orElse(0);

				ModelBakedWeedwoodBush model = this.modelCache.getUnchecked(index);

				List<BakedQuad> quads = model.baseQuads;

				if (Minecraft.isFancyGraphicsEnabled()) {
					quads = new CompositeList<>(quads, model.fancyQuads);

					int cSticks = 5;

					Random rnd = new Random();
					long seed = posX * 0x2FC20FL ^ posY * 0x6EBFFF5L ^ posZ;
					rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);

					QuadBuilder builder = new QuadBuilder(this.format).setTransformation(this.transformation);
					builder.setSprite(this.textureSticks);
					builder.setTintIndex(-1);

					for(int i = 0; i < cSticks; i++) {
						double rotation = Math.PI * 2.0f / (float)cSticks * (float)i;
						double xp1 = Math.sin(rotation) * 0.4f;
						double zp1 = Math.cos(rotation) * 0.4f;
						double xp2 = Math.sin(rotation+Math.PI/2.0f) * 0.4f;
						double zp2 = Math.cos(rotation+Math.PI/2.0f) * 0.4f;
						double xp3 = Math.sin(rotation+Math.PI) * 0.4f;
						double zp3 = Math.cos(rotation+Math.PI) * 0.4f;
						double xp4 = Math.sin(rotation+Math.PI+Math.PI/2.0f) * 0.4f;
						double zp4 = Math.cos(rotation+Math.PI+Math.PI/2.0f) * 0.4f;
						float xOff = (rnd.nextFloat() * 2.0f - 1.0f) * 0.4f + 0.5f;
						float yOff = (rnd.nextFloat() * 2.0f - 1.0f) * 0.4f;
						float zOff = (rnd.nextFloat() * 2.0f - 1.0f) * 0.4f + 0.5f;

						builder.addVertex(xp1+xOff, 0.8+yOff, zp1+zOff, 0.0F, 0.0F);
						builder.addVertex(xp2+xOff, 0.8+yOff, zp2+zOff, 16.0F, 0.0F);
						builder.addVertex(xp3+xOff, 0.2+yOff, zp3+zOff, 16.0F, 16.0F);
						builder.addVertex(xp4+xOff, 0.2+yOff, zp4+zOff, 0.0F, 16.0F);

						builder.addVertex(xp1+xOff, 0.8+yOff, zp1+zOff, 0.0F, 0.0F);
						builder.addVertex(xp4+xOff, 0.2+yOff, zp4+zOff, 0.0F, 16.0F);
						builder.addVertex(xp3+xOff, 0.2+yOff, zp3+zOff, 16.0F, 16.0F);
						builder.addVertex(xp2+xOff, 0.8+yOff, zp2+zOff, 16.0F, 0.0F);
					}

					quads = new CompositeList<>(quads, builder.build().nonCulledQuads);
				}

				return quads;
			}

			return Collections.emptyList();
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
			return this.textureLeaves;
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

	private static class CompositeList<E> extends AbstractList<E> {
		private final List<E> list1;
		private final List<E> list2;

		public CompositeList(List<E> list1, List<E> list2) {
			this.list1 = list1;
			this.list2 = list2;
		}

		@Override
		public E get(int index) {
			if (index < this.list1.size()) {
				return this.list1.get(index);
			}
			return this.list2.get(index - this.list1.size());
		}

		@Override
		public int size() {
			return this.list1.size() + this.list2.size();
		}
	}
}