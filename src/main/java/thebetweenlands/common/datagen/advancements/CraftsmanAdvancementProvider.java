package thebetweenlands.common.datagen.advancements;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.*;

import java.util.Optional;
import java.util.function.Consumer;

public class CraftsmanAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(ItemRegistry.DRY_BARK,
				Component.translatable("advancement.thebetweenlands.craftsman.root"),
				Component.translatable("advancement.thebetweenlands.craftsman.root.desc"),
				TheBetweenlands.prefix("textures/block/weedwood.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_base_root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(TheBetweenlands.prefix("adventurer/root"), true).build())).build())))))
			.save(consumer, "thebetweenlands:craftsman/root");

		var wood = Advancement.Builder.advancement().parent(root).display(BlockRegistry.WEEDWOOD_LOG,
				Component.translatable("advancement.thebetweenlands.craftsman.getting_harder_wood"),
				Component.translatable("advancement.thebetweenlands.craftsman.getting_harder_wood.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("has_bl_log", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BLItemTagProvider.BL_LOGS)))
			.save(consumer, "thebetweenlands:craftsman/getting_harder_wood");

		var table = Advancement.Builder.advancement().parent(wood).display(BlockRegistry.WEEDWOOD_CRAFTING_TABLE,
				Component.translatable("advancement.thebetweenlands.craftsman.all_over_again"),
				Component.translatable("advancement.thebetweenlands.craftsman.all_over_again.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("workbench", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WEEDWOOD_CRAFTING_TABLE))
			.save(consumer, "thebetweenlands:craftsman/all_over_again");

		var eff = Advancement.Builder.advancement().parent(table).display(ItemRegistry.SULFUR,
				Component.translatable("advancement.thebetweenlands.craftsman.efficiency"),
				Component.translatable("advancement.thebetweenlands.craftsman.efficiency.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("furnace", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.DUAL_SULFUR_FURNACE))
			.save(consumer, "thebetweenlands:craftsman/efficiency");

		Advancement.Builder.advancement().parent(eff).display(ItemRegistry.LIMESTONE_FLUX,
				Component.translatable("advancement.thebetweenlands.craftsman.freebies"),
				Component.translatable("advancement.thebetweenlands.craftsman.freebies.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("add_flux", AdvancementCriteriaRegistry.FLUX_ADDED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/freebies");

		Advancement.Builder.advancement().parent(table).display(BlockRegistry.WIND_CHIME,
				Component.translatable("advancement.thebetweenlands.craftsman.in_the_air_tonight"),
				Component.translatable("advancement.thebetweenlands.craftsman.in_the_air_tonight.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("prediction", AdvancementCriteriaRegistry.WIND_CHIME_PREDICTION.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/in_the_air_tonight");

		var station = Advancement.Builder.advancement().parent(table).display(BlockRegistry.WATER_FILTER,
				Component.translatable("advancement.thebetweenlands.craftsman.percolation_station"),
				Component.translatable("advancement.thebetweenlands.craftsman.percolation_station.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("filter", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WATER_FILTER))
			.addCriterion("clean_water", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemRegistry.WEEDWOOD_BUCKET, ItemRegistry.SYRMORITE_BUCKET).hasComponents(DataComponentPredicate.builder().expect(DataComponentRegistry.STORED_FLUID.get(), SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.CLEAN_WATER_STILL, FluidType.BUCKET_VOLUME))).build())))
			.requirements(AdvancementRequirements.Strategy.AND)
			.save(consumer, "thebetweenlands:craftsman/percolation_station");

		var oil = Advancement.Builder.advancement().parent(station).display(new ItemStack(ItemRegistry.WEEDWOOD_BUCKET, 1, DataComponentPatch.builder().set(DataComponentRegistry.STORED_FLUID.get(), SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build()),
				Component.translatable("advancement.thebetweenlands.craftsman.omega_3s"),
				Component.translatable("advancement.thebetweenlands.craftsman.omega_3s.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("fish_oil", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemRegistry.WEEDWOOD_BUCKET, ItemRegistry.SYRMORITE_BUCKET).hasComponents(DataComponentPredicate.builder().expect(DataComponentRegistry.STORED_FLUID.get(), SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build())))
			.save(consumer, "thebetweenlands:craftsman/omega_3s");

		Advancement.Builder.advancement().parent(oil).display(BlockRegistry.TREATED_WEEDWOOD_PLANKS,
				Component.translatable("advancement.thebetweenlands.craftsman.ill_treat_you_right"),
				Component.translatable("advancement.thebetweenlands.craftsman.ill_treat_you_right.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("treated_weedwood", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.TREATED_WEEDWOOD_PLANKS))
			.addCriterion("treated_rubber", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.TREATED_RUBBER_TREE_PLANKS))
			.addCriterion("treated_root", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.TREATED_GIANT_ROOT_PLANKS))
			.addCriterion("treated_nibbletwig", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.TREATED_NIBBLETWIG_PLANKS))
			.addCriterion("treated_hearthgrove", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.TREATED_HEARTHGROVE_PLANKS))
			.addCriterion("treated_rotten", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.TREATED_ROTTEN_PLANKS))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:craftsman/ill_treat_you_right");

		var dye = Advancement.Builder.advancement().parent(oil).display(BlockRegistry.DULL_LAVENDER_SAMITE,
				Component.translatable("advancement.thebetweenlands.craftsman.paint_job"),
				Component.translatable("advancement.thebetweenlands.craftsman.paint_job.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("glass", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BLItemTagProvider.FILTERED_SILT_GLASS)))
			.addCriterion("shingles", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BLItemTagProvider.MUD_BRICK_SHINGLES)))
			.addCriterion("mat", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BLItemTagProvider.REED_MATS)))
			.addCriterion("samite", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BLItemTagProvider.SAMITE)))
			.addCriterion("frame", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BLItemTagProvider.ITEM_FRAMES)))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:craftsman/paint_job");

		Advancement.Builder.advancement().parent(dye).display(BlockRegistry.GLOWING_GOOP,
				Component.translatable("advancement.thebetweenlands.craftsman.let_it_shine"),
				Component.translatable("advancement.thebetweenlands.craftsman.let_it_shine.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("glowing", AdvancementCriteriaRegistry.ITEM_FRAME_GLOWING.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/let_it_shine");

		Advancement.Builder.advancement().parent(dye).display(ItemRegistry.CAMELOT_MAGENTA_DYE,
				Component.translatable("advancement.thebetweenlands.craftsman.dye_hard"),
				Component.translatable("advancement.thebetweenlands.craftsman.dye_hard.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("lavender", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.DULL_LAVENDER_DYE))
			.addCriterion("maroon", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.MAROON_DYE))
			.addCriterion("shadow", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SHADOW_GREEN_DYE))
			.addCriterion("magenta", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.CAMELOT_MAGENTA_DYE))
			.addCriterion("saffron", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SAFFRON_DYE))
			.addCriterion("caribbean", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.CARIBBEAN_GREEN_DYE))
			.addCriterion("tangerine", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.VIVID_TANGERINE_DYE))
			.addCriterion("champagne", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.CHAMPAGNE_DYE))
			.addCriterion("black", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RAISIN_BLACK_DYE))
			.addCriterion("sushi", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SUSHI_GREEN_DYE))
			.addCriterion("cyan", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.ELM_CYAN_DYE))
			.addCriterion("cadmium", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.CADMIUM_GREEN_DYE))
			.addCriterion("blue", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.LAVENDER_BLUE_DYE))
			.addCriterion("brown", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.BROWN_RUST_DYE))
			.addCriterion("purple", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.MIDNIGHT_PURPLE_DYE))
			.addCriterion("gray", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.PEWTER_GREY_DYE))
			.save(consumer, "thebetweenlands:craftsman/dye_hard");

		var rod = Advancement.Builder.advancement().parent(table).display(ItemRegistry.WEEDWOOD_FISHING_ROD,
				Component.translatable("advancement.thebetweenlands.craftsman.teach_a_man_to_fish"),
				Component.translatable("advancement.thebetweenlands.craftsman.teach_a_man_to_fish.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("rod", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WEEDWOOD_FISHING_ROD))
			.save(consumer, "thebetweenlands:craftsman/teach_a_man_to_fish");

		var jar = Advancement.Builder.advancement().parent(rod).display(BlockRegistry.SILT_GLASS_JAR,
				Component.translatable("advancement.thebetweenlands.craftsman.jar_o_worms"),
				Component.translatable("advancement.thebetweenlands.craftsman.jar_o_worms.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("jar", AdvancementCriteriaRegistry.USED_ROD_ON_JAR.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/jar_o_worms");

		Advancement.Builder.advancement().parent(jar).display(BlockRegistry.FISHING_TACKLE_BOX,
				Component.translatable("advancement.thebetweenlands.craftsman.net_fish_and_chill"),
				Component.translatable("advancement.thebetweenlands.craftsman.net_fish_and_chill.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("sit_on_box", AdvancementCriteriaRegistry.SIT_ON_TACKLE_BOX.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/net_fish_and_chill");

		var anadia = Advancement.Builder.advancement().parent(rod).display(ItemRegistry.ANADIA,
				Component.translatable("advancement.thebetweenlands.craftsman.fishy_business"),
				Component.translatable("advancement.thebetweenlands.craftsman.fishy_business.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("anadia", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.ANADIA))
			.save(consumer, "thebetweenlands:craftsman/fishy_business");

		var trimming = Advancement.Builder.advancement().parent(anadia).display(BlockRegistry.FISH_TRIMMING_TABLE,
				Component.translatable("advancement.thebetweenlands.craftsman.fish_guts"),
				Component.translatable("advancement.thebetweenlands.craftsman.fish_guts.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("trim_fish", AdvancementCriteriaRegistry.TRIM_FISH.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/fish_guts");

		Advancement.Builder.advancement().parent(trimming).display(ItemRegistry.AMPHIBIOUS_LEGGINGS,
				Component.translatable("advancement.thebetweenlands.craftsman.aquaman"),
				Component.translatable("advancement.thebetweenlands.craftsman.aquaman.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("helmet", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.AMPHIBIOUS_HELMET))
			.addCriterion("chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.AMPHIBIOUS_CHESTPLATE))
			.addCriterion("leggings", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.AMPHIBIOUS_LEGGINGS))
			.addCriterion("boots", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.AMPHIBIOUS_BOOTS))
			.save(consumer, "thebetweenlands:craftsman/aquaman");

		Advancement.Builder.advancement().parent(trimming).display(BlockRegistry.SMOKING_RACK,
				Component.translatable("advancement.thebetweenlands.craftsman.authentic_flavor"),
				Component.translatable("advancement.thebetweenlands.craftsman.authentic_flavor.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("smoke_item", AdvancementCriteriaRegistry.SMOKE_ITEM.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/authentic_flavor");

		var pot = Advancement.Builder.advancement().parent(trimming).display(BlockRegistry.CRAB_POT,
				Component.translatable("advancement.thebetweenlands.craftsman.its_a_trap"),
				Component.translatable("advancement.thebetweenlands.craftsman.its_a_trap.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("catch_crab", AdvancementCriteriaRegistry.CRAB_POT.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/its_a_trap");

		Advancement.Builder.advancement().parent(pot).display(BlockRegistry.CRAB_POT_FILTER,
				Component.translatable("advancement.thebetweenlands.craftsman.natures_purifier"),
				Component.translatable("advancement.thebetweenlands.craftsman.natures_purifier.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("filter_item", AdvancementCriteriaRegistry.CRAB_FILTER.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:craftsman/natures_purifier");
	}
}
