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
import javax.annotation.Nullable;
import thebetweenlands.common.item.recipe.MortarGrindRecipe;

public class MortarRecipeBuilder implements RecipeBuilder {

	private final Ingredient input;
	private final ItemStack result;

	private MortarRecipeBuilder(Ingredient input, ItemStack result) {
		this.input = input;
		this.result = result;
	}

	public static MortarRecipeBuilder grinding(ItemLike input, ItemLike output) {
		return new MortarRecipeBuilder(Ingredient.of(input), new ItemStack(output));
	}

	public static MortarRecipeBuilder grinding(ItemStack input, ItemLike output) {
		return new MortarRecipeBuilder(Ingredient.of(input), new ItemStack(output));
	}

	public static MortarRecipeBuilder grinding(TagKey<Item> input, ItemLike output) {
		return new MortarRecipeBuilder(Ingredient.of(input), new ItemStack(output));
	}

	public static MortarRecipeBuilder grinding(Ingredient input, ItemLike output) {
		return new MortarRecipeBuilder(input, new ItemStack(output));
	}

	public static MortarRecipeBuilder grinding(ItemLike input, ItemStack output) {
		return new MortarRecipeBuilder(Ingredient.of(input), output);
	}

	public static MortarRecipeBuilder grinding(ItemStack input, ItemStack output) {
		return new MortarRecipeBuilder(Ingredient.of(input), output);
	}

	public static MortarRecipeBuilder grinding(TagKey<Item> input, ItemStack output) {
		return new MortarRecipeBuilder(Ingredient.of(input), output);
	}

	public static MortarRecipeBuilder grinding(Ingredient input, ItemStack output) {
		return new MortarRecipeBuilder(input, output);
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
		return this.result.getItem();
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id.withPrefix("mortar/"), new MortarGrindRecipe(this.input, this.result), null);
	}
}
