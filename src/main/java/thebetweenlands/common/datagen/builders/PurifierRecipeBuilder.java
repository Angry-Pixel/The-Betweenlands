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
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.item.recipe.PurifierRecipe;

public class PurifierRecipeBuilder implements RecipeBuilder {

	private final Ingredient input;
	private final ItemStack result;
	private int purifyTime = 432;
	private int requiredWater = 250;

	private PurifierRecipeBuilder(Ingredient input, ItemStack result) {
		this.input = input;
		this.result = result;
	}

	public static PurifierRecipeBuilder purifying(ItemLike input, ItemLike output) {
		return new PurifierRecipeBuilder(Ingredient.of(input), new ItemStack(output));
	}

	public static PurifierRecipeBuilder purifying(ItemStack input, ItemLike output) {
		return new PurifierRecipeBuilder(Ingredient.of(input), new ItemStack(output));
	}

	public static PurifierRecipeBuilder purifying(TagKey<Item> input, ItemLike output) {
		return new PurifierRecipeBuilder(Ingredient.of(input), new ItemStack(output));
	}

	public static PurifierRecipeBuilder purifying(Ingredient input, ItemLike output) {
		return new PurifierRecipeBuilder(input, new ItemStack(output));
	}

	public static PurifierRecipeBuilder purifying(ItemLike input, ItemStack output) {
		return new PurifierRecipeBuilder(Ingredient.of(input), output);
	}

	public static PurifierRecipeBuilder purifying(ItemStack input, ItemStack output) {
		return new PurifierRecipeBuilder(Ingredient.of(input), output);
	}

	public static PurifierRecipeBuilder purifying(TagKey<Item> input, ItemStack output) {
		return new PurifierRecipeBuilder(Ingredient.of(input), output);
	}

	public static PurifierRecipeBuilder purifying(Ingredient input, ItemStack output) {
		return new PurifierRecipeBuilder(input, output);
	}

	private PurifierRecipeBuilder setPurifyingTime(int time) {
		this.purifyTime = time;
		return this;
	}

	private PurifierRecipeBuilder setRequiredWater(int water) {
		this.requiredWater = water;
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
		return this.result.getItem();
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id.withPrefix("purifying/"), new PurifierRecipe(this.input, this.result, this.purifyTime, this.requiredWater), null);
	}
}
