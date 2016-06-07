package thebetweenlands.common.registries;

import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.recipe.DruidAltarRecipe;

public class RecipeRegistry {
    public void init() {

        registerDruidAltarRecipes();
    }

    private void registerDruidAltarRecipes() {
        DruidAltarRecipe.addRecipe(ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_1, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_2, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_3, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_4, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_0, 1));
    }
}
