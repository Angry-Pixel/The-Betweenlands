package thebetweenlands.common.items.shield;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ToolMaterialRegistry;

public class WeedwoodShieldItem extends BaseShieldItem {
	public WeedwoodShieldItem(Properties properties) {
		super(ToolMaterialRegistry.WEEDWOOD, properties);
	}

	@Override
	public void onAttackBlocked(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		super.onAttackBlocked(stack, attacked, damage, source);
		if(!attacked.level().isClientSide() && source.getEntity() != null) {
			Entity attacker = source.getEntity();
			if((attacker.isOnFire() || attacker instanceof SmallFireball) && attacked.level().getRandom().nextFloat() < 0.5F) {
				stack.set(DataComponentRegistry.BURN_TICKS, 80);
			} else if(attacker instanceof LivingEntity living && attacked.level().getRandom().nextFloat() < 0.25F) {
				ItemStack activeItem = living.getUseItem();
				if(!activeItem.isEmpty()) {
					if(activeItem.getItem() instanceof TieredItem tier && tier.getTier() == ToolMaterialRegistry.OCTINE)
						stack.set(DataComponentRegistry.BURN_TICKS, 120);
				}
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if(!level.isClientSide()) {
			int burningTicks = stack.getOrDefault(DataComponentRegistry.BURN_TICKS, 0);
			if(burningTicks > 0) {
				stack.set(DataComponentRegistry.BURN_TICKS, burningTicks - 1);
				if(burningTicks % 5 == 0)
					level.playSound(null, entity.blockPosition().atY((int) entity.getEyeY()), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + level.getRandom().nextFloat(), level.getRandom().nextFloat() * 0.7F + 0.3F);
				if(burningTicks % 10 == 0 && level.getRandom().nextFloat() < 0.3F)
					entity.level().broadcastEntityEvent(entity, (byte)30);
				if(burningTicks % 3 == 0 && entity instanceof LivingEntity living)
					stack.hurtAndBreak(1, living, living.getEquipmentSlotForItem(stack));
				if(stack.getCount() <= 0 && entity instanceof LivingEntity living) {
					if(entity instanceof Player player) {
						player.getInventory().setItem(slotId, ItemStack.EMPTY);
						if(living.getOffhandItem() == stack)
							living.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		if(!entity.level().isClientSide()) {
			int burningTicks = stack.getOrDefault(DataComponentRegistry.BURN_TICKS, 0);
			if(burningTicks > 0) {
				stack.set(DataComponentRegistry.BURN_TICKS, burningTicks - 1);
				if(burningTicks % 5 == 0)
					entity.playSound(SoundEvents.FIRE_AMBIENT, 1.0F + entity.level().getRandom().nextFloat(), entity.level().getRandom().nextFloat() * 0.7F + 0.3F);
				if(burningTicks % 3 == 0) {
					stack.hurtAndBreak(1, ((ServerLevel)entity.level()), null, item -> {});
					if (stack.isEmpty()) {
						this.renderBrokenItemStack(entity.level(), entity.getX(), entity.getY() + entity.getBbHeight() / 2.0F, entity.getZ(), stack);
						entity.discard();
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasBurning = oldStack.getOrDefault(DataComponentRegistry.BURN_TICKS, 0) > 0;
		boolean isBurning =  newStack.getOrDefault(DataComponentRegistry.BURN_TICKS, 0) > 0;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isBurning || isBurning != wasBurning);
	}

	protected void renderBrokenItemStack(Level level, double x, double y, double z, ItemStack stack) {
		level.playSound(null, x, y, z, SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F);
		for (int i = 0; i < 5; ++i) {
			Vec3 motion = new Vec3((level.getRandom().nextDouble() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), x, y, z, motion.x, motion.y + 0.05D, motion.z);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		boolean isBurning = stack.getOrDefault(DataComponentRegistry.BURN_TICKS, 0) > 0;
		return isBurning ? new InteractionResultHolder<>(InteractionResult.PASS, stack) : super.use(level, player, hand);
	}
}
