package thebetweenlands.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
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

		FluidStack fluidStack = this.censer.getTankProperties()[0].getContents();
		if(fluidStack != null) {
			ResourceLocation fluidTexture = fluidStack.getFluid().getStill();
			if(fluidTexture != null) {
				TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());

				this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				this.zLevel = 100.0F;
				this.itemRender.zLevel = 100.0F;

				GlStateManager.disableLighting();

				GlStateManager.enableRescaleNormal();
				GlStateManager.enableAlpha();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				this.zLevel = 90.0F;

				this.drawTexturedModalRect(this.guiLeft + internalSlot.xPos - 2, this.guiTop + internalSlot.yPos - 2, sprite, 20, 20);

				//Render cover
				this.mc.getTextureManager().bindTexture(CENSER_GUI_TEXTURE);
				this.drawTexturedModalRect(this.guiLeft + internalSlot.xPos - 2, this.guiTop + internalSlot.yPos - 2, 176, 100, 20, 20);

				GlStateManager.disableAlpha();
				GlStateManager.disableRescaleNormal();
				GlStateManager.enableLighting();

				this.itemRender.zLevel = 0.0F;
				this.zLevel = 0.0F;

				if(this.isPointInRegion(internalSlot.xPos, internalSlot.yPos, 16, 16, mouseX, mouseY)) {
					List<String> list = new ArrayList<>();
					list.add(fluidStack.getLocalizedName());
					this.drawHoveringText(list, mouseX, mouseY, this.fontRenderer);
				}
			}
		}

		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
