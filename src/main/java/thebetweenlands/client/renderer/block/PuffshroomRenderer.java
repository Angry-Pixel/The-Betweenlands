package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.PuffshroomModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.PuffshroomBlockEntity;

public class PuffshroomRenderer implements BlockEntityRenderer<PuffshroomBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/puffshroom.png"));
	private final PuffshroomModel puffshroom;

	public PuffshroomRenderer(BlockEntityRendererProvider.Context context) {
		this.puffshroom = new PuffshroomModel(context.bakeLayer(BLModelLayers.PUFFSHROOM));
	}

	@Override
	public void render(PuffshroomBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (entity.getLevel() != null && entity.animation_1 != 0) {
			stack.pushPose();
			stack.translate(0.5F, 0.5F, 0.5F);
			stack.scale(1.0F, -1.0F, -1.0F);
			this.puffshroom.renderAndAnimate(entity, partialTicks, stack, source.getBuffer(TEXTURE), LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().above()), overlay);
			stack.popPose();
		}
	}
}
