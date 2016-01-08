package thebetweenlands.event.entity;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.GemCircleHelper;
import thebetweenlands.items.tools.ItemAxeBL;
import thebetweenlands.items.tools.ItemPickaxeBL;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.items.tools.ItemSwordBL;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.server.PacketGemProc;

public class AttackDamageHandler {
	public static final AttackDamageHandler INSTANCE = new AttackDamageHandler();

	private boolean ignoreEvent = false;

	public static final float DAMAGE_REDUCTION = 0.3F;

	public static final float MAX_GEM_DAMAGE_VARIATION = 8.0F;
	public static final float GEM_PROC_CHANCE = 0.1F;

	@SubscribeEvent
	public void onEntityAttacked(LivingHurtEvent event) {
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
		if(attackedEntity.hurtTime == 0 && attackedEntity.deathTime == 0 && source instanceof EntityDamageSource && (attackedEntity instanceof EntityPlayer == false || !((EntityPlayer)attackedEntity).capabilities.disableDamage)) {
			Entity attacker = null;
			Entity user = null;
			if(source instanceof EntityDamageSourceIndirect) {
				attacker = ((EntityDamageSourceIndirect)source).getSourceOfDamage();
				user = ((EntityDamageSource)source).getEntity();
			} else {
				attacker = ((EntityDamageSource)source).getEntity();
				user = attacker;
			}
			if(attacker != null && user != null) {
				CircleGem attackerGem = GemCircleHelper.getGem(attacker);
				CircleGem userGem = CircleGem.NONE;
				if(user != attacker) {
					userGem = GemCircleHelper.getGem(user);
				}
				CircleGem attackerItemGem = CircleGem.NONE;
				if(attacker instanceof EntityLivingBase) {
					ItemStack heldItem = ((EntityLivingBase)attacker).getHeldItem();
					if(heldItem != null) attackerItemGem = GemCircleHelper.getGem(heldItem);
				}
				//At this point either userGem or attackerItemGem are set because either there's a user shooting a (non-living) projectile (user != attacker) or the user is attacking directly (user == attacker)
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
				gemRelation += userGem.getRelation(attackedGem);
				gemRelation += userGem.getRelation(attackedBlockingItemGem);
				if(attackedEntity instanceof EntityPlayer) {
					InventoryPlayer inventory = ((EntityPlayer)attackedEntity).inventory;
					ItemStack[] armorInventory = inventory.armorInventory;
					for(int i = 0; i < armorInventory.length; i++) {
						ItemStack armorStack = armorInventory[i];
						if(armorStack != null) {
							CircleGem armorGem = GemCircleHelper.getGem(armorStack);
							gemRelation += attackerGem.getRelation(armorGem);
							gemRelation += attackerItemGem.getRelation(armorGem);
							gemRelation += userGem.getRelation(armorGem);
						}
					}
				}
				float gemDamageVariation = Math.min(((gemRelation != 0 ? Math.signum(gemRelation) * 1 : 0) + gemRelation) / 6.0F * MAX_GEM_DAMAGE_VARIATION, MAX_GEM_DAMAGE_VARIATION);
				if(gemDamageVariation != 0.0F) {
					damage = Math.max(damage + gemDamageVariation, 1.0F);
					damageChanged = true;
				}

				boolean attackerProc = attacker.worldObj.rand.nextFloat() <= (user == attacker && !attacker.onGround && attacker.motionY < 0 ? GEM_PROC_CHANCE * 1.33F : GEM_PROC_CHANCE);
				boolean defenderProc = attacker.worldObj.rand.nextFloat() <= GEM_PROC_CHANCE;

				boolean attackerProcd = false;
				boolean defenderProcd = false;

				attackerProcd |= attackerGem.applyProc(user, user, attackedEntity, attackerProc, defenderProc, damage);
				attackerProcd |= attackerItemGem.applyProc(user, user, attackedEntity, attackerProc, defenderProc, damage);
				defenderProcd |= attackedGem.applyProc(attackedEntity, user, attackedEntity, attackerProc, defenderProc, damage);
				defenderProcd |= attackedBlockingItemGem.applyProc(attackedEntity, user, attackedEntity, attackerProc, defenderProc, damage);
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
						strength += damage / Math.pow(1.4F, i);
					}
					defenderProcd |= gemCount.getKey().applyProc(attackedEntity, user, attackedEntity, attackerProc, defenderProc, strength);
				}

				if(attackerProcd || defenderProcd) {
					Random rnd = user.worldObj.rand;
					World world = attackedEntity.worldObj;
					int dim = 0;
					if (world instanceof WorldServer) {
						dim = ((WorldServer)world).provider.dimensionId;
					}
					if(attackerProcd) {
						TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketGemProc((byte)0, attackedEntity.getEntityId())), new TargetPoint(dim, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 64D));
					}
					if(defenderProcd) {
						TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketGemProc((byte)1, attackedEntity.getEntityId())), new TargetPoint(dim, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 64D));
					}
					user.worldObj.playSoundAtEntity(user, "random.successful_hit", 1, 1);
					attackedEntity.worldObj.playSoundAtEntity(attackedEntity, "random.successful_hit", 1, 1);
				}
			}
		}

		event.ammount = damage;
	}

	@SubscribeEvent
	public void onEntityAttack(EntityJoinWorldEvent event) {
		if(event.entity instanceof EntityArrow) {
			EntityArrow entityArrow = (EntityArrow)event.entity;
			if(entityArrow.shootingEntity != null) {
				if(entityArrow.shootingEntity instanceof EntityLivingBase) {
					EntityLivingBase attacker = (EntityLivingBase)entityArrow.shootingEntity;
					if(attacker.getHeldItem() != null) {
						CircleGem gem = GemCircleHelper.getGem(attacker.getHeldItem());
						if(gem != CircleGem.NONE) {
							GemCircleHelper.setGem(entityArrow, gem);
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribePacket
	public static void onProcPacket(PacketGemProc packet) {
		byte type = packet.type;
		Entity entityHit = packet.getEntity(TheBetweenlands.proxy.getClientWorld());
		if(entityHit != null) {
			switch(type) {
			default:
				Random rnd = entityHit.worldObj.rand;
				for(int i = 0; i < 40; i++) {
					entityHit.worldObj.spawnParticle("magicCrit", 
							entityHit.posX + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width, 
							entityHit.boundingBox.minY + rnd.nextFloat() * entityHit.height, 
							entityHit.posZ + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width, 
							entityHit.motionX + rnd.nextFloat() * 0.4F - 0.2F, entityHit.motionY + rnd.nextFloat() * 0.4F - 0.2F, entityHit.motionZ + rnd.nextFloat() * 0.4F - 0.2F);
				}
			}
		}
	}
}
