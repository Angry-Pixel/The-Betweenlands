package thebetweenlands.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
import thebetweenlands.common.item.tools.ItemBLAxe;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.network.clientbound.MessagePowerRingParticles;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class AttackDamageHandler {
	public static final float DAMAGE_REDUCTION = 0.3F;

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float damage = event.getAmount();

		if(attackedEntity instanceof IEntityBL) {
			if (source.getTrueSource() instanceof EntityPlayer) {
				//Cap damage of overly OP weapons
				damage = Math.min(damage, 40.0F);

				//BL mobs overworld item resistance
				EntityPlayer entityPlayer = (EntityPlayer) source.getTrueSource();
				ItemStack heldItem = entityPlayer.getHeldItem(entityPlayer.getActiveHand());
				if (heldItem != null) {
					boolean isWhitelisted = heldItem.getItem() instanceof ItemBLSword || heldItem.getItem() instanceof ItemBLAxe || heldItem.getItem() instanceof ItemBLPickaxe || heldItem.getItem() instanceof ItemBLShovel || OverworldItemHandler.WHITELIST.contains(heldItem.getItem());
					if (!isWhitelisted) {
						damage = damage * DAMAGE_REDUCTION;
					}
				}
			}
		}

		damage = CircleGemHelper.handleAttack(source, attackedEntity, damage);

		if(source.getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();

			if(attacker.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = attacker.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
				IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);
				int rings = 0;

				for(int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if(stack != null && stack.getItem() == ItemRegistry.RING_OF_POWER) {
						rings++;
					}
				}

				if(rings > 0) {
					TheBetweenlands.networkWrapper.sendToAllAround(new MessagePowerRingParticles(attackedEntity), new TargetPoint(attackedEntity.dimension, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 32.0D));
				}
				
				damage *= 1.0F + 0.5F * rings;
			}
		}

		event.setAmount(damage);
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
