package thebetweenlands.common.datagen.advancements;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.AnimateTrigger;
import thebetweenlands.common.advancement.BreakBlockTrigger;
import thebetweenlands.common.advancement.EventTrigger;
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.datagen.tags.BlockTagProvider;
import thebetweenlands.common.registries.*;

import java.util.Optional;
import java.util.function.Consumer;

public class AdventurerAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(ItemRegistry.SWAMP_TALISMAN,
			Component.translatable("advancement.thebetweenlands.adventurer.root"),
			Component.translatable("advancement.thebetweenlands.adventurer.root.desc"),
			TheBetweenlands.prefix("textures/block/temple_bricks.png"),
			AdvancementType.TASK, false, false, false)
			.addCriterion("talisman", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SWAMP_TALISMAN))
			.addCriterion("in_betweenlands", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setDimension(DimensionRegistries.DIMENSION_KEY)))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:adventurer/root");

		var gimme = Advancement.Builder.advancement().parent(root).display(ItemRegistry.SWAMP_TALISMAN,
				Component.translatable("advancement.thebetweenlands.adventurer.give_me_that"),
				Component.translatable("advancement.thebetweenlands.adventurer.give_me_that.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("talisman", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SWAMP_TALISMAN))
			.save(consumer, "thebetweenlands:adventurer/give_me_that");

		var dankWorld = Advancement.Builder.advancement().parent(gimme).display(BlockRegistry.SWAMP_GRASS,
				Component.translatable("advancement.thebetweenlands.adventurer.dank_world"),
				Component.translatable("advancement.thebetweenlands.adventurer.dank_world.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("in_betweenlands", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setDimension(DimensionRegistries.DIMENSION_KEY)))
			.save(consumer, "thebetweenlands:adventurer/dank_world");

		Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.PYRAD_FLAME,
				Component.translatable("advancement.thebetweenlands.adventurer.3spoopy5me"),
				Component.translatable("advancement.thebetweenlands.adventurer.3spoopy5me.desc"),
				null, AdvancementType.TASK, true, false, true)
			.addCriterion("event", EventTrigger.TriggerInstance.triggeredEvent(EnvironmentEventRegistry.SPOOPY.get()))
			.save(consumer, "thebetweenlands:adventurer/3spoopy5me");

		var bigWood = Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.WEEDWOOD_BARK,
				Component.translatable("advancement.thebetweenlands.adventurer.big_wood"),
				Component.translatable("advancement.thebetweenlands.adventurer.big_wood.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("giant_tree", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.GIANT_TREE))))
			.save(consumer, "thebetweenlands:adventurer/big_wood");

		Advancement.Builder.advancement().parent(bigWood).display(BlockRegistry.WEEDWOOD_SAPLING,
				Component.translatable("advancement.thebetweenlands.adventurer.environmentalist"),
				Component.translatable("advancement.thebetweenlands.adventurer.environmentalist.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("ruin", AdvancementCriteriaRegistry.PYRAD_AGGRO.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/environmentalist");

		var boulder = Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.ANGRY_PEBBLE,
				Component.translatable("advancement.thebetweenlands.adventurer.bouldery_bois"),
				Component.translatable("advancement.thebetweenlands.adventurer.bouldery_bois.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("boulder", AdvancementCriteriaRegistry.ROLLED_OVER_BY_BOULDER_SPRITE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/bouldery_bois");

		Advancement.Builder.advancement().parent(boulder).display(ItemRegistry.CRITTER_CRUNCHER,
				Component.translatable("advancement.thebetweenlands.adventurer.shake_it_off"),
				Component.translatable("advancement.thebetweenlands.adventurer.shake_it_off.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("infested", AdvancementCriteriaRegistry.INFESTED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/shake_it_off");

		var captain = Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.WEEDWOOD_ROWBOAT,
				Component.translatable("advancement.thebetweenlands.adventurer.captain101"),
				Component.translatable("advancement.thebetweenlands.adventurer.captain101.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("boat", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WEEDWOOD_ROWBOAT))
			.save(consumer, "thebetweenlands:adventurer/captain101");

		Advancement.Builder.advancement().parent(captain).display(ItemRegistry.DRAETON,
				Component.translatable("advancement.thebetweenlands.adventurer.full_of_hot_air"),
				Component.translatable("advancement.thebetweenlands.adventurer.full_of_hot_air.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("draeton", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.DRAETON))
			.save(consumer, "thebetweenlands:adventurer/full_of_hot_air");

		Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.GLOWING_BETWEENSTONE_TILE,
				Component.translatable("advancement.thebetweenlands.adventurer.enter_the_stronghold"),
				Component.translatable("advancement.thebetweenlands.adventurer.enter_the_stronghold.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("stronghold", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.WIGHT_FORTRESS))))
			.save(consumer, "thebetweenlands:adventurer/enter_the_stronghold");

		var isles = Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.MOSSY_CRAGROCK_TOP,
				Component.translatable("advancement.thebetweenlands.adventurer.flat_round_and_up_in_the_sky"),
				Component.translatable("advancement.thebetweenlands.adventurer.flat_round_and_up_in_the_sky.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("isles", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.FLOATING_ISLAND))))
			.save(consumer, "thebetweenlands:adventurer/flat_round_and_up_in_the_sky");

		Advancement.Builder.advancement().parent(isles).display(ItemRegistry.CHIROMAW_EGG,
				Component.translatable("advancement.thebetweenlands.adventurer.queen_of_the_hill"),
				Component.translatable("advancement.thebetweenlands.adventurer.queen_of_the_hill.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("raided_nest", AdvancementCriteriaRegistry.CHIROMAW_MATRIARCH_NEST_RAIDED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/queen_of_the_hill");

		Advancement.Builder.advancement().parent(isles).display(ItemRegistry.CHIROMAW_BARB,
				Component.translatable("advancement.thebetweenlands.adventurer.king_of_the_hill"),
				Component.translatable("advancement.thebetweenlands.adventurer.king_of_the_hill.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("claimed_nest", AdvancementCriteriaRegistry.CHIROMAW_MATRIARCH_NEST_CLAIMED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/king_of_the_hill");

		Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.OCTINE_BLOCK,
				Component.translatable("advancement.thebetweenlands.adventurer.idolised"),
				Component.translatable("advancement.thebetweenlands.adventurer.idolised.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("idol", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.IDOL_HEAD))))
			.save(consumer, "thebetweenlands:adventurer/idolised");

		var brazier = Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.BRAZIER,
				Component.translatable("advancement.thebetweenlands.adventurer.light_the_braziers"),
				Component.translatable("advancement.thebetweenlands.adventurer.light_the_braziers.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("dungeon", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.SLUDGE_WORM_DUNGEON))))
			.save(consumer, "thebetweenlands:adventurer/light_the_braziers");

		Advancement.Builder.advancement().parent(brazier).display(BlockRegistry.DUNGEON_DOOR_RUNES,
				Component.translatable("advancement.thebetweenlands.adventurer.a_real_poopy_experience"),
				Component.translatable("advancement.thebetweenlands.adventurer.a_real_poopy_experience.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("maze", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.SLUDGE_WORM_DUNGEON_MAZE))))
			.save(consumer, "thebetweenlands:adventurer/a_real_poopy_experience");

		Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.RING_OF_POWER,
				Component.translatable("advancement.thebetweenlands.adventurer.rule_them_all"),
				Component.translatable("advancement.thebetweenlands.adventurer.rule_them_all.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("power", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RING_OF_POWER))
			.addCriterion("flight", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RING_OF_ASCENT))
			.addCriterion("recruitment", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RING_OF_RECRUITMENT))
			.addCriterion("summoning", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RING_OF_SUMMONING))
			.addCriterion("dispersion", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RING_OF_DISPERSION))
			.addCriterion("gathering", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RING_OF_GATHERING))
			.save(consumer, "thebetweenlands:adventurer/none_to_rule_them_all");

		var pot = Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.LOOT_POT_1,
				Component.translatable("advancement.thebetweenlands.adventurer.pothead"),
				Component.translatable("advancement.thebetweenlands.adventurer.pothead.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("pot1", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot2", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot3", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot4", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot5", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot6", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot7", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot8", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot9", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.addCriterion("pot10", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build()))
			.save(consumer, "thebetweenlands:adventurer/pothead");

		var urn = Advancement.Builder.advancement().parent(pot).display(BlockRegistry.LOOT_URN_1,
				Component.translatable("advancement.thebetweenlands.adventurer.disgracing_the_dead"),
				Component.translatable("advancement.thebetweenlands.adventurer.disgracing_the_dead.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("urn1", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn2", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn3", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn4", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn5", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn6", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn7", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn8", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn9", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn10", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn11", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn12", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn13", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn14", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn15", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn16", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn17", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn18", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn19", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.addCriterion("urn20", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build()))
			.save(consumer, "thebetweenlands:adventurer/disgracing_the_dead");

		Advancement.Builder.advancement().parent(urn).display(BlockRegistry.MUD_BRICK_ALCOVE,
				Component.translatable("advancement.thebetweenlands.adventurer.i_think_you_urned_this"),
				Component.translatable("advancement.thebetweenlands.adventurer.i_think_you_urned_this.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("alcove1", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove2", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove3", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove4", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove5", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove6", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove7", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove8", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove9", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove10", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove11", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove12", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove13", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove14", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove15", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove16", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove17", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove18", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove19", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.addCriterion("alcove20", AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/i_think_you_urned_this");

		var ruined = Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.CHISELED_BETWEENSTONE,
				Component.translatable("advancement.thebetweenlands.adventurer.ruined_it"),
				Component.translatable("advancement.thebetweenlands.adventurer.ruined_it.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("ruins", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.SMALL_RUINS))))
			.save(consumer, "thebetweenlands:adventurer/ruined_it");

		var gambling = Advancement.Builder.advancement().parent(ruined).display(ItemRegistry.ITEM_SCROLL,
				Component.translatable("advancement.thebetweenlands.adventurer.gambling_addict"),
				Component.translatable("advancement.thebetweenlands.adventurer.gambling_addict.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("animate1", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate2", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate3", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate4", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate5", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate6", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate7", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate8", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate9", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate10", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate11", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate12", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate13", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate14", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.addCriterion("animate15", AnimateTrigger.TriggerInstance.animateItem(ItemRegistry.ITEM_SCROLL))
			.save(consumer, "thebetweenlands:adventurer/gambling_addict");

		Advancement.Builder.advancement().parent(gambling).display(ItemRegistry.RECORD_WANDERING_WISPS,
				Component.translatable("advancement.thebetweenlands.adventurer.vinyl_countdown"),
				Component.translatable("advancement.thebetweenlands.adventurer.vinyl_countdown.desc"),
				null, AdvancementType.CHALLENGE, true, true, true)
			.addCriterion("ancient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_ANCIENT))
			.addCriterion("astatos", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_ASTATOS))
			.addCriterion("beneath", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_BENEATH_A_GREEN_SKY))
			.addCriterion("between", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_BETWEEN_YOU_AND_ME))
			.addCriterion("christmas", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_CHRISTMAS_ON_THE_MARSH))
			.addCriterion("explorer", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_THE_EXPLORER))
			.addCriterion("hag", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_HAG_DANCE))
			.addCriterion("lonely", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_LONELY_FIRE))
			.addCriterion("mixtape", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_DJ_WIGHTS_MIXTAPE))
			.addCriterion("mystery", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.MYSTERIOUS_RECORD))
			.addCriterion("onwards", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_ONWARDS))
			.addCriterion("stuck", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_STUCK_IN_THE_MUD))
			.addCriterion("wandering", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_WANDERING_WISPS))
			.addCriterion("waterlogged", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.RECORD_WATERLOGGED))
			.rewards(AdvancementRewards.Builder.experience(5345))
			.save(consumer, "thebetweenlands:adventurer/vinyl_countdown");

		var tower = Advancement.Builder.advancement().parent(dankWorld).display(BlockRegistry.GLOWING_SMOOTH_CRAGROCK,
				Component.translatable("advancement.thebetweenlands.adventurer.tall_tough"),
				Component.translatable("advancement.thebetweenlands.adventurer.tall_tough.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("tower", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(StructureRegistry.CRAGROCK_TOWER))))
			.save(consumer, "thebetweenlands:adventurer/tall_tough");

		Advancement.Builder.advancement().parent(tower).display(BlockRegistry.MOB_SPAWNER,
				Component.translatable("advancement.thebetweenlands.adventurer.cookie_crumbles"),
				Component.translatable("advancement.thebetweenlands.adventurer.cookie_crumbles.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("tower_top", AdvancementCriteriaRegistry.CRAGROCK_TOP.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/cookie_crumbles");

		Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.VOLARKITE,
				Component.translatable("advancement.thebetweenlands.adventurer.thunderstruck"),
				Component.translatable("advancement.thebetweenlands.adventurer.thunderstruck.desc"),
				null, AdvancementType.TASK, true, true, true)
			.addCriterion("struck", AdvancementCriteriaRegistry.STRUCK_BY_LIGHTNING_WHILE_FLYING.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:adventurer/thunderstruck");

		Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.VOLARKITE,
				Component.translatable("advancement.thebetweenlands.adventurer.up_up_and_away"),
				Component.translatable("advancement.thebetweenlands.adventurer.up_up_and_away.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("struck", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.VOLARKITE))
			.save(consumer, "thebetweenlands:adventurer/up_up_and_away");

		var winter = Advancement.Builder.advancement().parent(dankWorld).display(ItemRegistry.CANDY_CANE,
				Component.translatable("advancement.thebetweenlands.adventurer.winter_is_coming"),
				Component.translatable("advancement.thebetweenlands.adventurer.winter_is_coming.desc"),
				null, AdvancementType.TASK, true, false, true)
			.addCriterion("winter", EventTrigger.TriggerInstance.triggeredEvent(EnvironmentEventRegistry.WINTER.get()))
			.save(consumer, "thebetweenlands:adventurer/winter_is_coming");

		Advancement.Builder.advancement().parent(winter).display(BlockRegistry.RED_PRESENT,
				Component.translatable("advancement.thebetweenlands.adventurer.santa_wight"),
				Component.translatable("advancement.thebetweenlands.adventurer.santa_wight.desc"),
				null, AdvancementType.TASK, true, false, true)
			.addCriterion("present", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockTagProvider.PRESENTS).build()))
			.save(consumer, "thebetweenlands:adventurer/santa_wight");
	}
}
