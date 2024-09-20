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
import thebetweenlands.common.block.container.GeckoCageBlock;
import thebetweenlands.common.block.entity.GeckoCageBlockEntity;

public class GeckoCageRenderer implements BlockEntityRenderer<GeckoCageBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/gecko_cage.png"));
	private final ModelPart cage;

	public GeckoCageRenderer(BlockEntityRendererProvider.Context context) {
		this.cage = context.bakeLayer(BLModelLayers.GECKO_CAGE);
	}

	@Override
	public void render(GeckoCageBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(GeckoCageBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.cage.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		//TODO gecko rendering
	}
}
