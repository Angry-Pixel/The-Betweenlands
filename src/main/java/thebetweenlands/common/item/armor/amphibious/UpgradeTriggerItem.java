package thebetweenlands.common.item.armor.amphibious;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TriggerableAmphibiousArmorUpgrade;
import thebetweenlands.common.item.misc.HoverTextItem;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.ArrayList;
import java.util.List;

public class UpgradeTriggerItem extends HoverTextItem {

	public static final List<TriggerableAmphibiousArmorUpgrade> TRIGGER_LIST = new ArrayList<>();

	public UpgradeTriggerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		this.initToggleMap();
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			if (stack.has(DataComponentRegistry.SELECTED_UPGRADE)) {
				int index = TRIGGER_LIST.indexOf(stack.get(DataComponentRegistry.SELECTED_UPGRADE));
				if (index + 2 > TRIGGER_LIST.size()) {
					stack.set(DataComponentRegistry.SELECTED_UPGRADE, AmphibiousArmorUpgradeRegistry.NONE.get());
				} else {
					stack.set(DataComponentRegistry.SELECTED_UPGRADE, TRIGGER_LIST.get(index + 1));
				}
			} else {
				stack.set(DataComponentRegistry.SELECTED_UPGRADE, TRIGGER_LIST.getFirst());
			}
			ResourceLocation upgradeLoc = BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.getKey(stack.get(DataComponentRegistry.SELECTED_UPGRADE));
			player.displayClientMessage(Component.translatable("item.thebetweenlands.stone.selected", Component.translatable(Util.makeDescriptionId("amphibious_upgrade", upgradeLoc))), true);
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		} else {
			var upgrade = stack.getOrDefault(DataComponentRegistry.SELECTED_UPGRADE, AmphibiousArmorUpgradeRegistry.NONE.get());
			if (!(upgrade instanceof TriggerableAmphibiousArmorUpgrade trigger) || !TRIGGER_LIST.contains(trigger)) {
				return InteractionResultHolder.pass(stack);
			}
			boolean anyTriggered = false;
			for (ItemStack armorStack : player.getInventory().armor) {
				if (armorStack.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
					int amount = armorStack.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES).getAllUniqueUpgradesWithCounts().getOrDefault(BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.wrapAsHolder(upgrade), 0);
					if (amount > 0) {
						anyTriggered |= trigger.onTrigger(level, player, stack, armorStack, 0);
					}
				}
			}
			if (anyTriggered) {
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
			}
		}
		return super.use(level, player, hand);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("item.thebetweenlands.stone.select").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(this.getDescriptionId() + ".activate").withStyle(ChatFormatting.GRAY));
		ResourceLocation upgradeLoc = BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.getKey(stack.getOrDefault(DataComponentRegistry.SELECTED_UPGRADE, AmphibiousArmorUpgradeRegistry.NONE.get()));
		tooltip.add(Component.translatable("item.thebetweenlands.stone.selected", Component.translatable(Util.makeDescriptionId("amphibious_upgrade", upgradeLoc))).withStyle(ChatFormatting.GRAY));
	}

	private void initToggleMap() {
		if (TRIGGER_LIST.isEmpty()) {
			for (AmphibiousArmorUpgrade upgrade : BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES) {
				if (upgrade instanceof TriggerableAmphibiousArmorUpgrade trigger) {
					TRIGGER_LIST.add(trigger);
				}
			}
		}
	}
}
