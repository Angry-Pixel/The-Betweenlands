package thebetweenlands.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class PurifierRecipe {

	private static final List<PurifierRecipe> recipes = new ArrayList<PurifierRecipe>();

	/**
	 *
	 * @param output
	 *            what will be produced by the recipe
	 * @param inputs
	 *            the input item for the recipe
	 */
	public static void addRecipe(ItemStack output, ItemStack inputs) {
		recipes.add(new PurifierRecipe(output, inputs));
	}

	public static ItemStack getOutput(ItemStack inputs) {
		for (PurifierRecipe recipe : recipes)
			if (recipe.matches(inputs))
				return recipe.getOutput();

		return null;
	}

	public static List<PurifierRecipe> getRecipeList() {
		return Collections.unmodifiableList(recipes);
	}

	private final ItemStack output;
	private final ItemStack inputs;

	private PurifierRecipe(ItemStack output, ItemStack inputs) {
		this.output = ItemStack.copyItemStack(output);
		this.inputs = ItemStack.copyItemStack(inputs);

			if (inputs instanceof ItemStack)
				inputs = ItemStack.copyItemStack((ItemStack) inputs);

			else
				throw new IllegalArgumentException("Input must be an ItemStack");
	}

	public ItemStack getInputs() {
		return ItemStack.copyItemStack(inputs);
	}

	public ItemStack getOutput() {
		return ItemStack.copyItemStack(output);
	}


	public boolean matches(ItemStack stacks) {
		if (stacks != null)
			if (areStacksTheSame(getInputs(), stacks)) {
				stacks = null;
				return true;
			}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean areStacksTheSame(Object obj, ItemStack target) {
		if (obj instanceof ItemStack)
			return areStacksTheSame((ItemStack) obj, target, false);
		else if (obj instanceof List) {
			List<ItemStack> list = (List<ItemStack>) obj;
			for (ItemStack stack : list)
				if (areStacksTheSame(stack, target, false))
					return true;
		}

		return false;
	}
	
	public static boolean areStacksTheSame(ItemStack stack1, ItemStack stack2, boolean matchSize) {
		if (stack1 == null || stack2 == null)
			return false;

		if (stack1.getItem() == stack2.getItem())
			if (stack1.getItemDamage() == stack2.getItemDamage() || isWildcard(stack1.getItemDamage()) || isWildcard(stack2.getItemDamage()))
				if (!matchSize || stack1.stackSize == stack2.stackSize) {
					if (stack1.hasTagCompound() && stack2.hasTagCompound())
						return stack1.getTagCompound().equals(stack2.getTagCompound());
					return stack1.hasTagCompound() == stack2.hasTagCompound();
				}
		return false;
	}
	
	private static boolean isWildcard(int meta) {
		return meta == OreDictionary.WILDCARD_VALUE;
	}
}