package thebetweenlands.common.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import javax.annotation.Nullable;
import thebetweenlands.common.item.recipe.BasicAnimatorRecipe;

import java.util.Optional;

public class AnimatorRecipeBuilder implements RecipeBuilder {

	private final Ingredient input;
	@Nullable
	private ItemStack resultStack = null;
	@Nullable
	private EntityType<?> resultEntity;
	private int requiredFuel;
	private int requiredLife;
	@Nullable
	private EntityType<?> renderEntity;
	@Nullable
	private ResourceKey<LootTable> lootTable;
	private boolean closeOnFinish;

	private AnimatorRecipeBuilder(Ingredient input) {
		this.input = input;
	}

	public static AnimatorRecipeBuilder animator(TagKey<Item> input) {
		return new AnimatorRecipeBuilder(Ingredient.of(input));
	}

	public static AnimatorRecipeBuilder animator(ItemLike input) {
		return new AnimatorRecipeBuilder(Ingredient.of(input));
	}

	public static AnimatorRecipeBuilder animator(ItemStack input) {
		return new AnimatorRecipeBuilder(Ingredient.of(input));
	}

	public static AnimatorRecipeBuilder animator(Ingredient input) {
		return new AnimatorRecipeBuilder(input);
	}

	public AnimatorRecipeBuilder setResultStack(ItemStack output) {
		this.resultStack = output;
		return this;
	}

	public AnimatorRecipeBuilder setResultStack(ItemLike output) {
		this.resultStack = new ItemStack(output);
		return this;
	}

	public AnimatorRecipeBuilder requiredFuel(int fuel) {
		this.requiredFuel = fuel;
		return this;
	}

	public AnimatorRecipeBuilder requiredLife(int life) {
		this.requiredLife = life;
		return this;
	}

	public AnimatorRecipeBuilder closeOnFinish() {
		this.closeOnFinish = true;
		return this;
	}

	public AnimatorRecipeBuilder setResultLoot(ResourceKey<LootTable> table) {
		this.lootTable = table;
		return this;
	}

	public AnimatorRecipeBuilder setResultEntity(EntityType<?> type) {
		this.resultEntity = type;
		return this;
	}

	public AnimatorRecipeBuilder setRenderEntity(EntityType<?> type) {
		this.renderEntity = type;
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
		return this.resultStack.getItem();
	}

	@Override
	public void save(RecipeOutput output) {
		if (this.resultStack.isEmpty())
			throw new IllegalArgumentException("Empty result recipes must define a recipe ID");
		RecipeBuilder.super.save(output);
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id.withPrefix("animator/"), new BasicAnimatorRecipe(this.input, Optional.ofNullable(this.resultStack),
			Optional.ofNullable(this.resultEntity), this.requiredFuel, this.requiredLife,
			Optional.ofNullable(this.renderEntity), Optional.ofNullable(this.lootTable), this.closeOnFinish), null);
	}
}
