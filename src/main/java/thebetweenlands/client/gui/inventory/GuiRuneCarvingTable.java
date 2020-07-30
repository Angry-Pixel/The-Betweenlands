package thebetweenlands.client.gui.inventory;

import java.io.IOException;
import java.util.List;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.item.IRuneletItem;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.inventory.container.ContainerRuneCarvingTableGui;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

@SideOnly(Side.CLIENT)
public class GuiRuneCarvingTable extends GuiContainer implements IRecipeShownListener {
	private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(ModInfo.ID, "textures/gui/rune/rune_carving_table_full.png");
	private GuiButtonImage recipeButton;
	private final GuiRecipeBook recipeBookGui;
	private boolean widthTooNarrow;
	private TileEntityRuneCarvingTable table;

	private int carveTicks = 0;

	public GuiRuneCarvingTable(InventoryPlayer playerInventory, TileEntityRuneCarvingTable table) {
		super(new ContainerRuneCarvingTableGui(playerInventory, table));
		((ContainerRuneCarvingTableGui) this.inventorySlots).setGui(this);
		this.recipeBookGui = new GuiRecipeBook();
		this.table = table;

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

		this.carveTicks = Math.max(this.carveTicks - 1, 0);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);

		int centerX = this.guiLeft;
		int centerY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(centerX, centerY, 0, 0, this.xSize, this.ySize);

		//Draw rune slot covers
		for(int i = 0; i < 4; i++) {
			Slot slot = this.inventorySlots.getSlot(11 + i);
			if(!slot.getHasStack()) {
				this.drawTexturedModalRect(this.guiLeft + slot.xPos, this.guiTop + slot.yPos, 182, 20, 16, 16);
			}
		}

		//Draw aspect overlays
		ItemStack aspectStack = this.inventorySlots.getSlot(10).getStack();
		if(!aspectStack.isEmpty()) {
			List<Aspect> aspects = ItemAspectContainer.fromItem(aspectStack, AspectManager.get(this.table.getWorld())).getAspects();

			if(!aspects.isEmpty()) {
				int color = aspects.get(0).type.getColor();

				float r = (float)(color >> 16 & 255) / 255.0F;
				float g = (float)(color >> 8 & 255) / 255.0F;
				float b = (float)(color & 255) / 255.0F;
				float a = (float)(color >> 24 & 255) / 255.0F;

				GlStateManager.color(r, g, b, a);

				//Circle
				this.drawTexturedModalRect(this.guiLeft + 131, this.guiTop + 96, 179, 68, 22, 22);

				ItemStack craftingStack = this.inventorySlots.getSlot(0).getStack();

				if(!craftingStack.isEmpty() && (craftingStack.getItem() instanceof IRuneletItem || craftingStack.getItem() instanceof IRuneItem)) {
					ItemStack output1 = this.inventorySlots.getSlot(11).getStack();
					ItemStack output2 = this.inventorySlots.getSlot(12).getStack();
					ItemStack output3 = this.inventorySlots.getSlot(13).getStack();
					ItemStack output4 = this.inventorySlots.getSlot(14).getStack();

					if(!output1.isEmpty() && output1.getItem() instanceof IRuneItem && ((IRuneItem) output1.getItem()).getInfusedAspect(output1) != null) {
						//Top left rune slot
						this.drawTexturedModalRect(this.guiLeft + 73, this.guiTop + 117, 209, 68, 5, 4);
					}

					if(!output2.isEmpty() && output2.getItem() instanceof IRuneItem && ((IRuneItem) output2.getItem()).getInfusedAspect(output2) != null) {
						//Bottom left rune slot
						this.drawTexturedModalRect(this.guiLeft + 73, this.guiTop + 136, 209, 87, 5, 4);
					}

					if(!output3.isEmpty() && output3.getItem() instanceof IRuneItem && ((IRuneItem) output3.getItem()).getInfusedAspect(output3) != null) {
						//Bottom right rune slot
						this.drawTexturedModalRect(this.guiLeft + 98, this.guiTop + 117, 234, 68, 5, 4);
					}

					if(!output4.isEmpty() && output4.getItem() instanceof IRuneItem && ((IRuneItem) output4.getItem()).getInfusedAspect(output4) != null) {
						//Top right rune slot
						this.drawTexturedModalRect(this.guiLeft + 98, this.guiTop + 136, 234, 87, 5, 4);
					}

					//Pipe
					this.drawTexturedModalRect(this.guiLeft + 96, this.guiTop + 90, 203, 23, 50, 39);
				}


				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		//Chisel
		this.drawTexturedModalRect(this.guiLeft + 85, this.guiTop + 91 + Math.max(this.carveTicks - partialTickTime, 0), 187, 37, 6, 26);
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

	public void onCrafting() {
		this.carveTicks = 5;
	}
}
