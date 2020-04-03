package thebetweenlands.client.gui.inventory;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.inventory.container.ContainerDraetonUpgrades;

@SideOnly(Side.CLIENT)
public class GuiDraetonUpgrades extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/draeton_upgrades.png");

	
	
	public GuiDraetonUpgrades(InventoryPlayer playerInventory, EntityDraeton draeton) {
		super(new ContainerDraetonUpgrades(playerInventory, draeton));
		xSize = 182;
		ySize = 256;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        Slot hoveredSlot = this.getSlotUnderMouse();
        if(hoveredSlot != null && hoveredSlot.getStack().isEmpty()) {
        	String key = null;
        	
        	switch(this.inventorySlots.inventorySlots.indexOf(hoveredSlot)) {
        	default:
        		break;
        	case 0:
        	case 1:
        	case 2:
        	case 3:
        	case 4:
        	case 5:
        		key = "gui.bl.draeton.main.puller";
        		break;
        	case 6:
        	case 7:
        	case 8:
        	case 9:
        		key = "gui.bl.draeton.main.upgrade_utility";
        		break;
        	case 10:
        		key = "gui.bl.draeton.main.upgrade_anchor";
        		break;
        	}
        	
        	if(key != null) {
    			List<String> lines = ItemTooltipHandler.splitTooltip(I18n.format(key), 0);
    			this.drawHoveringText(lines, mouseX, mouseY);
        	}
        }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format(new TextComponentTranslation("container.inventory").getFormattedText()), xSize - 170, ySize - 93, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}
}