package thebetweenlands.manual;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 23/11/2015.
 */
public class ManualCategory {
    public List<Page> pages = new ArrayList<>();

    public Page blankPage = new Page("blank");
    public Page currentPageLeft;
    public Page currentPageRight;
    public int currentPage = 1;
    public int indexPages = 0;

    public ManualCategory(ArrayList<Page> pages) {
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
        currentPageLeft = this.pages.get(0);
        if (this.pages.size() > 1)
            currentPageRight = this.pages.get(1);
        else
            currentPageRight = blankPage;
        currentPageRight.setPageToRight();
    }


    public void init(GuiManualBase manual) {
        if (currentPageLeft != null)
            currentPageLeft.init(manual);
        if (currentPageRight != null)
            currentPageRight.init(manual);
    }


    public void setPage(int pageNumber, GuiManualBase manual) {
        if (pageNumber % 2 == 0)
            pageNumber--;
        if (pages.size() >= pageNumber) {
            currentPageLeft = pages.get(pageNumber - 1);
            if (pages.size() >= pageNumber + 1)
                currentPageRight = pages.get(pageNumber);
            else
                currentPageRight = blankPage;
            currentPage = pageNumber;
        }
        currentPageRight.setPageToRight();
        currentPageLeft.init(manual);
        currentPageRight.init(manual);
    }

    public void nextPage(GuiManualBase manual) {
        if (currentPage + 2 <= pages.size()) {
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
