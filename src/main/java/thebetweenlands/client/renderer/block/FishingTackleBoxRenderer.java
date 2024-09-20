package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.FishingTackleBoxBlock;
import thebetweenlands.common.block.entity.FishingTackleBoxBlockEntity;

public class FishingTackleBoxRenderer implements BlockEntityRenderer<FishingTackleBoxBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/fishing_tackle_box.png"));
	private final ModelPart box;
	private final ModelPart lid;

	public FishingTackleBoxRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart part = context.bakeLayer(BLModelLayers.FISHING_TACKLE_BOX);
		this.box = part;
		this.lid = part.getChild("lid");
	}

	@Override
	public void render(FishingTackleBoxBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(FishingTackleBoxBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.lid.xRot = entity.getLevel() != null ? (float) -Math.toRadians(entity.getLidAngle(entity.getBlockState(), partialTicks)) : 0.0F;
		this.box.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();
	}
}
