package thebetweenlands.common.handler;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.GameruleRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class PlayerDecayHandler {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;

		if(!player.world.isRemote && event.phase == Phase.START) {
			if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
				IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);

				DecayStats stats = capability.getDecayStats();

				IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
				
				if(attr != null) {
					int currentMaxHealth = (int) attr.getBaseValue();

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
						attr.setBaseValue(currentMaxHealth + healthDiff);
						if(player.getHealth() > attr.getAttributeValue()) {
							player.setHealth((float)attr.getAttributeValue());
						}

						//Keep track of how much was removed
						capability.setRemovedHealth(capability.getRemovedHealth() - healthDiff);
					}
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

						float decayBaseSpeed = getDecayBaseSpeed(difficulty);

						float decaySpeed = 0;

						if(player.distanceWalkedModified - player.prevDistanceWalkedModified > 0) {
							decaySpeed += (player.distanceWalkedModified - player.prevDistanceWalkedModified) * 4 * decayBaseSpeed;
						}

						BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(player.world);
						if(storage.getEnvironmentEventRegistry().heavyRain.isActive() && player.world.canSeeSky(player.getPosition())) {
							decaySpeed += decayBaseSpeed;
						}

						if(player.isInWater()) {
							decaySpeed += decayBaseSpeed * 2.75F;
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
	public static void onEntityAttacked(LivingHurtEvent event) {
		if(!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
				IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
				float decayBaseSpeed = getDecayBaseSpeed(player.world.getDifficulty());
				capability.getDecayStats().addDecayAcceleration(decayBaseSpeed * 60);
			}
		}
	}

	/**
	 * Returns the base decay speed per tick
	 * @param difficulty
	 * @return
	 */
	public static float getDecayBaseSpeed(EnumDifficulty difficulty) {
		switch(difficulty) {
		case PEACEFUL:
			return 0.0F;
		case EASY:
			return 0.0025F;
		default:
		case NORMAL:
			return 0.0033F;
		case HARD:
			return 0.005F;
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerRespawnEvent event) {
		//Workaround for client not receiving the new MAX_HEALTH attribute after a respawn
		EntityPlayer player = event.player;
		if(!player.world.isRemote && player instanceof EntityPlayerMP && player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null) {
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
