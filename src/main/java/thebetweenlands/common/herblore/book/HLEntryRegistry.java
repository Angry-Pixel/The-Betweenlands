package thebetweenlands.common.herblore.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.book.widgets.ButtonWidget;
import thebetweenlands.common.herblore.book.widgets.PictureWidget;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class HLEntryRegistry {
    public static ArrayList<ManualCategory> CATEGORIES = new ArrayList<>();
    public static ManualCategory aspectCategory;
    public static ArrayList<Page> aspectPages = new ArrayList<>();
    public static ArrayList<Page> itemPages = new ArrayList<>();
    public static ArrayList<Page> elixirPages = new ArrayList<>();

    public static Item manualType = ItemRegistry.MANUAL_HL;

    public static ManualCategory elixirCategory;

    /**
     * initializes the HL book
     */
    @SideOnly(Side.CLIENT)
    public static void init() {
        CATEGORIES.clear();
        initAspectEntries();
        initElixirEntries();
    }

    /**
     * initializes the aspect pages
     */
    @SideOnly(Side.CLIENT)
    public static void initAspectEntries() {
        int indexPages = 3;
        aspectPages.clear();
        itemPages.clear();
        ArrayList<Page> temp = new ArrayList<>();
        ArrayList<Page> entryPages = new ArrayList<>();
        aspectPages.add(new Page("aspect_info", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 454, 271, 1024.0D, 1024.0D)));
        for (IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
            aspectPages.addAll(PageCreators.AspectPages(aspect, manualType));
        }

        entryPages.add(new Page("aspect_list", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 162, 271, 1024.0D, 1024.0D)));
        int pageNumber = 1;
        for (Page page : aspectPages) {
            page.setPageNumber(pageNumber);
            temp.add(page);
            pageNumber++;
        }
        entryPages.addAll(PageCreators.pageCreatorButtons(temp, manualType));
        indexPages += PageCreators.pageCreatorButtons(temp, manualType).size();

        Map<AspectItem, List<AspectManager.AspectItemEntry>> matchedAspects = AspectManager.getRegisteredItems();
        for (Map.Entry<AspectItem, List<AspectManager.AspectItemEntry>> e : matchedAspects.entrySet()) {
            if (e.getKey() != null)
                itemPages.addAll(PageCreators.AspectItemPages(e.getKey(), manualType));
        }

        ArrayList<Page> tempItems = new ArrayList<>();
        int tempNum = pageNumber;
        pageNumber++;
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
                        if (charactersFirst.length > i) {
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
            if (currentFirst != null)
                currentFirst.setPageNumber(pageNumber);
            pageNumber++;
            tempItems.add(currentFirst);
        }
        tempItems.add(0, new Page("ingredient_info", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 600, 271, 1024.0D, 1024.0D)).setPageNumber(tempNum));
        entryPages.add(new Page("ingredient_list", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 308, 271, 1024.0D, 1024.0D)));
        ArrayList<Page> buttons = PageCreators.pageCreatorButtons(tempItems, manualType);
        indexPages += buttons.size();
        entryPages.addAll(buttons);
        entryPages.addAll(temp);
        entryPages.addAll(tempItems);
        entryPages.add(0, new Page("intro_1", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 16, 271, 1024.0D, 1024.0D), new ButtonWidget(31, 49, 87, 9, 6, false), new ButtonWidget(31, 68, 55, 9, 1, true), new ButtonWidget(31, 86, 73, 9, tempNum, true)));
        aspectCategory = new ManualCategory(entryPages, 1, manualType, "aspect_category", true, indexPages);
        CATEGORIES.add(aspectCategory);
    }

    @SideOnly(Side.CLIENT)
    public static void initElixirEntries() {
        elixirPages.clear();
        ArrayList<Page> infusionPages = new ArrayList<>();
        ArrayList<Page> antiInfusionPages = new ArrayList<>();

        for (ElixirEffect effect : ElixirEffectRegistry.getEffects()) {
            if (effect.shouldShowInBook()) {
                if (effect.isAntiInfusion())
                    antiInfusionPages.addAll(PageCreators.elixirPages(ItemRegistry.ELIXIR.getElixirItem(effect, 1, 1, 0), manualType, effect));
                else
                    infusionPages.addAll(PageCreators.elixirPages(ItemRegistry.ELIXIR.getElixirItem(effect, 1, 1, 0), manualType, effect));
            }
        }

        ArrayList<Page> temp = new ArrayList<>();
        while (infusionPages.size() > 0) {
            Page currentFirst = null;
            for (Page page : infusionPages) {
                if (currentFirst == null)
                    currentFirst = page;
                else {
                    String pageName = page.pageName.toLowerCase();
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase();
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if (charactersFirst.length > i) {
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
            infusionPages.remove(currentFirst);
            temp.add(currentFirst);
        }
        infusionPages.clear();
        infusionPages.addAll(temp);
        infusionPages.add(0, new Page("infusions", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 16, 451, 1024.0D, 1024.0D)).setPageNumber(1));
        temp.clear();
        while (antiInfusionPages.size() > 0) {
            Page currentFirst = null;
            for (Page page : antiInfusionPages) {
                if (currentFirst == null)
                    currentFirst = page;
                else {
                    String pageName = page.pageName.toLowerCase();
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase();
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if (charactersFirst.length > i) {
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
            antiInfusionPages.remove(currentFirst);
            temp.add(currentFirst);
        }
        antiInfusionPages.clear();
        antiInfusionPages.addAll(temp);
        antiInfusionPages.add(0, new Page("infusions", false, manualType, new PictureWidget(16, 12, "thebetweenlands:textures/gui/manual/manual_hl.png", 122, 150, 162, 451, 1024.0D, 1024.0D)).setPageNumber(infusionPages.size()));
        elixirPages.clear();
        elixirPages.addAll(infusionPages);
        elixirPages.addAll(antiInfusionPages);
        elixirCategory = new ManualCategory(elixirPages, 2, manualType, "elixir_category");
        CATEGORIES.add(elixirCategory);
    }
}
