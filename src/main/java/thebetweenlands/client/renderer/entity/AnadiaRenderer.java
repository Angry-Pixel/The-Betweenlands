package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.AnadiaModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.entities.fishing.anadia.AnadiaParts;

import javax.annotation.Nullable;

public class AnadiaRenderer extends MobRenderer<Anadia, AnadiaModel> {

	public AnadiaRenderer(EntityRendererProvider.Context context) {
		super(context, new AnadiaModel(context.bakeLayer(BLModelLayers.ANADIA)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Anadia entity) {
		return TheBetweenlands.prefix("textures/entity/anadia_1_base.png");
	}

	@Override
	protected void setupRotations(Anadia entity, PoseStack stack, float bob, float yBodyRot, float partialTicks, float scale) {
		float smoothedPitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
		float smoothedYaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;

		stack.mulPose(Axis.YP.rotationDegrees(180 - smoothedYaw));

		float fishSize = entity.getFishSize();
		stack.translate(0, 0.4f * fishSize, 0);
		//stack.mulPose(Axis.XP.rotationDegrees(-smoothedPitch));
		stack.translate(0, -0.4f * fishSize, 0);

		super.setupRotations(entity, stack, bob, yBodyRot, partialTicks, scale);
	}

	@Override
	protected void scale(Anadia entity, PoseStack stack, float partialTick) {
		stack.scale(entity.getFishSize(), entity.getFishSize(), entity.getFishSize());
	}

	//[VanillaCopy] of LivingEntityRenderer.render, replaced Model.renderToBuffer with a custom rendering method
	@Override
	public void render(Anadia entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
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
		this.renderFish(entity, stack, buffer, partialTicks, packedLight, flag, flag1, flag2);

		if (!entity.isSpectator()) {
			for (RenderLayer<Anadia, AnadiaModel> renderlayer : this.layers) {
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

	private void renderFish(Anadia entity, PoseStack stack, MultiBufferSource buffer, float partialTicks, int light, boolean visible, boolean translucent, boolean glowing) {
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
		int color = translucent ? 654311423 : -1;

		RenderType rendertype = this.getRenderType(entity.getHeadType(), entity, visible, translucent, glowing);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			this.model.renderPart(entity.getHeadType(), "head", stack, vertexconsumer, light, overlay, color);
		}

		rendertype = this.getRenderType(entity.getBodyType(), entity, visible, translucent, glowing);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			this.model.renderPart(entity.getBodyType(), "body", stack, vertexconsumer, light, overlay, color);
		}

		rendertype = this.getRenderType(entity.getTailType(), entity, visible, translucent, glowing);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			this.model.renderPart(entity.getTailType(), "tail", stack, vertexconsumer, light, overlay, color);
		}
	}

	@Nullable
	protected RenderType getRenderType(Enum<?> type, Anadia entity, boolean bodyVisible, boolean translucent, boolean glowing) {
		ResourceLocation texture = this.assembleTexturePath(type, entity.getFishColor());
		if (translucent) {
			return RenderType.itemEntityTranslucentCull(texture);
		} else if (bodyVisible) {
			return this.model.renderType(texture);
		} else {
			return glowing ? RenderType.outline(texture) : null;
		}
	}

	private ResourceLocation assembleTexturePath(Enum<?> type, AnadiaParts.AnadiaColor color) {
		return TheBetweenlands.prefix("textures/entity/anadia/anadia_" + (type.ordinal() + 1) + "_" + color.getSerializedName() + ".png");
	}
}
