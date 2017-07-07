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
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelItemShelf;
import thebetweenlands.common.block.container.BlockItemShelf;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.util.TileEntityHelper;

public class RenderItemShelf extends TileEntitySpecialRenderer<TileEntityItemShelf> {
	public static final ModelItemShelf MODEL = new ModelItemShelf();
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/item_shelf.png");

	protected final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

	@Override
	public void renderTileEntityAt(TileEntityItemShelf te, double x, double y, double z, float partialTicks, int destroyStage) {
		double unitPixel = 0.0625D;
		double offSetX = 0;
		double offSetZ = 0;
		double offSetXX = 0;
		double offSetZZ = 0;
		float rotation = 0;

		this.bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.enableRescaleNormal();

		EnumFacing facing = EnumFacing.NORTH;

		boolean isItem = true;

		if(te != null && te.hasWorld()) {
			isItem = false;
			facing = TileEntityHelper.getStatePropertySafely(te, BlockItemShelf.class, BlockItemShelf.FACING, EnumFacing.NORTH);
		}

		switch (facing) {
		default:
		case NORTH:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			rotation = 180F;
			offSetX = unitPixel * 3.75;
			offSetZ = unitPixel * 3.75;
			offSetXX = unitPixel * 12;
			offSetZZ = unitPixel * 3.75;
			break;
		case SOUTH:
			GlStateManager.rotate(180F, 0.0F, 1F, 0F);
			rotation = 0F;
			offSetX = unitPixel * 12;
			offSetZ = unitPixel * 12;
			offSetXX = unitPixel * 3.75;
			offSetZZ = unitPixel * 12;
			break;
		case EAST:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			rotation = 90F;
			offSetX = unitPixel * 12;
			offSetZ = unitPixel * 3.75;
			offSetXX = unitPixel * 12;
			offSetZZ = unitPixel * 12;
			break;
		case WEST:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			rotation = -90F;
			offSetX = unitPixel * 3.75;
			offSetZ = unitPixel * 12;
			offSetXX = unitPixel * 3.75;
			offSetZZ = unitPixel * 3.75;
			break;
		}

		if(isItem) {
			GlStateManager.translate(0.0D, 0.0D, -0.25D);
		}

		MODEL.render();

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();

		if(te != null) {
			this.renderItemInSlot(te, 0, x + offSetX, y + unitPixel * 11, z + offSetZ, rotation);
			this.renderItemInSlot(te, 1, x + offSetXX, y + unitPixel * 11, z + offSetZZ, rotation);
			this.renderItemInSlot(te, 2, x + offSetX, y + unitPixel * 3, z + offSetZ, rotation);
			this.renderItemInSlot(te, 3, x + offSetXX, y + unitPixel * 3, z + offSetZZ, rotation);
		}
	}

	protected void renderItemInSlot(TileEntityItemShelf te, int slot, double x, double y, double z, float rotation) {
		ItemStack stack = te.getStackInSlot(slot);

		if (stack != null) {
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(rotation, 0.0F, 1F, 0F);
			GlStateManager.scale(0.4F, 0.4F, 0.4F);
			RenderHelper.enableStandardItemLighting();

			if (!this.renderItem.shouldRenderItemIn3D(stack) || stack.getItem() instanceof ItemSkull) {
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			}

			this.renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

			RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
	}
}
