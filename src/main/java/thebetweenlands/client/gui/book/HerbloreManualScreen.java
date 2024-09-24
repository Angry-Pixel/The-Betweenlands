package thebetweenlands.client.gui.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;

import javax.annotation.Nullable;

public class HerbloreManualScreen extends Screen {

	public static final int BOOK_WIDTH = 292;
	public static final int BOOK_HEIGHT = 180;

	public static final ResourceLocation BOOK = TheBetweenlands.prefix("textures/gui/manual/main_book.png");
	public static final ResourceLocation ASPECT_TAB_ICON = TheBetweenlands.prefix("manual/index_icon");
	public static final ResourceLocation ELIXIR_TAB_ICON = TheBetweenlands.prefix("manual/vial_icon");
	public int leftPos;
	public int rightPos;
	public int topPos;
	private final ItemStack manual;
	private final AspectManager manager = AspectManager.get(BetweenlandsClient.getClientLevel());
	private final HolderLookup.Provider registries = BetweenlandsClient.getClientLevel().registryAccess();

	@Nullable
	public ManualCategory currentCategory;

	public HerbloreManualScreen(ItemStack manual) {
		super(Component.empty());
		this.manual = manual;
		if (HerbloreEntryCategory.CATEGORIES.isEmpty()) HerbloreEntryCategory.init(this.getRegistryLookup());
		ManualCategory currCategory = ManualManager.getCurrentCategory(this.manual);
		int currPage = ManualManager.getCurrentPageNumber(this.manual);
		this.changeCategory(currCategory == null ? HerbloreEntryCategory.aspectCategory : currCategory, currPage == -1 ? 1 : currPage, false);
	}

	public ItemStack getManual() {
		return this.manual;
	}

	public AspectManager getAspectManager() {
		return this.manager;
	}

	public HolderLookup.Provider getRegistryLookup() {
		return this.registries;
	}

	@Override
	protected void init() {
		super.init();
		this.leftPos = this.width / 2 - (BOOK_WIDTH / 2);
		this.rightPos = this.leftPos + (BOOK_WIDTH / 2);
		this.topPos = (this.height - BOOK_HEIGHT) / 2;

		if (this.currentCategory != null) {
			if (this.currentCategory.getCurrentPage() - 2 >= 1) {
				this.addRenderableWidget(new HerbloreArrowButton(this.leftPos + 15, this.topPos + 160, true, button -> this.currentCategory.previousPage(this)));
			}

			if (this.currentCategory.getCurrentPage() + 2 <= this.currentCategory.getVisiblePages().size()) {
				this.addRenderableWidget(new HerbloreArrowButton(this.leftPos + 256, this.topPos + 160, false, button -> this.currentCategory.nextPage(this)));
			}

			boolean leftTab = this.currentCategory.getCategoryNumber() == 2;
			this.addRenderableWidget(new HerbloreTabButton(this.leftPos + (leftTab ? 0 : 279), this.topPos + 33, leftTab, 0xFFA55154, (graphics, x, y) -> graphics.blitSprite(ELIXIR_TAB_ICON, x + (leftTab ? 5 : 0), y + 4, 9, 11), button -> this.changeCategory(HerbloreEntryCategory.elixirCategory)));
			this.addRenderableWidget(new HerbloreTabButton(this.leftPos, this.topPos + 11, true, 0xFF6195CC, (graphics, x, y) -> graphics.blitSprite(ASPECT_TAB_ICON, x + 5, y + 4, 9, 11), button -> this.changeCategory(HerbloreEntryCategory.aspectCategory)));
		}
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
		this.currentCategory.refreshPage(this);
	}

	//made public
	@Override
	public  <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
		return super.addRenderableWidget(widget);
	}

	//made public
	@Override
	public void rebuildWidgets() {
		super.rebuildWidgets();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.currentCategory != null) {
			this.currentCategory.tick();
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(BOOK, this.leftPos, this.topPos, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);

		for (Renderable renderable : this.renderables) {
			renderable.render(graphics, mouseX, mouseY, partialTick);
		}

		if (this.currentCategory != null) {
			int leftPageStrWidth = this.font.width(String.valueOf(this.currentCategory.getCurrentPage()));
			graphics.drawString(this.font, String.valueOf(this.currentCategory.getCurrentPage()), this.leftPos + BOOK_WIDTH / 2 - 11 - leftPageStrWidth, this.topPos + BOOK_HEIGHT - 17, 0x804f4314, false);
			graphics.drawString(this.font, String.valueOf(this.currentCategory.getCurrentPage() + 1), this.leftPos + BOOK_WIDTH / 2 + 11, this.topPos + BOOK_HEIGHT - 17, 0x804f4314, false);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		super.onClose();
		if (this.currentCategory != null) {
			ManualManager.setCurrentPage(this.currentCategory.getName(), this.currentCategory.getCurrentPage(), this.manual);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			case GLFW.GLFW_KEY_ESCAPE:
				this.onClose();
				return true;
			case GLFW.GLFW_KEY_BACKSPACE:
				if (this.currentCategory != null) {
					this.currentCategory.previousOpenPage(this);
					return true;
				}
		}

		if (this.currentCategory != null) {
			Options gameSettings = Minecraft.getInstance().options;
			if (keyCode == gameSettings.keyLeft.getKey().getValue()) {
				this.currentCategory.previousPage(this);
				return true;
			} else if (keyCode == gameSettings.keyLeft.getKey().getValue()) {
				this.currentCategory.nextPage(this);
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (this.currentCategory != null) {
			if (scrollY > 0) {
				this.currentCategory.nextPage(this);
				return true;
			} else {
				this.currentCategory.previousPage(this);
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	public void changeTo(int pageNumber) {
		this.currentCategory.setPage(pageNumber, this, true);
	}

	public void changeCategory(ManualCategory category) {
		this.currentCategory = category;
		this.currentCategory.init(this, true);
		this.currentCategory.setPage(1, this, true);
	}

	public void changeCategory(ManualCategory category, int page, boolean rebuild) {
		this.currentCategory = category;
		this.currentCategory.init(this, false);
		this.currentCategory.setPage(page, this, rebuild);
	}
}
