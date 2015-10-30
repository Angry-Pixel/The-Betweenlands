package thebetweenlands.manual.gui.pages;

import net.minecraft.item.ItemStack;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.entries.IManualEntryItem;
import thebetweenlands.manual.gui.widgets.*;

import java.util.Collections;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualPageRecipe extends ManualPage {

    public ManualPageRecipe(GuiManualBase manual, IManualEntryItem item, ManualWidgetsBase... recipes){
        switch (item.recipeType(0)) {
            case 0:
                this.widgets.add(new SmeltingRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10));
                break;
            case 1:
                this.widgets.add(new PurifierRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10));
                break;
            case 2:
                this.widgets.add(new PestleAndMortarRecipeWidget(manual, new ItemStack(item.getItem()), 5, 10));
                break;
            case 3:
                this.widgets.add(new CompostRecipeWidget(manual, 5, 10));
                break;
            default:
                Collections.addAll(this.widgets, recipes);
                break;
        }
    }
}
