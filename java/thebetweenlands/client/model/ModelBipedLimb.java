package thebetweenlands.client.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelDragonfly;
import thebetweenlands.utils.MathUtils;

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
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPushMatrix();
		GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
		if (rotateAngleZ != 0) {
			GL11.glRotatef(rotateAngleZ * MathUtils.RAD_TO_DEG, 0, 0, 1);
		}
		if (rotateAngleY != 0) {
			GL11.glRotatef(rotateAngleY * MathUtils.RAD_TO_DEG, 0, 1, 0);
		}
		if (rotateAngleX != 0) {
			GL11.glRotatef(rotateAngleX * MathUtils.RAD_TO_DEG, 1, 0, 0);
		}
		GL11.glTranslatef(offsetX * scale, offsetY * scale, offsetZ * scale);
		GL11.glBegin(GL11.GL_QUADS);
		// top
		GL11.glNormal3f(0, -1, 0);
		setUV(4, 0);
		GL11.glVertex3f(0, 0, 0);
		setUV(8, 0);
		GL11.glVertex3f(0, 0, size);
		setUV(8, 4);
		GL11.glVertex3f(size, 0, size);
		setUV(4, 4);
		GL11.glVertex3f(size, 0, 0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		// front
		GL11.glNormal3f(0, 0, -1);
		setUV(8, 4);
		GL11.glVertex3f(0, 0, 0);
		setUV(4, 4);
		GL11.glVertex3f(size, 0, 0);
		setUV(4, 10 + y1i * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y1i, 0);
		setUV(8, 10 + y1i * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y1i, 0);
		// back
		GL11.glNormal3f(0, 0, 1);
		setUV(12, 4);
		GL11.glVertex3f(0, 0, size);
		setUV(16, 4);
		GL11.glVertex3f(size, 0, size);
		setUV(16, 10 - y1i * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio - y1i, size);
		setUV(12, 10 - y1i * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio - y1i, size);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glNormal3f(-1, 0, 0);
		// middle
		setUV(8, 4);
		GL11.glVertex3f(0, 0, 0);
		setUV((x1p + size / 2) * 4 / size + 8, 10 + y1p * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y1p, x1p + size / 2);
		setUV(12, 4);
		GL11.glVertex3f(0, 0, size);
		// inner
		setUV(8, 4);
		GL11.glVertex3f(0, 0, 0);
		setUV((x1p + size / 2) * 4 / size + 8, 10 + y1p * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y1p, x1p + size / 2);
		setUV((x1i + size / 2) * 4 / size + 8, 10 + y1i * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y1i, x1i + size / 2);
		// outer
		setUV(12, 4);
		GL11.glVertex3f(0, 0, size);
		setUV((x1p + size / 2) * 4 / size + 8, 10 + y1p * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y1p, x1p + size / 2);
		setUV((x2p + size / 2) * 4 / size + 8, 10 + y2p * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y2p, x2p + size / 2);
		// elbow
		setUV(12, 4);
		GL11.glVertex3f(0, 0, size);
		setUV((x2i + size / 2) * 4 / size + 8, 10 + y2i * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y2i, x2i + size / 2);
		setUV((x2p + size / 2) * 4 / size + 8, 10 + y2p * (size / scale / ratio) * 6);
		GL11.glVertex3f(0, size * ratio + y2p, x2p + size / 2);
		// other side
		GL11.glNormal3f(1, 0, 0);
		// middle
		setUV(4, 4);
		GL11.glVertex3f(size, 0, 0);
		setUV(4 - (x1p + size / 2) * 4 / size, 10 + y1p * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y1p, x1p + size / 2);
		setUV(0, 4);
		GL11.glVertex3f(size, 0, size);
		// inner
		setUV(4, 4);
		GL11.glVertex3f(size, 0, 0);
		setUV(4 - (x1p + size / 2) * 4 / size, 10 + y1p * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y1p, x1p + size / 2);
		setUV(4 - (x1i + size / 2) * 4 / size, 10 + y1i * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y1i, x1i + size / 2);
		// outer
		setUV(0, 4);
		GL11.glVertex3f(size, 0, size);
		setUV(4 - (x1p + size / 2) * 4 / size, 10 + y1p * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y1p, x1p + size / 2);
		setUV(4 - (x2p + size / 2) * 4 / size, 10 + y2p * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y2p, x2p + size / 2);
		// elbow
		setUV(0, 4);
		GL11.glVertex3f(size, 0, size);
		setUV(4 - (x2i + size / 2) * 4 / size, 10 + y2i * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y2i, x2i + size / 2);
		setUV(4 - (x2p + size / 2) * 4 / size, 10 + y2p * (size / scale / ratio) * 6);
		GL11.glVertex3f(size, size * ratio + y2p, x2p + size / 2);
		GL11.glEnd();
		GL11.glTranslatef(-offsetX * scale, -offsetY * scale, -offsetZ * scale);
		renderChildren(childModels, scale);
		GL11.glTranslatef(0, size * ratio + offsetY * scale, 0);
		GL11.glRotatef(flexionAngle, 1, 0, 0);
		GL11.glTranslatef(offsetX * scale, 0, offsetZ * scale);
		// front
		GL11.glTranslatef(-x1i, -y1i, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glNormal3f(0, 0, -1);
		setUV(8, 16 - len1 / scale);
		GL11.glVertex3f(-size / 2, 0, 0);
		setUV(8, 16);
		GL11.glVertex3f(-size / 2, size * ratio + y1i, 0);
		setUV(4, 16);
		GL11.glVertex3f(size / 2, size * ratio + y1i, 0);
		setUV(4, 16 - len1 / scale);
		GL11.glVertex3f(size / 2, 0, 0);
		GL11.glEnd();
		GL11.glTranslatef(x1i, y1i, 0);
		// back
		GL11.glTranslatef(-x2i, -y2i, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glNormal3f(0, 0, 1);
		setUV(16, 16 - len2 / scale);
		GL11.glVertex3f(size + size / 2, 0, size);
		setUV(16, 16);
		GL11.glVertex3f(size + size / 2, size * ratio + y2i, size);
		setUV(12, 16);
		GL11.glVertex3f(size / 2, size * ratio + y2i, size);
		setUV(12, 16 - len2 / scale);
		GL11.glVertex3f(size / 2, 0, size);
		GL11.glEnd();
		GL11.glTranslatef(x2i, y2i, 0);
		GL11.glBegin(GL11.GL_QUADS);
		// right side
		GL11.glNormal3f(-1, 0, 0);
		setUV(12, 10);
		GL11.glVertex3f(0, 0, size);
		setUV(12, 16);
		GL11.glVertex3f(0, size * ratio, size);
		setUV(8, 16);
		GL11.glVertex3f(0, size * ratio, 0);
		setUV(8, 10);
		GL11.glVertex3f(0, 0, 0);
		// left side
		GL11.glNormal3f(1, 0, 0);
		setUV(4, 10);
		GL11.glVertex3f(size, 0, 0);
		setUV(4, 16);
		GL11.glVertex3f(size, size * ratio, 0);
		setUV(0, 16);
		GL11.glVertex3f(size, size * ratio, size);
		setUV(0, 10);
		GL11.glVertex3f(size, 0, size);
		// hand
		GL11.glNormal3f(0, 1, 0);
		setUV(8, 0);
		GL11.glVertex3f(0, size * ratio, 0);
		setUV(12, 0);
		GL11.glVertex3f(0, size * ratio, size);
		setUV(12, 4);
		GL11.glVertex3f(size, size * ratio, size);
		setUV(8, 4);
		GL11.glVertex3f(size, size * ratio, 0);
		GL11.glEnd();
		renderChildren(forearmChildModels, scale);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void setUV(float u, float v) {
		GL11.glTexCoord2f((textureOffsetXLimb + u) / textureWidth, (textureOffsetYLimb + v) / textureHeight);
	}

	private void renderChildren(List<ModelRenderer> children, float scale) {
		if (children != null) {
			for (ModelRenderer child : children) {
				child.render(scale);
			}
		}
	}
}
