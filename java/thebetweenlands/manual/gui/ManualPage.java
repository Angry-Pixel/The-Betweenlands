package thebetweenlands.manual.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;
import thebetweenlands.manual.gui.widgets.TextWidget;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bart on 11-8-2015.
 */
public class ManualPage {
    public int x;
    public int y;
    public int pageNumber;

    public ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();

    boolean rightPage = false;

    public ManualEntry entry;

    public ManualPage( ManualWidgetsBase... widgets){
        Collections.addAll(this.widgets, widgets);
    }

    public ArrayList<ManualPage> setManualEntry(ManualEntry entry){
        ArrayList<ManualPage> pages = new ArrayList<>();
        this.entry = entry;

        for(ManualWidgetsBase widget:widgets){
            if(widget instanceof TextWidget){
                FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                String[] words = ((TextWidget) widget).text.split(" ");

                int widthLine = 0;
                String text = "";
                int page = 0;

                int heightPage = 0;

                for (String word : words) {
                    int widthWord = fontRenderer.getStringWidth(word + " ");
                    if (word.equals("/n/")) {
                        heightPage += 9;
                        widthLine = 0;
                    } else if (word.contains("<color:") || word.equals("</color>") || word.contains("<tooltip:") || word.equals("</tooltip>")) {
                        widthLine += 0;
                    } else if (widthLine + widthWord <= GuiManualBase.WIDTH - widget.unchangedXStart) {
                        widthLine += widthWord;
                    } else {
                        heightPage += 9;
                        if(heightPage > GuiManualBase.HEIGHT - widget.yStart - 10) {
                            heightPage = 0;
                            if(page > 0)
                                pages.add(new ManualPage(new TextWidget(widget.manual, widget.unchangedXStart, widget.yStart, text)));
                            else
                                ((TextWidget) widget).text = text;
                            page++;
                            text = "";
                        }
                        widthLine = widthWord;
                    }
                    text += word + " ";
                }
                if(page > 0 && (widthLine > 0 || heightPage > 0))
                    pages.add(new ManualPage(new TextWidget(widget.manual, 5, 5, text)));
            }
        }
        return pages;
    }

    public void setPageNumber(int pageNumber){
        this.pageNumber = pageNumber;
        rightPage = pageNumber%2 == 0;

        for (ManualWidgetsBase widget:widgets)
            if(rightPage)
                widget.setPageToRight();
    }


    public void clear(){
        widgets.clear();
    }

    public void draw(int mouseX, int mouseY){
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
