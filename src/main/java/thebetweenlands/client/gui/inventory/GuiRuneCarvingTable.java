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
	private static final ResourceLocation CRAFTING_TABLE_FULL_GUI_TEXTURES = new ResourceLocation(ModInfo.ID, "textures/gui/rune/rune_carving_table_full.png");
	private static final ResourceLocation CRAFTING_TABLE_SINGLE_GUI_TEXTURES = new ResourceLocation(ModInfo.ID, "textures/gui/rune/rune_carving_table_single.png");

	private boolean fullGrid;

	private GuiButtonImage recipeButton;
	private final GuiRecipeBook recipeBookGui;
	private boolean widthTooNarrow;
	private TileEntityRuneCarvingTable table;

	private int carveTicks = 0;

	public GuiRuneCarvingTable(InventoryPlayer playerInventory, TileEntityRuneCarvingTable table, boolean fullGrid) {
		super(new ContainerRuneCarvingTableGui(playerInventory, table, fullGrid));
		((ContainerRuneCarvingTableGui) this.inventorySlots).setGui(this);
		this.fullGrid = fullGrid;
		this.table = table;

		if(fullGrid) {
			this.recipeBookGui = new GuiRecipeBook();
		} else {
			this.recipeBookGui = null;
		}

		this.xSize = 176;
		this.ySize = 247;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.widthTooNarrow = this.width < 379;

		if(this.recipeBookGui != null) {
			this.recipeBookGui.func_194303_a(this.width, this.height, this.mc, this.widthTooNarrow, ((ContainerWorkbench)this.inventorySlots).craftMatrix);
			this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
			this.recipeButton = new GuiButtonImage(10, this.guiLeft + 20, this.guiTop + 53, 20, 18, 235, 209, 19, CRAFTING_TABLE_FULL_GUI_TEXTURES);
			this.buttonList.add(this.recipeButton);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if(this.recipeBookGui != null) {
			this.recipeBookGui.tick();
		}

		this.carveTicks = Math.max(this.carveTicks - 1, 0);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if(this.fullGrid) {
			this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_FULL_GUI_TEXTURES);
		} else {
			this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_SINGLE_GUI_TEXTURES);
		}

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
				Aspect aspect = aspects.get(0);
				
				//Text holder
				this.drawTexturedModalRect(this.guiLeft + 145, this.guiTop + 78, 227, 105, 29, 31);
				
				String displayAmount = aspect.getRoundedDisplayAmount();
				this.fontRenderer.drawString(displayAmount, this.guiLeft + 159 - this.fontRenderer.getStringWidth(displayAmount) / 2, this.guiTop + 81, 4210752);
				
				if(this.fullGrid) {
					this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_FULL_GUI_TEXTURES);
				} else {
					this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_SINGLE_GUI_TEXTURES);
				}
				
				int color = aspect.type.getColor();

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
						this.drawTexturedModalRect(this.guiLeft + 73, this.guiTop + 117, 209, 68, 10, 10);
					}

					if(!output2.isEmpty() && output2.getItem() instanceof IRuneItem && ((IRuneItem) output2.getItem()).getInfusedAspect(output2) != null) {
						//Bottom left rune slot
						this.drawTexturedModalRect(this.guiLeft + 73, this.guiTop + 130, 209, 81, 10, 10);
					}

					if(!output3.isEmpty() && output3.getItem() instanceof IRuneItem && ((IRuneItem) output3.getItem()).getInfusedAspect(output3) != null) {
						//Bottom right rune slot
						this.drawTexturedModalRect(this.guiLeft + 93, this.guiTop + 117, 229, 68, 10, 10);
					}

					if(!output4.isEmpty() && output4.getItem() instanceof IRuneItem && ((IRuneItem) output4.getItem()).getInfusedAspect(output4) != null) {
						//Top right rune slot
						this.drawTexturedModalRect(this.guiLeft + 93, this.guiTop + 130, 229, 81, 10, 10);
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

		if(this.recipeBookGui != null) {
			if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
				this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
				this.recipeBookGui.render(mouseX, mouseY, partialTicks);
			} else {
				this.recipeBookGui.render(mouseX, mouseY, partialTicks);
				super.drawScreen(mouseX, mouseY, partialTicks);
				this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
			}
		} else {
			super.drawScreen(mouseX, mouseY, partialTicks);
		}

		this.renderHoveredToolTip(mouseX, mouseY);
		if(this.recipeBookGui != null) {
			this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
		}
	}

	@Override
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		return (!this.widthTooNarrow || this.recipeBookGui == null || !this.recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (this.recipeBookGui == null || !this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
			if (!this.widthTooNarrow || this.recipeBookGui == null || !this.recipeBookGui.isVisible()) {
				super.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}

	@Override
	protected boolean hasClickedOutside(int x1, int x2, int x3, int x4) {
		boolean insideMainGui = x1 < x3 || x2 < x4 || x1 >= x3 + this.xSize || x2 >= x4 + this.ySize;
		return (this.recipeBookGui == null || this.recipeBookGui.hasClickedOutside(x1, x2, this.guiLeft, this.guiTop, this.xSize, this.ySize)) && insideMainGui;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (this.recipeBookGui != null && button.id == 10) {
			this.recipeBookGui.initVisuals(this.widthTooNarrow, ((ContainerWorkbench)this.inventorySlots).craftMatrix);
			this.recipeBookGui.toggleVisibility();
			this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
			this.recipeButton.setPosition(this.guiLeft + 20, this.guiTop + 53);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.recipeBookGui == null || !this.recipeBookGui.keyPressed(typedChar, keyCode)) {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		super.handleMouseClick(slotIn, slotId, mouseButton, type);
		if(this.recipeBookGui != null) {
			this.recipeBookGui.slotClicked(slotIn);
		}
	}

	@Override
	public void recipesUpdated() {
		if(this.recipeBookGui != null) {
			this.recipeBookGui.recipesUpdated();
		}
	}

	@Override
	public void onGuiClosed() {
		if(this.recipeBookGui != null) {
			this.recipeBookGui.removed();
		}
		super.onGuiClosed();
	}

	@Override
	public GuiRecipeBook func_194310_f() {
		return this.recipeBookGui;
	}

	public void onCrafting() {
		this.carveTicks = 5;
	}
}
