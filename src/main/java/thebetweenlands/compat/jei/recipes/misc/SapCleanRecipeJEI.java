package thebetweenlands.compat.jei.recipes.misc;

import static thebetweenlands.compat.jei.recipes.misc.CoatingRecipeJEI.coatableItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;


public class SapCleanRecipeJEI implements ICraftingRecipeWrapper, ICustomCraftingRecipeWrapper {

    private final ICraftingGridHelper craftingGridHelper;

    public SapCleanRecipeJEI(IGuiHelper guiHelper) {
        craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.SAP_SPIT_CLEAN_TOOL;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputLists = new ArrayList<>();
        List<List<ItemStack>> outputLists = new ArrayList<>();
        ItemStack sap = new ItemStack(ItemRegistry.SAP_SPIT);

        List<ItemStack> inputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        for (ItemStack stack: coatableItems) {
            ItemStack corroded = stack.copy();
            ((ICorrodible) corroded.getItem()).setCorrosion(corroded, ((ICorrodible)corroded.getItem()).getMaxCorrosion(corroded));

            inputs.add(corroded);
            outputs.add(stack);
        }
        inputLists.add(inputs);
        outputLists.add(outputs);

        for (int i = 0; i < 8; i++)
            inputLists.add(Collections.singletonList(sap));

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputLists(VanillaTypes.ITEM, outputLists);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        IStackHelper stackHelper = BetweenlandsJEIPlugin.jeiHelper.getStackHelper();
        recipeLayout.setShapeless();

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IFocus focus = recipeLayout.getFocus();
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        if (focus != null && focus.getValue() instanceof ItemStack && ((ItemStack) focus.getValue()).getItem() instanceof ICorrodible) {
            if (focus.getMode() == IFocus.Mode.INPUT) {
                outputs.get(0).removeIf(stack -> !stackHelper.isEquivalent((ItemStack) focus.getValue(), stack));
            } else {
                inputs.get(0).removeIf(stack -> !stackHelper.isEquivalent((ItemStack) focus.getValue(), stack));
            }
        }

        craftingGridHelper.setInputs(guiItemStacks, inputs);
        guiItemStacks.set(0, outputs.get(0));
        BetweenlandsJEIPlugin.addRecipeName(getRegistryName(), guiItemStacks, 0);
    }
}
