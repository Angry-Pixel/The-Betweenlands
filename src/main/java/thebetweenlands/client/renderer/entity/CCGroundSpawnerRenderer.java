package thebetweenlands.client.renderer.entity;

import org.joml.Matrix4f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.CCGroundSpawner;

public class CCGroundSpawnerRenderer extends EntityRenderer<CCGroundSpawner> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_shingles.png");
	private static final ResourceLocation HOLE_TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_hole.png");
	private static final ResourceLocation GROUND_TEXTURE = TheBetweenlands.prefix("textures/entity/cc_ground_spawner_ground.png");

	public CCGroundSpawnerRenderer(Context context) {
		super(context);
	}

	@Override
	public void render(CCGroundSpawner entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
		poseStack.pushPose();
		poseStack.translate( -0.5D, 0.01D, -0.5D);
		Matrix4f matrix4f = poseStack.last().pose();

		RenderTarget fbo = Minecraft.getInstance().getMainRenderTarget();
/*
		try(Stencil stencil = Stencil.reserve(fbo)) {
			if(stencil.isValid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);

				stencil.clear(false);

				stencil.func(GL11.GL_ALWAYS, true);
				stencil.op(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);

				GlStateManager._depthMask(false);
				GlStateManager._colorMask(false, false, false, false);
				//GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

				//GlStateManager.disableAlpha();
				//GlStateManager.disableTexture2D();

				//Polygon offset required so that there's no  fighting with the window and background wall
				GlStateManager._enablePolygonOffset();
				GlStateManager._polygonOffset(-5.0F, -5.0F);

				//Render window through which the hole will be visible
				this.renderWindow(entity, matrix4f, buffer, packedLight);

				GlStateManager._disablePolygonOffset();

				//GlStateManager.enableAlpha();
				//GlStateManager.enableTexture2D();

				GlStateManager._depthMask(true);
				GlStateManager._colorMask(true, true, true, true);
				//GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

				stencil.func(GL11.GL_EQUAL, true);
				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP); //dunno
			}

			//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
			GlStateManager._depthFunc(GL11.GL_GEQUAL);
			GlStateManager._colorMask(false, false, false, false);

			this.renderModel(entity, matrix4f, buffer, packedLight);

			GlStateManager._colorMask(true, true, true, true);
			GlStateManager._depthFunc(GL11.GL_LEQUAL);

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
*/
		//Render visible pass
		this.renderModel(entity, matrix4f, buffer, packedLight);
		poseStack.popPose();
	}

	private void renderWindow(CCGroundSpawner entity, Matrix4f matrix4f, MultiBufferSource buffer, int packedLight) {
		VertexConsumer texture = buffer.getBuffer(RenderType.entitySmoothCutout(getTextureLocation(entity)));
		GlStateManager._disableCull();
		texture.addVertex(matrix4f, -1, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, -1, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, 2, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, 2, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		GlStateManager._enableCull();
	}

	private void renderModel(CCGroundSpawner entity, Matrix4f matrix4f, MultiBufferSource buffer, int packedLight) {
		GlStateManager._disableCull();
		GlStateManager._polygonOffset(0.05F, 3.0F);
		GlStateManager._enablePolygonOffset();
		VertexConsumer texture = buffer.getBuffer(RenderType.entitySmoothCutout(getTextureLocation(entity)));

		//north
		texture.addVertex(matrix4f, -1, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, -1, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, 2, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, 2, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(3, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		//slope
		texture.addVertex(matrix4f, -1, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, -1, 0, 0).setColor(1F, 1F, 1F, 1F).setUv(0, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, -1);
		texture.addVertex(matrix4f, 2, 0, 0).setColor(1F, 1F, 1F, 1F).setUv(3, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0.5f, -1);
		texture.addVertex(matrix4f, 2, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0.5f, -1);

		//south
		texture.addVertex(matrix4f, -1, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, 1);
		texture.addVertex(matrix4f, -1, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, 1);
		texture.addVertex(matrix4f, 2, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, 1);
		texture.addVertex(matrix4f, 2, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, 1);
		//slope
		texture.addVertex(matrix4f, -1, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, 1);
		texture.addVertex(matrix4f, -1, 0, 1).setColor(1F, 1F, 1F, 1F).setUv(0, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0, 1);
		texture.addVertex(matrix4f, 2, 0, 1).setColor(1F, 1F, 1F, 1F).setUv(3, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0.5f, 1);
		texture.addVertex(matrix4f, 2, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 0.5f, 1);

		//west
		texture.addVertex(matrix4f, -1, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0, 0);
		texture.addVertex(matrix4f, -1, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0, 0);
		texture.addVertex(matrix4f, -1, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0, 0);
		texture.addVertex(matrix4f, -1, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0, 0);
		//slope
		texture.addVertex(matrix4f, -1, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0, 0);
		texture.addVertex(matrix4f, 0, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0, 0);
		texture.addVertex(matrix4f, 0, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0.5f, 0);
		texture.addVertex(matrix4f, -1, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(-1, 0.5f, 0);

		//east
		texture.addVertex(matrix4f, 2, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0, 0);
		texture.addVertex(matrix4f, 2, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0, 0);
		texture.addVertex(matrix4f, 2, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0, 0);
		texture.addVertex(matrix4f, 2, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0, 0);
		//slope
		texture.addVertex(matrix4f, 2, 1, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0, 0);
		texture.addVertex(matrix4f, 1, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0, 0);
		texture.addVertex(matrix4f, 1, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0.5f, 0);
		texture.addVertex(matrix4f, 2, 1, 2).setColor(1F, 1F, 1F, 1F).setUv(3, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(1, 0.5f, 0);

		GlStateManager._polygonOffset(0, 0);
		GlStateManager._disablePolygonOffset();
		GlStateManager._enableCull();
		texture = buffer.getBuffer(RenderType.entitySmoothCutout(getHoleTextureLocation(entity)));
		//hole
		texture.addVertex(matrix4f, -1, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture.addVertex(matrix4f, -1, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture.addVertex(matrix4f, 2, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture.addVertex(matrix4f, 2, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture = buffer.getBuffer(RenderType.entitySmoothCutout(getGroundTextureLocation(entity)));
		//ground
		texture.addVertex(matrix4f, -1, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture.addVertex(matrix4f, -1, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture.addVertex(matrix4f, 2, 0, -1).setColor(1F, 1F, 1F, 1F).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
		texture.addVertex(matrix4f, 2, 0, 2).setColor(1F, 1F, 1F, 1F).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, 240).setNormal(0, 1, 0);
	}

	@Override
	public ResourceLocation getTextureLocation(CCGroundSpawner entity) {
		return TEXTURE;
	}

	public ResourceLocation getHoleTextureLocation(CCGroundSpawner entity) {
		return HOLE_TEXTURE;
	}

	public ResourceLocation getGroundTextureLocation(CCGroundSpawner entity) {
		return GROUND_TEXTURE;
	}
}
