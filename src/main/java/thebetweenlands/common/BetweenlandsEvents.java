package thebetweenlands.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;
import thebetweenlands.common.component.entity.BlessingData;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.*;

import java.util.Optional;

public class BetweenlandsEvents {

	private static void modifyBreakSpeedWithSimulacrum(PlayerEvent.BreakSpeed event) {
		Optional<BlockPos> pos = event.getPosition();
		Player player = event.getEntity();

		if (pos.isPresent()) {
			SimulacrumBlockEntity simulacrum = SimulacrumBlockEntity.getClosestActiveTile(SimulacrumBlockEntity.class, null, player.level(), pos.get().getX() + 0.5D, pos.get().getY() + 0.5D, pos.get().getZ() + 0.5D, 16.0D, SimulacrumEffectRegistry.WEAKNESS.get(), null);

			if (simulacrum != null) {
				double dst = simulacrum.getBlockPos().distToCenterSqr(pos.get().getX() + 0.5D, pos.get().getY() + 0.5D, pos.get().getZ() + 0.5D);

				float multiplier = (float) (0.075f + 0.925f * dst / 256.0D);

				event.setNewSpeed(event.getNewSpeed() * multiplier);
			}
		}
	}

	private static void handleBlessingDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();

		if (!entity.level().isClientSide() && entity instanceof Player player && player.hasData(AttachmentRegistry.BLESSING)) {
			BlessingData data = entity.getData(AttachmentRegistry.BLESSING);

			if (data.isBlessed()) {
				BlockPos location = data.getBlessingLocation();

				if (location != null && data.getBlessingDimension() == entity.level().dimension()) {
					event.setCanceled(true);

					entity.setHealth(entity.getMaxHealth() * 0.5f);

					int droppedExperience = player.totalExperience / 3;
					player.experienceProgress = 0;
					player.experienceLevel = 0;
					player.totalExperience = 0;
					while (droppedExperience > 0) {
						int xp = ExperienceOrb.getExperienceValue(droppedExperience);
						droppedExperience -= xp;
						ExperienceOrb xpOrb = new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), xp);
						player.level().addFreshEntity(xpOrb);
					}

//					if (entity.level().getRandom().nextBoolean()) {
//						BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos(entity.level(), location, 8, false, 4, 0);
//
//						if (spawnPoint != null) {
//							if (entity.distanceToSqr(Vec3.atCenterOf(spawnPoint)) > 24) {
//								playThunderSounds(entity.level(), entity.getX(), entity.getY(), entity.getZ());
//								entity.level().addFreshEntity(new BLLightningBolt(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 1, false, true));
//							}
//
//							PlayerUtil.teleport(entity, spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);
//
//							playThunderSounds(entity.level(), entity.getX(), entity.getY(), entity.getZ());
//							entity.level().addFreshEntity(new BLLightningBolt(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 1, false, true));
//
//							entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
//						} else if (entity instanceof ServerPlayer) {
//							player.displayClientMessage(Component.translatable("chat.simulacrum.obstructed"), true);
//						}
//					} else {
//						playThunderSounds(entity.level(), entity.getX(), entity.getY(), entity.getZ());
//						entity.level().addFreshEntity(new BLLightningBolt(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 1, false, true));
//					}

					if (player instanceof ServerPlayer sp) {
						AdvancementCriteriaRegistry.REVIVED_BLESSED.get().trigger(sp);
					}

					data.clearBlessed();
				}
			}
		}
	}

	protected static void playThunderSounds(Level level, double x, double y, double z) {
		level.playSound(null, x, y, z, SoundRegistry.RIFT_CREAK, SoundSource.PLAYERS, 2, 1);
		level.playSound(null, x, y, z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.75F, 0.75F);
	}

	private static void resurrectDeadEntities(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();

		if (!entity.level().isClientSide() && !(entity instanceof Player) && entity.level().getRandom().nextInt(4) == 0) {
			SimulacrumBlockEntity simulacrum = SimulacrumBlockEntity.getClosestActiveTile(SimulacrumBlockEntity.class, null, entity.level(), entity.getX(), entity.getY(), entity.getZ(), 16.0D, SimulacrumEffectRegistry.RESURRECTION.get(), null);

			if (simulacrum != null) {
//				entity.setDropItemsWhenDead(false);
//
//				CompoundTag tag = new CompoundTag();
//				entity.addAdditionalSaveData(tag);
//
//				if (!tag.isEmpty()) {
//					EntityResurrection resurrection = new EntityResurrection(entity.level(), tag, () -> !entity.isDeadOrDying() ? entity.position() : null, 60 + entity.level().getRandom().nextInt(60));
//					resurrection.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
//					entity.level().addFreshEntity(resurrection);
//				}
			}
		}
	}

	private static void addBlessingEffect(PlayerTickEvent.Pre event) {
		if(!event.getEntity().level().isClientSide()) {
			BlessingData data = event.getEntity().getData(AttachmentRegistry.BLESSING);

			if(data.isBlessed() && data.getBlessingLocation() != null && event.getEntity().level().dimension() == data.getBlessingDimension()) {
				event.getEntity().addEffect(ElixirEffectRegistry.EFFECT_BLESSED.get().createEffect(205, 0));
			}
		}
	}

	private static void protectFromMagicDamage(LivingIncomingDamageEvent event) {
		if(event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
			float damageMultiplier = 1.0F;

			LivingEntity entityHit = event.getEntity();

			ItemStack boots = entityHit.getItemBySlot(EquipmentSlot.FEET);
			ItemStack legs = entityHit.getItemBySlot(EquipmentSlot.LEGS);
			ItemStack chest = entityHit.getItemBySlot(EquipmentSlot.CHEST);
			ItemStack helm = entityHit.getItemBySlot(EquipmentSlot.HEAD);

			if (!boots.isEmpty() && boots.is(ItemRegistry.ANCIENT_BOOTS) && boots.getDamageValue() < boots.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!legs.isEmpty()  && legs.is(ItemRegistry.ANCIENT_LEGGINGS) && legs.getDamageValue() < legs.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!chest.isEmpty() && chest.is(ItemRegistry.ANCIENT_CHESTPLATE) && chest.getDamageValue() < chest.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!helm.isEmpty() && helm.is(ItemRegistry.ANCIENT_HELMET) && helm.getDamageValue() < helm.getMaxDamage())
				damageMultiplier -= 0.125F;

			event.setAmount(event.getAmount() * damageMultiplier);
		}
	}
}
