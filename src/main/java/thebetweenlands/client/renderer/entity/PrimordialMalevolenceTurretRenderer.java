package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.WightModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceTurret;

public class PrimordialMalevolenceTurretRenderer extends EntityRenderer<PrimordialMalevolenceTurret> {

	private final WightModel<PrimordialMalevolenceTurret> model;

	public PrimordialMalevolenceTurretRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new WightModel<PrimordialMalevolenceTurret>(context.bakeLayer(BLModelLayers.WIGHT)).setRenderHeadOnly(true);
	}

	@Override
	public void render(PrimordialMalevolenceTurret entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));
		stack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
		stack.translate(0, 0, 0.25D);
		stack.translate(Math.sin((entity.tickCount + partialTick) / 5.0D) * 0.1F, Math.cos((entity.tickCount + partialTick) / 7.0D) * 0.1F, Math.cos((entity.tickCount + partialTick) / 6.0D) * 0.1F);
		int color;
		if (!entity.isObstructedByBoss()) {
			color = FastColor.ARGB32.colorFromFloat(0.8F, 1.0F, 1.0F, 1.0F);
		} else {
			color = FastColor.ARGB32.colorFromFloat(0.8F, 1.0F, 0.4F, 0.4F);
		}
		stack.mulPose(Axis.XP.rotation(0.4F));
		this.model.prepareMobModel(entity, 0.0F, 0.0F, partialTick);
		this.model.renderToBuffer(stack, buffer.getBuffer(BLRenderTypes.translucentCulling(this.getTextureLocation(entity))), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity))), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialMalevolenceTurret entity) {
		return WightRenderer.TEXTURE;
	}
}
