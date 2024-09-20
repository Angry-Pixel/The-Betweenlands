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
import thebetweenlands.api.item.amphibious.ToggleableAmphibiousArmorUpgrade;
import thebetweenlands.common.item.misc.HoverTextItem;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.ArrayList;
import java.util.List;

public class UpgradeToggleItem extends HoverTextItem {

	public static final List<ToggleableAmphibiousArmorUpgrade> TOGGLE_LIST = new ArrayList<>();

	public UpgradeToggleItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		this.initToggleMap();
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			if (stack.has(DataComponentRegistry.SELECTED_UPGRADE)) {
				int index = TOGGLE_LIST.indexOf(stack.get(DataComponentRegistry.SELECTED_UPGRADE));
				if (index + 2 > TOGGLE_LIST.size()) {
					stack.set(DataComponentRegistry.SELECTED_UPGRADE, AmphibiousArmorUpgradeRegistry.NONE.get());
				} else {
					stack.set(DataComponentRegistry.SELECTED_UPGRADE, TOGGLE_LIST.get(index + 1));
				}
			} else {
				stack.set(DataComponentRegistry.SELECTED_UPGRADE, TOGGLE_LIST.getFirst());
			}
			ResourceLocation upgradeLoc = BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.getKey(stack.get(DataComponentRegistry.SELECTED_UPGRADE));
			player.displayClientMessage(Component.translatable("item.thebetweenlands.stone.selected", Component.translatable(Util.makeDescriptionId("amphibious_upgrade", upgradeLoc))), true);
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		} else {
			var upgrade = stack.getOrDefault(DataComponentRegistry.SELECTED_UPGRADE, AmphibiousArmorUpgradeRegistry.NONE.get());
			if (!(upgrade instanceof ToggleableAmphibiousArmorUpgrade toggle) || !TOGGLE_LIST.contains(toggle)) {
				return InteractionResultHolder.pass(stack);
			}

			for (ItemStack armorStack : player.getInventory().armor) {
				if (armorStack.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
					if (armorStack.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES).getAllUniqueUpgradesWithCounts().containsKey(BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.wrapAsHolder(upgrade))) {
						boolean result = toggle.onToggle(level, player, stack);
						ResourceLocation upgradeLoc = BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.getKey(upgrade);
						player.displayClientMessage(Component.translatable("item.thebetweenlands.biopathic_linkstone.toggle", Component.translatable(Util.makeDescriptionId("amphibious_upgrade", upgradeLoc)), String.valueOf(result)), true);
						return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
					}
				}
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
		if (TOGGLE_LIST.isEmpty()) {
			for (AmphibiousArmorUpgrade upgrade : BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES) {
				if (upgrade instanceof ToggleableAmphibiousArmorUpgrade toggle) {
					TOGGLE_LIST.add(toggle);
				}
			}
		}
	}
}
