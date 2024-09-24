package thebetweenlands.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static net.minecraft.client.renderer.LevelRenderer.DIRECTIONS;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

	@Shadow(remap = false) protected abstract void renderModelFaceFlat(BlockAndTintGetter level, BlockState state, BlockPos pos, int packedLight, int packedOverlay, boolean repackLight, PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, BitSet shapeFlags);
	@Shadow(remap = false) protected abstract void renderModelFaceAO(BlockAndTintGetter level, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, float[] shape, BitSet shapeFlags, ModelBlockRenderer.AmbientOcclusionFace aoFace, int packedOverlay);

	/* TODO: this overwrites the original tesselateWithAO function, replace with something less hacky */
	@Inject(method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
		at = @At("HEAD"),
		remap = false, cancellable = true)
	protected void tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay, ModelData modelData, RenderType renderType, CallbackInfo ci) {
		float[] afloat = new float[DIRECTIONS.length * 2];
		BitSet bitset = new BitSet(3);
		ModelBlockRenderer.AmbientOcclusionFace ambientOcclusionFace = new ModelBlockRenderer.AmbientOcclusionFace();
		BlockPos.MutableBlockPos mutablePos = pos.mutable();

		for (Direction direction : DIRECTIONS) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(state, direction, random, modelData, renderType);
			if (!list.isEmpty()) {
				mutablePos.setWithOffset(pos, direction);
				if (!checkSides || Block.shouldRenderFace(state, level, pos, direction, mutablePos)) {
					List<BakedQuad> flatlist = new ArrayList<BakedQuad>();
					List<BakedQuad> standardlist = new ArrayList<BakedQuad>();

					// Fetch quad shading flags
					for (BakedQuad quad : list) {
						if (!quad.isShade()) {
							flatlist.add(quad);
							continue;
						}
						standardlist.add(quad);
					}

					// Flat faces
					if (!flatlist.isEmpty()) {
						BlockPos.MutableBlockPos mutableblockpos = pos.mutable().move(direction);

						int i = LevelRenderer.getLightColor(level, state, mutableblockpos);

						this.renderModelFaceFlat(level, state, pos, i, packedOverlay, false, poseStack, consumer, flatlist, bitset);
					}

					// AO faces
					if (!standardlist.isEmpty()) {
						this.renderModelFaceAO(level, state, pos, poseStack, consumer, standardlist, afloat, bitset, ambientOcclusionFace, packedOverlay);
					}
				}
			}
		}

		// Elements without a cull face
		random.setSeed(seed);
		List<BakedQuad> list1 = model.getQuads(state, null, random, modelData, renderType);
		if (!list1.isEmpty()) {
			this.renderModelFaceAO(level, state, pos, poseStack, consumer, list1, afloat, bitset, ambientOcclusionFace, packedOverlay);
		}
		ci.cancel();
	}
}
