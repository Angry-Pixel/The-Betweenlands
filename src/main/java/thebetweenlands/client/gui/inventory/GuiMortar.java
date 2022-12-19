package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
    	GlStateManager.color(1, 1, 1, 1);
    	mc.renderEngine.bindTexture(PESTLE_AND_MORTAR_GUI_TEXTURE);
        int xx = (width - xSize) / 2;
        int yy = (height - ySize) / 2;
        drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);

        int progress = mortar.progress;
        drawTexturedModalRect(xx + 45, yy + 69, 0, 166, progress, 6);

        if(this.mortar.getStackInSlot(3).isEmpty())
        	renderSlot(new ItemStack(ItemRegistry.LIFE_CRYSTAL), 79 + xx, 8 + yy);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
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
}