package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ChiromawEggModel;
import thebetweenlands.client.model.entity.ChiromawHatchlingModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawHatchling;

public class ChiromawHatchlingRenderer extends MultiPieceMobModelRenderer<ChiromawHatchling, ChiromawHatchlingModel> {

	private static final ResourceLocation TEXTURE_HATCHLING = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_hatchling.png");
	private static final ResourceLocation TEXTURE_HATCHLING_BLINK_1 = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_hatchling_blink_1.png");
	private static final ResourceLocation TEXTURE_HATCHLING_BLINK_2 = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_hatchling_blink_2.png");
	private static final ResourceLocation TEXTURE_HATCHLING_LIGHTNING = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_hatchling_lightning.png");
	private static final ResourceLocation TEXTURE_HATCHLING_LIGHTNING_BLINK_1 = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_hatchling_lightning_blink_1.png");
	private static final ResourceLocation TEXTURE_HATCHLING_LIGHTNING_BLINK_2 = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_hatchling_lightning_blink_2.png");
	private static final ResourceLocation TEXTURE_EGG = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_egg.png");
	private final ChiromawEggModel egg;

	public ChiromawHatchlingRenderer(EntityRendererProvider.Context context) {
		super(context, new ChiromawHatchlingModel(context.bakeLayer(BLModelLayers.CHIROMAW_HATCHLING)), 0.2F);
		this.egg = new ChiromawEggModel(context.bakeLayer(BLModelLayers.CHIROMAW_EGG));
	}

	@Override
	protected void renderModel(ChiromawHatchling entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state) {
		RenderType type = this.getRenderType(entity, state.visible(), state.translucent(), state.glowing());
		if (type != null) {
			if (entity.getHasHatched()) {
				float eggFade = Mth.lerp(state.partialTick(), entity.prevTransformTick, entity.getTransformCount());

				if (entity.getTransformCount() > 0) {
					RenderSystem.depthMask(false);
				}

				this.getModel().renderEgg(stack, buffer.getBuffer(type), packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(FastColor.as8BitChannel(1F - eggFade * 0.02F), color));

				RenderSystem.depthMask(true);

				float smootherRise = Mth.lerp(state.partialTick(), entity.prevRise, entity.getRiseCount());
				float flap = Mth.sin(state.ageInTicks() * 0.5F) * 0.15F;
				if (!entity.getIsTransforming())
					flap = 0F;

				stack.pushPose();
				stack.translate(0.0F, 0.5F - smootherRise * 0.0125F - eggFade * 0.01F - flap * 0.5F, 0.0F);
				stack.translate(0.0F, 0.0F, 0.2F - smootherRise * 0.00625F);
				this.getModel().renderBaby(stack, buffer.getBuffer(type), packedLight, overlay, color);
				stack.popPose();
			} else {
				float flap = Mth.sin((entity.hatchAnimation + state.partialTick()) * 0.125F) * 0.03125F * 1F / 60F * entity.getHatchTick();
				if (entity.getHatchTick() < 1)
					flap = 0F;
				stack.pushPose();
				stack.scale(1.0F + flap, 1F - flap, 1.0F + flap);
				stack.translate(0.0D, flap * 2.0D, 0.0D);
				this.egg.renderToBuffer(stack, buffer.getBuffer(type), packedLight, OverlayTexture.NO_OVERLAY, color);
				stack.popPose();
			}

			if (entity.getIsHungry() && entity.getRiseCount() > 0) {
				stack.pushPose();

				float smoothRise = Mth.lerp(state.partialTick(), entity.prevRise, entity.getRiseCount());
				float scale = (0.25F + (Mth.sin(state.ageInTicks() * 0.125F) * 0.0625F)) * smoothRise / ChiromawHatchling.MAX_RISE;
				if (!entity.getFoodCraved().isEmpty()) {
					stack.mulPose(Axis.YP.rotationDegrees(-state.yRot() + 180));
					stack.translate(0.0D, 1.0D - smoothRise * 0.025D, 0.0D);
					stack.scale(scale, -scale, -scale);
					Quaternionf camera = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
					stack.mulPose(new Quaternionf(0.0F, camera.y, 0.0F, camera.w));
					Minecraft.getInstance().getItemRenderer().renderStatic(entity.getFoodCraved(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, stack, buffer, null, entity.getId());

					if (entity.getFoodCraved().getCount() != 1) {
						stack.pushPose();
						String s = String.valueOf(entity.getFoodCraved().getCount());
						stack.translate(0.1D, 0.1D, 0.0D);
						stack.scale(-0.05F, -0.05F, 0.05F);

						Minecraft.getInstance().font.drawInBatch(s, 17.0F - Minecraft.getInstance().font.width(s), 9.0F, -1, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
						stack.popPose();
					}
				}
				stack.popPose();
			}
		}
	}

	@Override
	protected float getFlipDegrees(ChiromawHatchling entity) {
		return 0.0F;
	}

	@Override
	public ResourceLocation getTextureLocation(ChiromawHatchling entity) {
		if (entity.getHasHatched()) {
			if (entity.getElectricBoogaloo()) {
				if (entity.blinkCount <= 10 && entity.blinkCount > 8 || entity.blinkCount <= 4 && entity.blinkCount > 2)
					return TEXTURE_HATCHLING_LIGHTNING_BLINK_1;
				if (entity.blinkCount <= 8 && entity.blinkCount > 6 || entity.blinkCount <= 2 && entity.blinkCount > 0)
					return TEXTURE_HATCHLING_LIGHTNING_BLINK_2;
				return TEXTURE_HATCHLING_LIGHTNING;
			} else {
				if (entity.blinkCount <= 10 && entity.blinkCount > 8 || entity.blinkCount <= 4 && entity.blinkCount > 2)
					return TEXTURE_HATCHLING_BLINK_1;
				if (entity.blinkCount <= 8 && entity.blinkCount > 6 || entity.blinkCount <= 2 && entity.blinkCount > 0)
					return TEXTURE_HATCHLING_BLINK_2;
				return TEXTURE_HATCHLING;
			}
		}
		return TEXTURE_EGG;
	}
}
