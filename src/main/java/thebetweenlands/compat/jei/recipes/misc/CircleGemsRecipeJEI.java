package thebetweenlands.compat.jei.recipes.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;

public class CircleGemsRecipeJEI implements ICraftingRecipeWrapper, ICustomCraftingRecipeWrapper {

    private static List<ItemStack> applicableItems = NonNullList.create();
    private final ICraftingGridHelper craftingGridHelper;

    public CircleGemsRecipeJEI(IGuiHelper guiHelper) {
        craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);
    }

    public static void updateApplicableItems() {
        applicableItems.clear();
        for (ItemStack stack: BetweenlandsJEIPlugin.ingredientRegistry.getAllIngredients(VanillaTypes.ITEM)) {
            if (!stack.isEmpty() && CircleGemHelper.isApplicable(stack.getItem()) && (BetweenlandsConfig.COMPATIBILITY.showNonBLGemRecipes || ModInfo.ID.equals(stack.getItem().getRegistryName().getNamespace())))
                applicableItems.add(stack);
        }
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.CIRCLE_GEMS;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        updateApplicableItems();
        List<List<ItemStack>> inputLists = new ArrayList<>();
        List<List<ItemStack>> outputLists = new ArrayList<>();
        ItemStack[] gems = new ItemStack[] {new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM), new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM), new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM)};

        List<ItemStack> inputs = new ArrayList<>();
        List<ItemStack> gemInputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        for (ItemStack stack: applicableItems) {
            for (int i = 0; i < 3; i++) {
                ItemStack result = stack.copy();
                ItemStack gem = gems[i];
                CircleGemType appliedGem = ((ItemGem)gem.getItem()).type;
                CircleGemHelper.setGem(result, appliedGem);

                inputs.add(stack);
                gemInputs.add(gem);
                outputs.add(result);
            }
        }
        inputLists.add(inputs);
        inputLists.add(gemInputs);
        outputLists.add(outputs);

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputLists(VanillaTypes.ITEM, outputLists);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        recipeLayout.setShapeless();

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IFocus focus = recipeLayout.getFocus();
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        if (focus != null && focus.getValue() instanceof ItemStack && (CircleGemHelper.isApplicable(((ItemStack) focus.getValue()).getItem()) || ((ItemStack) focus.getValue()).getItem() instanceof ItemGem)) {
            if (focus.getMode() == IFocus.Mode.INPUT) {
                if (!(((ItemStack) focus.getValue()).getItem() instanceof ItemGem)) {
                    outputs.get(0).removeIf(stack -> stack.getItem() != ((ItemStack) focus.getValue()).getItem());
                } else if (((ItemStack) focus.getValue()).getItem() instanceof ItemGem) {
                    Iterator<ItemStack> outIterator = outputs.get(0).iterator();
                    Iterator<ItemStack> inIterator = inputs.get(0).iterator();
                    while (outIterator.hasNext()) {
                        ItemStack stack = outIterator.next();
                        inIterator.next();
                        if (CircleGemHelper.getGem(stack) != ((ItemGem) ((ItemStack) focus.getValue()).getItem()).type) {
                            outIterator.remove();
                            inIterator.remove();
                        }
                    }
                }
            } else {
                inputs.get(0).removeIf(stack -> stack.getItem() != ((ItemStack) focus.getValue()).getItem() || CircleGemHelper.getGem(stack) != CircleGemType.NONE);
                inputs.get(1).removeIf(stack -> CircleGemHelper.getGem(((ItemStack) focus.getValue())) != CircleGemType.NONE && ((ItemGem)stack.getItem()).type != CircleGemHelper.getGem(((ItemStack) focus.getValue())));
                outputs.get(0).removeIf(stack -> stack.getItem() != ((ItemStack) focus.getValue()).getItem());
                if (CircleGemHelper.getGem(((ItemStack) focus.getValue())) != CircleGemType.NONE)
                    outputs.get(0).removeIf(stack -> CircleGemHelper.getGem(((ItemStack) focus.getValue())) != CircleGemHelper.getGem(stack));
            }
        }

        craftingGridHelper.setInputs(guiItemStacks, inputs);
        guiItemStacks.setOverrideDisplayFocus(null);
        guiItemStacks.set(0, outputs.get(0));
        BetweenlandsJEIPlugin.addRecipeName(getRegistryName(), guiItemStacks, 0);
    }
}
