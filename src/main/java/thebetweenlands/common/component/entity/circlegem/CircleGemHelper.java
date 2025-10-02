package thebetweenlands.common.component.entity.circlegem;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.entity.CircleGemData;
import thebetweenlands.common.network.clientbound.GemProtectionPacket;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.ArrayList;
import java.util.List;

public class CircleGemHelper {

	public static final float MAX_GEM_DAMAGE_VARIATION = 8.0F;
	public static final float GEM_PROC_CHANCE = 0.15F;

	/**
	 * Sets the gem of the specified item stack
	 * @param stack
	 * @param gem
	 */
	public static void setGem(ItemStack stack, CircleGemType gem) {
		stack.set(DataComponentRegistry.CIRCLE_GEM, gem);
	}

	/**
	 * Returns the gem on the specified item stack
	 *
	 * @param stack
	 * @return
	 */
	public static CircleGemType getGem(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.CIRCLE_GEM, CircleGemType.NONE);
	}

	/**
	 * Adds a gem to the specified entity
	 *
	 * @param entity
	 */
	public static void addGem(Entity entity, CircleGemType gemType, CircleGem.CombatType combatType) {
		entity.getData(AttachmentRegistry.CIRCLE_GEM).addGem(new CircleGem(gemType, combatType));
	}

	/**
	 * Returns a list of gems on the specified entity
	 *
	 * @param entity
	 * @return
	 */
	public static List<CircleGem> getGems(Entity entity) {
		return entity.getData(AttachmentRegistry.CIRCLE_GEM).getGems();
	}

	/**
	 * Returns the gem of the slot on the specified entity
	 *
	 * @param entity
	 * @param slot
	 * @return
	 */
	public static CircleGem getGem(Entity entity, int slot) {
		CircleGemData data = entity.getData(AttachmentRegistry.CIRCLE_GEM);
		List<CircleGem> gems = data.getGems();
		if (gems.size() > slot)
			return data.getGems().get(slot);

		return new CircleGem(CircleGemType.NONE, CircleGem.CombatType.BOTH);
	}

	public static float handleAttack(DamageSource damageSource, LivingEntity attackedEntity, float damage) {
		if (attackedEntity.hurtTime == 0 && attackedEntity.deathTime == 0 && (!(attackedEntity instanceof Player player) || !player.getAbilities().invulnerable)) {
			Entity attacker = damageSource.getEntity();
			Entity source = damageSource.getDirectEntity();

			if (attacker != null && source != null) {
				List<CircleGem> attackerGems = CircleGemHelper.getGems(attacker);
				List<CircleGem> sourceGems = new ArrayList<CircleGem>();
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

				boolean attackerProc = attacker.getRandom().nextFloat() <= (source == attacker && !attacker.onGround() && attacker.getDeltaMovement().y() < 0 ? GEM_PROC_CHANCE * 1.33F : GEM_PROC_CHANCE);
				boolean defenderProc = attacker.getRandom().nextFloat() <= GEM_PROC_CHANCE;

				boolean attackerProcd = false;
				boolean defenderProcd = false;

				List<CircleGemType> attackerProcdGems = new ArrayList<CircleGemType>();
				List<CircleGemType> defenderProcdGems = new ArrayList<CircleGemType>();

				//Attacker gems
				Object2IntMap<CircleGemType> attackerGemCounts = new Object2IntOpenHashMap<>();
				for (CircleGem gem : attackerGems) {
					if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						attackerGemCounts.merge(gem.gemType(), 1, Integer::sum);
					}
				}
				for (CircleGem gem : sourceGems) {
					if (gem.matchCombatType(CircleGem.CombatType.OFFENSIVE)) {
						attackerGemCounts.merge(gem.gemType(), 1, Integer::sum);
					}
				}
				attackerGemCounts.merge(attackerItemGem, 1, Integer::sum);
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
							defenderGemCounts.merge(armorGem, 1, Integer::sum);
						}
					}
				}
				for (CircleGem gem : attackedGems) {
					if (gem.matchCombatType(CircleGem.CombatType.DEFENSIVE)) {
						defenderGemCounts.merge(gem.gemType(), 1, Integer::sum);
					}
				}
				defenderGemCounts.merge(attackedBlockingItemGem, 1, Integer::sum);
				for (CircleGemType gem : defenderGemCounts.keySet()) {
					if (gem != CircleGemType.NONE && applyProc(gem, attackedEntity, source, attacker, attackedEntity, attackerProc, defenderProc, getMultipleProcStrength(defenderGemCounts.getInt(gem), damage), damageSource, damage)) {
						defenderProcd = true;
						if (!defenderProcdGems.contains(gem)) {
							defenderProcdGems.add(gem);
						}
					}
				}

				if (attackerProcd || defenderProcd) {
					Level level = attackedEntity.level();
					if (level instanceof ServerLevel serverLevel) {
						if (attackerProcd) {
							for (CircleGemType gem : attackerProcdGems) {
								PacketDistributor.sendToPlayersNear(serverLevel, null, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 64.0D, new GemProtectionPacket(attackedEntity.getId(), true, gem));
							}
						}
						if (defenderProcd) {
							for (CircleGemType gem : defenderProcdGems) {
								PacketDistributor.sendToPlayersNear(serverLevel, null, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 64.0D, new GemProtectionPacket(attackedEntity.getId(), false, gem));
							}
						}
					}
					level.playSound(null, source.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1, 1);
					level.playSound(null, attackedEntity.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1, 1);
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
			if (living.getUsedItemHand() != null) {
				return living.getItemInHand(living.getUsedItemHand());
			}
		}
		return ItemStack.EMPTY;
	}

	private static float getMultipleProcStrength(int procs, float strength) {
		float ret = 0;
		for (int i = 0; i < procs; i++) {
			ret += strength / Math.pow(1.4F, i);
		}
		return ret;
	}

	private static boolean applyProc(CircleGemType gem, Entity owner, Entity source, Entity attacker, Entity defender, boolean attackerProc, boolean defenderProc, float strength, DamageSource damageSource, float damage) {
		boolean isAttacker = owner == attacker;
		if ((isAttacker && attackerProc) || (!isAttacker && defenderProc)) {
			return gem.applyProc(isAttacker, source, attacker, defender, strength, damageSource, damage);
		}
		return false;
	}
}
