package thebetweenlands.common.capability.circlegem;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.common.item.misc.ItemGemSinger;

public enum CircleGemType {
	AQUA("aqua", 1, ItemGemSinger.GemSingerTarget.AQUA_MIDDLE_GEM),
	CRIMSON("crimson", 2, ItemGemSinger.GemSingerTarget.CRIMSON_MIDDLE_GEM),
	GREEN("green", 3, ItemGemSinger.GemSingerTarget.GREEN_MIDDLE_GEM),
	NONE("none", 0, null);

	public final String name;
	public final int id;
	
	@Nullable
	public final ItemGemSinger.GemSingerTarget gemSingerTarget;

	public static final CircleGemType[] TYPES = CircleGemType.values();

	private CircleGemType(String name, int id, ItemGemSinger.GemSingerTarget gemSingerTarget) {
		this.name = name;
		this.id = id;
		this.gemSingerTarget = gemSingerTarget;
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
	 * @param strength Proc strength
	 * @param damageSource The damage source
	 * @param damage The damage that was dealt
	 */
	public boolean applyProc(boolean isAttacker, Entity owner, Entity source, Entity attacker, Entity defender, float strength, DamageSource damageSource, float damage) {
		switch(this) {
		case CRIMSON:
			if(isAttacker) {
				if(defender instanceof EntityLivingBase) {
					float knockbackStrength = Math.min(2.5F / 10.0F * (float)strength, 2.5F);
					((EntityLivingBase)defender).knockBack(attacker, knockbackStrength, attacker.posX - defender.posX, attacker.posZ - defender.posZ);
					if(attacker instanceof EntityLivingBase) {
						((EntityLivingBase)attacker).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 110, Math.min(MathHelper.floor(strength * 0.2F), 2)));
					}
					if(source != attacker && source instanceof EntityLivingBase) {
						((EntityLivingBase)source).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 110, Math.min(MathHelper.floor(strength * 0.2F), 2)));
					}
					return true;
				}
			} else {
				DamageSource returnedDamageSource;
				if(defender instanceof EntityPlayer) {
					returnedDamageSource = DamageSource.causePlayerDamage((EntityPlayer)defender);
				} else if(defender instanceof EntityLivingBase) {
					returnedDamageSource = DamageSource.causeMobDamage((EntityLivingBase)defender);
				} else {
					returnedDamageSource = DamageSource.GENERIC;
				}
				attacker.attackEntityFrom(returnedDamageSource, Math.min(damage / 16.0F * strength, damage / 1.5F));
				if(source != attacker) {
					source.attackEntityFrom(returnedDamageSource, Math.min(damage / 16.0F * strength, damage / 1.5F));
				}
				return true;
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
			} else {
				if(defender instanceof EntityLivingBase) {
					((EntityLivingBase)defender).addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 260, Math.min(MathHelper.floor(strength * 0.25F), 2)));
					return true;
				}
			}
			break;
		case AQUA:
			if(isAttacker) {
				if(defender instanceof EntityLivingBase) {
					int amplifier = Math.min(MathHelper.floor(strength * 0.1F), 2);
					switch(amplifier) {
					case 0:
						((EntityLivingBase)defender).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, 0));
						break;
					case 1:
					case 2:
						((EntityLivingBase)defender).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 40, amplifier));
						break;
					}
					return true;
				}
				break;
			} else {
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
