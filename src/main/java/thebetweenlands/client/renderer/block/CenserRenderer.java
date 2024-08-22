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
import thebetweenlands.common.block.CenserBlock;
import thebetweenlands.common.block.entity.CenserBlockEntity;

public class CenserRenderer implements BlockEntityRenderer<CenserBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/censer.png"));
	private final ModelPart censer;

	public CenserRenderer(BlockEntityRendererProvider.Context context) {
		this.censer = context.bakeLayer(BLModelLayers.CENSER);
	}

	@Override
	public void render(CenserBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(CenserBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		this.censer.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();
	}
}
