package thebetweenlands.client.model.baked.walkway;

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
import thebetweenlands.common.block.misc.WalkwayBlock;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class WalkwayModel implements IDynamicBakedModel {
    
    private final TextureAtlasSprite texture;
    private final TextureAtlasSprite particleTexture;
	private final ItemTransforms transforms;
	private final List<BakedQuad> quads;
	private final List<BakedQuad> standQuads;

    public WalkwayModel(TextureAtlasSprite texture, TextureAtlasSprite particleTexture, ItemTransforms transforms, Transformation identity) {
		this.texture = texture;
		this.transforms = transforms;
		this.particleTexture = particleTexture;

		QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
			.setTransformation(identity)
			.setSprite(texture)
			.setTintIndex(-1);

		WalkwayGeometry.build(builder);
		this.quads = builder.build().nonCulledQuads;
		WalkwayGeometry.buildStands(builder);
		this.standQuads = builder.build().nonCulledQuads;
	}

    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side != null) {
			return Collections.emptyList();
		}
		if (state == null) return new CompositeList<BakedQuad>(this.quads, this.standQuads);
		return state.getValue(WalkwayBlock.HAS_STANDS) ? new CompositeList<BakedQuad>(this.quads, this.standQuads) : this.quads;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
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
		return this.particleTexture;
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

