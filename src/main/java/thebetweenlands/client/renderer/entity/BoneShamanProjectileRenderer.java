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
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BoneShamanProjectileModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.BoneShamanProjectile;

public class BoneShamanProjectileRenderer extends EntityRenderer<BoneShamanProjectile> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bone_shaman_projectile.png");
	private final BoneShamanProjectileModel<BoneShamanProjectile> skull;

	public BoneShamanProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        skull = new BoneShamanProjectileModel<BoneShamanProjectile>(context.bakeLayer(BLModelLayers.BONE_SHAMAN_PROJECTILE));
    }

	 @Override
	 public void render(BoneShamanProjectile entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
		stack.scale(1F, -1F, -1F);
		stack.translate(0F, -1.5F, 0F);
		skull.renderToBuffer(stack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(BoneShamanProjectile entity) {
		return TEXTURE;
	}
}
