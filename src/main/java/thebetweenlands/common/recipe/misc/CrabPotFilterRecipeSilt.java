package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeSilt;

public class CrabPotFilterRecipeSilt implements ICrabPotFilterRecipeSilt {
	public static final List<ICrabPotFilterRecipeSilt> RECIPES = new ArrayList<ICrabPotFilterRecipeSilt>();

	private ItemStack input;
	private ItemStack output;

	public CrabPotFilterRecipeSilt(ItemStack output, ItemStack input) {
		this.output = output;
		this.input = input;
	}

	public CrabPotFilterRecipeSilt(Item output, Item input) {
		this.output = new ItemStack(output, OreDictionary.WILDCARD_VALUE);
		this.input = new ItemStack(input, OreDictionary.WILDCARD_VALUE);
	}

	public static void addRecipe(ICrabPotFilterRecipeSilt recipe) {
		RECIPES.add(recipe);
	}

	public static void addRecipe(ItemStack stackOut, ItemStack stackIn) {
		RECIPES.add(new CrabPotFilterRecipeSilt(stackOut, stackIn));
	}

	public static void addRecipe(Item itemOut, Item itemIn) {
		RECIPES.add(new CrabPotFilterRecipeSilt(itemOut, itemIn));
	}

	public static void removeRecipe(ICrabPotFilterRecipeSilt recipe) {
		RECIPES.remove(recipe);
	}

	public static Item getItem(Block block) {
		return Item.getItemFromBlock(block);
	}

	public static ICrabPotFilterRecipeSilt getFilterRecipe(ItemStack stack) {
		for (ICrabPotFilterRecipeSilt filterRecipe : RECIPES) {
			if (filterRecipe.matchesInput(stack))
				return filterRecipe;
		}
		return null;
	}

	public static ItemStack getRecipeOutput(ItemStack input) {
		for (ICrabPotFilterRecipeSilt recipe : RECIPES) {
			if (recipe.matchesInput(input)) {
				return recipe.getOutput(input);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return this.input.getItemDamage() == OreDictionary.WILDCARD_VALUE ? this.input.getItem() == stack.getItem() : this.input.getItem() == stack.getItem() && this.input.getItemDamage() == stack.getItemDamage();
	}

	public ItemStack getOutput() {
		return this.output;
	}

	public ItemStack getInput(){
		return input;
	}

	@Override
	public ItemStack getOutput(ItemStack stack) {
		return this.output;
	}
}
