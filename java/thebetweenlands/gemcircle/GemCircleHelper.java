package thebetweenlands.gemcircle;

import java.util.Random;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.network.packet.server.PacketGemProc;

public class GemCircleHelper {
	public static final float MAX_GEM_DAMAGE_VARIATION = 8.0F;
	public static final float GEM_PROC_CHANCE = 0.1F;

	/**
	 * Handles an attack and returns the new damage
	 * @param source
	 * @param attackedEntity
	 * @param damage
	 * @return
	 */
	public static float handleAttack(DamageSource source, EntityLivingBase attackedEntity, float damage) {
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
				if(attackedEntity instanceof EntityLivingBase) {
					ItemStack[] equipment = ((EntityLivingBase)attackedEntity).getLastActiveItems();
					for(int i = 0; i < equipment.length; i++) {
						ItemStack equipmentStack = equipment[i];
						if(equipmentStack != null && !equipmentStack.equals(((EntityLivingBase)attackedEntity).getHeldItem()) && equipmentStack.getItem() instanceof ItemArmor) {
							CircleGem armorGem = GemCircleHelper.getGem(equipmentStack);
							gemRelation += attackerGem.getRelation(armorGem);
							gemRelation += attackerItemGem.getRelation(armorGem);
							gemRelation += userGem.getRelation(armorGem);
						}
					}
				}
				float gemDamageVariation = Math.min(((gemRelation != 0 ? Math.signum(gemRelation) * 1 : 0) + gemRelation) / 6.0F * MAX_GEM_DAMAGE_VARIATION, MAX_GEM_DAMAGE_VARIATION);
				if(gemDamageVariation != 0.0F) {
					damage = Math.max(damage + gemDamageVariation, 1.0F);
				}

				boolean attackerProc = attacker.worldObj.rand.nextFloat() <= (user == attacker && !attacker.onGround && attacker.motionY < 0 ? GEM_PROC_CHANCE * 1.33F : GEM_PROC_CHANCE);
				boolean defenderProc = attacker.worldObj.rand.nextFloat() <= GEM_PROC_CHANCE;

				boolean attackerProcd = false;
				boolean defenderProcd = false;

				//Attacker gems
				TObjectIntHashMap<CircleGem> attackerGemCounts = new TObjectIntHashMap<CircleGem>();
				attackerGemCounts.adjustOrPutValue(attackerGem, 1, 1);
				attackerGemCounts.adjustOrPutValue(attackerItemGem, 1, 1);
				for(CircleGem gem : attackerGemCounts.keySet()) {
					attackerProcd |= applyProc(gem, user, user, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(attackerGemCounts.get(gem), damage));
				}

				//Defender gems
				TObjectIntHashMap<CircleGem> defenderGemCounts = new TObjectIntHashMap<CircleGem>();
				if(attackedEntity instanceof EntityLivingBase) {
					ItemStack[] equipment = ((EntityLivingBase)attackedEntity).getLastActiveItems();
					for(int i = 0; i < equipment.length; i++) {
						ItemStack equipmentStack = equipment[i];
						if(equipmentStack != null && !equipmentStack.equals(((EntityLivingBase)attackedEntity).getHeldItem()) && equipmentStack.getItem() instanceof ItemArmor) {
							CircleGem armorGem = GemCircleHelper.getGem(equipmentStack);
							if(armorGem != CircleGem.NONE) {
								defenderGemCounts.adjustOrPutValue(armorGem, 1, 1);
							}
						}
					}
				}
				defenderGemCounts.adjustOrPutValue(attackedGem, 1, 1);
				defenderGemCounts.adjustOrPutValue(attackedBlockingItemGem, 1, 1);
				for(CircleGem gem : defenderGemCounts.keySet()) {
					defenderProcd |= applyProc(gem, attackedEntity, user, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(defenderGemCounts.get(gem), damage));
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
		return damage;
	}

	private static float getMultipleProcStrength(int procs, float strength) {
		float ret = 0;
		for(int i = 0; i < procs; i++) {
			ret += strength / Math.pow(1.4F, i);
		}
		return ret;
	}

	private static boolean applyProc(CircleGem gem, Entity owner, Entity attacker, Entity defender, boolean attackerProc, boolean defenderProc, float strength) {
		boolean isAttacker = owner == attacker;
		if((isAttacker && attackerProc) || (!isAttacker && defenderProc)) {
			return gem.applyProc(isAttacker, owner, attacker, defender, strength);
		}
		return false;
	}

	/**
	 * Returns true if gems are applicable to the object
	 * @param obj
	 * @return
	 */
	public static boolean isApplicable(Object obj) {
		if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			Item item = stack.getItem();
			return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemBow;
		} else if(obj instanceof EntityLivingBase) {
			return true;
		}
		return false;
	}

	private static void setGem(NBTTagCompound root, CircleGem gem) {
		if(root != null) {
			root.setString("blCircleGem", gem == null ? "none" : gem.name);
		}
	}

	private static CircleGem getGem(NBTTagCompound root) {
		if(root != null) {
			return CircleGem.fromName(root.getString("blCircleGem"));
		}
		return CircleGem.NONE;
	}

	public static int getRelation(CircleGem gem1, CircleGem gem2) {
		return gem1.getRelation(gem2);
	}

	public static void setGem(ItemStack stack, CircleGem gem) {
		setGem(stack.stackTagCompound, gem);
	}

	public static CircleGem getGem(ItemStack stack) {
		return getGem(stack.stackTagCompound);
	}

	public static void setGem(Entity entity, CircleGem gem) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			property.setGem(gem);
		}
	}

	public static CircleGem getGem(Entity entity) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			return property.getGem();
		}
		return CircleGem.NONE;
	}
}
