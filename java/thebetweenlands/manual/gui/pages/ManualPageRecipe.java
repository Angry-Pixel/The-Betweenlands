package thebetweenlands.manual.gui.pages;

import net.minecraft.item.ItemStack;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.entries.IManualEntryItem;
import thebetweenlands.manual.gui.widgets.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualPageRecipe extends ManualPage {

    public static int craftingRecipeHeight = CraftingRecipeWidget.height + 5;
    public static int smeltingRecipeHeight = SmeltingRecipeWidget.height + 5;
    public static int compostRecipeHeight = CompostRecipeWidget.height + 5;
    public static int pestleAndMortarRecipeHeight = PestleAndMortarRecipeWidget.height + 5;
    public static int purifierRecipeHeight = PurifierRecipeWidget.height + 5;

    public ManualPageRecipe(GuiManualBase manual, IManualEntryItem item, ManualWidgetsBase... recipes) {
        int height = 0;
        for (int i : item.recipeType(0)) {
            switch (i) {
                case 1:
                    this.widgets.add(new SmeltingRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10 + height));
                    height += smeltingRecipeHeight;
                    break;
                case 2:
                    this.widgets.add(new CraftingRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10 + height));
                    height += craftingRecipeHeight;
                    break;
                case 3:
                    this.widgets.add(new PestleAndMortarRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10 + height));
                    height += pestleAndMortarRecipeHeight;
                    break;
                case 4:
                    this.widgets.add(new CompostRecipeWidget(manual, 5, 10 + height));
                    height += compostRecipeHeight;
                    break;
                case 5:
                    this.widgets.add(new PurifierRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10 + height));
                    height += purifierRecipeHeight;
                    break;
                default:
                    Collections.addAll(this.widgets, recipes);
                    break;
            }
        }
    }

    public ManualPageRecipe(GuiManualBase manual, ArrayList<IManualEntryItem> items, ManualWidgetsBase... recipes) {
        int height = 0;
        int type = 0;
        while (type <= 5) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (IManualEntryItem item : items) {
                for (int i : item.recipeType(0)) {
                    if(i == type)
                        itemStacks.add(new ItemStack(item.getItem()));
                }
            }
            if (itemStacks.size() > 0) {
                switch (type) {
                    case 1:
                        this.widgets.add(new SmeltingRecipeWidget(manual, itemStacks, 5, 10 + height));
                        height += smeltingRecipeHeight;
                        break;
                    case 2:
                        this.widgets.add(new CraftingRecipeWidget(manual, itemStacks, 5, 10 + height));
                        height += craftingRecipeHeight;
                        break;
                    case 3:
                        this.widgets.add(new PestleAndMortarRecipeWidget(manual, itemStacks, 5, 10 + height));
                        height += pestleAndMortarRecipeHeight;
                        break;
                    case 4:
                        this.widgets.add(new CompostRecipeWidget(manual, 5, 10 + height));
                        height += compostRecipeHeight;
                        break;
                    case 5:
                        this.widgets.add(new PurifierRecipeWidget(manual, itemStacks, 5, 10 + height));
                        height += purifierRecipeHeight;
                        break;
                    default:
                        Collections.addAll(this.widgets, recipes);
                        break;
                }
            }
            type++;
        }
    }
}
