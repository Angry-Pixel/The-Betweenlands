package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;

public abstract class MultiPieceMobModelRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {

	public MultiPieceMobModelRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	//[VanillaCopy] of LivingEntityRenderer.render, replaced Model.renderToBuffer with a custom rendering method
	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		if (NeoForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entity, this, partialTicks, stack, buffer, packedLight)).isCanceled()) return;
		stack.pushPose();
		this.model.attackTime = this.getAttackAnim(entity, partialTicks);
		boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
		this.model.riding = shouldSit;
		this.model.young = entity.isBaby();
		float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		float f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
		float f2 = f1 - f;
		if (shouldSit && entity.getVehicle() instanceof LivingEntity livingentity) {
			f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
			f2 = f1 - f;
			float f7 = Mth.wrapDegrees(f2);
			if (f7 < -85.0F) {
				f7 = -85.0F;
			}

			if (f7 >= 85.0F) {
				f7 = 85.0F;
			}

			f = f1 - f7;
			if (f7 * f7 > 2500.0F) {
				f += f7 * 0.2F;
			}

			f2 = f1 - f;
		}

		float f6 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		if (isEntityUpsideDown(entity)) {
			f6 *= -1.0F;
			f2 *= -1.0F;
		}

		f2 = Mth.wrapDegrees(f2);
		if (entity.hasPose(Pose.SLEEPING)) {
			Direction direction = entity.getBedOrientation();
			if (direction != null) {
				float f3 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				stack.translate((float) (-direction.getStepX()) * f3, 0.0F, (float) (-direction.getStepZ()) * f3);
			}
		}

		float f8 = entity.getScale();
		stack.scale(f8, f8, f8);
		float f9 = this.getBob(entity, partialTicks);
		this.setupRotations(entity, stack, f9, f, partialTicks, f8);
		stack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(entity, stack, partialTicks);
		stack.translate(0.0F, -1.501F, 0.0F);
		float f4 = 0.0F;
		float f5 = 0.0F;
		if (!shouldSit && entity.isAlive()) {
			f4 = entity.walkAnimation.speed(partialTicks);
			f5 = entity.walkAnimation.position(partialTicks);
			if (entity.isBaby()) {
				f5 *= 3.0F;
			}

			if (f4 > 1.0F) {
				f4 = 1.0F;
			}
		}

		this.model.prepareMobModel(entity, f5, f4, partialTicks);
		this.model.setupAnim(entity, f5, f4, f9, f2, f6);
		Minecraft minecraft = Minecraft.getInstance();
		boolean flag = this.isBodyVisible(entity);
		boolean flag1 = !flag && !entity.isInvisibleTo(minecraft.player);
		boolean flag2 = minecraft.shouldEntityAppearGlowing(entity);
		this.renderModel(entity, stack, buffer, partialTicks, entityYaw, packedLight, flag, flag1, flag2);

		if (!entity.isSpectator()) {
			for (RenderLayer<T, M> renderlayer : this.layers) {
				renderlayer.render(stack, buffer, packedLight, entity, f5, f4, partialTicks, f9, f2, f6);
			}
		}

		stack.popPose();
		var event = new RenderNameTagEvent(entity, entity.getDisplayName(), this, stack, buffer, packedLight, partialTicks);
		NeoForge.EVENT_BUS.post(event);
		if (event.canRender().isTrue() || event.canRender().isDefault() && this.shouldShowName(entity)) {
			this.renderNameTag(entity, event.getContent(), stack, buffer, packedLight, partialTicks);
		}
		NeoForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entity, this, partialTicks, stack, buffer, packedLight));
	}

	protected abstract void renderModel(T entity, PoseStack stack, MultiBufferSource buffer, float partialTick, float yaw, int packedLight, boolean visible, boolean translucent, boolean glowing);
}
