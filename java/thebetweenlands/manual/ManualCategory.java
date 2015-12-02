package thebetweenlands.manual;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 23/11/2015.
 */
public class ManualCategory {
    public List<Page> pages = new ArrayList<>();
    public List<Page> visiblePages = new ArrayList<>();

    public Page blankPage = new Page("blank");
    public Page currentPageLeft = null;
    public Page currentPageRight = null;
    public int currentPage = 1;
    public int indexPages = 0;
    public int number;

    public ManualCategory(ArrayList<Page> pages, int number) {
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
        ArrayList<Page> buttonPagesNew;
        buttonPagesNew = PageCreators.pageCreatorButtons(buttonPages);
        indexPages = buttonPagesNew.size();
        this.pages.addAll(buttonPagesNew);
        this.pages.addAll(tempPages);
        this.number = number;
    }


    public void init(GuiManualBase manual, boolean force) {
        if (currentPageLeft == null || currentPageRight == null || force) {
            visiblePages.clear();
            for (Page page : pages)
                if (!page.isHidden || ManualManager.hasFoundPage(Minecraft.getMinecraft().theWorld.func_152378_a(manual.player), page.unlocalizedPageName))
                    visiblePages.add(page);
            currentPageLeft = this.visiblePages.get(0);
            if (this.visiblePages.size() > 1)
                currentPageRight = this.visiblePages.get(1);
            else
                currentPageRight = blankPage;
            currentPageRight.setPageToRight();
        }
        if (currentPageLeft != null)
            currentPageLeft.init(manual);
        if (currentPageRight != null) {
            currentPageRight.init(manual);
        }
    }


    public void setPage(int pageNumber, GuiManualBase manual) {
        if (pageNumber % 2 == 0)
            pageNumber--;
        if (pageNumber <= visiblePages.size()) {
            currentPageLeft = visiblePages.get(pageNumber - 1);
            if (visiblePages.size() >= pageNumber + 1)
                currentPageRight = visiblePages.get(pageNumber);
            else
                currentPageRight = blankPage;
            currentPage = pageNumber;
        }
        currentPageLeft.init(manual);
        currentPageRight.init(manual);
        currentPageRight.setPageToRight();
    }

    public void nextPage(GuiManualBase manual) {
        if (currentPage + 2 <= visiblePages.size()) {
            setPage(currentPage + 2, manual);
        }
    }

    public void previousPage(GuiManualBase manual) {
        if (currentPage - 2 >= 1) {
            setPage(currentPage - 2, manual);
        }
    }


    public void draw(int mouseX, int mouseY) {
        if (currentPageLeft != null)
            currentPageLeft.draw(mouseX, mouseY);
        if (currentPageRight != null)
            currentPageRight.draw(mouseX, mouseY);
    }

    public void keyTyped(char c, int key) {
        if (currentPageLeft != null)
            currentPageLeft.keyTyped(c, key);
        if (currentPageRight != null)
            currentPageRight.keyTyped(c, key);
    }

    public void mouseClicked(int x, int y, int button) {
        if (currentPageLeft != null)
            currentPageLeft.mouseClicked(x, y, button);
        if (currentPageRight != null)
            currentPageRight.mouseClicked(x, y, button);
    }

    public void updateScreen() {
        if (currentPageLeft != null)
            currentPageLeft.updateScreen();
        if (currentPageRight != null)
            currentPageRight.updateScreen();
    }

}
