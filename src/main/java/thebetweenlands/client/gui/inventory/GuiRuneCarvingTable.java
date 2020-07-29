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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerRuneCarvingTable;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

@SideOnly(Side.CLIENT)
public class GuiRuneCarvingTable extends GuiContainer implements IRecipeShownListener {
	private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(ModInfo.ID, "textures/gui/rune/rune_carving_table_full.png");
	private GuiButtonImage recipeButton;
	private final GuiRecipeBook recipeBookGui;
	private boolean widthTooNarrow;

	public GuiRuneCarvingTable(InventoryPlayer playerInventory, TileEntityRuneCarvingTable table) {
		super(new ContainerRuneCarvingTable(playerInventory, table));
		recipeBookGui = new GuiRecipeBook();
		
		this.xSize = 176;
		this.ySize = 247;
	}

	@Override
	public void initGui() {
		super.initGui();
		widthTooNarrow = width < 379;
		recipeBookGui.func_194303_a(width, height, mc, widthTooNarrow, ((ContainerWorkbench)inventorySlots).craftMatrix);
		guiLeft = recipeBookGui.updateScreenPosition(widthTooNarrow, width, xSize);
		recipeButton = new GuiButtonImage(10, guiLeft + 20, guiTop + 53, 20, 18, 235, 209, 19, CRAFTING_TABLE_GUI_TEXTURES);
		buttonList.add(recipeButton);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		recipeBookGui.tick();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		if (this.recipeBookGui.isVisible() && widthTooNarrow) {
			drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
			recipeBookGui.render(mouseX, mouseY, partialTicks);
		} else {
			recipeBookGui.render(mouseX, mouseY, partialTicks);
			super.drawScreen(mouseX, mouseY, partialTicks);
			recipeBookGui.renderGhostRecipe(guiLeft, guiTop, true, partialTicks);
		}

		this.renderHoveredToolTip(mouseX, mouseY);
		this.recipeBookGui.renderTooltip(guiLeft, guiTop, mouseX, mouseY);
	}

	@Override
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		return (!widthTooNarrow || !recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (!recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
			if (!widthTooNarrow || !recipeBookGui.isVisible()) {
				super.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}

	@Override
	protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_) {
		boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + xSize || p_193983_2_ >= p_193983_4_ + ySize;
		return this.recipeBookGui.hasClickedOutside(p_193983_1_, p_193983_2_, guiLeft, guiTop, xSize, ySize) && flag;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 10) {
			recipeBookGui.initVisuals(widthTooNarrow, ((ContainerWorkbench)inventorySlots).craftMatrix);
			recipeBookGui.toggleVisibility();
			guiLeft = recipeBookGui.updateScreenPosition(widthTooNarrow, width, xSize);
			recipeButton.setPosition(guiLeft + 20, guiTop + 53);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!recipeBookGui.keyPressed(typedChar, keyCode)) {
			super.keyTyped(typedChar, keyCode);
		}
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
