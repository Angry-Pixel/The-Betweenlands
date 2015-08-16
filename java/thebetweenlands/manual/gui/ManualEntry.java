package thebetweenlands.manual.gui;

import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;

import java.util.ArrayList;

/**
 * Created by Bart on 11-8-2015.
 */
public class ManualEntry {

    public ArrayList<ManualPage> pages = new ArrayList<>();

    public ManualPage currentPageLeft;
    public ManualPage currentPageRight;
    public int currentPage = 0;
    public ManualPage blankPage = new ManualPage();

    public ManualEntry(ManualPage... pages){
        int pageNumber = 1;
        if(pages.length < 2) {
            currentPageRight = blankPage;
            if (pages.length < 1)
                currentPageLeft = blankPage;
        }
        for(ManualPage page:pages) {
            if (pageNumber == 1)
                currentPageLeft = page;
            if (pageNumber == 2)
                currentPageRight = page;
            page.setPageNumber(pageNumber);
            this.pages.add(page);
            pageNumber++;
            for (ManualPage page1:page.setManualEntry(this)){
                if (pageNumber == 2)
                    currentPageRight = page1;
                page1.setPageNumber(pageNumber);
                this.pages.add(page1);
                pageNumber++;
            }
        }
    }

    public void clear(){
        currentPageLeft.clear();
        currentPageRight.clear();
    }

    public void draw(int mouseX, int mouseY){
        currentPageLeft.draw(mouseX, mouseY);
        currentPageRight.draw(mouseX, mouseY);
    }

    public void keyTyped(char c, int key) {
        currentPageLeft.keyTyped(c, key);
        currentPageRight.keyTyped(c, key);
    }

    public void mouseClicked(int x, int y, int button) {
        currentPageLeft.mouseClicked(x, y, button);
        currentPageRight.mouseClicked(x, y, button);
    }

    public void updateScreen() {
        currentPageLeft.updateScreen();
        currentPageRight.updateScreen();
    }

    public void nextPage(){
        if(currentPage + 2 < pages.size()){
            currentPageLeft = getNextPageLeft();
            currentPageRight = getNextPageRight();
            currentPage += 2;
        }
    }

    public void previousPage(){
        if (currentPage - 2 >= 0){
            currentPageLeft = getPreviousPageLeft();
            currentPageRight = getPreviousPageRight();
            currentPage -= 2;
        }
    }

    public ManualPage getPreviousPageLeft(){
        return pages.get(currentPage - 2);
    }
    public ManualPage getPreviousPageRight(){
        return pages.get(currentPage - 1);
    }

    public ManualPage getNextPageLeft(){
        return pages.get(currentPage + 2);
    }
    public ManualPage getNextPageRight(){
        if(pages.size() > currentPage + 3)
            return pages.get(currentPage + 3);
        return blankPage;
    }
}
