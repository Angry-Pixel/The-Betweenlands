package thebetweenlands.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class GuiFirefly extends Gui {
	public static final ResourceLocation FIREFLY_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/firefly.png");

	private float updateCounter = 0;
	private float prevPosX = 0;
	private float prevPosY = 0;
	private float posX = 0;
	private float posY = 0;
	private float motionY = 0;
	private float motionX = 0;

	public GuiFirefly(float x, float y, float mX, float mY) {
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.motionX = mX;
		this.motionY = mY;
	}

	public void update() {
		this.updateCounter++;

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;

		this.posX -= 0.4F;

		this.posY += this.motionY;
		this.posX += this.motionX;

		if (this.motionY > 0) {
			this.motionY -= 0.001F;
		} else {
			this.motionY += 0.001F;
		}
	}

	public float getPosX() {
		return this.posX;
	}

	public float getPosY() {
		return this.posY;
	}

	public void drawFireFly(Minecraft minecraft, float partialTicks) {
		float interpX = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
		float interpY = this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
		float interpUpdateCounter = (this.updateCounter + partialTicks) / 15.0F;

		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		minecraft.getTextureManager().bindTexture(FIREFLY_TEXTURE);
		GlStateManager.translate(interpX + (float) (Math.sin(interpUpdateCounter) * 500) * 0.01F, interpY, 0);
		GlStateManager.scale((0.1F + (float) (Math.sin(interpUpdateCounter) * Math.sin(interpUpdateCounter)) / 4) * 0.1F, (0.1F + (float) (Math.sin(interpUpdateCounter) * Math.sin(interpUpdateCounter)) / 4) * 0.1F, 1);
		this.drawTexturedModalRectWithColor(0, 0, 0, 0, 250, 250, 0xFFEC810E);
		this.drawTexturedModalRectWithColor(0, 0, 0, 0, 250, 250, 0xFFEC810E);
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
	}

	public void drawTexturedModalRectWithColor(int x, int y, int textureX, int textureY, int width, int height, int color) {
		float a = (float) (color >> 24 & 0xff) / 255F;
		float r = (float) (color >> 16 & 0xff) / 255F;
		float g = (float) (color >> 8 & 0xff) / 255F;
		float b = (float) (color & 0xff) / 255F;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).color(r, g, b, a).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).color(r, g, b, a).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).color(r, g, b, a).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).color(r, g, b, a).endVertex();
		tessellator.draw();
	}
}
