package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemTags {
   public static final TagKey<Item> WOOL = bind("wool");
   public static final TagKey<Item> PLANKS = bind("planks");
   public static final TagKey<Item> STONE_BRICKS = bind("stone_bricks");
   public static final TagKey<Item> WOODEN_BUTTONS = bind("wooden_buttons");
   public static final TagKey<Item> BUTTONS = bind("buttons");
   public static final TagKey<Item> CARPETS = bind("carpets");
   public static final TagKey<Item> WOODEN_DOORS = bind("wooden_doors");
   public static final TagKey<Item> WOODEN_STAIRS = bind("wooden_stairs");
   public static final TagKey<Item> WOODEN_SLABS = bind("wooden_slabs");
   public static final TagKey<Item> WOODEN_FENCES = bind("wooden_fences");
   public static final TagKey<Item> WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
   public static final TagKey<Item> WOODEN_TRAPDOORS = bind("wooden_trapdoors");
   public static final TagKey<Item> DOORS = bind("doors");
   public static final TagKey<Item> SAPLINGS = bind("saplings");
   public static final TagKey<Item> LOGS_THAT_BURN = bind("logs_that_burn");
   public static final TagKey<Item> LOGS = bind("logs");
   public static final TagKey<Item> DARK_OAK_LOGS = bind("dark_oak_logs");
   public static final TagKey<Item> OAK_LOGS = bind("oak_logs");
   public static final TagKey<Item> BIRCH_LOGS = bind("birch_logs");
   public static final TagKey<Item> ACACIA_LOGS = bind("acacia_logs");
   public static final TagKey<Item> JUNGLE_LOGS = bind("jungle_logs");
   public static final TagKey<Item> SPRUCE_LOGS = bind("spruce_logs");
   public static final TagKey<Item> CRIMSON_STEMS = bind("crimson_stems");
   public static final TagKey<Item> WARPED_STEMS = bind("warped_stems");
   public static final TagKey<Item> BANNERS = bind("banners");
   public static final TagKey<Item> SAND = bind("sand");
   public static final TagKey<Item> STAIRS = bind("stairs");
   public static final TagKey<Item> SLABS = bind("slabs");
   public static final TagKey<Item> WALLS = bind("walls");
   public static final TagKey<Item> ANVIL = bind("anvil");
   public static final TagKey<Item> RAILS = bind("rails");
   public static final TagKey<Item> LEAVES = bind("leaves");
   public static final TagKey<Item> TRAPDOORS = bind("trapdoors");
   public static final TagKey<Item> SMALL_FLOWERS = bind("small_flowers");
   public static final TagKey<Item> BEDS = bind("beds");
   public static final TagKey<Item> FENCES = bind("fences");
   public static final TagKey<Item> TALL_FLOWERS = bind("tall_flowers");
   public static final TagKey<Item> FLOWERS = bind("flowers");
   public static final TagKey<Item> PIGLIN_REPELLENTS = bind("piglin_repellents");
   public static final TagKey<Item> PIGLIN_LOVED = bind("piglin_loved");
   public static final TagKey<Item> IGNORED_BY_PIGLIN_BABIES = bind("ignored_by_piglin_babies");
   public static final TagKey<Item> PIGLIN_FOOD = bind("piglin_food");
   public static final TagKey<Item> FOX_FOOD = bind("fox_food");
   public static final TagKey<Item> GOLD_ORES = bind("gold_ores");
   public static final TagKey<Item> IRON_ORES = bind("iron_ores");
   public static final TagKey<Item> DIAMOND_ORES = bind("diamond_ores");
   public static final TagKey<Item> REDSTONE_ORES = bind("redstone_ores");
   public static final TagKey<Item> LAPIS_ORES = bind("lapis_ores");
   public static final TagKey<Item> COAL_ORES = bind("coal_ores");
   public static final TagKey<Item> EMERALD_ORES = bind("emerald_ores");
   public static final TagKey<Item> COPPER_ORES = bind("copper_ores");
   public static final TagKey<Item> NON_FLAMMABLE_WOOD = bind("non_flammable_wood");
   public static final TagKey<Item> SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
   public static final TagKey<Item> CANDLES = bind("candles");
   public static final TagKey<Item> DIRT = bind("dirt");
   public static final TagKey<Item> TERRACOTTA = bind("terracotta");
   public static final TagKey<Item> BOATS = bind("boats");
   public static final TagKey<Item> FISHES = bind("fishes");
   public static final TagKey<Item> SIGNS = bind("signs");
   public static final TagKey<Item> MUSIC_DISCS = bind("music_discs");
   public static final TagKey<Item> CREEPER_DROP_MUSIC_DISCS = bind("creeper_drop_music_discs");
   public static final TagKey<Item> COALS = bind("coals");
   public static final TagKey<Item> ARROWS = bind("arrows");
   public static final TagKey<Item> LECTERN_BOOKS = bind("lectern_books");
   public static final TagKey<Item> BEACON_PAYMENT_ITEMS = bind("beacon_payment_items");
   public static final TagKey<Item> STONE_TOOL_MATERIALS = bind("stone_tool_materials");
   public static final TagKey<Item> STONE_CRAFTING_MATERIALS = bind("stone_crafting_materials");
   public static final TagKey<Item> FREEZE_IMMUNE_WEARABLES = bind("freeze_immune_wearables");
   public static final TagKey<Item> AXOLOTL_TEMPT_ITEMS = bind("axolotl_tempt_items");
   public static final TagKey<Item> OCCLUDES_VIBRATION_SIGNALS = bind("occludes_vibration_signals");
   public static final TagKey<Item> CLUSTER_MAX_HARVESTABLES = bind("cluster_max_harvestables");

   private ItemTags() {
   }

   private static TagKey<Item> bind(String p_203855_) {
      return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(p_203855_));
   }

   public static TagKey<Item> create(final ResourceLocation name) {
      return TagKey.create(Registry.ITEM_REGISTRY, name);
   }
}
