package thebetweenlands.common.item.misc;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.item.equipment.AmuletItem;
import thebetweenlands.common.registries.AttachmentRegistry;

public class AmuletSlotItem extends Item {

	public AmuletSlotItem(Properties properties) {
		super(properties);
	}

	@Override
	public float getXpRepairRatio(ItemStack stack) {
		return 0.0F;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return !enchantment.is(Enchantments.MENDING) && super.supportsEnchantment(stack, enchantment);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		boolean success = false;
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			if (player.isSecondaryUseActive() && player.isCreative()) {
				success = removeAmuletSlot(player);
			} else {
				success = addAmuletSlot(player, stack, player, hand);
			}
		}
		return success ? InteractionResultHolder.success(stack) : super.use(level, player, hand);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if (!player.level().isClientSide()) {
			if (AmuletItem.canPlayerAddAmulet(player, target) || player.isCreative()) {
				if (player.isSecondaryUseActive() && player.isCreative()) {
					if (removeAmuletSlot(target)) {
						return InteractionResult.SUCCESS;
					}
				} else {
					if (addAmuletSlot(player, stack, target, hand)) {
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return super.interactLivingEntity(stack, player, target, hand);
	}

	public static boolean addAmuletSlot(Player player, ItemStack stack, LivingEntity entity, InteractionHand hand) {
		EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
		if (data.getAmuletSlots() < data.getMazSizeForType(EquipmentInventoryType.AMULET)) {
			data.setAmuletSlots(data.getAmuletSlots() + 1);

			if (!player.isCreative()) {
				if (entity instanceof Player) {
					stack.hurtAndBreak(5, player, LivingEntity.getSlotForHand(hand));
				} else {
					stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(hand));
				}
			}
			player.displayClientMessage(Component.translatable("equipment.thebetweenlands.slot_added"), true);
			return true;
		}
		player.displayClientMessage(Component.translatable("equipment.thebetweenlands.slots_full"), true);
		return false;
	}

	public static boolean removeAmuletSlot(LivingEntity entity) {
		EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
		if (data.getAmuletSlots() > 1) {
			data.setAmuletSlots(data.getAmuletSlots() - 1);
			return true;
		}
		return false;
	}
}
