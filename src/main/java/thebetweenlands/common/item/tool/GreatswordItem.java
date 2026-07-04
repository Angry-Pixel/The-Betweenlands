package thebetweenlands.common.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import thebetweenlands.api.item.BigSwingAnimation;
import thebetweenlands.common.component.item.SwingData;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Collection;
import java.util.List;

public class GreatswordItem extends TieredItem implements BigSwingAnimation {

	public static final ResourceLocation BASE_INTERACTION_ID = ResourceLocation.withDefaultNamespace("base_interaction");

	public GreatswordItem(Tier tier, Properties properties) {
		super(tier, properties.component(DataComponents.TOOL, SwordItem.createToolProperties()));
	}

	public GreatswordItem(Tier tier, TagKey<Block> breakables, Properties properties) {
		super(tier, properties.component(DataComponents.TOOL, tier.createToolProperties(breakables)));
	}

	public static ItemAttributeModifiers createAttributes(Tier tier, float damage, float speed, float interaction) {
		return ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BASE_INTERACTION_ID, interaction, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
			.build();
	}

	@Override
	public void onLeftClick(Player player, ItemStack stack) {
		boolean enemiesInReach = false;

		if (!player.level().isClientSide() && !player.swinging) {
			stack.set(DataComponentRegistry.SWING_DATA, new SwingData(player.tickCount, true, 0, player.getAttackStrengthScale(0)));
		}

		double aoeReach = this.getAoEReach(player, stack);
		double aoeHalfAngle = this.getAoEHalfAngle(player, stack);

		//oof
		AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
		double baseAttackSpeed = attackSpeed.getBaseValue();

		Collection<AttributeModifier> attackSpeedModifiers = attackSpeed.getModifiers();
		for (AttributeModifier modifier : attackSpeedModifiers) {
			attackSpeed.removeModifier(modifier);
		}

		float initialAttackStrength = Math.max(player.getAttackStrengthScale(0.5F), stack.getOrDefault(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT).cooldown());

		List<Entity> others = player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(aoeReach), Entity::isAttackable);
		for (Entity target : others) {
			if (target != player) {
				Entity[] parts = target.getParts();

				for (int i = 0; i < 1 + (parts != null ? parts.length : 0); i++) {
					Entity part;
					if (i == 0) {
						part = target;
					} else {
						part = parts[i - 1];
					}

					double dist = part.distanceTo(player);

					if (dist < aoeReach) {
						double angle = Math.min(
								Math.toDegrees(Math.acos(part.position().subtract(player.position()).normalize().dot(player.getLookAngle()))),
								Math.min(
										Math.toDegrees(Math.acos(part.position().subtract(player.getEyePosition(1)).normalize().dot(player.getLookAngle()))),
										Math.toDegrees(Math.acos(part.position().add(0, part.getBbHeight() / 2, 0).subtract(player.getEyePosition(1)).normalize().dot(player.getLookAngle())))
								)
						);

						if (angle < aoeHalfAngle) {
							double distXZ = Math.sqrt((part.getX() - player.getX()) * (part.getX() - player.getX()) + (part.getZ() - player.getZ()) * (part.getZ() - player.getZ()));

							double hitY = player.getY() + player.getEyeHeight() + player.getLookAngle().y / Math.sqrt(Math.pow(player.getLookAngle().x, 2) + Math.pow(player.getLookAngle().z, 2) + 0.1D) * distXZ;

							if (hitY > part.getBoundingBox().minY - 0.25D && hitY < part.getBoundingBox().maxY + 0.25D) {
								if (player.level().clip(new ClipContext(player.position().add(0, player.getEyeHeight(), 0), part.position().add(0, part.getBbHeight() / 2, 0), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getType() == HitResult.Type.MISS) {
									if (!player.level().isClientSide()) {
										//yikes
										//Adjust attack speed such that the current attack strength becomes the same as the initial attack strength
										player.resetAttackStrengthTicker();
										attackSpeed.setBaseValue(20 * initialAttackStrength / 0.5f);

										player.attack(target);
									}

									enemiesInReach = true;

									break;
								}
							}
						}
					}
				}
			}
		}

		//oof
		attackSpeed.setBaseValue(baseAttackSpeed);
		for (AttributeModifier modifier : attackSpeedModifiers) {
			if (!attackSpeed.hasModifier(modifier.id())) {
				attackSpeed.addOrReplacePermanentModifier(modifier);
			}
		}

		if (player.level().isClientSide() && (!player.swinging || player.swingTime >= player.getCurrentSwingDuration() / 2 || player.swingTime < 0)) {
			player.playSound(SoundRegistry.LONG_SWING.get(), 1.2F, 0.925F * ((0.65F + this.getSwingSpeedMultiplier(player, stack)) * 0.66F + 0.33F) + player.getRandom().nextFloat() * 0.15F);

			if (enemiesInReach) {
				player.playSound(SoundRegistry.LONG_SLICE.get(), 1.2F, 0.925F * ((0.65F + this.getSwingSpeedMultiplier(player, stack)) * 0.66F + 0.33F) + player.getRandom().nextFloat() * 0.15F);
			}
		}

		stack.update(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT, data -> data.withCooldown(0));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if (!level.isClientSide()) {
			boolean swingInProgress = this.isLongSwingInProgress(stack);
			boolean newSwingInProgress;

			if (entity instanceof LivingEntity living && isSelected) {
				int ticksElapsed = living.tickCount - this.getSwingStartTicks(stack);
				newSwingInProgress = ticksElapsed >= 0 && ticksElapsed < this.getLongSwingDuration(living, stack);
			} else {
				newSwingInProgress = false;
			}

			if (swingInProgress != newSwingInProgress) {
				stack.update(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT, swingData -> swingData.setSwinging(newSwingInProgress));
			}
		}
	}

	protected float getSwingStartCooledAttackStrength(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT).startCooldown();
	}

	protected int getSwingStartTicks(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT).startTick();
	}

	protected boolean isLongSwingInProgress(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT).swinging();
	}

	protected float getLongSwingDuration(LivingEntity entity, ItemStack stack) {
		return entity.getCurrentSwingDuration() / 3.0F / this.getSwingSpeedMultiplier(entity, stack);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		target.knockback(0.8F, Mth.sin(attacker.getYRot() * 0.017453292F), -Mth.cos(attacker.getYRot() * 0.017453292F));
		return true;
	}

	@Override
	public float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.35F;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability);
	}
}
