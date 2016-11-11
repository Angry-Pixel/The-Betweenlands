package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.common.inventory.container.ContainerMortar;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMortar;

public class GuiMortar extends GuiContainer {

    private TileEntityMortar mortar;
    private static final ResourceLocation PESTLE_AND_MORTAR_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/pestle_and_mortar.png");

    public GuiMortar(InventoryPlayer inv, TileEntityMortar tile) {
        super(new ContainerMortar(inv, tile));
        mortar = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(PESTLE_AND_MORTAR_GUI_TEXTURE);
        int xx = (width - xSize) / 2;
        int yy = (height - ySize) / 2;
        drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);

        int progress = mortar.progress;
        drawTexturedModalRect(xx + 45, yy + 69, 0, 166, progress, 6);

        if (mortar.getStackInSlot(3) == null) {
            renderSlot(new ItemStack(ItemRegistry.LIFE_CRYSTAL), 79 + xx, 8 + yy);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
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
}