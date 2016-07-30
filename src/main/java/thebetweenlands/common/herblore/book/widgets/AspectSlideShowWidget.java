package thebetweenlands.common.herblore.book.widgets;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.herblore.aspect.Aspect;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.IAspectType;
import thebetweenlands.util.AspectIconRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AspectSlideShowWidget extends ManualWidgetBase {
    public ItemStack itemStack;
    public ArrayList<IAspectType> aspects = new ArrayList<>();

    public int currentItems = 0;

    public AspectSlideShowWidget(int xStart, int yStart, ItemStack itemStack) {
        super(xStart, yStart);
        this.itemStack = itemStack;
    }

    public AspectSlideShowWidget(int xStart, int yStart, ArrayList<IAspectType> aspects) {
        super(xStart, yStart);
        this.aspects = aspects;
    }

    public AspectSlideShowWidget(int xStart, int yStart, IAspectType[] aspects) {
        super(xStart, yStart);
        Collections.addAll(this.aspects, aspects);
    }


    @Override
    public void drawForeGround() {
        List<IAspectType> subItems = aspects.subList(currentItems, currentItems + (aspects.size() - currentItems > 5 ? 6 : aspects.size() - currentItems));
        int width = 0;
        for (IAspectType aspect : subItems) {
            AspectIconRenderer.renderIcon(xStart + width, yStart, 16, 16, aspect.getIconIndex());
            PageLink link = new PageLink(xStart + width, yStart, 16, 16, aspect);
            if (link.category != null)
                pageLinks.add(link);
            if (mouseX >= xStart + width && mouseX <= xStart + 16 + width && mouseY >= yStart && mouseY <= yStart + 16) {
                List<String> tooltipData = new ArrayList<>();
                tooltipData.add(aspect.getName());
                tooltipData.add(ChatFormatting.GRAY + aspect.getType());
                tooltipData.add(ChatFormatting.GRAY + "Open guide book entry");
                renderTooltip(mouseX, mouseY, tooltipData, 0xffffff, 0xf0100010);
            }
            width += 18;
        }
    }

    @Override
    public void resize() {
        super.resize();
        if (itemStack != null)
            getAspects();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (manual.untilUpdate % 20 == 0) {
            if (currentItems + 1 < aspects.size() && aspects.size() - currentItems > 6) {
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
            if (currentItems + 1 < aspects.size() && aspects.size() - currentItems > 6) {
                currentItems++;
            } else
                currentItems = 0;
            drawForeGround();
        }
    }

    public void getAspects() {
        aspects.clear();
        for (Aspect aspect : AspectManager.get(Minecraft.getMinecraft().theWorld).getDiscoveredAspects(itemStack, AspectManager.getMergedDiscoveryContainer(Minecraft.getMinecraft().thePlayer))) {
            aspects.add(aspect.type);
        }
    }
}
