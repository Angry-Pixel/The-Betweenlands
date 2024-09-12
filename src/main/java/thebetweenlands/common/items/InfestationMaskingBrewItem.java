package thebetweenlands.common.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.InfestationIgnoreData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class InfestationMaskingBrewItem extends Item {
	public InfestationMaskingBrewItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && entity instanceof Player player) {
			InfestationIgnoreData ignore = player.getData(AttachmentRegistry.INFESTATION_IGNORE);
			if (!ignore.isImmune(player))
				ignore.setImmune(player, Math.max(ignore.getRemainingImmunityTicks(player), 400));
		}
		return super.finishUsingItem(stack, level, entity);
	}
}
