package thebetweenlands.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerLurkerPouchSmall;
import thebetweenlands.items.equipment.ItemBasicInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLurkerPouchSmall extends GuiContainer {

	private static final ResourceLocation guiTexture = new ResourceLocation("textures/gui/container/generic_54.png");
	private final ItemBasicInventory inventory;
    private int inventoryRows;

	public GuiLurkerPouchSmall(ContainerLurkerPouchSmall pouch) {
		super(pouch);
		this.inventory = pouch.inventory;
        short short1 = 222;
        int i = short1 - 108;
        inventoryRows = inventory.getSizeInventory() / 9;
        ySize = i + inventoryRows * 18;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRendererObj.drawString(inventory.hasCustomInventoryName() ? inventory.getInventoryName() : I18n.format(this.inventory.getInventoryName(), new Object[0]), 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(guiTexture);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, inventoryRows * 18 + 17);
        drawTexturedModalRect(k, l + inventoryRows * 18 + 17, 0, 126, xSize, 96);
    }
}