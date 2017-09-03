package thebetweenlands.common.handler;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PlayerDecayHandler {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.START) {
			EntityPlayer player = event.player;

			if(!player.world.isRemote && player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
				IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);

				float currentMaxHealth = (float) player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
				float decayMaxHealth = (int)(capability.getMaxPlayerHealth() / 2.0F) * 2;

				DecayStats stats = capability.getDecayStats();

				//Only reset the health to default once, hopefully should improve mod compatilibity?
				boolean requiresHealthReset = capability.isDecayEnabled() ? (stats.getDecayLevel() != stats.getPrevDecayLevel() && currentMaxHealth < 20.0F && decayMaxHealth >= 20.0F) : (currentMaxHealth < 20.0F);

				if(!capability.isDecayEnabled()) {
					if(requiresHealthReset) {
						player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0F);
					}
				} else {
					//Clamp health
					if(decayMaxHealth < 20.0F || requiresHealthReset) {
						player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(decayMaxHealth);
						if(player.getHealth() > decayMaxHealth) {
							player.setHealth(decayMaxHealth);
						}
					}

					int decay = stats.getDecayLevel();

					if (decay >= 16) {
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, 2, true, false));
						player.jumpMovementFactor = 0.001F;
					} else if (decay >= 13) {
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, 1, true, false));
						player.jumpMovementFactor = 0.002F;
					} else if (decay >= 10) {
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, 0, true, false));
					}

					if(!event.player.isRiding()) {
						EnumDifficulty difficulty = player.world.getDifficulty();

						float decaySpeed = 0.0F;

						switch(difficulty) {
						case PEACEFUL:
							decaySpeed = 0.0F;
							break;
						case EASY:
							decaySpeed = 0.0025F;
							break;
						case NORMAL:
							decaySpeed = 0.0033F;
							break;
						case HARD:
							decaySpeed = 0.005F;
							break;
						}

						if(player.isInWater()) {
							decaySpeed *= 2.75F;
						}

						if(decaySpeed > 0.0F) {
							stats.addDecayAcceleration(decaySpeed);
						}
					}

					capability.getDecayStats().onUpdate(player);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUseItem(LivingEntityUseItemEvent.Finish event) {
		if (event.getItem() != null && event.getItem().getItem() instanceof IDecayFood && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
				IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
				IDecayFood food = (IDecayFood) event.getItem().getItem();
				capability.getDecayStats().addStats(-food.getDecayHealAmount(event.getItem()), food.getDecayHealSaturation(event.getItem()));
			}
		}
	}

	@SubscribeEvent
	public static void onStartUsingItem(LivingEntityUseItemEvent.Start event) {
		if(event.getItem() != null && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			boolean isDecayFood = event.getItem().getItem() instanceof IDecayFood;
			if(isDecayFood) {
				boolean canEatFood = player.getFoodStats().needFood() && event.getItem().getItem() instanceof ItemFood && ((ItemFood)event.getItem().getItem()).getHealAmount(event.getItem()) > 0;
				boolean canEatDecayFood = false;
				if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
					IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
					canEatDecayFood = capability.getDecayStats().getDecayLevel() > 0;
				}
				if (!canEatFood && !canEatDecayFood) {
					event.setDuration(-1);
					event.setCanceled(true);
				}
			}
		}
	}
}
