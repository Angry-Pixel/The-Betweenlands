package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL.TypeReference;

public class References {
   public static final TypeReference LEVEL = () -> {
      return "level";
   };
   public static final TypeReference PLAYER = () -> {
      return "player";
   };
   public static final TypeReference CHUNK = () -> {
      return "chunk";
   };
   public static final TypeReference HOTBAR = () -> {
      return "hotbar";
   };
   public static final TypeReference OPTIONS = () -> {
      return "options";
   };
   public static final TypeReference STRUCTURE = () -> {
      return "structure";
   };
   public static final TypeReference STATS = () -> {
      return "stats";
   };
   public static final TypeReference SAVED_DATA = () -> {
      return "saved_data";
   };
   public static final TypeReference ADVANCEMENTS = () -> {
      return "advancements";
   };
   public static final TypeReference POI_CHUNK = () -> {
      return "poi_chunk";
   };
   public static final TypeReference ENTITY_CHUNK = () -> {
      return "entity_chunk";
   };
   public static final TypeReference BLOCK_ENTITY = () -> {
      return "block_entity";
   };
   public static final TypeReference ITEM_STACK = () -> {
      return "item_stack";
   };
   public static final TypeReference BLOCK_STATE = () -> {
      return "block_state";
   };
   public static final TypeReference ENTITY_NAME = () -> {
      return "entity_name";
   };
   public static final TypeReference ENTITY_TREE = () -> {
      return "entity_tree";
   };
   public static final TypeReference ENTITY = () -> {
      return "entity";
   };
   public static final TypeReference BLOCK_NAME = () -> {
      return "block_name";
   };
   public static final TypeReference ITEM_NAME = () -> {
      return "item_name";
   };
   public static final TypeReference UNTAGGED_SPAWNER = () -> {
      return "untagged_spawner";
   };
   public static final TypeReference STRUCTURE_FEATURE = () -> {
      return "structure_feature";
   };
   public static final TypeReference OBJECTIVE = () -> {
      return "objective";
   };
   public static final TypeReference TEAM = () -> {
      return "team";
   };
   public static final TypeReference RECIPE = () -> {
      return "recipe";
   };
   public static final TypeReference BIOME = () -> {
      return "biome";
   };
   public static final TypeReference WORLD_GEN_SETTINGS = () -> {
      return "world_gen_settings";
   };
}