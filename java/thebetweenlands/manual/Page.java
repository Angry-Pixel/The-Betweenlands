package thebetweenlands.manual;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.manual.widgets.ManualWidgetsBase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bart on 23/11/2015.
 */
public class Page {
    public int pageNumber;
    public ArrayList<ItemStack> pageItems = new ArrayList<>();
    public IManualEntryEntity pageEntity;

    public String resourceLocation;
    public int xStartTexture = 0;
    public int xEndTexture = 0;
    public int yStartTexture = 0;
    public int yEndTexture = 0;
    public int textureWidth = 0;
    public int textureHeight = 0;


    public String pageName;

    public ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
    public boolean rightPage = false;

    public boolean isParent = false;

    public Page(String pageName, ArrayList<ManualWidgetsBase> widgets) {
        this.widgets = widgets;
        this.pageName = StatCollector.translateToLocal(pageName);
    }

    public Page(String pageName, ManualWidgetsBase... widgets) {
        Collections.addAll(this.widgets, widgets);
        this.pageName = StatCollector.translateToLocal(pageName);
    }

    public void init(GuiManualBase manual) {
        for (ManualWidgetsBase widget : widgets)
            widget.init(manual);
    }

    public Page setParent() {
        this.isParent = true;
        return this;
    }

    public Page addItems(ArrayList<ItemStack> items) {
        pageItems.addAll(items);
        return this;
    }


    public Page setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        rightPage = pageNumber % 2 == 0;

        return this;
    }


    public void setPageToRight() {
        for (ManualWidgetsBase widget : widgets)
            widget.setPageToRight();
    }

    public void draw(int mouseX, int mouseY) {
        for (ManualWidgetsBase widget : widgets)
            widget.draw(mouseX, mouseY);
    }

    public void keyTyped(char c, int key) {
        for (ManualWidgetsBase widget : widgets)
            widget.keyTyped(c, key);
    }

    public void mouseClicked(int x, int y, int button) {
        for (ManualWidgetsBase widget : widgets)
            widget.mouseClicked(x, y, button);
    }

    public void updateScreen() {
        for (ManualWidgetsBase widget : widgets)
            widget.updateScreen();
    }
}
