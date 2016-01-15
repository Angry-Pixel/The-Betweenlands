package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Bart on 10/12/2015.
 */
public class ItemSlideShowWidget extends ManualWidgetsBase {
    public IAspectType aspectType;
    public ArrayList<ItemStack> items = new ArrayList<>();

    public int currentItems = 0;

    public ItemSlideShowWidget(int xStart, int yStart, IAspectType aspectType) {
        super(xStart, yStart);
        this.aspectType = aspectType;
    }

    public ItemSlideShowWidget(int xStart, int yStart, ArrayList<ItemStack> items) {
        super(xStart, yStart);
        this.items = items;
    }


    @Override
    public void drawForeGround() {
        super.drawForeGround();
        List<ItemStack> subItems = items.subList(currentItems, currentItems + (items.size() - currentItems > 5 ? 6 : items.size() - currentItems));
        int width = 0;
        for (ItemStack itemStack : subItems) {
            renderItem(xStart + width, yStart, itemStack, false);
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
        Map<AspectManager.AspectItem, List<Aspect>> matchedAspects = AspectManager.get(Minecraft.getMinecraft().theWorld).getMatchedAspects();
        for (Map.Entry<AspectManager.AspectItem, List<Aspect>> e : matchedAspects.entrySet()) {
            for (Aspect aspect1 : e.getValue()) {
                if (aspect1.type == aspectType) {
                    items.add(new ItemStack(e.getKey().item, 1, e.getKey().damage));
                }
            }
        }
    }
}
