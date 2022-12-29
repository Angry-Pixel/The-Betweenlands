package thebetweenlands.compat.jei.recipes.misc;

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
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;

public class TarringRecipeJEI implements ICraftingRecipeWrapper, ICustomCraftingRecipeWrapper {

    private final ICraftingGridHelper craftingGridHelper;

    public TarringRecipeJEI(IGuiHelper guiHelper) {
        craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.HEARTHGROVE_LOG_TARRING;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        Item log = Item.getItemFromBlock(BlockRegistry.LOG_HEARTHGROVE);
        ItemStack inTarWeedwood = ItemRegistry.BL_BUCKET.withFluid(0, FluidRegistry.TAR);
        ItemStack inTarSyrmorite = ItemRegistry.BL_BUCKET.withFluid(1, FluidRegistry.TAR);
        ItemStack inBlock = new ItemStack(log, 1, 1);
        ItemStack outBlock = new ItemStack(log, 1, 5);

        List<List<ItemStack>> inputLists = new ArrayList<>(18);
        List<List<ItemStack>> outputLists = new ArrayList<>(18);
        inputLists.add(0, NonNullList.create());
        outputLists.add(0, NonNullList.create());

        for (int i = 0; i < 16; i++) {
            int type = i / 8;
            int amount = i;
            if (inBlock.getItemDamage() == 1 && type == 1)
                inBlock.setItemDamage(3);
            if (outBlock.getItemDamage() == 5 && type == 1) {
                outBlock.setCount(1);
                outBlock.setItemDamage(7);
            }

            List<ItemStack> inStacks = inputLists.get(0);
            inStacks.add(inTarWeedwood);
            inStacks.add(inTarSyrmorite);

            for (int j = 0; j < 8; j++) {
                List<ItemStack> inputStacks;
                if (inputLists.size() <= j + 1) {
                    inputStacks = new ArrayList<>(Collections.nCopies(16, null));
                    inputLists.add(j + 1, inputStacks);
                } else
                    inputStacks = inputLists.get(j + 1);

                if (j <= amount % 8)
                    inputStacks.set(amount, inBlock.copy());
            }

            List<ItemStack> outputStacks = outputLists.get(0);
            outputStacks.add(outBlock.copy());
            outBlock.grow(1);
        }

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputLists(VanillaTypes.ITEM, outputLists);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        recipeLayout.setShapeless();

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        guiItemStacks.setOverrideDisplayFocus(null);
        craftingGridHelper.setInputs(guiItemStacks, inputs);
        guiItemStacks.set(0, outputs.get(0));
        BetweenlandsJEIPlugin.addRecipeName(getRegistryName(), guiItemStacks, 0);
    }
}
