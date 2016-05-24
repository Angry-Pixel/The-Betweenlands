package thebetweenlands.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
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

    //FIXME
    @Override
    public void render(VertexBuffer renderer, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX1, posY1, posZ1);
        GlStateManager.scale(width / 16F, height / 16F, 1);
        float depth = 0.0625F;
        Tessellator tessellator = Tessellator.getInstance();

        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.normal(0.0F, 0.0F, 1.0F);
        renderer.pos(0.0D, 0.0D, 0.0D).tex(u1, v2).endVertex();
        renderer.pos(1.0D, 0.0D, 0.0D).tex(u2, v2).endVertex();
        renderer.pos(1.0D, 1.0D, 0.0D).tex(u2, v1).endVertex();
        renderer.pos(0.0D, 1.0D, 0.0D).tex(u1, v1).endVertex();
        tessellator.draw();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.normal(0.0F, 0.0F, -1.0F);
        renderer.pos(0.0D, 1.0D, (0.0F - depth)).tex(u1, v2).endVertex();
        renderer.pos(1.0D, 1.0D, (0.0F - depth)).tex(u2, v2).endVertex();
        renderer.pos(1.0D, 0.0D, (0.0F - depth)).tex(u2, v1).endVertex();
        renderer.pos(0.0D, 0.0D, (0.0F - depth)).tex(u1, v1).endVertex();
        tessellator.draw();
        float f5 = 0.5F * (u1 - u2) / width;
        float f6 = 0.5F * (v2 - v1) / height;
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.normal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;

        for (k = 0; k < width; k++) {
            f7 = (float) k / (float) width;
            f8 = u1 + (u2 - u1) * f7 - f5;
            renderer.pos(f7, 0.0D, (0.0F - depth)).tex(f8, v2).endVertex();
            renderer.pos(f7, 0.0D, 0.0D).tex(f8, v2).endVertex();
            renderer.pos(f7, 1.0D, 0.0D).tex(f8, v1).endVertex();
            renderer.pos(f7, 1.0D, (0.0F - depth)).tex(f8, v1).endVertex();
        }

        tessellator.draw();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.normal(1.0F, 0.0F, 0.0F);
        float f9;

        for (k = 0; k < width; k++) {
            f7 = (float) k / (float) width;
            f8 = u1 + (u2 - u1) * f7 - f5;
            f9 = f7 + 1.0F / width;
            renderer.pos(f9, 1.0D, (0.0F - depth)).tex(f8, v1).endVertex();
            renderer.pos(f9, 1.0D, 0.0D).tex(f8, v1).endVertex();
            renderer.pos(f9, 0.0D, 0.0D).tex(f8, v2).endVertex();
            renderer.pos(f9, 0.0D, (0.0F - depth)).tex(f8, v2).endVertex();
        }

        tessellator.draw();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.normal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < height; k++) {
            f7 = (float) k / (float) height;
            f8 = v2 + (v1 - v2) * f7 - f6;
            f9 = f7 + 1.0F / height;
            renderer.pos(0.0D, f9, 0.0D).tex(u1, f8).endVertex();
            renderer.pos(1.0D, f9, 0.0D).tex(u2, f8).endVertex();
            renderer.pos(1.0D, f9, (0.0F - depth)).tex(u2, f8).endVertex();
            renderer.pos(0.0D, f9, (0.0F - depth)).tex(u1, f8).endVertex();
        }

        tessellator.draw();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.normal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < height; k++) {
            f7 = (float) k / (float) height;
            f8 = v2 + (v1 - v2) * f7 - f6;
            renderer.pos(1.0D, f7, 0.0D).tex(u2, f8).endVertex();
            renderer.pos(0.0D, f7, 0.0D).tex(u2, f8).endVertex();
            renderer.pos(0.0D, f7, (0.0F - depth)).tex(u1, f8).endVertex();
            renderer.pos(1.0D, f7, (0.0F - depth)).tex(u2, f8).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }
}