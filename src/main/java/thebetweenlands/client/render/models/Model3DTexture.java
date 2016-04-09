package thebetweenlands.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

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

    /*@Override
    public void render(VertexBuffer renderer, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX1, posY1, posZ1);
        GL11.glScalef(width / 16F, height / 16F, 1);
        float depth = 0.0625F;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, u1, v2);
        tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, u2, v2);
        tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, u2, v1);
        tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, u1, v1);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.addVertexWithUV(0.0D, 1.0D, (0.0F - depth), u1, v1);
        tessellator.addVertexWithUV(1.0D, 1.0D, (0.0F - depth), u2, v1);
        tessellator.addVertexWithUV(1.0D, 0.0D, (0.0F - depth), u2, v2);
        tessellator.addVertexWithUV(0.0D, 0.0D, (0.0F - depth), u1, v2);
        tessellator.draw();
        float f5 = 0.5F * (u1 - u2) / width;
        float f6 = 0.5F * (v2 - v1) / height;
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;

        for (k = 0; k < width; k++) {
            f7 = (float) k / (float) width;
            f8 = u1 + (u2 - u1) * f7 - f5;
            tessellator.addVertexWithUV(f7, 0.0D, (0.0F - depth), f8, v2);
            tessellator.addVertexWithUV(f7, 0.0D, 0.0D, f8, v2);
            tessellator.addVertexWithUV(f7, 1.0D, 0.0D, f8, v1);
            tessellator.addVertexWithUV(f7, 1.0D, (0.0F - depth), f8, v1);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        float f9;

        for (k = 0; k < width; k++) {
            f7 = (float) k / (float) width;
            f8 = u1 + (u2 - u1) * f7 - f5;
            f9 = f7 + 1.0F / width;
            tessellator.addVertexWithUV(f9, 1.0D, (0.0F - depth), f8, v1);
            tessellator.addVertexWithUV(f9, 1.0D, 0.0D, f8, v1);
            tessellator.addVertexWithUV(f9, 0.0D, 0.0D, f8, v2);
            tessellator.addVertexWithUV(f9, 0.0D, (0.0F - depth), f8, v2);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < height; k++) {
            f7 = (float) k / (float) height;
            f8 = v2 + (v1 - v2) * f7 - f6;
            f9 = f7 + 1.0F / height;
            tessellator.addVertexWithUV(0.0D, f9, 0.0D, u1, f8);
            tessellator.addVertexWithUV(1.0D, f9, 0.0D, u2, f8);
            tessellator.addVertexWithUV(1.0D, f9, (0.0F - depth), u2, f8);
            tessellator.addVertexWithUV(0.0D, f9, (0.0F - depth), u1, f8);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < height; k++) {
            f7 = (float) k / (float) height;
            f8 = v2 + (v1 - v2) * f7 - f6;
            tessellator.addVertexWithUV(1.0D, f7, 0.0D, u2, f8);
            tessellator.addVertexWithUV(0.0D, f7, 0.0D, u1, f8);
            tessellator.addVertexWithUV(0.0D, f7, (0.0F - depth), u1, f8);
            tessellator.addVertexWithUV(1.0D, f7, (0.0F - depth), u2, f8);
        }

        tessellator.draw();
        GL11.glPopMatrix();
    }*/
}