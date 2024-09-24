package thebetweenlands.client.gui.book;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.aspect.registry.AspectType;

import javax.annotation.Nullable;

public class PageLink {
	public int x;
	public int y;
	public int width;
	public int height;
	public int pageNumber;
	@Nullable
	public ManualCategory category;

	public PageLink(int x, int y, int width, int height, @Nullable ItemStack item) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (item != null) {
			for (ManualCategory category : HerbloreEntryCategory.CATEGORIES) {
				for (Page page : category.getVisiblePages()) {
					if (!page.pageItems.isEmpty()) {
						for (ItemStack stack : page.pageItems) {
							if (stack != null && ItemStack.isSameItem(stack, item)) {
								this.pageNumber = page.pageNumber;
								this.category = category;
								break;
							}
						}
					}
				}
			}
		}
	}


	public PageLink(int x, int y, int width, int height, @Nullable Holder<AspectType> aspectType) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (aspectType != null) {
			for (Page page : HerbloreEntryCategory.aspectCategory.getVisiblePages()) {
				if (!page.pageAspects.isEmpty()) {
					if (page.pageAspects.contains(aspectType)) {
						this.pageNumber = page.pageNumber;
						this.category = HerbloreEntryCategory.aspectCategory;
						break;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "page number: " + this.pageNumber + ", category name: " + this.category.getName() + ", xStart: " + this.x + ", yStart: " + this.y + ", width: " + this.width + ", height: " + this.height;
	}

}
