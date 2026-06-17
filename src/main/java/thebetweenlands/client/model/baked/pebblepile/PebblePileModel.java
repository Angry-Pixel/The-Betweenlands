package thebetweenlands.client.model.baked.pebblepile;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.common.block.terrain.PebblePileBlock;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class PebblePileModel implements IDynamicBakedModel {
    
    private final TextureAtlasSprite texture;
	private final ItemTransforms transforms;
	private final List<BakedQuad> pebble1Quads;
	private final List<BakedQuad> pebble2Quads;
	private final List<BakedQuad> pebble3Quads;
	private final List<BakedQuad> pebble4Quads;
	private final List<BakedQuad> plantQuads;

    public PebblePileModel(TextureAtlasSprite texture, ItemTransforms transforms, Transformation identity) {
		this.texture = texture;
		this.transforms = transforms;

		QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
			.setTransformation(identity)
			.setSprite(texture)
			.setTintIndex(-1);

		PebblePileGeometry.buildPebble1(builder);
		this.pebble1Quads = builder.build().nonCulledQuads;
		PebblePileGeometry.buildPebble2(builder);
		this.pebble2Quads = builder.build().nonCulledQuads;
		PebblePileGeometry.buildPebble3(builder);
		this.pebble3Quads = builder.build().nonCulledQuads;
		PebblePileGeometry.buildPebble4(builder);
		this.pebble4Quads = builder.build().nonCulledQuads;
		PebblePileGeometry.buildPlants(builder);
		this.plantQuads = builder.build().nonCulledQuads;
	}

    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side != null) return Collections.emptyList();
		if (state == null) return pebble1Quads; //but this shouldnt happen since pebbles have 2d texture while in gui
		List<BakedQuad> cur = this.pebble1Quads;
		if (state.getValue(PebblePileBlock.PLANT)) cur = new CompositeList<>(cur, this.plantQuads);
		if (state.getValue(PebblePileBlock.PEBBLES) > 1) cur = new CompositeList<>(cur, this.pebble2Quads);
		if (state.getValue(PebblePileBlock.PEBBLES) > 2) cur = new CompositeList<>(cur, this.pebble3Quads);
		if (state.getValue(PebblePileBlock.PEBBLES) > 3) cur = new CompositeList<>(cur, this.pebble4Quads);
		return cur;
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
		return this.texture;
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
		return ChunkRenderTypeSet.of(RenderType.cutout());
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

