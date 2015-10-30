package thebetweenlands.manual.gui.entries;

import net.minecraft.item.ItemStack;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.pages.ManualPageRecipe;
import thebetweenlands.manual.gui.widgets.*;

import java.util.ArrayList;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualEntryItem extends ManualEntry {

    public ManualEntryItem(IManualEntryItem item, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super(new ManualPage(new TextWidget(manual, 5, 5, "manual." + item.manualName(0) + ".title"), new ItemWidget(manual, (127 / 2) - 24, 77, new ItemStack(item.getItem()), 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual." + item.manualName(0) + ".description")), new ManualPageRecipe(manual, item, recipes));
    }

    public ManualEntryItem(ArrayList<ItemStack> item, String name, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super(new ManualPage(new TextWidget(manual, 5, 5, "manual." + name + ".title"), new ItemWidget(manual, (127 / 2) - 24, 77, item, 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual." + name + ".description")), new ManualPage(recipes));
    }

    public ManualEntryItem(IManualEntryItem item, int meta, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super(new ManualPage(new TextWidget(manual, 5, 5, "manual." + item.manualName(meta) + ".title"), new ItemWidget(manual, (127 / 2) - 24, 77, new ItemStack(item.getItem(), 1, meta), 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual." + item.manualName(meta) + ".description")), new ManualPage(recipes));
    }
}
