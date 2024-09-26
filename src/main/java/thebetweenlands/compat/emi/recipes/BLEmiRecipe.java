package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.compat.emi.BLRecipeCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class BLEmiRecipe<T extends Recipe<?>> implements EmiRecipe {

	private final BLRecipeCategory category;
	private final RecipeHolder<T> recipe;
	private final ResourceLocation id;
	private final int width;
	private final int height;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;
	private final EmiTexture background;

	public BLEmiRecipe(BLRecipeCategory category, RecipeHolder<T> recipe, int width, int height) {
		this.category = category;
		this.recipe = recipe;
		this.width = width;
		this.height = height;

		ResourceLocation recipeId = recipe.id();
		String path = String.format("emi/%s/%s/%s", category.name, recipeId.getNamespace(), recipeId.getPath());
		this.id = TheBetweenlands.prefix(path);
		var background = TheBetweenlands.prefix("textures/gui/viewer/" + category.name + "_grid.png");
		this.background = new EmiTexture(background, 0, 0, width, height);

		this.inputs = new ArrayList<>();
		this.inputs.addAll(recipe.value().getIngredients().stream().map(EmiIngredient::of).toList());
		this.addAdditionalInputs(this.inputs);
		this.outputs = new ArrayList<>();
		this.outputs.add(EmiStack.of(recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess())));
		this.addAdditionalOutputs(this.outputs);
	}

	protected void addAdditionalInputs(List<EmiIngredient> inputs) {

	};

	protected void addAdditionalOutputs(List<EmiStack> outputs) {

	};

	public T getRecipe() {
		return this.recipe.value();
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return this.category;
	}

	@Override
	@Nullable
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public int getDisplayWidth() {
		return this.width;
	}

	@Override
	public int getDisplayHeight() {
		return this.height;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return this.inputs;
	}

	@Override
	public List<EmiIngredient> getCatalysts() {
		return List.of(this.category.getIcon());
	}

	@Override
	public List<EmiStack> getOutputs() {
		return this.outputs;
	}

	public EmiTexture getBackgroundTexture() {
		return this.background;
	}
}
