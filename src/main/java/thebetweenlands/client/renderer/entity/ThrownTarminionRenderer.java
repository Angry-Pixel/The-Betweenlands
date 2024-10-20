package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.TarminionModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.ThrownTarminion;

public class ThrownTarminionRenderer extends EntityRenderer<ThrownTarminion> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/tarminion.png");
	private final TarminionModel model;

	public ThrownTarminionRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new TarminionModel(context.bakeLayer(BLModelLayers.TARMINION));
	}

	@Override
	public void render(ThrownTarminion entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource source, int light) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(entityYaw));
		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.translate(0.0F, -1.501F, 0.0F);
		this.model.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();
		super.render(entity, entityYaw, partialTick, stack, source, light);
	}

	@Override
	public ResourceLocation getTextureLocation(ThrownTarminion entity) {
		return TEXTURE;
	}
}
