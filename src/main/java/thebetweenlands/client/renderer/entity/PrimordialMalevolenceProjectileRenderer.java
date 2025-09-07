package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SwordEnergyModel;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceProjectile;

public class PrimordialMalevolenceProjectileRenderer extends EntityRenderer<PrimordialMalevolenceProjectile> {

	private final SwordEnergyModel model;

	public PrimordialMalevolenceProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SwordEnergyModel(context.bakeLayer(BLModelLayers.SWORD_ENERGY));
	}

	@Override
	public void render(PrimordialMalevolenceProjectile entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		float ticks = entity.tickCount + partialTick;
		stack.pushPose();
		stack.translate(0.0D, -0.3D, 0.0D);
		float uOffset = ticks * 0.01F;
		float vOffset = ticks * 0.01F;
		stack.scale(0.6F, 0.6F, 0.6F);
		int color;
		if (!entity.isDeflectable()) {
			color = FastColor.ARGB32.colorFromFloat(1.0F, 0.8F, 0.6F, 0.4F);
		} else {
			color = FastColor.ARGB32.colorFromFloat(1.0F, 0.15F, 1.0F, 0.35F);
		}
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.energySwirl(this.getTextureLocation(entity), uOffset, vOffset)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialMalevolenceProjectile entity) {
		return PrimordialMalevolenceRenderer.SHIELD_TEXTURE;
	}
}
