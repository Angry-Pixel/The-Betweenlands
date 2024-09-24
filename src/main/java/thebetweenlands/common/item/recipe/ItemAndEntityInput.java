package thebetweenlands.common.item.recipe;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nullable;

public record ItemAndEntityInput(@Nullable EntityType<?> type, ItemStack stack) implements RecipeInput {

	@Override
	public ItemStack getItem(int index) {
		if (index != 0) {
			throw new IllegalArgumentException("No item for index " + index);
		} else {
			return this.stack;
		}
	}

	@Override
	public int size() {
		return 1;
	}
}
