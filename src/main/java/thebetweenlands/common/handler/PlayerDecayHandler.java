package thebetweenlands.common.handler;

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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.api.capability.IDecayData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.network.clientbound.UpdateDecayDataPacket;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.MathUtils;

public class PlayerDecayHandler {
	public static final ResourceLocation DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID = TheBetweenlands.prefix("decay_health_modifier");

	static void tickDecay(PlayerTickEvent.Post event) {
		Player player = event.getEntity();

		if (!player.level().isClientSide()) {
			IDecayData decayData = player.getData(AttachmentRegistry.DECAY);
			applyDecayAttributeModifiers(player);

			if (decayData.isDecayEnabled(player)) {
				int decay = decayData.getDecayLevel(player);

				if (decay >= 16) {
					player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, true, false));
				} else if (decay >= 13) {
					player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, true, false));
				} else if (decay >= 10) {
					player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, true, false));
				}

				if (!player.isPassenger()) {
					Difficulty difficulty = player.level().getDifficulty();

					float decayBaseSpeed = isTargetSmelly(player) ? getDecayBaseSpeed(difficulty) * 1.5F : getDecayBaseSpeed(difficulty);

					float decaySpeed = 0;

					if (player.walkDist - player.walkDistO > 0) {
						decaySpeed += (player.walkDist - player.walkDistO) * 4 * decayBaseSpeed;
					}

					BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.get(player.level());
					if (storage != null && storage.getEnvironmentEventRegistry().isEventActive(EnvironmentEventRegistry.HEAVY_RAIN.getId()) && player.level().canSeeSky(player.blockPosition())) {
						decaySpeed += decayBaseSpeed;
					}

					if (player.isInWater()) {
						decaySpeed += decayBaseSpeed * 2.75F;
					}

					if (decaySpeed > 0.0F) {
						int armorDecayReduction = getArmorDecayReduction(player);

						if (armorDecayReduction > 0) {
							decaySpeed -= decaySpeed * (armorDecayReduction / 4f);
						}

						decayData.addDecayAcceleration(player, decaySpeed);
					}
				}

				decayData.tick(player);
			} else {
				decayData.setDecayLevel(player, 0);
				decayData.setDecaySaturationLevel(player, 1);
			}
		}
	}

	static void accelerateDecayOnDamage(LivingDamageEvent.Pre event) {
		LivingEntity entity = event.getEntity();
		if (!entity.level().isClientSide() && entity instanceof Player player) {

			IDecayData cap = player.getData(AttachmentRegistry.DECAY);
			float decayBaseSpeed = getDecayBaseSpeed(player.level().getDifficulty());
			cap.addDecayAcceleration(player, decayBaseSpeed * 60);
		}
	}

	public static void applyDecayAttributeModifiers(Player player) {
		IDecayData decayData = player.getData(AttachmentRegistry.DECAY);
		AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);

		if (attr != null) {
			if (BetweenlandsConfig.decayPercentual) {
				float decayMaxBaseHealthPercentage = decayData.getPlayerMaxHealthPenaltyPercentage(player, decayData.getDecayLevel(player));
				float prevDecayMaxBaseHealthPercentage = decayData.getPlayerMaxHealthPenaltyPercentage(player, decayData.getPrevDecayLevel());

				AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

				if (!MathUtils.epsilonEquals(decayMaxBaseHealthPercentage, prevDecayMaxBaseHealthPercentage) || (currentDecayModifier == null && decayMaxBaseHealthPercentage < 1)) {
					attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

					if (decayMaxBaseHealthPercentage < 1) {
						attr.addPermanentModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, -decayMaxBaseHealthPercentage, Operation.ADD_MULTIPLIED_TOTAL));
					}
				}
			} else {
				int currentMaxHealth = (int) attr.getValue();
				int decayHealthPenalty = (int) (decayData.getPlayerMaxHealthPenalty(player, decayData.getDecayLevel(player)) / 2.0F) * 2;
				int prevDecayHealthPenalty = (int) (decayData.getPlayerMaxHealthPenalty(player, decayData.getPrevDecayLevel()) / 2.0F) * 2;
				boolean decayHealthChange = (decayHealthPenalty - prevDecayHealthPenalty) != 0;
				AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);
				// Only change modifier if decay modifier is missing, decay health modifier value has changed or if player has less than 3 hearts (in which case decay modifier should be reduced or removed)
				if ((currentMaxHealth > BetweenlandsConfig.decayMinHealth && decayHealthPenalty != 0 && (currentDecayModifier == null || decayHealthPenalty != (int) currentDecayModifier.amount())) ||
					decayHealthChange ||
					(currentMaxHealth < BetweenlandsConfig.decayMinHealth && currentDecayModifier != null)) {
					attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);
					// Get current max health without the decay modifier
					currentMaxHealth = (int) attr.getValue();
					// TODO: there's potential for an issue here with an ADD_MULTIPLIED_TOTAL modifier from another mod bringing us below the minimum regardless
					// Don't go below min health
					int newHealth = (int) Math.max(currentMaxHealth - decayHealthPenalty, BetweenlandsConfig.decayMinHealth);
					int attributeHealth = -(currentMaxHealth - newHealth);
					if (attributeHealth < 0) {
						attr.addPermanentModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, attributeHealth, Operation.ADD_VALUE));
//						cap.setRemovedHealth(-attributeHealth);
					} else {
//						cap.setRemovedHealth(0);
					}
				}
			}
		}
	}

	/**
	 * Returns the base decay speed per tick
	 *
	 * @param difficulty
	 * @return
	 */
	public static float getDecayBaseSpeed(Difficulty difficulty) {
		return switch (difficulty) {
			case PEACEFUL -> 0.0F;
			case EASY -> 0.0025F;
			default -> 0.0033F;
			case HARD -> 0.005F;
		};
	}

	static void syncDecayOnJoin(PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if(!player.level().isClientSide() && player instanceof ServerPlayer) {
			IDecayData decayData = player.getData(AttachmentRegistry.DECAY);
			((ServerPlayer)player).connection.send(new UpdateDecayDataPacket(decayData.getDecayLevel(player), decayData.getPrevDecayLevel(), decayData.getSaturationLevel(), decayData.getAccelerationLevel()));
		}
	}
	
	static void syncDecayOnChangeDimension(PlayerChangedDimensionEvent event) {
		Player player = event.getEntity();
		if(!player.level().isClientSide() && player instanceof ServerPlayer) {
			IDecayData decayData = player.getData(AttachmentRegistry.DECAY);
			((ServerPlayer)player).connection.send(new UpdateDecayDataPacket(decayData.getDecayLevel(player), decayData.getPrevDecayLevel(), decayData.getSaturationLevel(), decayData.getAccelerationLevel()));
		}
	}
	
	// TODO OverworldItemHandler required for item use methods

	private static boolean isTargetSmelly(LivingEntity entity) {
		if (entity.hasData(AttachmentRegistry.ROT_SMELL))
			return entity.getData(AttachmentRegistry.ROT_SMELL).isSmellingBad(entity);
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
