package thebetweenlands.common.item.misc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UndyingEmberItem extends Item {
	public UndyingEmberItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		//TODO
//		if (interactionTarget instanceof WildEmberling wild) {
//			Emberling newEmberling = new Emberling(player.level());
//			if (!player.level().isClientSide()) {
//				newEmberling.moveTo(wild);
//				newEmberling.setTamedBy(player);
//				oldEmberling.discard();
//				player.level().addFreshEntity(newEmberling);
//				stack.consume(1, player);
//			} else {
//				wild.playTameEffect(true);
//			}
//			return InteractionResult.sidedSuccess(player.level().isClientSide());
//		}
		return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
	}
}
