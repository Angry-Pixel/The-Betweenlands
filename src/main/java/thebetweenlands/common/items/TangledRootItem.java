package thebetweenlands.common.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;

public class TangledRootItem extends Item {

	public TangledRootItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
		if (!level.isClientSide()) {
			living.removeEffectsCuredBy(EffectCures.MILK);
		}

		return super.finishUsingItem(stack, level, living);
	}
}
