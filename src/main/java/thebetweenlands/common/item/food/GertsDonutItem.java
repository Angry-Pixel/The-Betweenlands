package thebetweenlands.common.item.food;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GertsDonutItem extends Item {
	public GertsDonutItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		livingEntity.heal(8.0F);
		return super.finishUsingItem(stack, level, livingEntity);
	}
}
