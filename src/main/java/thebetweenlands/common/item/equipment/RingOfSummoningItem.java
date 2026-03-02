package thebetweenlands.common.item.equipment;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BetweenlandsKeybinds;
import thebetweenlands.common.component.entity.RingOfSummoningEntityData;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.entity.monster.MummyArm;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class RingOfSummoningItem extends RingItem {
	public static final int MAX_USE_TIME = 100;
	public static final int USE_COOLDOWN = 120;
	public static final int MAX_ARMS = 32;

	public RingOfSummoningItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
		component.add(Component.translatable(("tooltip.bl.ring.summoning.bonus"), 0));
		if (flag.hasShiftDown()) {
			component.add(Component.translatable(("tooltip.bl.ring.summoning"), BetweenlandsKeybinds.RADIAL_MENU.getKey().getDisplayName(), BetweenlandsKeybinds.USE_RING.getDisplayName(), BetweenlandsKeybinds.USE_SECONDARY_RING.getDisplayName(), 1));
		} else {
			component.add(Component.translatable("tooltip.bl.press.shift"));
		}
	}

	@Override
	MutableComponent getUsageTooltip() {
		return Component.empty();
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, Container inventory) {
		if(!entity.level().isClientSide() && entity instanceof Player player) {
			RingOfSummoningEntityData cap = player.getData(AttachmentRegistry.RING_OF_SUMMONING_ENTITY_DATA);
			if (cap != null && stack.has(DataComponentRegistry.RING_ACTIVE)) {

				if (cap.getCooldownTicks() > 0) {
					cap.setCooldownTicks(player, cap.getCooldownTicks() - 1);
					stack.set(DataComponentRegistry.RING_ACTIVE, false);
				} else {
					if (cap.isActive()) {
						cap.setActiveTicks(player, cap.getActiveTicks() + 1);
						stack.set(DataComponentRegistry.RING_ACTIVE, true);

						if (cap.getActiveTicks() > MAX_USE_TIME) {
							cap.setActive(player, false);
							cap.setCooldownTicks(player, USE_COOLDOWN);
						} else {
							int arms = player.level().getEntitiesOfClass(MummyArm.class, player.getBoundingBox().inflate(18), e -> e.distanceTo(player) <= 18.0D).size();

							if (arms < MAX_ARMS) {
								List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16), e -> e instanceof LivingEntity && e.distanceTo(player) <= 16.0D && e != player && (e instanceof Mob));

								BlockPos targetPos = null;

								if (!targets.isEmpty()) {
									LivingEntity target = targets.get(player.level().getRandom().nextInt(targets.size()));
									boolean isAttacked = !player.level().getEntitiesOfClass(MummyArm.class, target.getBoundingBox()).isEmpty();
									if (!isAttacked) {
										targetPos = target.blockPosition();
									}
								}

								if (targetPos == null && player.level().getRandom().nextInt(3) == 0) {
									targetPos = player.blockPosition().offset(player.level().getRandom().nextInt(16) - 8, player.level().getRandom().nextInt(6) - 3, player.level().getRandom().nextInt(16) - 8);
									boolean isAttacked = !player.level().getEntitiesOfClass(MummyArm.class, new AABB(targetPos)).isEmpty();
									if (isAttacked) {
										targetPos = null;
									}
								}

								if (targetPos != null && player.level().getBlockState(targetPos.below()).isFaceSturdy(player.level(), targetPos.below(), Direction.UP)) {
									MummyArm arm = new MummyArm(player.level(), (Player) player);
									arm.setPos(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D);

									if (arm.level().noCollision(arm)) {
										this.drainPower(stack, player);
										player.level().addFreshEntity(arm);
									}
								}
							}
						}
					} else {
						stack.set(DataComponentRegistry.RING_ACTIVE, false);
					}
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, Container inventory) {
		stack.set(DataComponentRegistry.RING_ACTIVE, false);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(DataComponentRegistry.RING_ACTIVE) && stack.get(DataComponentRegistry.RING_ACTIVE);
	}

	public static boolean isRingActive(Entity entity) {
		EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
		Container inv = data.getContainer(entity, EquipmentInventoryType.RING);
		boolean hasRing = false;
		if(data != null) {
			for(int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);
				if(!stack.isEmpty() && stack.is(ItemRegistry.RING_OF_SUMMONING)/* && ((RingItem) stack.getItem()).canBeUsed(stack)*/) {
					hasRing = true;
					break;
				}
			}
		}
		return hasRing;
	}

	@Override
	public void onKeybindState(Player player, ItemStack stack, Container inventory, boolean active) {
		RingOfSummoningEntityData cap = player.getData(AttachmentRegistry.RING_OF_SUMMONING_ENTITY_DATA);
		if (cap != null) {
			if(!active && cap.isActive()) {
				cap.setActive(player, false);
				cap.setCooldownTicks(player, RingOfSummoningItem.USE_COOLDOWN);
			} else if(active && !cap.isActive() && cap.getCooldownTicks() <= 0 && RingOfSummoningItem.isRingActive(player)) {
				cap.setActive(player, true);
				cap.setActiveTicks(player, 0);
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.PEAT_MUMMY_CHARGE.get(), SoundSource.PLAYERS, 0.4F, (player.level().getRandom().nextFloat() * 0.4F + 0.8F) * 0.8F);
			}
		}
	}
}
