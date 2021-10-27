package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.api.recipes.ISmokingRackRecipe;

public class SmokingRackRecipe implements ISmokingRackRecipe {
	public static final List<ISmokingRackRecipe> RECIPES = new ArrayList<ISmokingRackRecipe>();

	private ItemStack input;
	private ItemStack output;
	private int smokingTime;

	public SmokingRackRecipe(ItemStack output, int smokingTime, ItemStack input) {
		this.output = output;
		this.smokingTime = smokingTime;
		this.input = input;
	}

	public SmokingRackRecipe(Item output, int smokingTime, Item input) {
		this.output = new ItemStack(output, 1, OreDictionary.WILDCARD_VALUE);
		this.smokingTime = smokingTime;
		this.input = new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE);
	}

	public static void addRecipe(ISmokingRackRecipe recipe) {
		RECIPES.add(recipe);
	}

	public static void addRecipe(ItemStack stackOut, int smokingTime, ItemStack stackIn) {
		RECIPES.add(new SmokingRackRecipe(stackOut, smokingTime, stackIn));
	}

	public static void addRecipe(Item itemOut, int smokingTime, Item itemIn) {
		RECIPES.add(new SmokingRackRecipe(itemOut, smokingTime, itemIn));
	}

	public static void removeRecipe(ISmokingRackRecipe recipe) {
		RECIPES.remove(recipe);
	}

	public static Item getItem(Block block) {
		return Item.getItemFromBlock(block);
	}

	public static ISmokingRackRecipe getSmokingRecipe(ItemStack stack) {
		for (ISmokingRackRecipe smokingRecipe : RECIPES) {
			if (smokingRecipe.matchesInput(stack))
				return smokingRecipe;
		}
		return null;
	}

	public static ItemStack getRecipeOutput(ItemStack input) {
		for (ISmokingRackRecipe recipe : RECIPES) {
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

	@Override
	public int getSmokingTime(ItemStack stack) {
		return this.smokingTime;
	}

	public ItemStack getInput(){
		return input;
	}

	@Override
	public ItemStack getOutput(ItemStack stack) {
		return this.output;
	}

	public static List<ISmokingRackRecipe> getRecipeList() {
		return Collections.unmodifiableList(RECIPES);
	}
}
