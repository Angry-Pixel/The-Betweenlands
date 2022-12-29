package thebetweenlands.common.recipe.custom;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.api.recipes.ISmokingRackRecipe;
import thebetweenlands.api.recipes.ISteepingPotRecipe;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;
import thebetweenlands.common.recipe.misc.SteepingPotRecipes;

import java.util.ArrayList;

public class CustomSteepingPotRecipes extends CustomRecipes<ISteepingPotRecipe> {
    public CustomSteepingPotRecipes() {
        super("steeping_pot", new ImmutableMap.Builder<String, RecipeArg<?>>()
                        .put("input/item_1", RecipeArg.ITEM_INPUT)
                        .put("input/fluid", RecipeArg.FLUID_INPUT)
                .build(),
                new ImmutableMap.Builder<String, RecipeArg<?>>()
                        .put("input/item_2", RecipeArg.ITEM_INPUT)
                        .put("input/item_3", RecipeArg.ITEM_INPUT)
                        .put("input/item_4", RecipeArg.ITEM_INPUT)
                        .put("output/fluid", RecipeArg.FLUID_OUTPUT)
                        .put("output/item", RecipeArg.ITEM_OUTPUT)
                        .put("output/fluid_meta", RecipeArg.INT)
                        .build());
    }

    @Override
    public ISteepingPotRecipe load() {
        ItemStack input_1 = this.get("input/item_1", RecipeArg.ITEM_INPUT).get().create();
        FluidStack inputFluid = this.get("input/fluid", RecipeArg.FLUID_OUTPUT).get().create();

        Optional<IRecipeEntry<ItemStack>> input_2 = this.get("input/item_2", RecipeArg.ITEM_INPUT);
        Optional<IRecipeEntry<ItemStack>> input_3 = this.get("input/item_3", RecipeArg.ITEM_INPUT);
        Optional<IRecipeEntry<ItemStack>> input_4 = this.get("input/item_4", RecipeArg.ITEM_INPUT);
        Optional<IRecipeEntry<ItemStack>> outputItem = this.get("output/item", RecipeArg.ITEM_OUTPUT);
        Optional<IRecipeEntry<FluidStack>> outputFluid = this.get("output/fluid", RecipeArg.FLUID_OUTPUT);
        Optional<IRecipeEntry<Integer>> outputFluidMeta = this.get("output/fluid_meta", RecipeArg.INT);

        if(outputItem.isPresent()) {
            return new SteepingPotRecipes(
                    outputItem.get().create(),
                    inputFluid,
                    input_1,
                    (input_2.isPresent() ? input_2.get().create() : ItemStack.EMPTY),
                    (input_3.isPresent() ? input_3.get().create() : ItemStack.EMPTY),
                    (input_4.isPresent() ? input_4.get().create() : ItemStack.EMPTY)
            );
        } else {
            return new SteepingPotRecipes(
                    outputFluid.get().create(),
                    outputFluidMeta.get().create(),
                    inputFluid,
                    input_1,
                    (input_2.isPresent() ? input_2.get().create() : ItemStack.EMPTY),
                    (input_3.isPresent() ? input_3.get().create() : ItemStack.EMPTY),
                    (input_4.isPresent() ? input_4.get().create() : ItemStack.EMPTY)
            );
        }
    }

    @Override
    public IRecipeRegistrar<ISteepingPotRecipe> createRegistrar() {
        return new IRecipeRegistrar<ISteepingPotRecipe>() {
            @Override
            public boolean register(ISteepingPotRecipe recipe) {
                SteepingPotRecipes.addRecipe(recipe);
                return true;
            }

            @Override
            public boolean unregister(ISteepingPotRecipe recipe) {
                SteepingPotRecipes.removeRecipe(recipe);
                return true;
            }
        };
    }
}
