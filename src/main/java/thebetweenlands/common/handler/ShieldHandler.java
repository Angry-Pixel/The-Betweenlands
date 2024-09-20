package thebetweenlands.common.handler;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.items.shield.BaseShieldItem;

public class ShieldHandler {

	static void applyShieldLogic(LivingIncomingDamageEvent event) {
		LivingEntity attacked = event.getEntity();
		DamageSource source = event.getSource();
		for(InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = attacked.getItemInHand(hand);
			if(!stack.isEmpty() && stack.getItem() instanceof BaseShieldItem shield) {

				if(shield.canBlockDamageSource(stack, attacked, hand, source)) {
					//Cancel event
					if(!attacked.level().isClientSide()) {
						event.setCanceled(true);
					}

					if(!attacked.level().isClientSide()) {
						//Apply damage with multiplier
						float defenderKbMultiplier = shield.getDefenderKnockbackMultiplier(stack, attacked, event.getAmount(), source);
						float newDamage = shield.getBlockedDamage(stack, attacked, event.getAmount(), source);
						if(newDamage > 0.0F) {
							Vec3 prevMotion = attacked.getDeltaMovement();
							DamageSource newSource = new DamageSource(Holder.direct(source.type())) {
								@Override
								public @Nullable Vec3 getSourcePosition() {
									return null;
								}
							};

							attacked.hurt(newSource, newDamage);
							attacked.setDeltaMovement(prevMotion);
						}
						if(source.getEntity() != null) {
							//Knock back defender
							double prevMotionY = attacked.getDeltaMovement().y();
							attacked.knockback(defenderKbMultiplier, source.getEntity().getX() - attacked.getX(), source.getEntity().getZ() - attacked.getZ());
							attacked.setDeltaMovement(attacked.getDeltaMovement().x(), prevMotionY, attacked.getDeltaMovement().z());
							attacked.hurtMarked = true;
						}
						//Shield block sound effect
						attacked.level().broadcastEntityEvent(attacked, (byte)29);
					}

					//Knock back attacker
					if(!attacked.level().isClientSide()) {
						if (source.getEntity() == source.getDirectEntity() && source.getEntity() instanceof LivingEntity living) {
							float attackerKbMultiplier = shield.getAttackerKnockbackMultiplier(stack, attacked, event.getAmount(), source);
							if(attackerKbMultiplier > 0.0F) {
								living.knockback(attackerKbMultiplier, attacked.getX() - source.getEntity().getX(), attacked.getZ() - source.getEntity().getZ());
							}
						}
					}

					if(attacked instanceof Player player) {
						int cooldown = shield.getShieldBlockingCooldown(stack, player, event.getAmount(), source);
						if(cooldown > 0) {
							player.getCooldowns().addCooldown(shield, cooldown);
							attacked.stopUsingItem();
						}
					}

					shield.onAttackBlocked(stack, attacked, event.getAmount(), source);

					if(!attacked.level().isClientSide()) {
						//Damage item
						int itemDamage = 1 + Mth.floor(event.getAmount());
						stack.hurtAndBreak(itemDamage, attacked, attacked.getEquipmentSlotForItem(stack));
						//Shield broke
						if (stack.getCount() <= 0) {
							shield.onShieldBreak(stack, attacked, hand, source);
						}
					}

					break;
				}
			}
		}
	}
}
