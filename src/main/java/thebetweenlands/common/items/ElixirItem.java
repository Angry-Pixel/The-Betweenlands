package thebetweenlands.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.entities.ThrownElixir;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;

public class ElixirItem extends Item {
	public ElixirItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (player.isShiftKeyDown()) {
			player.getItemInHand(hand).set(DataComponentRegistry.THROWING, Unit.INSTANCE);
		}
		return ItemUtils.startUsingInstantly(level, player, hand);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
		if (stack.has(DataComponentRegistry.THROWING)) {
			entity.playSound(SoundEvents.ARROW_SHOOT, 0.5F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!level.isClientSide()) {
				int useCount = this.getUseDuration(stack, entity) - timeCharged;
				ThrownElixir elixir = new ThrownElixir(level, entity);
				elixir.setItem(stack);
				float strength = Math.min(0.2F + useCount / 20.0F, 1.0F);
				elixir.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), -20.0F, strength, 1.0F);
				level.addFreshEntity(elixir);

				stack.remove(DataComponentRegistry.THROWING);
				if (entity instanceof Player player && !player.hasInfiniteMaterials()) {
					stack.shrink(1);
				}
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!stack.has(DataComponentRegistry.THROWING)) {
			Player player = entity instanceof Player ? (Player) entity : null;
			if (player instanceof ServerPlayer) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
			}

			if (!level.isClientSide()) {
				ElixirContents contents = stack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY);
				if (contents.elixir().isPresent()) {
					MobEffectInstance effect = contents.elixir().get().value().createEffect(contents.duration(), contents.strength());
					if (effect.getEffect().value().isInstantenous()) {
						effect.getEffect().value().applyInstantenousEffect(player, player, entity, contents.strength(), 1.0);
					} else {
						entity.addEffect(effect);
					}

				}
			}

			if (player != null) {
				player.awardStat(Stats.ITEM_USED.get(this));
				stack.consume(1, player);
			}

			if (player == null || !player.hasInfiniteMaterials()) {
				if (stack.isEmpty()) {
					return this.getCraftingRemainingItem(stack);
				}

				if (player != null) {
					player.getInventory().add(this.getCraftingRemainingItem(stack));
				}
			}

			entity.gameEvent(GameEvent.DRINK);
		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return stack.has(DataComponentRegistry.THROWING) ? 10000000 : 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return stack.has(DataComponentRegistry.THROWING) ? UseAnim.BOW : UseAnim.DRINK;
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return ElixirEffect.getName(stack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).elixir(), this.getDescriptionId());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		stack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).addElixirTooltip(tooltip::add, context.registries(), 1.0F, context.tickRate());
	}
}
