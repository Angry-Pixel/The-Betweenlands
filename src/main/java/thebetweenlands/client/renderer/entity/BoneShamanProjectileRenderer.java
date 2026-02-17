package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import thebetweenlands.client.renderer.SpikeRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.BoneShamanProjectile;

public class BoneShamanProjectileRenderer extends EntityRenderer<BoneShamanProjectile> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bone_spike.png");
	private SpikeRenderer spike;
	
	public BoneShamanProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        spike = new SpikeRenderer(3, 0.25F, 0.5F, 0.25F, 0);
    }

	 @Override
	 public void render(BoneShamanProjectile entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
		stack.pushPose();
		stack.translate(0F, 0.25F, -0.5F);
		stack.pushPose();
		stack.mulPose(Axis.XP.rotationDegrees(90.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
		stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.prevAnimationTicks, entity.animationTicks)));
		stack.pushPose();
		stack.translate(-0.125F, 0.0F, -0.125F);
		spike.render(stack.last(), consumer,  packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(1F, 1.0F, 1.0F, 1.0F));
		stack.popPose();
		stack.popPose();
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(BoneShamanProjectile entity) {
		return TEXTURE;
	}
}
