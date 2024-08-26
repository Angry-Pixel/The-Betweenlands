package thebetweenlands.common.component.entity.circlegem;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.common.items.GemSingerItem;

import java.util.function.IntFunction;

public enum CircleGemType implements StringRepresentable {
	AQUA("aqua", 1, GemSingerItem.GemSingerTarget.AQUA_MIDDLE_GEM),
	CRIMSON("crimson", 2, GemSingerItem.GemSingerTarget.CRIMSON_MIDDLE_GEM),
	GREEN("green", 3, GemSingerItem.GemSingerTarget.GREEN_MIDDLE_GEM),
	NONE("none", 0, null);

	public final String name;
	public final int id;

	@Nullable
	public final GemSingerItem.GemSingerTarget gemSingerTarget;

	public static final StringRepresentable.EnumCodec<CircleGemType> CODEC = StringRepresentable.fromEnum(CircleGemType::values);
	public static final IntFunction<CircleGemType> BY_ID = ByIdMap.continuous(CircleGemType::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, CircleGemType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CircleGemType::getId);

	CircleGemType(String name, int id, @Nullable GemSingerItem.GemSingerTarget gemSingerTarget) {
		this.name = name;
		this.id = id;
		this.gemSingerTarget = gemSingerTarget;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public int getId() {
		return this.id;
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
		return switch (this) {
			case CRIMSON -> switch (gem) {
				case GREEN -> 1;
				case AQUA -> -1;
				default -> 0;
			};
			case GREEN -> switch (gem) {
				case AQUA -> 1;
				case CRIMSON -> -1;
				default -> 0;
			};
			case AQUA -> switch (gem) {
				case CRIMSON -> 1;
				case GREEN -> -1;
				default -> 0;
			};
			default -> 0;
		};
	}

	/**
	 * Applies the gem proc to the attacker or defender
	 * @param isAttacker Whether the owner is the attacker
	 * @param source The source (damage source, e.g. the entity that shot the projectile)
	 * @param attacker The entity that is actually attacking (can be both the owner himself or a projectile)
	 * @param defender The defending entity
	 * @param strength Proc strength
	 * @param damageSource The damage source
	 * @param damage The damage that was dealt
	 */
	public boolean applyProc(boolean isAttacker, Entity source, Entity attacker, Entity defender, double strength, DamageSource damageSource, float damage) {
		switch(this) {
		case CRIMSON:
			if(isAttacker) {
				if(defender instanceof LivingEntity living) {
					float knockbackStrength = (float) Math.min(2.5F / 10.0F * strength, 2.5F);
					living.knockback(knockbackStrength, attacker.getX() - defender.getX(), attacker.getZ() - defender.getZ());
					if(attacker instanceof LivingEntity living1) {
						living1.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 110, Math.min(Mth.floor(strength * 0.2F), 2)));
					}
					if(source != attacker && source instanceof LivingEntity living1) {
						living1.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 110, Math.min(Mth.floor(strength * 0.2F), 2)));
					}
					return true;
				}
			} else {
				DamageSource returnedDamageSource;
				if(defender instanceof Player player) {
					returnedDamageSource = player.damageSources().playerAttack(player);
				} else if(defender instanceof LivingEntity living) {
					returnedDamageSource = living.damageSources().mobAttack(living);
				} else {
					returnedDamageSource = defender.damageSources().generic();
				}
				attacker.hurt(returnedDamageSource, (float) Math.min(damage / 16.0F * strength, damage / 1.5F));
				if(source != attacker) {
					source.hurt(returnedDamageSource, (float) Math.min(damage / 16.0F * strength, damage / 1.5F));
				}
				return true;
			}
			break;
		case GREEN:
			if(isAttacker) {
				boolean healed = false;
				if(attacker instanceof LivingEntity living) {
					living.heal((float) Math.min(Math.max(strength * 0.45F, 1.0F), 10.0F));
					healed = true;
				}
				if(source != attacker && source instanceof LivingEntity living) {
					living.heal((float) Math.min(Math.max(strength * 0.45F, 1.0F), 10.0F));
					healed = true;
				}
				return healed;
			} else {
				if(defender instanceof LivingEntity living) {
					living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 260, Math.min(Mth.floor(strength * 0.25F), 2)));
					return true;
				}
			}
			break;
		case AQUA:
			if(isAttacker) {
				if(defender instanceof LivingEntity living) {
					int amplifier = Math.min(Mth.floor(strength * 0.1F), 2);
					switch(amplifier) {
					case 0:
						living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
						break;
					case 1:
					case 2:
						living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, amplifier));
						break;
					}
					return true;
				}
				break;
			} else {
				if(defender instanceof LivingEntity living) {
					living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 130, Math.min(Mth.floor(strength * 0.3F), 2)));
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
		for(CircleGemType gem : values()) {
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
		for(CircleGemType gem : values()) {
			if(gem.id == id) {
				return gem;
			}
		}
		return NONE;
	}

//	public final IAmphibiousArmorUpgrade getAmphibiousArmorUpgrade() {
//		return switch (this) {
//			case AQUA -> AmphibiousArmorUpgrades.AQUA_GEM;
//			case GREEN -> AmphibiousArmorUpgrades.GREEN_GEM;
//			case CRIMSON -> AmphibiousArmorUpgrades.CRIMSON_GEM;
//			default -> null;
//		};
//	}

//	public final Consumer<ItemStack> getAmphibiousArmorOnChangedHandler() {
//		return armor -> {
//			if(armor.getItem() instanceof AmphibiousArmorItem armorItem) {
//				if(armorItem.getUpgradeCount(armor, this.getAmphibiousArmorUpgrade()) > 0) {
//					CircleGemHelper.setGem(armor, this);
//				} else {
//					CircleGemHelper.setGem(armor, CircleGemType.NONE);
//				}
//			}
//		};
//	}
}
