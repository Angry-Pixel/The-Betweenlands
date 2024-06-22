package thebetweenlands.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thebetweenlands.client.rendering.mixintypes.subclasses.BakedQuadEX;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

// Inject a patch
@Mixin(ModelBlockRenderer.class)
public abstract class MixinModelBlockRenderer {
	@Shadow
	public abstract boolean tesselateWithoutAO(BlockAndTintGetter p_111091_, BakedModel p_111092_, BlockState p_111093_, BlockPos p_111094_, PoseStack p_111095_, VertexConsumer p_111096_, boolean p_111097_, Random p_111098_, long p_111099_, int p_111100_, IModelData modelData);

	@Shadow
	protected abstract void renderModelFaceFlat(BlockAndTintGetter p_111002_, BlockState p_111003_, BlockPos p_111004_, int p_111005_, int p_111006_, boolean p_111007_, PoseStack p_111008_, VertexConsumer p_111009_, List<BakedQuad> p_111010_, BitSet p_111011_);

	@Shadow
	protected abstract void renderModelFaceAO(BlockAndTintGetter p_111013_, BlockState p_111014_, BlockPos p_111015_, PoseStack p_111016_, VertexConsumer p_111017_, List<BakedQuad> p_111018_, float[] p_111019_, BitSet p_111020_, ModelBlockRenderer.AmbientOcclusionFace p_111021_, int p_111022_);


	// Ambient occlusion fix (block models ignoring ao flag)
	@Inject(method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z", at = @At("HEAD"), cancellable = true)
	protected void tesselateWithAOPatch(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int packedOverlay, IModelData modelData, CallbackInfoReturnable<Boolean> cir) {

		// if model requests to not be rendered using smooth lightning
		if (!model.useAmbientOcclusion()) {
			cir.setReturnValue(this.tesselateWithoutAO(level, model, state, pos, poseStack, buffer, checkSides, rand, seed, packedOverlay, modelData));
		}
	}

	// Add face ao flag
	@Inject(method = "renderModelFaceAO", at = @At("HEAD"), cancellable = true)
	protected void renderModelFaceAOPatch(BlockAndTintGetter p_111013_, BlockState p_111014_, BlockPos p_111015_, PoseStack p_111016_, VertexConsumer p_111017_, List<BakedQuad> p_111018_, float[] p_111019_, BitSet p_111020_, ModelBlockRenderer.AmbientOcclusionFace p_111021_, int p_111022_, CallbackInfo ci) {

		// NOTE: this is untested for models that contain no quads

		// does list need to be sorted

		// TODO: split lists into their separate render methods
		List<BakedQuad> flatlist = new ArrayList<BakedQuad>();
		List<Direction> directionlist = new ArrayList<Direction>();
		List<BakedQuad> standardlist = new ArrayList<BakedQuad>();
		boolean flag = false;

		for (BakedQuad quad : p_111018_) {
			if (quad instanceof BakedQuadEX && ((BakedQuadEX) quad).flatShade) {
				directionlist.add(((BakedQuadEX) quad).shadeSide);
				flatlist.add(quad);
				flag = true;
				continue;
			}
			standardlist.add(quad);
		}

		// standard operation
		if (!flag) {
			return;
		}

		// special operations
		if (!flatlist.isEmpty()) {
			Direction dir = directionlist.get(0) == null ? p_111018_.get(0).getDirection() : directionlist.get(0);
			BlockPos.MutableBlockPos mutableblockpos = p_111015_.mutable().move(dir);

			int i = LevelRenderer.getLightColor(p_111013_, p_111014_, mutableblockpos);

			this.renderModelFaceFlat(p_111013_, p_111014_, p_111015_, i, p_111022_, false, p_111016_, p_111017_, flatlist, p_111020_);
		}

		if (!standardlist.isEmpty()) {
			this.renderModelFaceAO(p_111013_, p_111014_, p_111015_, p_111016_, p_111017_, standardlist, p_111019_, p_111020_, p_111021_, p_111022_);
		}

		ci.cancel();

        /*if (quad instanceof BakedQuadEX && ((BakedQuadEX) quad).flatShade) {
            BakedQuadEX quadex = (BakedQuadEX) quad;

            // TODO: get direction
            Direction dir = quadex.getDirection();
            BlockPos.MutableBlockPos mutableblockpos = p_111015_.mutable().move(dir);

            int i = LevelRenderer.getLightColor(p_111013_, p_111014_, mutableblockpos);
            this.renderModelFaceFlat(p_111013_, p_111014_, p_111015_, i, p_111022_, false, p_111016_, p_111017_, p_111018_, p_111020_);


            //this.renderModelFaceFlat(p_111013_, p_111014_, p_111015_, -1, p_111022_, true, p_111016_, p_111017_, p_111018_, p_111020_);
            ci.cancel();
        }*/
	}


    /*
    public boolean tesselateWithAO(BlockAndTintGetter p_111079_, BakedModel p_111080_, BlockState p_111081_, BlockPos p_111082_, PoseStack p_111083_, VertexConsumer p_111084_, boolean p_111085_, Random p_111086_, long p_111087_, int p_111088_, net.minecraftforge.client.model.data.IModelData modelData) {
        boolean flag = false;
        float[] afloat = new float[DIRECTIONS.length * 2];
        BitSet bitset = new BitSet(3);
        ModelBlockRenderer.AmbientOcclusionFace modelblockrenderer$ambientocclusionface = new ModelBlockRenderer.AmbientOcclusionFace();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_111082_.mutable();

        for(Direction direction : DIRECTIONS) {
            p_111086_.setSeed(p_111087_);
            List<BakedQuad> list = p_111080_.getQuads(p_111081_, direction, p_111086_, modelData);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(p_111082_, direction);
                if (!p_111085_ || Block.shouldRenderFace(p_111081_, p_111079_, p_111082_, direction, blockpos$mutableblockpos)) {
                    this.renderModelFaceAO(p_111079_, p_111081_, p_111082_, p_111083_, p_111084_, list, afloat, bitset, modelblockrenderer$ambientocclusionface, p_111088_);
                    flag = true;
                }
            }
        }

        p_111086_.setSeed(p_111087_);
        List<BakedQuad> list1 = p_111080_.getQuads(p_111081_, (Direction)null, p_111086_, modelData);
        if (!list1.isEmpty()) {
            this.renderModelFaceAO(p_111079_, p_111081_, p_111082_, p_111083_, p_111084_, list1, afloat, bitset, modelblockrenderer$ambientocclusionface, p_111088_);
            flag = true;
        }

        return flag;
    }

    private void renderModelFaceAO(BlockAndTintGetter p_111013_, BlockState p_111014_, BlockPos p_111015_, PoseStack p_111016_, VertexConsumer p_111017_, List<BakedQuad> p_111018_, float[] p_111019_, BitSet p_111020_, ModelBlockRenderer.AmbientOcclusionFace p_111021_, int p_111022_) {
        for(BakedQuad bakedquad : p_111018_) {
            this.calculateShape(p_111013_, p_111014_, p_111015_, bakedquad.getVertices(), bakedquad.getDirection(), p_111019_, p_111020_);
            p_111021_.calculate(p_111013_, p_111014_, p_111015_, bakedquad.getDirection(), p_111019_, p_111020_, bakedquad.isShade());
            this.putQuadData(p_111013_, p_111014_, p_111015_, p_111017_, p_111016_.last(), bakedquad, p_111021_.brightness[0], p_111021_.brightness[1], p_111021_.brightness[2], p_111021_.brightness[3], p_111021_.lightmap[0], p_111021_.lightmap[1], p_111021_.lightmap[2], p_111021_.lightmap[3], p_111022_);
        }

    }

    public boolean tesselateWithoutAO(BlockAndTintGetter p_111091_, BakedModel p_111092_, BlockState p_111093_, BlockPos p_111094_, PoseStack p_111095_, VertexConsumer p_111096_, boolean p_111097_, Random p_111098_, long p_111099_, int p_111100_, net.minecraftforge.client.model.data.IModelData modelData) {
        boolean flag = false;
        BitSet bitset = new BitSet(3);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_111094_.mutable();

        for(Direction direction : DIRECTIONS) {
            p_111098_.setSeed(p_111099_);
            List<BakedQuad> list = p_111092_.getQuads(p_111093_, direction, p_111098_, modelData);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(p_111094_, direction);
                if (!p_111097_ || Block.shouldRenderFace(p_111093_, p_111091_, p_111094_, direction, blockpos$mutableblockpos)) {
                    int i = LevelRenderer.getLightColor(p_111091_, p_111093_, blockpos$mutableblockpos);
                    this.renderModelFaceFlat(p_111091_, p_111093_, p_111094_, i, p_111100_, false, p_111095_, p_111096_, list, bitset);
                    flag = true;
                }
            }
        }

        p_111098_.setSeed(p_111099_);
        List<BakedQuad> list1 = p_111092_.getQuads(p_111093_, (Direction)null, p_111098_, modelData);
        if (!list1.isEmpty()) {
            this.renderModelFaceFlat(p_111091_, p_111093_, p_111094_, -1, p_111100_, true, p_111095_, p_111096_, list1, bitset);
            flag = true;
        }

        return flag;
    }

    private void renderModelFaceFlat(BlockAndTintGetter p_111002_, BlockState p_111003_, BlockPos p_111004_, int p_111005_, int p_111006_, boolean p_111007_, PoseStack p_111008_, VertexConsumer p_111009_, List<BakedQuad> p_111010_, BitSet p_111011_) {
        for(BakedQuad bakedquad : p_111010_) {
            if (p_111007_) {
                this.calculateShape(p_111002_, p_111003_, p_111004_, bakedquad.getVertices(), bakedquad.getDirection(), (float[])null, p_111011_);
                BlockPos blockpos = p_111011_.get(0) ? p_111004_.relative(bakedquad.getDirection()) : p_111004_;
                p_111005_ = LevelRenderer.getLightColor(p_111002_, p_111003_, blockpos);
            }

            float f = p_111002_.getShade(bakedquad.getDirection(), bakedquad.isShade());
            this.putQuadData(p_111002_, p_111003_, p_111004_, p_111009_, p_111008_.last(), bakedquad, f, f, f, f, p_111005_, p_111005_, p_111005_, p_111005_, p_111006_);
        }

    }
    */
}