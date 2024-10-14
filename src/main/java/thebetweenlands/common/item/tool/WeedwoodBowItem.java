package thebetweenlands.common.item.tool;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.item.misc.OctineIngotItem;
import thebetweenlands.common.item.tool.arrow.*;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class WeedwoodBowItem extends BowItem {

	public WeedwoodBowItem(Properties properties) {
		super(properties);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {
			ItemStack itemstack = player.getProjectile(stack);
			if (!itemstack.isEmpty()) {
				int i = this.getUseDuration(stack, entityLiving) - timeLeft;
				i = EventHooks.onArrowLoose(stack, level, player, i, !itemstack.isEmpty());
				if (i < 0) return;
				float f = getPowerForTime(i);
				f *= CorrosionHelper.getModifier(stack);
				if (!((double) f < 0.1)) {
					List<ItemStack> list = draw(stack, itemstack, player);
					if (level instanceof ServerLevel serverlevel && !list.isEmpty()) {
						this.shoot(serverlevel, player, player.getUsedItemHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
					}

					level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	public static float	getArrowType(Player player, ItemStack stack) {
		return switch (player.getProjectile(stack).getItem()) {
			case PoisonAnglerToothArrowItem ignored -> 1.0F;
			case OctineIngotItem ignored -> 2.0F;
			case BasiliskArrowItem ignored -> 3.0F;
			case ShockArrowItem ignored -> 4.0F;
			case ChiromawBarbItem ignored -> 5.0F;
			case SludgeWormArrowItem ignored -> 6.0F;
			default -> 0.0F;
		};
	}
}
