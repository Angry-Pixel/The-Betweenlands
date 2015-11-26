package thebetweenlands.manual;

import net.minecraft.item.ItemStack;
import thebetweenlands.manual.widgets.*;
import thebetweenlands.manual.widgets.text.TextWidget;

import java.util.ArrayList;

/**
 * Created by Bart on 23/11/2015.
 */
public class PageCreators {

    public static int craftingRecipeHeight = CraftingRecipeWidget.height + 5;
    public static int smeltingRecipeHeight = SmeltingRecipeWidget.height + 5;
    public static int compostRecipeHeight = CompostRecipeWidget.height + 5;
    public static int pestleAndMortarRecipeHeight = PestleAndMortarRecipeWidget.height + 5;
    public static int purifierRecipeHeight = PurifierRecipeWidget.height + 5;
    public static int rubberTabRecipeHeight = RubberTabWidget.height + 5;

    public static ArrayList<Page> pageCreatorButtons(ArrayList<Page> pages) {
        ArrayList<Page> newPages = new ArrayList<>();
        int pageAmount = pages.size();
        int times = 0;
        while (pageAmount > 0) {
            ArrayList<Page> pagesTemp = new ArrayList<>();
            pagesTemp.addAll(pages.subList(8 * times, (pageAmount > 8 ? 8 + 8 * times : 8 * times + pageAmount)));
            int height = 0;
            ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
            for (Page page : pagesTemp) {
                widgets.add(new ButtonWidget(15, 10 + height, page));
                height += 18;
            }
            newPages.add(new Page("index" + times, (ArrayList<ManualWidgetsBase>) widgets.clone()));
            widgets.clear();
            pagesTemp.clear();
            pageAmount -= 8;
            times++;
        }

        return newPages;
    }

    public static ArrayList<Page> pageCreatorItems(IManualEntryItem item) {
        ArrayList<Page> newPages = new ArrayList<>();
        String title = item.manualName(0);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i <= item.metas(); i++)
            items.add(new ItemStack(item.getItem(), 1, i));
        newPages.add(new Page(title, new TextWidget(15, 10, "manual." + item.manualName(0) + ".title", 1.5f), new ItemWidget(49, 77, item, 3)).addItems(items).setParent());
        newPages.add(new Page(title, new TextWidget(16, 10, "manual." + item.manualName(0) + ".description")));
        ArrayList<IManualEntryItem> manualItem = new ArrayList<>();
        manualItem.add(item);
        newPages.addAll(recipePages(manualItem, title));
        return newPages;
    }

    public static ArrayList<Page> pageCreatorItems(String name, ArrayList<IManualEntryItem> manualItems) {
        ArrayList<Page> newPages = new ArrayList<>();
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (IManualEntryItem item : manualItems) {
            for (int i = 0; i <= item.metas(); i++)
                items.add(new ItemStack(item.getItem(), 1, i));
        }
        newPages.add(new Page(name, new TextWidget(15, 10, "manual." + name + ".title", 1.5f), new ItemWidget(49, 77, items, 3)).addItems(items).setParent());
        newPages.add(new Page(name, new TextWidget(16, 10, "manual." + name + ".description")));
        newPages.addAll(recipePages(manualItems, name));
        return newPages;
    }


    public static ArrayList<Page> recipePages(ArrayList<IManualEntryItem> items, String title) {
        ArrayList<Page> newPages = new ArrayList<>();
        int height = 10;
        int type = 0;
        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        while (type <= 6) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (IManualEntryItem item : items) {
                for (int i : item.recipeType(0)) {
                    for (int j = 0; j <= item.metas(); j++)
                        if (i == type)
                            itemStacks.add(new ItemStack(item.getItem(), 1, j));
                }
            }

            if (itemStacks.size() > 0) {
                int latestAdded = 0;
                switch (type) {
                    case 1:
                        widgets.add(new SmeltingRecipeWidget(itemStacks, 15, height));
                        height += smeltingRecipeHeight;
                        latestAdded += smeltingRecipeHeight;
                        break;
                    case 2:
                        widgets.add(new CraftingRecipeWidget(itemStacks, 15, height));
                        height += craftingRecipeHeight;
                        latestAdded += craftingRecipeHeight;
                        break;
                    case 3:
                        widgets.add(new PestleAndMortarRecipeWidget(itemStacks, 15, height));
                        height += pestleAndMortarRecipeHeight;
                        latestAdded += pestleAndMortarRecipeHeight;
                        break;
                    case 4:
                        widgets.add(new CompostRecipeWidget(15, height));
                        height += compostRecipeHeight;
                        latestAdded += compostRecipeHeight;
                        break;
                    case 5:
                        widgets.add(new PurifierRecipeWidget(itemStacks, 15, height));
                        height += purifierRecipeHeight;
                        latestAdded += purifierRecipeHeight;
                        break;
                    case 6:
                        widgets.add(new RubberTabWidget(15, height));
                        height += rubberTabRecipeHeight;
                        latestAdded += rubberTabRecipeHeight;
                        break;
                }
                if (height >= 152) {
                    ManualWidgetsBase temp = widgets.get(widgets.size() - 1);
                    widgets.remove(widgets.size() - 1);
                    Page page = new Page(title, (ArrayList<ManualWidgetsBase>) widgets.clone());
                    newPages.add(page);
                    widgets.clear();
                    temp.changeYStart(10);
                    widgets.add(temp);
                    height = 10 + latestAdded;
                }
            }
            type++;
        }
        if (widgets.size() > 0) {
            Page page = new Page(title, (ArrayList<ManualWidgetsBase>) widgets.clone());
            newPages.add(page);
            widgets.clear();
        }

        return newPages;
    }


}
