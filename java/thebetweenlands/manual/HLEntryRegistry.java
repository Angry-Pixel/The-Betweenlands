package thebetweenlands.manual;

import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Bart on 06/12/2015.
 */
public class HLEntryRegistry {
    public static ArrayList<ManualCategory> CATEGORIES = new ArrayList<>();
    public static ManualCategory aspectCategory;
    public static ArrayList<Page> aspectPages = new ArrayList<>();
    public static ArrayList<Page> itemPages = new ArrayList<>();

    public static ManualManager.EnumManual manualType = ManualManager.EnumManual.HL;

    public static void init() {
        initAspectEntries();
    }


    public static void initAspectEntries() {
        aspectPages.clear();
        itemPages.clear();
        for (IAspectType aspect : AspectManager.ASPECT_TYPES) {
            aspectPages.addAll(PageCreators.AspectPages(aspect, manualType ));
        }
        
        Map<AspectManager.AspectItem, List<AspectManager.AspectItemEntry>> matchedAspects = AspectManager.getRegisteredItems();
        for (Map.Entry<AspectManager.AspectItem, List<AspectManager.AspectItemEntry>> e : matchedAspects.entrySet()) {
            if (e.getKey() != null)
                itemPages.addAll(PageCreators.AspectItemPages(e.getKey(), manualType));
        }

        ArrayList<Page> temp = new ArrayList<>();
        while (itemPages.size() > 0) {
            Page currentFirst = null;
            for (Page page : itemPages) {
                if (currentFirst == null)
                    currentFirst = page;
                else {
                    String pageName = page.pageName.toLowerCase();
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase();
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if(charactersFirst.length > i) {
                            if (((Character) characters[i]).compareTo(charactersFirst[i]) > 0) {
                                break;
                            } else if (((Character) characters[i]).compareTo(charactersFirst[i]) < 0) {
                                currentFirst = page;
                                break;
                            }
                        }
                    }
                }
            }
            itemPages.remove(currentFirst);
            temp.add(currentFirst);
        }
        aspectPages.addAll(temp);
        
        
        aspectCategory = new ManualCategory(aspectPages, 1, manualType, "aspectCategory");
        CATEGORIES.add(aspectCategory);
    }
}