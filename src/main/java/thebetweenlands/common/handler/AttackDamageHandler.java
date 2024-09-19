package thebetweenlands.common.handler;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AttackDamageHandler {

	public static final float DAMAGE_REDUCTION = 0.3F;

	static void weakenKnockback(LivingKnockBackEvent event) {
		LivingEntity attackedEntity = event.getEntity();
		LivingEntity attacker = attackedEntity.getLastHurtByMob();

		if(attackedEntity instanceof BLEntity && attacker != null) {
			ItemStack heldItem = attacker.getUseItem();

//			if (!heldItem.isEmpty() && OverworldItemHandler.isToolWeakened(heldItem)) {
//				event.setStrength(event.getStrength() * 0.3F);
//			}
		}
	}

	static void handleCircleGemDamageBlock(LivingIncomingDamageEvent event) {
		LivingEntity attackedEntity = event.getEntity();
		DamageSource source = event.getSource();

		//Handle circle gem for blocking
		//For BL shields this is handled in ItemBLShield#onAttackBlocked
		if(canBlockDamageSource(attackedEntity, source)) {
//			CircleGemHelper.handleAttack(source, attackedEntity, event.getAmount());
		}
	}

	private static boolean canBlockDamageSource(LivingEntity entity, DamageSource source) {
		if(!source.is(DamageTypeTags.BYPASSES_SHIELD) && entity.isBlocking() && source.getEntity() != null) {
			Vec3 location = source.getSourcePosition();

			if(location != null) {
				Vec3 look = entity.getViewVector(1.0F);
				Vec3 diff = location.vectorTo(entity.position()).normalize();
				diff = new Vec3(diff.x, 0.0D, diff.z);

				return diff.dot(look) < 0.0D;
			}
		}

		return false;
	}

	static void handleAttacks(LivingDamageEvent.Pre event) {
		LivingEntity attackedEntity = event.getEntity();
		DamageSource source = event.getSource();
		float damage = event.getOriginalDamage();

		Entity entity = source.getEntity();
//		if(attackedEntity instanceof BLEntity && entity instanceof LivingEntity living) {
//			//BL mobs overworld item resistance
//			ItemStack heldItem = living.getUseItem();
//
//			if (heldItem.isEmpty() || OverworldItemHandler.isToolWeakened(heldItem)) {
//				//Cap damage of overly OP weapons
//				damage = Math.min(damage, 40.0F);
//			}
//
//			if (!heldItem.isEmpty()) {
//				if (OverworldItemHandler.isToolWeakened(heldItem)) {
//					damage = damage * DAMAGE_REDUCTION;
//
//					if(!attackedEntity.level().isClientSide()) {
//						Vec3 center = attackedEntity.position().add(0, attackedEntity.getBbHeight() / 2.0F, 0);
//
//						Vec3 hitOffset = null;
//
//						Entity immediateAttacker = source.getDirectEntity();
//
//						if(immediateAttacker == null || entity == immediateAttacker) {
//							Optional<Vec3> result = attackedEntity.getBoundingBox().clip(entity.getEyePosition(1), entity.getEyePosition(1).add(entity.getLookAngle().scale(10)));
//							if(result.isPresent()) {
//								hitOffset = result.get().subtract(center);
//							}
//						}
//						if(immediateAttacker != null && hitOffset == null) {
//							hitOffset = immediateAttacker.position().add(0, immediateAttacker.getBbHeight() / 2.0F, 0).subtract(center);
//						}
//						if(hitOffset != null) {
//							Vec3 offsetDirXZ = new Vec3(hitOffset.x, 0, hitOffset.z).normalize();
//							Vec3 offset = offsetDirXZ.scale(attackedEntity.getBbWidth()).add(0, hitOffset.y + attackedEntity.getBbHeight() / 2.0F, 0);
//
//							attackedEntity.playSound(SoundRegistry.DAMAGE_REDUCTION.get(), 0.7F, 0.75F + attackedEntity.level().getRandom().nextFloat() * 0.3F);
//
//							PacketDistributor.sendToPlayersNear(attackedEntity.level(), null, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 32.0D, new DamageReductionParticlePacket(attackedEntity, offset, offsetDirXZ.scale(attackedEntity.getBbWidth() + 0.2F).normalize()));
//						}
//					}
//				}
//			}
//		}
//
//		damage = CircleGemHelper.handleAttack(source, attackedEntity, damage);
//
//		if(entity instanceof LivingEntity) {
//			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
//			if(cap != null) {
//				Inventory inv = cap.getInventory(EquipmentInventory.RING);
//				int rings = 0;
//
//				for(int i = 0; i < inv.getContainerSize(); i++) {
//					ItemStack stack = inv.getItem(i);
//					if(!stack.isEmpty() && stack.is(ItemRegistry.RING_OF_POWER) && stack.getDamageValue() < stack.getMaxDamage()) {
//						rings++;
//					}
//				}
//
//				if(rings > 0) {
//					PacketDistributor.sendToPlayersNear(attackedEntity.level(), null, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 32.0D, new PowerRingParticlePacket(attackedEntity));
//				}
//
//				damage *= 1.0F + 0.5F * rings;
//			}
//		}

		int thornsCount = 0;

		for (ItemStack armor : attackedEntity.getArmorAndBodyArmorSlots()) {
			if (armor.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
				if (armor.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES).getAllUniqueUpgradesWithCounts().containsKey(AmphibiousArmorUpgradeRegistry.THORNS)) {
					thornsCount++;
				}
			}
		}

		if(entity != null && thornsCount > 0 && shouldHit(thornsCount, entity.level().getRandom())) {
			// Perhaps also damage armor like normal thorns would do? though thats usually really annoying
			entity.hurt(entity.damageSources().thorns(attackedEntity), thornsCount + entity.level().getRandom().nextInt(4));
		}

		event.setNewDamage(damage);
	}

	private static boolean shouldHit(int amount, RandomSource random) {
		if (amount <= 0) {
			return false;
		} else {
			return random.nextFloat() < 0.15F * (float)amount;
		}
	}

	public static void spawnDamageReductionParticle(Entity entity, Vec3 offset, Vec3 dir) {
//		BLParticles.DAMAGE_REDUCTION.spawn(entity.level(), 0, 0, 0, ParticleFactory.ParticleArgs.get().withScale(2F).withData(entity, offset, dir));
	}

	public static void spawnPowerRingParticles(Entity entityHit) {
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX() + entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleFactory.ParticleArgs.get().withMotion(0.08D, 0.05D, 0));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() + entityHit.getBbWidth() / 2.0D, ParticleFactory.ParticleArgs.get().withMotion(0, 0.05D, 0.08D));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX() - entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleFactory.ParticleArgs.get().withMotion(-0.08D, 0.05D, 0));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() - entityHit.getBbWidth() / 2.0D, ParticleFactory.ParticleArgs.get().withMotion(0, 0.05D, -0.08D));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX() + entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleFactory.ParticleArgs.get().withMotion(0.08D, -0.05D, 0));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() + entityHit.getBbWidth() / 2.0D, ParticleFactory.ParticleArgs.get().withMotion(0, -0.05D, 0.08D));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX() - entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleFactory.ParticleArgs.get().withMotion(-0.08D, -0.05D, 0));
//		BLParticles.GREEN_FLAME.spawn(entityHit.level(), entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() - entityHit.getBbWidth() / 2.0D, ParticleFactory.ParticleArgs.get().withMotion(0, -0.05D, -0.08D));
	}
}
