package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.particle.SpikeParticle;
import thebetweenlands.client.renderer.SpikeRenderer;
import thebetweenlands.client.renderer.block.DecayPitHangingChainRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.RootGrabber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootGrabberRenderer extends EntityRenderer<RootGrabber> {

	public final ModelPart chainModel;
	private final Map<Integer, List<RootPart>> modelParts = new HashMap<>();

	public RootGrabberRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.chainModel = context.bakeLayer(BLModelLayers.DECAY_PIT_CHAIN);
	}

	@Override
	public void render(RootGrabber entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
		this.initRootModels(entity);

		if (!this.modelParts.isEmpty()) {
			stack.pushPose();

			for (RootPart part : this.modelParts.get(entity.getId())) {
				stack.pushPose();

				stack.translate(part.x, part.y, part.z);

				stack.mulPose(Axis.YP.rotationDegrees(part.yaw - 90));
				stack.mulPose(Axis.XP.rotationDegrees(part.pitch));

				stack.translate(0, entity.getRootYOffset(partialTick), 0);

				if (!entity.isChains()) {
					float animationTicks = entity.tickCount + partialTick;
					stack.mulPose(Axis.ZP.rotationDegrees((float) Math.cos(animationTicks / 4) * 0.5F));
					stack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(animationTicks / 5) * 0.8F));
				}

				part.render(stack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(part.texture)), packedLight, OverlayTexture.NO_OVERLAY, -1);

				int damage = Mth.ceil(entity.getDamage() * 10.0F);
				if (damage > 0 && damage <= 10) {
					part.render(stack, new SheetedDecalTextureGenerator(Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(damage - 1)), stack.last(), 1.0F), packedLight, OverlayTexture.NO_OVERLAY, -1);
				}

				stack.popPose();
			}

			stack.popPose();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(RootGrabber entity) {
		return SpikeParticle.ROOT_TEXTURE;
	}

	private void initRootModels(RootGrabber entity) {
		if (!this.modelParts.containsKey(entity.getId())) {
			List<RootPart> parts = new ArrayList<>();
			int rings = 2 + entity.level().getRandom().nextInt(2);

			for (int j = 0; j < rings; j++) {
				float radius = (entity.getBbWidth() - 0.5F) / rings * j;
				int roots = entity.isChains() ? (2 + entity.level().getRandom().nextInt(3)) : (5 + entity.level().getRandom().nextInt(5));

				for (int i = 0; i < roots; i++) {
					float scale = 0.6F + entity.getRandom().nextFloat() * 0.2F;
					double angle = i * Math.PI * 2 / roots;

					Vec3 offset = new Vec3(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);

					RootPart part;

					if (!entity.isChains()) {
						final SpikeRenderer renderer = new SpikeRenderer(3, scale * 0.5F, scale, 1, entity.getRandom().nextLong(), -scale * 0.5F * 1.5F, 0, -scale * 0.5F * 1.5F);

						part = new RootPart(SpikeParticle.ROOT_TEXTURE) {
							@Override
							public void render(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
								renderer.render(stack.last(), consumer, light, overlay, color);
							}
						};

						part.pitch = 30.0F;
					} else {
						final float[] yaws = new float[5];
						final float[] pitches = new float[5];

						for (int k = 0; k < 5; k++) {
							yaws[k] = (entity.getRandom().nextFloat() - 0.5F) * 360.0F;
							pitches[k] = (entity.getRandom().nextFloat() - 0.5F) * 40.0F;
						}

						part = new RootPart(DecayPitHangingChainRenderer.TEXTURE) {
							@Override
							public void render(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
								final float scale = 0.3F;

								stack.pushPose();
								stack.scale(scale, scale, scale);

								for (int w = 0; w < 5; w++) {
									stack.translate(0, 1, 0);
									stack.mulPose(Axis.YP.rotationDegrees(yaws[w]));
									stack.mulPose(Axis.XP.rotationDegrees(pitches[w]));
									stack.translate(0, -1, 0);

									RootGrabberRenderer.this.chainModel.render(stack, consumer, light, overlay, color);

									stack.translate(0, 1, 0);
								}

								stack.popPose();
							}
						};

						part.pitch = 15.0F;
					}

					part.x = (float) offset.x;
					part.y = (float) offset.y;
					part.z = (float) offset.z;
					part.yaw = -(float) Math.toDegrees(angle);

					parts.add(part);
				}
			}
			this.modelParts.put(entity.getId(), parts);
		}
	}

	public static class RootPart {
		public float x, y, z;
		public float yaw, pitch;
		private final ResourceLocation texture;

		public RootPart(ResourceLocation texture) {
			this.texture = texture;
		}

		public void render(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {}
	}
}
