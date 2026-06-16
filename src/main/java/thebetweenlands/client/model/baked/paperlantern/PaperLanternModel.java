package thebetweenlands.client.model.baked.paperlantern;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import thebetweenlands.common.block.misc.BLLanternBlock;
import thebetweenlands.util.QuadBuilder;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class PaperLanternModel implements IDynamicBakedModel {
    
    private final TextureAtlasSprite texture;
    private final TextureAtlasSprite ropeSprite;
	private final ItemTransforms transforms;
	private final List<BakedQuad>[] quadss = new List[8];
	private final List<BakedQuad> lanternRope;

    public PaperLanternModel(TextureAtlasSprite texture, TextureAtlasSprite ropeSprite, ItemTransforms transforms, Transformation identity) {
		this.texture = texture;
		this.ropeSprite = ropeSprite;
		this.transforms = transforms;

		QuadBuilder ropeBuilder = new QuadBuilder(DefaultVertexFormat.BLOCK)
			.setTransformation(identity)
			.setSprite(this.ropeSprite)
			.setTintIndex(-1);
		PaperLanternGeometry.buildRope(ropeBuilder);
		this.lanternRope = ropeBuilder.build().nonCulledQuads;

		for (int i = 0; i < 8; i++) {
			float degrees = (360 - i*45) % 360;
			Transformation rot = new Transformation(
				null,
				new Quaternionf().rotateY((float) Math.toRadians(degrees)),
				null, null
			).applyOrigin(new Vector3f(0.5F, 0.5F, 0.5F));

			QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
				.setTransformation(rot)
				.setSprite(this.texture)
				.setTintIndex(-1);
			PaperLanternGeometry.build(builder);
			this.quadss[i] = builder.build().nonCulledQuads;
		}
	}

    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side != null) return Collections.emptyList();
		if (state == null) return this.quadss[0];
		return state.getValue(LanternBlock.HANGING) ? new CompositeList<>(this.quadss[state.getValue(BLLanternBlock.ROTATION)], this.lanternRope) : this.quadss[state.getValue(BLLanternBlock.ROTATION)];
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

