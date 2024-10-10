package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.GreeblingModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Greebling;

public class GreeblingRenderer extends MobRenderer<Greebling, GreeblingModel> {

	private static final ResourceLocation TEXTURE_1 = TheBetweenlands.prefix("textures/entity/greebling_0.png");
	private static final ResourceLocation TEXTURE_2 = TheBetweenlands.prefix("textures/entity/greebling_1.png");
	private final GreeblingModel variant1 = this.getModel();
	private final GreeblingModel variant2;

	public GreeblingRenderer(EntityRendererProvider.Context context) {
		super(context, new GreeblingModel(context.bakeLayer(BLModelLayers.GREEBLING_1)), 0.0F);
		this.variant2 = new GreeblingModel(context.bakeLayer(BLModelLayers.GREEBLING_2));
	}

	@Override
	public void render(Greebling entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {
		this.model = entity.getGreeblingType() == 0 ? this.variant1 : this.variant2;
		float disappearFrame = entity.disappearTimer > 0 ? (float) Math.pow(entity.disappearTimer / 8f, 4) : 0;
		float scaleXZ = 1 - disappearFrame;
		float scaleY = 1 + 0.1F * disappearFrame;
		stack.scale(scaleXZ, scaleY, scaleXZ);
		super.render(entity, entityYaw, partialTicks, stack, buffer, light);
		stack.scale(1 / scaleXZ, 1 / scaleY, 1 / scaleXZ);
		if (this.model.getCup() != null) {
			stack.pushPose();
			stack.scale(-1.0F, -1.0F, 1.0F);
			stack.translate(0.0F, -1.501F, 0.0F);
			this.model.getCup().render(stack, buffer.getBuffer(this.model.renderType(TEXTURE_2)), light, OverlayTexture.NO_OVERLAY, -1);
			stack.popPose();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Greebling entity) {
		return entity.getGreeblingType() == 0 ? TEXTURE_1 : TEXTURE_2;
	}
}
