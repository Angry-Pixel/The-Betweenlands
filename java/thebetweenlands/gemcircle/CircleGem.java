package thebetweenlands.gemcircle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

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
}
