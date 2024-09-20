package thebetweenlands.common.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.common.block.entity.DruidAltarBlockEntity;
import thebetweenlands.common.item.recipe.DruidAltarAssemblyRecipe;
import thebetweenlands.common.item.recipe.DruidAltarReversionRecipe;

public class DruidAltarRecipeBuilder implements RecipeBuilder {

	@Nullable
	private final ItemStack resultStack;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();
	private int processTime = DruidAltarBlockEntity.DEFAULT_CRAFTING_TIME;

	private DruidAltarRecipeBuilder(@Nullable ItemStack result) {
		this.resultStack = result;
	}

	public static DruidAltarRecipeBuilder assembly(ItemLike result) {
		return new DruidAltarRecipeBuilder(new ItemStack(result, 1));
	}

	public static DruidAltarRecipeBuilder assembly(ItemStack result) {
		return new DruidAltarRecipeBuilder(result);
	}

	public static DruidAltarRecipeBuilder reversion() {
		return new DruidAltarRecipeBuilder(null);
	}

	public DruidAltarRecipeBuilder requires(TagKey<Item> tag) {
		return this.requires(Ingredient.of(tag));
	}

	public DruidAltarRecipeBuilder requires(ItemLike item) {
		return this.requires(item, 1);
	}

	public DruidAltarRecipeBuilder requires(ItemLike item, int quantity) {
		for (int i = 0; i < quantity; i++) {
			this.requires(Ingredient.of(item));
		}

		return this;
	}

	public DruidAltarRecipeBuilder requires(Ingredient ingredient) {
		return this.requires(ingredient, 1);
	}

	public DruidAltarRecipeBuilder requires(Ingredient ingredient, int quantity) {
		for (int i = 0; i < quantity; i++) {
			this.ingredients.add(ingredient);
		}

		return this;
	}

	public DruidAltarRecipeBuilder processTime(int time) {
		this.processTime = time;
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
		return this.resultStack != null ? this.resultStack.getItem() : Items.AIR;
	}

	@Override
	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		DruidAltarRecipe recipe;
		if (this.resultStack != null) {
			recipe = new DruidAltarAssemblyRecipe(this.ingredients, this.resultStack, this.processTime);
		} else {
			recipe = new DruidAltarReversionRecipe(this.ingredients, this.processTime);
		}
		recipeOutput.accept(id.withPrefix("druid_altar/"), recipe, null);
	}
}
