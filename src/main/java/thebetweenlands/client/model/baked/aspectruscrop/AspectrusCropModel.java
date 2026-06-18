package thebetweenlands.client.model.baked.aspectruscrop;

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
import net.neoforged.neoforge.client.model.data.ModelProperty;

import org.jetbrains.annotations.Nullable;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.common.block.farming.DecayableCropBlock;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class AspectrusCropModel implements IDynamicBakedModel {
    
    private final TextureAtlasSprite stage0Texture;
    private final TextureAtlasSprite stage1Texture;
    private final TextureAtlasSprite stage2Texture;
    private final TextureAtlasSprite stage3Texture;
    private final TextureAtlasSprite stage4Texture;
    private final TextureAtlasSprite stage5Texture;
    private final TextureAtlasSprite stage6Texture;
    private final TextureAtlasSprite fenceTexture;
    private final TextureAtlasSprite particleTexture;
	private final ItemTransforms transforms;
	private final List<BakedQuad> fenceQuads;
	private final List<BakedQuad> stage0Quads;
	private final List<BakedQuad> stage1Quads;
	private final List<BakedQuad> stage2Quads;
	private final List<BakedQuad> stage3Quads;
	private final List<BakedQuad> stage4Quads, stage4FruitQuads, stage4FruitAspectQuads;
	private final List<BakedQuad> stage5Quads, stage5FruitQuads, stage5FruitAspectQuads;
	private final List<BakedQuad> stage6Quads;
	public static final ModelProperty<Integer> ASPECT_COLOR = new ModelProperty<>();

    public AspectrusCropModel(TextureAtlasSprite stage0Texture, TextureAtlasSprite stage1Texture, TextureAtlasSprite stage2Texture, TextureAtlasSprite stage3Texture, TextureAtlasSprite stage4Texture, 
						   TextureAtlasSprite stage5Texture, TextureAtlasSprite stage6Texture, TextureAtlasSprite fenceTexture, TextureAtlasSprite particleTexture, ItemTransforms transforms, Transformation identity) {
		this.stage0Texture = stage0Texture;
		this.stage1Texture = stage1Texture;
		this.stage2Texture = stage2Texture;
		this.stage3Texture = stage3Texture;
		this.stage4Texture = stage4Texture;
		this.stage5Texture = stage5Texture;
		this.stage6Texture = stage6Texture;
		this.fenceTexture = fenceTexture;
		this.particleTexture = particleTexture;
		this.transforms = transforms;

		QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
			.setTransformation(identity)
			.setSprite(this.stage0Texture)
			.setTintIndex(-1);

		AspectrusCropGeometry.buildStage1(builder);
		this.stage0Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage1Texture);
		AspectrusCropGeometry.buildStage1(builder);
		this.stage1Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage2Texture);
		AspectrusCropGeometry.buildStage2(builder);
		this.stage2Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage3Texture);
		AspectrusCropGeometry.buildStage2(builder);
		this.stage3Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage4Texture);
		AspectrusCropGeometry.buildStage3(builder);
		this.stage4Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage5Texture);
		AspectrusCropGeometry.buildStage4(builder);
		this.stage5Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage6Texture);
		AspectrusCropGeometry.buildStage4(builder);
		this.stage6Quads = builder.build().nonCulledQuads;

		builder.setSprite(this.fenceTexture);
		AspectrusCropGeometry.buildFence(builder);
		this.fenceQuads = builder.build().nonCulledQuads;

		builder.setTintIndex(1);
		
		builder.setSprite(this.stage4Texture);
		AspectrusCropGeometry.buildStage3Fruits(builder);
		this.stage4FruitQuads = builder.build().nonCulledQuads;
		builder.setSprite(this.stage5Texture);
		AspectrusCropGeometry.buildStage4Fruits(builder);
		this.stage5FruitQuads = builder.build().nonCulledQuads;

		
		builder.setLightmap(15, 15);
		
		builder.setSprite(this.stage4Texture);
		AspectrusCropGeometry.buildStage3FruitAspects(builder);
		this.stage4FruitAspectQuads = builder.build().nonCulledQuads;

		builder.setSprite(this.stage5Texture);
		AspectrusCropGeometry.buildStage4FruitAspects(builder);
		this.stage5FruitAspectQuads = builder.build().nonCulledQuads;
	}

	private List<BakedQuad> withFence(List<BakedQuad> list) {
		return new CompositeList<>(fenceQuads, list);
	}

    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side != null) return Collections.emptyList();
		if (state == null) return stage1Quads;
		if (state.getValue(DecayableCropBlock.DECAYED)) return stage6Quads;
		return withFence(switch(state.getValue(DecayableCropBlock.STAGE)) {
			case 1 -> stage1Quads;
			case 2 -> stage2Quads;
			case 3 -> stage3Quads;
			case 4 -> renderType == null ? (new CompositeList<>(stage4Quads, stage4FruitQuads, stage4FruitAspectQuads)) : renderType == RenderType.translucent() ? stage4FruitQuads : new CompositeList<>(stage4Quads, stage4FruitAspectQuads);
			case 5 -> renderType == null ? (new CompositeList<>(stage5Quads, stage5FruitQuads, stage5FruitAspectQuads)) : renderType == RenderType.translucent() ? stage5FruitQuads : new CompositeList<>(stage5Quads, stage5FruitAspectQuads);
			default -> stage0Quads;
		});
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
        return ChunkRenderTypeSet.of(RenderType.cutout(), RenderType.translucent());
	}
	private static class CompositeList<E> extends AbstractList<E> {
		private final List<E> list1;
		private final List<E> list2;

		public CompositeList(List<E> list1, List<E> list2) {
			this.list1 = list1;
			this.list2 = list2;
		}
		
		public CompositeList(List<E> list1, List<E> list2, List<E> list3) {
			this.list1 = new CompositeList<>(list1, list2);
			this.list2 = list3;
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

