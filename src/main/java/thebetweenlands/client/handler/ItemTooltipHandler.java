package thebetweenlands.client.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.CompostData;
import thebetweenlands.common.component.item.UpgradeDamage;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.item.armor.amphibious.ArmorEffectHelper;
import thebetweenlands.common.registries.*;
import thebetweenlands.util.FoodSickness;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemTooltipHandler {
	public static final DecimalFormat COMPOST_AMOUNT_FORMAT = new DecimalFormat("#.##");

	static {
		COMPOST_AMOUNT_FORMAT.setRoundingMode(RoundingMode.CEILING);
	}

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ItemTooltipHandler::addTooltips);
	}

	private static void addTooltips(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<Component> toolTip = event.getToolTip();
		Player player = event.getEntity();

		if (CorrosionHelper.isCorrodible(stack)) {
			CorrosionHelper.addCorrosionTooltips(stack, event.getToolTip(), event.getFlags(), event.getContext());
		}

		UpgradeDamage damage = stack.getOrDefault(DataComponentRegistry.UPGRADE_DAMAGE, UpgradeDamage.EMPTY);
		if (damage.damage() > 0) {
			toolTip.add(Component.translatable("item.thebetweenlands.amphibious_upgrade.damaged", Math.max(0, damage.maxDamage() - damage.damage()), damage.maxDamage()));
		}

		CircleGemType circleGem = stack.getOrDefault(DataComponentRegistry.CIRCLE_GEM, CircleGemType.NONE);
		if (circleGem != CircleGemType.NONE) {
			toolTip.add(1, Component.translatable("item.thebetweenlands.circle_gem." + circleGem.name).withStyle(circleGem.color));
		}

		if (stack.getItem().builtInRegistryHolder().getData(DataMapRegistry.DECAY_FOOD) != null) {
			toolTip.add(Component.translatable("item.thebetweenlands.decay_food").withStyle(ChatFormatting.GRAY));
		}

		if (player != null) {
			if (FoodSicknessHandler.isFoodSicknessEnabled(player.level()) && stack.has(DataComponents.FOOD) && stack.is(BLItemTagProvider.GIVES_FOOD_SICKNESS)) {
				FoodSicknessData data = player.getData(AttachmentRegistry.FOOD_SICKNESS);
				FoodSickness sickness = data.getSickness(stack.getItem());
				int hatred = data.getFoodHatred(stack.getItem());
				getSicknessTooltip(sickness, hatred, event.getFlags().isAdvanced(), toolTip);
			}

//			if (stack.getItem() instanceof IEquippable equippable && equippable.canEquip(stack, player, player)) {
//				toolTip.add(Component.translatable("item.thebetweenlands.equippable").withStyle(ChatFormatting.GRAY));
//			}
		}

		if (BetweenlandsConfig.itemUsageTooltip) {
			List<Component> usedInMachines = new ArrayList<>();

			if (player != null) {
				AspectManager aspectManager = AspectManager.get(player.level());

				if (aspectManager != null) {
					if (!aspectManager.getStaticAspects(stack).isEmpty()) {
						usedInMachines.add(Component.translatable("item.thebetweenlands.static_aspects").withStyle(ChatFormatting.GRAY));
					}

					if (stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).aspect().isPresent()) {
						usedInMachines.add(Component.translatable("item.thebetweenlands.aspects").withStyle(ChatFormatting.GRAY));
					}
				}

				if (!player.level().getRecipeManager().getRecipesFor(RecipeRegistry.MORTAR_RECIPE.get(), new SingleRecipeInput(stack), player.level()).isEmpty()) {
					usedInMachines.add(Component.translatable("item.thebetweenlands.mortar_recipe").withStyle(ChatFormatting.GRAY));
				}

				if (!player.level().getRecipeManager().getRecipesFor(RecipeRegistry.ANIMATOR_RECIPE.get(), new SingleRecipeInput(stack), player.level()).isEmpty()) {
					usedInMachines.add(Component.translatable("item.thebetweenlands.animator_recipe").withStyle(ChatFormatting.GRAY));
				}

				IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
				if (CenserRecipeRegistry.getRecipe(stack) != null || (fluidHandler != null && CenserRecipeRegistry.getRecipe(fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)) != null)) {
					usedInMachines.add(Component.translatable("item.thebetweenlands.censer_primary").withStyle(ChatFormatting.GRAY));
				}
				if (CenserRecipeRegistry.getRecipeWithSecondaryInput(stack) != null) {
					usedInMachines.add(Component.translatable("item.thebetweenlands.censer_secondary").withStyle(ChatFormatting.GRAY));
				}

				if (!player.level().getRecipeManager().getRecipesFor(RecipeRegistry.PURIFIER_RECIPE.get(), new SingleRecipeInput(stack), player.level()).isEmpty()) {
					usedInMachines.add(Component.translatable("item.thebetweenlands.purifier_recipe").withStyle(ChatFormatting.GRAY));
				}
			}

			List<Component> amphibiousUpgrades = new ArrayList<>();

			if (ArmorEffectHelper.getUpgrade(EquipmentSlot.HEAD, stack) != AmphibiousArmorUpgradeRegistry.NONE) {
				amphibiousUpgrades.add(Component.translatable("item.thebetweenlands.amphibious_upgrade.helmet").withStyle(ChatFormatting.GRAY));
			}

			if (ArmorEffectHelper.getUpgrade(EquipmentSlot.CHEST, stack) != AmphibiousArmorUpgradeRegistry.NONE) {
				amphibiousUpgrades.add(Component.translatable("item.thebetweenlands.amphibious_upgrade.chestplate").withStyle(ChatFormatting.GRAY));
			}

			if (ArmorEffectHelper.getUpgrade(EquipmentSlot.LEGS, stack) != AmphibiousArmorUpgradeRegistry.NONE) {
				amphibiousUpgrades.add(Component.translatable("item.thebetweenlands.amphibious_upgrade.leggings").withStyle(ChatFormatting.GRAY));
			}

			if (ArmorEffectHelper.getUpgrade(EquipmentSlot.FEET, stack) != AmphibiousArmorUpgradeRegistry.NONE) {
				amphibiousUpgrades.add(Component.translatable("item.thebetweenlands.amphibious_upgrade.boots").withStyle(ChatFormatting.GRAY));
			}

			if (!amphibiousUpgrades.isEmpty()) {
				usedInMachines.add(Component.translatable("item.thebetweenlands.amphibious_upgrade.format", amphibiousUpgrades.stream().map(Component::getString).collect(Collectors.joining("/"))).withStyle(ChatFormatting.GRAY));
			}

			if (stack.has(DataComponentRegistry.COMPOST)) {
				CompostData data = stack.get(DataComponentRegistry.COMPOST);
				String debug = "";
				if (event.getFlags().isAdvanced()) {
					debug = " (T: " + COMPOST_AMOUNT_FORMAT.format(data.compostTime() / 20.0F) + "s A: " + data.compostLevel() + ")";
				}
				usedInMachines.add(Component.translatable("item.thebetweenlands.compost_recipe").append(debug).withStyle(ChatFormatting.GRAY));
			}

			if (stack.is(ItemRegistry.SPIRIT_FRUIT) || stack.is(ItemRegistry.BONE_WAYFINDER)) {
				usedInMachines.add(Component.translatable("item.thebetweenlands.offering_table_recipe").withStyle(ChatFormatting.GRAY));
			}

			if (stack.is(ItemRegistry.MOSS_FILTER) || stack.is(ItemRegistry.SILK_FILTER)) {
				usedInMachines.add(Component.translatable("item.thebetweenlands.water_filter_recipe").withStyle(ChatFormatting.GRAY));
			}

			if (stack.is(ItemRegistry.SILK_BUNDLE)) {
				usedInMachines.add(Component.translatable("item.thebetweenlands.steeping_pot_recipe").withStyle(ChatFormatting.GRAY));
			}

			if (RecipeRegistry.doesSteepingPotUseItem(event.getContext().level(), stack)) {
				usedInMachines.add(Component.translatable("item.thebetweenlands.silk_bundle_recipe"));
				usedInMachines.add(Component.translatable("item.thebetweenlands.steeping_pot_recipe"));
			}

			if (!usedInMachines.isEmpty()) {
				toolTip.add(Component.translatable("item.thebetweenlands.used_in_recipes", usedInMachines.stream().map(Component::getString).collect(Collectors.joining(", "))).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	private static void getSicknessTooltip(FoodSickness sickness, int hatred, boolean advancedTooltips, List<Component> toolTip) {
		String debug = "";
		if(advancedTooltips) {
			debug = " (" + hatred + "/" + sickness.maxHatred + ")";
		}
		toolTip.add(Component.translatable("item.thebetweenlands.food_sickness.state." + sickness.name().toLowerCase()).append(debug).withStyle(ChatFormatting.GRAY));
	}
}
