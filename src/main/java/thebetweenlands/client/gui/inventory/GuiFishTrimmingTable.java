package thebetweenlands.client.gui.inventory;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.gui.GuiButtonNoClickSound;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.container.ContainerFishTrimmingTable;
import thebetweenlands.common.network.serverbound.MessageButcherFish;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

@SideOnly(Side.CLIENT)
public class GuiFishTrimmingTable extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/fish_trimming_table.png");
	private TileEntityFishTrimmingTable fish_trimming_table;
	private Color color = new Color(192, 192, 192, 200);
	public GuiFishTrimmingTable(EntityPlayer inventory, TileEntityFishTrimmingTable tile) {
		super(new ContainerFishTrimmingTable(inventory, tile));
		fish_trimming_table = tile;
		ySize = 226;
		xSize = 176;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		int xOffSet = (width - xSize) / 2;
		int yOffSet = (height - ySize) / 2;
		addButton(new GuiButtonNoClickSound(0, xOffSet + 48, yOffSet + 111, 80, 20, I18n.format("gui.bl.fish_trimming_table.butcher")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = fish_trimming_table.hasCustomName() ? fish_trimming_table.getName() : I18n.format(fish_trimming_table.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 11, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (fish_trimming_table.hasAnadia() || fish_trimming_table.hasSiltCrab() || fish_trimming_table.hasBubblerCrab()) { //test
			mc.getRenderItem().renderItemIntoGUI(fish_trimming_table.getSlotResult(1, 0), guiLeft + 44, guiTop + 77);
			mc.getRenderItem().renderItemIntoGUI(fish_trimming_table.getSlotResult(2, 0), guiLeft + 80, guiTop + 77);
			mc.getRenderItem().renderItemIntoGUI(fish_trimming_table.getSlotResult(3, 0), guiLeft + 116, guiTop + 77);

			GlStateManager.depthFunc(516);
			drawRect(guiLeft + 44, guiTop + 77, guiLeft + 60, guiTop + 93, color.getRGB());
			drawRect(guiLeft + 80, guiTop + 77, guiLeft + 96, guiTop + 93, color.getRGB());
			drawRect(guiLeft + 116, guiTop + 77, guiLeft + 132, guiTop + 93, color.getRGB());
			GlStateManager.depthFunc(515);

			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, fish_trimming_table.getSlotResult(1, 0), guiLeft + 44, guiTop + 77, null);
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, fish_trimming_table.getSlotResult(2, 0), guiLeft + 80, guiTop + 77, null);
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, fish_trimming_table.getSlotResult(3, 0), guiLeft + 116, guiTop + 77, null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button.id == 0)
			TheBetweenlands.networkWrapper.sendToServer(new MessageButcherFish());
	}
}
