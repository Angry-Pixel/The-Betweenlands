package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
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
	private final ItemRenderer itemRenderer;

	public CrabPotFilterRenderer(BlockEntityRendererProvider.Context context) {
		this.filter = context.bakeLayer(BLModelLayers.CRAB_POT_FILTER);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(CrabPotFilterBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		Direction dir = entity.getBlockState().getValue(CrabPotFilterBlock.FACING);
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
		stack.pushPose();
		stack.scale(1.0F, -1.0F, -1.0F);
		this.filter.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (entity.getLevel() != null) {
			// input rendering
			if (!entity.getItem(0).isEmpty() && entity.getPottedCrab(entity.getLevel(), entity.getBlockPos()) != null) {
				stack.pushPose();
				stack.translate(0.0F, 1.25D - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.000625D), 0.25F);
				stack.pushPose();
				stack.scale(0.5F - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.0025F), 0.5F - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.0025F), 0.5F - (entity.getFilteringAnimationScaled(200, partialTicks) * 0.0025F));
				stack.mulPose(Axis.XP.rotationDegrees((entity.getFilteringAnimationScaled(200, partialTicks) * 2F)));
				stack.mulPose(Axis.ZP.rotationDegrees((entity.getFilteringAnimationScaled(200, partialTicks) * 2F)));
				this.itemRenderer.renderStatic(entity.getItem(0), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
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
					this.itemRenderer.renderStatic(entity.getItem(2), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				}
				stack.popPose();
			}
		}
		stack.popPose();
	}
}
