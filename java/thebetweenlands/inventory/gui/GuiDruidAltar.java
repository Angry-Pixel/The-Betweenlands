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
public class GuiDruidAltar
        extends GuiContainer
{
    private static final ResourceLocation GUI_DRUID_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/guiDruidAltar.png");
    private final TileEntityDruidAltar tile;

    public static Item ghostIcon = BLItemRegistry.swampTalisman;
    private ItemStack stack;
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

        drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);

        if( tile.getStackInSlot(0) == null ) {
            renderSlot(new ItemStack(ghostIcon).getIconIndex(), 81, 35);
        }

        if( tile.getStackInSlot(1) == null ) {
            renderSlot(stack.getIconIndex(), 53, 7);
        }

        if( tile.getStackInSlot(2) == null ) {
            renderSlot(stack.getIconIndex(), 109, 7);
        }

        if( tile.getStackInSlot(3) == null ) {
            renderSlot(stack.getIconIndex(), 53, 63);
        }

        if( tile.getStackInSlot(4) == null ) {
            renderSlot(stack.getIconIndex(), 109, 63);
        }


    }

    private void renderSlot(IIcon icon, int iconX, int iconY) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 0.2f);
        mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
        drawTexturedModelRectFromIcon(guiLeft + iconX, guiTop + iconY, icon, 16, 16);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if( this.mc.theWorld.getWorldTime() % 40 == 0 ) {
            this.stack = new ItemStack(ghostIcon, 1, this.iconCountTool);
            this.iconCountTool++;
            if( this.iconCountTool > 4 ) {
                this.iconCountTool = 1;
            }
        }

        if( this.tile.craftingProgress == 1 ) {
            this.mc.thePlayer.closeScreen();
        }
    }
}
