package thebetweenlands.common.datagen.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.function.BiConsumer;

public class BLMiscLootProvider implements LootTableSubProvider {

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		output.accept(LootTableRegistry.SCROLL, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.OCTINE_INGOT).setWeight(20)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 12.0F))))
				.add(LootItem.lootTableItem(ItemRegistry.SYRMORITE_INGOT).setWeight(20)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 12.0F))))
				.add(LootItem.lootTableItem(ItemRegistry.VOODOO_DOLL).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.MAGIC_ITEM_MAGNET).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.SWIFT_PICK).setWeight(12))
				.add(LootItem.lootTableItem(ItemRegistry.WIGHTS_BANE).setWeight(12))
				.add(LootItem.lootTableItem(ItemRegistry.HAG_HACKER).setWeight(12))
				.add(LootItem.lootTableItem(ItemRegistry.CRITTER_CRUNCHER).setWeight(12))
				.add(LootItem.lootTableItem(ItemRegistry.SLUDGE_SLICER).setWeight(12))
				.add(LootItem.lootTableItem(ItemRegistry.SKULL_MASK).setWeight(12))
				.add(LootItem.lootTableItem(ItemRegistry.AMULET_SOCKET).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.VALONITE_SHARD).setWeight(12)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
				.add(LootItem.lootTableItem(ItemRegistry.RING_OF_POWER).setWeight(10))
				.add(NestedLootTable.lootTableReference(LootTableRegistry.MUSIC_DISC).setWeight(5))
				.add(LootItem.lootTableItem(ItemRegistry.SHADOW_STAFF).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.MIST_STAFF).setWeight(10))));

		output.accept(LootTableRegistry.FABRICATED_SCROLL, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.LOOT_SCRAPS).setWeight(2)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
				.add(NestedLootTable.lootTableReference(LootTableRegistry.SCROLL).setWeight(3))));

		output.accept(LootTableRegistry.MUSIC_DISC, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_ASTATOS))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_BETWEEN_YOU_AND_ME))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_CHRISTMAS_ON_THE_MARSH))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_THE_EXPLORER))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_HAG_DANCE))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_LONELY_FIRE))
				.add(LootItem.lootTableItem(ItemRegistry.MYSTERIOUS_RECORD))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_ANCIENT))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_BENEATH_A_GREEN_SKY))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_DJ_WIGHTS_MIXTAPE))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_ONWARDS))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_STUCK_IN_THE_MUD))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_WANDERING_WISPS))
				.add(LootItem.lootTableItem(ItemRegistry.RECORD_WATERLOGGED))));

		output.accept(LootTableRegistry.CHIROMAW_HATCHLING_FEED_ITEMS, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RAW_ANADIA_MEAT))
				.add(LootItem.lootTableItem(ItemRegistry.SILT_CRAB_CLAW))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY_WING))
				.add(LootItem.lootTableItem(ItemRegistry.FIREFLY))
				.add(LootItem.lootTableItem(ItemRegistry.RAW_FROG_LEGS))
				.add(LootItem.lootTableItem(ItemRegistry.GECKO))
				.add(LootItem.lootTableItem(ItemRegistry.LURKER_SKIN))
				.add(LootItem.lootTableItem(ItemRegistry.MIRE_SNAIL_EGG))
				.add(LootItem.lootTableItem(ItemRegistry.RAW_SNAIL_FLESH))));

		output.accept(LootTableRegistry.FILTERED_STAGNANT_WATER, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.SULFUR))
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_REMAINS))
				.add(LootItem.lootTableItem(ItemRegistry.SYRMORITE_NUGGET))
				.add(LootItem.lootTableItem(ItemRegistry.SLIMY_BONE))
				.add(LootItem.lootTableItem(ItemRegistry.LIMESTONE_FLUX))));

		output.accept(LootTableRegistry.FILTERED_SWAMP_WATER, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.SLUDGE_BALL))
				.add(LootItem.lootTableItem(ItemRegistry.ANGLER_TOOTH))
				.add(LootItem.lootTableItem(ItemRegistry.LURKER_SKIN))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY_WING))
				.add(LootItem.lootTableItem(ItemRegistry.BETWEENSTONE_PEBBLE))));

		output.accept(LootTableRegistry.GREEBLING_CORPSE, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RAW_ANADIA_MEAT).setWeight(8))
				.add(LootItem.lootTableItem(ItemRegistry.SILT_CRAB_CLAW).setWeight(8))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY_WING).setWeight(8))
				.add(LootItem.lootTableItem(ItemRegistry.FIREFLY).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.RAW_FROG_LEGS).setWeight(8))
				.add(LootItem.lootTableItem(ItemRegistry.GECKO).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.LURKER_SKIN).setWeight(8))
				.add(LootItem.lootTableItem(ItemRegistry.MIRE_SNAIL_EGG).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.RAW_SNAIL_FLESH).setWeight(8))
				.add(LootItem.lootTableItem(ItemRegistry.NET).setWeight(2))
				.add(LootItem.lootTableItem(ItemRegistry.AMATE_MAP))
				.add(LootItem.lootTableItem(ItemRegistry.SLINGSHOT))
				.add(LootItem.lootTableItem(ItemRegistry.BETWEENSTONE_PEBBLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.REED_ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.WEEDWOOD_STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.FABRICATED_SCROLL))
				.add(LootItem.lootTableItem(ItemRegistry.AMATE_PAPER).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).setWeight(5))));
	}
}
