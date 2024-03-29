package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.ISteepingPotRecipe;
import thebetweenlands.common.inventory.container.ContainerSilkBundle;

public class SteepingPotRecipes implements ISteepingPotRecipe {

	private static final List<SteepingPotRecipes> recipes = new ArrayList<SteepingPotRecipes>();

	public static void addRecipe(ISteepingPotRecipe recipe) {
		if(recipe.getOutputFluidStack() != null) {
			addRecipe(
					recipe.getOutputFluidStack(),
					recipe.getOutputFluidMeta(),
					recipe.getInputFluidStack(),
					recipe.getInputs()
			);
		} else {
			addRecipe(
					recipe.getOutputItem(),
					recipe.getInputFluidStack(),
					recipe.getInputs()
			);
		}
	}

	public static void removeRecipe(ISteepingPotRecipe recipe) {
		if(recipe.getOutputFluidStack() != null) {
			recipes.removeIf(r -> r.getOutputFluidStack() == recipe.getOutputFluidStack() && r.getInputFluidStack() == recipe.getInputFluidStack());
		} else {
			recipes.removeIf(r -> r.getOutputItem() == recipe.getOutputItem() && r.getInputFluidStack() == recipe.getInputFluidStack());
		}
	}

	public static void addRecipe(ItemStack output, Fluid fluid, Object... input) {
		addRecipe(output, new FluidStack(fluid, Fluid.BUCKET_VOLUME), input);
	}

	public static void addRecipe(Fluid output, int outputFluidMeta, Fluid fluid, Object... input) {
		addRecipe(new FluidStack(output, Fluid.BUCKET_VOLUME), outputFluidMeta, new FluidStack(fluid, Fluid.BUCKET_VOLUME), input);
	}

	public static void addRecipe(ItemStack output, FluidStack fluid,  Object... input) {
		whitelistIngredient(input);
		recipes.add(new SteepingPotRecipes(output, fluid, input));
	}

	public static void addRecipe(FluidStack output, int outputFluidMeta, FluidStack fluid,  Object... input) {
		whitelistIngredient(input);
		recipes.add(new SteepingPotRecipes(output, outputFluidMeta, fluid, input));
	}

	private static void whitelistIngredient(Object... input) {
		for (int i = 0; i < input.length; i++) {
			if (input[i] instanceof ItemStack) {
				ContainerSilkBundle.acceptedItems.add(((ItemStack) input[i]).copy());
			} else if (input[i] instanceof String) {
				ContainerSilkBundle.acceptedItems.add(OreDictionary.getOres((String) input[i]).get(0));
			}
		}
	}

	public static ItemStack getOutputItem(IFluidTank tank, ItemStack... input) {
		SteepingPotRecipes recipe = getRecipe(tank, input);
		return recipe != null ? recipe.getOutputItem() : ItemStack.EMPTY;
	}

	public static FluidStack getOutputFluid(IFluidTank tank, ItemStack... input) {
		SteepingPotRecipes recipe = getRecipe(tank, input);
		return recipe != null ? recipe.getOutputFluidStack() : null;
	}

	public static SteepingPotRecipes getRecipe(IFluidTank tank, ItemStack... input) {
		for (SteepingPotRecipes recipe : recipes)
			if (recipe.matches(tank, input))
				return recipe;
		return null;
	}
	
	public static SteepingPotRecipes getRecipeForInputStack(ItemStack stack) {
		for (SteepingPotRecipes recipe : recipes)
			if (recipe.containsInput(stack))
				return recipe;
		return null;
	}

	public static List<SteepingPotRecipes> getRecipeList() {
		return Collections.unmodifiableList(recipes);
	}

	private final ItemStack output;
	private final FluidStack fluidStackIn;
	private final FluidStack fluidStackOut;
	private final int fluidMeta;
	private final Object[] input;

	public SteepingPotRecipes(ItemStack output, FluidStack fluidIn, Object... input) {
		this.output = output.copy();
		this.fluidStackOut = null;
		this.fluidMeta = 0;
		this.fluidStackIn = fluidIn;
		this.input = new Object[4];

		if (input.length > 4)
			throw new IllegalArgumentException("Input must be 1 to 4.");

		for (int c = 0; c < input.length; c++)
			if (input[c] instanceof ItemStack)
				this.input[c] = ((ItemStack) input[c]).copy();
			else if (input[c] instanceof String)
				this.input[c] = OreDictionary.getOres((String) input[c]);
			else
				throw new IllegalArgumentException("Input must be an ItemStack or an OreDictionary name");

		for (int i = input.length; i < 4; i++) {
			this.input[i] = ItemStack.EMPTY.copy();
		}
	}

	public SteepingPotRecipes(FluidStack fluidOut, int outputFluidMeta, FluidStack fluidIn, Object... input) {
		this.output = ItemStack.EMPTY;
		this.fluidStackOut = fluidOut.copy();
		this.fluidMeta = outputFluidMeta;
		this.fluidStackIn = fluidIn;
		this.input = new Object[4];

		if (input.length > 4)
			throw new IllegalArgumentException("Input must be 1 to 4.");

		for (int c = 0; c < input.length; c++)
			if (input[c] instanceof ItemStack)
				this.input[c] = ((ItemStack) input[c]).copy();
			else if (input[c] instanceof String)
				this.input[c] = OreDictionary.getOres((String) input[c]);
			else
				throw new IllegalArgumentException("Input must be an ItemStack or an OreDictionary name");

		for (int i = input.length; i < 4; i++) {
			this.input[i] = ItemStack.EMPTY.copy();
		}
	}

	@Override
	public Object[] getInputs() {
		return input;
	}

	@Override
	public List<ItemStack> getInputAsStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		for (int c = 0; c < getInputs().length; c++)
			if (getInputs()[c] instanceof ItemStack)
				stacks.add((ItemStack) getInputs()[c]);
			else
				stacks.add(ItemStack.EMPTY);
		return stacks;
	}

	@Override
	public FluidStack getInputFluidStack() {
		return fluidStackIn;
	}

	@Override
	public ItemStack getOutputItem() {
		return output.copy();
	}

	@Override
	public FluidStack getOutputFluidStack() {
		return fluidStackOut == null ? null : fluidStackOut.copy();
	}

	@Override
	public int getOutputFluidMeta() {
		return fluidMeta;
	}

	@Override
	public boolean matches(IFluidTank tankIn, ItemStack... stacks) {
		if (tankIn.getFluid() == null || !tankIn.getFluid().isFluidEqual(getInputFluidStack()))
			return false;
		if (tankIn.getFluidAmount() < getInputFluidStack().amount)
			return false;

		List<ItemStack> inputList = Lists.newArrayList();
		for (Object inputIt : input)
			inputList.add((ItemStack) inputIt);

		List<Ingredient> stackList = Lists.newArrayList();
		for (ItemStack stackIt : stacks)
			stackList.add(Ingredient.fromStacks(stackIt));

		return RecipeMatcher.findMatches(inputList, stackList) != null;
	}

	@Override
	public boolean containsInput(ItemStack stack) {
		for (Object inputIt : input) {
			if(inputIt instanceof ItemStack && Ingredient.fromStacks((ItemStack) inputIt).apply(stack)) {
				return true;
			}
		}
		return false;
	}

}