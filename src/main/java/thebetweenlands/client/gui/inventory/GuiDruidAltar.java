package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerDruidAltar;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityDruidAltar;

@SideOnly(Side.CLIENT)
public class GuiDruidAltar extends GuiContainer {
    private static final ResourceLocation GUI_DRUID_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/druidAltar.png");
    private static Item ghostIcon = ItemRegistry.SWAMP_TALISMAN;
    private final TileEntityDruidAltar tile;
    private ItemStack stack;
    private int iconCountTool = 1;

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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_DRUID_ALTAR);

        drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        /* I don't like this rendering in the gui
        if (tile.getStackInSlot(0) == null) {
            renderSlot(new ItemStack(ghostIcon),  81 + xStart,  35 + yStart);
        }*/

        if (tile.getStackInSlot(1) == null) {
            renderSlot(stack, 53 + xStart, 7 + yStart);
        }

        if (tile.getStackInSlot(2) == null) {
            renderSlot(stack, 109 + xStart, 7 + yStart);
        }

        if (tile.getStackInSlot(3) == null) {
            renderSlot(stack, 53 + xStart, 63 + yStart);
        }

        if (tile.getStackInSlot(4) == null) {
            renderSlot(stack, 109 + xStart, 63 + yStart);
        }


    }

    private void renderSlot(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        //TODO fix alpha because minecraft just changes it to 1 in it's item render code
        GlStateManager.color(1f, 1f, 1f, 0.2f);
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.mc.theWorld.getWorldTime() % 40 == 0) {
            this.stack = new ItemStack(ghostIcon, 1, this.iconCountTool);
            this.iconCountTool++;
            if (this.iconCountTool > 4) {
                this.iconCountTool = 1;
            }
        }

        if (this.tile.craftingProgress == 1) {
            this.mc.thePlayer.closeScreen();
        }
    }
}
