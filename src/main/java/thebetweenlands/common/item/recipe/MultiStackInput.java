package thebetweenlands.common.item.recipe;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class MultiStackInput implements RecipeInput {

	private final List<ItemStack> items;
	private final StackedContents stackedContents = new StackedContents();
	private final int ingredientCount;

	public MultiStackInput(List<ItemStack> item) {
		this.items = item;
		int i = 0;

		for (ItemStack itemstack : item) {
			if (!itemstack.isEmpty()) {
				i++;
				this.stackedContents.accountStack(itemstack, 1);
			}
		}

		this.ingredientCount = i;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public int size() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		return this.ingredientCount == 0;
	}

	public StackedContents stackedContents() {
		return this.stackedContents;
	}

	public List<ItemStack> items() {
		return this.items;
	}

	public int ingredientCount() {
		return this.ingredientCount;
	}
}
