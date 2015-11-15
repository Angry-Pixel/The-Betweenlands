package thebetweenlands.manual.gui.entries;

import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPageEntryButtons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Bart on 3-11-2015.
 */
public class ManualEntryEntryList extends ManualEntry {
    public ManualEntryEntryList(String unlocalizedEntryName, GuiManualBase manual, ArrayList<ManualEntryItem> entries) {
        super(unlocalizedEntryName);

        if (entries.size() > 8) {
            int entryAmount = entries.size();
            int times = 0;
            while (entryAmount > 0) {
                ArrayList<ManualEntryItem> entriesTemp = new ArrayList<>();
                entriesTemp.addAll(entries.subList(8 * times, (entryAmount > 8 ? 8 + 8 * times : 8 * times + entryAmount)));
                ManualPageEntryButtons page = new ManualPageEntryButtons(manual, entriesTemp);
                page.setPageNumber(times + 1);
                pages.add(page);
                entryAmount -= 8;
                times++;
            }
        } else {
            ManualPageEntryButtons page = new ManualPageEntryButtons(manual, entries);
            page.setPageNumber(pages.size() + 1);
            pages.add(page);
        }

        if (pages.size() >= 1)
            currentPageLeft = pages.get(0);
        if (pages.size() >= 2)
            currentPageRight = pages.get(1);
    }


    public ManualEntryEntryList(String unlocalizedEntryName, GuiManualBase manual, ArrayList<ManualEntry> entries, String resourceLocation) {
        super(unlocalizedEntryName);

        int index = 0;
        if (entries.size() > 8) {
            int entryAmount = entries.size();
            int times = 0;
            while (entryAmount > 0) {
                ArrayList<ManualEntry> entriesTemp = new ArrayList<>();
                entriesTemp.addAll(entries.subList(8 * times, (entryAmount > 8 ? 8 + 8 * times : 8 * times + entryAmount)));
                ManualPageEntryButtons page = new ManualPageEntryButtons(manual, entriesTemp, resourceLocation, index);
                page.setPageNumber(times + 1);
                pages.add(page);
                entryAmount -= 8;
                index += 8;
                times++;
            }
        } else {
            ManualPageEntryButtons page = new ManualPageEntryButtons(manual, entries, resourceLocation, 0);
            page.setPageNumber(pages.size() + 1);
            pages.add(page);
        }

        if (pages.size() >= 1)
            currentPageLeft = pages.get(0);
        if (pages.size() >= 2)
            currentPageRight = pages.get(1);
    }

}
