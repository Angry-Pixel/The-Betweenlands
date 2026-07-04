package thebetweenlands.common.item.misc;

import com.google.common.collect.Lists;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.VolarkiteEntity;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;

public class VolarkiteItem extends Item {
	public VolarkiteItem(Properties properties) {
		super(properties);
		/*
		 * this.setCreativeTab(BLCreativeTabs.GEARS);
		 * 
		 * this.addPropertyOverride(new ResourceLocation("using"), (stack, worldIn,
		 * entityIn) -> { if(entityIn != null && (entityIn.getRidingEntity() instanceof
		 * EntityVolarkite || entityIn.getPassengers().stream().filter(e -> e instanceof
		 * EntityVolarkite).findAny().isPresent())) { return stack.getTagCompound() !=
		 * null && stack.getTagCompound().getBoolean("using_kite") ? 1 : 0; } return 0;
		 * });
		 */
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.getCooldowns().isOnCooldown(this))
			return InteractionResultHolder.pass(stack);

		if (!level.isClientSide()) {
			if (!player.isPassenger() && Lists.newArrayList(player.getIndirectPassengers()).stream().noneMatch(e -> e instanceof VolarkiteEntity)) {
				VolarkiteEntity entity = new VolarkiteEntity(EntityRegistry.VOLARKITE.get(), level);
				entity.setPos(player.getX(), player.getY() + player.getBbHeight(), player.getZ());
				entity.setYRot(player.getYRot());
				entity.setDeltaMovement(player.getDeltaMovement());
				entity.hasImpulse = true;
				level.addFreshEntity(entity);
				player.startRiding(entity);
				level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1, 1);

				if (stack.has(DataComponentRegistry.VOLARKITE_DATA)) {
					stack.set(DataComponentRegistry.VOLARKITE_DATA, true);
					player.getCooldowns().addCooldown(this, 20);
				}
			}
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		boolean isRidingKite = entity.getVehicle() instanceof VolarkiteEntity;

		if (entity instanceof LivingEntity && (isRidingKite || entity.getPassengers().stream().filter(e -> e instanceof VolarkiteEntity).findAny().isPresent())) {
			LivingEntity living = (LivingEntity) entity;

			boolean isMainHand = stack == living.getItemInHand(InteractionHand.MAIN_HAND);
			boolean isOffHand = stack == living.getItemInHand(InteractionHand.OFF_HAND);
			boolean hasOffHand = !living.getItemInHand(InteractionHand.OFF_HAND).isEmpty() && living.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof VolarkiteItem;
			if ((isMainHand || isOffHand) && ((isMainHand && !hasOffHand) || isOffHand)) {
				if (!level.isClientSide() && isRidingKite && entity.tickCount % 20 == 0) {
					stack.hurtAndBreak(1, (LivingEntity) entity, isMainHand && !hasOffHand ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
				}
				stack.set(DataComponentRegistry.VOLARKITE_DATA, true);
			} else if (stack.has(DataComponentRegistry.VOLARKITE_DATA))
				stack.set(DataComponentRegistry.VOLARKITE_DATA, false);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || slotChanged;
	}

	public boolean canRideKite(ItemStack stack, Entity entity) {
		return true;
	}
}
