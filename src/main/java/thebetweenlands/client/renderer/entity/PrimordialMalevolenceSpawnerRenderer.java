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
import thebetweenlands.client.model.entity.WightModel;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceSpawner;

public class PrimordialMalevolenceSpawnerRenderer extends EntityRenderer<PrimordialMalevolenceSpawner> {

	private final WightModel<PrimordialMalevolenceSpawner> model;

	public PrimordialMalevolenceSpawnerRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new WightModel<>(context.bakeLayer(BLModelLayers.WIGHT));
	}

	@Override
	public void render(PrimordialMalevolenceSpawner entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.translate(0.0D, 1.338F, 0.0D);
		stack.scale(0.9F, 0.9F, 0.9F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		float progress = 1.0F - (float)entity.spawnDelay / 20.0F;
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(progress, 1.0F, 1.0F, 1.0F));
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialMalevolenceSpawner entity) {
		return WightRenderer.TEXTURE;
	}
}
