package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IDruidAltarRecipe;

public class DruidAltarRecipe implements IDruidAltarRecipe {
	private static ArrayList<IDruidAltarRecipe> druidAltarRecipes = new ArrayList<IDruidAltarRecipe>();

	private ItemStack input1;
	private ItemStack input2;
	private ItemStack input3;
	private ItemStack input4;
	private ItemStack output;

	public DruidAltarRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack output) {
		this.input1 = input1;
		this.input2 = input2;
		this.input3 = input3;
		this.input4 = input4;
		this.output = output;
	}

	public static void addRecipe(IDruidAltarRecipe recipe) {
		druidAltarRecipes.add(recipe);
	}

	public static void addRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack output) {
		druidAltarRecipes.add(new DruidAltarRecipe(input1, input2, input3, input4, output));
	}

	public static void removeRecipe(IDruidAltarRecipe recipe) {
		druidAltarRecipes.remove(recipe);
	}

	public static List<IDruidAltarRecipe> getRecipes() {
		return Collections.unmodifiableList(druidAltarRecipes);
	}

	public static Item getItem(Block block) {
		return Item.getItemFromBlock(block);
	}

	/*public static DruidAltarRecipe getDruidAltarRecipe(ItemStack output) {
		for (IDruidAltarRecipe druidAltarRecipe : druidAltarRecipes) {
			if (druidAltarRecipe.output.getItem() == output.getItem() && druidAltarRecipe.output.getItemDamage() == output.getItemDamage())
				return druidAltarRecipe;
		}
		return null;
	}*/

	public static IDruidAltarRecipe getDruidAltarRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4) {
		ItemStack[] input = new ItemStack[]{input1, input2, input3, input4};
		for (IDruidAltarRecipe druidAltarRecipe : druidAltarRecipes) {
			if(druidAltarRecipe.matchesInput(input)) {
				return druidAltarRecipe;
			}
		}
		return null;
	}

	public static boolean isValidItem(ItemStack stack) {
		for (IDruidAltarRecipe recipe : druidAltarRecipes) {
			if(recipe.containsInputItem(stack)) {
				return true;
			}
		}
		return false;
	}

	private static boolean matches(ItemStack input, ItemStack toCheck) {
		return toCheck.getItem() == input.getItem() && (input.getItemDamage() == OreDictionary.WILDCARD_VALUE || toCheck.getItemDamage() == input.getItemDamage());
	}

	public ArrayList<ItemStack> getInputs(){
		ArrayList<ItemStack> l = new ArrayList<>();
		l.add(input1);
		l.add(input2);
		l.add(input3);
		l.add(input4);
		return l;
	}

	@Override
	public boolean matchesInput(ItemStack[] input) {
		ArrayList<ItemStack> recipeStacks = new ArrayList<ItemStack>();
		recipeStacks.add(this.input1);
		recipeStacks.add(this.input2);
		recipeStacks.add(this.input3);
		recipeStacks.add(this.input4);
		boolean next = false;
		if (input[0] == null || input[1] == null || input[2] == null || input[3] == null)
			return false;
		for (ItemStack itemStack : recipeStacks) {
			if (matches(itemStack, input[0])) {
				next = true;
				recipeStacks.remove(itemStack);
			}
			if (next)
				break;
		}
		if (next) {
			next = false;
			for (ItemStack itemStack : recipeStacks) {
				if (matches(itemStack, input[1])) {
					next = true;
					recipeStacks.remove(itemStack);
				}
				if (next)
					break;
			}
		}
		if (next) {
			next = false;
			for (ItemStack itemStack : recipeStacks) {
				if (matches(itemStack, input[2])) {
					next = true;
					recipeStacks.remove(itemStack);
				}
				if (next)
					break;
			}
		}
		if (next) {
			next = false;
			for (ItemStack itemStack : recipeStacks) {
				if (matches(itemStack, input[3])) {
					next = true;
					recipeStacks.remove(itemStack);
				}
				if (next)
					break;
			}
		}
		return next && recipeStacks.size() == 0;
	}

	public ItemStack getDefaultOutput() {
		return this.output;
	}

	@Override
	public ItemStack getOutput(ItemStack[] input) {
		return this.output;
	}

	@Override
	public boolean containsInputItem(ItemStack input) {
		return matches(this.input1, input) || matches(this.input2, input) || matches(this.input3, input) || matches(this.input4, input);
	}
}
