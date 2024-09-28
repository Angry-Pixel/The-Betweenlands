package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.builders.*;
import thebetweenlands.common.datagen.recipes.BLBlockRecipeProvider;
import thebetweenlands.common.datagen.recipes.BLCookingRecipeProvider;
import thebetweenlands.common.datagen.recipes.BLCustomRecipeProvider;
import thebetweenlands.common.datagen.recipes.BLItemRecipeProvider;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.item.recipe.AnadiaSmokingRecipe;
import thebetweenlands.common.item.recipe.AnadiaTrimmingRecipe;
import thebetweenlands.common.item.recipe.MortarAspectrusRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.concurrent.CompletableFuture;

public class BLRecipeProvider extends RecipeProvider {
	public BLRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
		BLBlockRecipeProvider.buildRecipes(output, registries);
		BLCookingRecipeProvider.buildRecipes(output, registries);
		BLCustomRecipeProvider.buildRecipes(output, registries);
		BLItemRecipeProvider.buildRecipes(output, registries);
	}
}
