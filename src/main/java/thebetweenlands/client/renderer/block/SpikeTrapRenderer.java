package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.SpikeTrapBlock;
import thebetweenlands.common.block.entity.SpikeTrapBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class SpikeTrapRenderer implements BlockEntityRenderer<SpikeTrapBlockEntity> {

	private static final RenderType NORMAL_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/spike_block_spikes_1.png"));
	private static final RenderType MUD_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/spike_block_spikes_2.png"));
	private static final RenderType SPOOP_TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/spoop.png"));

	private final ModelPart spikes;
	private final ModelPart spoop;

	public SpikeTrapRenderer(BlockEntityRendererProvider.Context context) {
		this.spikes = context.bakeLayer(BLModelLayers.SPIKE_BLOCK);
		this.spoop = context.bakeLayer(BLModelLayers.SPOOP);
	}

	@Override
	public void render(SpikeTrapBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(entity.getBlockState().getValue(SpikeTrapBlock.FACING).getRotation());
		stack.scale(1.0F, -1.0F, -1.0F);

		if (entity.animationTicks > 0) {
			stack.pushPose();
			float interpolatedAnimationTicks = entity.prevAnimationTicks + (entity.animationTicks - entity.prevAnimationTicks) * partialTick;
			if (entity.stabbing || interpolatedAnimationTicks > 0) {
				if (interpolatedAnimationTicks <= 5.0F) {
					stack.translate(0.0F, 0.0F - 1.0F / 5.0F * interpolatedAnimationTicks, 0.0F);
				} else {
					stack.translate(0.0F, -1.0F, 0.0F);
				}
			}
			this.spikes.render(stack, source.getBuffer(entity.getBlockState().is(BlockRegistry.SPIKE_TRAP.get()) ? NORMAL_TEXTURE : MUD_TEXTURE), light, overlay);
			stack.popPose();
		}

		if (entity.canSpook) {
			float spoopTicks = entity.prevSpoopAnimationTicks + (entity.spoopAnimationTicks - entity.prevSpoopAnimationTicks) * partialTick;
			if (entity.activeSpoop || spoopTicks > 0) {
				float alpha = 0.0375F * spoopTicks;
				this.spoop.render(stack, source.getBuffer(SPOOP_TEXTURE), light, overlay, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
			}
		}
		stack.popPose();
	}
}
