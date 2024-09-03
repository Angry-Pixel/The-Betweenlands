package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.MortarBlock;
import thebetweenlands.common.block.entity.MortarBlockEntity;

public class MortarRenderer implements BlockEntityRenderer<MortarBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/pestle_and_mortar.png"));
	private final ModelPart mortar;
	private final ModelPart pestle;

	public MortarRenderer(BlockEntityRendererProvider.Context context) {
		var root = context.bakeLayer(BLModelLayers.MORTAR);
		this.mortar = root.getChild("mortar");
		this.pestle = root.getChild("pestle");
	}

	@Override
	public void render(MortarBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(MortarBlock.FACING).toYRot() + 180));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.mortar.render(stack, source.getBuffer(TEXTURE), light, overlay);

		if (entity.isPestleInstalled()) {
			stack.pushPose();
			if (entity.progress > 0 && entity.progress < 84) {
				float interpProgress = entity.progress - 1.0F + partialTicks;
				float rise = (interpProgress - 42.0F) * -0.03F;
				//stack.mulPose(Axis.YP.rotationDegrees(Math.min(interpProgress * 8.6747F, 360.0F)));
				stack.mulPose(Axis.YP.rotationDegrees(Math.min((float) Math.pow(((float) Math.tanh(interpProgress * 1.5F / 8.3F - 5.0F) + 1.0F) * 0.5F, 0.5F) * 360.0F, 360.0F)));
				if (interpProgress > 42 && interpProgress < 53)
					stack.translate(0.0F, rise, 0.0F);
				if (interpProgress >= 53 && interpProgress < 63)
					stack.translate(0.0F, -0.33F + Math.pow((-0.332F - rise) / 0.2975F, 3.0F) * 0.33F, 0.0F);
				if (interpProgress >= 63 && interpProgress < 73)
					stack.translate(0.0F, 0.63F + rise, 0.0F);
				if (interpProgress >= 73 && interpProgress < 83)
					stack.translate(0.0F, -0.31F + Math.pow((-0.9323952F - rise) / 0.2975F, 3.0F) * 0.31F, 0.0F);
			}
			this.pestle.render(stack, source.getBuffer(TEXTURE), light, overlay);
			stack.popPose();
		}

		stack.popPose();

		if (entity.getLevel() != null && !entity.getItem(3).isEmpty()) {
			stack.pushPose();
			stack.translate(0.5F, 1.43D, 0.5D);
			stack.scale(0.15F, 0.15F, 0.15F);
			stack.translate(0D, Mth.sin((entity.itemBob + partialTicks) / 10.0F) * 0.1F + 0.1F, 0D);
			stack.mulPose(Axis.YP.rotationDegrees(entity.crystalRotation));
			Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(3), ItemDisplayContext.GROUND, light, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}
}
