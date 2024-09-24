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
import thebetweenlands.common.item.recipe.BubblerCrabPotFilterRecipe;
import thebetweenlands.common.item.recipe.SiltCrabPotFilterRecipe;

public class CrabPotFilterRecipeBuilder implements RecipeBuilder {
	private final boolean siltCrab;
	private final Ingredient input;
	private final ItemStack output;
	private int time = 200;

	private CrabPotFilterRecipeBuilder(boolean siltCrab, Ingredient input, ItemStack result) {
		this.siltCrab = siltCrab;
		this.input = input;
		this.output = result;
	}

	public static CrabPotFilterRecipeBuilder siltCrab(Ingredient input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(true, input, new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder siltCrab(TagKey<Item> input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(true, Ingredient.of(input), new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder siltCrab(ItemStack input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(true, Ingredient.of(input), new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder siltCrab(ItemLike input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(true, Ingredient.of(input), new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder siltCrab(Ingredient input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(true, input, result);
	}

	public static CrabPotFilterRecipeBuilder siltCrab(TagKey<Item> input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(true, Ingredient.of(input), result);
	}

	public static CrabPotFilterRecipeBuilder siltCrab(ItemStack input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(true, Ingredient.of(input), result);
	}

	public static CrabPotFilterRecipeBuilder siltCrab(ItemLike input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(true, Ingredient.of(input), result);
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(Ingredient input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(false, input, new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(TagKey<Item> input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(false, Ingredient.of(input), new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(ItemStack input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(false, Ingredient.of(input), new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(ItemLike input, ItemLike result) {
		return new CrabPotFilterRecipeBuilder(false, Ingredient.of(input), new ItemStack(result));
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(Ingredient input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(false, input, result);
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(TagKey<Item> input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(false, Ingredient.of(input), result);
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(ItemStack input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(false, Ingredient.of(input), result);
	}

	public static CrabPotFilterRecipeBuilder bubblerCrab(ItemLike input, ItemStack result) {
		return new CrabPotFilterRecipeBuilder(false, Ingredient.of(input), result);
	}

	public CrabPotFilterRecipeBuilder setFilterTime(int time) {
		this.time = time;
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
		return this.output.getItem();
	}


	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id.withPrefix("filter/" + (this.siltCrab ? "silt/" : "bubbler/")), this.siltCrab ? new SiltCrabPotFilterRecipe(this.input, this.output, this.time) : new BubblerCrabPotFilterRecipe(this.input, this.output, this.time), null);
	}
}
