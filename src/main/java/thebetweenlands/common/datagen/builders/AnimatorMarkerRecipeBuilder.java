package thebetweenlands.common.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import thebetweenlands.common.item.recipe.AnimatorMarkerRecipe;

import javax.annotation.Nullable;

public class AnimatorMarkerRecipeBuilder implements RecipeBuilder {

	private final Ingredient input;
	private int requiredFuel;
	private int requiredLife;

	private AnimatorMarkerRecipeBuilder(Ingredient input) {
		this.input = input;
	}

	public static AnimatorMarkerRecipeBuilder animator(TagKey<Item> input) {
		return new AnimatorMarkerRecipeBuilder(Ingredient.of(input));
	}

	public static AnimatorMarkerRecipeBuilder animator(ItemLike input) {
		return new AnimatorMarkerRecipeBuilder(Ingredient.of(input));
	}

	public static AnimatorMarkerRecipeBuilder animator(ItemStack input) {
		return new AnimatorMarkerRecipeBuilder(Ingredient.of(input));
	}

	public static AnimatorMarkerRecipeBuilder animator(Ingredient input) {
		return new AnimatorMarkerRecipeBuilder(input);
	}

	public AnimatorMarkerRecipeBuilder requiredFuel(int fuel) {
		this.requiredFuel = fuel;
		return this;
	}

	public AnimatorMarkerRecipeBuilder requiredLife(int life) {
		this.requiredLife = life;
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
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id.withPrefix("animator/"), new AnimatorMarkerRecipe(this.input, this.requiredFuel, this.requiredLife), null);
	}
}
