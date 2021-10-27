package thebetweenlands.common.handler;

import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorUpgrades;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;
import thebetweenlands.common.network.clientbound.MessageDamageReductionParticle;
import thebetweenlands.common.network.clientbound.MessagePowerRingParticles;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class AttackDamageHandler {
	public static final float DAMAGE_REDUCTION = 0.3F;

	@SubscribeEvent
	public static void onEntityKnockback(LivingKnockBackEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		Entity attacker = event.getAttacker();

		if(attackedEntity instanceof IEntityBL && attacker instanceof EntityLivingBase && ((EntityLivingBase) attacker).getActiveHand() != null) {
			EntityLivingBase entityLiving = (EntityLivingBase) attacker;
			ItemStack heldItem = entityLiving.getHeldItem(entityLiving.getActiveHand());

			if (!heldItem.isEmpty() && OverworldItemHandler.isToolWeakened(heldItem)) {
				event.setStrength(event.getStrength() * 0.3F);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onEntityAttack(LivingAttackEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		DamageSource source = event.getSource();

		//Handle circle gem for blocking
		//For BL shields this is handled in ItemBLShield#onAttackBlocked
		if(canBlockDamageSource(attackedEntity, source)) {
			CircleGemHelper.handleAttack(source, attackedEntity, event.getAmount());
		}
	}

	private static boolean canBlockDamageSource(EntityLivingBase entity, DamageSource source) {
		if(!source.isUnblockable() && entity.isActiveItemStackBlocking() && (source instanceof EntityDamageSource == false || source.getTrueSource() != null)) {
			Vec3d location = source.getDamageLocation();

			if(location != null) {
				Vec3d look = entity.getLook(1.0F);
				Vec3d diff = location.subtractReverse(new Vec3d(entity.posX, entity.posY, entity.posZ)).normalize();
				diff = new Vec3d(diff.x, 0.0D, diff.z);

				if(diff.dotProduct(look) < 0.0D) {
					return true;
				}
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float damage = event.getAmount();

		Entity entity = source.getTrueSource();
		if(attackedEntity instanceof IEntityBL && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveHand() != null) {
			//BL mobs overworld item resistance
			EntityLivingBase attacker = (EntityLivingBase) entity;
			ItemStack heldItem = attacker.getHeldItem(attacker.getActiveHand());

			if (heldItem.isEmpty() || OverworldItemHandler.isToolWeakened(heldItem)) {
				//Cap damage of overly OP weapons
				damage = Math.min(damage, 40.0F);
			}

			if (!heldItem.isEmpty()) {
				if (OverworldItemHandler.isToolWeakened(heldItem)) {
					damage = damage * DAMAGE_REDUCTION;

					if(!attackedEntity.world.isRemote) {
						Vec3d center = attackedEntity.getPositionVector().add(0, attackedEntity.height / 2.0F, 0);

						Vec3d hitOffset = null;

						Entity immediateAttacker = source.getImmediateSource();

						if(immediateAttacker == null || attacker == immediateAttacker) {
							RayTraceResult result = attackedEntity.getEntityBoundingBox().calculateIntercept(attacker.getPositionEyes(1), attacker.getPositionEyes(1).add(attacker.getLookVec().scale(10)));
							if(result != null) {
								hitOffset = result.hitVec.subtract(center);
							}
						}
						if(immediateAttacker != null && hitOffset == null) {
							hitOffset = immediateAttacker.getPositionVector().add(0, immediateAttacker.height / 2.0F, 0).subtract(center);
						}
						if(hitOffset != null) {
							Vec3d offsetDirXZ = new Vec3d(hitOffset.x, 0, hitOffset.z).normalize();
							Vec3d offset = offsetDirXZ.scale(attackedEntity.width).add(0, hitOffset.y + attackedEntity.height / 2.0F, 0);

							attackedEntity.world.playSound(null, attackedEntity.posX, attackedEntity.posY + 0.5D, attackedEntity.posZ, SoundRegistry.DAMAGE_REDUCTION, SoundCategory.PLAYERS, 0.7F, 0.75F + attackedEntity.world.rand.nextFloat() * 0.3F);

							TheBetweenlands.networkWrapper.sendToAllAround(new MessageDamageReductionParticle(attackedEntity, offset, offsetDirXZ.scale(attackedEntity.width + 0.2F).normalize()), new TargetPoint(attackedEntity.dimension, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 32.0D));
						}
					}
				}
			}
		}

		damage = CircleGemHelper.handleAttack(source, attackedEntity, damage);

		if(entity instanceof EntityLivingBase) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);
				int rings = 0;

				for(int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_POWER && stack.getItemDamage() < stack.getMaxDamage()) {
						rings++;
					}
				}

				if(rings > 0) {
					TheBetweenlands.networkWrapper.sendToAllAround(new MessagePowerRingParticles(attackedEntity), new TargetPoint(attackedEntity.dimension, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 32.0D));
				}

				damage *= 1.0F + 0.5F * rings;
			}
		}

		int thornsCount = 0;

		for (ItemStack armor : attackedEntity.getArmorInventoryList()) {
			if (armor.getItem() instanceof ItemAmphibiousArmor) {
				if (((ItemAmphibiousArmor) armor.getItem()).getUpgradeCount(armor, AmphibiousArmorUpgrades.THORNS) >= 1) {
					thornsCount++;
				}
			}
		}

		if(entity != null && thornsCount > 0 && EnchantmentThorns.shouldHit(thornsCount, entity.world.rand)) {
			// Perhaps also damage armor like normal thorns would do? though thats usually really annoying
			entity.attackEntityFrom(DamageSource.causeThornsDamage(attackedEntity), thornsCount + entity.world.rand.nextInt(4));
		}

		event.setAmount(damage);
	}

	@SideOnly(Side.CLIENT)
	public static void spawnDamageReductionParticle(Entity entity, Vec3d offset, Vec3d dir) {
		BLParticles.DAMAGE_REDUCTION.spawn(entity.world, 0, 0, 0, ParticleArgs.get().withScale(2F).withData(entity, offset, dir));
	}

	@SideOnly(Side.CLIENT)
	public static void spawnPowerRingParticles(Entity entityHit) {
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX + entityHit.width / 2.0D, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ, ParticleArgs.get().withMotion(0.08D, 0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ + entityHit.width / 2.0D, ParticleArgs.get().withMotion(0, 0.05D, 0.08D));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX - entityHit.width / 2.0D, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ, ParticleArgs.get().withMotion(-0.08D, 0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ - entityHit.width / 2.0D, ParticleArgs.get().withMotion(0, 0.05D, -0.08D));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX + entityHit.width / 2.0D, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ, ParticleArgs.get().withMotion(0.08D, -0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ + entityHit.width / 2.0D, ParticleArgs.get().withMotion(0, -0.05D, 0.08D));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX - entityHit.width / 2.0D, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ, ParticleArgs.get().withMotion(-0.08D, -0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.world, entityHit.posX, entityHit.posY + entityHit.height / 2.0D + 0.5D, entityHit.posZ - entityHit.width / 2.0D, ParticleArgs.get().withMotion(0, -0.05D, -0.08D));
	}
}
