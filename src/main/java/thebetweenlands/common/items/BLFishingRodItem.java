package thebetweenlands.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.entities.fishing.BLFishHook;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class BLFishingRodItem extends Item {
	public BLFishingRodItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.getDamageValue() == this.getMaxDamage(stack)) {
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
				if (player.fishing.getHookedIn() != null && !player.fishing.isPassenger())
					player.fishing.setHookedEntity(null);

				if (player.fishing.getHookedIn() != null && stack.has(DataComponentRegistry.FISHING_ROD_BAIT)) {
					stack.set(DataComponentRegistry.FISHING_ROD_BAIT, false);
					if (((Anadia) player.fishing.getHookedIn()).getStaminaTicks() % 20 == 0 && ((Anadia) player.fishing.getHookedIn()).getStaminaTicks() != 0) {
						stack.hurtAndBreak(i, player, LivingEntity.getSlotForHand(hand));
						level.playSound(null, player.blockPosition(), SoundRegistry.BL_FISHING_ROD_CREAK.get(), SoundSource.NEUTRAL, 0.2F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
					}
				}
				if (player.fishing.getHookedIn() == null && (int) player.fishing.distanceTo(player.fishing.getPlayerOwner()) > 0)
					stack.hurtAndBreak(i, player, LivingEntity.getSlotForHand(hand));

				if ((int) player.fishing.distanceTo(player.fishing.getPlayerOwner()) <= 0 && !player.fishing.isPassenger()) {
					player.fishing.discard();
				}
			}
		} else {
			level.playSound(null, player.blockPosition(), SoundRegistry.BL_FISHING_CAST.get(), SoundSource.NEUTRAL, 0.2F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

			if (!level.isClientSide() && player.fishing == null) {
				BLFishHook hook = new BLFishHook(player, level);
				if (stack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false))
					hook.setBaited(true);
				level.addFreshEntity(hook);
			}

			player.swing(hand);
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		ItemStack otherStack;
		if (hand == InteractionHand.MAIN_HAND) {
			otherStack = player.getOffhandItem();
		} else {
			otherStack = player.getMainHandItem();
		}

		if (!otherStack.isEmpty() && otherStack.is(ItemRegistry.NET)) {
			// Allow net to be used to catch fish
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (stack.getCount() != 1 || action != ClickAction.SECONDARY || stack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false)) {
			return false;
		} else {
			if (slot.hasItem() && (slot.getItem().is(ItemRegistry.TINY_SLUDGE_WORM) || slot.getItem().is(ItemRegistry.TINY_SLUDGE_WORM_HELPER))) {
				stack.set(DataComponentRegistry.FISHING_ROD_BAIT, true);
				slot.remove(1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (stack.getCount() != 1 || action != ClickAction.SECONDARY || stack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false)) {
			return false;
		} else if (slot.allowModification(player)) {
			if (other.is(ItemRegistry.TINY_SLUDGE_WORM) || other.is(ItemRegistry.TINY_SLUDGE_WORM_HELPER)) {
				stack.set(DataComponentRegistry.FISHING_ROD_BAIT, true);
				other.shrink(1);
				return true;
			}
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.getDamageValue() == stack.getMaxDamage()) {
			tooltip.add(Component.translatable("item.thebetweenlands.weedwood_fishing_rod.broken", stack.getDisplayName()).withStyle(ChatFormatting.GRAY));
		}
		tooltip.add(Component.translatable("item.thebetweenlands.weedwood_fishing_rod.baited", stack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(ability);
	}
}
