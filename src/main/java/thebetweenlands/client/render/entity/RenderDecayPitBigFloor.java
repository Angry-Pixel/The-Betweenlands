package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityDecayPitBigFloor;
@SideOnly(Side.CLIENT)
public class RenderDecayPitBigFloor extends Render<EntityDecayPitBigFloor> {

	public static final ResourceLocation OUTER_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/outer_ring.png");
	public static final ResourceLocation INNER_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/inner_ring.png");
	public static final ResourceLocation MASK_MUD_TILE_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/gear_mask.png");
	public static final ResourceLocation MASK_MUD_TILE_TEXTURE_HOLE = new ResourceLocation("thebetweenlands:textures/entity/gear_mask_hole.png");
	public static final ResourceLocation VERTICAL_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/vertical_ring.png");
	public RenderDecayPitBigFloor(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitBigFloor entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float ring_rotate = entity.animationTicksPrev + (entity.animationTicks - entity.animationTicksPrev) * partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		if(ring_rotate >=360)
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glStencilMask(0xFF);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1F, 1F, 1F, 1F);

		for (int part = 0; part < 24; part++) {
			rendertopVertex(entity, x, y + 1D + 0.001D, z, 15F * part, 7.5D, 7.5D, 4.25D, 4.25D, true);
			//rendertopVertex(entity, x, y + 0.001F, z, 15F * part, 4.25D, 4.25D, 2.75D, 2.75D, false);
		}

		if(ring_rotate >=360)
			GL11.glStencilMask(0x00);
		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 0, 0xFF);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1D, z);
		GlStateManager.rotate(ring_rotate, 0F, 1F, 0F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bindTexture(OUTER_RING_TEXTURE);
		buffer.pos(7.5D, 0.01F, 7.5D).tex(1, 0).endVertex();
		buffer.pos(7.5D, 0.01F, -7.5D).tex(0, 0).endVertex();
		buffer.pos(-7.5D, 0.01F, -7.5D).tex(0, 1).endVertex();
		buffer.pos(-7.5D, 0.01F, 7.5D).tex(1, 1).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
/*
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(-ring_rotate, 0F, 1F, 0F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bindTexture(INNER_RING_TEXTURE);
		buffer.pos(4.25D, 0.01F, 4.25D).tex(1, 0).endVertex();
		buffer.pos(4.25D, 0.01F, -4.25D).tex(0, 0).endVertex();
		buffer.pos(-4.25D, 0.01F, -4.25D).tex(0, 1).endVertex();
		buffer.pos(-4.25D, 0.01F, 4.25D).tex(1, 1).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
*/
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_STENCIL_TEST);

		GlStateManager.popMatrix();
	}

	private void rendertopVertex(EntityDecayPitBigFloor entity, double x, double y, double z, double angle, double offsetXOuter, double offsetZOuter, double offsetXInner, double offsetZInner, boolean renderVertical) {
		double startAngle = Math.toRadians(angle);
		double endAngle = Math.toRadians(angle + 15D);
		double offSetXOut1 = -Math.sin(startAngle) * offsetXOuter;
		double offSetZOut1 = Math.cos(startAngle) * offsetZOuter;
		double offSetXIn1 = -Math.sin(startAngle) * offsetXInner;
		double offSetZIn1 = Math.cos(startAngle) * offsetZInner;

		double offSetXOut2 = -Math.sin(endAngle) * offsetXOuter;
		double offSetZOut2 = Math.cos(endAngle) * offsetXOuter;
		double offSetXIn2 = -Math.sin(endAngle) * offsetXInner;
		double offSetZIn2 = Math.cos(endAngle) * offsetXInner;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		if (!renderVertical) {
		/*	buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bindTexture(MASK_MUD_TILE_TEXTURE);
			buffer.pos(offSetXOut1, 0F, offSetZOut1).tex(1, 0).endVertex();
			buffer.pos(offSetXIn1, 0F, offSetZIn1).tex(0, 0).endVertex();
			buffer.pos(offSetXIn2, 0F, offSetZIn2).tex(0, 1).endVertex();
			buffer.pos(offSetXOut2, 0F, offSetZOut2).tex(1, 1).endVertex();
			tessellator.draw();*/
		}
		if (renderVertical) {
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			if(angle != 15 && angle != 105 && angle != 195 && angle != 285)
				bindTexture(MASK_MUD_TILE_TEXTURE);
			else
				bindTexture(MASK_MUD_TILE_TEXTURE_HOLE);
			buffer.pos(offSetXOut1, 0F, offSetZOut1).tex(1, 0).endVertex();
			buffer.pos(offSetXIn1, 0F, offSetZIn1).tex(0, 0).endVertex();
			buffer.pos(offSetXIn2, 0F, offSetZIn2).tex(0, 1).endVertex();
			buffer.pos(offSetXOut2, 0F, offSetZOut2).tex(1, 1).endVertex();
			tessellator.draw();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bindTexture(VERTICAL_RING_TEXTURE);
			buffer.pos(offSetXIn1, 0F, offSetZIn1).tex(1, 1).endVertex();
			buffer.pos(offSetXIn1, -1F, offSetZIn1).tex(1, 0).endVertex();
			buffer.pos(offSetXIn2, -1F, offSetZIn2).tex(0, 0).endVertex();
			buffer.pos(offSetXIn2, 0F, offSetZIn2).tex(0, 1).endVertex();
			tessellator.draw();
		}
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitBigFloor entity) {
		return null;
	}
}