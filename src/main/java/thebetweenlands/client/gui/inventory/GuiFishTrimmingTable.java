package thebetweenlands.client.gui.inventory;

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
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button.id == 0)
			System.out.println("Chop, Chop, Chop!");
			//TheBetweenlands.networkWrapper.sendToServer(new MessageButcherFish(mc.player, button.id, fish_trimming_table.getPos()));
	}
}
