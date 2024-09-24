package thebetweenlands.client.gui.book;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.gui.book.widget.BookWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Page {

	public int pageNumber;
	public List<ItemStack> pageItems = NonNullList.create();
	public List<Holder<AspectType>> pageAspects = new ArrayList<>();

	public boolean isHidden;

	public Component pageName;
	public String unlocalizedPageName;

	public List<BookWidget> widgets = new ArrayList<>();
	public boolean rightPage = false;
	public boolean isParent = false;

	public Page(String pageName, List<BookWidget> widgets, boolean isHidden) {
		this.widgets = widgets;
		this.pageName = Component.translatable("manual.thebetweenlands.herblore." + pageName + ".title");
		this.unlocalizedPageName = pageName;
		this.isHidden = isHidden;
	}

	public Page(String pageName, boolean isHidden, BookWidget... widgets) {
		Collections.addAll(this.widgets, widgets);
		this.pageName = Component.translatable("manual.thebetweenlands.herblore." + pageName + ".title");
		this.unlocalizedPageName = pageName;
		this.isHidden = isHidden;
	}

	public void init(HerbloreManualScreen screen) {
		for (BookWidget widget : this.widgets) {
			widget.setupWidget(screen);
			screen.addRenderableWidget(widget);
		}
	}

	public void tick() {
		for (BookWidget widget : this.widgets) {
			widget.tick();
		}
	}

	public Page setParent() {
		this.isParent = true;
		return this;
	}

	public Page addItems(List<ItemStack> items) {
		this.pageItems.addAll(items);
		return this;
	}

	public Page addItem(ItemStack item) {
		this.pageItems.add(item);
		return this;
	}

	public Page setAspect(Holder<AspectType> aspect) {
		this.pageAspects.add(aspect);
		return this;
	}

	public Page setAspects(Holder<AspectType>[] aspects) {
		Collections.addAll(this.pageAspects, aspects);
		return this;
	}

	public Page setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		this.rightPage = pageNumber % 2 == 0;

		return this;
	}

	public void setPageToLeft() {
		for (BookWidget widget : this.widgets)
			widget.setPageToLeft();
	}

	public void setPageToRight() {
		for (BookWidget widget : this.widgets)
			widget.setPageToRight();
	}

	public Page setLocalizedPageName(Component text){
		this.pageName = text;
		return this;
	}
}
