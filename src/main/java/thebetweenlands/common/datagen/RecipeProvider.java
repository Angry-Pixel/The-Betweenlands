package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.builders.DruidAltarRecipeBuilder;
import thebetweenlands.common.datagen.builders.FishTrimmingRecipeBuilder;
import thebetweenlands.common.items.recipe.AnadiaTrimmingRecipe;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
	public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
		DruidAltarRecipeBuilder.assembly(ItemRegistry.SWAMP_TALISMAN)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_1)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_2)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_3)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_4)
			.save(output);

		DruidAltarRecipeBuilder.reversion()
			.requires(ItemTags.SAPLINGS).requires(ItemTags.SAPLINGS)
			.requires(ItemTags.SAPLINGS).requires(ItemTags.SAPLINGS)
			.save(output, TheBetweenlands.prefix("sapling_altar_reversion"));

		FishTrimmingRecipeBuilder.trimming(ItemRegistry.BUBBLER_CRAB)
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.outputs(new ItemStack(ItemRegistry.CRAB_STICK.get(), 2))
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.save(output);

		FishTrimmingRecipeBuilder.trimming(ItemRegistry.SILT_CRAB)
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.outputs(new ItemStack(ItemRegistry.CRAB_STICK.get(), 2))
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.save(output);

		output.accept(TheBetweenlands.prefix("anadia_trimming"), new AnadiaTrimmingRecipe(), null);
	}
}
