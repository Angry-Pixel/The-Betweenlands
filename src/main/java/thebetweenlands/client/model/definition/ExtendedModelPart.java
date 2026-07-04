package thebetweenlands.client.model.definition;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import thebetweenlands.util.RotationOrder;

import java.util.List;
import java.util.Map;
import java.util.Set;

//[VanillaCopy] of ModelPart, with additional context for RotationOrder and cube type
//also allows setting a flexion angle for the special arm cubes
public class ExtendedModelPart extends ModelPart {

	private final RotationOrder order;
	private final ExtendedPartDefinition.Type type;
	private float flexionAngle;

	public ExtendedModelPart(List<Cube> cubes, Map<String, ModelPart> children, RotationOrder order, ExtendedPartDefinition.Type type) {
		super(cubes, children);
		this.order = order;
		this.type = type;
	}

	@Override
	public void translateAndRotate(PoseStack stack) {
		stack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
		if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
			stack.mulPose(this.order.rotate(this.xRot, this.yRot, this.zRot));
		}

		if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
			stack.scale(this.xScale, this.yScale, this.zScale);
		}
	}

	public void setFlexionAngle(float angle) {
		this.flexionAngle = angle;
	}

	//TODO 3D model parts for dragonfly wings
	@Override
	public void render(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		if (this.visible) {
			if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
				stack.pushPose();
				this.translateAndRotate(stack);
				if (!this.skipDraw) {
					for (ModelPart.Cube cube : this.cubes) {
						switch (this.type) {
							case VANILLA -> cube.compile(stack.last(), consumer, light, overlay, color);
							case ARM -> {
								if (cube instanceof ContextCube ctx) {
									this.renderFlexibleArm(ctx, stack, consumer, light, overlay, color);
								}
							}
							case THREE_DIMENSIONAL ->
								throw new IllegalArgumentException("3D model parts not implemented yet");
						}
					}
				}

				for (ModelPart modelpart : this.children.values()) {
					modelpart.render(stack, consumer, light, overlay, color);
				}

				stack.popPose();
			}
		}
	}

	private void renderFlexibleArm(ContextCube cube, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		float width = cube.dimensionX;
		float depth = cube.dimensionZ;
		float scale = 0.0625F;
		float ex = cube.growY * scale;
		float ratio = 3 / 2F;
		float sizeZ = depth * scale;
		float sizeX = width * scale;
		float sizeY = sizeZ * ratio;
		final float flexMin = -95;
		float angle = flexionAngle > 0 ? 0 : Math.max(flexionAngle, flexMin);
		float theta = -angle * Mth.DEG_TO_RAD;
		float x1 = -sizeZ / 2 - ex, y1 = 0;
		float x2 = sizeZ / 2 + ex, y2 = 0;
		float x3 = -sizeZ / 2 - ex, y3 = sizeY + ex;
		float x4 = sizeZ / 2 + ex, y4 = sizeY + ex;
		float c = Mth.cos(theta), s = Mth.sin(theta);
		float x1p = x1 * c - y1 * s;
		float y1p = x1 * s + y1 * c;
		float x2p = x2 * c - y2 * s;
		float y2p = x2 * s + y2 * c;
		float x3p = x3 * c - y3 * s;
		float y3p = x3 * s + y3 * c;
		float x4p = x4 * c - y4 * s;
		float y4p = x4 * s + y4 * c;
		float slope = (float) Math.tan(theta - Mth.HALF_PI);
		float x1i = x1;
		float y1i = slope * x1i + (y1p - x1p * slope);
		float x2o = x2;
		float y2o = -y1i;
		float len1 = Mth.sqrt((x1i - x3p) * (x1i - x3p) + (y1i - y3p) * (y1i - y3p));
		float len2 = Mth.sqrt((x2o - x4p) * (x2o - x4p) + (y2o - y4p) * (y2o - y4p));

		stack.pushPose();
		stack.translate(cube.originX * scale, cube.originY * scale, cube.originZ * scale);
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
		// top
		vertex(consumer, stack.last(), cube, -ex, -ex, -ex, depth, depth, color, light, overlay, Direction.UP);
		vertex(consumer, stack.last(), cube, -ex, -ex, sizeZ, depth, 0.0F, color, light, overlay, Direction.UP);
		vertex(consumer, stack.last(), cube, sizeX, -ex, sizeZ, dw, 0.0F, color, light, overlay, Direction.UP);
		vertex(consumer, stack.last(), cube, sizeX, -ex, -ex, dw, depth, color, light, overlay, Direction.UP);
//		// front
		vertex(consumer, stack.last(), cube, -ex, -ex, -ex, depth, depth, color, light, overlay, Direction.NORTH);
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, -ex, dw, depth, color, light, overlay, Direction.NORTH);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y1i, -ex, dw, dm + y1i / sizeYES * m, color, light, overlay, Direction.NORTH);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y1i, -ex, depth, dm + y1i / sizeYES * m, color, light, overlay, Direction.NORTH);
//		// back
		vertex(consumer, stack.last(), cube, -ex, -ex, sizeZ + ex, dwdw, depth, color, light, overlay, Direction.SOUTH);
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, sizeZ + ex, dwd, depth, color, light, overlay, Direction.SOUTH);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio - y1i, sizeZ + ex, dwd, dm - y1i / sizeYES * m, color, light, overlay, Direction.SOUTH);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio - y1i, sizeZ + ex, dwdw, dm - y1i / sizeYES * m, color, light, overlay, Direction.SOUTH);

		float rightU = 0;
		float rightV = depth;
		// middle
		vertex(consumer, stack.last(), cube, -ex, -ex, -ex, rightU + depth, rightV, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y1p, x1p + sizeZ / 2, rightU + depth - (x1p / sizeZES * depth + depth / 2), rightV + m + y1p / sizeYES * m, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, -ex, sizeZ + ex, rightU, rightV, color, light, overlay, Direction.WEST);
		// inner
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y1p, x1p + sizeZ / 2, rightU + depth - (x1p / sizeZES * depth + depth / 2), rightV + m + y1p / sizeYES * m, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y1i, x1i + sizeZ / 2, rightU + depth - (x1i / sizeZES * depth + depth / 2), rightV + m + y1i / sizeYES * m, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, -ex, -ex, rightU + depth, rightV, color, light, overlay, Direction.WEST);
		// outer
		vertex(consumer, stack.last(), cube, -ex, -ex, sizeZ + ex, rightU, rightV, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y2p, x2p + sizeZ / 2, rightU + depth - (x2p + sizeZES / 2) * depth / sizeZES, rightV + m + y2p / sizeYES * m, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y1p, x1p + sizeZ / 2, rightU + depth - (x1p + sizeZES / 2) * depth / sizeZES, rightV + m + y1p / sizeYES * m, color, light, overlay, Direction.WEST);
		// elbow
		vertex(consumer, stack.last(), cube, -ex, -ex, sizeZ + ex, rightU, rightV, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y2o, x2o + sizeZ / 2, rightU + depth - (x2o + sizeZES / 2) * depth / sizeZES, rightV + m + y2o / sizeYES * m, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + y2p, x2p + sizeZ / 2, rightU + depth - (x2p + sizeZES / 2) * depth / sizeZES, rightV + m + y2p / sizeYES * m, color, light, overlay, Direction.WEST);
		// other side
		float leftU = dw;
		float leftV = depth;
		// middle
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, -ex, leftU, leftV, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y1p, x1p + sizeZ / 2, leftU + x1p / sizeZES * depth + depth / 2, leftV + m + y1p / sizeYES * m, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, sizeZ + ex, leftU + depth, leftV, color, light, overlay, Direction.EAST);
		// inner
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y1p, x1p + sizeZ / 2, leftU + x1p / sizeZES * depth + depth / 2, leftV + m + y1p / sizeYES * m, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y1i, x1i + sizeZ / 2, leftU + x1i / sizeZES * depth + depth / 2, leftV + m + y1i / sizeYES * m, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, -ex, leftU, leftV, color, light, overlay, Direction.EAST);
		// outer
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, sizeZ + ex, leftU + depth, leftV, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y2p, x2p + sizeZ / 2, leftU + x2p / sizeZES * depth + depth / 2, leftV + m + y2p / sizeYES * m, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y1p, x1p + sizeZ / 2, leftU + x1p / sizeZES * depth + depth / 2, leftV + m + y1p / sizeYES * m, color, light, overlay, Direction.EAST);
		// elbow
		vertex(consumer, stack.last(), cube, sizeX + ex, -ex, sizeZ + ex, leftU + depth, leftV, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y2o, x2o + sizeZ / 2, leftU + x2o / sizeZES * depth + depth / 2, leftV + m + y2o / sizeYES * m, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + y2p, x2p + sizeZ / 2, leftU + x2p / sizeZES * depth + depth / 2, leftV + m + y2p / sizeYES * m, color, light, overlay, Direction.EAST);
		stack.translate(-cube.originX * scale, -cube.originY * scale, -cube.originZ * scale);
		stack.translate(0, sizeZ * ratio + cube.originY * scale, 0);
		stack.mulPose(Axis.XP.rotationDegrees(angle));
		stack.translate(cube.originX * scale, 0, cube.originZ * scale);
		// front
		stack.translate(sizeX / 2, -y1i, 0);
		vertex(consumer, stack.last(), cube, -sizeX / 2 - ex, 0, -ex, depth, dmm - len1 / (m * scale + ex) * m, color, light, overlay, Direction.NORTH);
		vertex(consumer, stack.last(), cube, -sizeX / 2 - ex, sizeZ * ratio + y1i + ex, -ex, depth, dmm, color, light, overlay, Direction.NORTH);
		vertex(consumer, stack.last(), cube, sizeX / 2 + ex, sizeZ * ratio + y1i + ex, -ex, dw, dmm, color, light, overlay, Direction.NORTH);
		vertex(consumer, stack.last(), cube, sizeX / 2 + ex, 0, -ex, dw, dmm - len1 / (m * scale + ex) * m, color, light, overlay, Direction.NORTH);
		stack.translate(-sizeX / 2, y1i, 0);
		// back
		stack.translate(-sizeX / 2, -y2o, 0);
		vertex(consumer, stack.last(), cube, sizeX + sizeX / 2 + ex, 0, sizeZ + ex, dwd, dmm - len2 / (h * scale + ex * 2) * h, color, light, overlay, Direction.SOUTH);
		vertex(consumer, stack.last(), cube, sizeX + sizeX / 2 + ex, sizeZ * ratio + y2o + ex, sizeZ + ex, dwd, dmm, color, light, overlay, Direction.SOUTH);
		vertex(consumer, stack.last(), cube, sizeX / 2 - ex, sizeZ * ratio + y2o + ex, sizeZ + ex, dwdw, dmm, color, light, overlay, Direction.SOUTH);
		vertex(consumer, stack.last(), cube, sizeX / 2 - ex, 0, sizeZ + ex, dwdw, dmm - len2 / (h * scale + ex * 2) * h, color, light, overlay, Direction.SOUTH);
		stack.translate(sizeX / 2, y2o, 0);
		// right side
		vertex(consumer, stack.last(), cube, -ex, 0, sizeZ + ex, rightU, rightV + m, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + ex, sizeZ + ex, rightU, rightV + h, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + ex, -ex, rightU + depth, rightV + h, color, light, overlay, Direction.EAST);
		vertex(consumer, stack.last(), cube, -ex, 0, -ex, rightU + depth, rightV + m, color, light, overlay, Direction.EAST);
		// left side
		vertex(consumer, stack.last(), cube, sizeX + ex, 0, -ex, leftU, leftV + m, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + ex, -ex, leftU, leftV + h, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + ex, sizeZ + ex, leftU + depth, leftV + h, color, light, overlay, Direction.WEST);
		vertex(consumer, stack.last(), cube, sizeX + ex, 0, sizeZ + ex, leftU + depth, leftV + m, color, light, overlay, Direction.WEST);
		// hand
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + ex, -ex, dw, depth, color, light, overlay, Direction.DOWN);
		vertex(consumer, stack.last(), cube, -ex, sizeZ * ratio + ex, sizeZ + ex, dw, 0, color, light, overlay, Direction.DOWN);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + ex, sizeZ + ex, dw + width, 0, color, light, overlay, Direction.DOWN);
		vertex(consumer, stack.last(), cube, sizeX + ex, sizeZ * ratio + ex, -ex, dw + width, depth, color, light, overlay, Direction.DOWN);
		stack.popPose();
	}

	private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, ContextCube cube, float x, float y, float z, float u, float v, int color, int light, int overlay, Direction facing) {
		consumer.addVertex(pose, x, y, z).setUv((cube.texCoordU + u) / 64, (cube.texCoordV + v) / 64).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, facing.getStepX(), facing.getStepY(), facing.getStepZ());
	}
}
