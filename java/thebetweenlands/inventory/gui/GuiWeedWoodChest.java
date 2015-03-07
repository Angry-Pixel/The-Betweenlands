package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWeedWoodChest extends GuiContainer {
    private static final ResourceLocation guiTexture = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventoryRows;

    public GuiWeedWoodChest(IInventory upperInventory, IInventory lowerInventory) {
        super(new ContainerChest(upperInventory, lowerInventory));
        upperChestInventory = upperInventory;
        lowerChestInventory = lowerInventory;
        allowUserInput = false;
        short short1 = 222;
        int i = short1 - 108;
        inventoryRows = lowerInventory.getSizeInventory() / 9;
        ySize = i + inventoryRows * 18;
    }

    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRendererObj.drawString(lowerChestInventory.hasCustomInventoryName() ? lowerChestInventory.getInventoryName() : I18n.format(lowerChestInventory.getInventoryName(), new Object[0]), 8, 6, 4210752);
        fontRendererObj.drawString(upperChestInventory.hasCustomInventoryName() ? upperChestInventory.getInventoryName() : I18n.format(upperChestInventory.getInventoryName(), new Object[0]), 8, ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(guiTexture);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, inventoryRows * 18 + 17);
        drawTexturedModalRect(k, l + inventoryRows * 18 + 17, 0, 126, xSize, 96);
    }
}
