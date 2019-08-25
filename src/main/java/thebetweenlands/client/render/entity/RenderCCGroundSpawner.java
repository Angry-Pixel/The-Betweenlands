package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityCCGroundSpawner;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.Stencil;

@SideOnly(Side.CLIENT)
public class RenderCCGroundSpawner extends Render<EntityCCGroundSpawner> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/blocks/compacted_mud.png");

	public RenderCCGroundSpawner(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityCCGroundSpawner entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		this.bindEntityTexture(entity);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x - 0.5D, y + 0.01D, z - 0.5D);

		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		try(Stencil stencil = Stencil.reserve(fbo)) {
			if(stencil.valid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);

				stencil.clear(false);

				stencil.func(GL11.GL_ALWAYS, true);
				stencil.op(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);

				GlStateManager.depthMask(false);
				GlStateManager.colorMask(false, false, false, false);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				GlStateManager.disableTexture2D();

				//Polygon offset required so that there's no z fighting with the window and background wall
				GlStateManager.enablePolygonOffset();
				GlStateManager.doPolygonOffset(-5.0F, -5.0F);

				//Render window through which the hole will be visible
				this.renderWindow();

				GlStateManager.disablePolygonOffset();

				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();

				GlStateManager.depthMask(true);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

				stencil.func(GL11.GL_EQUAL, true);
				stencil.op(GL11.GL_KEEP);
			}

			//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
			GlStateManager.depthFunc(GL11.GL_GEQUAL);
			GlStateManager.colorMask(false, false, false, false);

			this.renderModel();

			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		//Render visible pass
		this.renderModel();

		GlStateManager.popMatrix();
	}

	private void renderWindow() {
		GlStateManager.disableCull();

		Tessellator tessellator = Tessellator.getInstance();

		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(-1, 1, 2).endVertex();
		buffer.pos(-1, 1, -1).endVertex();
		buffer.pos(2, 1, -1).endVertex();
		buffer.pos(2, 1, 2).endVertex();
		tessellator.draw();

		GlStateManager.enableCull();
	}

	private void renderModel() {
		GlStateManager.disableCull();

		Tessellator tessellator = Tessellator.getInstance();

		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

		//ground
		buffer.pos(-1, 0, 2).tex(0, 3).normal(0, 1, 0).endVertex();
		buffer.pos(-1, 0, -1).tex(0, 0).normal(0, 1, 0).endVertex();
		buffer.pos(2, 0, -1).tex(3, 0).normal(0, 1, 0).endVertex();
		buffer.pos(2, 0, 2).tex(3, 3).normal(0, 1, 0).endVertex();

		//north
		buffer.pos(-1, 1, -1).tex(0, 1).normal(0, 0, -1).endVertex();
		buffer.pos(-1, 0, -1).tex(0, 0).normal(0, 0, -1).endVertex();
		buffer.pos(2, 0, -1).tex(3, 0).normal(0, 0, -1).endVertex();
		buffer.pos(2, 1, -1).tex(3, 1).normal(0, 0, -1).endVertex();
		//slope
		buffer.pos(-1, 1, -1).tex(0, 0).normal(0, 0, -1).endVertex();
		buffer.pos(-1, 0, 0).tex(0, 1.4142f).normal(0, 0, -1).endVertex();
		buffer.pos(2, 0, 0).tex(3, 1.4142f).normal(0, 0.5f, -1).endVertex();
		buffer.pos(2, 1, -1).tex(3, 0).normal(0, 0.5f, -1).endVertex();

		//south
		buffer.pos(-1, 1, 2).tex(0, 1).normal(0, 0, 1).endVertex();
		buffer.pos(-1, 0, 2).tex(0, 0).normal(0, 0, 1).endVertex();
		buffer.pos(2, 0, 2).tex(3, 0).normal(0, 0, 1).endVertex();
		buffer.pos(2, 1, 2).tex(3, 1).normal(0, 0, 1).endVertex();
		//slope
		buffer.pos(-1, 1, 2).tex(0, 0).normal(0, 0, 1).endVertex();
		buffer.pos(-1, 0, 1).tex(0, 1.4142f).normal(0, 0, 1).endVertex();
		buffer.pos(2, 0, 1).tex(3, 1.4142f).normal(0, 0.5f, 1).endVertex();
		buffer.pos(2, 1, 2).tex(3, 0).normal(0, 0.5f, 1).endVertex();

		//west
		buffer.pos(-1, 1, -1).tex(0, 1).normal(-1, 0, 0).endVertex();
		buffer.pos(-1, 0, -1).tex(0, 0).normal(-1, 0, 0).endVertex();
		buffer.pos(-1, 0, 2).tex(3, 0).normal(-1, 0, 0).endVertex();
		buffer.pos(-1, 1, 2).tex(3, 1).normal(-1, 0, 0).endVertex();
		//slope
		buffer.pos(-1, 1, -1).tex(0, 0).normal(-1, 0, 0).endVertex();
		buffer.pos(0, 0, -1).tex(0, 1.4142f).normal(-1, 0, 0).endVertex();
		buffer.pos(0, 0, 2).tex(3, 1.4142f).normal(-1, 0.5f, 0).endVertex();
		buffer.pos(-1, 1, 2).tex(3, 0).normal(-1, 0.5f, 0).endVertex();

		//east
		buffer.pos(2, 1, -1).tex(0, 1).normal(1, 0, 0).endVertex();
		buffer.pos(2, 0, -1).tex(0, 0).normal(1, 0, 0).endVertex();
		buffer.pos(2, 0, 2).tex(3, 0).normal(1, 0, 0).endVertex();
		buffer.pos(2, 1, 2).tex(3, 1).normal(1, 0, 0).endVertex();
		//slope
		buffer.pos(2, 1, -1).tex(0, 0).normal(1, 0, 0).endVertex();
		buffer.pos(1, 0, -1).tex(0, 1.4142f).normal(1, 0, 0).endVertex();
		buffer.pos(1, 0, 2).tex(3, 1.4142f).normal(1, 0.5f, 0).endVertex();
		buffer.pos(2, 1, 2).tex(3, 0).normal(1, 0.5f, 0).endVertex();

		tessellator.draw();

		GlStateManager.enableCull();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCCGroundSpawner entity) {
		return TEXTURE;
	}
}
