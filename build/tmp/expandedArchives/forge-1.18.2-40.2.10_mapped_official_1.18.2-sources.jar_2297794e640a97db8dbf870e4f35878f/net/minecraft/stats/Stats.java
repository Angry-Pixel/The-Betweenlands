package net.minecraft.stats;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Stats {
   public static final StatType<Block> BLOCK_MINED = makeRegistryStatType("mined", Registry.BLOCK);
   public static final StatType<Item> ITEM_CRAFTED = makeRegistryStatType("crafted", Registry.ITEM);
   public static final StatType<Item> ITEM_USED = makeRegistryStatType("used", Registry.ITEM);
   public static final StatType<Item> ITEM_BROKEN = makeRegistryStatType("broken", Registry.ITEM);
   public static final StatType<Item> ITEM_PICKED_UP = makeRegistryStatType("picked_up", Registry.ITEM);
   public static final StatType<Item> ITEM_DROPPED = makeRegistryStatType("dropped", Registry.ITEM);
   public static final StatType<EntityType<?>> ENTITY_KILLED = makeRegistryStatType("killed", Registry.ENTITY_TYPE);
   public static final StatType<EntityType<?>> ENTITY_KILLED_BY = makeRegistryStatType("killed_by", Registry.ENTITY_TYPE);
   public static final StatType<ResourceLocation> CUSTOM = makeRegistryStatType("custom", Registry.CUSTOM_STAT);
   public static final ResourceLocation LEAVE_GAME = makeCustomStat("leave_game", StatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_TIME = makeCustomStat("play_time", StatFormatter.TIME);
   public static final ResourceLocation TOTAL_WORLD_TIME = makeCustomStat("total_world_time", StatFormatter.TIME);
   public static final ResourceLocation TIME_SINCE_DEATH = makeCustomStat("time_since_death", StatFormatter.TIME);
   public static final ResourceLocation TIME_SINCE_REST = makeCustomStat("time_since_rest", StatFormatter.TIME);
   public static final ResourceLocation CROUCH_TIME = makeCustomStat("sneak_time", StatFormatter.TIME);
   public static final ResourceLocation WALK_ONE_CM = makeCustomStat("walk_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation CROUCH_ONE_CM = makeCustomStat("crouch_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation SPRINT_ONE_CM = makeCustomStat("sprint_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation WALK_ON_WATER_ONE_CM = makeCustomStat("walk_on_water_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation FALL_ONE_CM = makeCustomStat("fall_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation CLIMB_ONE_CM = makeCustomStat("climb_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation FLY_ONE_CM = makeCustomStat("fly_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation WALK_UNDER_WATER_ONE_CM = makeCustomStat("walk_under_water_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation MINECART_ONE_CM = makeCustomStat("minecart_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation BOAT_ONE_CM = makeCustomStat("boat_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation PIG_ONE_CM = makeCustomStat("pig_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation HORSE_ONE_CM = makeCustomStat("horse_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation AVIATE_ONE_CM = makeCustomStat("aviate_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation SWIM_ONE_CM = makeCustomStat("swim_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation STRIDER_ONE_CM = makeCustomStat("strider_one_cm", StatFormatter.DISTANCE);
   public static final ResourceLocation JUMP = makeCustomStat("jump", StatFormatter.DEFAULT);
   public static final ResourceLocation DROP = makeCustomStat("drop", StatFormatter.DEFAULT);
   public static final ResourceLocation DAMAGE_DEALT = makeCustomStat("damage_dealt", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_DEALT_ABSORBED = makeCustomStat("damage_dealt_absorbed", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_DEALT_RESISTED = makeCustomStat("damage_dealt_resisted", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_TAKEN = makeCustomStat("damage_taken", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_BLOCKED_BY_SHIELD = makeCustomStat("damage_blocked_by_shield", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_ABSORBED = makeCustomStat("damage_absorbed", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_RESISTED = makeCustomStat("damage_resisted", StatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DEATHS = makeCustomStat("deaths", StatFormatter.DEFAULT);
   public static final ResourceLocation MOB_KILLS = makeCustomStat("mob_kills", StatFormatter.DEFAULT);
   public static final ResourceLocation ANIMALS_BRED = makeCustomStat("animals_bred", StatFormatter.DEFAULT);
   public static final ResourceLocation PLAYER_KILLS = makeCustomStat("player_kills", StatFormatter.DEFAULT);
   public static final ResourceLocation FISH_CAUGHT = makeCustomStat("fish_caught", StatFormatter.DEFAULT);
   public static final ResourceLocation TALKED_TO_VILLAGER = makeCustomStat("talked_to_villager", StatFormatter.DEFAULT);
   public static final ResourceLocation TRADED_WITH_VILLAGER = makeCustomStat("traded_with_villager", StatFormatter.DEFAULT);
   public static final ResourceLocation EAT_CAKE_SLICE = makeCustomStat("eat_cake_slice", StatFormatter.DEFAULT);
   public static final ResourceLocation FILL_CAULDRON = makeCustomStat("fill_cauldron", StatFormatter.DEFAULT);
   public static final ResourceLocation USE_CAULDRON = makeCustomStat("use_cauldron", StatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_ARMOR = makeCustomStat("clean_armor", StatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_BANNER = makeCustomStat("clean_banner", StatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_SHULKER_BOX = makeCustomStat("clean_shulker_box", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BREWINGSTAND = makeCustomStat("interact_with_brewingstand", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BEACON = makeCustomStat("interact_with_beacon", StatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_DROPPER = makeCustomStat("inspect_dropper", StatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_HOPPER = makeCustomStat("inspect_hopper", StatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_DISPENSER = makeCustomStat("inspect_dispenser", StatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_NOTEBLOCK = makeCustomStat("play_noteblock", StatFormatter.DEFAULT);
   public static final ResourceLocation TUNE_NOTEBLOCK = makeCustomStat("tune_noteblock", StatFormatter.DEFAULT);
   public static final ResourceLocation POT_FLOWER = makeCustomStat("pot_flower", StatFormatter.DEFAULT);
   public static final ResourceLocation TRIGGER_TRAPPED_CHEST = makeCustomStat("trigger_trapped_chest", StatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_ENDERCHEST = makeCustomStat("open_enderchest", StatFormatter.DEFAULT);
   public static final ResourceLocation ENCHANT_ITEM = makeCustomStat("enchant_item", StatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_RECORD = makeCustomStat("play_record", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_FURNACE = makeCustomStat("interact_with_furnace", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CRAFTING_TABLE = makeCustomStat("interact_with_crafting_table", StatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_CHEST = makeCustomStat("open_chest", StatFormatter.DEFAULT);
   public static final ResourceLocation SLEEP_IN_BED = makeCustomStat("sleep_in_bed", StatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_SHULKER_BOX = makeCustomStat("open_shulker_box", StatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_BARREL = makeCustomStat("open_barrel", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BLAST_FURNACE = makeCustomStat("interact_with_blast_furnace", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_SMOKER = makeCustomStat("interact_with_smoker", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_LECTERN = makeCustomStat("interact_with_lectern", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CAMPFIRE = makeCustomStat("interact_with_campfire", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CARTOGRAPHY_TABLE = makeCustomStat("interact_with_cartography_table", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_LOOM = makeCustomStat("interact_with_loom", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_STONECUTTER = makeCustomStat("interact_with_stonecutter", StatFormatter.DEFAULT);
   public static final ResourceLocation BELL_RING = makeCustomStat("bell_ring", StatFormatter.DEFAULT);
   public static final ResourceLocation RAID_TRIGGER = makeCustomStat("raid_trigger", StatFormatter.DEFAULT);
   public static final ResourceLocation RAID_WIN = makeCustomStat("raid_win", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_ANVIL = makeCustomStat("interact_with_anvil", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_GRINDSTONE = makeCustomStat("interact_with_grindstone", StatFormatter.DEFAULT);
   public static final ResourceLocation TARGET_HIT = makeCustomStat("target_hit", StatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_SMITHING_TABLE = makeCustomStat("interact_with_smithing_table", StatFormatter.DEFAULT);

   private static ResourceLocation makeCustomStat(String p_13008_, StatFormatter p_13009_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_13008_);
      Registry.register(Registry.CUSTOM_STAT, p_13008_, resourcelocation);
      CUSTOM.get(resourcelocation, p_13009_);
      return resourcelocation;
   }

   private static <T> StatType<T> makeRegistryStatType(String p_13011_, Registry<T> p_13012_) {
      return Registry.register(Registry.STAT_TYPE, p_13011_, new StatType<>(p_13012_));
   }
}