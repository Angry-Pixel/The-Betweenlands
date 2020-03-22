package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerDraetonInventory;

@SideOnly(Side.CLIENT)
public class GuiDraetonInventory extends GuiContainer {
	
	private static final ResourceLocation DRAETON_INVENTORY = new ResourceLocation("thebetweenlands:textures/gui/draeton_inventory.png");

	public GuiDraetonInventory(InventoryPlayer inventory, Entity entityInventory) {
		super(new ContainerDraetonInventory(inventory, (IInventory) entityInventory));
		xSize = 176;
		ySize = 168;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format(new TextComponentTranslation("container.bl.draeton_inventory").getFormattedText()), xSize / 2 - fontRenderer.getStringWidth(I18n.format(new TextComponentTranslation("container.bl.draeton_inventory").getFormattedText())) / 2, 6, 4210752);
		fontRenderer.drawString(I18n.format(new TextComponentTranslation("container.inventory").getFormattedText()), xSize - 170, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(DRAETON_INVENTORY);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}
}