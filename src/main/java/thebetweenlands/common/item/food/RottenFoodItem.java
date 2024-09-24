package thebetweenlands.common.item.food;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.registries.DataComponentRegistry;

public class RottenFoodItem extends Item {
	public RottenFoodItem(Properties properties) {
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(stack.getOrDefault(DataComponentRegistry.ROTTEN_FOOD, stack).getDescriptionId());
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.ROTTEN_FOOD, stack).getMaxStackSize();
	}
}
