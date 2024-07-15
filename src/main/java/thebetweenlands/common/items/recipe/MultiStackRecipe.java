package thebetweenlands.common.items.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class MultiStackRecipe implements Recipe<MultiStackInput> {

	private final NonNullList<Ingredient> items;
	private final ItemStack result;

	public MultiStackRecipe(NonNullList<Ingredient> items, ItemStack result) {
		this.items = items;
		this.result = result;
	}

	@Override
	public boolean matches(MultiStackInput input, Level level) {
		if (input.size() != this.items.size()) {
			return false;
		}
		return input.size() == 1 && this.items.size() == 1
			? this.items.getFirst().test(input.getItem(0))
			: input.stackedContents().canCraft(this, null);
	}

	@Override
	public ItemStack assemble(MultiStackInput input, HolderLookup.Provider registries) {
		return this.result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= this.items.size();
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.result;
	}

	public NonNullList<Ingredient> getItems() {
		return this.items;
	}

	public ItemStack getResult() {
		return this.result;
	}
}
