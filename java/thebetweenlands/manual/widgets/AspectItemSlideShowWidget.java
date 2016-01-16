package thebetweenlands.manual.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectManager.AspectItem;
import thebetweenlands.herblore.aspects.AspectManager.AspectItemEntry;
import thebetweenlands.herblore.aspects.IAspectType;

/**
 * Created by Bart on 10/12/2015.
 */
public class AspectItemSlideShowWidget extends ManualWidgetsBase {
	public IAspectType aspectType;
	public ArrayList<ItemStack> items = new ArrayList<>();

	public int currentItems = 0;

	public AspectItemSlideShowWidget(int xStart, int yStart, IAspectType aspectType) {
		super(xStart, yStart);
		this.aspectType = aspectType;
	}

	public AspectItemSlideShowWidget(int xStart, int yStart, ArrayList<ItemStack> items) {
		super(xStart, yStart);
		this.items = items;
	}


	@Override
	public void drawForeGround() {
		pageLinks.clear();
		super.drawForeGround();
		List<ItemStack> subItems = items.subList(currentItems, currentItems + (items.size() - currentItems > 5 ? 6 : items.size() - currentItems));
		int width = 0;
		for (ItemStack itemStack : subItems) {
			renderItem(xStart + width, yStart, itemStack, false, true);
			width += 18;
		}
	}

	@Override
	public void resize() {
		super.resize();
		if (aspectType != null)
			getItems();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateScreen() {
		super.updateScreen();
		if (manual.untilUpdate % 60 == 0) {
			if (currentItems + 1 < items.size() && items.size() - currentItems > 6) {
				currentItems++;
			} else
				currentItems = 0;
			drawForeGround();
		}
	}

	public void mouseClicked(int x, int y, int mouseButton) {
		super.mouseClicked(x, y, mouseButton);
		if (mouseButton == 2 && x >= xStart && x <= xStart + 96 && y >= yStart && y <= yStart + 16) {
			if (currentItems + 1 < items.size() && items.size() - currentItems > 6) {
				currentItems++;
			} else
				currentItems = 0;
			drawForeGround();
		}
	}

	public void getItems() {
		items.clear();
		AspectManager manager = AspectManager.get(Minecraft.getMinecraft().theWorld);
		for(Entry<AspectItem, List<AspectItemEntry>> entry : AspectManager.getRegisteredItems().entrySet()) {
			//TODO: HL - Move NBT to book instead of player
			List<Aspect> discoveredAspects = manager.getDiscoveredAspects(entry.getKey(), AspectManager.getMergedDiscoveryContainer(Minecraft.getMinecraft().thePlayer));
			for(Aspect aspect : discoveredAspects) {
				if(aspect.type.equals(this.aspectType))
					items.add(new ItemStack(entry.getKey().item, 1, entry.getKey().damage));
			}
		}
	}
}
