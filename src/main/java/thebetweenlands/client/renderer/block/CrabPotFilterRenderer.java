package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.CrabPotFilterBlock;
import thebetweenlands.common.block.entity.CrabPotFilterBlockEntity;

public class CrabPotFilterRenderer implements BlockEntityRenderer<CrabPotFilterBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/crab_pot_filter.png"));
	private final ModelPart filter;

	public CrabPotFilterRenderer(BlockEntityRendererProvider.Context context) {
		this.filter = context.bakeLayer(BLModelLayers.CRAB_POT_FILTER);
	}

	@Override
	public void render(CrabPotFilterBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(CrabPotFilterBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.filter.render(stack, source.getBuffer(TEXTURE), LightTexture.FULL_BLOCK, overlay);
		stack.popPose();

		if (entity.getLevel() != null) {
			// input rendering
			if (!entity.getItem(1).isEmpty() && entity.isActive() && entity.hasBait() && entity.getSlotProgress() > 0) {
				stack.pushPose();
				stack.translate(0.0D, 1.25D - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.000625D), 0.0D);
				stack.pushPose();
				stack.scale(0.5F - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.0025F), 0.5F - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.0025F), 0.5F - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.0025F));
				stack.mulPose(Axis.XP.rotationDegrees((entity.getFilteringAnimationScaled(200, partialTicks) * 2F)));
				stack.mulPose(Axis.ZP.rotationDegrees((entity.getFilteringAnimationScaled(200, partialTicks) * 2F)));
				Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(1), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
				stack.popPose();
				stack.popPose();
			}

			// result rendering
			if (!entity.getItem(2).isEmpty()) {
				RandomSource random = RandomSource.create(entity.getBlockPos().asLong());
				stack.pushPose();
				double yUp = 0.25D;
				if (entity.getItem(2).getItem() instanceof BlockItem)
					yUp = 0.29D;
				stack.translate(0.0D, yUp, 0.0D);
				int items = entity.getItem(2).getCount() / 2 + 1;
				for (int i = 0; i < items; i++) {
					stack.pushPose();
					stack.translate(random.nextDouble() / 2.0D - 1.0D / 4.0D, 0.0D, random.nextDouble() / 2.0D - 1.0D / 4.0D);
					stack.mulPose(Axis.XP.rotationDegrees((float) (random.nextDouble() * 30.0F - 15.0F)));
					stack.mulPose(Axis.ZP.rotationDegrees((float) (random.nextDouble() * 30.0F - 15.0F)));
					stack.scale(0.25F, 0.25F, 0.25F);
					stack.mulPose(Axis.XP.rotationDegrees(90.0F));
					stack.mulPose(Axis.ZP.rotationDegrees((float) (random.nextDouble() * 360.0F)));
					Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(1), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				}
				stack.popPose();
			}
		}
	}
}
