package thebetweenlands.common.item.tool;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.component.item.ChirobarbErrupterData;
import thebetweenlands.common.entity.projectile.arrow.ChiromawBarb;
import thebetweenlands.common.entity.projectile.arrow.ChiromawShockBarb;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;


public class ChirobarbErupterItem extends Item {
	public final boolean electric;

	public ChirobarbErupterItem(Properties properties, boolean electric) {
		super(properties);
		this.electric = electric;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
		list.add(Component.translatable("tooltip.bl.chirobarb_erupter.usage"));
	}

	@Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if(!level.isClientSide()) {
			boolean shooting = !stack.has(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA) ? false : stack.get(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA).shooting();
			int rotation = 0 + (!stack.has(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA) ? 0 : stack.get(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA).rotation());

			if (shooting && entity instanceof LivingEntity) {
				stack.set(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA, new ChirobarbErrupterData(rotation  + 30, true));

				if (rotation  > 720) {
					stack.set(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA, ChirobarbErrupterData.DEFAULT);
				} else if (rotation % 30 == 0) {
					
					AbstractArrow arrow = this.electric ? new ChiromawShockBarb(EntityRegistry.CHIROMAW_SHOCK_BARB.get(), level) : new ChiromawBarb(EntityRegistry.CHIROMAW_BARB.get(), level);
					arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
					arrow.setBaseDamage(6D);

					double angle = Math.toRadians(entity.getYRot() + rotation - 30F);
					double dx = -Math.sin(angle);
					double dz = Math.cos(angle);
					double offsetX = dx * 1.5D;
					double offsetZ = dz * 1.5D;
					List<Entity> nearbyEntities = level.getEntities(entity, entity.getBoundingBox().inflate(12, 0, 12), e -> e instanceof LivingEntity && e instanceof Mob && Math.abs(e.getPersistentData().getInt("thebetweenlands.chirobarb_erupter.lastTargetted") - e.tickCount) >= 60);
					arrow.setPos(entity.getX() + offsetX, entity.getY() + entity.getBbHeight() * 0.75D, entity.getZ() + offsetZ);
					Entity closestNearby = null;
					double closestNearbyAngle = 0;
					double closestNearbyDstSq = Double.MAX_VALUE;

					for(Entity nearby : nearbyEntities) {
						Vec3 pos = nearby.position().add(0, nearby.getBbHeight()/ 2, 0);
						Vec3 diff = pos.subtract(entity.getEyePosition(1.0F));
						double dstSq = diff.lengthSqr();
						Vec3 dir = new Vec3(diff.x, 0, diff.z).normalize();
						double angleDiff = Math.acos(dir.x * dx + dir.z * dz);

						if(dstSq < closestNearbyDstSq && Math.abs(diff.y) < 2 && angleDiff <= Math.toRadians(15.0f)) {
							closestNearby = nearby;
							closestNearbyDstSq = dstSq;
							Vec3 trajectory = pos.subtract(arrow.position()).normalize();
							closestNearbyAngle = Math.toDegrees(Math.atan2(trajectory.z, trajectory.x)) - 90;
						}
					}

					float velocity = this.electric ? 1.4F : 1.1F;

					if(closestNearby != null) {
						closestNearby.getPersistentData().putInt("thebetweenlands.chirobarb_erupter.lastTargetted", closestNearby.tickCount);
						arrow.shootFromRotation(entity, 0F, (float)closestNearbyAngle, 1.5F, velocity, 0F);
					} else
						arrow.shootFromRotation(entity, 0F, entity.getYRot() + rotation - 30F, 1.5F, velocity, 0F);

					level.playSound(null, entity.blockPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE.get(), SoundSource.NEUTRAL, 0.25F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
					level.addFreshEntity(arrow);
				}
			}
		}
	}

	@Override
	 public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		boolean shooting = !stack.has(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA) ? false : stack.get(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA).shooting();

		if (!stack.has(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA))
			return InteractionResultHolder.pass(stack);

		if(player.getCooldowns().isOnCooldown(this))
			return InteractionResultHolder.pass(stack);

		if (!shooting) {
			if (!level.isClientSide()) {
				stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
				player.getCooldowns().addCooldown(this, 60);
				stack.set(DataComponentRegistry.CHIROBARB_ERRUPTER_DATA, new ChirobarbErrupterData(0, true));
				level.playSound(null, player.blockPosition(), SoundRegistry.CHIROBARB_ERUPTER.get(), SoundSource.NEUTRAL, 1F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
			}
			player.swing(usedHand);
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	   public boolean isFoil(ItemStack stack) {
		return this.electric;
	}
}
