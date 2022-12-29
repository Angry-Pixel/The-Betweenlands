package thebetweenlands.compat.jei.recipes.druid_altar;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;

public class DruidAltarReactivationRecipeJEI implements IRecipeWrapper {

    public DruidAltarReactivationRecipeJEI(){ }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> saplings = BetweenlandsJEIPlugin.jeiHelper.getStackHelper().toItemStackList("treeSapling");
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(saplings, saplings, saplings, saplings));
        ingredients.setOutput(VanillaTypes.ITEM, ItemStack.EMPTY);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String string = I18n.format("jei.thebetweenlands.druid_circle_reactivate");

        int stringWidth = minecraft.fontRenderer.getStringWidth(string);
        int fontX = recipeWidth / 2 - (stringWidth / 2);
        int fontY = recipeHeight / 2;
        Gui.drawRect(fontX - 1, fontY - 5, fontX + stringWidth + 1, fontY + 10 - 5, 0xAA000000);
        minecraft.fontRenderer.drawStringWithShadow(string, fontX, fontY - 4, 0xFFFFFF);
    }
}
