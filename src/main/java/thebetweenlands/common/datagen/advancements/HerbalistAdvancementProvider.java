package thebetweenlands.common.datagen.advancements;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.GeckoTrigger;
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.*;

import java.util.Optional;
import java.util.function.Consumer;

public class HerbalistAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(ItemRegistry.SICKLE,
				Component.translatable("advancement.thebetweenlands.herbalist.root"),
				Component.translatable("advancement.thebetweenlands.herbalist.root.desc"),
				TheBetweenlands.prefix("textures/block/green_dentrothyst.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_base_root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(TheBetweenlands.prefix("adventurer/dank_world"), true).build())).build())))))
			.save(consumer, "thebetweenlands:herbalist/root");

		var druid = Advancement.Builder.advancement().parent(root).display(ItemRegistry.SICKLE,
				Component.translatable("advancement.thebetweenlands.herbalist.im_a_druid"),
				Component.translatable("advancement.thebetweenlands.herbalist.im_a_druid.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("use_sickle", AdvancementCriteriaRegistry.SICKLE_USE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:herbalist/im_a_druid");

		var bookworm = Advancement.Builder.advancement().parent(druid).display(ItemRegistry.AMATE_PAPER,
				Component.translatable("advancement.thebetweenlands.herbalist.bookworms"),
				Component.translatable("advancement.thebetweenlands.herbalist.bookworms.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("get_book", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.HERBLORE_BOOK))
			.save(consumer, "thebetweenlands:herbalist/bookworms");

		Advancement.Builder.advancement().parent(bookworm).display(AspectContents.createItemStack(ItemRegistry.GREEN_ASPECT_VIAL.get(), registries.holderOrThrow(AspectTypeRegistry.FREIWYNN), 2),
				Component.translatable("advancement.thebetweenlands.herbalist.you_cant_see_me"),
				Component.translatable("advancement.thebetweenlands.herbalist.you_cant_see_me.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("invis_frame", AdvancementCriteriaRegistry.ITEM_FRAME_INVISIBLE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:herbalist/you_cant_see_me");

		Advancement.Builder.advancement().parent(bookworm).display(ItemRegistry.HERBLORE_BOOK,
				Component.translatable("advancement.thebetweenlands.herbalist.fully_booked"),
				Component.translatable("advancement.thebetweenlands.herbalist.fully_booked.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("invis_frame", AdvancementCriteriaRegistry.HERBLORE_FIND_ALL.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:herbalist/fully_booked");

		var peta = Advancement.Builder.advancement().parent(druid).display(ItemRegistry.GECKO,
				Component.translatable("advancement.thebetweenlands.herbalist.screw_peta"),
				Component.translatable("advancement.thebetweenlands.herbalist.screw_peta.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("gecko_test", AdvancementCriteriaRegistry.GECKO.get().createCriterion(new GeckoTrigger.TriggerInstance(Optional.empty(), true, false)))
			.save(consumer, "thebetweenlands:herbalist/screw_peta");

		Advancement.Builder.advancement().parent(peta).display(BlockRegistry.GECKO_CAGE,
				Component.translatable("advancement.thebetweenlands.herbalist.a_kind_heart"),
				Component.translatable("advancement.thebetweenlands.herbalist.a_kind_heart.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("gecko_release", AdvancementCriteriaRegistry.GECKO.get().createCriterion(new GeckoTrigger.TriggerInstance(Optional.empty(), false, true)))
			.save(consumer, "thebetweenlands:herbalist/a_kind_heart");

		var pot = Advancement.Builder.advancement().parent(root).display(BlockRegistry.STEEPING_POT,
				Component.translatable("advancement.thebetweenlands.herbalist.steep_learning_curve"),
				Component.translatable("advancement.thebetweenlands.herbalist.steep_learning_curve.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("place_pot", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(BlockRegistry.STEEPING_POT.get()))
			.save(consumer, "thebetweenlands:herbalist/steep_learning_curve");

		var tea = Advancement.Builder.advancement().parent(pot).display(ItemRegistry.SILK_BUNDLE,
				Component.translatable("advancement.thebetweenlands.herbalist.tea_bagging"),
				Component.translatable("advancement.thebetweenlands.herbalist.tea_bagging.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("nettle_tea", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.NETTLE_TEA))
			.addCriterion("witch_tea", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WITCH_TEA))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:herbalist/tea_bagging");

		Advancement.Builder.advancement().parent(tea).display(ItemRegistry.SWAMP_BROTH,
				Component.translatable("advancement.thebetweenlands.herbalist.home_remedy_extraordinaire"),
				Component.translatable("advancement.thebetweenlands.herbalist.home_remedy_extraordinaire.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("nettle_tea", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.NETTLE_TEA))
			.addCriterion("nettle_soup", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.NETTLE_SOUP))
			.addCriterion("pheromone_extract", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.PHEROMONE_EXTRACT))
			.addCriterion("swamp_broth", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SWAMP_BROTH))
			.addCriterion("sturdy_stock", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.STURDY_STOCK))
			.addCriterion("pear_cordial", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.PEAR_CORDIAL))
			.addCriterion("shamans_brew", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SHAMANS_BREW))
			.addCriterion("lake_broth", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.LAKE_BROTH))
			.addCriterion("shell_stock", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SHELL_STOCK))
			.addCriterion("frog_leg_extract", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.FROG_LEG_EXTRACT))
			.addCriterion("witch_tea", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WITCH_TEA))
			.requirements(AdvancementRequirements.Strategy.AND)
			.save(consumer, "thebetweenlands:herbalist/home_remedy_extraordinaire");
	}
}
