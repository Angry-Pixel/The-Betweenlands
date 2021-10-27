package thebetweenlands.common.capability.circlegem;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.api.capability.ICircleGemCapability;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.network.clientbound.MessageGemProc;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.NBTHelper;

import java.util.ArrayList;
import java.util.List;

public class CircleGemHelper {
	public static final String ITEM_GEM_NBT_TAG = "Gem";

	/**
	 * Returns true if gems are applicable to the item
	 * @param item
	 * @return
	 */
	public static boolean isApplicable(Item item) {
		return (item instanceof ItemAmulet || item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemBow || item instanceof ItemTool || item instanceof ItemShield) && item instanceof ItemAmphibiousArmor == false;
	}

	/**
	 * Returns true if gems are applicable to the entity
	 * @param entity
	 * @return
	 */
	public static boolean isApplicable(Entity entity) {
		return entity instanceof EntityLivingBase;
	}

	/**
	 * Sets the gem of the specified item stack
	 * @param stack
	 * @param gem
	 */
	public static void setGem(ItemStack stack, CircleGemType gem) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setInteger(ITEM_GEM_NBT_TAG, gem.id);
	}

	/**
	 * Returns the gem on the specified item stack
	 * @param stack
	 * @return
	 */
	public static CircleGemType getGem(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasKey(ITEM_GEM_NBT_TAG, Constants.NBT.TAG_INT)) {
			return CircleGemType.fromID(nbt.getInteger(ITEM_GEM_NBT_TAG));
		}
		return CircleGemType.NONE;
	}

	/**
	 * Adds a gem to the specified entity
	 * @param entity

	 */
	public static void addGem(Entity entity, CircleGemType gemType, CircleGem.CombatType combatType) {
		ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
		if(cap != null) {
			CircleGem gem = new CircleGem(gemType, combatType);
			if(cap.canAdd(gem)) {
				cap.addGem(gem);
			}
		}
	}

	/**
	 * Returns a list of gems on the specified entity
	 * @param entity
	 * @return
	 */
	public static List<CircleGem> getGems(Entity entity) {
		List<CircleGem> gems = new ArrayList<CircleGem>();
		ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
		if(cap != null) {
			return cap.getGems();
		}
		return gems;
	}

	/**
	 * Returns the gem of the slot on the specified entity
	 * @param entity
	 * @param slot
	 * @return
	 */
	public static CircleGem getGem(Entity entity, int slot) {
		ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
		if(cap != null) {
			List<CircleGem> gems = cap.getGems();
			if(gems.size() > slot)
				return cap.getGems().get(slot);
		}
		return new CircleGem(CircleGemType.NONE, CombatType.BOTH);
	}

	/**
	 * Adds the gem property overrides to the specified item
	 * @param item
	 */
	public static void addGemPropertyOverrides(Item item) {
		item.addPropertyOverride(new ResourceLocation("gem"), (stack, worldIn, entityIn) -> CircleGemHelper.getGem(stack).id);
	}

	public static final float MAX_GEM_DAMAGE_VARIATION = 8.0F;
	public static final float GEM_PROC_CHANCE = 0.15F;

	/**
	 * Handles an attack and returns the new damage
	 * @param damageSource
	 * @param attackedEntity
	 * @param damage
	 * @return
	 */
	public static float handleAttack(DamageSource damageSource, EntityLivingBase attackedEntity, float damage) {
		if(attackedEntity.hurtTime == 0 && attackedEntity.deathTime == 0 && damageSource instanceof EntityDamageSource && (attackedEntity instanceof EntityPlayer == false || !((EntityPlayer)attackedEntity).capabilities.disableDamage)) {
			Entity attacker;
			Entity source;
			if(damageSource instanceof EntityDamageSourceIndirect) {
				attacker = damageSource.getTrueSource();
				source = damageSource.getImmediateSource();
			} else {
				attacker = damageSource.getImmediateSource();
				source = attacker;
			}
			if(attacker != null && source != null) {
				List<CircleGem> attackerGems = CircleGemHelper.getGems(attacker);
				List<CircleGem> sourceGems = new ArrayList<CircleGem>();
				if(source != attacker) {
					sourceGems.addAll(CircleGemHelper.getGems(source));
				}
				CircleGemType attackerItemGem = CircleGemType.NONE;
				if(attacker instanceof EntityLivingBase) {
					ItemStack heldItem = getActiveItem(attacker);
					if(!heldItem.isEmpty()) attackerItemGem = CircleGemHelper.getGem(heldItem);
				}
				//At this point either userGem or attackerItemGem are set because either there's a user shooting a (non-living) projectile (user != attacker) or the user is attacking directly (user == attacker)
				List<CircleGem> attackedGems = CircleGemHelper.getGems(attackedEntity); 
				CircleGemType attackedBlockingItemGem = CircleGemType.NONE;
				if(attackedEntity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) attackedEntity;
					ItemStack heldItem = player.getActiveItemStack();
					if(!heldItem.isEmpty() && player.isActiveItemStackBlocking()) {
						attackedBlockingItemGem = CircleGemHelper.getGem(heldItem);
					}
				}
				int gemRelation = 0;
				for(CircleGem gem : attackerGems) {
					if(gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						for(CircleGem gemAttacked : attackedGems) {
							if(gemAttacked.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
								gemRelation += gem.getGemType().getRelation(gemAttacked.getGemType());
							}
						}
						gemRelation += gem.getGemType().getRelation(attackedBlockingItemGem);
					}
				}
				for(CircleGem gemAttacked : attackedGems) {
					if(gemAttacked.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
						gemRelation += attackerItemGem.getRelation(gemAttacked.getGemType());
					}
				}
				gemRelation += attackerItemGem.getRelation(attackedBlockingItemGem);
				for(CircleGem gem : sourceGems) {
					if(gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						for(CircleGem gemAttacked : attackedGems) {
							if(gemAttacked.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
								gemRelation += gem.getGemType().getRelation(gemAttacked.getGemType());
							}
						}
						gemRelation += gem.getGemType().getRelation(attackedBlockingItemGem);
					}
				}
				Iterable<ItemStack> equipment = attackedEntity.getEquipmentAndArmor();
				for(ItemStack equipmentStack : equipment) {
					if(!equipmentStack.isEmpty() && !equipmentStack.equals(getActiveItem(attackedEntity)) && equipmentStack.getItem() instanceof ItemArmor) {
						CircleGemType armorGem = CircleGemHelper.getGem(equipmentStack);
						for(CircleGem gem : attackerGems) {
							if(gem.matchCombatType(CombatType.OFFENSIVE)) {
								gemRelation += gem.getGemType().getRelation(armorGem);
							}
						}
						gemRelation += attackerItemGem.getRelation(armorGem);
						for(CircleGem gem : sourceGems) {
							if(gem.matchCombatType(CombatType.OFFENSIVE)) {
								gemRelation += gem.getGemType().getRelation(armorGem);
							}
						}
					}
				}
				float gemDamageVariation = Math.min(((gemRelation != 0 ? Math.signum(gemRelation) * 1 : 0) + gemRelation) / 6.0F * MAX_GEM_DAMAGE_VARIATION, MAX_GEM_DAMAGE_VARIATION);
				if(gemDamageVariation != 0.0F) {
					damage = Math.max(damage + gemDamageVariation, 1.0F);
				}
				damage = Math.max(0, damage);

				boolean attackerProc = attacker.world.rand.nextFloat() <= (source == attacker && !attacker.onGround && attacker.motionY < 0 ? GEM_PROC_CHANCE * 1.33F : GEM_PROC_CHANCE);
				boolean defenderProc = attacker.world.rand.nextFloat() <= GEM_PROC_CHANCE;

				boolean attackerProcd = false;
				boolean defenderProcd = false;

				List<CircleGemType> attackerProcdGems = new ArrayList<CircleGemType>();
				List<CircleGemType> defenderProcdGems = new ArrayList<CircleGemType>();

				//Attacker gems
				TObjectIntHashMap<CircleGemType> attackerGemCounts = new TObjectIntHashMap<CircleGemType>();
				for(CircleGem gem : attackerGems) {
					if(gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						attackerGemCounts.adjustOrPutValue(gem.getGemType(), 1, 1);
					}
				}
				for(CircleGem gem : sourceGems) {
					if(gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						attackerGemCounts.adjustOrPutValue(gem.getGemType(), 1, 1);
					}
				}
				attackerGemCounts.adjustOrPutValue(attackerItemGem, 1, 1);
				for(CircleGemType gem : attackerGemCounts.keySet()) {
					if(gem != CircleGemType.NONE && applyProc(gem, attacker, source, attacker, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(attackerGemCounts.get(gem), damage), damageSource, damage)) {
						attackerProcd = true;
						if(!attackerProcdGems.contains(gem)){
							attackerProcdGems.add(gem);
						}
					}
				}

				//Defender gems
				TObjectIntHashMap<CircleGemType> defenderGemCounts = new TObjectIntHashMap<CircleGemType>();
				for(ItemStack equipmentStack : equipment) {
					if(!equipmentStack.isEmpty() && !equipmentStack.equals(getActiveItem(attackedEntity)) && equipmentStack.getItem() instanceof ItemArmor) {
						CircleGemType armorGem = CircleGemHelper.getGem(equipmentStack);
						if(armorGem != CircleGemType.NONE) {
							defenderGemCounts.adjustOrPutValue(armorGem, 1, 1);
						}
					}
				}
				for(CircleGem gem : attackedGems) {
					if(gem.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
						defenderGemCounts.adjustOrPutValue(gem.getGemType(), 1, 1);
					}
				}
				defenderGemCounts.adjustOrPutValue(attackedBlockingItemGem, 1, 1);
				for(CircleGemType gem : defenderGemCounts.keySet()) {
					if(gem != CircleGemType.NONE && applyProc(gem, attackedEntity, source, attacker, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(defenderGemCounts.get(gem), damage), damageSource, damage)) {
						defenderProcd = true;
						if(!defenderProcdGems.contains(gem)){
							defenderProcdGems.add(gem);
						}
					}
				}

				if(attackerProcd || defenderProcd) {
					World world = attackedEntity.world;
					int dim = 0;
					if (world instanceof WorldServer) {
						dim = ((WorldServer)world).provider.getDimension();
					}
					if(attackerProcd) {
						for(CircleGemType gem : attackerProcdGems) {
							TheBetweenlands.networkWrapper.sendToAllAround(new MessageGemProc(attackedEntity, true, gem), new TargetPoint(dim, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 64.0D));
						}
					}
					if(defenderProcd) {
						for(CircleGemType gem : defenderProcdGems) {
							TheBetweenlands.networkWrapper.sendToAllAround(new MessageGemProc(attackedEntity, false, gem), new TargetPoint(dim, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, 64.0D));
						}
					}
					source.world.playSound(null, source.posX, source.posY, source.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1, 1);
					source.world.playSound(null, attackedEntity.posX, attackedEntity.posY, attackedEntity.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1, 1);
				}
			}
		}
		return damage;
	}

	private static ItemStack getActiveItem(Entity entity) {
		if(entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			if(!living.getActiveItemStack().isEmpty()) {
				return living.getActiveItemStack();
			}
			if(living.getActiveHand() != null) {
				return living.getHeldItem(living.getActiveHand());
			}
		}
		return ItemStack.EMPTY;
	}

	private static float getMultipleProcStrength(int procs, float strength) {
		float ret = 0;
		for(int i = 0; i < procs; i++) {
			ret += strength / Math.pow(1.4F, i);
		}
		return ret;
	}

	private static boolean applyProc(CircleGemType gem, Entity owner, Entity source, Entity attacker, Entity defender, boolean attackerProc, boolean defenderProc, float strength, DamageSource damageSource, float damage) {
		boolean isAttacker = owner == attacker;
		if((isAttacker && attackerProc) || (!isAttacker && defenderProc)) {
			return gem.applyProc(isAttacker, owner, source, attacker, defender, strength, damageSource, damage);
		}
		return false;
	}
}
