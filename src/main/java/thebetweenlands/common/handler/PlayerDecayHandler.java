package thebetweenlands.common.handler;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.api.capability.IDecayData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.MathUtils;

@EventBusSubscriber(modid = TheBetweenlands.ID, bus = Bus.GAME)
public class PlayerDecayHandler {
	public static final ResourceLocation DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID = TheBetweenlands.prefix("decay_health_modifier");

	// TODO Maybe should be post
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();

		if(player != null && !player.level().isClientSide()) {
			IDecayData cap = player.getData(AttachmentRegistry.DECAY);
			if(cap != null) {

				AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);

				if(attr != null) {
					if(BetweenlandsConfig.decayPercentual) {
						float decayMaxBaseHealthPercentage = cap.getPlayerMaxHealthPenaltyPercentage(player, cap.getDecayLevel(player));   
						float prevDecayMaxBaseHealthPercentage = cap.getPlayerMaxHealthPenaltyPercentage(player, cap.getPrevDecayLevel());

						AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

						if(!MathUtils.epsilonEquals(decayMaxBaseHealthPercentage, prevDecayMaxBaseHealthPercentage) || (currentDecayModifier == null && decayMaxBaseHealthPercentage < 1)) {
							attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

							if(decayMaxBaseHealthPercentage < 1) {
								attr.addPermanentModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, -decayMaxBaseHealthPercentage,  Operation.ADD_MULTIPLIED_TOTAL));
							}
						}
					} else {
						int currentMaxHealth = (int) attr.getValue();

						int decayHealthPenalty = (int)(cap.getPlayerMaxHealthPenalty(player, cap.getDecayLevel(player)) / 2.0F) * 2;   
						int prevDecayHealthPenalty = (int)(cap.getPlayerMaxHealthPenalty(player, cap.getPrevDecayLevel()) / 2.0F) * 2;

						boolean decayHealthChange = (decayHealthPenalty - prevDecayHealthPenalty) != 0;

						AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

						// Only change modifier if decay modifier is missing, decay health modifier value has changed or if player has less than 3 hearts (in which case decay modifier should be reduced or removed)
						if((currentMaxHealth > BetweenlandsConfig.decayMinHealth && decayHealthPenalty != 0 && (currentDecayModifier == null || decayHealthPenalty != (int)currentDecayModifier.amount())) ||
								decayHealthChange ||
								(currentMaxHealth < BetweenlandsConfig.decayMinHealth && currentDecayModifier != null)) {
							attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

							// Get current max health without the decay modifier
							currentMaxHealth = (int) attr.getValue();

							// TODO: there's potential for an issue here with an ADD_MULTIPLIED_TOTAL modifier from another mod bringing us below the minimum regardless
							// Don't go below min health
							int newHealth = (int) Math.max(currentMaxHealth - decayHealthPenalty, BetweenlandsConfig.decayMinHealth);

							int attributeHealth = -(currentMaxHealth - newHealth);

							if(attributeHealth < 0) {
								attr.addPermanentModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, attributeHealth, Operation.ADD_VALUE));
//								cap.setRemovedHealth(-attributeHealth);
							} else {
//								cap.setRemovedHealth(0);
							}
						}
					}
				}

				if(cap.isDecayEnabled(player)) {
					int decay = cap.getDecayLevel(player);
					
					 // TODO find modern equiv. of jump factor
					if (decay >= 16) {
						player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, true, false));
//						player.jumpMovementFactor = 0.001F;
					} else if (decay >= 13) {
						player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, true, false));
//						player.jumpMovementFactor = 0.002F;
					} else if (decay >= 10) {
						player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, true, false));
					}
					
					if(!player.isPassenger()) {
						Difficulty difficulty = player.level().getDifficulty();

						float decayBaseSpeed = isTargetSmelly(player) ? getDecayBaseSpeed(difficulty) * 1.5F : getDecayBaseSpeed(difficulty); 

						float decaySpeed = 0;

						if(player.walkDist - player.walkDistO > 0) {
							decaySpeed += (player.walkDist - player.walkDistO) * 4 * decayBaseSpeed;
						}

						BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.get(player.level());
						if(storage != null && storage.getEnvironmentEventRegistry().isEventActive(EnvironmentEventRegistry.HEAVY_RAIN.getId()) && player.level().canSeeSky(player.blockPosition())) {
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

							cap.addDecayAcceleration(player, decaySpeed);
						}
					}
					
					cap.tick(player);
				} else {
					cap.setDecayLevel(player, 0);
					cap.setDecaySaturationLevel(player, 1);
				}
			}
		}
	}

	

	@SubscribeEvent
	public static void onEntityAttacked(LivingDamageEvent.Pre event) {
		LivingEntity entity = event.getEntity();
		if(entity != null && !entity.level().isClientSide && entity instanceof Player) {
			Player player = (Player) entity;

			IDecayData cap = player.getData(AttachmentRegistry.DECAY);
			if(cap != null) {
				float decayBaseSpeed = getDecayBaseSpeed(player.level().getDifficulty());
				cap.addDecayAcceleration(player, decayBaseSpeed * 60);
			}
		}
	}

	/**
	 * Returns the base decay speed per tick
	 * @param difficulty
	 * @return
	 */
	public static float getDecayBaseSpeed(Difficulty difficulty) {
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
	public static void syncMaxHealthOnPlayerRespawn(PlayerRespawnEvent event) {
		// TODO: May be unnecessary in 1.21+
		//Workaround for client not receiving the new MAX_HEALTH attribute after a respawn
		Player player = event.getEntity();
		if(!player.level().isClientSide() && player instanceof ServerPlayer && player.getAttribute(Attributes.MAX_HEALTH) != null) {
			((ServerPlayer)player).connection.send(new ClientboundUpdateAttributesPacket(player.getId(), ImmutableList.of(player.getAttribute(Attributes.MAX_HEALTH))));
		}
	}

	// TODO OverworldItemHandler required for item use methods
	

	private static boolean isTargetSmelly(LivingEntity entity) {
		if(entity.hasData(AttachmentRegistry.ROT_SMELL))
			if(entity.getData(AttachmentRegistry.ROT_SMELL).isSmellingBad(entity))
				return true;
		return false;
	}

	private static int getArmorDecayReduction(LivingEntity entity) {
		int armorCount = 0;

		// TODO After Amphibious Armour is done (also, maybe make this something an interface or data component can handle)
//		for (ItemStack armor : entity.getArmorSlots()) {
//			if (armor.getItem() instanceof ItemAmphibiousArmor) {
//				if (((ItemAmphibiousArmor) armor.getItem()).getUpgradeCount(armor, AmphibiousArmorUpgrades.DECAY_DECREASE) >= 1) {
//					armorCount++;
//				}
//			}
//		}

		return armorCount;
	}
}
