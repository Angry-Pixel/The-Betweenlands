package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.tile.TileEntityCenser;

@SideOnly(Side.CLIENT)
public class GuiCenser extends GuiContainer {
	private TileEntityCenser censer;
	private static final ResourceLocation CENSER_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/censer.png");

	public GuiCenser(InventoryPlayer inv, TileEntityCenser tile) {
		super(new ContainerCenser(inv, tile));
		this.ySize = 256;
		this.censer = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(CENSER_GUI_TEXTURE);
		int xx = (width - xSize) / 2;
		int yy = (height - ySize) / 2;
		drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);

		/*int water = purifier.getScaledWaterAmount(60);
        drawTexturedModalRect(xx + 34, yy + 72 - water, 176, 74 - water, 11, water);

        if (purifier.isPurifying()) {
            int count = purifier.getPurifyingProgress();
            drawTexturedModalRect(xx + 62, yy + 36 + count, 176, count, 12, 14 - count);
            drawTexturedModalRect(xx + 84, yy + 34, 176, 74, count * 2, 16);
        }*/
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);

		Slot internalSlot = this.inventorySlots.getSlot(ContainerCenser.SLOT_INTERNAL);
		ItemStack internalStack = internalSlot.getStack();
		if(!internalStack.isEmpty()) {
			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;


			GlStateManager.enableDepth();

			this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, internalStack, this.guiLeft + internalSlot.xPos, this.guiTop + internalSlot.yPos);
			this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, internalStack, this.guiLeft + internalSlot.xPos, this.guiTop + internalSlot.yPos, null);

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;

			if(this.isPointInRegion(internalSlot.xPos, internalSlot.yPos, 16, 16, mouseX, mouseY)) {
				this.renderToolTip(internalStack, mouseX, mouseY);
			}
		}

		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
