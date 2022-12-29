package thebetweenlands.common.herblore.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.book.widgets.ManualWidgetBase;
import thebetweenlands.common.registries.ItemRegistry;

public class Page {
    public int pageNumber;
    public List<ItemStack> pageItems = NonNullList.create();
    public List<IAspectType> pageAspects = new ArrayList<>();

    public String resourceLocation;
    public int xStartTexture = 0;
    public int xEndTexture = 0;
    public int yStartTexture = 0;
    public int yEndTexture = 0;
    public int textureWidth = 0;
    public int textureHeight = 0;

    public boolean isHidden = false;

    public String pageName;
    public String unlocalizedPageName;
    public String localizedPageName;

    public List<ManualWidgetBase> widgets = new ArrayList<>();
    public boolean rightPage = false;
    public ManualCategory parentCategory;
    public boolean isParent = false;

    /**
     * Constructor for pages
     *
     * @param pageName   the name of the page
     * @param widgets    a array containing all widgets for this page
     * @param isHidden   whether or not it should be shown without unlocking it
     * @param manualType the type of manual it is in. Either manualHL or manualGuideBook
     */
    public Page(String pageName, List<ManualWidgetBase> widgets, boolean isHidden, Item manualType) {
        this.widgets = widgets;
        this.pageName = I18n.translateToLocal("manual." + pageName + ".title");
        this.unlocalizedPageName = pageName;
        this.isHidden = isHidden;
        if (isHidden && manualType == ItemRegistry.MANUAL_HL) {
            ManualManager.findablePagesHL.add(pageName);
            ManualManager.findablePagesAll.add(pageName);
        }
    }

    /**
     * Constructor for pages
     *
     * @param pageName   the name of the page
     * @param isHidden   whether or not it should be shown without unlocking it
     * @param manualType the type of manual it is in. Either manualHL or manualGuideBook
     * @param widgets    the widgets that need to be in this page
     */
    public Page(String pageName, boolean isHidden, Item manualType, ManualWidgetBase... widgets) {
        Collections.addAll(this.widgets, widgets);
        this.pageName = I18n.translateToLocal("manual." + pageName + ".title");
        this.unlocalizedPageName = pageName;
        this.isHidden = isHidden;
        if (isHidden && manualType == ItemRegistry.MANUAL_HL) {
            ManualManager.findablePagesHL.add(pageName);
            ManualManager.findablePagesAll.add(pageName);
        }
    }

    /**
     * Initializes the widgets in this page
     *
     * @param manual the type of manual it is in. Either manualHL or manualGuideBook
     */
    public void init(GuiManualHerblore manual) {
        for (ManualWidgetBase widget : widgets)
            widget.init(manual);
    }

    /**
     * Sets this page as a parent of a group of pages
     *
     * @return this page
     */
    public Page setParent() {
        this.isParent = true;
        return this;
    }

    /**
     * Adds the items a page contains
     *
     * @param items the items the page contains
     * @return this page
     */
    public Page addItems(List<ItemStack> items) {
        pageItems.addAll(items);
        return this;
    }

    /**
     * Adds 1 item a page contains
     *
     * @param item the item the page contains
     * @return this page
     */
    public Page addItem(ItemStack item) {
        pageItems.add(item);
        return this;
    }

    /**
     * Adds the aspect a page contains
     *
     * @param aspect the aspect the page contains
     * @return this page
     */
    public Page setAspect(IAspectType aspect) {
        pageAspects.add(aspect);
        return this;
    }

    /**
     * Adds the aspect a page contains
     *
     * @param aspects the aspects the page contains
     * @return this page
     */
    public Page setAspects(IAspectType[] aspects) {
        Collections.addAll(pageAspects, aspects);
        return this;
    }

    /**
     * Sets the page number
     *
     * @param pageNumber the number of the page
     * @return this page
     */
    public Page setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        rightPage = pageNumber % 2 == 0;

        return this;
    }

    /**
     * Sets the page to the left side, default is left but to be sure with reusing them
     */
    public void setPageToLeft() {
        for (ManualWidgetBase widget : widgets)
            widget.setPageToLeft();
    }

    /**
     * Sets the page to the right side
     */
    public void setPageToRight() {
        for (ManualWidgetBase widget : widgets)
            widget.setPageToRight();
    }

    /**
     * Draws the widgets
     *
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     */
    public void draw(int mouseX, int mouseY) {
        for (ManualWidgetBase widget : widgets)
            widget.draw(mouseX, mouseY);
        for (ManualWidgetBase widget : widgets)
            widget.drawToolTip();
    }

    /**
     * Passes on the key typed
     *
     * @param c   the key as character
     * @param key the key as number
     */
    public void keyTyped(char c, int key) {
        for (ManualWidgetBase widget : widgets)
            widget.keyTyped(c, key);
    }

    /**
     * Passes on the mouse click
     *
     * @param x      the x coordinate of the mouse click
     * @param y      the y coordinate of the mouse click
     * @param button the mouse button pressed
     */
    public void mouseClicked(int x, int y, int button) {
        for (ManualWidgetBase widget : widgets)
            widget.mouseClicked(x, y, button);
    }

    /**
     * Passes on a screen update
     */
    public void updateScreen() {
        for (ManualWidgetBase widget : widgets)
            widget.updateScreen();
    }

    /**
     * Resizes everything if needed
     */
    public void resize() {
        for (ManualWidgetBase widget : widgets)
            widget.resize();
    }


    /**
     * Sets the category this page is in. Might be usefull for pagelinking
     * @param category the category this page is in
     * @return this page
     */
    public Page setParentCategory(ManualCategory category){
        parentCategory = category;
        return this;
    }

    public Page setLocalizedPageName(String text){
        pageName = text;
        return this;
    }
}
