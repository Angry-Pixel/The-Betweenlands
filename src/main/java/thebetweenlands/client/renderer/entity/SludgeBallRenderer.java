package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.MummyArmModel;
import thebetweenlands.client.model.entity.SludgeBallModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.AnimatorBlock;
import thebetweenlands.common.entity.monster.MummyArm;
import thebetweenlands.common.entity.projectile.SludgeBall;
import thebetweenlands.common.registries.BlockRegistry;

public class SludgeBallRenderer extends EntityRenderer<SludgeBall> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/sludge.png");
	private final SludgeBallModel model;

	public SludgeBallRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SludgeBallModel(context.bakeLayer(BLModelLayers.SLUDGE_BALL));
	}

	@Override
	public void render(SludgeBall entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.scale(0.5F, 0.5F, 0.5F);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(SludgeBall entity) {
		return TEXTURE;
	}
}
