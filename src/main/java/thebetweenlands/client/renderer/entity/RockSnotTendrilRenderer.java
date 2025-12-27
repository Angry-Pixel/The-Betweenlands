package thebetweenlands.client.renderer.entity;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.RockSnotGrabberModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.RockSnotTendril;

public class RockSnotTendrilRenderer extends EntityRenderer<RockSnotTendril> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/rock_snot_grabber.png");
	private static final ResourceLocation VERTICAL_RING_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_vertical_ring.png");
	public final RockSnotGrabberModel GRABBER_MODEL;

	public RockSnotTendrilRenderer(Context context) {
		super(context);
		GRABBER_MODEL = new RockSnotGrabberModel(context.bakeLayer(BLModelLayers.ROCK_SNOT_GRABBER));
	}

	@Override
	public void render(RockSnotTendril entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
		super.render(entity, entityYaw, partialTick, stack, bufferSource, packedLight);
		if(entity.getParentEntity() != null) {
			float smoothedYaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTick;
			float smoothedPitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTick;
			stack.pushPose();
			stack.translate(0, 0.125F, 0);
			if (entity.getExtending())
				stack.mulPose(Axis.YP.rotationDegrees(smoothedYaw + 180F));
			else
				stack.mulPose(Axis.YP.rotationDegrees(smoothedYaw));
			stack.mulPose(Axis.XP.rotationDegrees(smoothedPitch + 90F));
			VertexConsumer consumer = bufferSource.getBuffer(RenderType.entitySmoothCutout(getTextureLocation(entity)));
			GRABBER_MODEL.renderToBuffer(stack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
			stack.popPose();
	
			stack.pushPose();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			Tesselator tessellator = Tesselator.getInstance();
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, VERTICAL_RING_TEXTURE);
			BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			for (int part = 0; part < 8; part++)
				buildRingQuads(entity, stack, buffer, packedLight, (float) (entity.getParentEntity().getX() - entity.getX()), (float) (entity.getParentEntity().getY() - entity.getY()), (float) (entity.getParentEntity().getZ() - entity.getZ()), 45F * part, 0.125F, 0.125F, 0.25F, 0.25F);
			BufferUploader.drawWithShader(buffer.buildOrThrow());
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			stack.popPose();
		}
	}

	public void buildRingQuads(RockSnotTendril entity, PoseStack stack, BufferBuilder buffer, int packedLight, float xp, float yp, float zp, float angle, float offsetXInner, float offsetZInner, float offsetXInnerP, float offsetZInnerP) {
		Matrix4f matrix4f = stack.last().pose();
		float startAngle = (float) Math.toRadians(angle);
		float endAngle = (float) Math.toRadians(angle + 45D);

		float startAngleP = (float) Math.toRadians(angle + 180);
		float endAngleP = (float) Math.toRadians(angle + 235D);

		float offSetXIn1 = (float) (-Math.sin(startAngle) * offsetXInner);
		float offSetZIn1 = (float) (Math.cos(startAngle) * offsetZInner);
		float offSetXIn2 = (float) (-Math.sin(endAngle) * offsetXInner);
		float offSetZIn2 = (float) (Math.cos(endAngle) * offsetZInner);

		float offSetXIn1P = (float) (-Math.sin(startAngleP) * offsetXInnerP);
		float offSetZIn1P = (float) (Math.cos(startAngleP) * offsetZInnerP);
		float offSetXIn2P = (float) (-Math.sin(endAngleP) * offsetXInnerP);
		float offSetZIn2P = (float) (Math.cos(endAngleP) * offsetZInnerP);

		buffer.addVertex(matrix4f, xp - offSetXIn1P, yp + 0.0625F, zp + offSetZIn1P).setColor(1F, 1F, 1F, 1F).setUv(1, 1);
		buffer.addVertex(matrix4f, -offSetXIn1, 0.125F, offSetZIn1).setColor(1F, 1F, 1F, 1F).setUv(1, 0);
		buffer.addVertex(matrix4f, -offSetXIn2, 0.125F, offSetZIn2).setColor(1F, 1F, 1F, 1F).setUv(0, 0);
		buffer.addVertex(matrix4f, xp - offSetXIn2P, yp + 0.0625F, zp + offSetZIn2P).setColor(1F, 1F, 1F, 1F).setUv(0, 1);

		buffer.addVertex(matrix4f, xp + offSetXIn1P, yp + 0.0625F, zp + offSetZIn1P).setColor(1F, 1F, 1F, 1F).setUv(1, 1);
		buffer.addVertex(matrix4f, offSetXIn1, 0.125F, offSetZIn1).setColor(1F, 1F, 1F, 1F).setUv(1, 0);
		buffer.addVertex(matrix4f, offSetXIn2, 0.125F, offSetZIn2).setColor(1F, 1F, 1F, 1F).setUv(0, 0);
		buffer.addVertex(matrix4f, xp + offSetXIn2P, yp + 0.0625F, zp + offSetZIn2P).setColor(1F, 1F, 1F, 1F).setUv(0, 1);
	}

	@Override
	public ResourceLocation getTextureLocation(RockSnotTendril entity) {
		return TEXTURE;
	}
}
