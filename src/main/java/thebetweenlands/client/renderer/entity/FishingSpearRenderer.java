package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import thebetweenlands.client.model.entity.FishingSpearModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.spear.FishingSpear;
import thebetweenlands.common.entity.projectile.spear.RobustFishingSpear;

import java.util.Optional;

public class FishingSpearRenderer<T extends FishingSpear, M extends FishingSpearModel<T>> extends EntityRenderer<T> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/fishing_spear.png");
	private final M model;

	public FishingSpearRenderer(EntityRendererProvider.Context context, M model) {
		super(context);
		this.model = model;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		stack.pushPose();
		if (!(entity instanceof RobustFishingSpear robust) || robust.clientReturnTick <= 0) {
			stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
			stack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));
		} else {
			var owner = entity.getOwner();
			double d0 = owner.getX() - entity.getX();
			double d1 = owner.getZ() - entity.getZ();
			var facing = (float)(Mth.atan2(d1, d0) * Mth.RAD_TO_DEG) + 90.0F;
			stack.mulPose(Axis.YP.rotationDegrees(-facing));
			stack.mulPose(Axis.ZP.rotationDegrees(-180.0F));
			stack.mulPose(Axis.XP.rotationDegrees(120.0F));
		}

		float shake = entity.shakeTime - partialTick;
		if (shake > 0.0F) {
			float f10 = -Mth.sin(shake * 3.0F) * shake;
			stack.mulPose(Axis.ZP.rotationDegrees(f10));
		}

		this.model.prepareMobModel(entity, 0.0F, 0.0F, partialTick);
		this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);
		this.model.renderToBuffer(stack, buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
		stack.popPose();
		super.render(entity, entityYaw, partialTick, stack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEXTURE;
	}
}
