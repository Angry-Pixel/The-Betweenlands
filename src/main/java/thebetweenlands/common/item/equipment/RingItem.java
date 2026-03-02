package thebetweenlands.common.item.equipment;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.client.BetweenlandsKeybinds;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;

public abstract class RingItem extends Item implements RadialMenuEquippable {
	public RingItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
		component.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
		if (flag.hasShiftDown()) {
			component.add(Component.translatable("item.thebetweenlands.ring.equip", BetweenlandsKeybinds.RADIAL_MENU.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
			if (!this.getUsageTooltip().getString().isBlank()) {
				component.add(this.getUsageTooltip().withStyle(ChatFormatting.GRAY));
			}
		} else {
			component.add(Component.translatable("item.thebetweenlands.hold_shift").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		}
	}

	abstract MutableComponent getUsageTooltip();

	public boolean canBeUsed(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage();
	}

	protected float getXPConversionRate(ItemStack stack, Player player) {
		//1 xp = 5 damage repaired
		return 5.0F;
	}

	public void drainPower(ItemStack stack, Entity entity) {
		if (stack.getDamageValue() < stack.getMaxDamage() && stack.getItem() instanceof RingItem ring && ring.canBeUsed(stack)) {
			stack.setDamageValue(stack.getDamageValue() + 1);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isSecondaryUseActive()) {
			if (stack.getDamageValue() > 0 && (player.totalExperience > 0 || player.experienceLevel > 0 || player.experienceProgress > 0)) {
				if (!level.isClientSide()) {
					int repairPerClick = 40;
					float conversion = this.getXPConversionRate(stack, player);
					float requiredRepair = Math.min(repairPerClick, stack.getDamageValue() / conversion);
					stack.setDamageValue(Math.max(0, stack.getDamageValue() - Mth.ceil(Mth.abs(removeXp(player, Mth.ceil(requiredRepair))) * conversion)));
				}

				return InteractionResultHolder.success(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	public static int removeXp(Player player, int amount) {
		int change = amount;

		float playerXp = player.experienceProgress * (float) player.getXpNeededForNextLevel();
		player.experienceProgress -= (float) amount / (float) player.getXpNeededForNextLevel();
		player.totalExperience = Mth.clamp(player.totalExperience - amount, 0, Integer.MAX_VALUE);

		while (player.experienceProgress < 0) {
			float xp = player.experienceProgress * (float) player.getXpNeededForNextLevel();

			if (player.experienceLevel > 0) {
				player.giveExperienceLevels(-1);
				player.experienceProgress = 1.0F + xp / (float) player.getXpNeededForNextLevel();
				playerXp += (float) player.getXpNeededForNextLevel();
			} else {
				player.giveExperienceLevels(-1);
				change = Mth.abs(Math.round(playerXp));
				player.experienceProgress = 0.0F;
			}
		}

		return change;
	}

	@Override
	public EquipmentInventoryType getEquipmentCategory(ItemStack stack) {
		return EquipmentInventoryType.RING;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, Player player, Entity target) {
		return stack.getDamageValue() == 0 || player.totalExperience == 0 || player.experienceProgress == 0 || player.experienceLevel == 0 || player.isSecondaryUseActive();
	}

	@Override
	public boolean canEquip(ItemStack stack, Player player, Entity target) {
		return player == target;
	}

	@Override
	public boolean canUnequip(ItemStack stack, Player player, Entity target, Container inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, Container inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, Container inventory) {
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, Container inventory) {
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, Container inventory) {
		if (entity.tickCount % 20 == 0) {
			this.drainPower(stack, entity);
		}
	}

	/**
	 * Called when the ring use keybind is pressed
	 *
	 * @param player
	 * @param stack
	 * @param inventory
	 * @param active    Whether the key is pressed or not
	 */
	public abstract void onKeybindState(Player player, ItemStack stack, Container inventory, boolean active);
}
