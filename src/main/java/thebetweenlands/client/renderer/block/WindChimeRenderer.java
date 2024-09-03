package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.WindChimeModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.WindChimeBlockEntity;

public class WindChimeRenderer implements BlockEntityRenderer<WindChimeBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/wind_chime.png"));
	private final WindChimeModel chime;

	public WindChimeRenderer(BlockEntityRendererProvider.Context context) {
		this.chime = new WindChimeModel(context.bakeLayer(BLModelLayers.WIND_CHIME));
	}

	@Override
	public void render(WindChimeBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.chime.renderWithAnimation(stack, source.getBuffer(TEXTURE), light, overlay, entity.renderTicks + partialTicks, Math.min((entity.prevChimeTicks + (entity.chimeTicks - entity.prevChimeTicks) * partialTicks) / 100.0F, 1.25F));
		stack.popPose();

//		ParticleBatch batch = entity.getParticleBatch();
//		if (batch != null) {
//			BatchedParticleRenderer.INSTANCE.renderBatch(batch, Minecraft.getInstance().cameraEntity, partialTicks);
//		}
	}
}
