package thebetweenlands.client.model.baked.bulbcappedmushroom;

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

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class BulbCappedMushroomModel implements IDynamicBakedModel {

    private final TextureAtlasSprite texture;
    private final ItemTransforms transforms;
    private final List<BakedQuad> stalkQuads;
    private final List<BakedQuad> capQuads;

    public BulbCappedMushroomModel(TextureAtlasSprite texture, ItemTransforms transforms, Transformation identity) {
        this.texture = texture;
        this.transforms = transforms;

        QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK)
                .setTransformation(identity)
                .setSprite(texture)
                .setTintIndex(-1);
        BulbCappedMushroomGeometry.buildStalks(builder);
        this.stalkQuads = builder.build().nonCulledQuads;
        BulbCappedMushroomGeometry.buildCaps(builder); //vertices are cleared when built, so might as well reuse the builder
        this.capQuads = builder.build().nonCulledQuads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
            ModelData extraData, @Nullable RenderType renderType) {
        if (side != null) return Collections.emptyList();

        if (renderType == RenderType.translucent()) return this.capQuads;
        if (renderType == RenderType.cutout()) return this.stalkQuads;

        return new CompositeList<>(this.stalkQuads, this.capQuads);
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
        return ChunkRenderTypeSet.of(RenderType.cutout(), RenderType.translucent());
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
