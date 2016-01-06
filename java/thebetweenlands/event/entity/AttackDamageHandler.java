package thebetweenlands.event.entity;

import java.util.HashMap;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.GemCircleHelper;
import thebetweenlands.items.tools.ItemAxeBL;
import thebetweenlands.items.tools.ItemPickaxeBL;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.items.tools.ItemSwordBL;

public class AttackDamageHandler {
	public static final AttackDamageHandler INSTANCE = new AttackDamageHandler();

	private boolean ignoreEvent = false;

	public static final float DAMAGE_REDUCTION = 0.3F;

	public static final float MAX_GEM_DAMAGE_VARIATION = 6.0F;
	public static final float GEM_PROC_CHANCE = 0.1F;

	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		if(this.ignoreEvent) return;
		EntityLivingBase attackedEntity = event.entityLiving;
		DamageSource source = event.source;
		float damage = event.ammount;
		boolean damageChanged = false;

		//BL mobs overworld item resistance
		if(attackedEntity instanceof IEntityBL) {
			if (source.getSourceOfDamage() instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
				ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
				if (heldItem != null) {
					boolean isUsingBLWeapon = heldItem.getItem() instanceof ItemSwordBL || heldItem.getItem() instanceof ItemAxeBL || heldItem.getItem() instanceof ItemPickaxeBL || heldItem.getItem() instanceof ItemSpadeBL;
					if (!isUsingBLWeapon) {
						damage = damage * DAMAGE_REDUCTION;
						damageChanged = true;
					}
				}
			}
		}

		//Gem Circle
		if(attackedEntity.hurtTime == 0 && source instanceof EntityDamageSource) {
			Entity attacker = null;
			if(source instanceof EntityDamageSourceIndirect) {
				attacker = ((EntityDamageSourceIndirect)source).getSourceOfDamage();
			} else {
				attacker = ((EntityDamageSource)source).getEntity();
			}
			if(attacker != null) {
				CircleGem attackerGem = GemCircleHelper.getGem(attacker);
				CircleGem attackerItemGem = CircleGem.NONE;
				if(attacker instanceof EntityLivingBase) {
					ItemStack heldItem = ((EntityLivingBase)attacker).getHeldItem();
					if(heldItem != null) attackerItemGem = GemCircleHelper.getGem(heldItem);
				}
				CircleGem attackedGem = GemCircleHelper.getGem(attackedEntity);
				CircleGem attackedBlockingItemGem = CircleGem.NONE;
				if(attacker instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) attacker;
					ItemStack heldItem = player.getHeldItem();
					if(heldItem != null && player.isBlocking()) {
						attackedBlockingItemGem = GemCircleHelper.getGem(heldItem);
					}
				}
				int gemRelation = 0;
				gemRelation += attackerGem.getRelation(attackedGem);
				gemRelation += attackerGem.getRelation(attackedBlockingItemGem);
				gemRelation += attackerItemGem.getRelation(attackedGem);
				gemRelation += attackerItemGem.getRelation(attackedBlockingItemGem);
				if(attackedEntity instanceof EntityPlayer) {
					InventoryPlayer inventory = ((EntityPlayer)attackedEntity).inventory;
					ItemStack[] armorInventory = inventory.armorInventory;
					for(int i = 0; i < armorInventory.length; i++) {
						ItemStack armorStack = armorInventory[i];
						if(armorStack != null) {
							CircleGem armorGem = GemCircleHelper.getGem(armorStack);
							gemRelation += attackerGem.getRelation(armorGem);
							gemRelation += attackerItemGem.getRelation(armorGem);
						}
					}
				}
				float gemDamageVariation = ((gemRelation != 0 ? Math.signum(gemRelation) * 2 : 0) + gemRelation) / 15.0F * MAX_GEM_DAMAGE_VARIATION;
				damage = Math.max(damage + gemDamageVariation, 1.0F);
				damageChanged = true;

				boolean attackerProc = attacker.worldObj.rand.nextFloat() <= GEM_PROC_CHANCE;
				boolean defenderProc = attacker.worldObj.rand.nextFloat() <= GEM_PROC_CHANCE;

				boolean procd = false;

				procd |= attackerGem.applyProc(attacker, attacker, attackedEntity, attackerProc, defenderProc, damage);
				procd |= attackerItemGem.applyProc(attacker, attacker, attackedEntity, attackerProc, defenderProc, damage);
				procd |= attackedGem.applyProc(attackedEntity, attacker, attackedEntity, attackerProc, defenderProc, damage);
				procd |= attackedBlockingItemGem.applyProc(attackedEntity, attacker, attackedEntity, attackerProc, defenderProc, damage);
				HashMap<CircleGem, Integer> gemCountMap = new HashMap<CircleGem, Integer>();
				if(attackedEntity instanceof EntityPlayer) {
					InventoryPlayer inventory = ((EntityPlayer)attackedEntity).inventory;
					ItemStack[] armorInventory = inventory.armorInventory;
					for(int i = 0; i < armorInventory.length; i++) {
						ItemStack armorStack = armorInventory[i];
						if(armorStack != null) {
							CircleGem armorGem = GemCircleHelper.getGem(armorStack);
							if(armorGem != CircleGem.NONE) {
								if(!gemCountMap.containsKey(armorGem)) {
									gemCountMap.put(armorGem, 1);
								} else {
									gemCountMap.put(armorGem, gemCountMap.get(armorGem) + 1);
								}
							}
						}
					}
				}
				for(Entry<CircleGem, Integer> gemCount : gemCountMap.entrySet()) {
					float strength = 0;
					for(int i = 0; i < gemCount.getValue(); i++) {
						strength += damage / Math.pow(2.0F, i);
					}
					procd |= gemCount.getKey().applyProc(attackedEntity, attacker, attackedEntity, attackerProc, defenderProc, strength);
				}

				if(procd) {
					attackedEntity.worldObj.playSoundAtEntity(attackedEntity, "random.successful_hit", 1, 1);
					Entity damageSourceEntity = source.getEntity();
					damageSourceEntity.worldObj.playSoundAtEntity(damageSourceEntity, "random.successful_hit", 1, 1);
				}
			}
		}

		if(damageChanged) {
			this.ignoreEvent = true;
			attackedEntity.attackEntityFrom(source, MathHelper.ceiling_float_int(damage));
			this.ignoreEvent = false;
			event.setCanceled(true);
		}
	}
}
