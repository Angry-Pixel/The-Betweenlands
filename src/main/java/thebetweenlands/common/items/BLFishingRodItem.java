package thebetweenlands.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.entities.fishing.BLFishHook;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BLFishingRodItem extends Item {
	public BLFishingRodItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if(stack.getMaxDamage() == this.getMaxDamage(stack)) {
			if (!level.isClientSide() && player.fishing != null) {
				player.fishing.discard();
				level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.AMBIENT, 1F, 1F);
			}
			return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
		}

		if (player.fishing != null) {
			int i = player.fishing.retrieve(stack);
			level.playSound(null, player.blockPosition(), SoundRegistry.BL_FISHING_REEL.get(), SoundSource.NEUTRAL, 1.0F, 1F);

			if (!level.isClientSide() && player.fishing != null) {
				//fixes stupid entity MobItem still being ridden after netted
				if(player.fishing.getHookedIn() != null && !player.fishing.isPassenger())
					player.fishing.setHookedEntity(null);

				if (player.fishing.getHookedIn() != null && stack.has(DataComponentRegistry.FISHING_ROD_BAIT)) {
					stack.set(DataComponentRegistry.FISHING_ROD_BAIT, false);
					if (((Anadia) player.fishing.getHookedIn()).getStaminaTicks() % 20 == 0 && ((Anadia) player.fishing.getHookedIn()).getStaminaTicks() != 0) {
						stack.hurtAndBreak(i, player, LivingEntity.getSlotForHand(hand));
						level.playSound(null, player.blockPosition(), SoundRegistry.BL_FISHING_ROD_CREAK.get(), SoundSource.NEUTRAL, 0.2F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
					}
				}
				if (player.fishing.getHookedIn() == null && (int)player.fishing.distanceTo(player.fishing.getPlayerOwner()) > 0)
					stack.hurtAndBreak(i, player, LivingEntity.getSlotForHand(hand));

				if ((int)player.fishing.distanceTo(player.fishing.getPlayerOwner()) <= 0 && !player.fishing.isPassenger()) {
					player.fishing.discard();
				}
			}
		} else {
			level.playSound(null, player.blockPosition(), SoundRegistry.BL_FISHING_CAST.get(), SoundSource.NEUTRAL, 0.2F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

			if (!level.isClientSide() && player.fishing == null) {
				BLFishHook hook = new BLFishHook(player, level);
				if(stack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false))
					hook.setBaited(true);
				level.addFreshEntity(hook);
			}

			player.swing(hand);
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		ItemStack otherStack;
		if(hand == InteractionHand.MAIN_HAND) {
			otherStack = player.getOffhandItem();
		} else {
			otherStack = player.getMainHandItem();
		}

		if(!otherStack.isEmpty() && otherStack.is(ItemRegistry.NET)) {
			// Allow net to be used to catch fish
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}
}
