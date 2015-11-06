package thebetweenlands.manual.gui.entries;

import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPageEntryButtons;

import java.util.ArrayList;

/**
 * Created by Bart on 3-11-2015.
 */
public class ManualEntryEntryList extends ManualEntry {
    public ManualEntryEntryList(String unlocalizedEntryName, GuiManualBase manual, ArrayList<ManualEntryItem> entries) {
        super(unlocalizedEntryName);

        if (entries.size() > 10) {
            int entryAmount = entries.size();
            int times = 0;
            while (entryAmount > 0) {
                ArrayList<ManualEntryItem> entriesTemp = new ArrayList<>();
                entriesTemp.addAll(entries.subList(10 * times, (entryAmount > 10 ? 10 + 10 * times : 10 * times + entryAmount)));
                System.out.println(entriesTemp.size());
                ManualPageEntryButtons page = new ManualPageEntryButtons(manual, entriesTemp);
                page.setPageNumber(times + 1);
                pages.add(page);
                entryAmount -= 10;
                times++;
            }
        } else {
            pages.add(new ManualPageEntryButtons(manual, entries));
        }

        if (pages.size() >= 1)
            currentPageLeft = pages.get(0);
        if (pages.size() >= 2)
            currentPageRight = pages.get(1);
    }

}
