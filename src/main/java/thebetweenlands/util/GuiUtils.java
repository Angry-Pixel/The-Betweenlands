package thebetweenlands.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GuiUtils {

	public static void drawPartialCircle(PoseStack.Pose pose, int x, int y, double radius, int startAngle, int endAngle) {
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION);
		startAngle -= 90;
		endAngle -= 90;
		for (int angle = startAngle; angle <= endAngle; angle++) {
			float rad = (float) (Math.PI * angle / 180F);
			float x2 = (float) (x + radius * Math.cos(rad));
			float y2 = (float) (y + radius * Math.sin(rad));
			builder.addVertex(pose, x2, y2, 0.0F);
			builder.addVertex(pose, x, y, 0.0F);
		}
		BufferUploader.drawWithShader(builder.buildOrThrow());
	}

	public static void drawCircle(PoseStack.Pose pose, int x, int y, float radius) {
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
		for (int i = 0; i <= 360; i++) {
			float sin = Mth.sin((i * Mth.PI) / 180.0F) * radius;
			float cos = Mth.cos((i * Mth.PI) / 180.0F) * radius;
			builder.addVertex(pose, x + sin, y + cos, 0.0F);
		}
		BufferUploader.drawWithShader(builder.buildOrThrow());
	}

	public static void drawCircleOutline(PoseStack.Pose pose, int x, int y, double radius) {
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
		float rotation = (float) (Math.PI * 2D / 360D);
		float moveX = (float) Math.cos(rotation);
		float moveY = (float) Math.sin(rotation);
		float xOffset = (float) radius;
		float yOffset = 0.0F;
		for (int i = 0; i < 360; i++) {
			builder.addVertex(pose, x + xOffset, y + yOffset, 0.0F);
			float prevXOffset = xOffset;
			xOffset = moveX * xOffset - moveY * yOffset;
			yOffset = moveY * prevXOffset + moveX * yOffset;
		}
		BufferUploader.drawWithShader(builder.buildOrThrow());
	}

	public static void drawCircleOutline(PoseStack.Pose pose, int x, int y, double radius, int corners) {
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
		float rotation = (float) (Math.PI * 2D / (double) corners);
		float moveX = (float) Math.cos(rotation);
		float moveY = (float) Math.sin(rotation);
		float xOffset = (float) radius;
		float yOffset = 0.0F;
		for (int i = 0; i < corners; i++) {
			builder.addVertex(pose, x + xOffset, y + yOffset, 0.0F);
			float prevXOffset = xOffset;
			xOffset = moveX * xOffset - moveY * yOffset;
			yOffset = moveY * prevXOffset + moveX * yOffset;
		}
		BufferUploader.drawWithShader(builder.buildOrThrow());
	}

	public static void renderTexturedCircleSegment(PoseStack.Pose pose, ResourceLocation texture, int segments, float maxAngle, float wrapAngle, float radius, float innerRadius, float minU, float maxU, float minV, float maxV) {
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		for (int i = 0; i < segments; i++) {
			float angle = i * (maxAngle / segments);
			float nextAngle = (i + 1) * (maxAngle / segments);
			float sin = Mth.sin((angle * Mth.PI) / 180.0F) * radius;
			float cos = Mth.cos((angle * Mth.PI) / 180.0F) * radius;
			float len = Mth.sqrt(sin * sin + cos * cos);
			float nextSin = Mth.sin((nextAngle * Mth.PI) / 180.0F) * radius;
			float nextCos = Mth.cos((nextAngle * Mth.PI) / 180.0F) * radius;
			float nextLen = Mth.sqrt(nextSin * nextSin + nextCos * nextCos);

			float wrapCircumference = Mth.PI * wrapAngle * 2.0F;
			float circumference = Mth.PI * angle * 2.0F;
			float nextCircumference = Mth.PI * nextAngle * 2.0F;

			float diffU = maxU - minU;

			float textureU = minU + (diffU / wrapCircumference * circumference);
			float nextTextureU = minU + (diffU / wrapCircumference * nextCircumference);

			if (textureU % diffU > nextTextureU % diffU) {
				float diffToLimit = maxU - textureU % diffU;

				float interpolatedAngle = angle + (nextAngle - angle) / (nextTextureU - textureU) * diffToLimit;
				float interpolatedSin = Mth.sin((interpolatedAngle * Mth.PI) / 180.0F) * radius;
				float interpolatedCos = Mth.cos((interpolatedAngle * Mth.PI) / 180.0F) * radius;
				float interpolatedLen = Mth.sqrt(interpolatedSin * interpolatedSin + interpolatedCos * interpolatedCos);

				textureU %= diffU;
				nextTextureU %= diffU;

				builder.addVertex(pose, sin, cos, 0.0F).setUv(textureU, maxV);
				builder.addVertex(pose, interpolatedSin, interpolatedCos, 0.0F).setUv(maxU, maxV);
				builder.addVertex(pose, interpolatedSin / interpolatedLen * innerRadius, interpolatedCos / interpolatedLen * innerRadius, 0.0F).setUv(maxU, minV);
				builder.addVertex(pose, sin / interpolatedLen * innerRadius, cos / interpolatedLen * innerRadius, 0.0F).setUv(textureU, minV);


				builder.addVertex(pose, interpolatedSin, interpolatedCos, 0.0F).setUv(minU, maxV);
				builder.addVertex(pose, nextSin, nextCos, 0.0F).setUv(nextTextureU, maxV);
				builder.addVertex(pose, nextSin / nextLen * innerRadius, nextCos / nextLen * innerRadius, 0.0F).setUv(nextTextureU, minV);
				builder.addVertex(pose, interpolatedSin / interpolatedLen * innerRadius, interpolatedCos / interpolatedLen * innerRadius, 0.0F).setUv(minU, minV);
			} else {
				textureU %= diffU;
				if (nextTextureU % diffU == textureU && nextTextureU > textureU) {
					nextTextureU = maxU;
				} else {
					nextTextureU %= diffU;
				}

				builder.addVertex(pose, sin, cos, 0.0F).setUv(textureU, maxV);
				builder.addVertex(pose, nextSin, nextCos, 0.0F).setUv(nextTextureU, maxV);
				builder.addVertex(pose, nextSin / nextLen * innerRadius, nextCos / nextLen * innerRadius, 0.0F).setUv(nextTextureU, minV);
				builder.addVertex(pose, sin / len * innerRadius, cos / len * innerRadius, 0.0F).setUv(textureU, minV);
			}
		}
		BufferUploader.drawWithShader(builder.buildOrThrow());
	}

	public static void renderTexturedCircleSegment(PoseStack.Pose pose, ResourceLocation texture, int segments, float maxAngle, float wrapAngle, float wrapRadius, float radius, float innerRadius, float minU, float maxU, float minV, float maxV) {
		float segmentWidth = (radius - innerRadius);
		float requiredSegments = segmentWidth / wrapRadius;
		float diffV = maxV - minV;
		float vPerSegment = (float) Mth.clamp(diffV * requiredSegments, 0.0D, diffV);
		for (int i = 0; i < Mth.ceil(requiredSegments); i++) {
			float renderInnerRadius = segmentWidth / requiredSegments * i;
			float renderOuterRadius = segmentWidth / requiredSegments * (i + 1);
			float segmentLength = renderOuterRadius - renderInnerRadius;
			if (renderInnerRadius > segmentWidth)
				break;
			if (renderOuterRadius > segmentWidth) {
				renderOuterRadius = segmentWidth;
			}

			float textureVOuter = minV + vPerSegment / segmentLength * (renderOuterRadius - renderInnerRadius);

			if (vPerSegment < diffV) {
				textureVOuter = minV + diffV / segmentLength * (renderOuterRadius - renderInnerRadius);
			}

			renderTexturedCircleSegment(pose, texture, segments, maxAngle, wrapAngle, renderOuterRadius + innerRadius, renderInnerRadius + innerRadius, minU, maxU, minV, textureVOuter);
		}
	}

	/**
	 * Renders a texture mapped circle segment with wrapping textures.
	 * UVs in the range [0,1][0,1]
	 *
	 * @param segments    Number of sub segments to render
	 * @param maxAngle    Circle segment angle
	 * @param wrapAngle   Texture wrapping angle
	 * @param wrapRadius  Texture wrapping radius
	 * @param radius      Circle segment radius
	 * @param innerRadius Inner circle segment radius
	 * @param borderWidth Border width
	 */
	public static void renderMappedCircleSegmentWrapped(PoseStack stack, ResourceLocation texture,
														int segments, float maxAngle, float wrapAngle, float wrapRadius,
														float radius, float innerRadius, float borderWidth,
														float sminU, float smaxU, float sminV, float smaxV,
														float b1minU, float b1maxU, float b1minV, float b1maxV,
														float b2minU, float b2maxU, float b2minV, float b2maxV,
														float b3minU, float b3maxU, float b3minV, float b3maxV,
														float b4minU, float b4maxU, float b4minV, float b4maxV,
														float c1minU, float c1maxU, float c1minV, float c1maxV,
														float c2minU, float c2maxU, float c2minV, float c2maxV,
														float c3minU, float c3maxU, float c3minV, float c3maxV,
														float c4minU, float c4maxU, float c4minV, float c4maxV) {

		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		float borderAngle = borderWidth / (Mth.PI * innerRadius * 2.0F / 360.0F);
		float initialMaxAngle = maxAngle;
		maxAngle = maxAngle + borderAngle * 2;
		innerRadius = innerRadius - borderWidth;
		float innerSegmentMaxAngle = maxAngle - 2.0F * borderAngle;
		float wrapAngleInner = wrapAngle * (Mth.PI * radius * 2.0F) / (Mth.PI * innerRadius * 2.0F);

		stack.pushPose();

		//Inner segment
		stack.pushPose();
		renderTexturedCircleSegment(stack.last(), texture, segments, innerSegmentMaxAngle, wrapAngle, wrapRadius, radius - borderWidth, innerRadius + borderWidth, sminU, smaxU, sminV, smaxV);
		stack.popPose();

		//Border 1
		if (initialMaxAngle < 360.0D) {
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(-maxAngle + borderAngle * 2));
			stack.translate(0, innerRadius + borderWidth, 0);
			RenderSystem.disableCull();
			BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

			float borderLength = (radius - innerRadius);
			float requiredSegments = borderLength / wrapRadius;
			float diffV = b1maxV - b1minV;
			float vPerSegment = (float) Mth.clamp(diffV * requiredSegments, 0.0D, diffV);
			for (int i = 0; i < Mth.ceil(requiredSegments); i++) {
				float renderInnerRadius = borderLength / requiredSegments * i;
				float renderOuterRadius = borderLength / requiredSegments * (i + 1);
				float segmentLength = renderOuterRadius - renderInnerRadius;
				if (renderInnerRadius > borderLength - borderWidth * 2)
					break;
				if (renderOuterRadius > borderLength - borderWidth * 2) {
					renderOuterRadius = borderLength - borderWidth * 2;
				}

				float textureVOuter = b1minV + vPerSegment / segmentLength * (renderOuterRadius - renderInnerRadius);

				if (vPerSegment < diffV) {
					textureVOuter = b1minV + diffV / segmentLength * (renderOuterRadius - renderInnerRadius);
				}

				builder.addVertex(stack.last(), 0, renderInnerRadius, 0.0F).setUv(b1maxU, b1minV);
				builder.addVertex(stack.last(), borderWidth, renderInnerRadius, 0.0F).setUv(b1minU, b1minV);
				builder.addVertex(stack.last(), borderWidth, renderOuterRadius, 0.0F).setUv(b1minU, textureVOuter);
				builder.addVertex(stack.last(), 0, renderOuterRadius, 0.0F).setUv(b1maxU, textureVOuter);
			}

			BufferUploader.drawWithShader(builder.buildOrThrow());
			stack.popPose();
		}

		//Border 2
		stack.pushPose();
		renderTexturedCircleSegment(stack.last(), texture, segments, innerSegmentMaxAngle, wrapAngle, radius, radius - borderWidth, b2minU, b2maxU, b2maxV, b2minV);
		stack.popPose();

		//Border 3
		if (initialMaxAngle < 360.0D) {
			stack.pushPose();
			stack.translate(0.05D, innerRadius + borderWidth, 0);
			BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

			float borderLength = (radius - innerRadius);
			float requiredSegments = borderLength / wrapRadius;
			float diffV = b3maxV - b3minV;
			float vPerSegment = (float) Mth.clamp(diffV * requiredSegments, 0.0D, diffV);
			for (int i = 0; i < Mth.ceil(requiredSegments); i++) {
				float renderInnerRadius = borderLength / requiredSegments * i;
				float renderOuterRadius = borderLength / requiredSegments * (i + 1);
				float segmentLength = renderOuterRadius - renderInnerRadius;
				if (renderInnerRadius > borderLength - borderWidth * 2)
					break;
				if (renderOuterRadius > borderLength - borderWidth * 2) {
					renderOuterRadius = borderLength - borderWidth * 2;
				}

				float textureVOuter = b3minV + vPerSegment / segmentLength * (renderOuterRadius - renderInnerRadius);

				if (vPerSegment < diffV) {
					textureVOuter = b1minV + diffV / segmentLength * (renderOuterRadius - renderInnerRadius);
				}

				builder.addVertex(stack.last(), 0, renderInnerRadius, 0.0F).setUv(b3minU, b3minV);
				builder.addVertex(stack.last(), -borderWidth, renderInnerRadius, 0.0F).setUv(b3maxU, b3minV);
				builder.addVertex(stack.last(), -borderWidth, renderOuterRadius, 0.0F).setUv(b3maxU, textureVOuter);
				builder.addVertex(stack.last(), 0, renderOuterRadius, 0.0F).setUv(b3minU, textureVOuter);
			}

			BufferUploader.drawWithShader(builder.buildOrThrow());
			stack.popPose();
		}

		//Border 4
		stack.pushPose();
		renderTexturedCircleSegment(stack.last(), texture, segments, innerSegmentMaxAngle, wrapAngleInner, innerRadius + borderWidth + 0.05F, innerRadius, b4minU, b4maxU, b4maxV, b4minV);
		stack.popPose();

		if (initialMaxAngle < 360.0D) {
			//Corner 1
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(-maxAngle + borderAngle * 2));
			stack.translate(0, innerRadius, 0);
			RenderSystem.disableCull();

			BufferBuilder cornerBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			cornerBuilder.addVertex(stack.last(), 0, borderWidth, 0.0F).setUv(c1maxU, c1maxV);
			cornerBuilder.addVertex(stack.last(), borderWidth, borderWidth, 0.0F).setUv(c1minU, c1maxV);
			cornerBuilder.addVertex(stack.last(), borderWidth, 0, 0.0F).setUv(c1minU, c1minV);
			cornerBuilder.addVertex(stack.last(), 0, 0, 0.0F).setUv(c1maxU, c1minV);
			BufferUploader.drawWithShader(cornerBuilder.buildOrThrow());

			stack.popPose();

			//Corner 2
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(-maxAngle + borderAngle * 2));
			stack.translate(0, innerRadius, 0);

			BufferBuilder cornerBuilder2 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			cornerBuilder2.addVertex(stack.last(), 0, radius - innerRadius, 0.0F).setUv(c2maxU, c2minV);
			cornerBuilder2.addVertex(stack.last(), borderWidth, radius - innerRadius, 0.0F).setUv(c2minU, c2minV);
			cornerBuilder2.addVertex(stack.last(), borderWidth, radius - innerRadius - borderWidth, 0.0F).setUv(c2minU, c2maxV);
			cornerBuilder2.addVertex(stack.last(), 0, radius - innerRadius - borderWidth, 0.0F).setUv(c2maxU, c2maxV);
			BufferUploader.drawWithShader(cornerBuilder2.buildOrThrow());

			stack.popPose();

			//Corner 3
			stack.pushPose();
			stack.translate(-borderWidth, innerRadius, 0);

			BufferBuilder cornerBuilder3 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			cornerBuilder3.addVertex(stack.last(), 0, radius - innerRadius, 0.0F).setUv(c3minU, c3minV);
			cornerBuilder3.addVertex(stack.last(), borderWidth, radius - innerRadius, 0.0F).setUv(c3maxU, c3minV);
			cornerBuilder3.addVertex(stack.last(), borderWidth, radius - innerRadius - borderWidth, 0.0F).setUv(c3maxU, c3maxV);
			cornerBuilder3.addVertex(stack.last(), 0, radius - innerRadius - borderWidth, 0.0F).setUv(c3minU, c3maxV);
			BufferUploader.drawWithShader(cornerBuilder3.buildOrThrow());

			stack.popPose();

			//Corner 4
			stack.pushPose();
			stack.translate(-borderWidth, innerRadius, 0);

			BufferBuilder cornerBuilder4 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			cornerBuilder4.addVertex(stack.last(), 0, borderWidth, 0.0F).setUv(c4minU, c4maxV);
			cornerBuilder4.addVertex(stack.last(), borderWidth, borderWidth, 0.0F).setUv(c4maxU, c4maxV);
			cornerBuilder4.addVertex(stack.last(), borderWidth, 0, 0.0F).setUv(c4maxU, c4minV);
			cornerBuilder4.addVertex(stack.last(), 0, 0, 0.0F).setUv(c4minU, c4minV);
			BufferUploader.drawWithShader(cornerBuilder4.buildOrThrow());

			stack.popPose();
		}

		stack.popPose();
	}
}
