package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import thebetweenlands.client.particle.SpikeParticle;
import thebetweenlands.client.renderer.SpikeRenderer;
import thebetweenlands.common.entity.SpikeWave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpikeWaveRenderer extends EntityRenderer<SpikeWave> {

	private final Map<Integer, Map<BlockPos, List<SpikeRenderer>>> modelParts = new HashMap<>();

	public SpikeWaveRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(SpikeWave entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
		this.initRootModels(entity);

		if (!this.modelParts.isEmpty()) {
			stack.pushPose();

			for (List<SpikeRenderer> renderers : this.modelParts.get(entity.getId()).values()) {
				for (SpikeRenderer renderer : renderers) {
					renderer.render(stack.last(), bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, -1);
				}
			}
			stack.popPose();
		}

		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() && !entity.positions.isEmpty()) {
			for (BlockPos spikePos : entity.positions) {
				LevelRenderer.renderLineBox(stack, bufferSource.getBuffer(RenderType.debugLineStrip(4.0F)), new AABB(spikePos).deflate(0.0D, 0.75D, 0.0D), 1.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(SpikeWave entity) {
		return SpikeParticle.ROOT_TEXTURE;
	}

	private void initRootModels(SpikeWave entity) {
		if (!this.modelParts.containsKey(entity.getId())) {
			Map<BlockPos, List<SpikeRenderer>> parts = new HashMap<>();
			for (BlockPos pos : entity.positions) {
				int models = 1 + entity.getRandom().nextInt(2);
				List<SpikeRenderer> renderers = new ArrayList<>();
				for (int i = 0; i < models; i++) {
					Vector3f offset = new Vector3f(
						(float) (pos.getX() + entity.getRandom().nextFloat() * 0.6F - 0.3F - entity.getX()),
						(float) (pos.getY() + 0.25F - entity.getY()),
						(float) (pos.getZ() + entity.getRandom().nextFloat() * 0.6F - 0.3F - entity.getZ())
					);
					float scale = 0.4F + entity.getRandom().nextFloat() * 0.2F;
					SpikeRenderer renderer = new SpikeRenderer(2, scale * 0.5F, scale, 1, entity.getRandom().nextLong(), offset.x, offset.y, offset.z);
					renderers.add(renderer);
				}

				parts.put(pos, renderers);
			}
			this.modelParts.put(entity.getId(), parts);
		}
	}
}
