package thebetweenlands.client.renderer.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.client.renderer.HalfBlockModelRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.LivingHanger;

public class LivingHangerRenderer extends EntityRenderer<LivingHanger> {
	public final static ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/block/hanger.png");
	public final static ModelResourceLocation HANGER_MODEL_PATH = ModelResourceLocation.standalone(TheBetweenlands.prefix("block/hanger"));

	public LivingHangerRenderer(Context context) {
		super(context);
	}

	@Override
	public void render(LivingHanger entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		super.render(entity, entityYaw, partialTick, stack, buffer, light);

		BakedModel fullModel = Minecraft.getInstance().getModelManager().getModel(HANGER_MODEL_PATH);
		VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
		double rootX = Mth.lerp(partialTick, entity.xo, entity.getX());
		double rootY = Mth.lerp(partialTick, entity.yo, entity.getY());
		double rootZ = Mth.lerp(partialTick, entity.zo, entity.getZ());
		float rootYaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
		Vec3[] currentJoints = entity.getJointPositions();
		Vec3[] prevJoints = entity.getPrevJointPositions();

		stack.pushPose();
		stack.mulPose(new Quaternionf().rotationY(-rootYaw * Mth.DEG_TO_RAD));
		stack.translate(-0.5D, 0.0D, -0.5D);

		List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
		directions.add(null);

		Matrix4f rootMatrix = stack.last().pose();
		for (net.minecraft.core.Direction dir : directions) {
			java.util.List<BakedQuad> fullQuads = fullModel.getQuads(Blocks.AIR.defaultBlockState(), dir,
					RandomSource.create(), ModelData.EMPTY, null);
			for (BakedQuad quad : fullQuads) {
				int[] vertexData = quad.getVertices();
				for (int v = 0; v != 4; v++) {
					int offset = v * 8;
					float x = Float.intBitsToFloat(vertexData[offset]);
					float y = Float.intBitsToFloat(vertexData[offset + 1]);
					float z = Float.intBitsToFloat(vertexData[offset + 2]);
					float u = Float.intBitsToFloat(vertexData[offset + 4]);
					float vTex = Float.intBitsToFloat(vertexData[offset + 5]);

					Vector4f pos = new Vector4f(x, y, z, 1.0f).mul(rootMatrix);

					consumer.addVertex(pos.x(), pos.y(), pos.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(u, vTex) .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
				}
			}
		}

		stack.popPose();

		for (int i = 0; i != entity.getHangerLength(); i++) {
			var part = entity.parts[i];
			double partX = Mth.lerp(partialTick, part.xo, part.getX()) - rootX;
			double partY = Mth.lerp(partialTick, part.yo, part.getY()) - rootY;
			double partZ = Mth.lerp(partialTick, part.zo, part.getZ()) - rootZ;
			float calculatedYaw = 0.0F;
			float calculatedPitch = 0.0F;

			if (currentJoints[i] != null && currentJoints[i + 1] != null && prevJoints[i] != null && prevJoints[i + 1] != null) {
				double topX = Mth.lerp(partialTick, prevJoints[i].x, currentJoints[i].x);
				double topY = Mth.lerp(partialTick, prevJoints[i].y, currentJoints[i].y);
				double topZ = Mth.lerp(partialTick, prevJoints[i].z, currentJoints[i].z);
				double bottomX = Mth.lerp(partialTick, prevJoints[i + 1].x, currentJoints[i + 1].x);
				double bottomY = Mth.lerp(partialTick, prevJoints[i + 1].y, currentJoints[i + 1].y);
				double bottomZ = Mth.lerp(partialTick, prevJoints[i + 1].z, currentJoints[i + 1].z);
				double dx = bottomX - topX;
				double dy = bottomY - topY;
				double dz = bottomZ - topZ;
				double distanceXZ = Math.sqrt(dx * dx + dz * dz);
				calculatedYaw = (float) (Math.atan2(dz, -dx) * (180.0 / Math.PI)) - 90.0F;
				calculatedPitch = (float) (Math.atan2(-dy, distanceXZ) * (180.0 / Math.PI)) - 90.0F;
			}

			boolean renderTopHalf = (i % 2 == 0);

			stack.pushPose();
			
			stack.translate(partX, partY + 0.25D, partZ);
			stack.mulPose(new Quaternionf().rotationY(-rootYaw * Mth.DEG_TO_RAD));
			stack.mulPose(new Quaternionf().rotationY(calculatedYaw * Mth.DEG_TO_RAD));
			stack.mulPose(new Quaternionf().rotationX(calculatedPitch * Mth.DEG_TO_RAD));

			double pivotY = renderTopHalf ? -0.75D : -0.25D;

			stack.translate(-0.5D, pivotY, -0.5D);

			List<BakedQuad> targetQuads = HalfBlockModelRenderer.getHalfQuads(fullModel, renderTopHalf, 8.0f);

			Matrix4f matrix = stack.last().pose();
			for (BakedQuad quad : targetQuads) {
				int[] vertexData = quad.getVertices();
				for (int v = 0; v != 4; v++) {
					int offset = v * 8;
					float x = Float.intBitsToFloat(vertexData[offset]);
					float y = Float.intBitsToFloat(vertexData[offset + 1]);
					float z = Float.intBitsToFloat(vertexData[offset + 2]);
					float u = Float.intBitsToFloat(vertexData[offset + 4]);
					float vTex = Float.intBitsToFloat(vertexData[offset + 5]);

					Vector4f pos = new Vector4f(x, y, z, 1.0f).mul(matrix);

					consumer.addVertex(pos.x(), pos.y(), pos.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(u, vTex) .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
				}
			}
			stack.popPose();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(LivingHanger entity) {
		return TEXTURE;
	}

}
