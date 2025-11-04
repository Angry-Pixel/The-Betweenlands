package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.GreeblingCorpseModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.GreeblingCorpse;

public class GreeblingCorpseRenderer extends EntityRenderer<GreeblingCorpse> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/greebling_corpse.png");

	private final GreeblingCorpseModel model;

	public GreeblingCorpseRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new GreeblingCorpseModel(context.bakeLayer(BLModelLayers.GREEBLING_CORPSE));
	}

	@Override
	public void render(GreeblingCorpse entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		stack.pushPose();
		stack.translate(0.0D, 1.5D, 0.0D);
		stack.mulPose(Axis.YP.rotationDegrees(entity.rotation));
		stack.scale(1.0F, -1.0F, -1.0F);
		float alpha = entity.fadeTimer > 0 ? 1.0F - Math.min(1.0F, (entity.fadeTimer - 1 + partialTick) / 40.0F) : 1.0F;
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
		stack.popPose();

		super.render(entity, entityYaw, partialTick, stack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(GreeblingCorpse entity) {
		return TEXTURE;
	}
}
