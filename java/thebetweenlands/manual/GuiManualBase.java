package thebetweenlands.manual;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.items.BLItemRegistry;

/**
 * Created by Bart on 22/11/2015.
 */
public class GuiManualBase extends GuiScreen {
	private static ResourceLocation book = new ResourceLocation("thebetweenlands:textures/gui/manual/manual.png");
	public int xStart;
	public int xStartRightPage;
	public int yStart;
	public int WIDTH = 292;
	public int HEIGHT = 180;
	public EntityPlayer player;

	public int untilUpdate = 0;
	public ManualCategory currentCategory;

	public Item manualType;

	public GuiManualBase(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void initGui() {
		manualType = BLItemRegistry.manualGuideBook;
		xStart = width / 2 - 146;
		xStartRightPage = xStart + 146;
		yStart = (height - HEIGHT) / 2;
		untilUpdate = 0;
		changeCategory(ManualManager.getCurrentCategory(manualType, player), ManualManager.getCurrentPageNumber(manualType, player));
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ManualManager.setCurrentPage(currentCategory.getName(), currentCategory.getCurrentPage(), manualType, player);
		ManualManager.setCurrentPage(currentCategory.getName(), currentCategory.getCurrentPage(), manualType, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawScreen(int mouseX, int mouseY, float renderPartials) {
		super.drawScreen(mouseX, mouseY, renderPartials);

		mc.renderEngine.bindTexture(book);

		drawTexture(xStart, yStart, WIDTH, HEIGHT, 512.0D, 512.0D, 0.0D, 292.0D, 0.0D, 180.0D);
		if (currentCategory != null) {
			if (currentCategory.getCurrentPage() - 2 >= 1) {
				if (mouseX >= xStart + 15 && mouseX <= xStart + 15 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8)
					drawTexture(xStart + 15, yStart + 160, 19, 8, 512.0D, 512.0D, 292.0D, 311.0D, 9.0D, 18.0D);
				else
					drawTexture(xStart + 15, yStart + 160, 19, 8, 512.0D, 512.0D, 292.0D, 311.0D, 0.0D, 9.0D);
			}
			if (currentCategory.getCurrentPage() + 2 <= currentCategory.getVisiblePages().size()) {
				if (mouseX >= xStart + 256 && mouseX <= xStart + 256 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8)
					drawTexture(xStart + 256, yStart + 160, 19, 8, 512.0D, 512.0D, 311.0D, 330.0D, 9.0D, 18.0D);
				else
					drawTexture(xStart + 256, yStart + 160, 19, 8, 512.0D, 512.0D, 311.0D, 330.0D, 0.0D, 9.0D);
			}
			drawTexture(xStart, yStart + 10, 14, 22 * currentCategory.getCategoryNumber(), 512.0D, 512.0D, 293.0D, 306.0D, 18.0D, 18.0D + 22.0D * currentCategory.getCategoryNumber());
			drawTexture(xStart + 279, yStart + 10 + 22 * currentCategory.getCategoryNumber(), 14, 154 - 22 * currentCategory.getCategoryNumber(), 512.0D, 512.0D, 306.0D, 293.0D, 18.0D + 22.0D * currentCategory.getCategoryNumber(), 174.0D);

			currentCategory.draw(mouseX, mouseY);
		}

		this.renderPageNumbers(mouseX, mouseY, renderPartials);
	}

	/**
	 * Renders the default page numbers.
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	protected void renderPageNumbers(int mouseX, int mouseY, float partialTicks) {
		if(this.currentCategory != null) {
			int leftPageStrWidth = this.fontRendererObj.getStringWidth(String.valueOf(this.currentCategory.getCurrentPage()));
			GL11.glEnable(GL11.GL_BLEND);
			this.fontRendererObj.drawString(String.valueOf(this.currentCategory.getCurrentPage()), this.xStart + this.WIDTH / 2 - 11 - leftPageStrWidth, this.yStart + this.HEIGHT - 17, 0x804f4314);
			this.fontRendererObj.drawString(String.valueOf(this.currentCategory.getCurrentPage() + 1), this.xStart + this.WIDTH / 2 + 11, this.yStart + this.HEIGHT - 17, 0x804f4314);
		}
	}

	/**
	 * Drawing a scalable texture
	 *
	 * @param xStart        the x coordinate to start drawing
	 * @param yStart        the y coordinate to start drawing
	 * @param width         the width for drawing
	 * @param height        the height for drawing
	 * @param textureWidth  the width of the texture
	 * @param textureHeight the height of the texture
	 * @param textureXStart the x start in the texture
	 * @param textureXEnd   the x end in the texture
	 * @param textureYStart the y start in the texture
	 * @param textureYEnd   the y end in the texture
	 */
	public void drawTexture(int xStart, int yStart, int width, int height, double textureWidth, double textureHeight, double textureXStart, double textureXEnd, double textureYStart, double textureYEnd) {
		double umin = 1.0D / textureWidth * textureXStart;
		double umax = 1.0D / textureWidth * textureXEnd;
		double vmin = 1.0D / textureHeight * textureYStart;
		double vmax = 1.0D / textureHeight * textureYEnd;
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addVertexWithUV(xStart, yStart, 0, umin, vmin);
		Tessellator.instance.addVertexWithUV(xStart, yStart + height, 0, umin, vmax);
		Tessellator.instance.addVertexWithUV(xStart + width, yStart + height, 0, umax, vmax);
		Tessellator.instance.addVertexWithUV(xStart + width, yStart, 0, umax, vmin);
		Tessellator.instance.draw();
	}


	@Override
	protected void keyTyped(char c, int key) {
		switch (key) {
		case Keyboard.KEY_ESCAPE:
			mc.displayGuiScreen(null);
			break;
		case Keyboard.KEY_BACK:
			if(this.currentCategory != null)
				this.currentCategory.previousOpenPage(this);
		}

		if(this.currentCategory != null) {
			GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
			if(key == gameSettings.keyBindLeft.getKeyCode()) {
				this.currentCategory.previousPage(this);
			} else if(key == gameSettings.keyBindRight.getKeyCode()) {
				this.currentCategory.nextPage(this);
			}

			currentCategory.keyTyped(c, key);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int mouseScroll = Mouse.getEventDWheel();

		if (mouseScroll != 0) {
			if (mouseScroll > 1) {
				this.currentCategory.nextPage(this);
			} else if (mouseScroll < -1) {
				this.currentCategory.previousPage(this);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (currentCategory != null) {
			if (mouseX >= xStart + 15 && mouseX <= xStart + 15 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0) {
				currentCategory.previousPage(this);
			}
			if (mouseX >= xStart + 256 && mouseX <= xStart + 256 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0) {
				currentCategory.nextPage(this);
			}
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 1 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 1 ? 0 : 279) + 14 && mouseY >= yStart + 11 && mouseY <= yStart + 10 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.itemsCategory);
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 2 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 2 ? 0 : 279) + 14 && mouseY >= yStart + 33 && mouseY <= yStart + 32 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.machineCategory);
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 3 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 3 ? 0 : 279) + 14 && mouseY >= yStart + 55 && mouseY <= yStart + 54 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.entitiesCategory);
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 4 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 4 ? 0 : 279) + 14 && mouseY >= yStart + 77 && mouseY <= yStart + 76 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.category4);
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 5 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 5 ? 0 : 279) + 14 && mouseY >= yStart + 99 && mouseY <= yStart + 98 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.category5);
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 6 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 6 ? 0 : 279) + 14 && mouseY >= yStart + 121 && mouseY <= yStart + 120 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.category6);
			if (mouseX >= xStart + (currentCategory.getCategoryNumber() >= 7 ? 0 : 279) && mouseX <= xStart + (currentCategory.getCategoryNumber() >= 7 ? 0 : 279) + 14 && mouseY >= yStart + 143 && mouseY <= yStart + 142 + 20 && button == 0)
				changeCategory(GuideBookEntryRegistry.category7);

			currentCategory.mouseClicked(mouseX, mouseY, button);
		}
	}

	/**
	 * Change to a page
	 *
	 * @param pageNumber the number of a page
	 */
	public void changeTo(int pageNumber, boolean doMathWithIndexPages) {
		currentCategory.setPage(pageNumber + (doMathWithIndexPages? currentCategory.getIndexPages():0), this);
	}

	/**
	 * Change the current category
	 *
	 * @param category a guidebook category
	 */
	public void changeCategory(ManualCategory category) {
		currentCategory = category;
		currentCategory.init(this, true);
		currentCategory.setPage(1, this);
	}

	/**
	 * Change the current category and page
	 *
	 * @param category a guidebook category
	 * @param page     the number of a page
	 */
	public void changeCategory(ManualCategory category, int page) {
		currentCategory = category;
		currentCategory.init(this, false);
		currentCategory.setPage(page, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateScreen() {
		untilUpdate++;
		if (currentCategory != null)
			currentCategory.updateScreen();
	}

	public boolean matches(ItemStack itemStack1, ItemStack itemStack2) {
		return itemStack2.getItem() == itemStack1.getItem() && itemStack2.getItemDamage() == itemStack1.getItemDamage();
	}
}
