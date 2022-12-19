package thebetweenlands.client.render.model.entity.rowboat;

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

    private int width;

    private int depth;

    private float expand;

    public ModelBipedLimb(ModelBase model, int textureOffsetX, int textureOffsetY, int width, int depth, float expand) {
        super(model, textureOffsetX, textureOffsetY);
        textureOffsetXLimb = textureOffsetX;
        textureOffsetYLimb = textureOffsetY;
        this.width = width;
        this.depth = depth;
        this.expand = expand;
    }

    public void setFlexionAngle(float flexionAngle) {
        this.flexionAngle = flexionAngle;
    }

    public void addForearmChild(ModelRenderer forearmChildModel) {
        if (forearmChildModels == null) {
            forearmChildModels = new ArrayList<>();
        }
        forearmChildModels.add(forearmChildModel);
    }

    @Override
    public void render(float scale) {
        // TODO: correct triangle winding and use VertexBuffer
        float ex = expand * scale;
        float ratio = 3 / 2F;
        float sizeZ = depth * scale;
        float sizeX = width * scale;
        float sizeY = sizeZ * ratio;
        final float flexMin = -95;
        float angle = flexionAngle > 0 ? 0 : flexionAngle < flexMin ? flexMin : flexionAngle;
        float theta = -angle * MathUtils.DEG_TO_RAD;
        float x1 = -sizeZ / 2 - ex, y1 = 0;
        float x2 = sizeZ / 2 + ex, y2 = 0;
        float x3 = -sizeZ / 2 - ex, y3 = sizeY + ex;
        float x4 = sizeZ / 2 + ex, y4 = sizeY + ex;
        float c = MathHelper.cos(theta), s = MathHelper.sin(theta);
        float x1p = x1 * c - y1 * s;
        float y1p = x1 * s + y1 * c;
        float x2p = x2 * c - y2 * s;
        float y2p = x2 * s + y2 * c;
        float x3p = x3 * c - y3 * s;
        float y3p = x3 * s + y3 * c;
        float x4p = x4 * c - y4 * s;
        float y4p = x4 * s + y4 * c;
        float slope = (float) Math.tan(theta - Math.PI / 2);
        float x1i = x1;
        float y1i = slope * x1i + (y1p - x1p * slope);
        float x2o = x2;
        float y2o = -y1i;
        float len1 = MathHelper.sqrt((x1i - x3p) * (x1i - x3p) + (y1i - y3p) * (y1i - y3p));
        float len2 = MathHelper.sqrt((x2o - x4p) * (x2o - x4p) + (y2o - y4p) * (y2o - y4p));
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
        float h = depth * 3;
        float m = h / 2;
        float dw = depth + width;
        float dwd = dw + depth;
        float dwdw = dwd + width;
        float dm = depth + m;
        float dmm = depth + h;
        float sizeZEB = sizeZ - ex / 2;
        float sizeZES = sizeZ + ex * 2;
        float sizeYES = sizeY + ex;
        GlStateManager.glBegin(GL11.GL_QUADS);
        // top
        GlStateManager.glNormal3f(0, -1, 0);
        setUV(depth, depth);
        GlStateManager.glVertex3f(-ex, -ex, -ex);
        setUV(depth, 0);
        GlStateManager.glVertex3f(-ex, -ex, sizeZ + ex);
        setUV(dw, 0);
        GlStateManager.glVertex3f(sizeX + ex, -ex, sizeZ + ex);
        setUV(dw, depth);
        GlStateManager.glVertex3f(sizeX + ex, -ex, -ex);
        GlStateManager.glEnd();
        GlStateManager.glBegin(GL11.GL_QUADS);
        // front
        GlStateManager.glNormal3f(0, 0, -1);
        setUV(depth, depth);
        GlStateManager.glVertex3f(-ex, -ex, -ex);
        setUV(dw, depth);
        GlStateManager.glVertex3f(sizeX + ex, -ex, -ex);
        setUV(dw, dm + y1i / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y1i, -ex);
        setUV(depth, dm + y1i / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y1i, -ex);
        // back
        GlStateManager.glNormal3f(0, 0, 1);
        setUV(dwdw, depth);
        GlStateManager.glVertex3f(-ex, -ex, sizeZ + ex);
        setUV(dwd, depth);
        GlStateManager.glVertex3f(sizeX + ex, -ex, sizeZ + ex);
        setUV(dwd, dm - y1i / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio - y1i, sizeZ + ex);
        setUV(dwdw, dm - y1i / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio - y1i, sizeZ + ex);
        GlStateManager.glEnd();
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        float rightU = 0, rightV = depth;
        GlStateManager.glNormal3f(-1, 0, 0);
        // middle
        setUV(rightU + depth, rightV);
        GlStateManager.glVertex3f(-ex, -ex, -ex);
        setUV(rightU + depth - (x1p / sizeZES * depth + depth / 2), rightV + m + y1p / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y1p, x1p + sizeZ / 2);
        setUV(rightU, rightV);
        GlStateManager.glVertex3f(-ex, -ex, sizeZ + ex);
        // inner
        setUV(rightU + depth, rightV);
        GlStateManager.glVertex3f(-ex, -ex, -ex);
        setUV(rightU + depth - (x1p / sizeZES * depth + depth / 2), rightV + m + y1p / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y1p, x1p + sizeZ / 2);
        setUV(rightU + depth - (x1i / sizeZES * depth + depth / 2), rightV + m + y1i / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y1i, x1i + sizeZ / 2);
        // outer
        setUV(rightU, rightV);
        GlStateManager.glVertex3f(-ex, -ex, sizeZ + ex);
        setUV(rightU + depth - (x1p + sizeZES / 2) * depth / sizeZES, rightV + m + y1p / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y1p, x1p + sizeZ / 2);
        setUV(rightU + depth - (x2p + sizeZES / 2) * depth / sizeZES, rightV + m + y2p / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y2p, x2p + sizeZ / 2);
        // elbow
        setUV(rightU, rightV);
        GlStateManager.glVertex3f(-ex, -ex, sizeZ + ex);
        setUV(rightU + depth - (x2o + sizeZES / 2) * depth / sizeZES, rightV + m + y2o / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y2o, x2o + sizeZ / 2);
        setUV(rightU + depth - (x2p + sizeZES / 2) * depth / sizeZES, rightV + m + y2p / sizeYES * m);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + y2p, x2p + sizeZ / 2);
        // other side
        float leftU = dw, leftV = depth;
        GlStateManager.glNormal3f(1, 0, 0);
        // middle
        setUV(leftU, leftV);
        GlStateManager.glVertex3f(sizeX + ex, -ex, -ex);
        setUV(leftU + x1p / sizeZES * depth + depth / 2, leftV + m + y1p / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y1p, x1p + sizeZ / 2);
        setUV(leftU + depth, leftV);
        GlStateManager.glVertex3f(sizeX + ex, -ex, sizeZ + ex);
        // inner
        setUV(leftU, leftV);
        GlStateManager.glVertex3f(sizeX + ex, -ex, -ex);
        setUV(leftU + x1p / sizeZES * depth + depth / 2, leftV + m + y1p / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y1p, x1p + sizeZ / 2);
        setUV(leftU + x1i / sizeZES * depth + depth / 2, leftV + m + y1i / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y1i, x1i + sizeZ / 2);
        // outer
        setUV(leftU + depth, leftV);
        GlStateManager.glVertex3f(sizeX + ex, -ex, sizeZ + ex);
        setUV(leftU + x1p / sizeZES * depth + depth / 2, leftV + m + y1p / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y1p, x1p + sizeZ / 2);
        setUV(leftU + x2p / sizeZES * depth + depth / 2, leftV + m + y2p / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y2p, x2p + sizeZ / 2);
        // elbow
        setUV(leftU + depth, leftV);
        GlStateManager.glVertex3f(sizeX + ex, -ex, sizeZ + ex);
        setUV(leftU + x2o / sizeZES * depth + depth / 2, leftV + m + y2o / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y2o, x2o + sizeZ / 2);
        setUV(leftU + x2p / sizeZES * depth + depth / 2, leftV + m + y2p / sizeYES * m);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + y2p, x2p + sizeZ / 2);
        GlStateManager.glEnd();
        GlStateManager.translate(-offsetX * scale, -offsetY * scale, -offsetZ * scale);
        renderChildren(childModels, scale);
        GlStateManager.translate(0, sizeZ * ratio + offsetY * scale, 0);
        GlStateManager.rotate(angle, 1, 0, 0);
        GlStateManager.translate(offsetX * scale, 0, offsetZ * scale);
        // front
        GlStateManager.translate(sizeX / 2, -y1i, 0);
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glNormal3f(0, 0, -1);
        setUV(depth, dmm - len1 / (m * scale + ex) * m);
        GlStateManager.glVertex3f(-sizeX / 2 - ex, 0, -ex);
        setUV(depth, dmm);
        GlStateManager.glVertex3f(-sizeX / 2 - ex, sizeZ * ratio + y1i + ex, -ex);
        setUV(dw, dmm);
        GlStateManager.glVertex3f(sizeX / 2 + ex, sizeZ * ratio + y1i + ex, -ex);
        setUV(dw, dmm - len1 / (m * scale + ex) * m);
        GlStateManager.glVertex3f(sizeX / 2 + ex, 0, -ex);
        GlStateManager.glEnd();
        GlStateManager.translate(-sizeX / 2, y1i, 0);
        // back
        GlStateManager.translate(-sizeX / 2, -y2o, 0);
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glNormal3f(0, 0, 1);
        setUV(dwd, dmm - len2 / (h * scale + ex * 2) * h);
        GlStateManager.glVertex3f(sizeX + sizeX / 2 + ex, 0, sizeZ + ex);
        setUV(dwd, dmm);
        GlStateManager.glVertex3f(sizeX + sizeX / 2 + ex, sizeZ * ratio + y2o + ex, sizeZ + ex);
        setUV(dwdw, dmm);
        GlStateManager.glVertex3f(sizeX / 2 - ex, sizeZ * ratio + y2o + ex, sizeZ + ex);
        setUV(dwdw, dmm - len2 / (h * scale + ex * 2) * h);
        GlStateManager.glVertex3f(sizeX / 2 - ex, 0, sizeZ + ex);
        GlStateManager.glEnd();
        GlStateManager.translate(sizeX / 2, y2o, 0);
        GlStateManager.glBegin(GL11.GL_QUADS);
        // right side
        GlStateManager.glNormal3f(-1, 0, 0);
        setUV(rightU, rightV + m);
        GlStateManager.glVertex3f(-ex, 0, sizeZ + ex);
        setUV(rightU, rightV + h);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + ex, sizeZ + ex);
        setUV(rightU + depth, rightV + h);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + ex, -ex);
        setUV(rightU + depth, rightV + m);
        GlStateManager.glVertex3f(-ex, 0, -ex);
        // left side
        GlStateManager.glNormal3f(1, 0, 0);
        setUV(leftU, leftV + m);
        GlStateManager.glVertex3f(sizeX + ex, 0, -ex);
        setUV(leftU, leftV + h);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + ex, -ex);
        setUV(leftU + depth, leftV + h);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + ex, sizeZ + ex);
        setUV(leftU + depth, leftV + m);
        GlStateManager.glVertex3f(sizeX + ex, 0, sizeZ + ex);
        // hand
        GlStateManager.glNormal3f(0, 1, 0);
        setUV(dw, depth);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + ex, -ex);
        setUV(dw, 0);
        GlStateManager.glVertex3f(-ex, sizeZ * ratio + ex, sizeZ + ex);
        setUV(dw + width, 0);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + ex, sizeZ + ex);
        setUV(dw + width, depth);
        GlStateManager.glVertex3f(sizeX + ex, sizeZ * ratio + ex, -ex);
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