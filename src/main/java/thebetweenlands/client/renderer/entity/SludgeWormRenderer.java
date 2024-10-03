package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SludgeWormModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.SludgeWorm;
import thebetweenlands.common.entity.monster.SludgeWormMultipart;

public class SludgeWormRenderer extends MobRenderer<SludgeWorm, SludgeWormModel> {
	public static final ResourceLocation TEXTURE_HEAD = TheBetweenlands.prefix("textures/entity/sludge_worm_head.png");
	public static final ResourceLocation TEXTURE_BODY = TheBetweenlands.prefix("textures/entity/sludge_worm_body.png");


	public SludgeWormRenderer(EntityRendererProvider.Context context) {
		super(context, new SludgeWormModel(context.bakeLayer(BLModelLayers.SLUDGE_WORM)), 0F);
	}

	@Override
	public void render(SludgeWorm entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
/*
		boolean isVisible = this.isBodyVisible(entity);
		boolean isTranslucentToPlayer = !isVisible && !entity.isInvisibleTo(Minecraft.getInstance().player);
		if(!isVisible && !isTranslucentToPlayer) {
			return;
		}

		boolean useBrightness = this.setDoRenderBrightness(entity, partialTicks);

		if(isTranslucentToPlayer) {
			stack.enableBlendProfile(stack.Profile.TRANSPARENT_MODEL);
		}
		
		boolean useTeamColors = false;

		if(this.renderOutlines) {
			useTeamColors = this.setScoreTeamColor(entity);
			stack.enableColorMaterial();
			stack.enableOutlineMode(this.getTeamColor(entity));
		}
	*/	
		stack.pushPose();

		float totalAngleDiff = 0.0f;

		for(int i = 1; i < entity.parts.length; i++) {
			SludgeWormMultipart prevPart = entity.parts[i - 1];
			SludgeWormMultipart part = entity.parts[i];

			double yawDiff = (prevPart.getYRot() - part.getYRot()) % 360.0F;
			double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;

			totalAngleDiff += (float) Math.abs(yawInterpolant);
		}

		float avgAngleDiff = totalAngleDiff;
		if(entity.parts.length > 1) {
			avgAngleDiff /= (entity.parts.length - 1);
		}

		float avgWibbleStrength = Math.clamp(1.0F - avgAngleDiff / 60.0F, 0, 1);

		float x = 0F;
		float y = 0F;
		float z = 0F;
		renderHead(stack, buffer, packedLight, entity, 1, x, y + 1.5F, z, entity.parts[0].getYRot(), avgWibbleStrength, partialTicks);

		double ex = entity.xOld + (entity.getX() - entity.xOld) * (double)partialTicks;
		double ey = entity.yOld + (entity.getY() - entity.yOld) * (double)partialTicks;
		double ez = entity.zOld + (entity.getZ() - entity.zOld) * (double)partialTicks;

		double rx = ex - x;
		double ry = ey - y;
		double rz = ez - z;

		float zOffset = 0;

		for(int i = 1; i < entity.parts.length - 1; i++) {
			
			renderBodyPart(stack, buffer, packedLight, entity, entity.parts[i], entity.parts[i - 1], rx, ry, rz, i, avgWibbleStrength, zOffset -= 0.001F, partialTicks);
		//	renderDebugBoundingBox(stack, buffer, entity.parts[i], x, y, z, entityYaw, partialTicks, entity.parts[i].xo - entity.xo, entity.parts[i].yo - entity.yo, entity.parts[i].zo - entity.zo);
		}

		renderTailPart(stack, buffer, packedLight, entity, entity.parts[entity.parts.length - 1], entity.parts[entity.parts.length - 2], rx, ry, rz, entity.parts.length - 1, avgWibbleStrength, partialTicks);

		stack.popPose();
	/*	
		if(this.renderOutlines) {
			stack.disableOutlineMode();
			stack.disableColorMaterial();
		}

		if(useTeamColors) {
			this.unsetScoreTeamColor();
		}

		if(isTranslucentToPlayer) {
			stack.disableBlendProfile(stack.Profile.TRANSPARENT_MODEL);
		}

		if(useBrightness) {
			this.unsetBrightness();
		}
		*/
	}
	
	private void renderDebugBoundingBox(PoseStack stack, MultiBufferSource buffer, Entity entity, double x, double y, double z, float yaw, float partialTicks, double xOff, double yOff, double zOff) {
		VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
		AABB axisalignedbb = entity.getBoundingBox();
		AABB axisalignedbb1 = new AABB(axisalignedbb.minX - entity.xo + x + xOff, axisalignedbb.minY - entity.yo + y + yOff, axisalignedbb.minZ - entity.zo + z + zOff, axisalignedbb.maxX - entity.xo + x + xOff, axisalignedbb.maxY - entity.yo + y + yOff, axisalignedbb.maxZ - entity.zo + z + zOff);
		stack.pushPose();
		LevelRenderer.renderLineBox(stack, consumer, axisalignedbb1, 1F, 1F, 1F, 1F);
		stack.popPose();
	}


	private void renderHead(PoseStack stack, MultiBufferSource buffer, int light, SludgeWorm entity, int frame, double x, double y, double z, float yaw, float avgWibbleStrength, float partialTicks) {
		double yawDiff = (yaw - entity.parts[1].getYRot()) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float wibbleStrength = Math.min(avgWibbleStrength, Math.clamp(1.0F - (float)Math.abs(yawInterpolant) / 60.0F, 0, 1));

		stack.pushPose();
		stack.translate(x, y, z);
		stack.scale(-1F, -1F, 1F);
		stack.mulPose(Axis.YP.rotationDegrees(180F + yaw));

		this.renderHeadPartModel(stack, buffer, light, entity, frame, wibbleStrength, partialTicks);

		stack.popPose();
	}

	protected void renderHeadPartModel(PoseStack stack, MultiBufferSource buffer, int light, SludgeWorm entity, int frame, float wibbleStrength, float partialTicks) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE_HEAD));
		model.renderHead(stack, consumer, light, entity, frame, wibbleStrength, partialTicks);
	}

	protected void renderBodyPart(PoseStack stack, MultiBufferSource buffer, int light, SludgeWorm entity, SludgeWormMultipart part, SludgeWormMultipart prevPart, double rx, double ry, double rz, int frame, float avgWibbleStrength, float zOffset, float partialTicks) {
		double x = part.xOld + (part.xo - part.xOld) * (double)partialTicks - rx;
		double y = part.yOld + (part.yo - part.yOld) * (double)partialTicks - ry;
		double z = part.zOld + (part.zo - part.zOld) * (double)partialTicks - rz;

		float yaw = part.yRotO + (part.getYRot() - part.yRotO) * partialTicks;

		double yawDiff = (prevPart.getYRot() - part.getYRot()) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float wibbleStrength = Math.min(avgWibbleStrength, Math.clamp(1.0F - (float)Math.abs(yawInterpolant) / 60.0F, 0, 1));

		stack.pushPose();
		stack.translate(x, y - 1.125f + zOffset, z);// stack.translate(x, y, z);
		//stack.scale(-1F, -1F, 1F);
		stack.mulPose(Axis.YP.rotationDegrees(-yaw));
		this.renderBodyPartModel(stack, buffer, light, entity, frame, wibbleStrength, partialTicks);

		stack.popPose();
	}

	protected void renderBodyPartModel(PoseStack stack, MultiBufferSource buffer, int light, SludgeWorm entity, int frame, float wibbleStrength, float partialTicks) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE_BODY));
		model.renderBody(stack, consumer, light, entity, frame, wibbleStrength, partialTicks);
	}

	protected void renderTailPart(PoseStack stack, MultiBufferSource buffer, int light, SludgeWorm entity, SludgeWormMultipart part, SludgeWormMultipart prevPart, double rx, double ry, double rz, int frame, float avgWibbleStrength, float partialTicks) {
		double x = part.xOld + (part.xo - part.xOld) * (double)partialTicks - rx;
		double y = part.yOld + (part.yo - part.yOld) * (double)partialTicks - ry;
		double z = part.zOld + (part.zo - part.zOld) * (double)partialTicks - rz;

		float yaw = part.yRotO + (part.getYRot() - part.yRotO) * partialTicks;

		double yawDiff = (prevPart.getYRot() - part.getYRot()) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float wibbleStrength = Math.min(avgWibbleStrength, Math.clamp(1.0F - (float)Math.abs(yawInterpolant) / 60.0F, 0, 1));

		stack.pushPose();
		stack.translate(x, y + 1.525f, z);
		stack.scale(-1F, -1F, 1F);
		stack.mulPose(Axis.YP.rotationDegrees(180F + yaw));
		//stack.disableCull();

		this.renderTailPartModel(stack, buffer, light, entity, frame, wibbleStrength, partialTicks);

		//stack.enableCull();
		stack.popPose();
	}

	protected void renderTailPartModel(PoseStack stack, MultiBufferSource buffer, int light, SludgeWorm entity, int frame, float wibbleStrength, float partialTicks) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE_BODY));
		model.renderTail(stack, consumer, light, entity, frame, wibbleStrength, partialTicks);
	}

	@Override
	public ResourceLocation getTextureLocation(SludgeWorm entity) {
		return TEXTURE_HEAD;
	}


}