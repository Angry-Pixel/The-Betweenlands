package thebetweenlands.client.gui.inventory;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

@OnlyIn(Dist.CLIENT)
public class GuiWeedwoodWorkbench extends GuiContainer implements IRecipeShownListener {
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");
    private GuiButtonImage recipeButton;
    private final GuiRecipeBook recipeBookGui;
    private boolean widthTooNarrow;

    public GuiWeedwoodWorkbench(InventoryPlayer playerInventory, TileEntityWeedwoodWorkbench table) {
        super(new ContainerWeedwoodWorkbench(playerInventory, table));
        recipeBookGui = new GuiRecipeBook();
    }

    @Override
    public void initGui() {
        super.initGui();
        widthTooNarrow = width < 379;
        recipeBookGui.func_194303_a(width, height, mc, widthTooNarrow, ((ContainerWorkbench)inventorySlots).craftMatrix);
        guiLeft = recipeBookGui.updateScreenPosition(widthTooNarrow, width, xSize);
        recipeButton = new GuiButtonImage(10, guiLeft + 5, height / 2 - 49, 20, 18, 0, 168, 19, CRAFTING_TABLE_GUI_TEXTURES) {
        	@Override
        	public void onClick(double mouseX, double mouseY) {
        		recipeBookGui.initVisuals(widthTooNarrow, ((ContainerWorkbench)inventorySlots).craftMatrix);
                recipeBookGui.toggleVisibility();
                guiLeft = recipeBookGui.updateScreenPosition(widthTooNarrow, width, xSize);
                recipeButton.setPosition(guiLeft + 5, height / 2 - 49);
        	}
        };
        buttons.add(recipeButton);
    }

    @Override
    public void tick() {
        super.tick();
        recipeBookGui.tick();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRenderer.drawString(I18n.format("container.crafting"), 28, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int centerX = guiLeft;
        int centerY = (height - ySize) / 2;
        drawTexturedModalRect(centerX, centerY, 0, 0, xSize, ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (this.recipeBookGui.isVisible() && widthTooNarrow) {
            drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            recipeBookGui.render(mouseX, mouseY, partialTicks);
        } else {
            recipeBookGui.render(mouseX, mouseY, partialTicks);
            super.render(mouseX, mouseY, partialTicks);
            recipeBookGui.renderGhostRecipe(guiLeft, guiTop, true, partialTicks);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
        this.recipeBookGui.renderTooltip(guiLeft, guiTop, mouseX, mouseY);
    }

    @Override
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY) {
        return (!widthTooNarrow || !recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
            if (!widthTooNarrow || !recipeBookGui.isVisible()) {
                return super.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        return false;
    }

    @Override
    protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_) {
        boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + xSize || p_193983_2_ >= p_193983_4_ + ySize;
        return this.recipeBookGui.hasClickedOutside(p_193983_1_, p_193983_2_, guiLeft, guiTop, xSize, ySize) && flag;
    }

    @Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (recipeBookGui.keyPressed(key, scanCode, modifiers)) {
        	return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void recipesUpdated() {
        recipeBookGui.recipesUpdated();
    }

    @Override
    public void onGuiClosed() {
        recipeBookGui.removed();
        super.onGuiClosed();
    }

    @Override
    public GuiRecipeBook func_194310_f() {
        return recipeBookGui;
    }
}
