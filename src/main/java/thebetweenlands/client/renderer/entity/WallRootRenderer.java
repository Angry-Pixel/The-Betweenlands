package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.model.BlankModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.wall.WallRoot;

import javax.annotation.Nullable;

//TODO fix rendering artifact in hull (line renders from top to bottom of the root)
public class WallRootRenderer<T extends WallRoot> extends WallHoleRenderer<T, BlankModel<T>> {

	private static final ResourceLocation ROOT_TEXTURE = TheBetweenlands.prefix("textures/block/root_middle.png");

	public WallRootRenderer(EntityRendererProvider.Context context) {
		super(context, new BlankModel<>(), ROOT_TEXTURE);
	}

	protected WallRootRenderer(EntityRendererProvider.Context context, ResourceLocation armTexture) {
		super(context, new BlankModel<>(), armTexture);
	}

	@Override
	protected void renderEntityModel(T entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state, boolean overlays) {
		if(this.model instanceof BlankModel<T>) {
			this.renderRootModel(entity, state.yRot(), state.partialTick(), stack, buffer, packedLight, overlay, color);
		} else {
			super.renderEntityModel(entity, stack, buffer, packedLight, overlay, color, state, overlays);
		}
	}

	protected void renderRootModel(T entity, float yRot, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color) {
		stack.pushPose();

		//Undo model transforms because arms are in world space relative to entity pos
		stack.translate(0.0F, 1.501F, 0.0F);
		stack.translate(0, -entity.getBbWidth() / 2, 0);
		stack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
		stack.translate(0, entity.getBbWidth() / 2, 0);
		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));

		this.renderBodyHull(entity, partialTick, stack.last(), buffer.getBuffer(RenderType.entityTranslucent(this.modelTexture)), packedLight, overlay, color);

		stack.popPose();
	}

	protected float getUvScale(T entity, float partialTicks) {
		return 2.0f;
	}

	protected float calculateHullContraction(T entity, int i, float armSize, float partialTicks) {
		return (1 - i / (float)(entity.armSegments.size() - 1)) * armSize;
	}

	protected void renderBodyHull(T entity, float partialTicks, PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, int color) {
		float uOffset = 0;

		float armSize = entity.getArmSize(partialTicks);

		float uvScale = this.getUvScale(entity, partialTicks) / Math.max(0.001f, armSize);

		float pos1X = 0, pos1Y = 0, pos1Z = 0;
		WallRoot.ArmSegment segment1 = null;
		for(int i = 0; i < entity.armSegments.size(); ++i) {
			WallRoot.ArmSegment segment2 = entity.armSegments.get(i);
			Vec3 pos = segment2.pos;
			Vec3 prevPos = segment2.prevPos;

			float pos2X = Mth.lerp(partialTicks, (float) prevPos.x, (float) pos.x);
			float pos2Y = Mth.lerp(partialTicks, (float) prevPos.y, (float) pos.y);
			float pos2Z = Mth.lerp(partialTicks, (float) prevPos.z, (float) pos.z);

			if(segment1 != null) {
				float maxUW = 0;

				int hullVerts = Math.min(segment1.offsetX.length, segment2.offsetX.length);

				for(int vertIndex = 0; vertIndex < hullVerts; vertIndex++) {
					int nextVertIndex = (vertIndex + 1) % hullVerts;

					float contraction1 = this.calculateHullContraction(entity, i - 1, armSize, partialTicks);
					float contraction2 = this.calculateHullContraction(entity, i, armSize, partialTicks);

					float v11x = pos1X + segment1.offsetX[vertIndex] * contraction1;
					float v11y = pos1Y + segment1.offsetY[vertIndex] * contraction1;
					float v11z = pos1Z + segment1.offsetZ[vertIndex] * contraction1;

					float v12x = pos1X + segment1.offsetX[nextVertIndex] * contraction1;
					float v12y = pos1Y + segment1.offsetY[nextVertIndex] * contraction1;
					float v12z = pos1Z + segment1.offsetZ[nextVertIndex] * contraction1;

					float v21x = pos2X + segment2.offsetX[vertIndex] * contraction2;
					float v21y = pos2Y + segment2.offsetY[vertIndex] * contraction2;
					float v21z = pos2Z + segment2.offsetZ[vertIndex] * contraction2;

					float v22x = pos2X + segment2.offsetX[nextVertIndex] * contraction2;
					float v22y = pos2Y + segment2.offsetY[nextVertIndex] * contraction2;
					float v22z = pos2Z + segment2.offsetZ[nextVertIndex] * contraction2;

					float uw1 = dist(v12x, v12y, v12z, v11x, v11y, v11z) * 0.5F * uvScale;
					float vw1 = dist(v21x, v21y, v21z, v11x, v11y, v11z) * uvScale;

					float uw2 = dist(v22x, v22y, v22z, v21x, v21y, v21z) * 0.5F * uvScale;
					float vw2 = dist(v22x, v22y, v22z, v12x, v12y, v12z) * uvScale;

					float uw = Math.max(uw1, uw2);
					float vw = Math.max(vw1, vw2);

					float d1x = v21x - v12x;
					float d1y = v21y - v12y;
					float d1z = v21z - v12z;

					float d2x = v22x - v11x;
					float d2y = v22y - v11y;
					float d2z = v22z - v11z;

					float nx = d1y * d2z - d1z * d2y;
					float ny = d1z * d2x - d1x * d2z;
					float nz = d1x * d2y - d1y * d2x;

					float len = len(nx, ny, nz);

					nx /= len;
					ny /= len;
					nz /= len;

					float us = uOffset;
					float vs = 0;

					consumer.addVertex(pose, v11x, v11y, v11z).setUv(us, vs).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
					consumer.addVertex(pose, v21x, v21y, v21z).setUv(us, vs + vw).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
					consumer.addVertex(pose, v22x, v22y, v22z).setUv(us + uw, vs + vw).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
					consumer.addVertex(pose, v12x, v12y, v12z).setUv(us + uw, vs).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);

					maxUW = Math.max(maxUW, uw);
				}

				uOffset += maxUW;
			}

			segment1 = segment2;
			pos1X = pos2X;
			pos1Y = pos2Y;
			pos1Z = pos2Z;
		}
	}

	protected static float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
		return len(x2 - x1, y2 - y1, z2 - z1);
	}

	protected static float len(float x, float y, float z) {
		return Mth.sqrt(x * x + y * y + z * z);
	}

	@Nullable
	@Override
	protected TextureAtlasSprite getWallSprite(T entity) {
		return entity.info.getWallSprite();
	}

	@Override
	protected float getHoleDepthPercent(T entity, float partialTicks) {
		return entity.getHoleDepthPercent(partialTicks);
	}

	@Override
	protected float getMainModelVisibilityPercent(T entity, float partialTicks) {
		return entity.getHoleDepthPercent(partialTicks);
	}
}
