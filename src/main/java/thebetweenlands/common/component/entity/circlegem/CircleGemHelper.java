package thebetweenlands.common.component.entity.circlegem;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.entity.CircleGemData;
import thebetweenlands.common.network.GemProtectionPacket;
import thebetweenlands.common.registries.AttachmentRegistry;

public class CircleGemHelper {

	//TODO tag
//	/**
//	 * Returns true if gems are applicable to the item
//	 * @param item
//	 * @return
//	 */
//	public static boolean isApplicable(Item item) {
//		return (item instanceof AmuletItem || item instanceof ArmorItem || item instanceof SwordItem || item instanceof BowItem || item instanceof ItemTool || item instanceof ShieldItem) && item instanceof ItemAmphibiousArmor;
//	}

	/**
	 * Returns true if gems are applicable to the entity
	 *
	 * @param entity
	 * @return
	 */
	public static boolean isApplicable(Entity entity) {
		return entity instanceof LivingEntity;
	}

	/**
	 * Sets the gem of the specified item stack
	 *
	 * @param stack
	 * @param gem
	 */
	//TODO convert to data component
	public static void setGem(ItemStack stack, CircleGemType gem) {
//		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
//		nbt.setInteger(ITEM_GEM_NBT_TAG, gem.id);
	}

	/**
	 * Returns the gem on the specified item stack
	 *
	 * @param stack
	 * @return
	 */
	public static CircleGemType getGem(ItemStack stack) {
//		NBTTagCompound nbt = stack.getTagCompound();
//		if(nbt != null && nbt.hasKey(ITEM_GEM_NBT_TAG, Constants.NBT.TAG_INT)) {
//			return CircleGemType.fromID(nbt.getInteger(ITEM_GEM_NBT_TAG));
//		}
		return CircleGemType.NONE;
	}

	/**
	 * Adds a gem to the specified entity
	 *
	 * @param entity
	 */
	public static void addGem(Entity entity, CircleGemType gemType, CircleGem.CombatType combatType) {
		CircleGemData cap = entity.getData(AttachmentRegistry.CIRCLE_GEM);
		CircleGem gem = new CircleGem(gemType, combatType);
		if (cap.canAdd(gem) && entity instanceof LivingEntity living) {
			cap.addGem(living, gem);
		}
	}

	/**
	 * Returns a list of gems on the specified entity
	 *
	 * @param entity
	 * @return
	 */
	public static List<CircleGem> getGems(Entity entity) {
		if (entity.hasData(AttachmentRegistry.CIRCLE_GEM)) {
			return entity.getData(AttachmentRegistry.CIRCLE_GEM).getGems();
		}
		return new ArrayList<>();
	}

	/**
	 * Returns the gem of the slot on the specified entity
	 *
	 * @param entity
	 * @param slot
	 * @return
	 */
	public static CircleGem getGem(Entity entity, int slot) {
		if (entity.hasData(AttachmentRegistry.CIRCLE_GEM)) {
			List<CircleGem> gems = entity.getData(AttachmentRegistry.CIRCLE_GEM).getGems();
			if (gems.size() > slot)
				return gems.get(slot);
		}
		return new CircleGem(CircleGemType.NONE, CircleGem.CombatType.BOTH);
	}

	/**
	 * Adds the gem property overrides to the specified item
	 *
	 * @param item
	 */
	//TODO
	public static void addGemPropertyOverrides(Item item) {
		//item.addPropertyOverride(new ResourceLocation("gem"), (stack, worldIn, entityIn) -> CircleGemHelper.getGem(stack).id);
	}

	public static final float MAX_GEM_DAMAGE_VARIATION = 8.0F;
	public static final float GEM_PROC_CHANCE = 0.15F;

	/**
	 * Handles an attack and returns the new damage
	 *
	 * @param damageSource
	 * @param attackedEntity
	 * @param damage
	 * @return
	 */
	public static float handleAttack(DamageSource damageSource, LivingEntity attackedEntity, float damage) {
		if (attackedEntity.hurtTime == 0 && attackedEntity.deathTime == 0 && (!(attackedEntity instanceof Player player) || !(player.getAbilities().invulnerable))) {
			Entity attacker = damageSource.getEntity();
			Entity source = damageSource.getDirectEntity();

			if (attacker != null && source != null) {
				List<CircleGem> attackerGems = CircleGemHelper.getGems(attacker);
				List<CircleGem> sourceGems = new ArrayList<>();
				if (source != attacker) {
					sourceGems.addAll(CircleGemHelper.getGems(source));
				}
				CircleGemType attackerItemGem = CircleGemType.NONE;
				if (attacker instanceof LivingEntity) {
					ItemStack heldItem = getActiveItem(attacker);
					if (!heldItem.isEmpty()) attackerItemGem = CircleGemHelper.getGem(heldItem);
				}
				//At this point either userGem or attackerItemGem are set because either there's a user shooting a (non-living) projectile (user != attacker) or the user is attacking directly (user == attacker)
				List<CircleGem> attackedGems = CircleGemHelper.getGems(attackedEntity);
				CircleGemType attackedBlockingItemGem = CircleGemType.NONE;
				if (attackedEntity instanceof Player player) {
					ItemStack heldItem = player.getUseItem();
					if (!heldItem.isEmpty() && player.isBlocking()) {
						attackedBlockingItemGem = CircleGemHelper.getGem(heldItem);
					}
				}
				int gemRelation = 0;
				for (CircleGem gem : attackerGems) {
					if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						for (CircleGem gemAttacked : attackedGems) {
							if (gemAttacked.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
								gemRelation += gem.gemType().getRelation(gemAttacked.gemType());
							}
						}
						gemRelation += gem.gemType().getRelation(attackedBlockingItemGem);
					}
				}
				for (CircleGem gemAttacked : attackedGems) {
					if (gemAttacked.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
						gemRelation += attackerItemGem.getRelation(gemAttacked.gemType());
					}
				}
				gemRelation += attackerItemGem.getRelation(attackedBlockingItemGem);
				for (CircleGem gem : sourceGems) {
					if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						for (CircleGem gemAttacked : attackedGems) {
							if (gemAttacked.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
								gemRelation += gem.gemType().getRelation(gemAttacked.gemType());
							}
						}
						gemRelation += gem.gemType().getRelation(attackedBlockingItemGem);
					}
				}
				Iterable<ItemStack> equipment = attackedEntity.getArmorAndBodyArmorSlots();
				for (ItemStack equipmentStack : equipment) {
					if (!equipmentStack.isEmpty() && !equipmentStack.equals(getActiveItem(attackedEntity)) && equipmentStack.getItem() instanceof ArmorItem) {
						CircleGemType armorGem = CircleGemHelper.getGem(equipmentStack);
						for (CircleGem gem : attackerGems) {
							if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
								gemRelation += gem.gemType().getRelation(armorGem);
							}
						}
						gemRelation += attackerItemGem.getRelation(armorGem);
						for (CircleGem gem : sourceGems) {
							if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
								gemRelation += gem.gemType().getRelation(armorGem);
							}
						}
					}
				}
				float gemDamageVariation = Math.min(((gemRelation != 0 ? Math.signum(gemRelation) * 1 : 0) + gemRelation) / 6.0F * MAX_GEM_DAMAGE_VARIATION, MAX_GEM_DAMAGE_VARIATION);
				if (gemDamageVariation != 0.0F) {
					damage = Math.max(damage + gemDamageVariation, 1.0F);
				}
				damage = Math.max(0, damage);

				boolean attackerProc = attacker.level().getRandom().nextFloat() <= (source == attacker && !attacker.onGround() && attacker.getDeltaMovement().y() < 0 ? GEM_PROC_CHANCE * 1.33F : GEM_PROC_CHANCE);
				boolean defenderProc = attacker.level().getRandom().nextFloat() <= GEM_PROC_CHANCE;

				boolean attackerProcd = false;
				boolean defenderProcd = false;

				List<CircleGemType> attackerProcdGems = new ArrayList<>();
				List<CircleGemType> defenderProcdGems = new ArrayList<>();

				//Attacker gems
				Object2IntMap<CircleGemType> attackerGemCounts = new Object2IntOpenHashMap<>();
				for (CircleGem gem : attackerGems) {
					if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						if (attackerGemCounts.containsKey(gem.gemType())) {
							attackerGemCounts.put(gem.gemType(), attackerGemCounts.getInt(gem.gemType()) + 1);
						} else {
							attackerGemCounts.put(gem.gemType(), 1);
						}
					}
				}
				for (CircleGem gem : sourceGems) {
					if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						if (attackerGemCounts.containsKey(gem.gemType())) {
							attackerGemCounts.put(gem.gemType(), attackerGemCounts.getInt(gem.gemType()) + 1);
						} else {
							attackerGemCounts.put(gem.gemType(), 1);
						}
					}
				}
				if (attackerGemCounts.containsKey(attackerItemGem)) {
					attackerGemCounts.put(attackerItemGem, attackerGemCounts.getInt(attackerItemGem) + 1);
				} else {
					attackerGemCounts.put(attackerItemGem, 1);
				}
				for (CircleGemType gem : attackerGemCounts.keySet()) {
					if (gem != CircleGemType.NONE && applyProc(gem, attacker, source, attacker, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(attackerGemCounts.get(gem), damage), damageSource, damage)) {
						attackerProcd = true;
						if (!attackerProcdGems.contains(gem)) {
							attackerProcdGems.add(gem);
						}
					}
				}

				//Defender gems
				Object2IntMap<CircleGemType> defenderGemCounts = new Object2IntOpenHashMap<>();
				for (ItemStack equipmentStack : equipment) {
					if (!equipmentStack.isEmpty() && !equipmentStack.equals(getActiveItem(attackedEntity)) && equipmentStack.getItem() instanceof ArmorItem) {
						CircleGemType armorGem = CircleGemHelper.getGem(equipmentStack);
						if (armorGem != CircleGemType.NONE) {
							if (defenderGemCounts.containsKey(armorGem)) {
								defenderGemCounts.put(armorGem, defenderGemCounts.getInt(armorGem) + 1);
							} else {
								defenderGemCounts.put(armorGem, 1);
							}
						}
					}
				}
				for (CircleGem gem : attackedGems) {
					if (gem.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
						if (defenderGemCounts.containsKey(gem.gemType())) {
							defenderGemCounts.put(gem.gemType(), defenderGemCounts.getInt(gem.gemType()) + 1);
						} else {
							defenderGemCounts.put(gem.gemType(), 1);
						}
					}
				}
				if (defenderGemCounts.containsKey(attackedBlockingItemGem)) {
					defenderGemCounts.put(attackedBlockingItemGem, defenderGemCounts.getInt(attackedBlockingItemGem) + 1);
				} else {
					defenderGemCounts.put(attackedBlockingItemGem, 1);
				}
				for (CircleGemType gem : defenderGemCounts.keySet()) {
					if (gem != CircleGemType.NONE && applyProc(gem, attackedEntity, source, attacker, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(defenderGemCounts.get(gem), damage), damageSource, damage)) {
						defenderProcd = true;
						if (!defenderProcdGems.contains(gem)) {
							defenderProcdGems.add(gem);
						}
					}
				}

				if (attackerProcd || defenderProcd) {
					Level level = attackedEntity.level();
					if (attackerProcd) {
						for (CircleGemType gem : attackerProcdGems) {
							PacketDistributor.sendToPlayersNear((ServerLevel) level, null, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 64.0D, new GemProtectionPacket(attackedEntity.getId(), true, gem));
						}
					}
					if (defenderProcd) {
						for (CircleGemType gem : defenderProcdGems) {
							PacketDistributor.sendToPlayersNear((ServerLevel) level, null, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 64.0D, new GemProtectionPacket(attackedEntity.getId(), false, gem));
						}
					}
					level.playSound(null, source.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 1.0F);
					level.playSound(null, attackedEntity.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 1.0F);
				}
			}
		}
		return damage;
	}

	private static ItemStack getActiveItem(Entity entity) {
		if (entity instanceof LivingEntity living) {
			if (!living.getUseItem().isEmpty()) {
				return living.getUseItem();
			}
			return living.getItemInHand(living.getUsedItemHand());
		}
		return ItemStack.EMPTY;
	}

	private static double getMultipleProcStrength(int procs, float strength) {
		double ret = 0;
		for (int i = 0; i < procs; i++) {
			ret += strength / Math.pow(1.4F, i);
		}
		return ret;
	}

	private static boolean applyProc(CircleGemType gem, Entity owner, Entity source, Entity attacker, Entity defender, boolean attackerProc, boolean defenderProc, double strength, DamageSource damageSource, float damage) {
		boolean isAttacker = owner == attacker;
		if ((isAttacker && attackerProc) || (!isAttacker && defenderProc)) {
			return gem.applyProc(isAttacker, source, attacker, defender, strength, damageSource, damage);
		}
		return false;
	}
}
