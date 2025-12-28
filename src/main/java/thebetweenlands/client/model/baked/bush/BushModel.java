package thebetweenlands.client.model.baked.bush;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.plant.WeedwoodBushBlock;
import thebetweenlands.util.QuadBuilder;

import java.util.*;

public class BushModel implements IDynamicBakedModel {

	private final LoadingCache<Long, BushModel> modelCache = CacheBuilder.newBuilder().maximumSize(256).build(new CacheLoader<>() {
		@Override
		public BushModel load(Long key) {
			return new BushModel(BushModel.this.textureLeaves, BushModel.this.textureSticks, BushModel.this.transforms, BushModel.this.identity, key);
		}
	});

	private final TextureAtlasSprite textureLeaves;
	private final TextureAtlasSprite textureSticks;
	private final ItemTransforms transforms;
	private final Transformation identity;
	private List<BakedQuad> baseQuads;
	private List<BakedQuad> fancyQuads;

	public BushModel(TextureAtlasSprite leaves, TextureAtlasSprite sticks, ItemTransforms transforms, Transformation identity) {
		this.textureLeaves = leaves;
		this.textureSticks = sticks;
		this.transforms = transforms;
		this.identity = identity;
	}

	private BushModel(TextureAtlasSprite leaves, TextureAtlasSprite sticks, ItemTransforms transforms, Transformation identity, long key) {
		this(leaves, sticks, transforms, identity);

		if (key != -1) {
			QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK).setTransformation(identity);

			builder.setTintIndex(0);
			builder.setSprite(this.textureLeaves);

			float mini = (key & 1) != 0 ? -0.25F : 0.0F;
			float maxi = (key & (1 << 1)) != 0 ? 0.25F : 0.0F;
			float minj = (key & (1 << 2)) != 0 ? -0.25F : 0.0F;
			float maxj = (key & (1 << 3)) != 0 ? 0.25F : 0.0F;
			float mink = (key & (1 << 4)) != 0 ? -0.25F : 0.0F;
			float maxk = (key & (1 << 5)) != 0 ? 0.25F : 0.0F;

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
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side == null) {
			long index = 0;

			if (state != null) {
				if (state.getValue(WeedwoodBushBlock.WEST))
					index |= 1;
				if (state.getValue(WeedwoodBushBlock.EAST))
					index |= 1 << 1;
				if (state.getValue(WeedwoodBushBlock.DOWN))
					index |= 1 << 2;
				if (state.getValue(WeedwoodBushBlock.UP))
					index |= 1 << 3;
				if (state.getValue(WeedwoodBushBlock.NORTH))
					index |= 1 << 4;
				if (state.getValue(WeedwoodBushBlock.SOUTH))
					index |= 1 << 5;
			}

			BushModel model = this.modelCache.getUnchecked(index);

			List<BakedQuad> quads = model.baseQuads;

			if (Minecraft.useFancyGraphics()) {
				quads = new CompositeList<>(quads, model.fancyQuads);

				int cSticks = 5;

				QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK).setTransformation(this.identity);
				builder.setSprite(this.textureSticks);
				builder.setTintIndex(-1);

				for (int i = 0; i < cSticks; i++) {
					float rotation = Mth.TWO_PI / (float) cSticks * (float) i;
					float xp1 = Mth.sin(rotation) * 0.4f;
					float zp1 = Mth.cos(rotation) * 0.4f;
					float xp2 = Mth.sin(rotation + Mth.HALF_PI) * 0.4f;
					float zp2 = Mth.cos(rotation + Mth.HALF_PI) * 0.4f;
					float xp3 = Mth.sin(rotation + Mth.PI) * 0.4f;
					float zp3 = Mth.cos(rotation + Mth.PI) * 0.4f;
					float xp4 = Mth.sin(rotation + Mth.PI + Mth.HALF_PI) * 0.4f;
					float zp4 = Mth.cos(rotation + Mth.PI + Mth.HALF_PI) * 0.4f;
					float xOff = (rand.nextFloat() * 2.0f - 1.0f) * 0.4f + 0.5f;
					float yOff = (rand.nextFloat() * 2.0f - 1.0f) * 0.4f;
					float zOff = (rand.nextFloat() * 2.0f - 1.0f) * 0.4f + 0.5f;

					builder.addVertex(xp1 + xOff, 0.8 + yOff, zp1 + zOff, 0.0F, 0.0F);
					builder.addVertex(xp2 + xOff, 0.8 + yOff, zp2 + zOff, 16.0F, 0.0F);
					builder.addVertex(xp3 + xOff, 0.2 + yOff, zp3 + zOff, 16.0F, 16.0F);
					builder.addVertex(xp4 + xOff, 0.2 + yOff, zp4 + zOff, 0.0F, 16.0F);

					builder.addVertex(xp1 + xOff, 0.8 + yOff, zp1 + zOff, 0.0F, 0.0F);
					builder.addVertex(xp4 + xOff, 0.2 + yOff, zp4 + zOff, 0.0F, 16.0F);
					builder.addVertex(xp3 + xOff, 0.2 + yOff, zp3 + zOff, 16.0F, 16.0F);
					builder.addVertex(xp2 + xOff, 0.8 + yOff, zp2 + zOff, 16.0F, 0.0F);
				}

				quads = new CompositeList<>(quads, builder.build().nonCulledQuads);
			}

			return quads;
		}

		return Collections.emptyList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.textureLeaves;
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	@Override
	public ItemTransforms getTransforms() {
		return this.transforms;
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
		return ChunkRenderTypeSet.of(Minecraft.useFancyGraphics() ? RenderType.cutoutMipped() : RenderType.solid());
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
