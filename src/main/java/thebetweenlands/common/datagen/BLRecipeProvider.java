package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import thebetweenlands.common.datagen.recipes.BLBlockRecipeProvider;
import thebetweenlands.common.datagen.recipes.BLCookingRecipeProvider;
import thebetweenlands.common.datagen.recipes.BLCustomRecipeProvider;
import thebetweenlands.common.datagen.recipes.BLItemRecipeProvider;

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
