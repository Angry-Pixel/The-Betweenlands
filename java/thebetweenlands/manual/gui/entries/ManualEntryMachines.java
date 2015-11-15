package thebetweenlands.manual.gui.entries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.widgets.ItemWidget;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;
import thebetweenlands.manual.gui.widgets.PictureWidget;
import thebetweenlands.manual.gui.widgets.text.TextWidget;

import java.util.ArrayList;

/**
 * Created by Bart on 15/11/2015.
 */
public class ManualEntryMachines extends ManualEntry {
    public ArrayList<ItemStack> items = new ArrayList<ItemStack>();

    public ManualEntryMachines(String entryName, GuiManualBase manual, ItemStack machine, String imageLocation, int width, int height) {
        super("manual." + entryName + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + entryName + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, machine, 3)), new ManualPage(new PictureWidget(manual, 1, 1, imageLocation, width, height)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + entryName + ".description")));
        items.add(machine);
    }

    public ManualEntryMachines(String entryName, GuiManualBase manual, ItemStack machine, String imageLocation, int width, int height, int xStart, int yStart) {
        super("manual." + entryName + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + entryName + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, machine, 3)), new ManualPage(new PictureWidget(manual, xStart, yStart, imageLocation, width, height)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + entryName + ".description")));
        items.add(machine);
    }

}
