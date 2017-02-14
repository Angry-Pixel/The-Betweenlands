package thebetweenlands.client.render.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import thebetweenlands.util.MathUtils;

public class ModelBipedLimb extends ModelRenderer {
    private float flexionAngle;

    private List<ModelRenderer> forearmChildModels;

    private int textureOffsetXLimb;

    private int textureOffsetYLimb;

    public ModelBipedLimb(ModelBase model, int textureOffsetX, int textureOffsetY) {
        super(model, textureOffsetX, textureOffsetY);
        textureOffsetXLimb = textureOffsetX;
        textureOffsetYLimb = textureOffsetY;
    }

    public void setFlexionAngle(float flexionAngle) {
        this.flexionAngle = flexionAngle;
    }

    public void addForearmChild(ModelRenderer forearmChildModel) {
        if (forearmChildModels == null) {
            forearmChildModels = new ArrayList<ModelRenderer>();
        }
        forearmChildModels.add(forearmChildModel);
    }

    @Override
    public void render(float scale) {
        float size = 4 * scale;
        float ratio = 3 / 2F;
        final float flexMin = -105;
        float flexionAngle = this.flexionAngle > 0 ? 0 : this.flexionAngle < flexMin ? flexMin : this.flexionAngle;
        float theta = -flexionAngle * MathUtils.DEG_TO_RAD;
        float x1 = -size / 2, y1 = 0;
        float x2 = size / 2, y2 = 0;
        float x3 = -size / 2, y3 = size * ratio;
        float x4 = size / 2, y4 = size * ratio;
        float c = MathHelper.cos(theta), s = MathHelper.sin(theta);
        float x1p = x1 * c - y1 * s;
        float y1p = x1 * s + y1 * c;
        float x2p = x2 * c - y2 * s;
        float y2p = x2 * s + y2 * c;
        float x3p = x3 * c - y3 * s;
        float y3p = x3 * s + y3 * c;
        float x4p = x4 * c - y4 * s;
        float y4p = x4 * s + y4 * c;
        float m = (float) Math.tan(theta - Math.PI / 2);
        float x1i = x1;
        float y1i = m * x1i + (y1p - x1p * m);
        float x2i = x2;
        float y2i = -y1i;
        float len1 = MathHelper.sqrt_float((x1i - x3p) * (x1i - x3p) + (y1i - y3p) * (y1i - y3p));
        float len2 = MathHelper.sqrt_float((x2i - x4p) * (x2i - x4p) + (y2i - y4p) * (y2i - y4p));
        // TODO: correct triangle winding on ModelBipedLimb
        GL11.glPushAttrib(GL11.GL_CULL_FACE);
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
        if (rotateAngleZ != 0) {
            GlStateManager.rotate(rotateAngleZ * MathUtils.RAD_TO_DEG, 0, 0, 1);
        }
        if (rotateAngleY != 0) {
            GlStateManager.rotate(rotateAngleY * MathUtils.RAD_TO_DEG, 0, 1, 0);
        }
        if (rotateAngleX != 0) {
            GlStateManager.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, 1, 0, 0);
        }
        GlStateManager.translate(offsetX * scale, offsetY * scale, offsetZ * scale);
        GlStateManager.glBegin(GL11.GL_QUADS);
        // top
        GlStateManager.glNormal3f(0, -1, 0);
        setUV(4, 0);
        GlStateManager.glVertex3f(0, 0, 0);
        setUV(8, 0);
        GlStateManager.glVertex3f(0, 0, size);
        setUV(8, 4);
        GlStateManager.glVertex3f(size, 0, size);
        setUV(4, 4);
        GlStateManager.glVertex3f(size, 0, 0);
        GlStateManager.glEnd();
        GlStateManager.glBegin(GL11.GL_QUADS);
        // front
        GlStateManager.glNormal3f(0, 0, -1);
        setUV(8, 4);
        GlStateManager.glVertex3f(0, 0, 0);
        setUV(4, 4);
        GlStateManager.glVertex3f(size, 0, 0);
        setUV(4, 10 + y1i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y1i, 0);
        setUV(8, 10 + y1i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y1i, 0);
        // back
        GlStateManager.glNormal3f(0, 0, 1);
        setUV(12, 4);
        GlStateManager.glVertex3f(0, 0, size);
        setUV(16, 4);
        GlStateManager.glVertex3f(size, 0, size);
        setUV(16, 10 - y1i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio - y1i, size);
        setUV(12, 10 - y1i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio - y1i, size);
        GlStateManager.glEnd();
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        GlStateManager.glNormal3f(-1, 0, 0);
        // middle
        setUV(8, 4);
        GlStateManager.glVertex3f(0, 0, 0);
        setUV((x1p + size / 2) * 4 / size + 8, 10 + y1p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y1p, x1p + size / 2);
        setUV(12, 4);
        GlStateManager.glVertex3f(0, 0, size);
        // inner
        setUV(8, 4);
        GlStateManager.glVertex3f(0, 0, 0);
        setUV((x1p + size / 2) * 4 / size + 8, 10 + y1p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y1p, x1p + size / 2);
        setUV((x1i + size / 2) * 4 / size + 8, 10 + y1i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y1i, x1i + size / 2);
        // outer
        setUV(12, 4);
        GlStateManager.glVertex3f(0, 0, size);
        setUV((x1p + size / 2) * 4 / size + 8, 10 + y1p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y1p, x1p + size / 2);
        setUV((x2p + size / 2) * 4 / size + 8, 10 + y2p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y2p, x2p + size / 2);
        // elbow
        setUV(12, 4);
        GlStateManager.glVertex3f(0, 0, size);
        setUV((x2i + size / 2) * 4 / size + 8, 10 + y2i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y2i, x2i + size / 2);
        setUV((x2p + size / 2) * 4 / size + 8, 10 + y2p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(0, size * ratio + y2p, x2p + size / 2);
        // other side
        GlStateManager.glNormal3f(1, 0, 0);
        // middle
        setUV(4, 4);
        GlStateManager.glVertex3f(size, 0, 0);
        setUV(4 - (x1p + size / 2) * 4 / size, 10 + y1p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y1p, x1p + size / 2);
        setUV(0, 4);
        GlStateManager.glVertex3f(size, 0, size);
        // inner
        setUV(4, 4);
        GlStateManager.glVertex3f(size, 0, 0);
        setUV(4 - (x1p + size / 2) * 4 / size, 10 + y1p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y1p, x1p + size / 2);
        setUV(4 - (x1i + size / 2) * 4 / size, 10 + y1i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y1i, x1i + size / 2);
        // outer
        setUV(0, 4);
        GlStateManager.glVertex3f(size, 0, size);
        setUV(4 - (x1p + size / 2) * 4 / size, 10 + y1p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y1p, x1p + size / 2);
        setUV(4 - (x2p + size / 2) * 4 / size, 10 + y2p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y2p, x2p + size / 2);
        // elbow
        setUV(0, 4);
        GlStateManager.glVertex3f(size, 0, size);
        setUV(4 - (x2i + size / 2) * 4 / size, 10 + y2i * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y2i, x2i + size / 2);
        setUV(4 - (x2p + size / 2) * 4 / size, 10 + y2p * (size / scale / ratio) * 6);
        GlStateManager.glVertex3f(size, size * ratio + y2p, x2p + size / 2);
        GlStateManager.glEnd();
        GlStateManager.translate(-offsetX * scale, -offsetY * scale, -offsetZ * scale);
        renderChildren(childModels, scale);
        GlStateManager.translate(0, size * ratio + offsetY * scale, 0);
        GlStateManager.rotate(flexionAngle, 1, 0, 0);
        GlStateManager.translate(offsetX * scale, 0, offsetZ * scale);
        // front
        GlStateManager.translate(-x1i, -y1i, 0);
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glNormal3f(0, 0, -1);
        setUV(8, 16 - len1 / scale);
        GlStateManager.glVertex3f(-size / 2, 0, 0);
        setUV(8, 16);
        GlStateManager.glVertex3f(-size / 2, size * ratio + y1i, 0);
        setUV(4, 16);
        GlStateManager.glVertex3f(size / 2, size * ratio + y1i, 0);
        setUV(4, 16 - len1 / scale);
        GlStateManager.glVertex3f(size / 2, 0, 0);
        GlStateManager.glEnd();
        GlStateManager.translate(x1i, y1i, 0);
        // back
        GlStateManager.translate(-x2i, -y2i, 0);
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glNormal3f(0, 0, 1);
        setUV(16, 16 - len2 / scale);
        GlStateManager.glVertex3f(size + size / 2, 0, size);
        setUV(16, 16);
        GlStateManager.glVertex3f(size + size / 2, size * ratio + y2i, size);
        setUV(12, 16);
        GlStateManager.glVertex3f(size / 2, size * ratio + y2i, size);
        setUV(12, 16 - len2 / scale);
        GlStateManager.glVertex3f(size / 2, 0, size);
        GlStateManager.glEnd();
        GlStateManager.translate(x2i, y2i, 0);
        GlStateManager.glBegin(GL11.GL_QUADS);
        // right side
        GlStateManager.glNormal3f(-1, 0, 0);
        setUV(12, 10);
        GlStateManager.glVertex3f(0, 0, size);
        setUV(12, 16);
        GlStateManager.glVertex3f(0, size * ratio, size);
        setUV(8, 16);
        GlStateManager.glVertex3f(0, size * ratio, 0);
        setUV(8, 10);
        GlStateManager.glVertex3f(0, 0, 0);
        // left side
        GlStateManager.glNormal3f(1, 0, 0);
        setUV(4, 10);
        GlStateManager.glVertex3f(size, 0, 0);
        setUV(4, 16);
        GlStateManager.glVertex3f(size, size * ratio, 0);
        setUV(0, 16);
        GlStateManager.glVertex3f(size, size * ratio, size);
        setUV(0, 10);
        GlStateManager.glVertex3f(size, 0, size);
        // hand
        GlStateManager.glNormal3f(0, 1, 0);
        setUV(8, 0);
        GlStateManager.glVertex3f(0, size * ratio, 0);
        setUV(12, 0);
        GlStateManager.glVertex3f(0, size * ratio, size);
        setUV(12, 4);
        GlStateManager.glVertex3f(size, size * ratio, size);
        setUV(8, 4);
        GlStateManager.glVertex3f(size, size * ratio, 0);
        GlStateManager.glEnd();
        renderChildren(forearmChildModels, scale);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void setUV(float u, float v) {
        GlStateManager.glTexCoord2f((textureOffsetXLimb + u) / textureWidth, (textureOffsetYLimb + v) / textureHeight);
    }

    private void renderChildren(List<ModelRenderer> children, float scale) {
        if (children != null) {
            for (ModelRenderer child : children) {
                child.render(scale);
            }
        }
    }
}