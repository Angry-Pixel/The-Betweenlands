package thebetweenlands.common.item.food;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class TaintedPotionItem extends Item {
	public TaintedPotionItem(Properties properties) {
		super(properties);
	}

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player)entityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!level.isClientSide) {
            entityLiving.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(180, 3));
            entityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 2));
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.consume(1, player);
        }

        if (player == null || !player.hasInfiniteMaterials()) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        entityLiving.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
	    return ItemUtils.startUsingInstantly(level, player, hand);
	}

	@Override
	public Component getName(ItemStack stack) {
		if(stack.has(DataComponentRegistry.ROTTEN_FOOD))
			return Component.translatable(this.getDescriptionId(stack) + ".potion", stack.get(DataComponentRegistry.ROTTEN_FOOD).originalStack().getHoverName());
		else
			return super.getName(stack);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return stack.has(DataComponentRegistry.ROTTEN_FOOD) ? stack.get(DataComponentRegistry.ROTTEN_FOOD).originalStack().getMaxStackSize() : super.getMaxStackSize(stack);
	}
}
