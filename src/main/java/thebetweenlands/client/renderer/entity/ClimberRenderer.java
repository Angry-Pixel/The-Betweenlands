package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import thebetweenlands.common.entity.ClimbingMob;
import thebetweenlands.common.entity.Orientation;

import java.util.List;

public abstract class ClimberRenderer<T extends ClimbingMob, M extends EntityModel<T>> extends MobRenderer<T, M> {

	public ClimberRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		Orientation orientation = entity.getOrientation();
		Orientation renderOrientation = entity.calculateOrientation(partialTicks);

		float verticalOffset = 0.075F;

		double x = entity.getAttachmentOffset(Direction.Axis.X, partialTicks) - (float) renderOrientation.normal().x * verticalOffset;
		double y = entity.getAttachmentOffset(Direction.Axis.Y, partialTicks) - (float) renderOrientation.normal().y * verticalOffset;
		double z = entity.getAttachmentOffset(Direction.Axis.Z, partialTicks) - (float) renderOrientation.normal().z * verticalOffset;

		stack.translate(x, y, z);

		stack.pushPose();

		stack.mulPose(Axis.YP.rotationDegrees(orientation.yaw()));
		stack.mulPose(Axis.XP.rotationDegrees(orientation.pitch()));
		stack.mulPose(Axis.YP.rotationDegrees(Math.signum(0.5f - orientation.componentY() - orientation.componentZ() - orientation.componentX()) * orientation.yaw()));

		stack.translate(0.0D, -1.5D, 0.0D);

		super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);

		stack.translate(0.0D, 1.5D, 0.0D);

		stack.popPose();

		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			stack.pushPose();
			RenderSystem.enableBlend();

			double rx = Mth.lerp(partialTicks, entity.xo, entity.getX());
			double ry = Mth.lerp(partialTicks, entity.yo, entity.getY());
			double rz = Mth.lerp(partialTicks, entity.zo, entity.getZ());

			DebugRenderer.renderFilledBox(stack, buffer, new AABB(0, 0, 0, 0, 0, 0).inflate(0.1f).move(entity.attachmentNormal), 1.0F, 0.0F, 0.0F, 0.75F);

			VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
			Vec3 forward = orientation.getGlobal(entity.getYRot(), 0);

			consumer.addVertex(stack.last(), (float) orientation.normal().x, (float) orientation.normal().y, (float) orientation.normal().z).setColor(0.0F, 0.0F, 1.0F, 1.0F).setNormal(stack.last(), 0.0F, 0.0F, 1.0F);
			consumer.addVertex(stack.last(), (float) (orientation.normal().x * 2 + forward.x * 2), (float) (orientation.normal().y + forward.y * 2), (float) (orientation.normal().z + forward.z * 2)).setColor(0.0F, 0.0F, 1.0F, 1.0F).setNormal(stack.last(), 0.0F, 0.0F, 1.0F);

			consumer = buffer.getBuffer(RenderType.lines());
			forward = orientation.getGlobal(entity.getYRot(), -90);

			consumer.addVertex(stack.last(), (float) orientation.normal().x, (float) orientation.normal().y, (float) orientation.normal().z).setColor(0.0F, 1.0F, 0.0F, 1.0F).setNormal(stack.last(), 0.0F, 1.0F, 0.0F);
			consumer.addVertex(stack.last(), (float) (orientation.normal().x * 2 + forward.x * 2), (float) (orientation.normal().y + forward.y * 2), (float) (orientation.normal().z + forward.z * 2)).setColor(0.0F, 1.0F, 0.0F, 1.0F).setNormal(stack.last(), 0.0F, 1.0F, 0.0F);

			consumer = buffer.getBuffer(RenderType.lines());
			forward = orientation.getGlobal(entity.getYRot() - 90, 0);

			consumer.addVertex(stack.last(), (float) orientation.normal().x, (float) orientation.normal().y, (float) orientation.normal().z).setColor(1.0F, 0.0F, 0.0F, 1.0F).setNormal(stack.last(), 1.0F, 0.0F, 0.0F);
			consumer.addVertex(stack.last(), (float) (orientation.normal().x * 2 + forward.x * 2), (float) (orientation.normal().y + forward.y * 2), (float) (orientation.normal().z + forward.z * 2)).setColor(1.0F, 0.0F, 0.0F, 1.0F).setNormal(stack.last(), 1.0F, 0.0F, 0.0F);

			BlockPos target = entity.getPathingTarget();
			if (target != null) {
				LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), new AABB(target).move(-rx - x, -ry - y, -rz - z), 0.0F, 1.0F, 1.0F, 1.0F);
			}

			List<ClimbingMob.PathingTarget> pathingTargets = entity.getTrackedPathingTargets();

			if(pathingTargets != null) {
				int i = 0;

				for(ClimbingMob.PathingTarget pathingTarget : pathingTargets) {
					BlockPos pos = pathingTarget.pos();

					LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), new AABB(pos).move(-rx - x, -ry - y, -rz - z), 1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 0.15f);

					stack.pushPose();
					stack.translate(pos.getX() + 0.5D - rx - x, pos.getY() + 0.5D - ry - y, pos.getZ() + 0.5D - rz - z);

					stack.mulPose(pathingTarget.side().getOpposite().getRotation());

					LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), new AABB(-0.501D, -0.501D, -0.501D, 0.501D, -0.45D, 0.501D), 1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f);
					DebugRenderer.renderFilledBox(stack, buffer, new AABB(-0.501D, -0.501D, -0.501D, 0.501D, -0.45D, 0.501D), 1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 0.15F);

					PoseStack.Pose matrix4f = stack.last();
					VertexConsumer builder = buffer.getBuffer(RenderType.lines());

					builder.addVertex(matrix4f, -0.501f, -0.45f, -0.501f).setColor(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).setNormal(matrix4f, pathingTarget.side().getNormal().getX(), pathingTarget.side().getNormal().getY(), pathingTarget.side().getNormal().getZ());
					builder.addVertex(matrix4f, 0.501f, -0.45f, 0.501f).setColor(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).setNormal(matrix4f, pathingTarget.side().getNormal().getX(), pathingTarget.side().getNormal().getY(), pathingTarget.side().getNormal().getZ());
					builder.addVertex(matrix4f, -0.501f, -0.45f, 0.501f).setColor(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).setNormal(matrix4f, pathingTarget.side().getNormal().getX(), pathingTarget.side().getNormal().getY(), pathingTarget.side().getNormal().getZ());
					builder.addVertex(matrix4f, 0.501f, -0.45f, -0.501f).setColor(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).setNormal(matrix4f, pathingTarget.side().getNormal().getX(), pathingTarget.side().getNormal().getY(), pathingTarget.side().getNormal().getZ());

					stack.popPose();
					i++;
				}
			}


			stack.popPose();
			RenderSystem.disableBlend();
		}

		stack.translate(-x, -y, -z);
	}
}
