package thebetweenlands.manual.gui.widgets;

import java.util.ArrayList;

/**
 * Created by Bart on 12-8-2015.
 */
public class ManualToolTip {
    public int x;
    public int y;
    public int width;
    public int height;
    public ArrayList<String> tooltips;
    public int color;
    public int color2;

    public ManualToolTip(int x, int y, int width, int height, ArrayList<String> tooltips, int color, int color2){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tooltips = tooltips;
        this.color = color;
        this.color2 = color2;
    }

    public void renderTooltip(int mouseX, int mouseY){
        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y+height)
            ManualWidgetsBase.renderTooltip(mouseX, mouseY, tooltips, color, color2);
    }
}
