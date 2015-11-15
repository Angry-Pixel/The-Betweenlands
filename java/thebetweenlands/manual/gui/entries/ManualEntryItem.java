package thebetweenlands.manual.gui.entries;

import net.minecraft.item.ItemStack;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.widgets.*;
import thebetweenlands.manual.gui.widgets.text.TextWidget;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualEntryItem extends ManualEntry {
    public ArrayList<ItemStack> items = new ArrayList<ItemStack>();


    public static int craftingRecipeHeight = CraftingRecipeWidget.height + 5;
    public static int smeltingRecipeHeight = SmeltingRecipeWidget.height + 5;
    public static int compostRecipeHeight = CompostRecipeWidget.height + 5;
    public static int pestleAndMortarRecipeHeight = PestleAndMortarRecipeWidget.height + 5;
    public static int purifierRecipeHeight = PurifierRecipeWidget.height + 5;

    public ManualEntryItem(IManualEntryItem item, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + item.manualName(0) + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + item.manualName(0) + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, item, 3)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + item.manualName(0) + ".description")));
        for (int i = 0; i <= item.metas(); i++)
            this.items.add(new ItemStack(item.getItem(), 1, i));

        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        int height = 10;
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i <= item.metas(); i++)
            itemStacks.add(new ItemStack(item.getItem(), 1, i));
        for (int i : item.recipeType(0)) {
            switch (i) {
                case 1:
                    widgets.add(new SmeltingRecipeWidget(manual, itemStacks, 15, height));
                    height += smeltingRecipeHeight;
                    break;
                case 2:
                    widgets.add(new CraftingRecipeWidget(manual, itemStacks, 15, height));
                    height += craftingRecipeHeight;
                    break;
                case 3:
                    widgets.add(new PestleAndMortarRecipeWidget(manual, itemStacks, 15, height));
                    height += pestleAndMortarRecipeHeight;
                    break;
                case 4:
                    widgets.add(new CompostRecipeWidget(manual, 15, height));
                    height += compostRecipeHeight;
                    break;
                case 5:
                    widgets.add(new PurifierRecipeWidget(manual, itemStacks, 15, height));
                    height += purifierRecipeHeight;
                    break;
                default:
                    Collections.addAll(widgets, recipes);
                    break;
            }
            if (widgets.size() >= 2) {
                ManualPage page = new ManualPage((ArrayList<ManualWidgetsBase>) widgets.clone());
                page.setPageNumber(pages.size() + 1);
                pages.add(page);
                widgets.clear();
                height = 10;
            }
        }
        if (widgets.size() > 0) {
            ManualPage page = new ManualPage((ArrayList<ManualWidgetsBase>) widgets.clone());
            page.setPageNumber(pages.size() + 1);
            pages.add(page);
            widgets.clear();
        }
    }

    public ManualEntryItem(ArrayList<ItemStack> item, String name, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + name + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + name + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, item, 3)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + name + ".description")), new ManualPage(recipes));
        this.items = item;
    }

    public ManualEntryItem(String name, ArrayList<IManualEntryItem> items, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + name + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + name + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, 3, items)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + name + ".description")));
        for (IManualEntryItem item : items) {
            for (int i = 0; i <= item.metas(); i++)
                this.items.add(new ItemStack(item.getItem(), 1, i));
        }


        int height = 10;
        int type = 0;
        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        while (type <= 5) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (IManualEntryItem item : items) {
                for (int i : item.recipeType(0)) {
                    for (int j = 0; j <= item.metas(); j++)
                        if (i == type)
                            itemStacks.add(new ItemStack(item.getItem(), 1, j));
                }
            }

            if (itemStacks.size() > 0) {
                switch (type) {
                    case 1:
                        widgets.add(new SmeltingRecipeWidget(manual, itemStacks, 15, height));
                        height += smeltingRecipeHeight;
                        break;
                    case 2:
                        widgets.add(new CraftingRecipeWidget(manual, itemStacks, 15, height));
                        height += craftingRecipeHeight;
                        break;
                    case 3:
                        widgets.add(new PestleAndMortarRecipeWidget(manual, itemStacks, 15, height));
                        height += pestleAndMortarRecipeHeight;
                        break;
                    case 4:
                        widgets.add(new CompostRecipeWidget(manual, 15, height));
                        height += compostRecipeHeight;
                        break;
                    case 5:
                        widgets.add(new PurifierRecipeWidget(manual, itemStacks, 15, height));
                        height += purifierRecipeHeight;
                        break;
                    default:
                        Collections.addAll(widgets, recipes);
                        break;
                }
                if (widgets.size() >= 2) {
                    ManualPage page = new ManualPage((ArrayList<ManualWidgetsBase>) widgets.clone());
                    page.setPageNumber(pages.size() + 1);
                    pages.add(page);
                    widgets.clear();
                    height = 10;
                }
            }
            type++;
        }
        if (widgets.size() > 0) {
            ManualPage page = new ManualPage((ArrayList<ManualWidgetsBase>) widgets.clone());
            page.setPageNumber(pages.size() + 1);
            pages.add(page);
            widgets.clear();
        }
    }

    public ManualEntryItem(IManualEntryItem item, int meta, GuiManualBase manual, ManualWidgetsBase... recipes) {
        super("manual." + item.manualName(meta) + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + item.manualName(meta) + ".title", 1.5f), new ItemWidget(manual, (GuiManualBase.WIDTH / 2) - 24, 77, new ItemStack(item.getItem(), 1, meta), 3)), new ManualPage(new TextWidget(manual, 16, 10, "manual." + item.manualName(meta) + ".description")), new ManualPage(recipes));
        this.items.add(new ItemStack(item.getItem(), 1, meta));
    }
}
