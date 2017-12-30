package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.ICompostBinRecipe;

public class CompostRecipe implements ICompostBinRecipe {
	public static final List<ICompostBinRecipe> RECIPES = new ArrayList<ICompostBinRecipe>();

	private ItemStack input;
	private int compostAmount;
	private int compostTime;

	public CompostRecipe(int compostAmount, int compostTime, ItemStack input) {
		this.compostAmount = compostAmount;
		this.compostTime = compostTime;
		this.input = input;
	}

	public CompostRecipe(int compostAmount, int compostTime, Item input) {
		this.compostAmount = compostAmount;
		this.compostTime = compostTime;
		this.input = new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE);
	}

	public static void addRecipe(ICompostBinRecipe recipe) {
		RECIPES.add(recipe);
	}

	public static void addRecipe(int compostAmount, int compostTime, ItemStack stack) {
		RECIPES.add(new CompostRecipe(compostAmount, compostTime, stack));
	}

	public static void addRecipe(int compostAmount, int compostTime, Item compostItem) {
		RECIPES.add(new CompostRecipe(compostAmount, compostTime, compostItem));
	}

	public static void removeRecipe(ICompostBinRecipe recipe) {
		RECIPES.remove(recipe);
	}

	public static Item getItem(Block block) {
		return Item.getItemFromBlock(block);
	}

	public static ICompostBinRecipe getCompostRecipe(ItemStack stack) {
		for (ICompostBinRecipe compostRecipe : RECIPES) {
			if (compostRecipe.matchesInput(stack))
				return compostRecipe;
		}
		return null;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return this.input.getItemDamage() == OreDictionary.WILDCARD_VALUE ? this.input.getItem() == stack.getItem() : this.input.getItem() == stack.getItem() && this.input.getItemDamage() == stack.getItemDamage();
	}

	@Override
	public int getCompostAmount(ItemStack stack) {
		return this.compostAmount;
	}

	@Override
	public int getCompostingTime(ItemStack stack) {
		return this.compostTime;
	}


	public ItemStack getInput(){
		return input;
	}
}
