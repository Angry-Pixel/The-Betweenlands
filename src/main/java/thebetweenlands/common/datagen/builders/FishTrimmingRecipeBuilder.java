package thebetweenlands.common.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.items.recipe.BasicTrimmingTableRecipe;

import java.util.Optional;

public class FishTrimmingRecipeBuilder implements RecipeBuilder {

	private final Ingredient input;
	private final NonNullList<ItemStack> outputs = NonNullList.create();
	private ItemStack remains = null;

	private FishTrimmingRecipeBuilder(Ingredient input) {
		this.input = input;
	}

	public static FishTrimmingRecipeBuilder trimming(Ingredient input) {
		return new FishTrimmingRecipeBuilder(input);
	}

	public static FishTrimmingRecipeBuilder trimming(TagKey<Item> input) {
		return new FishTrimmingRecipeBuilder(Ingredient.of(input));
	}

	public static FishTrimmingRecipeBuilder trimming(ItemStack input) {
		return new FishTrimmingRecipeBuilder(Ingredient.of(input));
	}

	public static FishTrimmingRecipeBuilder trimming(ItemLike input) {
		return new FishTrimmingRecipeBuilder(Ingredient.of(new ItemStack(input, 1)));
	}

	public FishTrimmingRecipeBuilder outputs(ItemLike result) {
		return this.outputs(new ItemStack(result, 1));
	}

	public FishTrimmingRecipeBuilder outputs(ItemStack result) {
		this.outputs.add(result);
		return this;
	}

	public FishTrimmingRecipeBuilder setRemains(ItemStack remains) {
		this.remains = remains;
		return this;
	}

	public FishTrimmingRecipeBuilder setRemains(ItemLike remains) {
		this.remains = new ItemStack(remains);
		return this;
	}

	@Override
	public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		return this;
	}

	@Override
	public RecipeBuilder group(@Nullable String groupName) {
		return this;
	}

	@Override
	public Item getResult() {
		return this.input.getItems()[0].getItem();
	}

	@Override
	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		recipeOutput.accept(id, new BasicTrimmingTableRecipe(this.input, this.outputs, Optional.ofNullable(this.remains)), null);
	}
}
