package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerAmphibiousArmour;

@SideOnly(Side.CLIENT)
public class GuiAmphibiousArmour extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/amphibious_armour.png");
	private final InventoryItem inventory;
	public GuiAmphibiousArmour(ContainerAmphibiousArmour armour) {
		super(armour);
		this.inventory = armour.getItemInventory();
		ySize = 182;
		xSize = 174;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = I18n.format(inventory.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
