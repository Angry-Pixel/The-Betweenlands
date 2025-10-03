package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.LargeSludgeWormModel;
import thebetweenlands.client.model.entity.SludgeWormEggSacModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.LargeSludgeWorm;

import javax.annotation.Nullable;

public class LargeSludgeWormRenderer extends MobRenderer<LargeSludgeWorm, LargeSludgeWormModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/large_sludge_worm.png");
	private final SludgeWormEggSacModel eggModel;

	public LargeSludgeWormRenderer(EntityRendererProvider.Context context) {
		super(context, new LargeSludgeWormModel(context.bakeLayer(BLModelLayers.LARGE_SLUDGE_WORM)), 0.0F);
		this.eggModel = new SludgeWormEggSacModel(context.bakeLayer(BLModelLayers.SLUDGE_WORM_EGG_SAC));
		this.addLayer(new WormSlimeLayer(this, context.getModelSet()));
	}

	@Override
	public void render(LargeSludgeWorm entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean isVisible = this.isBodyVisible(entity);
		boolean isTranslucentToPlayer = !isVisible && !entity.isInvisibleTo(minecraft.player);
		boolean isGlowing = minecraft.shouldEntityAppearGlowing(entity);
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
		int color = isTranslucentToPlayer ? 654311423 : -1;

		stack.pushPose();

		stack.translate(0.0D, 0.55D, 0.0D);

		var renderType = this.getRenderType(entity, isVisible, isTranslucentToPlayer, isGlowing);
		if (renderType != null) {
			this.model.renderHead(entity, stack, buffer.getBuffer(renderType), packedLight, overlay, color, partialTicks, true);
			this.model.renderTail(entity, stack, buffer.getBuffer(renderType), packedLight, overlay, color, partialTicks, true);
			this.renderSpine(entity, stack, buffer.getBuffer(renderType), packedLight, overlay, color, partialTicks);
			this.renderEggSac(entity, stack, buffer.getBuffer(this.getEggRenderType(isVisible, isTranslucentToPlayer, isGlowing)), packedLight, partialTicks);
		}

		if (!entity.isSpectator()) {
			for (RenderLayer<LargeSludgeWorm, LargeSludgeWormModel> renderlayer : this.layers) {
				renderlayer.render(stack, buffer, packedLight, entity, 0, 0, partialTicks, entity.tickCount + partialTicks, 0, 0);
			}
		}

		stack.popPose();
	}

	@Nullable
	protected RenderType getEggRenderType(boolean bodyVisible, boolean translucent, boolean glowing) {
		ResourceLocation resourcelocation = SludgeWormEggSacRenderer.TEXTURE;
		if (translucent) {
			return RenderType.itemEntityTranslucentCull(resourcelocation);
		} else if (bodyVisible) {
			return this.eggModel.renderType(resourcelocation);
		} else {
			return glowing ? RenderType.outline(resourcelocation) : null;
		}
	}

	protected void renderEggSac(LargeSludgeWorm entity, PoseStack stack, VertexConsumer consumer, int light, float partialTicks) {
		if(entity.eggSacPosition != null && entity.prevEggSacPosition != null) {
			Vec3 pos = entity.eggSacPosition;
			Vec3 prevPos = entity.prevEggSacPosition;

			double x = Mth.lerp(partialTicks, prevPos.x, pos.x);
			double y = Mth.lerp(partialTicks, prevPos.y, pos.y);
			double z = Mth.lerp(partialTicks, prevPos.z, pos.z);

			stack.pushPose();
			stack.translate(x, y - 0.4D + Math.sin((entity.tickCount + partialTicks) * 0.25D) * 0.025D, z);
			stack.scale(-1, -1, 1);

			float scale = Math.max(0, Math.min(1, entity.getEggSacPercentage()));

			stack.scale(scale, scale, scale);

			stack.translate(0, -1.5D, 0);

			this.eggModel.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY);

			stack.popPose();
		}
	}

	protected void renderSpine(LargeSludgeWorm entity, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color, float partialTicks) {
		int i = 0;
		for(LargeSludgeWorm.SpineBone bone : entity.bones) {
			Vec3 pos = bone.pos;
			Vec3 prevPos = bone.prevPos;

			double x = Mth.lerp(partialTicks, prevPos.x, pos.x);
			double y = Mth.lerp(partialTicks, prevPos.y, pos.y);
			double z = Mth.lerp(partialTicks, prevPos.z, pos.z);

			float boneYaw = Mth.lerp(partialTicks, bone.prevYaw, bone.yaw);

			stack.pushPose();
			stack.translate(x, y + Math.sin(-(entity.tickCount + partialTicks) * 0.25F + i * 0.2F) * 0.05F, z);
			this.model.renderSpinePiece(i % 6, boneYaw, stack, consumer, light, overlay, color);
			stack.popPose();

			i++;
		}
	}

	protected static float dist(double x1, double y1, double z1, double x2, double y2, double z2) {
		return len(x2 - x1, y2 - y1, z2 - z1);
	}

	protected static float len(double x, double y, double z) {
		return Mth.sqrt((float) (x * x + y * y + z * z));
	}

	@Override
	public ResourceLocation getTextureLocation(LargeSludgeWorm entity) {
		return TEXTURE;
	}

	public static class WormSlimeLayer extends RenderLayer<LargeSludgeWorm, LargeSludgeWormModel> {

		public static final ResourceLocation HULL_TEXTURE = TheBetweenlands.prefix("textures/entity/large_sludge_worm_hull.png");
		private final LargeSludgeWormModel model;

		public WormSlimeLayer(RenderLayerParent<LargeSludgeWorm, LargeSludgeWormModel> renderer, EntityModelSet modelSet) {
			super(renderer);
			this.model = new LargeSludgeWormModel(modelSet.bakeLayer(BLModelLayers.LARGE_SLUDGE_WORM_OUTER));
		}

		@Override
		public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, LargeSludgeWorm entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
			if(!entity.segmentsAvailable) {
				return;
			}
			Minecraft minecraft = Minecraft.getInstance();
			boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
			if (!entity.isInvisible() || flag) {
				VertexConsumer vertexconsumer;
				if (flag) {
					vertexconsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
				} else {
					vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
				}

				this.getParentModel().copyPropertiesTo(this.model);
				this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
				this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
				this.model.renderHead(entity, stack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), -1, partialTick, false);
				this.model.renderTail(entity, stack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), -1, partialTick, false);

				stack.pushPose();
				stack.translate(0.0D, 0.055D, 0.0D);
				this.renderBodyHull(entity, stack, buffer.getBuffer(flag ? RenderType.outline(HULL_TEXTURE) : RenderType.entityTranslucent(HULL_TEXTURE)), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), partialTick);
				stack.popPose();
			}
		}

		protected void renderBodyHull(LargeSludgeWorm entity, PoseStack stack, VertexConsumer consumer, int light, int overlay, float partialTicks) {
			if(entity.segments.length == 0) return;
			
			float uOffset = 0;

			int i = 0;

			float pos1X = 0, pos1Y = 0, pos1Z = 0;
			LargeSludgeWorm.HullSegment segment1 = entity.segments[0]; // cheap hack until the actual root cause is figured out
			for(LargeSludgeWorm.HullSegment segment2 : entity.segments) {
				Vec3 pos = segment2.pos;
				Vec3 prevPos = segment2.prevPos;

				float pos2X = (float) Mth.lerp(partialTicks, prevPos.x, pos.x);
				float pos2Y = (float) Mth.lerp(partialTicks, prevPos.y, pos.y);
				float pos2Z = (float) Mth.lerp(partialTicks, prevPos.z, pos.z);

				if(segment1 != null) {
					float maxUW = 0;

					int hullVerts = Math.min(segment1.offsetX.length, segment2.offsetX.length);

					for(int vertIndex = 0; vertIndex < hullVerts; vertIndex++) {
						int nextVertIndex = (vertIndex + 1) % hullVerts;

						float contraction1 = this.calculateHullContraction(entity, (i - 1) / (float)(entity.segments.length - 1), partialTicks);
						float contraction2 = this.calculateHullContraction(entity, i / (float)(entity.segments.length - 1), partialTicks);

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

						float uw1 = dist(v12x, v12y, v12z, v11x, v11y, v11z) * 0.5F;
						float vw1 = dist(v21x, v21y, v21z, v11x, v11y, v11z);

						float uw2 = dist(v22x, v22y, v22z, v21x, v21y, v21z) * 0.5F;
						float vw2 = dist(v22x, v22y, v22z, v12x, v12y, v12z);

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

						PoseStack.Pose pose = stack.last();

						consumer.addVertex(pose, v11x, v11y, v11z).setUv(us, vs).setColor(-1).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
						consumer.addVertex(pose, v21x, v21y, v21z).setUv(us, vs + vw).setColor(-1).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
						consumer.addVertex(pose, v22x, v22y, v22z).setUv(us + uw, vs + vw).setColor(-1).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
						consumer.addVertex(pose, v12x, v12y, v12z).setUv(us + uw, vs).setColor(-1).setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);

						maxUW = Math.max(maxUW, uw);
					}

					uOffset += maxUW;
				}

				segment1 = segment2;
				pos1X = pos2X;
				pos1Y = pos2Y;
				pos1Z = pos2Z;

				i++;
			}
		}

		protected float calculateHullContraction(LargeSludgeWorm entity, float percent, float partialTicks) {
			double arcLength = entity.spineySpliney.getArcLength();
			float minBound = 1.8F / (float)arcLength;
			float maxBound = 1.0F - minBound;
			float lerp = 1;
			if(percent < minBound) {
				lerp = percent / minBound;
			} else if(percent > maxBound) {
				lerp = 1 - (percent - maxBound) / minBound;
			}
			float contraction = ((float)Math.sin(percent * entity.spineySpliney.getArcLength() * 4 - (entity.tickCount + partialTicks) * 0.25F) + 1.0F) / 2.0F * 0.2F + 0.8F;
			return 0.99F + (contraction - 0.99F) * lerp;
		}
	}
}
