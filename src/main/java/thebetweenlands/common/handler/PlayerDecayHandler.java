package thebetweenlands.common.handler;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.GameruleRegistry;

public class PlayerDecayHandler {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.START) {
			EntityPlayer player = event.player;

			if(!player.world.isRemote && player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
				IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);

				int currentMaxHealth = (int) player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();

				DecayStats stats = capability.getDecayStats();

				int decayMaxHealth = (int)(capability.getMaxPlayerHealth(stats.getDecayLevel()) / 2.0F) * 2;
				int prevDecayMaxHealth = (int)(capability.getMaxPlayerHealth(stats.getPrevDecayLevel()) / 2.0F) * 2;
				int healthDiff = decayMaxHealth - prevDecayMaxHealth;

				if(healthDiff != 0) {
					//Don't go below 3 hearts
					int newHealth = Math.max(currentMaxHealth + healthDiff, 6);

					healthDiff = newHealth - currentMaxHealth;

					//Don't give more health back than was removed
					healthDiff = Math.min(healthDiff, capability.getRemovedHealth());

					//Update health
					player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(currentMaxHealth + healthDiff);
					if(player.getHealth() > currentMaxHealth + healthDiff) {
						player.setHealth(currentMaxHealth + healthDiff);
					}

					//Keep track of how much was removed
					capability.setRemovedHealth(capability.getRemovedHealth() - healthDiff);
				}

				if(capability.isDecayEnabled()) {
					int decay = stats.getDecayLevel();
	
					if (decay >= 16) {
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 2, true, false));
						player.jumpMovementFactor = 0.001F;
					} else if (decay >= 13) {
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1, true, false));
						player.jumpMovementFactor = 0.002F;
					} else if (decay >= 10) {
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 0, true, false));
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
				}

				if(GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_DECAY)) {
					capability.getDecayStats().onUpdate(player);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerRespawnEvent event) {
		//Workaround for client not receiving the new MAX_HEALTH attribute after a respawn
		EntityPlayer player = event.player;
		if(!player.world.isRemote && player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).connection.sendPacket(new SPacketEntityProperties(player.getEntityId(), ImmutableList.of(player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH))));
		}
	}

	@SubscribeEvent
	public static void onUseItem(LivingEntityUseItemEvent.Finish event) {
		if (!event.getItem().isEmpty() && event.getItem().getItem() instanceof IDecayFood && event.getEntityLiving() instanceof EntityPlayer) {
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
		if(!event.getItem().isEmpty() && event.getEntityLiving() instanceof EntityPlayer) {
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
