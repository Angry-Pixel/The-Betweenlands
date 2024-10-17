package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ShamblerModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.GenericPartEntity;
import thebetweenlands.common.entity.monster.Shambler;

public class ShamblerRenderer extends MobRenderer<Shambler, ShamblerModel <Shambler>> {
	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("thebetweenlands:textures/entity/shambler.png");

	public ShamblerRenderer(EntityRendererProvider.Context context) {
		super(context, new ShamblerModel<>(context.bakeLayer(BLModelLayers.SHAMBLER)), 0.5F);
	}

	@Override
    protected void scale(Shambler entity, PoseStack stack, float partialTickTime) {
		float flap = (float) (Math.sin(entity.tickCount * 0.3F) * 0.8F);
		stack.pushPose();
		stack.translate(0F, 0F - flap * 0.0625F, 0F);
		stack.popPose();
    }

	@Override
	public void render(Shambler entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
		Minecraft minecraft = Minecraft.getInstance();
		boolean isVisible = this.isBodyVisible(entity);
		boolean isTranslucentToPlayer = !isVisible && !entity.isInvisibleTo(minecraft.player);
		boolean isGlowing = minecraft.shouldEntityAppearGlowing(entity);
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
		int colour = isTranslucentToPlayer ? 654311423 : -1;

		if (entity.getTongueLength() > 0) {
			double ex = entity.xOld + (entity.xo - entity.xOld) * (double) partialTicks;
			double ey = entity.yOld + (entity.yo - entity.yOld) * (double) partialTicks;
			double ez = entity.zOld + (entity.zo - entity.zOld) * (double) partialTicks;
			double rx = ex;
			double ry = ey;
			double rz = ez;
			RenderType renderType = getRenderType(entity, isGlowing, isGlowing, isGlowing);
			if (renderType != null) {
				for (int i = 0; i < entity.tongue_array.length; i++) {
					renderTonguePart(entity, entity.tongue_array[i], rx, ry, rz, partialTicks, stack, buffer.getBuffer(renderType), packedLight, overlay, colour);
				}
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Shambler entity) {
		return TEXTURE;
	}

	private void renderTonguePart(Shambler entity, GenericPartEntity part, double rx, double ry, double rz, float partialTicks, PoseStack stack, VertexConsumer consumer, int packedLight, int overlay, int colour) {
		double x = part.xOld + (part.xo - part.xOld) * (double)partialTicks - rx;
        double y = part.yOld + (part.yo - part.yOld) * (double)partialTicks - ry;
        double z = part.zOld + (part.zo - part.zOld) * (double)partialTicks - rz;
        float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;
        float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
		stack.pushPose();
		stack.translate(x, y - 0.85D, z);
		stack.scale(-1F, -1F, 1F);
		stack.mulPose(Axis.YP.rotationDegrees(180F + yaw));
		stack.mulPose(Axis.XP.rotationDegrees(180F + pitch));
		if(part == entity.tongue_end)
			model.renderTongueEnd(stack, consumer, packedLight, overlay, colour);
		else
			model.renderTonguePart(stack, consumer, packedLight, overlay, colour);
		stack.popPose();
	}
}
