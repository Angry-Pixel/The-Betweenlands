package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SludgeModel;
import thebetweenlands.client.model.entity.SmolSludgeModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Sludge;
import thebetweenlands.common.entity.monster.SmolSludge;
import thebetweenlands.common.world.event.SpoopyEvent;

public class SmolSludgeRenderer extends MobRenderer<SmolSludge, SmolSludgeModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/smol_sludge.png");
	private final BlockState pumpkin = Blocks.JACK_O_LANTERN.defaultBlockState();

	public SmolSludgeRenderer(EntityRendererProvider.Context context) {
		super(context, new SmolSludgeModel(context.bakeLayer(BLModelLayers.SMOL_SLUDGE)), 0.0F);
		this.addLayer(new SmolSludgeOuterLayer(this));
	}

	@Override
	protected void scale(SmolSludge entity, PoseStack stack, float partialTick) {
		float squishFactor = entity.getSquishFactor(partialTick) / 1.5F;
		float scale = 1.0F / (squishFactor + 1.0F);

		float squish = entity.scale.getAnimationProgressSinSqrt(partialTick);
		stack.scale(squish, squish, squish);
		stack.translate(0, (1.0F - entity.scale.getAnimationProgressSin(partialTick)) * 2.5F, 0);
		stack.scale(scale, 1.0F / scale, scale);

		if (SpoopyEvent.isSpoooopy(entity.level())) {
			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(90F));
			stack.translate(0.37F, -0.13F, 0.37F);
			stack.scale(-scale * 0.65F, -1.0F / scale * 0.65F, scale * 0.65F);
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(this.pumpkin, stack, Minecraft.getInstance().renderBuffers().bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
			stack.popPose();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(SmolSludge entity) {
		return TEXTURE;
	}

	public static class SmolSludgeOuterLayer extends RenderLayer<SmolSludge, SmolSludgeModel> {

		public SmolSludgeOuterLayer(RenderLayerParent<SmolSludge, SmolSludgeModel> renderer) {
			super(renderer);
		}

		public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, SmolSludge entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			Minecraft minecraft = Minecraft.getInstance();
			boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
			if (!entity.isInvisible() || flag) {
				VertexConsumer vertexconsumer;
				if (flag) {
					vertexconsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
				} else {
					vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
				}

				this.getParentModel().prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
				this.getParentModel().setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
				this.getParentModel().renderSlime(stack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
			}
		}
	}
}
