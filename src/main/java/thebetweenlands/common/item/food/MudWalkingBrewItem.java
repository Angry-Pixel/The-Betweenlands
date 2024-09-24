package thebetweenlands.common.item.food;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.MudWalkerData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class MudWalkingBrewItem extends Item {
	public MudWalkingBrewItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && entity instanceof Player player) {
			MudWalkerData mudWalk = player.getData(AttachmentRegistry.MUD_WALKER);
			if (!mudWalk.isActive(player)) {
				mudWalk.setActive(player, Math.max(mudWalk.getRemainingActiveTicks(player), 400));
			}
		}
		return super.finishUsingItem(stack, level, entity);
	}
}
