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
	}
}
