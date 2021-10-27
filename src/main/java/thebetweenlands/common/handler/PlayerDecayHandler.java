package thebetweenlands.common.handler;

import java.util.UUID;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.config.properties.ItemDecayFoodProperty.DecayFoodStats;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorUpgrades;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.MathUtils;

public class PlayerDecayHandler {
	public static final UUID DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID = UUID.fromString("033f5f10-67b3-42f3-8511-67a575fbb099");

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;

		if(!player.world.isRemote && event.phase == Phase.START) {
			IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				DecayStats stats = cap.getDecayStats();

				IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

				if(attr != null) {
					if(BetweenlandsConfig.GENERAL.decayPercentual) {
						float decayMaxBaseHealthPercentage = cap.getMaxPlayerHealthPercentage(stats.getDecayLevel());   
						float prevDecayMaxBaseHealthPercentage = cap.getMaxPlayerHealthPercentage(stats.getPrevDecayLevel());

						AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

						if(!MathUtils.epsilonEquals(decayMaxBaseHealthPercentage, prevDecayMaxBaseHealthPercentage) || (currentDecayModifier == null && decayMaxBaseHealthPercentage < 1)) {
							attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

							if(decayMaxBaseHealthPercentage < 1) {
								attr.applyModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, "Decay health modifier", -1 + decayMaxBaseHealthPercentage, 2));
							}
						}
					} else {
						int currentMaxHealth = (int) attr.getAttributeValue();

						int decayMaxBaseHealth = (int)(cap.getMaxPlayerHealth(stats.getDecayLevel()) / 2.0F) * 2;   
						int prevDecayMaxBaseHealth = (int)(cap.getMaxPlayerHealth(stats.getPrevDecayLevel()) / 2.0F) * 2;

						boolean decayHealthChange = (decayMaxBaseHealth - prevDecayMaxBaseHealth) != 0;

						int decayHealthDiff = decayMaxBaseHealth - 20;

						AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

						//Only change modifier if deay modifier is missing, decay health modifier value has changed or if player has less than 3 hearts (in which case decay modifier should be reduced or removed)
						if((currentMaxHealth > BetweenlandsConfig.GENERAL.decayMinHealth && decayHealthDiff != 0 && (currentDecayModifier == null || decayHealthDiff != (int)currentDecayModifier.getAmount())) ||
								decayHealthChange ||
								(currentMaxHealth < BetweenlandsConfig.GENERAL.decayMinHealth && currentDecayModifier != null)) {
							attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

							//Get current max health without the decay modifier
							currentMaxHealth = (int) attr.getAttributeValue();

							//Don't go below 3 hearts
							int newHealth = (int) Math.max(currentMaxHealth + decayHealthDiff, BetweenlandsConfig.GENERAL.decayMinHealth);

							int attributeHealth = newHealth - currentMaxHealth;

							if(attributeHealth < 0) {
								attr.applyModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, "Decay health modifier", attributeHealth, 0));
								cap.setRemovedHealth(-attributeHealth);
							} else {
								cap.setRemovedHealth(0);
							}
						}
					}
				}

				if(cap.isDecayEnabled()) {
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

						float decayBaseSpeed = isTargetSmelly(player) ? getDecayBaseSpeed(difficulty) * 1.5F : getDecayBaseSpeed(difficulty); 

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
							int armorDecayReduction = getArmorDecayReduction(player);

							if(armorDecayReduction > 0) {
								decaySpeed -= decaySpeed * (armorDecayReduction / 4f);
							}

							stats.addDecayAcceleration(decaySpeed);
						}
					}
					
					stats.onUpdate(player);
				} else {
					stats.setDecayLevel(0);
					stats.setDecaySaturationLevel(1);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		if(!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				float decayBaseSpeed = getDecayBaseSpeed(player.world.getDifficulty());
				cap.getDecayStats().addDecayAcceleration(decayBaseSpeed * 60);
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

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
		//Check if item will be consumed this tick
		if(!event.getEntityLiving().getEntityWorld().isRemote && event.getDuration() <= 1) {
			if (!event.getItem().isEmpty() && event.getEntityLiving() instanceof EntityPlayer) {
				DecayFoodStats decayFoodStats = OverworldItemHandler.getDecayFoodStats(event.getItem());
				if(decayFoodStats != null) {
					EntityPlayer player = (EntityPlayer) event.getEntityLiving();
					IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
					if(cap != null) {
						cap.getDecayStats().addStats(-decayFoodStats.decay, decayFoodStats.saturation);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onStartUsingItem(LivingEntityUseItemEvent.Start event) {
		if(!event.getItem().isEmpty() && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			boolean isDecayFood = OverworldItemHandler.getDecayFoodStats(event.getItem()) != null;
			if(isDecayFood) {
				boolean canEatFood = player.getFoodStats().needFood() && event.getItem().getItem() instanceof ItemFood && ((ItemFood)event.getItem().getItem()).getHealAmount(event.getItem()) > 0;
				boolean canEatDecayFood = false;
				IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
				if(cap != null) {
					canEatDecayFood = cap.getDecayStats().getDecayLevel() > 0;
				}
				if (!canEatFood && !canEatDecayFood) {
					event.setDuration(-1);
					event.setCanceled(true);
				}
			}
		}
	}

	private static boolean isTargetSmelly(EntityLivingBase entity) {
		IRotSmellCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
		if(cap != null)
			if(cap.isSmellingBad())
				return true;
		return false;
	}

	private static int getArmorDecayReduction(EntityLivingBase entity) {
		int armorCount = 0;

		for (ItemStack armor : entity.getArmorInventoryList()) {
			if (armor.getItem() instanceof ItemAmphibiousArmor) {
				if (((ItemAmphibiousArmor) armor.getItem()).getUpgradeCount(armor, AmphibiousArmorUpgrades.DECAY_DECREASE) >= 1) {
					armorCount++;
				}
			}
		}

		return armorCount;
	}
}
