package thebetweenlands.client.gui.book.widget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.gui.book.HerbloreManualScreen;
import thebetweenlands.client.gui.book.PageLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BookWidget extends AbstractWidget {

	public ArrayList<PageLink> pageLinks = new ArrayList<>();
	public boolean isPageRight = false;
	private HerbloreManualScreen screen;

	public BookWidget(int x, int y, int width, int height) {
		super(x, y, width, height, Component.empty());
	}

	public void setupWidget(HerbloreManualScreen screen) {
		this.screen = screen;
	}

	public void tick() {

	}

	public void setPageToLeft() {
		this.isPageRight = false;
	}

	public void setPageToRight() {
		this.isPageRight = true;
	}

	@Override
	public int getX() {
		return this.isPageRight ? super.getX() + this.screen.rightPos : super.getX() + this.screen.leftPos;
	}

	@Override
	public int getY() {
		return super.getY() + this.screen.topPos;
	}

	@Override
	protected final void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.pageLinks.clear();
		this.renderBookWidget(graphics, mouseX, mouseY, partialTick);
	}

	protected abstract void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

	public final void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, int mouseX, int mouseY, boolean addPageLink) {
		graphics.renderItem(stack, x, y);
		boolean shouldShowTooltip = false;
		if (addPageLink) {
			int lengthBefore = this.pageLinks.size();
			PageLink link = new PageLink(x, y, 16, 16, stack);
			if (link.category != null) {
				this.pageLinks.add(link);
			}
			shouldShowTooltip = this.pageLinks.size() > lengthBefore;
		}
		if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
			List<Component> tooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
			if (shouldShowTooltip) {
				tooltip.add(Component.translatable("manual.thebetweenlands.herblore.open_entry").withStyle(ChatFormatting.GRAY));
			}
			graphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
		}
	}

	public final void renderAspect(GuiGraphics graphics, Holder<AspectType> aspect, int x, int y, int mouseX, int mouseY, boolean addPageLink) {
		TextureAtlasSprite sprite = BetweenlandsClient.getAspectIconManager().get(aspect);
		int width = sprite.contents().width();
		int height = sprite.contents().height();
		graphics.blit(x, y, 0, width, height, sprite);
		boolean shouldShowTooltip = false;
		if (addPageLink) {
			int lengthBefore = this.pageLinks.size();
			PageLink link = new PageLink(x, y, width, height, aspect);
			if (link.category != null) {
				this.pageLinks.add(link);
			}
			shouldShowTooltip = this.pageLinks.size() > lengthBefore;
		}
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			List<Component> tooltips = new ArrayList<>();
			tooltips.add(AspectType.getAspectName(aspect));
			tooltips.add(AspectType.getAspectType(aspect).withStyle(ChatFormatting.GRAY));
			if (shouldShowTooltip) {
				tooltips.add(Component.translatable("manual.thebetweenlands.herblore.open_entry").withStyle(ChatFormatting.GRAY));
			}
			graphics.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			for (PageLink link : this.pageLinks) {
				if (this.isHoveredOverLink(mouseX, mouseY, link)) {
					this.screen.changeCategory(link.category, link.pageNumber + link.category.getIndexPages(), true);
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	public boolean isHoveredOverLink(double mouseX, double mouseY, PageLink link) {
		return mouseX >= link.x && mouseY >= link.y && mouseX <= link.x + link.width && mouseY <= link.y + link.height;
	}
}
