package thebetweenlands.client.gui.book.widget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.gui.book.HerbloreManualScreen;
import thebetweenlands.client.gui.book.ManualManager;
import thebetweenlands.client.gui.book.Page;
import thebetweenlands.client.gui.book.widget.text.FormatTags;
import thebetweenlands.client.gui.book.widget.text.TextContainer;

import javax.annotation.Nullable;
import java.util.List;

public class ButtonWidget extends BookWidget {

	public int pageNumber;
	public boolean isPageHidden;
	private int currentItem;
	boolean renderSomething = true;
	boolean doMathWithIndexPages = true;
	private final List<ItemStack> items = NonNullList.create();
	@Nullable
	private Holder<AspectType> aspect;
	@Nullable
	private TextContainer textContainer;
	@Nullable
	private Page page;
	private boolean isFullyDiscovered = false;
	private long lastCycleTimestamp = System.currentTimeMillis();
	@Nullable
	private HerbloreManualScreen manual;

	public ButtonWidget(int x, int y, Page page) {
		super(x, y, 115, 16);
		this.pageNumber = page.pageNumber;
		this.page = page;
		if (!page.pageItems.isEmpty()) {
			this.items.addAll(page.pageItems);
		} else if (!page.pageAspects.isEmpty()) {
			aspect = page.pageAspects.getFirst();
		}

		this.isPageHidden = page.isHidden;
		this.initTextContainer();
	}

	public ButtonWidget(int x, int y, int width, int height, int pageNumber, boolean doMathWithIndexPages) {
		super(x, y, width, height);
		this.pageNumber = pageNumber;
		this.renderSomething = false;
		this.doMathWithIndexPages = doMathWithIndexPages;
	}

	@Override
	public void setupWidget(HerbloreManualScreen screen) {
		super.setupWidget(screen);
		this.manual = screen;

		if (this.page != null) {
			this.pageNumber = this.page.pageNumber;

			if (!this.page.isHidden) {
				this.isPageHidden = false;
			} else {
				this.isPageHidden = !ManualManager.hasFoundPage(this.page.unlocalizedPageName, screen.getAspectManager(), screen.getRegistryLookup(), screen.getManual());
				this.isFullyDiscovered = ManualManager.isFullyDiscovered(this.page.unlocalizedPageName, screen.getAspectManager(), screen.getRegistryLookup(), screen.getManual());
			}

			this.initTextContainer();
		}
	}


	@Override
	public void setPageToRight() {
		super.setPageToRight();
		if (this.renderSomething) {
			this.initTextContainer();
		}
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (this.renderSomething) {
			if (!this.items.isEmpty()) {
				this.renderItem(graphics, this.items.get(this.currentItem), this.getX(), this.getY(), mouseX, mouseY, false);
			} else if (this.aspect != null) {
				this.renderAspect(graphics, this.aspect, this.getX(), this.getY(), mouseX, mouseY, false);
			}

			if (this.textContainer != null) {
				TextContainer.TextPage page = this.textContainer.getPages().getFirst();
				page.render(graphics, this.getX() + 18, this.getY() + 2);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 2 && this.isHovered()) {
			if (this.currentItem + 1 < this.items.size()) {
				this.currentItem++;
			} else {
				this.currentItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
			return true;
		} else if (button == 0 && this.isMouseOver(mouseX, mouseY) && !this.isPageHidden) {
			List<Page> visiblePages = this.manual.currentCategory.getVisiblePages();
			int targetPage = -1;
			if (this.doMathWithIndexPages) {
				for (int i = 0; i < visiblePages.size(); i++) {
					if (visiblePages.get(i).pageNumber == this.pageNumber) {
						targetPage = i;
					}
				}
				if (targetPage >= 0) {
					this.manual.changeTo(targetPage + 1);
					return true;
				}
			} else {
				this.manual.changeTo(this.pageNumber);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		super.tick();
		if (System.currentTimeMillis() > this.lastCycleTimestamp + 2000L) {
			if (this.currentItem + 1 < this.items.size()) {
				this.currentItem++;
			} else {
				this.currentItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
		}
	}

	public void initTextContainer() {
		if (this.page != null && this.manual != null) {
			MutableComponent text = this.page.pageName.copy();
			if(!this.isPageHidden) {
				text = text.withStyle(ChatFormatting.UNDERLINE);
			}
			if(this.isFullyDiscovered) {
				text = text.withStyle(style -> style.withColor(0x559030));
			}
			this.textContainer = new TextContainer(84, 22, text, Style.DEFAULT_FONT);

			this.textContainer.setCurrentScale(1f).setCurrentColor(0x606060);
			this.textContainer.registerTag(new FormatTags.TagScale(1.0F));
			this.textContainer.registerTag(new FormatTags.TagColor(0x606060));
			this.textContainer.registerTag(new FormatTags.TagTooltip(Component.empty()));
			this.textContainer.registerTag(new FormatTags.TagSimple("bold", ChatFormatting.BOLD));
			this.textContainer.registerTag(new FormatTags.TagSimple("obfuscated", ChatFormatting.OBFUSCATED));
			this.textContainer.registerTag(new FormatTags.TagSimple("italic", ChatFormatting.ITALIC));
			this.textContainer.registerTag(new FormatTags.TagSimple("strikethrough", ChatFormatting.STRIKETHROUGH));
			this.textContainer.registerTag(new FormatTags.TagSimple("underline", ChatFormatting.UNDERLINE));

			try {
				this.textContainer.parse();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			this.textContainer = null;
		}
	}
}
