package thebetweenlands.common.datagen.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.BreakBlockTrigger;
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.block.DecayableCropBlock;
import thebetweenlands.common.block.DugSoilBlock;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Optional;
import java.util.function.Consumer;

public class FarmerAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(ItemRegistry.COMPOST,
				Component.translatable("advancement.thebetweenlands.farmer.root"),
				Component.translatable("advancement.thebetweenlands.farmer.root.desc"),
				TheBetweenlands.prefix("textures/block/dug_swamp_dirt_composted_3.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_base_root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(TheBetweenlands.prefix("adventurer/root"), true).build())).build())))))
			.save(consumer, "thebetweenlands:farmer/root");

		Advancement.Builder.advancement().parent(root).display(ItemRegistry.BARNACLE,
				Component.translatable("advancement.thebetweenlands.farmer.bountiful_barnacles"),
				Component.translatable("advancement.thebetweenlands.farmer.bountiful_barnacles.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("barnacle", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.BARNACLE))
			.save(consumer, "thebetweenlands:farmer/bountiful_barnacles");

		var fertile = Advancement.Builder.advancement().parent(root).display(ItemRegistry.TINY_SLUDGE_WORM,
				Component.translatable("advancement.thebetweenlands.farmer.fertile_soil"),
				Component.translatable("advancement.thebetweenlands.farmer.fertile_soil.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("worm", AdvancementCriteriaRegistry.WORM_FROM_DIRT.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:farmer/fertile_soil");

		var compost = Advancement.Builder.advancement().parent(fertile).display(ItemRegistry.COMPOST,
				Component.translatable("advancement.thebetweenlands.farmer.mr_compost"),
				Component.translatable("advancement.thebetweenlands.farmer.mr_compost.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("compost", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.DUG_SWAMP_DIRT.get(), BlockRegistry.DUG_SWAMP_GRASS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DugSoilBlock.COMPOSTED, false))), ItemPredicate.Builder.item().of(ItemRegistry.COMPOST)))
			.save(consumer, "thebetweenlands:farmer/mr_compost");

		var pear = Advancement.Builder.advancement().parent(compost).display(ItemRegistry.MIDDLE_FRUIT,
				Component.translatable("advancement.thebetweenlands.farmer.a_vine_bush"),
				Component.translatable("advancement.thebetweenlands.farmer.a_vine_bush.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("plant_seeds", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(BlockRegistry.MIDDLE_FRUIT_BUSH.get()))
			.addCriterion("pear", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS))
			.save(consumer, "thebetweenlands:farmer/a_vine_bush");

		Advancement.Builder.advancement().parent(pear).display(ItemRegistry.PEARLED_PEAR,
				Component.translatable("advancement.thebetweenlands.farmer.premium_produce"),
				Component.translatable("advancement.thebetweenlands.farmer.premium_produce.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("craft_pear", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.PEARLED_PEAR))
			.addCriterion("eat_pear", ConsumeItemTrigger.TriggerInstance.usedItem(ItemRegistry.PEARLED_PEAR))
			.save(consumer, "thebetweenlands:farmer/premium_produce");

		Advancement.Builder.advancement().parent(compost).display(ItemRegistry.MARSH_MARIGOLD_FLOWER_DROP,
				Component.translatable("advancement.thebetweenlands.farmer.flower_power"),
				Component.translatable("advancement.thebetweenlands.farmer.flower_power.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("flower", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.DUG_SWAMP_DIRT.get(), BlockRegistry.DUG_SWAMP_GRASS.get(), BlockRegistry.PURIFIED_DUG_SWAMP_DIRT.get(), BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DugSoilBlock.COMPOSTED, true))),
				ItemPredicate.Builder.item().of(BlockRegistry.EDGE_SHROOM, BlockRegistry.EDGE_MOSS, BlockRegistry.EDGE_LEAF, BlockRegistry.PITCHER_PLANT, BlockRegistry.WEEPING_BLUE, BlockRegistry.SUNDEW, BlockRegistry.VENUS_FLY_TRAP,
					BlockRegistry.VOLARPAD, BlockRegistry.SWAMP_PLANT, BlockRegistry.ARROW_ARUM, BlockRegistry.BLUE_EYED_GRASS, BlockRegistry.BLUE_IRIS, BlockRegistry.BONESET, BlockRegistry.BOTTLE_BRUSH_GRASS, BlockRegistry.BROOMSEDGE,
					BlockRegistry.BUTTON_BUSH, BlockRegistry.CARDINAL_FLOWER, BlockRegistry.CATTAIL, BlockRegistry.CAVE_GRASS, BlockRegistry.COPPER_IRIS, BlockRegistry.MARSH_HIBISCUS, BlockRegistry.MARSH_MALLOW, BlockRegistry.TALL_SWAMP_GRASS,
					BlockRegistry.MILKWEED, BlockRegistry.NETTLE, BlockRegistry.FLOWERED_NETTLE, BlockRegistry.PICKERELWEED, BlockRegistry.PHRAGMITES, BlockRegistry.SHOOTS, BlockRegistry.SLUDGECREEP, BlockRegistry.SOFT_RUSH, BlockRegistry.SHORT_SWAMP_GRASS,
					BlockRegistry.TALL_SLUDGECREEP, BlockRegistry.PALE_GRASS, BlockRegistry.ROTBULB)))
			.save(consumer, "thebetweenlands:farmer/flower_power");

		//TODO necrofarmer

		Advancement.Builder.advancement().parent(compost).display(BlockRegistry.PURIFIED_SWAMP_DIRT,
				Component.translatable("advancement.thebetweenlands.farmer.pure_and_dirty"),
				Component.translatable("advancement.thebetweenlands.farmer.pure_and_dirty.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("compost", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.PURIFIED_DUG_SWAMP_DIRT.get(), BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DugSoilBlock.COMPOSTED, false))), ItemPredicate.Builder.item().of(ItemRegistry.COMPOST)))
			.save(consumer, "thebetweenlands:farmer/pure_and_dirty");

		var spores = Advancement.Builder.advancement().parent(compost).display(ItemRegistry.SPORES,
				Component.translatable("advancement.thebetweenlands.farmer.sporadically_radical"),
				Component.translatable("advancement.thebetweenlands.farmer.sporadically_radical.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("place_fungus", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(BlockRegistry.FUNGUS_CROP.get()))
			.addCriterion("break_fungus", BreakBlockTrigger.TriggerInstance.breakBlock(BlockPredicate.Builder.block().of(BlockRegistry.FUNGUS_CROP.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DecayableCropBlock.DECAYED, false).hasProperty(DecayableCropBlock.AGE, 15)).build()))
			.save(consumer, "thebetweenlands:farmer/sporadically_radical");

		Advancement.Builder.advancement().parent(spores).display(ItemRegistry.YELLOW_DOTTED_FUNGUS,
				Component.translatable("advancement.thebetweenlands.farmer.lively_crops"),
				Component.translatable("advancement.thebetweenlands.farmer.lively_crops.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("compost", AdvancementCriteriaRegistry.SPORELING_HATCH.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:farmer/lively_crops");

		Advancement.Builder.advancement().parent(spores).display(ItemRegistry.BULB_CAPPED_MUSHROOM,
				Component.translatable("advancement.thebetweenlands.farmer.mushroom_master"),
				Component.translatable("advancement.thebetweenlands.farmer.mushroom_master.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("yellow_dotted", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.YELLOW_DOTTED_FUNGUS))
			.addCriterion("bulb_capped", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.BULB_CAPPED_MUSHROOM))
			.addCriterion("black_hat", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.BLACK_HAT_MUSHROOM))
			.addCriterion("flathead", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.FLATHEAD_MUSHROOM))
			.addCriterion("rotbulb", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.ROTBULB))
			.addCriterion("edge_shroom", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.EDGE_SHROOM))
			.save(consumer, "thebetweenlands:farmer/mushroom_master");

		var kiss = Advancement.Builder.advancement().parent(root).display(ItemRegistry.OCHRE_SNAIL_SHELL,
				Component.translatable("advancement.thebetweenlands.farmer.now_kiss"),
				Component.translatable("advancement.thebetweenlands.farmer.now_kiss.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("snails", BredAnimalsTrigger.TriggerInstance.bredAnimals(Optional.of(EntityPredicate.Builder.entity().of(EntityRegistry.MIRE_SNAIL.get()).build()), Optional.of(EntityPredicate.Builder.entity().of(EntityRegistry.MIRE_SNAIL.get()).build()), Optional.empty()))
			.save(consumer, "thebetweenlands:farmer/now_kiss");

		Advancement.Builder.advancement().parent(kiss).display(ItemRegistry.COOKED_MIRE_SNAIL_EGG,
				Component.translatable("advancement.thebetweenlands.farmer.baby_murderer"),
				Component.translatable("advancement.thebetweenlands.farmer.baby_murderer.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("cooked", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.COOKED_MIRE_SNAIL_EGG))
			.addCriterion("scramble", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.MIRE_SCRAMBLE))
			.save(consumer, "thebetweenlands:farmer/baby_murderer");

		var thorax = Advancement.Builder.advancement().parent(compost).display(ItemRegistry.PHEROMONE_THORAX,
				Component.translatable("advancement.thebetweenlands.farmer.your_bush_smells"),
				Component.translatable("advancement.thebetweenlands.farmer.your_bush_smells.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("thorax", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.WEEDWOOD_BUSH.get())), ItemPredicate.Builder.item().of(ItemRegistry.PHEROMONE_THORAX)))
			.save(consumer, "thebetweenlands:farmer/your_bush_smells");

		var grub = Advancement.Builder.advancement().parent(thorax).display(ItemRegistry.SILK_GRUB,
				Component.translatable("advancement.thebetweenlands.farmer.moving_day"),
				Component.translatable("advancement.thebetweenlands.farmer.moving_day.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("grub", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.MOTH_HOUSE.get())), ItemPredicate.Builder.item().of(ItemRegistry.SILK_GRUB)))
			.save(consumer, "thebetweenlands:farmer/moving_day");

		var house = Advancement.Builder.advancement().parent(grub).display(BlockRegistry.MOTH_HOUSE,
				Component.translatable("advancement.thebetweenlands.farmer.multicore_threading"),
				Component.translatable("advancement.thebetweenlands.farmer.multicore_threading.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("house_maxed", AdvancementCriteriaRegistry.MOTH_HOUSE_MAXED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:farmer/multicore_threading");

		Advancement.Builder.advancement().parent(house).display(BlockRegistry.GRUB_HUB,
				Component.translatable("advancement.thebetweenlands.farmer.grubhub_inc"),
				Component.translatable("advancement.thebetweenlands.farmer.grubhub_inc.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("hub", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.GRUB_HUB.get())), ItemPredicate.Builder.item().of(ItemRegistry.PHEROMONE_EXTREACT_BUCKET)))
			.save(consumer, "thebetweenlands:farmer/grubhub_inc");
	}
}
