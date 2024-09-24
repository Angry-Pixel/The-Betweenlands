package thebetweenlands.client.gui.book.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.gui.book.HerbloreManualScreen;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.herblore.aspect.AspectManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AspectSlideshowWidget extends BookWidget {

	@Nullable
	public ItemStack itemStack;
	public List<ResourceKey<AspectType>> rawList = new ArrayList<>();
	public ArrayList<Holder<AspectType>> aspects = new ArrayList<>();

	private int currentDisplayItem = 0;
	private long lastCycleTimestamp = System.currentTimeMillis();

	public AspectSlideshowWidget(int x, int y, ItemStack itemStack) {
		super(x, y, 96, 16);
		this.itemStack = itemStack;
	}

	public AspectSlideshowWidget(int x, int y, List<ResourceKey<AspectType>> aspects) {
		super(x, y, 96, 16);
		this.rawList = aspects;
	}

	@Override
	public void setupWidget(HerbloreManualScreen screen) {
		super.setupWidget(screen);
		if (this.itemStack != null) {
			this.getAspects(screen.getRegistryLookup(), screen.getAspectManager());
		} else {
			if (this.aspects.isEmpty()) {
				this.rawList.forEach(key -> this.aspects.add(screen.getRegistryLookup().holderOrThrow(key)));
			}
		}
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		List<Holder<AspectType>> subItems = this.aspects.subList(this.currentDisplayItem, this.currentDisplayItem + (this.aspects.size() - this.currentDisplayItem > 5 ? 6 : this.aspects.size() - this.currentDisplayItem));
		int width = 0;
		for (Holder<AspectType> aspect : subItems) {
			this.renderAspect(graphics, aspect, this.getX() + width, this.getY(), mouseX, mouseY, true);
			width += 18;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 2 && this.isHovered()) {
			if (this.currentDisplayItem + 1 < this.aspects.size()) {
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
		if (System.currentTimeMillis() > this.lastCycleTimestamp + 6000L) {
			if (this.currentDisplayItem + 1 < this.aspects.size() && this.aspects.size() - this.currentDisplayItem > 6) {
				this.currentDisplayItem++;
			} else {
				this.currentDisplayItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
		}
	}

	public void getAspects(HolderLookup.Provider registries, AspectManager manager) {
		this.aspects.clear();
		List<Aspect> visibleAspects = AspectContents.getAspectsFromContainer(this.itemStack, registries, manager, DiscoveryContainerData.getMergedDiscoveryContainer(Minecraft.getInstance().player));
		for (Aspect aspect : visibleAspects) {
			this.aspects.add(aspect.type());
		}
	}
}
