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
		if(stack.has(DataComponentRegistry.ROTTEN_FOOD))
			return Component.translatable(stack.get(DataComponentRegistry.ROTTEN_FOOD).originalStack().getDescriptionId());
		else
			return super.getName(stack);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return stack.has(DataComponentRegistry.ROTTEN_FOOD) ? stack.get(DataComponentRegistry.ROTTEN_FOOD).originalStack().getMaxStackSize() : super.getMaxStackSize(stack);
	}
}
