package thebetweenlands.common.herblore.book;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.Item;
import thebetweenlands.common.registries.ItemRegistry;

public class ManualCategory {
    class LimitedList<E> extends LinkedList<E> {
        private int limit;

        public LimitedList(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            boolean added = super.add(o);
            while (added && size() > this.limit) {
                super.remove();
            }
            return added;
        }
    }

    private List<Page> pages = new ArrayList<>();
    private List<Page> visiblePages = new ArrayList<>();

    private LimitedList<Integer> lastPages = new LimitedList<Integer>(100);

    private Page blankPage = new Page("blank", false, ItemRegistry.MANUAL_HL);
    private Page currentPageLeft = null;
    private Page currentPageRight = null;
    private int currentPage = 1;
    private int indexPages = 0;
    private int number;
    private String name;

    /**
     * The constructor for the manual categories
     *
     * @param pages      all pages in this category
     * @param number     a number that shows which where the category is located
     * @param manualType the type of manual it is. manualHL or manualGuideBook
     * @param name       the name of the category
     */
    public ManualCategory(ArrayList<Page> pages, int number, Item manualType, String name) {
        int pageNumber = 1;
        ArrayList<Page> buttonPages = new ArrayList<>();
        ArrayList<Page> tempPages = new ArrayList<>();
        for (Page page : pages) {
            page.setPageNumber(pageNumber);
            if (page.isParent)
                buttonPages.add(page);
            tempPages.add(page);
            page.setParentCategory(this);
            pageNumber++;
        }
        ArrayList<Page> buttonPagesNew;
        buttonPagesNew = PageCreators.pageCreatorButtons(buttonPages, manualType);
        indexPages = buttonPagesNew.size();
        this.pages.addAll(buttonPagesNew);
        this.pages.addAll(tempPages);
        this.number = number;
        this.name = name;
    }

    /**
     * @param pages                  all pages in this category
     * @param number                 a number that shows which where the category is located
     * @param manualType             the type of manual it is. manualHL or manualGuideBook
     * @param name                   the name of the category
     * @param customPageInitializing whether or not it has a custom way of doing pages
     * @param indexPages             if not needed use -1
     */
    public ManualCategory(ArrayList<Page> pages, int number, Item manualType, String name, boolean customPageInitializing, int indexPages) {
        ArrayList<Page> buttonPagesNew;
        if (!customPageInitializing) {
            int pageNumber = 1;
            ArrayList<Page> buttonPages = new ArrayList<>();
            ArrayList<Page> tempPages = new ArrayList<>();
            for (Page page : pages) {
                page.setPageNumber(pageNumber);
                if (page.isParent)
                    buttonPages.add(page);
                tempPages.add(page);
                pageNumber++;
            }
            buttonPagesNew = PageCreators.pageCreatorButtons(buttonPages, manualType);
            this.pages.addAll(buttonPagesNew);
            this.pages.addAll(tempPages);
            this.indexPages = buttonPagesNew.size();
        } else {
            this.pages.addAll(pages);
            this.indexPages = indexPages;
        }
        this.number = number;
        this.name = name;
    }

    /**
     * Initializing a category
     *
     * @param manual the current manual gui
     * @param force  whether to force it to reconstruct all pages
     */
    public void init(GuiManualHerblore manual, boolean force) {
        if (currentPageLeft == null || currentPageRight == null || force) {
            visiblePages.clear();
            for (Page page : pages)
                if (!page.isHidden || ManualManager.hasFoundPage(manual.player, page.unlocalizedPageName, manual.manualType))
                    visiblePages.add(page);
            if (!visiblePages.isEmpty()) {
                currentPageLeft = this.visiblePages.get(0);
                currentPageLeft.setPageToLeft();
                if (this.visiblePages.size() > 1)
                    currentPageRight = this.visiblePages.get(1);
                else
                    currentPageRight = blankPage;
                currentPageRight.setPageToRight();
            }
        }
        if (currentPageLeft != null)
            currentPageLeft.init(manual);
        if (currentPageRight != null) {
            currentPageRight.init(manual);
        }
    }

    /**
     * Returns the current page
     * @return
     */
    public int getCurrentPage() {
        return this.currentPage;
    }

    /**
     * Returns the category number
     * @return
     */
    public int getCategoryNumber() {
        return this.number;
    }

    /**
     * Returns the index pages
     * @return
     */
    public int getIndexPages() {
        return this.indexPages;
    }

    /**
     * Returns the name of this category
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a list of all visible pages
     * @return
     */
    public List<Page> getVisiblePages() {
        return this.visiblePages;
    }

    /**
     * Set the current display page
     *
     * @param pageNumber the page number
     * @param manual     the manual gui
     */
    public void setPage(int pageNumber, GuiManualHerblore manual) {
        if (currentPageLeft != null && currentPageRight != null) {
            this.lastPages.add(this.currentPage);
            if (pageNumber % 2 == 0)
                pageNumber--;
            if (pageNumber <= visiblePages.size() && pageNumber > 0) {
                currentPageLeft = visiblePages.get(pageNumber - 1);
                if (visiblePages.size() >= pageNumber + 1)
                    currentPageRight = visiblePages.get(pageNumber);
                else
                    currentPageRight = blankPage;
                currentPage = pageNumber;
            }
            currentPageLeft.init(manual);
            currentPageLeft.setPageToLeft();
            currentPageRight.init(manual);
            currentPageRight.setPageToRight();

            currentPageLeft.resize();
            currentPageRight.resize();
        }
    }

    /**
     * Flip to the next page
     *
     * @param manual the manual gui
     */
    public void nextPage(GuiManualHerblore manual) {
        if (currentPage + 2 <= visiblePages.size()) {
            setPage(currentPage + 2, manual);
        }
    }

    /**
     * Flip to the previous page
     *
     * @param manual the manual gui
     */
    public void previousPage(GuiManualHerblore manual) {
        if (currentPage - 2 >= 1) {
            setPage(currentPage - 2, manual);
        }
    }

    public void previousOpenPage(GuiManualHerblore manual) {
        if(this.lastPages.size() > 0) {
            int prevPage = this.lastPages.get(this.lastPages.size() - 1);
            this.lastPages.remove(this.lastPages.size() - 1);
            this.setPage(prevPage, manual);
            this.lastPages.remove(this.lastPages.size() - 1);
        }
    }

    /**
     * Draw the current pages
     *
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     */
    public void draw(int mouseX, int mouseY) {
        if (currentPageLeft != null)
            currentPageLeft.draw(mouseX, mouseY);
        if (currentPageRight != null)
            currentPageRight.draw(mouseX, mouseY);
    }

    /**
     * Passes on the typed key
     *
     * @param c   key in character
     * @param key key in number
     */
    public void keyTyped(char c, int key) {
        if (currentPageLeft != null)
            currentPageLeft.keyTyped(c, key);
        if (currentPageRight != null)
            currentPageRight.keyTyped(c, key);
    }

    /**
     * Passes on the mouse clicks
     *
     * @param x      the x coordinate of the click
     * @param y      the y coordinate of the click
     * @param button the button that was pressed
     */
    public void mouseClicked(int x, int y, int button) {
        if (currentPageLeft != null)
            currentPageLeft.mouseClicked(x, y, button);
        if (currentPageRight != null)
            currentPageRight.mouseClicked(x, y, button);
    }

    /**
     * Passes on the screen updates
     */
    public void updateScreen() {
        if (currentPageLeft != null)
            currentPageLeft.updateScreen();
        if (currentPageRight != null)
            currentPageRight.updateScreen();
    }

}
