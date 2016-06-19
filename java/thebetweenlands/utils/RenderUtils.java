package thebetweenlands.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;

public class RenderUtils {
	public static void renderTexturedCircleSegment(int segments, double maxAngle, double wrapAngle, double radius, double innerRadius, double minU, double maxU, double minV, double maxV) {
		GL11.glDisable(GL11.GL_CULL_FACE);
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
	}

	public static void renderTexturedCircleSegment(int segments, double maxAngle, double wrapAngle, double wrapRadius, double radius, double innerRadius, double minU, double maxU, double minV, double maxV) {
		double segmentWidth = (radius - innerRadius);
		double requiredSegments = segmentWidth / wrapRadius;
		double diffV = maxV - minV;
		double vPerSegment = MathHelper.clamp_double(diffV * requiredSegments, 0.0D, diffV);
		for(int i = 0; i < MathHelper.ceiling_double_int(requiredSegments); i++) {
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
	 * Renders a texture mapped circle segment.
	 * UVs in the range [0,textureWidth][0,textureHeight]
	 * @param segments Number of sub segments to render
	 * @param maxAngle Circle segment angle
	 * @param radius Circle segment radius
	 * @param innerRadius Inner circle segment radius
	 * @param borderWidth Border width
	 * @param textureWidth Texture width
	 * @param textureHeight Texture height
	 */
	/*public static void renderMappedCircleSegment(int segments, double maxAngle, double radius, 
			double innerRadius, double borderWidth, double textureWidth, double textureHeight,
			double sminU, double smaxU, double sminV, double smaxV,
			double b1minU, double b1maxU, double b1minV, double b1maxV,
			double b2minU, double b2maxU, double b2minV, double b2maxV,
			double b3minU, double b3maxU, double b3minV, double b3maxV,
			double b4minU, double b4maxU, double b4minV, double b4maxV,
			double c1minU, double c1maxU, double c1minV, double c1maxV,
			double c2minU, double c2maxU, double c2minV, double c2maxV,
			double c3minU, double c3maxU, double c3minV, double c3maxV,
			double c4minU, double c4maxU, double c4minV, double c4maxV) {
		double circumference = Math.PI * radius * 2.0D;
		double straightBorderAspect = (b1maxU - b1minU) / (b1maxV - b1minV);
		double angularBorderAspect = (b2maxU - b2minU) / (b2maxV - b2minV);
	}*/

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
		double radiusDiff = radius - innerRadius;
		double wrapAngleInner = wrapAngle * (Math.PI * radius * 2.0D) / (Math.PI * innerRadius * 2.0D);

		GL11.glPushMatrix();

		//Inner segment
		GL11.glPushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngle, wrapRadius, radius - borderWidth, innerRadius + borderWidth, sminU, smaxU, sminV, smaxV);
		GL11.glPopMatrix();

		//Border 1
		if(initialMaxAngle < 360.0D) {
			GL11.glPushMatrix();
			GL11.glRotated(-maxAngle+borderAngle*2, 0, 0, 1);
			GL11.glTranslated(0, innerRadius + borderWidth, 0);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glBegin(GL11.GL_QUADS);

			double borderLength = (radius - innerRadius);
			double requiredSegments = borderLength / wrapRadius;
			double diffV = b1maxV - b1minV;
			double vPerSegment = MathHelper.clamp_double(diffV * requiredSegments, 0.0D, diffV);
			for(int i = 0; i < MathHelper.ceiling_double_int(requiredSegments); i++) {
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
			GL11.glPopMatrix();
		}

		//Border 2
		GL11.glPushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngle, radius, radius - borderWidth, b2minU, b2maxU, b2maxV, b2minV);
		GL11.glPopMatrix();

		//Border 3
		if(initialMaxAngle < 360.0D) {
			GL11.glPushMatrix();
			GL11.glTranslated(0.05D, innerRadius + borderWidth, 0);
			GL11.glBegin(GL11.GL_QUADS);

			double borderLength = (radius - innerRadius);
			double requiredSegments = borderLength / wrapRadius;
			double diffV = b3maxV - b3minV;
			double vPerSegment = MathHelper.clamp_double(diffV * requiredSegments, 0.0D, diffV);
			for(int i = 0; i < MathHelper.ceiling_double_int(requiredSegments); i++) {
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
			GL11.glPopMatrix();
		}

		//Border 4
		GL11.glPushMatrix();
		renderTexturedCircleSegment(segments, innerSegmentMaxAngle, wrapAngleInner, innerRadius + borderWidth + 0.05D, innerRadius, b4minU, b4maxU, b4maxV, b4minV);
		GL11.glPopMatrix();

		if(initialMaxAngle < 360.0D) {
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
		}


		GL11.glPopMatrix();
	}

	/**
	 * Saves the texture of an FBO to the specified PNG file.
	 * 
	 * @param file
	 * @param fbo
	 */
	public static void saveFboToFile(File file, Framebuffer fbo) {
		int prevFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

		fbo.bindFramebuffer(false);

		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = fbo.framebufferWidth;
		int height= fbo.framebufferHeight;
		int bpp = 4;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		String format = "PNG";
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int a = buffer.get(i + 3) & 0xFF;
				image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
			}
		}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) { 
			e.printStackTrace(); 
		}

		//Bind previous fbo
		OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, prevFBO);
	}
}
