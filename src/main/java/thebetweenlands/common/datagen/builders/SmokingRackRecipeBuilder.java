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
import thebetweenlands.common.item.recipe.BasicSmokingRackRecipe;

public class SmokingRackRecipeBuilder implements RecipeBuilder {

	private final Ingredient input;
	private final ItemStack result;
	private final int time;

	private SmokingRackRecipeBuilder(Ingredient input, ItemStack result, int time) {
		this.input = input;
		this.result = result;
		this.time = time;
	}

	public static SmokingRackRecipeBuilder smoking(ItemLike input, ItemLike output, int time) {
		return new SmokingRackRecipeBuilder(Ingredient.of(input), new ItemStack(output), time);
	}

	public static SmokingRackRecipeBuilder smoking(ItemStack input, ItemLike output, int time) {
		return new SmokingRackRecipeBuilder(Ingredient.of(input), new ItemStack(output), time);
	}

	public static SmokingRackRecipeBuilder smoking(TagKey<Item> input, ItemLike output, int time) {
		return new SmokingRackRecipeBuilder(Ingredient.of(input), new ItemStack(output), time);
	}

	public static SmokingRackRecipeBuilder smoking(Ingredient input, ItemLike output, int time) {
		return new SmokingRackRecipeBuilder(input, new ItemStack(output), time);
	}

	public static SmokingRackRecipeBuilder smoking(ItemLike input, ItemStack output, int time) {
		return new SmokingRackRecipeBuilder(Ingredient.of(input), output, time);
	}

	public static SmokingRackRecipeBuilder smoking(ItemStack input, ItemStack output, int time) {
		return new SmokingRackRecipeBuilder(Ingredient.of(input), output, time);
	}

	public static SmokingRackRecipeBuilder smoking(TagKey<Item> input, ItemStack output, int time) {
		return new SmokingRackRecipeBuilder(Ingredient.of(input), output, time);
	}

	public static SmokingRackRecipeBuilder smoking(Ingredient input, ItemStack output, int time) {
		return new SmokingRackRecipeBuilder(input, output, time);
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
		output.accept(id.withPrefix("smoking_rack/"), new BasicSmokingRackRecipe(this.input, this.result, this.time), null);
	}
}
