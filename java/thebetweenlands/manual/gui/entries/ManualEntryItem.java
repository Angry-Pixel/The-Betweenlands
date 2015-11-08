package thebetweenlands.manual.gui.entries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.pages.ManualPageRecipe;
import thebetweenlands.manual.gui.widgets.*;
import thebetweenlands.manual.gui.widgets.text.TextWidget;

import java.util.ArrayList;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualEntryItem extends ManualEntry {
    public ArrayList<ItemStack> items = new ArrayList<ItemStack>();

    public ManualEntryItem(IManualEntryItem item, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + item.manualName(0) + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + item.manualName(0) + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, new ItemStack(item.getItem()), 3)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + item.manualName(0) + ".description")), new ManualPageRecipe(manual, item, recipes));
        this.items.add(new ItemStack(item.getItem()));
    }

    public ManualEntryItem(ArrayList<ItemStack> item, String name, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + name + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + name + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, item, 3)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + name + ".description")), new ManualPage(recipes));
        this.items = item;
    }

    public ManualEntryItem(String name, ArrayList<IManualEntryItem> items, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + name + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + name + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, 3, items)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + name + ".description")), new ManualPageRecipe(manual, items, recipes));
        for (IManualEntryItem item:items)
            this.items.add(new ItemStack(item.getItem()));
    }

    public ManualEntryItem(IManualEntryItem item, int meta, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + item.manualName(meta) + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + item.manualName(meta) + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, new ItemStack(item.getItem(), 1, meta), 3)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + item.manualName(meta) + ".description")), new ManualPage(recipes));
        this.items.add(new ItemStack(item.getItem(), 1, meta));
    }
}
