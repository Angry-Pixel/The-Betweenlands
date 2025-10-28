package thebetweenlands.common.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.MathUtils;

public class PlayerDecayHandler {
	public static final ResourceLocation DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID = TheBetweenlands.prefix("decay_health_modifier");

	public static void init() {
		NeoForge.EVENT_BUS.addListener(PlayerDecayHandler::accelerateDecayOnDamage);
		NeoForge.EVENT_BUS.addListener(PlayerDecayHandler::tickDecay);
	}

	private static void tickDecay(PlayerTickEvent.Post event) {
		Player player = event.getEntity();

		if (!player.level().isClientSide()) {
			DecayData decayData = player.getData(AttachmentRegistry.DECAY);
			applyDecayAttributeModifiers(player);

			if (DecayData.isDecayEnabled(player)) {
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

					BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.getNullable(player.level());
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

						decayData = decayData.addDecayAcceleration(decaySpeed);
					}
				}

				decayData = decayData.update(player);
				player.setData(AttachmentRegistry.DECAY, decayData);
			} else {
				player.setData(AttachmentRegistry.DECAY, new DecayData());
			}
		}
	}

	private static void accelerateDecayOnDamage(LivingDamageEvent.Pre event) {
		LivingEntity entity = event.getEntity();
		if (!entity.level().isClientSide() && entity instanceof Player player) {
			float decayBaseSpeed = getDecayBaseSpeed(player.level().getDifficulty());
			player.setData(AttachmentRegistry.DECAY, player.getData(AttachmentRegistry.DECAY).addDecayAcceleration(decayBaseSpeed * 60));
		}
	}

	public static void applyDecayAttributeModifiers(Player player) {
		DecayData decayData = player.getData(AttachmentRegistry.DECAY);
		AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);

		if (attr != null) {
			if (BetweenlandsConfig.decayPercentual) {
				float decayMaxBaseHealthPercentage = DecayData.getPlayerMaxHealthPenaltyPercentage(player, decayData.getDecayLevel(player));
				float prevDecayMaxBaseHealthPercentage = DecayData.getPlayerMaxHealthPenaltyPercentage(player, decayData.prevDecayLevel());

				AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

				if (!MathUtils.epsilonEquals(decayMaxBaseHealthPercentage, prevDecayMaxBaseHealthPercentage) || (currentDecayModifier == null && decayMaxBaseHealthPercentage < 1)) {
					attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

					if (decayMaxBaseHealthPercentage < 1) {
						attr.addPermanentModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, -decayMaxBaseHealthPercentage, Operation.ADD_MULTIPLIED_TOTAL));
					}
				}
			} else {
				int currentMaxHealth = (int) attr.getValue();
				int decayHealthPenalty = (int) (DecayData.getPlayerMaxHealthPenalty(player, decayData.getDecayLevel(player)) / 2.0F) * 2;
				int prevDecayHealthPenalty = (int) (DecayData.getPlayerMaxHealthPenalty(player, decayData.prevDecayLevel()) / 2.0F) * 2;
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
			case NORMAL -> 0.0033F;
			case HARD -> 0.005F;
		};
	}

	// TODO OverworldItemHandler required for item use methods

	private static boolean isTargetSmelly(LivingEntity entity) {
		if (entity.hasData(AttachmentRegistry.ROT_SMELL))
			return entity.getData(AttachmentRegistry.ROT_SMELL).isSmellingBad(entity);
		return false;
	}

	private static int getArmorDecayReduction(LivingEntity entity) {
		int armorCount = 0;

		for (ItemStack armor : entity.getArmorSlots()) {
			if (armor.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
				if (armor.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES).getAllUniqueUpgradesWithCounts().containsKey(AmphibiousArmorUpgradeRegistry.DECAY_DECREASE)) {
					armorCount++;
				}
			}
		}

		return armorCount;
	}
}
