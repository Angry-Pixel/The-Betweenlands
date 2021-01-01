package thebetweenlands.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.util.Stencil;

@SideOnly(Side.CLIENT)
public class GuiFishStaminaBar extends Gui {
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/fish_stamina_bar.png");
	public static final ResourceLocation GUI_FISHING_LINE = new ResourceLocation("thebetweenlands:textures/gui/fishing_line.png");

	@SubscribeEvent
	public void onRenderHUD(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)) {
			EntityPlayerSP player = mc.player;
			if (player != null && player.fishEntity != null && player.fishEntity instanceof EntityBLFishHook) {
				if (player.fishEntity.isRiding() && player.fishEntity.getRidingEntity() instanceof EntityAnadia) {
					if(((EntityAnadia) player.fishEntity.getRidingEntity()).getStaminaTicks() <= 0)
						return;
					int fishpos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getStaminaTicks() * 256 / 180;
					int escapepos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getEscapeTicks() * 256 / 1024;
					int helpMe = (int) ((EntityAnadia) player.fishEntity.getRidingEntity()).getStaminaMods() * 30;
					int escapeDelay = ((EntityAnadia) player.fishEntity.getRidingEntity()).getEscapeDelay();
					int obstructpos1 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction1Ticks();
					int obstructpos2 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction2Ticks();
					int obstructpos3 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction3Ticks();
					int obstructpos4 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction4Ticks() * 256 / 512;
					int treasurePos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getTreasureTicks() * 256 / 1024;
					boolean showTreasure = ((EntityAnadia) player.fishEntity.getRidingEntity()).isTreasureFish();
					boolean treasureUnlocked = ((EntityAnadia) player.fishEntity.getRidingEntity()).getTreasureUnlocked();
					int aniFrame = ((EntityAnadia) player.fishEntity.getRidingEntity()).animationFrame;
					int aniFrameCrab = ((EntityAnadia) player.fishEntity.getRidingEntity()).animationFrameCrab;
					GlStateManager.color(1F, 1F, 1F, 1F);
					mc.renderEngine.bindTexture(GUI_TEXTURE);
					ScaledResolution res = new ScaledResolution(mc);
					renderStaminaBar(-256 + fishpos, -256 + Math.min(256, escapepos),  escapeDelay < 10 ? escapeDelay: 10, 0 - obstructpos1, 0 - obstructpos2, 0 - obstructpos3, 0 - obstructpos4, 0 - treasurePos, showTreasure, treasureUnlocked, (float)res.getScaledWidth() * 0.5F - 128F, (float)res.getScaledHeight() * 0.5F - 120F, aniFrame, aniFrameCrab);
				}
			}
		}
	}

	private void renderStaminaBar(int staminaTicks, int escapeTicks, int escapeDelay, int obstructionTicks1, int obstructionTicks2, int obstructionTicks3, int obstructionTicks4, int treasureTick, boolean hasTreasure, boolean treasureUnlocked,float posX, float posY, int aniFrame, int aniFrameCrab) {
		Minecraft mc = Minecraft.getMinecraft();
		Framebuffer fbo = mc.getFramebuffer();
		GlStateManager.pushMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		drawTexturedModalRect(posX, posY + 2, 0, 0, 256, 25); // background
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

		try (Stencil stencil = Stencil.reserve(fbo)) {
			if (stencil.valid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				stencil.clear(false);
				stencil.func(GL11.GL_ALWAYS, true);
				stencil.op(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);
				GlStateManager.colorMask(false, false, false, false);
				drawTexturedModalRect(posX + 4, posY, 4, 25, 248, 19); // bar stencil
				GlStateManager.colorMask(true, true, true, true);
				stencil.func(GL11.GL_EQUAL, true);
				stencil.op(GL11.GL_KEEP);
			}

			if (hasTreasure)
				drawTexturedModalRect(posX - treasureTick, posY + 1, treasureUnlocked ? 16 : 0, 128, 16, 16); // chest

			drawHangingRope(staminaTicks, posX - staminaTicks + 7, posY + 12, posX + 256 + 16, posY, 0.5F, 0D); // line

			mc.renderEngine.bindTexture(GUI_TEXTURE); // because depth // stuffs :p

			drawTexturedModalRect(posX - staminaTicks - 8, posY + 1, 0 + aniFrame, 48, 16, 16); // fish

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			drawTexturedModalRect(posX - obstructionTicks4 - 8, posY + 2, 0 + aniFrame, 112, 16, 16); // jolly fush
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			drawTexturedModalRect(posX - escapeTicks - 8, posY + 2 + escapeDelay, escapeDelay < 10 ? aniFrameCrab : aniFrame, 144, 16, 16); // crab
			
			drawTexturedModalRect(posX - obstructionTicks1 - 8, posY, 0 + aniFrame, 64, 16, 16); // weed
			drawTexturedModalRect(posX - obstructionTicks3 - 8, posY, 0 + aniFrame, 96, 16, 16); // coral
			drawTexturedModalRect(posX - obstructionTicks2 - 8, posY, 0 + aniFrame, 80, 16, 16); // rock

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		GlStateManager.popMatrix();
	}

	public static void drawHangingRope(int updateCounter, float sx, float sy, float ex, float ey, float hang, double zLevel) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(GUI_FISHING_LINE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		float x1 = sx;
		float y1 = sy;

		float x3 = ex;
		float y3 = ey;

		if(x1 - x3 >= 0.0F && x1 - x3 < 1.0F) {
			x3 = x1 + 1;
		} else if (x1 - x3 < 0.0F && x1 - x3 > -1.0F) {
			x3 = x1 - 1;
		}

		float x2 = (x1 + x3) / 2.0F;
		float y2 = Math.max(y1, y3) + hang + (float)Math.sin((updateCounter + mc.getRenderPartialTicks()) / 25.0F) * 1.5f;

		//Fit parabola
		float a1 = -x1*x1 + x2*x2;
		float b1 = -x1 + x2;
		float d1 = -y1 + y2;
		float a2 = -x2*x2 + x3*x3;
		float b2 = -x2 + x3;
		float d2 = -y2 + y3;
		float b3 = -b2 / b1;
		float a3 = b3 * a1 + a2;
		float d = b3 * d1 + d2;
		float a = d / a3;
		float b = (d1 - a1 * a) / b1;
		float c = y1 - a * x1*x1 - b * x1;

		float px = x1;
		float py = y1;

		float width = 0.75F;

		float pxc1 = x1 - width;
		float pyc1 = y1;
		float pxc2 = x1 + width;
		float pyc2 = y1;

		boolean isTowardsRight = x1 < x3;

		float ropeV1 = isTowardsRight ? 0 : 0.5F;
		float ropeV2 = isTowardsRight ? 0.5F : 0;

		float endV1 = isTowardsRight ? 0.5F : 1.0F;
		float endV2 = isTowardsRight ? 1.0F : 0.5F;

		float endU11 = isTowardsRight ? 0 : 1.0F;
		float endU12 = isTowardsRight ? 0.5F : 0.5F;

		float endU21 = isTowardsRight ? 0.5F : 0.5F;
		float endU22 = isTowardsRight ? 1.0F : 0;

		float u = 0;

		buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

		int pieces = 32;

		for(int i = -1; i <= pieces; i++) {
			float x = x1 + (x3 - x1) / (float) (pieces - 1) * i;
			float y = a * x*x + b * x + c;

			float sideX = y - py;
			float sideY = -(x - px);
			float sideDirLength = (float) Math.sqrt(sideX*sideX + sideY*sideY);
			sideX *= width / sideDirLength;
			sideY *= width / sideDirLength;

			float xc1 = x - sideX;
			float yc1 = y - sideY;
			float xc2 = x + sideX;
			float yc2 = y + sideY;

			if(i == 1) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2 - offX, pyc2 - offY, zLevel).tex(endU11, endV1).endVertex();
				buffer.pos(pxc1 - offX, pyc1 - offY, zLevel).tex(endU11, endV2).endVertex();
				buffer.pos(pxc2, pyc2, zLevel).tex(endU12, endV1).endVertex();
				buffer.pos(pxc1, pyc1, zLevel).tex(endU12, endV2).endVertex();
			}

			if(i > 0) {
				buffer.pos(pxc2, pyc2, zLevel).tex(u, ropeV1).endVertex();
				buffer.pos(pxc1, pyc1, zLevel).tex(u, ropeV2).endVertex();

				u += (float) Math.sqrt((x-px)*(x-px) + (y-py)*(y-py)) / 16.0F;
			}

			if(i == pieces) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2, pyc2, zLevel).tex(endU21, endV1).endVertex();
				buffer.pos(pxc1, pyc1, zLevel).tex(endU21, endV2).endVertex();
				buffer.pos(pxc2 + offX, pyc2 + offY, zLevel).tex(endU22, endV1).endVertex();
				buffer.pos(pxc1 + offX, pyc1 + offY, zLevel).tex(endU22, endV2).endVertex();
			}

			px = x;
			py = y;

			pxc1 = xc1;
			pyc1 = yc1;
			pxc2 = xc2;
			pyc2 = yc2;
		}

		tessellator.draw();
	}
}
