package thebetweenlands.client.gui.book.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.gui.book.HerbloreManualScreen;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.herblore.aspect.AspectManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AspectItemSlideshowWidget extends BookWidget {

	@Nullable
	public Holder<AspectType> type;
	public ArrayList<ItemStack> items = new ArrayList<>();

	private int currentDisplayItem = 0;
	private long lastCycleTimestamp = System.currentTimeMillis();

	public AspectItemSlideshowWidget(int x, int y, Holder<AspectType> type) {
		super(x, y, 96, 16);
		this.type = type;
	}

	public AspectItemSlideshowWidget(int x, int y, ArrayList<ItemStack> items) {
		super(x, y, 96, 16);
		this.items = items;
	}

	@Override
	public void setupWidget(HerbloreManualScreen screen) {
		super.setupWidget(screen);
		if (this.type != null) {
			this.getItems(screen.getRegistryLookup(), screen.getAspectManager());
		}
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (this.items.size() < this.currentDisplayItem) {
			this.currentDisplayItem = this.items.size();
		}
		List<ItemStack> subItems = this.items.subList(this.currentDisplayItem, this.currentDisplayItem + (this.items.size() - this.currentDisplayItem > 5 ? 6 : this.items.size() - this.currentDisplayItem));
		int width = 0;
		for (ItemStack itemStack : subItems) {
			this.renderItem(graphics, itemStack, this.getX() + width, this.getY(), mouseX, mouseY, true);
			width += 18;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 2 && this.isHovered()) {
			if (this.currentDisplayItem + 1 < this.items.size()) {
				this.currentDisplayItem++;
			} else {
				this.currentDisplayItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		super.tick();
		if (System.currentTimeMillis() > this.lastCycleTimestamp + 4000L) {
			if (this.currentDisplayItem + 1 < this.items.size() && this.items.size() - this.currentDisplayItem > 6) {
				this.currentDisplayItem++;
			} else {
				this.currentDisplayItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
		}
	}

	public void getItems(HolderLookup.Provider registries, AspectManager manager) {
		this.items.clear();
		for (ResourceKey<AspectItem> aspectItem : registries.lookupOrThrow(BLRegistries.Keys.ASPECT_ITEMS).listElementIds().toList()) {
			List<Aspect> discoveredAspects = manager.getDiscoveredStaticAspects(aspectItem, DiscoveryContainerData.getMergedDiscoveryContainer(Minecraft.getInstance().player));
			for (Aspect aspect : discoveredAspects) {
				if (aspect.type().is(this.type))
					this.items.add(new ItemStack(registries.holderOrThrow(aspectItem).value().item(), 1));
			}
		}
	}
}
