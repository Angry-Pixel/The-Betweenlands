package thebetweenlands.common.item.shield;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.item.ShieldSpitData;
import thebetweenlands.common.entity.projectile.SapSpit;
import thebetweenlands.common.network.clientbound.LivingWeedwoodShieldSpitPacket;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class LivingWeedwoodShieldItem extends WeedwoodShieldItem {

	public LivingWeedwoodShieldItem(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if(entity instanceof LivingEntity living) {
			if(!level.isClientSide()) {
				boolean mainhand = living.getItemInHand(InteractionHand.MAIN_HAND) == stack;
				boolean offhand = living.getItemInHand(InteractionHand.OFF_HAND) == stack;
				if(living.isBlocking() && (mainhand || offhand)) {
					int spitCooldown = this.getSpitCooldown(stack);
					if(spitCooldown <= 0) {
						this.trySpit(stack, level, living, mainhand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
						this.setSpitCooldown(stack, 30 + level.getRandom().nextInt(30));
					} else {
						this.setSpitCooldown(stack, spitCooldown - 1);
					}

					if(level.getRandom().nextInt(60) == 0) {
						level.playSound(null, BlockPos.containing(entity.getEyePosition()), SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING.get(), SoundSource.PLAYERS, 0.35F, 1.4F);
					}
				}
			} else {
				int spitTicks = this.getSpitTicks(stack);
				if(spitTicks > 0) {
					this.setSpitTicks(stack, spitTicks - 1);
				}
			}
		}
	}

	@Override
	public void onShieldBreak(ItemStack stack, LivingEntity attacked, InteractionHand hand, DamageSource source) {
		super.onShieldBreak(stack, attacked, hand, source);

		if(stack.getOrDefault(DataComponentRegistry.BURN_TICKS, 0) == 0) {
			attacked.setItemInHand(hand, new ItemStack(ItemRegistry.SMALL_SPIRIT_TREE_FACE_MASK.get()));
		}
	}

	public void setSpitTicks(ItemStack stack, int ticks) {
		stack.set(DataComponentRegistry.SHIELD_SPIT, stack.getOrDefault(DataComponentRegistry.SHIELD_SPIT, ShieldSpitData.EMPTY).setSpitTicks(ticks));
	}

	public int getSpitTicks(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.SHIELD_SPIT, ShieldSpitData.EMPTY).ticks();
	}

	public void setSpitCooldown(ItemStack stack, int ticks) {
		stack.set(DataComponentRegistry.SHIELD_SPIT, stack.getOrDefault(DataComponentRegistry.SHIELD_SPIT, ShieldSpitData.EMPTY).setCooldown(ticks));
	}

	public int getSpitCooldown(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.SHIELD_SPIT, ShieldSpitData.EMPTY).cooldown();
	}

	protected boolean trySpit(ItemStack stack, Level level, LivingEntity owner, InteractionHand hand) {
		LivingEntity target = null;
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(8, 3, 8), EntitySelector.NO_CREATIVE_OR_SPECTATOR);

		Vec3 lookVec = owner.getLookAngle().normalize();

		for(LivingEntity e : entities) {
			if(Math.toDegrees(Math.acos(lookVec.dot(e.getEyePosition(1).subtract(owner.getEyePosition(1)).normalize()))) <= 70) {
				if(target == null || target.distanceTo(owner) > e.distanceTo(owner)) {
					target = e;
				}
			}
		}

		if(target != null) {
			float yaw = -(180 - owner.yBodyRot);
			Vec3 bodyForward = new Vec3(Mth.sin(-yaw * 0.017453292F - Mth.PI), 0, Mth.cos(-yaw * 0.017453292F - Mth.PI));
			Vec3 up = new Vec3(0, 1, 0);
			Vec3 right = bodyForward.cross(up);
			Vec3 offset = new Vec3(bodyForward.x, owner.getEyeHeight(), bodyForward.z).add(right.scale(hand == InteractionHand.MAIN_HAND ? 0.35D : -0.35D).add(0, lookVec.y * 0.5D - 0.4D, 0).add(bodyForward.scale(-0.1D)));

			SapSpit spit = new SapSpit(level, owner, 4.5F);
			spit.setPos(owner.getX() + owner.getDeltaMovement().x() + offset.x, owner.getY() + offset.y, owner.getZ() + owner.getDeltaMovement().z() + offset.z);

			double dx = target.getX() - spit.getX();
			double dy = target.getBoundingBox().minY + (double)(target.getBbHeight() / 3.0F) - spit.getY();
			double dz = target.getZ() - spit.getZ();
			double dist = Mth.sqrt((float) (dx * dx + dz * dz));
			spit.shoot(dx, dy + dist * 0.20000000298023224D, dz, 1, 1);

			level.addFreshEntity(spit);

			level.playSound(null, spit.blockPosition(), SoundRegistry.SPIRIT_TREE_FACE_SMALL_SPIT.get(), SoundSource.PLAYERS, 1, 1);

			PacketDistributor.sendToPlayersNear((ServerLevel) level, null, owner.getX(), owner.getY(), owner.getZ(), 64.0D, new LivingWeedwoodShieldSpitPacket(owner, hand == InteractionHand.MAIN_HAND, 15));

			return true;
		}

		return false;
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		return newStack.is(oldStack.getItem());
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !newStack.is(oldStack.getItem());
	}
}
