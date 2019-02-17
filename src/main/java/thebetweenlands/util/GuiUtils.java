package thebetweenlands.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.math.MathHelper;

public class GuiUtils {
	public static void drawLine(float xPos1, float yPos1, float xPos2, float yPos2, int colour) {
		float[] colors = ColorUtils.getRGBA(colour);

		GlStateManager.disableTexture2D();

		GlStateManager.pushMatrix();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(colors[0], colors[1], colors[2], colors[3]);

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(xPos1, yPos1);
		GL11.glVertex2d(xPos2, yPos2);
		GL11.glEnd();

		GlStateManager.popMatrix();

		GlStateManager.enableTexture2D();
	}

	public static void drawRect(float xPos1, float yPos1, float xPos2, float yPos2, int colour) {
		float[] colors = ColorUtils.getRGBA(colour);

		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(colors[0], colors[1], colors[2], colors[3]);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(xPos1, yPos1);
		GL11.glVertex2d(xPos1, yPos2);
		GL11.glVertex2d(xPos2, yPos2);
		GL11.glVertex2d(xPos2, yPos1);
		GL11.glEnd();

		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
	}

	public static void drawPartialCircle(int x, int y, double radius, int startAngle, int endAngle) {
		GlStateManager.disableTexture2D();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		startAngle -= 90;
		endAngle -= 90;
		for(int angle = startAngle; angle <= endAngle; angle++) {
			float rad = (float) (Math.PI * angle / 180F);
			float x2 = (float) (x + radius * Math.cos(rad));
			float y2 = (float) (y + radius * Math.sin(rad));
			GL11.glVertex3f(x2, y2, 0.0F);
			GL11.glVertex3f(x, y, 0.0F);
		}
		GL11.glEnd();
		GlStateManager.enableTexture2D();
	}

	public static void drawCircle(int x, int y, double radius) {
		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableBlend();
		GL11.glBegin(6);
		for(int i = 0; i <= 360; i++) {
			double sin = Math.sin(((double)i * Math.PI) / 180D) * radius;
			double cos = Math.cos(((double)i * Math.PI) / 180D) * radius;
			GL11.glVertex2d((double)x + sin, (double)y + cos);
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
	}

	public static void drawCircleOutline(int x, int y, double radius) {
		GlStateManager.enableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableBlend();
		float rotation = (float)(Math.PI * 2D / 360D);
		float moveX = (float)Math.cos(rotation);
		float moveY = (float)Math.sin(rotation);
		float xOffset = (float)radius;
		float yOffset = 0.0F;
		GL11.glBegin(2);
		for(int i = 0; i < 360; i++) {
			GL11.glVertex2f(x + xOffset, y + yOffset);
			float prevXOffset = xOffset;
			xOffset = moveX * xOffset - moveY * yOffset;
			yOffset = moveY * prevXOffset + moveX * yOffset;
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
	}

	public static void drawCircleOutline(int x, int y, double radius, int corners) {
		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableBlend();
		float rotation = (float)(Math.PI * 2D / (double)corners);
		float moveX = (float)Math.cos(rotation);
		float moveY = (float)Math.sin(rotation);
		float xOffset = (float)radius;
		float yOffset = 0.0F;
		GL11.glBegin(2);
		for(int i = 0; i < corners; i++) {
			GL11.glVertex2f(x + xOffset, y + yOffset);
			float prevXOffset = xOffset;
			xOffset = moveX * xOffset - moveY * yOffset;
			yOffset = moveY * prevXOffset + moveX * yOffset;
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
	}

	public static void renderTexturedCircleSegment(int segments, double maxAngle, double wrapAngle, double radius, double innerRadius, double minU, double maxU, double minV, double maxV) {
		GlStateManager.disableCull();
		GL11.glBegin(GL11.GL_QUADS);
		for(int i = 0; i < segments; i++) {
			double angle = i * (maxAngle / segments);
			double nextAngle = (i + 1) * (maxAngle / segments);
			double sin = Math.sin((angle * Math.PI) / 180D) * radius;
			double cos = Math.cos((angle * Math.PI) / 180D) * radius;
			double len = Math.sqrt(sin*sin+cos*cos);
			double nextSin = Math.sin((nextAngle * Math.PI) / 180D) * radius;
			double nextCos = Math.cos((nextAngle * Math.PI) / 180D) * radius;
			double nextLen = Math.sqrt(nextSin*nextSin+nextCos*nextCos);

			double wrapCircumference = Math.PI * wrapAngle * 2.0D;
			double circumference = Math.PI * angle * 2.0D;
			double nextCircumference = Math.PI * nextAngle * 2.0D;

			double diffU = maxU - minU;

			double textureU = minU + (diffU / wrapCircumference * circumference);
			double nextTextureU = minU + (diffU / wrapCircumference * nextCircumference);

			if(textureU % diffU > nextTextureU % diffU) {
				double diffToLimit = maxU - textureU % diffU;

				double interpolatedAngle = angle + (nextAngle - angle) / (nextTextureU - textureU) * diffToLimit;
				double interpolatedSin = Math.sin((interpolatedAngle * Math.PI) / 180D) * radius;
				double interpolatedCos = Math.cos((interpolatedAngle * Math.PI) / 180D) * radius;
				double interpolatedLen = Math.sqrt(interpolatedSin*interpolatedSin+interpolatedCos*interpolatedCos);

				textureU %= diffU;
				nextTextureU %= diffU;

				GL11.glTexCoord2d(textureU, maxV);
				GL11.glVertex2d(sin, cos);
				GL11.glTexCoord2d(maxU, maxV);
				GL11.glVertex2d(interpolatedSin, interpolatedCos);
				GL11.glTexCoord2d(maxU, minV);
				GL11.glVertex2d(interpolatedSin / interpolatedLen * innerRadius, interpolatedCos / interpolatedLen * innerRadius);
				GL11.glTexCoord2d(textureU, minV);
				GL11.glVertex2d(sin / interpolatedLen * innerRadius, cos / interpolatedLen * innerRadius);

				GL11.glTexCoord2d(minU, maxV);
				GL11.glVertex2d(interpolatedSin, interpolatedCos);
				GL11.glTexCoord2d(nextTextureU, maxV);
				GL11.glVertex2d(nextSin, nextCos);
				GL11.glTexCoord2d(nextTextureU, minV);
				GL11.glVertex2d(nextSin / nextLen * innerRadius, nextCos / nextLen * innerRadius);
				GL11.glTexCoord2d(minU, minV);
				GL11.glVertex2d(interpolatedSin / interpolatedLen * innerRadius, interpolatedCos / interpolatedLen * innerRadius);
			} else {
				textureU %= diffU;
				if(nextTextureU % diffU == textureU && nextTextureU > textureU) {
					nextTextureU = maxU;
				} else {
					nextTextureU %= diffU;
				}
				GL11.glTexCoord2d(textureU, maxV);
				GL11.glVertex2d(sin, cos);
				GL11.glTexCoord2d(nextTextureU, maxV);
				GL11.glVertex2d(nextSin, nextCos);
				GL11.glTexCoord2d(nextTextureU, minV);
				GL11.glVertex2d(nextSin / nextLen * innerRadius, nextCos / nextLen * innerRadius);
				GL11.glTexCoord2d(textureU, minV);
				GL11.glVertex2d(sin / len * innerRadius, cos / len * innerRadius);
			}
		}
		GL11.glEnd();
		GlStateManager.enableCull();
	}

	public static void renderTexturedCircleSegment(int segments, double maxAngle, double wrapAngle, double wrapRadius, double radius, double innerRadius, double minU, double maxU, double minV, double maxV) {
		double segmentWidth = (radius - innerRadius);
		double requiredSegments = segmentWidth / wrapRadius;
		double diffV = maxV - minV;
		double vPerSegment = MathHelper.clamp(diffV * requiredSegments, 0.0D, diffV);
		for(int i = 0; i < MathHelper.ceil(requiredSegments); i++) {
			double renderInnerRadius = segmentWidth / requiredSegments * i;
			double renderOuterRadius = segmentWidth / requiredSegments * (i+1);
			double segmentLength = renderOuterRadius - renderInnerRadius;
			if(renderInnerRadius > segmentWidth)
				break;
			if(renderOuterRadius > segmentWidth) {
				renderOuterRadius = segmentWidth;
			}

			double textureV = minV;
			double textureVOuter = minV + vPerSegment / segmentLength * (renderOuterRadius - renderInnerRadius);

			if(vPerSegment < diffV) {
				textureVOuter = minV + diffV / segmentLength * (renderOuterRadius - renderInnerRadius);
			}

			renderTexturedCircleSegment(segments, maxAngle, wrapAngle, renderOuterRadius + innerRadius, renderInnerRadius + innerRadius, minU, maxU, textureV, textureVOuter);
		}
	}

	/**
	 * Renders a texture mapped circle segment with wrapping textures.
	 * UVs in the range [0,1][0,1]
	 * @param segments Number of sub segments to render
	 * @param maxAngle Circle segment angle
	 * @param wrapAngle Texture wrapping angle
	 * @param wrapRadius Texture wrapping radius
	 * @param radius Circle segment radius
	 * @param innerRadius Inner circle segment radius
	 * @param borderWidth Border width
	 */
	public static void renderMappedCircleSegmentWrapped(int segments, double maxAngle, double wrapAngle, double wrapRadius, 
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
		double initialMaxAngle = maxAngle;
		maxAngle = maxAngle + borderAngle*2;
		innerRadius = innerRadius - borderWidth;
		double innerSegmentMaxAngle = maxAngle - 2.0D * borderAngle;
		double wrapAngleInner = wrapAngle * (Math.PI * radius * 2.0D) / (Math.PI * innerRadius * 2.0D);

		GlStateManager.pushMatrix();

		//Inner segment
		GlStateManager.pushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngle, wrapRadius, radius - borderWidth, innerRadius + borderWidth, sminU, smaxU, sminV, smaxV);
		GlStateManager.popMatrix();

		//Border 1
		if(initialMaxAngle < 360.0D) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate((float)(-maxAngle+borderAngle*2), 0, 0, 1);
			GlStateManager.translate(0, innerRadius + borderWidth, 0);
			GlStateManager.disableCull();
			GL11.glBegin(GL11.GL_QUADS);

			double borderLength = (radius - innerRadius);
			double requiredSegments = borderLength / wrapRadius;
			double diffV = b1maxV - b1minV;
			double vPerSegment = MathHelper.clamp(diffV * requiredSegments, 0.0D, diffV);
			for(int i = 0; i < MathHelper.ceil(requiredSegments); i++) {
				double renderInnerRadius = borderLength / requiredSegments * i;
				double renderOuterRadius = borderLength / requiredSegments * (i+1);
				double segmentLength = renderOuterRadius - renderInnerRadius;
				if(renderInnerRadius > borderLength - borderWidth * 2)
					break;
				if(renderOuterRadius > borderLength - borderWidth * 2) {
					renderOuterRadius = borderLength - borderWidth * 2;
				}

				double textureV = b1minV;
				double textureVOuter = b1minV + vPerSegment / segmentLength * (renderOuterRadius - renderInnerRadius);

				if(vPerSegment < diffV) {
					textureVOuter = b1minV + diffV / segmentLength * (renderOuterRadius - renderInnerRadius);
				}

				GL11.glTexCoord2d(b1maxU, textureV);
				GL11.glVertex2d(0, renderInnerRadius);
				GL11.glTexCoord2d(b1minU, textureV);
				GL11.glVertex2d(borderWidth, renderInnerRadius);
				GL11.glTexCoord2d(b1minU, textureVOuter);
				GL11.glVertex2d(borderWidth, renderOuterRadius);
				GL11.glTexCoord2d(b1maxU, textureVOuter);
				GL11.glVertex2d(0, renderOuterRadius);
			}

			GL11.glEnd();
			GlStateManager.popMatrix();
		}

		//Border 2
		GlStateManager.pushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngle, radius, radius - borderWidth, b2minU, b2maxU, b2maxV, b2minV);
		GlStateManager.popMatrix();

		//Border 3
		if(initialMaxAngle < 360.0D) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.05D, innerRadius + borderWidth, 0);
			GL11.glBegin(GL11.GL_QUADS);

			double borderLength = (radius - innerRadius);
			double requiredSegments = borderLength / wrapRadius;
			double diffV = b3maxV - b3minV;
			double vPerSegment = MathHelper.clamp(diffV * requiredSegments, 0.0D, diffV);
			for(int i = 0; i < MathHelper.ceil(requiredSegments); i++) {
				double renderInnerRadius = borderLength / requiredSegments * i;
				double renderOuterRadius = borderLength / requiredSegments * (i+1);
				double segmentLength = renderOuterRadius - renderInnerRadius;
				if(renderInnerRadius > borderLength - borderWidth * 2)
					break;
				if(renderOuterRadius > borderLength - borderWidth * 2) {
					renderOuterRadius = borderLength - borderWidth * 2;
				}

				double textureV = b3minV;
				double textureVOuter = b3minV + vPerSegment / segmentLength * (renderOuterRadius - renderInnerRadius);

				if(vPerSegment < diffV) {
					textureVOuter = b1minV + diffV / segmentLength * (renderOuterRadius - renderInnerRadius);
				}

				GL11.glTexCoord2d(b3minU, textureV);
				GL11.glVertex2d(0, renderInnerRadius);
				GL11.glTexCoord2d(b3maxU, textureV);
				GL11.glVertex2d(-borderWidth, renderInnerRadius);
				GL11.glTexCoord2d(b3maxU, textureVOuter);
				GL11.glVertex2d(-borderWidth, renderOuterRadius);
				GL11.glTexCoord2d(b3minU, textureVOuter);
				GL11.glVertex2d(0, renderOuterRadius);
			}

			GL11.glEnd();
			GlStateManager.popMatrix();
		}

		//Border 4
		GlStateManager.pushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngleInner, innerRadius + borderWidth + 0.05D, innerRadius, b4minU, b4maxU, b4maxV, b4minV);
		GlStateManager.popMatrix();

		if(initialMaxAngle < 360.0D) {
			//Corner 1
			GlStateManager.pushMatrix();
			GlStateManager.rotate((float)(-maxAngle+borderAngle*2), 0, 0, 1);
			GlStateManager.translate(0, innerRadius, 0);

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

			GlStateManager.popMatrix();

			//Corner 2
			GlStateManager.pushMatrix();
			GlStateManager.rotate((float)(-maxAngle+borderAngle*2), 0, 0, 1);
			GlStateManager.translate(0, innerRadius, 0);

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

			GlStateManager.popMatrix();

			//Corner 3
			GlStateManager.pushMatrix();
			GlStateManager.translate(-borderWidth, innerRadius, 0);

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

			GlStateManager.popMatrix();

			//Corner 4
			GlStateManager.pushMatrix();
			GlStateManager.translate(-borderWidth, innerRadius, 0);

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

			GlStateManager.popMatrix();
		}


		GlStateManager.popMatrix();
	}
}