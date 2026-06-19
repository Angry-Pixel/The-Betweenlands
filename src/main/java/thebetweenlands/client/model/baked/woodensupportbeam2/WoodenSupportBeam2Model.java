package thebetweenlands.client.model.baked.woodensupportbeam2;

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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import thebetweenlands.common.block.structure.WoodenSupportBeamBlock;

import thebetweenlands.util.QuadBuilder;

import java.util.Collections;
import java.util.List;

public class WoodenSupportBeam2Model implements IDynamicBakedModel {
    
    private final TextureAtlasSprite texture;
    private final TextureAtlasSprite particleTexture;
	private final ItemTransforms transforms;
	private final List<BakedQuad> quadsTop;
	private final List<BakedQuad> quadsBottom;

    public WoodenSupportBeam2Model(TextureAtlasSprite texture, TextureAtlasSprite particleTexture, ItemTransforms transforms, Transformation identity) {
		this.texture = texture;
		this.transforms = transforms;
		this.particleTexture = particleTexture;

		QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
			.setTransformation(identity)
			.setSprite(texture)
			.setTintIndex(-1);

		WoodenSupportBeam2Geometry.build(builder);
		this.quadsTop = builder.build().nonCulledQuads;
		Transformation rot = new Transformation(
				null,
				new Quaternionf().rotateZ((float) Math.toRadians(180)),
				null, null
			).applyOrigin(new Vector3f(0.5F, 0.5F, 0.5F));

		builder.setTransformation(identity.compose(rot));
		WoodenSupportBeam2Geometry.build(builder);
		this.quadsBottom = builder.build().nonCulledQuads;
	}

    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		if (side != null) return Collections.emptyList();
		if (state == null) return this.quadsTop;
		return state.getValue(WoodenSupportBeamBlock.TOP) ? this.quadsTop : this.quadsBottom;
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

