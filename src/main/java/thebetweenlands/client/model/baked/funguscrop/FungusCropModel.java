package thebetweenlands.client.model.baked.funguscrop;

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
import thebetweenlands.common.block.farming.FungusCropBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.common.block.farming.DecayableCropBlock;

import java.util.Collections;
import java.util.List;

public class FungusCropModel implements IDynamicBakedModel {

    private final TextureAtlasSprite stage1Texture;
    private final TextureAtlasSprite stage2Texture;
    private final TextureAtlasSprite stage3Texture;
    private final TextureAtlasSprite stage4Texture;
    private final TextureAtlasSprite stage4DecayedTexture;
    private final TextureAtlasSprite particleTexture;
	private final ItemTransforms transforms;
	private final List<BakedQuad> stage1Quads;
	private final List<BakedQuad> stage2Quads;
	private final List<BakedQuad> stage3Quads;
	private final List<BakedQuad> stage4Quads;
	private final List<BakedQuad> stage4DecayedQuads;

    public FungusCropModel(TextureAtlasSprite stage1Texture, TextureAtlasSprite stage2Texture, TextureAtlasSprite stage3Texture,
						   TextureAtlasSprite stage4Texture, TextureAtlasSprite stage4DecayedTexture, TextureAtlasSprite particleTexture, ItemTransforms transforms, Transformation identity) {
		this.stage1Texture = stage1Texture;
		this.stage2Texture = stage2Texture;
		this.stage3Texture = stage3Texture;
		this.stage4Texture = stage4Texture;
		this.stage4DecayedTexture = stage4DecayedTexture;
		this.particleTexture = particleTexture;
		this.transforms = transforms;

		QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
			.setTransformation(identity)
			.setSprite(this.stage1Texture)
			.setTintIndex(-1);

		FungusCropGeometry.buildStage1(builder);
		this.stage1Quads = builder.build().nonCulledQuads;
		builder.setSprite(this.stage2Texture);
		FungusCropGeometry.buildStage2(builder);
		this.stage2Quads = builder.build().nonCulledQuads;
		builder.setSprite(this.stage3Texture);
		FungusCropGeometry.buildStage3(builder);
		this.stage3Quads = builder.build().nonCulledQuads;
		builder.setSprite(this.stage4Texture);
		FungusCropGeometry.buildStage4(builder);
		this.stage4Quads = builder.build().nonCulledQuads;
		builder.setSprite(this.stage4DecayedTexture);
		FungusCropGeometry.buildStage4Decayed(builder);
		this.stage4DecayedQuads = builder.build().nonCulledQuads;
	}

    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side != null) return Collections.emptyList();
		if (state == null) return stage1Quads;
		if (state.getValue(DecayableCropBlock.DECAYED)) return stage4DecayedQuads;
		return switch(BlockRegistry.FUNGUS_CROP.get().getAge(state)) {
			case 1 -> stage2Quads;
			case 2 -> stage3Quads;
			case 3 -> stage4Quads;
			default -> stage1Quads;
		};
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
		return ChunkRenderTypeSet.of(RenderType.cutout());
	}
}

