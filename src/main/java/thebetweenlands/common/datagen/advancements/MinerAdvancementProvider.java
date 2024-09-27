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
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Optional;
import java.util.function.Consumer;

public class MinerAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(BlockRegistry.BETWEENSTONE,
				Component.translatable("advancement.thebetweenlands.miner.root"),
				Component.translatable("advancement.thebetweenlands.miner.root.desc"),
				TheBetweenlands.prefix("textures/block/pitstone.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_base_root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(TheBetweenlands.prefix("adventurer/dank_world"), true).build())).build())))))
			.save(consumer, "thebetweenlands:miner/root");

		var darkness = Advancement.Builder.advancement().parent(root).display(BlockRegistry.BETWEENSTONE,
				Component.translatable("advancement.thebetweenlands.miner.advanced_darkness"),
				Component.translatable("advancement.thebetweenlands.miner.advanced_darkness.desc"),
				null, AdvancementType.TASK, false, false, false)
			.addCriterion("underground", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.between(0, 100))))
			.save(consumer, "thebetweenlands:miner/advanced_darkness");

		Advancement.Builder.advancement().parent(darkness).display(ItemRegistry.LIFE_CRYSTAL,
				Component.translatable("advancement.thebetweenlands.miner.rock_bottom"),
				Component.translatable("advancement.thebetweenlands.miner.rock_bottom.desc"),
				null, AdvancementType.GOAL, false, false, false)
			.addCriterion("pitstone_depth", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.between(0, 45))))
			.save(consumer, "thebetweenlands:miner/rock_bottom");

		Advancement.Builder.advancement().parent(darkness).display(BlockRegistry.OCTINE_ORE,
				Component.translatable("advancement.thebetweenlands.miner.new_shinies"),
				Component.translatable("advancement.thebetweenlands.miner.new_shinies.desc"),
				null, AdvancementType.TASK, false, false, false)
			.addCriterion("syrmorite", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SYRMORITE_INGOT))
			.addCriterion("octine", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.OCTINE_INGOT))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:miner/new_shinies");

		Advancement.Builder.advancement().parent(root).display(ItemRegistry.SWIFT_PICK,
				Component.translatable("advancement.thebetweenlands.miner.get_schwifty"),
				Component.translatable("advancement.thebetweenlands.miner.get_schwifty.desc"),
				null, AdvancementType.TASK, false, false, false)
			.addCriterion("pick", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SWIFT_PICK))
			.save(consumer, "thebetweenlands:miner/get_schwifty");

		Advancement.Builder.advancement().parent(root).display(ItemRegistry.CAVING_ROPE,
				Component.translatable("advancement.thebetweenlands.miner.spelunking"),
				Component.translatable("advancement.thebetweenlands.miner.spelunking.desc"),
				null, AdvancementType.TASK, false, false, false)
			.addCriterion("rope_1", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_2", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_3", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_4", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_5", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_6", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_7", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_8", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_9", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("rope_10", AdvancementCriteriaRegistry.CAVING_ROPE_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.requirements(AdvancementRequirements.Strategy.AND)
			.save(consumer, "thebetweenlands:miner/spelunking");
	}
}
