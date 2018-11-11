package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.aspect.AspectManager;

@SideOnly(Side.CLIENT)
public class AspectItemSlideShowWidget extends ManualWidgetBase {
    public IAspectType aspectType;
    public ArrayList<ItemStack> items = new ArrayList<>();

    public int currentItems = 0;

    public AspectItemSlideShowWidget(int xStart, int yStart, IAspectType aspectType) {
        super(xStart, yStart);
        this.aspectType = aspectType;
        currentItems = 0;
    }

    public AspectItemSlideShowWidget(int xStart, int yStart, ArrayList<ItemStack> items) {
        super(xStart, yStart);
        this.items = items;
        currentItems = 0;
    }


    @Override
    public void drawForeGround() {
        super.drawForeGround();
        if (items.size() < currentItems)
            currentItems = items.size();
        List<ItemStack> subItems = items.subList(currentItems, currentItems + ((items.size() - currentItems) > 5 ? 6 : items.size() - currentItems));
        int width = 0;
        for (ItemStack itemStack : subItems) {
            renderItem(xStart + width, yStart, itemStack, false, true, manual.manualType);
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

    @Override
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
        AspectManager manager = AspectManager.get(Minecraft.getMinecraft().world);
        for (Map.Entry<AspectItem, List<AspectManager.AspectItemEntry>> entry : AspectManager.getRegisteredItems().entrySet()) {
            List<Aspect> discoveredAspects = manager.getDiscoveredStaticAspects(entry.getKey(), DiscoveryContainer.getMergedDiscoveryContainer(Minecraft.getMinecraft().player));
            for (Aspect aspect : discoveredAspects) {
                if (aspect.type.equals(this.aspectType))
                    items.add(new ItemStack(entry.getKey().getOriginal().getItem(), 1, entry.getKey().getOriginal().getItemDamage()));
            }
        }
    }
}
