package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.LurkerModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Dragonfly;
import thebetweenlands.common.entity.creature.Lurker;

public class LurkerRenderer extends MobRenderer<Lurker, LurkerModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/lurker.png");

	public LurkerRenderer(EntityRendererProvider.Context context) {
		super(context, new LurkerModel(context.bakeLayer(BLModelLayers.LURKER)), 0.5F);
	}

	@Override
	protected void scale(Lurker entity, PoseStack stack, float partialTick) {
		stack.mulPose(Axis.XP.rotationDegrees(-entity.getRotationPitch(partialTick)));
	}

	@Override
	public void render(Lurker entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
		//TODO I hate this
//		if (!entity.getPassengers().isEmpty() && entity.getFirstPassenger() instanceof Dragonfly dragonfly) {
//			stack.pushPose();
//			stack.mulPose(Axis.XP.rotationDegrees(180));
//			stack.translate(0.0D, -0.75D, 0.7D);
//			stack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
//			stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
//			stack.translate(0.0D, 0.0D, 0.8D);
//			dragonfly.setYRot(dragonfly.yRotO = 0);
//			dragonfly.setYHeadRot(dragonfly.yHeadRotO = 0);
//			dragonfly.setYBodyRot(dragonfly.yBodyRotO = 0);
//			Minecraft.getInstance().getEntityRenderDispatcher().render(dragonfly, 0, 0, 0, 0, partialTicks, stack, buffer, packedLight);
//			stack.popPose();
//		}
	}

	@Override
	public ResourceLocation getTextureLocation(Lurker entity) {
		return TEXTURE;
	}
}
