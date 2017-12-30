package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

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
import net.minecraftforge.common.property.IExtendedBlockState;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.QuadBuilder;

public class ModelWeedwoodBush implements IModel {
	private final ResourceLocation leavesTexture;
	private final ResourceLocation sticksTexture;

	public ModelWeedwoodBush() {
		this.leavesTexture = new ResourceLocation(ModInfo.ID, "blocks/leaves_weedwood_bush");
		this.sticksTexture = new ResourceLocation(ModInfo.ID, "items/weedwood_stick");
	}

	public ModelWeedwoodBush(ResourceLocation leaves, ResourceLocation sticks) {
		this.leavesTexture = leaves;
		this.sticksTexture = sticks;
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
		return new ModelBakedWeedwoodBush(format, state.apply(Optional.empty()), map, bakedTextureGetter.apply(this.leavesTexture), bakedTextureGetter.apply(this.sticksTexture));
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

		return new ModelWeedwoodBush(leaves, sticks);
	}

	public static class ModelBakedWeedwoodBush implements IBakedModel {
		protected final TRSRTransformation transformation;
		protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private final VertexFormat format;
		private final TextureAtlasSprite textureLeaves;
		private final TextureAtlasSprite textureSticks;

		private ModelBakedWeedwoodBush(VertexFormat format, Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, TextureAtlasSprite textureLeaves, TextureAtlasSprite textureSticks) {
			this.transformation = transformation.isPresent() ? transformation.get() : null;
			this.transforms = transforms;
			this.format = format;
			this.textureLeaves = textureLeaves;
			this.textureSticks = textureSticks;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState stateOld, EnumFacing side, long rand) {
			IExtendedBlockState state = (IExtendedBlockState) stateOld;

			List<BakedQuad> quads = new ArrayList<>();

			if(side == null) {
				float mini = 0F, minj = 0F, mink = 0F, maxi = 0.0F, maxj = 0.0F, maxk = 0.0F;
				int posX = 0, posY = 0, posZ = 0;

				try {
					if (state.getValue(BlockWeedwoodBush.WEST))
						mini = -0.25F;
					if (state.getValue(BlockWeedwoodBush.EAST))
						maxi = 0.25F;
					if (state.getValue(BlockWeedwoodBush.DOWN))
						minj = -0.25F;
					if (state.getValue(BlockWeedwoodBush.UP))
						maxj = 0.25F;
					if (state.getValue(BlockWeedwoodBush.NORTH))
						mink = -0.25F;
					if (state.getValue(BlockWeedwoodBush.SOUTH))
						maxk = 0.25F;
					posX = state.getValue(BlockWeedwoodBush.POS_X);
					posY = state.getValue(BlockWeedwoodBush.POS_Y);
					posZ = state.getValue(BlockWeedwoodBush.POS_Z);
				} catch(Exception ex) {
					//how should this handle item rendering gracefully? :(
				}

				QuadBuilder builder = new QuadBuilder(this.format).setTransformation(this.transformation);

				builder.setTintIndex(0);
				builder.setSprite(this.textureLeaves);

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

				if (Minecraft.isFancyGraphicsEnabled()) {
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

					builder.setSprite(this.textureSticks);

					int cSticks = 5;

					Random rnd = new Random();
					long seed = posX * 0x2FC20FL ^ posY * 0x6EBFFF5L ^ posZ;
					rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);

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
				}
				quads = builder.build();
			}

			return quads;
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
}