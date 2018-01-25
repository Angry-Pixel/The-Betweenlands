package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerDruidAltar;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityDruidAltar;

@SideOnly(Side.CLIENT)
public class GuiDruidAltar extends GuiContainer {
    private static final ResourceLocation GUI_DRUID_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/druid_altar.png");
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

        int dmg = stack.getItemDamage();
        
        if (tile.getStackInSlot(1).isEmpty()) {
        	stack.setItemDamage((0 + (dmg - 1)) % 4 + 1);
            renderSlot(stack, 53 + xStart, 7 + yStart);
        }

        if (tile.getStackInSlot(2).isEmpty()) {
        	stack.setItemDamage((1 + (dmg - 1)) % 4 + 1);
            renderSlot(stack, 109 + xStart, 7 + yStart);
        }

        if (tile.getStackInSlot(3).isEmpty()) {
        	stack.setItemDamage((2 + (dmg - 1)) % 4 + 1);
            renderSlot(stack, 53 + xStart, 63 + yStart);
        }

        if (tile.getStackInSlot(4).isEmpty()) {
        	stack.setItemDamage((3 + (dmg - 1)) % 4 + 1);
            renderSlot(stack, 109 + xStart, 63 + yStart);
        }

        stack.setItemDamage(dmg);

    }

    private void renderSlot(ItemStack stack, int x, int y) {
    	GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GL14.glBlendColor(0, 0, 0, 0.35f);
		GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA); //ugly hack
		GlStateManager.pushMatrix();
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		GlStateManager.popMatrix();
		GL14.glBlendColor(1, 1, 1, 1);
		GlStateManager.blendFunc(SourceFactor.CONSTANT_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA); //ugly
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.mc.world.getTotalWorldTime() % 40 == 0) {
            this.stack = new ItemStack(ghostIcon, 1, this.iconCountTool);
            this.iconCountTool++;
            if (this.iconCountTool > 4) {
                this.iconCountTool = 1;
            }
        }
        TileEntity tile2 = tile.getWorld().getTileEntity(tile.getPos());
        if (tile2.getBlockMetadata() == 1) {
            this.mc.player.closeScreen();
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
