package thebetweenlands.compat.jei.recipes.smoking_rack;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;
import thebetweenlands.common.registries.BlockRegistry;

public class SmokingRackRecipeJEI implements IRecipeWrapper {

    private ItemStack input;
    private ItemStack output;
    private int smokingTime;

    public SmokingRackRecipeJEI(SmokingRackRecipe recipe) {
        this.input = recipe.getInput();
        this.output = recipe.getOutput(input);
        this.smokingTime = recipe.getSmokingTime(input);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
    	ArrayList<ItemStack> inputs = new ArrayList<>();
        inputs.add(input);
        inputs.add(new ItemStack(BlockRegistry.FALLEN_LEAVES));
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      String time = String.format("%ds", 10 * smokingTime );
      int x = 66;
      x -= minecraft.fontRenderer.getStringWidth(time) / 2;
      minecraft.fontRenderer.drawStringWithShadow(time, x, 8, 0xFFFFFF);
    }
}
