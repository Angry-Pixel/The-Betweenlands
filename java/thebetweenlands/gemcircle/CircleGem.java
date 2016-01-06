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
	public boolean applyProc(Entity owner, Entity attacker, Entity defender, boolean attackerProc, boolean defenderProc, float strength) {
		switch(this) {
		case CRIMSON:
			if(attackerProc && owner == attacker) {
				defender.setFire(MathHelper.floor_float(Math.min(strength * 0.2F, 6.0F)) + 2);
				return true;
			}
			break;
		case GREEN:
			if(attackerProc && owner == attacker) {
				if(attacker instanceof EntityLivingBase) {
					((EntityLivingBase)attacker).heal(Math.min(Math.max(strength * 0.45F, 1.0F), 10.0F));
					return true;
				}
			}
			break;
		case AQUA:
			if(defenderProc && owner == defender) {
				if(defender instanceof EntityLivingBase) {
					EntityLivingBase entityLiving = (EntityLivingBase)defender;
					entityLiving.addPotionEffect(new PotionEffect(Potion.resistance.getId(), 90, Math.min(MathHelper.floor_float(strength * 0.4F), 3)));
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
