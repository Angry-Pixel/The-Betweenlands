package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityBubblerCrabBubble;

@SideOnly(Side.CLIENT)
public class RenderBubblerCrabBubble extends Render<EntityBubblerCrabBubble> {
	public final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/bubbler_crab_bubble.png");
	
	private final float scale;
	
	public RenderBubblerCrabBubble(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.scale = 1.0F;
	}

	@Override
	public void doRender(EntityBubblerCrabBubble entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();

		float radius = Math.min(entity.swell, 120) * 0.0065f;
		
		this.bindEntityTexture(entity);
		GlStateManager.translate((float)x, (float)y + (1 + radius) * 0.25f - 0.05f, (float)z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(this.scale + radius, this.scale + radius, this.scale + radius);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;
		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vertexbuffer.pos(-0.25D, -0.25D, 0.0D).tex((double)minU, (double)maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertexbuffer.pos(0.25D, -0.25D, 0.0D).tex((double)maxU, (double)maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertexbuffer.pos(0.25D, 0.25D, 0.0D).tex((double)maxU, (double)minV).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertexbuffer.pos(-0.25D, 0.25D, 0.0D).tex((double)minU, (double)minV).normal(0.0F, 1.0F, 0.0F).endVertex();
		tessellator.draw();

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBubblerCrabBubble entity) {
		return TEXTURE;
	}
}
