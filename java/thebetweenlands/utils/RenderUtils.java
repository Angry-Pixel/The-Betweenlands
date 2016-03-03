package thebetweenlands.utils;

import org.lwjgl.opengl.GL11;

public class RenderUtils {
	public static void renderTexturedCircleSegment(int segments, double maxAngle, double wrapAngle, double radius, double innerRadius, double minU, double maxU, double minV, double maxV) {
		GL11.glBegin(GL11.GL_QUADS);
		for(int i = 0; i < segments; i++) {
			double angle = i * (maxAngle / segments);
			double nextAngle = (i + 0.99999999999D) * (maxAngle / segments);
			double sin = Math.sin((angle * Math.PI) / 180D) * radius;
			double cos = Math.cos((angle * Math.PI) / 180D) * radius;
			double len = Math.sqrt(sin*sin+cos*cos);
			double nextSin = Math.sin((nextAngle * Math.PI) / 180D) * radius;
			double nextCos = Math.cos((nextAngle * Math.PI) / 180D) * radius;
			double nextLen = Math.sqrt(nextSin*nextSin+nextCos*nextCos);

			double diffU = maxU - minU;

			double textureU = minU + (angle / wrapAngle * diffU) % (diffU);
			double nextTextureU = minU + (nextAngle / wrapAngle * diffU) % (diffU);

			GL11.glTexCoord2d(textureU, maxV);
			GL11.glVertex2d(sin, cos);
			GL11.glTexCoord2d(nextTextureU, maxV);
			GL11.glVertex2d(nextSin, nextCos);
			GL11.glTexCoord2d(nextTextureU, minV);
			GL11.glVertex2d(nextSin / nextLen * innerRadius, nextCos / nextLen * innerRadius);
			GL11.glTexCoord2d(textureU, minV);
			GL11.glVertex2d(sin / len * innerRadius, cos / len * innerRadius);
		}
		GL11.glEnd();
	}

	public static void renderMappedCircleSegment(int segments, double maxAngle, double wrapAngle, double wrapRadius, 
			double radius, double innerRadius, double borderWidth,
			double sminU, double smaxU, double sminV, double smaxV,
			double b1minU, double b1maxU, double b1minV, double b1maxV,
			double b2minU, double b2maxU, double b2minV, double b2maxV,
			double b3minU, double b3maxU, double b3minV, double b3maxV,
			double b4minU, double b4maxU, double b4minV, double b4maxV,
			double c1minU, double c1maxU, double c1minV, double c1maxV,
			double c2minU, double c2maxU, double c2minV, double c2maxV,
			double c3minU, double c3maxU, double c3minV, double c3maxV,
			double c4minU, double c4maxU, double c4minV, double c4maxV) {
		double borderAngle = borderWidth / (Math.PI * innerRadius * 2.0D / 360.0D);
		maxAngle = maxAngle + borderAngle*2;
		innerRadius = innerRadius - borderWidth;
		double innerSegmentMaxAngle = maxAngle - 2.0D * borderAngle;
		double radiusDiff = radius - innerRadius;
		double wrapAngleInner = wrapAngle * (Math.PI * radius * 2.0D) / (Math.PI * innerRadius * 2.0D);

		GL11.glPushMatrix();
		//GL11.glTranslated(borderWidth, borderWidth, 0);

		//Inner segment
		GL11.glPushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngle, radius - borderWidth, innerRadius + borderWidth, sminU, smaxU, sminV, smaxV);
		GL11.glPopMatrix();

		//Border 1
		GL11.glPushMatrix();
		GL11.glRotated(-maxAngle+borderAngle*2, 0, 0, 1);
		GL11.glTranslated(0, innerRadius, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(b1maxU, b1minV);
		GL11.glVertex2d(0, radiusDiff - borderWidth);
		GL11.glTexCoord2d(b1minU, b1minV);
		GL11.glVertex2d(borderWidth, radiusDiff - borderWidth);
		GL11.glTexCoord2d(b1minU, b1maxV);
		GL11.glVertex2d(borderWidth, borderWidth);
		GL11.glTexCoord2d(b1maxU, b1maxV);
		GL11.glVertex2d(0, borderWidth);
		GL11.glEnd();
		GL11.glPopMatrix();

		//Border 2
		GL11.glPushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngle, radius, radius - borderWidth, b2maxU, b2minU, b2maxV, b2minV);
		GL11.glPopMatrix();

		//Border 3
		GL11.glPushMatrix();
		GL11.glTranslated(-borderWidth*Math.sqrt(2), 0, 0);
		GL11.glTranslated(borderAngle, innerRadius, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(b3minU, b3minV);
		GL11.glVertex2d(0, borderWidth);
		GL11.glTexCoord2d(b3maxU, b3minV);
		GL11.glVertex2d(-borderWidth, borderWidth);
		GL11.glTexCoord2d(b3maxU, b3maxV);
		GL11.glVertex2d(-borderWidth, radiusDiff - borderWidth);
		GL11.glTexCoord2d(b3minU, b3maxV);
		GL11.glVertex2d(0, radiusDiff - borderWidth);
		GL11.glEnd();
		GL11.glPopMatrix();

		//Border 4
		GL11.glPushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngleInner, innerRadius + borderWidth, innerRadius, b4maxU, b4minU, b4maxV, b4minV);
		GL11.glPopMatrix();

		//Corner 1
		GL11.glPushMatrix();
		GL11.glRotated(-maxAngle+borderAngle*2, 0, 0, 1);
		GL11.glTranslated(0, innerRadius, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(c1maxU, c1maxV);
		GL11.glVertex2d(0, borderWidth);
		GL11.glTexCoord2d(c1minU, c1maxV);
		GL11.glVertex2d(borderWidth, borderWidth);
		GL11.glTexCoord2d(c1minU, c1minV);
		GL11.glVertex2d(borderWidth, 0);
		GL11.glTexCoord2d(c1maxU, c1minV);
		GL11.glVertex2d(0, 0);
		GL11.glEnd();
		GL11.glPopMatrix();

		//Corner 2
		GL11.glPushMatrix();
		GL11.glRotated(-maxAngle+borderAngle*2, 0, 0, 1);
		GL11.glTranslated(0, innerRadius, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(c2maxU, c2minV);
		GL11.glVertex2d(0, radius-innerRadius);
		GL11.glTexCoord2d(c2minU, c2minV);
		GL11.glVertex2d(borderWidth, radius-innerRadius);
		GL11.glTexCoord2d(c2minU, c2maxV);
		GL11.glVertex2d(borderWidth, radius-innerRadius-borderWidth);
		GL11.glTexCoord2d(c2maxU, c2maxV);
		GL11.glVertex2d(0, radius-innerRadius-borderWidth);
		GL11.glEnd();
		GL11.glPopMatrix();

		//Corner 3
		GL11.glPushMatrix();
		GL11.glTranslated(-borderWidth, innerRadius, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(c3minU, c3minV);
		GL11.glVertex2d(0, radius-innerRadius);
		GL11.glTexCoord2d(c3maxU, c3minV);
		GL11.glVertex2d(borderWidth, radius-innerRadius);
		GL11.glTexCoord2d(c3maxU, c3maxV);
		GL11.glVertex2d(borderWidth, radius-innerRadius-borderWidth);
		GL11.glTexCoord2d(c3minU, c3maxV);
		GL11.glVertex2d(0, radius-innerRadius-borderWidth);
		GL11.glEnd();
		GL11.glPopMatrix();

		//Corner 4
		GL11.glPushMatrix();
		GL11.glTranslated(-borderWidth, innerRadius, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(c4minU, c4maxV);
		GL11.glVertex2d(0, borderWidth);
		GL11.glTexCoord2d(c4maxU, c4maxV);
		GL11.glVertex2d(borderWidth, borderWidth);
		GL11.glTexCoord2d(c4maxU, c4minV);
		GL11.glVertex2d(borderWidth, 0);
		GL11.glTexCoord2d(c4minU, c4minV);
		GL11.glVertex2d(0, 0);
		GL11.glEnd();
		GL11.glPopMatrix();


		GL11.glPopMatrix();
	}
}
