package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

public class LootPoolEntries {
   public static final LootPoolEntryType EMPTY = register("empty", new EmptyLootItem.Serializer());
   public static final LootPoolEntryType ITEM = register("item", new LootItem.Serializer());
   public static final LootPoolEntryType REFERENCE = register("loot_table", new LootTableReference.Serializer());
   public static final LootPoolEntryType DYNAMIC = register("dynamic", new DynamicLoot.Serializer());
   public static final LootPoolEntryType TAG = register("tag", new TagEntry.Serializer());
   public static final LootPoolEntryType ALTERNATIVES = register("alternatives", CompositeEntryBase.createSerializer(AlternativesEntry::new));
   public static final LootPoolEntryType SEQUENCE = register("sequence", CompositeEntryBase.createSerializer(SequentialEntry::new));
   public static final LootPoolEntryType GROUP = register("group", CompositeEntryBase.createSerializer(EntryGroup::new));

   private static LootPoolEntryType register(String p_79630_, Serializer<? extends LootPoolEntryContainer> p_79631_) {
      return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(p_79630_), new LootPoolEntryType(p_79631_));
   }

   public static Object createGsonAdapter() {
      return GsonAdapterFactory.builder(Registry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootPoolEntryContainer::getType).build();
   }
}