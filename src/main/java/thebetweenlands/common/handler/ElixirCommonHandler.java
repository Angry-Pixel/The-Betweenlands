package thebetweenlands.common.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.effects.ElixirMasking;

import java.util.ArrayList;
import java.util.List;

public class ElixirCommonHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectArrowStrength);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectBreakSpeed);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectJumpingPower);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::changeItemUsageTime);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::changeMaskingTarget);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::tickEffects);
	}

	private static void changeMaskingTarget(LivingChangeTargetEvent event) {
		if (event.getNewAboutToBeSetTarget() != null && !((ElixirMasking)ElixirEffectRegistry.EFFECT_MASKING.get()).canEntityBeSeenBy(event.getNewAboutToBeSetTarget(), event.getEntity())) {
			event.setNewAboutToBeSetTarget(null);
		}
	}

	private static void affectBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (ElixirEffectRegistry.EFFECT_SWIFTARM.get().isActive(player) && ElixirEffectRegistry.EFFECT_SWIFTARM.get().getStrength(player) >= 0) {
			event.setNewSpeed(event.getNewSpeed() * (1.0F + (ElixirEffectRegistry.EFFECT_SWIFTARM.get().getStrength(player) + 1) * 0.3F));
		}
		if (ElixirEffectRegistry.EFFECT_SLUGARM.get().isActive(player) && ElixirEffectRegistry.EFFECT_SLUGARM.get().getStrength(player) >= 0) {
			event.setNewSpeed(event.getNewSpeed() / (1.0F + (ElixirEffectRegistry.EFFECT_SLUGARM.get().getStrength(player) + 1) * 0.3F));
		}
	}

	private static void changeItemUsageTime(LivingEntityUseItemEvent.Start event) {
		LivingEntity entity = event.getEntity();
		if (ElixirEffectRegistry.EFFECT_SWIFTARM.get().isActive(entity) && ElixirEffectRegistry.EFFECT_SWIFTARM.get().getStrength(entity) >= 0) {
			float newDuration = event.getDuration();
			newDuration *= 1.0F - 0.5F / 4.0F * (ElixirEffectRegistry.EFFECT_SWIFTARM.get().getStrength(entity) + 1);
			event.setDuration(Mth.ceil(newDuration));
		}
		if (ElixirEffectRegistry.EFFECT_SLUGARM.get().isActive(entity) && ElixirEffectRegistry.EFFECT_SLUGARM.get().getStrength(entity) >= 0) {
			if (!event.getItem().isEmpty() && !(event.getItem().getItem() instanceof BowItem)) {
				float newDuration = event.getDuration();
				newDuration /= 1.0F - 0.5F / 4.0F * (ElixirEffectRegistry.EFFECT_SLUGARM.get().getStrength(entity) + 1);
				event.setDuration(Mth.ceil(newDuration));
			}
		}
	}

	private static void affectArrowStrength(ArrowLooseEvent event) {
		if (ElixirEffectRegistry.EFFECT_WEAKBOW.get().isActive(event.getEntity())) {
			event.setCharge(Math.min(event.getCharge(), 10));
			event.setCharge((int) (event.getCharge() * (1.0F - (ElixirEffectRegistry.EFFECT_WEAKBOW.get().getStrength(event.getEntity()) + 1) / 4.0F * 0.75F)));
		}
	}

	private static final AttributeModifier FOLLOW_RANGE_MODIFIER = new AttributeModifier(TheBetweenlands.prefix("stenching_follow_range"), 10, AttributeModifier.Operation.ADD_VALUE);

	private static void tickEffects(EntityTickEvent.Pre event) {
		if (event.getEntity() instanceof LivingEntity entity) {
			if (ElixirEffectRegistry.EFFECT_SPIDERBREED.get().isActive(entity)) {
				int strength = ElixirEffectRegistry.EFFECT_SPIDERBREED.get().getStrength(entity);
				float relStrength = Math.min((strength + 1) / 4.0F, 1.0F);
				Vec3 lookVec = entity.getLookAngle().normalize();
				if (entity.zza < 0.0F) {
					lookVec = new Vec3(lookVec.x(), lookVec.y() * -1, lookVec.z());
				}
				if ((!entity.onGround() || isEntityOnWall(entity)) && (entity.horizontalCollision || entity.verticalCollision)) {
					if (entity instanceof Player) {
						entity.setDeltaMovement(entity.getDeltaMovement().x(), lookVec.y * 0.22F * relStrength, entity.getDeltaMovement().z());
					} else {
						entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.22F * relStrength, entity.getDeltaMovement().z());
					}
				}
				if (!entity.onGround() && isEntityOnWall(entity)) {
					if (entity.getDeltaMovement().y() < 0.0F && (lookVec.y > 0.0F || (entity.zza == 0.0F && entity.xxa == 0.0F))) {
						entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0F, 0.9F - relStrength * 0.5F, 1.0F));
					}
					if (entity.isShiftKeyDown()) {
						entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0F, 0.15F * (1.0F - relStrength), 1.0F));
					}
					entity.setDeltaMovement(entity.getDeltaMovement().multiply(relStrength, 1.0F, relStrength));
					entity.fallDistance = 0.0F;
				}
			}

			if (ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.get().isActive(entity) && !entity.isInWater() && !entity.isShiftKeyDown()) {
				BlockState state = entity.level().getBlockState(BlockPos.containing(entity.getX(), entity.getBoundingBox().minY + Math.min(-0.1D, entity.getDeltaMovement().y()), entity.getZ()));
				if (state.liquid()) {
					float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.get().getStrength(entity)) / 4.0F, 1.0F);
					entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.1F + relStrength * 0.9F, 1.0F, 0.1F + relStrength * 0.9F));
					if (entity.getDeltaMovement().y() < 0.0D) entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.0D, entity.getDeltaMovement().z());
					entity.setOnGround(true);
					entity.fallDistance = 0.0F;
				}
			}

			if (ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.get().isActive(entity) && entity.isInWater()) {
				if (entity.getDeltaMovement().y() > -0.1F) {
					entity.setDeltaMovement(entity.getDeltaMovement().subtract(0.0D, 0.005F + 0.035F / 5.0F * (1 + ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.get().getStrength(entity)), 0.0D));
				}
			}

			if (!entity.level().isClientSide()) {
				if (ElixirEffectRegistry.EFFECT_CATSEYES.get().isActive(entity)) {
					entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, ElixirEffectRegistry.EFFECT_CATSEYES.get().getDuration(entity), ElixirEffectRegistry.EFFECT_CATSEYES.get().getStrength(entity)));
					ElixirEffectRegistry.EFFECT_CATSEYES.get().removeElixir(entity);
				}

				if (ElixirEffectRegistry.EFFECT_POISONSTING.get().isActive(entity)) {
					entity.addEffect(new MobEffectInstance(MobEffects.POISON, ElixirEffectRegistry.EFFECT_POISONSTING.get().getDuration(entity), ElixirEffectRegistry.EFFECT_POISONSTING.get().getStrength(entity)));
					ElixirEffectRegistry.EFFECT_POISONSTING.get().removeElixir(entity);
				}

				if (ElixirEffectRegistry.EFFECT_DRUNKARD.get().isActive(entity)) {
					entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, ElixirEffectRegistry.EFFECT_DRUNKARD.get().getDuration(entity), ElixirEffectRegistry.EFFECT_DRUNKARD.get().getStrength(entity)));
					ElixirEffectRegistry.EFFECT_DRUNKARD.get().removeElixir(entity);
				}

				if (ElixirEffectRegistry.EFFECT_BLINDMAN.get().isActive(entity)) {
					entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ElixirEffectRegistry.EFFECT_BLINDMAN.get().getDuration(entity), ElixirEffectRegistry.EFFECT_BLINDMAN.get().getStrength(entity)));
					ElixirEffectRegistry.EFFECT_BLINDMAN.get().removeElixir(entity);
				}

				//Stenching
				if (entity instanceof Monster monster && entity.tickCount % 20 == 0) {
					AttributeInstance followRangeAttrib = entity.getAttribute(Attributes.FOLLOW_RANGE);
					if (followRangeAttrib != null) {
						List<Player> stenchingPlayers = getStenchingPlayersInRange(entity);
						if (stenchingPlayers.isEmpty()) {
							if (followRangeAttrib.hasModifier(FOLLOW_RANGE_MODIFIER.id())) {
								followRangeAttrib.removeModifier(FOLLOW_RANGE_MODIFIER);
							}
						} else {
							AttributeModifier currentModifier = followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.id());
							if (monster.getTarget() == null || entity.getLastHurtByMob() == null) {
								Player closestPlayer = null;
								for (Player player : stenchingPlayers) {
									if (closestPlayer == null || player.distanceToSqr(entity) < closestPlayer.distanceToSqr(entity))
										closestPlayer = player;
								}
								int strength = ElixirEffectRegistry.EFFECT_STENCHING.get().getStrength(closestPlayer);
								AttributeModifier stenchModifier = getFollowRangeModifier(strength);
								boolean shouldApplyModifier = currentModifier == null || currentModifier.amount() < stenchModifier.amount();
								if (shouldApplyModifier) {
									followRangeAttrib.addOrReplacePermanentModifier(getFollowRangeModifier(strength));
								}
								monster.setTarget(closestPlayer);
								entity.setLastHurtByMob(closestPlayer);
							}
						}
					}
				}
			}

			if (entity.hasEffect(ElixirEffectRegistry.ROOT_BOUND)) {
				entity.resetFallDistance();
				entity.setDeltaMovement(0.0D, 0.05D, 0.0D);
			}

			if (ElixirEffectRegistry.EFFECT_BASILISK.get().isActive(entity) || ElixirEffectRegistry.EFFECT_PETRIFY.get().isActive(entity)) {
				entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
			}
		}
	}

	private static AttributeModifier getFollowRangeModifier(int strength) {
		return new AttributeModifier(FOLLOW_RANGE_MODIFIER.id(), FOLLOW_RANGE_MODIFIER.amount() / 4.0D * (Math.min(strength, 4)), FOLLOW_RANGE_MODIFIER.operation());
	}

	private static List<Player> getStenchingPlayersInRange(LivingEntity entity) {
		List<Player> playerList = new ArrayList<>();
		for (Player player : entity.level().players()) {
			if (ElixirEffectRegistry.EFFECT_STENCHING.get().isActive(player)) {
				int strength = ElixirEffectRegistry.EFFECT_STENCHING.get().getStrength(player);
				double spottingRange = getSpottingRange(entity, strength);
				if (player.distanceToSqr(entity) <= spottingRange * spottingRange) {
					playerList.add(player);
				}
			}
		}
		return playerList;
	}

	private static double getSpottingRange(LivingEntity entity, int strength) {
		AttributeInstance followRangeAttrib = entity.getAttribute(Attributes.FOLLOW_RANGE);
		AttributeModifier rangeMod = followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.id());
		if (rangeMod != null) {
			followRangeAttrib.removeModifier(rangeMod);
		}
		AttributeModifier tempRangeMod = getFollowRangeModifier(strength);
		followRangeAttrib.addOrReplacePermanentModifier(tempRangeMod);
		double spottingRange = followRangeAttrib == null ? (16.0D + FOLLOW_RANGE_MODIFIER.amount() / 4.0D * Math.min(strength, 4)) : followRangeAttrib.getValue();
		followRangeAttrib.removeModifier(tempRangeMod);
		if (rangeMod != null) {
			followRangeAttrib.addOrReplacePermanentModifier(rangeMod);
		}
		return spottingRange;
	}

	private static boolean isEntityOnWall(LivingEntity entity) {
		AABB bb = entity.getBoundingBox().inflate(0.05D, 0.05D, 0.05D);
		int mX = Mth.floor(bb.minX);
		int mY = Mth.floor(bb.minY + 0.06D);
		int mZ = Mth.floor(bb.minZ);
		for (int y2 = mY; y2 < bb.maxY - 0.06D; y2++) {
			for (int x2 = mX; x2 < bb.maxX; x2++) {
				for (int z2 = mZ; z2 < bb.maxZ; z2++) {
					BlockPos pos = new BlockPos(x2, y2, z2);
					BlockState state = entity.level().getBlockState(pos);
					if (!state.getCollisionShape(entity.level(), pos).isEmpty()) {
						AABB boundingBox = state.getCollisionShape(entity.level(), pos).bounds();
						if (boundingBox.move(pos).intersects(bb))
							return true;
					}
				}
			}
		}
		return false;
	}

	private static void affectJumpingPower(LivingEvent.LivingJumpEvent event) {
		LivingEntity entity = event.getEntity();

		if (ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.get().isActive(entity)) {
			float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.get().getStrength(entity)) / 9.0F, 0.4F);
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0F, 1.0F + relStrength, 1.0F));
		}

		if (entity.hasEffect(ElixirEffectRegistry.ROOT_BOUND) || ElixirEffectRegistry.EFFECT_BASILISK.get().isActive(entity) || ElixirEffectRegistry.EFFECT_PETRIFY.get().isActive(entity)) {
			entity.setDeltaMovement(0.0D, entity.getDeltaMovement().y(), 0.0D);
			if (entity.getDeltaMovement().y() > -0.1D) {
				entity.setDeltaMovement(entity.getDeltaMovement().x(), -0.1D, entity.getDeltaMovement().z());
				entity.hurtMarked = true;
			}
		}
	}
}
