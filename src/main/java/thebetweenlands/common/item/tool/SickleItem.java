package thebetweenlands.common.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import thebetweenlands.common.TheBetweenlands;

public class SickleItem extends Item {
	public SickleItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return itemAbility == TheBetweenlands.SICKLE_HARVEST;
	}
}
