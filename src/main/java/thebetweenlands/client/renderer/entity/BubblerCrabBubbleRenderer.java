package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.BubblerCrabBubble;

public class BubblerCrabBubbleRenderer extends EntityRenderer<BubblerCrabBubble> {
	public final static ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bubbler_crab_bubble.png");
	protected final EntityRenderDispatcher renderDispatcher;
	private final float scale;
	
	public BubblerCrabBubbleRenderer(Context context) {
		super(context);
		renderDispatcher = context.getEntityRenderDispatcher();
		this.scale = 1.0F;
	}

	@Override
	public void render(BubblerCrabBubble entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
		stack.pushPose();

		float radius = Math.min(entity.swell, 120) * 0.0065f;
		
		stack.translate(0F, 1 + radius * 0.25f - 0.05f, 0F);
		//RenderSystem.enableRescaleNormal();
		stack.scale(this.scale + radius, this.scale + radius, this.scale + radius);
		Tesselator tessellator = Tesselator.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;
		stack.mulPose(Axis.YP.rotationDegrees(180.0F - renderDispatcher.camera.getYRot()));
		stack.mulPose(Axis.XP.rotationDegrees((renderDispatcher.options.getCameraType() == CameraType.THIRD_PERSON_FRONT ? -1F : 1F) * - renderDispatcher.camera.getXRot()));
/*
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.enableOutlineMode(this.getTeamColor(entity));
		}
*/
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		buffer.addVertex(stack.last().pose(), -0.25F, -0.25F, 0.0F).setUv(minU, maxV).setColor(1F, 1F, 1F, 1F).setNormal(0.0F, 1.0F, 0.0F);
		buffer.addVertex(stack.last().pose(), 0.25F, -0.25F, 0.0F).setUv(maxU, maxV).setColor(1F, 1F, 1F, 1F).setNormal(0.0F, 1.0F, 0.0F);
		buffer.addVertex(stack.last().pose(), 0.25F, 0.25F, 0.0F).setUv(maxU, minV).setColor(1F, 1F, 1F, 1F).setNormal(0.0F, 1.0F, 0.0F);
		buffer.addVertex(stack.last().pose(), -0.25F, 0.25F, 0.0F).setUv(minU, minV).setColor(1F, 1F, 1F, 1F).setNormal(0.0F, 1.0F, 0.0F);
		BufferUploader.drawWithShader(buffer.buildOrThrow());
/*
		if (this.renderOutlines) {
			stack.disableOutlineMode();
			stack.disableColorMaterial();
		}
*/
		//RenderSystem.disableRescaleNormal();
		RenderSystem.disableBlend();
		stack.popPose();
		super.render(entity, entityYaw, partialTick, stack, bufferSource, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(BubblerCrabBubble entity) {
		return TEXTURE;
	}
}
