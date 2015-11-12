package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.entries.ManualEntry;
import thebetweenlands.manual.gui.widgets.text.TextContainer;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents;
import thebetweenlands.recipes.CompostRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 6-11-2015.
 */
public class ButtonWidget extends ManualWidgetsBase {
    private ArrayList<ItemStack> items = new ArrayList<>();
    private ManualEntry entry;
    private TextContainer textContainer;

    public static int width = 100;
    public static int height = 16;
    int untilUpdate = 0;
    int currentItem;

    public ButtonWidget(GuiManualBase manual, int xStart, int yStart, ItemStack item, ManualEntry entry) {
        super(manual, xStart, yStart);
        items.add(item);
        this.entry = entry;
        this.textContainer = new TextContainer(this.xStart + 18, this.yStart + 4, 100, 16, entry.entryName);
        this.init();
    }

    public ButtonWidget(GuiManualBase manual, int xStart, int yStart, ArrayList<ItemStack> items, ManualEntry entry) {
        super(manual, xStart, yStart);
        this.items = items;
        this.entry = entry;
        this.textContainer = new TextContainer(this.xStart + 18, this.yStart + 4, 100, 16, entry.entryName);
        this.init();
    }

    @Override
    public void setPageToRight() {
        super.setPageToRight();
        this.textContainer = new TextContainer(this.xStart + 18, this.yStart + 4, 100, 16, entry.entryName);
        this.init();
    }

    public void init() {
        this.textContainer.setCurrentScale(1f).setCurrentColor(0x808080).setCurrentFormat("");
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatScale(1.0F));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatColor(0x808080));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatTooltip("N/A"));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("bold", EnumChatFormatting.BOLD));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("italic", EnumChatFormatting.ITALIC));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("underline", EnumChatFormatting.UNDERLINE));

        try {
            this.textContainer.parse();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {

        renderItem(xStart, yStart, items.get(currentItem), false);
        textContainer.render();
    }

    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 2 && x >= xStart && x <= xStart + 16 && y >= yStart && y <= yStart + height) {
            if (currentItem + 1 < items.size()) {
                currentItem++;
            } else
                currentItem = 0;
            drawForeGround();
            untilUpdate = 0;
        } else if (mouseButton == 0 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height){
            manual.changeTo(entry);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (untilUpdate >= 20) {
            if (currentItem + 1 < items.size()) {
                currentItem++;
            } else
                currentItem = 0;
            drawForeGround();
            untilUpdate = 0;
        } else
            untilUpdate++;
    }

    @Override
    public void resize() {
        super.resize();
        this.textContainer = new TextContainer(this.xStart + 18, this.yStart + 4, 116, 144, entry.entryName);
        this.init();
    }
}
