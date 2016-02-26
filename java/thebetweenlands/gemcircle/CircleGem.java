package thebetweenlands.gemcircle;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.network.packet.server.PacketGemProc;

public enum CircleGem {
	CRIMSON("crimson"), GREEN("green"), AQUA("aqua"), NONE("none");

	public final String name;
	public static final CircleGem[] TYPES = CircleGem.values();

	private CircleGem(String name) {
		this.name = name;
	}

	/**
	 * Returns the relation between two gems.
	 * <p>0: Neutral
	 * <p>1: This gem has an advantage over the other gem
	 * <p>-1: This gem has a disadvantage over the other gem
	 * @param gem Circle gem to compare to
	 * @return
	 */
	public int getRelation(CircleGem gem) {
		switch(this) {
		case CRIMSON:
			switch(gem){
			case GREEN:
				return 1;
			case AQUA:
				return -1;
			default:
				return 0;
			}
		case GREEN:
			switch(gem){
			case AQUA:
				return 1;
			case CRIMSON:
				return -1;
			default:
				return 0;
			}
		case AQUA:
			switch(gem){
			case CRIMSON:
				return 1;
			case GREEN:
				return -1;
			default:
				return 0;
			}
		default:
			return 0;
		}
	}

	/**
	 * Applies the gem proc to the attacker or defender
	 * @param owner
	 * @param attacker
	 * @param defender
	 * @param attackerProc
	 * @param defenderProc
	 * @param strength
	 */
	public boolean applyProc(boolean isAttacker, Entity owner, Entity attacker, Entity defender, float strength) {
		switch(this) {
		case CRIMSON:
			if(isAttacker) {
				if(defender instanceof EntityLivingBase) {
					double knockbackStrength = Math.min(2.2D / 10.0D * strength, 2.2D);
					double mx = attacker.posX - defender.posX;
					double mz;
					for(mz = attacker.posZ - defender.posZ; mx * mx + mz * mz < 1.0E-4D; mz = (Math.random() - Math.random()) * 0.01D) {
						mx = (Math.random() - Math.random()) * 0.01D;
					}
					double len = Math.sqrt(mx*mx+mz*mz);
					((EntityLivingBase)defender).knockBack(attacker, strength, mx * 6.0F, mz * 6.0F);
					defender.motionX /= 2.0D;
					defender.motionY /= 2.0D;
					defender.motionZ /= 2.0D;
					defender.motionX -= mx / len * knockbackStrength;
					defender.motionY += 0.4D;
					defender.motionZ -= mz / len * knockbackStrength;
					if (defender.motionY > 0.4D) {
						defender.motionY = 0.4D;
					}
					if(attacker instanceof EntityLivingBase) {
						((EntityLivingBase)attacker).addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), 90, Math.min(MathHelper.floor_float(strength * 0.2F), 2)));
					}
					return true;
				}
			}
			break;
		case GREEN:
			if(isAttacker) {
				if(attacker instanceof EntityLivingBase) {
					((EntityLivingBase)attacker).heal(Math.min(Math.max(strength * 0.45F, 1.0F), 10.0F));
					return true;
				}
			}
			break;
		case AQUA:
			if(!isAttacker) {
				if(defender instanceof EntityLivingBase) {
					EntityLivingBase entityLiving = (EntityLivingBase)defender;
					entityLiving.addPotionEffect(new PotionEffect(Potion.resistance.getId(), 90, Math.min(MathHelper.floor_float(strength * 0.3F), 2)));
					return true;
				}
			}
			break;
		default:
		}
		return false;
	}

	public static CircleGem fromName(String name) {
		for(CircleGem gem : TYPES) {
			if(gem.name.equals(name)) {
				return gem;
			}
		}
		return NONE;
	}

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
				List<EntityGem> attackerGems = getGems(attacker);
				List<EntityGem> userGems = new ArrayList<EntityGem>();
				if(user != attacker) {
					userGems.addAll(getGems(user));
				}
				CircleGem attackerItemGem = CircleGem.NONE;
				if(attacker instanceof EntityLivingBase) {
					ItemStack heldItem = ((EntityLivingBase)attacker).getHeldItem();
					if(heldItem != null) attackerItemGem = getGem(heldItem);
				}
				//At this point either userGem or attackerItemGem are set because either there's a user shooting a (non-living) projectile (user != attacker) or the user is attacking directly (user == attacker)
				List<EntityGem> attackedGems = getGems(attackedEntity); 
				CircleGem attackedBlockingItemGem = CircleGem.NONE;
				if(attacker instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) attacker;
					ItemStack heldItem = player.getHeldItem();
					if(heldItem != null && player.isBlocking()) {
						attackedBlockingItemGem = getGem(heldItem);
					}
				}
				int gemRelation = 0;
				for(EntityGem gem : attackerGems) {
					if(gem.matches(EntityGem.Type.OFFENSIVE)) {
						for(EntityGem gemAttacked : attackedGems) {
							if(gemAttacked.matches(EntityGem.Type.DEFENSIVE)) {
								gemRelation += gem.getGem().getRelation(gemAttacked.getGem());
							}
						}
						gemRelation += gem.getGem().getRelation(attackedBlockingItemGem);
					}
				}
				for(EntityGem gemAttacked : attackedGems) {
					if(gemAttacked.matches(EntityGem.Type.DEFENSIVE)) {
						gemRelation += attackerItemGem.getRelation(gemAttacked.getGem());
					}
				}
				gemRelation += attackerItemGem.getRelation(attackedBlockingItemGem);
				for(EntityGem gem : userGems) {
					if(gem.matches(EntityGem.Type.OFFENSIVE)) {
						for(EntityGem gemAttacked : attackedGems) {
							if(gemAttacked.matches(EntityGem.Type.DEFENSIVE)) {
								gemRelation += gem.getGem().getRelation(gemAttacked.getGem());
							}
						}
						gemRelation += gem.getGem().getRelation(attackedBlockingItemGem);
					}
				}
				if(attackedEntity instanceof EntityLivingBase) {
					ItemStack[] equipment = ((EntityLivingBase)attackedEntity).getLastActiveItems();
					for(int i = 0; i < equipment.length; i++) {
						ItemStack equipmentStack = equipment[i];
						if(equipmentStack != null && !equipmentStack.equals(((EntityLivingBase)attackedEntity).getHeldItem()) && equipmentStack.getItem() instanceof ItemArmor) {
							CircleGem armorGem = getGem(equipmentStack);
							for(EntityGem gem : attackerGems) {
								if(gem.matches(EntityGem.Type.OFFENSIVE)) {
									gemRelation += gem.getGem().getRelation(armorGem);
								}
							}
							gemRelation += attackerItemGem.getRelation(armorGem);
							for(EntityGem gem : userGems) {
								if(gem.matches(EntityGem.Type.OFFENSIVE)) {
									gemRelation += gem.getGem().getRelation(armorGem);
								}
							}
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
				for(EntityGem gem : attackerGems) {
					if(gem.matches(EntityGem.Type.OFFENSIVE)) {
						attackerGemCounts.adjustOrPutValue(gem.getGem(), 1, 1);
					}
				}
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
							CircleGem armorGem = getGem(equipmentStack);
							if(armorGem != CircleGem.NONE) {
								defenderGemCounts.adjustOrPutValue(armorGem, 1, 1);
							}
						}
					}
				}
				for(EntityGem gem : attackedGems) {
					if(gem.matches(EntityGem.Type.DEFENSIVE)) {
						defenderGemCounts.adjustOrPutValue(gem.getGem(), 1, 1);
					}
				}
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

	public static void setGem(ItemStack stack, CircleGem gem) {
		if(stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setString("blCircleGem", gem == null ? "none" : gem.name);
	}

	public static CircleGem getGem(ItemStack stack) {
		if(stack.stackTagCompound != null) {
			return CircleGem.fromName(stack.stackTagCompound.getString("blCircleGem"));
		}
		return CircleGem.NONE;
	}

	public static void addGem(Entity entity, CircleGem gem, EntityGem.Type type) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			property.addGem(gem, type);
		}
	}

	public static List<EntityGem> getGems(Entity entity) {
		List<EntityGem> gems = new ArrayList<EntityGem>();
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			gems.addAll(property.getGems());
		}
		return gems;
	}

	public static EntityGem getGem(Entity entity, int slot) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null && property.getGems().size() > slot) {
			return property.getGems().get(slot);
		}
		return null;
	}
}