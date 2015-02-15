package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerDruidAltar;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDruidAltar extends GuiContainer {

	private static final ResourceLocation GUI_DRUID_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/guiDruidAltar.png");
	private final TileEntityDruidAltar tile;
	
	public static Item ghostIcon = BLItemRegistry.swampTalisman;
	private ItemStack stack, stack2;
	int iconCountTool = 1;

	public GuiDruidAltar(InventoryPlayer playerInventory, TileEntityDruidAltar tile) {
		super(new ContainerDruidAltar(playerInventory, tile));
		this.tile = tile;
		allowUserInput = false;
		ySize = 168;
		stack = new ItemStack(ghostIcon, 1, iconCountTool);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		// TODO Texture interferes with info...
		// fontRendererObj.drawString(StatCollector.translateToLocal(tile.getInventoryName()), 8, 6, 4210752);
		// fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_DRUID_ALTAR);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		
		if (tile.getStackInSlot(0) == null) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1f, 1f, 1f, 0.2f);
			IIcon iicon1 = new ItemStack(ghostIcon).getIconIndex();
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			drawTexturedModelRectFromIcon(guiLeft + 81, guiTop + 35, iicon1, 16, 16);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
		if (tile.getStackInSlot(1) == null) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1f, 1f, 1f, 0.2f);
			IIcon iicon1 = stack.getIconIndex();
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			drawTexturedModelRectFromIcon(guiLeft + 53, guiTop + 7, iicon1, 16, 16);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
		if (tile.getStackInSlot(2) == null) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1f, 1f, 1f, 0.2f);
			IIcon iicon1 = stack.getIconIndex();
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			drawTexturedModelRectFromIcon(guiLeft + 109, guiTop + 7, iicon1, 16, 16);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
		if (tile.getStackInSlot(3) == null) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1f, 1f, 1f, 0.2f);
			IIcon iicon1 = stack.getIconIndex();
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			drawTexturedModelRectFromIcon(guiLeft + 53, guiTop + 63, iicon1, 16, 16);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
		if (tile.getStackInSlot(4) == null) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1f, 1f, 1f, 0.2f);
			IIcon iicon1 = stack.getIconIndex();
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			drawTexturedModelRectFromIcon(guiLeft + 109, guiTop + 63, iicon1, 16, 16);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		if (mc.theWorld.getWorldTime() % 40 == 0) {
			stack = new ItemStack(ghostIcon, 1, iconCountTool);
			iconCountTool++;
			if (iconCountTool > 4)
				iconCountTool = 1;
		}
		if(tile.craftingProgress == 1)
			mc.thePlayer.closeScreen();
	}
}