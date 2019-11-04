package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thebetweenlands.common.block.container.BlockItemShelf;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.util.StatePropertyHelper;

public class RenderItemShelf extends TileEntitySpecialRenderer<TileEntityItemShelf> {
	@Override
	public void render(TileEntityItemShelf te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(te != null) {
			double unitPixel = 0.0625D;
			double offSetX = 0;
			double offSetZ = 0;
			double offSetXX = 0;
			double offSetZZ = 0;
			float rotation = 0;

			switch (StatePropertyHelper.getStatePropertySafely(te, BlockItemShelf.class, BlockItemShelf.FACING, EnumFacing.NORTH)) {
			default:
			case NORTH:
				rotation = 0F;
				offSetX = unitPixel * 3.75;
				offSetZ = unitPixel * 3.75;
				offSetXX = unitPixel * 12;
				offSetZZ = unitPixel * 3.75;
				break;
			case SOUTH:
				rotation = 180F;
				offSetX = unitPixel * 12;
				offSetZ = unitPixel * 12;
				offSetXX = unitPixel * 3.75;
				offSetZZ = unitPixel * 12;
				break;
			case EAST:
				rotation = -90F;
				offSetX = unitPixel * 12;
				offSetZ = unitPixel * 3.75;
				offSetXX = unitPixel * 12;
				offSetZZ = unitPixel * 12;
				break;
			case WEST:
				rotation = 90F;
				offSetX = unitPixel * 3.75;
				offSetZ = unitPixel * 12;
				offSetXX = unitPixel * 3.75;
				offSetZZ = unitPixel * 3.75;
				break;
			}

			this.renderItemInSlot(te, 0, x + offSetX, y + unitPixel * 11, z + offSetZ, rotation);
			this.renderItemInSlot(te, 1, x + offSetXX, y + unitPixel * 11, z + offSetZZ, rotation);
			this.renderItemInSlot(te, 2, x + offSetX, y + unitPixel * 3, z + offSetZ, rotation);
			this.renderItemInSlot(te, 3, x + offSetXX, y + unitPixel * 3, z + offSetZZ, rotation);
		}
	}

	protected void renderItemInSlot(TileEntityItemShelf te, int slot, double x, double y, double z, float rotation) {
		ItemStack stack = te.getStackInSlot(slot);

		if (!stack.isEmpty()) {
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(rotation, 0.0F, 1F, 0F);
			GlStateManager.scale(0.4F, 0.4F, 0.4F);
			RenderHelper.enableStandardItemLighting();

			if (!renderItem.shouldRenderItemIn3D(stack) || stack.getItem() instanceof ItemSkull) {
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			}

			renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

			RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
	}
}
