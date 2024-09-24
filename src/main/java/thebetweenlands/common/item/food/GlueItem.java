package thebetweenlands.common.item.food;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.DataComponentRegistry;

public class GlueItem extends Item {
	public GlueItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (player.canEat(true)) {
			player.startUsingItem(hand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
		} else {
			return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
		stack.shrink(1);

		if (living instanceof Player player) {
			level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
			if (!level.isClientSide()) {
				player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 1));
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
				player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));
			}
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if(entity instanceof Player player) {
			String uuidStr = player.getUUID().toString();
			if("ea341fd9-27d1-4ffe-a1e0-5b05a5c8a234".equals(uuidStr)) {
				stack.set(DataComponentRegistry.GLU, Unit.INSTANCE);
			}
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 32;
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		if(stack.has(DataComponentRegistry.GLU)) {
			return "Sniff.. sniff... Hmm, I like this stuff";
		}
		return super.getDescriptionId(stack);
	}
}
