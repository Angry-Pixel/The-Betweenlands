package thebetweenlands.common.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.items.recipe.FluidSteepingPotRecipe;

public class SteepingPotRecipeBuilder implements RecipeBuilder {

	private final FluidIngredient input;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();
	private final FluidStack result;

	private SteepingPotRecipeBuilder(FluidIngredient input, FluidStack result) {
		this.input = input;
		this.result = result;
	}

	public static SteepingPotRecipeBuilder steeping(Fluid input, Fluid result, int amount) {
		return new SteepingPotRecipeBuilder(FluidIngredient.of(input), new FluidStack(result, amount));
	}

	public static SteepingPotRecipeBuilder steeping(TagKey<Fluid> input, Fluid result, int amount) {
		return new SteepingPotRecipeBuilder(FluidIngredient.tag(input), new FluidStack(result, amount));
	}

	public static SteepingPotRecipeBuilder steeping(FluidStack input, Fluid result, int amount) {
		return new SteepingPotRecipeBuilder(FluidIngredient.of(input), new FluidStack(result, amount));
	}

	public static SteepingPotRecipeBuilder steeping(FluidIngredient input, Fluid result, int amount) {
		return new SteepingPotRecipeBuilder(input, new FluidStack(result, amount));
	}

	public static SteepingPotRecipeBuilder steeping(Fluid input, FluidStack result) {
		return new SteepingPotRecipeBuilder(FluidIngredient.of(input), result);
	}

	public static SteepingPotRecipeBuilder steeping(TagKey<Fluid> input, FluidStack result) {
		return new SteepingPotRecipeBuilder(FluidIngredient.tag(input), result);
	}

	public static SteepingPotRecipeBuilder steeping(FluidStack input, FluidStack result) {
		return new SteepingPotRecipeBuilder(FluidIngredient.of(input), result);
	}

	public static SteepingPotRecipeBuilder steeping(FluidIngredient input, FluidStack result) {
		return new SteepingPotRecipeBuilder(input, result);
	}

	public SteepingPotRecipeBuilder requires(TagKey<Item> tag) {
		return this.requires(Ingredient.of(tag));
	}

	public SteepingPotRecipeBuilder requires(ItemLike item) {
		return this.requires(item, 1);
	}

	public SteepingPotRecipeBuilder requires(ItemLike item, int quantity) {
		for (int i = 0; i < quantity; i++) {
			this.requires(Ingredient.of(item));
		}

		return this;
	}

	public SteepingPotRecipeBuilder requires(Ingredient ingredient) {
		return this.requires(ingredient, 1);
	}

	public SteepingPotRecipeBuilder requires(Ingredient ingredient, int quantity) {
		for (int i = 0; i < quantity; i++) {
			this.ingredients.add(ingredient);
		}

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
		return Items.AIR;
	}

	@Override
	public void save(RecipeOutput output) {
		this.save(output, getDefaultRecipeId(this.result));
	}

	@Override
	public void save(RecipeOutput output, String id) {
		ResourceLocation resourcelocation = getDefaultRecipeId(this.result);
		ResourceLocation resourcelocation1 = ResourceLocation.parse(id);
		if (resourcelocation1.equals(resourcelocation)) {
			throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
		} else {
			this.save(output, resourcelocation1);
		}
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id.withPrefix("steeping/"), new FluidSteepingPotRecipe(this.input, this.ingredients, this.result), null);
	}

	static ResourceLocation getDefaultRecipeId(FluidStack result) {
		return BuiltInRegistries.FLUID.getKey(result.getFluid());
	}
}
