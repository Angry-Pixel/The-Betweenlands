package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerSilkBundle;

public class GuiSilkBundle extends GuiContainer {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/silk_bundle.png");
	private final InventoryItem inventory;

	public GuiSilkBundle(ContainerSilkBundle bundle) {
		super(bundle);
		this.inventory = bundle.getItemInventory();
		this.xSize = 174;
		this.ySize = 164;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = inventory.hasCustomName() ? inventory.getName() : I18n.format(inventory.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 4, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int xStart = (this.width - this.xSize) / 2;
		int yStart = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (mouseX < this.guiLeft || mouseX > this.guiLeft + this.ySize)
			return;
		 else if (mouseY < this.guiTop || mouseY > this.guiTop + this.xSize)
			return;
		else
			super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char key, int event){  
	   if (event == 1 || event == this.mc.gameSettings.keyBindInventory.getKeyCode())
		   this.mc.player.closeScreen();
	       this.checkHotbarKeys(key);
	   }
}