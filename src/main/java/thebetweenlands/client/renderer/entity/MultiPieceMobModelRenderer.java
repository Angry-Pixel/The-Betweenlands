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
		float yBodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		float yHeadRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
		float yRot = yHeadRot - yBodyRot;
		if (shouldSit && entity.getVehicle() instanceof LivingEntity livingentity) {
			yBodyRot = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
			yRot = yHeadRot - yBodyRot;
			float wrappedYRot = Mth.wrapDegrees(yRot);
			if (wrappedYRot < -85.0F) {
				wrappedYRot = -85.0F;
			}

			if (wrappedYRot >= 85.0F) {
				wrappedYRot = 85.0F;
			}

			yBodyRot = yHeadRot - wrappedYRot;
			if (wrappedYRot * wrappedYRot > 2500.0F) {
				yBodyRot += wrappedYRot * 0.2F;
			}

			yRot = yHeadRot - yBodyRot;
		}

		float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		if (isEntityUpsideDown(entity)) {
			xRot *= -1.0F;
			yRot *= -1.0F;
		}

		yRot = Mth.wrapDegrees(yRot);
		if (entity.hasPose(Pose.SLEEPING)) {
			Direction direction = entity.getBedOrientation();
			if (direction != null) {
				float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				stack.translate((float) (-direction.getStepX()) * eyeHeight, 0.0F, (float) (-direction.getStepZ()) * eyeHeight);
			}
		}

		float scale = entity.getScale();
		stack.scale(scale, scale, scale);
		float ageInTicks = this.getBob(entity, partialTicks);
		this.setupRotations(entity, stack, ageInTicks, yBodyRot, partialTicks, scale);
		stack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(entity, stack, partialTicks);
		stack.translate(0.0F, -1.501F, 0.0F);
		float limbSwingAmount = 0.0F;
		float limbSwing = 0.0F;
		if (!shouldSit && entity.isAlive()) {
			limbSwingAmount = entity.walkAnimation.speed(partialTicks);
			limbSwing = entity.walkAnimation.position(partialTicks);
			if (entity.isBaby()) {
				limbSwing *= 3.0F;
			}

			if (limbSwingAmount > 1.0F) {
				limbSwingAmount = 1.0F;
			}
		}

		this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
		this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, yRot, xRot);
		Minecraft minecraft = Minecraft.getInstance();
		boolean visible = this.isBodyVisible(entity);
		boolean translucent = !visible && !entity.isInvisibleTo(minecraft.player);
		boolean glowing = minecraft.shouldEntityAppearGlowing(entity);
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
		int color = translucent ? 654311423 : -1;
		this.renderModel(entity, stack, buffer, packedLight, overlay, color, new RenderState(partialTicks, visible, translucent, glowing, limbSwing, limbSwingAmount, ageInTicks, xRot, yRot));

		if (!entity.isSpectator()) {
			for (RenderLayer<T, M> renderlayer : this.layers) {
				renderlayer.render(stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, yRot, xRot);
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

	protected abstract void renderModel(T entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state);

	public record RenderState(float partialTick, boolean visible, boolean translucent, boolean glowing, float limbSwing, float limbSwingAmount, float ageInTicks, float xRot, float yRot) {}

}
