package thebetweenlands.common.item.food;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.FallDamageReductionData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class FallDamageReductionBrewItem extends Item {
	public FallDamageReductionBrewItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && entity instanceof Player player) {
			FallDamageReductionData reduce = player.getData(AttachmentRegistry.FALL_DAMAGE_REDUCTION);
			if (!reduce.isActive(player))
				reduce.setActive(player, Math.max(reduce.getRemainingImmunityTicks(player), 400));
		}
		return super.finishUsingItem(stack, level, entity);
	}
}
