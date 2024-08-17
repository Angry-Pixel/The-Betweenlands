package thebetweenlands.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import thebetweenlands.common.TheBetweenlands;

public class Firefly {

	public static final ResourceLocation FIREFLY_TEXTURE = TheBetweenlands.prefix("textures/gui/menu/firefly.png");

	private float updateCounter = 0;
	private float prevPosX;
	private float prevPosY;
	private float posX;
	private float posY;
	private float motionY;
	private final float motionX;

	public Firefly(float x, float y, float mX, float mY) {
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.motionX = mX;
		this.motionY = mY;
	}

	public void tick() {
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

	public void render(GuiGraphics graphics, float partialTicks) {
		float interpX = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
		float interpY = this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
		float interpUpdateCounter = (this.updateCounter + partialTicks) / 15.0F;

		graphics.pose().pushPose();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		graphics.pose().translate(interpX + (float) (Math.sin(interpUpdateCounter) * 500) * 0.01F, interpY, 0);
		graphics.pose().scale((0.1F + (float) (Math.sin(interpUpdateCounter) * Math.sin(interpUpdateCounter)) / 4) * 0.1F, (0.1F + (float) (Math.sin(interpUpdateCounter) * Math.sin(interpUpdateCounter)) / 4) * 0.1F, 1);
		this.drawFirefly(graphics, 0xFFEC810E);
		this.drawFirefly(graphics, 0xFFEC810E);
		RenderSystem.depthMask(true);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		graphics.pose().popPose();
	}

	public void drawFirefly(GuiGraphics graphics, int color) {
		float a = (float) (color >> 24 & 0xff) / 255F;
		float r = (float) (color >> 16 & 0xff) / 255F;
		float g = (float) (color >> 8 & 0xff) / 255F;
		float b = (float) (color & 0xff) / 255F;

		graphics.setColor(r, g, b, a);
		RenderSystem.setShaderTexture(0, FIREFLY_TEXTURE);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Matrix4f matrix4f = graphics.pose().last().pose();
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		builder.addVertex(matrix4f, 0.0F, 250.0F, 0.0F).setUv(0.0F, 250.0F * 0.00390625F);
		builder.addVertex(matrix4f, 250.0F, 250.0F, 0.0F).setUv(250.0F * 0.00390625F, 250.0F * 0.00390625F);
		builder.addVertex(matrix4f, 250.0F, 0.0F, 0.0F).setUv(250.0F * 0.00390625F, 0.0F);
		builder.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F);
		BufferUploader.drawWithShader(builder.buildOrThrow());
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
