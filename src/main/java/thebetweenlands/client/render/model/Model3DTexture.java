package thebetweenlands.client.render.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Model3DTexture extends ModelBox {
	private int textureOffsetX;
	private int textureOffsetY;

	private int width;
	private int height;

	private float u1;
	private float v1;
	private float u2;
	private float v2;

	public Model3DTexture(ModelRenderer model, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int width, int height) {
		super(model, 0, 0, posX, posY, posZ, 0, 0, 0, 0);
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
		this.width = width;
		this.height = height;
		u1 = textureOffsetX / model.textureWidth;
		v1 = textureOffsetY / model.textureHeight;
		u2 = (textureOffsetX + width) / model.textureWidth;
		v2 = (textureOffsetY + height) / model.textureHeight;
	}

	public Model3DTexture(ModelRenderer model, int textureOffsetX, int textureOffsetY, int width, int height) {
		this(model, textureOffsetX, textureOffsetY, 0, 0, 0, width, height);
	}

	@Override
	public void render(BufferBuilder renderer, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX1, posY1, posZ1);
		GlStateManager.scale(width / 16F, height / 16F, 1);
		float depth = 0.0625F;
		Tessellator tessellator = Tessellator.getInstance();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		renderer.pos(0.0D, 0.0D, 0.0D).tex(u1, v2).normal(0.0F, 0.0F, 1.0F).endVertex();
		renderer.pos(1.0D, 0.0D, 0.0D).tex(u2, v2).normal(0.0F, 0.0F, 1.0F).endVertex();
		renderer.pos(1.0D, 1.0D, 0.0D).tex(u2, v1).normal(0.0F, 0.0F, 1.0F).endVertex();
		renderer.pos(0.0D, 1.0D, 0.0D).tex(u1, v1).normal(0.0F, 0.0F, 1.0F).endVertex();
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		renderer.pos(0.0D, 1.0D, (0.0F - depth)).tex(u1, v1).normal(0.0F, 0.0F, -1.0F).endVertex();
		renderer.pos(1.0D, 1.0D, (0.0F - depth)).tex(u2, v1).normal(0.0F, 0.0F, -1.0F).endVertex();
		renderer.pos(1.0D, 0.0D, (0.0F - depth)).tex(u2, v2).normal(0.0F, 0.0F, -1.0F).endVertex();
		renderer.pos(0.0D, 0.0D, (0.0F - depth)).tex(u1, v2).normal(0.0F, 0.0F, -1.0F).endVertex();
		tessellator.draw();

		float halfU = 0.5F * (u1 - u2) / width;
		float halfV = 0.5F * (v2 - v1) / height;

		int pixel;
		float pixelPos;
		float mirroredPixelPos;
		float pixelUV;

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		for (pixel = 0; pixel < width; pixel++) {
			pixelPos = (float) pixel / (float) width;
			pixelUV = u1 + (u2 - u1) * pixelPos - halfU;
			renderer.pos(pixelPos, 0.0D, (0.0F - depth)).tex(pixelUV, v2).normal(-1.0F, 0.0F, 0.0F).endVertex();
			renderer.pos(pixelPos, 0.0D, 0.0D).tex(pixelUV, v2).normal(-1.0F, 0.0F, 0.0F).endVertex();
			renderer.pos(pixelPos, 1.0D, 0.0D).tex(pixelUV, v1).normal(-1.0F, 0.0F, 0.0F).endVertex();
			renderer.pos(pixelPos, 1.0D, (0.0F - depth)).tex(pixelUV, v1).normal(-1.0F, 0.0F, 0.0F).endVertex();
		}
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		for (pixel = 0; pixel < width; pixel++) {
			pixelPos = (float) pixel / (float) width;
			pixelUV = u1 + (u2 - u1) * pixelPos - halfU;
			mirroredPixelPos = pixelPos + 1.0F / width;
			renderer.pos(mirroredPixelPos, 1.0D, (0.0F - depth)).tex(pixelUV, v1).normal(1.0F, 0.0F, 0.0F).endVertex();
			renderer.pos(mirroredPixelPos, 1.0D, 0.0D).tex(pixelUV, v1).normal(1.0F, 0.0F, 0.0F).endVertex();
			renderer.pos(mirroredPixelPos, 0.0D, 0.0D).tex(pixelUV, v2).normal(1.0F, 0.0F, 0.0F).endVertex();
			renderer.pos(mirroredPixelPos, 0.0D, (0.0F - depth)).tex(pixelUV, v2).normal(1.0F, 0.0F, 0.0F).endVertex();
		}
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		for (pixel = 0; pixel < height; pixel++) {
			pixelPos = (float) pixel / (float) height;
			pixelUV = v2 + (v1 - v2) * pixelPos - halfV;
			renderer.pos(1.0D, pixelPos, 0.0D).tex(u2, pixelUV).normal(0.0F, -1.0F, 0.0F).endVertex();
			renderer.pos(0.0D, pixelPos, 0.0D).tex(u1, pixelUV).normal(0.0F, -1.0F, 0.0F).endVertex();
			renderer.pos(0.0D, pixelPos, (0.0F - depth)).tex(u1, pixelUV).normal(0.0F, -1.0F, 0.0F).endVertex();
			renderer.pos(1.0D, pixelPos, (0.0F - depth)).tex(u2, pixelUV).normal(0.0F, -1.0F, 0.0F).endVertex();
		}
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		for (pixel = 0; pixel < height; pixel++) {
			pixelPos = (float) pixel / (float) height;
			pixelUV = v2 + (v1 - v2) * pixelPos - halfV;
			mirroredPixelPos = pixelPos + 1.0F / height;
			renderer.pos(0.0D, mirroredPixelPos, 0.0D).tex(u1, pixelUV).normal(0.0F, 1.0F, 0.0F).endVertex();
			renderer.pos(1.0D, mirroredPixelPos, 0.0D).tex(u2, pixelUV).normal(0.0F, 1.0F, 0.0F).endVertex();
			renderer.pos(1.0D, mirroredPixelPos, (0.0F - depth)).tex(u2, pixelUV).normal(0.0F, 1.0F, 0.0F).endVertex();
			renderer.pos(0.0D, mirroredPixelPos, (0.0F - depth)).tex(u1, pixelUV).normal(0.0F, 1.0F, 0.0F).endVertex();
		}
		tessellator.draw();

		GlStateManager.popMatrix();
	}
}