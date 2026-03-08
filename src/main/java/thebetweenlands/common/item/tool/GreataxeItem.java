package thebetweenlands.common.item.tool;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GreataxeItem extends GreatswordItem {

	public GreataxeItem(Tier tier, Properties properties) {
		super(tier, BlockTags.MINEABLE_WITH_AXE, properties);
	}

	public static ItemAttributeModifiers createAttributes(Tier tier, float damage, float speed, float interaction) {
		return ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BASE_INTERACTION_ID, interaction, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
			.build();
	}

	protected double getBlockBreakHalfAngle(LivingEntity entity, ItemStack stack) {
		return 45.0D;
	}

	protected double getBlockBreakReach(LivingEntity entity, ItemStack stack) {
		return 2.6D;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if (entity instanceof ServerPlayer player && !level.isClientSide()) {
			if (isSelected && this.isLongSwingInProgress(stack) && this.getSwingStartCooledAttackStrength(stack) < 0.85F) {
				int ticksElapsed = player.tickCount - this.getSwingStartTicks(stack) - 1;

				float longSwingTickProgress = 1.0F / (this.getLongSwingDuration(player, stack) - 1);
				float longSwingProgressEnd = (ticksElapsed + 1) / (this.getLongSwingDuration(player, stack) - 1);

				List<BlockPos> targetBlocks = new ArrayList<>();

				for (float longSwingProgressStart = Math.max(0, longSwingProgressEnd - Math.max(0.25F, longSwingTickProgress)); longSwingProgressStart < longSwingProgressEnd; longSwingProgressStart += longSwingTickProgress) {
					double breakReach = this.getBlockBreakReach(player, stack);
					int blockReach = Mth.ceil(breakReach);

					double breakHalfAngle = this.getBlockBreakHalfAngle(player, stack);

					double minAngle = -breakHalfAngle + breakHalfAngle * 2 * longSwingProgressStart;
					double maxAngle = -breakHalfAngle + breakHalfAngle * 2 * longSwingProgressEnd;

					float yaw = player.getYRot();
					float pitch = player.getXRot();

					float yc = Mth.cos(-yaw * 0.017453292F - Mth.PI);
					float ys = Mth.sin(-yaw * 0.017453292F - Mth.PI);
					float pc = -Mth.cos(-pitch * 0.017453292F);
					float ps = Mth.sin(-pitch * 0.017453292F);

					Vec3 forward = new Vec3(ys * pc, ps, yc * pc).normalize();

					pc = -Mth.cos(-(pitch - 90) * 0.017453292F);
					ps = Mth.sin(-(pitch - 90) * 0.017453292F);

					Vec3 up = new Vec3(ys * pc, ps, yc * pc).normalize();

					Vec3 right = forward.cross(up);

					for (int xo = -blockReach; xo <= blockReach; xo++) {
						for (int yo = -blockReach; yo <= blockReach; yo++) {
							for (int zo = -blockReach; zo <= blockReach; zo++) {
								BlockPos pos = BlockPos.containing(player.getX() + xo, player.getY() + player.getBbHeight() * 0.5D + yo, player.getZ() + zo);
								Vec3 center = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

								double dist = center.distanceTo(player.getEyePosition(1));

								if (dist < breakReach) {
									Vec3 dir = center.subtract(player.getEyePosition(1)).normalize();

									double py = forward.dot(dir);
									double px = right.dot(dir);

									double angle = Math.toDegrees(-Math.atan2(px, py));

									if (angle >= minAngle && angle < maxAngle) {
										double distUp = up.dot(new Vec3(center.x - player.getX(), center.y - player.getY() - player.getEyeHeight(), center.z - player.getZ()));

										double verticalRange = 1.0D + 1.5D * (3 - Mth.clamp(dist, 0, 3)) / 3.0D;

										if (distUp >= -verticalRange - 0.5D && distUp <= verticalRange - 0.5D) {
											BlockState state = level.getBlockState(pos);

											if (state.is(BlockTags.LOGS) && state.getDestroySpeed(level, pos) <= 2.25F && state.getDestroyProgress(player, level, pos) > 0.01F) {
												targetBlocks.add(pos);
											}
										}
									}
								}
							}
						}
					}
				}

				if (!targetBlocks.isEmpty()) {
					Collections.shuffle(targetBlocks);

					int playedEffects = 0;
					for (BlockPos pos : targetBlocks) {
						if (!level.isEmptyBlock(pos)) {
							BlockState state = level.getBlockState(pos);

							if (player.gameMode.destroyBlock(pos)) {
								if (++playedEffects <= 3) {
									level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.14F;
	}

	@Override
	public double getAoEReach(LivingEntity entityLiving, ItemStack stack) {
		return 0;
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return true;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return true;
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Player player = context.getPlayer();
		if (playerHasShieldUseIntent(context)) {
			return InteractionResult.PASS;
		} else {
			Optional<BlockState> optional = this.evaluateNewBlockState(level, blockpos, player, level.getBlockState(blockpos), context);
			if (optional.isEmpty()) {
				return InteractionResult.PASS;
			} else {
				ItemStack itemstack = context.getItemInHand();
				if (player instanceof ServerPlayer) {
					CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
				}

				level.setBlock(blockpos, optional.get(), 11);
				level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional.get()));
				if (player != null) {
					itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
	}

	private static boolean playerHasShieldUseIntent(UseOnContext context) {
		Player player = context.getPlayer();
		return context.getHand().equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().canPerformAction(ItemAbilities.SHIELD_BLOCK) && !player.isSecondaryUseActive();
	}

	private Optional<BlockState> evaluateNewBlockState(Level level, BlockPos pos, @Nullable Player player, BlockState state, UseOnContext p_40529_) {
		Optional<BlockState> optional = Optional.ofNullable(state.getToolModifiedState(p_40529_, ItemAbilities.AXE_STRIP, false));
		if (optional.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			return optional;
		} else {
			Optional<BlockState> optional1 = Optional.ofNullable(state.getToolModifiedState(p_40529_, ItemAbilities.AXE_SCRAPE, false));
			if (optional1.isPresent()) {
				level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.levelEvent(player, 3005, pos, 0);
				return optional1;
			} else {
				Optional<BlockState> optional2 = Optional.ofNullable(state.getToolModifiedState(p_40529_, ItemAbilities.AXE_WAX_OFF, false));
				if (optional2.isPresent()) {
					level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.levelEvent(player, 3004, pos, 0);
					return optional2;
				} else {
					return Optional.empty();
				}
			}
		}
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability);
	}
}
