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
import thebetweenlands.common.inventory.container.ContainerFishTrimmingTable;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

@SideOnly(Side.CLIENT)
public class GuiFishTrimmingTable extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/fish_trimming_table.png");
	private TileEntityFishTrimmingTable fish_trimming_table;
	private Color color = new Color(192, 192, 192, 200);
	public GuiFishTrimmingTable(EntityPlayer inventory, TileEntityFishTrimmingTable tile) {
		super(new ContainerFishTrimmingTable(inventory, tile));
		fish_trimming_table = tile;
		ySize = 222;
		xSize = 176;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		int xOffSet = (width - xSize) / 2;
		int yOffSet = (height - ySize) / 2;
		addButton(new GuiButton(0, xOffSet + 48, yOffSet + 106, 80, 20, I18n.format("gui.bl.fish_trimming_table.butcher")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = fish_trimming_table.hasCustomName() ? fish_trimming_table.getName() : I18n.format(fish_trimming_table.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		GlStateManager.depthFunc(516);
		drawRect(guiLeft + 44, guiTop + 72, guiLeft + 44 + 16, guiTop + 72 + 16, 822083583);
		GlStateManager.depthFunc(515);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (!fish_trimming_table.getItems().get(0).isEmpty()) { //test
			mc.getRenderItem().renderItemIntoGUI(fish_trimming_table.getSlotresult(1), guiLeft + 44, guiTop + 72);
			mc.getRenderItem().renderItemIntoGUI(fish_trimming_table.getSlotresult(2), guiLeft + 80, guiTop + 72);
			mc.getRenderItem().renderItemIntoGUI(fish_trimming_table.getSlotresult(3), guiLeft + 116, guiTop + 72);
			
			GlStateManager.depthFunc(516);
			drawRect(guiLeft + 44, guiTop + 72, guiLeft + 60, guiTop + 88, color.getRGB());
			drawRect(guiLeft + 80, guiTop + 72, guiLeft + 96, guiTop + 88, color.getRGB());
			drawRect(guiLeft + 116, guiTop + 72, guiLeft + 132, guiTop + 88, color.getRGB());
			GlStateManager.depthFunc(515);
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
			System.out.println("Chop, Chop, Chop!");
			//TheBetweenlands.networkWrapper.sendToServer(new MessageButcherFish(mc.player, button.id, fish_trimming_table.getPos()));
	}
}
