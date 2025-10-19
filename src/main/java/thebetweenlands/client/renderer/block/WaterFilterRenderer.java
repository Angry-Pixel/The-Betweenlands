package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.InfuserBlock;
import thebetweenlands.common.block.container.RubberTapBlock;
import thebetweenlands.common.block.entity.WaterFilterBlockEntity;
import thebetweenlands.util.RenderUtils;

import java.util.SplittableRandom;

public class WaterFilterRenderer implements BlockEntityRenderer<WaterFilterBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/water_filter.png"));
	private final ModelPart filter;
	private final ItemRenderer itemRenderer;

	public WaterFilterRenderer(BlockEntityRendererProvider.Context context) {
		this.filter = context.bakeLayer(BLModelLayers.WATER_FILTER);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(WaterFilterBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		float fluidLevel = entity.tank.getFluidAmount();
		SplittableRandom rand = new SplittableRandom(entity.getBlockPos().asLong());

		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.filter.render(stack, buffer.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (fluidLevel > 0) {
			FluidStack fluid = entity.tank.getFluid();
			TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
			TextureAtlasSprite flowing = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getFlowingTexture());
			int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid) | 0xFF000000;
			stack.pushPose();
			float textureYPos = (0.45F * ((float) entity.tank.getFluidAmount() / entity.tank.getCapacity()));
			RenderUtils.renderTopQuad(stack.last(), buffer.getBuffer(RenderType.entityTranslucent(still.atlasLocation())), light, color, 0.125F, 0.875F, 0.5F + textureYPos, 0.125F, 0.875F, still.getU0(), still.getU1(), still.getV0(), still.getV1());
			stack.popPose();

			if(entity.getFluidAnimation()) {
				RenderUtils.renderCuboid(stack.last(), buffer.getBuffer(RenderType.entityTranslucent(flowing.atlasLocation())), light, color, still, 0.45F, 0.55F, 0.0F, 0.4F, 0.45F, 0.55F);
				if (entity.hasMossFilter() || entity.hasSilkFilter())
					RenderUtils.renderCuboid(stack.last(), buffer.getBuffer(RenderType.entityTranslucent(still.atlasLocation())), light, color, still, 0.3F, 0.7F, 0.31F, 0.315F, 0.3F, 0.7F);
			}
		}

		if (!entity.getItem(0).isEmpty()) {
			stack.pushPose();
			double yUp = 0.28125D;
			stack.translate(0.5D, yUp, 0.5D);
			stack.mulPose(Axis.XP.rotationDegrees(180));
			stack.pushPose();
			stack.scale(0.5F, 1.0F, 0.5F);
			stack.mulPose(Axis.XP.rotationDegrees(90));
			this.itemRenderer.renderStatic(entity.getItem(0), ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
			stack.popPose();
			stack.popPose();
		}

		for (ItemStack item : entity.getItems().subList(1, entity.getContainerSize())) {
			if (!item.isEmpty()) {
				double yUp = 0.3125D;
				int items = item.getCount() / 4 + 1;
				stack.pushPose();
				stack.translate(0.5D, yUp, 0.5D);
				stack.mulPose(Axis.XP.rotationDegrees(180));
				for (int i = 0; i < items; i++) {
					stack.pushPose();
					stack.translate(rand.nextDouble() * 0.25D - 0.125D, 0.0D, rand.nextDouble() * 0.25D - 0.125D);
					stack.mulPose(Axis.XP.rotationDegrees(rand.nextFloat() * 30.0F - 15.0F));
					stack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 30.0F - 15.0F));
					stack.scale(0.125F, 0.125F, 0.125F);
					stack.mulPose(Axis.XP.rotationDegrees(90));
					stack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 360.0F));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
					stack.popPose();
				}
				stack.popPose();
			}
		}
	}
}
