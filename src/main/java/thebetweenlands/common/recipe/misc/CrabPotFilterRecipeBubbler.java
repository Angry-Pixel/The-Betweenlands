package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeBubbler;

public class CrabPotFilterRecipeBubbler implements ICrabPotFilterRecipeBubbler {
	public static final List<ICrabPotFilterRecipeBubbler> RECIPES = new ArrayList<ICrabPotFilterRecipeBubbler>();

	private ItemStack input;
	private ItemStack output;

	public CrabPotFilterRecipeBubbler(ItemStack output, ItemStack input) {
		this.output = output;
		this.input = input;
	}

	public CrabPotFilterRecipeBubbler(Item output, Item input) {
		this.output = new ItemStack(output, OreDictionary.WILDCARD_VALUE);
		this.input = new ItemStack(input, OreDictionary.WILDCARD_VALUE);
	}

	public static void addRecipe(ICrabPotFilterRecipeBubbler recipe) {
		RECIPES.add(recipe);
	}

	public static void addRecipe(ItemStack stackOut, ItemStack stackIn) {
		RECIPES.add(new CrabPotFilterRecipeBubbler(stackOut, stackIn));
	}

	public static void addRecipe(Item itemOut, Item itemIn) {
		RECIPES.add(new CrabPotFilterRecipeBubbler(itemOut, itemIn));
	}

	public static void removeRecipe(ICrabPotFilterRecipeBubbler recipe) {
		RECIPES.remove(recipe);
	}

	public static Item getItem(Block block) {
		return Item.getItemFromBlock(block);
	}

	public static ICrabPotFilterRecipeBubbler getFilterRecipe(ItemStack stack) {
		for (ICrabPotFilterRecipeBubbler filterRecipe : RECIPES) {
			if (filterRecipe.matchesInput(stack))
				return filterRecipe;
		}
		return null;
	}

	public static ItemStack getRecipeOutput(ItemStack input) {
		for (ICrabPotFilterRecipeBubbler recipe : RECIPES) {
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
