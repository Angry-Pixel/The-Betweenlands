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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.WightModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.world.event.WinterEvent;

public class WightRenderer<T extends Wight> extends MobRenderer<T, WightModel<T>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/wight.png");
	public static final ResourceLocation TEXTURE_FROSTY = TheBetweenlands.prefix("textures/entity/wight_frosty.png");

	public WightRenderer(EntityRendererProvider.Context context) {
		super(context, new WightModel<>(context.bakeLayer(BLModelLayers.WIGHT)), 0.5F);
	}

	@Override
	public void render(T entity, float yaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {
		if (!entity.isVolatile()) {
			this.model.renderHeadOnly = false;
			this.renderWithTranslucentFix(entity, partialTicks, stack, buffer, light, FastColor.ARGB32.colorFromFloat(1.0F - entity.getHidingAnimation(partialTicks) * 0.5F, 1.0F, 1.0F, 1.0F));
		} else {
			this.model.renderHeadOnly = true;

			stack.pushPose();

			if (entity.getVehicle() != null) {
				stack.scale(-1.0F, -1.0F, 1.0F);
				stack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) / 30.0F * 360.0F));
				stack.mulPose(Axis.YP.rotationDegrees(180));
				stack.translate(0, -0.25D, 0.8D);
				stack.scale(0.5F, 0.5F, 0.5F);

				stack.mulPose(Axis.XP.rotation(0.4F));
				this.model.prepareMobModel(entity, 0.0F, 0.0F, partialTicks);
				VertexConsumer consumer = buffer.getBuffer(BLRenderTypes.translucentCulling(this.getTextureLocation(entity)));
				this.model.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY);
				consumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
				this.model.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.4F, 1.0F, 1.0F, 1.0F));
			} else {
				this.renderWithTranslucentFix(entity, partialTicks, stack, buffer, light, FastColor.ARGB32.colorFromFloat(0.4F, 1.0F, 1.0F, 1.0F));
			}

			stack.popPose();
		}
	}

	private void renderWithTranslucentFix(T entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int color) {
		if (NeoForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entity, this, partialTicks, stack, buffer, light)).isCanceled()) return;
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
		int i = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
		if (flag) {
			this.model.renderToBuffer(stack, buffer.getBuffer(BLRenderTypes.translucentCulling(this.getTextureLocation(entity))), light, i, flag1 ? 654311423 : -1);
		}
		RenderType rendertype = this.getRenderType(entity, flag, flag1, flag2);
		if (rendertype != null) {
			this.model.renderToBuffer(stack, buffer.getBuffer(rendertype), light, i, flag1 ? 654311423 : color);
		}

		if (!entity.isSpectator()) {
			for (RenderLayer<T, WightModel<T>> renderlayer : this.layers) {
				renderlayer.render(stack, buffer, light, entity, f5, f4, partialTicks, f9, f2, f6);
			}
		}

		stack.popPose();

		var event = new RenderNameTagEvent(entity, entity.getDisplayName(), this, stack, buffer, light, partialTicks);
		NeoForge.EVENT_BUS.post(event);
		if (event.canRender().isTrue() || event.canRender().isDefault() && this.shouldShowName(entity)) {
			this.renderNameTag(entity, event.getContent(), stack, buffer, light, partialTicks);
		}
		NeoForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entity, this, partialTicks, stack, buffer, light));
	}

	public void scale(T entity, PoseStack stack, float partialTicks) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(), 10.0f, -1, -1, -1));
		}

		float scale = 0.9F / 40F * (entity.getGrowthFactor(partialTicks));
		stack.scale(0.9F, entity.isInTar() ? scale : 0.9F, 0.9F);

		if (entity.isVolatile()) {
			stack.scale(0.5f, 0.5f, 0.5f);
			stack.translate(0, 1.0D, 0);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Wight entity) {
		if (WinterEvent.isFroooosty(entity.level())) {
			return TEXTURE_FROSTY;
		}
		return TEXTURE;
	}

}
