package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerSilkBundle;

public class GuiSilkBundle extends GuiContainer {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final InventoryItem inventory;
	private int inventoryRows;

	public GuiSilkBundle(ContainerSilkBundle bundle) {
		super(bundle);
		this.inventory = bundle.getItemInventory();
		this.inventoryRows = this.inventory.getSizeInventory() / 9;
		this.ySize = 114 + this.inventoryRows * 18;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.fontRenderer.drawString(this.inventory.getName(), 8, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int xStart = (this.width - this.xSize) / 2;
		int yStart = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
		this.drawTexturedModalRect(xStart, yStart + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}