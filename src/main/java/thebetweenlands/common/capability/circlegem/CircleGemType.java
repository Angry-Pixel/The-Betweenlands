package thebetweenlands.common.capability.circlegem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public enum CircleGemType {
	AQUA("aqua", 1), CRIMSON("crimson", 2), GREEN("green", 3), NONE("none", 0);

	public final String name;
	public final int id;

	public static final CircleGemType[] TYPES = CircleGemType.values();

	private CircleGemType(String name, int id) {
		this.name = name;
		this.id = id;
	}

	/**
	 * Returns the relation between two gems.
	 * <p>0: Neutral
	 * <p>1: This gem has an advantage over the other gem
	 * <p>-1: This gem has a disadvantage over the other gem
	 * @param gem Circle gem to compare to
	 * @return
	 */
	public int getRelation(CircleGemType gem) {
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
	 * @param isAttacker Whether the owner is the attacker
	 * @param owner Owner of the gem
	 * @param source The source (damage source, e.g. the entity that shot the projectile)
	 * @param attacker The entity that is actually attacking (can be both the owner himself or a projectile)
	 * @param defender The defending entity
	 * @param strength Attack strength
	 */
	public boolean applyProc(boolean isAttacker, Entity owner, Entity source, Entity attacker, Entity defender, float strength) {
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
						((EntityLivingBase)attacker).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 110, Math.min(MathHelper.floor(strength * 0.2F), 2)));
					}
					if(source != attacker && source instanceof EntityLivingBase) {
						((EntityLivingBase)source).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 110, Math.min(MathHelper.floor(strength * 0.2F), 2)));
					}
					return true;
				}
			}
			break;
		case GREEN:
			if(isAttacker) {
				boolean healed = false;
				if(attacker instanceof EntityLivingBase) {
					((EntityLivingBase)attacker).heal(Math.min(Math.max(strength * 0.45F, 1.0F), 10.0F));
					healed = true;
				}
				if(source != attacker && source instanceof EntityLivingBase) {
					((EntityLivingBase)source).heal(Math.min(Math.max(strength * 0.45F, 1.0F), 10.0F));
					healed = true;
				}
				return healed;
			}
			break;
		case AQUA:
			if(!isAttacker) {
				if(defender instanceof EntityLivingBase) {
					((EntityLivingBase)defender).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 130, Math.min(MathHelper.floor(strength * 0.3F), 2)));
					return true;
				}
			}
			break;
		default:
		}
		return false;
	}

	/**
	 * Returns the gem for the specified name
	 * @param name
	 * @return
	 */
	public static CircleGemType fromName(String name) {
		for(CircleGemType gem : TYPES) {
			if(gem.name.equals(name)) {
				return gem;
			}
		}
		return NONE;
	}

	/**
	 * Returns the gem for the specified ID
	 * @param id
	 * @return
	 */
	public static CircleGemType fromID(int id) {
		for(CircleGemType gem : TYPES) {
			if(gem.id == id) {
				return gem;
			}
		}
		return NONE;
	}
}
